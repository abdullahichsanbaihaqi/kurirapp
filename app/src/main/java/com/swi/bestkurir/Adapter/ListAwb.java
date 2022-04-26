package com.swi.bestkurir.Adapter;

import android.content.Context;

/**
 * Created by Ditya Geraldy on 31 March 2021
 */
public class ListAwb {

    private String awb;

    public String getPod() {
        return pod;
    }

    public void setPod(String pod) {
        this.pod = pod;
    }

    private String pod;
    private Integer stat;

    public Integer getStat() {
        return stat;
    }

    public void setStat(Integer stat) {
        this.stat = stat;
    }

    public Integer getIdawb() {
        return idawb;
    }

    public void setIdawb(Integer idawb) {
        this.idawb = idawb;
    }

    private Integer idawb;

    Context context;
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public ListAwb(String awb,Integer stat,String pod, Context context) {
        this.awb = awb;
        this.pod = pod;
        this.stat = stat;
        this.context = context;
    }

    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
    }



    @Override
    public String toString() {
        return "ListAwb{" +
                "tid='" + awb + '\'' +
                '}';
    }
}
