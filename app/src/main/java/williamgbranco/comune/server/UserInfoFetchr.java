package williamgbranco.comune.server;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import williamgbranco.comune.callback.AuthorizeUserCallback;
import williamgbranco.comune.callback.DownloadedFileCallbacks;
import williamgbranco.comune.callback.GetUserCallback;
import williamgbranco.comune.callback.UploadedFileCallbacks;
import williamgbranco.comune.manager.UserManager;
import williamgbranco.comune.server.httpUtility.HttpDownloadUtility;
import williamgbranco.comune.server.httpUtility.MultipartUtility;
import williamgbranco.comune.user.User;
import williamgbranco.comune.util.Constants;

/**
 * Created by William on 21/11/2015.
 */
public class UserInfoFetchr
{
    private static final String TAG = "comune.UserInfoFetchr";

    private Context mAppContext;
    private UserManager mUserManager;
    //private DatabaseHelper mHelper;
    private SingletonRequestQueue mSingletonRequestQueue;


    public UserInfoFetchr(Context pAppContext)
    {
        mAppContext = pAppContext;
        mUserManager = UserManager.get(mAppContext);
        //mHelper = new DatabaseHelper(mAppContext);
        mSingletonRequestQueue = SingletonRequestQueue.getInstance(mAppContext);
    }


    //url: http://localhost:8080/comuneWS/users/authenticateUser
    public void authenticateUser(String email, String password, final AuthorizeUserCallback pCallbacks)
    {
        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_AUTHENTICATE_USER;
        Log.i(TAG, "authenticateUser, URL: " + url);

        try
        {
            JSONObject jsonObjectParams = new JSONObject();
            jsonObjectParams.put("email", email);
            jsonObjectParams.put("password", password);

            final JsonObjectRequest jsonObjectReq = new JsonObjectRequest(Request.Method.POST, url, jsonObjectParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    User returnedUser = null;

                    try
                    {
                        returnedUser = new User();
                        returnedUser.setUserId(response.getInt("id"));
                        returnedUser.setFirstName(response.getString("firstName"));
                        returnedUser.setLastName(response.getString("lastName"));
                        GregorianCalendar date = new GregorianCalendar();
                        date.setTimeInMillis(response.getLong("dateOfBirth"));
                        returnedUser.setDateOfBirth(date);
                        returnedUser.setEmail(response.getString("email"));
                        returnedUser.setPhoneNumber(response.getString("mobileNumber"));
                        returnedUser.setPictureId(response.getInt("idPhoto"));

                        pCallbacks.done(returnedUser);

                        Log.i(TAG, "authenticateUser, OK");
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG, "authenticateUser, JSONException: " + e.toString());
                        pCallbacks.onServerError();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        pCallbacks.onUnauthorizedAccess();
                    }
                    else {
                        Log.i(TAG, "authenticateUser, onErrorResponse: " + error.toString());
                        pCallbacks.onServerError();
                    }
                }
            }) {
                /**
                 * Passing some request headers
                 * */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

            jsonObjectReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            mSingletonRequestQueue.addToRequestQueue(jsonObjectReq);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //url: http://localhost:8080/comuneWS/users/registerUser
    public void registerUser(final User newUser, final GetUserCallback pCallbacks)
    {
        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_REGISTER_USER;
        Log.i(TAG, "registerUser, URL: " + url);

        try
        {
            JSONObject jsonObjectParams = new JSONObject();
            jsonObjectParams.put("firstName", newUser.getFirstName());
            jsonObjectParams.put("lastName", newUser.getLastName());
            jsonObjectParams.put("dateOfBirth", newUser.getDateOfBirth().getTimeInMillis());
            jsonObjectParams.put("email", newUser.getEmail());
            jsonObjectParams.put("password", newUser.getPassword());
            jsonObjectParams.put("mobileNumber", newUser.getPhoneNumber());

            final JsonObjectRequest jsonObjectReq = new JsonObjectRequest(Request.Method.POST, url, jsonObjectParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    try
                    {
                        newUser.setUserId(response.getInt("id"));

                        if (newUser.getPictureUri() != null)
                        {
                            mUserManager.uploadUserPhoto(newUser, new UploadedFileCallbacks() {
                                @Override
                                public void done(Integer ownerId, Integer fileId, String fileName)
                                {
                                    try {
                                        if (fileId != null)
                                            newUser.setPictureId(fileId);
                                        pCallbacks.done(newUser);
                                    }
                                    catch (Exception e)
                                    {
                                        Log.e(TAG, e.toString());
                                    }
                                }

                                @Override
                                public void onNetworkNonAvailable() {
                                    pCallbacks.onNetworkNonAvailable();
                                }
                            });
                        }
                        else
                        {
                            pCallbacks.done(newUser);
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG, "registerUser, JSONException: " + e.toString());
                        pCallbacks.done(null);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pCallbacks.done(null);
                }
            }) {
                /**
                 * Passing some request headers
                 * */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

            jsonObjectReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            mSingletonRequestQueue.addToRequestQueue(jsonObjectReq);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //url: http://localhost:8080/comuneWS/reports/uploadUserPhoto
    public void uploadUserPhoto(User user, UploadedFileCallbacks pCallbacks)
    {
        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_UPLOAD_USER_PHOTO;

        if ((user != null) && (user.getPictureUri() != null))
        {
            String charset = "UTF-8";
            File file = new File(user.getPictureUri().getPath());

            try {
                MultipartUtility multipart = new MultipartUtility(url, charset, file.length());

                /*multipart.addHeaderField("User-Agent", "CodeJava");
                multipart.addHeaderField("Test-Header", "Header-Value");*/

                multipart.addFormField("userId", String.valueOf(user.getUserId()));
                multipart.addFilePart("file", file);

                String response = multipart.finish();
                Log.i(TAG, "uploadUserPhoto, SERVER REPLIED:" + response);

                JSONObject jsonResponse = new JSONObject(response);
                int ownerId = jsonResponse.getInt("ownerId");
                int fileId = jsonResponse.getInt("fileId");
                String filename = jsonResponse.getString("fileName");

                pCallbacks.done(ownerId, fileId, filename);
            }
            catch (IOException ex) {
                pCallbacks.done(null, null, null);
            }
            catch (JSONException e) {
                e.printStackTrace();
                pCallbacks.done(null, null, null);
            }
        }
    }


    //url: http://localhost:8080/comuneWS/users/downloadUserPhoto?id=<photo_id>
    public void downloadUserPhoto(User user, String directory, DownloadedFileCallbacks pCallbacks)
    {
        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_DOWNLOAD_USER_PHOTO;

        if ((user != null) && (user.getPictureId() != -1))
        {
            url = url + "?id=" + user.getPictureId();

            try
            {
                HttpDownloadUtility.downloadFile(Constants.CODE_DOWNLOAD_PICTURE, url, directory, pCallbacks);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else
        {
            pCallbacks.done(null, null, -2, null, null);
        }
    }


}
