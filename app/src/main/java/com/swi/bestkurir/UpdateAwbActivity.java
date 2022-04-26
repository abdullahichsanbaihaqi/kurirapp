package com.swi.bestkurir;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import com.swi.bestkurir.Adapter.ListAwb;
import com.swi.bestkurir.database.DataHelper;
import com.swi.bestkurir.dialog.CustomAlertDialog;
import com.swi.bestkurir.dialog.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateAwbActivity extends AppCompatActivity {
    TextView header,penerima,deskripsi;
    String stat,awb,Spenerima, Sdeskripsi,relasi,iduser;
    Spinner spinner;
    RequestQueue queue;
    CustomAlertDialog loading= null;
    String URL = "https://app.bestindo-express.co.id/api2/daftar_hubungan";
    String urlAddress="https://app.bestindo-express.co.id/api2/update_status_awb_new";
    ArrayList<String> listSelect;

    ImageView mPreviewIv;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;
    Bitmap bitmap;
    String encodeImageString;
    DataHelper dataHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_awb);
        Intent intent = getIntent();
        awb = intent.getStringExtra("awb");
        stat = intent.getStringExtra("stat");
        iduser = SharedData.getKey(UpdateAwbActivity.this, "iduser");
        header = findViewById(R.id.header);
        penerima = findViewById(R.id.editpenerima);
        deskripsi = findViewById(R.id.editdesc);
        mPreviewIv = findViewById(R.id.mPreviewIv);
        relasi = "Pilih";
        header.setText(awb);

        dataHelper = new DataHelper(this);
        //camera permission
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //storage permission
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

//        stat = Integer.parseInt(intent.getStringExtra("stat"));
        spinner = findViewById(R.id.spinner);


        listSelect();
        spinner.setAdapter(new ArrayAdapter<>(UpdateAwbActivity.this,android.R.layout.simple_list_item_1,listSelect));

//        spinner.setSelection(getIndex(spinner,tid));//Seleted Item on Create

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    getdata();
                    relasi = "Pilih";
                }else{
//                    loading = DialogUtils.showProcessingMessage(SaleActivity.this, "Loading..",60);
                    String sNumber = parent.getItemAtPosition(position).toString();
                    relasi = sNumber;
//                    Toast.makeText(getApplicationContext(),sNumber,Toast.LENGTH_SHORT).show();

//                    AwbList = new ArrayList<>();
//                    AwbList.add(new ListAwb(sNumber,1,AwbActivity.this));
//
//                    setRecyclerView();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void back(View v){

        Intent myIntent = new Intent(UpdateAwbActivity.this,
                AwbDetailActivity.class);
        myIntent.putExtra("awb",awb );
        myIntent.putExtra("stat",stat );
        UpdateAwbActivity.this.startActivity(myIntent);

    }

    public void listSelect(){
        listSelect = new ArrayList<>();
        listSelect.add("Pilih");
    }
    private void getdata(){

        loading = DialogUtils.showProcessingMessage(UpdateAwbActivity.this, "Proses..",30);
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
                mPreviewIv.setImageURI(image_uri);
                try {
                    InputStream inputStream = getContentResolver().openInputStream(image_uri);
                    Bitmap bitmapfull = BitmapFactory.decodeStream(inputStream);
                    bitmap = Bitmap.createScaledBitmap(bitmapfull,600,400,true);
                    encodeBitmapStream(bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                mPreviewIv.setImageURI(image_uri);;
                try {
                    InputStream inputStream = getContentResolver().openInputStream(image_uri);
                    Bitmap bitmapfull = BitmapFactory.decodeStream(inputStream);
                    bitmap = Bitmap.createScaledBitmap(bitmapfull,600,400,true);
                    encodeBitmapStream(bitmap);

                    File finalFile = new File(getRealPathFromURI(image_uri));
                    finalFile.delete();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public String getRealPathFromURI (Uri contentUri) {
        String path = null;
        String[] proj = { MediaStore.MediaColumns.DATA };
        Cursor cursor = this.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }
    private void encodeBitmapStream(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] bytesofimage = byteArrayOutputStream.toByteArray();
        encodeImageString = android.util.Base64.encodeToString(bytesofimage, Base64.DEFAULT);
        Log.i("bidmap",encodeImageString);

    }

    public void update_awb(View view){
        loading = DialogUtils.showProcessingMessage(UpdateAwbActivity.this, "Proses..",30);
        Spenerima = penerima.getText().toString().trim();
        Sdeskripsi = deskripsi.getText().toString().trim();

        if(!Spenerima.equals("") && !relasi.equals("Pilih")){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, urlAddress, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("response",response);
                    Integer value = Integer.parseInt(response);
                    if (value >0 ) {
                        Integer idkurir = Integer.parseInt(iduser);
                        dataHelper.deleteResi(awb);
                        dataHelper.addResiPOD(awb,idkurir);
                        Intent intent = new Intent(UpdateAwbActivity.this, HomeActivity2.class);
                        startActivity(intent);
                        finish();
                    } else if (value == 0) {
                        Toast.makeText(UpdateAwbActivity.this, "Salah User atau Password", Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(UpdateAwbActivity.this,error.toString().trim(),Toast.LENGTH_SHORT).show();
                    Log.i("error",error.toString().trim());

                    loading.dismiss();
                }
            }){

                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("penerima",Spenerima);
                    params.put("deskripsi",Sdeskripsi);
                    params.put("relasi",relasi);
                    params.put("upload",encodeImageString);
                    params.put("awb",awb);
                    params.put("iduser",iduser);
                    params.put("versi", "v.2.3");

                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }else{
            Toast.makeText(UpdateAwbActivity.this, "field cant be empty", Toast.LENGTH_SHORT).show();
            loading.dismiss();
        }

    }

}