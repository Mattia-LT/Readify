package it.unimib.readify.ui.main;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import it.unimib.readify.model.Collection;

public class CollectionViewModel extends ViewModel {

    private MutableLiveData<List<Collection>> collectionListLiveData = new MutableLiveData<>();

    public MutableLiveData<List<Collection>> getCollectionListLiveData() {
        return collectionListLiveData;
    }

    public void updateCollectionListLiveData(List<Collection> newList) {
        collectionListLiveData.setValue(newList);
    }
}
