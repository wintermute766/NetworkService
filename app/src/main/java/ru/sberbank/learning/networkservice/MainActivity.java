package ru.sberbank.learning.networkservice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ProgressBar;

public class MainActivity extends Activity {

    private DownloadService.LocalBinder binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (DownloadService.LocalBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    };

    private View downloadButton;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downloadButton = findViewById(R.id.button_download);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service = new Intent(MainActivity.this, DownloadService.class);
                service.setData(Uri.parse("http://droider.ru/wp-content/uploads/2017/03/AndroidPIT-android-O-Oreo-2083.jpg"));
                service.putExtra(DownloadService.EXTRA_FILE_NAME, "android.jpg");
                startService(service);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent service = new Intent(this, DownloadService.class);
        bindService(service, connection, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (binder != null) {
            unbindService(connection);
        }
    }
}
