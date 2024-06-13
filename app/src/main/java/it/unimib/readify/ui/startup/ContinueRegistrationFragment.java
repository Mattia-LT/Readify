package it.unimib.readify.ui.startup;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
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

import static it.unimib.readify.util.Constants.AVATAR_DEFAULT;
import static it.unimib.readify.util.Constants.USERNAME_AVAILABLE;
import static it.unimib.readify.util.Constants.USERNAME_ERROR;
import static it.unimib.readify.util.Constants.USERNAME_NOT_AVAILABLE;
import static it.unimib.readify.util.Constants.USER_VISIBILITY_PUBLIC;

public class ContinueRegistrationFragment extends Fragment {

    private final String TAG = ContinueRegistrationFragment.class.getSimpleName();

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentContinueRegistrationBinding = FragmentContinueRegistrationBinding.inflate(inflater,container,false);
        return fragmentContinueRegistrationBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        initViewModels();
        initObservers();
        setConfirmPreferences();

        isContinueButtonPressed = false;
        isUsernameAvailable = false;
        isNavigationStarted = false;

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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this.requireActivity(), R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentContinueRegistrationBinding.spinnerGender.setAdapter(adapter);

        chipGroupGenre = fragmentContinueRegistrationBinding.chipgroupGenreFilter;
        loadSubjectsChips();
    }

    private void initViewModels() {
        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);
    }

    private void initObservers() {
        loggedUserObserver = loggedUserResult -> {
            if(loggedUserResult.isSuccess()) {
                this.user = ((Result.UserSuccess) loggedUserResult).getData();
                onSaveUser = new User(user);
                if(checkUserData(onSaveUser) && isContinueButtonPressed && !isNavigationStarted){
                    isNavigationStarted = true;
                    NavDirections action = ContinueRegistrationFragmentDirections.actionContinueRegistrationFragmentToHomeActivity();
                    Navigation.findNavController(requireView()).navigate(action);
                    requireActivity().finish();
                }
            } else {
                String errorMessage = ((Result.Error) loggedUserResult).getMessage();
                Log.e(TAG, "Error: Logged user fetch wasn't successful -> " + errorMessage);
            }
        };

        usernameErrorObserver = result -> {
            switch (result) {
                case USERNAME_AVAILABLE:
                    isUsernameAvailable = true;
                    break;
                case USERNAME_NOT_AVAILABLE:
                    isUsernameAvailable = false;
                    isContinueButtonPressed = false;
                    break;
                case USERNAME_ERROR:
                    Toast.makeText(requireContext(), R.string.generic_error, Toast.LENGTH_SHORT).show();
                    isUsernameAvailable = false;
                    isContinueButtonPressed = false;
                    break;
            }
        };
        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
        userViewModel.getUsernameAvailableResult().observe(getViewLifecycleOwner(), usernameErrorObserver);
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

            String username = Objects
                    .requireNonNull(fragmentContinueRegistrationBinding.textInputLayoutChooseUsername.getEditText())
                    .getText().toString().trim();
            int selectedChipNumber = getSelectedChipCount(chipGroupGenre);
            if(isUsernameOk() && (fragmentContinueRegistrationBinding.spinnerGender.getSelectedItemPosition() != 0)
                    && (selectedChipNumber>2)) {
                username = username.toLowerCase();
                onSaveUser.setUsername(username);
                userViewModel.setUserUsername(onSaveUser);
                onSaveUser.setGender(fragmentContinueRegistrationBinding.spinnerGender.getSelectedItem().toString());
                userViewModel.setUserGender(onSaveUser);
                onSaveUser.setRecommended(getSelectedGenres(chipGroupGenre));
                userViewModel.setUserRecommended(onSaveUser);
                onSaveUser.setAvatar(AVATAR_DEFAULT);
                userViewModel.setUserAvatar(onSaveUser);
                onSaveUser.setFollowers(new FollowGroup(0, null));
                onSaveUser.setFollowing(new FollowGroup(0, null));
                userViewModel.setUserFollowers(onSaveUser);
                userViewModel.setUserFollowing(onSaveUser);
                onSaveUser.setVisibility(USER_VISIBILITY_PUBLIC);
                userViewModel.setUserVisibility(onSaveUser);
                onSaveUser.setTotalNumberOfBooks(0);
                userViewModel.setUserTotalNumberOfBooks(onSaveUser);
            } else {
                if(fragmentContinueRegistrationBinding.spinnerGender.getSelectedItemPosition()== 0){
                    fragmentContinueRegistrationBinding.continueRegistrationGenderErrorMessage
                            .setText(R.string.error_gender);
                    fragmentContinueRegistrationBinding.continueRegistrationGenderErrorMessage
                            .setVisibility(View.VISIBLE);
                } else {
                    fragmentContinueRegistrationBinding.continueRegistrationGenderErrorMessage
                            .setText("");
                    fragmentContinueRegistrationBinding.continueRegistrationGenderErrorMessage
                            .setVisibility(View.GONE);
                }

                if(selectedChipNumber <= 2){
                    fragmentContinueRegistrationBinding.continueRegistrationChipErrorMessage
                            .setText(R.string.error_not_enough_genre);
                    fragmentContinueRegistrationBinding.continueRegistrationChipErrorMessage
                            .setVisibility(View.VISIBLE);
                } else {
                    fragmentContinueRegistrationBinding.continueRegistrationChipErrorMessage
                            .setText("");
                    fragmentContinueRegistrationBinding.continueRegistrationChipErrorMessage
                            .setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean isUsernameOk() {
        if(fragmentContinueRegistrationBinding.textInputEditTextChooseUsername.getText() != null) {
            String username = fragmentContinueRegistrationBinding.textInputEditTextChooseUsername
                    .getText().toString().trim();
            TextView errorUsername = fragmentContinueRegistrationBinding.continueRegistrationUsernameErrorMessage;
            if (!username.isEmpty()) {
                if (username.length() > 20) {
                    errorUsername.setText(R.string.error_username_length);
                    errorUsername.setVisibility(View.VISIBLE);
                    return false;
                } else if (username.contains("/") || username.contains("@")) {
                    errorUsername.setText(R.string.error_username_illegal_symbols);
                    errorUsername.setVisibility(View.VISIBLE);
                    return false;
                } else if(isUsernameAvailable){
                    fragmentContinueRegistrationBinding.textInputEditTextChooseUsername.setText("");
                    return true;
                } else {
                    errorUsername.setText(R.string.username_already_taken);
                    errorUsername.setVisibility(View.VISIBLE);
                    return false;
                }
            }
            //if username is empty
            errorUsername.setText(R.string.input_lack_error);
            errorUsername.setVisibility(View.VISIBLE);
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
            Chip chip = (Chip) getLayoutInflater()
                    .inflate(R.layout.single_subject_chip_layout, chipGroupGenre, false);
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
        userViewModel.getUserMediatorLiveData().removeObserver(loggedUserObserver);
        userViewModel.getUsernameAvailableResult().removeObserver(usernameErrorObserver);
    }
}