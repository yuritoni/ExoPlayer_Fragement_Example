package com.example.exopalyerapp.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class VideoPlayerViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    LiveData<com.example.exopalyerapp.model.ViewModel> selected2;
    MutableLiveData<com.example.exopalyerapp.model.ViewModel> selected = new MutableLiveData<com.example.exopalyerapp.model.ViewModel>();

    public LiveData<com.example.exopalyerapp.model.ViewModel> getSelected() {
        selected2 = selected;
        return selected2;
    }


}
