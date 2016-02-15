package williamgbranco.comune.server.httpUtility;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import williamgbranco.comune.callback.DownloadedFileCallbacks;
import williamgbranco.comune.util.Constants;

/**
 * A utility that downloads a file from a URL.
 * @author www.codejava.net, modified by William Gomes de Branco
 *
 */
public class HttpDownloadUtility
{
    private static final String TAG = "HttpDownloadUtility";
    private static final int BUFFER_SIZE = 4096;

    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
    public static void downloadFile(int code, String fileURL, String saveDir, DownloadedFileCallbacks pCallbacks)
            throws IOException
    {
        Log.i(TAG, "URL: " + fileURL);

        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        String fileName = null;
        String disposition = null;
        String contentType = null;
        int contentLength = 0;
        Uri saveFileUri = null;
        File storageDir;
        File media = null;

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            fileName = "";
            disposition = httpConn.getHeaderField("Content-Disposition");
            contentType = httpConn.getContentType();
            contentLength = httpConn.getContentLength();

            /*if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }*/

            Log.i(TAG, "Content-Type = " + contentType);
            Log.i(TAG, "Content-Disposition = " + disposition);
            Log.i(TAG, "Content-Length = " + contentLength);
            //Log.i(TAG, "fileName = " + fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();

            storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            if (Constants.CODE_DOWNLOAD_PICTURE == code)
            {
                media = File.createTempFile(
                        saveDir,   //prefix
                        ".jpg",    //suffix
                        storageDir //directory
                );            }
            else if (Constants.CODE_DOWNLOAD_FOOTAGE == code)
            {
                media = File.createTempFile(
                        saveDir,   //prefix
                        ".mp4",    //suffix
                        storageDir //directory
                );            }


            //String saveFilePath = saveDir + File.separator + fileName;
            saveFileUri = Uri.fromFile(media);
            Log.i(TAG, "saveFileUri: "+saveFileUri);

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(media.getPath());

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            Log.i(TAG, "File downloaded");
        } else {
            Log.i(TAG, "No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();

        pCallbacks.done(contentType, disposition, contentLength, fileName, saveFileUri);
    }
}
