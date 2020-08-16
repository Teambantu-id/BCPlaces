package id.teambantu.bcgoogle.event;

import java.util.List;

import id.teambantu.bcgoogle.model.BCLocation;

public abstract class BCPlacesListener {
    public void onSuccess(BCLocation locations){};
    public void onSuccess(List<BCLocation> locations){};
    public abstract void onFailed(String message);

}
