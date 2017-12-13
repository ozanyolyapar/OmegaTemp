package de.nutco.omegatemp;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import static junit.framework.TestSuite.warning;

/**
 * Created by ozan on 12.12.2017.
 */

public class HttpVerbindung extends Verbindung {
    public HttpVerbindung(Context context, LinearLayout button, AnzeigeCallback anzeigeCallback) {
        super(context, button, anzeigeCallback);
    }

    @Override
    public void init() {
        // nothing to do
    }

    @Override
    public void execute() {
        String url = "http://"+einstellungen.getString("url", mContext.getString(R.string.url));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    mAnzeigeCallback.execute(response.getDouble("temperature"), response.getDouble("humidity"));
                } catch (Exception e) {
                    mAnzeigeCallback.onError(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mAnzeigeCallback.onError(error);
            }
        });
        ozansSingleton.getInstance(mContext).addToRequestQueue(jsObjRequest);
    }

    @Override
    public void setupButtons() {
        btn_primary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execute();
            }
        });
        ImageView imageView = (ImageView) btn_primary.getChildAt(0);
        imageView.setImageResource(R.drawable.ic_replay_white_24dp);
    }
}
