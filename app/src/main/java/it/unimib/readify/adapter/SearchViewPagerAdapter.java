package it.unimib.readify.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import it.unimib.readify.ui.main.TabSearchBooksFragment;
import it.unimib.readify.ui.main.TabSearchUsersFragment;

public class SearchViewPagerAdapter extends FragmentStateAdapter {
    public SearchViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch(position){
            case 0:
            default:
                fragment = new TabSearchBooksFragment();
                return fragment;
            case 1:
                fragment = new TabSearchUsersFragment();
                return fragment;

        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
