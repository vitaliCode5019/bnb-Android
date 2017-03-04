package com.bicyclebnb.groupridefinder.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bicyclebnb.groupridefinder.R;
import com.bicyclebnb.groupridefinder.interfaces.IActivityInteractionListener;
import com.bicyclebnb.groupridefinder.interfaces.IFragmentinteractionListener;
import com.bicyclebnb.groupridefinder.models.CoordComparableModel;
import com.bicyclebnb.groupridefinder.models.GroupRideModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupRidesFragment extends Fragment implements IFragmentinteractionListener {
    static String TAG = "GroupRidesFragment";
    //region UI
    @BindView(R.id.txtName) TextView txtName;
    @BindView(R.id.txtDate) TextView txtDate;
    @BindView(R.id.txtLocation) TextView txtLocation;
    @BindView(R.id.txtProximity) TextView txtProximity;
    @BindView(R.id.txtRideLength) TextView txtRideLength;
    @BindView(R.id.txtDiscipline) TextView txtDiscipline;
    @BindView(R.id.txtNotes) TextView txtNotes;

    @OnClick(R.id.txtUrl)
    void onVisitWebsite()
    {
        if(mData == null || mData.url.isEmpty()) return;
        Log.e(TAG, "Go to url " + mData.url);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mData.url)));
    }
    @OnClick(R.id.btnReview)
    void onReview()
    {
        if(mData == null || mData.eventUrl.isEmpty()) return;
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mData.eventUrl)));
    }
    //endregion

    IActivityInteractionListener mListener;
    GroupRideModel mData;

    public GroupRidesFragment() {
    }

    public static GroupRidesFragment newInstance() {
        return new GroupRidesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_group_rides, container, false);
        ButterKnife.bind(this, rootView);

        updateUI();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof IActivityInteractionListener) {
            mListener = (IActivityInteractionListener)context;
            CoordComparableModel model = mListener.getSelectedModel();
            if(model instanceof  GroupRideModel) {
                mData = (GroupRideModel)model;
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setSelectedModel(CoordComparableModel model) {
        if(model instanceof GroupRideModel) {
            mData = (GroupRideModel) model;
            updateUI();
        }
    }

    void updateUI() {
        if(mData != null) {
            txtName.setText(mData.name);
            txtDate.setText(mData.dayTime);
            txtLocation.setText(mData.location);
            txtRideLength.setText(mData.lengthOfRide);
            txtDiscipline.setText(mData.discipline);
            txtNotes.setText(mData.notes);
            if(mListener != null)
                txtProximity.setText(mListener.getProximity(mData));
        }
    }
}
