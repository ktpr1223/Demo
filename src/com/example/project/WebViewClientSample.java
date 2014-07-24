package com.example.project;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
 
public class WebViewClientSample extends WebViewClient {
	
	// for intent
	public final static String EXTRA_MESSAGE ="com.example.project.message";
	
    private Dialog dialog;
    public WebViewClientSample() {
        super();
        dialog = null;
    }
 
    private void disimissDialog() {
        dialog.dismiss();
        dialog = null;
    }
 
    //ページの読み込み開始
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        dialog = new Dialog(view.getContext());
        dialog.setTitle("Now Loading");
        dialog.show();
    }
 
    //ページの読み込み完了
    @Override
    public void onPageFinished(WebView view, String url) {
        disimissDialog();
    }
 
    //ページの読み込み失敗
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (null != dialog) {
            disimissDialog();
        }
        Toast.makeText(view.getContext(), "エラー", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
    	
    //Toast.makeText(view.getContext(), view.getId(), Toast.LENGTH_SHORT).show();
    	Intent intent = new Intent(view.getContext(), SendDropBox.class);
    	String message = url;
    	intent.putExtra(EXTRA_MESSAGE, message);
    	view.getContext().startActivity(intent);
    	return true;    	
    }
}
