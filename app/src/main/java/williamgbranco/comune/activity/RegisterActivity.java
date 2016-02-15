package williamgbranco.comune.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;

import williamgbranco.comune.R;

public class RegisterActivity extends SingleFragmentActivity
{
    private RegisterFragment mRegisterFragment;


    @Override
    protected Fragment createFragment()
    {
        mRegisterFragment = new RegisterFragment();
        return mRegisterFragment;
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setMessage(R.string.msg_exit_register_activity);

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

    //private ViewPager mViewPager;

    /*@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.registerViewPager);
        setContentView(mViewPager);

        FragmentManager fm = getSupportFragmentManager();

        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position)
            {
                return RegisterFragment.newInstance(position);
            }

            @Override
            public int getCount()
            {
                return 3;
            }
        });
    }*/
}
