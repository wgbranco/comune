package williamgbranco.comune.activity;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import williamgbranco.comune.R;
import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.util.Constants;


public class FindPlacesFragment extends Fragment
{
    private static final String TAG = "comune.FindPlacesFrag";
    private static final Integer MIN_RADIUS_IN_KM = 5;
    private static final Integer MAX_RADIUS_IN_KM = 30;


    private TextView mTextViewHeaderMsg; //textview_header_msg

    private LinearLayout mItemEducation; //item_education
    private ImageView mImageViewEducationIcon; //imageview_education_icon
    private TextView mTextViewItemEducation; //textview_item_education

    private LinearLayout mItemHealth; //item_health
    private ImageView mImageViewHealthIcon; //imageview_health_icon
    private TextView mTextViewItemHealth; //textview_item_health

    private LinearLayout mItemSecurity; //item_security
    private ImageView mImageViewSecurityIcon; ////imageview_security_icon
    private TextView mTextViewItemSecurity; //textview_item_security

    private ImageButton mButtonLowerGrades; //button_lower_grades
    private ImageButton mButtonHigherGrades; //button_higher_grades

    //private View mSurfaceViewDrawing; //surfaceview_drawing
    private SeekBar mSeekBarCircleRadius; //seekbar_circle_radius
    private TextView mTextViewRadius; //textview_radius

    private Button mButtonFindPlaces; //button_find_places

    private Toast mToast;
    private Integer mFetchRadius;
    private Integer mFetchInstType;
    private boolean mFetchHighGradedPlaces;


    public FindPlacesFragment()
    {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mFetchRadius = MIN_RADIUS_IN_KM;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_find_places, container, false);

        mTextViewHeaderMsg = (TextView) v.findViewById(R.id.textview_header_msg);
        mTextViewHeaderMsg.setText(getResources().getString(R.string.msg_activity_find_places) + ":");

        mImageViewEducationIcon = (ImageView) v.findViewById(R.id.imageview_education_icon);
        mImageViewHealthIcon = (ImageView) v.findViewById(R.id.imageview_health_icon);
        mImageViewSecurityIcon = (ImageView) v.findViewById(R.id.imageview_security_icon);

        mTextViewItemEducation = (TextView) v.findViewById(R.id.textview_item_education);
        mTextViewItemHealth = (TextView) v.findViewById(R.id.textview_item_health);
        mTextViewItemSecurity = (TextView) v.findViewById(R.id.textview_item_security);

        mItemEducation = (LinearLayout) v.findViewById(R.id.item_education);
        mItemHealth = (LinearLayout) v.findViewById(R.id.item_health);
        mItemSecurity = (LinearLayout) v.findViewById(R.id.item_security);

        mButtonLowerGrades = (ImageButton) v.findViewById(R.id.button_lower_grades);
        mButtonHigherGrades = (ImageButton) v.findViewById(R.id.button_higher_grades);

        //mSurfaceViewDrawing = (View) v.findViewById(R.id.surfaceview_drawing);

        mSeekBarCircleRadius = (SeekBar) v.findViewById(R.id.seekbar_circle_radius);

        mTextViewRadius = (TextView) v.findViewById(R.id.textview_radius);

        mButtonFindPlaces = (Button) v.findViewById(R.id.button_find_places);
        mButtonFindPlaces.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        //mButtonFindPlaces.getBackground().setColorFilter(getResources().getColor(R.color.complete_survey_button_color), PorterDuff.Mode.SRC_ATOP);

        enableItem(mItemHealth);
        setFetchHighGradedPlaces(true);
        wireUpWidgets();

