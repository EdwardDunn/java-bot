package abertay.ac.uk.java_bot_app;

/**
 * Created by edwar on 23/02/2018.
 */

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class ResponsesRemoteDatabaseHelper {

    private final String rootURL = "edwarddunn.pipeten.co.uk/"; // replace this with your server address
    private final String insertSolutionURL = rootURL + "java-bot-insert-solution.php";
    private final String getSolutionsURL = rootURL + "java-bot-get-solutions-list.php";
    private final String getSystemResponsesURL = rootURL + "java-bot-get-system-responses.php";
    private final String getCommonResponsesURL = rootURL + "java-bot-get-common-responses.php";
    private final String getCheckResponsesURL = rootURL + "java-bot-get-check-responses.php";
    public static final String[] SOLUTION_COLUMN_NAMES = {"solution_key", "solution"};
    public static final String[] RESPONSE_COLUMN_NAMES = {"response_key", "response"};
    /*
        We hold an instance of parent activity here so that we could send the result back to it.
        Note that in case another activity (not MainActivity) makes use of this helper class, you
        will need to modify this to be a generic Activity class instead.
    */
    private final MainActivity parentActivity;
    ResponsesRemoteDatabaseHelper(MainActivity context) {
        parentActivity = context;
    }

    // adds contact to the remote database using AddContactTask
    public void addSolution(Solution question) {
        AddSolutionTask task = new AddSolutionTask();
        task.execute(question);
    }
    // pulls the list of solutions from the remote database using GetSolutionsTask
    public void getSolutions() {
        GetSolutionsTask task = new GetSolutionsTask();
        task.execute();
    }

    // pulls the list of system responses from the remote database using GetSystemResponsesTask
    public void getSystemResponses() {
       GetSystemResponsesTask task = new GetSystemResponsesTask();
        task.execute();
    }

    // pulls the list of common responses from the remote database using GetCommonResponsesTask
    public void getCommonResponses() {
        GetCommonResponsesTask task = new GetCommonResponsesTask();
        task.execute();
    }

    // pulls the list of check responses from the remote database using GetCheckResponsesTask
    public void getCheckResponses() {
        GetCheckResponsesTask task = new GetCheckResponsesTask();
        task.execute();
    }

    // Helper function to generate request string
    private String generateRequest(List<AbstractMap.SimpleEntry> params) throws UnsupportedEncodingException {
        StringBuilder request = new StringBuilder();

        boolean first = true; // is it the first parameter?

        /* Encode POST data. */
        for (AbstractMap.SimpleEntry param : params)
        {
            if (first)
                first = false;
            else
                request.append("&"); // add param separator

            request.append(URLEncoder.encode((String)param.getKey(), "UTF-8"));
            request.append("=");
            request.append(URLEncoder.encode((String)param.getValue(), "UTF-8"));
        }

        return request.toString();
    }

    // Helper function to read result into a string
    private String readResult(BufferedReader reader) throws IOException {
        StringBuilder result = new StringBuilder();
        String line;
        // read line-by-line while line isn't empty
        while ((line = reader.readLine()) != null){
            result.append(line).append('\n');
        }

        return result.toString();
    }

    // Asynchronous task for adding a contact to DB. Runs on a background thread.
    private class AddSolutionTask extends AsyncTask<Solution, Void, String> {
        private final ProgressDialog progressDialog = new ProgressDialog(parentActivity);

        /* Before executing this task, set the progressDialog message and show it. */
        @Override
        protected void onPreExecute() {
            Log.e("INSERT_RESULT", "adding solution...");
            this.progressDialog.setMessage("adding solution...");
            this.progressDialog.show();
        }

        /* This is what is done in the background. */
        // This function requires a list of parameters, but we will be using only one [0].
        @Override
        protected String doInBackground(Solution... questions) {
            Solution q = questions[0];

            URL url;
            HttpURLConnection conn = null;

            String result = "";

            try {
                /* Get HTTP connection. */
                url = new URL(insertSolutionURL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(10000);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                /* Add POST parameters to list. */
                List<AbstractMap.SimpleEntry> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("solution_key", questions[0].solution_key));
                params.add(new AbstractMap.SimpleEntry("solution", questions[0].solution));

                /* Generate request params string */
                String request = generateRequest(params);

                /* Set length of the streamed data */
                conn.setFixedLengthStreamingMode(request.getBytes().length);

                Log.e("INSERT_RESULT", "Requesting: " + request);

                /* Write HTTP request */
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                writer.write(request);
                writer.flush();
                writer.close();
                os.close();

                /* Read Response */
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = readResult(reader);
                return response.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }

            return null; // Make sure to handle this in onPostExecute
        }

        /* After the task is finished, dismiss the progressDialog.*/
        @Override
        protected void onPostExecute(String result) {
            this.progressDialog.dismiss();
            Log.e("INSERT_RESULT", result);
        }
    }


    // Asynchronous task for getting the list of solutions from the remote DB. Runs on a background thread.
    private class GetSolutionsTask extends AsyncTask<Void, Void, String> {
        private final ProgressDialog progressDialog = new ProgressDialog(parentActivity);

        /* Before executing this task, set the progressDialog message and show it. */
        @Override
        protected void onPreExecute() {
            this.progressDialog.setMessage("Getting solutions list...");
            this.progressDialog.show();
        }

        /* This is what is done in the background. */
        // This time we don't need to pass any parameters to the task, hence using Void.
        @Override
        protected String doInBackground(Void... v) {
            URL url;
            HttpURLConnection conn = null;

            try {
                /* Get HTTP connection. */
                url = new URL(getSolutionsURL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(10000);
                conn.setDoInput(true); // we only need input here as we are not sending any data to the server

                // Read server response string
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String responseString = readResult(reader);

                /* Return the contacts JSON. */
                return responseString;

                // handle exceptions
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }

            return null; // if something fails, make sure to handle this
        }

        /* After the task is finished, dismiss the progressDialog.*/
        @Override
        protected void onPostExecute(String result) {
            Log.i("RESULT:", result.toString());
            // send the result to the main activity
            parentActivity.result = result;
            // process it
            parentActivity.resultRunnable.run();

            this.progressDialog.dismiss();
        }
    }

    // Asynchronous task for getting the list of system responses from the remote DB. Runs on a background thread.
    private class GetSystemResponsesTask extends AsyncTask<Void, Void, String> {
        private final ProgressDialog progressDialog = new ProgressDialog(parentActivity);

        /* Before executing this task, set the progressDialog message and show it. */
        @Override
        protected void onPreExecute() {
            this.progressDialog.setMessage("Getting system responses list...");
            this.progressDialog.show();
        }

        /* This is what is done in the background. */
        // This time we don't need to pass any parameters to the task, hence using Void.
        @Override
        protected String doInBackground(Void... v) {
            URL url;
            HttpURLConnection conn = null;

            try {
                /* Get HTTP connection. */
                url = new URL(getSystemResponsesURL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(10000);
                conn.setDoInput(true); // we only need input here as we are not sending any data to the server

                // Read server response string
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String responseString = readResult(reader);

                /* Return the contacts JSON. */
                return responseString;

                // handle exceptions
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }

            return null; // if something fails, make sure to handle this
        }

        /* After the task is finished, dismiss the progressDialog.*/
        @Override
        protected void onPostExecute(String result) {
            Log.i("RESULT:", result.toString());
            // send the result to the main activity
            parentActivity.result = result;
            // process it
            parentActivity.resultRunnable.run();

            this.progressDialog.dismiss();
        }
    }

    // Asynchronous task for getting the list of common responses from the remote DB. Runs on a background thread.
    private class GetCommonResponsesTask extends AsyncTask<Void, Void, String> {
        private final ProgressDialog progressDialog = new ProgressDialog(parentActivity);

        /* Before executing this task, set the progressDialog message and show it. */
        @Override
        protected void onPreExecute() {
            this.progressDialog.setMessage("Getting common responses list...");
            this.progressDialog.show();
        }

        /* This is what is done in the background. */
        // This time we don't need to pass any parameters to the task, hence using Void.
        @Override
        protected String doInBackground(Void... v) {
            URL url;
            HttpURLConnection conn = null;

            try {
                /* Get HTTP connection. */
                url = new URL(getCommonResponsesURL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(10000);
                conn.setDoInput(true); // we only need input here as we are not sending any data to the server

                // Read server response string
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String responseString = readResult(reader);

                /* Return the contacts JSON. */
                return responseString;

                // handle exceptions
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }

            return null; // if something fails, make sure to handle this
        }

        /* After the task is finished, dismiss the progressDialog.*/
        @Override
        protected void onPostExecute(String result) {
            Log.i("RESULT:", result.toString());
            // send the result to the main activity
            parentActivity.result = result;
            // process it
            parentActivity.resultRunnable.run();

            this.progressDialog.dismiss();
        }
    }

    // Asynchronous task for getting the list of check responses from the remote DB. Runs on a background thread.
    private class GetCheckResponsesTask extends AsyncTask<Void, Void, String> {
        private final ProgressDialog progressDialog = new ProgressDialog(parentActivity);

        /* Before executing this task, set the progressDialog message and show it. */
        @Override
        protected void onPreExecute() {
            this.progressDialog.setMessage("Getting check responses list...");
            this.progressDialog.show();
        }

        /* This is what is done in the background. */
        // This time we don't need to pass any parameters to the task, hence using Void.
        @Override
        protected String doInBackground(Void... v) {
            URL url;
            HttpURLConnection conn = null;

            try {
                /* Get HTTP connection. */
                url = new URL(getCheckResponsesURL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(10000);
                conn.setDoInput(true); // we only need input here as we are not sending any data to the server

                // Read server response string
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String responseString = readResult(reader);

                /* Return the contacts JSON. */
                return responseString;

                // handle exceptions
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }

            return null; // if something fails, make sure to handle this
        }

        /* After the task is finished, dismiss the progressDialog.*/
        @Override
        protected void onPostExecute(String result) {
            Log.i("RESULT:", result.toString());
            // send the result to the main activity
            parentActivity.result = result;
            // process it
            parentActivity.resultRunnable.run();

            this.progressDialog.dismiss();
        }
    }


}
