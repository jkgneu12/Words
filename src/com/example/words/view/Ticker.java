package com.example.words.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import android.widget.TextView;

import com.example.words.R;


/**
 * http://stackoverflow.com/questions/8970927/marquee-set-speed
 */
public class Ticker extends TextView {

	// scrolling feature
	private Scroller scroller;

	// milliseconds for a round of scrolling
	private int fullScrollDuration = 20000;

	// the X offset when paused
	private int xPaused = 0;

	// whether it's being paused
	private boolean paused = true;

	public Ticker(Context context, AttributeSet attrs) {
		super(context, attrs);
		setSingleLine();
		setEllipsize(null);
		setVisibility(INVISIBLE);
		setTextColor(Color.LTGRAY);
		setText(context.getResources().getString(R.string.ticker_text));
		startScroll();
	}

	/**
	 * begin to scroll the text from the original position
	 */
	public void startScroll() {
		// begin from the very right side
		xPaused = -1 * getWidth();
		// assume it's paused
		paused = true;
		resumeScroll();
	}

	/**
	 * resume the scroll from the pausing point
	 */
	public void resumeScroll() {

		if (!paused)
			return;

		setHorizontallyScrolling(true);

		scroller = new Scroller(this.getContext(), new LinearInterpolator());
		setScroller(scroller);

		int scrollingLen = calculateScrollingLen();
		int distance = scrollingLen - (getWidth() + xPaused);
		int duration = (new Double(fullScrollDuration * distance * 1.00000
				/ scrollingLen)).intValue();

		setVisibility(VISIBLE);
		scroller.startScroll(xPaused, 0, distance, 0, duration);
		paused = false;
	}

	/**
	 * calculate the scrolling length of the text in pixel
	 *
	 * @return the scrolling length in pixels
	 */
	private int calculateScrollingLen() {
		TextPaint tp = getPaint();
		Rect rect = new Rect();
		String strTxt = getText().toString();
		tp.getTextBounds(strTxt, 0, strTxt.length(), rect);
		int scrollingLen = rect.width() + getWidth();
		rect = null;
		return scrollingLen;
	}

	/**
	 * pause scrolling the text
	 */
	public void pauseScroll() {
		if (null == scroller)
			return;

		if (paused)
			return;

		paused = true;

		// abortAnimation sets the current X to be the final X,
		// and sets isFinished to be true
		// so current position shall be saved
		xPaused = scroller.getCurrX();

		scroller.abortAnimation();
	}

	@Override
	/*
	 * override the computeScroll to restart scrolling when finished so as that
	 * the text is scrolled forever
	 */
	public void computeScroll() {
		super.computeScroll();

		if (null == scroller) return;

		if (scroller.isFinished() && (!paused)) {
			this.startScroll();
		}
	}

	public int getRndDuration() {
		return fullScrollDuration;
	}

	public void setRndDuration(int duration) {
		this.fullScrollDuration = duration;
	}

	public boolean isPaused() {
		return paused;
	}
}
