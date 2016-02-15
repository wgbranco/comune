package williamgbranco.comune.manager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import williamgbranco.comune.callback.GetResultCallback;
import williamgbranco.comune.callback.GetSurveyCallback;
import williamgbranco.comune.callback.SentSurveyCallback;
import williamgbranco.comune.database.DatabaseHelper;
import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.server.ServerRequests;
import williamgbranco.comune.service.CompleteSurveyService;
import williamgbranco.comune.survey.Survey;
import williamgbranco.comune.survey.question.CheckboxQuestion;
import williamgbranco.comune.survey.question.Question;
import williamgbranco.comune.survey.question.RadioButtonQuestion;
import williamgbranco.comune.survey.question.RatingQuestion;
import williamgbranco.comune.survey.question.YesOrNoQuestion;
import williamgbranco.comune.survey.question.answer_option.AnswerOption;
import williamgbranco.comune.survey.question.answer_option.AnswerOptionWithStatus;
import williamgbranco.comune.util.Constants;

/**
 * Created by William on 25/07/2015.
 */
public class SurveyManager
{
    private static final String TAG = "comune.SurveyManager";

    public static final String ACTION_NEW_SURVEY_INSERTED = "williamgbranco.comune.NEW_SURVEY_INSERTED";
    //public static final String PERM_PRIVATE = "williamgbranco.comune.PRIVATE";

    private static SurveyManager sSurveyManager;
    private ArrayList<Survey> mCurrentSurveys;
    private ArrayList<Survey> mCompletedSurveys;
    //private PublicInstitution mCurrentInstitution;
    private Context mAppContext;
    private DatabaseHelper mHelper;


    public SurveyManager(Context pAppContext)
    {
        mAppContext = pAppContext;
        mHelper = new DatabaseHelper(mAppContext);
    }

    public static SurveyManager get(Context c)
    {
        if (sSurveyManager == null) {
            sSurveyManager = new SurveyManager(c.getApplicationContext());
        }
        return sSurveyManager;
    }

    public void resetManager()
    {
        //mCurrentSurveys.clear();
        mCompletedSurveys = null;
        mCurrentSurveys = null;
        sSurveyManager = null;
    }

    public void setCurrentSurveys(ArrayList<Survey> pCurrentSurveys)
    {
        mCurrentSurveys = pCurrentSurveys;
    }

    public void addCurrentSurvey(Survey pSurvey)
    {
        Log.i(TAG, "addCurrentSurvey(" + pSurvey.toString()+")");

        if (mCurrentSurveys == null)
            mCurrentSurveys = new ArrayList<>();

        mCurrentSurveys.add(pSurvey);
    }

    public ArrayList<Survey> getCurrentSurveys()
    {
        return mCurrentSurveys;
    }

    public Survey getCurrentSurveyById(Integer instId, Integer surveyId)
    {
        Log.i(TAG, "getCurrentSurveyById: instId = " + instId + ", surveyId = " + surveyId);

        if ((getCurrentSurveys() != null) && (instId != null) && (surveyId != null))
        {
            for (Survey aux : getCurrentSurveys())
            {
                if ((aux.getId().equals(surveyId))
                    && (aux.getPublicInstitutionId().equals(instId)))
                {
                    Log.i(TAG, "getCurrentSurveyById return: " + aux.toString());
                    return aux;
                }
            }
        }

        Log.i(TAG, "getCurrentSurveyById return NULL");
        return null;
    }

    public void setCompletedSurveys(ArrayList<Survey> pCompletedSurveys)
    {
        mCompletedSurveys = pCompletedSurveys;
    }

    public void addCompletedSurvey(Survey pSurvey)
    {
        Log.i(TAG, "addCompletedSurvey(" + pSurvey.toString()+")");

        if (mCompletedSurveys == null)
            mCompletedSurveys = new ArrayList<>();

        Log.i(TAG, "mCompletedSurveys size: " + mCompletedSurveys.size());

        mCompletedSurveys.add(pSurvey);
    }

    public ArrayList<Survey> getCompletedSurveys()
    {
        return mCompletedSurveys;
    }

    public Survey getCompletedSurveyById(Integer instId, Integer surveyId)
    {
        Log.i(TAG, "getCompletedSurveyById: instId = " + instId + ", surveyId = " + surveyId);

        if ((getCompletedSurveys() != null) && (instId != null) && (surveyId != null))
        {
            for (Survey aux : getCompletedSurveys())
            {
                if ((aux.getId().equals(surveyId))
                        && (aux.getPublicInstitutionId().equals(instId)))
                {
                    Log.i(TAG, "getCompletedSurveyById return: " + aux.toString());
                    return aux;
                }
            }
        }

        Log.i(TAG, "getCompletedSurveyById return NULL");
        return null;
    }

    private void removeCurrentSurveyById(Integer instId, Integer surveyId)
    {
        Survey survey = getCurrentSurveyById(instId, surveyId);
        if (survey != null)
            mCurrentSurveys.remove(survey);
    }


    /*
    *  Database Functions
    * ***********************************************************************/

    public void insertSurveyForInstitution(Survey pSurvey)
    {
        mHelper.insertSurveyForInstitution(pSurvey);
    }

