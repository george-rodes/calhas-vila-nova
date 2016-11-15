package br.com.valterdiascalhas.orcamentos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class PopupActivity extends AppCompatActivity {
    ImageView ivImage;
    String picturesPath;
    final static int PIXELS_FOR_DISPLAY = 512;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPopUp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Imagem");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivImage = (ImageView) findViewById(R.id.ivImagem);

        Intent i = getIntent();
        if (i.hasExtra("imagem")) {
            String imagem = i.getStringExtra("imagem");
            picturesPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/";
            imagem = picturesPath + imagem;
            ivImage.setImageBitmap(decodeBitmap(imagem, PIXELS_FOR_DISPLAY, PIXELS_FOR_DISPLAY));


        }
        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float i = ivImage.getRotation();
                i += 90;
                ivImage.setRotation(i);
            }
        });

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeBitmap(String imgDecodableString, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgDecodableString, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgDecodableString, options);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Intent intent = new Intent(this,ItemOrcamentoActivity.class);
            // startActivity(intent);
            finish();
            return true;
        }

        return true;
    }


}
