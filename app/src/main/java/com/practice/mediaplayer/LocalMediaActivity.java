package com.practice.mediaplayer;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

public class LocalMediaActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    @BindView(R.id.movieTxv)
    TextureView movieTxv;
    File movieFile;
    MediaPlayer mediaPlayer;
    @BindView(R.id.replayIBtn)
    ImageButton replayIBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_media);
        ButterKnife.bind(this);
        movieTxv.setSurfaceTextureListener(this);
        movieFile = (File) getIntent().getSerializableExtra(MainActivity.EXTRA_KEY_MOVIE_FILE);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
                valueAnimator.setDuration(600);
                valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float v = (float) animation.getAnimatedValue();
                        replayIBtn.setScaleX(v);
                        replayIBtn.setScaleY(v);
                        replayIBtn.setRotation(-360f * (1f - v));
                    }
                });
                replayIBtn.setVisibility(View.VISIBLE);
                valueAnimator.start();
            }
        });
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(movieFile.getAbsolutePath());
            mediaPlayer.setSurface(new Surface(surface));
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Toast.makeText(this, "打开文件失败", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @OnClick(R.id.replayIBtn)
    public void onViewClicked() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, 0f);
        valueAnimator.setDuration(600);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                replayIBtn.setScaleX(v);
                replayIBtn.setScaleY(v);
                replayIBtn.setTranslationY(replayIBtn.getMeasuredHeight() * (1f - v));
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                replayIBtn.setVisibility(GONE);
                replayIBtn.setTranslationY(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
        mediaPlayer.start();


    }
}
