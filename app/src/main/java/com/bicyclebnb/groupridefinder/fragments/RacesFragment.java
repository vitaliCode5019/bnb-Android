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
import com.bicyclebnb.groupridefinder.models.RaceModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RacesFragment extends Fragment implements IFragmentinteractionListener{
    static String TAG = "RacesFragment";
    //region UI
    @BindView(R.id.txtName) TextView txtName;
    @BindView(R.id.txtLocation) TextView txtLocation;
    @BindView(R.id.txtStartDate) TextView txtStartDate;
    @BindView(R.id.txtEndDate) TextView txtEndDate;
    @BindView(R.id.txtProximity) TextView txtProximity;
    @BindView(R.id.txtRaceType) TextView txtRaceType;
    @BindView(R.id.txtRaceFormat) TextView txtRaceFormat;
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
    private IActivityInteractionListener mListener;
    RaceModel mData;

    public RacesFragment() {
        // Required empty public constructor
    }

    public static RacesFragment newInstance() {
        return new RacesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_races, container, false);
        ButterKnife.bind(this, rootView);

        updateUI();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IActivityInteractionListener) {
            mListener = (IActivityInteractionListener) context;
            CoordComparableModel model = mListener.getSelectedModel();
            if(model instanceof RaceModel) {
                mData = (RaceModel) model;
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
        if(model instanceof RaceModel) {
            mData = (RaceModel) model;
            updateUI();
        }
    }
/*
    @BindView(R.id.txtName) TextView txtName;
    @BindView(R.id.txtLocation) TextView txtLocation;
    @BindView(R.id.txtStartDate) TextView txtStartDate;
    @BindView(R.id.txtEndDate) TextView txtEndDate;
    @BindView(R.id.txtProximity) TextView txtProximity;
    @BindView(R.id.txtRaceType) TextView txtRaceType;
    @BindView(R.id.txtRaceFormat) TextView txtRaceFormat;
    @BindView(R.id.txtNotes) TextView txtNotes;

 */
    void updateUI()
    {
        if(mData != null) {
            txtName.setText(mData.name);
            txtLocation.setText(mData.location);
            txtStartDate.setText(mData.startDate);
            txtEndDate.setText(mData.endDate);
            txtRaceType.setText(mData.type);
            txtRaceFormat.setText(mData.format);
            txtNotes.setText(mData.notes);
            if(mListener != null)
                txtProximity.setText(mListener.getProximity(mData));
        }
    }
}
