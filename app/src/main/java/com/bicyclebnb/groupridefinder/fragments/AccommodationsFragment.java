package com.bicyclebnb.groupridefinder.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bicyclebnb.groupridefinder.R;
import com.bicyclebnb.groupridefinder.interfaces.IActivityInteractionListener;
import com.bicyclebnb.groupridefinder.interfaces.IFragmentinteractionListener;
import com.bicyclebnb.groupridefinder.models.AccommodationModel;
import com.bicyclebnb.groupridefinder.models.BikeShopModel;
import com.bicyclebnb.groupridefinder.models.CoordComparableModel;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccommodationsFragment extends Fragment implements IFragmentinteractionListener {
    private IActivityInteractionListener mListener;

    //region UI
    @BindView(R.id.txtName) TextView txtName;
    @BindView(R.id.txtPrice) TextView txtPrice;
    @BindView(R.id.txtLocation) TextView txtLocation;
    @BindView(R.id.txtCategory) TextView txtCategory;
    @BindView(R.id.txtProximity) TextView txtProximity;
    @BindView(R.id.imgAccommodation) ImageView imgAccommodation;

    @OnClick(R.id.btnBook)
    void onBook()
    {
        if(mData == null || mData.url.isEmpty()) return;
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mData.url)));
    }

    //endregion
    AccommodationModel mData;


    public AccommodationsFragment() {
        // Required empty public constructor
    }

    public static AccommodationsFragment newInstance() {
        return new AccommodationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_accommodations, container, false);
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
            if(model instanceof AccommodationModel) {
                mData = (AccommodationModel)model;
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
        if(model instanceof AccommodationModel) {
            mData = (AccommodationModel)model;
            updateUI();
        }
    }

    void updateUI() {
        if(mData != null) {
            txtName.setText(mData.name);
            txtLocation.setText(mData.location);
            txtPrice.setText(mData.price);
            Picasso.with(getContext()).load(mData.imageUrl).into(imgAccommodation);
            txtCategory.setText(mData.category);
            if(mListener != null)
                txtProximity.setText(mListener.getProximity(mData));
        }
    }
}
