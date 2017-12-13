package de.nutco.omegatemp;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * Created by ozan on 05.12.2017.
 */

public abstract class Verbindung {
    interface AnzeigeCallback{
        public void execute(double temperature, double humidity);
        public void onError(Throwable error);
    }

    protected LinearLayout btn_primary;
    protected AnzeigeCallback mAnzeigeCallback;
    protected Context mContext;
    protected Einstellungen einstellungen;

    public Verbindung(Context context, LinearLayout button, AnzeigeCallback anzeigeCallback){
        mContext = context;
        btn_primary = button;
        mAnzeigeCallback = anzeigeCallback;
        einstellungen = new Einstellungen(context);

        setupButtons();
        try {
            init();
        } catch (Exception e) {
            mAnzeigeCallback.onError(e);
        }
    }

    public void registerButton(){

    }

    public abstract void init() throws Exception;
    public abstract void execute() throws Exception;
    public abstract void setupButtons();
}
