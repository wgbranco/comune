package williamgbranco.comune.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by William Gomes de Branco on 11/01/2016.
 */
public class MediaUtils
{
    public static String getRealPathFromURI(Context appContext, Uri contentUri)
    {
        Cursor cursor = null;

        try
        {
            String[] proj = {MediaStore.Video.Media.DATA};
            cursor = appContext.getApplicationContext().getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        finally
        {
            if (cursor != null) cursor.close();
        }
    }
}
