package abertay.ac.uk.java_bot_app;

/**
 * MainActivity
 * The MainActivity class provides the chat bot features to the app.
 * The user can enter a question and a response is calculated and sent back.
 **
 * @author  Edward Dunn
 * @version 1.0
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toolbar;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, android.widget.PopupMenu.OnMenuItemClickListener
{
    /* Current issues
    * Tool bar not working for getSupportActionBar (MainActivity)
    * Items not sitting under toolbar
    * Remote DB not working (ChatBot and ChatBotRemoteDatabaseHelper)
     */


    private LinearLayout layout;
    private Button ask_btn;
    private EditText question_field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Toolbar customToolbar = (Toolbar) findViewById(R.id.customToolbar);
        setSupportActionBar(customToolbar);
        getSupportActionBar().setTitle("Java Bot");
        getSupportActionBar().setIcon(getDrawable(R.drawable.clip_art_desktop));*/

        setupUIViews();

        ask_btn.setOnClickListener(this);

        // Initiates chat bot with param indicating start of chat
        getChatBotResponse("onstart");
    }

    protected void onPause(){
        super.onPause();
    }

    protected void onResume(){
        super.onResume();
        // Display 'welcome back' chat bot message when resumed
        getChatBotResponse("resumed");
    }

    private void setupUIViews(){
        layout = findViewById(R.id.main_ll_question_layout);
        ask_btn = findViewById(R.id.main_btn_ask);
        question_field = findViewById(R.id.main_et_question_field);
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.main_btn_ask){
            // Create new TextView with text entered into question field in questions layout
            String question = "";
            question  = question_field.getText().toString();
            getChatBotResponse(question);
            question_field.setText("");
            showPopup(view);

        }else{
            // TODO - populate with other UI clicks
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

        ChatBot cb = new ChatBot();
        response = cb.askQuestion(question);
        questionType = cb.getSolutionType();

        if(questionType != "solution question") {
            layout.addView(createNewTextView(response));
        }else{
            Intent solutionIntent = new Intent(this, SolutionActivity.class);
            solutionIntent.putExtra("solution", response);
            startActivity(solutionIntent);
        }

    }
}
