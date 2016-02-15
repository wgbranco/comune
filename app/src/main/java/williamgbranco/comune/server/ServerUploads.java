package williamgbranco.comune.server;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import williamgbranco.comune.R;
import williamgbranco.comune.callback.SentReportCallback;
import williamgbranco.comune.callback.SentSurveyCallback;
import williamgbranco.comune.callback.UploadedFileCallbacks;
import williamgbranco.comune.database.DatabaseHelper;
import williamgbranco.comune.manager.ReportManager;
import williamgbranco.comune.manager.SurveyManager;
import williamgbranco.comune.report.Report;
import williamgbranco.comune.survey.Survey;
import williamgbranco.comune.util.Constants;

/**
 * Created by William Gomes de Branco on 04/10/2015.
 */
public class ServerUploads
{
    public static final String TAG = "ServerUploads";

    public static final String ACTION_REPORT_COMMENT_SENT_TO_SERVER = "williamgbranco.comune.ACTION_REPORT_COMMENT_SENT_TO_SERVER";
    public static final String ACTION_REPORT_PICTURE_SENT_TO_SERVER = "williamgbranco.comune.ACTION_REPORT_PICTURE_SENT_TO_SERVER";
    public static final String ACTION_REPORT_FOOTAGE_SENT_TO_SERVER = "williamgbranco.comune.ACTION_REPORT_FOOTAGE_SENT_TO_SERVER";

    private Context mAppContext;
    private SurveyManager mSurveyManager;
    private ReportManager mReportManager;
    private DatabaseHelper mHelper;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    private Report mCurrentReport;
    private SentReportCallback mCurrentReportSuperCallback;

    private BroadcastReceiver onReportCommentSentToServer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            mAppContext.unregisterReceiver(onReportCommentSentToServer);

            Log.i(TAG, "onReportCommentSentToServer received");

