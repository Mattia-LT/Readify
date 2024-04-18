package it.unimib.readify.adapter;

import static it.unimib.readify.util.Constants.BUNDLE_ID_TOKEN;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import it.unimib.readify.ui.main.TabFollowerListFragment;
import it.unimib.readify.ui.main.TabFollowingListFragment;

public class FollowListViewPagerAdapter extends FragmentStateAdapter {

    private final String idToken;
    public FollowListViewPagerAdapter(Fragment fragment, String idToken) {
        super(fragment);
        this.idToken = idToken;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        Bundle bundleArgs = new Bundle();
        bundleArgs.putString(BUNDLE_ID_TOKEN, idToken);
        switch(position){
            case 0:
            default:
                fragment = new TabFollowerListFragment();
                fragment.setArguments(bundleArgs);
                return fragment;
            case 1:
                fragment = new TabFollowingListFragment();
                fragment.setArguments(bundleArgs);
                return fragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
