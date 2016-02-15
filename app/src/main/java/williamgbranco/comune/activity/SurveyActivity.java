package williamgbranco.comune.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.Toast;

import williamgbranco.comune.R;
import williamgbranco.comune.callback.GetSurveyCallback;
import williamgbranco.comune.callback.PageIndicatorCallback;
import williamgbranco.comune.database.loader.EntireSurveyLoader;
import williamgbranco.comune.manager.SurveyManager;
import williamgbranco.comune.manager.UserManager;
import williamgbranco.comune.service.ExpiringSoonSurveyService;
import williamgbranco.comune.survey.Survey;
import williamgbranco.comune.util.Constants;


public class SurveyActivity extends HeaderContentFooterActivity implements PageIndicatorCallback
{
    private static final String TAG = "SurveyActivity";

    private static final int LOAD_ENTIRE_SURVEY = 0;

    public static final String EXTRA_SURVEY_ID = "SurveyActivity.survey_id";
    public static final String EXTRA_SURVEY_SOURCE = "SurveyActivity.survey_source";
    public static final String EXTRA_INST_ID = "SurveyActivity.institution_id";

    private Bundle mArgs;
    private Integer mExtraSurveyId;
    private Integer mExtraInstId;

    private static Survey mCurrentSurvey;
    private SurveyManager mSurveyManager;

    private SurveyPageIndicatorFragment mPageIndicatorFragment;
    private SurveyPagerFragment mPagerFragment;
    private SurveyPageFooterFragment mPageFooterFragment;

    private BroadcastReceiver mOnNewSurveyInserted = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            unregisterReceiver(mOnNewSurveyInserted);

            Log.i(TAG, "Intent received: survey " + mExtraSurveyId + " inserted.");

            String msg = getResources().getString(R.string.msg_survey_download_complete) +
                    System.getProperty("line.separator") +
                    " '" + getResources().getString(R.string.title_activity_surveys) + "'";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

            int userId = UserManager.get(context).getCurrentUser().getUserId();

            Intent i = new Intent(context, ExpiringSoonSurveyService.class);
            i.putExtra(ExpiringSoonSurveyService.EXTRA_USER_ID, userId);
            getApplicationContext().startService(i);

            LoadSurveyFromDB(mArgs);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSurveyManager = SurveyManager.get(getApplicationContext());

