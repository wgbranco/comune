package williamgbranco.comune.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.GregorianCalendar;

import williamgbranco.comune.callback.SentSurveyCallback;
import williamgbranco.comune.manager.SurveyManager;
import williamgbranco.comune.server.ServerUploads;


public class CompleteSurveyService extends IntentService
{
    private static final String TAG = "CompleteSurveyService";

    protected SurveyManager mSurveyManager;
    protected ServerUploads mServerUploads;

    /*private final BroadcastReceiver mConnectionDetectorBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "mConnectionDetectorRecv:" + intent.getAction());

            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()))
            {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                @SuppressWarnings("deprecation")
                boolean connected = (cm.getBackgroundDataSetting() && (cm.getActiveNetworkInfo() != null));
                // Use extras to verify that connection has been re-established...
                Log.i(TAG, "mConnectionDetectorRecv: CONNECTED: " + connected);

                if (connected)
                {
                    // Unregister until we lose network connectivity again.
                    CompleteSurveyService.this.unregisterReceiver(CompleteSurveyService.this.mConnectionDetectorBroadcastReceiver);
                    Log.i(TAG, "UNregisterReceiver: " + CompleteSurveyService.this.mConnectionDetectorBroadcastReceiver.toString());

                    // Resume handling requests.
                    Intent i = new Intent(context, CompleteSurveyService.class);
                    context.startService(i);
                }
            }
        }
    };*/

    private SentSurveyCallback mCallbacks = new SentSurveyCallback()
    {
        @Override
        public void done(Integer userId, Integer instId, Integer surveyId)
        {
            try {
                Log.i(TAG, "SentSurveyCallback mCallbacks DONE("+userId +", " + instId + ", " + surveyId+"");

                if ((userId != null) && (instId != null) && (surveyId != null))
                {
                    mSurveyManager.deleteSurveyForUser(userId, instId, surveyId);
                    Log.i(TAG, "SentSurveyCallback survey DELETED()");

                    finishService();
                }
                else
                {
                    CompleteSurveyService.this.setAlarmOn();
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
                CompleteSurveyService.this.setAlarmOn();
            }
        }

        @Override
        public void onNetworkNonAvailable()
        {
            CompleteSurveyService.this.setAlarmOn();
        }

        @Override
        public void errorQueryingSurvey(Integer userId, Integer instId, Integer surveyId) {
            CompleteSurveyService.this.setAlarmOn();
        }
    };


    public CompleteSurveyService()
    {
        super(TAG);
        Log.i(TAG, "onCreate");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.i(TAG, "CompleteSurveyService onHandleIntent");

        mSurveyManager = SurveyManager.get(getApplication());

        if (mServerUploads == null) mServerUploads = new ServerUploads(getApplicationContext());

        Log.i(TAG, "CompleteSurveyService STARTED");
        //Toast.makeText(getApplication(), TAG, Toast.LENGTH_SHORT).show();

        if (mSurveyManager.hasCompleteSurveys())
        {
            CompleteSurveyService.this.startClockAlarm(getApplicationContext(), false);

            mServerUploads.sendCompleteSurveyToSeverOnAtATime(mCallbacks);
        }
        else
        {
            Log.i(TAG, "CompleteSurveyService STOPPED");
            finishService();
        }
    }

    private void finishService()
    {
        CompleteSurveyService.this.startClockAlarm(getApplicationContext(), false);
        stopSelf();
    }

    private void setAlarmOn()
    {
        CompleteSurveyService.this.startClockAlarm(getApplicationContext(), true);
    }

    private static PendingIntent createClockIntent(Context context) {
        Intent i = new Intent(context, CompleteSurveyService.class);
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

        if (on) {
            Log.i(TAG, "alarm ON");

            /*alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                    10*60000, clockIntent);*/
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis()+10*60000, clockIntent);
        } else
        {
            Log.i(TAG, "alarm OFF");

            alarmManager.cancel(clockIntent);
            clockIntent.cancel();
        }
    }

    /*private void tryAgainLater()
    {
        Log.i(TAG, "SentSurveyCallback, Try Again ()");

        *//*try {
            CompleteSurveyService.this.unregisterReceiver(CompleteSurveyService.this.mConnectionDetectorBroadcastReceiver);
            Log.i(TAG, "UNregisterReceiver: " + CompleteSurveyService.this.mConnectionDetectorBroadcastReceiver);
        } catch (Exception e) {Log.i(TAG, e.toString());}*//*

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        CompleteSurveyService.this.registerReceiver(CompleteSurveyService.this.mConnectionDetectorBroadcastReceiver, filter);
        Log.i(TAG, "registerReceiver: " + CompleteSurveyService.this.mConnectionDetectorBroadcastReceiver);

        *//*try {
        } catch (Exception e) {Log.i(TAG, e.toString());}*//*

    }

    @Override
    public void onDestroy()
    {
        Log.i(TAG, "ON DESTROY()");
        super.onDestroy();
    }*/
}
