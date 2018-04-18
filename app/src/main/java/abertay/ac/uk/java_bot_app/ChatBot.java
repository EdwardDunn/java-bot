/**
 * ChatBot
 * The ChatBot class provides the Chat Bots functionality. It provides a public function for
 * asking a question and connects to the database to retrieve current set of questions
 *
 * @author  Edward Dunn
 * @version 1.0
 */

package abertay.ac.uk.java_bot_app;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class ChatBot extends Activity {

    protected ChatBotRemoteDatabaseHelper chatBotRemoteDatabaseHelper;

    private static HashMap<String, String> solutions;
    private static HashMap<String, String> commonResponses;
    private static HashMap<String, String> systemResponses;
    private static HashMap<String, String> checkResponses;

    private static String solutionKey;

    private static String solutionType;
    // Getter setters for solutionType
    public static void setSolutionType(String type){
        solutionType = type;
    }
    public static String getSolutionType(){
        return solutionType;
    }

    private static String NOTHING_FETCHED_MESSAGE = "Sorry I don't have a response to that";

    /**
     * Method used to provide easy setting of question types
     */
    public enum questionTypes {
        SYSTEM("system question"), COMMON("common question"), SOLUTION("solution question"), CHECK("response to question");
        private String type;
        questionTypes(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }
    }

    // Used for singleton pattern
    private static ChatBot instance = null;

    /**
     * Private Constructor(singleton pattern)
     */
    private ChatBot(){
        solutions = new HashMap<String, String>();
        commonResponses = new HashMap<String, String>();
        systemResponses = new HashMap<String, String>();
        checkResponses = new HashMap<String, String>();
        solutionType = "";
        solutionKey = "";

        chatBotRemoteDatabaseHelper = new ChatBotRemoteDatabaseHelper(this);

        try {
            chatBotRemoteDatabaseHelper.getSolutions();
            chatBotRemoteDatabaseHelper.getCommonResponses();
            chatBotRemoteDatabaseHelper.getSystemResponses();
            chatBotRemoteDatabaseHelper.getCheckResponses();
        }
        catch (Exception e){
            Log.d("Error populating chat bot responses", e.getMessage());
        }
    }

    /**
     *  Used for singleton pattern
     */
    public static ChatBot getInstance( ){
        if(instance == null) {
            instance = new ChatBot();
        }
        return instance;
    }

    /**
     * Method used to populate the solutions hash map from the database
     */
    public static void populateSolutions(String result){
        try{
            // Create JSON array for response from database
            JSONArray ja = new JSONArray(result);

            JSONObject jo = null;

            for(int i = 0; i < ja.length(); i++){
                jo = ja.getJSONObject(i);

                // Get values from object
                String solutionKey = jo.getString("solution_key");
                String solution = jo.getString("solution");

                // Add to solutions map
                solutions.put(solutionKey, solution);
            }

        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Method used to populate the commonResponses hash map from the database
     */
    public static void populateCommonResponses(String result){
        try{
            // Create JSON array for response from database
            JSONArray ja = new JSONArray(result);

            JSONObject jo = null;

            for(int i = 0; i < ja.length(); i++){
                jo = ja.getJSONObject(i);

                // Get values from object
                String responseKey = jo.getString("response_key");
                String response = jo.getString("response");

                // Add to solutions map
                commonResponses.put(responseKey, response);
            }

        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Method used to populate the systemResponses hash map from the database
     */
    public static void populateSystemResponses(String result){
        try{
            // Create JSON array for response from database
            JSONArray ja = new JSONArray(result);

            JSONObject jo = null;

            for(int i = 0; i < ja.length(); i++){
                jo = ja.getJSONObject(i);

                // Get values from object
                String responseKey = jo.getString("response_key");
                String response = jo.getString("response");

                // Add to solutions map
                systemResponses.put(responseKey, response);
            }

        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Method used to populate the checkResponses hash map from the database
     */
    public static void populateCheckResponses(String result){
        try{
            // Create JSON array for response from database
            JSONArray ja = new JSONArray(result);

            JSONObject jo = null;

            for(int i = 0; i < ja.length(); i++){
                jo = ja.getJSONObject(i);

                // Get values from object
                String responseKey = jo.getString("response_key");
                String response = jo.getString("response");

                // Add to solutions map
                checkResponses.put(responseKey, response);
            }

        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Method used to evaluate the questions asked by the user, decides what type the questions is.
     * Returns the correct response after for type
     */
    public static String askQuestion(String question){
        String response = "";

        // Check if question is a common question
        for(String key : commonResponses.keySet()){
            if(question.contains(key)){
                // If question does match a key, set solution to value for key
                response = commonResponses.get(key);
                setSolutionType(questionTypes.COMMON.getType());
            }
        }

        // Check if question is a system question
        for(String key : systemResponses.keySet()){
            if(question.contains(key)){
                // If question does match a key, set solution to value for key
                response = systemResponses.get(key);
                setSolutionType(questionTypes.SYSTEM.getType());
            }
        }

        // Check if question has a solution , i.e. it is a programming question
        for(String key : solutions.keySet()){
            if(question.contains(key)){
                // If question does match a key, set solution to value for key
                response = solutions.get(key);
                solutionKey = key;
                setSolutionType(questionTypes.SOLUTION.getType());
            }
        }

        // If not defined as system, common or solution question - display message
        if(response == ""){
            response = NOTHING_FETCHED_MESSAGE;
            setSolutionType(questionTypes.SYSTEM.getType());
        }

        return response;
    }

    // Used for stack overflow query parameter
    public String getSolutionKey(){
        return solutionKey;
    }

    /**
     * Method used to evaluate the response to a question asked by by the chat bot.
     * The main function is to identify if the user is happy or not with the chat bots response
     */
    public static String checkResponseToQuestion(String responseToQuestion){
        String chatBotResponse = "";

        for(String key : checkResponses.keySet()){
            if(responseToQuestion.contains(key)){
                // If question does match a key, set solution to value for key
                chatBotResponse = checkResponses.get(key);
                setSolutionType(questionTypes.CHECK.getType());
            }
        }

        return chatBotResponse;
    }

}
