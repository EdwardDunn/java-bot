package abertay.ac.uk.java_bot_app;

/**
 * Created by Edward Dunn on 22/03/2018.
 */

public class TechMeetup {
    private String summary;
    private String description;
    //private String date;

    public TechMeetup(String _summary, String _description){
        summary = _summary;
        description = _description;
        //date = _date;
    }

    public String getSummary(){
        return summary;
    }

    public String getDescription(){
        return description;
    }

   // public String getDate(){
    //    return date;
   // }



}
