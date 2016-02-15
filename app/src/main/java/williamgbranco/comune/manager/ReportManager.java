package williamgbranco.comune.manager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import williamgbranco.comune.callback.GetInstitutionListCallbacks;
import williamgbranco.comune.callback.GetReportCallback;
import williamgbranco.comune.callback.GetReportListCallbacks;
import williamgbranco.comune.callback.SentReportCallback;
import williamgbranco.comune.callback.UploadedFileCallbacks;
import williamgbranco.comune.database.DatabaseHelper;
import williamgbranco.comune.report.Report;
import williamgbranco.comune.server.ServerRequests;
import williamgbranco.comune.service.ReportMadeService;
import williamgbranco.comune.user.User;
import williamgbranco.comune.util.Constants;

/**
 * Created by William Gomes de Branco on 30/08/2015.
 */
public class ReportManager
{
    private static final String TAG = "comune.ReportManager";

    private static ReportManager sReportManager;
    private Context mAppContext;
    private DatabaseHelper mHelper;
    //private Report mCurrentReport;
    private static ArrayList<Report> mFetchedReports;
    //private static HashMap<Report, Integer> mRespondedReportHashMap;
    //private HashMap<Report, Report> mReportResponseHashMap;


    public static ReportManager get(Context c)
    {
        if ((sReportManager == null) && (c != null)) {
            sReportManager = new ReportManager(c.getApplicationContext());
        }
        return sReportManager;
    }

    private ReportManager(Context pAppContext)
    {
        mAppContext = pAppContext;
        mHelper = new DatabaseHelper(mAppContext);
    }

    public void resetManager()
    {
        //mFetchedReports.clear();
        mFetchedReports = null;
        sReportManager = null;
    }

    public void insertReport(Report pReport)
    {
        new InsertReportIntoDatabase(pReport).execute();
    }

    private class InsertReportIntoDatabase extends AsyncTask<Void, Void, Long>
    {
        Report mReport;

        public InsertReportIntoDatabase(Report pReport)
        {
            mReport = pReport;
        }

        @Override
        protected Long doInBackground(Void... params)
        {
            return mHelper.insertReport(mReport);
        }

        @Override
        protected void onPostExecute(Long result)
        {
            if (result != Constants.ERROR_ON_INSERT)
            {
                Intent i = new Intent(mAppContext, ReportMadeService.class);
                mAppContext.getApplicationContext().startService(i);
            }
        }
    }

    public boolean hasReportsMade()
    {
        boolean hasReportsMade = false;
        DatabaseHelper.ReportCursor reportCursor = mHelper.queryReportsMade();

        reportCursor.moveToFirst();
        if (!reportCursor.isAfterLast())
        {
            hasReportsMade = true;
        }
        reportCursor.close();

        return hasReportsMade;
    }

    public void fetchReportById(User pUser, Integer pReportId, GetReportCallback pCallbacks, boolean isResponse)
    {
        Report returnedReport = getReportById(pReportId);

        if (returnedReport != null)
        {
            Log.i(TAG, "report " + pReportId + "já baixado!");
            pCallbacks.done(returnedReport);
        }
        else
        {
            Log.i(TAG, "report " + pReportId + " será baixado!");

            if (!isResponse) {
                new ServerRequests(mAppContext).fetchReportById(pUser, pReportId, pCallbacks);
            }
            else
            {
                new ServerRequests(mAppContext).fetchReportResponseById(pUser, pReportId, pCallbacks);
            }
        }
    }

    public void fetchInstitutionsWithReportsSubmittedByUser(User pUser, GetInstitutionListCallbacks pCallbacks)
    {
        new ServerRequests(mAppContext).fetchInstitutionsWithReportsSubmittedByUser(pUser, pCallbacks);
    }

    public void fetchReportsSubmittedToInstitution(User pUser, Integer pInstID, GetReportListCallbacks pCallbacks)
    {
        new ServerRequests(mAppContext).fetchReportsSubmittedForInstitution(pUser, pInstID, pCallbacks);
    }

    public void resetFetchedReports()
    {
        mFetchedReports = null;
    }

    public Report addFetchedReport(Report report)
    {
        if (mFetchedReports == null)
            mFetchedReports = new ArrayList<>();

        Report aux = getReportById(report.getId());
        if (aux == null)
        {
            mFetchedReports.add(report);

            return report;
        }

        return aux;
    }

    public Report getReportById(Integer id)
    {
        if (mFetchedReports != null)
        {
            for (Report report : mFetchedReports)
            {
                if (report.getId().equals(id))
                {
                    return report;
                }
            }
        }

        return null;
    }

    public int deleteReportForUser(Integer userId, Integer instId, Integer reportId)
    {
        return mHelper.deleteReportForUser(userId, instId, reportId);
    }

    public int deleteReportById(Integer reportId)
    {
        return mHelper.deleteReportById(reportId);
    }

    public void uploadReportToServer(Report pReport, SentReportCallback pCallbacks)
    {
        new ServerRequests(mAppContext).uploadReportToServer(pReport, pCallbacks);
    }

    public void uploadReportPicture(Report pReport, UploadedFileCallbacks pCallbacks)
    {
        new ServerRequests(mAppContext).uploadReportPicture(pReport, pCallbacks);
    }

    public void uploadReportFootage(Report pReport, UploadedFileCallbacks pCallbacks)
    {
        new ServerRequests(mAppContext).uploadReportFootage(pReport, pCallbacks);
    }

    public void markResponseAsVisualized(int reportId, int responseId)
    {
        new ServerRequests(mAppContext).markResponseAsVisualized(reportId, responseId);
    }

}
