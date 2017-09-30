package com.example.guoyang.imagecompress;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imagecompress.ImageCompressUtil;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private static final int SELECT_ORIGINAL_PIC = 1;
    private ImageView beforeim,afterim;
    private TextView beforetv,aftertv;
    private Button startbt,choosebt;
    private String mAfterPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
            "image";
    private String mBeforePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();

    }

    private void initListener() {
        choosebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //调用相册
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_ORIGINAL_PIC);
            }
        });
        startbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap beforeBt = ImageCompressUtil.decodeFile(mBeforePath);
                ImageCompressUtil.compressImage(beforeBt, 75,mAfterPath);
                Bitmap afterBt = BitmapFactory.decodeFile(mAfterPath);
                afterim.setImageBitmap(afterBt);
                try {
                    long filesize = FileSizeUtils.getFileSize(new File(mAfterPath));
                    String textsize = FileSizeUtils.formetFileSize(filesize);
                    aftertv.setText(textsize);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView() {
        beforeim = (ImageView) findViewById(R.id.before_compression_iv);
        afterim = (ImageView) findViewById(R.id.after_compression_iv);
        beforetv = (TextView) findViewById(R.id.before_compression_tv);
        aftertv = (TextView) findViewById(R.id.after_compression_tv);
        startbt = (Button) findViewById(R.id.compress_start_bt);
        choosebt = (Button) findViewById(R.id.compress_choose_bt);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (requestCode == SELECT_ORIGINAL_PIC && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            mBeforePath = c.getString(columnIndex);
            showImage(mBeforePath);
            c.close();
        }
    }

    private void showImage(String imagePath) {
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        beforeim.setImageBitmap(bm);
        try {
            long filesize = FileSizeUtils.getFileSize(new File(imagePath));
            String textsize = FileSizeUtils.formetFileSize(filesize);
            beforetv.setText(textsize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
