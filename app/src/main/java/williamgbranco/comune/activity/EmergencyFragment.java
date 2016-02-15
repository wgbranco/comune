package williamgbranco.comune.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import williamgbranco.comune.R;
import williamgbranco.comune.service.FetchAddressIntentService;


public class EmergencyFragment extends Fragment
{
    private static final String TAG = "comune.EmergencyFrag";
    private static final String PHONE_NUMBER_POLICE = "190";
    private static final String PHONE_NUMBER_AMBULANCE = "192";
    private static final String PHONE_NUMBER_FIRE_DEPT = "193";

    private TextView mTextViewHeaderMsg; //textview_header_text
    private TextView mTextViewAddress; //textview_address
    private ProgressBar mProgressBarAddress; //progress_bar_address
    private Button mButtonCallPolice; //button_call_police
    private Button mButtonCallFireDept; //button_call_fire_department
    private Button mButtonCallAmbulance; //button_call_ambulance

    private boolean loading;
    protected Location mLastLocation;
    private String mAddressOutput;
    private AddressResultReceiver mResultReceiver;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            startFetchAddressIntentService();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {}

        @Override
        public void onProviderEnabled(String provider)
        {}

        @Override
        public void onProviderDisabled(String provider)
        {}
    };


    public EmergencyFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        loading = true;

        mResultReceiver = new AddressResultReceiver(new Handler());

        if (mLocationManager == null)
        {
            mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MapFragment.MINIMUM_TIME_TO_WAIT,
                    MapFragment.MAXIMUM_DISTANCE_FROM_MARKER, mLocationListener);
        }

        startFetchAddressIntentService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_emergency, container, false);

        mTextViewHeaderMsg = (TextView) v.findViewById(R.id.textview_header_msg);
        mTextViewHeaderMsg.setText(getResources().getString(R.string.textview_location_address_text) + ":");

        mProgressBarAddress = (ProgressBar) v.findViewById(R.id.progress_bar_address);

        mTextViewAddress = (TextView) v.findViewById(R.id.textview_address);

        mButtonCallPolice = (Button) v.findViewById(R.id.button_call_police);
        mButtonCallPolice.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        mButtonCallPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:" + PHONE_NUMBER_POLICE);
                Intent intent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(intent);
            }
        });

        mButtonCallFireDept = (Button) v.findViewById(R.id.button_call_fire_department);
        mButtonCallFireDept.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        mButtonCallFireDept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:" + PHONE_NUMBER_FIRE_DEPT);
                Intent intent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(intent);
            }
        });

        mButtonCallAmbulance = (Button) v.findViewById(R.id.button_call_ambulance);
        mButtonCallAmbulance.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        mButtonCallAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:" + PHONE_NUMBER_AMBULANCE);
                Intent intent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(intent);
            }
        });

        updateUI();

        return v;
    }

    protected void startFetchAddressIntentService()
    {
        Log.i(TAG, "startFetchAddressIntentService()");

        mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if ((mLastLocation != null) && (isAdded()))
        {
            Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
            intent.putExtra(FetchAddressIntentService.RECEIVER, mResultReceiver);
            intent.putExtra(FetchAddressIntentService.LOCATION_DATA_EXTRA, mLastLocation);

            if (isAdded()) getActivity().startService(intent);
        }
    }

    private void updateUI()
    {
        Log.i(TAG, "updateUI()");

        mProgressBarAddress.setVisibility(View.INVISIBLE);

        if (loading)
        {
            mProgressBarAddress.setVisibility(View.VISIBLE);
        }
        else
        {
            mTextViewAddress.setText(mAddressOutput);
        }
    }

    class AddressResultReceiver extends ResultReceiver
    {
        public AddressResultReceiver(Handler handler)
        {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData)
        {
            Log.i(TAG, "AddressResultReceiver, onReceiveResult(...)");
            loading = false;

            // Display the address string
            // or an error message sent from the intent service.
            mAddressOutput = resultData.getString(FetchAddressIntentService.RESULT_DATA_KEY);

            // Show a toast message if an address was found.
            //TODO:
            /*if (resultCode == FetchAddressIntentService.SUCCESS_RESULT)
            {
                if (isAdded())
                    Toast.makeText(getActivity(), R.string.address_found, Toast.LENGTH_LONG).show();
            }
            else
            {

            }*/

            updateUI();
        }
    }
}
