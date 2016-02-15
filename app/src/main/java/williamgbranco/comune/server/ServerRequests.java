package williamgbranco.comune.server;


import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import williamgbranco.comune.callback.AuthorizeUserCallback;
import williamgbranco.comune.callback.DownloadedFileCallbacks;
import williamgbranco.comune.callback.GetInstitutionListCallbacks;
import williamgbranco.comune.callback.GetPublicInstitutionCallbacks;
import williamgbranco.comune.callback.GetReportCallback;
import williamgbranco.comune.callback.GetReportListCallbacks;
import williamgbranco.comune.callback.GetSurveyCallback;
import williamgbranco.comune.callback.GetUserCallback;
import williamgbranco.comune.callback.ResponseAvailableCallbacks;
import williamgbranco.comune.callback.SentReportCallback;
import williamgbranco.comune.callback.SentSurveyCallback;
import williamgbranco.comune.callback.UploadedFileCallbacks;
import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.manager.PublicInstitutionManager;
import williamgbranco.comune.manager.ReportManager;
import williamgbranco.comune.manager.UserManager;
import williamgbranco.comune.report.Report;
import williamgbranco.comune.survey.Survey;
import williamgbranco.comune.user.User;
import williamgbranco.comune.util.Constants;


/**
 * Created by William Gomes de Branco on 07/09/2015.
 */
public class ServerRequests
{
    public static final String TAG = "comune.ServerRequests";

    private Context mAppContext;
    private PublicInstitutionManager mInstitutionManager;
    private ReportManager mReportManager;


    public ServerRequests(Context pContext)
    {
        if (pContext != null) {
            mAppContext = pContext.getApplicationContext();
            mInstitutionManager = PublicInstitutionManager.get(mAppContext);
            mReportManager = ReportManager.get(mAppContext);
        }
    }


    /*
    *  NETWORK related functions
    * ***********************************************************************/

    public boolean isNetworkAvailable()
    {
        boolean isNetworkAvailable = false;

        if (mAppContext != null)
        {
            ConnectivityManager cm = (ConnectivityManager) mAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            isNetworkAvailable = (cm.getBackgroundDataSetting() && (cm.getActiveNetworkInfo() != null));
        }

        Log.i(TAG, "network available: " + isNetworkAvailable);

        return isNetworkAvailable;
    }


    /*
    *  User Related Tasks
    * ***********************************************************************/

    public void fetchUserInfoInBackground(String email, String password, AuthorizeUserCallback pCallbacks)
    {
        if (isNetworkAvailable()) {
            new UserInfoFetchr (mAppContext).authenticateUser(email, password, pCallbacks);
        } else {
            pCallbacks.onNetworkNonAvailable();
        }
    }

    /************************************************************************/

    public void registerNewUser(User newUser, GetUserCallback pCallbacks)
    {
        if (isNetworkAvailable())
        {
            new UserInfoFetchr(mAppContext).registerUser(newUser, pCallbacks);
        } else {
            pCallbacks.onNetworkNonAvailable();
        }
    }

    /************************************************************************/

    public void uploadUserPhoto(User pUser, UploadedFileCallbacks pCallbacks)
    {
        if (isNetworkAvailable())
        {
            new UploadUserPhotoAsyncTask(pUser, pCallbacks).execute();
        }
        else
        {
            pCallbacks.done(null, null, null);
        }
    }

    private class UploadUserPhotoAsyncTask extends AsyncTask<Void, Void, Void>
    {
        User mUser;
        UploadedFileCallbacks mCallbacks;

        public UploadUserPhotoAsyncTask(User pUser, UploadedFileCallbacks pCallbacks) {
            mUser = pUser;
            mCallbacks = pCallbacks;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            new UserInfoFetchr(mAppContext).uploadUserPhoto(mUser, mCallbacks);
            return null;
        }
    }

    /************************************************************************/

    public void downloadUserPhoto(User pUser, String directory, DownloadedFileCallbacks pCallbacks)
    {
        if (isNetworkAvailable())
        {
            new DownloadUserPhotoAsyncTask(pUser, directory, pCallbacks).execute();
        }
        else
        {
            pCallbacks.done(null, null, -1, null, null);
        }
    }

    private class DownloadUserPhotoAsyncTask extends AsyncTask<Void, Void, Void>
    {
        User mUser;
        String mDir;
        DownloadedFileCallbacks mCallbacks;

