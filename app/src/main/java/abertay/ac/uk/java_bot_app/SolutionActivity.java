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

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SolutionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution);
    }
}
