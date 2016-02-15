package williamgbranco.comune.server;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import williamgbranco.comune.callback.GetPublicInstitutionCallbacks;
import williamgbranco.comune.callback.GetSurveyCallback;
import williamgbranco.comune.callback.SentSurveyCallback;
import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.manager.UserManager;
import williamgbranco.comune.survey.Survey;
import williamgbranco.comune.survey.question.CheckboxQuestion;
import williamgbranco.comune.survey.question.Question;
import williamgbranco.comune.survey.question.RadioButtonQuestion;
import williamgbranco.comune.survey.question.RatingQuestion;
import williamgbranco.comune.survey.question.YesOrNoQuestion;
import williamgbranco.comune.survey.question.answer_option.AnswerOption;
import williamgbranco.comune.survey.question.answer_option.AnswerOptionWithStatus;
import williamgbranco.comune.user.User;
import williamgbranco.comune.util.Constants;

/**
 * Created by William Gomes de Branco on 25/07/2015.
 */
public class SurveyFetchr
{
    private static final String TAG = "comune.SurveyFetchr";

    private Context mAppContext;
    private UserManager mUserManager;
    private SingletonRequestQueue mSingletonRequestQueue;


    public SurveyFetchr(Context pAppContext)
    {
        mAppContext = pAppContext;
        mUserManager = UserManager.get(mAppContext);
        mSingletonRequestQueue = SingletonRequestQueue.getInstance(mAppContext);
    }

