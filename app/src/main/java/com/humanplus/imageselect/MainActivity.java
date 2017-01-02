package com.humanplus.imageselect;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private static final int ACTION_REQUEST_GALLERY = 2;
    private static final int ACTION_REQUEST_CAMERA = 1;
    private Uri initialUri;

    private static final int STORAGE_PERMISSION = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check read/write external storage first.
        checkPermission();

        imageView = (ImageView) findViewById(R.id.imageView);
    }

    public void onClickButton(View v) {
        switch(v.getId()) {
            case R.id.button:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("사진 선택");
                dialog.setItems(new CharSequence[]{"카메라", "갤러리"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which) {
                                    case 1:
                                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                        intent.setType("image/*");

                                        Intent chooser = Intent.createChooser(intent, "사진 선택");
                                        startActivityForResult(chooser, ACTION_REQUEST_GALLERY);
                                        break;

                                    case 0:
                                        Intent getCameraImage= new Intent("android.media.action.IMAGE_CAPTURE");
                                        File cameraFolder;

                                        if(android.os.Environment.getExternalStorageDirectory()
                                                .equals(android.os.Environment.MEDIA_MOUNTED))
                                            cameraFolder = new File(android.os.Environment.getExternalStorageDirectory(),
                                                    "/smartear/");
                                        else
                                            cameraFolder = getExternalFilesDir("smartear");

                                        if(!cameraFolder.exists()) {
                                            cameraFolder.mkdirs();
                                        }

                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                                        String timestamp = dateFormat.format(new Date());
                                        String fname = "pic_" + timestamp + ".jpg";

                                        File photo = new File(Environment.getExternalStorageDirectory(),
                                                "/smartear/" + fname);
                                        getCameraImage.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                                        initialUri = Uri.fromFile(photo);

                                        startActivityForResult(getCameraImage, ACTION_REQUEST_CAMERA);
                                        break;
                                }
                            }
                        }).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch(requestCode) {
                case ACTION_REQUEST_CAMERA:
                    imageView.setImageURI(initialUri);
                    break;

                case ACTION_REQUEST_GALLERY:
                    Uri imageUri = data.getData();
                    imageView.setImageURI(imageUri);
                    break;
            }
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "카메라 사용을 위해 읽기/쓰기가 필요합니다", Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]
                        {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION);

            } else {
                Toast.makeText(this, "권한 승인되었음", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
