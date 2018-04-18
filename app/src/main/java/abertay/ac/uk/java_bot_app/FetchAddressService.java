/**
 * FetchAddressService
 * The FetchAddressService is used to get the users last known location, this is required by the
 * techmeetups activity for search their city location to find nearest tech meetups. The address is
 * then displayed using the 'runOnUIThread' method to update the activity.
 *
 * References:
 * Geo Location;
 *  https://developer.android.com/training/location/display-address.html#java
 *
 * @author  Edward Dunn
 * @version 1.0
 */

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


public class FetchAddressService {

    Activity currentActivity;
    Location mLocation;

    private String city;
    private String address;

    /**
     * Constructor
     */
    public FetchAddressService(Activity _currentActivity, Location _mLocation){
        currentActivity = _currentActivity;
        mLocation = _mLocation;

        onHandleLocation(mLocation);
   }

    /**
     * Method deliver the address and city found to the techmeetups activity
     */
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

    /**
     * Method used to get last known location, exceptions are thrown for no network connection
     * and invalid geo locations. The address and city are returned.
     */
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
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
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
            deliverResultToTechMeetupsActivity(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments), city);
        }

    }

    /**
     * Method used to stores constants used for holding the result of the geo location search
     *
     */
    public final class Constants {
        public static final int SUCCESS_RESULT = 0;
        public static final int FAILURE_RESULT = 1;
        public static final String PACKAGE_NAME =
                "com.google.android.gms.location.sample.locationaddress";
    }
}
