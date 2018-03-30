package abertay.ac.uk.java_bot_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

public class SetupActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

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

        //--------------------------------Drawer Menu---------------------------------------------//
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toogle);
        toogle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void setupUIViews(){
        notificationsSwitch = findViewById(R.id.setup_txt_notifications_switch);

        // Set notifications switch to check is notifications are allowed

        //TODO -fix
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

    // Alert Dialog Box
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

    //----------------------------Drawer Menu Methods---------------------------------------------//

    @Override
    public void onBackPressed(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_settings){
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.home){
            Intent searchIntent = new Intent(SetupActivity.this, MainActivity.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
        else if (id == R.id.training){
            Intent searchIntent = new Intent(SetupActivity.this, TrainingActivity.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
        else if (id == R.id.tech_meetups){
            Intent searchIntent = new Intent(SetupActivity.this, TechMeetupsActivity.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
        else if(id == R.id.setup){
            Intent searchIntent = new Intent(SetupActivity.this, SetupActivity.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
