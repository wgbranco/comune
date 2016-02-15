package williamgbranco.comune.institution;

import android.content.Context;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import williamgbranco.comune.R;

/**
 * Created by William de Branco on 17/05/2015.
 */
public class PublicInstitutionSeguranca extends PublicInstitution
{
    public PublicInstitutionSeguranca(Integer pId, Context pContext)
    {
        super(pId, pContext);
        mPlaceBlackIconId = (int) R.drawable.ic_security_black;
        mPlaceDarkGrayIconId = (int) R.drawable.ic_security_dark_gray;
        mPlaceGrayIconId = (int) R.drawable.ic_security_gray;
        mPlaceLightGrayIconId = (int) R.drawable.ic_security_light_gray;
    }

    @Override
    public Integer getTipo()
    {
        return TIPO_SEGURANCA;
    }

    @Override
    public BitmapDescriptor getMapMarkerIconDrawable()
    {
        int icone_resource = R.drawable.ic_security_not_marked;

        if (this.getNotaMedia() > 0.0) {
            if (this.getNotaMedia() >= NOTA_ALTA) {
                icone_resource = R.drawable.ic_security_marked_green;
            } else if (this.getNotaMedia() >= NOTA_MEDIA) {
                icone_resource = R.drawable.ic_security_marked_yellow;
            } else {
                icone_resource = R.drawable.ic_security_marked_red;
            }
        }

        return BitmapDescriptorFactory.fromResource(icone_resource);
    }

    /*@Override
    public boolean isVisible()
    {
        *//*SharedPreferences sharedPrefs = getAppContext().getSharedPreferences(MapFragment.MAP_LAYERS_SHARED_PREFS, Context.MODE_PRIVATE);
        boolean visible = true;

        if (sharedPrefs != null)
        {
            visible = sharedPrefs.getBoolean(MapFragment.MAP_LAYER_SECURITY, true);
        }

        return visible;*//*

        return SharedPrefsManager.get(getAppContext()).getSecurityLayerVisibility();
    }*/
}
