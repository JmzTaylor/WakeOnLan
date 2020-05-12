package com.jmzsoft.wol;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        final TextView textView = findViewById(R.id.status);
        final ImageView button = findViewById(R.id.wolButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new WakeServer().execute();
            }
        });

        final ImageView button2 = findViewById(R.id.shutdownButton);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new ShutdownServer().execute();
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getServerStatus(textView);
            }
        });

        getServerStatus(textView);
    }

    public void getServerStatus(final TextView textView) {
        int MAX_LEVEL = 10000;

        Drawable[] myTextViewCompoundDrawables = textView.getCompoundDrawables();
        for(Drawable drawable: myTextViewCompoundDrawables) {

            if(drawable == null)
                continue;

            ObjectAnimator anim = ObjectAnimator.ofInt(drawable, "level", 0, MAX_LEVEL);
            anim.start();
        }
        new Thread ( new Runnable() {
            @Override
            public void run() {
                String status = getResources().getString(R.string.server_status);
                if (isURLReachable(MainActivity.this)) {
                    status += getResources().getString(R.string.online);
                    textView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                } else {
                    textView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    status += getResources().getString(R.string.offline);
                }
                textView.setText(status);

            }
        }).start();
    }
    private class WakeServer extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return WakeOnLan.sendPacket();
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(mContext, "Successfully sent magic packet", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mContext, "Magic packet was not sent successfully", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class ShutdownServer extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return Shutdown.ShutdownServer();
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(mContext, "Successfully shutdown server", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mContext, "Did not execute properly.  Ensure server is shutdown", Toast.LENGTH_LONG).show();
            }
        }
    }

    static public boolean isURLReachable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL url = new URL(Constants.URL);
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(10 * 1000);
                urlc.connect();
                return urlc.getResponseCode() == 200;
            } catch (IOException e1) {
                return false;
            }
        }
        return false;
    }
}
