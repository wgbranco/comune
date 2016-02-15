package williamgbranco.comune.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;

import williamgbranco.comune.R;

public class ReportActivity extends SingleFragmentActivity
{
    @Override
    protected ReportFragment createFragment()
    {
        Integer instId = getIntent().getIntExtra(ReportFragment.EXTRA_INST_ID, -1);

        return ReportFragment.newInstance(instId);
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setMessage(R.string.msg_exit_report_activity);

        dialog.setPositiveButton(R.string.text_button_continue, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                backButtonPressed();
            }
        });

        dialog.setNegativeButton(R.string.text_button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });

        dialog.create().show();
    }

    private void backButtonPressed()
    {
        super.onBackPressed();
    }
}
