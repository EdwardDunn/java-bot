package abertay.ac.uk.java_bot_app;

/**
 * MainActivity
 * The MainActivity class provides the chat bot features to the app.
 * The user can enter a question and a response is calculated and sent back.
 **
 * @author  Edward Dunn
 * @version 1.0
 */

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, android.widget.PopupMenu.OnMenuItemClickListener
{
    private LinearLayout layout;
    private Button ask_btn;
    private EditText question_field;
    private ImageView menu;
    private ImageView optionsMenu;

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

        optionsMenu.setOnClickListener(this);

        // Initiates ChatBot with param indicating start of chat
        getChatBotResponse("onstart");

        // DEBUG - used for testing
        // TODO - remove
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);

        //---------------Training Session Notification--------------///

        // Initialise notification object
        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);

        // This object is used to extend the timer class
        NotificationTimer notificationTask = new NotificationTimer();

        // Create timer object
        Timer notificationTimer = new Timer();

        // TODO - for completed app set time to 7 days (604800000 milliseconds)
        // For demonstration purposes 2 minutes is used (120000 milliseconds)
        notificationTimer.schedule(notificationTask, 5000, 120000);
    }

    protected void onPause(){
        super.onPause();
    }

    protected void onResume(){
        super.onResume();
    }

    private void setupUIViews(){
        layout = findViewById(R.id.main_ll_question_layout);
        ask_btn = findViewById(R.id.main_btn_ask);
        question_field = findViewById(R.id.main_et_question_field);
        menu = findViewById(R.id.main_img_menu);
        optionsMenu = findViewById(R.id.main_img_options_menu);
        questionCounter = 0;
        initialQuestion = "";
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.main_btn_ask){
            // Used to set initialQuestion variable
            questionCounter++;

            // Create new TextView with text entered into question field in questions layout
            String question = "";
            question  = question_field.getText().toString();

            if(questionCounter == 1){
                initialQuestion = question;
            }

            getChatBotResponse(question);
            question_field.setText("");

        }else if (view.getId() == R.id.main_img_menu){
            showPopup(view);
        }else if (view.getId() == R.id.main_img_options_menu){
            // Show options menu here
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
       MenuInflater inflater = getMenuInflater();
       inflater.inflate(R.menu.options_menu, menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Take item click
        switch (item.getItemId()){
            case R.id.action_refresh:
                //Refresh page
                Intent refresh = new Intent(this, MainActivity.class);
                startActivity(refresh);
                this.finish();

             default:
                 return super.onOptionsItemSelected(item);
        }
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

        // Create a new ChatBot object for getting response to the users question
        ChatBot cb = new ChatBot();
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
     * Generate notification for alerting user that weeks training session is available
     */
    public void generateNotification(){
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
