package williamgbranco.comune.callback;

/**
 * Created by William Gomes de Branco on 05/10/2015.
 */
public interface SentReportCallback
{
    void done(Integer userId, Integer instId, Integer reportId);//

    void onNetworkNonAvailable();

    //void errorQueryingReport(Integer userId, Integer instId, Integer reportId);

    //void errorUploadingPhoto(String photoPath);

    //void errorUploadingVideo(String videoPath);
}
