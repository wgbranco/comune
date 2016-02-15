package williamgbranco.comune.survey.question;

import android.content.Context;

import williamgbranco.comune.survey.Survey;

/**
 * Created by William on 09/07/2015.
 */
public abstract class Question
{
    protected static final String TAG = "comune.Question";
    public static final String ACTION_QUESTION_ANSWERED = "comune.Question.Answered";
    public static final String ACTION_EXTRA_INST_ID = "comune.Question.Answered.inst_id";
    public static final String ACTION_EXTRA_SURVEY_ID = "comune.Question.Answered.survey_id";
    public static final String ACTION_EXTRA_QUESTION_ID = "comune.Question.Answered.question_id";
    //protected static final String PERM_PRIVATE = "williamgbranco.comune.PRIVATE";

    public static final int QUESTION_TYPE_YES_OR_NO = 1;
    public static final int QUESTION_TYPE_RATING = 2;
    public static final int QUESTION_TYPE_RADIOBUTTON = 3;
    public static final int QUESTION_TYPE_CHECKBOX = 4;

    private Integer mId;
    private String mDescription;
    private boolean isMandatory;
    private Survey mSurvey;

    protected Context mAppContext;
    protected boolean notifyQuestionAnswered;


    public Question(Context pAppContext)
    {
        mAppContext = pAppContext;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer pId) {
        mId = pId;
    }

    public abstract Integer getType();

    /*protected void setType(Integer pType) {
        mType = pType;
    }*/

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(boolean pIsMandatory) {
        isMandatory = pIsMandatory;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String pDescription) {
        mDescription = pDescription;
    }

    public Survey getSurvey()
    {
        return mSurvey;
    }

    public void setSurvey(Survey pSurvey)
    {
        mSurvey = pSurvey;
    }

    public void notifyQuestionAnswered(boolean value)
    {
        notifyQuestionAnswered = value;
    }

    public abstract boolean isAnswered();

}
