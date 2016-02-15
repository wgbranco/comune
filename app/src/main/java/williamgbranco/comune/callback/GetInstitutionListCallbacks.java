package williamgbranco.comune.callback;

import java.util.ArrayList;

import williamgbranco.comune.institution.PublicInstitution;

/**
 * Created by William on 13/09/2015.
 */
public interface GetInstitutionListCallbacks
{
    public abstract void done(ArrayList<PublicInstitution> returnedInstitutions);
}
