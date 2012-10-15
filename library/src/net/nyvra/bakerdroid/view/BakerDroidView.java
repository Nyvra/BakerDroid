package net.nyvra.bakerdroid.view;

import net.nyvra.bakerdroid.R;
import net.nyvra.bakerdroid.model.Book;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

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
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			WebView webView = (WebView) LayoutInflater.from(mContext).inflate(R.layout.webview, null);
			webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
			webView.getSettings().setBuiltInZoomControls(true);
			webView.loadUrl(mBook.getUrlAtPosition(position));
			webView.setInitialScale(100);
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

}
