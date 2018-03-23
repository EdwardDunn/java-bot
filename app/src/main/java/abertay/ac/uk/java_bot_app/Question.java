package abertay.ac.uk.java_bot_app;

/**
 * Created by Edward Dunn on 23/03/2018.
 */

public class Question {

   private String solutionKey;
   private String solution;

    public Question(String _solutionKey, String _solution){
        solutionKey = _solutionKey;
        solution = _solution;
    }

    public String getSolutionKey() {
        return solutionKey;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolutionKey(String solutionKey) {
        this.solutionKey = solutionKey;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }
}
