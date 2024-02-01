package it.unimib.readify.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.unimib.readify.R;
import it.unimib.readify.adapter.BookSearchResultAdapter;
import it.unimib.readify.adapter.ViewPagerAdapter;
import it.unimib.readify.databinding.FragmentSearchBinding;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.viewmodel.BookViewModel;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding fragmentSearchBinding;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;


    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentSearchBinding = FragmentSearchBinding.inflate(inflater,container,false);
        return fragmentSearchBinding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //todo gestire menu

        Bundle args = getArguments();


        tabLayout = fragmentSearchBinding.tabLayout;
        viewPager = fragmentSearchBinding.viewpager;
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
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
                    tabLayout.getTabAt(position).select();
                }
            }
        });


    }







    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //bookViewModel.searchBooks(null,null).removeObservers(getViewLifecycleOwner());
    }

}