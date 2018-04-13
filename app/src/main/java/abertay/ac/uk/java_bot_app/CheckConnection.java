package abertay.ac.uk.java_bot_app;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;

/**
 * Created by Edward Dunn on 06/04/2018.
 */

public class CheckConnection {

    private Activity parentActivity;

    public CheckConnection(Activity _context){
        parentActivity = _context;
    }


    public Boolean checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) parentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            openConnectionDialog();
            return false;
        }
        else{
            return true;
        }

    }

    // Alert Dialog Box
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
