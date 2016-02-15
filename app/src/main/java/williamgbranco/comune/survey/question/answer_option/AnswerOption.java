package williamgbranco.comune.survey.question.answer_option;

import android.content.Context;

import williamgbranco.comune.survey.question.Question;

/**
 * Created by William on 28/07/2015.
 */
public class AnswerOption
{
    private Integer mId;
    private String mAnswerText;
    private Question mQuestion;
    protected Context mAppContext;

    public AnswerOption(Context pAppContext)
    {
        mAppContext = pAppContext;
    }

    public AnswerOption(Context pAppContext, Integer pId, String pAnswerText)
    {
        mAppContext = pAppContext;
        mId = pId;
        mAnswerText = pAnswerText;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer pId) {
        mId = pId;
    }

    public String getAnswerText() {
        return mAnswerText;
    }

    public void setAnswerText(String pAnswerText) {
        mAnswerText = pAnswerText;
    }

    public Question getQuestion()
    {
        return mQuestion;
    }

    public void setQuestion(Question pQuestion)
    {
        mQuestion = pQuestion;
    }
}
