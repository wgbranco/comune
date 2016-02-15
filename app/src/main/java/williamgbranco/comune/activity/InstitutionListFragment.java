package williamgbranco.comune.activity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import williamgbranco.comune.R;
import williamgbranco.comune.callback.GetInstitutionListCallbacks;
import williamgbranco.comune.callback.LoadingCompleteCallback;
import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.manager.PublicInstitutionManager;
import williamgbranco.comune.manager.ReportManager;
import williamgbranco.comune.manager.UserManager;


/**
 * A simple {@link ListFragment} subclass.
 */
public class InstitutionListFragment extends ListFragment
{
    public static final String TAG = "comune.InstListFrag";

    public static final String EXTRA_INST_ID = "InstitutionsListFrag.institution_id";

    private UserManager mUserManager;
    private ReportManager mReportManager;
    private PublicInstitutionManager mInstitutionManager;
    private ArrayList<PublicInstitution> mListOfInstitutions;
    private LoadingCompleteCallback mCallback;


    public InstitutionListFragment()
    {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mUserManager = UserManager.get(getActivity());

        mInstitutionManager = PublicInstitutionManager.get(getActivity());

        mReportManager = ReportManager.get(getActivity());

        mCallback.startLoadingData();

        mReportManager.fetchInstitutionsWithReportsSubmittedByUser(mUserManager.getCurrentUser(), new GetInstitutionListCallbacks() {
            @Override
            public void done(ArrayList<PublicInstitution> returnedInstitutions)
            {
                try {
                    if (returnedInstitutions == null)
                    {
                        Log.i(TAG, "returnedInstitutions NULL");
                        mCallback.errorLoadingData();
                    }
                    else if (returnedInstitutions.size() == 0)
                    {
                        Log.i(TAG, "returnedInstitutions.size() == 0");
                        mCallback.emptyDataSetLoaded();
                    }
                    else
                    {
                        Log.i(TAG, "returnedInstitutions NOT NULL");

                        mCallback.finishLoadingData();

                        mListOfInstitutions = returnedInstitutions;

                        configListAdapter();
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
        });
    }

    private void configListAdapter()
    {
        if ((mListOfInstitutions != null) && (mListOfInstitutions.size() > 0))
        {
            Log.i(TAG, "configListAdapter()");

            InstitutionAdapter adapter = new InstitutionAdapter(mListOfInstitutions);
            setListAdapter(adapter);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        PublicInstitution institution = ((InstitutionAdapter) getListAdapter()).getItem(position);
        Log.i(TAG, "instituição clicada: " + institution.getId());

        Intent i = new Intent(getActivity(), ReportListForInstitutionActivity.class);
        i.putExtra(ReportListForInstitutionActivity.EXTRA_INST_ID, institution.getId());
        startActivity(i);
    }

    private class InstitutionAdapter extends ArrayAdapter<PublicInstitution>
    {
        public InstitutionAdapter(ArrayList<PublicInstitution> pInstitutions)
        {
            super(getActivity(), 0, pInstitutions);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_user_institution_reports_list_item, null);

            PublicInstitution institution = getItem(position);
            Log.i(TAG, institution.toString());

            Button buttonInstitutionName = (Button) convertView.findViewById(R.id.button_institution_name);
            buttonInstitutionName.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

            String abbrev = institution.getNomeAbreviado();
            String nome = institution.getNome();
            if ((abbrev != null) && (abbrev.length() > 0))
            {
                buttonInstitutionName.setText(abbrev);
            } else {
                buttonInstitutionName.setText(nome);
            }

            Drawable icon = ContextCompat.getDrawable(getActivity(), institution.getPlaceBlackIconId());
            buttonInstitutionName.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

            TextView textViewInstitutionCompleteName = (TextView) convertView.findViewById(R.id.textview_institution_complete_name);
            textViewInstitutionCompleteName.setText(nome);

            TextView textViewNumberReports = (TextView) convertView.findViewById(R.id.textview_number_submitted_reports);

            int nReports = mInstitutionManager.getNumberReportsForInstitution(institution);
            String msg = null;

            if (nReports == 1)
            {
                msg = nReports + " " + getResources().getString(R.string.msg_report_submitted);
            }
            else {
                msg = nReports + " " + getResources().getString(R.string.msg_reports_submitted);
            }
            textViewNumberReports.setText(msg);

            return convertView;
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mCallback = (LoadingCompleteCallback) activity;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mCallback = null;
    }
}
