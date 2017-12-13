package de.nutco.omegatemp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by ozan on 12.12.2017.
 */

public class SocketVerbindung extends Verbindung{
    SocketClient socketClient;
    Activity mActivity;
    public SocketVerbindung(Context context, LinearLayout button, AnzeigeCallback anzeigeCallback, Activity activity) {
        super(context, button, anzeigeCallback);
        mActivity = activity;
    }

    @Override
    public void init() throws Exception {
        String sockUrl = einstellungen.getString("socketurl", "");
        if (sockUrl == ""){
            throw new Exception("No socket url is defined!");
        }
        String[] urlparts = sockUrl.split(":");
        socketClient = new SocketClient(urlparts[0], Integer.parseInt(urlparts[1]), mActivity);
        socketClient.setCallback(new SocketClient.Callback() {
            @Override
            public void onMessage(String message) {
                Log.i("SOCKETCLIENT_ONMESSAGE", message);
                JsonParser jsonParser = new JsonParser();
                JsonObject json = jsonParser.parse(message).getAsJsonObject();
                mAnzeigeCallback.execute(json.get("temperature").getAsDouble(), json.get("humidity").getAsDouble());
            }

            @Override
            public void onConnect(Socket socket) {
                socketClient.send("init");
//                socketClient.disconnect();
            }

            @Override
            public void onDisconnect(Socket socket, String message) {
            }

            @Override
            public void onConnectError(Socket socket, String message) {
                Log.e("CLIENTCALLBACK", message);
            }
        });
        socketClient.connect();
    }

    @Override
    public void execute() throws Exception {
        Log.i("SOCKETVERB", "executed");
    }

    @Override
    public void setupButtons() {
        btn_primary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    execute();
                }catch (Exception e){
                    Log.e("SOCKETVERB", e.getMessage(), e);
                }
            }
        });
        ImageView imageView = (ImageView) btn_primary.getChildAt(0);
        imageView.setImageResource(R.drawable.ic_play_arrow_white_24dp);
    }
}
