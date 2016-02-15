package williamgbranco.comune.manager;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import williamgbranco.comune.callback.GetPublicInstitutionCallbacks;
import williamgbranco.comune.database.DatabaseHelper;
import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.server.ServerRequests;

/**
 * Created by William on 12/07/2015.
 */
public class PublicInstitutionManager
{
    private static final String TAG = "PublicInstitutionManagr";

    private Context mAppContext;
    private static PublicInstitutionManager sPublicInstitutionManager;

    private PublicInstitution mCurrentInstitution;
    private ArrayList<PublicInstitution> mPublicInstitutions;
    private ArrayList<PublicInstitution> mFoundPublicInstitutions;
    //private ArrayList<PublicInstitution> mStoredPublicInstitutions;
    private HashMap<PublicInstitution, Integer> mNumberReportsForInstitutionHashMap;


    public static PublicInstitutionManager get(Context c)
    {
        if ((sPublicInstitutionManager == null) && (c != null)) {
            sPublicInstitutionManager = new PublicInstitutionManager(c.getApplicationContext());
        }
        return sPublicInstitutionManager;
    }

    private PublicInstitutionManager(Context pAppContext)
    {
        mAppContext = pAppContext;
    }

    public void setPublicInstitutions(ArrayList<PublicInstitution> pPublicInstitutions)
    {
        if ((pPublicInstitutions != null) && (pPublicInstitutions.size() > 0))
            mPublicInstitutions = pPublicInstitutions;
    }

    /*public void addPublicInstitution(PublicInstitution pPublicInstitution)
    {
        if (mPublicInstitutions == null) {
            mPublicInstitutions = new ArrayList<>();
        }

        PublicInstitution institution = getPublicInstitutionById(pPublicInstitution.getId());

        if (institution == null)
        {
            mPublicInstitutions.add(pPublicInstitution);
        }
    }*/

    public ArrayList<PublicInstitution> getPublicInstitutions()
    {
        return mPublicInstitutions;
    }

    public PublicInstitution getPublicInstitutionById(Integer id)
    {
        if (mPublicInstitutions != null)
        {
            for (PublicInstitution ps : mPublicInstitutions)
            {
                if (ps.getId().equals(id)) {
                    return ps;
                }
            }
        }

        return null;
    }

    public void removePublicInstitutionById(Integer id)
    {
        if (mPublicInstitutions != null) {
            for (PublicInstitution ps : mPublicInstitutions) {
                if (ps.getId().equals(id)) {
                    mPublicInstitutions.remove(ps);
                }
            }
        }
    }

    public void resetNumberReportsForInstitutionHashMap()
    {
        Log.i(TAG, "resetNumberReportsForInstitutionHashMap()");
        mNumberReportsForInstitutionHashMap = null;
    }

    public PublicInstitution addNumberReportsForInstitution(PublicInstitution pInstitution, Integer numberReports)
    {
        PublicInstitution added = null;

        if (mNumberReportsForInstitutionHashMap == null)
            mNumberReportsForInstitutionHashMap = new HashMap<>();

        PublicInstitution institution = getReportedInstitutionById(pInstitution.getId());
        if (institution == null)
        {
            institution = getPublicInstitutionById(pInstitution.getId());

            if (institution == null)
            {
                mNumberReportsForInstitutionHashMap.put(pInstitution, numberReports);
                added = pInstitution;
            }
            else
            {
                Log.i(TAG, "addNumberReportsForInstitution( institution id: " + institution.getId() + ", number of reports: " + numberReports+")");
                mNumberReportsForInstitutionHashMap.put(institution, numberReports);
                added = institution;
            }
        }
        else
        {
            mNumberReportsForInstitutionHashMap.put(institution, numberReports);
            added = institution;
        }

        return added;
    }

    public int getNumberReportsForInstitution(PublicInstitution pInstitution)
    {
        try
        {
            Log.i(TAG, "getNumberReportsForInstitution: " + pInstitution.getId());
            int number = mNumberReportsForInstitutionHashMap.get(pInstitution);
            Log.i(TAG, "getNumberReportsForInstitution, number: " + number);
            return number;
        }
        catch (Exception e) {}

        return 0;
    }

