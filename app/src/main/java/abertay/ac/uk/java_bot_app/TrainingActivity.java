/**
 * TrainingActivity
 * Activity used to provide the user with the questions they have asked that week as a training session.
 * Gesture detection methods allow the user to swipe the screen to see next question.
 *
 * References:
 *  Gesture detection with drawer menu:
 *  https://stackoverflow.com/questions/17882255/gestures-not-working-when-using-drawerlayout-in-android-app#19393718
 *
 * @author  Edward Dunn
 * @version 1.0
 */

package abertay.ac.uk.java_bot_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;


public class TrainingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, GestureDetector.OnGestureListener {

    // Used for swipe action
    private GestureDetectorCompat gestureDetector;

    // Permissions request unique ids
    public static final int PERMISSIONS_LOCATION_REQUEST = 1;
    public static final int PERMISSIONS_EXTERNAL_STORAGE_REQUEST = 2;

    // Used to access questions database to show training questions
    private QuestionsSQLiteDatabaseHelper questionsDatabase;

    // Activity UI elements
    private TextView questionHeader;
    private TextView currentQuestion;
    private TextView solutionHeader;
    private TextView currentSolution;
    private TextView trainingMessage;
    private TextView swipeMessage;
    private ArrayList<Question> questionsList;
    private int questionCounter;

    @SuppressLint("HandlerLeak")
    Handler questionHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle bundle = msg.getData();

            String questionText = bundle.getString("question");
            String solutionText = bundle.getString("solution");

            currentQuestion.setText(questionText);
            currentSolution.setText(solutionText);
        }

    };

    @SuppressLint("HandlerLeak")
    Handler emptyDbHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            setUIElementsVisibility(false);
            trainingMessage.setText(R.string.training_complete_message);
        }
    };

    private NotificationManager nm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        setupUIViews();

        this.gestureDetector = new GestureDetectorCompat(TrainingActivity.this, TrainingActivity.this);

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // If there is a training notification active, cancel it
        cancelNotification();

        questionsDatabase = new QuestionsSQLiteDatabaseHelper(this);

        // Set question counter to zero when activity first opens
        questionCounter = 0;

        // Populate question list with the current weeks questions asked
        questionsList = questionsDatabase.getQuestions();
        questionCounter = 0;

        // Show first question on activity load
        showNextQuestion();

        //--------------------------------Drawer Menu---------------------------------------------//
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toogle);
        toogle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Used to ensure gesture detector for other UI components can function correctly
        drawer.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

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
     * Method used to cancel notifications, the training notifications shown direct to this activity
     * so should be cancel when ever the user views this activity
     */
    public void cancelNotification(){
        // Cancel any notifications
        nm.cancelAll();

        // Remove app icon badges
        AppIconBadgeSetter iconBadge = new AppIconBadgeSetter();
        iconBadge.removeAllBadges(this);
    }

    /**
     * Method used to instantiate activity elements
     */
    private void setupUIViews(){
        questionHeader = findViewById(R.id.training_txt_question_header);
        currentQuestion = findViewById(R.id.training_txt_question);
        solutionHeader = findViewById(R.id.training_txt_solution_header);
        currentSolution = findViewById(R.id.training_txt_solution);
        trainingMessage = findViewById(R.id.training_txt_training_message);
        swipeMessage = findViewById(R.id.training_txt_swipe_notice);
    }

    /**
     * Method used to handle onClick events
     */
    @Override
    public void onClick(View view){}

    /**
     * Method used to show next training question
     */
    private void showNextQuestion(){

            if (questionsList.size() > 0) {

                // Account for -1 errors
                if (questionCounter > questionsList.size() - 1) {

                    // If asked all questions then empty the database
                    Runnable emptyDbRunnable = new Runnable() {
                        @Override
                        public void run() {
                            questionsDatabase.emptyDatabase();
                            emptyDbHandler.sendEmptyMessage(0);
                        }
                    };

                    Thread emptyDatabaseThread = new Thread(emptyDbRunnable);
                    emptyDatabaseThread.start();
                }
                else {

                    setUIElementsVisibility(true);
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            String questionToShow = "";
                            String solutionToShow = "";

                            Question q = questionsList.get(questionCounter);

                            questionToShow = q.getQuestion();
                            solutionToShow = q.getSolution();

                            Message message = questionHandler.obtainMessage();
                            Bundle bundle = new Bundle();
                            bundle.putString("question", questionToShow);
                            bundle.putString("solution", solutionToShow);
                            message.setData(bundle);
                            questionHandler.sendMessage(message);
                            questionCounter++;
                        }
                    };
                    Thread showQuestionThread = new Thread(r);
                    showQuestionThread.start();
                }
            }
            else{
                // If no questions held in questions database
                setUIElementsVisibility(false);
                trainingMessage.setText(R.string.no_question_available);
            }
    }

    /**
     * Method used to control the visibility of the UI elements, if there is not questions to ask
     * only the header and a no questions message should be shown
     */
    private void setUIElementsVisibility(Boolean visibility){

        if(visibility == false) {
            questionHeader.setVisibility(View.INVISIBLE);
            currentQuestion.setVisibility(View.INVISIBLE);
            solutionHeader.setVisibility(View.INVISIBLE);
            currentSolution.setVisibility(View.INVISIBLE);
            swipeMessage.setVisibility(View.INVISIBLE);
            trainingMessage.setVisibility(View.VISIBLE);
        }else{
            questionHeader.setVisibility(View.VISIBLE);
            currentQuestion.setVisibility(View.VISIBLE);
            currentQuestion.setVisibility(View.VISIBLE);
            currentSolution.setVisibility(View.VISIBLE);
            swipeMessage.setVisibility(View.VISIBLE);
            trainingMessage.setVisibility(View.INVISIBLE);
        }
    }

    //-------------------------------Gesture Detector Methods-------------------------------------//
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {}

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {}

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        showNextQuestion();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //--------------------------------Drawer Menu Methods-----------------------------------------//
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
     * Method used to show options items in drawer menu
     */
    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.home){
            Intent searchIntent = new Intent(TrainingActivity.this, MainActivity.class);
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
            Intent searchIntent = new Intent(TrainingActivity.this, SetupActivity.class);
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
        if (ContextCompat.checkSelfPermission(TrainingActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) + ContextCompat
                .checkSelfPermission(TrainingActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (TrainingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (TrainingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show snackbar with rationale for needing permissions
                Snackbar.make(TrainingActivity.this.findViewById(android.R.id.content),
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
            Intent techMeetupsIntent = new Intent(TrainingActivity.this, TechMeetupsActivity.class);
            startActivity(techMeetupsIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
    }

    /**
     * Method used to check storage permissions and request if needed
     */
    private void requestStoragePermissions(){
        // Check for fine and coarse location permissions
        if (ContextCompat.checkSelfPermission(TrainingActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (TrainingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show snackbar with rationale for needing permissions
                Snackbar.make(TrainingActivity.this.findViewById(android.R.id.content),
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
            Intent trainingIntent = new Intent(TrainingActivity.this, TrainingActivity.class);
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
                    Intent searchIntent = new Intent(TrainingActivity.this, TechMeetupsActivity.class);
                    startActivity(searchIntent);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                } else {
                    // Ask again for permissions
                    Snackbar.make(TrainingActivity.this.findViewById(android.R.id.content),
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
                    Intent trainingIntent = new Intent(TrainingActivity.this, TrainingActivity.class);
                    startActivity(trainingIntent);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                } else {
                    // Ask again for permissions
                    Snackbar.make(TrainingActivity.this.findViewById(android.R.id.content),
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
