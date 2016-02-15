package williamgbranco.comune.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Calendar;

import williamgbranco.comune.R;
import williamgbranco.comune.callback.GetReportCallback;
import williamgbranco.comune.manager.ReportManager;
import williamgbranco.comune.manager.UserManager;
import williamgbranco.comune.report.Report;
import williamgbranco.comune.user.User;
import williamgbranco.comune.util.DateUtils;


public class DisplayReportResponseFragment extends Fragment
{
    private static final String TAG = "comune.DisRportRespFrag";

    public static final String ARG_INST_ID = "DisplayReportResponseFragment.inst_id";
    public static final String ARG_REPORT_ID = "DisplayReportResponseFragment.report_id";
    public static final String ARG_RESPONSE_ID = "DisplayReportResponseFragment.response_id";

    private Integer mParamInstId;
    private Integer mParamReportId;
    private Integer mParamResponseId;
    private Report mReport;
    private Report mResponse;
    private boolean mLoadingHeader;
    private boolean mLoadingReport;
    private boolean mLoadingResponse;
    private boolean mErrorLoadingReport;
    private boolean mErrorLoadingResponse;
    private UserManager mUserManager;
    private ReportManager mReportManager;
    //private PublicInstitution mInstitution;
    //private PublicInstitutionManager mInstitutionManager;

    private ProgressBar mProgressBarHeader; //progressbar_header
    //private ImageView mImageViewInstitutionTypeIcon; //imageview_institution_type_icon
    //private TextView mTextViewInstitutionName; //textview_institution_name
    private RelativeLayout mReportMediaContainer; //report_media_container
    private ImageView mImageViewThumbnail;      //imageview_thumbnail
    private ImageView mImageViewVideoPreview;   //imageview_video_preview
    private VideoView mVideoViewPreview;        //videoview_preview
    private EditText mEditTextUserComment;      //edittext_user_comment
    private ProgressBar mProgressBarReport; //progress_bar_report
    private TextView mTextViewErrorFetchingReport; //textview_error_fetching_report
    private TextView mTextViewReportDate;       //textview_report_date
    private ImageView mImageViewReportPicture; //imageview_report_picture
    private ImageView mImageViewReportFootage; //imageview_report_footage
    private TextView mTextViewResponseDate;     //textview_response_date
    private EditText mEditTextResponse;         //edittext_comment_response
    private ProgressBar mProgressBarResponse; //progress_bar_response
    private TextView mTextViewErrorFetchingResponse; //textview_error_fetching_response


