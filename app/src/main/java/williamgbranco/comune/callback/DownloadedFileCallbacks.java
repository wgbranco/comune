package williamgbranco.comune.callback;

import android.net.Uri;

/**
 * Created by William Gomes de Branco on 22/11/2015.
 */
public interface DownloadedFileCallbacks
{
    void done(String contentType, String disposition, int contentLength, String originalFileName, Uri fileUri);

    //void onNetworkNonAvailable();

}
