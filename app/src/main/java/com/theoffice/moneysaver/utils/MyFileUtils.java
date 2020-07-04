package com.theoffice.moneysaver.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.data.model.Goal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class MyFileUtils {

    private static String photoPath;
    private static Activity stActivity;

    public static void blurImage(Context context, ImageView view, Goal goal){
        //TODO Trabajando en transformar la imagen
        Glide.with(context)
                .load(goal.getGoalPhotoPath()).apply(RequestOptions.bitmapTransform(new BlurTransformation(25 - (calculatePercentage(goal) / 4), 1)))
                .placeholder(R.drawable.money_icon)
                .error(R.drawable.error_icon)
                .into(view);
    }

    private static int calculatePercentage(Goal goal) {
        int cost = goal.getGoalCost();
        int actual = goal.getGoalActualMoney();
        return (actual * 100) / cost;
    }

    public static String takeGoalPhoto(Activity activity){
        stActivity = activity;
        String photoPath = "";
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (photoIntent.resolveActivity(stActivity.getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
                photoPath = photoFile.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(stActivity,
                        "com.theoffice.moneysaver.fileprovider",
                        photoFile);
                photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                stActivity.startActivityForResult(photoIntent, AppConstants.REQUEST_IMAGE_CAPTURE);
            }

        }
        return photoPath;
    }

    private static File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = stActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        photoPath = image.getAbsolutePath();
        return image;
    }

    public static String compressImage(String goalPhotoPath, Context context) {
        try {
            File file = new File(goalPhotoPath);
            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=60;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            Matrix matrix = new Matrix();
            matrix.postRotate(90);

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            //file.createNewFile();
            File.createTempFile(file.getName().substring(0, file.getName().lastIndexOf('.')),
                    file.getName().substring(file.getName().lastIndexOf('.')), context.getCacheDir());
            File newFile = new File(context.getCacheDir().getPath() + file.getName());
            FileOutputStream outputStream = new FileOutputStream(newFile);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return rotateImage(newFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            return null;
        }
    }

    public static String rotateImage(String photoPath){
        File file = new File(photoPath);
        Bitmap bmp = BitmapFactory.decodeFile(photoPath);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
            return file.getAbsolutePath();

        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    public static void deleteFile(String photoPath){
        new File(photoPath).delete();
    }

}