    private String getYesOrNoQuestionUserAnswer(Integer userId, Integer instId, Integer surveyId, Integer questionId)
    {
        String userAnswer = null;
        //Integer instID = getCurrentInstitution().getId();

        DatabaseHelper.YesOrNoAnswerCursor yesOrNoAnswerCursor = mHelper.queryUserAnswerForYesOrNoQuestion(userId, instId, surveyId, questionId);
        yesOrNoAnswerCursor.moveToFirst();

        if (!yesOrNoAnswerCursor.isAfterLast())
            userAnswer = yesOrNoAnswerCursor.getUserAnswer();

        yesOrNoAnswerCursor.close();

        //Log.i(TAG, "setYesOrNoQuestionSpecs, user_answer: " + userAnswer);
        return userAnswer;
    }

    private Float getRatingQuestionUserRating(Integer userId, Integer instId, Integer surveyId, Integer questionId)
    {
        Float userRating = null;
        //Integer instID = getCurrentInstitution().getId();

        DatabaseHelper.RatingAnswerCursor ratingAnswerCursor = mHelper.queryUserRatingForRatingQuestion(userId, instId, surveyId, questionId);
        ratingAnswerCursor.moveToFirst();

        if (!ratingAnswerCursor.isAfterLast())
            userRating = ratingAnswerCursor.getUserRating();

        ratingAnswerCursor.close();

        //Log.i(TAG, "setRatingQuestionSpecs, user_rating: " + userRating);

        return userRating;
    }

    private void setYesOrNoQuestionSpecs(Integer userId, Integer instId, Integer surveyId, YesOrNoQuestion question)
    {
        question.setUserAnswer(getYesOrNoQuestionUserAnswer(userId, instId, surveyId, question.getId()));
    }

    private void setRatingQuestionSpecs(Integer userId, Integer instId, Integer surveyId, RatingQuestion question)
    {
        question.setUserRating(getRatingQuestionUserRating(userId, instId, surveyId, question.getId()));
    }

    private void setRadioButtonQuestionSpecs(Integer userId, Integer instId, Integer surveyId, RadioButtonQuestion pQuestion)
    {
        //Integer instID = getCurrentInstitution().getId();

        DatabaseHelper.RadioButtonQuestionAnswerCursor radioButtonQuestionCursor = null;
        radioButtonQuestionCursor = mHelper.queryRadioButtonQuestionAnswerById(userId, instId, surveyId, pQuestion.getId());
        radioButtonQuestionCursor.moveToFirst();

        pQuestion.setIdUserAnswer(null);
        if (!radioButtonQuestionCursor.isAfterLast())
        {
            if (radioButtonQuestionCursor.getAnswerId() != null)
                pQuestion.setIdUserAnswer(radioButtonQuestionCursor.getAnswerId());
        }

        radioButtonQuestionCursor.close();

        /*********************************************/

        ArrayList<AnswerOption> answerOptions = new ArrayList<AnswerOption>();
        DatabaseHelper.AnswerOptionCursor answerOptionCursor = null;

        ////Log.i(TAG, "-------------------------------------------------------------------------");
        ////Log.i(TAG, "setMultipleChoiceQuestionSpecs, answer options added: ");

        DatabaseHelper.MultipleChoiceQuestionCursor multiChoiceQuestionCursor = mHelper.queryMultipleChoiceQuestion(pQuestion.getId());
        multiChoiceQuestionCursor.moveToFirst();

        while (!multiChoiceQuestionCursor.isAfterLast())
        {
            ////Log.i(TAG, "multiChoiceQuestionCursor not null");

            int answerOptionID = multiChoiceQuestionCursor.getAnswerOptionId();
            answerOptionCursor = mHelper.queryAnswerOptionById(answerOptionID);
            answerOptionCursor.moveToFirst();

            if (!answerOptionCursor.isAfterLast())
            {
                ////Log.i(TAG, "answerOptionCursor not null");

                answerOptions.add(answerOptionCursor.getAnswerOption());
            }

            answerOptionCursor.close();

            multiChoiceQuestionCursor.moveToNext();
        }
        ////Log.i(TAG, "-------------------------------------------------------------------------");

        multiChoiceQuestionCursor.close();

        pQuestion.setAnswerOptions(answerOptions);
    }

    private void setCheckboxQuestionSpecs(Integer userId, Integer instId, Integer surveyId, CheckboxQuestion pQuestion)
    {
        boolean answerOptionChecked = false;

        //Integer instID = getCurrentInstitution().getId();

        DatabaseHelper.CheckboxQuestionAnswerCursor checkboxQuestionAnswerCursor = null;
        ArrayList<AnswerOptionWithStatus> answerOptionsWithStatus = new ArrayList<>();
        DatabaseHelper.AnswerOptionCursor answerOptionCursor = null;

        //Log.i(TAG, "-------------------------------------------------------------------------");
        //Log.i(TAG, "setCheckboxQuestionSpecs, answer options added: ");

        DatabaseHelper.MultipleChoiceQuestionCursor multiChoiceQuestionCursor = mHelper.queryMultipleChoiceQuestion(pQuestion.getId());
        multiChoiceQuestionCursor.moveToFirst();

        while (!multiChoiceQuestionCursor.isAfterLast())
        {
            //Log.i(TAG, "multiChoiceQuestionCursor not null");

            int answerOptionID = multiChoiceQuestionCursor.getAnswerOptionId();

            checkboxQuestionAnswerCursor = mHelper.queryCheckboxQuestionAnswerById(userId, instId, surveyId, pQuestion.getId(), answerOptionID);
            checkboxQuestionAnswerCursor.moveToFirst();
            if (!checkboxQuestionAnswerCursor.isAfterLast())
                answerOptionChecked = checkboxQuestionAnswerCursor.isChecked();

            checkboxQuestionAnswerCursor.close();

            /*********************************************/

            answerOptionCursor = mHelper.queryAnswerOptionById(answerOptionID);
            answerOptionCursor.moveToFirst();

            if (!answerOptionCursor.isAfterLast())
            {
                //Log.i(TAG, "answerOptionCursor not null");

                AnswerOption ao = answerOptionCursor.getAnswerOption();

                AnswerOptionWithStatus aows = new AnswerOptionWithStatus(mAppContext);
                aows.setId(ao.getId());
                aows.setAnswerText(ao.getAnswerText());
                aows.setIsChecked(answerOptionChecked);

                answerOptionsWithStatus.add(aows);
            }

            answerOptionCursor.close();

            multiChoiceQuestionCursor.moveToNext();
        }

        //Log.i(TAG, "-------------------------------------------------------------------------");

        multiChoiceQuestionCursor.close();

        pQuestion.setAnswerOptions(answerOptionsWithStatus);
    }

