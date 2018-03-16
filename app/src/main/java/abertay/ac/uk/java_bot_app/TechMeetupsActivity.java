package abertay.ac.uk.java_bot_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class TechMeetupsActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //GET LOCATION VARIABLE
    private GoogleApiClient googleAPIClient;
    private LocationManager locationManager;
    private Double latitude;
    private Double longitude;
    private final int REQUEST_LOCATION = 1;

    // GET ADDRESS VARIABLES
    protected Location mLastLocation;
    //private AddressResultReceiver mResultReceiver;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastKnownLocation;
    private String mAddressOutput;
    private TextView userAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_meetups);



        latitude = 0.0;
        longitude = 0.0;

        mAddressOutput = "";

        // TODO - implement permissions properly
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 5 );
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 5);
        mFusedLocationClient = new FusedLocationProviderClient(this);

        //mResultReceiver = new AddressResultReceiver(new Handler(), this, );

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
        if(this.getIntent().hasExtra("test")){
            userAddress.setText(this.getIntent().getExtras().getString("test", "unknown"));
        }

    }


    //-------------------------------Get Location Methods-------------------------------//
    public void getLocation(){

        // DEBUG
        Toast.makeText(this, "entered getLocation()", Toast.LENGTH_LONG).show();

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
            Toast.makeText(this, lastLocation.toString(), Toast.LENGTH_SHORT).show();

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
        // DEBUG
        Toast.makeText(TechMeetupsActivity.this,"entered startIntentService", Toast.LENGTH_LONG ).show();

        Intent intent = new Intent(this, FetchAddressIntentService.class);
        //intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mResultReceiver);
        intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, mLastKnownLocation);
        startService(intent);
    }


    private void fetchAddressHandler() {
        // DEBUG
        Toast.makeText(TechMeetupsActivity.this,"entered fetchAddressHandler", Toast.LENGTH_LONG ).show();

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
                        //updateUI();
                    }
                });
    }






}

//class AddressResultReceiver extends ResultReceiver {
//    public AddressResultReceiver(Handler handler, Context context, String addressOutput, TextView userAddress) {
//        super(handler);
//    }
//
//
//
//    @Override
//    protected void onReceiveResult(int resultCode, Bundle resultData) {
//
//        // DEBUG
//        Toast.makeText(TechMeetupsActivity.this,"entered onRecieveResult", Toast.LENGTH_LONG ).show();
//
//        if (resultData == null) {
//            return;
//        }
//
//        // Display the address string
//        // or an error message sent from the intent service.
//        mAddressOutput = resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY);
//        if (mAddressOutput == null) {
//            mAddressOutput = "";
//        }
//        displayAddressOutput(mAddressOutput);
//
//        // Show a toast message if an address was found.
//        if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT) {
//            Toast.makeText(TechMeetupsActivity.this,getString(R.string.address_found), Toast.LENGTH_LONG ).show();
//        }
//
//    }
//
//    public void displayAddressOutput(String result){
//        userAddress.setText(result);
//    }
//}
