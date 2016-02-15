package williamgbranco.comune.server;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import williamgbranco.comune.callback.GetInstitutionListCallbacks;
import williamgbranco.comune.callback.GetPublicInstitutionCallbacks;
import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.institution.PublicInstitutionEducacao;
import williamgbranco.comune.institution.PublicInstitutionSaude;
import williamgbranco.comune.institution.PublicInstitutionSeguranca;
import williamgbranco.comune.institution.WorkingDay;
import williamgbranco.comune.manager.PublicInstitutionManager;
import williamgbranco.comune.user.User;
import williamgbranco.comune.util.Constants;

/**
 * Created by William de Branco on 17/05/2015.
 */
public class PublicInstitutionFetchr
{
    private static final String TAG = "comune.PublicInstFetchr";

    private Context mAppContext;
    private PublicInstitutionManager mInstitutionManager;
    private SingletonRequestQueue mSingletonRequestQueue;


    public PublicInstitutionFetchr(Context pContext)
    {
        mAppContext = pContext.getApplicationContext();
        mInstitutionManager = PublicInstitutionManager.get(mAppContext);
        mSingletonRequestQueue = SingletonRequestQueue.getInstance(mAppContext);
    }


    // url: http://localhost:8080/comuneWS/places/getPlaceById?id=<instId>
    public void getPlaceById(Integer pInstitutionId, final GetPublicInstitutionCallbacks pCallbacks)
    {
        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_GET_PLACE_BY_ID;
        url = url + "?id=" + String.valueOf(pInstitutionId);
        Log.i(TAG, "getPlaceById, URL: " + url);

        final JsonObjectRequest jsonObjectReq = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                PublicInstitution institution = null;
                try
                {
                    JSONArray workingDaysJSON;
                    JSONObject workingDayJSON;

                    int id = response.getInt("id");
                    int category = response.getInt("category");

                    if (PublicInstitution.TIPO_EDUCACAO == category)
                    {
                        institution = new PublicInstitutionEducacao(id, mAppContext);
                    }
                    else if (PublicInstitution.TIPO_SAUDE == category)
                    {
                        institution = new PublicInstitutionSaude(id, mAppContext);
                    }
                    else if (PublicInstitution.TIPO_SEGURANCA == category)
                    {
                        institution = new PublicInstitutionSeguranca(id, mAppContext);
                    }

                    institution.setNome(response.getString("name"));
                    institution.setNomeAbreviado(response.getString("abbrevName"));
                    if (institution.getNomeAbreviado().toLowerCase().equals("null"))
                        institution.setNomeAbreviado(null);
                    institution.setStatus(response.getInt("status"));
                    institution.setNotaMedia(response.getDouble("rating"));
                    Double lat = response.getDouble("latitude");
                    Double lng = response.getDouble("longitude");
                    institution.setLatLng(new LatLng(lat, lng));
                    //inst.setEndereco(response.getString("address"));
                    //inst.setHoraAbertura(response.getInt("openingTime"));
                    //inst.setHoraFechamento(response.getInt("closingTime"));
                    //item.setWorkingDays(arrayDias);

                    ArrayList<WorkingDay> workingDays = new ArrayList<>();
                    WorkingDay workingDay;
                    workingDaysJSON = response.getJSONArray("workingDays");
                    int length = workingDaysJSON.length();

                    for (int i = 0; i < length; i++)
                    {
                        workingDayJSON = workingDaysJSON.getJSONObject(i);

                        workingDay = new WorkingDay();
                        workingDay.setDayOfTheWeek(workingDayJSON.getInt("dayOfTheWeek"));
                        workingDay.setOpeningTime(new Time(workingDayJSON.getLong("openingTime")));
                        workingDay.setClosingTime(new Time(workingDayJSON.getLong("closingTime")));

                        workingDays.add(workingDay);
                    }
                    institution.setWorkingDays(workingDays);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Log.i(TAG, "getReportResponseById, JSONException: " + e.toString());
                    institution = null;
                }
                finally
                {
                    pCallbacks.done(institution);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pCallbacks.done(null);
            }
        });

        jsonObjectReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mSingletonRequestQueue.addToRequestQueue(jsonObjectReq);

    }


    //url: http://localhost:8080/comuneWS/places/getNearbyPlaces?lat=<pLocation.getLatitude()>&lng=<pLocation.getLongitude()>&radius=<pRadius>
    public void getNearbyPlaces(Location pLocation, Integer pRadius, final GetInstitutionListCallbacks pCallbacks)
    {
        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_GET_NEARBY_PLACES;
        url = url + "?lat=" + String.valueOf(pLocation.getLatitude());
        url = url + "&lng=" + String.valueOf(pLocation.getLongitude());
        url = url + "&radius=" + Constants.SEARCHED_AREA_RADIUS_IN_KM;
        Log.i(TAG, "getNearbyPlaces, URL: " + url);

        final JsonArrayRequest jsonArrayReq = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response)
            {
                int i, j, length1, length2;
                PublicInstitution institution = null;
                JSONObject jsonObjectInstitution;
                ArrayList<PublicInstitution> institutions = new ArrayList<>();

                try
                {
                    length1 = response.length();
                    for (i = 0; i < length1; i++)
                    {
                        jsonObjectInstitution = response.getJSONObject(i);

                        int id = jsonObjectInstitution.getInt("id");
                        int category = jsonObjectInstitution.getInt("category");

                        if (PublicInstitution.TIPO_EDUCACAO == category)
                        {
                            institution = new PublicInstitutionEducacao(id, mAppContext);
                        }
                        else if (PublicInstitution.TIPO_SAUDE == category)
                        {
                            institution = new PublicInstitutionSaude(id, mAppContext);
                        }
                        else if (PublicInstitution.TIPO_SEGURANCA == category)
                        {
                            institution = new PublicInstitutionSeguranca(id, mAppContext);
                        }

                        institution.setNome(jsonObjectInstitution.getString("name"));
                        institution.setNomeAbreviado(jsonObjectInstitution.getString("abbrevName"));
                        if (institution.getNomeAbreviado().toLowerCase().equals("null"))
                            institution.setNomeAbreviado(null);
                        institution.setStatus(jsonObjectInstitution.getInt("status"));
                        institution.setNotaMedia(jsonObjectInstitution.getDouble("rating"));
                        Double lat = jsonObjectInstitution.getDouble("latitude");
                        Double lng = jsonObjectInstitution.getDouble("longitude");
                        institution.setLatLng(new LatLng(lat, lng));
                        //institution.setEndereco(jsonObjectInstitution.getString("address"));
                        ArrayList<WorkingDay> workingDays = new ArrayList<>();
                        WorkingDay workingDay;
                        JSONObject  workingDayJSON;
                        JSONArray workingDaysJSON = jsonObjectInstitution.getJSONArray("workingDays");
                        length2 = workingDaysJSON.length();

                        for (j = 0; j < length2; j++)
                        {
                            workingDayJSON = workingDaysJSON.getJSONObject(j);

                            workingDay = new WorkingDay();
                            workingDay.setDayOfTheWeek(workingDayJSON.getInt("dayOfTheWeek"));
                            workingDay.setOpeningTime(new Time(workingDayJSON.getLong("openingTime")));
                            workingDay.setClosingTime(new Time(workingDayJSON.getLong("closingTime")));

                            workingDays.add(workingDay);
                        }
                        institution.setWorkingDays(workingDays);

                        institutions.add(institution);
                    }

                    mInstitutionManager.setPublicInstitutions(institutions);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Log.i(TAG, "getNearbyPlaces, JSONException: " + e.toString());
                    institutions = null;
                }
                finally {
                    pCallbacks.done(institutions);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pCallbacks.done(null);
            }
        });

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mSingletonRequestQueue.addToRequestQueue(jsonArrayReq);
    }


    //url: http://localhost:8080/comuneWS/places/findNearbyPlaces?llat=<pLocation.getLatitude()>&lng=<pLocation.getLongitude()>
    //                                                          &radius=<pRadius>&category=<pInstType>&highRatings=<pHighGrades>
    public void findNearbyPlaces(Location pLocation, Integer pRadius, Integer pInstType, boolean pHighGrades, final GetInstitutionListCallbacks pCallbacks)
    {
        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_FIND_NEARBY_PLACES;
        url = url + "?lat=" + String.valueOf(pLocation.getLatitude());
        url = url + "&lng=" + String.valueOf(pLocation.getLongitude());
        url = url + "&radius=" + pRadius;
        url = url + "&category=" + pInstType;
        url = url + "&highRatings=" + pHighGrades;
        Log.i(TAG, "findNearbyPlaces, URL: " + url);

        final JsonArrayRequest jsonArrayReq = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response)
            {
                int i, j, length1, length2;
                PublicInstitution institution = null;
                JSONObject jsonObjectInstitution;
                ArrayList<PublicInstitution> institutions = new ArrayList<>();

                try
                {
                    length1 = response.length();
                    for (i = 0; i < length1; i++)
                    {
                        jsonObjectInstitution = response.getJSONObject(i);

                        int id = jsonObjectInstitution.getInt("id");
                        int category = jsonObjectInstitution.getInt("category");

                        if (PublicInstitution.TIPO_EDUCACAO == category)
                        {
                            institution = new PublicInstitutionEducacao(id, mAppContext);
                        }
                        else if (PublicInstitution.TIPO_SAUDE == category)
                        {
                            institution = new PublicInstitutionSaude(id, mAppContext);
                        }
                        else if (PublicInstitution.TIPO_SEGURANCA == category)
                        {
                            institution = new PublicInstitutionSeguranca(id, mAppContext);
                        }

                        institution.setNome(jsonObjectInstitution.getString("name"));
                        institution.setNomeAbreviado(jsonObjectInstitution.getString("abbrevName"));
                        if (institution.getNomeAbreviado().toLowerCase().equals("null"))
                            institution.setNomeAbreviado(null);
                        institution.setStatus(jsonObjectInstitution.getInt("status"));
                        institution.setNotaMedia(jsonObjectInstitution.getDouble("rating"));
                        Double lat = jsonObjectInstitution.getDouble("latitude");
                        Double lng = jsonObjectInstitution.getDouble("longitude");
                        institution.setLatLng(new LatLng(lat, lng));
                        //institution.setEndereco(jsonObjectInstitution.getString("address"));
                        ArrayList<WorkingDay> workingDays = new ArrayList<>();
                        WorkingDay workingDay;
                        JSONObject  workingDayJSON;
                        JSONArray workingDaysJSON = jsonObjectInstitution.getJSONArray("workingDays");
                        length2 = workingDaysJSON.length();

                        for (j = 0; j < length2; j++)
                        {
                            workingDayJSON = workingDaysJSON.getJSONObject(j);

                            workingDay = new WorkingDay();
                            workingDay.setDayOfTheWeek(workingDayJSON.getInt("dayOfTheWeek"));
                            workingDay.setOpeningTime(new Time(workingDayJSON.getLong("openingTime")));
                            workingDay.setClosingTime(new Time(workingDayJSON.getLong("closingTime")));

                            workingDays.add(workingDay);
                        }
                        institution.setWorkingDays(workingDays);

                        institutions.add(institution);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Log.i(TAG, "findNearbyPlaces, JSONException: " + e.toString());
                    institutions = null;
                }
                finally {
                    pCallbacks.done(institutions);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pCallbacks.done(null);
            }
        });

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mSingletonRequestQueue.addToRequestQueue(jsonArrayReq);
    }


    //url: http://localhost:8080/comuneWS/places/getPlacesReportedByUser
    public void getPlacesReportedByUser(User pUser, final GetInstitutionListCallbacks pCallbacks)
    {
        String url = Constants.WEB_SERVICE_URL + Constants.WS_METHOD_GET_PLACES_REPORTED_BY_USER;
        Log.i(TAG, "getPlacesReportedByUser, URL: " + url);

        try
        {
            JSONObject jsonObjectParams = new JSONObject();
            jsonObjectParams.put("id", pUser.getUserId());

            final JsonArrayRequest jsonArrayReq = new JsonArrayRequest(Request.Method.POST, url, jsonObjectParams, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response)
                {
                    int i, j, length1, length2;
                    PublicInstitution institution = null;
                    JSONObject jsonObjectInstitution;
                    JSONObject jsonObjectInstitutionWithReportsNumber;
                    ArrayList<PublicInstitution> institutions = new ArrayList<>();

                    try
                    {
                        length1 = response.length();
                        for(i = 0; i < length1; i++)
                        {
                            jsonObjectInstitutionWithReportsNumber = response.getJSONObject(i);

                            jsonObjectInstitution = jsonObjectInstitutionWithReportsNumber.getJSONObject("institution");
                            int id = jsonObjectInstitution.getInt("id");
                            int category = jsonObjectInstitution.getInt("category");

                            if (PublicInstitution.TIPO_EDUCACAO == category)
                            {
                                institution = new PublicInstitutionEducacao(id, mAppContext);
                            }
                            else if (PublicInstitution.TIPO_SAUDE == category)
                            {
                                institution = new PublicInstitutionSaude(id, mAppContext);
                            }
                            else if (PublicInstitution.TIPO_SEGURANCA == category)
                            {
                                institution = new PublicInstitutionSeguranca(id, mAppContext);
                            }

                            if (institution != null)
                            {
                                institution.setNome(jsonObjectInstitution.getString("name"));
                                institution.setNomeAbreviado(jsonObjectInstitution.getString("abbrevName"));
                                if (institution.getNomeAbreviado().toLowerCase().equals("null"))
                                    institution.setNomeAbreviado(null);
                                institution.setStatus(jsonObjectInstitution.getInt("status"));
                                institution.setNotaMedia(jsonObjectInstitution.getDouble("rating"));
                                Double lat = jsonObjectInstitution.getDouble("latitude");
                                Double lng = jsonObjectInstitution.getDouble("longitude");
                                institution.setLatLng(new LatLng(lat, lng));
                                //institution.setEndereco(jsonObjectInstitution.getString("address"));
                                /*ArrayList<WorkingDay> workingDays = new ArrayList<>();
                                WorkingDay workingDay;
                                JSONObject  workingDayJSON;
                                JSONArray workingDaysJSON = jsonObjectInstitution.getJSONArray("workingDays");
                                length2 = workingDaysJSON.length();

                                for (j = 0; j < length2; j++)
                                {
                                    workingDayJSON = workingDaysJSON.getJSONObject(j);

                                    workingDay = new WorkingDay();
                                    workingDay.setDayOfTheWeek(workingDayJSON.getInt("dayOfTheWeek"));
                                    workingDay.setOpeningTime(new Time(workingDayJSON.getLong("openingTime")));
                                    workingDay.setClosingTime(new Time(workingDayJSON.getLong("closingTime")));

                                    workingDays.add(workingDay);
                                }
                                institution.setWorkingDays(workingDays);*/

                                int numberReports = jsonObjectInstitutionWithReportsNumber.getInt("numberReportsMade");
                                institution = mInstitutionManager.addNumberReportsForInstitution(institution, numberReports);

                                institutions.add(institution);
                            }
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        Log.i(TAG, "getPlacesReportedByUser, JSONException: " + e.toString());
                        institutions = null;
                    }
                    finally
                    {
                        pCallbacks.done(institutions);
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

            jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            mSingletonRequestQueue.addToRequestQueue(jsonArrayReq);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
