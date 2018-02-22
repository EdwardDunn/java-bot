/**
 * MainActivity
 * The MainActivity class provides the chat bot features to the app.
 * The user can enter a question and a response is calculated and sent back.
 **
 * @author  Edward Dunn
 * @version 1.0
 */

package abertay.ac.uk.java_bot_app;

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
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.main_btn_ask){
            // Create new TextView with text entered into question field in questions layout
            String question = "";
            question  = question_field.getText().toString();
            getChatBotResponse(question);

        }else{

        }
    }

    /**
     * Function used to create new TextView on click on ask question button
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

    private void getChatBotResponse(String question){
        // If question has not value assigned 'empty' to it

        question = question.toLowerCase();

       if(question.contains("onstart")){
            layout.addView(createNewTextView("Hey, how can I help?"));
        }
        else if(question.contains("parse") && question.contains("int")) {
            layout.addView(createNewTextView("int b = Integer.parseInt(\"444\",16);"));
        }

    }
}
