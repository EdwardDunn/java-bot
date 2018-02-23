package abertay.ac.uk.java_bot_app;

import java.util.HashMap;

/**
 * ChatBot
 * The ChatBot class provides the Chat Bots functionality. It provides a public function for
 * asking a question and connects to the database to retrieve current set of questions
 *
 * @author  Edward Dunn
 * @version 1.0
 */

public class ChatBot {

    private HashMap<String, String> solutions;
    private HashMap<String, String> commonResponses;
    private HashMap<String, String> systemResponses;
    private HashMap<String, String> checkResponses;
    private String solutionType;
    private String NOTHING_FETCHED_MESSAGE = "Sorry I don't have a response to that";


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
    }


    private void populateSolutions(){
        // TODO - populate HashMap from database solutions table
        solutions.put("parse int", "int x = Integer.parseInt(\"9\")");

    }

    private void populateCommonResponses(){
        // TODO - populate HashMap from database common_responses table
        commonResponses.put("how are you", "great thanks, can I help with a question?");
        commonResponses.put("what is your name", "my name is Java Bot");
        commonResponses.put("why is java so difficult", "its not difficult, if i can do it anyone can");
        commonResponses.put("bye", "bye bye");
        commonResponses.put("hi", "hey, can I help?");
    }

    private void populateSystemResponses() {
        // TODO - populate HashMap from database system_responses table
        systemResponses.put("onstart", "Welcome back!");
        systemResponses.put("resumed", "Hello again");
        systemResponses.put("error", "Sorry something went wrong");
    }

    private void populateCheckResponses() {
        // TODO - populate HashMap from database check-responses table
        checkResponses.put("no", "Sorry let me check Stack Overflow");
        checkResponses.put("nope", "Sorry let me check Stack Overflow");
        checkResponses.put("not", "Sorry let me check Stack Overflow");
        checkResponses.put("yes", "Great!");
        checkResponses.put("yep", "Great!");
        checkResponses.put("yeah", "Great!");
    }



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

    public String checkResponseToQuestion(String responseToQuestion){
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

    public void setSolutionType(String type){
        solutionType = type;
    }

    public String getSolutionType(){
        return solutionType;
    }


}
