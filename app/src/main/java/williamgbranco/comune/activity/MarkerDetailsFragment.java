package williamgbranco.comune.activity;


import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import williamgbranco.comune.R;
import williamgbranco.comune.callback.GetPublicInstitutionCallbacks;
import williamgbranco.comune.database.loader.SurveysForInstitutionLoader;
import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.manager.PublicInstitutionManager;
import williamgbranco.comune.manager.SurveyManager;
import williamgbranco.comune.server.ServerRequests;
import williamgbranco.comune.survey.Survey;
import williamgbranco.comune.util.Constants;


/**
 * A simple {@link Fragment} subclass.
 */
public class MarkerDetailsFragment extends DialogFragment
{
    private static final String TAG = "MarkerDetailsFrag";

    private static  final String ARG_INST_SOURCE = "MarkerDetailsFragment.INST_SOURCE";
    private static  final String ARG_INST_ID = "MarkerDetailsFragment.INST_ID";

    private int mIconId;

    private ImageView mIconeTipoImageView;
    private ImageView mIconeNotaMediaImageView;
    private TextView mStatusLocalTextView;
    private TextView mNomeLocalTextView;
    private TextView mNomeCompletoLocalTextView;
    private TextView mHorarioFuncionamentoTextView;
    private TextView mDiasFuncionamentoTextView;
    private TextView mNotaMediaTextView;
    private Button mAbrirQuestionarioButton;
    private LinearLayout mContainerProgressBar; //container_progress_bar
    private ProgressBar mProgressBar; //marker_progress_bar
    private Button mEnviarSugestaoButton;
    private TextView mTextViewMsgUserTooFar;

    private Context mAppContext;
    private String mMapMode;
    private ArrayList<Survey> mSurveysForInstitution;
    private SurveyManager mSurveyManager;
    private PublicInstitutionManager mInstitutionManager;
    private PublicInstitution mPublicInstitution;

    private LocationManager mLocationManager;

    private static MarkerDetailsFragment fragment;

    public static MarkerDetailsFragment newInstance(Integer pPublicServiceId, String pMapMode)
    {
        Bundle args = new Bundle();
        args.putInt(ARG_INST_ID, pPublicServiceId);
        args.putString(ARG_INST_SOURCE, pMapMode);

        if (fragment == null)
            fragment = new MarkerDetailsFragment();

        fragment.setArguments(args);

        return fragment;
    }

    public MarkerDetailsFragment()
    {}

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mAppContext = activity.getApplicationContext();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAppContext = null;
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Integer instId = getArguments().getInt(ARG_INST_ID);
        mMapMode = getArguments().getString(ARG_INST_SOURCE);

        mSurveyManager = SurveyManager.get(mAppContext);
        mInstitutionManager = PublicInstitutionManager.get(mAppContext);
        mPublicInstitution = getCurrentInstitution(instId, mMapMode);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    private PublicInstitution getCurrentInstitution(Integer instId, String mapMode)
    {
        if (MapFragment.MAP_MODE_NORMAL.equals(mapMode)) {
            return mInstitutionManager.getPublicInstitutionById(instId);
        }
        else if (MapFragment.MAP_MODE_FIND.equals(mapMode))
        {
            return mInstitutionManager.getFoundPublicInstitutionById(instId);
        }

        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_marker_details, container, false);

