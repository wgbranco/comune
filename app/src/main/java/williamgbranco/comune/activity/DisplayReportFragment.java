package williamgbranco.comune.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import williamgbranco.comune.R;
import williamgbranco.comune.callback.DownloadedFileCallbacks;
import williamgbranco.comune.callback.GetReportCallback;
import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.manager.PublicInstitutionManager;
import williamgbranco.comune.manager.ReportManager;
import williamgbranco.comune.manager.UserManager;
import williamgbranco.comune.report.Report;
import williamgbranco.comune.server.ServerRequests;
import williamgbranco.comune.user.User;
import williamgbranco.comune.util.Constants;
import williamgbranco.comune.util.DateUtils;


public class DisplayReportFragment extends Fragment
{
    private static final String TAG = "comune.DispReportFrag";

    public static final String ARG_INST_ID = "DisplayReportFragment.inst_id";
    public static final String ARG_REPORT_ID = "DisplayReportFragment.report_id";

    public static final String ACTION_REPORT_PICTURE_DOWNLOADED = "williamgbranco.comune.ACTION_REPORT_PICTURE_DOWNLOADED";
    public static final String ACTION_REPORT_FOOTAGE_DOWNLOADED = "williamgbranco.comune.ACTION_REPORT_FOOTAGE_DOWNLOADED";


    //private static boolean viewCreated;
    private boolean mLoading;
    private boolean mErrorLoading;
    private Integer mParamInstId;
    private Integer mParamReportId;
    private Report mReport;
    private UserManager mUserManager;
    private ReportManager mReportManager;
    private PublicInstitution mInstitution;

    //private ImageView mImageViewInstitutionTypeIcon; //imageview_institution_type_icon
    //private TextView mTextViewInstitutionName; //textview_institution_name
    //private TextView mTextViewInstitutionCompleteName; //textview_institution_complete_name
    private TextView mTextViewReportSentDate; //textview_report_sent_date
    private ImageView mButtonResponse; //button_response
    private RelativeLayout mContainerPictureThumbnail; //container_picture_thumbnail
    private ProgressBar mProgressBarThumbnail; //progress_bar_report_picture
    private ImageView mImageViewThumbnail; //imageview_thumbnail
    private RelativeLayout mContainerVideoPreview; //container_video_preview
    private ProgressBar mProgressBarVideoPreview; //progress_bar_report_footage
    //private ImageView mImageViewVideoPreview; //imageview_video_preview
    private VideoView mVideoViewPreview; //videoview_preview
    private EditText mEditTextUserComment; //edittext_user_comment
    private ProgressBar mProgressBar; //progress_bar
    private TextView mTextViewErrorFetchingList; //textview_error_fetching_list

    private BroadcastReceiver mOnReportPictureDownloaded = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (isAdded())
                getActivity().getApplicationContext().unregisterReceiver(mOnReportPictureDownloaded);

