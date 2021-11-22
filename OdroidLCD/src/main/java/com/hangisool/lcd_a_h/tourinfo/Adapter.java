package com.hangisool.lcd_a_h.tourinfo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hangisool.lcd_a_h.LcdActivity;
import com.hangisool.lcd_a_h.R;

import java.util.*;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {
    private ArrayList<ItemTourInfo> list_itemArrayList;

    public Adapter(ArrayList<ItemTourInfo> list_itemArrayList) {
        this.list_itemArrayList = list_itemArrayList;
    }

    public static class MyViewHolder extends  RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textView1;
        public TextView textView2;

        public MyViewHolder(View view){
            super(view);
            this.imageView = view.findViewById(R.id.img_touritem);
            this.textView1 = view.findViewById(R.id.txt_touritem_title);
            this.textView2 = view.findViewById(R.id.txt_touritem_detail);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tourinfo, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(holderView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(LcdActivity.mContext).load(this.list_itemArrayList.get(position).getProfile_image()).into(holder.imageView);//holder.imageView.setImageResource(this.list_itemArrayList.get(position).getProfile_image());
        holder.textView1.setText(this.list_itemArrayList.get(position).getTitle());
        holder.textView2.setText(this.list_itemArrayList.get(position).getDetail());
    }

    @Override
    public int getItemCount() {
        return list_itemArrayList.size();
    }
}
