package williamgbranco.comune.database.loader;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import williamgbranco.comune.manager.SurveyManager;
import williamgbranco.comune.survey.Survey;

/**
 * Created by William Gomes de Branco on 16/08/2015.
 */
public class SurveysForInstitutionLoader extends DataLoader<ArrayList<Survey>>
{
    private static final String TAG = "SurveysForInstLoader";

    private Integer mInstId;

    public SurveysForInstitutionLoader(Context context, Integer pInstId)
    {
        super(context);
        mInstId = pInstId;
    }

    @Override
    public ArrayList<Survey> loadInBackground()
    {
        ArrayList<Survey> returnedSurveys = null;

        try {
             returnedSurveys = SurveyManager.get(getContext()).querySurveysForInstitution(mInstId);
        }catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        finally {
            return returnedSurveys;
        }
    }
}