    public static DisplayReportResponseFragment newInstance(Integer instId, Integer reportId, Integer responseId)
    {
        DisplayReportResponseFragment fragment = new DisplayReportResponseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INST_ID, instId);
        args.putInt(ARG_REPORT_ID, reportId);
        args.putInt(ARG_RESPONSE_ID, responseId);
        fragment.setArguments(args);
        return fragment;
    }

    public DisplayReportResponseFragment()
    {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mLoadingReport = true;
        mLoadingResponse = true;
        mErrorLoadingReport = false;
        mErrorLoadingResponse = false;

        mUserManager = UserManager.get(getActivity());
        mReportManager = ReportManager.get(getActivity());

        if (getArguments() != null)
        {
            mParamInstId = getArguments().getInt(ARG_INST_ID);
            //fetchPublicInstitution();

            User user = mUserManager.getCurrentUser();
            mParamReportId = getArguments().getInt(ARG_REPORT_ID);
            mParamResponseId = getArguments().getInt(ARG_RESPONSE_ID);
            fetchReportResponse(user, mParamReportId, mParamResponseId);
        }
    }

    private void fetchReportResponse(User user, Integer reportId, Integer responseId)
    {
        mReportManager.fetchReportById(user, reportId,
                new GetReportCallback() {
                    @Override
                    public void done(Report returnedReport)
                    {
                        try {
                            if (returnedReport != null)
                            {
                                mReport = returnedReport;

                                mLoadingReport = false;

                                try {
                                    updateReportUI();
                                }
                                catch (Exception e){}
                            }
                            else
                            {
                                mErrorLoadingReport = true;
                            }
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG, e.toString());
                        }
                    }
                }, false);

        mReportManager.fetchReportById(user, responseId, new GetReportCallback() {
            @Override
            public void done(Report returnedResponse)
            {
                try {
                    if (returnedResponse != null)
                    {
                        mResponse = returnedResponse;

                                /*if (mReport != null)
                                {
                                    mReportManager.addResponse(mReport, mResponse);
                                }*/

                        mLoadingResponse = false;

                        try {
                            updateResponseUI();
                        }
                        catch (Exception e){}
                    }
                    else {
                        mErrorLoadingResponse = true;
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
        }, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_display_report_response, container, false);

        mImageViewThumbnail = (ImageView) v.findViewById(R.id.imageview_thumbnail);
        mReportMediaContainer = (RelativeLayout) v.findViewById(R.id.report_media_container);
        mImageViewVideoPreview = (ImageView) v.findViewById(R.id.imageview_video_preview);
        mVideoViewPreview = (VideoView) v.findViewById(R.id.videoview_preview);
        mEditTextUserComment = (EditText) v.findViewById(R.id.edittext_user_comment);

        mProgressBarReport = (ProgressBar) v.findViewById(R.id.progress_bar_report);
        if (mProgressBarReport != null) {
            mProgressBarReport.setIndeterminate(true);
            mProgressBarReport.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.logo_orange), android.graphics.PorterDuff.Mode.SRC_ATOP);
            mProgressBarReport.setVisibility(View.INVISIBLE);
        }

        mTextViewErrorFetchingReport = (TextView) v.findViewById(R.id.textview_error_fetching_report);
        mTextViewReportDate = (TextView) v.findViewById(R.id.textview_report_date);

        mImageViewReportPicture = (ImageView) v.findViewById(R.id.imageview_report_picture);
        mImageViewReportFootage = (ImageView) v.findViewById(R.id.imageview_report_footage);

        mTextViewResponseDate = (TextView) v.findViewById(R.id.textview_response_date);
        mEditTextResponse = (EditText) v.findViewById(R.id.edittext_comment_response);
        mProgressBarResponse = (ProgressBar) v.findViewById(R.id.progress_bar_response);

        if (mProgressBarResponse != null) {
            mProgressBarResponse.setIndeterminate(true);
            mProgressBarResponse.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.logo_orange), android.graphics.PorterDuff.Mode.SRC_ATOP);
            mProgressBarResponse.setVisibility(View.INVISIBLE);
        }
        mTextViewErrorFetchingResponse = (TextView) v.findViewById(R.id.textview_error_fetching_response);

        try {
            //updateHeaderUI();
            updateReportUI();
            updateResponseUI();
        }
        catch (Exception e){}

        return v;
    }

    private void updateReportUI()
    {
        mProgressBarReport.setVisibility(View.INVISIBLE);
        mTextViewErrorFetchingReport.setVisibility(View.INVISIBLE);

        if (!mLoadingReport)
        {
            if (!mErrorLoadingReport)
            {
                getReportReady();
            }
            else
            {
                mTextViewErrorFetchingReport.setText(getResources().getString(R.string.msg_error_loading_list) + ".");
                mTextViewErrorFetchingReport.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            mProgressBarReport.setVisibility(View.VISIBLE);
        }
    }

    private void updateResponseUI()
    {
        mProgressBarResponse.setVisibility(View.INVISIBLE);
        mTextViewErrorFetchingResponse.setVisibility(View.INVISIBLE);

        if (!mLoadingResponse)
        {
            if (!mErrorLoadingResponse)
            {
                getResponseReady();
            }
            else
            {
                mTextViewErrorFetchingResponse.setText(getResources().getString(R.string.msg_error_loading_list) + ".");
                mTextViewErrorFetchingResponse.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            mProgressBarResponse.setVisibility(View.VISIBLE);
        }
    }

    private void getReportReady()
    {
        if (mReport != null)
        {
            /*if (mReport.getPicturePath() != null)
            {
                setPicture(mReport);
            }
            else if (mReport.getFootageUri() != null)
            {
                setVideoPreview(mReport);
            }*/

            mEditTextUserComment.setText(mReport.getComment());

            if (mReport.getPictureId() > 0) mImageViewReportPicture.setVisibility(View.VISIBLE);

            if (mReport.getFootageId() > 0) mImageViewReportFootage.setVisibility(View.VISIBLE);

            String textSentDate = mReport.madeAt().get(Calendar.DAY_OF_MONTH) + " " +
                    DateUtils.getMesAbrev(getActivity(), mReport.madeAt().get(Calendar.MONTH)).toUpperCase() + " " +
                    mReport.madeAt().get(Calendar.YEAR);
            mTextViewReportDate.setText(textSentDate);
            mTextViewReportDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), DisplayReportActivity.class);
                    i.putExtra(DisplayReportActivity.EXTRA_INST_ID, mParamInstId);
                    i.putExtra(DisplayReportActivity.EXTRA_REPORT_ID, mParamReportId);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    getActivity().finish();
                }
            });

            if (mResponse != null) markResponseAsVisualized(mReport.getId(), mResponse.getId());
        }
    }

    private void getResponseReady()
    {
        if (mResponse != null)
        {
            String textSentDate = mResponse.madeAt().get(Calendar.DAY_OF_MONTH) + " " +
                    DateUtils.getMesAbrev(getActivity(), mResponse.madeAt().get(Calendar.MONTH)).toUpperCase() + " " +
                    mResponse.madeAt().get(Calendar.YEAR);
            mTextViewResponseDate.setText(textSentDate);

            mEditTextResponse.setText(mResponse.getComment());

            if (mReport != null) markResponseAsVisualized(mReport.getId(), mResponse.getId());
        }
        else
        {
            mTextViewErrorFetchingResponse.setVisibility(View.VISIBLE);
        }
    }

    private void markResponseAsVisualized(int reportId, int responseId)
    {
        mReportManager.markResponseAsVisualized(reportId, responseId);
    }


    private void setPicture(Report pReport)
    {
        if ((pReport != null) && (pReport.getPicturePath() != null))
        {
            // Get the dimensions of the View
            int targetW = mImageViewThumbnail.getWidth();
            int targetH = mImageViewThumbnail.getHeight();
            Log.i(TAG, "targetW = " + targetW + ", targetH = " + targetH);
            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pReport.getPicturePath(), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            Bitmap bitmap = BitmapFactory.decodeFile(pReport.getPicturePath(), bmOptions);
            mImageViewThumbnail.setImageBitmap(bitmap);
            mImageViewThumbnail.setBackgroundResource(R.drawable.imageview_profile_photo);

            mReportMediaContainer.setVisibility(View.VISIBLE);
        }
    }

    private void setVideoPreview(Report pReport)
    {
        if ((pReport != null) && (pReport.getFootageUri() != null))
        {
            mVideoViewPreview.setVideoURI(pReport.getFootageUri());
            mVideoViewPreview.seekTo(100);
            mVideoViewPreview.setVisibility(View.VISIBLE);
            mImageViewVideoPreview.setBackgroundResource(R.drawable.imageview_profile_photo);

            mReportMediaContainer.setVisibility(View.VISIBLE);
        }
    }
}
