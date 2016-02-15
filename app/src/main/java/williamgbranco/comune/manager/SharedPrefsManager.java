package williamgbranco.comune.manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import williamgbranco.comune.institution.PublicInstitution;

/**
 * Created by William Gomes de Branco on 23/09/2015.
 */
public class SharedPrefsManager
{
    private static final String TAG = "SharedPrefsManager";

    public static final String ACTION_CHECK_FOR = "SharedPrefsManager.ActionCheckFor";

    public static final String MAP_LAYERS_SHARED_PREFS = "maps_layers_shared_prefs";
    public static final String MAP_LAYER_EDUCATION = "map_layer_education";
    public static final String MAP_LAYER_HEALTH = "map_layer_health";
    public static final String MAP_LAYER_SECURITY = "map_layer_security";
    public static final String MAP_LAYER_HIGH_GRADE = "map_layer_high_grade";
    public static final String MAP_LAYER_MEDIUM_GRADE = "map_layer_medium_grade";
    public static final String MAP_LAYER_LOW_GRADE = "map_layer_low_grade";

    public static final String SURVEY_SHARED_PREFS = "survey_shared_prefs";
    public static final String SURVEY_COMPLETE = "survey_complete";
    public static final String SURVEY_EXPIRED = "survey_expired";

    public static final String REPORT_SHARED_PREFS = "report_shared_prefs";
    public static final String REPORT_MADE = "report_made";

    private static SharedPrefsManager sMapSharedPrefsManager;
    private static SharedPreferences sSharedPrefsMapLayers;
    private static SharedPreferences sSharedPrefsSurvey;
    private static SharedPreferences sSharedPrefsReport;
    private Context mAppContext;