    private void setQuestionSpecs(Integer userId, Integer instId, Integer surveyId, Question question)
    {
        if (question != null)
        {
            switch (question.getType())
            {
                case (Question.QUESTION_TYPE_YES_OR_NO):
                    setYesOrNoQuestionSpecs(userId, instId, surveyId, (YesOrNoQuestion) question);
                    break;

                case (Question.QUESTION_TYPE_RATING):
                    setRatingQuestionSpecs(userId, instId, surveyId, (RatingQuestion) question);
                    break;

                case (Question.QUESTION_TYPE_RADIOBUTTON):
                    setRadioButtonQuestionSpecs(userId, instId, surveyId, (RadioButtonQuestion) question);
                    break;

                case (Question.QUESTION_TYPE_CHECKBOX):
                    setCheckboxQuestionSpecs(userId, instId, surveyId, (CheckboxQuestion) question);
                    break;
            }
        }
    }

    private ArrayList<Question> setSurveyQuestions(Integer userId, Integer instId, Survey pSurvey)
    {
        Question question;
        ArrayList<Question> questions = new ArrayList<>();
        DatabaseHelper.QuestionCursor questionCursor = null;

        DatabaseHelper.SurveyQuestionCursor questionIdCursor = mHelper.queryQuestionsIdsForSurvey(pSurvey.getId());
        questionIdCursor.moveToFirst();

        while (!questionIdCursor.isAfterLast())
        {
            questionCursor = mHelper.queryQuestionById(questionIdCursor.getQuestionId());
            questionCursor.moveToFirst();

            if (!questionCursor.isAfterLast())
            {
                question = questionCursor.getQuestion();
                question.setSurvey(pSurvey);
                setQuestionSpecs(userId, instId, pSurvey.getId(), question);

                questions.add(question);
            }

            questionCursor.close();

            questionIdCursor.moveToNext();
        }

        questionIdCursor.close();

        return questions;
    }

    private Survey querySurveyById(Integer id)
    {
        Survey survey = null;

        DatabaseHelper.SurveyCursor cursor = mHelper.querySurveyById(id);
        cursor.moveToFirst();

        if (!cursor.isAfterLast())
            survey = cursor.getSurvey();
        cursor.close();

        return survey;
    }

    public boolean hasCompleteSurveys()
    {
        boolean hasCompleteSurveys = false;
        DatabaseHelper.InstitutionSurveyCursor instSurveyCursor = mHelper.queryCompleteSurveys();

        instSurveyCursor.moveToFirst();
        if (!instSurveyCursor.isAfterLast())
        {
            hasCompleteSurveys = true;
        }
        instSurveyCursor.close();

        return hasCompleteSurveys;
    }

    public boolean hasExpiredSurveys()
    {
        boolean hasExpiredSurveys = false;
        DatabaseHelper.InstitutionSurveyCursor instSurveyCursor = mHelper.queryExpiredSurveys();

        instSurveyCursor.moveToFirst();
        if (!instSurveyCursor.isAfterLast())
        {
            hasExpiredSurveys = true;
        }
        instSurveyCursor.close();

        return hasExpiredSurveys;
    }

    public boolean hasSurveysExpiringSoon(int userId)
    {
        boolean hasSurveysExpiringSoon = false;
        DatabaseHelper.InstitutionSurveyCursor instSurveyCursor = mHelper.queryExpiringSoonSurveys(userId);

        instSurveyCursor.moveToFirst();
        if (!instSurveyCursor.isAfterLast())
        {
            hasSurveysExpiringSoon = true;
        }
        instSurveyCursor.close();

        return hasSurveysExpiringSoon;
    }

    public boolean userHasIncompleteSurveys(int userId)
    {
        boolean hasIncompleteSurveys = false;
        DatabaseHelper.InstitutionSurveyCursor instSurveyCursor = mHelper.queryIncompleteSurveysForUser(userId);

        instSurveyCursor.moveToFirst();
        if (!instSurveyCursor.isAfterLast())
        {
            if (!surveyExpired(instSurveyCursor.getStartTime()))
                hasIncompleteSurveys = true;
        }
        instSurveyCursor.close();

        return hasIncompleteSurveys;
    }

    public Survey queryCompleteSurveysOneAtATime()
    {
        Log.i(TAG, "queryCompleteSurveysOneAtATime");
        DatabaseHelper.InstitutionSurveyCursor instSurveyCursor = mHelper.queryCompleteSurveys();
        Survey survey = null;

        Log.i(TAG, "number of complete surveys: " + instSurveyCursor.getCount());

        instSurveyCursor.moveToFirst();
        if (!instSurveyCursor.isAfterLast())
        {
            int userId = instSurveyCursor.getUserId();
            int instId = instSurveyCursor.getInstitutionId();
            int surveyId = instSurveyCursor.getSurveyId();

            survey = queryEntireSurveyForInstitution(userId, instId, surveyId, false);
        }
        instSurveyCursor.close();

        return survey;
    }

