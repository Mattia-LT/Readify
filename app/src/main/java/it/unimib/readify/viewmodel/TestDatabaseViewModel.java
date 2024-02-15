package it.unimib.readify.viewmodel;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.readify.data.repository.user.TestIDatabaseRepository;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;

//todo rifare documentazione dei problemi ed implementazioni perchè sono cambiate molte cose
public class TestDatabaseViewModel extends ViewModel {

    private final TestIDatabaseRepository testDatabaseRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private boolean authenticationError;

    public TestDatabaseViewModel(TestIDatabaseRepository testDatabaseRepository) {
        this.testDatabaseRepository = testDatabaseRepository;
        userMutableLiveData = testDatabaseRepository.getUserMutableLiveData();
        authenticationError = false;
    }

    public MutableLiveData<Result> getLoggedUser(String email, String password, boolean isRegistered) {
        if(userMutableLiveData.getValue() == null) {
            userMutableLiveData = testDatabaseRepository.getUser(email, password, isRegistered);
        }
        return userMutableLiveData;
    }

    /*
        Problem:
        In case the user insert incorrect credentials
        in the login / registration fragment, which are going to cause database error "invalid credentials",
        (such as insert an email that is not memorized in the Authentication Database yet), these
        (incorrect) credentials will set the value of userMutableLiveData to a Result.Error and,
        after correcting the inputs (email / password) in the login section, the getLoggedUser() method
        is not going to invoke the Repository call because the value of userMutableLiveData is not null,
        causing the system to not be able to update the userMutableLiveData variable
        in the viewModel and Repository.
        This would cause the impossibility to log into the HomeActivity

        Solutions:
        1) adding boolean authenticationError, getter / setter methods and other system logic
            --> this will update the userMutableLiveData only in the Repository and solve the problem????
        2) implementing a mechanism which will set the value of userMutableLiveData to null / new variable
            in case its value is going to be a Result.Error
            --> this will work for the system logic, but each time the system will set the decided
                base value, another observer will be created; that's obviously a problem
     */

    public boolean getAuthenticationError() {
        return authenticationError;
    }
    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }

    //this method update the userMutableLiveData in the Repository
    //why actually does this method make the system alright?
    //how this method update the userMutableLiveData in the viewModel?
    public void getUser(String email, String password, boolean isRegistered) {
        testDatabaseRepository.getUser(email, password, isRegistered);
    }

    public void setUserMutableLiveData(String email, String password, boolean isRegistered) {
        Log.d("viewModel set before", userMutableLiveData.toString());
        if(userMutableLiveData.getValue() != null) {
            Log.d("viewModel set before value", userMutableLiveData.getValue().toString());
        } else {
            Log.d("viewModel set before value", "null");
        }

        userMutableLiveData = testDatabaseRepository.getUser(email, password, isRegistered);

        Log.d("viewModel set after", userMutableLiveData.toString());
        if(userMutableLiveData.getValue() != null) {
            Log.d("viewModel set after value", userMutableLiveData.getValue().toString());
        } else {
            Log.d("viewModel set after value", "null");
        }
    }

    public MutableLiveData<Result> getUserMutableLiveData() {
        Log.d("viewModel get", userMutableLiveData.toString());
        return userMutableLiveData;
    }

    /*
        Remember that userMutableLiveData is initialized in he constructor and that in the
        getLoggedUser method it asks for getLoggedUser.getValue()
        Take in consideration that this CAN (not sure) cause problems in the future
        The alternative is to not initialize the variable and ask for getLoggedUser == null
     */
}