    public PublicInstitution getReportedInstitutionById(Integer instId)
    {
        if (mNumberReportsForInstitutionHashMap != null)
        {
            Log.i(TAG, "getReportedInstitutionById("+instId+")");

            for (PublicInstitution institution : mNumberReportsForInstitutionHashMap.keySet())
            {
                if (institution.getId().equals(instId))
                {
                    Log.i(TAG, "getReportedInstitutionById(" + instId + ") ACHOU");
                    return institution;
                }
            }
        }

        return null;
    }

    public ArrayList<PublicInstitution> getFoundPublicInstitutions()
    {
        return mFoundPublicInstitutions;
    }

    public PublicInstitution getFoundPublicInstitutionById(Integer id)
    {
        if (mFoundPublicInstitutions != null)
        {
            for (PublicInstitution fps : mFoundPublicInstitutions)
            {
                if (fps.getId().equals(id)) {
                    return fps;
                }
            }
        }

        return null;
    }

    public void addFoundPublicInstitutions(PublicInstitution pInstitution)
    {
        if (mFoundPublicInstitutions == null)
            mFoundPublicInstitutions = new ArrayList<PublicInstitution>();

        mFoundPublicInstitutions.add(pInstitution);
    }

    public void setFoundPublicInstitutions(ArrayList<PublicInstitution> pFoundPublicInstitutions)
    {
        mFoundPublicInstitutions = pFoundPublicInstitutions;
    }

    public PublicInstitution getCurrentInstitution() {
        return mCurrentInstitution;
    }

    public void setCurrentInstitution(PublicInstitution pCurrentInstitution) {
        mCurrentInstitution = pCurrentInstitution;
    }

    /* public ArrayList<PublicInstitution> getStoredPublicInstitutions()
    {
        return mStoredPublicInstitutions;
    }

    public PublicInstitution getStoredPublicInstitutionById(Integer id)
    {
        if (mStoredPublicInstitutions != null)
        {
            for (PublicInstitution sps : mStoredPublicInstitutions)
            {
                if (sps.getId().equals(id)) {
                    return sps;
                }
            }
        }

        return null;
    }

    public void addStoredPublicInstitution(PublicInstitution pInstitution)
    {
        if (mStoredPublicInstitutions == null)
            mStoredPublicInstitutions = new ArrayList<>();

        mStoredPublicInstitutions.add(pInstitution);
    }

    public void setStoredPublicInstitutions(ArrayList<PublicInstitution> pStoredPublicInstitutions)
    {
        mStoredPublicInstitutions = pStoredPublicInstitutions;
    }*/

    public void resetManager()
    {
        //mPublicInstitutions.clear();
        mPublicInstitutions = null;
        //mFoundPublicInstitutions.clear();
        mFoundPublicInstitutions = null;
        //mStoredPublicInstitutions = null;
        //mNumberReportsForInstitutionHashMap.clear();
        mNumberReportsForInstitutionHashMap = null;
        sPublicInstitutionManager = null;
    }


    /*
    *  Database Functions
    * ***********************************************************************/

    protected void deleteInstitution(Integer instId)
    {
        DatabaseHelper mHelper = new DatabaseHelper(mAppContext);
        //table_report, table_institution_survey,
        // non-important: (se n está na tbl_inst_survey não está nas respostas) TABLE_RATING_QUESTION_ANSWER, TABLE_YES_NO_QUESTION_ANSWER, TABLE_RADIOBUTTON_QUESTION_ANSWER, TABLE_CHECKBOX_QUESTION_ANSWER
        DatabaseHelper.InstitutionSurveyCursor institutionSurveyCursor = mHelper.querySurveysForInstitutionForAnyUser(instId);
        if (institutionSurveyCursor.getCount() > 0)
        {
            institutionSurveyCursor.close();
            return;
        }
        institutionSurveyCursor.close();

        DatabaseHelper.ReportCursor institutionReportCursor = mHelper.queryReportsForInstitutionForAnyUser(instId);
        if (institutionReportCursor.getCount() > 0)
        {
            institutionReportCursor.close();
            return;
        }
        institutionReportCursor.close();

        mHelper.deleteInstitutionById(instId);
    }


    /*
    *  Server Functions
    * ***********************************************************************/

    public void fetchPublicInstitutionById(Integer pInstId, GetPublicInstitutionCallbacks pInstCallbacks)
    {
        new ServerRequests(mAppContext).fetchPublicInstitutionById(pInstId, pInstCallbacks);
    }

}
