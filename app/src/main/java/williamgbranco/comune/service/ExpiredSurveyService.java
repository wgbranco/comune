package williamgbranco.comune.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import williamgbranco.comune.database.DatabaseHelper;
import williamgbranco.comune.manager.SharedPrefsManager;
import williamgbranco.comune.manager.SurveyManager;


public class ExpiredSurveyService extends IntentService
{
    private static final String TAG = "ExpiredSurveyService";

    private DatabaseHelper mHelper;
    private SurveyManager mSurveyManager;
    private SharedPrefsManager mSharedPrefsManager;


    public ExpiredSurveyService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        mSurveyManager = SurveyManager.get(getApplication());
        mSharedPrefsManager = SharedPrefsManager.get(getApplication());

        Log.i(TAG, "ExpiredSurveyService STARTED");
        //Toast.makeText(getApplication(), TAG, Toast.LENGTH_SHORT).show();

        if (intent != null)
        {
            if (mSurveyManager.hasExpiredSurveys())
            {
                mSharedPrefsManager.checkForExpiredSurvey(true);

                while (mSurveyManager.hasExpiredSurveys())
                {
                    Integer userId = null;
                    Integer instId = null;
                    Integer surveyId = null;
                    mHelper = new DatabaseHelper(getApplication());

                    DatabaseHelper.InstitutionSurveyCursor instSurveyCursor = mHelper.queryExpiredSurveys();

                    Log.i(TAG, "number of expired surveys: " + instSurveyCursor.getCount());

                    instSurveyCursor.moveToFirst();
                    if (!instSurveyCursor.isAfterLast())
                    {
                        userId = instSurveyCursor.getUserId();
                        instId = instSurveyCursor.getInstitutionId();
                        surveyId = instSurveyCursor.getSurveyId();

                        Log.i(TAG, "survey expired to be deleted : userid = " + userId + ", instId = " + instId + ", surveyId = " + surveyId);
                    }
                    instSurveyCursor.close();

                    mSurveyManager.deleteSurveyForUser(userId, instId, surveyId);
                }
            }

            mSharedPrefsManager.checkForExpiredSurvey(false);

            Log.i(TAG, "ExpiredSurveyService STOPPED");
        }
    }
}
