/**
 * TechMeetupsActivity
 * The TechMeetupsActivity is used to show tech meetups near the users. The OpenTechCalender API
 * is used to provide this information. The users location is requested and their current city
 * is used to show any meet ups in their area.
 *
 * References:
 * Parse JSON:
 *  https://stackoverflow.com/questions/11579693/how-to-get-json-data-from-php-server-to-android-mobile#11579742
 * Make text hyperlink:
 *  https://stackoverflow.com/questions/9290651/make-a-hyperlink-textview-in-android
 *
 * @author  Edward Dunn
 * @version 1.0
 */

package abertay.ac.uk.java_bot_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TechMeetupsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public TechMeetupsActivity(){
    }

    // Permissions request unique ids
    public static final int PERMISSIONS_LOCATION_REQUEST = 1;
    public static final int PERMISSIONS_EXTERNAL_STORAGE_REQUEST = 2;

    // Get location variables
    private GoogleApiClient googleAPIClient;
    private LocationManager locationManager;

    // Get address variables
    protected Location mLastLocation;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastKnownLocation;
    private static TextView userAddress;

    // Used to show a message for no tech meetups found in users city
    private static TextView cityNotFound;
    public void setCityNotFound(Boolean visible){
        if(visible) {
            cityNotFound.setText(R.string.no_meet_ups);
            cityNotFound.setVisibility(View.VISIBLE);
        }
    }

    // Response from Open Tech Calender API
    private TextView apiResponse;
    private TechMeetupsAPIHelper techMeetupsAPIHelper;

    // Current city retrieved from current location
    private static String currentCity;

    // Used to show meetups list
    private LinearLayout techMeetupsLayout;

    // Progress Bar
    // setLoadingProgressBarVisibility() allows this variable to be easily set by other classes
    public static ProgressBar loadingProgressBar;
    public void setLoadingProgressBarVisibility(Boolean visible){
        if(visible){
            loadingProgressBar.setVisibility(View.VISIBLE);
        }
        else{
            loadingProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    // Loading Message (when connection is not available
    // setLoadingMessageVisibility() allows this variable to be easily set by other classes
    private static TextView loadingMessage;
    public void setLoadingMessageVisibility(Boolean visible){
        if(visible){
            loadingMessage.setVisibility(View.VISIBLE);
        }
        else{
            loadingMessage.setVisibility(View.INVISIBLE);
        }
    }

    // Used by the FetchAddressService class to show the users current location
    public static void setLocation(String _address, String _city){
        // Show address on activity
        userAddress.setText(_address);
        currentCity = _city;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_meetups);

        setupUIViews();

        mFusedLocationClient = new FusedLocationProviderClient(this);

        // Get location
        getLocation();

        // Get address
        fetchAddressHandler();

        techMeetupsAPIHelper = new TechMeetupsAPIHelper(this);

        // DEBUG - TODO - remove
        //currentCity = "Glasgow";

        // Send the city retrieved from getLocation() to the techMeetupsAPIHelper
        // This will show meetups for current city or all meetups if none in users current city
        if(currentCity.isEmpty()){
            techMeetupsAPIHelper.getTechMeetups("unknown");
        }
        else{
            techMeetupsAPIHelper.getTechMeetups("" + currentCity);
        }

        //-----------------------Drawer menu---------------------------------------///
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

    @Override
    protected void onResume() {
        super.onResume();

        // Check internet connection status
        CheckConnection cn = new CheckConnection(this);
        cn.checkConnection();
    }

    @Override
    protected void onStop() {
        // On app stop, disconnect Google API
        googleAPIClient.disconnect();

        // Empty location manager updates
        locationManager.removeUpdates(this);
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    /**
     * Method used to instantiate activity elements
     */
    private void setupUIViews() {
        userAddress = (TextView) findViewById(R.id.tech_meetups_txt_user_address);
        currentCity = "";
        apiResponse = findViewById(R.id.tech_meetups_txt_api_response);
        loadingProgressBar = (ProgressBar) findViewById(R.id.tech_meetups_pb_progress_bar);
        loadingMessage = (TextView) findViewById(R.id.tech_meetups_txt_loading_message);
        cityNotFound = findViewById(R.id.tech_meetups_txt_city_not_found);
        // Set to invisible by default, called by FetchAddressService to visible if not meetups are found for your city

        techMeetupsLayout = findViewById(R.id.tech_meetups_ll_meetup_layout);
    }

    /**
     * Method used to show the response from the Open Tech Calender API
     */
    public void populateAPIResponse(String response){
        // Holds list of meetups returned from the Open Tech Calendar API
        ArrayList<TechMeetup> meetupArray = new ArrayList<TechMeetup>();

        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            int size = jsonArray.length();

            for (int i = 0; i < size; i++) {
                JSONObject meetupObject = jsonArray.getJSONObject(i);

                // Only display results that have a url and areas (for address)
                Boolean meetupHasURL = meetupObject.has("url");
                Boolean meetupHasAreas = meetupObject.has("areas");

                if (meetupHasURL && meetupHasAreas) {

                    String summary = meetupObject.getString("summary");
                    String description = meetupObject.getString("description");
                    String url = meetupObject.getString("url");

                    // Get city
                    // The city in within an areas JSON array under the name title
                    String areasJSONString = meetupObject.getString("areas");
                    JSONArray areasJSONArray = new JSONArray(areasJSONString);
                    String city = "";
                    JSONObject areasJSONObject = areasJSONArray.getJSONObject(0);
                    city = areasJSONObject.getString("title");

                    // Get start time
                    String date = meetupObject.getString("start");
                    JSONObject dateJSONObject = new JSONObject(date);
                    String startDate = dateJSONObject.getString("displaylocal");

                    // Create TechMeetup object
                    TechMeetup techMeetup = new TechMeetup(summary, city, description, startDate, url);

                    meetupArray.add(techMeetup);
                }
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        // Display each meetup to the user
        for(TechMeetup tm : meetupArray){
            String summary = tm.getSummary();
            String city = tm.getCity();
            String description = tm.getDescription();
            String date = tm.getDate();
            String url = tm.getUrl();
            AddTechMeetupView(summary, city, description, date, url);
        }

    }

    /**
     * Method used to show the response from the Open Tech Calender API
     */
    private void AddTechMeetupView(String summary, String city, String description, String date, String url){
        techMeetupsLayout.addView(createNewTextView(summary.toString(), "header"));
        techMeetupsLayout.addView(createNewTextView(city.toString(), "city"));
        techMeetupsLayout.addView(createNewTextView(date.toString(), "date"));
        techMeetupsLayout.addView(createNewTextView(url.toString(), "url"));

        // Currently not used but maybe needed in the future
        //techMeetupsLayout.addView(createNewTextView(description.toString(), "content"));
    }

    /**
     * Method used to create a TextView for each tech meetup returned from the API
     */
    private TextView createNewTextView(String text, String type){
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        // Set font size depending on text type passed in
        if(type.equals("header")) {
            textView.setTextSize(18);
            textView.setText(text);
        }
        else if(type.equals("date")){
            textView.setTextSize(14);
            textView.setText(text);
        }
        else if (type.equals("city")){
            textView.setTextSize(16);
            textView.setText(text);
        }
        else if(type.equals("url")){
            textView.setTextSize(14);
            textView.setClickable(true);
            textView.setMovementMethod(LinkMovementMethod.getInstance());

            // Make url text a link
            text = "<a href='" + text + "'>Website</a>";

            // Set bottom padding
            textView.setPadding(0,0,0, 100);
            textView.setText(Html.fromHtml(text));
        }
        else{
            textView.setTextSize(14);
            // Place a new line at the end of the content
            textView.setText(text + "\n");
        }
        textView.setTextColor(Color.WHITE);
        return textView;
    }

    /**
     * Method used to handle onClick events
     */
    @Override
    public void onClick(View view){
    }

    //-------------------------------Get Location Methods-----------------------------------------//
    /**
     * Method used to instantiate the googleAPIClient
     */
    public void getLocation(){
        // if googleAPI client is null, initialise new instance
        if (googleAPIClient == null) {
            googleAPIClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Initialise new LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Connect to Google API
        googleAPIClient.connect();
    }

    /**
     * Method used to set criteria for the location manager
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        criteria.setCostAllowed(true);
        // Ensure location will still be request on batter POWER_LOW status
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);

        // Check for ACCESS_FINE_LOCATION permission
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(provider, 1000, 0, this);
        } else {
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }

        // Get last location
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleAPIClient);

        if(lastLocation != null) {
            // For address lookup - DEBUG
            mLastLocation = lastLocation;
        }
        else{
            startLocationUpdates();
        }
    }

    /**
     * Method used to update the location
     */
    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setFastestInterval(5000);

        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            getLocation();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleAPIClient,
                mLocationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onStart() {
        super.onStart();
        googleAPIClient.connect();
    }

    //--------------------------Get Address from Location Methods---------------------------------//
    /**
     * Method used to initialise the FetchAddressService class
     */
    protected void getAddressDetails() {
        FetchAddressService fas = new FetchAddressService(this, mLastKnownLocation);
    }

    /**
     * Method used to get the last known location of the user
     */
    private void fetchAddressHandler() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {

                    public void onSuccess(Location location) {
                        mLastKnownLocation = location;

                        // In some rare cases the location returned can be null
                        if (mLastKnownLocation == null) {
                            return;
                        }

                        if (!Geocoder.isPresent()) {
                            Toast.makeText(TechMeetupsActivity.this,
                                    R.string.no_geocoder_available,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Start service and update UI to reflect new location
                        getAddressDetails();
                    }
                });
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
            Intent searchIntent = new Intent(TechMeetupsActivity.this, MainActivity.class);
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
            Intent searchIntent = new Intent(TechMeetupsActivity.this, SetupActivity.class);
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
        if (ContextCompat.checkSelfPermission(TechMeetupsActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) + ContextCompat
                .checkSelfPermission(TechMeetupsActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (TechMeetupsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (TechMeetupsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show snackbar with rationale for needing permissions
                Snackbar.make(TechMeetupsActivity.this.findViewById(android.R.id.content),
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
            Intent techMeetupsIntent = new Intent(TechMeetupsActivity.this, TechMeetupsActivity.class);
            startActivity(techMeetupsIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
    }

    /**
     * Method used to check storage permissions and request if needed
     */
    private void requestStoragePermissions(){
        // Check for fine and coarse location permissions
        if (ContextCompat.checkSelfPermission(TechMeetupsActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (TechMeetupsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show snackbar with rationale for needing permissions
                Snackbar.make(TechMeetupsActivity.this.findViewById(android.R.id.content),
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
            Intent trainingIntent = new Intent(TechMeetupsActivity.this, TrainingActivity.class);
            startActivity(trainingIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
    }

    /**
     * Method used to show rationale for requested permissions and to re-request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Go to TechMeetups activity
                    Intent searchIntent = new Intent(TechMeetupsActivity.this, TechMeetupsActivity.class);
                    startActivity(searchIntent);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                } else {
                    // Ask again for permissions
                    Snackbar.make(TechMeetupsActivity.this.findViewById(android.R.id.content),
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
                    Intent trainingIntent = new Intent(TechMeetupsActivity.this, TrainingActivity.class);
                    startActivity(trainingIntent);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                } else {
                    // Ask again for permissions
                    Snackbar.make(TechMeetupsActivity.this.findViewById(android.R.id.content),
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
