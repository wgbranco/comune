package williamgbranco.comune.activity;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import williamgbranco.comune.R;
import williamgbranco.comune.manager.SharedPrefsManager;


public class MapLayersFragment extends DialogFragment
{
    private CheckBox mCheckBoxEducation;
    private CheckBox mCheckBoxHealth;
    private CheckBox mCheckBoxSecurity;
    private CheckBox mCheckBoxHighGrade;
    private CheckBox mCheckBoxMediumGrade;
    private CheckBox mCheckBoxLowGrade;

    private static SharedPrefsManager sMapSharedPrefs;


    public MapLayersFragment()
    {}

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (sMapSharedPrefs == null)
            sMapSharedPrefs = SharedPrefsManager.get(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map_layers_dialog, container, false);

        /************************************************************************/

        mCheckBoxEducation = (CheckBox) v.findViewById(R.id.checkbox_education_layer);
        mCheckBoxEducation.setButtonDrawable(R.drawable.survey_check_box);
        mCheckBoxEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sMapSharedPrefs.changeEducationLayerVisibility(mCheckBoxEducation.isChecked());
            }
        });
        if (sMapSharedPrefs.getEducationLayerVisibility())
            mCheckBoxEducation.setChecked(true);

        /************************************************************************/

        mCheckBoxHealth = (CheckBox) v.findViewById(R.id.checkbox_health_layer);
        mCheckBoxHealth.setButtonDrawable(R.drawable.survey_check_box);
        mCheckBoxHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sMapSharedPrefs.changeHealthLayerVisibility(mCheckBoxHealth.isChecked());
            }
        });
        if (sMapSharedPrefs.getHealthLayerVisibility())
            mCheckBoxHealth.setChecked(true);

        /************************************************************************/

        mCheckBoxSecurity = (CheckBox) v.findViewById(R.id.checkbox_security_layer);
        mCheckBoxSecurity.setButtonDrawable(R.drawable.survey_check_box);
        mCheckBoxSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sMapSharedPrefs.changeSecurityLayerVisibility(mCheckBoxSecurity.isChecked());
            }
        });
        if (sMapSharedPrefs.getSecurityLayerVisibility())
            mCheckBoxSecurity.setChecked(true);

        /************************************************************************/

        mCheckBoxHighGrade = (CheckBox) v.findViewById(R.id.checkbox_high_grade_layer);
        mCheckBoxHighGrade.setButtonDrawable(R.drawable.survey_check_box);
        mCheckBoxHighGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sMapSharedPrefs.changeHighGradeLayerVisibility(mCheckBoxHighGrade.isChecked());
            }
        });
        if (sMapSharedPrefs.getHighGradeLayerVisibility())
            mCheckBoxHighGrade.setChecked(true);

        /************************************************************************/

        mCheckBoxMediumGrade = (CheckBox) v.findViewById(R.id.checkbox_medium_grade_layer);
        mCheckBoxMediumGrade.setButtonDrawable(R.drawable.survey_check_box);
        mCheckBoxMediumGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sMapSharedPrefs.changeMediumGradeLayerVisibility(mCheckBoxMediumGrade.isChecked());
            }
        });
        if (sMapSharedPrefs.getMediumGradeLayerVisibility())
            mCheckBoxMediumGrade.setChecked(true);

        /************************************************************************/

        mCheckBoxLowGrade = (CheckBox) v.findViewById(R.id.checkbox_low_grade_layer);
        mCheckBoxLowGrade.setButtonDrawable(R.drawable.survey_check_box);
        mCheckBoxLowGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sMapSharedPrefs.changeLowGradeLayerVisibility(mCheckBoxLowGrade.isChecked());
            }
        });
        if (sMapSharedPrefs.getLowGradeLayerVisibility())
            mCheckBoxLowGrade.setChecked(true);

        /************************************************************************/

        return v;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        Dialog d = getDialog();

        int width, height;

        if (d != null)
        {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            {
                width = ViewGroup.LayoutParams.MATCH_PARENT;
                height = ViewGroup.LayoutParams.WRAP_CONTENT;

                //d.getWindow().setLayout(width, height);
            }
            else //if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                width = ViewGroup.LayoutParams.WRAP_CONTENT;
                height = ViewGroup.LayoutParams.MATCH_PARENT;
            }

            d.getWindow().setLayout(width, height);
        }
    }

}
