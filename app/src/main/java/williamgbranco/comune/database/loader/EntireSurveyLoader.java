package williamgbranco.comune.database.loader;

import android.content.Context;
import android.util.Log;

import williamgbranco.comune.manager.SurveyManager;
import williamgbranco.comune.manager.UserManager;
import williamgbranco.comune.survey.Survey;
import williamgbranco.comune.user.User;

/**
 * Created by William Gomes de Branco on 18/08/2015.
 */
public class EntireSurveyLoader extends DataLoader<Survey>
{
    private static final String TAG = "EntireSurveyLoader";

    private Context mContext;
    private Integer mSurveyId;
    private Integer mInstId;

    public EntireSurveyLoader(Context context, Integer pInstId, Integer pSurveyId)
    {
        super(context);
        mContext = context;
        mSurveyId = pSurveyId;
        mInstId = pInstId;
    }

    @Override
    public Survey loadInBackground()
    {
        User currentUser = UserManager.get(mContext).getCurrentUser();
        int userId = currentUser.getUserId();

        Log.i(TAG, "loadInBackground( userId: "+userId+", mInstId: "+mInstId+", mSurveyId: "+mSurveyId+")");

        return SurveyManager.get(getContext()).queryEntireSurveyForInstitution(userId, mInstId, mSurveyId, true);
    }
}
