package williamgbranco.comune.survey.question;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import williamgbranco.comune.survey.question.answer_option.AnswerOption;
import williamgbranco.comune.util.Constants;

/**
 * Created by William on 28/07/2015.
 */
public class RadioButtonQuestion extends Question
{
    private  ArrayList<AnswerOption> mAnswerOptions;
    private Integer mIdUserAnswer;


    public RadioButtonQuestion(Context pAppContext)
    {
        super(pAppContext);
    }

    @Override
    public Integer getType()
    {
        return QUESTION_TYPE_RADIOBUTTON;
    }

    public void setAnswerOptions(ArrayList<AnswerOption> pAnswerOptions)
    {
        for (AnswerOption ao : pAnswerOptions)
        {
            ao.setQuestion(this);
        }

        mAnswerOptions = pAnswerOptions;
    }

    public ArrayList<AnswerOption> getAnswerOptions()
    {
        return mAnswerOptions;
    }

    public void addAnswerOption(AnswerOption pAnswerOption)
    {
        if (mAnswerOptions == null)
        {
            mAnswerOptions = new ArrayList<AnswerOption>();
        }

        pAnswerOption.setQuestion(this);

        mAnswerOptions.add(pAnswerOption);
    }

    public void setIdUserAnswer(Integer pId)
    {
        mIdUserAnswer = pId;

        if (notifyQuestionAnswered)
        {
            Log.i(TAG, "RadioButtonQuestion - Intent broadcast: question answered: " + getId() +
                    " for survey: " + getSurvey().getId() +
                    " for institution: " + getSurvey().getPublicInstitutionId());
            Intent i = new Intent(ACTION_QUESTION_ANSWERED);
            i.putExtra(ACTION_EXTRA_INST_ID, getSurvey().getPublicInstitutionId());
            i.putExtra(ACTION_EXTRA_SURVEY_ID, getSurvey().getId());
            i.putExtra(ACTION_EXTRA_QUESTION_ID, getId());
            mAppContext.sendBroadcast(i, Constants.PERM_PRIVATE);
        }
    }

    public Integer getIdUserAnswer ()
    {
        return mIdUserAnswer;
    }

    @Override
    public boolean isAnswered()
    {
        return (getIdUserAnswer() != null);
    }
}
