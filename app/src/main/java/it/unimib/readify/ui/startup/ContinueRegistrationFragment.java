package it.unimib.readify.ui.startup;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.HashMap;
import java.util.Objects;

import it.unimib.readify.R;
import it.unimib.readify.databinding.FragmentContinueRegistrationBinding;
import it.unimib.readify.model.FollowGroup;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.CustomViewModelFactory;
import it.unimib.readify.viewmodel.UserViewModel;
import it.unimib.readify.util.SubjectsUtil;

public class ContinueRegistrationFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private FragmentContinueRegistrationBinding fragmentContinueRegistrationBinding;
    private UserViewModel userViewModel;
    private User user;
    private User onSaveUser;
    private Observer<String> usernameErrorObserver;
    private ChipGroup chipGroupGenre;
    private Observer<Result> loggedUserObserver;
    private boolean isUsernameAvailable;
    private boolean isContinueButtonPressed;
    private boolean isNavigationStarted;

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
        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);

        isContinueButtonPressed = false;
        isUsernameAvailable = false;
        isNavigationStarted = false;

        loggedUserObserver = result -> {
            if(result.isSuccess()) {
                this.user = ((Result.UserSuccess) result).getData();
                onSaveUser = new User(user);
                if(checkUserData(onSaveUser) && isContinueButtonPressed && !isNavigationStarted){
                    isNavigationStarted = true;
                    Log.d("USER: ", onSaveUser.toString());
                    Navigation.findNavController(requireView()).navigate(R.id.action_continueRegistrationFragment_to_homeActivity);
                    requireActivity().finish();
                }
            }
        };

        usernameErrorObserver = result -> {
            switch (result) {
                case "available":
                    isUsernameAvailable = true;
                    break;
                case "notAvailable":
                    isUsernameAvailable = false;
                    isContinueButtonPressed = false;
                    break;
                case "error":
                    Toast.makeText(requireContext(), "System: username error", Toast.LENGTH_SHORT).show();
                    isUsernameAvailable = false;
                    isContinueButtonPressed = false;
                    break;
            }
        };
        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
        userViewModel.getUsernameAvailableResult().observe(getViewLifecycleOwner(), usernameErrorObserver);

        fragmentContinueRegistrationBinding.textInputEditTextChooseUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed for this implementation
            }

            @Override
            public void afterTextChanged(Editable s) {
                userViewModel.isUsernameAvailable(s.toString());
            }
        });


        setConfirmPreferences();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.requireActivity(), R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentContinueRegistrationBinding.spinnerGender.setAdapter(adapter);
        fragmentContinueRegistrationBinding.spinnerGender.setOnItemSelectedListener(this);

        chipGroupGenre = fragmentContinueRegistrationBinding.chipgroupGenreFilter;
        loadSubjectsChips();
    }

    private boolean checkUserData(User onSaveUser) {
        return onSaveUser.getUsername() != null &&
                onSaveUser.getGender() != null &&
                onSaveUser.getFollowers() != null &&
                onSaveUser.getFollowing() != null &&
                onSaveUser.getRecommended() != null &&
                onSaveUser.getAvatar() != null &&
                onSaveUser.getVisibility() != null;
    }

    public void setConfirmPreferences() {
        fragmentContinueRegistrationBinding.confirmPreferences.setOnClickListener(v -> {
            isContinueButtonPressed = true;

            String username = Objects.requireNonNull(fragmentContinueRegistrationBinding.textInputLayoutChooseUsername.getEditText()).getText().toString();
            int selectedChipNumber = getSelectedChipCount(chipGroupGenre);
            if(isUsernameOk() && (fragmentContinueRegistrationBinding.spinnerGender.getSelectedItemPosition() != 0) && (selectedChipNumber>2)) {
                onSaveUser.setUsername(username);
                userViewModel.setUserUsername(onSaveUser);
                onSaveUser.setGender(fragmentContinueRegistrationBinding.spinnerGender.getSelectedItem().toString());
                userViewModel.setUserGender(onSaveUser);
                onSaveUser.setRecommended(getSelectedGenres(chipGroupGenre));
                userViewModel.setUserRecommended(onSaveUser);
                onSaveUser.setAvatar("avatar1");
                userViewModel.setUserAvatar(onSaveUser);
                onSaveUser.setFollowers(new FollowGroup(0, null));
                onSaveUser.setFollowing(new FollowGroup(0, null));
                userViewModel.setUserFollowers(onSaveUser);
                userViewModel.setUserFollowing(onSaveUser);
                onSaveUser.setVisibility("public");
                userViewModel.setUserVisibility(onSaveUser);

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
            String username = fragmentContinueRegistrationBinding.textInputEditTextChooseUsername.getText().toString();
            if (!username.trim().isEmpty()) {
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
                }else if(isUsernameAvailable){
                    Toast.makeText(requireContext(), "Correct Username", Toast.LENGTH_SHORT).show();
                    //fragmentContinueRegistrationBinding.textInputEditTextChooseUsername.setText("");
                    return true;
                } else {
                    fragmentContinueRegistrationBinding.continueRegistrationUsernameErrorMessage.setText(R.string.username_already_taken);
                    fragmentContinueRegistrationBinding.continueRegistrationUsernameErrorMessage.setVisibility(View.VISIBLE);
                    return false;
                }
            }
            //todo string se è vuoto
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("ON DESTROY continue", "TRIGGERED");
        userViewModel.getUserMediatorLiveData().removeObserver(loggedUserObserver);
    }
}