    public Survey queryEntireSurveyForInstitution(Integer userId, Integer instId, Integer surveyId, boolean isCurrentSurvey)
    {
        Log.i(TAG, "queryEntireSurveyForInstitution(" + userId + ", " + instId + ", " + surveyId + ")");
        Survey survey = null;

        survey = querySurveyById(surveyId);

        if (survey != null)
        {
            DatabaseHelper.InstitutionSurveyCursor instSurveyCursor = mHelper.queryInstSurveyById(userId, instId, surveyId);
            Log.i(TAG, "instSurveyCursor.getCount() = " + instSurveyCursor.getCount());
            instSurveyCursor.moveToFirst();
            if (!instSurveyCursor.isAfterLast())
            {
                Log.i(TAG, "instSurveyCursor NOT isAfterLast()");
                survey.setPublicInstitutionId(instId);
                survey.setStartTime(instSurveyCursor.getStartTime());
                survey.setIsComplete(instSurveyCursor.isCompleted());
                survey.setCompletionTime(instSurveyCursor.getCompleteTime());
            }
            instSurveyCursor.close();

            survey.setQuestions(setSurveyQuestions(userId, instId, survey));

            if (isCurrentSurvey) addCurrentSurvey(survey);
        }
        else {
            Log.i(TAG, "querySurveyById(" + surveyId + ") returns NULL");
        }

        return survey;
    }

    public ArrayList<Survey> querySurveysForInstitution(Integer institutionId)
    {
        ArrayList<Survey> surveysForInstitution = new ArrayList<Survey>();
        DatabaseHelper.InstitutionSurveyCursor instSurveyCursor;

        instSurveyCursor = mHelper.querySurveysForInstitution(institutionId);
        instSurveyCursor.moveToFirst();

        while (!instSurveyCursor.isAfterLast())
        {
            //Log.i(TAG, "On querySurveysForInstitution, surveysCursor is not empty aka retornou uma pesquisa do banco para a instituição: "+institutionId);

            int surveyId = instSurveyCursor.getSurveyId();

            DatabaseHelper.SurveyCursor surveyCursor = mHelper.querySurveyById(surveyId);
            surveyCursor.moveToFirst();

            if (!surveyCursor.isAfterLast())
            {
                Survey survey = surveyCursor.getSurvey();
                survey.setPublicInstitutionId(institutionId);
                survey.setSource(Survey.SURVEY_FROM_DB);
                survey.setStartTime(instSurveyCursor.getStartTime());
                survey.setIsComplete(instSurveyCursor.isCompleted());

                surveysForInstitution.add(survey);
            }

            surveyCursor.close();

            instSurveyCursor.moveToNext();
        }

        instSurveyCursor.close();

        return surveysForInstitution;
    }

    private boolean surveyExpired(GregorianCalendar startDate)
    {
        GregorianCalendar now = new GregorianCalendar();

        GregorianCalendar limite = startDate;
        limite.add(Calendar.DATE, Constants.SURVEY_MAX_DAYS_AVAILABLE);

        return now.after(limite);
    }

    public HashMap<PublicInstitution, Integer> queryInstitutionsWithIncompleteSurveys()
    {
        Log.i(TAG, "queryInstitutionsWithIncompleteSurveys");

        PublicInstitution institution = null;
        PublicInstitutionManager mPublicInstManager = PublicInstitutionManager.get(mAppContext);
        HashMap<PublicInstitution, Integer> instSurveysHashMap = new HashMap<>();
        DatabaseHelper.InstitutionSurveyCursor instWithIncompleteSurveyCursor;

        instWithIncompleteSurveyCursor = mHelper.queryInstitutionsWithIncompleteSurveysForCurrentUser();
        instWithIncompleteSurveyCursor.moveToFirst();
        while (!instWithIncompleteSurveyCursor.isAfterLast())
        {
            int instId = instWithIncompleteSurveyCursor.getInstitutionId();

            institution = mPublicInstManager.getPublicInstitutionById(instId);
            if (institution == null)
            {
                DatabaseHelper.PublicInstitutionCursor institutionCursor = mHelper.queryInstitutionById(instId);
                institutionCursor.moveToFirst();
                if (!institutionCursor.isAfterLast())
                {
                    institution = institutionCursor.getInstitution();
                    //mPublicInstManager.addPublicInstitution(institution);
                }
                institutionCursor.close();
            }

            int nSurveys = 0;

            if (institution != null) {
                DatabaseHelper.InstitutionSurveyCursor instSurveyCursor = mHelper.querySurveysForInstitution(instId);
                instSurveyCursor.moveToFirst();
                while (!instSurveyCursor.isAfterLast()) {
                    if (!instSurveyCursor.isCompleted()
                            && !surveyExpired(instSurveyCursor.getStartTime())) {
                        nSurveys++;
                    }

                    institution.setHasSurveyExpiringSoon(instSurveyCursor.isExpiringSoon());

                    instSurveyCursor.moveToNext();
                }
                instSurveyCursor.close();

                Log.i(TAG, "inst id = " + instId + ", num surveys = " + nSurveys);
                instSurveysHashMap.put(institution, nSurveys);

                instWithIncompleteSurveyCursor.moveToNext();
            }
        }
        instWithIncompleteSurveyCursor.close();

        return instSurveysHashMap;
    }

