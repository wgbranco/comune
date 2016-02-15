package williamgbranco.comune.activity;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import williamgbranco.comune.R;
import williamgbranco.comune.callback.PageIndicatorCallback;
import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.manager.PublicInstitutionManager;
import williamgbranco.comune.manager.SurveyManager;
import williamgbranco.comune.survey.Survey;
import williamgbranco.comune.survey.question.Question;


public class SurveyPagerFragment extends Fragment
{
    private static final String TAG = "SurveyPagerFragment";

    private static final int LOAD_ENTIRE_SURVEY = 0;

    public static final String EXTRA_SURVEY_ID = "SurveyPagerFragment.survey_id";
    public static final String EXTRA_SURVEY_SOURCE = "SurveyPagerFragment.survey_source";
    public static final String EXTRA_INST_ID = "SurveyPagerFragment.institution_id";

    private static ViewPager mViewPager;
    private static Integer mCurrentItem;
    private Integer mExtraSurveyId;
    private Integer mExtraInstId;
    private Bundle mArgs;

    private static Survey mCurrentSurvey;
    private static PublicInstitution mCurrentInstitution;
    private SurveyManager mSurveyManager;
    private PageIndicatorCallback mCallback;


    public static SurveyPagerFragment newInstance(Integer pInstId, Integer pSurveyId)
    {
        Bundle args = new Bundle();
        args.putInt(EXTRA_INST_ID, pInstId);
        args.putInt(EXTRA_SURVEY_ID, pSurveyId);
        //args.putString(EXTRA_SURVEY_SOURCE, pSurveySource);

        SurveyPagerFragment fragment = new SurveyPagerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public SurveyPagerFragment()
    {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");

        mSurveyManager = SurveyManager.get(getActivity());

        getImportantObjects();
    }

    protected void getImportantObjects()
    {
        mArgs = getArguments();
        if (mArgs != null)
        {
            mExtraSurveyId = mArgs.getInt(EXTRA_SURVEY_ID, -1);
            mExtraInstId = mArgs.getInt(EXTRA_INST_ID, -1);

            mCurrentSurvey = mSurveyManager.getCurrentSurveyById(mExtraInstId, mExtraSurveyId);
        }

        mCurrentInstitution = PublicInstitutionManager.get(getActivity()).getCurrentInstitution();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_view_pager, container, false);

        mViewPager = (ViewPager) v.findViewById(R.id.view_pager);

        getSurveyPagesReady();

        return v;
    }

    protected void getSurveyPagesReady()
    {
        Log.i(TAG, "getSurveyPagesReady()");

        FragmentManager fm = getFragmentManager();

        if (mCurrentSurvey != null)
        {
            Log.i(TAG, mCurrentSurvey.toString());
            mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
                @Override
                public Fragment getItem(int position) {
                    Integer id = null;

                    //Se for a primeira página
                    if (position == 0) {
                        id = Survey.FIRST_PAGE;
                    }
                    //Se for a última página
                    else if (position == getCount() - 1) {
                        Log.i(TAG, "id = Survey.LAST_PAGE");

                        id = Survey.LAST_PAGE;
                    }
                    //Se for qualquer outra página no meio
                    else {
                        position = position - 1;
                        Question question = mCurrentSurvey.getQuestions().get(position);
                        question.notifyQuestionAnswered(true);
                        id = question.getId();

                        //Se for penúltima página aka ultima questão
                        //if ((position + 1) == (getCount() - 2)) {
                            //question.notifyQuestionAnswered(true);
                        //}
                    }

                    return SurveyPageContentFragment.newInstance(mExtraInstId, mExtraSurveyId, id);
                }

                @Override
                public int getCount() {
                    return 1 + mCurrentSurvey.getQuestions().size() + 1;
                }
            });

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                @Override
                public void onPageSelected(int position)
                {
                    mCallback.pageChanged(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {}
            });
        } else {
            Log.i(TAG, "mCurrentSurvey NULL");

            mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
                @Override
                public Fragment getItem(int position) {
                    return SurveyPageContentFragment.newInstance(mExtraInstId, mExtraSurveyId, Survey.LOADING);
                }

                @Override
                public int getCount() {
                    return 1;
                }
            });
        }
    }

    protected void moveToPage(int index)
    {
        if ((mCurrentSurvey != null) && (mViewPager != null))
        {
            mViewPager.setCurrentItem(index, true);
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mCallback = (PageIndicatorCallback) activity;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Log.i(TAG, "onSaveInstanceState(...)");

        if (mViewPager != null)
        {
            mCurrentItem = mViewPager.getCurrentItem();
            Log.i(TAG, "current item: " + mCurrentItem);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy()
    {
        Log.i(TAG, "onDestroy()");

        //mCurrentInstitution = null;
        mCurrentSurvey = null;
        mCurrentItem = null;
        mViewPager = null;

        super.onDestroy();
    }
}
