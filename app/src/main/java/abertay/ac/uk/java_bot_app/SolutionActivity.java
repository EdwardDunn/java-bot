package abertay.ac.uk.java_bot_app;

/**
 * SolutionActivity
 * The SolutionActivity provides a solution fetched from the database and displays
 * it to the user with a small chat window at the bottom for comments on solution.
 *
 * @author  Edward Dunn
 * @version 1.0
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.ScrollView;
import android.widget.TextView;

public class SolutionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    private ChatBot cb;

    public static final int PERMISSIONS_LOCATION_REQUEST = 1;
    public static final int PERMISSIONS_EXTERNAL_STORAGE_REQUEST = 2;

    private ScrollView scrollView;

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
        scrollView = findViewById(R.id.scroll_solution);
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
        scrollDown();
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
        scrollDown();
        return textView;
    }

    public void scrollDown()
    {
        Thread scrollThread = new Thread(){
            public void run(){
                try {
                    sleep(200);
                    SolutionActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            scrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        scrollThread.start();
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
            requestStoragePermissions();
        }
        else if (id == R.id.tech_meetups){
            requestLocationsPermissions();
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

    //-----------------------------Request Permissions Methods------------------------------------//

    private void requestLocationsPermissions(){
        // Check for fine and coarse location permissions
        if (ContextCompat.checkSelfPermission(SolutionActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) + ContextCompat
                .checkSelfPermission(SolutionActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (SolutionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (SolutionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show snackbar with rationale for needing permissions
                Snackbar.make(SolutionActivity.this.findViewById(android.R.id.content),
                        "Please grant permissions to enable tech meetups near you to be shown",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestPermissions(
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSIONS_LOCATION_REQUEST);
                            }
                        }).show();

            } else {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_LOCATION_REQUEST);
            }
        } else {
            // Go to TechMeetups activity
            Intent techMeetupsIntent = new Intent(SolutionActivity.this, TechMeetupsActivity.class);
            startActivity(techMeetupsIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
    }

    private void requestStoragePermissions(){
        // Check for fine and coarse location permissions
        if (ContextCompat.checkSelfPermission(SolutionActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (SolutionActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show snackbar with rationale for needing permissions
                Snackbar.make(SolutionActivity.this.findViewById(android.R.id.content),
                        "Please grant permissions to be able to use the training features",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestPermissions(
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        PERMISSIONS_EXTERNAL_STORAGE_REQUEST);
                            }
                        }).show();

            } else {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_EXTERNAL_STORAGE_REQUEST);
            }
        } else {
            // Go to TechMeetups activity
            Intent trainingIntent = new Intent(SolutionActivity.this, TrainingActivity.class);
            startActivity(trainingIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Go to TechMeetups activity
                    Intent searchIntent = new Intent(SolutionActivity.this, TechMeetupsActivity.class);
                    startActivity(searchIntent);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                } else {
                    // Ask again for permissions
                    Snackbar.make(SolutionActivity.this.findViewById(android.R.id.content),
                            "Please grant permissions to enable tech meetups near you to be shown",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestPermissions(
                                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                            PERMISSIONS_LOCATION_REQUEST);
                                }
                            }).show();
                }
                return;
            }
            case PERMISSIONS_EXTERNAL_STORAGE_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Go to TechMeetups activity
                    Intent trainingIntent = new Intent(SolutionActivity.this, TrainingActivity.class);
                    startActivity(trainingIntent);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                } else {
                    // Ask again for permissions
                    Snackbar.make(SolutionActivity.this.findViewById(android.R.id.content),
                            "Please grant permissions to be able to use the training features",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestPermissions(
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            PERMISSIONS_EXTERNAL_STORAGE_REQUEST);
                                }
                            }).show();
                }
                return;
            }
        }

    }

}
