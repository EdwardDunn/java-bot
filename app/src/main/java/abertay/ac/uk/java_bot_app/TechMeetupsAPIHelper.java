package abertay.ac.uk.java_bot_app;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Edward Dunn on 22/03/2018.
 */

public class TechMeetupsAPIHelper {

    private final TechMeetupsActivity parentActivity;
    TechMeetupsAPIHelper(TechMeetupsActivity context) {
        parentActivity = context;
    }


    // Open Tech Calender Areas
    private static final String CITY_EDINBURGH = "edinburgh";
    private static final String EDINBURGH_AREA_CODE = "62";

    private static final String CITY_GLASGOW = "glasgow";
    private static final String GLASGOW_AREA_CODE = "65";

    private static final String CITY_ABERDEEN = "aberdeen";
    private static final String ABERDEEN_AREA_CODE = "60";

    private static final String CITY_FALKIRK = "falkirk";
    private static final String FALKIRK_AREA_CODE = "64";

    private static final String CITY_INVERNESS = "inverness";
    private static final String INVERNESS_AREA_CODE = "66";

    private static final String CITY_STIRLING = "stirling";
    private static final String STIRLING_AREA_CODE = "69";

    // API with area code
    public static final String BASE_API_URL_AREA = "https://opentechcalendar.co.uk/api1/area/";

    // API without area code
    public static final String BASE_API_URL = "https://opentechcalendar.co.uk/api1/";

    public void getTechMeetups(String city){
        URL getNearestMeetups = buildURL(city);
        new GetNearestMeetupsTask().execute(getNearestMeetups);
    }

    public static URL buildURL(String city){
        city = city.toLowerCase();

        String areaCode = getOpenTechCalendarAreaCode(city);

        if(city != "unknown"){

            // If areaCode is stated, search tech meetups in that city
            URL url = null;
            Uri uri = Uri.parse(BASE_API_URL_AREA).buildUpon()
                    .appendPath(areaCode)
                    .appendPath("events.json")
                    .build();

            try {
                url = new URL(uri.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return url;

        }
        else{
            // If not areaCode is state, return all locations
            URL url = null;
            Uri uri = Uri.parse(BASE_API_URL).buildUpon()
                    .appendPath("events.json")
                    .build();

            try {
                url = new URL(uri.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return url;
        }
    }

    private static String getOpenTechCalendarAreaCode(String city){
        String areaCode = "";

        if(city.contains(CITY_EDINBURGH)){
            areaCode = EDINBURGH_AREA_CODE ;
        }
        else if(city.contains(CITY_GLASGOW)){
            areaCode = GLASGOW_AREA_CODE;
        }
        else if(city.contains(CITY_ABERDEEN)){
            areaCode = ABERDEEN_AREA_CODE;
        }
        else if(city.contains(CITY_FALKIRK)){
            areaCode = FALKIRK_AREA_CODE;
        }
        else if(city.contains(CITY_INVERNESS)){
            areaCode = INVERNESS_AREA_CODE;
        }
        else if(city.contains(CITY_STIRLING)){
            areaCode = STIRLING_AREA_CODE;
        }
        else{
            areaCode = "unknown";
        }

        return areaCode;
    }

    public static String getJson(URL url) throws IOException {

        // Create connection object
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            InputStream stream = connection.getInputStream();
            Scanner scanner = new Scanner(stream);

            // Read everything
            scanner.useDelimiter("\\A");

            // Check if there is more data
            boolean hasData = scanner.hasNext();
            if (hasData) {
                return scanner.next();
            } else {
                return null;
            }
        }
        catch (Exception e){
            Log.d("Error", e.toString());
            return null;
        }
        finally {
            connection.disconnect();
        }

    }

    public class GetNearestMeetupsTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL searchURL = urls[0];
            String result = null;
            try{
                result = getJson(searchURL);
            }
            catch (Exception e){
                Log.d("Error", e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result){

            parentActivity.setLoadingProgressBarVisibility(false);

            if(result == null) {
                parentActivity.setLoadingMessageVisibility(true);
            }
            else{
                parentActivity.populateAPIResponse(result);
                parentActivity.setLoadingMessageVisibility(false);
            }

        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            parentActivity.setLoadingProgressBarVisibility(true);
        }
    }
}
