package abertay.ac.uk.java_bot_app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * AppIconBadgeSetter
 *
 * References:
 *  Notification count on app icon:
 *  https://stackoverflow.com/questions/17565307/how-to-display-count-of-notifications-in-app-launcher-icon#17565479
 *
 * @author  Edward Dunn
 * @version 1.0
 */

public class AppIconBadgeSetter {

    public AppIconBadgeSetter(){

    }

    private static int iconCount;

    public static void addAppIconBadge(Context context){
        // Increment badge count
        iconCount++;

        // Set new badge count
        setBadgeCount(context, iconCount);
    }

    public static void removeAllBadges(Context context){
        // Set badge count to 0
        iconCount = 0;

        // Removes existing badge count
        setBadgeCount(context, iconCount);
    }

    private static void setBadgeCount(Context context, int count) {
        // Get class name of calling activity
        String launcherClassName = getClassNameOfCaller(context);
        if (launcherClassName == null) {
            return;
        }

        // Update app icon badge with new count of open notifications
        Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        badgeIntent.putExtra("badge_count", count);
        badgeIntent.putExtra("badge_count_package_name", context.getPackageName());
        badgeIntent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(badgeIntent);
    }

    private static String getClassNameOfCaller(Context context) {
        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // Get class name from activity.info
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }
}
