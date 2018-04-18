/**
 * CheckConnection
 * The CheckConnection class is used to check the internet connection when moving to activities
 * that require an internet connection like techmeetups. If no connection is found, an alert dialog
 * box is opened.
 *
 * @author  Edward Dunn
 * @version 1.0
 */

package abertay.ac.uk.java_bot_app;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;


public class CheckConnection {

    private Activity parentActivity;

    public CheckConnection(Activity _context){
        parentActivity = _context;
    }


    /**
     * Method used to check internet connection state
     */
    public Boolean checkConnection() {

        try {
            ConnectivityManager cm =
                    (ConnectivityManager) parentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            // If no connection show alert dialog
            if (!isConnected) {
                openConnectionDialog();
                return false;
            }
            else{
                return true;
            }
        }
        catch (Exception e){
            Log.d("Check connection error", "Unable to establish state of internet connection");
            return false;
        }

    }

    /**
     * Method used to show alert dialog box displaying a no connection message
     */
    private void openConnectionDialog(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(parentActivity, R.style.AlertDialogCustom));
        alertDialogBuilder.setMessage(R.string.alert_dialog_no_connection_message)
                .setTitle(R.string.alert_dialog_no_connection_title)
                .setIcon(R.drawable.icon_warning);

        alertDialogBuilder.setPositiveButton(R.string.alert_dialog_no_connection_positive,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       // Close dialog box
                       dialogInterface.cancel();
                    }
                });

        alertDialogBuilder.setNegativeButton(R.string.alert_dialog_no_connection_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Close app
                parentActivity.finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
