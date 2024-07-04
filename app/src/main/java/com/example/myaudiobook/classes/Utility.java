package com.example.myaudiobook.classes;

import android.app.Activity;
import android.widget.Toast;

public class Utility {
    private static Toast mToast = null;

    public static void showSafeToast(Activity activity, String message){
        try{
            if(activity == null) return;

            if(mToast != null) mToast.cancel();

            mToast = Toast.makeText(activity,message,Toast.LENGTH_LONG);
            mToast.show();

        }catch (Exception ignored){}
    }


}
