package williamgbranco.comune.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import williamgbranco.comune.R;
import williamgbranco.comune.callback.DownloadedFileCallbacks;
import williamgbranco.comune.manager.PublicInstitutionManager;
import williamgbranco.comune.manager.ReportManager;
import williamgbranco.comune.manager.SurveyManager;
import williamgbranco.comune.manager.UserManager;
import williamgbranco.comune.server.ServerRequests;
import williamgbranco.comune.user.User;
import williamgbranco.comune.util.Constants;


public class ProfileFragment extends DialogFragment
{
    private static final String TAG = "ProfileFragment";

    public static final String ACTION_USER_PHOTO_DOWNLOADED = "williamgbranco.comune.USER_PHOTO_DOWNLOADED";
    //public static final String ACTION_EXTRA_FILE_PATH = "comune.user_photo.file_path";


    private User mUser;
    private PublicInstitutionManager mInstitutionManager;
    private ReportManager mReportManager;
    private SurveyManager mSurveyManager;
    private UserManager mUserManager;

    private static ProfileFragment sProfileFragment;

    private ImageView mUserPicture;
    private TextView mTextViewUserName; //textview_user_name
    private ProgressBar mProgressBar;
    private Button mButtonFind;
    //private Button mButtonTrackPerformance;
    private Button mButtonSurveys;
    private Button mButtonReports;
    private Button mButtonEmergency;
    private Button mLogoutButton;


    private BroadcastReceiver mOnUserPhotoDownloaded = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            //getActivity().getApplicationContext().unregisterReceiver(mOnUserPhotoDownloaded);

