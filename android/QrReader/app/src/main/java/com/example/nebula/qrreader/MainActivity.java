package com.example.nebula.qrreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.net.URL;
import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                sendData();
            }
        });

    }

    public void sendData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TextView textViewSendResult = (TextView) findViewById(R.id.textViewSendResult);
                try {
                    URL url = new URL("http://192.168.0.249/iventory/index.php");
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    String str = InputStreamToString(con.getInputStream());
                    con.disconnect();
                    Log.d("TAG", str);
                    textViewSendResult.setText(str);
                } catch(Exception ex) {
                    System.out.println(ex);
                }
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
        if (intentResult.getContents() == null){
            Log.d(TAG, "Cancelled Scan");
        } else {
            Log.d(TAG, "Scanned");
            RadioGroup rg = (RadioGroup)findViewById(R.id.radioGroup0);
            int checkedId = rg.getCheckedRadioButtonId();
            RadioButton rb = (RadioButton)findViewById(checkedId);

            TextView textViewRoomData = (TextView) findViewById(R.id.textViewRoomData);
            textViewRoomData.setText(rb.getText());

            TextView textViewScannedData = (TextView) findViewById(R.id.textViewScannedData);
            try {
                //textViewScannedData.setText(URLDecoder.decode("%22KanzakiLabIventory%22%2C%20%227PVG%201I9%22%2C%20%22Alexa%20Fluor%C2%AE%20555%20Hydrazide%2C%20Tris%28Triethylammonium%29%20Salt%22", "UTF8").toString());
                textViewScannedData.setText(URLDecoder.decode(intentResult.getContents(), "UTF8").toString());

            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
