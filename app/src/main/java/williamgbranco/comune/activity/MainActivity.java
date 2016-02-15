package williamgbranco.comune.activity;

import android.support.v4.app.Fragment;

public class MainActivity extends SingleFragmentActivity
{

    @Override
    protected Fragment createFragment()
    {
        String action = getIntent().getAction();

        return MainFragment.newInstance(action);
    }
}
