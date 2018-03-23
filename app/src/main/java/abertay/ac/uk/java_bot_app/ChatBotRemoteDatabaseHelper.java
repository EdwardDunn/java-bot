package abertay.ac.uk.java_bot_app;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class ChatBotRemoteDatabaseHelper{

    private final ChatBot parentActivity;
    ChatBotRemoteDatabaseHelper(ChatBot context) {
        parentActivity = context;
    }

    public static final String BASE_API_URL = "http://edwarddunn.pipeten.co.uk/";
    private final String GET_SOLUTIONS_FILE_NAME = "java-bot-get-solutions.php";
    private final String GET_COMMON_RESPONSES_FILE_NAME = "java-bot-get-common-responses.php";
    private final String GET_SYSTEM_RESPONSES_FILE_NAME = "java-bot-get-system-responses.php";
    private final String GET_CHECK_RESPONSES_FILE_NAME = "java-bot-get-check-responses.php";

    public void getSolutions(){
        URL getSolutionsUrl = buildURL(GET_SOLUTIONS_FILE_NAME);
        new GetSolutionsTask().execute(getSolutionsUrl);
    }

    public void getCommonResponses(){
        URL getCommonResponsesUrl = buildURL(GET_COMMON_RESPONSES_FILE_NAME);
        new GetCommonResponsesTask().execute(getCommonResponsesUrl);
    }

    public void getSystemResponses(){
        URL getSystemResponsesUrl = buildURL(GET_SYSTEM_RESPONSES_FILE_NAME);
        new GetSystemResponsesTask().execute(getSystemResponsesUrl);
    }

    public void getCheckResponses(){
        URL getCheckResponsesUrl = buildURL(GET_CHECK_RESPONSES_FILE_NAME);
        new GetCheckResponsesTask().execute(getCheckResponsesUrl);
    }

    public static URL buildURL(String fileName){

        URL url = null;
        Uri uri = Uri.parse(BASE_API_URL).buildUpon()
                .appendPath(fileName)
                .build();
        try {
            url = new URL(uri.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return url;
    }

    public static String getJson(URL url) throws IOException{

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

    public class GetSolutionsTask extends AsyncTask<URL, Void, String> {
        MainActivity mainActivity = new MainActivity();

        @Override
        protected String doInBackground(URL... urls) {
            URL searchURL = urls[0];
            String result = null;
            try{
                result = ChatBotRemoteDatabaseHelper.getJson(searchURL);
            }
            catch (Exception e){
                Log.d("Error", e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result){

            mainActivity.setLoadingProgressBarVisibility(false);

            if(result == null) {
                mainActivity.setErrorMessageVisibility(true);
                mainActivity.setLoadingProgressBarVisibility(true);
            }
            else{
                parentActivity.populateSolutions(result);
                mainActivity.setErrorMessageVisibility(false);
            }

        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mainActivity.setLoadingProgressBarVisibility(true);
        }
    }

    public class GetCommonResponsesTask extends AsyncTask<URL, Void, String> {

        MainActivity mainActivity = new MainActivity();

        @Override
        protected String doInBackground(URL... urls) {
            URL searchURL = urls[0];
            String result = null;
            try{
                result = ChatBotRemoteDatabaseHelper.getJson(searchURL);
            }
            catch (Exception e){
                Log.d("Error", e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result){

            mainActivity.setLoadingProgressBarVisibility(false);

            if(result == null) {
                mainActivity.setErrorMessageVisibility(true);
                mainActivity.setLoadingProgressBarVisibility(true);
            }
            else{
                parentActivity.populateCommonResponses(result);
                mainActivity.setErrorMessageVisibility(false);
            }

        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mainActivity.setLoadingProgressBarVisibility(true);
        }
    }

    public class GetSystemResponsesTask extends AsyncTask<URL, Void, String> {

        MainActivity mainActivity = new MainActivity();

        @Override
        protected String doInBackground(URL... urls) {
            URL searchURL = urls[0];
            String result = null;
            try{
                result = ChatBotRemoteDatabaseHelper.getJson(searchURL);
            }
            catch (Exception e){
                Log.d("Error", e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result){

            mainActivity.setLoadingProgressBarVisibility(false);

            if(result == null) {
                mainActivity.setErrorMessageVisibility(true);
                mainActivity.setLoadingProgressBarVisibility(true);
            }
            else{
                parentActivity.populateSystemResponses(result);
                mainActivity.setErrorMessageVisibility(false);
            }

        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mainActivity.setLoadingProgressBarVisibility(true);
        }
    }

    public class GetCheckResponsesTask extends AsyncTask<URL, Void, String> {

        MainActivity mainActivity = new MainActivity();

        @Override
        protected String doInBackground(URL... urls) {
            URL searchURL = urls[0];
            String result = null;
            try{
                result = ChatBotRemoteDatabaseHelper.getJson(searchURL);
            }
            catch (Exception e){
                Log.d("Error", e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result){

            mainActivity.setLoadingProgressBarVisibility(false);

            if(result == null) {
                mainActivity.setErrorMessageVisibility(true);
                mainActivity.setLoadingProgressBarVisibility(true);
            }
            else{
                parentActivity.populateCheckResponses(result);
                mainActivity.setErrorMessageVisibility(false);
            }

        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mainActivity.setLoadingProgressBarVisibility(true);
        }
    }
}
