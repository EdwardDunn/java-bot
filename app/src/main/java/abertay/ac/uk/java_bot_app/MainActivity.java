package abertay.ac.uk.java_bot_app;

/**
 * MainActivity
 * The MainActivity class provides the chat bot features to the app.
 * The user can enter a question and a response is calculated and sent back.
 **
 * @author  Edward Dunn
 * @version 1.0
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout layout;
    private Button ask_btn;
    private EditText question_field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUIViews();

        ask_btn.setOnClickListener(this);

        // Initiates chat bot with param indicating start of chat
        getChatBotResponse("onstart");
    }

    private void setupUIViews(){
        layout = findViewById(R.id.main_ll_question_layout);
        ask_btn = findViewById(R.id.main_btn_ask);
        question_field = findViewById(R.id.main_et_question_field);
    }

    public void onPause(){
        super.onPause();
    }

    public void onResume(){
        super.onResume();
        // Display 'welcome back' chat bot message when resumed
        getChatBotResponse("resumed");
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.main_btn_ask){
            // Create new TextView with text entered into question field in questions layout
            String question = "";
            question  = question_field.getText().toString();
            getChatBotResponse(question);
            question_field.setText("");

        }else{
            // TODO - populate with other UI clicks
        }
    }

    /**
     * Method used to create new TextView on click on ask question button
     * @param text
     * @return TextView
     */
    private TextView createNewTextView(String text){
        final LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);
        textView.setText(text);
        return textView;
    }

    /**
     * Method to evaluate the question being asked and provide a response, including opening the solution activity
     * @param question
     */
    private void getChatBotResponse(String question){
        // If question has not value assigned 'empty' to it

        String response = "";

        question = question.toLowerCase();

        ChatBot cb = new ChatBot();
        response = cb.askQuestion(question);

        layout.addView(createNewTextView(response));

       /*if(question.contains("onstart")){
            layout.addView(createNewTextView("Hey, how can I help?"));
        }
        else if(question.contains("resumed")){
            layout.addView(createNewTextView("Welcome back!"));
        }
        else if(question.contains("parse") && question.contains("int")) {
           layout.addView(createNewTextView("Let me chat for a solution"));

           // TODO - open SolutionActivity to display answer - pass in "parse int"
        }*/

    }
}
