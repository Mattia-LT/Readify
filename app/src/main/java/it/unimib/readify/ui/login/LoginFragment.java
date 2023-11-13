package it.unimib.readify.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import it.unimib.readify.util.DataEncryptionUtil;

public class LoginFragment extends Fragment {

    private static final String TAG = LoginFragment.class.getSimpleName();
    private static final boolean USE_NAVIGATION_COMPONENT = true;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private DataEncryptionUtil dataEncryptionUtil;

    public LoginFragment() {
        // Required empty public constructor
    }

    //TODO da finire di guardare esempio esercitazione 0.3

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }




}
