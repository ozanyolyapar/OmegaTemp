package de.nutco.omegatemp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class anzeigeActivity extends AppCompatActivity {
    static final String TAG = "ANZEIGE";
    public String mStatus = "Verbunden.";
    protected View layout;
    protected TextView tv_temp;
    protected TextView tv_hum;
    protected LinearLayout btn_ref;
    protected LinearLayout btn_dropup;
    protected LinearLayout ll_warning;
    protected Einstellungen einstellungen;
    private Context mContext;
    public static anzeigeActivity instance = null;
    private Verbindung.AnzeigeCallback mAnzeigeCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anzeige);

        layout = findViewById(R.id.fl_main);
        tv_temp = (TextView) findViewById(R.id.tv_temp);
        tv_hum = (TextView) findViewById(R.id.tv_hum);
        btn_ref = (LinearLayout) findViewById(R.id.btn_refresh);
        btn_dropup = (LinearLayout) findViewById(R.id.btn_dropup);
        ll_warning = (LinearLayout) findViewById(R.id.ll_warning);
        einstellungen = new Einstellungen(this);
        mContext = getApplicationContext();
        instance = this;
        mAnzeigeCallback = new Verbindung.AnzeigeCallback() {
            @Override
            public void execute(double temperature, double humidity) {
                resetWarning();
                setVisuals(temperature, humidity);
            }

            @Override
            public void onError(Throwable error) {
                Log.e("REQUEST_ERROR", error.getMessage(), error);
                String warning = "";
                if (error instanceof TimeoutError){
                    warning = "Server reagiert nicht.";
                }else if(error instanceof com.android.volley.NoConnectionError){
                    warning = "Keine Verbindung.";
                }else if(error instanceof com.android.volley.NetworkError){
                    warning = "Netzwerkfehler.";
                }else if(error instanceof com.android.volley.ParseError){
                    warning = "Unerwartete Antwort vom Server.";
                }else if(error instanceof com.android.volley.VolleyError){
                    warning = "Folgender Fehler trat bei der Anfrage auf: "+error.getMessage();
                }else{
                    warning = "Ein nicht klassifizierbarer Fehler ist aufgetreten. Fehlerbeschreibung: "+error.getMessage();
                }
                warning(warning);
            }
        };

        httpRequest();

        btn_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                httpRequest();
            }
        });
        btn_dropup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    AlertDialog.Builder builder = new AlertDialog.Builder(anzeigeActivity.this);
                    builder.setTitle("Na?");
                    final CharSequence[] dialogItems = new CharSequence[]{
//                            "SOCKET",
//                            "SERIELL",
                            "auto-refresh",
                            "Info",
                            "Testwert",
                            "Einstellungen"
                    };
                    builder.setItems(dialogItems, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (dialogItems[which] == "Testwert"){
                                Random r = new Random();
                                double rangeMin = (double) einstellungen.getFloat("mintemp", 16.0f);
                                double rangeMax = (double) einstellungen.getFloat("maxtemp", 32.0f);
                                double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
                                setVisuals(randomValue, 10 + (80 - 10) * r.nextDouble());
                            }else if (dialogItems[which] == "Einstellungen"){
                                einstellungen.showItemsAlert();
                            }else if (dialogItems[which] == "Info"){
                                AlertDialog.Builder adbInfo = OzansHelper.createBuilder(anzeigeActivity.this, "Info", "Die Git-Repository beinhaltet alle Informationen, die benötigt werden, um den Server zu konfigurieren.\nDiese App unterliegt den Creative-Commons-Lizenzen CC-BY-NC-ND-4.0.");
                                adbInfo.setPositiveButton("Projektseite", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String url = "https://ozan.us/ghaot";
                                        Intent browse = new Intent(Intent.ACTION_VIEW);
                                        browse.setData(Uri.parse(url));
                                        startActivity(browse);
                                    }
                                });
                                adbInfo.setNeutralButton("Lizenz", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String url = "https://ozan.us/ghaotlic";
                                        Intent browse = new Intent(Intent.ACTION_VIEW);
                                        browse.setData(Uri.parse(url));
                                        startActivity(browse);
                                    }
                                });
                                OzansHelper.showDialogBuilder(adbInfo);
                            }else if (dialogItems[which] == "SOCKET"){
                                try{
                                    SocketVerbindung socketVerbindung = new SocketVerbindung(mContext, btn_ref, mAnzeigeCallback, instance);
                                    socketVerbindung.execute();
                                }catch (Exception e){
                                    warning(e.getMessage());
                                    Log.e("ANZEIGE_SOCKET", e.getMessage(), e);
                                }
                            }else if (dialogItems[which] == "auto-refresh"){
                                try{
                                    einstellungen.putBoolean("autoloadable", true);
                                    final Handler handler = new Handler();
                                    final int delay = 2000;

                                    handler.postDelayed(new Runnable(){
                                        public void run(){
                                            httpRequest();
                                            if (einstellungen.getBoolean("autoloadable", false))
                                                handler.postDelayed(this, delay);
                                        }
                                    }, delay);
                                }catch (Exception e){
                                    warning(e.getMessage());
                                    Log.e("ANZEIGE_SOCKET", e.getMessage(), e);
                                }
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }catch (Exception e){
                    Log.e("DIALOG_CREATION", e.getMessage(), e);
                }
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        instance = this;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        instance = null;
    }


    public void httpRequest() {
        HttpVerbindung httpVerbindung = new HttpVerbindung(this, btn_ref, mAnzeigeCallback, this);
        httpVerbindung.execute();
    }

    protected void setVisuals(double temp, double hum){
        tv_temp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thermometer_white, 0, 0, 0);
        tv_hum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_opacity_white_24dp, 0, 0, 0);

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);
        tv_temp.setText(df.format(temp)+"°C");
        tv_hum.setText(df.format(hum)+"%");
        float ratio = (float) ((temp-einstellungen.getFloat("mintemp", 16))*(100f/(einstellungen.getFloat("maxtemp", 32)-einstellungen.getFloat("mintemp", 16))))/100; // ratio zwischen wohlfühltemp.
        if(ratio>1){ // sicherung, dass ratio nicht kleiner als 0 oder größer als 1 ist..
            ratio = 1f;
        }else if(ratio<0){
            ratio = 0f;
        }
        Log.e(TAG, "ratio: "+ratio);
        int warm = getColor(R.color.warm);
        int cold = getColor(R.color.cold);
        float cR = Color.red(warm) * ratio + Color.red(cold) * (1-ratio);
        float cG = Color.green(warm) * ratio + Color.green(cold) * (1-ratio);
        float cB = Color.blue(warm) * ratio + Color.blue(cold) * (1-ratio);
        Log.e(TAG, "cR: "+cR);
        Log.e(TAG, "cG: "+cG);
        Log.e(TAG, "cB: "+cB);
        int maincolor = Color.parseColor(
                "#"+
                        Integer.toString(Math.round(cR), 16)+
                        Integer.toString(Math.round(cG), 16)+
                        Integer.toString(Math.round(cB), 16)
        );
        float[] hsl = {0,0,0};
        ColorUtils.colorToHSL(maincolor, hsl);
        hsl[2] = hsl[2]*0.7f;
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] {maincolor, ColorUtils.HSLToColor(hsl)});
        gd.setCornerRadius(0f);
        layout.setBackground(gd);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(maincolor);
        }
        float[] textColor = {0,0,0};
        ColorUtils.colorToHSL(maincolor, textColor);
        int winkel = 120; // hue von 120°
        textColor[0] = textColor[0]-winkel>=0 ? textColor[0]-winkel : 360-(textColor[0]-winkel);
        textColor[2] = .9f; // light
        setTextColor(ColorUtils.HSLToColor(textColor));
    }

    protected void warning(String text){
        try{
            layout.setBackground(getDrawable(R.drawable.gradient_warm));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.setStatusBarColor(getColor(R.color.warm));
            }
            mStatus = text;
            ll_warning.setVisibility(View.VISIBLE);
            ll_warning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar snackbar = Snackbar.make(view, mStatus, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });
        }catch (Exception e){
            Log.e(TAG, e.getMessage(), e);
        }
    }

    protected void resetWarning(){
        ll_warning.setVisibility(View.GONE);
        mStatus = "";
    }

    protected void setTextColor(int color){
        // color is now forced to white...
        color = Color.WHITE;
        tv_temp.setTextColor(color);
        tv_hum.setTextColor(color);
    }
}
