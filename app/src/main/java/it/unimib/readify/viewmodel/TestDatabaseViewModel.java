package it.unimib.readify.viewmodel;


import android.util.Log;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.readify.data.repository.user.TestIDatabaseRepository;
import it.unimib.readify.model.Result;

//todo redo documentation, various things changed
public class TestDatabaseViewModel extends ViewModel {

    private final TestIDatabaseRepository testDatabaseRepository;
    private MutableLiveData<Result> repositoryData;
    private MediatorLiveData<Result> copiedData = new MediatorLiveData<>();
    private boolean authenticationError;

    public TestDatabaseViewModel(TestIDatabaseRepository testDatabaseRepository) {
        this.testDatabaseRepository = testDatabaseRepository;
        /*
            With the coming userMutableLiveData initialization, userMutableLiveData(1) (ViewModel)
            is pointing to the instance of userMutableLiveData(2) (Repository).
            This means that each of them will overwrite the value of the other with method postValue().
            Class MutableLiveData seems to have the same behavior as a POINTER, even though
            pointers don't exist in Java: this class is a type of OBJECT REFERENCE.

            To allow ViewModel to have a different version of the data memorized in Repository,
            it can be used another MutableLiveData instance(3)
            which gets (only) the value from the original one (1) using postValue().
            Using (a) postValue() isn't the same as (b) initialising a MutableLiveData instance with another:
            (a) changes only the value of an already created instance;
            (b) changes the reference of the current instance.

            usare un observe che osserva il value di (1), e quando cambia, si cambia quello di (3)
         */
        repositoryData = testDatabaseRepository.getUserMutableLiveData();
        //todo documentation MediatorLiveData vs MutableLiveData
        copiedData.addSource(repositoryData, newData -> {
            Log.d("viewModel", "source changed");
            /*
            if(newData.isSuccess()) {
                Result.UserSuccess result = new Result.UserSuccess(((Result.UserSuccess)newData).getData());
                copiedData.postValue(result);
            }
             */
            copiedData.postValue(newData);
        });

        authenticationError = false;
    }


    //new logic
    public void setUserMutableLiveData(String email, String password, boolean isRegistered) {
        //userMutableLiveData = testDatabaseRepository.getUser(email, password, isRegistered);
        testDatabaseRepository.getUser(email, password, isRegistered);
    }

    public MediatorLiveData<Result> getUserMediatorLiveData() {
        return copiedData;
    }

    public MutableLiveData<Result> getUserMutableLiveDataFromVM() {
        return repositoryData;
    }
    public MutableLiveData<Result> getUserMutableLiveDataFromRepo() {
        return testDatabaseRepository.getUserMutableLiveData();
    }

    public void setUserMediatorLiveData(Result newData) {
        copiedData.postValue(newData);
    }

    /*
        Remember that userMutableLiveData is initialized in the constructor and that in the
        getLoggedUser method it asks for getLoggedUser.getValue()
        Take in consideration that this CAN (not sure) cause problems in the future
        The alternative is to not initialize the variable and ask for getLoggedUser == null
     */













    //non serve, come molte altre cose
    /*
    public MutableLiveData<Result> getLoggedUser(String email, String password, boolean isRegistered) {
        if(userMutableLiveData.getValue() == null) {
            userMutableLiveData = testDatabaseRepository.getUser(email, password, isRegistered);
        }
        return userMutableLiveData;
    }

     */

    /*
        Problem:
        (Having only this function)
            public MutableLiveData<Result> getLoggedUser(String email, String password, boolean isRegistered) {
            if(userMutableLiveData.getValue() == null) {
                userMutableLiveData = testDatabaseRepository.getUser(email, password, isRegistered);
            }
            return userMutableLiveData;
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
                base value, another observer will be created because we return
                a new instance of userMutableLiveData; that's obviously a problem

         UPDATE:
         Implementing the new logic, the problem can't happen.
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



}
