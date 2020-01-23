package com.example.acgallery.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.acgallery.Composited.Picture;
import com.example.acgallery.R;

public class ImageActivity extends AppCompatActivity {

    SubsamplingScaleImageView image;
    private final Handler mHideHandler = new Handler();
    private boolean mVisible;

    @SuppressLint("ResourceAsColor")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image); //la interfaz que se muestra es la del activity_image.xml

        image = findViewById(R.id.entire_image); //obtenemos el view que vamos a utilizar

        /*
            Obtenemos el dato que queremos mostrar en la view utilizando el nombre que definimos en
            la activity de donde mandamos el dato y lo agregamos al view con Picasso
        */
        Intent intent = getIntent();
        //String path = intent.getExtras().getString("fullImage");

        Picture imageToShow = (Picture) getIntent().getSerializableExtra("fullImage");


        //Picasso.get().load(new File(path)).into(image);



        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(imageToShow.getAbsolutePath(),bmOptions);

        ImageSource source = ImageSource.bitmap(bitmap);
        image.setImage(source);

        mVisible = true;

        // Set up the user interaction to manually show or hide the system UI.
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            image.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.picture_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.delete_op){
            new AlertDialog.Builder(this)
                    .setTitle("Are you sure dude?")
                    .setMessage("The akatsuki members gonna kill u!")
                    .setPositiveButton("YOLO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getApplicationContext(),"Better hide while you can!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("I'm scared", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).create().show();
        }
        if(item.getItemId() == R.id.back_op) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;
        mHideHandler.postDelayed(mHidePart2Runnable, 300);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        image.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;
        mHideHandler.postDelayed(mShowPart2Runnable,300);
    }


}