            sendAttachedFiles(mCurrentReportSuperCallback);
        }
    };

    private BroadcastReceiver onReportPictureSentToServer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            mAppContext.unregisterReceiver(onReportPictureSentToServer);

            Log.i(TAG, "onReportPictureSentToServer received");

            sendAttachedFiles(mCurrentReportSuperCallback);
        }
    };

    private BroadcastReceiver onReportFootageSentToServer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            mAppContext.unregisterReceiver(onReportFootageSentToServer);

            Log.i(TAG, "onReportFootageSentToServer received");

            sendAttachedFiles(mCurrentReportSuperCallback);
        }
    };


    public ServerUploads(Context pContext)
    {
        mAppContext = pContext.getApplicationContext();
        //mSurveyManager = SurveyManager.get(mAppContext);
        mSurveyManager = new SurveyManager(mAppContext);
        mReportManager = ReportManager.get(mAppContext);
        mHelper = new DatabaseHelper(mAppContext);
    }


    /*
    *  SURVEY related functions
    * ***********************************************************************/

    public void sendCompleteSurveyToSeverOnAtATime(final SentSurveyCallback pCallback)
    {
        Integer userId = null;
        Integer instId = null;
        Integer surveyId = null;

        try {
            DatabaseHelper.InstitutionSurveyCursor instSurveyCursor = mHelper.queryCompleteSurveys();
            Survey survey = null;

            Log.i(TAG, "number of complete surveys: " + instSurveyCursor.getCount());

            /*//---------------------------
            Resources r = mAppContext.getResources();
            Notification notification = new NotificationCompat.Builder(mAppContext)
                    .setTicker(r.getString(R.string.surveys_answered_to_be_sent_title))
                    .setSmallIcon(R.drawable.ic_stat_comune)
                    .setContentTitle(r.getString(R.string.surveys_answered_to_be_sent_title))
                    .setContentText(r.getString(R.string.survey_expiring_soon_text))
                    .setAutoCancel(true)
                    .build();

            NotificationManager manager = (NotificationManager) mAppContext.getSystemService(mAppContext.NOTIFICATION_SERVICE);
            manager.notify(0, notification);
            //---------------------------*/

            instSurveyCursor.moveToFirst();
            if (!instSurveyCursor.isAfterLast())
            {
                userId = instSurveyCursor.getUserId();
                instId = instSurveyCursor.getInstitutionId();
                surveyId = instSurveyCursor.getSurveyId();

                survey = mSurveyManager.queryEntireSurveyForInstitution(userId, instId, surveyId, false);
            }
            instSurveyCursor.close();

            if ((survey != null) && (survey.isComplete())) {
                Log.i(TAG, "survey != null");

                //----------------------------------------------
                mNotifyManager =
                        (NotificationManager) mAppContext.getSystemService(Context.NOTIFICATION_SERVICE);
                mBuilder = new NotificationCompat.Builder(mAppContext);
                mBuilder.setContentTitle("Sincronizando...")
                        .setContentText("Enviando respostas para o servidor")
                        .setSmallIcon(R.drawable.ic_stat_comune);
                mBuilder.setProgress(0, 0, true);
                mNotifyManager.notify(0, mBuilder.build());
                //---------------------------------------------

                mSurveyManager.uploadSurveyAnswersToServer(userId, survey, new SentSurveyCallback() {
                    @Override
                    public void done(Integer userId, Integer instId, Integer surveyId) {

                        //----------------------------------------------
                        mBuilder.setContentTitle("Sincronização finalizada!")
                                .setContentText("Suas respostas foram salvas com sucesso.");
                        mBuilder.setProgress(0, 0, false);
                        mNotifyManager.notify(0, mBuilder.build());
                        //----------------------------------------------

                        pCallback.done(userId, instId, surveyId);
                    }

                    @Override
                    public void onNetworkNonAvailable()
                    {
                        if (mNotifyManager != null) mNotifyManager.cancel(0);
                        pCallback.onNetworkNonAvailable();
                    }

                    @Override
                    public void errorQueryingSurvey(Integer userId, Integer instId, Integer surveyId)
                    {
                        if (mNotifyManager != null) mNotifyManager.cancel(0);
                        pCallback.errorQueryingSurvey(userId, instId, surveyId);
                    }
                });
            } else {
                pCallback.errorQueryingSurvey(userId, instId, surveyId);
            }
        } catch (Exception e) {Log.e(TAG, e.toString());}
    }


    /*
    *  REPORT related functions
    * ***********************************************************************/

    private void sendReportComment(final SentReportCallback pSuperCallback)
    {
        Log.i(TAG, "sendReportComment");

        //----------------------------------------------
        mNotifyManager =
                (NotificationManager) mAppContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mAppContext);
        mBuilder.setContentTitle("Sincronizando...")
                .setContentText("Enviando relato")
                .setSmallIcon(R.drawable.ic_stat_comune);
        mBuilder.setProgress(0, 0, true);
        mNotifyManager.notify(1, mBuilder.build());
        //---------------------------------------------

        mReportManager.uploadReportToServer(mCurrentReport, new SentReportCallback() {
            @Override
            public void done(Integer userId, Integer instId, Integer reportId)
            {
                if (mNotifyManager != null) mNotifyManager.cancel(1);

                try {
                    if ((userId != null) && (instId != null) && (reportId != null)) {
                        mCurrentReport.setIdOnServer(reportId);
                        mCurrentReport.setCommentSentToServer(true);
                        int result = mHelper.updateReportCommentServerStatus(mCurrentReport);

                        if (result != -1) {
                            mAppContext.sendBroadcast(new Intent(ACTION_REPORT_COMMENT_SENT_TO_SERVER), Constants.PERM_PRIVATE);
                        }
                    } else {
                        pSuperCallback.done(null, null, null);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onNetworkNonAvailable()
            {
                if (mNotifyManager != null) mNotifyManager.cancel(1);
                pSuperCallback.onNetworkNonAvailable();
            }
        });
    }

    private void sendReportPicture(final SentReportCallback pSuperCallback)
    {
        Log.i(TAG, "sendReportPicture");

        //----------------------------------------------
        mNotifyManager =
                (NotificationManager) mAppContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mAppContext);
        mBuilder.setContentTitle("Sincronizando...")
                .setContentText("Enviando imagem")
                .setSmallIcon(R.drawable.ic_stat_comune);
        mBuilder.setProgress(0, 0, true);
        mNotifyManager.notify(1, mBuilder.build());
        //---------------------------------------------

        mReportManager.uploadReportPicture(mCurrentReport, new UploadedFileCallbacks() {
            @Override
            public void done(Integer ownerId, Integer fileId, String fileName) {
                if (mNotifyManager != null) mNotifyManager.cancel(1);

                try {
                    if ((ownerId != null) && (fileId != null) && (fileName != null)) {
                        mCurrentReport.setPictureSentToServer(true);
                        int result = mHelper.updateReportPictureServerStatus(mCurrentReport);

                        if (result != -1) {
                            mAppContext.sendBroadcast(new Intent(ACTION_REPORT_PICTURE_SENT_TO_SERVER), Constants.PERM_PRIVATE);
                        }
                    }
                    else
                    {
                        pSuperCallback.done(null, null, null);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onNetworkNonAvailable()
            {
                if (mNotifyManager != null) mNotifyManager.cancel(1);
                pSuperCallback.onNetworkNonAvailable();
            }
        });
    }

    private void sendReportVideo(final SentReportCallback pSuperCallback)
    {
        Log.i(TAG, "sendReportVideo");

        //----------------------------------------------
        mNotifyManager =
                (NotificationManager) mAppContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mAppContext);
        mBuilder.setContentTitle("Sincronizando...")
                .setContentText("Enviando vídeo")
                .setSmallIcon(R.drawable.ic_stat_comune);
        mBuilder.setProgress(0, 0, true);
        mNotifyManager.notify(1, mBuilder.build());
        //----------------------------------------------

        mReportManager.uploadReportFootage(mCurrentReport, new UploadedFileCallbacks() {
            @Override
            public void done(Integer ownerId, Integer fileId, String fileName)
            {
                if (mNotifyManager != null) mNotifyManager.cancel(1);

                try {
                    if ((ownerId != null) && (fileId != null) && (fileName != null)) {
                        mCurrentReport.setFootageSentToServer(true);
                        int result = mHelper.updateReportFootageServerStatus(mCurrentReport);

                        if (result != -1) {
                            mAppContext.sendBroadcast(new Intent(ACTION_REPORT_FOOTAGE_SENT_TO_SERVER), Constants.PERM_PRIVATE);
                        }
                    } else {
                        pSuperCallback.done(null, null, null);
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onNetworkNonAvailable()
            {
                if (mNotifyManager != null) mNotifyManager.cancel(1);

                pSuperCallback.onNetworkNonAvailable();
            }
        });
    }

    private void sendAttachedFiles(SentReportCallback pSuperCallback)
    {
        Log.i(TAG, "sendAttachedFiles");

        if ((mCurrentReport.getPictureUri() != null) && (!mCurrentReport.isPictureSentToServer()))
        {
            sendReportPicture(pSuperCallback);
        }
        else
        {
            if ((mCurrentReport.getFootageUri() != null) && (!mCurrentReport.isFootageSentToServer())) {
                sendReportVideo(pSuperCallback);
            }
            else
            {
                //----------------------------------------------
                mBuilder.setContentTitle("Sincronização finalizada!")
                        .setContentText("Relato salvo com sucesso.")
                        .setSmallIcon(R.drawable.ic_stat_comune);
                mBuilder.setProgress(0, 0, false);
                mNotifyManager.notify(1, mBuilder.build());
                //----------------------------------------------

                pSuperCallback.done(mCurrentReport.getUserId(), mCurrentReport.getPublicInstitutionId(), mCurrentReport.getId());
            }
        }
    }

    public void sendReportsToSeverOneAtATime(final SentReportCallback pSuperCallback)
    {
        mNotifyManager = (NotificationManager) mAppContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mAppContext);

        IntentFilter filter = new IntentFilter(ACTION_REPORT_COMMENT_SENT_TO_SERVER);
        mAppContext.registerReceiver(onReportCommentSentToServer, filter, Constants.PERM_PRIVATE, null);

        filter = new IntentFilter(ACTION_REPORT_PICTURE_SENT_TO_SERVER);
        mAppContext.registerReceiver(onReportPictureSentToServer, filter, Constants.PERM_PRIVATE, null);

        filter = new IntentFilter(ACTION_REPORT_FOOTAGE_SENT_TO_SERVER);
        mAppContext.registerReceiver(onReportFootageSentToServer, filter, Constants.PERM_PRIVATE, null);

        DatabaseHelper.ReportCursor reportCursor = mHelper.queryReportsMade();
        Log.i(TAG, "number of reports made: " + reportCursor.getCount());

        reportCursor.moveToFirst();
        if (!reportCursor.isAfterLast())
        {
            mCurrentReport = reportCursor.getReport();
            mCurrentReportSuperCallback = pSuperCallback;

            if (!mCurrentReport.isCommentSentToServer())
            {
                sendReportComment(pSuperCallback);
            }
            else
            {
                Log.i(TAG, "sent report COMMENT: " + mCurrentReport.getComment());

                sendAttachedFiles(pSuperCallback);
            }
        } else {
            pSuperCallback.done(null, null, null);
        }
        reportCursor.close();
    }
}
