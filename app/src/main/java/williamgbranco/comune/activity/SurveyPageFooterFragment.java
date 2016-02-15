package williamgbranco.comune.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import williamgbranco.comune.R;
import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.manager.PublicInstitutionManager;
import williamgbranco.comune.manager.SurveyManager;
import williamgbranco.comune.survey.Survey;


public class SurveyPageFooterFragment extends Fragment
{
    private static final String TAG = "SurveyPageFooterFrag";

    public static final String EXTRA_SURVEY_ID = "SurveyPageFooterFragment.survey_id";
    public static final String EXTRA_SURVEY_SOURCE = "SurveyPageFooterFragment.survey_source";
    public static final String EXTRA_INST_ID = "SurveyPageFooterFragment.institution_id";

    private Integer mExtraInstId;
    private Integer mExtraSurveyId;
    private PublicInstitution mCurrentInstitution;
    private SurveyManager mSurveyManager;
    private Survey mCurrentSurvey;
    private Integer mCurrentPageNumber;
    private Integer mNumberPages;

    private View mFooterSeparatorBar; //footer_separator_bar
    private TextView mTextViewCurrentPageNumber; //textview_current_page_number
    private TextView mTextViewTotalPageNumber; //textview_total_page_number


    public static SurveyPageFooterFragment newInstance(Integer pInstId, Integer pSurveyId)
    {
        Bundle args = new Bundle();
        args.putInt(EXTRA_INST_ID, pInstId);
        args.putInt(EXTRA_SURVEY_ID, pSurveyId);
        //args.putString(EXTRA_SURVEY_SOURCE, pSurveySource);

        SurveyPageFooterFragment fragment = new SurveyPageFooterFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public SurveyPageFooterFragment()
    {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSurveyManager = SurveyManager.get(getActivity());

        getImportantObjects();
    }

    protected void getImportantObjects()
    {
        Bundle args = getArguments();
        if (args != null)
        {
            mExtraInstId = args.getInt(EXTRA_INST_ID, -1);
            mExtraSurveyId = args.getInt(EXTRA_SURVEY_ID, -1);

            mCurrentSurvey = mSurveyManager.getCurrentSurveyById(mExtraInstId, mExtraSurveyId);

            try {
                mCurrentSurvey = mSurveyManager.getCurrentSurveyById(mExtraInstId, mExtraSurveyId);
                mNumberPages = 1 + mCurrentSurvey.getQuestions().size() + 1;
            } catch (Exception e){
                Log.e(TAG, e.toString());
            }
        }

        mCurrentInstitution = PublicInstitutionManager.get(getActivity()).getCurrentInstitution();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.i(TAG, "onCreateView");

        View v = inflater.inflate(R.layout.fragment_survey_page_footer, container, false);

        mFooterSeparatorBar = v.findViewById(R.id.footer_separator_bar);
        mTextViewCurrentPageNumber = (TextView) v.findViewById(R.id.textview_current_page_number);
        mTextViewTotalPageNumber = (TextView) v.findViewById(R.id.textview_total_page_number);

        try {
            setCurrentQuestionNumber(0);
        } catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        return v;
    }

    protected void updateUI()
    {
        Log.i(TAG, "updateUI");

        String questionPositionMsg = null;

        if (mTextViewTotalPageNumber != null) mTextViewTotalPageNumber.setVisibility(View.GONE);
        if (mTextViewCurrentPageNumber != null) mTextViewCurrentPageNumber.setVisibility(View.GONE);
        if (mFooterSeparatorBar != null) mFooterSeparatorBar.setVisibility(View.GONE);

        Log.i(TAG, "mCurrentPageNumber: "+mCurrentPageNumber);

        if (mCurrentPageNumber != null)
        {
            if (mCurrentPageNumber == 0)
            {
                mFooterSeparatorBar.setVisibility(View.VISIBLE);

                questionPositionMsg = getResources().getString(R.string.msg_number_of_question_1) + " " +
                        mCurrentSurvey.getQuestions().size() + " " +
                        getResources().getString(R.string.msg_number_of_question_2);

                //if (mTextViewCurrentPageNumber != null) mTextViewCurrentPageNumber.setTypeface(null, Typeface.NORMAL);

                if (mTextViewTotalPageNumber != null) {
                    mTextViewTotalPageNumber.setText(questionPositionMsg);
                    mTextViewTotalPageNumber.setVisibility(View.VISIBLE);
                }
            }
            else if (mCurrentPageNumber == (mNumberPages - 1))
            {
                //TODO:
            }
            else
            {
                questionPositionMsg = mCurrentPageNumber.toString();

                // if (mTextViewCurrentPageNumber != null) mTextViewCurrentPageNumber.setTypeface(null, Typeface.BOLD);

                if (mTextViewCurrentPageNumber != null)
                {
                    mTextViewCurrentPageNumber.setText(questionPositionMsg);
                    mTextViewCurrentPageNumber.setVisibility(View.VISIBLE);

                    if (mTextViewTotalPageNumber != null) {
                        mTextViewTotalPageNumber.setText("/" + mCurrentSurvey.getQuestions().size());
                        mTextViewTotalPageNumber.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    protected void setCurrentQuestionNumber(int index)
    {
        Log.i(TAG, "setCurrentQuestionNumber");

        mCurrentPageNumber = index;

        updateUI();
    }
}