        mArgs = getIntent().getExtras();
        if (mArgs != null)
        {
            mExtraInstId = mArgs.getInt(EXTRA_INST_ID, -1);
            mExtraSurveyId = mArgs.getInt(EXTRA_SURVEY_ID, -1);
            configFragments();
            Log.i(TAG, mExtraInstId + " " + mExtraSurveyId);
            getCurrentSurvey();
        }
    }

    @Override
    protected Fragment createHeaderFragment()
    {
        mPageIndicatorFragment = SurveyPageIndicatorFragment.newInstance(mExtraInstId, mExtraSurveyId);
        return mPageIndicatorFragment;
    }

    @Override
    protected Fragment createContentFragment()
    {
        mPagerFragment = SurveyPagerFragment.newInstance(mExtraInstId, mExtraSurveyId);
        return mPagerFragment;
    }

    @Override
    protected Fragment createFooterFragment()
    {
        mPageFooterFragment = SurveyPageFooterFragment.newInstance(mExtraInstId, mExtraSurveyId);
        return mPageFooterFragment;
    }

    @Override
    protected String getEmptyDataMessage() {
        return getErrorMessage();
    }

    @Override
    protected String getErrorMessage() {
        return getResources().getString(R.string.msg_error_loading_list);
    }

    private void getCurrentSurvey()
    {
        String source = getIntent().getStringExtra(EXTRA_SURVEY_SOURCE);

        if (source.equals(Survey.NEW_SURVEY_AVAILABLE))
        {
            Log.i(TAG, "Nova pesquisa, baixar do servidor.");
            fetchSurveyOnServer(mExtraSurveyId);
        }
        else if (source.equals(Survey.SURVEY_FROM_DB))
        {
            Survey currentSurvey = mSurveyManager.getCurrentSurveyById(mExtraInstId, mExtraSurveyId);

            if (currentSurvey != null)
            {
                Log.i(TAG, "Pesquisa já carregada no SurveyManager, 'currentSurvey'.");

                mCurrentSurvey = currentSurvey;
                configFragments();
            }
            else
            {
                Log.i(TAG, "Pesquisa já presente no BD, mas ainda não carregada no SurveyManager.");

                LoadSurveyFromDB(mArgs);
            }
        }
    }

    private void fetchSurveyOnServer(Integer pSurveyId)
    {
        IntentFilter filter = new IntentFilter(SurveyManager.ACTION_NEW_SURVEY_INSERTED);
        registerReceiver(mOnNewSurveyInserted, filter, Constants.PERM_PRIVATE, null);

        mSurveyManager.fetchEntireSurveyByIdOnServer(mExtraInstId, pSurveyId, new GetSurveyCallback() {
            @Override
            public void done(Survey returnedSurvey)
            {
                try {
                    if (returnedSurvey == null)
                    {
                        emptyDataSetLoaded();
                    }
                    else
                    {
                        finishLoadingData();
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
        });
    }

    private void LoadSurveyFromDB(Bundle args)
    {
        Log.i(TAG, "LoadSurveyFromDB(...) " + mExtraSurveyId + " from DB.");

        LoaderManager lm = getSupportLoaderManager();
        lm.initLoader(LOAD_ENTIRE_SURVEY, args, new EntireSurveyLoaderCallbacks());
    }

    @Override
    public void moveToIntroPage()
    {
        mPagerFragment.moveToPage(0);
    }

    @Override
    public void moveToConclusionPage()
    {
        mPagerFragment.moveToPage(1+mCurrentSurvey.getQuestions().size()+1);
    }

    @Override
    public void moveToPage(int index)
    {
        mPagerFragment.moveToPage(index+1);
    }

    @Override
    public void pageChanged(int index)
    {
        Log.i(TAG, "pageChanged(" + index + ")");

        mPageIndicatorFragment.focusOn(index);
        mPageFooterFragment.setCurrentQuestionNumber(index);
    }

    @Override
    protected void onDestroy()
    {
        Log.i(TAG, "onDestroy()");

        mCurrentSurvey = null;
        mPageIndicatorFragment = null;
        mPagerFragment = null;

        super.onDestroy();
    }

    private void updateFragmentsUI()
    {
        if (mPageIndicatorFragment != null)
        {
            mPageIndicatorFragment.getImportantObjects();
            mPageIndicatorFragment.updateUI();
        }

        if (mPagerFragment != null)
        {
            mPagerFragment.getImportantObjects();
            mPagerFragment.getSurveyPagesReady();
        }

        if (mPageFooterFragment != null)
        {
            Log.i(TAG, "mPageFooterFragment != null");
            mPageFooterFragment.getImportantObjects();
            mPageFooterFragment.setCurrentQuestionNumber(0);
        }
    }


    private class EntireSurveyLoaderCallbacks implements LoaderManager.LoaderCallbacks<Survey>
    {
        @Override
        public Loader<Survey> onCreateLoader(int id, Bundle args)
        {
            Log.i(TAG, "onCreateLoader ");
            return new EntireSurveyLoader(getApplicationContext(), mExtraInstId, mExtraSurveyId);
        }

        @Override
        public void onLoadFinished(Loader<Survey> loader, Survey pSurvey)
        {
            Log.i(TAG, "onLoadFinished(...), Survey "+ mExtraSurveyId +" queried.");

            mCurrentSurvey = pSurvey;

            updateFragmentsUI();

            LoaderManager lm = getSupportLoaderManager();
            lm.destroyLoader(LOAD_ENTIRE_SURVEY);
        }

        @Override
        public void onLoaderReset(Loader<Survey> loader)
        {
            Log.i(TAG, "EntireSurveyLoaderCallbacks, onLoaderReset");
        }
    }

}
