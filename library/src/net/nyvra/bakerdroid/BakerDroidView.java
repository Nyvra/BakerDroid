package net.nyvra.bakerdroid;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

/**
 * BakerDroid is the Android implementation of the the Baker framework HPub specification.
 * BakerDroidView overrides ViewPager and offers methods to easily read ebooks. It should be used as an Android widget.
 *
 */
public class BakerDroidView extends ViewPager {
    
    /**
     * The HPub document being showed.
     */
	private HPubDocument mDocument;
	
	/**
	 * The activity context.
	 */
	private Context mContext;
	
	/**
	 * A BakerDroidView reference to itself
	 */
	private BakerDroidView mPager;
	
	/**
	 * Listener used to dispatch HPub events
	 */
	private HPubListener mListener;
	
	/**
	 * Indicates the page which should be displayed first when the HPub is opened
	 */
	private int mInitialPage;
	
	/**
	 * Indicates the scrolling of the current page being displayed
	 */
	private int mCurrentItemScrolling;
	
	/**
	 * A HashMap of the javascript interfaces that will be added to the WebViews
	 */
	private HashMap<Object, String> mJavascriptInterfaces;
	
	/**
     * An enum used to indicate the storage mode: assets folder or external storage.
     *
     */
    public enum StorageMode {STORAGE_ASSETS_FOLDER, STORAGE_EXTERNAL}
    
    /**
     *  The storage mode
     */
    private StorageMode mStorageMode = StorageMode.STORAGE_ASSETS_FOLDER;
    
    /**
     * Boolean to check if the WebView zoom toast was already supressed
     */
    private boolean mToastSupressed = false;
    
    private WebView mWebView;
    
    private BakerWebViewClient mWebViewCLient;
    
    private BakerWebChromeClient mWebChromeClient;

    /**
    * A reference to the views being displayed in the ViewPager
    */
    private SparseArray<RelativeLayout> mCurrentViews;
    
    private int mLastPage = -1;
    
    public StorageMode getStorageMode() {
        return mStorageMode;
    }
    
    public void setStorageMode(StorageMode storageMode) {
        mStorageMode = storageMode;
    }

