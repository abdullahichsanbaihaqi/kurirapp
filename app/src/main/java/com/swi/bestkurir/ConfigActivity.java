package com.swi.bestkurir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swi.bestkurir.Adapter.ListAwb;
import com.swi.bestkurir.database.DataHelper;
import com.swi.bestkurir.dialog.CustomAlertDialog;
import com.swi.bestkurir.dialog.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConfigActivity extends AppCompatActivity {

    CustomAlertDialog loading= null;
    EditText  editpassword, editpassword2, editpassword3;
    private String u_password,u_password2,u_password3,iduser,password;
    String urlAddress="https://app.bestindo-express.co.id/api2/ubahpassword_new";
    RequestQueue queue;
    DataHelper dataHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        queue = Volley.newRequestQueue(this);
        iduser = SharedData.getKey(ConfigActivity.this, "iduser");
        password = SharedData.getKey(ConfigActivity.this, "password");
        dataHelper = new DataHelper(this);
        initFields();
    }

    private void initFields() {
        editpassword =  findViewById(R.id.editpassword);
        editpassword2 =  findViewById(R.id.editpassword2);
        editpassword3 =  findViewById(R.id.editpassword3);
    }


    public void back(View v){

        Intent myIntent = new Intent(ConfigActivity.this,
                HomeActivity2.class);
        ConfigActivity.this.startActivity(myIntent);
    }

    public void configuser(View v){

        loading = DialogUtils.showProcessingMessage(ConfigActivity.this, "Loading..",60);
        u_password = editpassword.getText().toString().trim();
        u_password2 = editpassword2.getText().toString().trim();
        u_password3 = editpassword3.getText().toString().trim();
        if (!u_password.equals("") && !u_password2.equals("") && !u_password3.equals("")) {
            if (u_password.equals(password)) {
                if (u_password2.equals(u_password3)) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, urlAddress, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("response",response);
                            Integer value = Integer.parseInt(response);
                            if (value == 0 ) {
                                Integer userid = Integer.parseInt(iduser);
                                dataHelper.updateUser(userid,u_password2);
                                Toast.makeText(ConfigActivity.this,"berhasil ubah password",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ConfigActivity.this, HomeActivity2.class);
                                startActivity(intent);
                                finish();
                            }
                            loading.dismiss();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ConfigActivity.this,error.toString().trim(),Toast.LENGTH_SHORT).show();
                            Log.i("error",error.toString().trim());

                            loading.dismiss();
                        }
                    }){

                        @Override
                        protected Map<String, String> getParams()
                        {
                            Map<String, String>  params = new HashMap<String, String>();
                            params.put("iduser",iduser);
                            params.put("password",u_password2);

                            return params;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);


                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(getApplicationContext(), "Password tidak sama", Toast.LENGTH_SHORT).show();
                }
            } else {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "Password Lama tidak sesuai", Toast.LENGTH_SHORT).show();
            }
        }else{
            loading.dismiss();
            Toast.makeText(getApplicationContext(), "Kolom harus diisi !!", Toast.LENGTH_SHORT).show();
        }
    }
}