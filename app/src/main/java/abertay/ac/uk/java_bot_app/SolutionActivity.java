package abertay.ac.uk.java_bot_app;

/**
 * SolutionActivity
 * The SolutionActivity provides a solution fetched from the database and displays
 * it to the user with a small chat window at the bottom for comments on solution.
 *
 * @author  Edward Dunn
 * @version 1.0
 */

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SolutionActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView solutionText;
    private LinearLayout layout;
    private String SUCCESS_QUESTION = "Did that help?";
    private Button ask_btn;
    private EditText question_field;
    private String solution;
    private String initialQuestion;

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
        solution = "";
        initialQuestion = "";
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
            layout.addView(createNewTextView(question));
            question_field.setText("");
            getChatBotResponse(question);

        }
    }

    private void showSolution(){
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        String passedSolution = data.getString("solution");
        initialQuestion = data.getString("initialQuestion");
        solution = passedSolution;
        solutionText.setText(passedSolution);
    }

    private TextView createNewTextView(String text){
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);
        textView.setText(text);
        textView.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        return textView;
    }

    private void getChatBotResponse(String question){
        String response = "";
        String questionType = "";

        question = question.toLowerCase();

        ChatBot cb = new ChatBot();
        response = cb.checkResponseToQuestion(question);
        questionType = cb.getSolutionType();

        // If response from user is not another question
        if(questionType != "solution question") {
            // If the returned response from ChatBot equals user is haapy with solution
            if (response == "Happy to help!") {
                layout.addView(createNewTextView(response));
            }else{
                // If user is not happy with response, search Stack Overflow with initial question
                layout.addView(createNewTextView(response));
                Intent stackOverflowIntent = new Intent(this, StackOverflowActivity.class);
                stackOverflowIntent.putExtra("url", "www.stackoverflow.com/search?q=" + initialQuestion + " in java" );
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

}
