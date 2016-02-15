package williamgbranco.comune.survey.question;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import williamgbranco.comune.util.Constants;

/**
 * Created by William on 28/07/2015.
 */
public class YesOrNoQuestion extends Question
{
    public static final String ANSWER_NO = "false";
    public static final String ANSWER_YES = "true";

    private String mUserAnswer;


    public YesOrNoQuestion(Context pAppContext)
    {
        super(pAppContext);
    }

    @Override
    public Integer getType()
    {
        return QUESTION_TYPE_YES_OR_NO;
    }

    public void setUserAnswer(String pOption)
    {
        mUserAnswer = pOption;

        if (notifyQuestionAnswered)
        {
            Log.i(TAG, "YesOrNoQuestion - Intent broadcast: question answered: " + getId() +
                    " for survey: " + getSurvey().getId() +
                    " for institution: " + getSurvey().getPublicInstitutionId());
            Intent i = new Intent(ACTION_QUESTION_ANSWERED);
            i.putExtra(ACTION_EXTRA_INST_ID, getSurvey().getPublicInstitutionId());
            i.putExtra(ACTION_EXTRA_SURVEY_ID, getSurvey().getId());
            i.putExtra(ACTION_EXTRA_QUESTION_ID, getId());
            mAppContext.sendBroadcast(i, Constants.PERM_PRIVATE);
        }
    }

    public String getUserAnswer()
    {
        return mUserAnswer;
    }

    @Override
    public boolean isAnswered()
    {
        return (getUserAnswer() != null);
    }

}
