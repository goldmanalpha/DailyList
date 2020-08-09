package com.goldmanalpha.androidutility;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileHelper {

    public static String EndSlash(String path){

        if(!path.endsWith("/")){
            return path + "/";
        }

        return path;
    }

    public void CopyFile(String from, String to) throws IOException {
            FileInputStream in = new FileInputStream(from);
            FileOutputStream out = new FileOutputStream(to);
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = in.read(buf)) != -1) {
                out.write(buf, 0, i);
            }
            in.close();
            out.close();
    }
}
