/**
 * TechMeetup
 * The TechMeetups class is a custom type used for adding tech meetups to the layout in the
 * TechMeetupsActivity
 *
 * @author  Edward Dunn
 * @version 1.0
 */

package abertay.ac.uk.java_bot_app;


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
