// References
// Webview progress bar - https://stackoverflow.com/questions/4331094/add-a-progress-bar-in-webview#4331437


package abertay.ac.uk.java_bot_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class StackOverflowActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String url;
    private WebView webViewer;
    public  ProgressBar loadingProgressBar;

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

        webViewer.setWebViewClient(new WebViewClient() {

                                     @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                         super.onPageStarted(view, url, favicon);
                                         loadingProgressBar.setVisibility(ProgressBar.VISIBLE);
                                         webViewer.setVisibility(View.INVISIBLE);
                                     }

                                     @Override public void onPageCommitVisible(WebView view, String url) {
                                         super.onPageCommitVisible(view, url);
                                         loadingProgressBar.setVisibility(ProgressBar.GONE);
                                         webViewer.setVisibility(View.VISIBLE);
                                         //isWebViewLoadingFirstPage=false;
                                     }
                                 });

        browseWeb(url);

        //-----------------------Drawer menu---------------------------------------///
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toogle);
        toogle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupUIViews(){
        url = "";
        webViewer = (WebView)findViewById(R.id.webView_webViewer);
        Log.d("debug", "entered setupUIViews");

        loadingProgressBar = findViewById(R.id.stack_overflow_pb_progress_bar);
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

    //-----------------------------Drawer Menu Methods--------------------------------------------//

    @Override
    public void onBackPressed(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_settings){
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.home){
            Intent searchIntent = new Intent(StackOverflowActivity.this, MainActivity.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
        else if (id == R.id.training){
            Intent searchIntent = new Intent(StackOverflowActivity.this, TrainingActivity.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
        else if (id == R.id.tech_meetups){
            Intent searchIntent = new Intent(StackOverflowActivity.this, TechMeetupsActivity.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
        else if(id == R.id.setup){
            Intent searchIntent = new Intent(StackOverflowActivity.this, SetupActivity.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
