package williamgbranco.comune;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


/**
 * A simple {@link com.google.android.gms.maps.SupportMapFragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends SupportMapFragment implements OnMapReadyCallback
{
    private static final String TAG = "comune.MapFragment";

    private static MapFragment map_fragment;

    private GoogleMap mGoogleMap;

    private LocationManager mLocationManager;

    private LocationListener mLocationListener = new LocationListener()
    {
        public void onLocationChanged(Location location)
        {
            // Called when a new location is found by the network location provider.
            updateUserLocationOnMap(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance()
    {
        if (map_fragment == null )
        {
            map_fragment = new MapFragment();
            Log.i(TAG, "instantiated");
        }
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //bm_fragment.setArguments(args);
        return map_fragment;
    }

    public MapFragment()
    {
        Log.i(TAG, "constructed");
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 100, mLocationListener);

        Log.i(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        getMapAsync(this);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap = googleMap;

        mGoogleMap.setMyLocationEnabled(true);

        Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        LatLng user = new LatLng(location.getLatitude(), location.getLongitude());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 14));
        /*mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        mGoogleMap.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));*/
    }

    private void updateUserLocationOnMap(Location location)
    {
        Log.i(TAG, "user location received");
        if (mGoogleMap != null)
        {
            LatLng user = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 14));
        }
    }

}
