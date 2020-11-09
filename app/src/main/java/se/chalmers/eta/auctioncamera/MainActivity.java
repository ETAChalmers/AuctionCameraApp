package se.chalmers.eta.auctioncamera;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;

import android.Manifest;
import android.app.VoiceInteractor;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;

    private File photoFile;
    private String photoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPremissions();
    }


    public void addNewAuctionItem(View view) {
        EditText idEditText = findViewById(R.id.id_editText);
        EditText yearEditText = findViewById(R.id.year_editText);

        photoName = yearEditText.getText().toString() + String.format("%04d", Integer.parseInt(idEditText.getText().toString()));

        photoFile = null;

        try {
            photoFile = createImageFile(photoName);
        } catch (IOException ex) {
            Toast myToast = Toast.makeText(this, "Error creating file.",
                    Toast.LENGTH_SHORT);
            myToast.show();
            return;
        }

        if (photoFile.exists()) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

            alertDialogBuilder.setMessage("File exists! How do you want to continue?");
            alertDialogBuilder.setPositiveButton("Overwrite existing", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    takePhoto(photoFile);
                }
            });

            alertDialogBuilder.setNeutralButton("Append picture", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    File tempfile;
                    try {
                        tempfile = createImageFile(photoName,true);
                    } catch (IOException ex) {
                            //Uncatched exception ## FIXME
                        return;
                    }

                    takePhoto(tempfile);
                }
            });

            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    return;
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            return;
        }

        takePhoto(photoFile);

    }

    private void takePhoto(File file) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            Uri photoURI = FileProvider.getUriForFile(this, "se.chalmers.eta.auctioncamera.fileProvider", file);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }


    private File createImageFile(String name) throws IOException {
        // Create an image file name
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir.getAbsolutePath() + "/" + name + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }


    private File createImageFile(String name,boolean append) throws IOException {
        // Create an image file name with append
        //Overloading the origional method
        int suflix = 1;
        File image;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        do{
            image = new  File(storageDir.getAbsolutePath() + "/" + name + "_" + suflix + ".jpg");
            suflix++;
        }while(image.exists());

        //image = new File(filename);

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }


    private void requestPremissions(){
        //Checks if premission is set
        if(ContextCompat.checkSelfPermission(this , Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
           //Access to external storage is denied, ask for premission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }


}