package williamgbranco.comune.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;

import williamgbranco.comune.R;
import williamgbranco.comune.callback.GetInstitutionListCallbacks;
import williamgbranco.comune.callback.MapCallbacks;
import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.manager.PublicInstitutionManager;
import williamgbranco.comune.manager.SharedPrefsManager;
import williamgbranco.comune.server.ServerRequests;


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback
{
    private static final String TAG = "comune.MapFragment";

    public static final String MAP_MODE_NORMAL = "normal_mode";
    public static final String MAP_MODE_FIND = "find_institutions_mode";

    public static final String MARKER_DETAILS_POPUP = "comune.MarkerDetailsDetails";
    public static final String ACTION_MARKER_CLICKED = "comune.MapFragment.MarkerClicked";
    public static final String ACTION_EXTRA_INST_ID = "comune.MapFragment.MarkerClicked.inst_id";

    public static final float INITIAL_ZOOM_LEVEL = (float) 15.0;
    public static final float MAXIMUM_DISTANCE_FROM_MARKER = (float) 100.00; // 100 metros
    public static final long MINIMUM_TIME_TO_WAIT = (long) 2*60*60; // 2 min, levado em conta o tempo médio que uma pessoa leva para percorrer 100m
    public static final int RADIUS_AROUND_USER_POSITION = 7; //em Km


    private Context mAppContext;
    private static MapFragment sMapFragment;

    private GoogleMap mGoogleMap;
    private MapCallbacks mCallbacks;
    private SharedPrefsManager mSharedPrefsManager;
    private PublicInstitutionManager mPublicInstitutionManager;

    private static String sMapMode;
    private static boolean mapJustCreated = true;
    private static Integer sTypeToSearchFor;
    private static Integer sRadiusToSearchAroud;
    private static Boolean sSearchForHighGradedInsts;
    private static boolean sLockPublicInstitutionArray;
    private static LocationManager sLocationManager;

    private static HashMap<Marker, PublicInstitution> sMarkerInstitutionHashMap = new HashMap<>();
    private static HashMap<Marker, PublicInstitution> sMarkerFoundInstHashMap = new HashMap<>();

    private LocationListener mLocationListener = new LocationListener()
    {
        public void onLocationChanged(Location location)
        {
            // Called when a new location is found by the network location provider.
            Log.i(TAG, "onLocationChanged");
            //Toast.makeText(mAppContext, "Location changed to: "+location.toString(), Toast.LENGTH_LONG).show();

            //sLastLocation = location;

            fetchInstitutionsAroundUser(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider)
        {
            if (LocationManager.GPS_PROVIDER.equals(provider))
            {
                Toast.makeText(getActivity(), R.string.gps_enabled, Toast.LENGTH_SHORT).show();
            }
            else if (LocationManager.NETWORK_PROVIDER.equals(provider))
            {
                Toast.makeText(getActivity(), R.string.network_enabled, Toast.LENGTH_SHORT).show();
            }

            if (!setLocationProvider())
            {
                directToLocationSourcesSettings();
            }
        }

        public void onProviderDisabled(String provider)
        {
            String provider_msg = null;

            if (LocationManager.GPS_PROVIDER.equals(provider)) {
                provider_msg = getString(R.string.gps_disabled);
            }
            else if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
                provider_msg = getString(R.string.network_disabled);
            }

            if (provider_msg != null) Toast.makeText(getActivity(), provider_msg, Toast.LENGTH_SHORT).show();

            if (!setLocationProvider()) directToLocationSourcesSettings();
        }
    };


    /*
    *  Map related functions
    ************************************************************************/

    public static MapFragment newInstance()
    {
        if (sMapFragment == null )
        {
            sMapFragment = new MapFragment();
        }

        return sMapFragment;
    }

    public MapFragment()
    {}

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mCallbacks = (MapCallbacks) activity;
        mAppContext = activity.getApplicationContext();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mCallbacks = null;
        mAppContext = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        configureLocationManager();
        mSharedPrefsManager = SharedPrefsManager.get(getActivity());
        mPublicInstitutionManager = PublicInstitutionManager.get(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        getMapAsync(this);

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        Log.i(TAG, "Map ready");

        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker pMarker) {
                try {
                    int instId = -1;
                    FragmentManager fm = getActivity().getSupportFragmentManager();

                    if (MAP_MODE_NORMAL.equals(sMapMode)) {
                        instId = sMarkerInstitutionHashMap.get(pMarker).getId();
                    } else if (MAP_MODE_FIND.equals(sMapMode)) {
                        instId = sMarkerFoundInstHashMap.get(pMarker).getId();
                    }

                    MarkerDetailsFragment dialog = MarkerDetailsFragment.newInstance(instId, sMapMode);
                    dialog.show(fm, MARKER_DETAILS_POPUP);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
                return true;
            }
        });

        Location lastLocation = getLastKnownLocation();
        Log.i(TAG, "onMapReady, lastLocation: " + lastLocation);
        centralizeMapOnUser(lastLocation);

        updateMapMarkers();
        Location lastKnownLocation = getLastKnownLocation();
        Log.i(TAG, "onMapReady, lastKnownLocation: " + lastKnownLocation);
        if (lastKnownLocation != null) fetchInstitutionsAroundUser(lastKnownLocation);
    }

    private void centralizeMapOnUser(Location userLocation)
    {
        try {
            LatLng user = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

            float zoomLevel = mGoogleMap.getCameraPosition().zoom;
            if (mapJustCreated) zoomLevel = INITIAL_ZOOM_LEVEL;
            mapJustCreated = false;

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, zoomLevel));
        } catch (Exception e) {Log.e(TAG, e.toString());}
    }


    /*
    *  Map Mode related functions
    ************************************************************************/

    private void setMapMode(String newMode)
    {
        Log.i(TAG, "setMapMode: " + newMode);

        sMapMode = newMode;

        //sFindInstModeLoading = (sMapMode.equals(MapFragment.MAP_MODE_FIND))?true:false;

        resetFoundInstitutionsHashMap();
    }

    public void startFindInstitutionsMapMode(Integer pType, Integer pRadius, boolean pHighGrades)
    {
        Log.i(TAG, "start FIND MODE");

        setMapMode(MAP_MODE_FIND);

        sTypeToSearchFor = pType;
        sRadiusToSearchAroud = pRadius;
        sSearchForHighGradedInsts = pHighGrades;

        findInstitutionsAroundUser(pType, pRadius, pHighGrades);
    }

    public void startNormalMapMode()
    {
        Log.i(TAG, "start NORMAL MODE");

        sTypeToSearchFor = null;
        sRadiusToSearchAroud = null;
        sSearchForHighGradedInsts = null;

        String oldMode = sMapMode;

        setMapMode(MAP_MODE_NORMAL);

        if ((oldMode != null) && (oldMode.equals(MAP_MODE_FIND)))
            changeFromFindToNormalMode();
    }


    /*
    *  Location related functions
    ************************************************************************/

    private Location getLastKnownLocation()
    {
        Location location;

        location = sLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null)
            location = sLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        return location;
    }

    private void configureLocationManager()
    {
        if (sLocationManager == null)
        {
            Log.i(TAG, "configureLocationManager");

            sLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            if (!setLocationProvider())
            {
                directToLocationSourcesSettings();
            }
        }
    }

    private void directToLocationSourcesSettings()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle(R.string.title_location_desabled);

        // Setting Dialog Message
        alertDialog.setMessage(R.string.msg_enable_phone_location);

        // On pressing Settings button
        alertDialog.setPositiveButton(
                getResources().getString(R.string.text_ok_button),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 1);
                    }
                });

        alertDialog.show();
    }

    private boolean setLocationProvider()
    {
        if (sLocationManager != null)
        {
            if (sLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                sLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_TO_WAIT,
                        MAXIMUM_DISTANCE_FROM_MARKER, mLocationListener);

                Log.i(TAG, "LocationManager.GPS_PROVIDER");
                return true;
            }
            else if (sLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            {
                sLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MINIMUM_TIME_TO_WAIT,
                        MAXIMUM_DISTANCE_FROM_MARKER, mLocationListener);
                Log.i(TAG, "LocationManager.NETWORK_PROVIDER");
                return true;
            }
        }

        return false;
    }


    /*
    *  Institutions related functions
    ************************************************************************/

    //Find Places Mode
    private void resetFoundInstitutionsHashMap()
    {
        if (sMarkerFoundInstHashMap != null)
        {
            for (HashMap.Entry<Marker, PublicInstitution> e : sMarkerFoundInstHashMap.entrySet()) {
                e.getKey().remove();
                Log.i(TAG, "found inst marker removed!!");
            }
            sMarkerFoundInstHashMap.clear();
            sMarkerFoundInstHashMap = new HashMap<>();
        }
    }

    //Find Places Mode
    private void displayFoundInstitutionsMarkers()
    {
        ArrayList<PublicInstitution> mItems;

        mItems = mPublicInstitutionManager.getFoundPublicInstitutions();
        if (mItems != null)
        {
            for (PublicInstitution item : mItems)
            {
                Marker marker = mGoogleMap.addMarker(item.getMarkerOptions());
                item.setMarker(marker);
                sMarkerFoundInstHashMap.put(marker, item);
                Log.i(TAG, item.toString());
                Log.i(TAG, "found marker added!!: " + item.getId());
            }
        }
        /*else
        {
            displayPublicInstitutionsMarkers();
        }*/
    }

    //Normal Mode
    private void resetPublicInstitutionsHashMap()
    {
        if (sMarkerInstitutionHashMap != null)
        {
            for (HashMap.Entry<Marker, PublicInstitution> e : sMarkerInstitutionHashMap.entrySet()) {
                Log.i(TAG, "normal removed!!");
                e.getKey().remove();
            }
            sMarkerInstitutionHashMap.clear();
            sMarkerInstitutionHashMap = new HashMap<>();
        }
    }

    //Normal Mode
    private void displayPublicInstitutionsMarkers()
    {
        ArrayList<PublicInstitution> mItems;

        sLockPublicInstitutionArray = true;
        mItems = mPublicInstitutionManager.getPublicInstitutions();
        if (mItems != null)
        {
            for (PublicInstitution item : mItems)
            {
                Marker marker = mGoogleMap.addMarker(item.getMarkerOptions());
                item.setMarker(marker);
                sMarkerInstitutionHashMap.put(marker, item);
                Log.i(TAG, item.toString());
                Log.i(TAG, "normal added!!");
            }
        }
        sLockPublicInstitutionArray = false;
    }

    private void changeFromFindToNormalMode()
    {
        Log.i(TAG, "changeFromFindToNormalMode()");

        if (mSharedPrefsManager != null)
            mSharedPrefsManager.resetMapSharedPreferences();

        displayPublicInstitutionsMarkers();
    }

    private void updateMapMarkers()
    {
        Log.i(TAG, "updateMapMarkers");

        if ((mGoogleMap != null) && (sMapMode != null))
        {
            resetPublicInstitutionsHashMap();

            if (sMapMode.equals(MAP_MODE_NORMAL))
            {
                displayPublicInstitutionsMarkers();
            }
            else if (sMapMode.equals(MAP_MODE_FIND))
            {
                displayFoundInstitutionsMarkers();
            }
        }
        else
        {
            Log.i(TAG, "mGoogleMap is null");
        }
    }

    private void fetchInstitutionsAroundUser(final Location pLocation)
    {
        Log.i(TAG, "fetch Institutions NORMAL MODE");

        //Conecta ao servidor para receber serviços públicos próximos
        new ServerRequests(getActivity()).fetchPublicInstitutionsAroundUser(pLocation, RADIUS_AROUND_USER_POSITION, new GetInstitutionListCallbacks() {
            @Override
            public void done(ArrayList<PublicInstitution> returnedInstitutions)
            {
                try {
                    Log.i(TAG, "fetch Institutions NORMAL mode DONE");

                    if ((returnedInstitutions != null) && (returnedInstitutions.size() > 0))
                    {
                        Log.i(TAG, "PublicInstitutionArray size:" + returnedInstitutions.size());

                        while (sLockPublicInstitutionArray) {
                            Log.i(TAG, "PublicInstitutionArray LOCKED");
                        }

                        centralizeMapOnUser(getLastKnownLocation());
                        mPublicInstitutionManager.setPublicInstitutions(returnedInstitutions);

                        if ((sMapMode != null) && (sMapMode.equals(MAP_MODE_NORMAL)))
                            updateMapMarkers();
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
        });
    }

    private void findInstitutionsAroundUser(Integer pType, Integer pRadius, boolean pHighGrades)
    {
        Log.i(TAG, "fetch Institutions FIND MODE");

        Location lastKnownLocation = getLastKnownLocation();

        Log.d(TAG, "findInstitutionsAroundUser, lastKnownLocation: "+lastKnownLocation);

        if (lastKnownLocation != null) {
            //Conecta ao servidor para encontrar (melhores ou piores) serviços públicos próximos ao usuário
            new ServerRequests(getActivity()).findPublicInstitutionsAroundUser(lastKnownLocation, pType, pRadius, pHighGrades, new GetInstitutionListCallbacks() {
                @Override
                public void done(ArrayList<PublicInstitution> returnedInstitutions) {
                    try {
                        Log.i(TAG, "fetch Institutions FIND mode DONE");

                        if (returnedInstitutions != null) {
                            mPublicInstitutionManager.setFoundPublicInstitutions(returnedInstitutions);

                            mSharedPrefsManager.setOnlyVisibleInstitutionType(sTypeToSearchFor);

                            if (mCallbacks != null) mCallbacks.onSearchComplete();

                            updateMapMarkers();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            });
        }
    }

}