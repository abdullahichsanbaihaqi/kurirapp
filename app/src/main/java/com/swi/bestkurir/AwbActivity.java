package com.swi.bestkurir;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
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
import com.android.volley.toolbox.Volley;
import com.swi.bestkurir.Adapter.ListAwb;
import com.swi.bestkurir.Adapter.ListAwbAdapter;
import com.swi.bestkurir.database.DataHelper;
import com.swi.bestkurir.dialog.CustomAlertDialog;
import com.swi.bestkurir.dialog.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AwbActivity extends AppCompatActivity {
    TextView textView,usernameView,qtyprosesView;
    RequestQueue queue;
    List<ListAwb> AwbList;
    RecyclerView recyclerView;
    String URL = "https://app.bestindo-express.co.id/api2/list_resi";
    String iduser;
    CustomAlertDialog loading= null;
    Spinner spinner;
    ArrayList<String> listSelect;
    DataHelper dataHelper;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awb);

        iduser = SharedData.getKey(AwbActivity.this, "iduser");
        String username = SharedData.getKey(AwbActivity.this, "username");
        textView = findViewById(R.id.text);
        usernameView = findViewById(R.id.username);
        recyclerView = findViewById(R.id.awblist);
        qtyprosesView = findViewById(R.id.qtyproses);
        spinner = findViewById(R.id.spinner);
        dataHelper = new DataHelper(this);


        listSelect();
        spinner.setAdapter(new ArrayAdapter<>(AwbActivity.this,android.R.layout.simple_list_item_1,listSelect));

//        spinner.setSelection(getIndex(spinner,tid));//Seleted Item on Create

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    getdata();
                }else{
//                    loading = DialogUtils.showProcessingMessage(SaleActivity.this, "Loading..",60);
                    String sNumber = parent.getItemAtPosition(position).toString();
                    Toast.makeText(getApplicationContext(),sNumber,Toast.LENGTH_SHORT).show();

                    AwbList = new ArrayList<>();
                    AwbList.add(new ListAwb(sNumber,1,"proses",AwbActivity.this));

                    setRecyclerView();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void getdata(){

        loading = DialogUtils.showProcessingMessage(AwbActivity.this, "Proses..",30);
        Integer idkurir = Integer.parseInt(iduser);
        ArrayList<DataHelper> data2 = dataHelper.getListResi(idkurir);
        AwbList = new ArrayList<>();
        String awb;
        for (int i=0; i<data2.size(); i++) {
            awb = data2.get(i).getAwb();
            AwbList.add(new ListAwb(awb,1,"proses",AwbActivity.this));
            listSelect.remove(awb);
            listSelect.add(awb);
        }
        setRecyclerView();

    }
    private void setRecyclerView() {
        ListAwbAdapter ListAwbAdapter = new ListAwbAdapter(AwbList);
        recyclerView.setAdapter(ListAwbAdapter);
        recyclerView.setHasFixedSize(true);
        loading.dismiss();
    }

    public void back(View v){

        Intent myIntent = new Intent(AwbActivity.this,
                HomeActivity2.class);
        AwbActivity.this.startActivity(myIntent);
    }

    private int getIndex(Spinner spinner, String tid) {
        for(int i= 0; i<spinner.getCount();i++){
            if( spinner.getItemAtPosition(i).toString().equalsIgnoreCase(tid)){
                return  i;
            }
        }
        return 0;
    }

    public void listSelect(){
        listSelect = new ArrayList<>();
        listSelect.add("Select AWB");
    }


}