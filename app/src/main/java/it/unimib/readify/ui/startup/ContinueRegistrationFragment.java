package it.unimib.readify.ui.startup;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.databinding.FragmentContinueRegistrationBinding;
import it.unimib.readify.model.ExternalGroup;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;
import it.unimib.readify.model.User;
import it.unimib.readify.util.SubjectsUtil;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class ContinueRegistrationFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private FragmentContinueRegistrationBinding fragmentContinueRegistrationBinding;
    private TestDatabaseViewModel testDatabaseViewModel;
    private User user;
    private User onSaveUser;
    private Observer<Result> userObserver;
    private Observer<String> usernameErrorObserver;
    private ChipGroup chipGroupGenre;
    private ExternalGroup externalGroup;

    Observer<Result> loggedUserObserver;

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
                    Toast.makeText(requireContext(), "Correct Username", Toast.LENGTH_SHORT).show();
                    fragmentContinueRegistrationBinding.textInputEditTextChooseUsername.setText("");
                    break;
                case "notAvailable":
                    fragmentContinueRegistrationBinding.continueRegistrationUsernameErrorMessage.setText(R.string.username_already_taken);
                    fragmentContinueRegistrationBinding.continueRegistrationUsernameErrorMessage.setVisibility(View.VISIBLE);
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

        chipGroupGenre = fragmentContinueRegistrationBinding.chipgroupGenreFilter;
        loadSubjectsChips();

}

    public void setConfirmPreferences(View view) {
        fragmentContinueRegistrationBinding.confirmPreferences.setOnClickListener(v -> {
            String username = fragmentContinueRegistrationBinding.textInputLayoutChooseUsername.getEditText().getText().toString();
            int selectedChipNumber = getSelectedChipCount(chipGroupGenre);
            if(isUsernameOk() && (fragmentContinueRegistrationBinding.spinnerGender.getSelectedItemPosition() != 0) && (selectedChipNumber>2))
            {
                onSaveUser.setUsername(username);
                testDatabaseViewModel.updateUserData(onSaveUser, null);
                onSaveUser.setGender(fragmentContinueRegistrationBinding.spinnerGender.getSelectedItem().toString());
                testDatabaseViewModel.setUserGender(onSaveUser);
                onSaveUser.setRecommended(getSelectedGenres(chipGroupGenre));
                testDatabaseViewModel.setUserRecommended(onSaveUser);
                onSaveUser.setAvatar("avatar1");
                testDatabaseViewModel.setUserAvatar(onSaveUser);
                onSaveUser.setFollowers(externalGroup = new ExternalGroup(0, null));
                onSaveUser.setFollowing(externalGroup = new ExternalGroup(0, null));
                testDatabaseViewModel.setUserFollowers(onSaveUser);
                testDatabaseViewModel.setUserFollowing(onSaveUser);
                

                Navigation.findNavController(requireView()).navigate(R.id.action_continueRegistrationFragment_to_homeActivity);

            } else {
                if(fragmentContinueRegistrationBinding.spinnerGender.getSelectedItemPosition()== 0){
                    fragmentContinueRegistrationBinding.continueRegistrationGenderErrorMessage.setText(R.string.error_gender);
                    fragmentContinueRegistrationBinding.continueRegistrationGenderErrorMessage.setVisibility(View.VISIBLE);
                }
                if(selectedChipNumber <= 2){
                    fragmentContinueRegistrationBinding.continueRegistrationChipErrorMessage.setText(R.string.error_not_enough_genre);
                    fragmentContinueRegistrationBinding.continueRegistrationChipErrorMessage.setVisibility(View.VISIBLE);
                }
            }
        });
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean isUsernameOk() {
        if(fragmentContinueRegistrationBinding.textInputEditTextChooseUsername.getText() != null) {
            if (!fragmentContinueRegistrationBinding.textInputEditTextChooseUsername.getText().toString().trim().isEmpty()) {
                if (fragmentContinueRegistrationBinding.textInputEditTextChooseUsername.getText().toString().trim().length() > 20) {
                    fragmentContinueRegistrationBinding.continueRegistrationUsernameErrorMessage.setText(R.string.error_username_length);
                    fragmentContinueRegistrationBinding.continueRegistrationUsernameErrorMessage.setVisibility(View.VISIBLE);
                    return false;
                }
                else if (fragmentContinueRegistrationBinding.textInputEditTextChooseUsername.getText().toString().trim().contains("/")
                        || fragmentContinueRegistrationBinding.textInputEditTextChooseUsername.getText().toString().trim().contains("@")) {
                    fragmentContinueRegistrationBinding.continueRegistrationUsernameErrorMessage.setText(R.string.error_username_illegal_symbols);
                    fragmentContinueRegistrationBinding.continueRegistrationUsernameErrorMessage.setVisibility(View.VISIBLE);
                    return false;
                }
                onSaveUser.setUsername(fragmentContinueRegistrationBinding.textInputEditTextChooseUsername.getText().toString().trim());
                return true;
            }
            fragmentContinueRegistrationBinding.continueRegistrationUsernameErrorMessage.setText("RURURURU");
            fragmentContinueRegistrationBinding.continueRegistrationUsernameErrorMessage.setVisibility(View.VISIBLE);
            return false;
        }
        return false;
    }

    private void loadSubjectsChips(){
        SubjectsUtil subjectsUtil = SubjectsUtil.getSubjectsUtil(requireContext());
        Resources res = getResources();
        String[] subjects = res.getStringArray(R.array.chip_genres);
        int[] subjectIds = subjectsUtil.getChipIdList();
        for(int i = 0; i < subjects.length; i++) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.single_subject_chip_layout, chipGroupGenre, false);
            chip.setText(subjects[i]);
            chip.setId(subjectIds[i]);
            chipGroupGenre.addView(chip);
        }
    }


    private int getSelectedChipCount(ChipGroup chipGroup) {
        return chipGroup.getCheckedChipIds().size();
    }

    private HashMap<String, Integer> getSelectedGenres(ChipGroup chipGroup) {
        SubjectsUtil subjectsUtil = SubjectsUtil.getSubjectsUtil(requireContext());
        HashMap<String, Integer> selectedGenresMap = new HashMap<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip != null){
                if (chip.isChecked()) {
                    String chipText = chip.getText().toString().toLowerCase();
                    selectedGenresMap.put(subjectsUtil.getApiValue(chipText), 5);
                } else {
                    String chipText = chip.getText().toString().toLowerCase();
                    selectedGenresMap.put(subjectsUtil.getApiValue(chipText), 0);
                }
            }
        }

        if (selectedGenresMap.isEmpty()) {
            selectedGenresMap = null;
        }
        return selectedGenresMap;
    }


}