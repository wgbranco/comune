package williamgbranco.comune.survey;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import williamgbranco.comune.survey.question.Question;
import williamgbranco.comune.util.Constants;

/**
 * Created by William on 09/07/2015.
 */
public class Survey
{
    public static final int FIRST_PAGE = -198;
    public static final int LAST_PAGE = -199;
    public static final int LOADING = -200;

    public static final String SURVEY_FROM_DB = "Survey.FromDatabase";
    public static final String NEW_SURVEY_AVAILABLE = "Survey.NewAvailable";

    private Integer mId;
    private GregorianCalendar mAvailableSince;
    private GregorianCalendar mStartTime;
    private GregorianCalendar mCompletionTime;
    private String mDescription;
    private Integer mPublicInstitutionId;
    private Integer mDepartmentCode;
    //private PublicInstitution mPublicInstitution;
    private ArrayList<Question> mQuestions;
    private String mSource;
    private boolean isComplete;


    public Survey() {}

    public Survey(Integer pId, String pDescription)
    {
        mId = pId;
        mDescription = pDescription;
        mStartTime = new GregorianCalendar();
    }

    public void setId(Integer pId) {
        mId = pId;
    }

    public Integer getId() {
        return mId;
    }

    public void setAvailableSince(GregorianCalendar pAvailableSince)
    {
        mAvailableSince = pAvailableSince;
    }

    public GregorianCalendar getAvailableSince()
    {
        return (GregorianCalendar) mAvailableSince.clone();
    }

    public void setStartTime(GregorianCalendar pStartTime)
    {
        mStartTime = pStartTime;
    }

    public GregorianCalendar getStartTime()
    {
        return (GregorianCalendar) mStartTime.clone();
    }

    public void setCompletionTime(GregorianCalendar pCompletionTime)
    {
        mCompletionTime = pCompletionTime;
    }

    public GregorianCalendar getCompletionTime()
    {
        return (GregorianCalendar) mCompletionTime.clone();
    }

    public void setDescription(String pDescription) {
        mDescription = pDescription;
    }

    public String getDescription()
    {
        return mDescription;
    }

    public Integer getPublicInstitutionId()
    {
        return mPublicInstitutionId;
    }

    public void setPublicInstitutionId(Integer pPublicInstitutionId) {
        mPublicInstitutionId = pPublicInstitutionId;
    }

    /*public PublicInstitution getPublicInstitution()
    {
        return mPublicInstitution;
    }

    public void setPublicInstitution(PublicInstitution pPublicInstitution)
    {
        mPublicInstitution = pPublicInstitution;
    }*/

    public Integer getDepartmentCode()
    {
        return mDepartmentCode;
    }

    public void setDepartmentCode(Integer pDepartmentCode)
    {
        mDepartmentCode = pDepartmentCode;
    }

    public void setQuestions(ArrayList<Question> pQuestions)
    {
        mQuestions = pQuestions;

        for (Question question : mQuestions)
        {
            question.setSurvey(this);
        }
    }

    public void addQuestion(Question pQuestion)
    {
        if (mQuestions == null)
        {
            mQuestions = new ArrayList<Question>();
        }

        pQuestion.setSurvey(this);
        mQuestions.add(pQuestion);
    }

    public ArrayList<Question> getQuestions()
    {
        return mQuestions;
    }

    public Question getQuestion(Integer id)
    {
        for (Question question : mQuestions)
        {
            if (question.getId().equals(id))
            {
                return question;
            }
        }
        return null;
    }

    public int getQuestionPosition(Question pQuestion)
    {
        int size = mQuestions.size();

        for (int i=0; i<size; i++)
        {
            Question question = mQuestions.get(i);

            if (question == pQuestion)
            {
                return i+1;
            }
        }
        return -1;
    }

    private int numberOfAnsweredQuestions(ArrayList<Question> pQuestions)
    {
        int nAnsweredQuestions = 0;

        for (Question question : pQuestions)
        {
            if (question.isAnswered())
                nAnsweredQuestions++;
        }

        return nAnsweredQuestions;
    }

    public boolean isFullyAnswered()
    {
        return (mQuestions.size() == numberOfAnsweredQuestions(mQuestions));
    }

    public boolean lastQuestionOnlyOneNotYetAnswered()
    {
        Question lastQuestion = mQuestions.get(mQuestions.size() - 1);

        ArrayList<Question> setOfQuestions = (ArrayList<Question>) mQuestions.clone();
        setOfQuestions.remove(lastQuestion);

        return ((setOfQuestions.size() == numberOfAnsweredQuestions(setOfQuestions)) && !(lastQuestion.isAnswered()));
    }

    public int getRemainingTimeInHoursForSurveyCompletion()
    {
        GregorianCalendar limite = getStartTime();
        limite.add(Calendar.DATE, Constants.SURVEY_MAX_DAYS_AVAILABLE);
        GregorianCalendar now = new GregorianCalendar();

        int dif = (int) (limite.getTime().getTime() - now.getTime().getTime())/(1000 * 60 * 60);

        return dif;
    }

    public boolean hasExpired()
    {
        GregorianCalendar now = new GregorianCalendar();

        GregorianCalendar limite = getStartTime();
        limite.add(Calendar.DATE, Constants.SURVEY_MAX_DAYS_AVAILABLE);

        return now.after(limite);
    }

    public String getSource()
    {
        return mSource;
    }

    public void setSource(String pSource)
    {
        mSource = pSource;
    }

    public boolean isComplete()
    {
        return isComplete;
    }

    public void setIsComplete(boolean complete)
    {
        isComplete = complete;
    }

    @Override
    public String toString()
    {
        return "surveyId = " + getId() + ", instId = " + getPublicInstitutionId() + ", availableSince = " + getAvailableSince().toString() + ", startTime = " + getStartTime().toString();
    }
}
