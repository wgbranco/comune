package williamgbranco.comune.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import williamgbranco.comune.R;
import williamgbranco.comune.callback.PageIndicatorCallback;
import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.manager.PublicInstitutionManager;
import williamgbranco.comune.manager.SurveyManager;
import williamgbranco.comune.survey.PageIndicator;
import williamgbranco.comune.survey.Survey;
import williamgbranco.comune.survey.question.Question;


public class SurveyPageIndicatorFragment extends Fragment
{
    private static final String TAG = "SurveyPgIndicatorFrag";

    public static final String EXTRA_INST_ID = "SurveyPageContentFragment.inst_id";
    public static final String EXTRA_SURVEY_ID = "SurveyPageContentFragment.survey_id";


    private Integer mExtraInstId;
    private Integer mExtraSurveyId;
    private PublicInstitution mCurrentInstitution;
    private SurveyManager mSurveyManager;
    private Survey mCurrentSurvey;
    private Integer mNumberPages;
    private HashMap<Integer, PageIndicator> mIndicatorHashMap;
    private PageIndicatorCallback mCallback;
    private PageIndicator mCurrentPageIndicator;

    private LinearLayout mHeaderContainer; //header_container
    private ImageView mImageViewInstTypeIcon; //imageview_institution_type_icon
    private TextView mTextViewInstitutionName; //textview_institution_name
    private TextView mTextViewInstitutionCompleteName; //textview_institution_complete_name
    private LinearLayout mContainerInstitutionCompleteName; //container_institution_complete_name
    private ProgressBar mProgressBar; //progress_bar_header

    private LinearLayout mIndicatorContainer; //indicator_container
    private HorizontalScrollView mScrollViewPageIndicators; //page_indicators_scrollview
    private ImageButton mPageIndicatorIntro; //indicator_intro_page
    private PageIndicator mPageIndicator;
    private ImageButton mPageIndicatorConclusion; //indicator_conclusion_page
    private LinearLayout mPageIndicatorsContainer; //page_indicators_container


