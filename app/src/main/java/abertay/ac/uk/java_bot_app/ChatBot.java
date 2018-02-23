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

    private HashMap<String, String> solutionQuestions;
    private HashMap<String, String> commonQuestions;
    private HashMap<String, String> systemQuestions;
    private String questionType;
    private String NOTHING_FETCHED_MESSAGE = "Sorry I don't have a response to that";

    public enum questionTypes {
        SYSTEM("system question"), COMMON("common question"), SOLUTION("solution question");

        private String type;

        questionTypes(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    public ChatBot(){
        solutionQuestions = new HashMap<String, String>();
        commonQuestions = new HashMap<String, String>();
        systemQuestions = new HashMap<String, String>();
        questionType = "";

        populateSolutions();
        populateCommonQuestions();
        populateSystemQuestions();
    }

    private void populateSolutions(){
        // TODO - populate HashMap from database questions table
        solutionQuestions.put("parse int", "int x = Integer.parseInt(\"9\")");

    }

    private void populateCommonQuestions(){
        // TODO - populate HashMap from database commonQuestions table
        commonQuestions.put("how are you", "great thanks, can I help with a question?");
        commonQuestions.put("what is your name", "my name is Java Bot");
        commonQuestions.put("why is java so difficult", "its not difficult, if i can do it anyone can");
        commonQuestions.put("bye", "bye bye");
        commonQuestions.put("hi", "hey, can I help?");
    }

    private void populateSystemQuestions() {
        // TODO - populate HashMap from database systemQuestions table
        systemQuestions.put("onstart", "Welcome back!");
        systemQuestions.put("resumed", "Hello again");
        systemQuestions.put("error", "Sorry something went wrong");
    }

    public String askQuestion(String question){
        String response = "";

        // Check if question is a common question
        for(String key : commonQuestions.keySet()){
            if(question.contains(key)){
                // If question does match a key, set solution to value for key
                response = commonQuestions.get(key);
                setQuestionType(questionTypes.COMMON.getType());
            }
        }

        // Check if question is a system question
        for(String key : systemQuestions.keySet()){
            if(question.contains(key)){
                // If question does match a key, set solution to value for key
                response = systemQuestions.get(key);
                setQuestionType(questionTypes.SYSTEM.getType());
            }
        }

        // Check if question is a system question
        for(String key : solutionQuestions.keySet()){
            if(question.contains(key)){
                // If question does match a key, set solution to value for key
                response = solutionQuestions.get(key);
                setQuestionType(questionTypes.SOLUTION.getType());
            }
        }

        // If not defined as system, common or solution question - display message
        if(response == ""){
            response = NOTHING_FETCHED_MESSAGE;
            setQuestionType(questionTypes.SYSTEM.getType());
        }

        return response;
    }

    public void setQuestionType(String type){
        questionType = type;
    }

    public String getQuestionType(){
        return questionType;
    }


}
