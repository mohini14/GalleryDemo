package com.example.mohini.gallerydemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    Button photoButton;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    ArrayList<String> f = new ArrayList<String>();// list of file paths
    File[] listFile;
    ImageAdapter adapter;
    GridView imagegrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoButton = findViewById(R.id.cameraButton);
        imagegrid = (GridView) findViewById(R.id.photoGrid);
        adapter = new ImageAdapter();
        imagegrid.setAdapter(adapter);
        getFromSdcard();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onCameraClick(View view){

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

            System.out.print("test");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        10);
            }
        }
        else
            getCamera();


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 10:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED   && grantResults[2] == PackageManager.PERMISSION_GRANTED) {


                    getCamera();

                } else
                        Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                    break;
            default:


        }
    }

    public void getCamera() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

         startActivityForResult(cameraIntent, 1);
    }



        private void createDirectoryAndSaveFile(Bitmap imageToSave) {

            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/MyGallery");

            if(!myDir.exists())
            myDir.mkdirs();

            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);

            String fname = "Image-"+ n +".jpg";
            File file = new File (myDir, fname);

            if (file.exists ())
                file.delete ();

            try {
                FileOutputStream out = new FileOutputStream(file);
                imageToSave.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {

                Bitmap photo = (Bitmap) data.getExtras().get("data");

                createDirectoryAndSaveFile(photo);
                getFromSdcard();
                adapter.notifyDataSetChanged();
                imagegrid.invalidateViews();
                imagegrid.setAdapter(adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




        public void getFromSdcard()
        {
            File file= new File(android.os.Environment.getExternalStorageDirectory(),"MyGallery");

            f.clear();
            if (file.isDirectory())
            {
                listFile = file.listFiles();


                for (int i = 0; i < listFile.length; i++)
                {

                    f.add(listFile[i].getAbsolutePath());

                }
            }
        }

        public class ImageAdapter extends BaseAdapter {

            private LayoutInflater mInflator;

            public ImageAdapter() {

            }

            @Override
            public int getCount() {
                return f.size();
            }

            @Override
            public Object getItem(int position) {
                return f.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                ViewHolder holder;

                if (convertView == null) {

                    holder = new ViewHolder();
                    mInflator = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = mInflator.inflate(R.layout.grid_view_cell, null);

                    holder.imageview = (ImageView) convertView.findViewById(R.id.gridView);
                    convertView.setTag(holder);

                } else {

                    holder = (ViewHolder) convertView.getTag();

                }
                Bitmap myBitmap = BitmapFactory.decodeFile(f.get(position));
                holder.imageview.setImageBitmap(myBitmap);
                return convertView;
            }
        }

        class ViewHolder{

            ImageView imageview;

        }
    }


