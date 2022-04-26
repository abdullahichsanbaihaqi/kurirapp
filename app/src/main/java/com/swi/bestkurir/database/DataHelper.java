package com.swi.bestkurir.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Ditya Geraldy on 06 August 2021
 */
public class DataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "kurir.db";
    private static final int DATABASE_VERSION = 1;
    public static final String IMAGE = "image";
    private String DbName;
    private String ServerURL;

    private Integer id;
    private String foto;
    private String username;
    private String password;
    private String awb;

    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public DataHelper(Context context){
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String sql = "create table tabelfoto (" +
                "id integer primary key, " +
                "foto blob null)";
        Log.e("Database", "On create"+sql);
        db.execSQL(sql);


        String sql2 = "create table tabeluser (" +
                "id integer primary key, " +
                "username text null, "+
                "password text null,"+
                "active integer)";
        Log.e("Database", "On create"+sql2);
        db.execSQL(sql2);

        String sql3 = "create table tabelproses (" +
                "id integer primary key, " +
                "awb text null, " +
                "iduser integer null)";
        Log.e("Database", "On create"+sql3);
        db.execSQL(sql3);

        String sql4 = "create table tabelpod (" +
                "id integer primary key, " +
                "awb text null, " +
                "iduser integer null)";
        Log.e("Database", "On create"+sql4);
        db.execSQL(sql4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    /*-----------------------INSERT Config-----------------------------------------*/
    public boolean setPP( Integer idUser, byte[] foto ){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id",idUser);
        values.put("foto",foto);

        long sid = db.insert("tabelfoto",null,values);
        if(sid>0){
            return true;
        }else{
            return false;
        }
    }
    /*-----------------------INSERT Config-----------------------------------------*/
    public boolean setUser( Integer idUser, String username, String password){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id",idUser);
        values.put("username",username);
        values.put("password",password);

        long sid = db.insert("tabeluser",null,values);
        if(sid>0){
            return true;
        }else{
            return false;
        }
    }
//    public String getPP() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res = db.rawQuery("select foto from tabelfoto where id = 1 ", null);
//        res.moveToFirst();
//
//        String i = res.getString(res.getColumnIndex("foto"));
//
//        return i;
//
//    }
    public byte[] getPP(Integer iduser) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("select foto from tabelfoto where id =  "+iduser, null);
        if (cur.moveToFirst()) {
            byte[] blob = cur.getBlob(0);
            cur.close();
            return blob;
        }
        cur.close();
        return null;
    }

    public void getUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from tabeluser where username='"+username+"' and password ='"+password+"'", null);
        res.moveToFirst();

        if(res.getCount()>0){
            setUsername(res.getString(res.getColumnIndex("username")));
            setPassword(res.getString(res.getColumnIndex("password")));
            setId(res.getInt(res.getColumnIndex("id")));
        }
    }
    /*-----------------------INSERT RESI-----------------------------------------*/
    public boolean addResi( String awb, Integer idkurir ){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("awb",awb);
        values.put("iduser",idkurir);

        long sid = db.insert("tabelproses",null,values);
        if(sid>0){
            return true;
        }else{
            return false;
        }
    }

    /*-----------------------INSERT RESI POD-----------------------------------------*/
    public boolean addResiPOD( String awb, Integer idkurir ){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("awb",awb);
        values.put("iduser",idkurir);

        long sid = db.insert("tabelpod",null,values);
        if(sid>0){
            return true;
        }else{
            return false;
        }
    }
    /*-----------------------GET LIST AWB Proses-----------------------------------------*/
    public ArrayList<DataHelper> getListResi(Integer idkurir) {
        ArrayList<DataHelper> array_list = new ArrayList<DataHelper>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select * from tabelproses where iduser = "+idkurir, null );
        cursor.moveToFirst();

        while(cursor.isAfterLast() == false){
            DataHelper dataHelper = new DataHelper(null);
            dataHelper.setId(cursor.getInt(
                    cursor.getColumnIndex("id")));
            dataHelper.setAwb(cursor.getString(
                    cursor.getColumnIndex("awb")));
            array_list.add(dataHelper);
            cursor.moveToNext();
        }

        return array_list;
    }
    public ArrayList<DataHelper> getListResiPOD(Integer idkurir) {
        ArrayList<DataHelper> array_list = new ArrayList<DataHelper>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select * from tabelpod where iduser = "+idkurir, null );
        cursor.moveToFirst();

        while(cursor.isAfterLast() == false){
            DataHelper dataHelper = new DataHelper(null);
            dataHelper.setId(cursor.getInt(
                    cursor.getColumnIndex("id")));
            dataHelper.setAwb(cursor.getString(
                    cursor.getColumnIndex("awb")));
            array_list.add(dataHelper);
            cursor.moveToNext();
        }

        return array_list;
    }

    public void getUserActive() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from tabeluser where active =1", null);
        res.moveToFirst();

        if(res.getCount()>0){
            setUsername(res.getString(res.getColumnIndex("username")));
            setPassword(res.getString(res.getColumnIndex("password")));
            setId(res.getInt(res.getColumnIndex("id")));
        }
    }
    public void updateUser(Integer id, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("update tabeluser set  password = '" +password+"' where id = "
                + id );
    }
    public void updateActive(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("update tabeluser set  active = 1 where id = "
                + id );
    }

    public void clearActive() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("update tabeluser set  active = 0 " );
    }

    public boolean deletePP()
    {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("tabelfoto",  "id !=" + 0, null) > 0;
    }

    public boolean deleteResi(String awb)
    {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("tabelproses",  "awb ='" + awb+"'", null) > 0;
    }

}