    public ArrayList<Integer> queryQuestionsIdsForSurvey(Integer surveyId)
    {
        ArrayList<Integer> questionsIds = new ArrayList<>();
        DatabaseHelper.SurveyQuestionCursor questionIdCursor;

        questionIdCursor = mHelper.queryQuestionsIdsForSurvey(surveyId);
        questionIdCursor.moveToFirst();

        while (!questionIdCursor.isAfterLast())
        {
            questionsIds.add(questionIdCursor.getQuestionId());

            questionIdCursor.moveToNext();
        }

        questionIdCursor.close();

        return questionsIds;
    }

    public void deleteSurveyForUser(Integer userId, Integer instId, Integer surveyId)
    {
        UserManager userManager = UserManager.get(mAppContext);
        PublicInstitutionManager institutionManager = PublicInstitutionManager.get(mAppContext);

        Log.i(TAG, "deleteUserAnswersForSurvey");
        deleteUserAnswersForSurvey(userId, instId, surveyId);

        Log.i(TAG, "deleteSurvey");
        deleteSurvey(surveyId);

        removeCurrentSurveyById(instId, surveyId);
        institutionManager.deleteInstitution(instId);

        userManager.deleteUser(userId);
    }

    private void deleteSurvey(Integer surveyId)
    {
        DatabaseHelper.InstitutionSurveyCursor instSurveyCursor = mHelper.queryUsesForSurvey(surveyId);

        if (instSurveyCursor.getCount() == 0)
        {
            DatabaseHelper.SurveyQuestionCursor questionsIdsCursor = mHelper.queryQuestionsIdsForSurvey(surveyId);

            questionsIdsCursor.moveToFirst();
            while (!questionsIdsCursor.isAfterLast())
            {
                Integer questionId = questionsIdsCursor.getQuestionId();

                DatabaseHelper.SurveyQuestionCursor surveysThatUseQuestionCursor = mHelper.queryUsesForQuestions(questionId);

                if (surveysThatUseQuestionCursor.getCount() == 1)
                {
                    DatabaseHelper.QuestionCursor questionCursor = mHelper.queryQuestionById(questionId);

                    questionCursor.moveToFirst();
                    if (!questionCursor.isAfterLast())
                    {
                        Integer questionType = questionCursor.getQuestion().getType();

                        if ((questionType.equals(Question.QUESTION_TYPE_RADIOBUTTON))
                                || (questionType.equals(Question.QUESTION_TYPE_CHECKBOX)))
                        {
                            DatabaseHelper.MultipleChoiceQuestionCursor multiChoiceQuestionCursor = mHelper.queryMultipleChoiceQuestion(questionId);

                            multiChoiceQuestionCursor.moveToFirst();
                            while (!multiChoiceQuestionCursor.isAfterLast())
                            {
                                int answerOptionId = multiChoiceQuestionCursor.getAnswerOptionId();

                                DatabaseHelper.MultipleChoiceQuestionCursor questionsThatUseAnswerOption = mHelper.queryUsesForAnswerOption(answerOptionId);

                                if (questionsThatUseAnswerOption.getCount() == 1)
                                {
                                    deleteAnswerOptionById(answerOptionId);
                                }
                                questionsThatUseAnswerOption.close();

                                multiChoiceQuestionCursor.moveToNext();
                            }
                            multiChoiceQuestionCursor.close();

                            deleteMultiChoiceQuestionEntryById(questionId);
                        }
                    }
                    questionCursor.close();

                    deleteQuestionById(questionId);
                }
                questionsIdsCursor.moveToNext();
            }
            questionsIdsCursor.close();

            deleteSurveyQuestionsEntryBySurveyId(surveyId);
            deleteSurveyById(surveyId);
        }
        else
        {
            Log.i(TAG, "survey " + surveyId + " being used, not deleted");
        }

        instSurveyCursor.close();
    }

    private void deleteUserAnswersForSurvey(Integer userId, Integer instId, Integer surveyId)
    {
        DatabaseHelper.SurveyQuestionCursor questionsIdsCursor = mHelper.queryQuestionsIdsForSurvey(surveyId);

        questionsIdsCursor.moveToFirst();
        while (!questionsIdsCursor.isAfterLast())
        {
            int questionId = questionsIdsCursor.getQuestionId();

            DatabaseHelper.QuestionCursor questionCursor = mHelper.queryQuestionById(questionId);

            questionCursor.moveToFirst();
            if (!questionCursor.isAfterLast())
            {
                Integer questionType = questionCursor.getQuestion().getType();

                deleteUserAnswerForQuestion(userId, instId, surveyId, questionId, questionType);
            }
            questionCursor.close();

            questionsIdsCursor.moveToNext();
        }
        questionsIdsCursor.close();

        deleteInstSurveyForUser(userId, instId, surveyId);
    }

    private void deleteUserAnswerForQuestion(Integer userId, Integer instId, Integer surveyId, Integer questionId, Integer questionType)
    {
        switch (questionType)
        {
            case (Question.QUESTION_TYPE_YES_OR_NO):
                deleteUserAnswerForYesOrNoQuestion(userId, instId, surveyId, questionId);
                break;

            case (Question.QUESTION_TYPE_RATING):
                deleteUserAnswerForRatingQuestion(userId, instId, surveyId, questionId);
                break;

            case (Question.QUESTION_TYPE_RADIOBUTTON):
                deleteUserAnswerForRadioButtonQuestion(userId, instId, surveyId, questionId);
                break;

            case (Question.QUESTION_TYPE_CHECKBOX):
                deleteUserAnswersForCheckBoxQuestion(userId, instId, surveyId, questionId);
                break;
        }
    }


