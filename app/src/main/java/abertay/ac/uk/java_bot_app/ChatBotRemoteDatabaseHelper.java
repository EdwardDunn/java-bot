/**
 * ChatBotRemoteDatabaseHelper
 * The ChatBotRemoteDatabaseHelper class provides connection methods to populate the chat bot with
 * the required responses for normal questions and pre-set java questions like 'how do I parse an
 * int?"
 *
 * @author  Edward Dunn
 * @version 1.0
 */

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

    // Remote database URLs
    // Strings not extracted for clarity on the remote db used here
    public static final String BASE_API_URL = "http://edwarddunn.pipeten.co.uk/";
    private final String GET_SOLUTIONS_FILE_NAME = "java-bot-get-solutions.php";
    private final String GET_COMMON_RESPONSES_FILE_NAME = "java-bot-get-common-responses.php";
    private final String GET_SYSTEM_RESPONSES_FILE_NAME = "java-bot-get-system-responses.php";
    private final String GET_CHECK_RESPONSES_FILE_NAME = "java-bot-get-check-responses.php";

    // Following methods used to return chat bot responses from remote database connection

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

    /**
     * Method used to build the URL in the correct format
     */
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

    /**
     * Method used to retrieve JSON from remote database connection from URl passed in
     */
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

    /**
     * Class used to get the chat bots responses for programming questions
     */
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
            mainActivity.setLoadingMessageVisibility(false);

            if(result == null) {
                mainActivity.setLoadingMessageVisibility(true);
                mainActivity.setLoadingProgressBarVisibility(true);
            }
            else{
                parentActivity.populateSolutions(result);
            }

        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mainActivity.setLoadingProgressBarVisibility(true);
            mainActivity.setLoadingMessageVisibility(true);
        }
    }

    /**
     * Class used to get the chat bots responses for common questions like 'How are you?"
     */
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
            mainActivity.setLoadingMessageVisibility(false);

            if(result == null) {
                mainActivity.setLoadingMessageVisibility(true);
                mainActivity.setLoadingProgressBarVisibility(true);
            }
            else{
                parentActivity.populateCommonResponses(result);
            }

        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mainActivity.setLoadingProgressBarVisibility(true);
            mainActivity.setLoadingMessageVisibility(true);
        }
    }

    /**
     * Class used to get the chat bots responses for system questions (if "onresume" is passed in
     * a welcome back message is returned
     */
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
            mainActivity.setLoadingMessageVisibility(false);

            if(result == null) {
                mainActivity.setLoadingMessageVisibility(true);
                mainActivity.setLoadingProgressBarVisibility(true);
            }
            else{
                parentActivity.populateSystemResponses(result);
            }

        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mainActivity.setLoadingProgressBarVisibility(true);
            mainActivity.setLoadingMessageVisibility(true);
        }
    }

    /**
     * Class used to get the chat bots responses checking the users response to one of it questions
     */
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
            mainActivity.setLoadingMessageVisibility(false);

            if(result == null) {
                mainActivity.setLoadingMessageVisibility(true);
                mainActivity.setLoadingProgressBarVisibility(true);
            }
            else{
                parentActivity.populateCheckResponses(result);
            }

        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mainActivity.setLoadingProgressBarVisibility(true);
            mainActivity.setLoadingMessageVisibility(true);
        }
    }
}
