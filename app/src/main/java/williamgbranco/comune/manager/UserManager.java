package williamgbranco.comune.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import williamgbranco.comune.callback.AuthorizeUserCallback;
import williamgbranco.comune.callback.GetUserCallback;
import williamgbranco.comune.callback.UploadedFileCallbacks;
import williamgbranco.comune.database.DatabaseHelper;
import williamgbranco.comune.server.ServerRequests;
import williamgbranco.comune.user.User;

/**
 * Created by William Gomes de Branco on 07/09/2015.
 */
public class UserManager
{
    private static final String LOGGED_IN_USER_SHARED_PREFS = "logged_in_user_shared_prefs";
    private static final String KEY_LOGGED_IN_USER_ID = "logged_in_user_id";

    private static final String TAG = "comune.UserManager";

    private static UserManager sUserManager;
    private User mCurrentUser;
    private Context mAppContext;
    private DatabaseHelper mHelper;

    private SharedPreferences mSharedPrefs;
    private GetUserCallback mLoginCallback;


    public static UserManager get(Context c)
    {
        if (sUserManager == null) {
            sUserManager = new UserManager(c.getApplicationContext());
        }
        return sUserManager;
    }

    private UserManager(Context pAppContext)
    {
        mAppContext = pAppContext.getApplicationContext();
    }

    public void getLoggedInUser(GetUserCallback pCallback)
    {
        Log.i(TAG, "getLoggedInUser");

        initialize();

        if (mCurrentUser == null)
        {
            getLoggedInUserInfo(pCallback);
        }
        else
        {
            pCallback.done(mCurrentUser);
        }
    }

    public void initialize()
    {
        Log.i(TAG, "initialize");

        if (mAppContext != null) {
            Log.i(TAG, "initialize context not null");

            mHelper = new DatabaseHelper(mAppContext);
            mSharedPrefs = mAppContext.getSharedPreferences(LOGGED_IN_USER_SHARED_PREFS, Context.MODE_PRIVATE);
        }
    }

    public boolean userIsLoggedIn()
    {
        return (getLoggedInUserId() > -1);
    }

    private int getLoggedInUserId()
    {
        return mSharedPrefs.getInt(KEY_LOGGED_IN_USER_ID, -1);
    }

    private void getLoggedInUserInfo(final GetUserCallback pCallback)
    {
        int userId = getLoggedInUserId();

        if (userId > -1)
        {
            new QueryUserByIdAsyncTask(userId, new GetUserCallback() {
                @Override
                public void done(User returnedUser)
                {
                    mCurrentUser = returnedUser;
                    Log.i(TAG, "returned user: " + mCurrentUser.getEmail() + ", " + mCurrentUser.getFirstName());
                    pCallback.done(mCurrentUser);
                }

                @Override
                public void onNetworkNonAvailable() {}
            }).execute();
        }
        else
        {
            pCallback.done(null);
        }
    }

    public void setLoggedInUser(User pUser, GetUserCallback pLoginCallback)
    {
        mLoginCallback = pLoginCallback;

        new InsertUserAsyncTask(pUser, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser != null)
                {
                    setCurrentUser(returnedUser);

                    mLoginCallback.done(returnedUser);
                }
            }

