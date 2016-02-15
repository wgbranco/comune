package williamgbranco.comune.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

import williamgbranco.comune.R;
import williamgbranco.comune.callback.GetResultCallback;
import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.manager.PublicInstitutionManager;
import williamgbranco.comune.manager.SurveyManager;
import williamgbranco.comune.survey.Survey;
import williamgbranco.comune.survey.question.CheckboxQuestion;
import williamgbranco.comune.survey.question.Question;
import williamgbranco.comune.survey.question.RadioButtonQuestion;
import williamgbranco.comune.survey.question.RatingQuestion;
import williamgbranco.comune.survey.question.YesOrNoQuestion;
import williamgbranco.comune.survey.question.answer_option.AnswerOption;
import williamgbranco.comune.survey.question.answer_option.AnswerOptionWithStatus;
import williamgbranco.comune.util.Constants;


public class SurveyPageContentFragment extends Fragment
{
    private static final String TAG = "SurveyPageContentFrag";

    public static final String EXTRA_INST_ID = "SurveyPageContentFragment.inst_id";
    public static final String EXTRA_SURVEY_ID = "SurveyPageContentFragment.survey_id";
    public static final String EXTRA_QUESTION_ID = "SurveyPageContentFragment.question_id";
    //public static final String KEY_CURRENT_ITEM = "current_item";

    private static final int LOAD_QUESTION = 0;


    private boolean inFirstPage;
    private boolean inLastPage;
    private boolean loading;
    private Question mCurrentQuestion;

    /*private RelativeLayout mHeaderContainer; //headerContainer
    private RelativeLayout mHeader; //header
    private ImageView mImageViewInstitutionTypeIcon;
    private TextView mTextViewInstitutionName;
    private LinearLayout mIndicatorContainer; //indicator_container*/
    //private TextView mTextViewPageIndicator;

    private LinearLayout mQuestionBodyPageContainer; //page_question
    //private TextView mTextViewQuestionNumber; //textview_question_number
    private TextView mTextViewQuestionText; //textview_question_text
    private ScrollView mScrollViewQuestionAnswerOptions; //container_multiple_choice_question
    private LinearLayout mAnswerOptionsRadioGroupContainer;
    private RadioGroup mAnswerOptionsRadioGroup; //answer_options_radiogroup
    private LinearLayout mQuestionCheckBoxesContainer; //question_checkboxes_container

    private LinearLayout mContainerAnswerButtons; //container_answer_buttons
    private LinearLayout mYesOrNoAnswerContainer; //answer_yes_or_no_container
    private Button mYesButton; //yes_button
    private Button mNoButton; //no_button
    private LinearLayout mRatingAnswerContainer; //answer_rating_bar_container
    private RatingBar mQuestionRatingBar; //question_ratingbar

    private RelativeLayout mInstructionsPageContainer; //page_instructions
    private LinearLayout mFirstPageContainer; //first_page
    private TextView mTextViewIntroduction; //textview_instroduction
    //private TextView mTextViewTotalPages; //R.id.textview_total_pages
    private LinearLayout mLastPageContainer; //last_page
    private LinearLayout mContainerMsgThankYou; //container_msg_thankyou
    private TextView mTextViewMsgThankYou; //textview_msg_thankyou
    private TextView mTextViewInstructions; //textview_instructions
    private TextView mTextViewRemainingTime; //textview_remaining_time
    private Button mButtonCompleteSurvey; //button_complete_survey

    private LinearLayout mLoadingPageContainer; //page_loading
    private ProgressBar mSurveyProgressBar; //survey_progressbar

    private Integer mExtraInstId;
    private Integer mExtraSurveyId;
    private Integer mExtraQuestionId;
    private PublicInstitution mCurrentInstitution;
    private SurveyManager mSurveyManager;
    private Survey mCurrentSurvey;


