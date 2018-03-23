package abertay.ac.uk.java_bot_app;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * References:
 * Geo Location;
 *  https://developer.android.com/training/location/display-address.html#java
 */

public class FetchAddressService {

    Activity currentActivity;
    Location mLocation;

    private String city;
    private String address;

    public FetchAddressService(Activity _currentActivity, Location _mLocation){
        //super(IntentService.class.getSimpleName());

        currentActivity = _currentActivity;
        mLocation = _mLocation;

        onHandleLocation(mLocation);
   }



    private void deliverResultToTechMeetupsActivity(int resultCode, String _message, String _city) {
        address = _message;
        city = _city;

        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TechMeetupsActivity.setLocation(address, city);
            }
        });

    }

    protected void onHandleLocation(Location mlocation) {
        Geocoder geocoder = new Geocoder(currentActivity, Locale.getDefault());

        String errorMessage = "";

        Location location = mlocation;

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            //errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            //errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        // Used for searching tech meetups in currently located city
        String city = "";

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                //errorMessage = getString(R.string.no_address_found);
                city = "No city found";
                Log.e(TAG, errorMessage);
            }
            deliverResultToTechMeetupsActivity(Constants.FAILURE_RESULT, errorMessage, city);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

           city = addresses.get(0).getLocality();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            //Log.i(TAG, getString(R.string.address_found));
            deliverResultToTechMeetupsActivity(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments), city);
        }
        //this.stopSelf();
    }

    public final class Constants {
        public static final int SUCCESS_RESULT = 0;
        public static final int FAILURE_RESULT = 1;
        public static final String PACKAGE_NAME =
                "com.google.android.gms.location.sample.locationaddress";
        public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
        public static final String RESULT_DATA_KEY = PACKAGE_NAME +
                ".RESULT_DATA_KEY";
        public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
                ".LOCATION_DATA_EXTRA";
    }
}
