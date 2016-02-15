package williamgbranco.comune.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import williamgbranco.comune.R;
import williamgbranco.comune.callback.GetUserCallback;
import williamgbranco.comune.manager.UserManager;
import williamgbranco.comune.user.User;
import williamgbranco.comune.util.MediaUtils;


public class RegisterFragment extends Fragment
{
    private static String TAG = "comune.RegisterFragment";

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int RESULT_LOAD_IMG = 101;

    private static final int MEDIA_TYPE_IMAGE = 1;

    /*private static final String EXTRA_PAGE = "register_page";*/

    public static int REGISTER_ACTIV_FIRST_PAGE = 0;
    public static int REGISTER_ACTIV_MIDDLE_PAGE = 1;
    public static int REGISTER_ACTIV_LAST_PAGE = 2;

    //private static RegisterFragment mRegisterFragment;

    private UserManager mUserManager;
    private Integer mCurentPage;
    private User newUser;
    /*private boolean inFirstPage;
    private boolean inMiddlePage;
    private boolean inLastPage;*/

    private LinearLayout mFisrtPage;
    private EditText mEditTextFirstName; //edittext_user_first_name
    private EditText mEditTextLastName; //edittext_user_last_name
    private EditText mEditTextAreaCode; //edittext_area_code
    private EditText mEditTextPhoneNumber; //edittext_phone_number
    private LinearLayout mContainerDateOfBirth; //container_date_of_birth
    private EditText mEditTextDay; //edittext_bday_day
    private EditText mEditTextMonth; //edittext_bday_month
    private EditText mEditTextYear; //edittext_bday_year

    private LinearLayout mMiddlePage;
    private ImageView mUserPicture;
    private ImageView mButtonDeletePicture; //button_delete_picture
    private ImageView mButtonPickPicture; //button_pick_picture
    private ImageView mButtonTakePicture; //button_take_picture

    private LinearLayout mLastPage;
    private EditText mEditTextUserEmail; //edittext_user_email
    private EditText mEditTextUserPassword; //edittext_user_password
    private EditText mEditTextUserRepeatedPassword; //edittext_user_repeated_password

    private Button mButtonReturn; //button_return
    private Button mButtonContinue; //button_continue

    private ProgressDialog ringProgressDialog;