            @Override
            public void onNetworkNonAvailable() {}
        }).execute();
    }

    private void setCurrentUser(User pCurrentUser)
    {
        Log.i(TAG, "setCurrentUser: " + pCurrentUser.getEmail() + ", " + pCurrentUser.getUserId());

        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putInt(KEY_LOGGED_IN_USER_ID, pCurrentUser.getUserId());
        editor.commit();

        mCurrentUser = pCurrentUser;
    }

    public void logOut()
    {
        Log.i(TAG, "log out");

        if (mSharedPrefs != null)
        {
            SharedPreferences.Editor editor = mSharedPrefs.edit();
            editor.putInt(KEY_LOGGED_IN_USER_ID, -1);
            editor.commit();
        }

        mCurrentUser = null;
    }

    public User getCurrentUser()
    {
        return mCurrentUser;
    }

    public void getUserByLoginInfo(String email, GetUserCallback getUserCallback)
    {
        new QueryUserByLoginInfo(email, getUserCallback).execute();
    }

    public void fetchUserDataInBackground(String email, String password, AuthorizeUserCallback pCallback)
    {
        new ServerRequests(mAppContext).fetchUserInfoInBackground(email, password, pCallback);
    }

    public void registerNewUser(User newUser, GetUserCallback getUserCallback)
    {
        new ServerRequests(mAppContext).registerNewUser(newUser, getUserCallback);
    }

    public void uploadUserPhoto(User pUser, UploadedFileCallbacks pCallbacks)
    {
        new ServerRequests(mAppContext).uploadUserPhoto(pUser, pCallbacks);
    }

    public void updateUserPhotoUri(Uri fileUri)
    {
        if (mCurrentUser != null)
        {
            mCurrentUser.setPictureUri(fileUri);
            mHelper.updateUserPhotoUri(mCurrentUser);
        }
    }

    private class InsertUserAsyncTask extends AsyncTask<Void, Void, Long>
    {
        User mUser;
        GetUserCallback mGetUserCallback;

        public InsertUserAsyncTask(User pUser, GetUserCallback pGetUserCallback)
        {
            mUser = pUser;
            mGetUserCallback = pGetUserCallback;
        }

        @Override
        protected Long doInBackground(Void... params)
        {
            return mHelper.insertUser(mUser);
        }

        @Override
        protected void onPostExecute(Long result)
        {
            Log.i(TAG, "InsertUserAsyncTask result:" + result);

            //if (result != -1)
            mGetUserCallback.done(mUser);

            super.onPostExecute(result);
        }
    }


    private class QueryUserByIdAsyncTask extends AsyncTask<Void, Void, User>
    {
        Integer mUserId;
        GetUserCallback mGetUserCallback;

        public QueryUserByIdAsyncTask(int pUserId, GetUserCallback pGetUserCallback)
        {
            mUserId = pUserId;
            mGetUserCallback = pGetUserCallback;
        }

        @Override
        protected User doInBackground(Void... params)
        {
            User returnedUser = null;

            DatabaseHelper.UserCursor userCursor = mHelper.queryUserById(mUserId);

            userCursor.moveToFirst();
            if (!userCursor.isAfterLast())
            {
                returnedUser = userCursor.getUser();
            }
            userCursor.close();

            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser)
        {
            mGetUserCallback.done(returnedUser);
        }
    }


    private class QueryUserByLoginInfo extends AsyncTask<Void, Void, User>
    {
        String mEmail;
        GetUserCallback mGetUserCallback;

        public QueryUserByLoginInfo(String email, GetUserCallback pGetUserCallback)
        {
            mEmail = email;
            mGetUserCallback = pGetUserCallback;
        }

        @Override
        protected User doInBackground(Void... params)
        {
            User returnedUser = null;

            DatabaseHelper.UserCursor userCursor = mHelper.queryUserByLoginInfo(mEmail);

            userCursor.moveToFirst();
            if (!userCursor.isAfterLast())
            {
                returnedUser = userCursor.getUser();
            }
            userCursor.close();

            return returnedUser;

        }

        @Override
        protected void onPostExecute(User returnedUser)
        {
            mGetUserCallback.done(returnedUser);

            super.onPostExecute(returnedUser);
        }
    }


    /*
    *  Database Functions
    * ***********************************************************************/
    protected void deleteUser(Integer userId)
    {
        Log.i(TAG, "deleting user: "+userId);

        Integer currentUserId = UserManager.get(mAppContext).getCurrentUser().getUserId();

        if (!currentUserId.equals(userId))
        {
            DatabaseHelper.InstitutionSurveyCursor userSurveyCursor = mHelper.querySurveysForUser(userId);
            if (userSurveyCursor.getCount() > 0)
            {
                userSurveyCursor.close();
                return;
            }
            userSurveyCursor.close();


            DatabaseHelper.ReportCursor userReportCursor = mHelper.queryReportsForUser(userId);
            if (userReportCursor.getCount() > 0)
            {
                userReportCursor.close();
                return;
            }
            userReportCursor.close();

            mHelper.deleteUserById(userId);

            Log.i(TAG, "user deleted: " + userId);

        }
    }


}
