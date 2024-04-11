package it.unimib.readify.ui.main;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import it.unimib.readify.R;
import it.unimib.readify.adapter.FollowListViewPagerAdapter;
import it.unimib.readify.databinding.FragmentFollowListBinding;

public class FollowListFragment extends Fragment {

    private FragmentFollowListBinding fragmentFollowListBinding;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

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

        loadMenu();

        int tabSelected = FollowListFragmentArgs.fromBundle(getArguments()).getDestination();
        String username = FollowListFragmentArgs.fromBundle(getArguments()).getUsername();
        String idToken = FollowListFragmentArgs.fromBundle(getArguments()).getIdToken();

        requireActivity().setTitle(username);
        tabLayout = fragmentFollowListBinding.tabLayoutFollow;
        viewPager = fragmentFollowListBinding.viewpager2Follow;

        FollowListViewPagerAdapter followListViewPagerAdapter = new FollowListViewPagerAdapter(this, idToken);
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
        tabLayout.selectTab(tabLayout.getTabAt(tabSelected));

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (tabLayout.getTabAt(position) != null) {
                    Objects.requireNonNull(tabLayout.getTabAt(position)).select();
                }
            }
        });
    }

    private void loadMenu(){
        // Set up the toolbar and remove all icons
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.top_appbar_home);

        // Enable the back button
        Drawable coloredIcon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_back_24);
        int newColor = getResources().getColor(R.color.white, null);
        if (coloredIcon != null) {
            coloredIcon.setColorFilter(newColor, PorterDuff.Mode.SRC_IN);
        }
        toolbar.setNavigationIcon(coloredIcon);
        toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
    }

}