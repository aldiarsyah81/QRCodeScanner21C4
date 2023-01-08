package com.example.qrcodescanner21c4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //view object
    private Button buttonScanning;
    private TextView textViewName, textViewClass, textViewId;

    //qrcode scanner object
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //view object
        buttonScanning = (Button) findViewById(R.id.buttonScan);
        textViewName = (TextView) findViewById(R.id.textViewNama);
        textViewClass = (TextView) findViewById(R.id.textViewKelas);
        textViewId = (TextView) findViewById(R.id.textViewNIM);

        //inisialisasi scan object
        qrScan = new IntentIntegrator(this);

        //implementasi oneclick listener
        buttonScanning.setOnClickListener(this);
    }
    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Not Scanned", Toast.LENGTH_SHORT).show();
            }
            else {
                // JSON
                try {
                    JSONObject jsonObject = new JSONObject(result.getContents());
                    textViewName.setText(jsonObject.getString("nama"));
                    textViewClass.setText(jsonObject.getString("kelas"));
                    textViewId.setText(jsonObject.getString("nim"));

                }  catch (JSONException e){
                    e.printStackTrace();
                }

                // dial
                try {
                    Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse(result.getContents()));
                    startActivity(intent2);
                } catch (Exception e){
                    e.printStackTrace();
                }

                // QR code geografis
                try {
                    String geoUri = result.getContents();
                    Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                    startActivity(geoIntent);
                } catch (Exception e){
                    e.printStackTrace();
                }

                // email
                try{
                    String scannedContent = result.getContents();
                    // mengecek
                    if (scannedContent.contains("@")) {
                        // mengirim
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", scannedContent.replace("http://", ""), null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "QR Code Scanner");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Halo, ini email yang dihasilkan dari QR Code Scanner.");
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    } else {
                        // menampilkan
                        Toast.makeText(this, "Scanned : " + scannedContent, Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                Toast.makeText(this, "Scanned : " + result.getContents(), Toast.LENGTH_SHORT).show();
            }

            // webview
            if (Patterns.WEB_URL.matcher(result.getContents()).matches()) {
                Intent visitUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents()));
                startActivity(visitUrl);
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        qrScan.initiateScan();
    }
}