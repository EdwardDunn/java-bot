package abertay.ac.uk.java_bot_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class TrainingActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        setupUIViews();

    }

    private void setupUIViews(){

    }

    @Override
    public void onClick(View view){
            // TODO - if not click listeners needed - delete
    }

}
