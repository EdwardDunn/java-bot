package abertay.ac.uk.java_bot_app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class MyWidget extends AppWidgetProvider {

    private static final String ACTION_CLICK = "ACTION_CLICK";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){

        ComponentName thisWidget = new ComponentName(context,
                MyWidget.class);

        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for(int widgetId : allWidgetIds){

            QuestionsSQLiteDatabaseHelper questionsDb = new QuestionsSQLiteDatabaseHelper(context);
            int number = questionsDb.questionCount();
            questionsDb.close();

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.my_widget);

            Log.w("WidgetExample", String.valueOf(number));

            remoteViews.setTextViewText(R.id.update, String.valueOf(number));

            Intent intent = new Intent(context, MyWidget.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);


            appWidgetManager.updateAppWidget(widgetId,remoteViews);

        }

    }

}

