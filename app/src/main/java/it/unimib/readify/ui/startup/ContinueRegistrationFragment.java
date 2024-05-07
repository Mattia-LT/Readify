package it.unimib.readify.ui.startup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import it.unimib.readify.R;
import it.unimib.readify.databinding.FragmentContinueRegistrationBinding;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class ContinueRegistrationFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private FragmentContinueRegistrationBinding fragmentContinueRegistrationBinding;
    private TestDatabaseViewModel testDatabaseViewModel;
    Observer<Result> loggedUserObserver;
    private Observer<String> usernameErrorObserver;
    private User user;
    private User onSaveUser;

    public ContinueRegistrationFragment() {}

    public static ContinueRegistrationFragment newInstance() { return new ContinueRegistrationFragment();}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentContinueRegistrationBinding = FragmentContinueRegistrationBinding.inflate(inflater,container,false);
        return fragmentContinueRegistrationBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        testDatabaseViewModel = TestDatabaseViewModelFactory.getInstance(requireActivity().getApplication())
                .create(TestDatabaseViewModel.class);

        loggedUserObserver = result -> {
            if(result.isSuccess()) {
                this.user = ((Result.UserSuccess) result).getData();
                onSaveUser = new User(user);

                setConfirmPreferences(view);
            }
        };

        usernameErrorObserver = result -> {
            //todo sometimes observer triggers when input is empty (?)
            switch (result) {
                case "available":
                    Toast.makeText(requireContext(), "Username available", Toast.LENGTH_SHORT).show();
                    break;
                case "notAvailable":
                    Toast.makeText(requireContext(), "Username already taken", Toast.LENGTH_SHORT).show();
                    break;
                case "error":
                    //todo use snack bar instead (to implement an action)?
                    Toast.makeText(requireContext(), "System: username error", Toast.LENGTH_SHORT).show();
                    break;
            }
        };

        testDatabaseViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
        testDatabaseViewModel.getSourceUsernameError().observe(getViewLifecycleOwner(), usernameErrorObserver);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.requireActivity(), R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentContinueRegistrationBinding.spinnerGender.setAdapter(adapter);
        fragmentContinueRegistrationBinding.spinnerGender.setOnItemSelectedListener(this);
}

    public void setConfirmPreferences(View view) {
        fragmentContinueRegistrationBinding.confirmPreferences.setOnClickListener(v -> {
            String username = fragmentContinueRegistrationBinding.textInputLayoutChooseUsername.getEditText().getText().toString();
            if(isUsernameOk(username) && (fragmentContinueRegistrationBinding.spinnerGender.getSelectedItemPosition() != 0))
            {
                onSaveUser.setUsername(username);
                onSaveUser.setGender(fragmentContinueRegistrationBinding.spinnerGender.getSelectedItem().toString());

                //TODO CALL NEW METHODS
                //testDatabaseViewModel.updateUserData(onSaveUser, null);

                testDatabaseViewModel.setUserGender(onSaveUser);
                Navigation.findNavController(requireView()).navigate(R.id.action_continueRegistrationFragment_to_homeActivity);
            } else {
                Snackbar.make(view, "Errore nei campi", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean isUsernameOk(String username) {
        if (username.isEmpty()) {
            fragmentContinueRegistrationBinding.textInputLayoutChooseUsername.setError(getString(R.string.error_username));
            return false;
        } else {
            fragmentContinueRegistrationBinding.textInputLayoutChooseUsername.setError(null);
            return true;
        }
    }
}