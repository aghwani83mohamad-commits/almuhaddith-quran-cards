package com.almuhaddith.app;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class AlySave {
    static Uri findOrCreate(Context context, String str, String str2) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        Cursor query = contentResolver.query(uri, new String[]{"_id"}, "_display_name=? AND relative_path=?", new String[]{str, "Download/"}, null);
        if (query != null) {
            if (query.moveToFirst()) {
                long j = query.getLong(0);
                query.close();
                return ContentUris.withAppendedId(uri, j);
            }
            query.close();
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("_display_name", str);
        contentValues.put("mime_type", str2);
        contentValues.put("relative_path", "Download/");
        return contentResolver.insert(uri, contentValues);
    }

    static OutputStream legacyStream(Context context, String str) {
        try {
            File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory("Download");
            externalStoragePublicDirectory.mkdirs();
            return new FileOutputStream(new File(externalStoragePublicDirectory, str));
        } catch (Throwable unused) {
            return new FileOutputStream(new File(context.getExternalFilesDir(null), str));
        }
    }

    public static String save(Context context, String str, String str2, String str3) {
        OutputStream legacyStream;
        try {
            byte[] decode = Base64.decode(str3, 0);
            if (Build.VERSION.SDK_INT >= 29) {
                legacyStream = context.getContentResolver().openOutputStream(findOrCreate(context, str, str2), "wt");
            } else {
                legacyStream = legacyStream(context, str);
            }
            legacyStream.write(decode);
            legacyStream.flush();
            legacyStream.close();
            return "ok";
        } catch (Throwable th) {
            return th.toString();
        }
    }
}