            if (intent != null)
            {
                Log.i(TAG, "BroadcastReceiver mOnUserPhotoDownloaded intent NOT null");
                int contentLength = intent.getIntExtra(Constants.ACTION_EXTRA_FILE_CONTENT_LENGTH, 0);
                String fileUriText = intent.getStringExtra(Constants.ACTION_EXTRA_FILE_URI);

                mProgressBar.setVisibility(View.INVISIBLE);

                if ((contentLength > 0) && (fileUriText != null))
                {
                    Uri fileUri = Uri.parse(fileUriText);
                    mUser.setPictureUri(fileUri);
                    mUserManager.updateUserPhotoUri(fileUri);

                    setPicture(fileUri.getPath());
                }
                else
                {
                    if (isAdded()) {
                        Drawable icon = ContextCompat.getDrawable(getActivity(), R.drawable.photo_placeholder);
                        mUserPicture.setImageDrawable(icon);
                    }
                }
            }
        }
    };


    public static ProfileFragment newInstance()
    {
        if (sProfileFragment == null) {
            sProfileFragment = new ProfileFragment();
        }

        return sProfileFragment;
    }

    public ProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mUserManager = UserManager.get(getActivity());
        mUser = mUserManager.getCurrentUser();

        mInstitutionManager = PublicInstitutionManager.get(getActivity());
        mReportManager = ReportManager.get(getActivity());
        mSurveyManager = SurveyManager.get(getActivity());

        IntentFilter filter = new IntentFilter(ACTION_USER_PHOTO_DOWNLOADED);
        if (isAdded()) getActivity().getApplicationContext().registerReceiver(mOnUserPhotoDownloaded, filter, Constants.PERM_PRIVATE, null);
    }

   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
   {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        mUserPicture = (ImageView) v.findViewById(R.id.user_picture);
        mUserPicture.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout()
            {
                //Remove the listener before proceeding
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mUserPicture.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mUserPicture.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                updateUserPicture();
            }
        });

       mTextViewUserName = (TextView) v.findViewById(R.id.textview_user_name);
       if(mUser != null)
       {
           mTextViewUserName.setText(mUser.getFirstName());
       }

        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        if (mProgressBar != null) {
            mProgressBar.setIndeterminate(true);
            mProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.logo_orange), android.graphics.PorterDuff.Mode.SRC_ATOP);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        mButtonFind = (Button) v.findViewById(R.id.button_find);
        mButtonFind.getBackground().setColorFilter(getResources().getColor(R.color.logo_green3), PorterDuff.Mode.SRC_ATOP);

        /*mButtonTrackPerformance = (Button) v.findViewById(R.id.button_acompanhe);
        mButtonTrackPerformance.getBackground().setColorFilter(getResources().getColor(R.color.complete_survey_button_color), PorterDuff.Mode.SRC_ATOP);*/

        mButtonSurveys = (Button) v.findViewById(R.id.button_surveys);
        mButtonSurveys.getBackground().setColorFilter(getResources().getColor(R.color.logo_green3), PorterDuff.Mode.SRC_ATOP);

        mButtonReports = (Button) v.findViewById(R.id.button_reports);
        mButtonReports.getBackground().setColorFilter(getResources().getColor(R.color.complete_survey_button_color), PorterDuff.Mode.SRC_ATOP);

        mButtonEmergency = (Button) v.findViewById(R.id.button_emergency);
        mButtonEmergency.getBackground().setColorFilter(getResources().getColor(R.color.complete_survey_button_color), PorterDuff.Mode.SRC_ATOP);

        mLogoutButton = (Button) v.findViewById(R.id.button_logout);
        mLogoutButton.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        configButtons();

        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    private void updateUserPicture()
    {
        mUserPicture.setImageDrawable(null);
        mProgressBar.setVisibility(View.VISIBLE);

        if (mUser != null) {
            if (mUser.getPictureUri() == null) {
                if (mUser.getUserId() > 0) {
                    getPicture();
                }
            } else {
                Log.i(TAG, "NÃO vai baixar a foto");
                mProgressBar.setVisibility(View.GONE);
                setPicture(mUser.getPictureUri().getPath());
            }
        }
    }

    private void getPicture()
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;

        Log.i(TAG, "vai baixar a foto");
        new ServerRequests(getActivity()).downloadUserPhoto(mUser, imageFileName, new DownloadedFileCallbacks() {
            @Override
            public void done(String contentType, String disposition, int contentLength, String originalFileName, Uri fileUri) {
                try {
                    Intent intent = new Intent(ACTION_USER_PHOTO_DOWNLOADED);
                    intent.putExtra(Constants.ACTION_EXTRA_FILE_CONTENT_LENGTH, contentLength);
                    intent.putExtra(Constants.ACTION_EXTRA_FILE_URI, fileUri.toString());

                    if (isAdded()) getActivity().getApplicationContext().sendBroadcast(intent, Constants.PERM_PRIVATE);

                    Log.i(TAG, "contentType: " + contentType + ", contentLength: " + contentLength + ", originalFileName: " + originalFileName);
                    Log.i(TAG, "filePath: " + fileUri);
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
        });
    }

    private void setPicture(String mediaPath)
    {
        // Get the dimensions of the View
        int targetW = mUserPicture.getWidth();
        int targetH = mUserPicture.getHeight();
        Log.i(TAG, "targetW = " + targetW + ", targetH = " + targetH);
        // Get the dimensions of the bitmap

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        //if (BitmapFactory.decodeFile(mediaPath, bmOptions) != null) {
        BitmapFactory.decodeFile(mediaPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            Bitmap bitmap = BitmapFactory.decodeFile(mediaPath, bmOptions);
            mUserPicture.setImageBitmap(bitmap);
        /*}
        else
        {
            getPicture();
        }*/
    }

    private void configButtons()
    {
        if (mButtonFind != null)
        {
            mButtonFind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), FindPlacesActivity.class);
                    startActivity(intent);

                    dismiss();
                }
            });
        }

        if (mButtonSurveys != null)
        {
            mButtonSurveys.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), StoredSurveysActivity.class);
                    startActivity(intent);

                    dismiss();
                }
            });
        }

        if (mButtonReports != null)
        {
            mButtonReports.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UserReportsActivity.class);
                    startActivity(intent);

                    dismiss();
                }
            });
        }

        if (mButtonEmergency != null)
        {
            mButtonEmergency.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), EmergencyActivity.class);
                    startActivity(intent);

                    dismiss();
                }
            });
        }

        if (mLogoutButton != null)
        {
            mLogoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    logout();

                    /*AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity().getApplicationContext());

                    dialog.setMessage(R.string.msg_logout);

                    dialog.setPositiveButton(R.string.text_button_continue, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            logout();
                        }
                    });

                    dialog.setNegativeButton(R.string.text_button_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    });

                    dialog.create().show();*/
                }
            });
        }
    }

    private void logout()
    {
        Context appContext = getActivity().getApplicationContext();

        // STOP background services
        //stopServices();

        // REMOVE user info from shared preferences
        mUserManager.logOut();

        //RESET managers
        mInstitutionManager.resetManager();
        mReportManager.resetManager();
        mSurveyManager.resetManager();

        // START main activity
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        // DISMISS current activity
        getActivity().finish();
    }


    /*private void stopServices()
    {
        Context appContext = getActivity().getApplicationContext();

        Intent i = new Intent(appContext, ReportResponseFetchrService.class);
        i.setAction(ReportResponseFetchrService.ACTION_SET_ALARM_OFF);
        appContext.startService(i);

        i = new Intent(appContext, ExpiringSoonSurveyService.class);
        i.setAction(ExpiringSoonSurveyService.ACTION_SET_ALARM_OFF);
        appContext.startService(i);
    }*/

    public void sendResult(int resultCode)
    {
        if (getTargetFragment() == null)
            return;

        Intent i = new Intent();

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        Dialog d = getDialog();

        if (d != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);

            d.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Log.i(TAG, "clicou backbutton");
                    sendResult(Activity.RESULT_OK);
                }
            });
        }
    }

}
