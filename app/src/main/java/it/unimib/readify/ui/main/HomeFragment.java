package it.unimib.readify.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import it.unimib.readify.R;
import it.unimib.readify.ui.startup.WelcomeActivity;

public class HomeFragment extends Fragment {

    private static final boolean USE_NAVIGATION_COMPONENT = true;


    public HomeFragment() {
        // Required empty public constructor
    }
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        final Button buttonDaRimuovere = rootView.findViewById(R.id.button_da_rimuovere);

        buttonDaRimuovere.setOnClickListener(v -> {
            navigateToHomeActivity();
        });

        return rootView;
    }




    private void navigateToHomeActivity() {
        if (USE_NAVIGATION_COMPONENT) {
            Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_bookDetailsFragment);
        } else {
            Intent intent = new Intent(requireContext(), HomeActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }
    }

}