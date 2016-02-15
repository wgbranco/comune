package williamgbranco.comune.activity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import williamgbranco.comune.R;
import williamgbranco.comune.callback.LoadingCompleteCallback;
import williamgbranco.comune.database.loader.InstitutionsWithIncompleteSurveysLoader;
import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.manager.PublicInstitutionManager;
import williamgbranco.comune.manager.SurveyManager;


public class StoredSurveysListFragment extends ListFragment
{
    private static final String TAG = "StoredSurveysListFrag";

    private static final int LOAD_STORED_SURVEYS = 0;

    private boolean loading;
    private SurveyManager mSurveyManager;
    private PublicInstitution mPublicInstitution;
    private HashMap<PublicInstitution, Integer> mInstitutionsWithIncompleteSurveys;

    private LoadingCompleteCallback mCallback;


    public StoredSurveysListFragment()
    {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSurveyManager = SurveyManager.get(getActivity());

        mCallback.startLoadingData();

        getLoaderManager().initLoader(LOAD_STORED_SURVEYS, null, new InstitutionsWithIncompleteSurveysLoaderCallbacks());
    }

    private void configListAdapter()
    {
        if ((mInstitutionsWithIncompleteSurveys != null) && (mInstitutionsWithIncompleteSurveys.size() > 0))
        {
            ArrayList<PublicInstitution> institutions = new ArrayList<>(mInstitutionsWithIncompleteSurveys.keySet());
            PublicInstitutionAdapter adapter = new PublicInstitutionAdapter(institutions);
            setListAdapter(adapter);
        }
        /*else
        {
            Log.i(TAG, "no surveys to display in the list.");
            getActivity().finish();
        }*/
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        PublicInstitution institution = ((PublicInstitutionAdapter) getListAdapter()).getItem(position);
        Log.i(TAG, "instituição clicada: " + institution.getId());

        PublicInstitutionManager.get(getActivity()).setCurrentInstitution(institution);

        Intent i = new Intent(getActivity(), SurveysForInstListActivity.class);
        i.putExtra(SurveysForInstListActivity.EXTRA_INST_ID, institution.getId());
        startActivity(i);
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


    private class InstitutionsWithIncompleteSurveysLoaderCallbacks implements android.support.v4.app.LoaderManager.LoaderCallbacks<HashMap<PublicInstitution, Integer>>
    {
        @Override
        public android.support.v4.content.Loader<HashMap<PublicInstitution, Integer>> onCreateLoader(int id, Bundle args)
        {
            return new InstitutionsWithIncompleteSurveysLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<HashMap<PublicInstitution, Integer>> loader, HashMap<PublicInstitution, Integer> returnedData)
        {
            if (returnedData == null)
            {
                mCallback.errorLoadingData();
            }
            else if (returnedData.size() == 0)
            {
                mCallback.emptyDataSetLoaded();
            }
            else
            {
                mCallback.finishLoadingData();

                mInstitutionsWithIncompleteSurveys = returnedData;

                Log.d(TAG, "SurveysForUserLoaderCallbacks - surveys for user: " + mInstitutionsWithIncompleteSurveys.size());

                configListAdapter();
            }
        }

        @Override
        public void onLoaderReset(Loader<HashMap<PublicInstitution, Integer>> loader)
        {}
    }


    private class PublicInstitutionAdapter extends ArrayAdapter<PublicInstitution>
    {
        public PublicInstitutionAdapter(ArrayList<PublicInstitution> pInstitutions)
        {
            super(getActivity(), 0, pInstitutions);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_inst_survey_list_item, null);

            PublicInstitution institution = getItem(position);

            Button buttonInstName = (Button) convertView.findViewById(R.id.button_institution_name);
            buttonInstName.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

            String abbrev = institution.getNomeAbreviado();
            String nome = institution.getNome();
            if ((abbrev != null) && (abbrev.length() > 0))
            {
                buttonInstName.setText(abbrev);
            } else {
                buttonInstName.setText(nome);
            }
            Drawable icon = ContextCompat.getDrawable(getActivity(), institution.getPlaceBlackIconId());
            buttonInstName.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

            TextView textViewInstitutionCompleteName = (TextView) convertView.findViewById(R.id.textview_institution_complete_name);
            textViewInstitutionCompleteName.setText(nome);

            int nSurveys = mInstitutionsWithIncompleteSurveys.get(institution);

            TextView textViewNoIncompleteSurveys = (TextView) convertView.findViewById(R.id.textview_number_incomplete_surveys);
            String msgNumberSurveys;
            if (nSurveys < 2)
            {
                msgNumberSurveys = nSurveys + " " +
                        getResources().getString(R.string.msg_incomplete_survey);
            }
            else
            {
                msgNumberSurveys = nSurveys + " " +
                        getResources().getString(R.string.msg_incomplete_surveys);
            }
            textViewNoIncompleteSurveys.setText(msgNumberSurveys);

            if (institution.hasSurveyExpiringSoon())
            {
                ImageView imageViewHasSurveyExpiring = (ImageView) convertView.findViewById(R.id.imageview_report_problem);
                imageViewHasSurveyExpiring.setVisibility(View.VISIBLE);
            }

            return convertView;
        }
    }

}
