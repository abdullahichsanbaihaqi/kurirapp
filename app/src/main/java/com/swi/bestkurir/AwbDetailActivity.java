package com.swi.bestkurir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.swi.bestkurir.Adapter.ListAwb;
import com.swi.bestkurir.dialog.CustomAlertDialog;
import com.swi.bestkurir.dialog.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AwbDetailActivity extends AppCompatActivity {

    TextView header,pengirim,penerima,alamat,alamat2,kontak,kontak2,kota,kota2,provinsi,provinsi2;
    Integer stat;
    String awb;
    String URL = "https://app.bestindo-express.co.id/api2/get_connote_detail_new";
    CustomAlertDialog loading= null;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awb_detail);
        header = findViewById(R.id.header);
        pengirim = findViewById(R.id.pengirim);
        penerima = findViewById(R.id.penerima);
        alamat = findViewById(R.id.alamat);
        alamat2 = findViewById(R.id.alamat2);
        kontak = findViewById(R.id.kontak);
        kontak2 = findViewById(R.id.kontak2);
//        kota = findViewById(R.id.kota);
//        kota2 = findViewById(R.id.kota2);
//        provinsi = findViewById(R.id.provinsi);
//        provinsi2 = findViewById(R.id.provinsi2);

        Intent intent = getIntent();
        awb = intent.getStringExtra("awb");
        stat = Integer.parseInt(intent.getStringExtra("stat"));
        Log.i("awb toas",awb);
        header.setText(awb);
        loading = DialogUtils.showProcessingMessage(AwbDetailActivity.this, "Proses..",30);
        getdata();
    }

    private void getdata(){

        queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL+"/"+awb, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.i("awb1", String.valueOf(response.length()));
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject responseObj = response.getJSONObject(i);
                        String shipper = responseObj.getString("Shipper");
                        String shipperadd = responseObj.getString("ShipperAddress");
                        String shipperphone = responseObj.getString("ShipperPhone");
                        String asal = responseObj.getString("Asal");
                        String prov1 = responseObj.getString("prov1");

                        String cons = responseObj.getString("Consignee");
                        String consadd = responseObj.getString("ConsAddress");
                        String consphone = responseObj.getString("ConsPhone");
                        String tujuan = responseObj.getString("Tujuan");
                        String prov2 = responseObj.getString("prov2");

                        pengirim.setText(shipper);
                        kontak.setText(shipperphone);
                        alamat.setText(shipperadd+" , "+asal+" , "+prov1);

                        penerima.setText(cons);
                        kontak2.setText(consphone);
                        alamat2.setText(consadd+" , "+tujuan+" , "+prov2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                loading.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error",error.toString());
                loading.dismiss();
            }
        });
        queue.add(jsonArrayRequest);
//        loading.dismiss();
    }
    public void back(View v){
        if(stat == 0) {
            Intent myIntent = new Intent(AwbDetailActivity.this,
                    HomeActivity2.class);
            AwbDetailActivity.this.startActivity(myIntent);
        }else{
            Intent myIntent = new Intent(AwbDetailActivity.this,
                    AwbActivity.class);
            AwbDetailActivity.this.startActivity(myIntent);

        }
    }

    public void UpdateAwb(View v){

        Intent myIntent = new Intent(AwbDetailActivity.this,
                UpdateAwbActivity.class);
        myIntent.putExtra("awb",awb);
        myIntent.putExtra("stat",stat.toString());
        AwbDetailActivity.this.startActivity(myIntent);
    }

    public void UndelAwb(View v){

        Intent myIntent = new Intent(AwbDetailActivity.this,
                UndelActivity.class);
        myIntent.putExtra("awb",awb);
        myIntent.putExtra("stat",stat.toString());
        AwbDetailActivity.this.startActivity(myIntent);
    }
}