package abertay.ac.uk.java_bot_app;

import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Edward Dunn on 06/04/2018.
 */

public class DismissNotification extends AppCompatActivity {

    private NotificationManager nm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancelAll();

    }
}
