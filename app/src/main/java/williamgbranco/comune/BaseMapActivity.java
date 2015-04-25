package williamgbranco.comune;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class BaseMapActivity extends SingleFragmentActivity
{
    private static final String TAG = "comune.BaseMapActivity";

    @Override
    protected Fragment createFragment()
    {
        Log.i(TAG, "fragment created");
        return BaseMapFragment.newInstance();
    }
}