            if (intent != null)
            {
                int reportId = intent.getIntExtra(Constants.ACTION_EXTRA_FILE_REPORT_ID, -1);
                String fileUriText = intent.getStringExtra(Constants.ACTION_EXTRA_FILE_URI);
                mProgressBarThumbnail.setVisibility(View.INVISIBLE);
                if ((mReport != null) && (mReport.getId() == reportId) && (fileUriText != null))
                {
                    Uri fileUri = Uri.parse(fileUriText);
                    mReport.setPictureUri(fileUri);
                    setPictureThumbnail();
                }
            }
        }
    };

    private BroadcastReceiver mOnReportFootageDownloaded = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (isAdded())
                getActivity().getApplicationContext().unregisterReceiver(mOnReportFootageDownloaded);

            if (intent != null)
            {
                int reportId = intent.getIntExtra(Constants.ACTION_EXTRA_FILE_REPORT_ID, -1);
                String fileUriText = intent.getStringExtra(Constants.ACTION_EXTRA_FILE_URI);
                mProgressBarVideoPreview.setVisibility(View.INVISIBLE);
                if ((mReport != null) && (mReport.getId() == reportId) && (fileUriText != null))
                {
                    Uri fileUri = Uri.parse(fileUriText);
                    mReport.setFootageUri(fileUri);
                    setVideoPreview();
                }
            }
        }
    };


    public static DisplayReportFragment newInstance(Integer instId, Integer reportId)
    {
        DisplayReportFragment fragment = new DisplayReportFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INST_ID, instId);
        args.putInt(ARG_REPORT_ID, reportId);
        fragment.setArguments(args);
        return fragment;
    }

    public DisplayReportFragment()
    {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mLoading = true;
        mErrorLoading = false;
        mUserManager = UserManager.get(getActivity());
        mReportManager = ReportManager.get(getActivity());

        IntentFilter filter = new IntentFilter(ACTION_REPORT_PICTURE_DOWNLOADED);
        if (isAdded()) getActivity().getApplicationContext().registerReceiver(mOnReportPictureDownloaded, filter, Constants.PERM_PRIVATE, null);

        filter = new IntentFilter(ACTION_REPORT_FOOTAGE_DOWNLOADED);
        if (isAdded()) getActivity().getApplicationContext().registerReceiver(mOnReportFootageDownloaded, filter, Constants.PERM_PRIVATE, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_display_report, container, false);

        /*mImageViewInstitutionTypeIcon = (ImageView) v.findViewById(R.id.imageview_institution_type_icon);
        mTextViewInstitutionName = (TextView) v.findViewById(R.id.textview_institution_name);
        mTextViewInstitutionCompleteName = (TextView) v.findViewById(R.id.textview_institution_complete_name);
        mTextViewInstitutionCompleteName.setVisibility(View.GONE);*/

        mTextViewReportSentDate = (TextView) v.findViewById(R.id.textview_report_sent_date);
        mButtonResponse = (ImageView) v.findViewById(R.id.button_response);
        mContainerPictureThumbnail = (RelativeLayout) v.findViewById(R.id.container_picture_thumbnail);
        mImageViewThumbnail = (ImageView) v.findViewById(R.id.imageview_thumbnail);
        mProgressBarThumbnail = (ProgressBar) v.findViewById(R.id.progress_bar_report_picture);
        if (mProgressBarThumbnail != null) {
            mProgressBarThumbnail.setIndeterminate(true);
            mProgressBarThumbnail.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.logo_orange), android.graphics.PorterDuff.Mode.SRC_ATOP);
            mProgressBarThumbnail.setVisibility(View.INVISIBLE);
        }
        mContainerVideoPreview = (RelativeLayout) v.findViewById(R.id.container_video_preview);
        //mImageViewVideoPreview = (ImageView) v.findViewById(R.id.imageview_video_preview);
        mProgressBarVideoPreview = (ProgressBar) v.findViewById(R.id.progress_bar_report_footage);
        if (mProgressBarVideoPreview != null) {
            mProgressBarVideoPreview.setIndeterminate(true);
            mProgressBarVideoPreview.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.logo_orange), android.graphics.PorterDuff.Mode.SRC_ATOP);
            mProgressBarVideoPreview.setVisibility(View.INVISIBLE);
        }

        mVideoViewPreview = (VideoView) v.findViewById(R.id.videoview_preview);
        mEditTextUserComment = (EditText) v.findViewById(R.id.edittext_user_comment);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar); //progress_bar
        if (mProgressBar != null) {
            mProgressBar.setIndeterminate(true);
            mProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.logo_orange), android.graphics.PorterDuff.Mode.SRC_ATOP);
            mProgressBar.setVisibility(View.VISIBLE);
        }
        mTextViewErrorFetchingList = (TextView) v.findViewById(R.id.textview_error_fetching_list);

        return v;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState)
    {
        if (getArguments() != null)
        {
            mParamInstId = getArguments().getInt(ARG_INST_ID);
            mInstitution = PublicInstitutionManager.get(getActivity()).getReportedInstitutionById(mParamInstId);

            User user = mUserManager.getCurrentUser();
            mParamReportId = getArguments().getInt(ARG_REPORT_ID);

            mReportManager.fetchReportById(user, mParamReportId, new GetReportCallback() {
                @Override
                public void done(Report returnedReport) {
                    try {
                        mErrorLoading = false;
                        mLoading = false;

                        if (returnedReport != null) {
                            Log.i(TAG, "returnedReport: " + returnedReport.getId());
                            mReport = returnedReport;
                        } else {
                            mErrorLoading = true;
                        }
                        //if (viewCreated)
                        try {
                            updateUI();
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
            }, false);
        }
    }

    private void updateUI()
    {
        Log.i(TAG, "updateUI()");

        mProgressBar.setVisibility(View.INVISIBLE);
        mTextViewErrorFetchingList.setVisibility(View.INVISIBLE);

        Log.i(TAG, "mLoading: " + mLoading + " , mErrorLoading: " + mErrorLoading);

        if (!mLoading) {
            if (!mErrorLoading) {
                Log.i(TAG, "carregado com sucesso");
                getReportPageReady();
            } else {
                Log.i(TAG, "carregado com erro");
                mTextViewErrorFetchingList.setText(getResources().getString(R.string.msg_error_loading_list) + ".");
                mTextViewErrorFetchingList.setVisibility(View.VISIBLE);
            }
        } else {
            Log.i(TAG, "ainda carregando");
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void getReportPageReady()
    {
        Log.i(TAG, "getReportPageReady() for report: " + mReport.getComment());

        if (mReport != null)
        {
            String textSentDate = getResources().getString(R.string.text_sent_date);
            textSentDate = textSentDate + " " +
                    mReport.madeAt().get(Calendar.DAY_OF_MONTH) + " " +
                    DateUtils.getMesAbrev(getActivity(), mReport.madeAt().get(Calendar.MONTH)).toUpperCase() + " " +
                    mReport.madeAt().get(Calendar.YEAR);
            mTextViewReportSentDate.setText(textSentDate);

            mEditTextUserComment.setText(mReport.getComment());

            if (mReport.getResponseId() > 0)
            {
                Drawable icon = null;
                if (mReport.isResponseVisualized()) {
                    icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_report_replied_light_orange);
                } else {
                    icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_report_replied);
                }
                //mButtonResponse.getBackground().setColorFilter(getResources().getColor(R.color.starFullySelected), PorterDuff.Mode.SRC_ATOP);
                mButtonResponse.setImageDrawable(icon);

                mButtonResponse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent i = new Intent(getActivity(), DisplayReportResponseActivity.class);
                        i.putExtra(DisplayReportResponseActivity.EXTRA_INST_ID, mParamInstId);
                        i.putExtra(DisplayReportResponseActivity.EXTRA_REPORT_ID, mParamReportId);
                        i.putExtra(DisplayReportResponseActivity.EXTRA_RESPONSE_ID, mReport.getResponseId());
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        getActivity().finish();
                    }
                });

                mButtonResponse.setVisibility(View.VISIBLE);
            }

            Log.i(TAG, "report " + mReport.getId() + ", getPictureUri(): " + mReport.getPictureUri());
            if (mReport.getPictureUri() == null) {
                if (mReport.getPictureId() > 0) {
                    mContainerPictureThumbnail.setVisibility(View.VISIBLE);
                    getReportPicture(mReport);
                }
            }
            else {
                mImageViewThumbnail.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        setPictureThumbnail();
                    }
                });
                mContainerPictureThumbnail.setVisibility(View.VISIBLE);
            }

            Log.i(TAG, "report " + mReport.getId() + ", getFootageUri(): " + mReport.getFootageUri());
            if (mReport.getFootageUri() == null) {
                if (mReport.getFootageId() > 0) {
                    mContainerVideoPreview.setVisibility(View.VISIBLE);
                    getReportFootage(mReport);
                }
            }
            else {
                mContainerVideoPreview.setVisibility(View.VISIBLE);
                setVideoPreview();
            }
        }
    }

    private void getReportPicture(Report pReport)
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;

        if (pReport.getPictureId() != -1)
        {
            mImageViewThumbnail.setImageDrawable(null);
            mProgressBarThumbnail.setVisibility(View.VISIBLE);

            Log.i(TAG, "vai baixar a foto");
            new ServerRequests(getActivity()).downloadReportPicture(pReport, imageFileName, new DownloadedFileCallbacks() {
                @Override
                public void done(String contentType, String disposition, int contentLength, String originalFileName, Uri fileUri) {

                    try {
                        if ((contentLength > 0) && (fileUri != null)) {
                            Intent intent = new Intent(ACTION_REPORT_PICTURE_DOWNLOADED);
                            intent.putExtra(Constants.ACTION_EXTRA_FILE_REPORT_ID, mReport.getId());
                            intent.putExtra(Constants.ACTION_EXTRA_FILE_URI, fileUri.toString());

                            if (isAdded())
                                getActivity().getApplicationContext().sendBroadcast(intent, Constants.PERM_PRIVATE);
                        }
                        Log.i(TAG, "contentType: " + contentType + ", contentLength: " + contentLength + ", originalFileName: " + originalFileName);
                        Log.i(TAG, "filePath: " + fileUri);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            });
        }
    }

    private void getReportFootage(Report pReport)
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "VID_" + timeStamp;

        if (pReport.getFootageId() != -1)
        {
            //mImageViewVideoPreview.setImageDrawable(null);
            mProgressBarVideoPreview.setVisibility(View.VISIBLE);

            Log.i(TAG, "vai baixar o video");
            new ServerRequests(getActivity()).downloadReportFootage(pReport, videoFileName, new DownloadedFileCallbacks() {
                @Override
                public void done(String contentType, String disposition, int contentLength, String originalFileName, Uri fileUri) {
                    try {
                        if ((contentLength > 0) && (fileUri != null)) {
                            mUserManager.updateUserPhotoUri(fileUri);

                            Intent intent = new Intent(ACTION_REPORT_FOOTAGE_DOWNLOADED);
                            intent.putExtra(Constants.ACTION_EXTRA_FILE_REPORT_ID, mReport.getId());
                            intent.putExtra(Constants.ACTION_EXTRA_FILE_URI, fileUri.toString());

                            if (isAdded())
                                getActivity().getApplicationContext().sendBroadcast(intent, Constants.PERM_PRIVATE);
                        }
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
    }

    private void setPictureThumbnail()
    {
        if (mReport != null)
        {
            // Get the dimensions of the View
            int targetW = mImageViewThumbnail.getWidth();
            int targetH = mImageViewThumbnail.getHeight();
            Log.i(TAG, "targetW = " + targetW + ", targetH = " + targetH);
            // Get the dimensions of the bitmap

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(mReport.getPicturePath(), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            Bitmap bitmap = BitmapFactory.decodeFile(mReport.getPicturePath(), bmOptions);
            mImageViewThumbnail.setImageBitmap(bitmap);
            //mImageViewThumbnail.setBackgroundResource(R.drawable.imageview_report_media);

            mImageViewThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mReport.getPictureUri() != null)
                    {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        Uri photoUri = mReport.getPictureUri();

                        Log.i(TAG, "uri: " + mReport.getPictureUri() + ", path: " + mReport.getPicturePath());

                        intent.setDataAndType(photoUri, "image/*");
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void setVideoPreview()
    {
        if (mReport != null)
        {
            mVideoViewPreview.setVideoURI(mReport.getFootageUri());
            mVideoViewPreview.seekTo(100);
            mVideoViewPreview.setVisibility(View.VISIBLE);
            //mImageViewVideoPreview.setBackgroundResource(R.drawable.imageview_report_media);

            mVideoViewPreview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.i(TAG, "click video");

                    if (mReport.getFootageUri() != null)
                    {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        Uri videoUri = mReport.getFootageUri();

                        Log.i(TAG, "uri: " + videoUri + ", path: " + mReport.getFootagePath());

                        intent.setDataAndType(videoUri, "video/mp4");
                        startActivity(intent);
                    }

                    return true;
                }
            });
        }
    }
}
