package abertay.ac.uk.java_bot_app;

/**
 * Created by Edward Dunn on 22/03/2018.
 */

public class TechMeetup {
    private String summary;
    private String city;
    private String description;
    private String date;
    private String url;

    public TechMeetup(String _summary, String _city, String _description, String _date, String _url){
        summary = _summary;
        city = _city;
        description = _description;
        date = _date;
        url = _url;
    }

    public String getSummary(){
        return summary;
    }

    public String getCity(){ return city; }

    public String getDescription(){
        return description;
    }

    public String getDate(){ return date; }

    public String getUrl(){ return url; }

}
