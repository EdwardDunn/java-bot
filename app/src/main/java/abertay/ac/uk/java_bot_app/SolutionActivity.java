package abertay.ac.uk.java_bot_app;

/**
 * SolutionActivity
 * The SolutionActivity provides a solution fetched from the database and displays
 * it to the user with a small chat window at the bottom for comments on solution.
 *
 * @author  Edward Dunn
 * @version 1.0
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SolutionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    private ChatBot cb;

    private TextView solutionText;
    private LinearLayout layout;
    private String SUCCESS_QUESTION = "Did that help?";
    private Button ask_btn;
    private EditText question_field;
    private String solution;
    private String initialQuestion;

    private String solutionkey;

    QuestionsSQLiteDatabaseHelper questionsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution);

        setupUIViews();


        questionsDatabase = new QuestionsSQLiteDatabaseHelper(this);

        ask_btn.setOnClickListener(this);

        showSolution();

        layout.addView(createNewTextView(SUCCESS_QUESTION));

        //-----------------------Drawer menu---------------------------------------///
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toogle);
        toogle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void setupUIViews(){
        solutionText = findViewById(R.id.solution_txt_solution);
        layout = findViewById(R.id.solution_ll_question_layout);
        ask_btn = findViewById(R.id.solution_btn_ask);
        question_field = findViewById(R.id.solution_et_question_field);
        solution = "";
        solutionkey = "";
        initialQuestion = "";

        cb = ChatBot.getInstance();
    }

    protected void onPause(){
        super.onPause();
    }

    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.solution_btn_ask){
            //Create new TextView with text entered into question field in questions layout
            String question = "";
            question  = question_field.getText().toString();
            layout.addView(createNewUserTextView(question));
            question_field.setText("");
            getChatBotResponse(question);

            // Close androids soft keyboard when ask button is pressed
            View viewCheck = this.getCurrentFocus();
            if (viewCheck != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void showSolution(){
        Intent intent = getIntent();
        Bundle data = intent.getExtras();

        // Used for adding to training database
        initialQuestion = data.getString("initialQuestion");

        // Used for searching stackoverflow, better than using the actual question as the query
        // parameter
        solutionkey = data.getString("solution_key");

        // Show solution found
        solution = data.getString("solution");
        solutionText.setText(solution);
    }

    private TextView createNewTextView(String text){
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);
        textView.setText(text);
        textView.setTextSize(18);
        textView.setTextColor(Color.WHITE);
        return textView;
    }

    private TextView createNewUserTextView(String text){
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);
        textView.setPadding(0,0,0,20);
        textView.setText(text);
        textView.setTextSize(18);
        textView.setTextColor(Color.GREEN);
        return textView;
    }

    private void getChatBotResponse(String question){
        String response = "";
        String questionType = "";

        question = question.toLowerCase();

        response = cb.checkResponseToQuestion(question);
        questionType = cb.getSolutionType();

        // If response from user is not another question
        if(questionType != "solution question") {
            // If the returned response from ChatBot equals user is happy with solution
            // TODO - this response 'Great' would have to made in to loop checking for other positive responses if app was released (as app is for educational purposes, this will suffice)
            if (response.contains("Great")) {
                addQuestionToDatabase();
                layout.addView(createNewTextView(response));
            }else{
                // If user is not happy with response, search Stack Overflow with initial question
                layout.addView(createNewTextView(response));
                Intent stackOverflowIntent = new Intent(this, StackOverflowActivity.class);
                stackOverflowIntent.putExtra("url", "www.stackoverflow.com/search?q=" + solutionkey + " in java" );
                startActivity(stackOverflowIntent);
            }
        }else{
            // TODO - if a new question is asked, refresh page with new question
            // If question is a programming one e.g. 'how do I parse an int?'
            //Intent solutionIntent = new Intent(this, SolutionActivity.class);
            //solutionIntent.putExtra("solution", response);
            //solutionIntent.putExtra("initialQuestion", question);
            //startActivity(solutionIntent);
        }

    }

    private void addQuestionToDatabase(){

        Runnable r = new Runnable() {
            @Override
            public void run() {
                questionsDatabase.addQuestion(new Question(initialQuestion, solution));
            }
        };
        Thread updateDatabase = new Thread(r);
        updateDatabase.start();
    }

    //---------------------------Drawer Menu Methods----------------------------------------------//

    @Override
    public void onBackPressed(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.refresh){
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.home){
            Intent searchIntent = new Intent(SolutionActivity.this, MainActivity.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
        else if (id == R.id.training){
            Intent searchIntent = new Intent(SolutionActivity.this, TrainingActivity.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
        else if (id == R.id.tech_meetups){
            Intent searchIntent = new Intent(SolutionActivity.this, TechMeetupsActivity.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
        else if(id == R.id.setup){
            Intent searchIntent = new Intent(SolutionActivity.this, SetupActivity.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
