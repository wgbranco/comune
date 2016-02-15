package williamgbranco.comune.survey.question.answer_option;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import williamgbranco.comune.util.Constants;

/**
 * Created by William on 29/07/2015.
 */
public class AnswerOptionWithStatus extends AnswerOption
{
    protected static final String TAG = "comune.AnswerOption";
    public static final String ACTION_OPTION_CHECKED = "comune.AnswerOption.Checked";
    protected static final String ACTION_EXTRA_INST_ID = "comune.AnswerOption.Checked.inst_id";
    protected static final String ACTION_EXTRA_SURVEY_ID = "comune.AnswerOption.Checked.survey_id";
    protected static final String ACTION_EXTRA_QUESTION_ID = "comune.AnswerOption.Checked.question_id";
    protected static final String ACTION_EXTRA_OPTION_ID = "comune.AnswerOption.Checked.option_id";
    //protected static final String PERM_PRIVATE = "williamgbranco.comune.PRIVATE";

    private boolean isChecked;
    private boolean notifyOptionChecked;


    public AnswerOptionWithStatus(Context pAppContext)
    {
        super(pAppContext);
    }

    public AnswerOptionWithStatus(Context pAppContext, Integer pId, String pAnswerText)
    {
        super(pAppContext, pId, pAnswerText);
    }

    public boolean isChecked()
    {
        return isChecked;
    }

    public void setIsChecked(boolean pIsChecked)
    {
        isChecked = pIsChecked;

        if (notifyOptionChecked)
        {
            Log.i(TAG, "AnswerOptionWithStatus - Intent broadcast: option checked: " + getId() +
                    " for question: " + getQuestion().getId() +
                    " for survey: " + getQuestion().getSurvey().getId() +
                    " for institution: " + getQuestion().getSurvey().getPublicInstitutionId());
            Intent i = new Intent(ACTION_OPTION_CHECKED);
            i.putExtra(ACTION_EXTRA_INST_ID, getQuestion().getSurvey().getPublicInstitutionId());
            i.putExtra(ACTION_EXTRA_SURVEY_ID, getQuestion().getSurvey().getId());
            i.putExtra(ACTION_EXTRA_QUESTION_ID, getQuestion().getId());
            i.putExtra(ACTION_EXTRA_OPTION_ID, getId());
            mAppContext.sendBroadcast(i, Constants.PERM_PRIVATE);
        }
    }

    public void notifyOptionChecked(boolean value)
    {
        notifyOptionChecked = value;
    }
}
