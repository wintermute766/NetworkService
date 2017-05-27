package ru.sberbank.learning.networkservice;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DownloadService extends IntentService {

    public static final String EXTRA_FILE_NAME = "file_name";
    public static final String ACTION_STATE_CHANGED = "download_state_changed";

    private boolean completed = false;
    private boolean withErrors = false;
    private int progress = 0;

    public DownloadService() {
        super("DownloadService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        URL url;

        try {
            url = new URL(intent.getDataString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        String fileName = intent.getStringExtra(EXTRA_FILE_NAME);

        try {
            downloadFile(url, fileName);
            withErrors = false;
            progress = 100;
        } catch (IOException e) {
            withErrors = true;
            progress = 0;
        } finally {
            completed = true;
            notifyStateChanged();
        }

    }

    private void downloadFile(URL url, String fileName) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        int size = connection.getContentLength();

        File directory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        directory.mkdirs();
        File file = new File(directory, fileName);

        BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));

        byte[] buffer = new byte[8096];
        int readed;
        int downloaded = 0;

        while ((readed = is.read(buffer)) > 0) {
            os.write(buffer, 0, readed);
            downloaded += readed;

            int percent = calculatePercent(size, downloaded);

            if (percent != progress) {
                progress = percent;
                notifyStateChanged();
            }

        }

        os.flush();
        os.close();

        connection.disconnect();
    }

    private int calculatePercent(int total, int current) {
        int onePercent = Math.max(1, total / 100);
        return Math.min(current / onePercent, 100);
    }

    private void notifyStateChanged() {
        Intent data = new Intent(ACTION_STATE_CHANGED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(data);
    }

    public class LocalBinder extends Binder {

        public boolean isCompleted() {
            return completed;
        }

        public boolean hasErrors() {
            return withErrors;
        }

        public int getProgress() {
            return progress;
        }
    }
}
