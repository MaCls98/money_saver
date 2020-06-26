package com.theoffice.moneysaver.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyFileUtils {

    private static String photoPath;
    private static Activity stActivity;

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

    public static void deleteFile(String photoPath){
        new File(photoPath).delete();
    }

}