    private SharedPreferences.OnSharedPreferenceChangeListener mMapLayersSharedPrefsListener = new SharedPreferences.OnSharedPreferenceChangeListener()
    {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            Log.i(TAG, "onSharedPreferenceChanged");

            Intent i = new Intent(PublicInstitution.ACTION_VISIBILITY_CHANGE);

            switch (key)
            {
                case MAP_LAYER_EDUCATION:
                    Log.i(TAG, "MAP_LAYER_EDUCATION Changed");
                    i.putExtra(PublicInstitution.EXTRA_INST_TYPE, PublicInstitution.TIPO_EDUCACAO);
                    break;

                case MAP_LAYER_HEALTH:
                    Log.i(TAG, "MAP_LAYER_HEALTH Changed");
                    i.putExtra(PublicInstitution.EXTRA_INST_TYPE, PublicInstitution.TIPO_SAUDE);
                    break;

                case MAP_LAYER_SECURITY:
                    Log.i(TAG, "MAP_LAYER_SECURITY Changed");
                    i.putExtra(PublicInstitution.EXTRA_INST_TYPE, PublicInstitution.TIPO_SEGURANCA);
                    break;

                case MAP_LAYER_HIGH_GRADE:
                    Log.i(TAG, "MAP_LAYER_HIGH_GRADE Changed");
                    i.putExtra(PublicInstitution.EXTRA_INST_GRADE_SUPERIOR_LIMIT, PublicInstitution.NOTA_MAXIMA);
                    i.putExtra(PublicInstitution.EXTRA_INST_GRADE_INFERIOR_LIMIT, PublicInstitution.NOTA_ALTA);
                    break;

                case MAP_LAYER_MEDIUM_GRADE:
                    Log.i(TAG, "MAP_LAYER_MEDIUM_GRADE Changed");
                    i.putExtra(PublicInstitution.EXTRA_INST_GRADE_SUPERIOR_LIMIT, PublicInstitution.NOTA_ALTA);
                    i.putExtra(PublicInstitution.EXTRA_INST_GRADE_INFERIOR_LIMIT, PublicInstitution.NOTA_MEDIA);
                    break;

                case MAP_LAYER_LOW_GRADE:
                    Log.i(TAG, "MAP_LAYER_LOW_GRADE Changed");
                    i.putExtra(PublicInstitution.EXTRA_INST_GRADE_SUPERIOR_LIMIT, PublicInstitution.NOTA_MEDIA);
                    i.putExtra(PublicInstitution.EXTRA_INST_GRADE_INFERIOR_LIMIT, PublicInstitution.NOTA_MINIMA);
                    break;
            }

            boolean visible = sharedPreferences.getBoolean(key, true);
            i.putExtra(PublicInstitution.EXTRA_INST_VISIBILITY, visible);

            Log.i(TAG, "Changed to: " + visible);

            if (mAppContext != null)
            {
                mAppContext.sendBroadcast(i, PublicInstitution.PERM_PRIVATE);
            }
        }
    };


    public static SharedPrefsManager get(Context pContext)
    {
        if (sMapSharedPrefsManager == null)
            sMapSharedPrefsManager = new SharedPrefsManager(pContext.getApplicationContext());

        return sMapSharedPrefsManager;
    }

    public SharedPrefsManager(Context pAppContext)
    {
        mAppContext = pAppContext;

        configureMapLayersSharedPreferences();
        configureSurveySharedPreferences();
        configureReportSharedPreferences();
    }


    /***********************************************************************
    *  MAP LAYERS functions
    ************************************************************************/

    private void configureMapLayersSharedPreferences()
    {
        Log.i(TAG, "configureMapLayersSharedPreferences");

        sSharedPrefsMapLayers = mAppContext.getSharedPreferences(MAP_LAYERS_SHARED_PREFS, Context.MODE_PRIVATE);
        sSharedPrefsMapLayers.registerOnSharedPreferenceChangeListener(mMapLayersSharedPrefsListener);

        resetMapSharedPreferences();
    }

    public void setOnlyVisibleInstitutionType(Integer pType)
    {
        if (sSharedPrefsMapLayers != null)
        {
            cleanMapSharedPreferences();

            SharedPreferences.Editor editor = sSharedPrefsMapLayers.edit();

            if (pType == PublicInstitution.TIPO_EDUCACAO)
            {
                editor.putBoolean(MAP_LAYER_EDUCATION, true);
            }
            else if (pType == PublicInstitution.TIPO_SAUDE)
            {
                editor.putBoolean(MAP_LAYER_HEALTH, true);
            }
            else if (pType == PublicInstitution.TIPO_SEGURANCA)
            {
                editor.putBoolean(MAP_LAYER_SECURITY, true);
            }

            editor.commit();
        }
    }

    public void resetMapSharedPreferences()
    {
        if (sSharedPrefsMapLayers != null)
        {
            Log.i(TAG, "reset map SharePreferences");

            SharedPreferences.Editor editor = sSharedPrefsMapLayers.edit();

            editor.putBoolean(MAP_LAYER_EDUCATION, true);
            editor.putBoolean(MAP_LAYER_HEALTH, true);
            editor.putBoolean(MAP_LAYER_SECURITY, true);

            editor.putBoolean(MAP_LAYER_HIGH_GRADE, true);
            editor.putBoolean(MAP_LAYER_MEDIUM_GRADE, true);
            editor.putBoolean(MAP_LAYER_LOW_GRADE, true);

            editor.commit();
        }
    }

    public void cleanMapSharedPreferences()
    {
        if (sSharedPrefsMapLayers != null)
        {
            Log.i(TAG, "clean map SharePreferences");

            SharedPreferences.Editor editor = sSharedPrefsMapLayers.edit();

            editor.putBoolean(MAP_LAYER_EDUCATION, false);
            editor.putBoolean(MAP_LAYER_HEALTH, false);
            editor.putBoolean(MAP_LAYER_SECURITY, false);

            editor.commit();
        }
    }

    public SharedPreferences getMapSharedPreferences()
    {
        return sSharedPrefsMapLayers;
    }


    /*  MAP LAYERS - Education functions
    ************************************************************************/
    public boolean getEducationLayerVisibility()
    {
        return sSharedPrefsMapLayers.getBoolean(SharedPrefsManager.MAP_LAYER_EDUCATION, true);
    }

    public void changeEducationLayerVisibility(boolean value)
    {
        SharedPreferences.Editor editor = sSharedPrefsMapLayers.edit();
        editor.putBoolean(SharedPrefsManager.MAP_LAYER_EDUCATION, value);
        editor.commit();
    }


    /*  MAP LAYERS - Security functions
    ************************************************************************/
    public boolean getSecurityLayerVisibility()
    {
        return sSharedPrefsMapLayers.getBoolean(SharedPrefsManager.MAP_LAYER_SECURITY, true);
    }

    public void changeSecurityLayerVisibility(boolean value)
    {
        SharedPreferences.Editor editor = sSharedPrefsMapLayers.edit();
        editor.putBoolean(SharedPrefsManager.MAP_LAYER_SECURITY, value);
        editor.commit();
    }


    /*  MAP LAYERS - Health functions
    ************************************************************************/
    public boolean getHealthLayerVisibility()
    {
        return sSharedPrefsMapLayers.getBoolean(SharedPrefsManager.MAP_LAYER_HEALTH, true);
    }

    public void changeHealthLayerVisibility(boolean value)
    {
        SharedPreferences.Editor editor = sSharedPrefsMapLayers.edit();
        editor.putBoolean(SharedPrefsManager.MAP_LAYER_HEALTH, value);
        editor.commit();
    }


    /*  MAP LAYERS - High grade functions
    ************************************************************************/
    public boolean getHighGradeLayerVisibility()
    {
        return sSharedPrefsMapLayers.getBoolean(SharedPrefsManager.MAP_LAYER_HIGH_GRADE, true);
    }

    public void changeHighGradeLayerVisibility(boolean value)
    {
        SharedPreferences.Editor editor = sSharedPrefsMapLayers.edit();
        editor.putBoolean(SharedPrefsManager.MAP_LAYER_HIGH_GRADE, value);
        editor.commit();
    }


    /*  MAP LAYERS - Medium grade functions
    ************************************************************************/
    public boolean getMediumGradeLayerVisibility()
    {
        return sSharedPrefsMapLayers.getBoolean(SharedPrefsManager.MAP_LAYER_MEDIUM_GRADE, true);
    }

    public void changeMediumGradeLayerVisibility(boolean value)
    {
        SharedPreferences.Editor editor = sSharedPrefsMapLayers.edit();
        editor.putBoolean(SharedPrefsManager.MAP_LAYER_MEDIUM_GRADE, value);
        editor.commit();
    }


    /*  MAP LAYERS - Low grade functions
    ************************************************************************/
    public boolean getLowGradeLayerVisibility()
    {
        return sSharedPrefsMapLayers.getBoolean(SharedPrefsManager.MAP_LAYER_LOW_GRADE, true);
    }

    public void changeLowGradeLayerVisibility(boolean value)
    {
        SharedPreferences.Editor editor = sSharedPrefsMapLayers.edit();
        editor.putBoolean(SharedPrefsManager.MAP_LAYER_LOW_GRADE, value);
        editor.commit();
    }


    /***********************************************************************
    *  SURVEY functions
    ************************************************************************/

    private void configureSurveySharedPreferences()
    {
        Log.i(TAG, "configureMapLayersSharedPreferences");

        sSharedPrefsSurvey = mAppContext.getSharedPreferences(SURVEY_SHARED_PREFS, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor;
        if (!sSharedPrefsSurvey.contains(SURVEY_COMPLETE))
        {
            editor = sSharedPrefsSurvey.edit();
            editor.putBoolean(SURVEY_COMPLETE, false);
            editor.commit();
        }

        if (!sSharedPrefsSurvey.contains(SURVEY_EXPIRED))
        {
            editor = sSharedPrefsSurvey.edit();
            editor.putBoolean(SURVEY_EXPIRED, false);
            editor.commit();
        }
    }

    public void checkForCompleteSurvey(boolean value)
    {
        SharedPreferences.Editor editor = sSharedPrefsSurvey.edit();
        editor.putBoolean(SharedPrefsManager.SURVEY_COMPLETE, value);
        editor.commit();
    }

    public void checkForExpiredSurvey(boolean value)
    {
        SharedPreferences.Editor editor = sSharedPrefsSurvey.edit();
        editor.putBoolean(SharedPrefsManager.SURVEY_EXPIRED, value);
        editor.commit();
    }

    /***********************************************************************
    *  REPORT functions
    ************************************************************************/

    private void configureReportSharedPreferences()
    {
        Log.i(TAG, "configureMapLayersSharedPreferences");

        sSharedPrefsReport = mAppContext.getSharedPreferences(REPORT_SHARED_PREFS, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor;
        if (!sSharedPrefsReport.contains(REPORT_MADE))
        {
            editor = sSharedPrefsReport.edit();
            editor.putBoolean(REPORT_MADE, false);
            editor.commit();
        }
    }

    public void checkForReportsMade(boolean value)
    {
        SharedPreferences.Editor editor = sSharedPrefsReport.edit();
        editor.putBoolean(SharedPrefsManager.REPORT_MADE, value);
        editor.commit();
    }
}
