package app.ij.mlwithtensorflowlite;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import app.ij.mlwithtensorflowlite.ml.ModelUnquant;

public class getImage2 extends AppCompatActivity {

    public static final String CLASSEMAJEUR2=
            "com.example.quizappproject.CLASSEMAJEUR2";
    public static final String CONFIDENCEMAJEUR2 =
            "com.example.quizappproject.CONFIDENCEMAJEUR2";

    public static final String CLASSEMAJEUR3=
            "com.example.quizappproject.CLASSEMAJEUR3";
    public static final String CONFIDENCEMAJEUR3 =
            "com.example.quizappproject.CONFIDENCEMAJEUR3";

    TextView result2, confidence2;
    ImageView imageView2;
    Button picture2;
    int imageSize = 224;
    String classeMajeur2;
    float confidenceMajeur2;
    Button nextPicture2;
    String classeMajeur1;
    String confidenceMajeur1;





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_image2);

        result2 = findViewById(R.id.result2);
        confidence2 = findViewById(R.id.confidence2);
        imageView2 = findViewById(R.id.imageView2);
        picture2 = findViewById(R.id.button2);
        nextPicture2 = findViewById(R.id.button22);
        Intent intent1 = getIntent();
        classeMajeur1=intent1.getStringExtra(MainActivity.CLASSEMAJEUR1);
        confidenceMajeur1= intent1.getStringExtra(MainActivity.CONFIDENCEMAJEUR1);
        System.out.println(classeMajeur1);


        picture2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    //Request camera permission if we don't have it.
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
        nextPicture2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent picture2Intent=new Intent(getImage2.this,finalresult.class);
                picture2Intent.putExtra(CLASSEMAJEUR3,classeMajeur2);
                picture2Intent.putExtra(CONFIDENCEMAJEUR3,String.valueOf(confidenceMajeur2));
                picture2Intent.putExtra(CLASSEMAJEUR2,classeMajeur1);
                picture2Intent.putExtra(CONFIDENCEMAJEUR2,String.valueOf(confidenceMajeur1));
                startActivity(picture2Intent);
            }
        });



    }

    public void classifyImage(Bitmap image){
        try {
            ModelUnquant model = ModelUnquant.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // get 1D array of 224 * 224 pixels in image
            int [] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
            int pixel = 0;
            for(int i = 0; i < imageSize; i++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            ModelUnquant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for(int i = 0; i < confidences.length; i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"piéce blanc double tige", "piéce blanc simple tige ", "iéce noir doule tige", "piéce lisse 3 troues","piéce a tétebombé 3 troues","classe7"};
            result2.setText(classes[maxPos] +"\n confidence ="+maxConfidence);
            confidenceMajeur2= 0;

            String s = "";
            for(int i = 0; i < classes.length; i++){
                s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100);
                if (confidences[i]>confidenceMajeur2){
                    confidenceMajeur2=confidences[i];
                    classeMajeur2=classes[i];
                }
            }

            //confidence2.setText(s);


            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView2.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
