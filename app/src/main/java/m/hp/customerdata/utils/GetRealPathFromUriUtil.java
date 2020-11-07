package m.hp.customerdata.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;

public class GetRealPathFromUriUtil {

    public static String getPath(Context context, Uri uri) {
        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;//是不是android4.4或以上
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {//外部存储文档
                String documentId = DocumentsContract.getDocumentId(uri);
                String[] split = documentId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
        }
        return "";
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        Log.e("uri.getAuthority()==", uri.getAuthority());
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
}
