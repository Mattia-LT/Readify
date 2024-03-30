package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.OL_COVERS_API_ID_PARAMETER;
import static it.unimib.readify.util.Constants.OL_COVERS_API_IMAGE_SIZE_L;
import static it.unimib.readify.util.Constants.OL_COVERS_API_URL;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.validator.routines.EmailValidator;

import de.hdodenhof.circleimageview.CircleImageView;
import it.unimib.readify.R;
import it.unimib.readify.data.repository.user.TestIDatabaseRepository;
import it.unimib.readify.databinding.FragmentSettingsBinding;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.util.TestServiceLocator;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding fragmentSettingsBinding;
    private int imageResourceId;

    private TestDatabaseViewModel  testDatabaseViewModel;
    private Observer<Result> observer;
    private User user;
    private User copiedUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater,container,false);
        return fragmentSettingsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadMenu();
        TestIDatabaseRepository testDatabaseRepository = TestServiceLocator
                .getInstance(requireActivity().getApplication())
                .getRepository(TestIDatabaseRepository.class);
        testDatabaseViewModel = TestDatabaseViewModelFactory.getInstance(testDatabaseRepository)
                .create(TestDatabaseViewModel.class);

        CircleImageView profileImageView = fragmentSettingsBinding.profileImageSelect;
        observer = result -> {
            Log.d("profile fragment", "user changed");
            if(result.isSuccess()) {
                user = ((Result.UserSuccess)result).getData();
                copiedUser = new User(user);
                updateUIObserver();
            } else {
                Snackbar.make(view, ((Result.Error)result).getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        };
        testDatabaseViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), observer);


        profileImageView.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_profileImageSelectorFragment);
        });

        fragmentSettingsBinding.buttonConfirmEdit.setOnClickListener(v -> {

            String username = fragmentSettingsBinding.textInputLayoutUsername.getEditText().getText().toString();
            String email = fragmentSettingsBinding.textInputLayoutEmail.getEditText().getText().toString();
            String password = fragmentSettingsBinding.textInputLayoutPassword.getEditText().getText().toString();
            String passwordConfirm = fragmentSettingsBinding.textInputLayoutPasswordConfirm.getEditText().getText().toString();

            if (isUsernameOk(username) & isEmailOk(email) & isPasswordOk(password) & isPasswordConfirmOk(passwordConfirm)) {
                //collegarsi col db e modificare i dati
            } else {
                // ????
            }
        });

        fragmentSettingsBinding.buttonUndoChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentSettingsBinding.textInputEditTextUsername.setText("");
                fragmentSettingsBinding.textInputEditTextEmail.setText("");
                fragmentSettingsBinding.textInputEditTextPassword.setText("");
                fragmentSettingsBinding.textInputEditTextPasswordConfirm.setText("");
            }
        });
    }

    private boolean isUsernameOk(String username) {
        if (username.isEmpty()) {
            fragmentSettingsBinding.textInputLayoutUsername.setError(getString(R.string.error_username));
            return false;
        } else {
            fragmentSettingsBinding.textInputLayoutUsername.setError(null);
            return true;
        }
    }
    private boolean isEmailOk(String email) {
        if (!EmailValidator.getInstance().isValid((email))) {
            fragmentSettingsBinding.textInputLayoutEmail.setError(getString(R.string.error_email));
            return false;
        } else {
            fragmentSettingsBinding.textInputLayoutEmail.setError(null);
            return true;
        }
    }

    private boolean isPasswordOk(String password) {
        if (password.isEmpty()) {
            fragmentSettingsBinding.textInputLayoutPassword.setError(getString(R.string.error_password));
            return false;
        } else {
            fragmentSettingsBinding.textInputLayoutPassword.setError(null);
            return true;
        }
    }

    private boolean isPasswordConfirmOk(String passwordConfirm) {
        if (passwordConfirm.isEmpty()) {
            fragmentSettingsBinding.textInputLayoutPasswordConfirm.setError(getString(R.string.error_password));
            return false;
        } else {
            fragmentSettingsBinding.textInputLayoutPasswordConfirm.setError(null);
            return true;
        }
    }

    public void loadMenu(){
        // Set up the toolbar and remove all icons
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.top_appbar_home);

        // Enable the back button
        Drawable coloredIcon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_back_24);
        int newColor = getResources().getColor(R.color.white, null);
        if (coloredIcon != null) {
            coloredIcon.setColorFilter(newColor, PorterDuff.Mode.SRC_IN);
        }
        toolbar.setNavigationIcon(coloredIcon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    public void updateUI(int imageResourceId){
        Glide.with(requireActivity().getApplication())
                .load(imageResourceId)
                .dontAnimate()
                .into(fragmentSettingsBinding.profileImageSelect);
    }

    public void updateUIObserver(){
        int avatarId;
        if(user != null){
            try {
                avatarId = R.drawable.class.getDeclaredField(user.getAvatar().toLowerCase()).getInt(null);
            } catch (Exception e) {
                avatarId = R.drawable.ic_baseline_profile_24;
            }
            Glide.with(requireActivity().getApplication())
                    .load(avatarId)
                    .dontAnimate()
                    .into(fragmentSettingsBinding.profileImageSelect);
        } else {
            Log.d("USER NULL", "USER NULL");
        }

        Bundle args = getArguments();
        if (args != null && args.containsKey("imageResourceId")) {
            imageResourceId = args.getInt("imageResourceId");
            updateUI(imageResourceId);
        } else {
            imageResourceId = R.drawable.ic_baseline_profile_24;
            fragmentSettingsBinding.profileImageSelect.setImageResource(imageResourceId);
        }
    }
}
