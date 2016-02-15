package williamgbranco.comune.callback;

import williamgbranco.comune.report.Report;

/**
 * Created by William on 13/09/2015.
 */
public interface GetReportCallback
{
    public abstract void done(Report returnedReport);
}
