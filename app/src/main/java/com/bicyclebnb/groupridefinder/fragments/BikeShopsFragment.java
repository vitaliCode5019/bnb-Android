package com.bicyclebnb.groupridefinder.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bicyclebnb.groupridefinder.R;
import com.bicyclebnb.groupridefinder.interfaces.IActivityInteractionListener;
import com.bicyclebnb.groupridefinder.interfaces.IFragmentinteractionListener;
import com.bicyclebnb.groupridefinder.models.BikeShopModel;
import com.bicyclebnb.groupridefinder.models.CoordComparableModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BikeShopsFragment extends Fragment implements IFragmentinteractionListener{
    IActivityInteractionListener mListener;

    //region UI
    @BindView(R.id.txtName) TextView txtName;
    @BindView(R.id.txtLocation) TextView txtLocation;
    @BindView(R.id.txtProximity) TextView txtProximity;
    @BindView(R.id.txtPhone) TextView txtPhone;
    //endregion

    BikeShopModel mData;

    public BikeShopsFragment() {
        // Required empty public constructor
    }

    public static BikeShopsFragment newInstance() {
        return new BikeShopsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bike_shops, container, false);
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
            if(model instanceof BikeShopModel) {
                mData = (BikeShopModel)model;
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
        if(model instanceof BikeShopModel) {
            mData = (BikeShopModel)model;
            updateUI();
        }
    }

    void updateUI() {
        if(mData != null) {
            txtName.setText(mData.name);
            txtLocation.setText(mData.location);
            txtPhone.setText(mData.phone);
            if(mListener != null)
                txtProximity.setText(mListener.getProximity(mData));
        }
    }
}
