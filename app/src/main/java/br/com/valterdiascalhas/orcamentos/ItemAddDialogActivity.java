package br.com.valterdiascalhas.orcamentos;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

public class ItemAddDialogActivity extends AppCompatActivity {
    AutoCompleteTextView etMaterial;
    EditText etMetro, etPrecoMetro;
    String material, metros, precometro, str, orcamento_id, material_do_orcamento;
    ArrayAdapter adapterMaterial;
    SQLiteDatabase sqLiteDatabase;
    DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_add_dialog);
        dbAdapter = new DBAdapter(ItemAddDialogActivity.this);
        sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();

        etMaterial = (AutoCompleteTextView) findViewById(R.id.etMaterial);
        etMetro = (EditText) findViewById(R.id.etMetro);
        etPrecoMetro = (EditText) findViewById(R.id.etRealMetro);

        adapterMaterial = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dbAdapter.retrieveAllMaterial());
        etMaterial.setAdapter(adapterMaterial);

        Intent intent = getIntent();
        if (intent.hasExtra("orcamento_id")) {
            orcamento_id = getIntent().getStringExtra("orcamento_id");

        } else {
            orcamento_id = "0";
            L.t(this, getString(R.string.orcamento_nao_definido));
        }


        etMaterial.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    /** entrando */
                    str = etMaterial.getText().toString();
                } else {
                    if (!dbAdapter.checkIfMaterialExists(etMaterial.getText().toString()).moveToNext() &&
                            !etMaterial.getText().toString().isEmpty()) {
                        dialogoNovoMaterial();
                    }
                }
            }
        });
    }

    public void cliqueIncluirSalvar(View view) {
        /** Requsitos Minimos
         * Material existe, se nao existir chamar novoMaterial
         * metro e precometro obedecem as mesmas regras
         * valores gravados como centimetros e centavos
         *
         * */
        String observacao;
        int orcamento;
        long iMetros, iPrecometro;

        material = etMaterial.getText().toString();
        metros = Utilidades.decNumStr(etMetro.getText().toString(), getApplicationContext());
        precometro = Utilidades.decNumStr(etPrecoMetro.getText().toString(), getApplicationContext());


        if (!material.isEmpty()) {
            if (dbAdapter.booleanCheckIfMaterialExists(material)) {
                if (!metros.equals("Error")) {
                    if (!precometro.equals("Error")) {
                        if (!dbAdapter.booleanCheckIfItemOrcamentoExists(orcamento_id, material)) {
                            orcamento = Integer.parseInt(orcamento_id);
                            iMetros = (long) (Float.parseFloat(metros) * 100);
                            iPrecometro = (long) (Float.parseFloat(precometro) * 100);
                            observacao = Utilidades.agora();

                            if (dbAdapter.insertItemOrcamento(new ItemOrcamento(0, orcamento, material, iMetros, iPrecometro, observacao))) {
                                Intent returnIntent = new Intent();
                                setResult(RESULT_OK, returnIntent);
                                finish();
                            } else L.t(this, getString(R.string.item_nao_adicionado));
                        } else dialogoMaterialJaAdicionado();
                    } else dialogoPrecoMetroInvalido();
                } else dialogoMetroInvalido();
            } else dialogoNovoMaterial();
        } else dialogoMaterialVazio();
    }

    private void dialogoMaterialVazio() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemAddDialogActivity.this);
        builder.setMessage(R.string.informar_material);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void dialogoMaterialJaAdicionado() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemAddDialogActivity.this);
        builder.setMessage(R.string.materal_ja_adicionado);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dialogoMetroInvalido() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemAddDialogActivity.this);
        builder.setMessage(R.string.valor_metro_invalido);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dialogoPrecoMetroInvalido() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemAddDialogActivity.this);
        builder.setMessage(R.string.valor_precometro_invalido);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void apagueMetro(View view) {
        etMetro.setText("");
    }

    public void apagueRealMetro(View view) {
        etPrecoMetro.setText("");
    }

    public void apagueMaterial(View view) {
        etMaterial.setText("");
    }

    public void dialogoNovoMaterial() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemAddDialogActivity.this);
        builder.setMessage(R.string.cadastrar_novo_material);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.SIM, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                startActivityAddMaterial();
            }
        });
        builder.setNegativeButton(R.string.NAO, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void novoMaterial(View view) {
        startActivityAddMaterial();
    }

    static final int ADD_MATERIAL = 11314;

    public void startActivityAddMaterial() {
        Intent intent = new Intent(this, MaterialAddActivity.class);
        intent.putExtra("Origem", "ItemAddDialogActivity");
        intent.putExtra("codigo", etMaterial.getText().toString());
        // /vai receber o nome do material = codigo
        startActivityForResult(intent, ADD_MATERIAL);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == ADD_MATERIAL) {
                if (resultCode == RESULT_OK) {
                    etMaterial.setText(data.getStringExtra("codigo"));
                } else if (resultCode == RESULT_CANCELED) {
                    //nothing changed
                }
            }
        }
    }


    public void cliqueCancelar(View view) {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }
}
