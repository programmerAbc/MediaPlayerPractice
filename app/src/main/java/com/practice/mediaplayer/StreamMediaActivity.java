package com.practice.mediaplayer;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

public class StreamMediaActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private static final String TAG = StreamMediaActivity.class.getSimpleName();
    @BindView(R.id.movieTxv)
    TextureView movieTxv;
    @BindView(R.id.replayIBtn)
    ImageButton replayIBtn;
    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_media);
        ButterKnife.bind(this);
        movieTxv.setSurfaceTextureListener(this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int videoHeight = mp.getVideoHeight();
                int videoWidth = mp.getVideoWidth();
                int viewWidth = movieTxv.getMeasuredWidth();
                int viewHeight = movieTxv.getMeasuredHeight();
                int newWidth, newHeight;
                float aspectRatio = (float) videoWidth / videoHeight;
                if (viewHeight >= viewWidth / aspectRatio) {
                    newWidth = viewWidth;
                    newHeight = (int) (viewWidth / aspectRatio);
                } else {
                    newWidth = (int) (viewHeight * aspectRatio);
                    newHeight = viewHeight;
                }
                int xoff = (viewWidth - newWidth) / 2;
                int yoff = (viewHeight - newHeight) / 2;
                Matrix txform = new Matrix();
                txform.setScale((float) newWidth / viewWidth, (float) newHeight / viewHeight);
                txform.postTranslate(xoff, yoff);
                movieTxv.setTransform(txform);
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

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource("http://www.w3school.com.cn/i/movie.mp4");
            mediaPlayer.setSurface(new Surface(surface));
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "onSurfaceTextureAvailable:" + Log.getStackTraceString(e));
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
}
