package williamgbranco.comune.callback;

/**
 * Created by William Gomes de Branco on 05/10/2015.
 */
public interface SentSurveyCallback
{
    void done(Integer userId, Integer instId, Integer surveyId);

    void onNetworkNonAvailable();

    void errorQueryingSurvey(Integer userId, Integer instId, Integer surveyId);
}
