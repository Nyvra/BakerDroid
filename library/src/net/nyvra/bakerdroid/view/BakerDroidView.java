package net.nyvra.bakerdroid.view;

import net.nyvra.bakerdroid.R;
import net.nyvra.bakerdroid.model.Book;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BakerDroidView extends ViewPager {
	private Book mBook;
	private Context mContext;
	int[] mScrollYPositions;

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
		BakerWebViewClient mWebViewCLient;
		BakerWebChromeClient mWebChromeClient;
		
		public BakerDroidAdapter() {
			mScrollYPositions = new int[mBook.getContent().size()];
		}
		
		@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			WebView webView = (WebView) LayoutInflater.from(mContext).inflate(R.layout.webview, null);
			webView.loadUrl(mBook.getUrlAtPosition(position));
			webView.getSettings().setBuiltInZoomControls(true);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				webView.getSettings().setDisplayZoomControls(false);
			}
			webView.getSettings().setPluginState(PluginState.ON);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setLoadWithOverviewMode(true);
			webView.getSettings().setUseWideViewPort(true);
			webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
			webView.getSettings().setCacheMode(WebSettings.LOAD_NORMAL);
			webView.setInitialScale(1);
			
			if (mWebViewCLient == null) {
				mWebViewCLient = new BakerWebViewClient();
			}
			webView.setWebViewClient(mWebViewCLient);
			
			if (mWebChromeClient == null) {
				mWebChromeClient = new BakerWebChromeClient();
			}
			webView.setWebChromeClient(mWebChromeClient);
			container.addView(webView);
			return webView;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			mScrollYPositions[position] = ((WebView) object).getScrollY();
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
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}
		
		@Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
			int position = mBook.getPositionFromPage(url);
	        if (position != -1) {
	            BakerDroidView.this.setCurrentItem(position, true);
	            return true;
	        } else {
	        	return false;
	        }
	    }
	}
	
	private class BakerWebChromeClient extends WebChromeClient {
		
	}

}
