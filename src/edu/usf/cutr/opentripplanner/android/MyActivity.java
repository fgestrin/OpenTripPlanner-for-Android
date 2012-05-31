/*
 * Copyright 2012 University of South Florida
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package edu.usf.cutr.opentripplanner.android;

import java.util.List;


import edu.usf.cutr.opentripplanner.android.R;
import org.opentripplanner.api.model.Leg;
import org.osmdroid.util.GeoPoint;

import edu.usf.cutr.opentripplanner.android.fragments.DirectionListFragment;
import edu.usf.cutr.opentripplanner.android.fragments.MainFragment;
import edu.usf.cutr.opentripplanner.android.model.OTPBundle;
import edu.usf.cutr.opentripplanner.android.sqlite.ServersDataSource;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Khoa Tran
 *
 */

public class MyActivity extends FragmentActivity implements OnFragmentListener{

	private List<Leg> currentItinerary = null;

	private OTPBundle bundle = null;

	private MainFragment mainFragment;

	private ServersDataSource datasource;

	private String TAG = "OTP";

	private boolean isLookingForCurrentLocation = false;
	
	private String currentRequestString="";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bundle = (OTPBundle)getLastCustomNonConfigurationInstance();

		if(savedInstanceState==null){
			setContentView(R.layout.activity);
			FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
			mainFragment = new MainFragment();
			fragmentTransaction.replace(R.id.mainFragment, mainFragment);
			fragmentTransaction.commit();
		}

		setDatasource(new ServersDataSource(this));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(requestCode) {
		case OTPApp.REFRESH_SERVER_LIST_REQUEST_CODE: 
			if (resultCode == RESULT_OK) {
				boolean shouldRefresh = data.getBooleanExtra(OTPApp.REFRESH_SERVER_RETURN_KEY, false);
				Toast.makeText(this, "Should server list refresh? " + shouldRefresh, Toast.LENGTH_LONG).show();
				if(shouldRefresh){
					mainFragment.processServerSelector(null, true);
				}
				break;
			}
		}
	}

	@Override
	public void onItinerarySelected(List<Leg> l) {
		// TODO Auto-generated method stub
		currentItinerary = l;
	}

	@Override
	public List<Leg> getCurrentItinerary() {
		// TODO Auto-generated method stub
		return currentItinerary;
	}

	@Override
	public void onDirectionFragmentSwitched() {
		// TODO Auto-generated method stub
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();

		//		transaction.hide(mainFragment);

		Fragment directionFragment = new DirectionListFragment();
		transaction.add(R.id.mainFragment, directionFragment);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.addToBackStack(null);

		transaction.commit();
	}

	@Override
	public OTPBundle getOTPBundle() {
		// TODO Auto-generated method stub
		return bundle;
	}

	@Override
	public void setOTPBundle(OTPBundle b) {
		// TODO Auto-generated method stub
		this.bundle = b;
		this.bundle.setCurrentItinerary(currentItinerary);
	}

	@Override
	public void onMainFragmentSwitched(Fragment f) {
		// TODO Auto-generated method stub
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.remove(f);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fm.popBackStack();
		transaction.commit();
	}

	/**
	 * @return the datasource
	 */
	public ServersDataSource getDatasource() {
		return datasource;
	}

	/**
	 * @param datasource the datasource to set
	 */
	public void setDatasource(ServersDataSource datasource) {
		this.datasource = datasource;
	}

	@Override
	public void setScreenCenterTo(GeoPoint gp) {
		// TODO Auto-generated method stub
		mainFragment.setScreenToPoint(gp);
		//		GeoPoint gp = new GeoPoint(selectedServer.getCenterLatitude()*1E6, selectedServer.getCenterLongitude()*1E6);
	}

	/**
	 * @return the isLookingForCurrentLocation
	 */
	public boolean isLookingForCurrentLocation() {
		return isLookingForCurrentLocation;
	}

	/**
	 * @param isLookingForCurrentLocation the isLookingForCurrentLocation to set
	 */
	@Override
	public void setLookingForCurrentLocation(boolean isLookingInput, GeoPoint currentLocation) {
		this.isLookingForCurrentLocation = isLookingInput;

		if(!isLookingInput){
			mainFragment.processServerSelector(currentLocation, false);
		}
	}

	@Override
	public void setCurrentRequestString(String url) {
		currentRequestString = url;
	}

	@Override
	public String getCurrentRequestString() {
		return currentRequestString;
	}
}