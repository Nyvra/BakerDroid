package net.nyvra.bakerdroid.model;

import java.util.List;

public class Book {
	private String mTitle;
	private String[] mAuthor;
	private String mUrl;
	private List<String> mContent;
	private String mHpub;
	private String[] mCreator;
	private String mPublisher;
	private String mDate;
	private String mOrientation;
	private boolean mZoomable = false;
	private String mCover;
	private String mFile;
	private List<Setting> mSettings;

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public String[] getAuthor() {
		return mAuthor;
	}

	public void setAuthor(String[] mAuthor) {
		this.mAuthor = mAuthor;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String mUrl) {
		this.mUrl = mUrl;
	}

	public List<String> getContent() {
		return mContent;
	}

	public void setContent(List<String> mContent) {
		this.mContent = mContent;
	}

	public String getHPup() {
		return mHpub;
	}

	public void setHPup(String mHPup) {
		this.mHpub = mHPup;
	}

	public String[] getCreator() {
		return mCreator;
	}

	public void setCreator(String[] mCreator) {
		this.mCreator = mCreator;
	}

	public String getPublisher() {
		return mPublisher;
	}

	public void setPublisher(String mPublisher) {
		this.mPublisher = mPublisher;
	}

	public String getDate() {
		return mDate;
	}

	public void setDate(String mDate) {
		this.mDate = mDate;
	}

	public String getOrientation() {
		return mOrientation;
	}

	public void setOrientation(String mOrientation) {
		this.mOrientation = mOrientation;
	}

	public boolean isZoomable() {
		return mZoomable;
	}

	public void setZoomable(boolean mZoomable) {
		this.mZoomable = mZoomable;
	}

	public String getCover() {
		return mCover;
	}

	public void setCover(String mCover) {
		this.mCover = mCover;
	}

	public String getFile() {
		return mFile;
	}

	public void setFile(String mFile) {
		this.mFile = mFile;
	}

	public List<Setting> getSettings() {
		return mSettings;
	}

	public void setSettings(List<Setting> mSettings) {
		this.mSettings = mSettings;
	}

}
