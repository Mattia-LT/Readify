package it.unimib.readify.repository;

import androidx.lifecycle.MutableLiveData;

import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.OLSearchApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.source.BaseBookRemoteDataSource;
import it.unimib.readify.util.ResponseCallback;

public class BookRepository implements IBookRepository, ResponseCallback{

    /*
    private final Application application;
    private final OLSearchApiService olSearchApiService;
    //private final BookDao bookDao;
    private final ResponseCallback responseCallback;
    */

    private MutableLiveData<Result> searchApiResponseLiveData;
    private MutableLiveData<Result> workApiResponseLiveData;
    private final BaseBookRemoteDataSource bookRemoteDataSource;

    public BookRepository(BaseBookRemoteDataSource bookRemoteDataSource) {
        searchApiResponseLiveData = new MutableLiveData<>();
        workApiResponseLiveData = new MutableLiveData<>();
        this.bookRemoteDataSource = bookRemoteDataSource;
        this.bookRemoteDataSource.setResponseCallback(this);
    }

    @Override
    public MutableLiveData<Result> searchBooks(String query, String sort, int limit, int offset) {
        //searchApiResponseLiveData = null;
        bookRemoteDataSource.searchBooks(query, sort, limit, offset);
        return searchApiResponseLiveData;
    }

    @Override
    public MutableLiveData<Result> fetchBook(String id) {
        bookRemoteDataSource.fetchBook(id);
        return workApiResponseLiveData;
    }

    @Override
    public void onSuccessFromRemote(Object response) {
        if(response instanceof OLSearchApiResponse){
            searchApiResponseLiveData.postValue(new Result.SearchSuccess((OLSearchApiResponse) response));
        } else if (response instanceof OLWorkApiResponse) {
            workApiResponseLiveData.postValue(new Result.WorkSuccess((OLWorkApiResponse) response));
        }
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        searchApiResponseLiveData.postValue(new Result.Error(exception.getMessage()));
        //workApiResponseLiveData.postValue(new Result.Error(exception.getMessage()));
    }
}
