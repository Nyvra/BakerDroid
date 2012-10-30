package net.nyvra.bakerdroid;

import net.nyvra.bakerdroid.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class BakerDroidView extends ViewPager {
	private static final String TAG = BakerDroidView.class.getSimpleName();
	
	private HPubDocument mDocument;
	private Context mContext;
	private int[] mScrollYPositions;
	private BakerDroidView mPager;
	private SparseArray<View> mViews;
	private OnHPubLoadedListener mListener;

	public BakerDroidView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mPager = this;
		mViews = new SparseArray<View>();
	}
	
	public BakerDroidView(Context context) {
		super(context);
		mContext = context;
		mPager = this;
		mViews = new SparseArray<View>();
	}

	public HPubDocument getDocument() {
		return mDocument;
	}

	public void loadDocument(final String pathToBook, final int[] scrollPositions, final int initialPage) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				mDocument = new HPubDocument(mContext, pathToBook);
				return null;
			}
			
			protected void onPostExecute(Void result) {
				BakerDroidAdapter adapter = null;
				if (scrollPositions == null) {
					adapter = new BakerDroidAdapter();
				} else {
					adapter = new BakerDroidAdapter(scrollPositions);
				}
				setAdapter(adapter);
				setOffscreenPageLimit(1);
				setCurrentItem(initialPage);
				if (mListener != null) {
					mListener.onHPubLoaded();
				}
			};
			
		}.execute();
		
	}
	
	public void setOnHpubLoadedListener(OnHPubLoadedListener listener) {
		mListener = listener;
	}
	
	public void persistCurrentItemsScrolling() {
		for (int i = 0; i < mViews.size(); i++) {
			int key = mViews.keyAt(i);
			View value = mViews.get(key);
			WebView webView = (WebView) value.findViewById(R.id.webview);
			int scrollY = webView.getScrollY();
			mScrollYPositions[key] = scrollY;
			Log.d(TAG, String.format("Caching: Position: %d; Scroll: %d", new Object[] {key, scrollY}));
		}
	}
	
	public int[] getScrollPositions() {
		return mScrollYPositions;
	}
	
	public int getCurrentItemScrollPosition() {
		return mScrollYPositions[getCurrentItem()];
	}
	
	@Override
	public Parcelable onSaveInstanceState() {
		return super.onSaveInstanceState();
	}
	
	@Override
	public void onRestoreInstanceState(Parcelable state) {
		super.onRestoreInstanceState(state);
	}
	
	class BakerDroidAdapter extends PagerAdapter {
		BakerWebViewClient mWebViewCLient;
		BakerWebChromeClient mWebChromeClient;
		
		public BakerDroidAdapter() {
			mScrollYPositions = new int[mDocument.getContent().size()];
		}
		
		public BakerDroidAdapter(int[] savedScrollPositions) {
			mScrollYPositions = savedScrollPositions;
		}
		
		@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = LayoutInflater.from(mContext).inflate(R.layout.webview, null);
			WebView webView = (WebView) view.findViewById(R.id.webview);
			webView.loadUrl(mDocument.getUrlAtPosition(position));
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
			ProgressBar progress = (ProgressBar) view.findViewById(R.id.progressbar);
			webView.setTag(progress);
			
			container.addView(view);
			mViews.put(position, view);
			return view;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			int scrollY = ((View) object).findViewById(R.id.webview).getScrollY();
			mScrollYPositions[position] = scrollY;
			Log.d(TAG, String.format("Caching: Position: %d; Scroll: %d", new Object[] {position, scrollY}));
			mViews.remove(position);
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return mDocument.getContent().size();
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
			
			ProgressBar progress = (ProgressBar) view.getTag();
			if (progress != null) {
				view.setVisibility(View.GONE);
				progress.setVisibility(View.VISIBLE);
			}
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			int position = mDocument.getPositionFromPage(url);
			if (position != -1) {
				int scrollY = mScrollYPositions[position];
				if (scrollY > 0) {
			        StringBuilder sb = new StringBuilder("javascript:window.scrollTo(0, ");
			        sb.append(scrollY);
			        sb.append("/ window.devicePixelRatio);");
			        view.loadUrl(sb.toString());
			    }
			}
			ProgressBar progress = (ProgressBar) view.getTag();
			if (progress != null) {
				view.setVisibility(View.VISIBLE);
				progress.setVisibility(View.GONE);
			}
		}
		
		@Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
			int position = mDocument.getPositionFromPage(url);
	        if (position != -1) {
	            mPager.setCurrentItem(position, true);
	            return true;
	        } else {
	        	return false;
	        }
	    }
	}
	
	private class BakerWebChromeClient extends WebChromeClient {
		
	}
	
	public interface OnHPubLoadedListener {
		public void onHPubLoaded();
	}

}
