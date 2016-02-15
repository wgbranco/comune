package williamgbranco.comune.activity;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import williamgbranco.comune.R;
import williamgbranco.comune.callback.MapCallbacks;
import williamgbranco.comune.manager.UserManager;
import williamgbranco.comune.service.ExpiredSurveyService;
import williamgbranco.comune.service.ExpiringSoonSurveyService;
import williamgbranco.comune.service.ReportResponseFetchrService;
import williamgbranco.comune.util.Constants;


public class BaseMapFragment extends Fragment implements MapCallbacks
{
    private static final String TAG = "comune.BaseMapFragment";

    private static final String PARAM_ACTION = "williamgbranco.comune.activity.BaseMapFragment.action";

    public static final String USER_PROFILE_POPUP = "comune.Profile";
    public static final String MAP_LAYERS_POPUP = "comune.MapLayers";
    //public static final String MARKER_DETAILS_POPUP = "comune.MarkerDetailsDetails";
    //private static final String HOME_BUTTON_CLICKED = "comune.HomeButtonClicked";
    //private static final String HOME_BUTTON_PRESSED = "comune.HomeButtonPressed";

    public static final String ACTION_MAP_MODE_NORMAL = "comune.BaseMapFragment.ChangeMapModeNormal";
    public static final String ACTION_MAP_MODE_FIND = "comune.BaseMapFragment.ChangeMapModeFind";
    public static final String EXTRA_FIND_TYPE = "comune.BaseMapFragment.FindType";
    public static final String EXTRA_FIND_RADIUS = "comune.BaseMapFragment.FindRadius";
    public static final String EXTRA_FIND_HIGH_GRADES = "comune.BaseMapFragment.FindHighGrades";

    //private static final int REQUEST_PROFILE_DIALOG_CLOSED = 0;


    private Context mAppContext;
    private static FragmentActivity sFragmentActivity;
    private FragmentManager mFragmentManager;
    private MapFragment mMapFragment;

    private ImageButton mHomeButton;
    private ImageButton mReturnNormalModeButton; //button_return_normal_mode
    private RelativeLayout mContainerProgressBar; //container_progress_bar

    private static String sMapMode;
    private static boolean widgetsCreated;
    private static boolean sReceiversRegistered;
    private static boolean sFindInstModeLoading;
    //private static Boolean wasHomeButtonClicked = false;
    //private static Boolean wasHomeButtonPressed = false;

    private static BaseMapFragment sBaseMapFragment;


