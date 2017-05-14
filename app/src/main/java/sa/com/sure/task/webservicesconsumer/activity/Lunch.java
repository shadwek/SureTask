package sa.com.sure.task.webservicesconsumer.activity;

import android.content.Context;
import android.content.Intent;

/**
 * Created by HussainHajjar on 5/13/2017.
 */

public class Lunch {
    public static void lunchActivity(Context fromActivity, Class toActivity){
        Intent toIntent = new Intent(fromActivity, toActivity);
        if(toIntent.resolveActivity(fromActivity.getPackageManager()) != null){
            fromActivity.startActivity(toIntent);
        }
    }
}
