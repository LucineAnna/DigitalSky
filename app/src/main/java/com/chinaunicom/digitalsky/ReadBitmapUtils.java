package com.chinaunicom.digitalsky;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

public class ReadBitmapUtils {
    /**
     * 以最省内存的方式读取本地资源图片
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitmap(Context context, int resId){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;

        //获取图片
        InputStream inputStream = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(inputStream, null, opt);
    }
}
