package br.com.valterdiascalhas.orcamentos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by George on 19/08/2016.
 */
public class MaterialAdapter extends ArrayAdapter<Material> {
    private Drawable mDrawable;
    String picturesPath;

    public MaterialAdapter(Context context, List<Material> materials) {
        super(context, 0, materials);
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        // Get the data item for this position
        Material material = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (row == null) {
            row = LayoutInflater.from(getContext()).inflate(R.layout.row_material, parent, false);
        }
        // Lookup view for data population
        TextView mMaterial = (TextView) row.findViewById(R.id.nomeCalha);
        TextView codigoMaterial = (TextView) row.findViewById(R.id.codigoCalha);
        ImageView imageViewModeloCalha = (ImageView) row.findViewById(R.id.imageViewModeloCalha);


        // Populate the data into the template view using the data object
        if (!material.getNome().isEmpty()) {
            mMaterial.setText(material.getNome());
        } else  mMaterial.setText("");
        codigoMaterial.setText(material.getCodigo());

        picturesPath = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/";
        //mDrawable = Drawable.createFromPath(picturesPath + material.getImagem());
        //imageViewModeloCalha.setImageDrawable(mDrawable);
        String imgDecodableString = picturesPath + material.getImagem();
        imageViewModeloCalha.setImageBitmap(decodeBitmap(imgDecodableString, 100,100));
        // Return the completed view to render on screen
        return row;
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

}
