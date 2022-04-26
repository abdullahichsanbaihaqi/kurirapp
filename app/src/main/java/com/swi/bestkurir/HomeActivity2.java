package com.swi.bestkurir;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.swi.bestkurir.Adapter.ListAwb;
import com.swi.bestkurir.Adapter.ListAwbAdapter;
import com.swi.bestkurir.database.DataHelper;
import com.swi.bestkurir.dialog.CustomAlertDialog;
import com.swi.bestkurir.dialog.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity2 extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    TextView textView,usernameView,qtypodView,qtyprosesView;
    RequestQueue queue;
    RequestQueue queue2;
    RequestQueue queue3;
    List<ListAwb> AwbList;
    RecyclerView recyclerView;
    ImageView profileview;
    String URL = "https://app.bestindo-express.co.id/api2/list_resi";
    String URL2 = "https://app.bestindo-express.co.id/api2/list_resi_pod";
    String URL3 = "https://app.bestindo-express.co.id/api2/get_username_new";
    String URLPotoProfile = "https://app.bestindo-express.co.id/api2/update_pp";
    CustomAlertDialog loading= null;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;
    Bitmap bitmap,bitmapfull;
    String encodeImageString,iduser;

    DataHelper dataHelper;

    Spinner spinner;
    ArrayList<String> listSelect;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        iduser = SharedData.getKey(HomeActivity2.this, "iduser");
        String username = SharedData.getKey(HomeActivity2.this, "username");
        textView = findViewById(R.id.text);
        usernameView = findViewById(R.id.username);
        recyclerView = findViewById(R.id.awblist);
        qtypodView = findViewById(R.id.qtypod);
        qtyprosesView = findViewById(R.id.qtyproses);
        profileview = findViewById(R.id.profile);
        usernameView.setText(username);
        queue = Volley.newRequestQueue(this);
        queue2 = Volley.newRequestQueue(this);
        queue3 = Volley.newRequestQueue(this);

        dataHelper = new DataHelper(this);
        //camera permission
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //storage permission
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        loading = DialogUtils.showProcessingMessage(HomeActivity2.this, "Proses..",30);
        initview();
        load_pic();
    }
    private void initview(){

        spinner = findViewById(R.id.spinner);

        listSelect();
        spinner.setAdapter(new ArrayAdapter<>(HomeActivity2.this,android.R.layout.simple_list_item_1,listSelect));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    listAwb();
                    listAwbPOD();
                }else{
                    String sNumber = parent.getItemAtPosition(position).toString();
                    Toast.makeText(getApplicationContext(),sNumber,Toast.LENGTH_SHORT).show();

                    AwbList = new ArrayList<>();
                    AwbList.add(new ListAwb(sNumber,0,"proses",HomeActivity2.this));

                    setRecyclerView();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void load_pic(){

        Integer user = Integer.parseInt(iduser);
        byte[] pp = dataHelper.getPP(user);
        if (pp != null  && pp.length > 0) {
            Log.i("pp", "kaga null");
            Bitmap bitpp = getImage(pp);
//
            Uri image_uripp = getImageUri(this,bitpp);

            profileview.setImageURI(image_uripp);

        }
        else{

            JsonArrayRequest jsonArrayRequest3 = new JsonArrayRequest(Request.Method.GET, URL3+"/"+iduser, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response3) {

                    for (int i = 0; i < response3.length(); i++) {
                        try {

                            JSONObject responseObj = response3.getJSONObject(i);
                            String ProfilePic = responseObj.getString("ProfilePic");
//                        String ProfilePic = response3;
                            Log.i("ptot",ProfilePic);
                            Glide.with(HomeActivity2.this).load("https://app.bestindo-express.co.id/images_pod/"+ProfilePic)
                                    .fitCenter() // menyesuaikan ukuran imageview
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(profileview);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("error",error.toString());
                }
            });
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL3+"/"+iduser, new Response.Listener<String>() {
                @Override
                public void onResponse(String response3) {
                    Log.i("response3",response3);
                    String ProfilePic = response3;
                    Log.i("ptot",ProfilePic);
                    Glide.with(HomeActivity2.this).load("https://app.bestindo-express.co.id/images_pod/"+ProfilePic)
                            .fitCenter() // menyesuaikan ukuran imageview
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(profileview);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(HomeActivity2.this,error.toString().trim(),Toast.LENGTH_SHORT).show();
                    Log.i("error",error.toString().trim());

                }
            });

            queue3.add(jsonArrayRequest3);

        }

    }
    private void start_data(){
        loading = DialogUtils.showProcessingMessage(HomeActivity2.this, "Proses..",30);
        Integer user = Integer.parseInt(iduser);

        JsonArrayRequest jsonArrayRequest2 = new JsonArrayRequest(Request.Method.GET, URL2+"/"+iduser, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response2) {

                Integer idkurir = Integer.parseInt(iduser);
                ArrayList<DataHelper> data2 = dataHelper.getListResi(idkurir);
                ArrayList<DataHelper> datapod = dataHelper.getListResiPOD(idkurir);
                for (int i = 0; i < response2.length(); i++) {
                    try {
                        JSONObject responseObj = response2.getJSONObject(i);
                        String resi = responseObj.getString("connote_local");
                        Integer cekpod = 1;
                        for (int i2=0; i2<datapod.size(); i2++) {
                            String awbpod = datapod.get(i2).getAwb();
                            if(awbpod.equals(resi)){
                                cekpod =0;
                            }
                        }
                        if(cekpod==1 || datapod.size() == 0){
                            dataHelper.addResiPOD(resi,idkurir);
                        }

                        Integer cek = 1;
                        for (int i2=0; i2<data2.size(); i2++) {
                            String awb = data2.get(i2).getAwb();
                            if(awb.equals(resi)){
                                cek =0;
                            }
                        }
                        if(cek==0){
                            dataHelper.deleteResi(resi);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                getdata();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error",error.toString());
            }
        });
        queue2.add(jsonArrayRequest2);
    }
    private void getdata() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL+"/"+iduser, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Integer idkurir = Integer.parseInt(iduser);
                ArrayList<DataHelper> data2 = dataHelper.getListResi(idkurir);
                AwbList = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject responseObj = response.getJSONObject(i);
                        String resi = responseObj.getString("connote_local");
                        Integer cek = 1;
                        for (int i2=0; i2<data2.size(); i2++) {
                            String awb = data2.get(i2).getAwb();
                            if(awb.equals(resi)){
                                cek =0;
                            }
                        }
                        if(cek==1 || data2.size() == 0){
                            dataHelper.addResi(resi,idkurir);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                initview();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error",error.toString());
                Toast.makeText(HomeActivity2.this,"Time Out Error",Toast.LENGTH_SHORT).show();
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonArrayRequest);

    }

    public void listAwb(){
//        ArrayList<DataHelper> data2 = dataHelper.getAllTrxDetail(TID);
        Integer idkurir = Integer.parseInt(iduser);
        ArrayList<DataHelper> data2 = dataHelper.getListResi(idkurir);
        AwbList = new ArrayList<>();
        qtyprosesView.setText(String.valueOf(data2.size()));
        String awb;
        for (int i=0; i<data2.size(); i++) {
            awb = data2.get(i).getAwb();
            AwbList.add(new ListAwb(awb,0,"proses",HomeActivity2.this));
            listSelect.remove(awb);
            listSelect.add(awb);
        }

        setRecyclerView();
    }
    public void listAwbPOD(){
        Integer idkurir = Integer.parseInt(iduser);
        ArrayList<DataHelper> data2 = dataHelper.getListResiPOD(idkurir);
        qtypodView.setText(String.valueOf(data2.size()));

    }
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }
    private void setRecyclerView() {
        ListAwbAdapter ListAwbAdapter = new ListAwbAdapter(AwbList);
        recyclerView.setAdapter(ListAwbAdapter);
        recyclerView.setHasFixedSize(true);
        loading.dismiss();
    }

    public void Showpopup(View v){

        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.menu_user);
        popupMenu.show();
    }

    public void reload(View v){
        start_data();
//        getdata();
    }


    public void PageAwbProses(View v){

        Intent myIntent = new Intent(HomeActivity2.this,
                AwbActivity.class);
        HomeActivity2.this.startActivity(myIntent);
    }
    public void PageAwbPOD(View v){

        Intent myIntent = new Intent(HomeActivity2.this,
                PODActivity.class);
        HomeActivity2.this.startActivity(myIntent);
    }
    public void Config(View v){

        Intent myIntent = new Intent(HomeActivity2.this,
                ConfigActivity.class);
        HomeActivity2.this.startActivity(myIntent);
    }

    private void logout() {
        DialogUtils.showConfirmDialog(HomeActivity2.this, "Log Out?", new CustomAlertDialog.OnCustomClickListener() {
            @Override // onclick no
            public void onClick(CustomAlertDialog alertDialog) {
                alertDialog.dismiss();

            }
        }, new CustomAlertDialog.OnCustomClickListener() {
            @Override // onclick yes
            public void onClick(CustomAlertDialog alertDialog) {
                alertDialog.dismiss();
                dataHelper.clearActive();
                Intent myIntent = new Intent(HomeActivity2.this,
                        MainActivity.class);
                HomeActivity2.this.startActivity(myIntent);

            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){

            case R.id.item1:

                logout();
                return true;
            default:
                return false;
        }
    }
    public void showImageImportDialog(View view){

        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0 ){
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }else{
                        pickCamera();
                    }
                }
                if(which == 1 ){
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }else{
                        pickGallery();
                    }
                }
            }
        });
        dialog.create().show();
    }

    private void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);

    }
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Fungsi Crop staart
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                profileview.setImageURI(image_uri);
                try {

                    InputStream inputStream = getContentResolver().openInputStream(image_uri);
                    Bitmap bitmapfull2 = BitmapFactory.decodeStream(inputStream);
                    bitmapfull = Bitmap.createScaledBitmap(bitmapfull2,300,300,true);
                    bitmap = Bitmap.createScaledBitmap(bitmapfull2,600,400,true);
                    encodeBitmapStream(bitmap);
                    update_awb();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                profileview.setImageURI(image_uri);
                try {
                    InputStream inputStream = getContentResolver().openInputStream(image_uri);
                    Bitmap bitmapfull2 = BitmapFactory.decodeStream(inputStream);
                    bitmapfull = Bitmap.createScaledBitmap(bitmapfull2,300,300,true);
                    bitmap = Bitmap.createScaledBitmap(bitmapfull2,600,400,true);
                    encodeBitmapStream(bitmap);
                    update_awb();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void encodeBitmapStream(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] bytesofimage = byteArrayOutputStream.toByteArray();
        encodeImageString = android.util.Base64.encodeToString(bytesofimage, Base64.DEFAULT);
        Log.i("bidmap",encodeImageString);

    }
    public Uri getImageUri(Context inContext, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
    public void update_awb(){
        loading = DialogUtils.showProcessingMessage(HomeActivity2.this, "Proses..",30);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLPotoProfile, new Response.Listener<String>() {
                @Override
                public void onResponse(String responsepp) {
                    Log.i("response",responsepp);
                    Integer valuepp = Integer.parseInt(responsepp);
                    if (valuepp >0 ) {
                        dataHelper.deletePP();
                        byte[] image = getBytes(bitmapfull);
                        Integer user = Integer.parseInt(iduser);
                        dataHelper.setPP(user,image);
                        Intent intent = new Intent(HomeActivity2.this, HomeActivity2.class);
                        startActivity(intent);
                        finish();
                    } else if (valuepp == 0) {
                        Toast.makeText(HomeActivity2.this, "Gagal Upload", Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(HomeActivity2.this,error.toString().trim(),Toast.LENGTH_SHORT).show();
                    Log.i("error",error.toString().trim());

                    loading.dismiss();
                }
            }){

                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("upload",encodeImageString);
                    params.put("iduser",iduser);

                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);


    }
    public void listSelect(){
        listSelect = new ArrayList<>();
        listSelect.add("Select AWB");
    }
}