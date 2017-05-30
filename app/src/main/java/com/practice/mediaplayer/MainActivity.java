package com.practice.mediaplayer;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.practice.mediaplayer.util.PermissionUtil;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE_FILE = 0x01;
    public static final String EXTRA_KEY_MOVIE_FILE = "EXTRA_KEY_MOVIE_FILE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Timber.uprootAll();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected String createStackElementTag(StackTraceElement element) {
                    return super.createStackElementTag(element) + "[LINE]" + element.getLineNumber();
                }
            });
        } else {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected String createStackElementTag(StackTraceElement element) {
                    return super.createStackElementTag(element) + "[LINE]" + element.getLineNumber();
                }

                @Override
                protected void log(int priority, String tag, String message, Throwable t) {
                    if (priority == Log.VERBOSE || priority == Log.DEBUG)
                        return;
                    super.log(priority, tag, message, t);
                }
            });
        }


        PermissionUtil.requestPermission(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
    }

    @OnClick({R.id.localMediaBtn, R.id.streamMediaBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.localMediaBtn:
                startActivityForResult(new Intent(this, FileChooserActivity.class), REQUEST_CODE_CHOOSE_FILE);
                break;
            case R.id.streamMediaBtn:
                startActivity(new Intent(this, StreamMediaActivity.class));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtil.REQUEST_PERMISSION_CODE:
                String[] deniedPermissions = PermissionUtil.handleRequestPermissionResult(permissions, grantResults);
                if (deniedPermissions != null) {
                    PermissionUtil.requestPermission(this, deniedPermissions);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CHOOSE_FILE:
                if (resultCode == RESULT_OK) {
                    final Uri uri = data.getData();
                    String path = FileUtils.getPath(this, uri);
                    if (path != null && FileUtils.isLocal(path)) {
                        File file = new File(path);
                        Intent intent = new Intent(this, LocalMediaActivity.class);
                        intent.putExtra(EXTRA_KEY_MOVIE_FILE, file);
                        startActivity(intent);
                    }
                } else {
                    Log.e(TAG, "onActivityResult: result error");
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
