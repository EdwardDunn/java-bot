package abertay.ac.uk.java_bot_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

public class TrainingActivity extends AppCompatActivity implements View.OnClickListener, android.widget.PopupMenu.OnMenuItemClickListener {

    private ImageView menu;

    private QuestionsSQLiteDatabaseHelper questionsDatabase;
    private Button showQuestionBtn;
    private TextView currentQuestion;
    private TextView currentSolution;
    private ArrayList<Question> questionsList;
    private int questionCounter;

    // TODO - sort this error
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

            questionCounter++;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        setupUIViews();

        menu.setOnClickListener(this);

        showQuestionBtn.setOnClickListener(this);

        questionsDatabase = new QuestionsSQLiteDatabaseHelper(this);

        //questionsDatabase.emptyDatabase();

        questionsDatabase.addQuestion(new Question("test solution key", "test solution"));
        questionsDatabase.addQuestion(new Question("test solution key 2", "test solution 2"));
        questionsDatabase.addQuestion(new Question("test solution key 3", "test solution 3"));

        // Populate question list with the current weeks questions asked
        questionsList = questionsDatabase.getQuestions();
        questionCounter = 0;


    }

    private void setupUIViews(){
        menu = findViewById(R.id.training_img_menu);
        showQuestionBtn = findViewById(R.id.training_btn_show_question);
        currentQuestion = findViewById(R.id.training_txt_question);
        currentSolution = findViewById(R.id.training_txt_solution);
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.training_img_menu){
            showPopup(view);
        }
        else if(view.getId() == R.id.training_btn_show_question){
            showNextQuestion();
        }
    }

    private void showNextQuestion(){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                String questionToShow = "";
                String solutionToShow = "";

                Question q = questionsList.get(questionCounter);

                questionToShow = q.getSolutionKey();
                solutionToShow = q.getSolution();

                Message message = questionHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("question", questionToShow);
                bundle.putString("solution", solutionToShow);
                message.setData(bundle);
                questionHandler.sendMessage(message);

            }
        };

        Thread showQuestionThread = new Thread(r);
        showQuestionThread.start();
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
    public boolean onMenuItemClick(MenuItem item) {
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
}
