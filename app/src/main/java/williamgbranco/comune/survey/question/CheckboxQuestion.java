package williamgbranco.comune.survey.question;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;

import williamgbranco.comune.survey.question.answer_option.AnswerOptionWithStatus;
import williamgbranco.comune.util.Constants;

/**
 * Created by William on 28/07/2015.
 */
public class CheckboxQuestion extends Question
{
    private  ArrayList<AnswerOptionWithStatus> mAnswerOptions;


    public CheckboxQuestion(Context pAppContext)
    {
        super(pAppContext);
    }

    @Override
    public Integer getType()
    {
        return QUESTION_TYPE_CHECKBOX;
    }

    public void setAnswerOptions(ArrayList<AnswerOptionWithStatus> pAnswerOptions)
    {
        for (AnswerOptionWithStatus aows : pAnswerOptions)
        {
            aows.setQuestion(this);
        }

        mAnswerOptions = pAnswerOptions;
    }

    public ArrayList<AnswerOptionWithStatus> getAnswerOptions()
    {
        return mAnswerOptions;
    }

    public void addAnswerOption(AnswerOptionWithStatus pAnswerOption)
    {
        if (mAnswerOptions == null)
        {
            mAnswerOptions = new ArrayList<AnswerOptionWithStatus>();
        }

        pAnswerOption.setQuestion(this);

        mAnswerOptions.add(pAnswerOption);
    }

    public AnswerOptionWithStatus getAnswerOptionById(Integer pId)
    {
        if (mAnswerOptions != null)
        {
            int size = mAnswerOptions.size();

            for (int i=0; i<size; i++)
            {
                AnswerOptionWithStatus aows = mAnswerOptions.get(i);

                if (aows.getId().equals(pId))
                {
                    return aows;
                }
            }
        }
        return null;
    }


    private BroadcastReceiver mOnOptionChecked = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "CheckboxQuestion - Intent broadcast: question answered: " + getId() +
                    " for survey: " + getSurvey().getId() +
                    " for institution: " + getSurvey().getPublicInstitutionId());
            Intent i = new Intent(ACTION_QUESTION_ANSWERED);
            i.putExtra(ACTION_EXTRA_INST_ID, getSurvey().getPublicInstitutionId());
            i.putExtra(ACTION_EXTRA_SURVEY_ID, getSurvey().getId());
            i.putExtra(ACTION_EXTRA_QUESTION_ID, getId());
            mAppContext.sendBroadcast(i, Constants.PERM_PRIVATE);
        }
    };

    @Override
    public void notifyQuestionAnswered(boolean value)
    {
        super.notifyQuestionAnswered(value);

        if (notifyQuestionAnswered)
        {
            for (AnswerOptionWithStatus aows : mAnswerOptions)
            {
                aows.notifyOptionChecked(true);
            }

            IntentFilter filter = new IntentFilter(AnswerOptionWithStatus.ACTION_OPTION_CHECKED);
            mAppContext.registerReceiver(mOnOptionChecked, filter, Constants.PERM_PRIVATE, null);
        }
        else
        {
            mAppContext.unregisterReceiver(mOnOptionChecked);

            for (AnswerOptionWithStatus aows : mAnswerOptions)
            {
                aows.notifyOptionChecked(false);
            }
        }
    }

    @Override
    public boolean isAnswered()
    {
        for (AnswerOptionWithStatus answer : mAnswerOptions)
        {
            if (answer.isChecked()) {
                return true;
            }
        }

        return false;
    }
}
