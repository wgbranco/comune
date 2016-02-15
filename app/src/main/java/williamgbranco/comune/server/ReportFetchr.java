package williamgbranco.comune.server;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import williamgbranco.comune.callback.DownloadedFileCallbacks;
import williamgbranco.comune.callback.GetReportCallback;
import williamgbranco.comune.callback.GetReportListCallbacks;
import williamgbranco.comune.callback.ResponseAvailableCallbacks;
import williamgbranco.comune.callback.SentReportCallback;
import williamgbranco.comune.callback.UploadedFileCallbacks;
import williamgbranco.comune.database.DatabaseHelper;
import williamgbranco.comune.manager.ReportManager;
import williamgbranco.comune.manager.UserManager;
import williamgbranco.comune.report.Report;
import williamgbranco.comune.report.response.AvailableResponse;
import williamgbranco.comune.server.httpUtility.HttpDownloadUtility;
import williamgbranco.comune.server.httpUtility.MultipartUtility;
import williamgbranco.comune.user.User;
import williamgbranco.comune.util.Constants;
import williamgbranco.comune.util.MediaUtils;

/**
 * Created by William Gomes de Branco on 08/10/2015.
 */
public class ReportFetchr
{
    private static final String TAG = "comune.ReportFetchr";

    private Context mAppContext;
    private UserManager mUserManager;
    private ReportManager mReportManager;
    private DatabaseHelper mHelper;
    private SingletonRequestQueue mSingletonRequestQueue;


    public ReportFetchr(Context mContext)
    {
        mAppContext = mContext.getApplicationContext();
        mUserManager = UserManager.get(mAppContext);
        mReportManager = ReportManager.get(mAppContext);
        mHelper = new DatabaseHelper(mAppContext);
        mSingletonRequestQueue = SingletonRequestQueue.getInstance(mAppContext);
    }


