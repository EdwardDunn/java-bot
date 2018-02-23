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
    private HashMap<String, String> commonQuestions;
    private HashMap<String, String> systemQuestions;

    public ChatBot(){
        solutions = new HashMap<String, String>();
        commonQuestions = new HashMap<String, String>();
        systemQuestions = new HashMap<String, String>();

        populateSolutions();
        populateCommonQuestions();
        populateSystemQuestions();
    }

    private void populateSolutions(){
        // TODO - populate HashMap from database questions table
        solutions.put("parse int", "int x = Integer.parseInt(\"9\")");

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
        systemQuestions.put("error", "Sorry something went wrong");
    }

    public String askQuestion(String question){
        String response = "";

        // Check if question is a common question
        for(String key : commonQuestions.keySet()){
            if(question.contains(key)){
                // If question does match a key, set solution to value for key
                response = commonQuestions.get(key);
            }
        }

        // Check if question is a system question
        for(String key : systemQuestions.keySet()){
            if(question.contains(key)){
                // If question does match a key, set solution to value for key
                response = systemQuestions.get(key);
            }
        }

        // If not defined as system or common question - pass to get solution for programming question
        if(response == ""){
            response = getSolution(question);
        }

        return response;
    }

    private String getSolution(String question){
        String solution = "";

        // Check if question contains any matching text against keys held for solutions in solutions hash map
        for(String key : solutions.keySet()){
            if(question.contains(key)){
                // If question does match a key, set solution to value for key
                solution = solutions.get(key);
            }
        }

        return solution;
    }

}
