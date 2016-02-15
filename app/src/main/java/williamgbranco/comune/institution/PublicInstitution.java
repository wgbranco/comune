package williamgbranco.comune.institution;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import williamgbranco.comune.R;
import williamgbranco.comune.survey.Survey;
import williamgbranco.comune.util.DateUtils;


/**
 * Created by William de Branco on 17/05/2015.
 */
public abstract class PublicInstitution
{
    protected static final String TAG = "PublicInstitution";

    public static final String ACTION_VISIBILITY_CHANGE = "PublicInstitution.VisibilityChange";

    public static final String EXTRA_INST_VISIBILITY = "PublicInstitution.visibility";
    public static final String EXTRA_INST_TYPE = "PublicInstitution.type";
    public static final String EXTRA_INST_GRADE_SUPERIOR_LIMIT = "PublicInstitution.grade_superior_limit";
    public static final String EXTRA_INST_GRADE_INFERIOR_LIMIT = "PublicInstitution.grade_inferior_limit";
    public static final String PERM_PRIVATE = "williamgbranco.comune.PRIVATE";

    public static final int TIPO_EDUCACAO = 1;
    public static final int TIPO_SAUDE = 2;
    public static final int TIPO_SEGURANCA = 3;

    public static final int STATUS_INATIVO = 0;
    public static final int STATUS_ATIVO = 1;

    public static final double NOTA_MAXIMA = 5.0 + 0.1;
    public static final double NOTA_ALTA = 4.0;
    public static final double NOTA_MEDIA = 3.0;
    public static final double NOTA_MINIMA = 0.0;


    private Integer mId;
    private String mNome;
    private String mNomeAbreviado;
    //private Integer mDepartmentId;
    private Integer mStatus;
    private Double mNotaMedia;
    private LatLng mLatLng;
    private ArrayList<WorkingDay> mWorkingDays;
    /*private int mHoraAbertura;
    private int mHoraFechamento;*/
    protected int mPlaceBlackIconId;
    protected int mPlaceDarkGrayIconId;
    protected int mPlaceGrayIconId;
    protected int mPlaceLightGrayIconId;
    private Marker mMarker;
    private MarkerOptions mMarkerOptions;
    private ArrayList<Survey> mAvailableSurveysList;
    private boolean typeIsVisible = true;
    private boolean gradeIsVisible = true;
    private boolean hasSurveyExpiringSoon = false;

    private Context mAppContext;

    private final BroadcastReceiver mOnMarkerVisibilityChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean visible = intent.getBooleanExtra(EXTRA_INST_VISIBILITY, false);

            int type = intent.getIntExtra(EXTRA_INST_TYPE, -1);
            if (type > -1)
            {
                if (type == getTipo())
                {
                    //Log.i(TAG, "setting visibility for type, inst id: " + getId());
                    typeIsVisible = visible;
                }
            }
            else
            {
                double superior = intent.getDoubleExtra(EXTRA_INST_GRADE_SUPERIOR_LIMIT, -1.0);
                double inferior = intent.getDoubleExtra(EXTRA_INST_GRADE_INFERIOR_LIMIT, -1.0);

                if ((superior > -1.0) && (inferior > -1.0) &&
                        (getNotaMedia() >= inferior) && (getNotaMedia() < superior))
                {
                    //Log.i(TAG, "setting visibility for grade, inst id: " + getId());
                    gradeIsVisible = visible;
                }
            }

