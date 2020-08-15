package id.teambantu.bcgoogle.event;

import id.teambantu.bcgoogle.model.Location;

public abstract class BCPlacesListener {
    public abstract void onSuccess(Location locations);
    public abstract void onFailed(String message);
}
