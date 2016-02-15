package williamgbranco.comune.database.loader;

import android.content.Context;

/**
 * Created by William Gomes de Branco on 10/08/2015.
 */
public abstract class DataLoader<D> extends android.support.v4.content.AsyncTaskLoader<D>
{
    private D mData;

    public DataLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading()
    {
        if (mData != null)
        {
            deliverResult(mData);
        }
        else
        {
            forceLoad();
        }
    }

    //@Override
    public void deliverResullt(D data)
    {
        mData = data;

        if (isStarted())
        {
            super.deliverResult(data);
        }
    }
}
