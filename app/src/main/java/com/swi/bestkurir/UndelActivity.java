package com.swi.bestkurir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swi.bestkurir.database.DataHelper;
import com.swi.bestkurir.dialog.CustomAlertDialog;
import com.swi.bestkurir.dialog.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UndelActivity extends AppCompatActivity {

    TextView header;
    String stat,awb,undel,iduser;
    ArrayList<String> listSelect;
    Spinner spinner;
    RequestQueue queue;
    CustomAlertDialog loading= null;
    String URL = "https://app.bestindo-express.co.id/api2/daftar_undel";
    String urlAddress="https://app.bestindo-express.co.id/api2/update_undel_awb_new";
    DataHelper dataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_undel);
        Intent intent = getIntent();
        awb = intent.getStringExtra("awb");
        stat = intent.getStringExtra("stat");
        iduser = SharedData.getKey(UndelActivity.this, "iduser");
        undel = "Pilih";
        header = findViewById(R.id.header);
        header.setText(awb);
        dataHelper = new DataHelper(this);
        spinner = findViewById(R.id.spinner);


        listSelect();
        spinner.setAdapter(new ArrayAdapter<>(UndelActivity.this,android.R.layout.simple_list_item_1,listSelect));

//        spinner.setSelection(getIndex(spinner,tid));//Seleted Item on Create

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    getdata();
                    undel = "Pilih";
                }else{
                    String sNumber = parent.getItemAtPosition(position).toString();
                    undel = sNumber;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void listSelect(){
        listSelect = new ArrayList<>();
        listSelect.add("Pilih");
    }
    private void getdata(){

        loading = DialogUtils.showProcessingMessage(UndelActivity.this, "Proses..",30);
        queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.i("awb1", String.valueOf(response.length()));
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject responseObj = response.getJSONObject(i);
                        String relasi = responseObj.getString("text");
                        Log.i("realasi",relasi);
                        listSelect.remove(relasi);
                        listSelect.add(relasi);
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
    }

    public void update_awb(View view){
        if(!undel.equals("Pilih")) {
            loading = DialogUtils.showProcessingMessage(UndelActivity.this, "Proses..", 30);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, urlAddress, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("response", response);
                    Integer value = Integer.parseInt(response);
                    if (value > 0) {
                        dataHelper.deleteResi(awb);
                        Intent intent = new Intent(UndelActivity.this, HomeActivity2.class);
                        startActivity(intent);
                        finish();
                    } else if (value == 0) {
                        Toast.makeText(UndelActivity.this, "Salah User atau Password", Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(UndelActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                    Log.i("error", error.toString().trim());

                    loading.dismiss();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("undel", undel);
                    params.put("awb", awb);
                    params.put("iduser", iduser);
                    params.put("versi", "v.2.3");

                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }

    public void back(View v){

        Intent myIntent = new Intent(UndelActivity.this,
                AwbDetailActivity.class);
        myIntent.putExtra("awb",awb );
        myIntent.putExtra("stat",stat );
        UndelActivity.this.startActivity(myIntent);

    }
}