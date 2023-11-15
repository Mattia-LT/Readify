package it.unimib.readify.ui.startup;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import it.unimib.readify.R;

public class LoginFragment extends Fragment {

    private static final boolean USE_NAVIGATION_COMPONENT = true;


    // TODO: Finisco di sistemare

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button buttonLogin = view.findViewById(R.id.button_login);

        buttonLogin.setOnClickListener( v -> {
            startActivityBasedOnCondition(WelcomeActivity.class,
                    R.id.action_loginFragment_to_homeActivity);
        });

        //TODO guarda codice delle slide del prof

    }




    /**
     * Starts another Activity using Intent or NavigationComponent.
     * @param destinationActivity The class of Activity to start.
     * @param destination The ID associated with the action defined in welcome_nav_graph.xml.
     */
    private void startActivityBasedOnCondition(Class<?> destinationActivity, int destination) {
        if (USE_NAVIGATION_COMPONENT) {
            Navigation.findNavController(requireView()).navigate(destination);
        } else {
            Intent intent = new Intent(requireContext(), destinationActivity);
            startActivity(intent);
        }
        requireActivity().finish();
    }







}