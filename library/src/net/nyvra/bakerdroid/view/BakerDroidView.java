package net.nyvra.bakerdroid.view;

import net.nyvra.bakerdroid.R;
import net.nyvra.bakerdroid.model.Book;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BakerDroidView extends ViewPager {
	private Book mBook;
	private Context mContext;

	public BakerDroidView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	public BakerDroidView(Context context) {
		super(context);
	}

	public Book getBook() {
		return mBook;
	}

	public void loadBook(String pathToBook) {
		this.mBook = new Book(this.mContext, pathToBook);
		this.setAdapter(new BakerDroidAdapter());
	}
	
	class BakerDroidAdapter extends PagerAdapter {
		
		@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			WebView webView = (WebView) LayoutInflater.from(mContext).inflate(R.layout.webview, null);
			webView.loadUrl(mBook.getUrlAtPosition(position));
			webView.getSettings().setBuiltInZoomControls(true);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				webView.getSettings().setDisplayZoomControls(false);
			}
			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setLoadWithOverviewMode(false);
			webView.getSettings().setUseWideViewPort(false);
			webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
			webView.setInitialScale(100);
			webView.setWebViewClient(new BakerWebViewClient());
			
			container.addView(webView);
			return webView;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return mBook.getContent().size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
		
	}
	
	private class BakerWebViewClient extends WebViewClient {
		@Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d("--------------hrhrr", url);
			int position = mBook.getPositionFromPage(url);
	        if (position != -1) {
	            BakerDroidView.this.setCurrentItem(position, true);
	            return true;
	        } else {
	        	return false;
	        }
	    }
	}

}
