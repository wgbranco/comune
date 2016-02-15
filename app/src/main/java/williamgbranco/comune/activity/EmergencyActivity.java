package williamgbranco.comune.activity;

import android.support.v4.app.Fragment;


public class EmergencyActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return new EmergencyFragment();
    }
}
