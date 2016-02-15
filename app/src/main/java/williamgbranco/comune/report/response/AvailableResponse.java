package williamgbranco.comune.report.response;

/**
 * Created by William Gomes de Branco on 08/10/2015.
 */
public class AvailableResponse
{
    private Integer mInstitutionId;
    private Integer mReportId;
    private Integer mResponseId;

    public AvailableResponse(Integer pInstitutionId, Integer pReportId, Integer pResponseId)
    {
        mInstitutionId = pInstitutionId;
        mReportId = pReportId;
        mResponseId = pResponseId;
    }

    public Integer getInstitutionId()
    {
        return mInstitutionId;
    }

    public Integer getReportId()
    {
        return mReportId;
    }

    public Integer getResponseId()
    {
        return mResponseId;
    }
}
