package com.example.gesturenavigator;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.Arrays;

public class Classifier {
    private static final String LOG_TAG = Classifier.class.getSimpleName();

    private static final String MODEL_PATH = "file:///android_asset/opt_mnist_convnet.pb";

    private static final int DIM_BATCH_SIZE = 1;
    public static final int DIM_IMG_SIZE_HEIGHT = 28;
    public static final int DIM_IMG_SIZE_WIDTH = 28;
    private static final int DIM_PIXEL_SIZE = 1;
    private static final int CATEGORY_COUNT = 10;

    private static final float THRESHOLD = 0.1f;


    private static final String INPUT_NAME = "conv2d_1_input";
    private static final String OUTPUT_NAME = "dense_2/Softmax";
    private static final String[] OUTPUT_NAMES = { OUTPUT_NAME };

    private final int[] mImagePixels = new int[DIM_IMG_SIZE_HEIGHT * DIM_IMG_SIZE_WIDTH];
    private final float[] mImageData = new float[DIM_IMG_SIZE_HEIGHT * DIM_IMG_SIZE_WIDTH];
    private final float[] mResult = new float[CATEGORY_COUNT];

    private TensorFlowInferenceInterface mInferenceInterface;

    public Classifier(Context context) {
        mInferenceInterface = new TensorFlowInferenceInterface(context.getAssets(), MODEL_PATH);
    }

    public Result classify(Bitmap bitmap) {
        convertBitmap(bitmap);

        float output[] = new float[26];

        mInferenceInterface.feed(INPUT_NAME, mImageData, DIM_BATCH_SIZE, DIM_IMG_SIZE_HEIGHT,
                DIM_IMG_SIZE_WIDTH, DIM_PIXEL_SIZE);
        mInferenceInterface.run(OUTPUT_NAMES);
        mInferenceInterface.fetch(OUTPUT_NAME, output);

        Result ans = new Result();

        for (int i = 0; i < output.length; ++i) {
            System.out.println(output[i]);
            System.out.println(Character.toString((char)(i+'A')));
            if (output[i] > THRESHOLD && output[i] > ans.getConf()) {
                ans.update(output[i],Character.toString((char)(i+'A')));
            }
        }

        //Log.v(LOG_TAG, "classify(): result = " + Arrays.toString(mResult));
        return ans;
    }

    public void close() {
        mInferenceInterface.close();
    }

    private void convertBitmap(Bitmap bitmap) {
        bitmap.getPixels(mImagePixels, 0, bitmap.getWidth(), 0, 0,
                bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < DIM_IMG_SIZE_HEIGHT * DIM_IMG_SIZE_WIDTH; i++) {
            mImageData[i] = convertToGreyScale(mImagePixels[i]);
        }
    }

    private float convertToGreyScale(int color) {
        return (((color >> 16) & 0xFF) + ((color >> 8) & 0xFF) + (color & 0xFF)) / 3.0f / 255.0f;
    }

}
