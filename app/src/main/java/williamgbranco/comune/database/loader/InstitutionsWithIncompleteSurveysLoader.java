package williamgbranco.comune.database.loader;

import android.content.Context;

import java.util.HashMap;

import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.manager.SurveyManager;

/**
 * Created by William Gomes de Branco on 16/08/2015.
 */
public class InstitutionsWithIncompleteSurveysLoader extends DataLoader<HashMap<PublicInstitution, Integer>>
{
    public InstitutionsWithIncompleteSurveysLoader(Context context)
    {
        super(context);
    }

    @Override
    public HashMap<PublicInstitution, Integer> loadInBackground()
    {
        return SurveyManager.get(getContext()).queryInstitutionsWithIncompleteSurveys();
    }
}
