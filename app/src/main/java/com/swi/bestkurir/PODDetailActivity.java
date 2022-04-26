package com.swi.bestkurir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.swi.bestkurir.dialog.CustomAlertDialog;
import com.swi.bestkurir.dialog.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PODDetailActivity extends AppCompatActivity {
    TextView header,editpenerima,edithubpenerima,editdesc;
    ImageView mPreviewIv;
    String awb;
    Integer stat;
    String URL = "https://app.bestindo-express.co.id/api2/get_connote_detail_new";
    CustomAlertDialog loading= null;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_o_d_detail);

        Intent intent = getIntent();
        awb = intent.getStringExtra("awb");
        stat = Integer.parseInt(intent.getStringExtra("stat"));
        header = findViewById(R.id.header);
        editpenerima = findViewById(R.id.editpenerima);
        edithubpenerima = findViewById(R.id.edithubpenerima);
        editdesc = findViewById(R.id.editdesc);
        mPreviewIv = findViewById(R.id.mPreviewIv);
        header.setText(awb);
        loading = DialogUtils.showProcessingMessage(PODDetailActivity.this, "Proses..",30);
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
                        String PODName = responseObj.getString("PODName");
                        String PODHubPenerima = responseObj.getString("PODHubPenerima");
                        String PODRemarks = responseObj.getString("PODRemarks");
                        String PODPict = responseObj.getString("PODPict");
                        Log.i("PODName",PODName);
                        Log.i("PODHubPenerima",PODHubPenerima);
                        Log.i("PODRemaks",PODRemarks);
                        editpenerima.setText(PODName);
                        edithubpenerima.setText(PODHubPenerima);
                        editdesc.setText(PODRemarks);
                        Glide.with(PODDetailActivity.this).load("https://app.bestindo-express.co.id/images_pod/"+PODPict)
                                .fitCenter() // menyesuaikan ukuran imageview
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(mPreviewIv);
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

        Intent myIntent = new Intent(PODDetailActivity.this,
                PODActivity.class);
        myIntent.putExtra("awb",awb );
        myIntent.putExtra("stat",stat );
        PODDetailActivity.this.startActivity(myIntent);

    }
}
