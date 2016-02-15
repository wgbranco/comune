package williamgbranco.comune.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import williamgbranco.comune.R;
import williamgbranco.comune.database.loader.SurveysForInstitutionLoader;
import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.manager.PublicInstitutionManager;
import williamgbranco.comune.manager.SurveyManager;
import williamgbranco.comune.manager.UserManager;
import williamgbranco.comune.survey.Survey;
import williamgbranco.comune.util.Constants;
import williamgbranco.comune.util.DateUtils;


public class SurveysForInstListFragment extends ListFragment
{
    public static final String TAG = "SveysForInstListFrag";
    public static final String EXTRA_INST_ID = "SurveysForInstListFrag.institution_id";
    public static final int LOAD_SURVEYS_FOR_INSTITUTION = 0;

    private Integer mParamInstId;
    private SurveyManager mSurveyManager;
    private PublicInstitution mPublicInstitution;
    private ArrayList<Survey> mSurveysForInstitution;
    private LocationManager mLocationManager;


    public static SurveysForInstListFragment newInstance(Integer pInstitutionID)
    {
        SurveysForInstListFragment fragment = new SurveysForInstListFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_INST_ID, pInstitutionID);
        fragment.setArguments(args);

        return fragment;
    }

    public SurveysForInstListFragment()
    {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate(...)");

        mSurveyManager = SurveyManager.get(getActivity());
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        Bundle args = getArguments();
        if (args != null) mParamInstId = args.getInt(EXTRA_INST_ID, -1);

        /*Bundle args = getArguments();
        if (args != null)
        {
            mParamInstId = args.getInt(EXTRA_INST_ID, -1);

            if (mParamInstId != -1)
            {
                mPublicInstitution = PublicInstitutionManager.get(getActivity()).getPublicInstitutionById(mParamInstId);
                //SurveyManager.get(getActivity()).setCurrentInstitution(mPublicInstitution);
            }
            else
            {
                Log.i(TAG, "ERRO -- Institution ID é -1");
            }
        }*/

        mPublicInstitution = PublicInstitutionManager.get(getActivity()).getCurrentInstitution();

        getLoaderManager().initLoader(LOAD_SURVEYS_FOR_INSTITUTION, args, new SurveysForInstitutionLoaderCallbacks());
    }

    private boolean surveyHasBeenCompleted(Survey pSurvey)
    {
        if (mSurveyManager.getCompletedSurveyById(pSurvey.getPublicInstitutionId(), pSurvey.getId()) != null) {
            Log.i(TAG, "survey recentemente concluída: " + pSurvey.getId());
            return true;
        }

        return false;
    }

    private boolean surveyHasBeenStarted(Survey pSurvey)
    {
        for (Survey survey: mSurveysForInstitution)
        {
            Log.i(TAG, "surveyHasBeenStarted(Survey " + pSurvey.getId() + ")");
            Log.i(TAG, "new - inst_id: " + pSurvey.getPublicInstitutionId() + " , survey_id: " + pSurvey.getId());
            Log.i(TAG, "stored - inst_id: " + survey.getPublicInstitutionId() + " , survey_id: " + survey.getId());

            if (survey.getPublicInstitutionId().equals(pSurvey.getPublicInstitutionId()) &&
                survey.getId().equals(pSurvey.getId()))
            {
                if (surveyHasBeenCompleted(survey))
                {
                    mSurveysForInstitution.remove(survey);
                    return true;
                }

                Log.i(TAG, "stored - inst_id: " + survey.getPublicInstitutionId() + " , survey_id: " + survey.getId() + ", complete: " + survey.isComplete());

                if (survey.isComplete())
                {
                    mSurveysForInstitution.remove(survey);
                    Log.i(TAG, "survey já concluída: " + survey.getId());
                    return true;
                }
                else if (survey.hasExpired())
                {
                    deleteExpiredSurveyFromDB(pSurvey);
                    //survey.setSource(Survey.SURVEY_FROM_DB);
                    Log.i(TAG, "survey já expirada: " + survey.getId());
                    mSurveysForInstitution.remove(survey);
                    return false;
                }

                return true;
            }
        }
        return false;
    }

    private void deleteExpiredSurveyFromDB(Survey pSurvey)
    {
        new DeleteExpiredSurveyFromDB(pSurvey).execute();
    }

    private void syncServerAndDatabaseData()
    {
        Log.i(TAG, "syncServerAndDatabaseData()");

        ArrayList<Survey> availableSurveys = mPublicInstitution.getAvailableSurveysList();

        if (availableSurveys != null)
        {
            for (Survey survey : availableSurveys)
            {
                if (!surveyHasBeenStarted(survey))
                {
                    if (!surveyHasBeenCompleted(survey))
                    {
                        Log.d(TAG, "Pesquisa adicionada à lista de SurveysForInstitution: " + survey.getId());

                        mSurveysForInstitution.add(mSurveysForInstitution.size(), survey);
                    }

                } else {
                    Log.d(TAG, "Pesquisa já presente na lista de SurveysForInstitution: " + survey.getId());
                }
            }
        }
    }

    private void configListAdapter()
    {
        Log.i(TAG, "configListAdapter()");

        if ((mSurveysForInstitution != null) && (mSurveysForInstitution.size() > 0))
        {
            Log.i(TAG, "mSurveysForInstitution.size(): " + mSurveysForInstitution.size());

            SurveyAdapter adapter = new SurveyAdapter(mSurveysForInstitution);
            setListAdapter(adapter);
        }
        else
        {
            Log.i(TAG, "no surveys to display in the list.");
            getActivity().finish();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        Survey survey = ((SurveyAdapter) getListAdapter()).getItem(position);

        if (survey != null)
        {
            Log.i(TAG, "pesquisa clicada: " + survey.getId());

            Intent i = new Intent(getActivity(), SurveyActivity.class);
            i.putExtra(SurveyActivity.EXTRA_INST_ID, mParamInstId);
            i.putExtra(SurveyActivity.EXTRA_SURVEY_ID, survey.getId());
            i.putExtra(SurveyActivity.EXTRA_SURVEY_SOURCE, survey.getSource());
            i.putExtra(HeaderContentActivity.EXTRA_CONFIG_FRAGS, false);
            startActivity(i);
        }

        mSurveysForInstitution.clear();
        mSurveysForInstitution = null;
    }


    private class SurveysForInstitutionLoaderCallbacks implements android.support.v4.app.LoaderManager.LoaderCallbacks<ArrayList<Survey>>
    {
        @Override
        public android.support.v4.content.Loader<ArrayList<Survey>> onCreateLoader(int id, Bundle args)
        {
            Log.d(TAG, "Loading surveys for institution id = " + args.getInt(EXTRA_INST_ID));

            return new SurveysForInstitutionLoader(getActivity(), args.getInt(EXTRA_INST_ID));
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Survey>> loader, ArrayList<Survey> pSurveysForInstitution)
        {
            mSurveysForInstitution = pSurveysForInstitution;

            if (mSurveysForInstitution != null)
            {
                Log.d(TAG, "SurveysForInstitutionLoaderCallbacks - surveys for institution already on database: " + mSurveysForInstitution.size());

                if (mPublicInstitution != null) {
                    Location userLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (userLocation.distanceTo(mPublicInstitution.getLocation()) <= Constants.MAXIMUM_DISTANCE_FROM_BUILDING) {
                        syncServerAndDatabaseData();
                    }
                }
            }

            configListAdapter();
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Survey>> loader)
        {}
    }

    private class DeleteExpiredSurveyFromDB extends AsyncTask<Void, Void, Void>
    {
        Survey mSurvey;

        public DeleteExpiredSurveyFromDB(Survey pSurvey)
        {
            mSurvey = pSurvey;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            int userId = UserManager.get(getActivity()).getCurrentUser().getUserId();

            mSurveyManager.deleteSurveyForUser(userId, mSurvey.getPublicInstitutionId(), mSurvey.getId());

            return null;
        }
    }

    private class SurveyAdapter extends ArrayAdapter<Survey>
    {
        public SurveyAdapter(ArrayList<Survey> surveys)
        {
            super(getActivity(), 0, surveys);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_survey_list_item, null);
            }

            Survey survey = getItem(position);

            Log.i(TAG, "POSITION: " + position + ", SURVEY ID: " + survey.getId());

            Button buttonStartSurvey = (Button) convertView.findViewById(R.id.button_start_survey);
            buttonStartSurvey.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

            TextView textViewSurveyAvailableSince = (TextView) convertView.findViewById(R.id.textview_survey_available_since);

            textViewSurveyAvailableSince.setVisibility(View.INVISIBLE);

            String availabilityText = getResources().getString(R.string.msg_survey_available_since) + " ";
            GregorianCalendar availableSince = survey.getAvailableSince();
            availabilityText = availabilityText + availableSince.get(Calendar.DAY_OF_MONTH) + " " +
                    DateUtils.getMes(getActivity(), availableSince.get(Calendar.MONTH)) + " " +
                    availableSince.get(Calendar.YEAR);

            TextView textViewSurveyRemaningTime = (TextView) convertView.findViewById(R.id.textview_survey_remaining_time);
            if (survey.getSource().equals(Survey.SURVEY_FROM_DB))
            {
                availabilityText = getResources().getString(R.string.msg_survey_started_on) + " ";
                availableSince = survey.getStartTime();
                availabilityText = availabilityText + availableSince.get(Calendar.DAY_OF_MONTH) + " " +
                        DateUtils.getMes(getActivity(), availableSince.get(Calendar.MONTH)) + " " +
                        availableSince.get(Calendar.YEAR);

                buttonStartSurvey.setText(getResources().getString(R.string.msg_resume_survey));

                int remainingTime = survey.getRemainingTimeInHoursForSurveyCompletion();

                if (remainingTime <= Constants.SURVEY_ALERT_FEW_HOURS_REMAINING)
                {
                    buttonStartSurvey.setText(getResources().getString(R.string.msg_finish_survey));

                    textViewSurveyRemaningTime.setTextColor(getResources().getColor(R.color.survey_expiring_soon));
                }

                textViewSurveyRemaningTime.setText(remainingTime + getResources().getString(R.string.msg_survey_remaining_time_2));
            }
            else if (survey.getSource().equals(Survey.NEW_SURVEY_AVAILABLE))
            {
                buttonStartSurvey.setText(getResources().getString(R.string.msg_start_survey));

                textViewSurveyRemaningTime.setTextColor(getResources().getColor(R.color.new_survey_available_text_color));
                textViewSurveyRemaningTime.setText(getResources().getString(R.string.new_survey_available).toUpperCase());
            }

            textViewSurveyAvailableSince.setText(availabilityText);
            textViewSurveyAvailableSince.setVisibility(View.VISIBLE);

            TextView mTextViewSurveyId = (TextView) convertView.findViewById(R.id.textview_survey_id);
            mTextViewSurveyId.setText("#"+survey.getId());

            TextView textViewSurveyDescription = (TextView) convertView.findViewById(R.id.textview_survey_description);
            textViewSurveyDescription.setText(survey.getDescription());

            return convertView;
        }
    }

}