    private BroadcastReceiver mOnQuestionAnswered = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            try
            {
                Log.i(TAG, "mOnQuestionAnswered");
                if (mExtraQuestionId.equals(Survey.LAST_PAGE))
                    updateUI();
            }
            catch (Exception e) {
                Log.i(TAG, "BroadcastReceiver mOnQuestionAnswered Exception");
            }
        }
    };


    public SurveyPageContentFragment()
    {}

    public static SurveyPageContentFragment newInstance(Integer pInstId, Integer pSurveyId, Integer pQuestionId)
    {
        Bundle args = new Bundle();
        args.putInt(EXTRA_INST_ID, pInstId);
        args.putInt(EXTRA_SURVEY_ID, pSurveyId);
        args.putInt(EXTRA_QUESTION_ID, pQuestionId);

        SurveyPageContentFragment fragment = new SurveyPageContentFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        //setRetainInstance(true);

        loading = false;
        inFirstPage = false;
        inLastPage = false;

        mCurrentInstitution = PublicInstitutionManager.get(getActivity()).getCurrentInstitution();

        Bundle args = getArguments();
        if (args != null)
        {
            mExtraInstId = args.getInt(EXTRA_INST_ID, -1);
            mExtraSurveyId = args.getInt(EXTRA_SURVEY_ID, -1);
            mExtraQuestionId = args.getInt(EXTRA_QUESTION_ID, -1);

            mSurveyManager = SurveyManager.get(getActivity());

            if (mExtraSurveyId != -1)
                mCurrentSurvey = mSurveyManager.getCurrentSurveyById(mExtraInstId, mExtraSurveyId);

            Log.i(TAG, "mExtraInstId: "+mExtraInstId);
            //Log.i(TAG, mCurrentInstitution.toString());

            if (mExtraQuestionId != -1)
            {
                if (mExtraQuestionId.equals(Survey.LOADING)) {
                    //Log.i(TAG, "Survey.LOADING");
                    loading = true;
                }
                else
                {
                    IntentFilter filter = new IntentFilter(Question.ACTION_QUESTION_ANSWERED);
                    getActivity().getApplicationContext().registerReceiver(mOnQuestionAnswered, filter, Constants.PERM_PRIVATE, null);

                    if (mExtraQuestionId.equals(Survey.FIRST_PAGE))
                    {
                        //Log.i(TAG, "Survey.FIRST_PAGE");

                        inFirstPage = true;
                    }
                    else if (mExtraQuestionId.equals(Survey.LAST_PAGE))
                    {
                        /*IntentFilter filter = new IntentFilter(Question.ACTION_QUESTION_ANSWERED);
                        getActivity().getApplicationContext().registerReceiver(mOnQuestionAnswered, filter, Constants.PERM_PRIVATE, null);*/

                        inLastPage = true;
                    }
                    else
                    {
                        Log.d(TAG, "número de questões: " + mCurrentSurvey.getQuestions().size());

                        mCurrentQuestion = mCurrentSurvey.getQuestion(mExtraQuestionId);
                    }
                }
            }
        }
    }

    /*@Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putInt(KEY_CURRENT_ITEM, );

        super.onSaveInstanceState(outState);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_survey_page, container, false);

        mQuestionBodyPageContainer = (LinearLayout) v.findViewById(R.id.page_question);
        //mTextViewQuestionNumber = (TextView) v.findViewById(R.id.textview_question_number);
        mTextViewQuestionText = (TextView) v.findViewById(R.id.textview_question_text);
        mScrollViewQuestionAnswerOptions = (ScrollView) v.findViewById(R.id.container_multiple_choice_question);
        mAnswerOptionsRadioGroupContainer = (LinearLayout) v.findViewById(R.id.container_radio_group);
        mAnswerOptionsRadioGroup = (RadioGroup) v.findViewById(R.id.radio_group_options);
        mQuestionCheckBoxesContainer = (LinearLayout) v.findViewById(R.id.container_checkboxes);

        mContainerAnswerButtons = (LinearLayout) v.findViewById(R.id.container_answer_buttons);
        mYesOrNoAnswerContainer = (LinearLayout) v.findViewById(R.id.container_yes_or_no_buttons);
        mYesButton = (Button) v.findViewById(R.id.yes_button);
        mNoButton = (Button) v.findViewById(R.id.no_button);
        mRatingAnswerContainer = (LinearLayout) v.findViewById(R.id.container_rating_bar);
        mQuestionRatingBar = (RatingBar) v.findViewById(R.id.rating_bar);
        LayerDrawable stars = (LayerDrawable) mQuestionRatingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.starFullySelected), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.starPartiallySelected), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.starNotSelected), PorterDuff.Mode.SRC_ATOP);

        mInstructionsPageContainer = (RelativeLayout) v.findViewById(R.id.page_instructions);
        mFirstPageContainer = (LinearLayout) v.findViewById(R.id.first_page);
        mTextViewIntroduction = (TextView) v.findViewById(R.id.textview_introduction);
        //mTextViewTotalPages = (TextView) v.findViewById(R.id.textview_total_pages);
        mLastPageContainer = (LinearLayout) v.findViewById(R.id.last_page);
        mContainerMsgThankYou = (LinearLayout) v.findViewById(R.id.container_msg_thankyou);
        mTextViewMsgThankYou = (TextView) v.findViewById(R.id.textview_msg_thankyou);
        mTextViewInstructions = (TextView) v.findViewById(R.id.textview_instructions);
        mTextViewRemainingTime = (TextView) v.findViewById(R.id.textview_remaining_time);
        mButtonCompleteSurvey = (Button) v.findViewById(R.id.button_complete_survey);
        mButtonCompleteSurvey.getBackground().setColorFilter(getResources().getColor(R.color.complete_survey_button_color), PorterDuff.Mode.SRC_ATOP);

        mLoadingPageContainer = (LinearLayout) v.findViewById(R.id.page_loading);
        mSurveyProgressBar = (ProgressBar) v.findViewById(R.id.survey_progressbar);
        if (mSurveyProgressBar != null) {
            mSurveyProgressBar.setIndeterminate(true);
            mSurveyProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.logo_orange), android.graphics.PorterDuff.Mode.SRC_ATOP);
        }

        updateUI();

        return v;
    }

    private void getYesOrNoQuestionPageReady()
    {
        if (((YesOrNoQuestion) mCurrentQuestion).getUserAnswer() != null)
        {
            if (((YesOrNoQuestion) mCurrentQuestion).getUserAnswer().equals(YesOrNoQuestion.ANSWER_YES)) {
                mNoButton.getBackground().clearColorFilter();
                mYesButton.getBackground().setColorFilter(getResources().getColor(R.color.starFullySelected), PorterDuff.Mode.SRC_ATOP);
            } else {
                mYesButton.getBackground().clearColorFilter();
                mNoButton.getBackground().setColorFilter(getResources().getColor(R.color.starFullySelected), PorterDuff.Mode.SRC_ATOP);
            }
        }
        else
        {
            mYesButton.getBackground().clearColorFilter();
            mNoButton.getBackground().clearColorFilter();
        }

        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((YesOrNoQuestion) mCurrentQuestion).setUserAnswer(YesOrNoQuestion.ANSWER_YES);
                mNoButton.getBackground().clearColorFilter();
                mYesButton.getBackground().setColorFilter(getResources().getColor(R.color.starFullySelected), PorterDuff.Mode.SRC_ATOP);

                mSurveyManager.updateUserAnswerForYesOrNoQuestion(mExtraInstId, mExtraSurveyId, mExtraQuestionId, ((YesOrNoQuestion) mCurrentQuestion).getUserAnswer());
            }
        });

        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((YesOrNoQuestion) mCurrentQuestion).setUserAnswer(YesOrNoQuestion.ANSWER_NO);
                mYesButton.getBackground().clearColorFilter();
                mNoButton.getBackground().setColorFilter(getResources().getColor(R.color.starFullySelected), PorterDuff.Mode.SRC_ATOP);

                mSurveyManager.updateUserAnswerForYesOrNoQuestion(mExtraInstId, mExtraSurveyId, mExtraQuestionId, ((YesOrNoQuestion) mCurrentQuestion).getUserAnswer());
            }
        });

        mYesOrNoAnswerContainer.setVisibility(View.VISIBLE);
    }

    private void getRatingQuestionPageReady()
    {
        if (((RatingQuestion) mCurrentQuestion).getUserRating() != null)
        {
            Float rating = ((RatingQuestion) mCurrentQuestion).getUserRating();
            mQuestionRatingBar.setRating(rating);
        }

        mQuestionRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ((RatingQuestion) mCurrentQuestion).setUserRating(rating);
                //Log.i(TAG, "Nota atribuida = " + Float.toString(rating));
                mSurveyManager.updateUserRatingForRatingQuestion(mExtraInstId, mExtraSurveyId, mExtraQuestionId, ((RatingQuestion) mCurrentQuestion).getUserRating());
            }
        });

        mRatingAnswerContainer.setVisibility(View.VISIBLE);
    }

    private void getCheckboxQuestionPageReady()
    {
        int size = ((CheckboxQuestion) mCurrentQuestion).getAnswerOptions().size();

        for (int i=0; i < size; i++)
        {
            AnswerOptionWithStatus answerOption = ((CheckboxQuestion) mCurrentQuestion).getAnswerOptions().get(i);

            CheckBox checkBox = new CheckBox(getActivity());
            checkBox.setText(answerOption.getAnswerText());
            checkBox.setTextSize(20);
            checkBox.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            checkBox.setId(new Integer(answerOption.getId()));
            checkBox.setButtonDrawable(R.drawable.survey_check_box);
            checkBox.setChecked(answerOption.isChecked());

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    AnswerOptionWithStatus aows = ((CheckboxQuestion) mCurrentQuestion).getAnswerOptionById(buttonView.getId());

                    if (aows != null)
                    {
                        if (isChecked) {
                            aows.setIsChecked(true);
                            Log.i(TAG, "Alternativa com id: " + Integer.toString(buttonView.getId()) + " foi selecionada.");
                        } else {
                            aows.setIsChecked(false);
                            Log.i(TAG, "Alternativa com id: " + Integer.toString(buttonView.getId()) + " foi deselecionada.");
                        }

                        mSurveyManager.updateCheckStatusForCheckBoxQuestion(mExtraInstId, mExtraSurveyId, mExtraQuestionId, aows.getId(), aows.isChecked());

                    } else {
                        Log.i(TAG, "Não foi encontrada a alternativa com id: " + Integer.toString(buttonView.getId()));
                    }
                }
            });

            Space space = new Space(getActivity());
            space.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 15));

            mQuestionCheckBoxesContainer.addView(checkBox);
            mQuestionCheckBoxesContainer.addView(space);
        }

        mQuestionCheckBoxesContainer.setVisibility(View.VISIBLE);
    }

    private void getRadioButtonQuestionPageReady()
    {
        mAnswerOptionsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ((RadioButtonQuestion) mCurrentQuestion).setIdUserAnswer(checkedId);
                Log.i(TAG, "Botao selecionado id = " + Integer.toString(checkedId));
                mSurveyManager.updateUserAnswerForRadioButtonQuestion(mExtraInstId, mExtraSurveyId, mExtraQuestionId, ((RadioButtonQuestion) mCurrentQuestion).getIdUserAnswer());
            }
        });

        int size = ((RadioButtonQuestion) mCurrentQuestion).getAnswerOptions().size();

        for (int i=0; i < size; i++)
        {
            AnswerOption answerOption = ((RadioButtonQuestion) mCurrentQuestion).getAnswerOptions().get(i);

            RadioButton rb = new RadioButton(getActivity());
            rb.setText(answerOption.getAnswerText());
            rb.setTextSize(20);
            rb.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            rb.setId(answerOption.getId());
            rb.setButtonDrawable(R.drawable.survey_radio_button);

            if ((((RadioButtonQuestion) mCurrentQuestion).getIdUserAnswer() != null) &&
                    answerOption.getId().equals(((RadioButtonQuestion) mCurrentQuestion).getIdUserAnswer()))
            {
                rb.setChecked(true);
            }
            //rb.setButtonTintList(getResources().getColorStateList(R.color.starFullySelected));

            Space space = new Space(getActivity());
            space.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 15));

            mAnswerOptionsRadioGroup.addView(rb);
            mAnswerOptionsRadioGroup.addView(space);
        }

        mAnswerOptionsRadioGroupContainer.setVisibility(View.VISIBLE);
    }

    private void getQuestionPageReady()
    {
        String questionPositionMsg = getResources().getString(R.string.msg_question_position) + " " +
                                     mCurrentSurvey.getQuestionPosition(mCurrentQuestion) + "/" +
                                     mCurrentSurvey.getQuestions().size();

        //mTextViewQuestionNumber.setText(questionPositionMsg);
        mTextViewQuestionText.setText(mCurrentQuestion.getDescription());

        switch (mCurrentQuestion.getType())
        {
            case (Question.QUESTION_TYPE_YES_OR_NO):
                getYesOrNoQuestionPageReady();
                mContainerAnswerButtons.setVisibility(View.VISIBLE);
                break;
            case (Question.QUESTION_TYPE_RATING):
                getRatingQuestionPageReady();
                mContainerAnswerButtons.setVisibility(View.VISIBLE);
                break;
            case (Question.QUESTION_TYPE_CHECKBOX):
                getCheckboxQuestionPageReady();
                mScrollViewQuestionAnswerOptions.setVisibility(View.VISIBLE);
                break;
            case (Question.QUESTION_TYPE_RADIOBUTTON):
                getRadioButtonQuestionPageReady();
                mScrollViewQuestionAnswerOptions.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void getFirstPageReady()
    {
        /*String nQuestionsMsg = getResources().getString(R.string.msg_number_of_question_1) + " " +
                               mCurrentSurvey.getQuestions().size() + " " +
                               getResources().getString(R.string.msg_number_of_question_2);*/

        mTextViewIntroduction.setText(mCurrentSurvey.getDescription());
        //mTextViewTotalPages.setText(nQuestionsMsg);
    }

    private void getLastPageReady()
    {
        Log.i(TAG, "getLastPageReady()");

        String timeRemainingMsg;

        mButtonCompleteSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mCurrentSurvey.isFullyAnswered()) {
                        mSurveyManager.updateSurveyCompleteness(mExtraInstId, mExtraSurveyId, true, new GetResultCallback() {
                            @Override
                            public void done() {
                                Log.i(TAG, "fecha atividade.");
                                getActivity().finish();
                            }
                        });

                    } else {
                        Log.i(TAG, "ERRO: pesquisa não foi totalmente respondida.");
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
        });

        if (mCurrentSurvey.isFullyAnswered())
        {
            //mContainerMsgThankYou.setVisibility(View.VISIBLE);

            Log.i(TAG, "Survey is fully answered");
            String msg = getResources().getString(R.string.msg_survey_completed) + " " + getResources().getString(R.string.msg_survey_alter_answers);
            mTextViewInstructions.setText(msg);
            mButtonCompleteSurvey.setVisibility(View.VISIBLE);
        }
        else
        {
            //mContainerMsgThankYou.setVisibility(View.GONE);

            mTextViewInstructions.setText(R.string.msg_survey_not_completed);
        }

        timeRemainingMsg = getResources().getString(R.string.msg_survey_remaining_time_1) + " " +
                mCurrentSurvey.getRemainingTimeInHoursForSurveyCompletion() + " " +
                           getResources().getString(R.string.msg_survey_remaining_time_2) + ".";

        mTextViewRemainingTime.setText(timeRemainingMsg);
    }

    private void updateUI()
    {
        if (!loading)
        {
            mLoadingPageContainer.setVisibility(View.INVISIBLE);

            mQuestionBodyPageContainer.setVisibility(View.INVISIBLE);
            mScrollViewQuestionAnswerOptions.setVisibility(View.INVISIBLE);
            mAnswerOptionsRadioGroupContainer.setVisibility(View.INVISIBLE);
            mQuestionCheckBoxesContainer.setVisibility(View.INVISIBLE);
            mContainerAnswerButtons.setVisibility(View.INVISIBLE);
            mYesOrNoAnswerContainer.setVisibility(View.INVISIBLE);
            mRatingAnswerContainer.setVisibility(View.INVISIBLE);

            mInstructionsPageContainer.setVisibility(View.INVISIBLE);
            mFirstPageContainer.setVisibility(View.INVISIBLE);
            mLastPageContainer.setVisibility(View.INVISIBLE);
            mButtonCompleteSurvey.setVisibility(View.INVISIBLE);

            if (inFirstPage)
            {
                getFirstPageReady();
                mInstructionsPageContainer.setVisibility(View.VISIBLE);
                mFirstPageContainer.setVisibility(View.VISIBLE);
            }
            else if (inLastPage)
            {
                getLastPageReady();
                mInstructionsPageContainer.setVisibility(View.VISIBLE);
                mLastPageContainer.setVisibility(View.VISIBLE);
            }
            else
            {
                getQuestionPageReady();
                mQuestionBodyPageContainer.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            mLoadingPageContainer.setVisibility(View.VISIBLE);
        }
    }

}
