package williamgbranco.comune.user;

import android.net.Uri;

import java.util.GregorianCalendar;

/**

 * Created by William on 25/04/2015.
 */
public class User
{
    //public static final int LOGIN_WITH_USER_ACCOUNT = 0;
    //public static final int LOGIN_VIA_FACEBOOK = 1;

    private Integer mUserId;
    private String mEmail;
    private String mPassword;
    private String mFirstName;
    private String mLastName;
    private String mPhoneNumber;
    private GregorianCalendar mDateOfBirth;
    //private Integer mLoginType;
    //private String mFbUserId;
    private String mGender;
    private int pictureId;
    private Uri mPictureUri;

    private boolean infoSentToServer;
    private boolean pictureSentToServer;


    public User()
    {
        setPictureId(-1);
    }

    public User(Integer pUserId)
    {
        mUserId = pUserId;
        setPictureId(-1);
    }

    public Integer getUserId()
    {
        return mUserId;
    }

    public void setUserId(Integer pUserId)
    {
        mUserId = pUserId;
    }

    /*public Integer getLoginType() {
        return mLoginType;
    }

    public void setLoginType(Integer pLoginType) {
        mLoginType = pLoginType;
    }*/

    /*public String getFbUserId() {
        return mFbUserId;
    }

    public void setFbUserId(String pFbUserId) {
        mFbUserId = pFbUserId;
    }*/

    /*public void setSucess(Boolean pSucess) {
        mSucess = pSucess;
    }*/

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String pFirstName) {
        mFirstName = pFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String pLastName) {
        mLastName = pLastName;
    }

    public String getPhoneNumber()
    {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String pPhoneNumber)
    {
        mPhoneNumber = pPhoneNumber;
    }

    public GregorianCalendar getDateOfBirth() {
        return mDateOfBirth;
    }

    public void setDateOfBirth(GregorianCalendar pDateOfBirth) {
        mDateOfBirth = pDateOfBirth;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String pEmail) {
        mEmail = pEmail;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String pPassword) {
        mPassword = pPassword;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String pGender) {
        mGender = pGender;
    }

    public int getPictureId() {
        return pictureId;
    }

    public void setPictureId(int pPictureId) {
        pictureId = pPictureId;
    }

    public Uri getPictureUri() {
        return mPictureUri;
    }

    public void setPictureUri(Uri pPictureUri) {
        mPictureUri = pPictureUri;
    }


    public boolean isInfoSentToServer() {
        return infoSentToServer;
    }

    public void setInfoSentToServer(boolean pInfoSentToServer) {
        infoSentToServer = pInfoSentToServer;
    }

    public boolean isPictureSentToServer() {
        return pictureSentToServer;
    }

    public void setPictureSentToServer(boolean pPictureSentToServer) {
        pictureSentToServer = pPictureSentToServer;
    }
}
