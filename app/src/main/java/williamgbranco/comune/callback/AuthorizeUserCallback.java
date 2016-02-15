package williamgbranco.comune.callback;

import williamgbranco.comune.user.User;

/**
 * Created by William Gomes de Branco on 26/01/2016.
 */
public interface AuthorizeUserCallback
{
    void done(User returnedUser);

    void onNetworkNonAvailable();

    void onUnauthorizedAccess();

    void onServerError();

}
