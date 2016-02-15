package williamgbranco.comune.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import williamgbranco.comune.R;
import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.manager.PublicInstitutionManager;
import williamgbranco.comune.manager.ReportManager;
import williamgbranco.comune.manager.UserManager;
import williamgbranco.comune.report.Report;
import williamgbranco.comune.user.User;
import williamgbranco.comune.util.MediaUtils;

public class ReportFragment extends Fragment
{
    public static final String TAG = "comune.ReportFragment";

    public static final String EXTRA_INST_ID = "ReportFragment.inst_id";

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int REQUEST_VIDEO_CAPTURE = 200;

    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;


    private ImageView mImageViewInstitutionTypeIcon; //imageview_institution_type_icon
    private TextView mTextViewInstitutionName; //textview_institution_name
    private TextView mTextViewInstitutionCompleteName; //textview_institution_complete_name
    private LinearLayout mContainerInstitutionCompleteName; //container_institution_complete_name
    private EditText mEditTextUserComment; //textview_user_comment
    private LinearLayout mCameraContainer; //camera_container
    private ImageView mButtonCapturePicture; //button_capture_picture
    private View mSeparationBar1; //separation_bar_1
    private ImageView mButtonRemovePicture; //button_remove_picture
    private ImageView mImageViewThumbnail; //imageview_thumbnail
    private VideoView mVideoViewPreview; //videoview_preview
    private ImageView mImageViewVideoPreview; //imageview_video_preview
    private ImageView mButtonRecordVideo; //button_record_video
    private View mSeparationBar2; //separation_bar_2
    private ImageView mButtonRemoveVideo; //button_remove_video
    private Button mButtonSendReport; //button_send_report

    private Report mReport;
    private Integer mArgInstId;
    private PublicInstitution mPublicInstitution;
    private PublicInstitutionManager mInstitutionManager;
    private ReportManager mReportManager;
    private User mUser;
    private UserManager mUserManager;


    public static ReportFragment newInstance(Integer pInstId)
    {
        Bundle args = new Bundle();
        args.putInt(EXTRA_INST_ID, pInstId);

        ReportFragment fragment = new ReportFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public ReportFragment()
    {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);

        mUserManager = UserManager.get(getActivity());
        mUser = mUserManager.getCurrentUser();
        mReportManager = ReportManager.get(getActivity());
        mInstitutionManager = PublicInstitutionManager.get(getActivity());

        Bundle args = getArguments();
        if (args != null)
        {
            mArgInstId = args.getInt(EXTRA_INST_ID, -1);

            mReport = new Report(mUser.getUserId(), mArgInstId, getActivity());

            if (mArgInstId != -1)
            {
                mPublicInstitution = mInstitutionManager.getPublicInstitutionById(mArgInstId);
                mInstitutionManager.setCurrentInstitution(mPublicInstitution);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_report, container, false);

        mPublicInstitution = mInstitutionManager.getCurrentInstitution();

        if (mPublicInstitution != null) {
            mImageViewInstitutionTypeIcon = (ImageView) v.findViewById(R.id.imageview_institution_type_icon);
            mImageViewInstitutionTypeIcon.setImageResource(mPublicInstitution.getPlaceBlackIconId());
            mTextViewInstitutionName = (TextView) v.findViewById(R.id.textview_institution_name);
            mTextViewInstitutionCompleteName = (TextView) v.findViewById(R.id.textview_institution_complete_name);
            mContainerInstitutionCompleteName = (LinearLayout) v.findViewById(R.id.container_institution_complete_name);
            mContainerInstitutionCompleteName.setVisibility(View.GONE);

            String abbrev = mPublicInstitution.getNomeAbreviado();
            String nome = mPublicInstitution.getNome();
            if ((abbrev != null) && (!abbrev.toLowerCase().equals("null")) && (abbrev.length() > 0)) {
                mTextViewInstitutionName.setText(abbrev);
            } else {
                mTextViewInstitutionName.setText(nome);
            }

            mTextViewInstitutionCompleteName.setText(nome);

            mEditTextUserComment = (EditText) v.findViewById(R.id.textview_user_comment);

            mCameraContainer = (LinearLayout) v.findViewById(R.id.camera_container);
            if (!deviceSupportCamera()) {
                mCameraContainer.setVisibility(View.INVISIBLE);
            }

            mButtonCapturePicture = (ImageView) v.findViewById(R.id.button_capture_picture);
            mButtonCapturePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    capturePicture();
                }
            });

            mSeparationBar1 = v.findViewById(R.id.separation_bar_1);

