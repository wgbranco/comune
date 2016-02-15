package williamgbranco.comune.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import williamgbranco.comune.R;
import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.manager.PublicInstitutionManager;


public class InstitutionHeaderFragment extends Fragment
{
    public static final String TAG = "comune.InstHeaderFrag";
    public static final String EXTRA_INST_ID = "InstHeaderFragment.institution_id";

    private Integer mParamInstId;
    private PublicInstitution mPublicInstitution;
    private ImageView mImageViewInstitutionTypeIcon; //imageview_institution_type_icon
    private TextView mTextViewInstitutionName; //textview_institution_name
    private TextView mTextViewInstitutionCompleteName; //textview_institution_complete_name
    private LinearLayout mContainerInstitutionCompleteName; //container_institution_complete_name


    public static InstitutionHeaderFragment newInstance(Integer pInstitutionID)
    {
        Bundle args = new Bundle();
        args.putInt(EXTRA_INST_ID, pInstitutionID);

        InstitutionHeaderFragment fragment = new InstitutionHeaderFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public InstitutionHeaderFragment()
    {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /*Bundle args = getArguments();
        if (args != null)
        {
            mParamInstId = args.getInt(EXTRA_INST_ID, -1);

            if (mParamInstId != -1)
            {
                mPublicInstitution = PublicInstitutionManager.get(getActivity()).getPublicInstitutionById(mParamInstId);
            }
            else
            {
                Log.i(TAG, "ERRO -- Institution ID é -1");
            }
        }*/

        mPublicInstitution = PublicInstitutionManager.get(getActivity()).getCurrentInstitution();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_institution_header, container, false);

        mImageViewInstitutionTypeIcon = (ImageView) v.findViewById(R.id.imageview_institution_type_icon);
        mImageViewInstitutionTypeIcon.setImageResource(mPublicInstitution.getPlaceDarkGrayIconId());
        Log.d(TAG, String.valueOf(mPublicInstitution.getPlaceBlackIconId()) + " " + mPublicInstitution.getNome());

        mTextViewInstitutionName = (TextView) v.findViewById(R.id.textview_institution_name);
        mTextViewInstitutionCompleteName = (TextView) v.findViewById(R.id.textview_institution_complete_name);
        mContainerInstitutionCompleteName = (LinearLayout) v.findViewById(R.id.container_institution_complete_name);
        mContainerInstitutionCompleteName.setVisibility(View.GONE);

        String abbrev = mPublicInstitution.getNomeAbreviado();
        String nome = mPublicInstitution.getNome();

        if ((abbrev != null) && (abbrev.length() > 0))
        {
            mTextViewInstitutionName.setText(abbrev);
        }
        else {
            mTextViewInstitutionName.setText(nome);
        }

        mTextViewInstitutionCompleteName.setText(nome);

        return v;
    }
}