    public void getUserReportById(User pUser, Integer pReportId, final GetReportCallback pCallbacks)
    {
        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_GET_USER_REPORT_BY_ID;
        Log.i(TAG, "getUserReportById, URL: " + url);

        try
        {
            JSONObject jsonObjectParams = new JSONObject();
            jsonObjectParams.put("userId", pUser.getUserId());
            jsonObjectParams.put("reportId", pReportId);

            final JsonObjectRequest jsonObjectReq = new JsonObjectRequest(Request.Method.POST, url, jsonObjectParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    Report report = null;

                    try
                    {
                        report = new Report(mAppContext);
                        report.setId(response.getInt("reportId"));
                        report.setPublicInstitutionId(response.getInt("placeId"));
                        GregorianCalendar madeAt = new GregorianCalendar();
                        madeAt.setTimeInMillis(response.getLong("madeAt"));
                        report.setMadeAt(madeAt);
                        report.setComment(response.getString("comment"));
                        report.setPictureId(response.getInt("idPicture"));
                        report.setFootageId(response.getInt("idFootage"));
                        report.setResponseId(response.getInt("responseId"));
                        report.setResponseVisualized(response.getBoolean("responseVisualized"));

                        report = mReportManager.addFetchedReport(report);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG, "getUserReportById, JSONException: " + e.toString());
                        report = null;
                    }
                    finally {
                        pCallbacks.done(report);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pCallbacks.done(null);
                }
            })
            {
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
                    2,
                    2));

            mSingletonRequestQueue.addToRequestQueue(jsonObjectReq);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void getReportResponseById(User pUser, Integer pResponseId, final GetReportCallback pCallbacks)
    {
        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_GET_REPORT_RESPONSE_BY_ID;
        Log.i(TAG, "getReportResponseById, URL: " + url);

        try
        {
            JSONObject jsonObjectParams = new JSONObject();
            jsonObjectParams.put("userId", pUser.getUserId());
            jsonObjectParams.put("reportId", pResponseId);

            final JsonObjectRequest jsonObjectReq = new JsonObjectRequest(Request.Method.POST, url, jsonObjectParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    Report reportResponse = null;

                    try
                    {
                        reportResponse = new Report(mAppContext);
                        reportResponse.setId(response.getInt("reportId"));
                        reportResponse.setPublicInstitutionId(response.getInt("placeId"));
                        GregorianCalendar madeAt = new GregorianCalendar();
                        madeAt.setTimeInMillis(response.getLong("madeAt"));
                        reportResponse.setMadeAt(madeAt);
                        reportResponse.setComment(response.getString("comment"));

                        reportResponse = mReportManager.addFetchedReport(reportResponse);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG, "getReportResponseById, JSONException: " + e.toString());
                        reportResponse = null;
                    }
                    finally {
                        pCallbacks.done(reportResponse);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pCallbacks.done(null);
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
                    2,
                    2));

            mSingletonRequestQueue.addToRequestQueue(jsonObjectReq);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void getPlaceReportsSubmittedByUser(User pUser, Integer pInstId, final GetReportListCallbacks pCallbacks)
    {
        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_GET_PLACE_REPORTS_SUBMITTED_BY_USER;
        Log.i(TAG, "getPlaceReportsSubmittedByUser, URL: " + url);

        try
        {
            JSONObject jsonObjectParams = new JSONObject();
            jsonObjectParams.put("userId", pUser.getUserId());
            jsonObjectParams.put("placeId", pInstId);
            Log.i(TAG, "getPlaceReportsSubmittedByUser, userId: " + pUser.getUserId());
            Log.i(TAG, "getPlaceReportsSubmittedByUser, placeId: " + pInstId);


            final JsonArrayRequest jsonArrayReq = new JsonArrayRequest(Request.Method.POST, url, jsonObjectParams, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response)
                {
                    int i, length;
                    Report report;
                    ArrayList<Report> reports = new ArrayList<>();
                    JSONObject jsonObjectReport;

                    try
                    {
                        length = response.length();
                        Log.i(TAG, "getPlaceReportsSubmittedByUser, array response length: " + length);

                        for (i = 0; i < length; i++)
                        {
                            jsonObjectReport = response.getJSONObject(i);

                            report = new Report(mAppContext);
                            report.setId(jsonObjectReport.getInt("reportId"));
                            report.setPublicInstitutionId(jsonObjectReport.getInt("placeId"));
                            GregorianCalendar madeAt = new GregorianCalendar();
                            madeAt.setTimeInMillis(jsonObjectReport.getLong("madeAt"));
                            report.setMadeAt(madeAt);
                            report.setComment(jsonObjectReport.getString("comment"));
                            report.setPictureId(jsonObjectReport.getInt("idPicture"));
                            report.setFootageId(jsonObjectReport.getInt("idFootage"));
                            report.setResponseId(jsonObjectReport.getInt("responseId"));
                            report.setResponseVisualized(jsonObjectReport.getBoolean("responseVisualized"));

                            reports.add(report);
                        }

                        Log.i(TAG, "getPlaceReportsSubmittedByUser: reports.size() = " + reports.size());

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG, "getPlaceReportsSubmittedByUser, JSONException: " + e.toString());
                        reports = null;
                    }
                    finally
                    {
                        Log.i(TAG, "getPlaceReportsSubmittedByUser, finally: " + reports);

                        pCallbacks.done(reports);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG, "getPlaceReportsSubmittedByUser, error response: " + error.toString());

                    pCallbacks.done(null);
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

            jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                    2,
                    2));

            mSingletonRequestQueue.addToRequestQueue(jsonArrayReq);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void getNewResponsesForUserReports(Integer pUserId, final ResponseAvailableCallbacks pCallbacks)
    {
        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_GET_NEW_RESPONSES_FOR_USER_REPORTS;
        Log.i(TAG, "getNewResponsesForUserReports, URL: " + url);

        try
        {
            JSONObject jsonObjectParams = new JSONObject();
            jsonObjectParams.put("id", pUserId);

            final JsonArrayRequest jsonArrayReq = new JsonArrayRequest(Request.Method.POST, url, jsonObjectParams, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response)
                {
                    int i, length, placeId, reportId, responseId;
                    AvailableResponse availableResponse;
                    ArrayList<AvailableResponse> availableResponses = new ArrayList<>();
                    JSONObject jsonObjectAvailableResponse;

                    try
                    {
                        length = response.length();
                        for (i = 0; i < length; i++)
                        {
                            jsonObjectAvailableResponse = response.getJSONObject(i);

                            placeId = jsonObjectAvailableResponse.getInt("placeId");
                            reportId = jsonObjectAvailableResponse.getInt("reportId");
                            responseId = jsonObjectAvailableResponse.getInt("responseId");

                            availableResponse = new AvailableResponse(placeId, reportId, responseId);

                            availableResponses.add(availableResponse);
                        }

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG, "getNewResponsesForUserReports, JSONException: " + e.toString());
                        availableResponses = null;
                    }
                    finally
                    {
                        pCallbacks.done(availableResponses);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pCallbacks.done(null);
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

            jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                    2,
                    2));

            mSingletonRequestQueue.addToRequestQueue(jsonArrayReq);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void markResponseAsVisualized(int userId, int reportId, int responseId)
    {
        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_MARK_RESPONSE_AS_VISUALIZED;
        Log.i(TAG, "markResponseAsVisualized, URL: " + url);

        try
        {
            JSONObject jsonObjectParams = new JSONObject();
            jsonObjectParams.put("userId", userId);
            jsonObjectParams.put("reportId", reportId);
            jsonObjectParams.put("responseId", responseId);

            final JsonObjectRequest jsonObjectReq = new JsonObjectRequest(Request.Method.POST, url, jsonObjectParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(TAG, "markResponseAsVisualized responded");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "markResponseAsVisualized, error:" + error.toString());
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
                    2,
                    2));

            mSingletonRequestQueue.addToRequestQueue(jsonObjectReq);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //url: http://localhost:8080/comuneWS/reports/saveNewReport, envia objeto JSON UserReport (maven)
    public void uploadReportToServer(final Report pReport, final SentReportCallback pCallback)
    {
        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_SAVE_NEW_REPORT;
        Log.i(TAG, "uploadReportToServer, URL: " + url);

        try
        {
            //final User user = mUserManager.getCurrentUser();

            JSONObject jsonObjectReport = new JSONObject();
            jsonObjectReport.put("userId", pReport.getUserId());
            jsonObjectReport.put("placeId", pReport.getPublicInstitutionId());
            jsonObjectReport.put("comment", pReport.getComment());
            jsonObjectReport.put("madeAt", pReport.madeAt().getTimeInMillis());

            /*final int mUserId = user.getUserId();
            final int mPlaceId = pReport.getPublicInstitutionId();
            final int mReportId = pReport.getId();*/

            //envia objeto json contendo relato e recebe confirmação
            JsonObjectRequest jsonObjectReq = new JsonObjectRequest(Request.Method.POST, url, jsonObjectReport, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    try
                    {
                        int userId = response.getInt("userId");
                        int placeId = response.getInt("placeId");
                        int reportId = response.getInt("reportId");

                        pCallback.done(userId, placeId, reportId);

                    }
                    catch (JSONException e)
                    {
                        Log.i(TAG, "uploadReportToServer, JSONException (onResponse): " + e.toString());
                        pCallback.done(pReport.getUserId(), pReport.getPublicInstitutionId(), null);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Log.i(TAG, "uploadReportToServer, onErrorResponse: " + error.toString());
                    pCallback.done(null, null, null);
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
                    2,
                    2));

            mSingletonRequestQueue.addToRequestQueue(jsonObjectReq);

        }
        catch (JSONException e)
        {
            Log.i(TAG, "uploadReportToServer, JSONException: " + e.toString());
        }
    }

    //url: http://localhost:8080/comuneWS/reports/uploadReportPicture
    public void uploadReportPicture(Report pReport, UploadedFileCallbacks pCallbacks)
    {
        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_UPLOAD_REPORT_PICTURE;
        Log.i(TAG, "uploadReportPicture, URL: " + url);

        if ((pReport != null) && (pReport.getPictureUri() != null))
        {
            String charset = "UTF-8";
            File file = new File(pReport.getPictureUri().getPath());

            try {
                MultipartUtility multipart = new MultipartUtility(url, charset, file.length());

                /*multipart.addHeaderField("User-Agent", "CodeJava");
                multipart.addHeaderField("Test-Header", "Header-Value");*/

                multipart.addFormField("reportId", String.valueOf(pReport.getIdOnServer()));
                multipart.addFilePart("file", file);

                String response = multipart.finish();
                Log.i(TAG, "uploadReportPicture, SERVER REPLIED:" + response);

                JSONObject jsonResponse = new JSONObject(response);
                int ownerId = jsonResponse.getInt("ownerId");
                int fileId = jsonResponse.getInt("fileId");
                String filename = jsonResponse.getString("fileName");

                pCallbacks.done(ownerId, fileId, filename);
            }
            catch (IOException e) {
                Log.i(TAG, "uploadReportPicture, IOException: " + e.toString());
                pCallbacks.done(null, null, null);
            }
            catch (JSONException e) {
                Log.i(TAG, "uploadReportPicture, JSONException: " + e.toString());
                pCallbacks.done(null, null, null);
            }
        }
    }


    //url: http://localhost:8080/comuneWS/reports/uploadReportFootage
    public void uploadReportFootage(Report pReport, UploadedFileCallbacks pCallbacks)
    {
        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_UPLOAD_REPORT_FOOTAGE;
        Log.i(TAG, "uploadReportFootage, URL: " + url);

        if ((pReport != null) && (pReport.getFootageUri() != null))
        {
            Log.i(TAG, "pReport.getFootageUri(): " + pReport.getFootageUri());
            Log.i(TAG, "pReport.getFootageUri().getPath(): " + pReport.getFootageUri().getPath());
            Log.i(TAG, "pReport.getFootageUri() REAL PATH: " + MediaUtils.getRealPathFromURI(mAppContext, pReport.getFootageUri()));
            Log.i(TAG, "pReport.getFootagePath(): " + pReport.getFootagePath());
            pReport.setFootagePath(MediaUtils.getRealPathFromURI(mAppContext, pReport.getFootageUri()));
            String charset = "UTF-8";
            File file = new File(pReport.getFootagePath());
            Log.i(TAG, "pReport.getFootagePath(): "+pReport.getFootagePath());

            try {
                MultipartUtility multipart = new MultipartUtility(url, charset, file.length());

                /*multipart.addHeaderField("User-Agent", "CodeJava");
                multipart.addHeaderField("Test-Header", "Header-Value");*/

                multipart.addFormField("reportId", String.valueOf(pReport.getIdOnServer()));
                multipart.addFilePart("file", file);

                String response = multipart.finish();
                Log.i(TAG, "uploadReportFootage, SERVER REPLIED:" + response);

                JSONObject jsonResponse = new JSONObject(response);
                int ownerId = jsonResponse.getInt("ownerId");
                int fileId = jsonResponse.getInt("fileId");
                String filename = jsonResponse.getString("fileName");

                pCallbacks.done(ownerId, fileId, filename);
            }
            catch (IOException ex) {
                Log.i(TAG, "uploadReportFootage, IOException: " + ex.toString());
                pCallbacks.done(null, null, null);
            }
            catch (JSONException e) {
                Log.i(TAG, "uploadReportFootage, JSONException: " + e.toString());
                pCallbacks.done(null, null, null);
            }
        }
    }


    //url: http://localhost:8080/comuneWS/reports/downloadReportPicture
    public void downloadReportPicture(Report report, String directory, DownloadedFileCallbacks pCallbacks)
    {
        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_DOWNLOAD_REPORT_PICTURE;

        if ((report != null) && (report.getPictureId() != -1))
        {
            url = url + "?id=" + report.getPictureId();

            try
            {
                HttpDownloadUtility.downloadFile(Constants.CODE_DOWNLOAD_PICTURE, url, directory, pCallbacks);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else
        {
            pCallbacks.done(null, null, -2, null, null);
        }
    }


    //url: http://localhost:8080/comuneWS/reports/downloadReportFootage
    public void downloadReportFootage(Report report, String directory, DownloadedFileCallbacks pCallbacks)
    {
        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_DOWNLOAD_REPORT_FOOTAGE;

        if ((report != null) && (report.getFootageId() != -1))
        {
            url = url + "?id=" + report.getFootageId();

            try
            {
                HttpDownloadUtility.downloadFile(Constants.CODE_DOWNLOAD_FOOTAGE, url, directory, pCallbacks);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else
        {
            pCallbacks.done(null, null, -2, null, null);
        }
    }

}