	public BakerDroidView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mPager = this;
		mCurrentViews = new SparseArray<RelativeLayout>();
	}
	
	public BakerDroidView(Context context) {
		super(context);
		mContext = context;
		mPager = this;
		mCurrentViews = new SparseArray<RelativeLayout>();
	}

	/**
	 * The method used to get the document being showed in BakerDroidView
	 * 
	 * @return The current document
	 */
	public HPubDocument getDocument() {
		return mDocument;
	}
	
	/**
	 * The method used to get the current WebView
	 * 
	 * @return The current page WebView.
	 */
	public WebView getCurrentPageWebView() {
	    return mWebView;
	}

	/**
	 * Load the document, showing its content when finished.
	 * 
	 * @param pathToBook The path where the document is stored
	 * @param initialPage The page that will be showed when the document is loaded
	 * @param currentItemScrolling The Y scroll position of the initial page
	 */
	public void loadDocument(final String pathToBook, int initialPage, int currentItemScrolling) {
		mInitialPage = initialPage;
		mCurrentItemScrolling = currentItemScrolling;
		
		new AsyncTask<Void, Void, Void>() {

            @Override
			protected Void doInBackground(Void... params) {
				mDocument = new HPubDocument(mContext, pathToBook, mStorageMode);
				return null;
			}
			
			protected void onPostExecute(Void result) {
			    setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    
                    @Override
                    public void onPageSelected(int position) {
                        if (position == getCurrentItem() && mLastPage != position) {
                            setWebView(position);
                            mListener.onPageSelected(position);
                        }
                        mLastPage = position;
                    }
                    
                    @Override
                    public void onPageScrolled(int arg0, float arg1, int arg2) {
                        
                    }
                    
                    @Override
                    public void onPageScrollStateChanged(int arg0) {
                        
                    }
                });
				setAdapter(new BakerDroidAdapter());
				setOffscreenPageLimit(1);
				setCurrentItem(mInitialPage);
				if (mInitialPage == 0 && mLastPage == -1) {
				    setWebView(mInitialPage);
				}
				
				if (mListener != null) mListener.onHPubLoaded();
			};
			
		}.execute();
		
	}
	
	/**
	 * Set the HPubListener
	 * 
	 * @param listener
	 */
	public void setHpubListener(HPubListener listener) {
		mListener = listener;
	}
	
	/**
	 * 
	 * @return The current page Y scrolling
	 */
	public int getCurrentItemScrolling() {
	    if (mWebView != null) {
	        return mWebView.getScrollY();
	    }
	    return -1;
	}
	
	/**
	 * Method used to add a Javascript interface to be added to the WebViews
	 * 
	 * @param jsInterface The interface object
	 * @param name The interface name
	 */
	public void addJavascriptInterface(Object jsInterface, String name) {
	    if (mJavascriptInterfaces == null) {
	        mJavascriptInterfaces = new HashMap<Object, String>();
	    }
	    mJavascriptInterfaces.put(jsInterface, name);
	}
	
	/**
	 * Used to set a HashMap of JS interfaces
	 * 
	 * @param interfaces A HashMap with the interface objects and their names
	 */
	public void setJavascriptInterfaces(HashMap<Object, String> interfaces) {
        mJavascriptInterfaces = interfaces;
    }
	
	/**
	 * BakerDroidView Adapter
	 * 
	 * @author castelanjr
	 *
	 */
	class BakerDroidAdapter extends PagerAdapter {
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
		    View view = LayoutInflater.from(mContext).inflate(R.layout.page_layout, null);
		    RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.page_layout);
		    mCurrentViews.append(position, layout);
		    container.addView(view);
		    return view;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
			mCurrentViews.remove(position);
			object = null;
		}

		@Override
		public int getCount() {
		    if (mDocument != null && mDocument.getContent() != null) {
		        return mDocument.getContent().size();
		    } else {
		        return 0;
		    }
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
		
	}
	
	@SuppressLint("SetJavaScriptEnabled")
    private void setWebView(int position) {
	    if (mWebView == null) {
	        mWebView = new WebView(mContext);
	        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	        mWebView.setLayoutParams(params);
	        mWebView.getSettings().setBuiltInZoomControls(false);
	        mWebView.getSettings().setJavaScriptEnabled(true);
	        mWebView.getSettings().setDatabaseEnabled(true);
	        mWebView.getSettings().setDatabasePath("/data/data/" + mContext.getPackageName() + "/databases/");
	        mWebView.getSettings().setDomStorageEnabled(true);
	        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
	        mWebView.getSettings().setAppCacheEnabled(false);
	        mWebView.getSettings().setLoadWithOverviewMode(true);
	        mWebView.getSettings().setUseWideViewPort(true);
	        mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
            mWebView.getSettings().setPluginState(PluginState.ON);
            mWebView.setInitialScale(1);
            
            mWebViewCLient = new BakerWebViewClient();
            mWebView.setWebViewClient(mWebViewCLient);
            
            mWebChromeClient = new BakerWebChromeClient();
            mWebView.setWebChromeClient(mWebChromeClient);
            
            if (mJavascriptInterfaces != null) {
                for (Object obj : mJavascriptInterfaces.keySet()) {
                    mWebView.addJavascriptInterface(obj, mJavascriptInterfaces.get(obj));
                }
            }
	    } else {
	        if (mListener != null && mLastPage != -1) mListener.onPageDestroyed(mLastPage);
	        mWebView.stopLoading();
	        mWebView.freeMemory();
	    }
	    
	    mWebView.setVisibility(View.INVISIBLE);
	    
	    mWebView.loadUrl(mDocument.getUrlAtPosition(position));
	}
	
	private class BakerWebViewClient extends WebViewClient {
		boolean alreadyLoaded = false;
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			Log.d("BakerDroidView", "Page started: " + url);
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			Log.d("BakerDroidView", "Page finished: " + url);
			if (!mToastSupressed) {
    			new Thread(new Runnable() {
    
                    @Override
                    public void run() {
                        SharedPreferences prefs = mContext.getSharedPreferences("WebViewSettings", Context.MODE_PRIVATE);
                        if (prefs.getInt("double_tap_toast_count", 1) > 0) {
                            prefs.edit().putInt("double_tap_toast_count", 0).commit();
                        }
                    }
    			    
    			}).start();
    			mToastSupressed = true;
			}
			
			int position = mDocument.getPositionFromPage(url);
			if (position != -1) {
    			if (position == mInitialPage) {
    				if (!alreadyLoaded && mCurrentItemScrolling > 0) {
    					alreadyLoaded = true;
    			        StringBuilder sb = new StringBuilder("javascript:window.scrollTo(0, ");
    			        sb.append(mCurrentItemScrolling);
    			        sb.append("/ window.devicePixelRatio);");
    			        view.loadUrl(sb.toString());
    			    }
    			}
    			
    			// Ugly hack to prevent the screen from blinking:
    			try {
                    Thread.sleep(350);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
    			
    			view.setVisibility(View.VISIBLE);
    			view.bringToFront();
    			Log.d("BakerDroidView", "Is WebView visible? " + (view.getVisibility() == View.VISIBLE));
    			
    			ViewGroup parent = (ViewGroup) view.getParent();
    	        if (parent != null) {
    	            parent.removeView(view);
    	        }
    			
    			RelativeLayout layout = mCurrentViews.get(position);
    	        if (layout != null) {
    	            layout.setGravity(Gravity.CENTER);
    	            layout.addView(mWebView);
    	        } else {
    	            Log.d("BakerDroidView", "Layout is null");
    	        }
    	        
    	        if (mListener != null) {
                    mListener.onPageLoaded(position);
                }
			}
		}
		
		@Override
		public void onReceivedError(WebView view, int errorCode, String description,
		        String failingUrl) {
		    if (mListener != null) mListener.onError(description);
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
	    
	    @Override
	    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
	        result.confirm();
            return true;
	    }
		
		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			super.onShowCustomView(view, callback);
			if (mListener != null) mListener.onShowCustomView(view, callback);
		}
		
		@Override
		public void onHideCustomView() {
			super.onHideCustomView();
			if (mListener != null) mListener.onHideCustomView();
		}
		
		@Override
	    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize,
	        long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
	        quotaUpdater.updateQuota(estimatedSize * 2);
	    }
	}
	
	/**
	 * BakerDroidView default listener to dispatch events
	 * 
	 * @author castelanjr
	 *
	 */
	public interface HPubListener {
	    
	    /**
	     * Event dispatched when the HPub is loaded
	     */
		public void onHPubLoaded();
		
		/**
		 * Event dispatched when the page is completely loaded
		 * 
		 * @param position the page position
		 * @param view the WebView of the page
		 */
		public void onPageLoaded(int position);
		
		public void onError(String description);
		
		public void onPageSelected(int position);
		
		/**
		 * Event dispatched when the page is destroyed
		 * 
		 * @param position the page position
		 * @param view the WebView of the page
		 */
		public void onPageDestroyed(int position);
		
		/**
		 * Notify the host application that the current page would like to show a custom View.
		 * 
		 * @param view is the view to be shown
		 * @param callback the callback
		 */
		public void onShowCustomView(View view, CustomViewCallback callback);
		
		/**
		 * Notify the host application that the current page would like to hide the custom View.
		 */
		public void onHideCustomView();
	}

}
