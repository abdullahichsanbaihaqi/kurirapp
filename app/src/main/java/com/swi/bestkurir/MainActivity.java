package com.swi.bestkurir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swi.bestkurir.database.DataHelper;
import com.swi.bestkurir.dialog.CustomAlertDialog;
import com.swi.bestkurir.dialog.DialogUtils;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    String urlAddress="https://app.bestindo-express.co.id/api2/login_kurir";
    EditText username,password;
    String Susername,Spassword;
    CustomAlertDialog loading= null;
    DataHelper dataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
//        username.setText("ichsan");
//        password.setText("1234dan5");
        dataHelper = new DataHelper(this);
        dataHelper.getUserActive();
        String Dusername = dataHelper.getUsername();
        String Dpassword = dataHelper.getPassword();
        Integer Diduser = dataHelper.getId();
        if(Dusername != null) {
            loading = DialogUtils.showProcessingMessage(MainActivity.this, "Proses..",30);
            Log.i("login", "sqlite active");
            SharedData.setKey(MainActivity.this, "iduser", Diduser.toString());
            SharedData.setKey(MainActivity.this, "username", Dusername);
            SharedData.setKey(MainActivity.this, "password", Dpassword);
            dataHelper.updateActive(Diduser);
            Intent intent = new Intent(MainActivity.this, HomeActivity2.class);
            startActivity(intent);
            finish();
            loading.dismiss();
        }
    }
    public void Login(View view){
        loading = DialogUtils.showProcessingMessage(MainActivity.this, "Proses..",30);
        Susername = username.getText().toString().trim();
        Spassword = password.getText().toString().trim();
        Log.i("username",Susername);
        Log.i("password",Spassword);
        if(!Susername.equals("") && !Spassword.equals("")){
            dataHelper.getUser(Susername,Spassword);
            String Dusername = dataHelper.getUsername();
            String Dpassword = dataHelper.getPassword();
            Integer Diduser = dataHelper.getId();
            if(Dusername != null){
                Log.i("login","sqlite");
                SharedData.setKey(MainActivity.this, "iduser", Diduser.toString());
                SharedData.setKey(MainActivity.this, "username", Dusername);
                SharedData.setKey(MainActivity.this, "password", Dpassword);
                dataHelper.updateActive(Diduser);
                Intent intent = new Intent(MainActivity.this, HomeActivity2.class);
                startActivity(intent);
                finish();
                loading.dismiss();
            }else {

                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlAddress, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("response", response);
                        Integer value = Integer.parseInt(response);
                        if (value > 0) {
                            dataHelper.setUser(value,Susername,Spassword);
                            SharedData.setKey(MainActivity.this, "iduser", value.toString());
                            SharedData.setKey(MainActivity.this, "username", Susername);
                            SharedData.setKey(MainActivity.this, "password", Spassword);
                            dataHelper.updateActive(value);
                            Intent intent = new Intent(MainActivity.this, HomeActivity2.class);
                            startActivity(intent);
                            finish();
                        } else if (value == 0) {
                            Toast.makeText(MainActivity.this, "Salah User atau Password", Toast.LENGTH_SHORT).show();
                        }
                        loading.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                        Log.i("error", error.toString().trim());

                        loading.dismiss();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("username", Susername);
                        params.put("password", Spassword);

                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }
        }else{
            Toast.makeText(MainActivity.this, "field cant be empty", Toast.LENGTH_SHORT).show();
            loading.dismiss();
        }
//        Intent myIntent = new Intent(MainActivity.this, HomeActivity2.class);
//        MainActivity.this.startActivity(myIntent);
        //START ASYNC TASK
//        Sender s=new Sender(MainActivity.this,urlAddress,username,password);
//        s.execute();
    }
}