            if (getMarker() != null)
                getMarker().setVisible(typeIsVisible && gradeIsVisible);
        }
    };


    public PublicInstitution(Integer pId, Context pContext)
    {
        if ((pId != null) && (pContext != null))
        {
            mId = pId;
            mAppContext = pContext.getApplicationContext();
            registerBroadcastReceiver();
        }
    }

    private void registerBroadcastReceiver()
    {
        IntentFilter filter = new IntentFilter(ACTION_VISIBILITY_CHANGE);
        mAppContext.registerReceiver(mOnMarkerVisibilityChange, filter, PERM_PRIVATE, null);
    }


    public Integer getId() {
        return mId;
    }

    public abstract Integer getTipo();

    protected Context getAppContext()
    {
        return mAppContext;
    }

    public String getNome() {
        return mNome;
    }

    public void setNome(String pNome) {
        mNome = pNome;
    }

    public String getNomeAbreviado() {
        return mNomeAbreviado;
    }

    public void setNomeAbreviado(String pNomeAbreviado) {
        mNomeAbreviado = pNomeAbreviado;
    }

    public Integer getStatus() {
        return mStatus;
    }

    public void setStatus(Integer pStatus) {
        mStatus = pStatus;
    }

    public double getNotaMedia() {
        return mNotaMedia;
    }

    public void setNotaMedia(double pNotaMedia) {
        mNotaMedia = pNotaMedia;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    public void setLatLng(LatLng pLatLng) {
        mLatLng = pLatLng;
    }

    public Location getLocation() {
        Location l = new Location("");
        l.setLatitude(getLatLng().latitude);
        l.setLongitude(getLatLng().longitude);

        return l;
    }

    public String getOpeningTimeOfDay()
    {
        String hora = null;
        GregorianCalendar now = new GregorianCalendar();

        WorkingDay workingDay = getWorkingDayInfo(now.get(Calendar.DAY_OF_WEEK));
        if (workingDay != null)
        {
            return workingDay.getOpeningTime().toString();
        }
        return hora;
    }

    public String getClosingTimeOfDay()
    {
        String hora = null;
        GregorianCalendar now = new GregorianCalendar();

        WorkingDay workingDay = getWorkingDayInfo(now.get(Calendar.DAY_OF_WEEK));
        if (workingDay != null)
        {
            return workingDay.getClosingTime().toString();
        }
        return hora;
    }

    public ArrayList<WorkingDay> getWorkingDays() {
        return mWorkingDays;
    }

    public WorkingDay getWorkingDayInfo(int day)
    {
        for (WorkingDay workingDay : getWorkingDays())
        {
            if (workingDay.getDayOfTheWeek() == day)
            {
                return workingDay;
            }
        }

        return null;
    }

    public void setWorkingDays(ArrayList<WorkingDay> pWorkingDays)
    {
        mWorkingDays = pWorkingDays;
    }

    public String getWorkingDaysAsText()
    {
        if ((mWorkingDays != null) && (mWorkingDays.size() > 0))
        {
            int nDays = mWorkingDays.size();

            if (nDays < 2) {
                return DateUtils.getDiaDaSemanaAbrev(mAppContext, mWorkingDays.get(0).getDayOfTheWeek());
            }
            else
            {
                if (nDays == 7)
                {
                    //return mAppContext.getString(R.string.msg_open_every_day);

                    return DateUtils.getDiaDaSemanaAbrev(mAppContext, 2)
                            + "   —   "
                            + DateUtils.getDiaDaSemanaAbrev(mAppContext, 1);
                }

                return DateUtils.getDiaDaSemanaAbrev(mAppContext, mWorkingDays.get(0).getDayOfTheWeek())
                        + "   —   "
                        + DateUtils.getDiaDaSemanaAbrev(mAppContext, mWorkingDays.get(nDays - 1).getDayOfTheWeek());
            }
        }
        return "";
    }

    public boolean isOpen()
    {
        GregorianCalendar now = new GregorianCalendar();
        /*int ano = now.get(GregorianCalendar.YEAR);
        int mes = now.get(GregorianCalendar.MONTH);
        int diaMes = now.get(GregorianCalendar.DAY_OF_MONTH);*/
        int diaSemana = now.get(GregorianCalendar.DAY_OF_WEEK);
        //int hora = now.get(Calendar.HOUR_OF_DAY);

        /*int hora_abre = getOpeningTimeOfDay();
        GregorianCalendar openingTime = new GregorianCalendar();
        openingTime.set(ano, mes, dia, hora_abre, 0, 0);

        int hora_fecha = getClosingTimeOfDay();
        GregorianCalendar closingTime = new GregorianCalendar();
        closingTime.set(ano, mes, dia, hora_fecha, 0, 0);

        Log.i(TAG, dia + " , " + mes + " , " + ano);
        Log.i(TAG, "now  : " + now.getTimeInMillis());
        Log.i(TAG, "abre : " + openingTime.getTimeInMillis());
        Log.i(TAG, "fecha: " + closingTime.getTimeInMillis());

        if (now.after(openingTime.getTime()) && now.before(closingTime.getTime()))
            return true;*/

        WorkingDay dayInfo = getWorkingDayInfo(diaSemana);
        if (dayInfo == null) return false;

        GregorianCalendar openingTime = new GregorianCalendar();
        openingTime.setTime(dayInfo.getOpeningTime());
        Log.i(TAG, "openingTime: " + openingTime.toString());

        GregorianCalendar closingTime = new GregorianCalendar();
        closingTime.setTime(dayInfo.getClosingTime());
        Log.i(TAG, "closingTime: " + closingTime.toString());

        if (((now.get(Calendar.HOUR_OF_DAY) > openingTime.get(Calendar.HOUR_OF_DAY)) &&
                (now.get(Calendar.HOUR_OF_DAY) < closingTime.get(Calendar.HOUR_OF_DAY)))
                || (dayInfo.getOpeningTime().equals(dayInfo.getClosingTime())))
            return true;

        return false;
    }

    public void addSurvey(Survey pSurvey)
    {
        if (mAvailableSurveysList == null)
        {
            mAvailableSurveysList = new ArrayList<>();
        }
        pSurvey.setPublicInstitutionId(getId());
        mAvailableSurveysList.add(pSurvey);
    }

    public void removeAvailableSurveyById(Integer surveyId)
    {
        try{
            for (Survey survey : mAvailableSurveysList)
            {
                if (survey.getId().equals(surveyId))
                {
                    mAvailableSurveysList.remove(survey);
                    //Log.i(TAG, "removeAvailableSurveyById("+surveyId+")" + ": removed!");
                }
            }
        }
        catch (Exception e) {}
    }

    public ArrayList<Survey> getAvailableSurveysList()
    {
        return mAvailableSurveysList;
    }

    public void setAvailableSurveysList(ArrayList<Survey> pListaPesquisas)
    {
        mAvailableSurveysList = pListaPesquisas;

        if (mAvailableSurveysList != null)
        {
            for (Survey survey : mAvailableSurveysList)
            {
                survey.setPublicInstitutionId(getId());
            }
        }
    }

    public MarkerOptions getMarkerOptions()
    {
        if (mMarkerOptions == null)
        {
            mMarkerOptions = new MarkerOptions()
                    .position(getLatLng())
                    .icon(getMapMarkerIconDrawable())
                    .title(getNome())
                    .snippet(Double.toString(getNotaMedia()));
        }

        mMarkerOptions.visible(isVisible());

        return mMarkerOptions;
    }

    public void setMarker (Marker pMarker)
    {
        mMarker = pMarker;
    }

    public Marker getMarker()
    {
        return mMarker;
    }

    public boolean anySurveyAvailable()
    {
        if ((mAvailableSurveysList != null) && (mAvailableSurveysList.size() > 0))
        {
            return true;
        }

        return false;
    }

    public int getPlaceBlackIconId()
    {
        return mPlaceBlackIconId;
    }

    public int getPlaceDarkGrayIconId()
    {
        return mPlaceDarkGrayIconId;


    }public int getPlaceGrayIconId()
    {
        return mPlaceGrayIconId;
    }

    public int getPlaceLightGrayIconId()
    {
        return mPlaceLightGrayIconId;
    }

    public int getRatingIconId()
    {
        double rating = getNotaMedia();

        if (rating > 0.0) {
            if (rating >= PublicInstitution.NOTA_ALTA) {
                return R.drawable.ic_mark_green;
            } else if (rating >= PublicInstitution.NOTA_MEDIA) {
                return R.drawable.ic_mark_yellow;
            } else {
                return R.drawable.ic_mark_red;
            }
        }

        return R.drawable.ic_mark_gray;
    }

    public abstract BitmapDescriptor getMapMarkerIconDrawable();

    public boolean isVisible()
    {
        //Log.i(TAG, "is marker visible: typeIsVisible = " + typeIsVisible + ", gradeIsVisible = " + gradeIsVisible);
        return (typeIsVisible && gradeIsVisible);
    }

    public boolean hasSurveyExpiringSoon()
    {
        return hasSurveyExpiringSoon;
    }

    public void setHasSurveyExpiringSoon(boolean pHasSurveyExpiringSoon)
    {
        hasSurveyExpiringSoon = pHasSurveyExpiringSoon;
    }

    @Override
    public String toString()
    {
        return "Serviço Público {" +
                "id = " + getId() + " " +
                "tipo = '" + getTipo() + '\'' +
                ", nome = '" + mNome + '\'' +
                ", nota média = " + mNotaMedia +
                ", status = '" + mStatus + '\'' +
                ", mLatLng=" + mLatLng.toString() +
                '}';
    }
}
