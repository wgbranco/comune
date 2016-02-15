package williamgbranco.comune.survey.question;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import williamgbranco.comune.util.Constants;

/**
 * Created by William on 28/07/2015.
 */
public class RatingQuestion extends Question
{
    private Float mUserRating;


    public RatingQuestion(Context pAppContext)
    {
        super(pAppContext);
    }

    @Override
    public Integer getType()
    {
        return QUESTION_TYPE_RATING;
    }

    public void setUserRating(Float pOption)
    {
        mUserRating = pOption;

        if (notifyQuestionAnswered)
        {
            Log.i(TAG, "RatingQuestion - Intent broadcast: question answered: " + getId() +
                    " for survey: " + getSurvey().getId() +
                    " for institution: " + getSurvey().getPublicInstitutionId());
            Intent i = new Intent(ACTION_QUESTION_ANSWERED);
            i.putExtra(ACTION_EXTRA_INST_ID, getSurvey().getPublicInstitutionId());
            i.putExtra(ACTION_EXTRA_SURVEY_ID, getSurvey().getId());
            i.putExtra(ACTION_EXTRA_QUESTION_ID, getId());
            mAppContext.sendBroadcast(i, Constants.PERM_PRIVATE);
        }
    }

    public Float getUserRating()
    {
        return mUserRating;
    }

    @Override
    public boolean isAnswered()
    {
        return ((getUserRating() != null) && (getUserRating() > 0.0));
    }
}
