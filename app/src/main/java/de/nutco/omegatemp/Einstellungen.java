package de.nutco.omegatemp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ozan on 07.12.2017.
 */

public class Einstellungen {
    public static final String PREFS_NAME = "OzansTempDingens";

    private Context mContext;
    public SharedPreferences preferences;
    public static final Map<String, String> fields = new HashMap<>();

    public Einstellungen(Context context){
        fields.put("url", "Adresse");
        fields.put("socketurl", "Socket-Adresse");
        fields.put("mintemp", "Mindesttemperatur");
        fields.put("maxtemp", "Höchsttemperatur");
        preferences = context.getSharedPreferences(PREFS_NAME, 0);

        mContext = context;
        putInt("mintemp", 16);
        putInt("maxtemp", 32);
    }

    public void showItemAlert(String key){

    }

    public void showItemsAlert(){
        AlertDialog.Builder builder = buildTheDialog("Einstellungen");
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View newLayoutView = inflater.inflate(R.layout.dialog_settings, null);
        builder.setView(newLayoutView);

        ((EditText) newLayoutView.findViewById(R.id.et_url)).setText(getString("url", "example.com"));
        ((EditText) newLayoutView.findViewById(R.id.et_socket)).setText(getString("socketurl", "example2.com"));

        builder.setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                putString("url", ((EditText) newLayoutView.findViewById(R.id.et_url)).getText().toString());
                putString("socketurl", ((EditText) newLayoutView.findViewById(R.id.et_socket)).getText().toString());
            }
        });
        showTheDialog(builder);
    }

    private AlertDialog.Builder buildTheDialog(String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        return builder;
    }

    private static void showTheDialog(AlertDialog.Builder adbuilder){
        AlertDialog ad = adbuilder.create();
        ad.show();
    }

    public void putString(String key, String value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void putBoolean(String key, Boolean value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void putFloat(String key, Float value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public void putInt(String key, int value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void putLong(String key, long value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void putStringSet(String key, Set<String> values){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(key, values);
        editor.apply();
    }

    public void remove(String key){
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public int getInt(String key, int standard){
        return preferences.getInt(key, standard);
    }

    public String getString(String key, String standard){
        return preferences.getString(key, standard);
    }

    public Boolean getBoolean(String key, Boolean standard){
        return preferences.getBoolean(key, standard);
    }

    public float getFloat(String key, float standard){
        return preferences.getFloat(key, standard);
    }

    public long getLong(String key, long standard){
        return preferences.getLong(key, standard);
    }

    public Set<String> get(String key, Set<String> standard){
        return preferences.getStringSet(key, standard);
    }
}
