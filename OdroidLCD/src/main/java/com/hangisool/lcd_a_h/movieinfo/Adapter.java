package com.hangisool.lcd_a_h.movieinfo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hangisool.lcd_a_h.R;
import java.util.*;

public class Adapter extends RecyclerView.Adapter<Adapter.RecyclerViewHolders>{

        private ArrayList<MovieListData> mMovieList;
        private LayoutInflater mInflate;
        private Context mContext;
        private MovieInfoFragment fragment;

        //constructor
    public Adapter(Context context, ArrayList<MovieListData> itemList, MovieInfoFragment fragment) {
            this.mContext = context;
            this.mInflate = LayoutInflater.from(context);
            this.mMovieList = itemList;
            this.fragment = fragment;
        }

        @NonNull
        @Override
        public RecyclerViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflate.inflate(R.layout.item_movieinfo, parent, false);
            RecyclerViewHolders viewHolder = new RecyclerViewHolders(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolders holder, int position) {
            //포스터만 출력하자.
            String url = "https://image.tmdb.org/t/p/w500" + mMovieList.get(position).getPoster_path();
            Glide.with(mContext)
                    .load(url)
                    .apply(new RequestOptions()
                            .centerInside())
                    .into(holder.imageView);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment.replaceFragment(mMovieList.get(position));//Change Fragment to MovieDetailFragment
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.mMovieList.size();
        }


        //뷰홀더 - 따로 클래스 파일로 만들어도 된다.
        public static class RecyclerViewHolders extends RecyclerView.ViewHolder {
            public ImageView imageView;

            public RecyclerViewHolders(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.iv_poster);
            }
        }

    }