    /*
    *  Background Database Functions
    * ***********************************************************************/

    /*********************** Operações de SELECT ***********************/

    public void fetchEntireSurveyByIdOnServer(final Integer instId, final Integer surveyId, final GetSurveyCallback pCallback)
    {
        new ServerRequests(mAppContext).fetchEntireSurveyOnServerById(instId, surveyId, new GetSurveyCallback() {
            @Override
            public void done(Survey returnedSurvey)
            {
                try {
                    if (returnedSurvey != null)
                    {
                        PublicInstitution institution = PublicInstitutionManager.get(mAppContext).getPublicInstitutionById(instId);

                        if (institution != null)
                        {
                            mHelper.insertInstitution(institution);
                            mHelper.insertSurveyForInstitution(returnedSurvey);

                            mAppContext.sendBroadcast(new Intent(ACTION_NEW_SURVEY_INSERTED), Constants.PERM_PRIVATE);
                        }
                    }
                    pCallback.done(returnedSurvey);
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
        });
    }


    /*********************** Operações de UPDATE ***********************/

    public void updateSurveyCompleteness(Integer instId, Integer surveyId, boolean completed, GetResultCallback pCallback)
    {
        new UpdateSurveyCompleteness(instId, surveyId, completed, pCallback).execute();
    }

    public void updateSurveyExpiration(Integer instId, Integer surveyId, Long newStartTime)
    {
        new UpdateSurveyExpiration(instId, surveyId, newStartTime).execute();
    }

    public void updateUserAnswerForYesOrNoQuestion(Integer instId, Integer surveyId, Integer questionId, String newAnswer)
    {
        new UpdateUserAnswerForYesOrNoQuestion(instId, surveyId, questionId, newAnswer).execute();
    }

    public void updateUserRatingForRatingQuestion(Integer instId, Integer surveyId, Integer questionId, Float newRating)
    {
        new UpdateUserRatingForRatingQuestion(instId, surveyId, questionId, newRating).execute();
    }

    public void updateUserAnswerForRadioButtonQuestion(Integer instId, Integer surveyId, Integer questionId, Integer answerOptionId)
    {
        new UpdateUserAnswerForRadioButtonQuestion(instId, surveyId, questionId, answerOptionId).execute();
    }

    public void updateCheckStatusForCheckBoxQuestion(Integer instId, Integer surveyId, Integer questionId, Integer answerOptionId, boolean newStatus)
    {
        new UpdateCheckStatusForCheckBoxQuestion(instId, surveyId, questionId, answerOptionId, newStatus).execute();
    }


    /*********************** Operações de DELETE ***********************/

    public void deleteSurveyQuestionsEntryBySurveyId(Integer surveyId)
    {
        mHelper.deleteSurveyQuestionsEntryBySurveyId(surveyId);
    }

    public void deleteSurveyQuestionsEntryBySurveyIdTask(Integer surveyId)
    {
        new DeleteSurveyQuestionsEntryBySurveyIdTask(surveyId).execute();
    }

    public void deleteSurveyById(Integer surveyId)
    {
        mHelper.deleteSurveyById(surveyId);
    }

    public void deleteSurveyByIdTask(Integer surveyId)
    {
        new DeleteSurveyByIdTask(surveyId).execute();
    }

    public void deleteQuestionById(Integer questionId)
    {
        mHelper.deleteQuestionById(questionId);
    }

    public void deleteQuestionByIdTask(Integer questionId)
    {
        new DeleteQuestionByIdTask(questionId).execute();
    }

    public void deleteMultiChoiceQuestionEntryById(Integer questionId)
    {
        mHelper.deleteMultiChoiceQuestionEntryById(questionId);
    }

    public void deleteMultiChoiceQuestionEntryByIdTask(Integer questionId)
    {
        new DeleteMultiChoiceQuestionEntryByIdTask(questionId).execute();
    }

    public void deleteAnswerOptionById(Integer answerOptionId)
    {
        mHelper.deleteAnswerOptionById(answerOptionId);
    }

    public void deleteAnswerOptionByIdTask(Integer answerOptionId)
    {
        new DeleteAnswerOptionByIdTask(answerOptionId).execute();
    }

    public void deleteUserAnswerForYesOrNoQuestion(Integer userId, Integer instId, Integer surveyId, Integer questionId)
    {
        mHelper.deleteUserAnswerForYesOrNoQuestion(userId, instId, surveyId, questionId);
    }

    public void deleteUserAnswerForYesOrNoQuestionTask(Integer userId, Integer instId, Integer surveyId, Integer questionId)
    {
        new DeleteUserAnswerForYesOrNoQuestionTask(userId, instId, surveyId, questionId).execute();
    }

    public void deleteUserAnswerForRatingQuestion(Integer userId, Integer instId, Integer surveyId, Integer questionId)
    {
        mHelper.deleteUserAnswerForRatingQuestion(userId, instId, surveyId, questionId);
    }

    public void deleteUserAnswerForRatingQuestionTask(Integer userId, Integer instId, Integer surveyId, Integer questionId)
    {
        new DeleteUserAnswerForRatingQuestionTask(userId, instId, surveyId, questionId).execute();
    }

    public void deleteUserAnswerForRadioButtonQuestion(Integer userId, Integer instId, Integer surveyId, Integer questionId)
    {
        mHelper.deleteUserAnswerForRadioButtonQuestion(userId, instId, surveyId, questionId);
    }

    public void deleteUserAnswerForRadioButtonQuestionTask(Integer userId, Integer instId, Integer surveyId, Integer questionId)
    {
        new DeleteUserAnswerForRadioButtonQuestionTask(userId, instId, surveyId, questionId).execute();
    }

    public void deleteUserAnswersForCheckBoxQuestion(Integer userId, Integer instId, Integer surveyId, Integer questionId)
    {
        mHelper.deleteUserAnswersForCheckBoxQuestion(userId, instId, surveyId, questionId);
    }

    public void deleteUserAnswersForCheckBoxQuestionTask(Integer userId, Integer instId, Integer surveyId, Integer questionId)
    {
        new DeleteUserAnswersForCheckBoxQuestionTask(userId, instId, surveyId, questionId).execute();
    }

    public void deleteInstSurveyForUser(Integer userId, Integer instId, Integer surveyId)
    {
        mHelper.deleteInstSurveyForUser(userId, instId, surveyId);
    }

    public void deleteInstSurveyForUserTask(Integer userId, Integer instId, Integer surveyId)
    {
        new DeleteInstSurveyForUserTask(userId, instId, surveyId).execute();
    }


    /*
    *  Background Database Tasks
    * ***********************************************************************/

    /*********************** Operações de UPDATE ***********************/

    private class UpdateUserAnswerForYesOrNoQuestion extends AsyncTask<Void, Void, Integer>
    {
        Integer mInstId;
        Integer mSurveyId;
        Integer mQuestionId;
        String mNewAnswer;

        public UpdateUserAnswerForYesOrNoQuestion(Integer instId, Integer surveyId, Integer questionId, String newAnswer)
        {
            mInstId = instId;
            mSurveyId = surveyId;
            mQuestionId = questionId;
            mNewAnswer = newAnswer;
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            return mHelper.updateUserAnswerForYesOrNoQuestion(mInstId, mSurveyId, mQuestionId, mNewAnswer);
        }
    }

    private class UpdateUserRatingForRatingQuestion extends AsyncTask<Void, Void, Integer>
    {
        Integer mInstId;
        Integer mSurveyId;
        Integer mQuestionId;
        Float mNewRating;

        public UpdateUserRatingForRatingQuestion(Integer instId, Integer surveyId, Integer questionId, Float newRating)
        {
            mInstId = instId;
            mSurveyId = surveyId;
            mQuestionId = questionId;
            mNewRating = newRating;
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            return mHelper.updateUserRatingForRatingQuestion(mInstId, mSurveyId, mQuestionId, mNewRating);
        }
    }

    private class UpdateCheckStatusForCheckBoxQuestion extends AsyncTask<Void, Void, Integer>
    {
        Integer mInstId;
        Integer mSurveyId;
        Integer mQuestionId;
        Integer mAnswerOptionId;
        boolean mNewStatus;

        public UpdateCheckStatusForCheckBoxQuestion(Integer instId, Integer surveyId, Integer questionId, Integer answerOptionId, boolean newStatus)
        {
            mInstId = instId;
            mSurveyId = surveyId;
            mQuestionId = questionId;
            mAnswerOptionId = answerOptionId;
            mNewStatus = newStatus;
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            return mHelper.updateCheckStatusForCheckboxQuestion(mInstId, mSurveyId, mQuestionId, mAnswerOptionId, mNewStatus);
        }
    }

    private class UpdateUserAnswerForRadioButtonQuestion extends AsyncTask<Void, Void, Integer>
    {
        Integer mInstId;
        Integer mSurveyId;
        Integer mQuestionId;
        Integer mAnswerOptionId;

        public UpdateUserAnswerForRadioButtonQuestion(Integer instId, Integer surveyId, Integer questionId, Integer answerOptionId)
        {
            mInstId = instId;
            mSurveyId = surveyId;
            mQuestionId = questionId;
            mAnswerOptionId = answerOptionId;
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            return mHelper.updateUserAnswerForRadioButtonQuestion(mInstId, mSurveyId, mQuestionId, mAnswerOptionId);
        }
    }

    private class UpdateSurveyCompleteness extends AsyncTask<Void, Void, Integer>
    {
        Integer mInstId;
        Integer mSurveyId;
        boolean mComplete;
        GetResultCallback mCallback;

        public UpdateSurveyCompleteness(Integer instId, Integer surveyId, boolean complete, GetResultCallback pCallback)
        {
            mInstId = instId;
            mSurveyId = surveyId;
            mComplete = complete;
            mCallback = pCallback;
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            return mHelper.updateSurveyCompleteness(mInstId, mSurveyId, mComplete);
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            if (result > 0)
            {
                addCompletedSurvey(getCurrentSurveyById(mInstId, mSurveyId));

                Intent i = new Intent(mAppContext, CompleteSurveyService.class);
                mAppContext.startService(i);
            }
            mCallback.done();
        }
    }

    private class UpdateSurveyExpiration extends AsyncTask<Void, Void, Integer>
    {
        Integer mInstId;
        Integer mSurveyId;
        Long mNewStartTime;

        public UpdateSurveyExpiration(Integer instId, Integer surveyId, Long newStartTime)
        {
            mInstId = instId;
            mSurveyId = surveyId;
            mNewStartTime = newStartTime;
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            return mHelper.updateSurveyExpiration(mInstId, mSurveyId, mNewStartTime);
        }
    }


    /*********************** Operações de DELETE ***********************/

    private class DeleteSurveyByIdTask extends AsyncTask<Void, Void, Integer>
    {
        Integer mSurveyId;

        public DeleteSurveyByIdTask(Integer pSurveyId)
        {
            mSurveyId = pSurveyId;
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            return mHelper.deleteSurveyById(mSurveyId);
        }
    }

    private class DeleteSurveyQuestionsEntryBySurveyIdTask extends AsyncTask<Void, Void, Integer>
    {
        Integer mSurveyId;

        public DeleteSurveyQuestionsEntryBySurveyIdTask(Integer pSurveyId)
        {
            mSurveyId = pSurveyId;
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            return mHelper.deleteSurveyQuestionsEntryBySurveyId(mSurveyId);
        }
    }

    private class DeleteMultiChoiceQuestionEntryByIdTask extends AsyncTask<Void, Void, Integer>
    {
        Integer mQuestionId;

        public DeleteMultiChoiceQuestionEntryByIdTask(Integer pQuestionId)
        {
            mQuestionId = pQuestionId;
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            return mHelper.deleteMultiChoiceQuestionEntryById(mQuestionId);
        }
    }

    private class DeleteQuestionByIdTask extends AsyncTask<Void, Void, Integer>
    {
        Integer mQuestionId;

        public DeleteQuestionByIdTask(Integer pQuestionId)
        {
            mQuestionId = pQuestionId;
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            return mHelper.deleteQuestionById(mQuestionId);
        }
    }

    private class DeleteAnswerOptionByIdTask extends AsyncTask<Void, Void, Integer>
    {
        Integer mAnswerOptionId;

        public DeleteAnswerOptionByIdTask(Integer pAnswerOptionId)
        {
            mAnswerOptionId = pAnswerOptionId;
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            return mHelper.deleteAnswerOptionById(mAnswerOptionId);
        }
    }

    private class DeleteUserAnswerForYesOrNoQuestionTask extends AsyncTask<Void, Void, Integer>
    {
        Integer mUserId;
        Integer mInstId;
        Integer mSurveyId;
        Integer mQuestionId;

        public DeleteUserAnswerForYesOrNoQuestionTask(Integer pUserId, Integer pInstId, Integer pSurveyId, Integer pQuestionId)
        {
            mUserId = pUserId;
            mInstId = pInstId;
            mSurveyId = pSurveyId;
            mQuestionId = pQuestionId;
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            return mHelper.deleteUserAnswerForYesOrNoQuestion(mUserId, mInstId, mSurveyId, mQuestionId);
        }
    }

    private class DeleteUserAnswerForRatingQuestionTask extends AsyncTask<Void, Void, Integer>
    {
        Integer mUserId;
        Integer mInstId;
        Integer mSurveyId;
        Integer mQuestionId;

        public DeleteUserAnswerForRatingQuestionTask(Integer pUserId, Integer pInstId, Integer pSurveyId, Integer pQuestionId)
        {
            mUserId = pUserId;
            mInstId = pInstId;
            mSurveyId = pSurveyId;
            mQuestionId = pQuestionId;
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            return mHelper.deleteUserAnswerForRatingQuestion(mUserId, mInstId, mSurveyId, mQuestionId);
        }
    }

    private class DeleteUserAnswerForRadioButtonQuestionTask extends AsyncTask<Void, Void, Integer>
    {
        Integer mUserId;
        Integer mInstId;
        Integer mSurveyId;
        Integer mQuestionId;

        public DeleteUserAnswerForRadioButtonQuestionTask(Integer pUserId, Integer pInstId, Integer pSurveyId, Integer pQuestionId)
        {
            mUserId = pUserId;
            mInstId = pInstId;
            mSurveyId = pSurveyId;
            mQuestionId = pQuestionId;
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            return mHelper.deleteUserAnswerForRadioButtonQuestion(mUserId, mInstId, mSurveyId, mQuestionId);
        }
    }

    private class DeleteUserAnswersForCheckBoxQuestionTask extends AsyncTask<Void, Void, Integer>
    {
        Integer mUserId;
        Integer mInstId;
        Integer mSurveyId;
        Integer mQuestionId;

        public DeleteUserAnswersForCheckBoxQuestionTask(Integer pUserId, Integer pInstId, Integer pSurveyId, Integer pQuestionId)
        {
            mUserId = pUserId;
            mInstId = pInstId;
            mSurveyId = pSurveyId;
            mQuestionId = pQuestionId;
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            return mHelper.deleteUserAnswersForCheckBoxQuestion(mUserId, mInstId, mSurveyId, mQuestionId);
        }
    }

    private class DeleteInstSurveyForUserTask extends AsyncTask<Void, Void, Integer>
    {
        Integer mUserId;
        Integer mInstId;
        Integer mSurveyId;

        public DeleteInstSurveyForUserTask(Integer pUserId, Integer pInstId, Integer pSurveyId)
        {
            mUserId = pUserId;
            mInstId = pInstId;
            mSurveyId = pSurveyId;
        }

        @Override
        protected Integer doInBackground(Void... params)
        {
            return mHelper.deleteInstSurveyForUser(mUserId, mInstId, mSurveyId);
        }
    }


    /*
    *  Server Functions
    * ***********************************************************************/

    public void uploadSurveyAnswersToServer(Integer userId, Survey pSurvey, SentSurveyCallback pCallback)
    {
        new ServerRequests(mAppContext).uploadSurveyAnswersToServer(userId, pSurvey, pCallback);
    }
}