        public DownloadUserPhotoAsyncTask(User pUser, String pDir, DownloadedFileCallbacks pCallbacks) {
            mUser = pUser;
            mDir = pDir;
            mCallbacks = pCallbacks;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            new UserInfoFetchr(mAppContext).downloadUserPhoto(mUser, mDir, mCallbacks);
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
        }
    }


    /*
    *  Survey Related Tasks
    * ***********************************************************************/

    public void fetchEntireSurveyOnServerById(Integer pInstId, Integer pSurveyId, GetSurveyCallback pGetSurveyCallback)
    {
        if (isNetworkAvailable()) {
            //new FetchEntireSurveyByIdTask(pInstId, pSurveyId, pGetSurveyCallback).execute();
            new SurveyFetchr(mAppContext.getApplicationContext()).fetchSurveyById(pInstId, pSurveyId, pGetSurveyCallback);
        } else
        {
            pGetSurveyCallback.done(null);
        }
    }

    public void fetchNewSurveysAvailableForInstitution(PublicInstitution pInstitution, GetPublicInstitutionCallbacks pGetSurveyCallback)
    {
        if (isNetworkAvailable())
        {
            new SurveyFetchr(mAppContext.getApplicationContext()).getNewSurveysAvailableForInstitution(pInstitution, pGetSurveyCallback);
        }
        else {
            pGetSurveyCallback.done(null);
        }
    }


    public void uploadSurveyAnswersToServer(Integer userId, Survey pSurvey, SentSurveyCallback pCallback)
    {
        if (isNetworkAvailable())
        {
            new SurveyFetchr(mAppContext).uploadSurveyAnswersToServer(userId, pSurvey, pCallback);
        }
        else
        {
            pCallback.onNetworkNonAvailable();
        }
    }


     /*
    *  Intitution Related Tasks
    * ***********************************************************************/

    public void fetchPublicInstitutionById(Integer pInstId, GetPublicInstitutionCallbacks pCallbacks)
    {
        if (isNetworkAvailable()) {
            new PublicInstitutionFetchr(mAppContext).getPlaceById(pInstId, pCallbacks);
        } else {
            pCallbacks.done(null);
        }
    }

    /************************************************************************/

    public void fetchPublicInstitutionsAroundUser(Location pLocation, Integer pRadius, GetInstitutionListCallbacks pCallbacks) {
        if (isNetworkAvailable())
        {
            new PublicInstitutionFetchr(mAppContext).getNearbyPlaces(pLocation, pRadius, pCallbacks);
        } else {
            pCallbacks.done(null);
        }
    }

    /************************************************************************/
    public void findPublicInstitutionsAroundUser(Location pLocation, Integer pInstType, Integer pRadius, boolean pHighGrades, GetInstitutionListCallbacks pCallbacks)
    {
        if (isNetworkAvailable()) {
            new PublicInstitutionFetchr(mAppContext).findNearbyPlaces(pLocation, pRadius, pInstType, pHighGrades, pCallbacks);
        }
        else
        {
            pCallbacks.done(null);
        }
    }

    /************************************************************************/

    public void fetchInstitutionsWithReportsSubmittedByUser(User pUser, GetInstitutionListCallbacks pCallbacks)
    {
        if (isNetworkAvailable())
        {
            new PublicInstitutionFetchr(mAppContext).getPlacesReportedByUser(pUser, pCallbacks);
        } else {
            pCallbacks.done(null);
        }
    }


    /*
    *  Report Related Tasks
    * ***********************************************************************/

    public void fetchReportsSubmittedForInstitution(User pUser, Integer pInstID, GetReportListCallbacks pCallbacks)
    {
        if (isNetworkAvailable()) {
            new ReportFetchr(mAppContext).getPlaceReportsSubmittedByUser(pUser, pInstID, pCallbacks);
        } else {
            pCallbacks.done(null);
        }
    }

    /************************************************************************/

    public void fetchReportById(User pUser, Integer pReportId, GetReportCallback pCallbacks)
    {
        if (isNetworkAvailable())
        {
            new ReportFetchr(mAppContext).getUserReportById(pUser, pReportId, pCallbacks);
        }
        else
        {
            pCallbacks.done(null);
        }
    }

    /************************************************************************/

    public void fetchReportResponseById(User pUser, Integer pResponseId, GetReportCallback pCallbacks)
    {
        if (isNetworkAvailable())
        {
            new ReportFetchr(mAppContext).getReportResponseById(pUser, pResponseId, pCallbacks);
        }
        else
        {
            pCallbacks.done(null);
        }
    }

