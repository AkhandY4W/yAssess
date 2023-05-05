package com.youth4work.yassess_new.ui.base;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.youth4work.yassess_new.infrastructure.UserManager;
import com.youth4work.yassess_new.ui.NoInternetActivity;
import com.youth4work.yassess_new.utils.CheckNetwork;
import com.youth4work.yassess_new.utils.PreferencesManager;


public class BaseFragment extends Fragment {

    protected Context self;
    protected PreferencesManager mPreferencesManager;
    protected UserManager mUserManager;
    private boolean mActivityStopped;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        self = getActivity();
        if(!CheckNetwork.isInternetAvailable(getContext())){
            {
                Intent intent=new Intent(getContext(), NoInternetActivity.class);
                startActivity(intent);
            }
        }
        mPreferencesManager = PreferencesManager.instance(getActivity());
        mUserManager = UserManager.getInstance(getActivity());

    }


    @Override
    public void onStop() {
        mActivityStopped = true;

        super.onStop();
    }


    public boolean isActivityStopped() {
        return mActivityStopped;
    }

    @NonNull
    protected String getActivityName() {
        // This is a fallback which just logs the activity name if sub classes did not give activity name
        return getClass().getSimpleName();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
