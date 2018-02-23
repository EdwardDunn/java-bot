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
import android.widget.TextView;

public class SolutionActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView solution;
    String test = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution);

        setupUIViews();



    }

    private void setupUIViews(){
        solution = findViewById(R.id.solution_txt_solution);
    }

    protected void onPause(){
        super.onPause();
    }

    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onClick(View view) {

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        test = data.getString("solution");

    }


}
