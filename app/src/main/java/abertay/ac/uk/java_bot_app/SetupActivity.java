package abertay.ac.uk.java_bot_app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener {

    private Switch notificationsSwitch;
    //Used to delete data from questions SQLite database
    private Button clearDataBtn;

    private QuestionsSQLiteDatabaseHelper questionsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        setupUIViews();

        notificationsSwitch.setOnClickListener(this);
        clearDataBtn.setOnClickListener(this);

        questionsDatabase = new QuestionsSQLiteDatabaseHelper(this);

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
            openDialog(view);
        }
    }

    public void openDialog(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        alertDialogBuilder.setMessage(R.string.remove_training_data_message)
                            .setTitle(R.string.training_data_title)
                            .setIcon(R.drawable.icon_warning);

        alertDialogBuilder.setPositiveButton(R.string.alert_dialog_positive,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try{
                            removeTrainingSessionData();
                            Toast.makeText(SetupActivity.this, R.string.training_data_deleted, Toast.LENGTH_SHORT).show();
                        }catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(SetupActivity.this, R.string.problem_deleting_training_data, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        alertDialogBuilder.setNegativeButton(R.string.alert_dialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
        Runnable emptyDbRunnable = new Runnable() {
            @Override
            public void run() {
                // Delete data from SQLite database
                questionsDatabase.emptyDatabase();
            }
        };
        Thread emptyDbThread = new Thread(emptyDbRunnable);
        emptyDbThread.start();
    }
}
