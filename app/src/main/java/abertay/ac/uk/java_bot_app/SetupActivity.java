package abertay.ac.uk.java_bot_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Switch;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener {

    private Switch notificationsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        setupUIViews();

        notificationsSwitch.setOnClickListener(this);

    }

    private void setupUIViews(){
        notificationsSwitch = (Switch) findViewById(R.id.setup_txt_notifications_switch);
        if(MainActivity.notifications == true){
            notificationsSwitch.setChecked(true);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.setup_txt_notifications_switch){
            setSwitch();
        }
    }

    private void setSwitch(){
        Boolean switchState = notificationsSwitch.isChecked();

        MainActivity.setNotificationsOnOrOff(switchState);
    }
}