        mIconeTipoImageView = (ImageView) v.findViewById(R.id.imageview_institution_type_icon);
        mNomeLocalTextView = (TextView) v.findViewById(R.id.textview_institution_name);
        mNomeCompletoLocalTextView = (TextView) v.findViewById(R.id.textview_institution_complete_name);
        mIconeNotaMediaImageView = (ImageView) v.findViewById(R.id.imageview_icone_nota_media);
        mStatusLocalTextView = (TextView) v.findViewById(R.id.textview_status_local);
        mHorarioFuncionamentoTextView = (TextView) v.findViewById(R.id.textview_horario_funcionamento);
        mDiasFuncionamentoTextView = (TextView) v.findViewById(R.id.textview_dias_funcionamento);
        mNotaMediaTextView = (TextView) v.findViewById(R.id.textview_nota_media);
        mAbrirQuestionarioButton = (Button) v.findViewById(R.id.button_opiniao);
        mContainerProgressBar = (LinearLayout) v.findViewById(R.id.container_marker_progress_bar);
        mProgressBar = (ProgressBar) v.findViewById(R.id.marker_progress_bar);
        if (mProgressBar != null) {
            mProgressBar.setIndeterminate(true);
            mProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.logo_orange), android.graphics.PorterDuff.Mode.SRC_ATOP);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
        mAbrirQuestionarioButton.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        mEnviarSugestaoButton = (Button) v.findViewById(R.id.button_sugestao);
        mEnviarSugestaoButton.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        mTextViewMsgUserTooFar = (TextView) v.findViewById(R.id.textview_msg_user_too_far);

        updateUI();

        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    public void updateUI()
    {
        Integer iconId = mPublicInstitution.getPlaceDarkGrayIconId();
        mIconeTipoImageView.setImageResource(iconId);

        String abbrev = mPublicInstitution.getNomeAbreviado();
        String nome = mPublicInstitution.getNome();

        if ((abbrev != null) && (!abbrev.toLowerCase().equals("null")) && (abbrev.length() > 0))
        {
            mNomeLocalTextView.setText(abbrev);
        } else {
            mNomeLocalTextView.setText(nome);
        }

        mNomeCompletoLocalTextView.setText(nome);

        Integer status = mPublicInstitution.getStatus();
        if (status.equals(PublicInstitution.STATUS_ATIVO))
        {
            if (mPublicInstitution.isOpen()) {
                mStatusLocalTextView.setText(R.string.status_local_aberto);
            } else {
                mStatusLocalTextView.setText(R.string.status_local_fechado);
            }
        }

        String openingTimeOfDay = mPublicInstitution.getOpeningTimeOfDay();
        String abertura = null;
        if (openingTimeOfDay != null) abertura = openingTimeOfDay.substring(0, 5);

        String closingTimeOfDay = mPublicInstitution.getClosingTimeOfDay();
        String fechamento = null;
        if (closingTimeOfDay != null) fechamento = closingTimeOfDay.substring(0, 5);

        if ((abertura != null) && (fechamento != null)) {
            if (abertura.equals(fechamento)) {
                mHorarioFuncionamentoTextView.setText(R.string.msg_open_24h);
            } else {
                mHorarioFuncionamentoTextView.setText(abertura + " — " + fechamento);
            }
        }
        else
        {
            mHorarioFuncionamentoTextView.setText(R.string.msg_closed_today);
        }

        mDiasFuncionamentoTextView.setText(mPublicInstitution.getWorkingDaysAsText().toUpperCase());

        double nota_media = mPublicInstitution.getNotaMedia();
        mNotaMediaTextView.setText("—");
        if (nota_media > 0.0) mNotaMediaTextView.setText(String.format("%.2f", nota_media));

        mIconeNotaMediaImageView.setImageResource(mPublicInstitution.getRatingIconId());

        configureButtons();
    }

    private void configureButtons()
    {
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null)
        {
            if (location.distanceTo(mPublicInstitution.getLocation()) <= Constants.MAXIMUM_DISTANCE_FROM_BUILDING)
            {
                loadNewSurveysForInstitution();

                mEnviarSugestaoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            mPublicInstitution = getCurrentInstitution(mPublicInstitution.getId(), mMapMode);
                            if (mPublicInstitution != null) {
                                Intent i = new Intent(getActivity(), ReportActivity.class);
                                i.putExtra(ReportFragment.EXTRA_INST_ID, mPublicInstitution.getId());
                                startActivity(i);
                            }
                        }
                        catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                        finally {
                            dismiss();
                        }
                    }
                });

                mEnviarSugestaoButton.setVisibility(View.VISIBLE);
            } else {
                mTextViewMsgUserTooFar.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            Toast.makeText(getActivity(), R.string.msg_location_not_found, Toast.LENGTH_LONG).show();
        }
    }

    private void loadNewSurveysForInstitution()
    {
        if (mPublicInstitution.getAvailableSurveysList() == null)
        {
            mContainerProgressBar.setVisibility(View.VISIBLE);

            new ServerRequests(mAppContext).fetchNewSurveysAvailableForInstitution(mPublicInstitution, new GetPublicInstitutionCallbacks() {
                @Override
                public void done(PublicInstitution returnedInstitution) {
                    try {
                        Log.d(TAG, "fetchNewSurveysAvailableForInstitution.GetPublicInstitutionCallbacks: " + returnedInstitution);

                        mContainerProgressBar.setVisibility(View.INVISIBLE);

                        loadStoredSurveysForInstitution();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
            });
        } else {
            loadStoredSurveysForInstitution();
        }
    }

    //private void initializeSurveyForInstitutionFetchrService()
    private void loadStoredSurveysForInstitution()
    {
        mContainerProgressBar.setVisibility(View.INVISIBLE);

        Bundle args = new Bundle();
        args.putInt(SurveysForInstListFragment.EXTRA_INST_ID, mPublicInstitution.getId());
        getLoaderManager().initLoader(SurveysForInstListFragment.LOAD_SURVEYS_FOR_INSTITUTION, args, new SurveysForInstitutionLoaderCallbacks());
    }

    private void configOpenSurveyListButton()
    {
        if ((mSurveysForInstitution != null) && (mSurveysForInstitution.size() > 0))
        {
            mAbrirQuestionarioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        mPublicInstitution = getCurrentInstitution(mPublicInstitution.getId(), mMapMode);
                        if (mPublicInstitution != null)
                        {
                            mInstitutionManager.setCurrentInstitution(mPublicInstitution);

                            Intent i = new Intent(getActivity(), SurveysForInstListActivity.class);
                            i.putExtra(SurveysForInstListActivity.EXTRA_INST_ID, mPublicInstitution.getId());
                            startActivity(i);
                        }
                    }
                    catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    finally {
                        dismiss();
                    }
                }
            });

            mAbrirQuestionarioButton.setVisibility(View.VISIBLE);
        }
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

                if (survey.isComplete() || survey.hasExpired())
                {
                    mSurveysForInstitution.remove(survey);
                    Log.i(TAG, "survey já concluída ou expirada: " + survey.getId());
                    return false;
                }

                return true;
            }
        }
        return false;
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
                }
                else
                {
                    Log.d(TAG, "Pesquisa já presente na lista de SurveysForInstitution: " + survey.getId());
                }
            }
        }
    }

    private class SurveysForInstitutionLoaderCallbacks implements android.support.v4.app.LoaderManager.LoaderCallbacks<ArrayList<Survey>>
    {
        @Override
        public android.support.v4.content.Loader<ArrayList<Survey>> onCreateLoader(int id, Bundle args)
        {
            Log.d(TAG, "Loading surveys for institution id = " + args.getInt(SurveysForInstListFragment.EXTRA_INST_ID));

            return new SurveysForInstitutionLoader(getActivity(), args.getInt(SurveysForInstListFragment.EXTRA_INST_ID));
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Survey>> loader, ArrayList<Survey> pSurveysForInstitution)
        {
            mSurveysForInstitution = pSurveysForInstitution;

            Log.d(TAG, "SurveysForInstitutionLoaderCallbacks - surveys for institution already on database: " + mSurveysForInstitution.size());

            syncServerAndDatabaseData();

            configOpenSurveyListButton();
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Survey>> loader)
        {}
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

                d.getWindow().setLayout(width, height);
            }
        }
    }

}
