package br.com.valterdiascalhas.orcamentos;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Material(String codigo, String nome, String materiaprima, String imagem, String obs)
 **/

public class MaterialAddActivity extends AppCompatActivity {
    DBAdapter dbAdapter;
    SQLiteDatabase sqLiteDatabase;
    AutoCompleteTextView etCodigo, etNome;
    EditText etObs;
    Button btSalvar, btCancelar, btUsadoEm, btEscolherImagem, btCamera;
    Drawable mDrawable;
    String picturesPath, str, origem, codigo, nome, materiaprima, imagem, imagemU, obs, imgDecodableString;
    ImageView imageViewModeloCalha;
    ArrayAdapter adapterMaterial, adapterNome;
    Intent i;
    Boolean avisadoPorFoco, avisadoPorClique, entrouPorIntentEditar; //que o Cliente já existe
    Cursor c;
    Bitmap mImageBitmap;
    static final int RESULT_LOAD_IMG = 401;
    static final int PIXELS_FOR_DISPLAY = 400;
    static final int COMPRESSAO_JPG = 70;
    static final int PIXELS_LARGURA = 512;
    static final int REQUEST_IMAGE_CAPTURE = 470;
    static final String BITMAP_STORAGE_KEY = "viewbitmap";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_add);
        dbAdapter = new DBAdapter(MaterialAddActivity.this);
        sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMaterial);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Material");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        avisadoPorClique = false;
        avisadoPorFoco = false;
        entrouPorIntentEditar = false;

        etCodigo = (AutoCompleteTextView) findViewById(R.id.etAddMaterial);
        etNome = (AutoCompleteTextView) findViewById(R.id.etAddNome);
        etObs = (EditText) findViewById(R.id.etObs);
        btSalvar = (Button) findViewById(R.id.btSalvarMaterial);
        btCancelar = (Button) findViewById(R.id.btCancelarMaterial);
        btUsadoEm = (Button) findViewById(R.id.btUsadoEm);
        btEscolherImagem = (Button) findViewById(R.id.btEscolherImagem);
        btCamera = (Button) findViewById(R.id.btCamera);
        imageViewModeloCalha = (ImageView) findViewById(R.id.ivModeloCalha);
        picturesPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/";
        //L.m(picturesPath);

        adapterMaterial = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dbAdapter.retrieveAllMaterial());
        etCodigo.setAdapter(adapterMaterial);
        adapterNome = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dbAdapter.retrieveADistinctNomeDeMaterial());
        etNome.setAdapter(adapterNome);

        mImageBitmap = null;
        i = getIntent();
        origem = i.getStringExtra("Origem");

        if (origem.equals("ItemAddDialogActivity")) {
            etCodigo.setText(i.getStringExtra("codigo"));
        } else if (origem.equals("MaterialListActivity")) {
            etCodigo.setText(i.getStringExtra("codigo"));
            c = dbAdapter.checkIfMaterialExists(etCodigo.getText().toString());
            if (c.moveToNext()) {
                preencherCampos(c);
            }
        }


        etCodigo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    /** entrando */
                    str = etCodigo.getText().toString();
                    avisadoPorClique = false;
                    avisadoPorFoco = false;
                } else {
                    /** saindo */
                    if (!avisadoPorClique || !str.equals(etCodigo.getText().toString())) {
                        avisadoPorFoco = true;
                        oldVsNewMAterial();
                    }
                }
            }
        });

        etCodigo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                Cursor c = dbAdapter.checkIfMaterialMatches(s.toString());
                if (c.moveToNext() && s.length() > 0) {
                    etCodigo.setTextColor(Color.RED);
                    getSupportActionBar().setTitle(R.string.cadastro_de_material);
                } else {
                    etCodigo.setTextColor(Color.BLACK);
                    getSupportActionBar().setTitle(R.string.novo_material);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        etCodigo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!avisadoPorFoco) {
                    avisadoPorClique = true;
                    /** para verificar se houve digitacao no Codigo*/
                    str = etCodigo.getText().toString();
                    oldVsNewMAterial();
                }
            }
        });

        imageViewModeloCalha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etCodigo.getText().toString().equals("")) {
                    imagem = etCodigo.getText().toString() + ".jpg";
                    Intent i = new Intent(MaterialAddActivity.this, PopupActivity.class);
                    i.putExtra("imagem", imagem);
                    startActivity(i);
                    //imageViewModeloCalha.setRotation(5);

                }
            }
        });

        imageViewModeloCalha.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!etCodigo.getText().toString().equals("")) {
                    float i = imageViewModeloCalha.getRotation();
                    i += 90;
                    imageViewModeloCalha.setRotation(i);

                }
                return true;
            }

        });
        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOrInsert();
            }
        });

        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnResultCancel();
            }
        });

        btUsadoEm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = etCodigo.getText().toString();
                if (!str.isEmpty()) {
                    c = dbAdapter.findFirstItemOrcamento(str);
                    if (c.moveToNext()) {
                        materialUsadoEm(str);
                    }
                }
            }
        });
        btEscolherImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //verificar se existe um registro, ao escolher a imagem, teremos uma copia com
                // o nome da imagem
                if (!etCodigo.getText().toString().equals("")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MaterialAddActivity.this);
                    builder1.setMessage(R.string.imagem_perdida);
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            //warning, picture will be overwritten
                            imagem = etCodigo.getText().toString() + ".jpg";
                            choosePictureIntent();
                        }
                    });
                    builder1.setNegativeButton("NÂO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            }
        });

        btCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //verificar se existe um registro, ao escolher a imagem, teremos uma copia com
                // o nome da imagem
                if (!etCodigo.getText().toString().equals("")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MaterialAddActivity.this);
                    builder1.setMessage(R.string.imagem_perdida);
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            //warning, picture will be overwritten
                            imagem = etCodigo.getText().toString() + ".jpg";
                            takePictureIntent();
                        }
                    });
                    builder1.setNegativeButton("NÂO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data && !imagem.equals("")) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                // Move to first row
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    copiaDaGaleriaAoAplicativo(imgDecodableString);
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && !imagem.equals("")) {
                if (imgDecodableString != null) {
                    galleryAddPic();
                    copiaDaGaleriaAoAplicativo(imgDecodableString);
                }
            }
        } catch (Exception e) {
            L.t(this, "Ocorreu um erro");
        }
    }

    public void copiaDaGaleriaAoAplicativo(String imagemFullPath) {
        Bitmap out = decodeAndScaleBitmap(imagemFullPath, PIXELS_FOR_DISPLAY, PIXELS_FOR_DISPLAY);
        ExifInterface exifInterface;
        Matrix matrix = new Matrix();
        int orientation;

        try {
            exifInterface = new ExifInterface(imagemFullPath);
            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            if (orientation != ExifInterface.ORIENTATION_NORMAL) {
                if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                    matrix.setRotate(90);
                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                    matrix.setRotate(180);
                }
                out = Bitmap.createBitmap(out, 0, 0, out.getWidth(), out.getHeight(), matrix, true);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        // diretorio interno do aplicativo
        File sd = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        //Bitmap out = decodeAndScaleBitmap(imagemFullPath, PIXELS_FOR_DISPLAY, PIXELS_FOR_DISPLAY);

        File file = new File(sd, imagem);
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            imagemFullPath = file.getAbsolutePath();
            out.compress(Bitmap.CompressFormat.JPEG, COMPRESSAO_JPG, fOut);
            fOut.flush();
            fOut.close();
            out.recycle();

        } catch (Exception e) {
            // L.m(e.toString());
        }
        imageViewModeloCalha.setImageBitmap(decodeBitmap(imagemFullPath, PIXELS_FOR_DISPLAY, PIXELS_FOR_DISPLAY));

    }

    // Copiar para o meu App
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(imgDecodableString);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    public static Bitmap decodeAndScaleBitmap(String imgDecodableString, int reqWidth, int reqHeight) {
        int largura = PIXELS_LARGURA;
        int altura;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        /** inJustDecodeBounds: If set to true, the decoder will return null (no bitmap),
         * but the out... fields will still be set, allowing the caller to query the bitmap
         * without having to allocate the memory for its pixels.
         * */
        options.inJustDecodeBounds = true;
        //L.m("before " + options.outHeight);
        BitmapFactory.decodeFile(imgDecodableString, options);
        //L.m("after " + options.outHeight);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap b = BitmapFactory.decodeFile(imgDecodableString, options);
        //verficar options relacao atlura/largura e definir nova altura/largura de acordo
        float relacao = (float) options.outHeight / options.outWidth;
        altura = (int) (relacao * largura);
        return Bitmap.createScaledBitmap(b, largura, altura, false);
    }

    public void setImage(String image) {
        picturesPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/";
        imgDecodableString = picturesPath + image;
        imageViewModeloCalha.setImageBitmap(decodeBitmap(imgDecodableString, PIXELS_FOR_DISPLAY, PIXELS_FOR_DISPLAY));
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        // L.m("calculateInSampleSize largura " + width + " altura " + height);
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

    public void materialUsadoEm(String codigo) {
        Cursor c = dbAdapter.buscaOrcamentosDoMaterial(codigo);
        StringBuilder sb = new StringBuilder();
        int counter;
        counter = 0;

        sb.append("Está sendo usado em ");
        sb.append(c.getCount());
        if (c.getCount() > 1) sb.append(" orçamentos.\nPor exemplo:\n");
        else sb.append(" orçamento.\nSendo:\n");

        while (c.moveToNext() && counter < 10) {
            sb.append("Cliente ");
            sb.append(c.getString(0));
            sb.append(", de ");
            sb.append(c.getString(1));
            sb.append("\n");
            counter++;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MaterialAddActivity.this);
        builder.setMessage(sb.toString());
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void updateOrInsert() {
        String s = etCodigo.getText().toString();
        if (dbAdapter.booleanCheckIfMaterialExists(s)) {
            updateMaterial(s);
        } else {
            insertMaterial();
        }
    }

    public void insertMaterial() {
        codigo = etCodigo.getText().toString();
        nome = etNome.getText().toString();
        materiaprima = "Galvanizado 050";
        imagem = codigo + ".jpg";

        obs = etObs.getText().toString();
        if (!codigo.isEmpty()) {
            if (!dbAdapter.booleanCheckIfMaterialExists(codigo)) {
                if (!nome.equals("")) {
                    Material m = new Material(codigo, nome, materiaprima, imagem, obs);
                    if (dbAdapter.insertMaterial(m)) {
                        L.t(this, "Material Cadastrado");
                        returnResultOk();
                    } else {
                        L.t(this, "Material NÃO foi Cadastrado");

                    }
                } else dialogoInformarNome();
            } else dialogMaterialJaCadastrado();

        } else L.t(this, getString(R.string.nada_a_salvar));
    }

    public void updateMaterial(String codigo) {
        nome = etNome.getText().toString();
        materiaprima = "Galvanizado 050";
        imagem = codigo + ".jpg";
        obs = etObs.getText().toString();

        //if (!dbAdapter.booleanCheckIfMaterialExists(codigo)) {
        if (!nome.equals("")) {
            Material m = new Material(codigo, nome, materiaprima, imagem, obs);
            if (dbAdapter.updateMaterial(m)) {
                L.t(this, "Material Cadastrado");
                returnResultOk();
            } else {
                L.t(this, "Material NÃO foi Cadastrado");

            }
        } else dialogoInformarNome();
        // } else dialogMaterialJaCadastrado();
    }

    public void oldVsNewMAterial() {
        c = dbAdapter.checkIfMaterialExists(etCodigo.getText().toString());
        if (c.moveToNext()) {
            if (!entrouPorIntentEditar) dialogMaterialJaCadastrado();
            entrouPorIntentEditar = false;
            preencherCampos(c);
            btSalvar.setText(getResources().getString(R.string.salvar));
        } else {
            btSalvar.setText(getResources().getString(R.string.incluir));
            limparCampos();
        }
    }

    public void preencherCampos(Cursor c) {
        etCodigo.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.CODIGO)));
        etNome.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.NOME)));
        etObs.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.OBS)));
        setImage(c.getString(c.getColumnIndex(DBAdapter.DBHelper.IMAGEM)));
    }

    public void limparCampos() {
        etNome.setText("");
        etObs.setText("");
        mDrawable = Drawable.createFromPath(picturesPath + "material.jpg");
    }

    public void choosePictureIntent() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    /**
     * Notice that the startActivityForResult() method is protected by a condition that calls resolveActivity(),
     * which returns the first activity component that can handle the intent.
     * Performing this check is important because if you call startActivityForResult()
     * using an intent that no app can handle, your app will crash.
     * So as long as the result is not null, it's safe to use the intent.
     */
    private void takePictureIntent() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (i.resolveActivity(getPackageManager()) != null) {
            //copiar para um lugar accesivel pela galeria
            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File f = new File(sd, imagem);
            imgDecodableString = f.getAbsolutePath();
            i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));//antes do retorno grava o arquivo
            startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_material_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            returnResultCancel();
            return true;
        }

        if (id == R.id.deleteMaterial) {
            final String str = etCodigo.getText().toString();
            if (!str.isEmpty()) {
                c = dbAdapter.findFirstItemOrcamento(str);

                if (!c.moveToNext()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MaterialAddActivity.this);
                    builder.setMessage("Confirma a exclusão do Material?");
                    builder.setCancelable(true);
                    builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (dbAdapter.deleteMaterial(str) > 0) {
                                L.t(getApplicationContext(), "Material se foi");
                                etCodigo.setText("");
                                ///limparCampos();
                                //resetAdapters();
                                dialog.cancel();
                                returnResultOk();

                            } else dialog.cancel();
                        }
                    });
                    builder.setNegativeButton("NÂO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }
                    );
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {

                    dialogoMaterialUsado(str);

                }

            }
        }
        return true;
    }

    public void dialogoInformarNome() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MaterialAddActivity.this);
        builder.setMessage(R.string.informar_nome);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dialogoMaterialUsado(String codigo) {
        Cursor c = dbAdapter.buscaOrcamentosDoMaterial(codigo);
        StringBuilder sb = new StringBuilder();
        int counter;
        counter = 0;

        sb.append("Material NÃO pode ser exluido!\nEstá sendo usado no mínimo em ");
        sb.append(c.getCount());
        if (c.getCount() > 1) sb.append(" orçamentos.\nPor Exemplo:\n");
        else sb.append(" orçamento.\nSendo:\n");

        while (c.moveToNext() && counter < 6) {
            sb.append("Cliente ");
            sb.append(c.getString(0));
            sb.append(", de ");
            sb.append(c.getString(1));
            sb.append("\n");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MaterialAddActivity.this);
        builder.setMessage(sb.toString());
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dialogMaterialJaCadastrado() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MaterialAddActivity.this);
        builder.setMessage(R.string.material_ja_cadastrado);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void returnResultOk() {
        String s = etCodigo.getText().toString();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("codigo", s);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public void returnResultCancel() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("codigo", "");
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public void startActivityVoltaParaCasa() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void apagueObs(View view) {
        etObs.setText("");
    }

    public void apagueNome(View view) {
        etNome.setText("");
    }

    public void apagueMaterial(View view) {
        etCodigo.setText("");
    }

    public void resetAdapters() {
        /** Adapters */
        adapterMaterial.clear();
        adapterNome.clear();
        adapterMaterial = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dbAdapter.retrieveAllMaterial());
        etCodigo.setAdapter(adapterMaterial);
        adapterNome = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dbAdapter.retrieveADistinctNomeDeMaterial());
        etNome.setAdapter(adapterNome);

    }
}
