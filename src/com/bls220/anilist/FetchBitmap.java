package com.bls220.anilist;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class FetchBitmap extends AsyncTask<Void, Void, Bitmap> {
	public interface OnBitmapResultListner {
		public void onBitmapResult(Bitmap bm);
	}

	private static final String TAG = FetchBitmap.class.getSimpleName();

	String url;
	OnBitmapResultListner callback;
	Context context;

	public FetchBitmap(Context context, String url, OnBitmapResultListner onBitmapResultListner) {
		this.url = url;
		this.callback = onBitmapResultListner;
		this.context = context;
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		Bitmap bm = null;

		try {
			URL link = new URL(url);
			String filename = link.getPath().replace("/", "-").replaceFirst("-", "/");
			Log.d(TAG, "Requesting: " + filename);
			// Is the file cached?
			File file = new File(context.getFilesDir() + filename);
			if (file.exists()) {
				Log.d(TAG, file + " exists.");
				bm = BitmapFactory.decodeFile(file.getAbsolutePath());
			} else {
				Log.d(TAG, "Fetching " + link.toString());
				bm = BitmapFactory.decodeStream(link.openConnection().getInputStream());
				if (bm != null) {
					Log.d(TAG, "Saving " + file);
					// Save image
					FileOutputStream out = context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
					bm.compress(CompressFormat.JPEG, 50, out);
					out.flush();
					out.close();
				}
			}
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