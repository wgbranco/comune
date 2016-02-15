package williamgbranco.comune.callback;

/**
 * Created by William Gomes de Branco on 22/11/2015.
 */
public interface UploadedFileCallbacks
{
    void done(Integer ownerId, Integer fileId, String fileName);

    void onNetworkNonAvailable();

}
