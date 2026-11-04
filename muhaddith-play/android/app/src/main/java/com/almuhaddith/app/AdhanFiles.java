package com.almuhaddith.app;

import android.content.Context;
import android.util.Base64;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class AdhanFiles {
    static File dir(Context context) {
        File file;
        try {
            file = context.getExternalFilesDir(null);
        } catch (Throwable th) {
            file = null;
        }
        if (file == null) {
            file = context.getFilesDir();
        }
        File file2 = new File(file, "adhan");
        file2.mkdirs();
        return file2;
    }

    static File file(Context context, String str) {
        if (str == null || str.contains("/") || str.contains("..") || str.contains("\\")) {
            return null;
        }
        return new File(dir(context), str);
    }

    public static boolean has(Context context, String str) {
        try {
            File file = file(context, str);
            if (file == null || !file.exists()) {
                return false;
            }
            return file.length() > 1000;
        } catch (Throwable th) {
            return false;
        }
    }

    public static String path(Context context, String str) {
        try {
            File file = file(context, str.concat(".mp3"));
            if (file == null || !file.exists() || file.length() <= 50000) {
                return null;
            }
            return file.getAbsolutePath();
        } catch (Throwable th) {
            return null;
        }
    }

    public static String read(Context context, String str) {
        try {
            File file = file(context, str);
            if (file == null || !file.exists()) {
                return "";
            }
            byte[] bArr = new byte[(int) file.length()];
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
            dataInputStream.readFully(bArr);
            dataInputStream.close();
            return Base64.encodeToString(bArr, 2);
        } catch (Throwable th) {
            return "";
        }
    }

    public static boolean save(Context context, String str, String str2) {
        try {
            File file = file(context, str);
            if (file == null || str2 == null) {
                return false;
            }
            byte[] decode = Base64.decode(str2, 0);
            if (decode.length < 1024) {
                return false;
            }
            File file2 = file(context, str.concat(".tmp"));
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            fileOutputStream.write(decode);
            fileOutputStream.flush();
            fileOutputStream.close();
            file.delete();
            return file2.renameTo(file);
        } catch (Throwable th) {
            return false;
        }
    }
}
