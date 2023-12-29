package it.unimib.readify.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.database.BookDao;
import it.unimib.readify.database.BookRoomDatabase;
import it.unimib.readify.model.OLDocs;
import it.unimib.readify.model.OLSearchApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.service.OLSearchApiService;
import it.unimib.readify.service.OLWorkApiService;
import it.unimib.readify.source.BaseBookRemoteDataSource;
import it.unimib.readify.util.ResponseCallback;
import it.unimib.readify.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookRepository implements IBookRepository, ResponseCallback{

    /*
    private final Application application;
    private final OLSearchApiService olSearchApiService;

    private final OLWorkApiService olWorkApiService;
    //private final BookDao bookDao;
    private final ResponseCallback responseCallback;
    */

    private final MutableLiveData<Result> searchApiResponseLiveData;
    private final BaseBookRemoteDataSource bookRemoteDataSource;

    public BookRepository(BaseBookRemoteDataSource bookRemoteDataSource) {
        searchApiResponseLiveData = new MutableLiveData<>();
        this.bookRemoteDataSource = bookRemoteDataSource;
        this.bookRemoteDataSource.setResponseCallback(this);
    }

    @Override
    public MutableLiveData<Result> searchBooks(String query, String sort, int limit, int offset) {
        bookRemoteDataSource.searchBooks(query, sort, limit, offset);
        return searchApiResponseLiveData;
    }

    @Override
    public MutableLiveData<Result> fetchBook(String id) {
        bookRemoteDataSource.fetchBook(id);
        return searchApiResponseLiveData;
    }

    @Override
    public void onSuccessFromRemote(Object response) {
        Result.Success result;
        if (response instanceof OLWorkApiResponse) {
            result = new Result.Success(response);
            searchApiResponseLiveData.postValue(result);
        }
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        searchApiResponseLiveData.postValue(result);
    }
}
