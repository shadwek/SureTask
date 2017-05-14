package sa.com.sure.task.webservicesconsumer.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import sa.com.sure.task.webservicesconsumer.R;

/**
 * Created by HussainHajjar on 5/13/2017.
 */

public class Dialog {

    public static void showNoInternetDialog(Activity activity){
        List<Dialog.Button> buttons = new ArrayList<>();
        buttons.add(Dialog.createButton(activity.getString(R.string.dialog_no_internet_button_neutral),
                Button.Type.NEUTRAL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) { }
        }));

        Dialog.showDialog(activity,
                activity.getString(R.string.dialog_no_internet_title),
                activity.getString(R.string.dialog_no_internet_message), buttons);
    }

    public static Button createButton(String value, Button.Type type,
                                      DialogInterface.OnClickListener onClickListener){
        return new Button(value, type, onClickListener);
    }

    public static void showDialog(Activity activity, String title, String message, List<Button> buttons){
        DialogGenerator dialogGenerator = new DialogGenerator();
        dialogGenerator.setActivity(activity);
        dialogGenerator.setTitle(title);
        dialogGenerator.setMessage(message);
        dialogGenerator.setButtons(buttons);
        dialogGenerator.onCreateDialog(null).show();
    }

    public static class DialogGenerator extends DialogFragment{

        private Activity mActivity;
        private String mTitle;
        private String mMessage;
        private List<Button> mButtons;

        public void setActivity(Activity activity){ this.mActivity = activity; }
        public void setTitle(String title){ this.mTitle = title; }
        public void setMessage(String message){ this.mMessage = message; }
        public void setButtons(List<Button> buttons){ this.mButtons = buttons; }

        @NonNull
        @Override
        public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.mActivity);
            dialogBuilder.setMessage(this.mMessage);
            dialogBuilder.setTitle(this.mTitle);
            for(Button button : mButtons){
                switch(button.type){
                    case NEGATIVE: dialogBuilder.setNegativeButton(button.value, button.onClickListener);
                    case POSITIVE: dialogBuilder.setPositiveButton(button.value, button.onClickListener);
                    case NEUTRAL: default: dialogBuilder.setNeutralButton(button.value, button.onClickListener);
                }
            }
            return dialogBuilder.create();
        }
    }

    public static class Button{

        public enum Type{ POSITIVE, NEGATIVE, NEUTRAL}

        String value;
        Type type;
        DialogInterface.OnClickListener onClickListener;

        Button(String value, Type type, DialogInterface.OnClickListener onClickListener){
            this.value = value;
            this.type = type;
            this.onClickListener = onClickListener;
        }
    }
}
