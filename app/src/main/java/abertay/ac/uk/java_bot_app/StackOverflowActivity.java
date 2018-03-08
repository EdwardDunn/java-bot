package abertay.ac.uk.java_bot_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class StackOverflowActivity extends AppCompatActivity {

    private String url;
    private WebView webViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stack_overflow);

        setupUIViews();

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        url = data.getString("url");

        if(url != null){
            webViewer.loadUrl(url);
        }

        webViewer.setWebViewClient(new WebViewClient());

        browseWeb(url);
    }

    private void setupUIViews(){
        url = "";
        webViewer = (WebView)findViewById(R.id.webView_webViewer);
        Log.d("debug", "entered setupUIViews");
    }

    private void browseWeb(String address){
        Log.d("debug", "entered browseWeb");

        if(address.contains("http://www.")){
            webViewer.loadUrl(address);
        }
        else if(address.contains("www.")){
            webViewer.loadUrl("http://" + address);
        }
        else{
            webViewer.loadUrl("http://www." + address);
        }
    }


}
