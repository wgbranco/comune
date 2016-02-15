package williamgbranco.comune.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import williamgbranco.comune.R;
import williamgbranco.comune.callback.AuthorizeUserCallback;
import williamgbranco.comune.callback.GetUserCallback;
import williamgbranco.comune.manager.UserManager;
import williamgbranco.comune.user.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment
{
    private static String TAG = "comune.LoginFragment";

    private static int REQUEST_CODE_REGISTER_ACTIVITY = 0;

    private static LoginFragment sLoginFragment;
    private UserManager mUserManager;
    private EditText mEditTextUserEmail;
    private EditText mEditTextUserPassword;
    private Button mButtonLogin;
    private Button mButtonRegister;
    //private LinearLayout mContainerProgressBar; //container_progress_bar


    public static LoginFragment newInstance()
    {
        if (sLoginFragment == null)
        {
            sLoginFragment = new LoginFragment();
        }

        return sLoginFragment;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        initializeUserManager();
    }

    private void initializeUserManager()
    {
        mUserManager = UserManager.get(getActivity());
        mUserManager.initialize();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        //mContainerProgressBar = (LinearLayout) v.findViewById(R.id.container_progress_bar);

        mEditTextUserEmail = (EditText) v.findViewById(R.id.edittext_user_email);
        mEditTextUserEmail.getBackground().setColorFilter(getResources().getColor(R.color.complete_survey_button_color), PorterDuff.Mode.SRC_ATOP);

        mEditTextUserPassword = (EditText) v.findViewById(R.id.edittext_user_password);
        mEditTextUserPassword.getBackground().setColorFilter(getResources().getColor(R.color.complete_survey_button_color), PorterDuff.Mode.SRC_ATOP);

        mButtonLogin = (Button) v.findViewById(R.id.button_login);
        mButtonLogin.getBackground().setColorFilter(getResources().getColor(R.color.complete_survey_button_color), PorterDuff.Mode.SRC_ATOP);

        mButtonRegister = (Button) v.findViewById(R.id.button_register);
        mButtonRegister.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        configButtons();

        return v;
    }

    private void configButtons()
    {
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEditTextUserEmail.getText().toString();
                String password = mEditTextUserPassword.getText().toString();

                if ((email.length() > 0) && (password.length() > 0)) {
                    authenticateOnServer(email, password);
                }
                else
                {
                    if (email.length() < 1)
                    {
                        mEditTextUserEmail.setError(getResources().getString(R.string.msg_empty_form_field));
                    }

                    if (password.length() < 1)
                    {
                        mEditTextUserPassword.setError(getResources().getString(R.string.msg_empty_form_field));
                    }
                }
            }
        });

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivityForResult(intent, REQUEST_CODE_REGISTER_ACTIVITY);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_CODE_REGISTER_ACTIVITY)
        {
            Toast.makeText(getActivity(), R.string.msg_user_register_sucess, Toast.LENGTH_LONG).show();
        }
    }

    private void authenticateOnServer(final String email, final String password)
    {
        if ((email.length() > 0) && (password.length() > 0))
        {
            final ProgressDialog ringProgressDialog = ProgressDialog.show(getActivity(),
                    getResources().getString(R.string.msg_wait),
                    getResources().getString(R.string.msg_authenticating), true);

            mUserManager.fetchUserDataInBackground(email, password, new AuthorizeUserCallback() {
                @Override
                public void done(User returnedUser)
                {
                    if (ringProgressDialog != null) ringProgressDialog.dismiss();

                    try {
                        Log.i(TAG, returnedUser.getUserId() + " " + returnedUser.getFirstName() + ", picture id = " + returnedUser.getPictureId());

                        mUserManager.setLoggedInUser(returnedUser, new GetUserCallback() {
                            @Override
                            public void done(User returnedUser)
                            {
                                Intent intent = new Intent(getActivity(), BaseMapActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                getActivity().finish();
                            }

                            @Override
                            public void onNetworkNonAvailable() {}
                        });
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }

                @Override
                public void onNetworkNonAvailable() {
                    if (ringProgressDialog != null) ringProgressDialog.dismiss();
                    Toast.makeText(getActivity(), R.string.msg_network_non_available, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onUnauthorizedAccess() {
                    if (ringProgressDialog != null) ringProgressDialog.dismiss();
                    Toast.makeText(getActivity(), R.string.msg_incorrect_login_info, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onServerError() {
                    if (ringProgressDialog != null) ringProgressDialog.dismiss();
                    Toast.makeText(getActivity(), R.string.msg_on_server_error, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (mEditTextUserEmail != null)
        {
            mEditTextUserEmail.setError(null);
        }

        if (mEditTextUserPassword != null)
        {
            mEditTextUserPassword.setError(null);
        }
    }
}
