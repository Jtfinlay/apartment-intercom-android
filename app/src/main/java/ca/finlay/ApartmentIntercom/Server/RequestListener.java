package ca.finlay.ApartmentIntercom.Server;

/**
 * Created by James on 2015-07-15.
 */
public interface RequestListener {
    public void onRequestComplete(String ID, String result);
    public void onRequestError(Exception e);
}
