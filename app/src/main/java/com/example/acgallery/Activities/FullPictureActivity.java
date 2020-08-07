package com.example.acgallery.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import com.example.acgallery.Adapters.SwipeAdapter;
import com.example.acgallery.Composite.AbstractFile;
import com.example.acgallery.Composite.Folder;
import com.example.acgallery.Composite.Picture;
import com.example.acgallery.Filters.PictureFilter;
import com.example.acgallery.Filters.TrueFilter;
import com.example.acgallery.R;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

/*
    this activity open a selected picture and shows a menu of actions like move, remove, etc.
     Additionaly it lets you to display in full screen mode and zoom the picture.
 */
public class FullPictureActivity extends AppCompatActivity {

    private Picture pictureToDisplay;
    private ArrayList<AbstractFile> picturesToDisplay;
    private ViewPager viewPager;
    private Picture pictureDisplayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_picture_layout);

        //changing the status bar color
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));

        //retrieving the picture to be shown
        pictureToDisplay = (Picture) getIntent().getSerializableExtra("file");
        boolean showAllPictures = (boolean) getIntent().getSerializableExtra("allPictures");
        if(showAllPictures)
            picturesToDisplay = pictureToDisplay.getParent().getFolderRoot().getDeepFilteredFiles(new PictureFilter());
        else
            picturesToDisplay = pictureToDisplay.getParent().getFilteredFiles(new PictureFilter());
        showPictureFullScreen();
    }

    //this method shows a menu layout over the activity_thumbnails_layout layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.picture_menu, menu);
        return true;
    }

    //in this method we indicate the actions to be taken for each option of the menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //the action selected must
        pictureDisplayed = getPictureDisplayed();

        if(item.getItemId() == R.id.delete_picture_op){
            new AlertDialog.Builder(this)
                    .setTitle("Delete picture permanently?")
                    .setMessage("If you delete this item, it will be removed permanently from your device.")
                    .setPositiveButton("Delete permanently", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //we delete the picture and make sure that won't remain an empty file of it.
                            pictureDisplayed.delete();
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(pictureDisplayed.getRealFile())));

                            //then we comeback to the folder where the picture was
                            Intent intent = new Intent(getApplicationContext(), ThumbnailsActivity.class);
                            intent.putExtra("file", pictureDisplayed.getParent());
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
            BitmapFactory.decodeFile(pictureDisplayed.getAbsolutePath(), options);
            int height = options.outHeight;
            int width = options.outWidth;

            //all this things are needed to show the creationTime
            String time = null;
            BasicFileAttributes attributes = null;
            Path filePath = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                filePath = Paths.get(pictureDisplayed.getAbsolutePath());
                try {
                    attributes = Files.readAttributes(filePath,BasicFileAttributes.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert attributes != null;
                time = attributes.creationTime().toString();
            }

            String units;
            float size;
            float pictureSize = (float) pictureDisplayed.getRealFile().length();
            if(pictureSize/(1024*1024) > 1.0){
                size = pictureSize/(1024*1024);
                units = "MB";
            }
            else{
                units = "KB";
                size = pictureSize/1024;
            }

            //finally we show all the info about this picture
            assert time != null;
            new AlertDialog.Builder(this)
                    .setTitle(pictureDisplayed.getName())
                    .setMessage(
                            "Dimensions: " + width + " x " + height + "\n" +
                                    "Size: " + size + " " + units + "\n" +
                                    "Path: " + pictureDisplayed.getAbsolutePath() + "\n" +
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
            inputNewName.setText(pictureDisplayed.getBaseName());
            inputNewName.setSelectAllOnFocus(true);

            new AlertDialog.Builder(this)
                    .setView(inputNewName)
                    .setTitle("Rename")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(!pictureDisplayed.rename(inputNewName.getText().toString()))
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
        else {
            Folder folderRoot = pictureDisplayed.getParent().getFolderRoot();
            if(item.getItemId() == R.id.copy_picture_op) {
                Intent intent = new Intent(getApplicationContext(), PasteActivity.class);
                intent.putExtra("file", folderRoot);
                intent.putExtra("paste", getPictureDisplayed());
                intent.putExtra("opCode", 0);
                startActivity(intent);
                finish();
            }
            else if(item.getItemId() == R.id.move_picture_op){
                Intent intent = new Intent(getApplicationContext(), PasteActivity.class);
                intent.putExtra("file", folderRoot);
                intent.putExtra("paste", getPictureDisplayed());
                intent.putExtra("opCode", 1);
                startActivity(intent);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /*
        this method remove all the abstract files that don't have a inner file which path is linked
        wieth a real file in the phone.
    */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ThumbnailsActivity.class);
        intent.putExtra("file", pictureToDisplay.getParent());
        startActivity(intent);
        finish();
    }

    /*
        we have to give only the pictures to the adapter so the app don't crush
        trying to show a folder, then we have to find what is the position of the
        picture to be shown in the arraylist so we can set it up as the current item
     */
    private void showPictureFullScreen(){
        viewPager = findViewById(R.id.view_pager);

        int currentPicturePos = 0;
        for(AbstractFile picture: picturesToDisplay){
            if(picture.equals(pictureToDisplay))
               break;
            currentPicturePos++;
        }
        SwipeAdapter adapter = new SwipeAdapter(this, picturesToDisplay, getSupportActionBar());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPicturePos);
    }

    //this method gets the picture that is currently displayed
    private Picture getPictureDisplayed(){
        int currentPicPosition = viewPager.getCurrentItem();
        for (int i = 0; i < picturesToDisplay.size(); i++){
            if (i == currentPicPosition){
                return (Picture) picturesToDisplay.get(i);
            }
        }
        return null;
    }
}