    //url: http://localhost:8080/comuneWS/surveys/getSurveyById, enviar objeto UserSurveyInfo (maven)
    public void fetchSurveyById(final Integer instId, Integer surveyId, final GetSurveyCallback pGetSurveyCallback)
    {
        User user = mUserManager.getCurrentUser();

        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_GET_SURVEY_BY_ID;
        Log.i(TAG, "fetchSurveyById, URL: " + url);

        try
        {
            JSONObject jsonObjectParams = new JSONObject();
            jsonObjectParams.put("userId", user.getUserId());
            jsonObjectParams.put("surveyId", surveyId);

            JsonObjectRequest jsonObjectReq = new JsonObjectRequest(Method.POST, url, jsonObjectParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    Survey survey = null;
                    int i, j, length1, length2;
                    Question question = null;
                    AnswerOption answerOption;
                    AnswerOptionWithStatus answerOptionWithStatus;
                    JSONObject jsonQuestion, jsonAnswerOption;
                    JSONArray jsonQuestions, jsonAnswerOptions;

                    try
                    {
                        survey = new Survey();
                        survey.setPublicInstitutionId(instId);
                        survey.setSource(Survey.NEW_SURVEY_AVAILABLE);
                        survey.setStartTime(new GregorianCalendar());
                        survey.setId(response.getInt("surveyId"));
                        survey.setDescription(response.getString("description"));
                        GregorianCalendar availableSince = new GregorianCalendar();
                        availableSince.setTimeInMillis(response.getLong("availableSince"));
                        survey.setAvailableSince(availableSince);

                        jsonQuestions = response.getJSONArray("questions");
                        length1 = jsonQuestions.length();

                        if (length1 == 0)
                        {
                            pGetSurveyCallback.done(null);
                            return;
                        }

                        for (i = 0; i < length1; i++)
                        {
                            jsonQuestion = jsonQuestions.getJSONObject(i);
                            int questionType = jsonQuestion.getInt("type");

                            if (Question.QUESTION_TYPE_YES_OR_NO == questionType)
                            {
                                question = new YesOrNoQuestion(mAppContext);
                                question.setId(jsonQuestion.getInt("id"));
                                question.setDescription(jsonQuestion.getString("descr"));
                            }
                            else if (Question.QUESTION_TYPE_RATING == questionType)
                            {
                                question = new RatingQuestion(mAppContext);
                                question.setId(jsonQuestion.getInt("id"));
                                question.setDescription(jsonQuestion.getString("descr"));
                            }
                            else if (Question.QUESTION_TYPE_RADIOBUTTON == questionType)
                            {
                                question = new RadioButtonQuestion(mAppContext);
                                question.setId(jsonQuestion.getInt("id"));
                                question.setDescription(jsonQuestion.getString("descr"));

                                jsonAnswerOptions = jsonQuestion.getJSONArray("answerOptions");
                                length2 = jsonAnswerOptions.length();

                                for (j = 0; j < length2; j++)
                                {
                                    jsonAnswerOption = jsonAnswerOptions.getJSONObject(j);

                                    answerOption = new AnswerOption(mAppContext);
                                    answerOption.setId(jsonAnswerOption.getInt("id"));
                                    answerOption.setAnswerText(jsonAnswerOption.getString("header"));

                                    ((RadioButtonQuestion) question).addAnswerOption(answerOption);
                                }
                            }
                            else if (Question.QUESTION_TYPE_CHECKBOX == questionType)
                            {
                                question = new CheckboxQuestion(mAppContext);
                                question.setId(jsonQuestion.getInt("id"));
                                question.setDescription(jsonQuestion.getString("descr"));

                                jsonAnswerOptions = jsonQuestion.getJSONArray("answerOptions");
                                length2 = jsonAnswerOptions.length();

                                for (j = 0; j < length2; j++)
                                {
                                    jsonAnswerOption = jsonAnswerOptions.getJSONObject(j);

                                    answerOptionWithStatus = new AnswerOptionWithStatus(mAppContext);
                                    answerOptionWithStatus.setId(jsonAnswerOption.getInt("id"));
                                    answerOptionWithStatus.setAnswerText(jsonAnswerOption.getString("header"));

                                    ((CheckboxQuestion) question).addAnswerOption(answerOptionWithStatus);
                                }
                            }

                            survey.addQuestion(question);
                        }

                        pGetSurveyCallback.done(survey);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG, "fetchSurveyById, JSONException: " + e.toString());
                        survey = null;
                        pGetSurveyCallback.done(survey);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pGetSurveyCallback.done(null);
                }
            }) {
                /**
                 * Passing some request headers
                 * */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            jsonObjectReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            mSingletonRequestQueue.addToRequestQueue(jsonObjectReq);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //url: http://localhost:8080/comuneWS/surveys/getAvailableSurveys
    public void getNewSurveysAvailableForInstitution(final PublicInstitution pInstitution, final GetPublicInstitutionCallbacks pCallback) {
        User user = mUserManager.getCurrentUser();

        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_GET_AVAILABLE_SURVEYS;
        Log.i(TAG, "getNewSurveysAvailableForInstitution, URL: " + url);

        if (user != null)
        {
            try {
                JSONObject jsonObjectParams = new JSONObject();
                jsonObjectParams.put("userId", user.getUserId());
                jsonObjectParams.put("placeId", pInstitution.getId());

                final JsonObjectRequest jsonObjectReq = new JsonObjectRequest(Method.POST, url, jsonObjectParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int i, length;
                        Survey survey;
                        JSONObject jsonObjectSurvey;
                        JSONArray jsonArraySurveys;
                        ArrayList<Survey> availableSurveys;

                        pInstitution.setAvailableSurveysList(null);

                        try {
                            availableSurveys = new ArrayList<>();
                            jsonArraySurveys = response.getJSONArray("surveys");
                            length = jsonArraySurveys.length();

                            for (i = 0; i < length; i++) {
                                jsonObjectSurvey = jsonArraySurveys.getJSONObject(i);

                                survey = new Survey();
                                survey.setId(jsonObjectSurvey.getInt("id"));
                                survey.setPublicInstitutionId(pInstitution.getId());
                                survey.setSource(Survey.NEW_SURVEY_AVAILABLE);
                                survey.setDescription(jsonObjectSurvey.getString("description"));
                                GregorianCalendar createdAt = new GregorianCalendar();
                                createdAt.setTimeInMillis(jsonObjectSurvey.getLong("createdAt"));
                                survey.setAvailableSince(createdAt);

                                availableSurveys.add(survey);
                            }

                            pInstitution.setAvailableSurveysList(availableSurveys);

                            Log.i(TAG, "getNewSurveysAvailableForInstitution: SUCCESS");

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "getNewSurveysAvailableForInstitution: Exception: " + e.getMessage());
                        } finally {
                            pCallback.done(pInstitution);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "getNewSurveysAvailableForInstitution: onErrorResponse: " + error.toString());
                        pInstitution.setAvailableSurveysList(null);
                        pCallback.done(null);
                    }
                }) {
                    /**
                     * Passing some request headers
                     */
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };

                jsonObjectReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                mSingletonRequestQueue.addToRequestQueue(jsonObjectReq);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    //url: http://localhost:8080/comuneWS/surveys/saveSurveyAnswers, enviar objeto JSON EntireQuestion (maven)
    public void uploadSurveyAnswersToServer(Integer userId, Survey pSurvey, final SentSurveyCallback pCallback)
    {
        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_SAVE_SURVEY_ANSWERS;
        Log.i(TAG, "uploadSurveyAnswersToServer, URL: " + url);

        final Integer mUserId = userId;
        final Integer mInstId = pSurvey.getPublicInstitutionId();
        final Integer mSurveyId = pSurvey.getId();

        try
        {
            //User user = mUserManager.getCurrentUser();

            JSONObject jsonObjectSurvey = new JSONObject();
            jsonObjectSurvey.put("userId", mUserId);
            jsonObjectSurvey.put("placeId", mInstId);
            jsonObjectSurvey.put("surveyId", mSurveyId);
            jsonObjectSurvey.put("startedAt", pSurvey.getStartTime().getTimeInMillis());
            jsonObjectSurvey.put("completedAt", pSurvey.getCompletionTime().getTimeInMillis());

            //Log.i(TAG, "jsonObjectSurvey");
            //Log.i(TAG, "userId: " + jsonObjectSurvey.getString("userId") + ", placeId: " + jsonObjectSurvey.getString("placeId") + ", surveyId: " + jsonObjectSurvey.getString("surveyId"));

            JSONObject jsonObjectQuestion;
            JSONArray jsonArrayQuestions = new JSONArray();
            JSONObject jsonObjectAnswerOption;
            JSONArray jsonArrayAnswerOptions;
            ArrayList<AnswerOptionWithStatus> answerOptionsWithStatus;

            //Log.i(TAG, "questions:");
            for (Question question : pSurvey.getQuestions())
            {
                jsonObjectQuestion = new JSONObject();
                jsonObjectQuestion.put("id", question.getId());
                jsonObjectQuestion.put("type", question.getType());

                if (question instanceof YesOrNoQuestion)
                {
                    jsonObjectQuestion.put("answer", ((YesOrNoQuestion) question).getUserAnswer());

                    //Log.i(TAG, "id: " + jsonObjectQuestion.getString("id") + ", type: " + jsonObjectQuestion.getString("type") + ", answer: " + jsonObjectQuestion.getString("answer"));
                }
                else if (question instanceof RatingQuestion)
                {
                    jsonObjectQuestion.put("answer", ((RatingQuestion) question).getUserRating());

                    //Log.i(TAG, "id: " + jsonObjectQuestion.getString("id") + ", type: " + jsonObjectQuestion.getString("type") + ", answer: " + jsonObjectQuestion.getString("answer"));
                }
                else if (question instanceof RadioButtonQuestion)
                {
                    jsonObjectQuestion.put("answerId", ((RadioButtonQuestion) question).getIdUserAnswer());

                    //Log.i(TAG, "id: " + jsonObjectQuestion.getString("id") + ", type: " + jsonObjectQuestion.getString("type") + ", answerId: " + jsonObjectQuestion.getString("answerId"));
                }
                else if (question instanceof CheckboxQuestion)
                {
                    jsonArrayAnswerOptions = new JSONArray();

                    answerOptionsWithStatus = ((CheckboxQuestion) question).getAnswerOptions();
                    for (AnswerOptionWithStatus answerOptionWithStatus : answerOptionsWithStatus)
                    {
                        jsonObjectAnswerOption = new JSONObject();
                        jsonObjectAnswerOption.put("id", answerOptionWithStatus.getId());
                        jsonObjectAnswerOption.put("checked", answerOptionWithStatus.isChecked());

                        jsonArrayAnswerOptions.put(jsonObjectAnswerOption);
                    }

                    jsonObjectQuestion.put("answerOptions", jsonArrayAnswerOptions);
                }
                jsonArrayQuestions.put(jsonObjectQuestion);
            }
            jsonObjectSurvey.put("questions", jsonArrayQuestions);

            //envia objeto json contendo respostas e recebe confirmação
            JsonObjectRequest jsonObjectReq = new JsonObjectRequest(Request.Method.POST, url, jsonObjectSurvey, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    // {"userId":55,"placeId":2,"surveyId":4}
                    int userId = -1;
                    int placeId = -1;
                    int surveyId = -1;

                    try
                    {
                        userId = response.getInt("userId");
                        placeId = response.getInt("placeId");
                        surveyId = response.getInt("surveyId");

                        pCallback.done(userId, placeId, surveyId);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        Log.i(TAG, "uploadSurveyAnswersToServer, JSONException: " + e.toString());

                        pCallback.done(null, null, null);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_CONFLICT) {
                        pCallback.done(mUserId, mInstId, mSurveyId);
                    }
                    else {
                        Log.i(TAG, "uploadSurveyAnswersToServer, onErrorResponse: " + error.toString());
                        pCallback.done(null, null, null);
                    }
                }
            }) {
                /**
                 * Passing some request headers
                 * */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            jsonObjectReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            mSingletonRequestQueue.addToRequestQueue(jsonObjectReq);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

}