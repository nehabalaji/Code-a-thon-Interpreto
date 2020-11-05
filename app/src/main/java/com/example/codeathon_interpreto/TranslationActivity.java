package com.example.codeathon_interpreto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Transformations;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TranslationActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button captureImgButton, TranslateButton, detectButton;
    private ImageView imageView;
    private TextView textView, textView1;
    private String originalText, translatedText, a;
    Bitmap imageBitmap;
    private boolean connected;
    Translate translate;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);

        captureImgButton = findViewById(R.id.button_capImg);
        TranslateButton = findViewById(R.id.button_translation);
        imageView = findViewById(R.id.imageToBeRecognized);
        textView = findViewById(R.id.textToBeDisplayed);
        textView1 = findViewById(R.id.translatedText);
        spinner = findViewById(R.id.spinner);
        detectButton = findViewById(R.id.button_detection);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages_array, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        captureImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectTextFromImage();
            }
        });

        TranslateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInternetConnection()){
                    getTranslateService();
                    translate();
                }
                else
                {
                    textView1.setText(getResources().getString(R.string.no_connection));
                }
            }
        });

    }


    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            textView1.setVisibility(View.VISIBLE);
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void detectTextFromImage() {
        final FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);

        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        firebaseVisionTextRecognizer.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displayTextFromImage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TranslationActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void displayTextFromImage(FirebaseVisionText firebaseVisiontext) {
        List<FirebaseVisionText.TextBlock> blockList = firebaseVisiontext.getTextBlocks();
        if(blockList.size() == 0){
            Toast.makeText(TranslationActivity.this, "No text found in image", Toast.LENGTH_LONG).show();
        }
        else{
            for(FirebaseVisionText.TextBlock block: firebaseVisiontext.getTextBlocks()) {
                String Text = block.getText();
                textView.setText(Text);
            }
        }
    }

    public boolean checkInternetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
        return connected;
    }

    public void getTranslateService() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try (InputStream is = getResources().openRawResource(R.raw.interpreto_eb585cb7eb97)) {

            //Get credentials:
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            //Set credentials and get translate service:
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = translateOptions.getService();

        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
    }

    public void translate() {

        String text = spinner.getSelectedItem().toString();
        switch (text){
            case "Kannada": a="kn";
                break;
            case "Tamil": a="ta";
                break;
            case "Telugu": a="te";
                break;
            case "Hindi": a="hi";
                break;
            case "Arabic": a="ar";
                break;
            case "Bengali": a="bn";
                break;
            case "Danish": a="da";
                break;
            case "Dutch": a="nl";
                break;
            case "English": a="el";
                break;
            case "French": a="fr";
                break;
            case "German": a="de";
                break;
            case "Gujarati": a="gu";
                break;
            case "Punjabi": a="pa";
                break;
            case "Sindhi": a="sd";
                break;
            case "Spanish": a="es";
                break;
            default: a="el";
            break;
        }

        //Get input text to be translated:
        originalText = textView.getText().toString();
        Translation translation = translate.translate(originalText, Translate.TranslateOption.targetLanguage(a), Translate.TranslateOption.model("base"));
        translatedText = translation.getTranslatedText();

        //Translated text and original text are set to TextViews:
        textView1.setText(translatedText);
        textView.setText(originalText);
    }

}