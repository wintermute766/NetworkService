package ru.sberbank.learning.networkservice;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends IntentService {

    public static final String EXTRA_FILE_NAME = "file_name";

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

        while ((readed = is.read(buffer)) > 0) {
            os.write(buffer, 0, readed);
        }

        os.flush();
        os.close();

        connection.disconnect();
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
