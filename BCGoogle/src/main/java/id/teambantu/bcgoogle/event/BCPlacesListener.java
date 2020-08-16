package id.teambantu.bcgoogle.event;

import java.util.List;

import id.teambantu.bcmodel.helper.Location;

public abstract class BCPlacesListener {
    public void onSuccess(Location locations){};
    public void onSuccess(List<Location> locations){};
    public abstract void onFailed(String message);

}
