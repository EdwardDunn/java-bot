package abertay.ac.uk.java_bot_app;

/**
 * MainActivity
 * The MainActivity class provides the chat bot features to the app.
 * The user can enter a question and a response is calculated and sent back.
 *
 * References:
 *  Notifications
 *  https://www.youtube.com/watch?v=SWsuijO5NGE&list=PL6gx4Cwl9DGBsvRxJJOzG4r4k_zLKrnxl&index=61
 *  Logo:
 *  http://www.clipartlord.com/wp-content/uploads/2013/12/robot13.png
 *  Scroll to bottom of linear layout:
 *  https://stackoverflow.com/questions/14801215/scrollview-not-scrolling-down-completely
 *  Close Android soft keyboard:
 *  https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard#1109108
 *  Permissions:
 *  https://stackoverflow.com/questions/34040355/how-to-check-the-multiple-permission-at-single-request-in-android-m
 *
 * @author  Edward Dunn
 * @version 1.0
 */

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
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
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener, GestureDetector.OnGestureListener
{
    private GestureDetectorCompat gestureDetector;

    public static final int PERMISSIONS_LOCATION_REQUEST = 1;
    public static final int PERMISSIONS_EXTERNAL_STORAGE_REQUEST = 2;

    private ScrollView scrollView;

    private CheckConnection checkCon;

    private ChatBot cb;
    private final String WELCOME_MESSAGE = "Hey, how can I help";
    private final String WELCOME_BACK_MESSAGE = "Great, your back";
    private final String NO_QUESTION_RESPONSE = "Sorry I didn't catch that";

    // Set notifications on or off
    public static Boolean notifications;
    public static void setNotificationsOnOrOff(Boolean visible){
        if(visible){
            notifications = true;

        }else{
            notifications = false;
        }
    }

    // Progress Bar
    // setLoodingProgressBarVisibility() allows this variable to be easily set by other classes (ChatBotRemoteDatabaseHelper)
    public static ProgressBar loadingProgressBar;
    public void setLoadingProgressBarVisibility(Boolean visible){
        if(visible){
            loadingProgressBar.setVisibility(View.VISIBLE);
        }
        else{
            loadingProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    // Loading Message (when connection is not available
    // setLoadingMessageVisibility() allows this variable to be easily set by other classes (ChatBotRemoteDatabaseHelper)
    private static TextView loadingMessage;
    public void setErrorMessageVisibility(Boolean visible){
        if(visible){
            loadingMessage.setVisibility(View.VISIBLE);
        }
        else{
            loadingMessage.setVisibility(View.INVISIBLE);
        }
    }

    private LinearLayout layout;
    private Button ask_btn;
    private EditText question_field;

    // Used for Stack Overflow search if solution not found
    private String initialQuestion;
    // Used to set initial question
    private int questionCounter;

    // Used for training session notification
    private NotificationCompat.Builder notification;
    //private final int UNIQUE_ID = 001;

    private NotificationManager nm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUIViews();

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        ask_btn.setOnClickListener(this);

        // Show ChatBot welcome message on start
        layout.addView(createNewBotTextView(WELCOME_MESSAGE));

        this.gestureDetector = new GestureDetectorCompat(this, this);

        //----------------------------Check Network State-----------------------------------------//
        checkCon = new CheckConnection(this);
        checkCon.checkConnection();

        //---------------------------Training Session Notification--------------------------------//

        // If notifications are allowed, set to true in setNotifications
        if(notifications) {
            // This object is used to extend the timer class
            NotificationTimer notificationTask = new NotificationTimer();

            // Create timer object
            Timer notificationTimer = new Timer();

            // TODO - for completed app set time to 7 days (604800000 milliseconds)
            // For demonstration purposes 2 minutes is used (120000 milliseconds)
            notificationTimer.schedule(notificationTask, 5000, 30000);
        }

        //-----------------------------Drawer Menu------------------------------------------------//
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

    protected void onPause(){
        super.onPause();
    }

    protected void onResume(){
        super.onResume();
        // Check connection
        checkCon.checkConnection();

        // Clear any previous ChatBot discussion
        layout.removeAllViews();
        // Display welcome back message
        layout.addView(createNewBotTextView(WELCOME_BACK_MESSAGE));
    }

    protected void onStop() {
        super.onStop();

    }

    private void setupUIViews(){
        scrollView = findViewById(R.id.scroll);

        loadingProgressBar = (ProgressBar) findViewById(R.id.main_pb_progress_bar);
        // Set invisible by default, will be shown when remote database connection in progress
        loadingProgressBar.setVisibility(View.INVISIBLE);

        loadingMessage = (TextView) findViewById(R.id.main_txt_loading_message);
        // Set invisible by default, will be shown when remote database connection in progress
        loadingMessage.setVisibility(View.INVISIBLE);

        layout = findViewById(R.id.main_ll_question_layout);
        ask_btn = findViewById(R.id.main_btn_ask);
        question_field = findViewById(R.id.main_et_question_field);
        questionCounter = 0;
        initialQuestion = "";
        cb = ChatBot.getInstance();

        // Ensures notifications are not set to null every time MainActivity is called
        if(notifications == null){
            notifications = true;
        }
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.main_btn_ask){
            // Create new TextView with text entered into question field in questions layout
            String question = "";
            question  = question_field.getText().toString();

            if(question.isEmpty()) {
                layout.addView(createNewBotTextView(NO_QUESTION_RESPONSE));
            }else {
                // Question asked
                initialQuestion = question;

                layout.addView(createNewUserTextView(question));

                getChatBotResponse(question);

                // Clear question box
                question_field.setText("");
            }

            // Close androids soft keyboard when ask button is pressed
            View viewCheck = this.getCurrentFocus();
            if (viewCheck != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /**
     * Method used to create new TextView on click on ask question button
     * @param text
     * @return textView
     */
    private TextView createNewBotTextView(String text){
        final LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);
        textView.setPadding(0,0,0,10);
        textView.setText(text);
        textView.setTextSize(18);
        textView.setTextColor(Color.WHITE);
        scrollDown();
        return textView;
    }

    private TextView createNewUserTextView(String text){
        final LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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
                    MainActivity.this.runOnUiThread(new Runnable() {
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
     * Method to evaluate the question being asked and provide a response, including opening the solution activity
     * @param question
     */
    private void getChatBotResponse(String question){
        String response = "";
        String solutionKey = "";
        String questionType = "";
        question = question.toLowerCase();

        // Get response to the users question
        response = cb.askQuestion(question);
        solutionKey = cb.getSolutionKey();
        questionType = cb.getSolutionType();

        // If question is not a solution type, e.g. not a programming question
        if(questionType != "solution question") {
            layout.addView(createNewBotTextView(response));
        }else{
            // If question is a programming one e.g. 'how do I parse an int?'
            Intent solutionIntent = new Intent(this, SolutionActivity.class);
            solutionIntent.putExtra("solution", response);
            solutionIntent.putExtra("solution_key", solutionKey);
            solutionIntent.putExtra("initialQuestion", initialQuestion);
            startActivity(solutionIntent);
        }

    }

    //-------------------------------Notification Methods-----------------------------------------//

    /**
     * Class used to extend TimerTask for displaying the training session notification
     */
    class NotificationTimer extends TimerTask {
        public void run() {
            generateNotification();
        }
    }

    /**
     * Generate notification for alerting user that this weeks training session is available
     */
    public void generateNotification(){

        // If notifications are set to true in the setup activity training notifications switch
        if(notifications == true) {

            notification = new NotificationCompat.Builder(this);
            notification.setAutoCancel(true);
            // Onclick of notification, go to the training activity
            Intent trainingIntent = new Intent(this, TrainingActivity.class);
            PendingIntent trainingActivityIntent = PendingIntent.getActivity(this, 0, trainingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Set the training activity to open on click of notification
            notification.setContentIntent(trainingActivityIntent);

            // Used for dismiss action
            Intent dismissIntent = new Intent(this, DismissNotification.class);
            PendingIntent dismissActivityIntent = PendingIntent.getActivity(this, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Build the notification
            notification.setAutoCancel(true);
            notification.setSmallIcon(R.mipmap.ic_launcher_round);
            notification.setWhen(System.currentTimeMillis());
            notification.setContentTitle(getString(R.string.training_session_notification_title));
            notification.setContentText(getString(R.string.training_session_notification_content));

            // Action buttons
            notification.addAction(R.mipmap.icon_java_bot_3_round, getString(R.string.notification_train_action), trainingActivityIntent);
            notification.addAction(R.mipmap.icon_java_bot_3_round, getString(R.string.notification_dismiss_action), dismissActivityIntent);

            // Create random Id
            Random rand = new Random();
            int notificationId = rand.nextInt();

            // Create notification
            nm.notify(notificationId, notification.build());
        }
    }

    //-------------------------------Gesture Detector Methods-------------------------------------//
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        //showNextQuestion();
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    //------------------------------Drawer Menu Methods-------------------------------------------//

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
            Intent searchIntent = new Intent(MainActivity.this, MainActivity.class);
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
            Intent searchIntent = new Intent(MainActivity.this, SetupActivity.class);
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
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) + ContextCompat
                .checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show snackbar with rationale for needing permissions
                Snackbar.make(MainActivity.this.findViewById(android.R.id.content),
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
            Intent techMeetupsIntent = new Intent(MainActivity.this, TechMeetupsActivity.class);
            startActivity(techMeetupsIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
    }

    private void requestStoragePermissions(){
        // Check for fine and coarse location permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show snackbar with rationale for needing permissions
                Snackbar.make(MainActivity.this.findViewById(android.R.id.content),
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
            Intent trainingIntent = new Intent(MainActivity.this, TrainingActivity.class);
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
                    Intent searchIntent = new Intent(MainActivity.this, TechMeetupsActivity.class);
                    startActivity(searchIntent);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                } else {
                    // Ask again for permissions
                    Snackbar.make(MainActivity.this.findViewById(android.R.id.content),
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
                    Intent trainingIntent = new Intent(MainActivity.this, TrainingActivity.class);
                    startActivity(trainingIntent);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                } else {
                    // Ask again for permissions
                    Snackbar.make(MainActivity.this.findViewById(android.R.id.content),
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
