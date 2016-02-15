package williamgbranco.comune.util;

import android.content.Context;

import williamgbranco.comune.R;

/**
 * Created by William Gomes de Branco on 23/08/2015.
 */
public class DateUtils
{
    public static String getStringHourFromInt(int hour)
    {
        String mHour = String.valueOf(hour);

        if (mHour.length() == 1) mHour = "0"+mHour;

        mHour = mHour + ":00";

        return mHour;
    }

    public static String getDiaDaSemana(Context appContext, Integer dia)
    {
        switch (dia)
        {
            case (1):
                return appContext.getResources().getString(R.string.sunday);

            case (2):
                return appContext.getResources().getString(R.string.monday);

            case (3):
                return appContext.getResources().getString(R.string.tuesday);

            case (4):
                return appContext.getResources().getString(R.string.wednesday);

            case (5):
                return appContext.getResources().getString(R.string.thursday);

            case (6):
                return appContext.getResources().getString(R.string.friday);

            case (7):
                return appContext.getResources().getString(R.string.saturday);

            default:
                return null;
        }
    }

    public static String getDiaDaSemanaAbrev(Context appContext, Integer dia)
    {
        String abrev = getDiaDaSemana(appContext, dia);

        if (abrev != null)
            abrev = abrev.substring(0, 3);

        return abrev;
    }

    public static String getMes(Context appContext, Integer mes)
    {
        switch (mes)
        {
            case (0):
                return appContext.getResources().getString(R.string.january);

            case (1):
                return appContext.getResources().getString(R.string.february);

            case (2):
                return appContext.getResources().getString(R.string.march);

            case (3):
                return appContext.getResources().getString(R.string.april);

            case (4):
                return appContext.getResources().getString(R.string.may);

            case (5):
                return appContext.getResources().getString(R.string.june);

            case (6):
                return appContext.getResources().getString(R.string.july);

            case (7):
                return appContext.getResources().getString(R.string.august);

            case (8):
                return appContext.getResources().getString(R.string.september);

            case (9):
                return appContext.getResources().getString(R.string.october);

            case (10):
                return appContext.getResources().getString(R.string.november);

            case (11):
                return appContext.getResources().getString(R.string.december);

            default:
                return null;
        }
    }

    public static String getMesAbrev(Context appContext, Integer mes)
    {
        String abrev = getMes(appContext, mes);

        if (abrev != null)
            abrev = abrev.substring(0, 3);

        return abrev;
    }
}
