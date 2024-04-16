package it.unimib.readify.ui.startup;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import it.unimib.readify.R;
import it.unimib.readify.data.repository.user.TestIDatabaseRepository;
import it.unimib.readify.databinding.FragmentContinueRegistrationBinding;
import it.unimib.readify.databinding.FragmentRegisterBinding;
import it.unimib.readify.model.Result;

public class ContinueRegistrationFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private FragmentContinueRegistrationBinding fragmentContinueRegistrationBinding;

    public ContinueRegistrationFragment() {}

    public static ContinueRegistrationFragment newInstance() { return new ContinueRegistrationFragment();
    }

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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.requireActivity(), R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentContinueRegistrationBinding.spinnerGender.setAdapter(adapter);
        fragmentContinueRegistrationBinding.spinnerGender.setOnItemSelectedListener(this);


        fragmentContinueRegistrationBinding.confirmPreferences.setOnClickListener(v -> {
            String username = fragmentContinueRegistrationBinding.textInputLayoutChooseUsername.getEditText().getText().toString();
            if(isUsernameOk(username) && (fragmentContinueRegistrationBinding.spinnerGender.getSelectedItemPosition() != 0))
            {
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

