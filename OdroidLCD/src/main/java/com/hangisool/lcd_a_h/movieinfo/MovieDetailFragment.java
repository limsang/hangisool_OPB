package com.hangisool.lcd_a_h.movieinfo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hangisool.lcd_a_h.LcdActivity;
import com.hangisool.lcd_a_h.R;
import java.util.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieDetailFragment extends Fragment{
    String title,original_title,poster_path,overview,release_date,m_id;
    ArrayList<MovieYoutubeData> youtubeList;

    private YouTubePlayerView youTubeView;
    private YouTubePlayerFragment youTubePlayerFragment;
    ImageView movie_poster;
    TextView btn_detail_back;
    TextView movie_title, movie_overview;
    private String trailer01;
    ViewGroup view;

    static MovieListData movieData;
        public static MovieDetailFragment newInstance(MovieListData movieListData) {
            movieData = movieListData;
            Bundle args = new Bundle();
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            view = (ViewGroup)inflater.inflate(R.layout.fragment_movie_detail,container,false);
        youTubePlayerFragment = new YouTubePlayerFragment();
        //youTubePlayerFragment.initialize("AIzaSyD6bruq6YtcIsW7HRRDuxrFYGybHV9ZGeQ", this);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_fragment, youTubePlayerFragment);
        fragmentTransaction.commit();

        youtubeList = new ArrayList<MovieYoutubeData>();

        movie_poster = view.findViewById(R.id.movie_poster);
        btn_detail_back = view.findViewById(R.id.btn_detail_back);
        movie_title = view.findViewById(R.id.movie_title);
        movie_overview = view.findViewById(R.id.movie_overview);

        title = movieData.getTitle();
        original_title = movieData.getOriginal_title();
        poster_path = movieData.getPoster_path();
        overview = movieData.getOverview();
        release_date = movieData.getRelease_date();
        m_id = movieData.getId();

        String url = "https://image.tmdb.org/t/p/w500" + poster_path;
        Glide.with(LcdActivity.mContext)
                .load(url)
                .apply(new RequestOptions()
                        .centerInside())
                .into(movie_poster);

        movie_title.setText(title);
        movie_overview.setText(overview);

        btn_detail_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment();
            }
        });

        Log.e("id",movieData.getId());
        Log.e("vote_average",movieData.getVote_average());
        Log.e("title",title);
        Log.e("originaltitle",original_title);
        Log.e("posterpath",poster_path);
        Log.e("overview",overview);
        Log.e("release_date",release_date);

        YoutubeAsyncTask mProcessTask = new YoutubeAsyncTask();
        mProcessTask.execute(m_id);

        return view;
    }

    public void replaceFragment(){
        Log.e("MovieInfoFrag","replaceFragment");
        ((LcdActivity)getActivity()).replaceFragment(MovieInfoFragment.newInstance());
    }

    public void playVideo(final String videoId, YouTubePlayerFragment youTubePlayerFragment) {
        //initialize youtube player view
        Log.d("Youtube", "trailer: " + videoId);
        youTubePlayerFragment.initialize("AIzaSyD6bruq6YtcIsW7HRRDuxrFYGybHV9ZGeQ",
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {
                        YouTubePlayer.PlayerStyle style = YouTubePlayer.PlayerStyle.MINIMAL;
                        youTubePlayer.setPlayerStyle(style);
                        youTubePlayer.cueVideo(videoId);
                        //youTubePlayer.loadVideo(videoId);
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {
                        Log.e("Failed","initYoutubePlayer");
                        youTubeInitializationResult.getErrorDialog(getActivity(),1).show();
                    }
                });
    }

    public class YoutubeAsyncTask extends AsyncTask<String, Void, MovieYoutubeData[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(MovieYoutubeData[] youtubes) {
            super.onPostExecute(youtubes);
            if(youtubes != null) {
                //ArrayList에 차례대로 집어 넣는다.
                if (youtubes.length > 0) {
                    for (MovieYoutubeData p : youtubes) {
                        youtubeList.add(p);
                    }

                    //유튜브뷰어를 이용 화면에 출력하자.
                    trailer01 = youtubeList.get(0).getKey();
                    Log.d("Youtube", "trailer : " + trailer01);
                    //playVideo(trailer01, youTubeView);
                    playVideo(trailer01, youTubePlayerFragment);
                }
            }
        }

        @Override
        protected MovieYoutubeData[] doInBackground(String... strings) {
            String m_id = strings[0];

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/movie/"+m_id+"/videos?api_key=ebeece1c4f5aa0946098a6cf40da99a7")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                Gson gson = new GsonBuilder().create();
                JsonParser parser = new JsonParser();
                JsonElement rootObject = parser.parse(response.body().charStream())
                        .getAsJsonObject().get("results");
                MovieYoutubeData[] posts = gson.fromJson(rootObject, MovieYoutubeData[].class);
                return posts;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
