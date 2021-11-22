package com.hangisool.lcd_a_h.tourinfo;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.*;

import com.hangisool.lcd_a_h.R;

public class TourInfoFragment extends Fragment {
    final int LANG_KOR = 1;
    final int LANG_ENG = 2;
    final int LANG_CHN = 3;
    final int LANG_JPN = 4;
    int Language;

    final int TOUR_SPOT = 5;
    final int TOUR_FACIL = 6;
    final int TOUR_FESTI = 7;
    int TourType;

    int BeforSetting;
    TextView btnKorean, btnEnglish, btnChinese, btnJapanes, btnTourSpot, btnCulFacil, btnFesti;

    RecyclerView rv_tourInfo;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<ItemTourInfo> list_itemArrayList;

    public static TourInfoFragment newInstance() {

        Bundle args = new Bundle();

        TourInfoFragment fragment = new TourInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
         ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_tour_info,container,false);

         /*한국어,영어,일본어,중국어*관광지,문화시설,축제 4*3으로 12가지의 경우의 수가 있는데 각각 보여줘야하는 내용이다름
        따라서 먼저 보여주고 있던 내용과 같은것을 클릭하면 기존 그대로 보여주기위에 전에 보여주던 내용이 무엇인지 저장하는 변수 */
        BeforSetting = 0;

        rv_tourInfo = view.findViewById(R.id.rv_tourinfo);
        btnKorean = (TextView) view.findViewById(R.id.btn_kor);
        btnEnglish = (TextView) view.findViewById(R.id.btn_eng);
        btnChinese = (TextView) view.findViewById(R.id.btn_chn);
        btnJapanes = (TextView) view.findViewById(R.id.btn_jpn);
        btnTourSpot = (TextView) view.findViewById(R.id.btn_spot);
        btnCulFacil = (TextView) view.findViewById(R.id.btn_facil);
        btnFesti = (TextView) view.findViewById(R.id.btn_festi);

        btnKorean.setOnTouchListener(touchListener);
        btnEnglish.setOnTouchListener(touchListener);
        btnChinese.setOnTouchListener(touchListener);
        btnJapanes.setOnTouchListener(touchListener);
        btnJapanes.setOnTouchListener(touchListener);
        btnTourSpot.setOnTouchListener(touchListener);
        btnCulFacil.setOnTouchListener(touchListener);
        btnFesti.setOnTouchListener(touchListener);

