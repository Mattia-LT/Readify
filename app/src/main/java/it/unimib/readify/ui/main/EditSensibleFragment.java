package it.unimib.readify.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.validator.routines.EmailValidator;

import it.unimib.readify.R;
import it.unimib.readify.databinding.FragmentEditSensibleBinding;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.CustomViewModelFactory;
import it.unimib.readify.viewmodel.UserViewModel;

public class EditSensibleFragment extends Fragment {

    private FragmentEditSensibleBinding fragmentEditSensibleBinding;
    private UserViewModel userViewModel;
    private Observer<Boolean> emailErrorObserver;
    private Observer<Boolean> passwordErrorObserver;
    private Observer<Result> userObserver;
    private User user;
    private User onSaveUser;
    private String newPassword;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentEditSensibleBinding = FragmentEditSensibleBinding.inflate(inflater,container,false);
        return fragmentEditSensibleBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);

        emailErrorObserver = result -> {
            /*
            switch (result) {
                case "available":
                    Toast.makeText(requireContext(), "Email updated", Toast.LENGTH_SHORT).show();
                    fragmentSettingsBinding.editSensibleInputEmail.setText("");
                    break;
                case "notAvailable":
                    fragmentSettingsBinding.editSensibleErrorEmail.setText(R.string.email_already_taken);
                    fragmentSettingsBinding.editSensibleErrorEmail.setVisibility(View.VISIBLE);
                    break;
                case "error":
                    //todo use snack bar instead (to implement an action)?
                    Toast.makeText(requireContext(), "System: email error", Toast.LENGTH_SHORT).show();
                    break;
            }

             */
            if(result) {
                Toast.makeText(requireContext(), "Email updated", Toast.LENGTH_SHORT).show();
                fragmentEditSensibleBinding.editSensibleInputEmail.setText("");
            } else {
                Toast.makeText(requireContext(), "failure", Toast.LENGTH_SHORT).show();
            }
        };

        userObserver = result -> {
            if(result.isSuccess()) {
                user = ((Result.UserSuccess)result).getData();
                onSaveUser = new User(user);
            } else {
                //todo navigate to login(?)
                Snackbar.make(view, ((Result.Error)result).getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        };

        passwordErrorObserver = result -> {
            Log.d("email changed", result.toString());
            if(result) {
                Toast.makeText(requireContext(), "Password updated", Toast.LENGTH_SHORT).show();
                fragmentEditSensibleBinding.editSensibleInputPassword.setText("");
                fragmentEditSensibleBinding.editSensibleInputPasswordConfirm.setText("");
            } else {
                //todo use snack bar instead (to implement an action)?
                Toast.makeText(requireContext(), "System: password error", Toast.LENGTH_SHORT).show();
            }
        };

        userViewModel.getSourceEmailError().observe(getViewLifecycleOwner(), emailErrorObserver);
        userViewModel.getSourcePasswordError().observe(getViewLifecycleOwner(), passwordErrorObserver);
        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), userObserver);

        //onclicklistener confirm
        /*
        fragmentEditSensibleBinding.editSensibleErrorEmail.setText("");
        fragmentEditSensibleBinding.editSensibleErrorEmail.setVisibility(View.GONE);
        fragmentEditSensibleBinding.editSensibleErrorPassword.setText("");
        fragmentEditSensibleBinding.editSensibleErrorPassword.setVisibility(View.GONE);
        fragmentEditSensibleBinding.editSensibleErrorConfirmPassword.setText("");
        fragmentEditSensibleBinding.editSensibleErrorConfirmPassword.setVisibility(View.GONE);

            && fragmentEditSensibleBinding.editSensibleInputEmail.getText() != null
                && fragmentEditSensibleBinding.editSensibleInputPassword.getText() != null
                && fragmentEditSensibleBinding.editSensibleInputPasswordConfirm.getText() != null)

                    || !fragmentEditSensibleBinding.editSensibleInputEmail.getText().toString().isEmpty()
                || !fragmentEditSensibleBinding.editSensibleInputPassword.getText().toString().isEmpty()
                || !fragmentEditSensibleBinding.editSensibleInputPasswordConfirm.getText().toString().isEmpty()

         */

        fragmentEditSensibleBinding.buttonUndoChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentEditSensibleBinding.editSensibleInputEmail.setText("");
                fragmentEditSensibleBinding.editSensibleInputPassword.setText("");
                fragmentEditSensibleBinding.editSensibleInputPasswordConfirm.setText("");
            }
        });
    }

    private void onChangeEmail() {
        if(fragmentEditSensibleBinding.editSensibleInputEmail.getText() != null) {
            if (!fragmentEditSensibleBinding.editSensibleInputEmail.getText().toString().trim().isEmpty()) {
                if(!EmailValidator.getInstance().isValid((fragmentEditSensibleBinding.editSensibleInputEmail.getText().toString().trim()))) {
                    fragmentEditSensibleBinding.editSensibleErrorEmail.setText(R.string.email_invalid_input);
                    fragmentEditSensibleBinding.editSensibleErrorEmail.setVisibility(View.VISIBLE);
                } else if(fragmentEditSensibleBinding.editSensibleInputEmail.getText().toString().trim().equals(user.getEmail())) {
                    Toast.makeText(requireContext(), "This is already your email", Toast.LENGTH_SHORT).show();
                } else {
                    String newEmail = fragmentEditSensibleBinding.editSensibleInputEmail.getText().toString().trim();
                    userViewModel.setUserEmail(newEmail);
                }
            }
        }
    }

    private void onChangePassword() {
        if(fragmentEditSensibleBinding.editSensibleInputPassword.getText() != null
                && fragmentEditSensibleBinding.editSensibleInputPasswordConfirm.getText() != null) {
            if(fragmentEditSensibleBinding.editSensibleInputPassword.getText().toString().trim().isEmpty()
                    && !fragmentEditSensibleBinding.editSensibleInputPasswordConfirm.getText().toString().trim().isEmpty()) {
                fragmentEditSensibleBinding.editSensibleErrorPassword.setText(R.string.field_not_filled_in);
                fragmentEditSensibleBinding.editSensibleErrorPassword.setVisibility(View.VISIBLE);
            } else if(!fragmentEditSensibleBinding.editSensibleInputPassword.getText().toString().trim().isEmpty()
                    && fragmentEditSensibleBinding.editSensibleInputPasswordConfirm.getText().toString().trim().isEmpty()) {
                fragmentEditSensibleBinding.editSensibleErrorConfirmPassword.setText(R.string.field_not_filled_in);
                fragmentEditSensibleBinding.editSensibleErrorConfirmPassword.setVisibility(View.VISIBLE);
            } else if(!fragmentEditSensibleBinding.editSensibleInputPassword.getText().toString().trim().isEmpty()
                    && !fragmentEditSensibleBinding.editSensibleInputPasswordConfirm.getText().toString().trim().isEmpty()) {
                if(fragmentEditSensibleBinding.editSensibleInputPassword.getText().toString().trim().length() < 6) {
                    fragmentEditSensibleBinding.editSensibleErrorPassword.setText(R.string.error_password_length);
                    fragmentEditSensibleBinding.editSensibleErrorPassword.setVisibility(View.VISIBLE);
                } else if(!fragmentEditSensibleBinding.editSensibleInputPasswordConfirm.getText().toString().trim()
                        .equals(fragmentEditSensibleBinding.editSensibleInputPassword.getText().toString().trim())) {
                    fragmentEditSensibleBinding.editSensibleErrorConfirmPassword.setText(R.string.error_confirm_password_equal);
                    fragmentEditSensibleBinding.editSensibleErrorConfirmPassword.setVisibility(View.VISIBLE);
                } else {
                    newPassword = fragmentEditSensibleBinding.editSensibleInputPassword.getText().toString().trim();
                    userViewModel.changeUserPassword(newPassword);
                }
            }
        }
    }
}
