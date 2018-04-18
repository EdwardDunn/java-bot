/**
 * SetupActivity
 * The SetupActivity is used to allow the user to disable notifications and delete any training data
 * held.
 *
 * @author  Edward Dunn
 * @version 1.0
 */

package abertay.ac.uk.java_bot_app;

import android.Manifest;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

    // These permissions are required for the menu (if going to the training or tech meetups activity check permissions)
    public static final int PERMISSIONS_LOCATION_REQUEST = 1;
    public static final int PERMISSIONS_EXTERNAL_STORAGE_REQUEST = 2;

    // Switch used to turn on or off notifications
    private Switch notificationsSwitch;

    //Used to delete data from questions SQLite database
    private Button clearDataBtn;

    // SQLite database used to hold training questions data
    private QuestionsSQLiteDatabaseHelper questionsDatabase;

    // Used to cancel any current notifications is notifications are turned off
    private NotificationManager nm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        setupUIViews();

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

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

    @Override
    protected void onPause(){
        super.onPause();
    }

    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        // Stops all background threads
        Thread[] backGroundThreads = new Thread[Thread.activeCount()];
        Thread.enumerate(backGroundThreads);
        for (Thread activeThread : backGroundThreads) {
            activeThread.interrupt();
        }
    }

    /**
     * Method used to instantiate activity elements
     */
    private void setupUIViews(){
        notificationsSwitch = findViewById(R.id.setup_txt_notifications_switch);

        // Set notifications switch to checked is notifications are allowed
        if(MainActivity.notifications == true){
            notificationsSwitch.setChecked(true);
        }

        clearDataBtn = findViewById(R.id.setup_btn_clear_data);
    }

    /**
     * Method used to handle onClick events
     */
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.setup_txt_notifications_switch){
            setNotificationsSwitch();
        }
        else if(view.getId() == R.id.setup_btn_clear_data){
            openDialog(view);
        }
    }

    /**
     * Method used to show dialog box for alerting the user that all training data will be deleted
     */
    public void openDialog(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        alertDialogBuilder.setMessage(R.string.remove_training_data_message)
                            .setTitle(R.string.training_data_title)
                            .setIcon(R.drawable.icon_warning);

        alertDialogBuilder.setPositiveButton(R.string.alert_dialog_training_positive,
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

        alertDialogBuilder.setNegativeButton(R.string.alert_dialog_training_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Method used to set the value of the notifications switch
     */
    private void setNotificationsSwitch(){
        Boolean switchState = notificationsSwitch.isChecked();
        MainActivity.setNotificationsOnOrOff(switchState);

        cancelNotifications();
    }

    /**
     * Method used to cancel all notifications if the notifications switched
     */
    public void cancelNotifications(){
        // Cancel any notifications
        nm.cancelAll();

        // Remove app icon badges
        AppIconBadgeSetter iconBadge = new AppIconBadgeSetter();
        iconBadge.removeAllBadges(this);
    }

    /**
     * Method used to delete the training data held in the question SQLite datavbase
     */
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

    /**
     * Method used to control the opening and closing of drawer menu
     */
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

    /**
     * Method used to create options menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    /**
     * Method used to provide a refresh button in the options menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.refresh){
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method used to set options items in drawer menu
     */
    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.home){
            Intent searchIntent = new Intent(SetupActivity.this, MainActivity.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
        else if (id == R.id.training){
            requestStoragePermissions();
        }
        else if (id == R.id.tech_meetups){
            // Check network state, if no internet connection display dialog
            CheckConnection checkCon = new CheckConnection(this);
            Boolean connected = checkCon.checkConnection();

            // Only go to TechMeetupsActivity if connected to internet
            if(connected) {
                requestLocationsPermissions();
            }
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

    //-----------------------------Request Permissions Methods------------------------------------//
    /**
     * Method used to check location permissions and request if needed
     */
    private void requestLocationsPermissions(){
        // Check for fine and coarse location permissions
        if (ContextCompat.checkSelfPermission(SetupActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) + ContextCompat
                .checkSelfPermission(SetupActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (SetupActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (SetupActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show snackbar with rationale for needing permissions
                Snackbar.make(SetupActivity.this.findViewById(android.R.id.content),
                        "Please grant permissions to enable tech meetups near you to be shown",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestPermissions(
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSIONS_LOCATION_REQUEST);
                            }
                        }).show();

            } else {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_LOCATION_REQUEST);
            }
        } else {
            // Go to TechMeetups activity
            Intent techMeetupsIntent = new Intent(SetupActivity.this, TechMeetupsActivity.class);
            startActivity(techMeetupsIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
    }

    /**
     * Method used to check storage permissions and request if needed
     */
    private void requestStoragePermissions(){
        // Check for fine and coarse location permissions
        if (ContextCompat.checkSelfPermission(SetupActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show snackbar with rationale for needing permissions
                Snackbar.make(SetupActivity.this.findViewById(android.R.id.content),
                        "Please grant permissions to be able to use the training features",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestPermissions(
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        PERMISSIONS_EXTERNAL_STORAGE_REQUEST);
                            }
                        }).show();

            } else {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_EXTERNAL_STORAGE_REQUEST);
            }
        } else {
            // Go to TechMeetups activity
            Intent trainingIntent = new Intent(SetupActivity.this, TrainingActivity.class);
            startActivity(trainingIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
    }

    /**
     * Method used to show rationale for requested permisions and to re-request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Go to TechMeetups activity
                    Intent searchIntent = new Intent(SetupActivity.this, TechMeetupsActivity.class);
                    startActivity(searchIntent);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                } else {
                    // Ask again for permissions
                    Snackbar.make(SetupActivity.this.findViewById(android.R.id.content),
                            "Please grant permissions to enable tech meetups near you to be shown",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestPermissions(
                                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                            PERMISSIONS_LOCATION_REQUEST);
                                }
                            }).show();
                }
                return;
            }
            case PERMISSIONS_EXTERNAL_STORAGE_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Go to TechMeetups activity
                    Intent trainingIntent = new Intent(SetupActivity.this, TrainingActivity.class);
                    startActivity(trainingIntent);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                } else {
                    // Ask again for permissions
                    Snackbar.make(SetupActivity.this.findViewById(android.R.id.content),
                            "Please grant permissions to be able to use the training features",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestPermissions(
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            PERMISSIONS_EXTERNAL_STORAGE_REQUEST);
                                }
                            }).show();
                }
                return;
            }
        }

    }

}
