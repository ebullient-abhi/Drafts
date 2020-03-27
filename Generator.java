package com.example.qrgenerator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class Generator extends AppCompatActivity {
    String TAG="GenerateQRCode";
    EditText edttext;
    ImageView qrimg;
    String inputvalue;
    Button start;
    Button send;
    Button reset;
    Bitmap bitmap;
    Bitmap abhi;
    QRGEncoder qrgEncoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generator);
        qrimg=(ImageView)findViewById(R.id.qrcode);
        edttext=(EditText)findViewById(R.id.edittext);
        start=(Button)findViewById(R.id.createbtn);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputvalue=edttext.getText().toString().trim();
                if (inputvalue.length()>0){
                    WindowManager manager = (WindowManager)getSystemService(WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int width = point.x;
                    int height = point.y;
                    int smallerdimension = width < height ? width : height;
                    smallerdimension = smallerdimension*3/4;
                    qrgEncoder = new QRGEncoder(inputvalue, null, QRGContents.Type.TEXT, smallerdimension);
                    try {
                        bitmap = qrgEncoder.encodeAsBitmap();
                        qrimg.setImageBitmap(bitmap);

                    }
                    catch (WriterException e){
                        Log.v(TAG, e.toString());
                    }
                }
                else
                {
                    edttext.setError("Required");
                }
            }
        });

        send = (Button)findViewById(R.id.sendbtn);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage(abhi);
            }
        });


        reset = (Button)findViewById(R.id.resetbtn);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrimg.setImageDrawable(null);
                edttext.setText("");
            }
        });
    }

    public void shareImage(Bitmap abhi){
        try {
            File cachePath = new File(this.getCacheDir(), "images");
            cachePath.mkdir();
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png");
            abhi.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        File imagePath = new File(this.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.setType("image/png");
            startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }

    }
}
