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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import williamgbranco.comune.R;
import williamgbranco.comune.callback.GetReportListCallbacks;
import williamgbranco.comune.callback.LoadingCompleteCallback;
import williamgbranco.comune.manager.ReportManager;
import williamgbranco.comune.manager.UserManager;
import williamgbranco.comune.report.Report;
import williamgbranco.comune.util.DateUtils;


/**
 * A simple {@link ListFragment} subclass.
 */
public class ReportListForInstitutionFragment extends ListFragment
{
    public static final String TAG = "comune.ReportListFrag";

    public static final String EXTRA_INST_ID = "ReportListFrag.institution_id";

    private Integer mParamInstId;
    private UserManager mUserManager;
    private ReportManager mReportManager;
    private ArrayList<Report> mListOfReports;
    private LoadingCompleteCallback mCallback;
    //private HashMap<Button, Report> mButtonDateReportHashMap;
    //private HashMap<ImageView, Report> mButtonResponseReportHashMap;


    public static ReportListForInstitutionFragment newInstance(Integer pInstId)
    {
        Bundle args = new Bundle();
        args.putInt(EXTRA_INST_ID, pInstId);

        ReportListForInstitutionFragment fragment = new ReportListForInstitutionFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public ReportListForInstitutionFragment()
    {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mUserManager = UserManager.get(getActivity());
        /*mButtonDateReportHashMap = new HashMap<>();
        mButtonResponseReportHashMap = new HashMap<>();*/
        mReportManager = ReportManager.get(getActivity());
        mReportManager.resetFetchedReports();

        mParamInstId = getArguments().getInt(EXTRA_INST_ID, -1);

        mCallback.startLoadingData();

        mReportManager.fetchReportsSubmittedToInstitution(mUserManager.getCurrentUser(), mParamInstId, new GetReportListCallbacks() {
            @Override
            public void done(ArrayList<Report> returnedReports)
            {
                try {
                    if (returnedReports == null)
                    {
                        Log.i(TAG, "returnedReports NULL");
                        mCallback.errorLoadingData();
                    }
                    else if (returnedReports.size() == 0)
                    {
                        Log.i(TAG, "returnedReports size = 0");
                        mCallback.emptyDataSetLoaded();
                    }
                    else
                    {
                        Log.i(TAG, "returnedReports NOT NULL");
                        mCallback.finishLoadingData();
                        mListOfReports = returnedReports;
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
        if ((mListOfReports != null) && (mListOfReports.size() > 0))
        {
            Log.i(TAG, "configListAdapter()");

            ReportAdapter adapter = new ReportAdapter(mListOfReports);
            setListAdapter(adapter);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        Report report = ((ReportAdapter) getListAdapter()).getItem(position);
        Log.i(TAG, "relato clicado: " + report.getId());

        if (report.getResponseId() > 0)
        {
            Intent i = new Intent(getActivity(), DisplayReportResponseActivity.class);
            i.putExtra(DisplayReportResponseActivity.EXTRA_INST_ID, mParamInstId);
            i.putExtra(DisplayReportResponseActivity.EXTRA_REPORT_ID, report.getId());
            i.putExtra(DisplayReportResponseActivity.EXTRA_RESPONSE_ID, report.getResponseId());
            startActivity(i);
        }
        else
        {
            Intent i = new Intent(getActivity(), DisplayReportActivity.class);
            i.putExtra(DisplayReportActivity.EXTRA_INST_ID, mParamInstId);
            i.putExtra(DisplayReportActivity.EXTRA_REPORT_ID, report.getId());
            startActivity(i);
        }
    }


    private class ReportAdapter extends ArrayAdapter<Report>
    {
        public ReportAdapter(ArrayList<Report> pReports)
        {
            super(getActivity(), 0, pReports);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            //if (convertView == null) {
            convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_report_list_item, null);
            //}

            Report report = getItem(position);
            Log.i(TAG, report.toString());

            Button buttonDateOfReport = (Button) convertView.findViewById(R.id.button_report_made_on);
            buttonDateOfReport.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            String buttonText = DateUtils.getDiaDaSemanaAbrev(getActivity(), report.madeAt().get(Calendar.DAY_OF_WEEK)) + ": " +
                    report.madeAt().get(Calendar.DAY_OF_MONTH) + " " +
                    DateUtils.getMesAbrev(getActivity(), report.madeAt().get(Calendar.MONTH)) + " " +
                    report.madeAt().get(Calendar.YEAR);
            buttonDateOfReport.setText(buttonText);
            //mButtonDateReportHashMap.put(buttonDateOfReport, report);

            Drawable icon = null;
            if (report.getResponseId() > 0)
            {
                if (report.isResponseVisualized()) {
                    icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_report_replied_light_orange);
                } else {
                    icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_report_replied);
                }
                buttonDateOfReport.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
               /* buttonDateOfReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Report report = mButtonDateReportHashMap.get(v);

                        Intent i = new Intent(getActivity(), DisplayReportResponseActivity.class);
                        i.putExtra(DisplayReportResponseActivity.EXTRA_INST_ID, mParamInstId);
                        i.putExtra(DisplayReportResponseActivity.EXTRA_REPORT_ID, report.getId());
                        i.putExtra(DisplayReportResponseActivity.EXTRA_RESPONSE_ID, report.getResponseId());
                        startActivity(i);
                    }
                });*/
            }
            /*else
            {
                    buttonDateOfReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Report report = mButtonDateReportHashMap.get((Button) v);

                        Intent i = new Intent(getActivity(), DisplayReportActivity.class);
                        i.putExtra(DisplayReportActivity.EXTRA_INST_ID, mParamInstId);
                        i.putExtra(DisplayReportActivity.EXTRA_REPORT_ID, report.getId());
                        startActivity(i);
                    }
                });
            }*/

            buttonText = getResources().getString(R.string.msg_report_made_at) + " " +
                    String.format("%02d:%02d", report.madeAt().get(Calendar.HOUR_OF_DAY),
                            report.madeAt().get(Calendar.MINUTE));
            TextView texteViewReportMadeAt = (TextView) convertView.findViewById(R.id.texteview_report_made_at);
            texteViewReportMadeAt.setText(buttonText);

            ImageView ImageViewReportPicture = (ImageView) convertView.findViewById(R.id.imageview_report_picture);
            if (report.getPictureId() > 0) ImageViewReportPicture.setVisibility(View.VISIBLE);

            ImageView ImageViewReportFootage = (ImageView) convertView.findViewById(R.id.imageview_report_footage);
            if (report.getFootageId() > 0) ImageViewReportFootage.setVisibility(View.VISIBLE);

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