    private GetUserCallback mGetUserCallback = new GetUserCallback() {
        @Override
        public void done(User returnedUser)
        {
            try {
                if (ringProgressDialog != null) ringProgressDialog.dismiss();

                if (returnedUser != null)
                {
                    closeActivity();
                }
                else
                {
                    Toast.makeText(getActivity(), R.string.msg_incorrect_registration_info, Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }
        }

        @Override
        public void onNetworkNonAvailable()
        {
            if (ringProgressDialog != null) ringProgressDialog.dismiss();

            Toast.makeText(getActivity(), R.string.msg_network_non_available, Toast.LENGTH_LONG).show();
        }
    };

    private void closeActivity()
    {
        if (isAdded()) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }
    }

    public RegisterFragment()
    {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        initializeUserManager();

        newUser = new User();
        mCurentPage = REGISTER_ACTIV_FIRST_PAGE;
    }

    private void initializeUserManager()
    {
        mUserManager = UserManager.get(getActivity());
        mUserManager.initialize();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        updateUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_register, container, false);

        mFisrtPage = (LinearLayout) v.findViewById(R.id.first_page);
        mMiddlePage = (LinearLayout) v.findViewById(R.id.middle_page);
        mLastPage = (LinearLayout) v.findViewById(R.id.last_page);

        mEditTextFirstName = (EditText) v.findViewById(R.id.edittext_user_first_name);
        mEditTextLastName = (EditText) v.findViewById(R.id.edittext_user_last_name);
        mEditTextAreaCode = (EditText) v.findViewById(R.id.edittext_area_code);
        mEditTextPhoneNumber = (EditText) v.findViewById(R.id.edittext_phone_number);
        mContainerDateOfBirth = (LinearLayout) v.findViewById(R.id.container_date_of_birth);
        mEditTextDay = (EditText) v.findViewById(R.id.edittext_bday_day);
        mEditTextMonth = (EditText) v.findViewById(R.id.edittext_bday_month);
        mEditTextYear = (EditText) v.findViewById(R.id.edittext_bday_year);

        mUserPicture = (ImageView) v.findViewById(R.id.user_picture);
        mButtonDeletePicture = (ImageView) v.findViewById(R.id.button_delete_picture);
        mButtonDeletePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePicture();
                mButtonDeletePicture.setVisibility(View.INVISIBLE);
            }
        });

        mButtonTakePicture = (ImageView) v.findViewById(R.id.button_take_picture);
        mButtonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePicture();
            }
        });

        mButtonPickPicture = (ImageView) v.findViewById(R.id.button_pick_picture);
        mButtonPickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPicture();
            }
        });

        mEditTextUserEmail = (EditText) v.findViewById(R.id.edittext_user_email);
        mEditTextUserPassword = (EditText) v.findViewById(R.id.edittext_user_password);
        mEditTextUserRepeatedPassword = (EditText) v.findViewById(R.id.edittext_user_repeated_password);

        mButtonReturn = (Button) v.findViewById(R.id.button_return);
        mButtonReturn.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        mButtonContinue = (Button) v.findViewById(R.id.button_continue);
        mButtonContinue.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        return v;
    }

    private void updateUI()
    {
        mFisrtPage.setVisibility(View.INVISIBLE);
        mMiddlePage.setVisibility(View.INVISIBLE);
        mLastPage.setVisibility(View.INVISIBLE);

        mButtonContinue.setAllCaps(false);
        mButtonContinue.setText(R.string.text_button_continue);
        mButtonContinue.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        mButtonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (checkFormFields())
                {
                    if (mCurentPage < REGISTER_ACTIV_LAST_PAGE) {
                        mCurentPage++;
                    }
                    updateUI();
                }
            }
        });

        if (mCurentPage == REGISTER_ACTIV_FIRST_PAGE)
        {
            mButtonReturn.setVisibility(View.INVISIBLE);
            mFisrtPage.setVisibility(View.VISIBLE);
        }
        else
        {
            mButtonReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (mCurentPage > REGISTER_ACTIV_FIRST_PAGE)
                        mCurentPage--;

                    updateUI();
                }
            });
            mButtonReturn.setVisibility(View.VISIBLE);

            if (mCurentPage == REGISTER_ACTIV_MIDDLE_PAGE)
            {
                mButtonContinue.setText(R.string.text_button_jump);

                if (newUser.getPictureUri() != null) mButtonContinue.setText(R.string.text_button_continue);

                mMiddlePage.setVisibility(View.VISIBLE);
            }
            else if (mCurentPage == REGISTER_ACTIV_LAST_PAGE)
            {
                mButtonContinue.setAllCaps(true);
                mButtonContinue.setText(R.string.text_button_conclude);
                mButtonContinue.getBackground().setColorFilter(getResources().getColor(R.color.complete_survey_button_color), PorterDuff.Mode.SRC_ATOP);
                mButtonContinue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkFormFields()) {
                            ringProgressDialog = ProgressDialog.show(getActivity(),
                                    getResources().getString(R.string.msg_wait),
                                    getResources().getString(R.string.msg_registering), true);

                            mUserManager.registerNewUser(newUser, mGetUserCallback);
                        }
                    }
                });

                mLastPage.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean checkFormFields()
    {
        if (mCurentPage == REGISTER_ACTIV_FIRST_PAGE)
        {
            return checkFormFieldsOnFirstPage();
        }
        else if (mCurentPage == REGISTER_ACTIV_MIDDLE_PAGE)
        {
            return checkFormFieldsOnMiddlePage();
        }
        else if (mCurentPage == REGISTER_ACTIV_LAST_PAGE)
        {
            return checkFormFieldsOnLastPage();
        }

        return false;
    }

    private boolean validateDateOfBirth()
    {
        mEditTextDay.setError(null);
        mEditTextMonth.setError(null);
        mEditTextYear.setError(null);

        String dayText = mEditTextDay.getText().toString();
        String monthText = mEditTextMonth.getText().toString();
        String yearText = mEditTextYear.getText().toString();

        int day = -1;
        if (dayText.length() > 0) day = Integer.parseInt(dayText);
        int month = -1;
        if (monthText.length() > 0) month = Integer.parseInt(monthText);
        int year = -1;
        if (yearText.length() > 0) year = Integer.parseInt(yearText);

        newUser.setDateOfBirth(null);

        if ((day > 0) && (month > 0) && (year > 0))
        {
            month = month - 1;
            GregorianCalendar date = new GregorianCalendar(year, month, day);
            GregorianCalendar now = new GregorianCalendar();
            GregorianCalendar past = new GregorianCalendar();
            past.add(Calendar.YEAR, -140);

            if (((day == date.get(Calendar.DAY_OF_MONTH)) &&
            (month == date.get(Calendar.MONTH)) &&
            (year == date.get(Calendar.YEAR))
            ) && !(date.after(now)) && !(date.before(past)))
            {
                newUser.setDateOfBirth(date);

                return true;
            }
            else
            {
                mEditTextDay.setError(getResources().getString(R.string.msg_invalide_date));
                mEditTextMonth.setError(getResources().getString(R.string.msg_invalide_date));
                mEditTextYear.setError(getResources().getString(R.string.msg_invalide_date));
            }
        }
        else
        {
            if (day <= 0)
            {
                mEditTextDay.setError(getResources().getString(R.string.msg_empty_form_field));
            }

            if (month <= 0)
            {
                mEditTextMonth.setError(getResources().getString(R.string.msg_empty_form_field));
            }

            if (year <= 0)
            {
                mEditTextYear.setError(getResources().getString(R.string.msg_empty_form_field));
            }
        }

        return false;
    }

    private boolean validatePhoneNumber()
    {
        String areaCode = mEditTextAreaCode.getText().toString();
        String phoneNumber = mEditTextPhoneNumber.getText().toString();

        mEditTextAreaCode.setError(null);
        mEditTextPhoneNumber.setError(null);

        newUser.setPhoneNumber(null);

        if ((areaCode.length() == 2) && (phoneNumber.length() >= 8))
        {
            newUser.setPhoneNumber("+55"+areaCode+phoneNumber);

            return true;
        }
        else
        {
            if (areaCode.length() < 2)
            {
                mEditTextAreaCode.setError(getResources().getString(R.string.msg_empty_area_code_field));
            }

            if (phoneNumber.length() < 8)
            {
                mEditTextPhoneNumber.setError(getResources().getString(R.string.msg_empty_phone_number_field));
            }
        }

        return false;
    }

    private boolean checkFormFieldsOnFirstPage()
    {
        String firstName = mEditTextFirstName.getText().toString();
        String lastName = mEditTextLastName.getText().toString();

        mEditTextFirstName.setError(null);
        mEditTextLastName.setError(null);

        newUser.setFirstName(null);
        newUser.setLastName(null);

        if ((firstName.length() > 0) && (lastName.length() > 0) &&
                validatePhoneNumber() && validateDateOfBirth())
        {
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);

            return true;
        }
        else
        {
            if (firstName.length() == 0)
            {
                mEditTextFirstName.setError(getResources().getString(R.string.msg_empty_form_field));
            }

            if (lastName.length() == 0)
            {
                mEditTextLastName.setError(getResources().getString(R.string.msg_empty_form_field));
            }
        }

        return false;
    }

    private boolean checkFormFieldsOnMiddlePage()
    {
        return true;
    }

    boolean isEmailValid(String email)
    {
        mEditTextUserEmail.setError(null);

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            return true;
        }

        mEditTextUserEmail.setError(getResources().getString(R.string.msg_invalid_email_address));

        return false;
    }

    private boolean validatePassword(String password, String repeatedPassword)
    {
        newUser.setPassword(null);

        if (password.equals(repeatedPassword))
        {
            newUser.setPassword(password);

            return true;
        }

        mEditTextUserPassword.setText(null);
        mEditTextUserPassword.setError(getResources().getString(R.string.msg_passwords_dont_match));

        mEditTextUserRepeatedPassword.setText(null);
        mEditTextUserRepeatedPassword.setError(getResources().getString(R.string.msg_passwords_dont_match));

        return false;
    }

    private boolean checkFormFieldsOnLastPage()
    {
        String email = mEditTextUserEmail.getText().toString();
        String password = mEditTextUserPassword.getText().toString();
        String repeatedPassword = mEditTextUserRepeatedPassword.getText().toString();

        newUser.setEmail(null);

        if ((email.length() > 0) && (password.length() > 0) && (repeatedPassword.length() > 0) && (isEmailValid(email)) && (validatePassword(password, repeatedPassword)))
        {
            newUser.setEmail(email);

            return true;
        }
        else
        {
            if (email.length() == 0)
            {
                mEditTextUserEmail.setError(getResources().getString(R.string.msg_empty_form_field));
            }

            if (password.length() == 0)
            {
                mEditTextUserPassword.setError(getResources().getString(R.string.msg_empty_form_field));
            }

            if (repeatedPassword.length() == 0)
            {
                mEditTextUserRepeatedPassword.setError(getResources().getString(R.string.msg_empty_form_field));
            }
        }

        return false;
    }

    private void deletePicture()
    {
        newUser.setPictureUri(null);
        mUserPicture.setImageResource(R.drawable.photo_placeholder);
        mButtonContinue.setText(R.string.text_button_jump);
    }

    private void pickPicture()
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
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

            newUser.setPictureUri(Uri.fromFile(media));
        }

        return media;
    }

    private void setTakenPicture()
    {
        // Get the dimensions of the View
        int targetW = mUserPicture.getWidth();
        int targetH = mUserPicture.getHeight();
        Log.i(TAG, "targetW = " + targetW + ", targetH = " + targetH);
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(newUser.getPictureUri().getPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(newUser.getPictureUri().getPath(), bmOptions);
        mUserPicture.setImageBitmap(bitmap);
        mUserPicture.setBackgroundResource(R.drawable.imageview_profile_photo);//R.drawable.imageview_report_media
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != getActivity().RESULT_OK) return;

        if (requestCode == REQUEST_IMAGE_CAPTURE)
        {
            setTakenPicture();
            mButtonDeletePicture.setVisibility(View.VISIBLE);
        }
        else if (requestCode == RESULT_LOAD_IMG)
        {
            if (data != null)
            {
                newUser.setPictureUri(Uri.parse(MediaUtils.getRealPathFromURI(getActivity(), data.getData())));
                setTakenPicture();
                mButtonDeletePicture.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setPickedPicture(Uri selectedImage)
    {
        if (selectedImage != null)
        {
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            // Get the cursor
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgDecodableString = cursor.getString(columnIndex);
            cursor.close();

            Log.i(TAG, "");
            // Set the Image in ImageView after decoding the String
            if (mUserPicture != null) mUserPicture.setImageBitmap(BitmapFactory
                    .decodeFile(imgDecodableString));
        }
    }

}


    /*protected void onActiqvityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = (ImageView) findViewById(R.id.imgView);
                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }*/