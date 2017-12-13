package de.nutco.omegatemp;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by ozan on 12.12.2017.
 */

public class OzansHelper {
    public static AlertDialog.Builder createBuilder(Context context, String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        return builder;
    }

    public static AlertDialog.Builder createBuilder(Context context, String title, String message){
        AlertDialog.Builder builder = createBuilder(context, title);
        builder.setMessage(message);
        return builder;
    }

    public static void showDialogBuilder(AlertDialog.Builder adBuilder){
        AlertDialog ad = adBuilder.create();
        ad.show();
    }
}
