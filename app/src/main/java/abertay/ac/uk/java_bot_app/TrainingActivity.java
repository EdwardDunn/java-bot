package abertay.ac.uk.java_bot_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

public class TrainingActivity extends AppCompatActivity implements View.OnClickListener, android.widget.PopupMenu.OnMenuItemClickListener {

    private ImageView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        setupUIViews();

        menu.setOnClickListener(this);

    }

    private void setupUIViews(){
        menu = findViewById(R.id.training_img_menu);
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.training_img_menu){
            showPopup(view);
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
}
