package com.uren.catchu.GeneralUtils.ImageCache;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.GeneralUtils.CircleTransform;
import com.uren.catchu.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.uren.catchu.Constants.NumericConstants.friendImageShown;
import static com.uren.catchu.Constants.StringConstants.displayRounded;
import static com.uren.catchu.Constants.StringConstants.friendsCacheDirectory;
import static com.uren.catchu.Constants.StringConstants.groupsCacheDirectory;

public class ImageLoader {

    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

    private Map<String, ImageView> imageViewsss = Collections.synchronizedMap(new WeakHashMap<String, ImageView>());

    ExecutorService executorService;
    Handler handler = new Handler();//handler to display images in UI thread
    String fileChild;
    Context context;

    public ImageLoader(Context context, String fileChild) {
        this.fileChild = fileChild;
        fileCache = new FileCache(context, fileChild);
        executorService = Executors.newFixedThreadPool(5);
        this.context = context;
    }

    public void removeImageViewFromMap(String url){
        imageViewsss.remove(url);
    }

    public int getImageId(){

        if(fileChild.equals(friendsCacheDirectory))
            return R.drawable.man;
        else if(fileChild.equals(groupsCacheDirectory))
            return R.drawable.user_groups;
        else
            return R.drawable.man;
    }

    public void DisplayImage(String url, ImageView imageView, String displayType) {

        imageViewsss.put(url, imageView);
        Bitmap bitmap = memoryCache.get(url);

        if (bitmap != null) {
            if(displayType == displayRounded) {
                bitmap = BitmapConversion.getRoundedShape(bitmap, friendImageShown, friendImageShown, null);
                imageView.setImageBitmap(bitmap);



                /*Resources res = context.getResources();
                RoundedBitmapDrawable dr =
                        RoundedBitmapDrawableFactory.create(res, bitmap);
                dr.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
                imageView.setImageDrawable(dr);*/


                /*Picasso.with(context)
                        //.load(userProfile.getResultArray().get(0).getProfilePhotoUrl())
                        .load(url)
                        .transform(new CircleTransform())
                        .into(imageView);*/

            }else{
                imageView.setImageBitmap(bitmap);
            }
                //imageView.setImageBitmap(bitmap);
        } else {
            queuePhoto(url, imageView, displayType);

            if (url != null)
                new DownloadImageTask(imageView, displayType, url).execute(url);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        String displayType;
        String url;

        public DownloadImageTask(ImageView bmImage, String displayType, String url) {
            this.bmImage = bmImage;
            this.displayType = displayType;
            this.url = url;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            if(result != null) {
                if (displayType == displayRounded) {
                    result = BitmapConversion.getRoundedShape(result, friendImageShown, friendImageShown, null);
                    bmImage.setImageBitmap(result);

                    /*Resources res = context.getResources();
                    RoundedBitmapDrawable dr =
                            RoundedBitmapDrawableFactory.create(res, result);
                    dr.setCornerRadius(Math.max(result.getWidth(), result.getHeight()) / 2.0f);
                    bmImage.setImageDrawable(dr);*/



                    /*Picasso.with(context)
                            .load(url)
                            .transform(new CircleTransform())
                            .into(bmImage);*/
                }else
                    //Picasso.with(context).load(url).into(bmImage);
                    bmImage.setImageBitmap(result);
            }
        }
    }

    private void queuePhoto(String url, ImageView imageView, String displayType) {
        PhotoToLoad p = new PhotoToLoad(url, imageView, displayType);
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String url) {
        File f = fileCache.getFile(url);

        //from SD cache
        Bitmap b = decodeFile(f);
        if (b != null)
            return b;

        //from web
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            LazyUtils.CopyStream(is, os);
            os.close();
            conn.disconnect();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();

            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;
        public String displayType;

        public PhotoToLoad(String u, ImageView i, String displayType) {
            this.url = u;
            this.imageView = i;
            this.displayType = displayType;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            try {
                if (imageViewReused(photoToLoad))
                    return;
                Bitmap bmp = getBitmap(photoToLoad.url);
                memoryCache.put(photoToLoad.url, bmp);
                if (imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {

        //String tag = imageViews.get(photoToLoad.imageView);

        String tag = photoToLoad.url;

        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null) {
                if(photoToLoad.displayType == displayRounded) {
                    bitmap = BitmapConversion.getRoundedShape(bitmap, friendImageShown, friendImageShown, null);
                    photoToLoad.imageView.setImageBitmap(bitmap);


                    /*Resources res = context.getResources();
                    RoundedBitmapDrawable dr =
                            RoundedBitmapDrawableFactory.create(res, bitmap);
                    dr.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
                    photoToLoad.imageView.setImageDrawable(dr);*/




                    /*Picasso.with(context)
                            //.load(userProfile.getResultArray().get(0).getProfilePhotoUrl())
                            .load(photoToLoad.url)
                            .transform(new CircleTransform())
                            .into(photoToLoad.imageView);*/
                }else
                    //Picasso.with(context).load(photoToLoad.url).into(photoToLoad.imageView);
                    photoToLoad.imageView.setImageBitmap(bitmap);
            } else
                photoToLoad.imageView.setImageResource(getImageId());
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

}