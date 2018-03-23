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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TechMeetupsActivity extends AppCompatActivity implements View.OnClickListener, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, android.widget.PopupMenu.OnMenuItemClickListener {

    //GET LOCATION VARIABLE
    private GoogleApiClient googleAPIClient;
    private LocationManager locationManager;
    private Double latitude;
    private Double longitude;
    private final int REQUEST_LOCATION = 1;

    // GET ADDRESS VARIABLES
    protected Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastKnownLocation;
    private TextView userAddress;

    private TextView apiResponse;

    private ImageView menu;

    private TechMeetupsAPIHelper techMeetupsAPIHelper;

    private String currentCity;
    private TextView cityField;

    private LinearLayout layout;

    // Progress Bar
    // setLoodingProgressBarVisibility() allows this variable to be easily set by other classes
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_meetups);

        // TODO - implement permissions properly
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 5 );
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 5);

        latitude = 0.0;
        longitude = 0.0;

        setupUIViews();

        menu.setOnClickListener(this);

        mFusedLocationClient = new FusedLocationProviderClient(this);

        techMeetupsAPIHelper = new TechMeetupsAPIHelper(this);

        if(currentCity.isEmpty()){
            techMeetupsAPIHelper.getTechMeetups("unknown");
        }
        else{
            techMeetupsAPIHelper.getTechMeetups("" + currentCity);
        }

        getLocation();
        fetchAddressHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupUIViews();
    }

    private void setupUIViews() {
        userAddress = (TextView) findViewById(R.id.tech_meetups_txt_user_address);
        if(this.getIntent().hasExtra("address")){
            userAddress.setText(this.getIntent().getExtras().getString("address", "unknown"));
        }


        cityField = (TextView) findViewById(R.id.tech_meetups_txt_city);
        currentCity = "";

        if(this.getIntent().hasExtra("city")){
            //currentCity = (this.getIntent().getExtras().getString("city", "unknown"));
            cityField.setText(this.getIntent().getExtras().getString("city", "unknown"));
            currentCity = cityField.toString();
        }


        menu = findViewById(R.id.tech_meetups_img_menu);
        apiResponse = findViewById(R.id.tech_meetups_txt_api_response);

        loadingProgressBar = (ProgressBar) findViewById(R.id.tech_meetups_pb_progress_bar);
        loadingMessage = (TextView) findViewById(R.id.tech_meetups_txt_error_message);

        layout = findViewById(R.id.tech_meetups_ll_meetup_layout);

    }

    public void populateAPIResponse(String response){

        /**
         //Reference - https://stackoverflow.com/questions/11579693/how-to-get-json-data-from-php-server-to-android-mobile#11579742
         */

        ArrayList<TechMeetup> meetupArray = new ArrayList<TechMeetup>();

        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            int size = jsonArray.length();

            for (int i = 0; i < size; i++) {
                JSONObject meetupObject = jsonArray.getJSONObject(i);

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

                    //String slug = meetupObject.getString("slug");

                    // Get start time
                    String date = meetupObject.getString("start");
                    JSONObject dateJSONObject = new JSONObject(date);
                    String startDate = dateJSONObject.getString("displaylocal");

                    TechMeetup techMeetup = new TechMeetup(summary, city, description, startDate, url);

                    meetupArray.add(techMeetup);

                }

            }

        }
        catch(JSONException e){
            e.printStackTrace();
        }

        for(TechMeetup tm : meetupArray){
            String summary = tm.getSummary();
            String city = tm.getCity();
            String description = tm.getDescription();
            String date = tm.getDate();
            String url = tm.getUrl();
            AddTechMeetupView(summary, city, description, date, url);
        }

    }

    private void AddTechMeetupView(String summary, String city, String description, String date, String url){
        layout.addView(createNewTextView(summary.toString(), "header"));
        layout.addView(createNewTextView(city.toString(), "city"));
        layout.addView(createNewTextView(date.toString(), "date"));
        layout.addView(createNewTextView(url.toString(), "url"));
        //layout.addView(createNewTextView(description.toString(), "content"));

    }

    private TextView createNewTextView(String text, String type){
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);

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

        textView.setTextColor(Color.BLACK);
        //textView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.img_java_bot_foreground, 0, 0 ,0);
        return textView;
    }

    @Override
    public void onClick(View view){
      if (view.getId() == R.id.tech_meetups_img_menu){
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


    //-------------------------------Get Location Methods-------------------------------//
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

        // DEBUG - Output location as toast
        // TODO - remove debug toast
        if(lastLocation != null) {
            // For address lookup - DEBUG
            mLastLocation = lastLocation;

            latitude = lastLocation.getLatitude();
            longitude = lastLocation.getLongitude();
        }
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
    protected void onStop() {
        // On app stop, disconnect Google API
        googleAPIClient.disconnect();

        // Empty location manager updates
        locationManager.removeUpdates(this);
        super.onStop();
    }


    //--------------------------Get Address from Location Methods-----------------------//

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressService.class);
        //intent.putExtra(FetchAddressService.Constants.RECEIVER, mResultReceiver);
        intent.putExtra(FetchAddressService.Constants.LOCATION_DATA_EXTRA, mLastKnownLocation);
        startService(intent);
    }


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
                        startIntentService();
                    }
                });
    }


}
