package com.cai.chat_05.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;


import com.cai.chat_05.utils.AttachmentManager;
import com.cai.chat_05.utils.FastDFSUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CircularImage extends MaskedImage {
	public CircularImage(Context paramContext) {
		super(paramContext);
	}

	public CircularImage(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public CircularImage(Context paramContext, AttributeSet paramAttributeSet,
						 int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	public Bitmap createMask() {
		int i = getWidth();
		int j = getHeight();
		Bitmap.Config localConfig = Bitmap.Config.ARGB_8888;
		Bitmap localBitmap = Bitmap.createBitmap(i, j, localConfig);
		Canvas localCanvas = new Canvas(localBitmap);
		Paint localPaint = new Paint(1);
		localPaint.setColor(16777216);
		float f1 = getWidth();
		float f2 = getHeight();
		RectF localRectF = new RectF(0.0F, 0.0F, f1, f2);
		localCanvas.drawOval(localRectF, localPaint);
		return localBitmap;
	}

	/**
	 * 设置头像地址
	 * 
	 * @param groupName
	 * @param path
	 */
	public void setImage(String groupName, String path) {
		Log.v("image", this + "");
		Log.v("image", path);
		File f = AttachmentManager.getFile(groupName, path);
		if (f != null) {
			Bitmap bm = BitmapFactory.decodeFile(f.getPath());
			this.setImageBitmap(bm);
		} else {

			String[] para = { groupName, path };
			new AsyncTask<String, Object, String>() {

				@Override
				protected String doInBackground(String... params) {
					String groupName = params[0];
					String path = params[1];
					byte[] bt = FastDFSUtil.getInstance().download(
							groupName + "/" + path);
					String root = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/weishe/" + groupName;

					File dir = new File(root);
					if (!dir.exists() && !dir.isDirectory()) {
						System.out.println("//不存在");
						dir.mkdirs();
					}
					String filePath = root + "/" + path.replace("/", "_");
					if (bt != null && bt.length > 0) {
						File file = new File(filePath);
						if (!file.exists()) {

							try {
								file.createNewFile();
								FileOutputStream out = new FileOutputStream(
										file);
								out.write(bt);
								out.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							return filePath;
						}
					}
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					if (result != null && !result.isEmpty()) {
						Bitmap bm = BitmapFactory.decodeFile(result);
						CircularImage.this.setImageBitmap(bm);
					}
				}
			}.execute(para);

		}
	}
}
