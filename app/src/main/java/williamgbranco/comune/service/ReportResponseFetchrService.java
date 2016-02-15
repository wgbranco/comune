package williamgbranco.comune.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import williamgbranco.comune.R;
import williamgbranco.comune.activity.DisplayReportResponseActivity;
import williamgbranco.comune.activity.ReportListForInstitutionActivity;
import williamgbranco.comune.activity.UserReportsActivity;
import williamgbranco.comune.callback.ResponseAvailableCallbacks;
import williamgbranco.comune.report.response.AvailableResponse;
import williamgbranco.comune.server.ServerRequests;


public class ReportResponseFetchrService extends IntentService
{
    private static final String TAG = "ReportResponseServ";

    private static final String ACTION_CHECK_REPORT_RESPONDED = "williamgbranco.comune.service.ReportResponseFetchrService.action.CHECK_REPORT_RESPONDED";
    public static final String ACTION_SET_ALARM_ON = "williamgbranco.comune.service.ReportResponseFetchrService.action.SET_ALARM_ON";
    public static final String ACTION_SET_ALARM_OFF = "williamgbranco.comune.service.ReportResponseFetchrService.action.SET_ALARM_OFF";

    public static final String EXTRA_USER_ID = "williamgbranco.comune.service.ReportResponseFetchrService.extra.USER_ID";

    private static final boolean ALARM_ON = true;
    private static final boolean ALARM_OFF = false;

    private int mUserId;

    private ResponseAvailableCallbacks mCallbacks = new ResponseAvailableCallbacks() {
        @Override
        public void done(ArrayList<AvailableResponse> responses)
        {
            try {
                if (responses != null)
                {
                    if (responses.size() > 0)
                    {
                        Intent intent = null;
                        PendingIntent pi = null;
                        AvailableResponse response = null;

                        if (responses.size() == 1)
                        {
                            response = responses.get(0);

                            intent = new Intent(getApplication(), DisplayReportResponseActivity.class);
                            intent.putExtra(DisplayReportResponseActivity.EXTRA_INST_ID, response.getInstitutionId());
                            intent.putExtra(DisplayReportResponseActivity.EXTRA_REPORT_ID, response.getReportId());
                            intent.putExtra(DisplayReportResponseActivity.EXTRA_RESPONSE_ID, response.getResponseId());

                            Log.i(TAG, "notification click start DisplayReportResponseActivity: instId = " + response.getInstitutionId() + ", reportId = " + response.getReportId() + ", responseId = " + response.getResponseId());
                        } else {
                            if (allResponsesComeFromSameInstitution(responses)) {
                                response = responses.get(0);

                                intent = new Intent(getApplicationContext(), ReportListForInstitutionActivity.class);
                                intent.putExtra(ReportListForInstitutionActivity.EXTRA_INST_ID, response.getInstitutionId());

                                Log.i(TAG, "notification click start ReportListForInstitutionActivity: instId = " + response.getInstitutionId());
                            } else {
                                intent = new Intent(getApplicationContext(), UserReportsActivity.class);

                                Log.i(TAG, "notification click start UserReportsActivity");
                            }
                        }

                        pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                        Resources r = getResources();
                        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                                .setTicker(r.getString(R.string.new_report_response_title))
                                .setSmallIcon(R.drawable.ic_stat_comune)
                                .setContentTitle(r.getString(R.string.new_report_response_title))
                                .setContentText(r.getString(R.string.new_report_response_text))
                                .setContentIntent(pi)
                                .setAutoCancel(true)
                                .build();

                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        manager.notify(0, notification);
                    }
                    else
                    {
                        setServiceAlarm(mUserId, getApplicationContext(), ALARM_OFF);
                        Log.i(TAG, "ReportResponseFetchrService STOPPED");
                        stopSelf();
                    }
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }
        }

        private boolean allResponsesComeFromSameInstitution(ArrayList<AvailableResponse> responses)
        {
            if ((responses != null) && (responses.size() > 1))
            {
                for (int i = 0; i < (responses.size()-1); i++)
                {
                    if (!(responses.get(i).getInstitutionId().equals(responses.get(i+1).getInstitutionId())))
                        return false;
                }
            }

            return true;
        }

        @Override
        public void onNetworkNonAvailable()
        {
            //setServiceAlarm(getApplicationContext(), ALARM_ON);
        }
    };


    public ReportResponseFetchrService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        String action = intent.getAction();
        mUserId = intent.getIntExtra(EXTRA_USER_ID, -1);

        //Toast.makeText(getApplicationContext() ,"Procurando se relato foi respondido", Toast.LENGTH_LONG).show();
        Log.i(TAG, "action: "+action);

        if (ACTION_CHECK_REPORT_RESPONDED.equals(action))
        {
            new ServerRequests(getApplication()).checkResponsesAvailable(mUserId, mCallbacks);
        }
        else if (ACTION_SET_ALARM_ON.equals(action))
        {
            setServiceAlarm(mUserId, getApplicationContext(), ALARM_ON);
        }
        else if (ACTION_SET_ALARM_OFF.equals(action))
        {
            setServiceAlarm(mUserId, getApplicationContext(), ALARM_OFF);
            Log.i(TAG, "ReportResponseFetchrService STOPPED");
            stopSelf();
        }
    }

    protected static void setServiceAlarm(int userId, Context context, boolean on)
    {
        Intent i = new Intent(context, ReportResponseFetchrService.class);
        i.setAction(ACTION_CHECK_REPORT_RESPONDED);
        i.putExtra(ReportResponseFetchrService.EXTRA_USER_ID, userId);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        GregorianCalendar calendar = new GregorianCalendar();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (on)
        {
            Log.i(TAG, "ReportResponseFetchrService STARTED");
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_HALF_DAY, pi); // a cada 12 horas
        }
        else
        {
            Log.i(TAG, "OFF");
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }
}
