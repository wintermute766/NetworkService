package ru.sberbank.learning.networkservice;

import android.app.IntentService;
import android.content.Intent;

public class DownloadService extends IntentService {

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