    public static SurveyPageIndicatorFragment newInstance(Integer pInstId,  Integer pSurveyId)
    {
        Bundle args = new Bundle();
        args.putInt(EXTRA_INST_ID, pInstId);
        args.putInt(EXTRA_SURVEY_ID, pSurveyId);

        SurveyPageIndicatorFragment fragment = new SurveyPageIndicatorFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public SurveyPageIndicatorFragment()
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
            } catch (Exception e){Log.e(TAG, "getImportantObjects: "+e.toString());}
        }

        mCurrentInstitution = PublicInstitutionManager.get(getActivity()).getCurrentInstitution();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_page_indicator, container, false);

        mHeaderContainer = (LinearLayout) v.findViewById(R.id.header_container);

        mImageViewInstTypeIcon = (ImageView) v.findViewById(R.id.imageview_institution_type_icon);
        mImageViewInstTypeIcon.setImageResource(mCurrentInstitution.getPlaceDarkGrayIconId());

        mTextViewInstitutionName = (TextView) v.findViewById(R.id.textview_institution_name);
        mTextViewInstitutionCompleteName = (TextView) v.findViewById(R.id.textview_institution_complete_name);
        mContainerInstitutionCompleteName = (LinearLayout) v.findViewById(R.id.container_institution_complete_name);
        mContainerInstitutionCompleteName.setVisibility(View.GONE);

        String abbrev = mCurrentInstitution.getNomeAbreviado();
        String nome = mCurrentInstitution.getNome();
        if ((abbrev != null) && (!abbrev.toLowerCase().equals("null")) && (abbrev.length() > 0)) {
            mTextViewInstitutionName.setText(abbrev);
        } else {
            mTextViewInstitutionName.setText(nome);
        }
        mTextViewInstitutionCompleteName.setText(nome);

        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar_header);

        mIndicatorContainer = (LinearLayout) v.findViewById(R.id.indicator_container);

        mScrollViewPageIndicators = (HorizontalScrollView) v.findViewById(R.id.page_indicators_scrollview);

        //mPageIndicatorsContainer = (LinearLayout) v.findViewById(R.id.page_indicators_container);

        mPageIndicatorIntro = (ImageButton) v.findViewById(R.id.indicator_intro_page);
        mPageIndicatorIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.moveToIntroPage();
            }
        });

        mPageIndicatorConclusion = (ImageButton) v.findViewById(R.id.indicator_conclusion_page);
        mPageIndicatorConclusion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.moveToConclusionPage();
            }
        });

        updateUI();

        return v;
    }

    protected void focusOn(int index)
    {
        Log.i(TAG, "focus on: " + index);

        mIndicatorContainer.setVisibility(View.VISIBLE);
        mHeaderContainer.setVisibility(View.GONE);

        //unfocus(mPageIndicatorIntro);
        //unfocus(mPageIndicatorConclusion);
        if (mCurrentPageIndicator != null)
            mCurrentPageIndicator.unfocus();

        if (index == 0)
        {
            focus(mPageIndicatorIntro);
        } else if (index == mNumberPages-1)
        {
            focus(mPageIndicatorConclusion);
        } else if (mIndicatorHashMap != null)
        {
            if (mCurrentPageIndicator != null)
            {
                int vLeft = mCurrentPageIndicator.getLeft();
                int vRight = mCurrentPageIndicator.getRight();
                int sWidth = mScrollViewPageIndicators.getWidth();
                int distance = 0;

                if (index <= (mNumberPages-2)/2) {
                    distance = ((vLeft + vRight) - sWidth) / 2;
                } else {
                    distance = (sWidth - (vLeft + vRight)) / 2;
                }
                mScrollViewPageIndicators.smoothScrollTo(distance, 0);
            }

            mCurrentPageIndicator = mIndicatorHashMap.get(index - 1);
            mCurrentPageIndicator.focus();
        }
    }

    public void focus(ImageButton imgButton)
    {
        Log.i(TAG, "focus: " + imgButton.getId());

        mHeaderContainer.setVisibility(View.VISIBLE);
        mIndicatorContainer.setVisibility(View.GONE);

        try {
            Class res = R.drawable.class;
            Field field = res.getField((String)imgButton.getTag()+"_focus");
            int drawableId = field.getInt(null);

            Drawable icon = ContextCompat.getDrawable(getActivity(), drawableId);
            imgButton.setImageDrawable(icon);
        }
        catch (Exception e) {
            Log.e(TAG, "Failure to get drawable id.", e);
        }
    }

    public void unfocus(ImageButton imgButton)
    {
        Log.i(TAG, "unfocus: " + imgButton.getId());

        try {
            Class res = R.drawable.class;
            Field field = res.getField((String)imgButton.getTag());
            int drawableId = field.getInt(null);

            Drawable icon = ContextCompat.getDrawable(getActivity(), drawableId);
            imgButton.setImageDrawable(icon);
        }
        catch (Exception e) {
            Log.e(TAG, "Failure to get drawable id.", e);
        }
    }

    protected void updateUI()
    {
        Log.i(TAG, "updateUI");

        mIndicatorContainer.setVisibility(View.GONE);

        if (mCurrentSurvey != null)
        {
            int index = 0;
            mIndicatorHashMap = new HashMap<>();
            ArrayList<Question> questions = mCurrentSurvey.getQuestions();

            LinearLayout linearLayout;
            LinearLayout.LayoutParams lp;
            FrameLayout.LayoutParams lp2;

            lp2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //lp2.gravity = Gravity.CENTER;

            mPageIndicatorsContainer = new LinearLayout(getActivity());
            mPageIndicatorsContainer.setOrientation(LinearLayout.HORIZONTAL);
            mPageIndicatorsContainer.setGravity(Gravity.CENTER);
            mPageIndicatorsContainer.setLayoutParams(lp2);

            /*Space space = new Space(getActivity());
            space.setMinimumWidth(100);
            mPageIndicatorsContainer.addView(space, 0);*/

            //for (int q=0; q<4; q++) {
                for (Question question : questions)
                {
                    linearLayout = new LinearLayout(getActivity());
                    //linearLayout.setPadding(padding, 0, padding, 0);
                    lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(5, 0, 5, 0);
                    //lp.weight = 1;
                    //lp.gravity = Gravity.CENTER;
                    linearLayout.setLayoutParams(lp);
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                    mPageIndicator = new PageIndicator(getActivity(), question, index);
                    mPageIndicator.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCallback.moveToPage(((PageIndicator) v).getIndex());
                        }
                    });
                    linearLayout.addView(mPageIndicator);
                    //linearLayout.
                    //mPageIndicatorsContainer.setLayoutParams(lp);
                    //mPageIndicatorsContainer.setOrientation(LinearLayout.HORIZONTAL);
                    mPageIndicatorsContainer.addView(linearLayout, index);

                    mIndicatorHashMap.put(index, mPageIndicator);

                    index++;
                }
            //}
            /*space = new Space(getActivity());
            space.setMinimumWidth(100);
            mPageIndicatorsContainer.addView(space);*/

            mScrollViewPageIndicators.removeAllViews();
            mScrollViewPageIndicators.addView(mPageIndicatorsContainer);

            Log.i(TAG, "updateUI: " + questions.size() + " bullets added to page indicator");
            Log.i(TAG, "updateUI: mPageIndicatorsContainer.getChildCount(): " + mPageIndicatorsContainer.getChildCount());

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
    public void onDestroy()
    {
        Log.i(TAG, "onDestroy()");

        super.onDestroy();
    }
}
