package it.unimib.readify.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import it.unimib.readify.adapter.FollowListViewPagerAdapter;
import it.unimib.readify.databinding.FragmentFollowListBinding;

public class FollowListFragment extends Fragment {

    private FragmentFollowListBinding fragmentFollowListBinding;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FollowListViewPagerAdapter followListViewPagerAdapter;

    public FollowListFragment(){}
    public static FollowListFragment newInstance() {
        return new FollowListFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentFollowListBinding = FragmentFollowListBinding.inflate(inflater,container,false);
        return fragmentFollowListBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int tabSelected = FollowListFragmentArgs.fromBundle(getArguments()).getDestination();
        String username = FollowListFragmentArgs.fromBundle(getArguments()).getUsername();
        String idToken = FollowListFragmentArgs.fromBundle(getArguments()).getIdToken();

        requireActivity().setTitle(username);
        tabLayout = fragmentFollowListBinding.tabLayoutFollow;
        viewPager = fragmentFollowListBinding.viewpager2Follow;

        followListViewPagerAdapter = new FollowListViewPagerAdapter(this, idToken);
        viewPager.setAdapter(followListViewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (tabLayout.getTabAt(position) != null) {
                    Objects.requireNonNull(tabLayout.getTabAt(position)).select();
                }
            }
        });

        viewPager.setCurrentItem(tabSelected, false);
    }

}