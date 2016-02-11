package com.example.nebula.qrreader;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.AsyncTask;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.net.URL;
import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Handler handler;
    String data_str = new String();
    String log_str = new String();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    //private String scaned_number = new String("7PVGG6");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = (Handler) new Handler();
        Button buttonStartCamera = (Button) findViewById(R.id.buttonStartCamera);
        buttonStartCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(MainActivity.this).initiateScan();
            }
        });

        Button buttonSendData = (Button) findViewById(R.id.buttonSendData);
        buttonSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendData();
                updateData();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TextView textViewScannedData = (TextView) findViewById(R.id.textViewScannedData);

                String number = new String(textViewScannedData.getText().toString());

                String send_string = new String();
                try {
                    send_string = "?number=" + URLEncoder.encode(number, "UTF8");
                    Log.d("TAG", send_string);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                try {
                    URL url = new URL("http://192.168.0.249/iventory/getinfo.php" + send_string);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    data_str = InputStreamToString(con.getInputStream());
                    con.disconnect();
                    Log.d("TAG", data_str);


                } catch (Exception ex) {
                    System.out.println(ex);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView textViewNameData = (TextView) findViewById(R.id.textViewNameData);
                        TextView textViewPlaceData = (TextView) findViewById(R.id.textViewPlaceData);

                        String[] record = data_str.split("\t", 0);
                        Log.d("TAG", record[0]);
                        Log.d("TAG", record[1]);
                        Log.d("TAG", record[2]);
                        //textViewNameData.setText(record[0]);
                        textViewNameData.setText(record[0]);
                        textViewPlaceData.setText(record[1]);
                    }
                });
            }
        }).start();
    }


    private void updateData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TextView textViewScannedData = (TextView) findViewById(R.id.textViewScannedData);
                EditText editText = (EditText) findViewById(R.id.editText);

                String user = new String(editText.getText().toString());
                String number = new String(textViewScannedData.getText().toString());
                String checked = new String("0");

                RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup0);
                Integer checkedId = rg.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(checkedId);

                Log.d("TAG", (String) rb.getText());
                Log.d("TAG", checkedId.toString());
                if (rb.getText().toString().equals("OK")) {
                    checked = "1";
                }

                String send_string = new String();
                try {
                    send_string = "?number=" + URLEncoder.encode(number, "UTF8") + "&checked=" + URLEncoder.encode(checked, "UTF8") + "&user=" + URLEncoder.encode(user, "UTF8");
                    Log.d("TAG", send_string);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                try {
                    URL url = new URL("http://192.168.0.249/iventory/index.php" + send_string);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    String str = InputStreamToString(con.getInputStream());
                    con.disconnect();
                    Log.d("TAG", str);
                    log_str = send_string;
                } catch (Exception ex) {
                    System.out.println(ex);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView textViewSendResult = (TextView) findViewById(R.id.textViewSendResult);
                        textViewSendResult.setText(log_str);
                    }
                });

            }
        }).start();
    }


    static String InputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult == null) {
            Log.d(TAG, "Failed");
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (intentResult.getContents() == null) {
            Log.d(TAG, "Cancelled Scan");
        } else {
            Log.d(TAG, "Scanned");
            RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup0);
            int checkedId = rg.getCheckedRadioButtonId();
            RadioButton rb = (RadioButton) findViewById(checkedId);

            //textViewRoomData.setText(rb.getText());

            TextView textViewScannedData = (TextView) findViewById(R.id.textViewScannedData);
            try {
                //textViewScannedData.setText(URLDecoder.decode("%22KanzakiLabIventory%22%2C%20%227PVG%201I9%22%2C%20%22Alexa%20Fluor%C2%AE%20555%20Hydrazide%2C%20Tris%28Triethylammonium%29%20Salt%22", "UTF8").toString());
                textViewScannedData.setText(URLDecoder.decode(intentResult.getContents(), "UTF8").toString().replaceAll(" ", ""));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            getData();

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.nebula.qrreader/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.nebula.qrreader/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