            mButtonRemovePicture = (ImageView) v.findViewById(R.id.button_remove_picture);
            mButtonRemovePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removePicture();
                }
            });

            mImageViewThumbnail = (ImageView) v.findViewById(R.id.imageview_thumbnail);
            mImageViewThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mReport.getPictureUri() != null) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        Uri photoUri = mReport.getPictureUri();

                        Log.i(TAG, "uri: " + mReport.getPictureUri() + ", path: " + mReport.getPicturePath());

                        intent.setDataAndType(photoUri, "image/*");
                        startActivity(intent);
                    }
                }
            });

            mVideoViewPreview = (VideoView) v.findViewById(R.id.videoview_preview);
            mVideoViewPreview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.i(TAG, "click video");

                    if (mReport.getFootageUri() != null) {
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

            mImageViewVideoPreview = (ImageView) v.findViewById(R.id.imageview_video_preview);

            mButtonRecordVideo = (ImageView) v.findViewById(R.id.button_record_video);
            mButtonRecordVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recordVideo();
                }
            });

            mSeparationBar2 = v.findViewById(R.id.separation_bar_2);

            mButtonRemoveVideo = (ImageView) v.findViewById(R.id.button_remove_video);
            mButtonRemoveVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeVideo();
                }
            });

            mButtonSendReport = (Button) v.findViewById(R.id.button_send_report);
            mButtonSendReport.getBackground().setColorFilter(getResources().getColor(R.color.complete_survey_button_color), PorterDuff.Mode.SRC_ATOP);
            mButtonSendReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mReport.setComment(mEditTextUserComment.getText().toString());

                    if ((mReport != null) && (mReport.getComment() != null) && (mReport.getComment().length() > 0)) {
                        mReportManager.insertReport(mReport);

                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.msg_null_comment), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            getActivity().finish();
        }

        return v;
    }

    private boolean deviceSupportCamera()
    {
        if (getActivity().getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void capturePicture()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null)
        {
            Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

            if (fileUri != null)
            {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
            else
            {
                Log.i(TAG, "capturePicture(), file URI is null");
            }
        }
        else
        {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_no_activity_capture_image), Toast.LENGTH_SHORT).show();
        }
    }

    private void recordVideo()
    {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null)
        {
            startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
        }
        else
        {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_no_activity_capture_video), Toast.LENGTH_SHORT).show();
        }
    }

    private void setPicture()
    {
        try {
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
            mImageViewThumbnail.setBackgroundResource(R.drawable.imageview_profile_photo);

            mSeparationBar1.setVisibility(View.VISIBLE);
            mButtonRemovePicture.setVisibility(View.VISIBLE);
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void removePicture()
    {
        mImageViewThumbnail.setImageDrawable(null);
        mImageViewThumbnail.setVisibility(View.INVISIBLE);
        mReport.setPictureUri(null);
        mReport.setPicturePath(null);

        mSeparationBar1.setVisibility(View.INVISIBLE);
        mButtonRemovePicture.setVisibility(View.INVISIBLE);
    }

    private void removeVideo()
    {
        mImageViewVideoPreview.setVisibility(View.INVISIBLE);
        mVideoViewPreview.setVisibility(View.INVISIBLE);
        mVideoViewPreview.setVideoURI(null);
        mReport.setFootageUri(null);
        mReport.setFootagePath(null);

        mSeparationBar2.setVisibility(View.INVISIBLE);
        mButtonRemoveVideo.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE)
        {
            if (resultCode == getActivity().RESULT_OK)
            {
                setPicture();
            }
            else if (resultCode == getActivity().RESULT_CANCELED)
            {
                //Toast.makeText(getActivity(), "cancelou foto", Toast.LENGTH_SHORT);
            }
        }
        else if (requestCode == REQUEST_VIDEO_CAPTURE)
        {
            if (resultCode == getActivity().RESULT_OK)
            {
                Uri videoUri = data.getData();

                mReport.setFootageUri(videoUri);
                mReport.setFootagePath(MediaUtils.getRealPathFromURI(getActivity(), videoUri));

                Log.i(TAG, "videoURI: " + videoUri);
                Log.i(TAG, "mReport.getFootagePath(): "+mReport.getFootagePath());

                mVideoViewPreview.setVideoURI(videoUri);
                mVideoViewPreview.seekTo(100);
                mVideoViewPreview.setVisibility(View.VISIBLE);
                mImageViewVideoPreview.setBackgroundResource(R.drawable.imageview_profile_photo);

                mSeparationBar2.setVisibility(View.VISIBLE);
                mButtonRemoveVideo.setVisibility(View.VISIBLE);
            }
            else if (resultCode == getActivity().RESULT_CANCELED)
            {
                //Toast.makeText(getActivity(), "cancelou video", Toast.LENGTH_SHORT);
            }
        }
    }

    private Uri getOutputMediaFileUri(int type)
    {
        try
        {
            return Uri.fromFile(createOutputMediaFile(type));
        }
        catch (IOException ex)
        {}

        return null;
    }

    private File createOutputMediaFile(int type) throws IOException
    {
        File media = null;

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        if (type == MEDIA_TYPE_IMAGE)
        {
            String imageFileName = "IMG_" + timeStamp;

            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            media = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            mReport.setPictureUri(Uri.fromFile(media));
        }

        return media;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if ((mReport != null) && (mReport.getFootageUri() != null) && (mVideoViewPreview != null))
        {
            mVideoViewPreview.seekTo(100);
        }
    }

    @Override
    public void onDestroy()
    {
        mReport = null;
        super.onDestroy();
    }

}
