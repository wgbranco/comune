package williamgbranco.comune.survey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageButton;

import williamgbranco.comune.R;
import williamgbranco.comune.survey.question.Question;
import williamgbranco.comune.util.Constants;

/**
 * Created by William on 06/12/2015.
 */
public class PageIndicator extends ImageButton
{
    private static final String TAG = "PageIndicator";
    private boolean isOnFocus;

    private BroadcastReceiver mOnQuestionAnswered = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            try
            {
                if (mQuestion != null) {
                    int instId = intent.getIntExtra(Question.ACTION_EXTRA_INST_ID, -1);
                    int surveyId = intent.getIntExtra(Question.ACTION_EXTRA_SURVEY_ID, -1);
                    int questionId = intent.getIntExtra(Question.ACTION_EXTRA_QUESTION_ID, -1);

                    if ((instId == mQuestion.getSurvey().getPublicInstitutionId())
                            && (surveyId == mQuestion.getSurvey().getId())
                            && (questionId == mQuestion.getId())) {
                        updateUI();
                    }
                }
            }
            catch (Exception e) {
                Log.i(TAG, "BroadcastReceiver mOnQuestionAnswered Exception");
            }
        }
    };


    private Context mAppContext;
    private Question mQuestion;
    private int mIndex;

    public PageIndicator(Context context, Question pQuestion, int index)
    {
        super(context);
        mAppContext = context.getApplicationContext();
        mQuestion = pQuestion;
        mIndex = index;

        IntentFilter filter = new IntentFilter(Question.ACTION_QUESTION_ANSWERED);
        mAppContext.registerReceiver(mOnQuestionAnswered, filter, Constants.PERM_PRIVATE, null);

        updateUI();
    }

    public int getIndex()
    {
        return mIndex;
    }

    public void focus()
    {
        isOnFocus = true;

        /*int width = (int)getResources().getDimension(R.dimen.page_indicator_height_focus);
        int heigth = (int)getResources().getDimension(R.dimen.page_indicator_width_focus);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, heigth);
        this.setLayoutParams(layoutParams);*/

        updateUI();
    }

    public void unfocus()
    {
        isOnFocus = false;

        /*int width = (int)getResources().getDimension(R.dimen.page_indicator_height);
        int heigth = (int)getResources().getDimension(R.dimen.page_indicator_width);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, heigth);
        this.setLayoutParams(layoutParams);*/

        updateUI();
    }

    private void updateUI()
    {
        Drawable icon;

        this.setBackgroundColor(Color.TRANSPARENT);

        if (mQuestion != null)
        {
            if (mQuestion.isMandatory())
            {
                if (mQuestion.isAnswered())
                {
                    if (isOnFocus) {
                        icon = ContextCompat.getDrawable(mAppContext, R.drawable.ic_answered_mandatory_question_indicator_focus);
                    }
                    else {
                        icon = ContextCompat.getDrawable(mAppContext, R.drawable.ic_answered_mandatory_question_indicator);
                    }
                    this.setImageDrawable(icon);
                }
                else
                {
                    if (isOnFocus) {
                        icon = ContextCompat.getDrawable(mAppContext, R.drawable.ic_mandatory_question_indicator_focus);
                    }
                    else {
                        icon = ContextCompat.getDrawable(mAppContext, R.drawable.ic_mandatory_question_indicator);
                    }
                    this.setImageDrawable(icon);
                }
            }
            else
            {
                if (mQuestion.isAnswered())
                {
                    if (isOnFocus) {
                        icon = ContextCompat.getDrawable(mAppContext, R.drawable.ic_answered_question_indicator_focus);
                    }
                    else {
                        icon = ContextCompat.getDrawable(mAppContext, R.drawable.ic_answered_question_indicator);
                    }
                    this.setImageDrawable(icon);
                }
                else
                {
                    if (isOnFocus) {
                        icon = ContextCompat.getDrawable(mAppContext, R.drawable.ic_question_indicator_focus);
                    }
                    else {
                        icon = ContextCompat.getDrawable(mAppContext, R.drawable.ic_question_indicator);
                    }
                    this.setImageDrawable(icon);
                }
            }
        }
    }

}
