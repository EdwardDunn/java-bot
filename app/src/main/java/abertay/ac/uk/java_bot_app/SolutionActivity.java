package abertay.ac.uk.java_bot_app;

/**
 * SolutionActivity
 * The SolutionActivity provides a solution fetched from the database and displays
 * it to the user with a small chat window at the bottom for comments on solution.
 *
 **
 * @author  Edward Dunn
 * @version 1.0
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SolutionActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView solutionText;
    private LinearLayout layout;
    private String SUCCESS_QUESTION = "Did that help?";
    private Button ask_btn;
    private EditText question_field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution);

        setupUIViews();

        ask_btn.setOnClickListener(this);

        showSolution();

        layout.addView(createNewTextView(SUCCESS_QUESTION));

    }

    private void setupUIViews(){
        solutionText = findViewById(R.id.solution_txt_solution);
        layout = findViewById(R.id.solution_ll_question_layout);
        ask_btn = findViewById(R.id.solution_btn_ask);
        question_field = findViewById(R.id.solution_et_question_field);
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
            // Create new TextView with text entered into question field in questions layout
            String question = "";
            question  = question_field.getText().toString();
            getChatBotResponse(question);
            question_field.setText("");

        }else{
            // TODO - populate with other UI clicks
        }
    }

    private void showSolution(){
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        String passedSolution = data.getString("solution");
        solutionText.setText(passedSolution);
    }

    private TextView createNewTextView(String text){
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);
        textView.setText(text);
        return textView;
    }

    private void getChatBotResponse(String question){
        // If question has not value assigned 'empty' to it

        String response = "";
        String questionType = "";

        question = question.toLowerCase();

        /*ChatBot cb = new ChatBot();
        response = cb.askQuestion(question);
        questionType = cb.getQuestionType();*/

        if (question.contains("no") || question.contains("not")) {

            String notSuccessfulResponse = "Sorry, let me check Stack Overflow";
            layout.addView(createNewTextView(notSuccessfulResponse));

            // TODO - show Stack Overflow page
        }
        else if(question.contains("yes") || question.contains("yeah") || question.contains("yep")){
            String successfulResponse = "Great!";
            layout.addView(createNewTextView(successfulResponse));
        }

        /*if(questionType != "solution question") {
            layout.addView(createNewTextView(response));
        }else{
            Intent solutionIntent = new Intent(this, SolutionActivity.class);
            solutionIntent.putExtra("solution", response);
            startActivity(solutionIntent);
        }*/


    }

}
