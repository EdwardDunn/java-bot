/**
 * SolutionActivity
 * The SolutionActivity provides a solution fetched from the database and displays
 * it to the user with a small chat window at the bottom for comments on solution.
 *
 * References
 * Shared Preferences:
 *   https://stackoverflow.com/questions/23024831/android-shared-preferences-example
 *
 * @author  Edward Dunn
 * @version 1.0
 */

package abertay.ac.uk.java_bot_app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    // Used to store question field state using shared preferences
    public static final String PREFS_FILE_NAME = "PrefsFile";

    private ChatBot cb;

    // These permissions are required for the menu (if going to the training or tech meetups activity check permissions
    public static final int PERMISSIONS_LOCATION_REQUEST = 1;
    public static final int PERMISSIONS_EXTERNAL_STORAGE_REQUEST = 2;

    // Activity UI elements
    private ScrollView scrollView;
    private TextView solutionText;
    private LinearLayout layout;
    private Button ask_btn;
    private EditText question_field;
    private String solution;
    private String initialQuestion;
    private String solutionkey;

    // Questions database used add successful solutions
    QuestionsSQLiteDatabaseHelper questionsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution);

        setupUIViews();

        questionsDatabase = new QuestionsSQLiteDatabaseHelper(this);

        ask_btn.setOnClickListener(this);

        // Show solution passed by MainActivity
        showSolution();

        // Show initial chat bot response
        layout.addView(createNewTextView(getString(R.string.solution_success_message)));

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

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Store text in question field
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE).edit();
        editor.putString("questionFieldText", question_field.getText().toString());
        editor.apply();

       /*
        int count = layout.getChildCount();

        ArrayList<String> list = new ArrayList();

        for(int i = 0; i < count; i++){
            View v = layout.getChildAt(i);
            String s = v.toString();
            list.add(s);
        }

        savedInstanceState.putStringArrayList("list", list);
        */

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore text in question field
        SharedPreferences prefs = getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        String name = prefs.getString("questionFieldText", "No text entered");
        question_field.setText(name);


        /*
        ArrayList<String> list;

        list = savedInstanceState.getStringArrayList("list");

        for(String s : list){
            createNewTextView(s.toString());

            //Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        }
        */
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onStop() { super.onStop(); }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        // Stops all background threads
        Thread[] backGroundThreads = new Thread[Thread.activeCount()];
        Thread.enumerate(backGroundThreads);
        for (Thread activeThread : backGroundThreads) {
            activeThread.interrupt();
        }
        
    }

    /**
     * Method used to instantiate activity elements
     */
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

    /**
     * Method used to handle onClick events
     */
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.solution_btn_ask){
            // Close androids soft keyboard when ask button is pressed
            View viewCheck = this.getCurrentFocus();
            if (viewCheck != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            String question = "";
            question  = question_field.getText().toString();
            layout.addView(createNewUserTextView(question));
            question_field.setText("");

            /* Thread used for delaying response by 800 milliseconds to allow for the soft keyboard
            to close first. If not used the response would not be seen as the web view opens too
            quickly */
            final String finalQuestion = question;
            Thread responseThread = new Thread(){
                public void run(){
                    try {
                        sleep(800);
                        SolutionActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                getChatBotResponse(finalQuestion);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            responseThread.start();
        }
    }

    /**
     * Method used to show the solution to the user
     */
    private void showSolution(){
        Intent intent = getIntent();
        Bundle data = intent.getExtras();

        // Used for adding to training database
        initialQuestion = data.getString("initialQuestion");

        // Used for searching stackoverflow, better than using the actual question as the query parameter
        solutionkey = data.getString("solution_key");

        // Show solution found
        solution = data.getString("solution");
        solutionText.setText(solution);
    }

    /**
     * Method used to create new TextView for chat bot response
     */
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

    /**
     * Method used to create a new TextView for user response
     */
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

    /**
     * Method used to ensure the scroll view always goes to absolute bottom, a timer is implemented
     * to wait for a new text view to be added and then scroll to bottom.
     */
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

    /**
     * Method to used to evaluate the users response to the solution provided, if it is positive the
     * solution and initial question will be added to the questions database for the weekly training
     * session, if not the StackOverflow activity will be opened with the solution key as the query
     * parameter.
     */
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
                // Check network state, if no internet connection display dialog
                CheckConnection checkCon = new CheckConnection(this);
                Boolean connected = checkCon.checkConnection();

                // Only go to StackOverflow Activity if connected to internet
                if(connected) {
                    Intent stackOverflowIntent = new Intent(this, StackOverflowActivity.class);
                    stackOverflowIntent.putExtra("url", "www.stackoverflow.com/search?q=" + solutionkey + " in java" );
                    startActivity(stackOverflowIntent);
                }

            }
        }

    }

    /**
     * Method to used to add successful solutions to questions database
     */
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
    /**
     * Method used to control the opening and closing of drawer menu
     */
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

    /**
     * Method used to create options menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    /**
     * Method used to provide a refresh button in the options menu
     */
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

    /**
     * Method used to set options items in drawer menu
     */
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
            // Check network state, if no internet connection display dialog
            CheckConnection checkCon = new CheckConnection(this);
            Boolean connected = checkCon.checkConnection();

            // Only go to TechMeetupsActivity if connected to internet
            if(connected) {
                requestLocationsPermissions();
            }
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
    /**
     * Method used to check location permissions and request if needed
     */
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

    /**
     * Method used to check storage permissions and request if needed
     */
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

    /**
     * Method used to show rationale for requested permissions and to re-request
     */
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
