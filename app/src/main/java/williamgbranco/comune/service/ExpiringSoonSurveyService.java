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

import java.util.GregorianCalendar;

import williamgbranco.comune.R;
import williamgbranco.comune.activity.MainActivity;
import williamgbranco.comune.manager.SurveyManager;
import williamgbranco.comune.util.Constants;


public class ExpiringSoonSurveyService extends IntentService
{
    private static final String TAG = "ExpiringSoonSurveyServ";

    public static final String ACTION_SET_ALARM_ON = "williamgbranco.comune.service.ExpiringSoonSurveyService.action.SET_ALARM_ON";
    public static final String ACTION_SET_ALARM_OFF = "williamgbranco.comune.service.ExpiringSoonSurveyService.action.SET_ALARM_OFF";

    public static final String EXTRA_USER_ID = "williamgbranco.comune.service.ExpiringSoonSurveyService.extra.EXTRA_USER_ID";


    private SurveyManager mSurveyManager;
    private int mUserId;

    public ExpiringSoonSurveyService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null)
        {
            //Toast.makeText(ExpiringSoonSurveyService.this, "ExpiringSoonSurveyService handle intent", Toast.LENGTH_SHORT).show();

            String action = intent.getAction();

            mUserId = intent.getIntExtra(EXTRA_USER_ID, -1);

            if (ACTION_SET_ALARM_OFF.equals(action))
            {
                setServiceAlarm(mUserId, getApplicationContext(), Constants.ALARM_OFF);
                stopSelf();
            }
            else
            {
                mSurveyManager = new SurveyManager(getApplication());

                if (mSurveyManager.userHasIncompleteSurveys(mUserId))
                {
                    if (mSurveyManager.hasSurveysExpiringSoon(mUserId))
                    {
                        //Intent i = new Intent(getApplicationContext(), StoredSurveysActivity.class);
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.setAction(Constants.ACTION_STORED_SURVEYS_ACTVITY);
                        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, i, 0);

                        Resources r = getResources();
                        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                                .setTicker(r.getString(R.string.survey_expiring_soon_title))
                                .setSmallIcon(R.drawable.ic_stat_comune)
                                .setContentTitle(r.getString(R.string.survey_expiring_soon_title))
                                .setContentText(r.getString(R.string.survey_expiring_soon_text))
                                .setContentIntent(pi)
                                .setAutoCancel(true)
                                .build();

                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        manager.notify(0, notification);

                        //setServiceAlarm(getApplicationContext(), ALARM_ON);
                    } else {
                        setServiceAlarm(mUserId, getApplicationContext(), Constants.ALARM_ON);
                    }
                }
                else
                {
                    setServiceAlarm(mUserId, getApplicationContext(), Constants.ALARM_OFF);
                    stopSelf();
                }
            }
        }
    }

    protected static void setServiceAlarm(int userId, Context context, boolean on)
    {
        Intent i = new Intent(context, ExpiringSoonSurveyService.class);
        i.putExtra(ExpiringSoonSurveyService.EXTRA_USER_ID, userId);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        GregorianCalendar calendar = new GregorianCalendar();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (on)
        {
            Log.i(TAG, "ExpiringSoonSurveyService STARTED");
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendar.getTimeInMillis()+36*60*60000, pi); //36horas depois
        }
        else
        {
            Log.i(TAG, "ExpiringSoonSurveyService STOPPED");
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }
}
