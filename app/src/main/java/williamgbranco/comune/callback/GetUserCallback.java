package williamgbranco.comune.callback;

import williamgbranco.comune.user.User;

/**
 * Created by William Gomes de Branco on 08/09/2015.
 */
public interface GetUserCallback
{
    void done(User returnedUser);

    void onNetworkNonAvailable();

}