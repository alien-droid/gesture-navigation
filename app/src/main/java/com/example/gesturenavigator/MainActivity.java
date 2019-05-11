package com.example.gesturenavigator;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.gesturenavigator.views.DrawingCanvasView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.resetBtn)
    Button resetButton;


    @BindView(R.id.single_touch_view)
    DrawingCanvasView drawingCanvasView;

    @BindView(R.id.button)
    Button button;

    private Classifier mClassifier;


    private void requestAppPermissions() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 123); // your request code
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        requestAppPermissions();

        init();

       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 1000);*/

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = String.valueOf(Calendar.getInstance().getTimeInMillis());
                // generate the image path
                String imagePath = Environment.getExternalStorageDirectory().toString() + File.separator +  fileName + ".png";

                try {

                    // save the image as png
                    FileOutputStream out = new FileOutputStream(imagePath);
                    // compress the image to png and pass it to the output stream
                    drawingCanvasView.mBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    // save the image
                    out.flush();
                    out.close();

                } catch (Exception error) {
                    Log.e("Error saving image", error.getMessage());
                }

                if (mClassifier == null) {
                    Log.e(LOG_TAG, "onDetectClick(): Classifier is not initialized");
                    return;
                } else if (drawingCanvasView.mCanvas == null) {
                    Toast.makeText(getApplicationContext(), "Write Something", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                        drawingCanvasView.mBitmap, 28, 28, false);
                // imp
                Bitmap inverted = ImageUtil.invert(resizedBitmap);
                Result result = mClassifier.classify(inverted);
                renderResult(result);
            }
        });


        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingCanvasView.clearScreen();
            }
        });
    }

    private void init() {
        try {
            mClassifier = new Classifier(this);
        } catch (RuntimeException e) {
            Log.e(LOG_TAG, "Failed to create classifier.", e);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void renderResult(Result result)
    {
        Toast.makeText(getApplicationContext(),"You Entered "+String.valueOf(result.getLabel()),Toast.LENGTH_LONG).show();
        if(String.valueOf(result.getLabel()).equals("W"))
            openApp(getApplicationContext(),"com.whatsapp");
        else if(String.valueOf(result.getLabel()).equals("C"))
        {
            Intent photo= new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(photo, 1);
        }
        else if(String.valueOf(result.getLabel()).equals("M"))
        {
            Intent eventIntentMessage =getPackageManager()
                    .getLaunchIntentForPackage(Telephony.Sms.getDefaultSmsPackage(getApplicationContext()));
            startActivity(eventIntentMessage);
        }
        else if(String.valueOf(result.getLabel()).equals("N")){
            openApp(getApplicationContext(),"com.netflix.mediaclient");
        }
        else if(String.valueOf(result.getLabel()).equals("P")){
            openApp(getApplicationContext(),"net.one97.paytm");
        }
        else if(String.valueOf(result.getLabel()).equals("G")){
            Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 2);
        }
        else if(String.valueOf(result.getLabel()).equals("S")){
            startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
        }
    }

    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {
                return false;
                //throw new ActivityNotFoundException();
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

}
