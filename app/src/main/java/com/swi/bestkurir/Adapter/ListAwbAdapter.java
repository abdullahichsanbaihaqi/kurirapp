package com.swi.bestkurir.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.swi.bestkurir.AwbDetailActivity;
import com.swi.bestkurir.MainActivity;
import com.swi.bestkurir.PODDetailActivity;
import com.swi.bestkurir.R;

import java.net.Inet4Address;
import java.util.List;

/**
 * Created by Ditya Geraldy on 31 March 2021
 */
public class ListAwbAdapter extends RecyclerView.Adapter<ListAwbAdapter.ListAwbVH> {

    List<ListAwb> ListAwb2;
    RecyclerView recyclerView;
    Context context;
    private String awb,pod;
    private Integer idawb,stat;

    public ListAwbAdapter(List<ListAwb> ListAwb2) {
        this.ListAwb2 = ListAwb2;
    }

    @NonNull
    @Override
    public ListAwbVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list,parent,false);
        return new ListAwbVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAwbVH holder, final int position) {
        ListAwb ListAwb = ListAwb2.get(position);
        awb = ListAwb.getAwb();
        stat = ListAwb.getStat();
        pod = ListAwb.getPod();
        context = ListAwb.getContext();
        holder.awbTxt.setText(ListAwb.getAwb());
    }

    @Override
    public int getItemCount() {
        return ListAwb2.size();
    }

    public class ListAwbVH extends RecyclerView.ViewHolder {

        TextView awbTxt;
        LinearLayout linearLayout;

        public ListAwbVH(@NonNull View itemView) {
            super(itemView);

            awbTxt = itemView.findViewById(R.id.awb);

            linearLayout = itemView.findViewById(R.id.linier_layout);
//            expendableLayout = itemView.findViewById(R.id.expendable_layout);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    CurrentTrx currentTrx = currentTrxList.get(getAdapterPosition());
//                    currentTrx.setExpendable(!currentTrx.isExpendable());
//                    notifyItemChanged(getAdapterPosition());
                    String pod = ListAwb2.get(getAdapterPosition()).getPod();
                    if(pod.equals("pod")){
                        Intent myIntent = new Intent(context, PODDetailActivity.class);
                        String awb = ListAwb2.get(getAdapterPosition()).getAwb();
                        String stat = ListAwb2.get(getAdapterPosition()).getStat().toString();
                        myIntent.putExtra("awb",awb );
                        myIntent.putExtra("stat",stat );
                        context.startActivity(myIntent);
                    }else{
                        Intent myIntent = new Intent(context, AwbDetailActivity.class);
                        String awb = ListAwb2.get(getAdapterPosition()).getAwb();
                        String stat = ListAwb2.get(getAdapterPosition()).getStat().toString();
                        myIntent.putExtra("awb",awb );
                        myIntent.putExtra("stat",stat );
                        context.startActivity(myIntent);

                    }
                }
            });
        }

    }

}
