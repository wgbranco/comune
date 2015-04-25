package williamgbranco.comune;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class BaseMapFragment extends android.support.v4.app.Fragment
{
    private static final String TAG = "comune.BaseMapFragment";

    private ImageButton mProfileButton;

    private MapFragment mMapFragment;
    private GoogleMap mGoogleMap;

    private static BaseMapFragment bm_fragment;

    public static BaseMapFragment newInstance()
    {
        if (bm_fragment == null)
        {
            bm_fragment = new BaseMapFragment();
        }
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        bm_fragment.setArguments(args);*/

        return bm_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_base_map, container, false);

        mProfileButton = (ImageButton) v.findViewById(R.id.profile_button);

        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "perfil do usuário", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }


    @Override
    public void onResume()
    {
        super.onResume();
    }

}