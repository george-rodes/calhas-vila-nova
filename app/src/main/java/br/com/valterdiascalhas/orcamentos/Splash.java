package br.com.valterdiascalhas.orcamentos;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView elementocalha = (ImageView) findViewById(R.id.elementocalha);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        elementocalha.setAnimation(animation);
        ImageView elementoelolico = (ImageView) findViewById(R.id.elementoelolico);
        Animation a2 = AnimationUtils.loadAnimation(this,R.anim.fade_in2);
        elementoelolico.setAnimation(a2);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
                finish();
            }
        }, 3000);//7000
    }

    public void nextActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
