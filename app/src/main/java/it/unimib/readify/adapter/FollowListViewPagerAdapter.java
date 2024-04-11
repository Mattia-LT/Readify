package it.unimib.readify.adapter;

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
        switch(position){
            case 0:
            default:
                fragment = new TabFollowerListFragment(idToken);
                return fragment;
            case 1:
                fragment = new TabFollowingListFragment(idToken);
                return fragment;
        }
    }

    @Override
    public int getItemCount() {
        return 100;
    }
}

// Instances of this class are fragments representing a single
// object in the collection.
