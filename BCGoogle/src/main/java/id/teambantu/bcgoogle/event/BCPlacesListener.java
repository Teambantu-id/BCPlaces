package id.teambantu.bcgoogle.event;

import id.teambantu.bcgoogle.model.BCLocation;

public abstract class BCPlacesListener {
    public abstract void onSuccess(BCLocation locations);
    public abstract void onFailed(String message);
}
