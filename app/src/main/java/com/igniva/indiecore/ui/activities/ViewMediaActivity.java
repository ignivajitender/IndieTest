package com.igniva.indiecore.ui.activities;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Log;

/**
 * Created by igniva-andriod-05 on 21/10/16.
 */
public class ViewMediaActivity extends BaseActivity implements View.OnTouchListener {
   private Toolbar mToolbar;
    private String mMediaPath;
    private ImageView mMediaImage;
    private TextView mMediaNotAvailable;
    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f, MAX_ZOOM = 1f;

    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_media);
        initToolbar();
        setUpLayout();
        setDataInViewObjects();
    }


    void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar_media_activity);
            TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            mTvTitle.setVisibility(View.GONE);
            //
            TextView mTvNext = (TextView) mToolbar.findViewById(R.id.toolbar_next);
            mTvNext.setText("DONE");
            mTvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            //
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }
    @Override
    protected void setUpLayout() {

        try {
            mMediaImage = (ImageView) findViewById(R.id.iv_media_image);
            mMediaImage.setOnTouchListener(this);
            mMediaPath = getIntent().getStringExtra(Constants.MEDIA_PATH);
            mMediaNotAvailable=(TextView) findViewById(R.id.tv_media_not_available);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setDataInViewObjects() {
        try {
            if ( mMediaPath!=null && !mMediaPath.isEmpty()) {
                Uri uri = Uri.parse(mMediaPath);
                Glide.with(this).load(uri.getPath())
                        .thumbnail(1f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mMediaImage);
//                mMediaImage.setRotation((float) 90.0);
            }else {
                mMediaImage.setVisibility(View.GONE);
                mMediaNotAvailable.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        try {
            ImageView view = (ImageView) v;
            view.setScaleType(ImageView.ScaleType.MATRIX);
            float scale;

            dumpEvent(event);
            // Handle touch events here...

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: // first finger down only
                    savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());
                    Log.d(TAG, "mode=DRAG"); // write to LogCat
                    mode = DRAG;
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:

                    mode = NONE;
                    Log.d(TAG, "mode=NONE");
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    Log.d(TAG, "oldDist=" + oldDist);
                    if (oldDist > 5f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                        Log.d(TAG, "mode=ZOOM");
                    }
                    break;

                case MotionEvent.ACTION_MOVE:

                    if (mode == DRAG) {
                        matrix.set(savedMatrix);
                        matrix.postTranslate(event.getX() - start.x, event.getY()
                                - start.y); /*
                                         * create the transformation in the matrix
                                         * of points
                                         */
                    } else if (mode == ZOOM) {
                        // pinch zooming
                        float newDist = spacing(event);
                        Log.d(TAG, "newDist=" + newDist);
                        if (newDist > 5f) {
                            matrix.set(savedMatrix);
                            scale = newDist / oldDist;
                        /*
                         * setting the scaling of the matrix...if scale > 1 means
                         * zoom in...if scale < 1 means zoom out
                         */
                            matrix.postScale(scale, scale, mid.x, mid.y);
                        }
                    }
                    break;
            }

            view.setImageMatrix(matrix); // display the transformation on screen
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }


    private void midPoint(PointF point, MotionEvent event) {
        try {
            float x = event.getX(0) + event.getX(1);
            float y = event.getY(0) + event.getY(1);
            point.set(x / 2, y / 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dumpEvent(MotionEvent event) {
        try {
            String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                    "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
            StringBuilder sb = new StringBuilder();
            int action = event.getAction();
            int actionCode = action & MotionEvent.ACTION_MASK;
            sb.append("event ACTION_").append(names[actionCode]);

            if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                    || actionCode == MotionEvent.ACTION_POINTER_UP) {
                sb.append("(pid ").append(
                        action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
                sb.append(")");
            }

            sb.append("[");
            for (int i = 0; i < event.getPointerCount(); i++) {
                sb.append("#").append(i);
                sb.append("(pid ").append(event.getPointerId(i));
                sb.append(")=").append((int) event.getX(i));
                sb.append(",").append((int) event.getY(i));
                if (i + 1 < event.getPointerCount())
                    sb.append(";");
            }

            sb.append("]");
            Log.d("Touch Event", sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
