package abertay.ac.uk.java_bot_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

public class TrainingActivity extends AppCompatActivity implements View.OnClickListener, android.widget.PopupMenu.OnMenuItemClickListener, GestureDetector.OnGestureListener {

    private ImageView menu;

    private GestureDetectorCompat gestureDetector;

    private QuestionsSQLiteDatabaseHelper questionsDatabase;
    private TextView questionHeader;
    private TextView currentQuestion;
    private TextView solutionHeader;
    private TextView currentSolution;
    private TextView trainingMessage;
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

        }

    };

    @SuppressLint("HandlerLeak")
    Handler emptyDbHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            setUIElementsVisiability(false);
            trainingMessage.setText(R.string.training_complete_message);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        setupUIViews();

        menu.setOnClickListener(this);

        this.gestureDetector = new GestureDetectorCompat(this, this);

        questionsDatabase = new QuestionsSQLiteDatabaseHelper(this);

        questionCounter = 0;


        // used for setting elements to invisible if no questions to show
        // message displaying this will be set to visible
        //setUIElementsVisiability(false);


        //questionsDatabase.emptyDatabase();

        //questionsDatabase.addQuestion(new Question("test solution key", "test solution"));
        //questionsDatabase.addQuestion(new Question("test solution key 2", "test solution 2"));
        //questionsDatabase.addQuestion(new Question("test solution key 3", "test solution 3"));

        // Populate question list with the current weeks questions asked
        questionsList = questionsDatabase.getQuestions();
        questionCounter = 0;

        showNextQuestion();

    }

    private void setupUIViews(){
        menu = findViewById(R.id.training_img_menu);
        questionHeader = findViewById(R.id.training_txt_question_header);
        currentQuestion = findViewById(R.id.training_txt_question);
        solutionHeader = findViewById(R.id.training_txt_solution_header);
        currentSolution = findViewById(R.id.training_txt_solution);
        trainingMessage = findViewById(R.id.training_txt_training_message);
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.training_img_menu){
            showPopup(view);
        }
    }

    private void showNextQuestion(){

            if (questionsList.size() > 0) {

                // Account for -1 errors
                if (questionCounter > questionsList.size() - 1) {

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

                    setUIElementsVisiability(true);
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
                setUIElementsVisiability(false);
                trainingMessage.setText("No questions asked yet");
            }

    }

    private void setUIElementsVisiability(Boolean visibility){

        if(visibility == false) {
            questionHeader.setVisibility(View.INVISIBLE);
            currentQuestion.setVisibility(View.INVISIBLE);
            solutionHeader.setVisibility(View.INVISIBLE);
            currentSolution.setVisibility(View.INVISIBLE);
            trainingMessage.setVisibility(View.VISIBLE);
        }else{
            questionHeader.setVisibility(View.VISIBLE);
            currentQuestion.setVisibility(View.VISIBLE);
            currentQuestion.setVisibility(View.VISIBLE);
            currentSolution.setVisibility(View.VISIBLE);
            trainingMessage.setVisibility(View.INVISIBLE);
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

    //-------------------------------Gesture Detector Methods---------------------------------------//

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

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
}
