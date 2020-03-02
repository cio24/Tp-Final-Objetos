package com.example.acgallery.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import com.example.acgallery.Adapters.SwipeAdapter;
import com.example.acgallery.Classifiers.TensorFlowClassifier;
import com.example.acgallery.Composited.AbstractFile;
import com.example.acgallery.Composited.Folder;
import com.example.acgallery.Composited.Picture;
import com.example.acgallery.Filters.FolderFilter;
import com.example.acgallery.Filters.PictureFilter;
import com.example.acgallery.R;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

/* Tensor flow

import org.tensorflow.yolo.R;
import org.tensorflow.yolo.TensorFlowImageRecognizer;
import org.tensorflow.yolo.model.Recognition;
import org.tensorflow.yolo.util.ImageUtils;
import org.tensorflow.yolo.view.components.BorderedText;
*/

public class FullPictureActivity extends AppCompatActivity {

    private Picture fullPictureDisplayed;
    private ViewPager viewPager;
    private ArrayList<AbstractFile> pictures;
    private ArrayList<Pair<String,Float>> results;
    private TensorFlowClassifier classifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_picture_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));
        }
        fullPictureDisplayed = (Picture) getIntent().getSerializableExtra("fullPicture");


        classifier = new TensorFlowClassifier(this);

        showFullPicture(fullPictureDisplayed);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.picture_menu, menu);
        return true;
    }

    /*
        we have to give only the pictures to the adapter so it don't crush the app
        trying to show a folder, then we have to find what is the posicion of the
        picture to be shown in the arraylist so we can set it up as the current item
     */
    private void showFullPicture(AbstractFile picture){
        viewPager = findViewById(R.id.view_pager);
        pictures = picture.getContainer().getFilteredFiles(new PictureFilter());
        int currentPicturePos = 0;
        for(AbstractFile pic: pictures){
            if(pic.equals(picture))
                break;
            currentPicturePos++;
        }
        SwipeAdapter adapter = new SwipeAdapter(this, pictures, getSupportActionBar());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPicturePos);
    }

    private Picture getPictureDisplayed(){
        int currentPicPosition = viewPager.getCurrentItem();
        for (int i = 0; i < pictures.size(); i++){
            if (i == currentPicPosition){
                return (Picture) pictures.get(i);
            }
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        fullPictureDisplayed = getPictureDisplayed();

        if(item.getItemId() == R.id.delete_picture_op){
            new AlertDialog.Builder(this)
                    .setTitle("Delete Item permanently?")
                    .setMessage("If you delete this item, it will be removed permanently from your device.")
                    .setPositiveButton("Delete Permanently", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //we delete the picture and make sure that won't remain an empty file of it.
                            fullPictureDisplayed.delete();
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fullPictureDisplayed.innerFile)));

                            //then we comeback to the folder where the picture was
                            Intent intent = new Intent(getApplicationContext(), ThumbnailsActivity.class);
                            intent.putExtra("idFolder", fullPictureDisplayed.getContainer());
                            startActivity(intent);

                            //and close this activity
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //close the Alert Dialog
                        }
                    }).create().show();
        }
        else if(item.getItemId() == R.id.back_picture_op) {
            onBackPressed();
        }
        else if(item.getItemId() == R.id.details_picture_op){

            //all of this is necessary to get the real dimensions of the picture
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fullPictureDisplayed.getAbsolutePath(), options);
            int height = options.outHeight;
            int width = options.outWidth;

            //all this things are needed to show the creationTime
            String time = null;
            BasicFileAttributes attributes = null;
            Path filePath = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                filePath = Paths.get(fullPictureDisplayed.getAbsolutePath());
                try {
                    attributes = Files.readAttributes(filePath,BasicFileAttributes.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                time = attributes.creationTime().toString();
            }

            String units;
            Float size;
            Float pictureSize = Float.valueOf(fullPictureDisplayed.getInnerFile().length());
            if(pictureSize/(1024*1024) > 1.0){
                size = pictureSize/(1024*1024);
                units = "MB";
            }
            else{
                units = "KB";
                size = pictureSize/1024;
            }

            //finally we show all the info about this picture
            new AlertDialog.Builder(this)
                    .setTitle(fullPictureDisplayed.getName())
                    .setMessage(
                            "Dimensions: " + width + " x " + height + "\n" +
                            "Size: " + size + " " + units + "\n" +
                            "Path: " + fullPictureDisplayed.getAbsolutePath() + "\n" +
                            "Taken on: " + time.substring(0,time.indexOf("T"))
                    )
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //close the Alert Dialog
                        }
                    })
                    .create().show();
        }
        else if(item.getItemId() == R.id.rename_picture_op){
            //we defined an editText to read the input of the user and let the user select all the text of old name
            final EditText inputNewName = new EditText(this);
            inputNewName.setText(fullPictureDisplayed.getBaseName());
            inputNewName.setSelectAllOnFocus(true);

            new AlertDialog.Builder(this)
                    .setView(inputNewName)
                    .setTitle("Rename")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(!fullPictureDisplayed.rename(inputNewName.getText().toString()))
                                Toast.makeText(getApplicationContext(),"The name couldn't be changed!",Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //close the Alert Dialog
                        }
                    })
                    .create().show();
        }
        else if(item.getItemId() == R.id.copy_picture_op) {
            Intent intent = new Intent(getApplicationContext(), PasteActivity.class);
            intent.putExtra("idFolder", getFolderRoot());
            intent.putExtra("idPicToPaste", getPictureDisplayed());
            intent.putExtra("opCode", 0);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.move_picture_op){
            Intent intent = new Intent(getApplicationContext(), PasteActivity.class);
            intent.putExtra("idFolder", getFolderRoot());
            intent.putExtra("idPicToPaste", getPictureDisplayed());
            intent.putExtra("opCode", 1);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.classify_picture_op){
            ImageView image = new ImageView(this);
            Bitmap myBitmap = BitmapFactory.decodeFile(fullPictureDisplayed.getAbsolutePath());
            image.setImageBitmap(myBitmap);
            results = classifier.classify(image);
            new AlertDialog.Builder(this)
                    .setTitle(fullPictureDisplayed.getName())
                    .setMessage(
                            "TOP 1: label " + results.get(2).first + ", confidence " + results.get(2).second + "\n" +
                            "TOP 2: label " + results.get(1).first + ", confidence " + results.get(1).second + "\n" +
                            "TOP 3: label " + results.get(0).first + ", confidence " + results.get(0).second + "\n"

                    )
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //close the Alert Dialog
                        }
                    })
                    .create().show();
        }


        return super.onOptionsItemSelected(item);
    }

    private Folder getFolderRoot(){
        Folder folderRoot = getPictureDisplayed().getContainer();
        while(folderRoot.getContainer() != null){
            folderRoot = folderRoot.getContainer();
        }
        return folderRoot;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ThumbnailsActivity.class);
        intent.putExtra("idFolder", fullPictureDisplayed.getContainer());
        startActivity(intent);
        finish();
        super.onBackPressed();
    }



}