    private BroadcastReceiver mOnMapChangeToFindMode = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "mOnMapChangeToFindMode, onReceive(...)");

            Integer mType = intent.getIntExtra(EXTRA_FIND_TYPE, -1);
            Integer mRadius = intent.getIntExtra(EXTRA_FIND_RADIUS, -1);
            boolean mHighGrades = intent.getBooleanExtra(EXTRA_FIND_HIGH_GRADES, true);

            if ((mType != -1) && (mRadius != -1))
            {
                setMapMode(MapFragment.MAP_MODE_FIND);

                mMapFragment.startFindInstitutionsMapMode(mType, mRadius, mHighGrades);
            }
        }
    };

    private BroadcastReceiver mOnMapChangeToNormalMode = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "mOnMapChangeToNormalMode, onReceive(...)");

            startNormalMapMode();
        }
    };

    public static BaseMapFragment newInstance(String action)
    {
        if (sBaseMapFragment == null)
        {
            sBaseMapFragment = new BaseMapFragment();
        }

        Bundle args = new Bundle();
        args.putString(PARAM_ACTION, action);
        sBaseMapFragment.setArguments(args);

        return sBaseMapFragment;
    }

    public BaseMapFragment()
    {
        if (sFragmentActivity == null)
            sFragmentActivity = getActivity();
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mAppContext = activity.getApplicationContext();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mAppContext = null;
    }

    private void registerReceivers()
    {
        Log.i(TAG, "register receivers");

        try {
            IntentFilter filter;

            filter = new IntentFilter(ACTION_MAP_MODE_FIND);
            if (isAdded()) getActivity().registerReceiver(mOnMapChangeToFindMode, filter, Constants.PERM_PRIVATE, null);

            filter = new IntentFilter(ACTION_MAP_MODE_NORMAL);
            if (isAdded()) getActivity().registerReceiver(mOnMapChangeToNormalMode, filter, Constants.PERM_PRIVATE, null);

            sReceiversRegistered = true;
        }
        catch (Exception e) {Log.e(TAG, "registerReceivers(): " + e.toString());}
    }

    private void unregisterReceivers()
    {
        Log.i(TAG, "unregister receivers");

        try {
            if (isAdded()) getActivity().unregisterReceiver(mOnMapChangeToFindMode);
            if (isAdded()) getActivity().unregisterReceiver(mOnMapChangeToNormalMode);
        }
        catch (Exception e) {Log.e(TAG, "unregisterReceivers(): " + e.toString());}
    }

    private void startNormalMapMode()
    {
        setMapMode(MapFragment.MAP_MODE_NORMAL);

        mMapFragment.startNormalMapMode();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate()");

        widgetsCreated = false;

        //if (!sReceiversRegistered)
            registerReceivers();

        startServices();

        String action;
        Bundle args = getArguments();
        if (args != null)
        {
            action = args.getString(PARAM_ACTION);

            Log.i(TAG, "EXTRA_ACTION: "+action);

            if (Constants.ACTION_STORED_SURVEYS_ACTVITY.equals(action))
            {
                Context appContext = getActivity().getApplicationContext();
                Intent i = new Intent(appContext, StoredSurveysActivity.class);
                startActivity(i);
            }
        }
    }

    private void startServices()
    {
        Log.i(TAG, "startServices()");

        int userId = UserManager.get(getActivity()).getCurrentUser().getUserId();

        Intent i;

        Context appContext = getActivity().getApplicationContext();

        i = new Intent(appContext, ExpiredSurveyService.class);
        appContext.startService(i);

        i = new Intent(appContext, ExpiringSoonSurveyService.class);
        i.putExtra(ExpiringSoonSurveyService.EXTRA_USER_ID, userId);
        appContext.startService(i);

        i = new Intent(appContext, ReportResponseFetchrService.class);
        i.putExtra(ReportResponseFetchrService.EXTRA_USER_ID, userId);
        appContext.startService(i);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.i(TAG, "onCreateView()");

        View v = inflater.inflate(R.layout.fragment_base_map, container, false);

        mMapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.base_map);

        mHomeButton = (ImageButton) v.findViewById(R.id.home_button);
        mHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*wasHomeButtonPressed = false;
                wasHomeButtonClicked = true;*/

                showUserProfile();
            }
        });
        mHomeButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                /*wasHomeButtonClicked = false;
                wasHomeButtonPressed = true;*/
                showMapLayersDialog();

                return true;
            }
        });

        mContainerProgressBar = (RelativeLayout) v.findViewById(R.id.container_progress_bar);

        mReturnNormalModeButton = (ImageButton) v.findViewById(R.id.button_return_normal_mode);
        mReturnNormalModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startNormalMapMode();
            }
        });

        widgetsCreated = true;

        if (sMapMode == null)
            startNormalMapMode();

        updateUI();

        return v;
    }

    private void setMapMode(String pMode)
    {
        Log.i(TAG, "setMapMode: " + pMode);

        sMapMode = pMode;

        sFindInstModeLoading = (sMapMode.equals(MapFragment.MAP_MODE_FIND))?true:false;

        if (widgetsCreated)
            updateUI();
    }

    private void updateUI()
    {
        Log.i(TAG, "updateUI()");

        mContainerProgressBar.setVisibility(View.INVISIBLE);
        Log.i(TAG, "mContainerProgressBar INVISIBLE");

        if (sMapMode.equals(MapFragment.MAP_MODE_NORMAL))
        {
            mReturnNormalModeButton.setVisibility(View.INVISIBLE);
        }
        else if (sMapMode.equals(MapFragment.MAP_MODE_FIND))
        {
            mReturnNormalModeButton.setVisibility(View.VISIBLE);

            if (sFindInstModeLoading)
            {
                mContainerProgressBar.setVisibility(View.VISIBLE);
                Log.i(TAG, "mContainerProgressBar VISIBLE");
            }
            else
            {
                mContainerProgressBar.setVisibility(View.INVISIBLE);
                Log.i(TAG, "mContainerProgressBar INVISIBLE");
            }
        }

        Log.i(TAG, "mContainerProgressBar visible: " + mContainerProgressBar.getVisibility());

    }

    private void showUserProfile()
    {
        try {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            ProfileFragment dialog = ProfileFragment.newInstance();
            //dialog.setTargetFragment(this, REQUEST_PROFILE_DIALOG_CLOSED);
            dialog.show(fm, USER_PROFILE_POPUP);
        }
        catch(Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void showMapLayersDialog()
    {
        try
        {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            MapLayersFragment dialog = new MapLayersFragment();
            dialog.show(fm, MAP_LAYERS_POPUP);
        }
        catch (Exception e){}
    }


    @Override
    public void onSearchComplete()
    {
        Log.i(TAG, "onSearchComplete()");

        sFindInstModeLoading = false;

        updateUI();
    }

    @Override
    public void onDestroy()
    {
        unregisterReceivers();

        super.onDestroy();
    }
}