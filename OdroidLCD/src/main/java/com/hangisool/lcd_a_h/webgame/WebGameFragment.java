package com.hangisool.lcd_a_h.webgame;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hangisool.lcd_a_h.R;

public class WebGameFragment extends Fragment {

    WebView gameWebView;
    private String myUrl = "http://192.168.11.10:12000/HTML5GAME/"; // 접속 URL (내장HTML의 경우 왼쪽과 같이 쓰고 아니면 걍 URL)

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_webview,container,false);

        gameWebView = view.findViewById(R.id.gameWebView);
        gameWebView.getSettings().setJavaScriptEnabled(true);

        gameWebView.setWebViewClient(new WebViewClient());
        gameWebView.setWebChromeClient(new WebChromeClient());
        gameWebView.setNetworkAvailable(true);
        gameWebView.getSettings().setJavaScriptEnabled(true);
        gameWebView.getSettings().setDomStorageEnabled(true);
        gameWebView.loadUrl(myUrl);


        gameWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    if(gameWebView.canGoBack()){
                        gameWebView.goBack();
                    }
                    return true;
                }else{
                    return false;
                }
            }
        });

        return view;
    }
}
