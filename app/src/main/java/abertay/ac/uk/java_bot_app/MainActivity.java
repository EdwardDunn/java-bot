package abertay.ac.uk.java_bot_app;

/**
 * MainActivity
 * The MainActivity class provides the chat bot features to the app.
 * The user can enter a question and a response is calculated and sent back.
 *
 * References:
 *  Notifications
 *  https://www.youtube.com/watch?v=SWsuijO5NGE&list=PL6gx4Cwl9DGBsvRxJJOzG4r4k_zLKrnxl&index=61
 *
 * @author  Edward Dunn
 * @version 1.0
 */

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener, android.widget.PopupMenu.OnMenuItemClickListener
{
    private ChatBot cb;
    private final String WELCOME_MESSAGE = "Hey, how can I help";
    private final String WELCOME_BACK_MESSAGE = "Great, your back";

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
    private ImageView menu;

    // Used for Stack Overflow search if solution not found
    private String initialQuestion;
    // Used to set initial question
    private int questionCounter;

    // Used for training session notification
    NotificationCompat.Builder notification;
    private static final int uniqueID = 001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUIViews();

        ask_btn.setOnClickListener(this);

        menu.setOnClickListener(this);

        // Show ChatBot welcome message on start
        layout.addView(createNewTextView(WELCOME_MESSAGE));

        //-------------------Permissions----------------------------//

        // TODO - implement permissions properly
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 5 );
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 5);

        //---------------Training Session Notification--------------//

        // If notifications are allowed, set to true in setNotifications
        if(notifications) {
            // Initialise notification object
            notification = new NotificationCompat.Builder(this);
            notification.setAutoCancel(true);

            // This object is used to extend the timer class
            NotificationTimer notificationTask = new NotificationTimer();

            // Create timer object
            Timer notificationTimer = new Timer();

            // TODO - for completed app set time to 7 days (604800000 milliseconds)
            // For demonstration purposes 2 minutes is used (120000 milliseconds)
            notificationTimer.schedule(notificationTask, 5000, 30000);
        }

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

    protected void onPause(){
        super.onPause();
    }

    protected void onResume(){
        super.onResume();
        // Clear any previous ChatBot discussion
        layout.removeAllViews();
        // Display welcome back message
        layout.addView(createNewTextView(WELCOME_BACK_MESSAGE));
    }

    private void setupUIViews(){
        loadingProgressBar = (ProgressBar) findViewById(R.id.main_pb_progress_bar);
        loadingMessage = (TextView) findViewById(R.id.main_txt_loading_message);

        layout = findViewById(R.id.main_ll_question_layout);
        ask_btn = findViewById(R.id.main_btn_ask);
        question_field = findViewById(R.id.main_et_question_field);
        menu = findViewById(R.id.main_img_menu);
        questionCounter = 0;
        initialQuestion = "";
        cb = ChatBot.getInstance();
        notifications = true;

    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.main_btn_ask){
            // Used to set initialQuestion variable
            //questionCounter++;

            // Create new TextView with text entered into question field in questions layout
            String question = "";
            question  = question_field.getText().toString();

            // If first question asked, set initialQuestion
            //if(questionCounter == 1){
                initialQuestion = question;
            //}

            getChatBotResponse(question);

            // Clear question box
            question_field.setText("");

        }else if (view.getId() == R.id.main_img_menu){
            showPopup(view);
        }
    }

    /**
     * Method used to show the java_bot_menu.xml file as a popup menu
     * @param view
     */
    public void showPopup(View view){
        PopupMenu popup = new PopupMenu(this,view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.java_bot_menu);
        popup.show();
    }

    /**
     * Method used provide onclick actions for menu items
     * @param item
     * @return boolean
     */
    public boolean onMenuItemClick(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_home:
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                return true;

            case R.id.action_training:
                Intent trainingIntent = new Intent(this, TrainingActivity.class);
                startActivity(trainingIntent);
                return true;

            case R.id.action_tech_meetups:
                Intent techMeetupsIntent = new Intent(this, TechMeetupsActivity.class);
                startActivity(techMeetupsIntent);
                return true;

            case R.id.action_setup:
                Intent setupIntent = new Intent(this, SetupActivity.class);
                startActivity(setupIntent);
                return true;

            default:
                // If here these has been an issue
                return false;
        }
    }

    /**
     * Method used to create new TextView on click on ask question button
     * @param text
     * @return textView
     */
    private TextView createNewTextView(String text){
        final LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);
        textView.setText(text);
        textView.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        //textView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.img_java_bot_foreground, 0, 0 ,0);
        return textView;
    }

    /**
     * Method to evaluate the question being asked and provide a response, including opening the solution activity
     * @param question
     */
    private void getChatBotResponse(String question){
        String response = "";
        String questionType = "";
        question = question.toLowerCase();

        // Get response to the users question
        response = cb.askQuestion(question);
        questionType = cb.getSolutionType();

        // If question is not a solution type, e.g. not a programming question
        if(questionType != "solution question") {
            layout.addView(createNewTextView(response));
        }else{
            // If question is a programming one e.g. 'how do I parse an int?'
            Intent solutionIntent = new Intent(this, SolutionActivity.class);
            solutionIntent.putExtra("solution", response);
            solutionIntent.putExtra("initialQuestion", initialQuestion);
            startActivity(solutionIntent);
        }

    }

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
            //Build the notification
            notification.setSmallIcon(R.drawable.ic_launcher_foreground);
            notification.setWhen(System.currentTimeMillis());
            notification.setContentTitle(getString(R.string.training_session_notification_title));
            notification.setContentText(getString(R.string.training_session_notification_content));

            // Onclick of notification, go to the training activity
            Intent trainingIntent = new Intent(this, TrainingActivity.class);
            // Give phone access to intent
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, trainingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setContentIntent(pendingIntent);

            // Build notification and issues it
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(uniqueID, notification.build());
        }
    }

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

        if(id == R.id.action_settings){
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
            Intent searchIntent = new Intent(MainActivity.this, TrainingActivity.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
        else if (id == R.id.tech_meetups){
            Intent searchIntent = new Intent(MainActivity.this, TechMeetupsActivity.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
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

}
