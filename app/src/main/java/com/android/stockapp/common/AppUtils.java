package com.android.stockapp.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.android.stockapp.ui.main.MainActivity;

import java.io.File;

public class AppUtils {
    public static void tackPick(Activity context){
        File path = new File(context.getExternalFilesDir(null),"test/");    // 值为Android/data/<package-name>/files/test/
        if(!path.exists())path.mkdirs();    //    创建目录
        File photoFile = new File(path, "photo.jpeg");    //    目标文件
        String fileProviderAuthority;
        Uri photoUri;
        if(Build.VERSION.SDK_INT>=24){
            fileProviderAuthority =context.getPackageName() + ".fileProvider";
            //    格式为：content://com.zzk.a1501systemactivity.fileProvider/testdir/audio.aac,
            //    testdir是res/file_paths/file_paths.xml中定义的目录别名
            photoUri = FileProvider.getUriForFile(context, fileProviderAuthority, photoFile);
        } else {    //  Android 7 以前可以直接在intent中向其他应用分享文件
            photoUri = Uri.fromFile(photoFile);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    //  拍照动作
        if(photoFile.exists()) photoFile.delete();          //  若保存照片的文件存在，先删除
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); //  拍摄的照片保存到photoUri指定的文件
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);    //  授予对方写该文件的权限
        context.startActivityForResult(intent, 202);
    }
}
