package com.bls220.anilist;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class FetchBitmap extends AsyncTask<Void, Void, Bitmap> {
	public interface OnBitmapResultListner {
		public void onBitmapResult(Bitmap bm);
	}

	String url;
	OnBitmapResultListner callback;

	public FetchBitmap(String url, OnBitmapResultListner onBitmapResultListner) {
		this.url = url;
		this.callback = onBitmapResultListner;
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		Bitmap bm = null;

		try {
			bm = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bm; // <<< return Bitmap
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		callback.onBitmapResult(result);
	}

}