package it.unimib.readify.ui.main;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;

import org.apache.commons.validator.routines.EmailValidator;

import it.unimib.readify.R;
import it.unimib.readify.viewmodel.CustomViewModelFactory;
import it.unimib.readify.viewmodel.UserViewModel;

public class AuthenticationDialogFragment extends DialogFragment {

    private UserViewModel userViewModel;
    private Observer<Boolean> userAuthenticationObserver;
    private boolean emailCanBeSubmitted;
    private boolean passwordCanBeSubmitted;
    private String emailToVerify;
    private String passwordToVerify;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_authentication, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModels();
        initObservers();

        EditText email = view.findViewById(R.id.authentication_email);
        EditText password = view.findViewById(R.id.authentication_password);
        Button cancel = view.findViewById(R.id.authentication_cancel);
        Button confirm = view.findViewById(R.id.authentication_confirm);
        emailCanBeSubmitted = false;
        passwordCanBeSubmitted = false;

        cancel.setOnClickListener(e -> dismiss());

        if(email != null) {
            email.addTextChangedListener(new TextWatcher() {
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
                    emailToVerify = email.getText().toString().trim();
                    if(!emailToVerify.isEmpty()) {
                        emailCanBeSubmitted = EmailValidator.getInstance().isValid(emailToVerify);
                    } else {
                        emailCanBeSubmitted = false;
                    }
                    setConfirmButtonState(confirm);
                }
            });
        }

        if(password != null) {
            password.addTextChangedListener(new TextWatcher() {
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
                    passwordToVerify = password.getText().toString().trim();
                    passwordCanBeSubmitted = !passwordToVerify.isEmpty();
                    setConfirmButtonState(confirm);
                }
            });
        }

        confirm.setOnClickListener(e -> {
            if(confirm.isEnabled()) {
                if(emailCanBeSubmitted && passwordCanBeSubmitted) {
                    userViewModel.userAuthentication(emailToVerify, passwordToVerify);
                }
            }
        });
    }

    public void initViewModels() {
        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);
    }

    public void initObservers() {
        userAuthenticationObserver = result -> {
            if(result != null) {
                if(result) {
                    Toast.makeText(requireContext(), R.string.authentication_confirmed, Toast.LENGTH_SHORT).show();
                    userViewModel.resetAuthenticationResult();
                    dismiss();
                } else {
                    Toast.makeText(requireContext(), R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                }
            }
        };
        userViewModel.getUserAuthenticationResult().observe(getViewLifecycleOwner(), userAuthenticationObserver);
    }

    public void setConfirmButtonState(Button confirm) {
        if(emailCanBeSubmitted && passwordCanBeSubmitted) {
            confirm.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.login_blue));
            confirm.setEnabled(true);
        } else {
            confirm.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey_disabled_item));
            confirm.setEnabled(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        userViewModel.getUserAuthenticationResult().removeObserver(userAuthenticationObserver);
    }
}