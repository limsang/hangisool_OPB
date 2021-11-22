package com.hangisool.lcd_a_h.movieinfo;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hangisool.lcd_a_h.LcdActivity;
import com.hangisool.lcd_a_h.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieInfoFragment extends Fragment {
    RecyclerView rv_movieInfo;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<MovieListData> movieList;
    MovieInfoFragment movieInfoFragment;

    public static MovieInfoFragment newInstance() {

        Bundle args = new Bundle();

        MovieInfoFragment fragment = new MovieInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void replaceFragment(MovieListData data){
        Log.e("MovieInfoFrag","replaceFragment");
        ((LcdActivity)getActivity()).replaceFragment(MovieDetailFragment.newInstance(data));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_movie_info,container,false);
        rv_movieInfo = view.findViewById(R.id.rv_movieinfo);

        movieList = new ArrayList<MovieListData>();
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();

        rv_movieInfo.setHasFixedSize(true);
        rv_movieInfo.setLayoutManager(new GridLayoutManager(LcdActivity.mContext,4));
        movieInfoFragment = this;

        return view;
    }

    public class MyAsyncTask extends AsyncTask<String, Void, MovieListData[]> {
        //로딩중 표시
        ProgressDialog progressDialog = new ProgressDialog(LcdActivity.mContext);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //progressDialog.setMessage("\t로딩중...");
            //show dialog
            //progressDialog.show();
        }

        @Override
        protected MovieListData[] doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/movie/upcoming?api_key=ebeece1c4f5aa0946098a6cf40da99a7&language=ko-KR&page=1")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                Gson gson = new GsonBuilder().create();
                JsonParser parser = new JsonParser();
                JsonElement rootObject = parser.parse(response.body().charStream())
                        .getAsJsonObject().get("results");
                MovieListData[] posts = gson.fromJson(rootObject, MovieListData[].class);
                return posts;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(MovieListData[] result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            //ArrayList에 차례대로 집어 넣는다.
            if(result != null){
                if(result.length > 0){
                    for(MovieListData p : result){
                        movieList.add(p);
                    }
                }
            }

            //어답터 설정
            adapter = new Adapter(LcdActivity.mContext, movieList, movieInfoFragment);
            rv_movieInfo.setAdapter(adapter);
        }
    }

}