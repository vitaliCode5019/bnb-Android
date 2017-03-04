package com.bicyclebnb.groupridefinder.interfaces;

import com.bicyclebnb.groupridefinder.models.CoordComparableModel;

/**
 * Created by admin on 3/3/17.
 */

public interface IActivityInteractionListener {
    CoordComparableModel getSelectedModel();
    String getProximity(CoordComparableModel model);

}
