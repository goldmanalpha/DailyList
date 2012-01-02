package com.goldmanalpha.androidutility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileHelper {

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
