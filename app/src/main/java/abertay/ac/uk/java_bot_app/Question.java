package abertay.ac.uk.java_bot_app;

/**
 * Created by Edward Dunn on 23/03/2018.
 */

public class Question {

   private String question;
   private String solution;

    public Question(String _question, String _solution){
        question = _question;
        solution = _solution;
    }

    public String getQuestion() {
        return question;
    }

    public String getSolution() {
        return solution;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }
}