        return v;
    }

    private void setFetchHighGradedPlaces(boolean value)
    {
        int msgId;
        Drawable icon;

        mFetchHighGradedPlaces = value;

        if (value)
        {
            icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_mark_red_disabled);
            mButtonLowerGrades.setImageDrawable(icon);

            icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_mark_green_enabled);
            mButtonHigherGrades.setImageDrawable(icon);

            msgId = R.string.button_higher_grades_descr;
        }
        else
        {
            icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_mark_green_disabled);
            mButtonHigherGrades.setImageDrawable(icon);

            icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_mark_red_enabled);
            mButtonLowerGrades.setImageDrawable(icon);

            msgId = R.string.button_lower_grades_descr;
        }

        if (mToast != null)
            mToast.cancel();

        mToast = Toast.makeText(getActivity(), msgId, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void wireUpWidgets()
    {
        mItemEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                enableItem(mItemEducation);
            }
        });

        mItemHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                enableItem(mItemHealth);
            }
        });

        mItemSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                enableItem(mItemSecurity);
            }
        });

        mButtonLowerGrades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFetchHighGradedPlaces(false);
            }
        });

        mButtonHigherGrades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFetchHighGradedPlaces(true);
            }
        });

        /*Canvas canvas = new Canvas();
        Paint paint = new Paint(Color.BLUE);
        paint.setAntiAlias(true);
        canvas.drawCircle(50, 50 , 100, paint);
        mSurfaceViewDrawing.draw(canvas);*/

        mTextViewRadius.setText(MIN_RADIUS_IN_KM + " KM");

        mSeekBarCircleRadius.setMax(MAX_RADIUS_IN_KM - MIN_RADIUS_IN_KM);
        mSeekBarCircleRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                mFetchRadius = MIN_RADIUS_IN_KM + progress;
                mTextViewRadius.setText(mFetchRadius + " KM");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                if (mToast != null)
                    mToast.cancel();

                String msg = getResources().getString(R.string.msg_selected_radius) + ": " + mFetchRadius + " km.";

                mToast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
                mToast.show();
            }
        });

        mButtonFindPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sendBroadcast();
            }
        });
    }

    private void disableItem(LinearLayout pItem)
    {
        Drawable icon;

        if (pItem == mItemEducation)
        {
            mTextViewItemEducation.setTextAppearance(getActivity(), R.style.find_frag_list_item_disabled);
            icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_education_light_gray);
            mImageViewEducationIcon.setImageDrawable(icon);

        }
        else if (pItem == mItemHealth)
        {
            mTextViewItemHealth.setTextAppearance(getActivity(), R.style.find_frag_list_item_disabled);
            icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_health_light_gray);
            mImageViewHealthIcon.setImageDrawable(icon);
        }
        else if (pItem == mItemSecurity)
        {
            mTextViewItemSecurity.setTextAppearance(getActivity(), R.style.find_frag_list_item_disabled);
            icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_security_light_gray);
            mImageViewSecurityIcon.setImageDrawable(icon);
        }

        pItem.setBackgroundColor(Color.TRANSPARENT);
    }

    private void enableItem(LinearLayout pItem)
    {
        Drawable icon;

        if (pItem == mItemEducation)
        {
            mFetchInstType = PublicInstitution.TIPO_EDUCACAO;

            disableItem(mItemSecurity);
            disableItem(mItemHealth);

            mTextViewItemEducation.setTextAppearance(getActivity(), R.style.find_frag_list_item_enabled);
            icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_education_gray);
            mImageViewEducationIcon.setImageDrawable(icon);
        }
        else if (pItem == mItemHealth)
        {
            mFetchInstType = PublicInstitution.TIPO_SAUDE;

            disableItem(mItemEducation);
            disableItem(mItemSecurity);

            mTextViewItemHealth.setTextAppearance(getActivity(), R.style.find_frag_list_item_enabled);
            icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_health_gray);
            mImageViewHealthIcon.setImageDrawable(icon);
        }
        else if (pItem == mItemSecurity)
        {
            mFetchInstType = PublicInstitution.TIPO_SEGURANCA;

            disableItem(mItemEducation);
            disableItem(mItemHealth);

            mTextViewItemSecurity.setTextAppearance(getActivity(), R.style.find_frag_list_item_enabled);
            icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_security_gray);
            mImageViewSecurityIcon.setImageDrawable(icon);
        }

        pItem.setBackgroundColor(getResources().getColor(R.color.find_item_light_gray));
    }

    private void sendBroadcast()
    {
        if ((mFetchInstType == null) || (mFetchRadius == null))
            return;

        if (isAdded())
        {
            Intent i = new Intent(BaseMapFragment.ACTION_MAP_MODE_FIND);
            i.putExtra(BaseMapFragment.EXTRA_FIND_TYPE, mFetchInstType);
            i.putExtra(BaseMapFragment.EXTRA_FIND_RADIUS, mFetchRadius);
            i.putExtra(BaseMapFragment.EXTRA_FIND_HIGH_GRADES, mFetchHighGradedPlaces);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Log.i(TAG, "sendBroadcast BaseMapFragment.ACTION_MAP_MODE_FIND");
            if (isAdded()) {
                getActivity().sendBroadcast(i, Constants.PERM_PRIVATE);
                getActivity().finish();
            }
        }
    }

}
