package abertay.ac.uk.java_bot_app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener {

    private Switch notificationsSwitch;
    //Used to delete data from questions SQLite database
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

        // Set notifications switch to check is notifications are allowed
        if(MainActivity.notifications == true){
            notificationsSwitch.setChecked(true);
        }

        clearDataBtn = findViewById(R.id.setup_btn_clear_data);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.setup_txt_notifications_switch){
            setNotificationsSwitch();
        }
        else if(view.getId() == R.id.setup_btn_clear_data){
            removeTrainingSessionData();
            openDialog(view);
        }
    }

    public void openDialog(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to do this");

        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(SetupActivity.this, "you clicked yes", Toast.LENGTH_SHORT).show();
                    }
                });

        alertDialogBuilder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(SetupActivity.this, "you clicked no", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void setNotificationsSwitch(){
        Boolean switchState = notificationsSwitch.isChecked();
        MainActivity.setNotificationsOnOrOff(switchState);
    }

    private void removeTrainingSessionData(){
        QuestionsSQLiteDatabaseHelper questionsDatabase = new QuestionsSQLiteDatabaseHelper(this);
        // Delete data from SQLite database
        questionsDatabase.emptyDatabase();
        Toast.makeText(this, "Training session data removed", Toast.LENGTH_SHORT).show();
    }
}
