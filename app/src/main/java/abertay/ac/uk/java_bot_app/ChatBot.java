package abertay.ac.uk.java_bot_app;

import android.app.Activity;

import java.util.HashMap;

/**
 * ChatBot
 * The ChatBot class provides the Chat Bots functionality. It provides a public function for
 * asking a question and connects to the database to retrieve current set of questions
 *
 * @author  Edward Dunn
 * @version 1.0
 */

public class ChatBot extends Activity {

    private HashMap<String, String> solutions;
    private HashMap<String, String> commonResponses;
    private HashMap<String, String> systemResponses;
    private HashMap<String, String> checkResponses;

    private String solutionType;
    // Getter setters for solutionType
    public void setSolutionType(String type){
        solutionType = type;
    }
    public String getSolutionType(){
        return solutionType;
    }

    private String NOTHING_FETCHED_MESSAGE = "Sorry I don't have a response to that";


    //private ProgressDialog progressDialog;
    // public String result = "";

    //private ChatBotRemoteDatabaseHelper chatBotRemoteDatabaseHelper;

    /**
     *  Runnable which will process the result
     */
    /*public final Runnable resultRunnable = new Runnable(){
        @Override
        public void run() {
            Log.e("PROCESSING RESULT", "...");
            // Display progress dialog
            progressDialog.setMessage("Processing results...");
            progressDialog.show();
            // Dismiss the dialog
            progressDialog.dismiss();
        }
    };
    */

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

    /**
     * Constructor
     */
    public ChatBot(){
        solutions = new HashMap<String, String>();
        commonResponses = new HashMap<String, String>();
        systemResponses = new HashMap<String, String>();
        checkResponses = new HashMap<String, String>();
        solutionType = "";

        populateSolutions();
        populateCommonResponses();
        populateSystemResponses();
        populateCheckResponses();

        //Initialise the progress dialog for result processing
        //progressDialog = new ProgressDialog(this);
        //chatBotRemoteDatabaseHelper = new ChatBotRemoteDatabaseHelper(this);
    }

    /**
     * Method used to populate the solutions hash map from the database
     */
    private void populateSolutions(){
        //chatBotRemoteDatabaseHelper.addSolution(new Solution("key test", "solution test"));
        /*
        try{

            // Call getSolutions method
            String solutionsReturned = chatBotRemoteDatabaseHelper.getSolutions();

            // Create JSON array for response from database
            JSONArray ja = new JSONArray(solutionsReturned);

            JSONObject jo = null;

            for(int i = 0; i < ja.length(); i++){
                jo = ja.getJSONObject(i);

                // Get values from object
                String solutionKey = jo.getString("solution_key");
                String solution = jo.getString("solution");

                // Add to solutions map
                solutions.put(solutionKey, solution);
            }

        }catch(JSONException e){
            e.printStackTrace();
        }

        */

        // Used for initial development - TODO - remove after debug
        //solutions.put("parse int", "int x = Integer.parseInt(\"9\")");
    }

    /**
     * Method used to populate the commonResponses hash map from the database
     */
    private void populateCommonResponses(){
        // TODO - populate HashMap from database common_responses table
        commonResponses.put("how are you", "great thanks, can I help with a question?");
        commonResponses.put("what is your name", "my name is Java Bot");
        commonResponses.put("why is java so difficult", "its not difficult, if i can do it anyone can");
        commonResponses.put("bye", "bye bye");
        commonResponses.put("hi", "hey, can I help?");
    }

    /**
     * Method used to populate the systemResponses hash map from the database
     */
    private void populateSystemResponses() {
        // TODO - populate HashMap from database system_responses table
        systemResponses.put("onstart", "Welcome back!");
        systemResponses.put("resumed", "Hello again");
        systemResponses.put("error", "Sorry something went wrong");
    }

    /**
     * Method used to populate the checkResponses hash map from the database
     */
    private void populateCheckResponses() {
        // TODO - populate HashMap from database check-responses table
        checkResponses.put("no", "Sorry let me check Stack Overflow");
        checkResponses.put("nope", "Sorry let me check Stack Overflow");
        checkResponses.put("not", "Sorry let me check Stack Overflow");
        checkResponses.put("yes", "Happy to help!");
        checkResponses.put("yep", "Happy to help!");
        checkResponses.put("yeah", "Happy to help!");
    }

    /**
     * Method used to evaluate the questions asked by the user, decides what type the questions is.
     * Returns the correct response after for type
     * @param question
     * @return response
     */
    public String askQuestion(String question){
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

        // Check if question is a system question
        for(String key : solutions.keySet()){
            if(question.contains(key)){
                // If question does match a key, set solution to value for key
                response = solutions.get(key);
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

    /**
     * Method used to evaluate the response to a question asked by by the chat bot.
     * The main function is to identify if the user is happy or not with the chat bots response
     * @param responseToQuestion
     * @return chatBotResponse
     */
    public String checkResponseToQuestion(String responseToQuestion){
        String chatBotResponse = "";

        for(String key : checkResponses.keySet()){
            if(responseToQuestion.contains(key)){
                // If question does match a key, set solution to value for key
                chatBotResponse = checkResponses.get(key);
                setSolutionType(questionTypes.CHECK.getType());
            }else{
                // TODO - if the response if not in checkResponses, send to askQuestion()
            }
        }

        return chatBotResponse;
    }

}
