package abertay.ac.uk.java_bot_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener {

    private Switch notificationsSwitch;
    private Button clearDataBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        setupUIViews();

        notificationsSwitch.setOnClickListener(this);
        clearDataBtn.setOnClickListener(this);

    }

    private void setupUIViews(){
        notificationsSwitch = findViewById(R.id.setup_txt_notifications_switch);
        if(MainActivity.notifications == true){
            notificationsSwitch.setChecked(true);
        }

        clearDataBtn = findViewById(R.id.setup_btn_clear_data);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.setup_txt_notifications_switch){
            setSwitch();
        }
        else if(view.getId() == R.id.setup_btn_clear_data){
            removeTrainingSessionData();
        }
    }

    private void setSwitch(){
        Boolean switchState = notificationsSwitch.isChecked();
        MainActivity.setNotificationsOnOrOff(switchState);
    }

    private void removeTrainingSessionData(){
        QuestionsSQLiteDatabaseHelper questionsDatabase = new QuestionsSQLiteDatabaseHelper(this);
        questionsDatabase.emptyDatabase();
        Toast.makeText(this, "Training session data removed", Toast.LENGTH_SHORT).show();
    }
}
