package com.euro16.Utils; /**
 * Align image to bottom, fill width. and crop top if needed.
 *
 * http://stackoverflow.com/questions/6330084/imageview-scaling-top-crop
 * https://gist.github.com/arriolac/3843346
 */
import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 *
 */
public class TopCropImageView extends ImageView {

    public TopCropImageView(Context context) {
        super(context);
        setup();
    }

    public TopCropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public TopCropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    private void setup() {
        setScaleType(ScaleType.MATRIX);
    }

//    @Override
//    protected boolean setFrame(int l, int t, int r, int b) {
//        Matrix matrix = getImageMatrix();
//
//        float scale;
//        int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
//        int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
//        int drawableWidth = getDrawable().getIntrinsicWidth();
//        int drawableHeight = getDrawable().getIntrinsicHeight();
//
//        if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
//            scale = (float) viewHeight / (float) drawableHeight;
//        } else {
//            scale = (float) viewWidth / (float) drawableWidth;
//        }
//
//        matrix.setScale(scale, scale);
//        setImageMatrix(matrix);
//
//        return super.setFrame(l, t, r, b);
//    }

    @Override
    protected boolean setFrame(int frameLeft, int frameTop, int frameRight, int frameBottom) {
        float frameWidth = frameRight - frameLeft;
        float frameHeight = frameBottom - frameTop;

        float originalImageWidth = (float)getDrawable().getIntrinsicWidth();
        float originalImageHeight = (float)getDrawable().getIntrinsicHeight();

        float usedScaleFactor = 1;

        if((frameWidth > originalImageWidth) || (frameHeight > originalImageHeight)) {
            // If frame is bigger than image
            // => Crop it, keep aspect ratio and position it at the bottom and center horizontally

            float fitHorizontallyScaleFactor = frameWidth/originalImageWidth;
            float fitVerticallyScaleFactor = frameHeight/originalImageHeight;

            usedScaleFactor = Math.max(fitHorizontallyScaleFactor, fitVerticallyScaleFactor);
        }

        float newImageWidth = originalImageWidth * usedScaleFactor;
        float newImageHeight = originalImageHeight * usedScaleFactor;

        Matrix matrix = getImageMatrix();
        matrix.setScale(usedScaleFactor, usedScaleFactor, 0, 0); // Replaces the old matrix completly
        matrix.postTranslate((frameWidth - newImageWidth) /2, frameHeight - newImageHeight);
        setImageMatrix(matrix);
        return super.setFrame(frameLeft, frameTop, frameRight, frameBottom);
    }
}