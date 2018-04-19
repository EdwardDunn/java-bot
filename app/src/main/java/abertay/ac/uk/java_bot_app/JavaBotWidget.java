/**
 * JavaBotWidget
 * The JavaBotWidget class controls the app widget available to users. It primary function is to
 * show the user the number of questions they have asked since their last training session.
 *
 * References:
 * Add basic widget
 *   https://www.youtube.com/watch?v=eR1bUdTB8kE&t=122s
 *
 * @author  Edward Dunn
 * @version 1.0
 */

package abertay.ac.uk.java_bot_app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;


public class JavaBotWidget extends AppWidgetProvider {

    /**
     * Method to used to update the Java Bot Widget with the number of questions asked by the user
     * since the last training session.
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){

        ComponentName thisWidget = new ComponentName(context,
                JavaBotWidget.class);

        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for(int widgetId : allWidgetIds){

            // Get number of questions held in questions SQLite database
            QuestionsSQLiteDatabaseHelper questionsDb = new QuestionsSQLiteDatabaseHelper(context);
            int number = questionsDb.questionCount();
            questionsDb.close();

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.java_bot_widget);
            Log.w("Java Bot Widget", String.valueOf(number));

            // Update the 'update' TextView in the widget
            remoteViews.setTextViewText(R.id.update, String.valueOf(number));

            Intent intent = new Intent(context, JavaBotWidget.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Update onClick (additional option)
            remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId,remoteViews);

        }

    }


}

