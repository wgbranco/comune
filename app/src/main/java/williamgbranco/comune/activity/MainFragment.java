package williamgbranco.comune.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import williamgbranco.comune.R;
import williamgbranco.comune.callback.GetUserCallback;
import williamgbranco.comune.manager.UserManager;
import williamgbranco.comune.service.CompleteSurveyService;
import williamgbranco.comune.service.ReportMadeService;
import williamgbranco.comune.user.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment
{
    private static String TAG = "MainFragment";

    private static int REQUEST_CODE_LOGIN_ACTIVITY = 0;
    private static final String PARAM_ACTION = "williamgbranco.comune.activity.MainFragment.action";

    private static MainFragment sMainFragment;
    private String mParamAction;

    private UserManager mUserManager;

    private ProgressBar mProgressBar;


    public static MainFragment newInstance(String action)
    {
        if (sMainFragment == null)
        {
            sMainFragment = new MainFragment();
        }

        Bundle args = new Bundle();
        args.putString(PARAM_ACTION, action);

        sMainFragment.setArguments(args);

        return sMainFragment;
    }

    public MainFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);

        Bundle args = getArguments();

        initializeUserManager(args);
    }

    private void startServices()
    {
        Log.i(TAG, "startServices()");

        Intent i;

        Context appContext = getActivity().getApplicationContext();

        i = new Intent(appContext, CompleteSurveyService.class);
        appContext.startService(i);

        i = new Intent(appContext, ReportMadeService.class);
        appContext.startService(i);
    }

    private void initializeUserManager(Bundle args)
    {
        Log.i(TAG, "initializeUserManager");

        if (args != null) mParamAction = args.getString(PARAM_ACTION);

        mUserManager = UserManager.get(getActivity());

        mUserManager.getLoggedInUser(new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser != null) {
                    Log.i(TAG, "user logged in");

                    Log.i(TAG, "EXTRA_ACTION: " + mParamAction);
                    Intent intent = new Intent(getActivity(), BaseMapActivity.class);
                    intent.putExtra(BaseMapActivity.EXTRA_ACTION, mParamAction);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    startServices();

                    getActivity().finish();
                } else {
                    Log.i(TAG, "no user logged in");

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();
                }
            }

            @Override
            public void onNetworkNonAvailable() {}
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);

        return v;
    }

}
