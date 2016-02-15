package williamgbranco.comune.callback;

import java.util.ArrayList;

import williamgbranco.comune.report.response.AvailableResponse;

/**
 * Created by William Gomes de Branco on 08/10/2015.
 */
public interface ResponseAvailableCallbacks
{
    void done(ArrayList<AvailableResponse> responses);

    void onNetworkNonAvailable();
}
