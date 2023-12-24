package com.example.gclo.Models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TerminalViewModel extends ViewModel {

    private MutableLiveData<String> terminalContent = new MutableLiveData<>();

    public LiveData<String> getTerminalContent() {
        return terminalContent;
    }

    public void addMessage(String message) {
        String currentContent = terminalContent.getValue();
        if (currentContent == null) {
            currentContent = "";
        }
        String newContent = currentContent + message + "\n";
        terminalContent.setValue(newContent);
    }
}

