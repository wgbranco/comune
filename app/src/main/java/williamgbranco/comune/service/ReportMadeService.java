package williamgbranco.comune.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.GregorianCalendar;

import williamgbranco.comune.callback.SentReportCallback;
import williamgbranco.comune.manager.ReportManager;
import williamgbranco.comune.server.ServerUploads;


public class ReportMadeService extends IntentService
{
    private static final String TAG = "ReportMadeService";

    protected ReportManager mReportManager;


    private SentReportCallback mCallbacks = new SentReportCallback()
    {
        @Override
        public void done(Integer userId, Integer instId, Integer reportId)
        {
            try {
                if ((userId != null) && (instId != null) && (reportId != null))
                {
                    Log.i(TAG, "SentReportCallback.done() userId = " + userId + ", instId = " + instId + ", reportId = " + reportId);

                    mReportManager.deleteReportForUser(userId, instId, reportId);

                    Intent i = new Intent(getApplication(), ReportResponseFetchrService.class);
                    i.setAction(ReportResponseFetchrService.ACTION_SET_ALARM_ON);
                    i.putExtra(ReportResponseFetchrService.EXTRA_USER_ID, userId);
                    getApplication().startService(i);

                    finishService();
                }
                else
                {
                    ReportMadeService.this.setAlarmOn();
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
                ReportMadeService.this.setAlarmOn();
            }
        }

        @Override
        public void onNetworkNonAvailable()
        {
            ReportMadeService.this.setAlarmOn();
        }
    };


    public ReportMadeService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        mReportManager = ReportManager.get(getApplication());

        Log.i(TAG, "ReportMadeService STARTED");

        if (mReportManager.hasReportsMade())
        {
            ReportMadeService.this.startClockAlarm(getApplicationContext(), false);

            new ServerUploads(getApplicationContext()).sendReportsToSeverOneAtATime(mCallbacks);
        }
        else
        {
            Log.i(TAG, "ReportMadeService STOPPED");
            stopSelf();
        }
    }

    private void finishService()
    {
        ReportMadeService.this.startClockAlarm(getApplicationContext(), false);
        stopSelf();
    }

    private void setAlarmOn()
    {
        ReportMadeService.this.startClockAlarm(getApplicationContext(), true);
    }

    private static PendingIntent createClockIntent(Context context) {
        Intent i = new Intent(context, ReportMadeService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        return pi;
    }

    private static void startClockAlarm(Context context, boolean on)
    {
        Log.i(TAG, "startClockAlarm");

        GregorianCalendar calendar = new GregorianCalendar();
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        PendingIntent clockIntent = createClockIntent(context);

        if (on)
        {
            Log.i(TAG, "alarm ON");
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis()+
                    10*60000, clockIntent); //a cada 15 min
        } else
        {
            Log.i(TAG, "alarm OFF");

            alarmManager.cancel(clockIntent);
            clockIntent.cancel();
        }
    }
}
