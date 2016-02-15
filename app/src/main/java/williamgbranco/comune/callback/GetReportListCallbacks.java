package williamgbranco.comune.callback;

import java.util.ArrayList;

import williamgbranco.comune.report.Report;

/**
 * Created by William Gomes de Branco on 13/09/2015.
 */
public interface GetReportListCallbacks
{
    void done(ArrayList<Report> returnedReports);
}