        // 리사이클러뷰의 notify()처럼 데이터가 변했을 떄 성능을 높이기 위해서 사용
        rv_tourInfo.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        //구분선 속성 적용
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),new LinearLayoutManager(getActivity()).getOrientation());
        rv_tourInfo.setLayoutManager(layoutManager);
        rv_tourInfo.addItemDecoration(dividerItemDecoration);
        //리스트 초기화
        list_itemArrayList = new ArrayList<ItemTourInfo>();

        /*초기값 세팅*/
        Language = LANG_KOR;
        TourType = TOUR_SPOT;
        setInformation(Language, TourType);
        setButtonColorText(Language, TourType);
        return view;
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                //show data control
                switch(v.getId()){
                    case R.id.btn_kor:
                        Language = LANG_KOR;
                        break;
                    case R.id.btn_eng:
                        Language = LANG_ENG;
                        break;
                    case R.id.btn_chn:
                        Language = LANG_CHN;
                        break;
                    case R.id.btn_jpn:
                        Language = LANG_JPN;
                        break;
                    case R.id.btn_spot:
                        TourType = TOUR_SPOT;
                        break;
                    case R.id.btn_facil:
                        TourType = TOUR_FACIL;
                        break;
                    case R.id.btn_festi:
                        TourType = TOUR_FESTI;
                        break;
                }
                switch(v.getId()){
                    case R.id.btn_kor:
                    case R.id.btn_eng:
                    case R.id.btn_chn:
                    case R.id.btn_jpn:
                    case R.id.btn_spot:
                    case R.id.btn_facil:
                    case R.id.btn_festi:
                        if(BeforSetting != (Language*TourType)) {
                            BeforSetting = Language*TourType;
                            setButtonColorText(Language, TourType);
                            setInformation(Language, TourType);
                        }
                        break;
                }
            }
            return false;
        }
    };

    public void setButtonColorText(int language, int type){
        switch(language){
            case LANG_KOR:
                btnKorean.setBackgroundResource(R.drawable.style_tour_btn_on);
                btnChinese.setBackgroundResource(R.drawable.style_tour_btn_off);
                btnEnglish.setBackgroundResource(R.drawable.style_tour_btn_off);
                btnJapanes.setBackgroundResource(R.drawable.style_tour_btn_off);
                btnTourSpot.setText("관광지");
                btnCulFacil.setText("문화시설");
                btnFesti.setText("축제행사");
                break;
            case LANG_ENG:
                btnKorean.setBackgroundResource(R.drawable.style_tour_btn_off);
                btnChinese.setBackgroundResource(R.drawable.style_tour_btn_off);
                btnEnglish.setBackgroundResource(R.drawable.style_tour_btn_on);
                btnJapanes.setBackgroundResource(R.drawable.style_tour_btn_off);
                btnTourSpot.setText("Tourist Attractions");
                btnCulFacil.setText("Curtural Facilities");
                btnFesti.setText("Festival Event");
                break;
            case LANG_CHN:
                btnKorean.setBackgroundResource(R.drawable.style_tour_btn_off);
                btnChinese.setBackgroundResource(R.drawable.style_tour_btn_on);
                btnEnglish.setBackgroundResource(R.drawable.style_tour_btn_off);
                btnJapanes.setBackgroundResource(R.drawable.style_tour_btn_off);
                btnTourSpot.setText("觀光地");
                btnCulFacil.setText("文化設施");
                btnFesti.setText("慶典活動");
                break;
            case LANG_JPN:
                btnKorean.setBackgroundResource(R.drawable.style_tour_btn_off);
                btnChinese.setBackgroundResource(R.drawable.style_tour_btn_off);
                btnEnglish.setBackgroundResource(R.drawable.style_tour_btn_off);
                btnJapanes.setBackgroundResource(R.drawable.style_tour_btn_on);
                btnTourSpot.setText("観光地");
                btnCulFacil.setText("文化施設");
                btnFesti.setText("祝祭の催し");
                break;
        }
        switch (type){
            case TOUR_SPOT:
                btnTourSpot.setBackgroundResource(R.drawable.style_tour_btn_on);
                btnCulFacil.setBackgroundResource(R.drawable.style_tour_btn_off);
                btnFesti.setBackgroundResource(R.drawable.style_tour_btn_off);
                break;
            case TOUR_FACIL:
                btnTourSpot.setBackgroundResource(R.drawable.style_tour_btn_off);
                btnCulFacil.setBackgroundResource(R.drawable.style_tour_btn_on);
                btnFesti.setBackgroundResource(R.drawable.style_tour_btn_off);
                break;
            case TOUR_FESTI:
                btnTourSpot.setBackgroundResource(R.drawable.style_tour_btn_off);
                btnCulFacil.setBackgroundResource(R.drawable.style_tour_btn_off);
                btnFesti.setBackgroundResource(R.drawable.style_tour_btn_on);
                break;
        }
    }

    public void setInformation(int language, int type){
        switch(language){
            case LANG_KOR:
                switch (type){
                    case TOUR_SPOT:
                        setByInfoKey(TourDataUpdateThread.KOR_TOUR_KEY);
                        break;
                    case TOUR_FACIL:
                        setByInfoKey(TourDataUpdateThread.KOR_CUR_KEY);
                        break;
                    case TOUR_FESTI:
//                        setByInfoKey(TourDataUpdateThread.KOR_FEST_KEY);
                        break;
                }
                break;
            case LANG_ENG:
                switch (type){
                    case TOUR_SPOT:
                        setByInfoKey(TourDataUpdateThread.ENG_TOUR_KEY);
                        break;
                    case TOUR_FACIL:
                        setByInfoKey(TourDataUpdateThread.ENG_CUR_KEY);
                        break;
                    case TOUR_FESTI:
//                        setByInfoKey(TourDataUpdateThread.ENG_FEST_KEY);
                        break;
                }
                break;
            case LANG_CHN:
                switch (type){
                    case TOUR_SPOT:
                        setByInfoKey(TourDataUpdateThread.CHN_TOUR_KEY);
                        break;
                    case TOUR_FACIL:
                        setByInfoKey(TourDataUpdateThread.CHN_CUR_KEY);
                        break;
                    case TOUR_FESTI:
//                        setByInfoKey(TourDataUpdateThread.CHN_FEST_KEY);
                        break;
                }
                break;
            case LANG_JPN:
                switch (type){
                    case TOUR_SPOT:
                        setByInfoKey(TourDataUpdateThread.JPN_TOUR_KEY);
                        break;
                    case TOUR_FACIL:
                        setByInfoKey(TourDataUpdateThread.JPN_CUR_KEY);
                        break;
                    case TOUR_FESTI:
//                        setByInfoKey(TourDataUpdateThread.JPN_FEST_KEY);
                        break;
                }
                break;
        }
    }

    public void setByInfoKey(String key){
        Log.e("setInformation","1");
        Log.e("key --> ",key);
//        Log.e("getAreaTourData --> ", String.valueOf(TourDataUpdateThread.getAreaTourData(key).getResponse().getBody().getItems().getItem()));
//        Log.e("getAreaTourData --> ", String.valueOf(TourDataUpdateThread.getAreaTourData(key).getResponse().getBody().getItems().getItem()));
//        Log.e("getAreaTourData --> ", String.valueOf(TourDataUpdateThread.getAreaTourData(key).getResponse().getBody().getItems().getItem().get(0)));
//        Log.e("getAreaTourData --> ", String.valueOf(TourDataUpdateThread.getAreaTourData(key).getResponse().getBody().getItems().getItem().get(0).getTitle()));
        if(TourDataUpdateThread.getAreaTourData(key) != null) {
            int cycle = TourDataUpdateThread.getAreaTourData(key).getResponse().getBody().getItems().getItem().size();
            String img_url;
            String title;
            String detail;
            Log.e("cycle", String.valueOf(cycle));
            list_itemArrayList.clear();
            for (int i = 0; i < cycle; i++) {
                img_url = TourDataUpdateThread.getAreaTourData(key).getResponse().getBody().getItems().getItem().get(i).getFirstimage2();
                title = TourDataUpdateThread.getAreaTourData(key).getResponse().getBody().getItems().getItem().get(i).getTitle();
                detail = TourDataUpdateThread.getAreaTourData(key).getResponse().getBody().getItems().getItem().get(i).getAddr1();
                list_itemArrayList.add(new ItemTourInfo(img_url, title, detail));
            }
            Log.e("list_itemArrayList", String.valueOf(list_itemArrayList));

            adapter = new Adapter(list_itemArrayList);
            rv_tourInfo.setAdapter(adapter);
        }
    }
}
