package williamgbranco.comune.report;

import android.content.Context;
import android.net.Uri;

import java.util.GregorianCalendar;

/**
 * Created by William Gomes de Branco on 30/08/2015.
 */
public class Report
{
    private static final String TAG = "comune.Report";

    private Context mAppContext;

    private Integer mId;
    private Integer mUserId;
    private Integer mPublicInstitutionId;
    private String mComment;
    private String mPicturePath;
    private Uri mPictureUri;
    private String mFootagePath;
    private Uri mFootageUri;
    private GregorianCalendar mMadeAt;
    private int mPictureId;
    private int mFootageId;
    private int mResponseId;
    private boolean mResponseVisualized;

    private Integer mIdOnServer;
    private boolean commentSentToServer;
    private boolean pictureSentToServer;
    private boolean footageSentToServer;


    public Report(Context pContext)
    {
        mAppContext = pContext.getApplicationContext();
        initializeIds();
    }

    public Report(Integer pUserId, Integer pInstd, Context pContext)
    {
        mMadeAt = new GregorianCalendar();
        mUserId = pUserId;
        mPublicInstitutionId = pInstd;
        mAppContext = pContext.getApplicationContext();
        initializeIds();
    }

    private void initializeIds()
    {
        setPictureId(-1);
        setFootageId(-1);
        setIdOnServer(-1);
    }

    public Integer getId()
    {
        return mId;
    }

    public void setId(Integer pId)
    {
        mId = pId;
    }

    public Integer getUserId()
    {
        return mUserId;
    }

    public void setUserId(Integer pUserId)
    {
        mUserId = pUserId;
    }

    public Integer getPublicInstitutionId()
    {
        return mPublicInstitutionId;
    }

    public void setPublicInstitutionId(Integer pPublicInstitutionId)
    {
        mPublicInstitutionId = pPublicInstitutionId;
    }

    public String getComment()
    {
        return mComment;
    }

    public void setComment(String pComment)
    {
        mComment = pComment;
    }

    public String getPicturePath()
    {
        return mPicturePath;
    }

    public Uri getPictureUri()
    {
        return mPictureUri;
    }

    public void setPicturePath(String pPicturePath)
    {
        mPicturePath = pPicturePath;
    }

    public void setPictureUri(Uri pPictureUri)
    {
        mPictureUri = pPictureUri;
        if (mPictureUri != null) setPicturePath(mPictureUri.getPath());
    }

    public String getFootagePath()
    {
        return mFootagePath;
    }

    public Uri getFootageUri()
    {
        return mFootageUri;
    }

    public void setFootagePath(String pFootagePath)
    {
        mFootagePath = pFootagePath;
    }

    public void setFootageUri(Uri pFootageUri)
    {
        mFootageUri = pFootageUri;
    }

    public void setMadeAt(GregorianCalendar pMadeAt)
    {
        if (mMadeAt == null)
            mMadeAt = pMadeAt;
    }

    public GregorianCalendar madeAt()
    {
        return mMadeAt;
    }

    public int getPictureId() {
        return mPictureId;
    }

    public void setPictureId(int pPictureId) {
        mPictureId = pPictureId;
    }

    public int getFootageId() {
        return mFootageId;
    }

    public void setFootageId(int pFootageId) {
        mFootageId = pFootageId;
    }

    public int getResponseId() {
        return mResponseId;
    }

    public void setResponseId(int pResponseId) {
        mResponseId = pResponseId;
    }

    public boolean isResponseVisualized() {
        return mResponseVisualized;
    }

    public void setResponseVisualized(boolean pResponseVisualized) {
        mResponseVisualized = pResponseVisualized;
    }

    public Integer getIdOnServer() {
        return mIdOnServer;
    }

    public void setIdOnServer(Integer pIdOnServer) {
        mIdOnServer = pIdOnServer;
    }

    public boolean isCommentSentToServer() {
        return commentSentToServer;
    }

    public void setCommentSentToServer(boolean pCommentSentToServer) {
        commentSentToServer = pCommentSentToServer;
    }

    public boolean isPictureSentToServer() {
        return pictureSentToServer;
    }

    public void setPictureSentToServer(boolean pPictureSentToServer) {
        pictureSentToServer = pPictureSentToServer;
    }

    public boolean isFootageSentToServer() {
        return footageSentToServer;
    }

    public void setFootageSentToServer(boolean pFootageSentToServer) {
        footageSentToServer = pFootageSentToServer;
    }

}
