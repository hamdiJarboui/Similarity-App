package app.ij.mlwithtensorflowlite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class finalresult extends AppCompatActivity {
    TextView textView;

    String[] classes = {"piéce blanc double tige", "piéce blanc simple tige ", "iéce noir doule tige", "piéce lisse 3 troues","piéce a tétebombé 3 troues","classe7"};
    String skip1=classes[0]+classes[3];
    String skip2=classes[3]+classes[0];
    String result;
    Button b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalresult);
        Intent intent1 = getIntent();
        String classeMajeur1=intent1.getStringExtra(getImage2.CLASSEMAJEUR2);
        String confidenceMajeur1= intent1.getStringExtra(getImage2.CONFIDENCEMAJEUR2);
        String classeMajeur2=intent1.getStringExtra(getImage2.CLASSEMAJEUR3);
        String confidenceMajeur2= intent1.getStringExtra(getImage2.CONFIDENCEMAJEUR3);
        float confidebceMajeur1Float=Float.parseFloat(confidenceMajeur1);
        float confidebceMajeur2Float=Float.parseFloat(confidenceMajeur2);

        b=findViewById(R.id.testagainbutton);


        String chaine1=classeMajeur2+classeMajeur1;
        System.out.println(chaine1);
        textView=findViewById(R.id.resultatf);
        if ((chaine1.equals(skip1)==true)||(chaine1.equals(skip2))==true){
            textView.setText("Skip");
        }
        else {
            textView.setText("Risque");
        }
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent finalintent = new Intent(finalresult.this,MainActivity.class);
                startActivity(finalintent);

            }
        });
    }

}