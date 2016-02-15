package williamgbranco.comune.callback;

/**
 * Created by William Gomes de Branco on 17/09/2015.
 */
public interface LoadingCompleteCallback
{
    void startLoadingData();

    void emptyDataSetLoaded();

    void errorLoadingData();

    void finishLoadingData();
}
