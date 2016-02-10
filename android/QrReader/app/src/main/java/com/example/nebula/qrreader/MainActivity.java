package com.example.nebula.qrreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
            TextView textViewScannedData = (TextView) findViewById(R.id.textViewScannedData);
            textViewScannedData.setText(intentResult.getContents());
        }
    }
}