    /************************************************************************/

    public void checkResponsesAvailable(int userId, ResponseAvailableCallbacks pCallbacks)
    {
        //int userId = UserManager.get(mAppContext).getCurrentUser().getUserId();

        if (isNetworkAvailable())
        {
            new ReportFetchr(mAppContext).getNewResponsesForUserReports(userId, pCallbacks);
        }
        else
        {
            pCallbacks.onNetworkNonAvailable();
        }
    }

    /************************************************************************/

    public void markResponseAsVisualized(int reportId, int responseId)
    {
        if (isNetworkAvailable()) {
            int userId = UserManager.get(mAppContext).getCurrentUser().getUserId();

            new ReportFetchr(mAppContext).markResponseAsVisualized(userId, reportId, responseId);
        }
    }

    /************************************************************************/

    public void uploadReportToServer(Report pReport, SentReportCallback pCallback)
    {
        if (isNetworkAvailable()) {
            new ReportFetchr(mAppContext).uploadReportToServer(pReport, pCallback);
        } else {
            pCallback.onNetworkNonAvailable();
        }
    }

    /************************************************************************/

    public void uploadReportFootage(Report pReport, UploadedFileCallbacks pCallbacks)
    {
        if (isNetworkAvailable()) {
            new UploadReportFootageAsyncTask(pReport, pCallbacks).execute();
        } else {
            pCallbacks.onNetworkNonAvailable();
        }
    }

    private class UploadReportFootageAsyncTask extends AsyncTask<Void, Void, Void>
    {
        Report mReport;
        UploadedFileCallbacks mCallbacks;

        public UploadReportFootageAsyncTask(Report pReport, UploadedFileCallbacks pCallbacks) {
            mReport = pReport;
            mCallbacks = pCallbacks;
        }

        @Override
        protected Void doInBackground(Void... params) {
            new ReportFetchr(mAppContext).uploadReportFootage(mReport, mCallbacks);
            return null;
        }
    }

    /************************************************************************/

    public void uploadReportPicture(Report pReport, UploadedFileCallbacks pCallbacks)
    {
        if (isNetworkAvailable()) {
            new UploadReportPictureAsyncTask(pReport, pCallbacks).execute();
        } else {
            pCallbacks.done(null, null, null);
        }
    }

    private class UploadReportPictureAsyncTask extends AsyncTask<Void, Void, Void>
    {
        Report mReport;
        UploadedFileCallbacks mCallbacks;

        public UploadReportPictureAsyncTask(Report pReport, UploadedFileCallbacks pCallbacks) {
            mReport = pReport;
            mCallbacks = pCallbacks;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            new ReportFetchr(mAppContext).uploadReportPicture(mReport, mCallbacks);
            return null;
        }
    }

    /************************************************************************/

    public void downloadReportPicture(Report pReport, String directory, DownloadedFileCallbacks pCallbacks)
    {
        if (isNetworkAvailable())
        {
            new DownloadReportFileAsyncTask(Constants.CODE_DOWNLOAD_PICTURE, pReport, directory, pCallbacks).execute();
        }
        else
        {
            pCallbacks.done(null, null, -1, null, null);
        }
    }

    public void downloadReportFootage(Report pReport, String directory, DownloadedFileCallbacks pCallbacks)
    {
        if (isNetworkAvailable())
        {
            new DownloadReportFileAsyncTask(Constants.CODE_DOWNLOAD_FOOTAGE, pReport, directory, pCallbacks).execute();
        }
        else
        {
            pCallbacks.done(null, null, -1, null, null);
        }
    }

    private class DownloadReportFileAsyncTask extends AsyncTask<Void, Void, Void>
    {
        int mCode;
        Report mReport;
        String mDir;
        DownloadedFileCallbacks mCallbacks;

        public DownloadReportFileAsyncTask(int pCode, Report pReport, String pDir, DownloadedFileCallbacks pCallbacks) {
            mCode = pCode;
            mReport = pReport;
            mDir = pDir;
            mCallbacks = pCallbacks;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            if (Constants.CODE_DOWNLOAD_PICTURE == mCode)
            {
                new ReportFetchr(mAppContext).downloadReportPicture(mReport, mDir, mCallbacks);
            }
            else if (Constants.CODE_DOWNLOAD_FOOTAGE == mCode)
            {
                new ReportFetchr(mAppContext).downloadReportFootage(mReport, mDir, mCallbacks);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
        }
    }


}