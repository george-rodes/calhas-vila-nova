package br.com.valterdiascalhas.orcamentos;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.TextView;

public class ClienteActivity extends AppCompatActivity implements ClienteDialogUpdate.Communicator {
    DBAdapter dbAdapter;
    ArrayAdapter<String> adapterApelido, adapterNome, adapterCidade, adapterBairro, adapterRua;
    AutoCompleteTextView etNome, etApelido, etRua, etBairro, etCidade;
    Boolean avisadoPorFoco, avisadoPorClique, entrouPorIntentEditar; //que o Cliente já existe
    Button btSalvarCliente;
    Cursor cursor, c;
    EditText etFone, etEmail;
    ImageView ivAlert;
    SQLiteDatabase sqLiteDatabase;
    String apelido, newNome, newFone, newEmail, newRua, newBairro, newCidade, cliente, id, origem, str, orcamento_id;
    TextView tvApelidoMatch;
    boolean answer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);
        dbAdapter = new DBAdapter(ClienteActivity.this);
        sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();
        Toolbar toolbar = (Toolbar) findViewById(R.id.cliente_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.novo_cliente_toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        avisadoPorClique = false;
        avisadoPorFoco = false;
        entrouPorIntentEditar = false;

        /** Views */
        etApelido = (AutoCompleteTextView) findViewById(R.id.etApelido);
        tvApelidoMatch = (TextView) findViewById(R.id.tvApelidoMatch);
        tvApelidoMatch.setText("");
        ivAlert = (ImageView) findViewById(R.id.ivAlert);
        ivAlert.setVisibility(View.INVISIBLE);
        etNome = (AutoCompleteTextView) findViewById(R.id.etNome);
        etFone = (EditText) findViewById(R.id.etFone);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etRua = (AutoCompleteTextView) findViewById(R.id.etRua);
        etBairro = (AutoCompleteTextView) findViewById(R.id.etBairro);
        etCidade = (AutoCompleteTextView) findViewById(R.id.etCidade);
        btSalvarCliente = (Button) findViewById(R.id.btSalvarCliente);
        /** Adapters */
        adapterApelido = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dbAdapter.retrieveAllApelidoDeClientes());
        adapterNome = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dbAdapter.retrieveAllNomeDeClientes());
        adapterCidade = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.cidade));
        adapterBairro = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dbAdapter.retrieveAllBairros());
        adapterRua = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dbAdapter.retrieveAllRuas());
        /** Set Adapters */
        etApelido.setAdapter(adapterApelido);
        etNome.setAdapter(adapterNome);
        etRua.setAdapter(adapterRua);
        etBairro.setAdapter(adapterBairro);
        etCidade.setAdapter(adapterCidade);

        origem = getIntent().getStringExtra("Origem");
        //L.t(this, "Cliente Activity : " + origem);
        //L.m("Cliente Activity : " + origem);

        /** pode vir de tres fontes, do
         * fragment_client_list,
         * Toolbar novo cliente, ou do formulario para incluir
         * novo orcamento a onde deve retornar **/


        if (origem.equals("MainActivity")) {
            getSupportActionBar().setTitle(R.string.novo_cliente_toolbar);
            btSalvarCliente.setText(getResources().getString(R.string.incluir));
            //depois de acabar aqui vai para OrcamentoAcitivity
        } else if (origem.equals("OrcamentoActivity")) {
            getSupportActionBar().setTitle(R.string.novo_cliente_toolbar);
            btSalvarCliente.setText(getResources().getString(R.string.incluir));
            apelido = getIntent().getStringExtra("apelido");
            if (apelido != null) {
                etApelido.setText(apelido);
            }

        } else if (origem.equals("ClienteAdapterEditIcon") ||
                origem.equals("SearchResultEditIcon") ||
                origem.equals("ItemOrcamentoActivity")) {
            apelido = getIntent().getStringExtra("apelido");
            etApelido.setText(apelido);
            avisadoPorClique = true;
            avisadoPorFoco = true;
            entrouPorIntentEditar = true;
            (c = dbAdapter.retrieveCliente(apelido)).moveToNext();
            preencherCampos(c);
            getSupportActionBar().setTitle(R.string.cadastro_de_cliente);
        }

        if (origem.equals("ItemOrcamentoActivity")) {
            //vai e volta
            orcamento_id = getIntent().getStringExtra("orcamento_id");

        }


        /** TextChangedListener  */
        etApelido.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                Cursor c = dbAdapter.checkIfClienteApelidoMatches(s.toString());
                if (c.moveToNext() && s.length() > 0) {
                    etApelido.setTextColor(Color.RED);
                    String str = "Nome Parecido: " + c.getString(c.getColumnIndex("apelido"));
                    tvApelidoMatch.setTextColor(Color.RED);
                    avisaSimilaridade(str);
                    getSupportActionBar().setTitle(R.string.cadastro_de_cliente);
                } else {
                    etApelido.setTextColor(Color.BLACK);
                    getSupportActionBar().setTitle(R.string.novo_cliente_toolbar);
                    apagaSimilaridade();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        etNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (dbAdapter.checkIfClienteNomeExists(s.toString()).moveToNext()) {
                    etNome.setTextColor(Color.RED);
                } else etNome.setTextColor(Color.BLACK);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        etApelido.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    /** entrando */
                    str = etApelido.getText().toString();
                    avisadoPorClique = false;
                    avisadoPorFoco = false;
                } else {
                    /** saindo */
                    if (!avisadoPorClique || !str.equals(etApelido.getText().toString())) {
                        avisadoPorFoco = true;
                        oldVsNewCliente();
                    }
                }
            }
        });

        etApelido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!avisadoPorFoco) {
                    avisadoPorClique = true;
                    /** para verificar se houve digitacao no apelido*/
                    str = etApelido.getText().toString();
                    oldVsNewCliente();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cadastro_cliente, menu);
        return true;
    }


    public void inserirRua(String r) {
        String rua;
        /** find a comma and filter the number
         * */
        if (r.indexOf(",") > 5) {
            rua = r.substring(0, r.indexOf(","));
            if (dbAdapter.insertRua(rua)) {
                L.t(getApplicationContext(), rua + " adicionada");
            }
        } else {
            if (dbAdapter.insertRua(r)) {
                L.t(getApplicationContext(), r + " adicionada");
            }
        }
    }

    public void inserirBairro(String b) {
        if (dbAdapter.insertBairro(b)) {
            L.t(getApplicationContext(), b + " adicionado");
        }
    }

    public void cliqueIncluirSalvar(View view) {
        FragmentManager fm = getSupportFragmentManager();
        ClienteDialogUpdate dialog = new ClienteDialogUpdate();
        Bundle args = new Bundle();

        apelido = etApelido.getText().toString();
        newNome = etNome.getText().toString();
        newFone = etFone.getText().toString();
        newEmail = etEmail.getText().toString();
        newRua = etRua.getText().toString();
        newBairro = etBairro.getText().toString();
        newCidade = etCidade.getText().toString();

        args.putString("apelido", apelido);
        args.putString("nome", newNome);
        args.putString("fone", newFone);
        args.putString("email", newEmail);
        args.putString("rua", newRua);
        args.putString("bairro", newBairro);
        args.putString("cidade", newCidade);

        if (!(apelido.isEmpty() || newNome.isEmpty())) {

            if ((c = dbAdapter.retrieveCliente(apelido)).moveToNext()) {
                /** Cliente já existe */

                String oldNome = c.getString(c.getColumnIndex(DBAdapter.DBHelper.NOME));
                String oldFone = c.getString(c.getColumnIndex(DBAdapter.DBHelper.FONE));
                String oldEmail = c.getString(c.getColumnIndex(DBAdapter.DBHelper.EMAIL));
                String oldRua = c.getString(c.getColumnIndex(DBAdapter.DBHelper.RUA));
                String oldBairro = c.getString(c.getColumnIndex(DBAdapter.DBHelper.BAIRRO));
                String oldCidade = c.getString(c.getColumnIndex(DBAdapter.DBHelper.CIDADE));

                /** Passar O que mudaou ou nao mudou, * O itens em vermelho mudaram */
                args.putString("nomec", setColor(newNome, oldNome));
                args.putString("fonec", setColor(newFone, oldFone));
                args.putString("emailc", setColor(newEmail, oldEmail));
                args.putString("ruac", setColor(newRua, oldRua));
                args.putString("bairroc", setColor(newBairro, oldBairro));
                args.putString("cidadec", setColor(newCidade, oldCidade));
                args.putString("InsertOrUpdate", "Update");

                dialog.setArguments(args);
                /** FOR MARSHMALLOW ************/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    dialog.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_Alert);
                }
                dialog.show(fm, "Cliente");


            } else {
                /** Cliente novo */
                //Insert
                /** A cor não importa aqui, tudo é novo mesmo, e tudo com vermelho estressa  */
                args.putString("nomec", "Black");
                args.putString("fonec", "Black");
                args.putString("emailc", "Black");
                args.putString("ruac", "Black");
                args.putString("bairroc", "Black");
                args.putString("cidadec", "Black");
                args.putString("InsertOrUpdate", "Insert");

                dialog.setArguments(args);
                /** FOR MARSHMALLOW ************/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    dialog.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_Alert);
                }
                dialog.show(fm, "Cliente");

            }

        } else dialogoCamposVazios();

    }

    @Override
    public void onDialogMessage(String s) {
        //update or insert?
        //L.m(s);
        if (s.equals("Update")) {
            if (dbAdapter.updateCliente(new Cliente(apelido, newNome, newFone, newEmail, newRua, newBairro, newCidade, Utilidades.agora()))) {
                L.t(getApplicationContext(), "Cliente Atualizado");
                adapterRua.notifyDataSetChanged();
                adapterNome.notifyDataSetChanged();
                //falta atualizar tabelas bairro, e rua sem a virgula e numero, filtrar
                inserirRua(newRua);
                inserirBairro(newBairro);

                if (origem.equals("SearchResultEditIcon") || origem.equals("ItemOrcamentoActivity")) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("apelido", apelido);
                    returnIntent.putExtra("orcamento_id", orcamento_id);
                    setResult(RESULT_OK, returnIntent);
                }

                finish();
            }
        } else if (s.equals("Insert")) {
            if (dbAdapter.insertCliente(new Cliente(apelido, newNome, newFone, newEmail, newRua, newBairro, newCidade, Utilidades.agora()))) {
                L.t(getApplicationContext(), "Cliente Cadastrado");
                adapterRua.notifyDataSetChanged();
                adapterApelido.notifyDataSetChanged();
                adapterNome.notifyDataSetChanged();
                inserirRua(newRua);
                inserirBairro(newBairro);
                //where did I come from?
                if (origem.equals("OrcamentoActivity")) {
                    //depois volta com resultado
                    //auqui precisa diferenciar se veio de uma nova icone
                    // quando vem da Main Activity deveria ir para um novo orçamento
                    //ja com o nome
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("apelido", apelido);
                    setResult(RESULT_OK, returnIntent);
                    finish();

                } else if (origem.equals("MainActivity")) {
                    Intent intent = new Intent(this, OrcamentoActivity.class);
                    intent.putExtra("apelido", apelido);
                    intent.putExtra("Origem", "ClienteActivity");
                    startActivity(intent);
                    finish();

                } else {
                    finish();
                }
            }
        }
    }

    public void apagueApelido(View view) {
        limparCampos();
        etApelido.setText("");
    }

    public String setColor(String novo, String old) {
        if (novo.equals(old)) return "Black";
        else return "Red";
    }

    public void oldVsNewCliente() {
        c = dbAdapter.checkIfClienteApelidoExists(etApelido.getText().toString());
        if (c.moveToNext()) {
            if (!entrouPorIntentEditar) dialogoClienteExiste();
            entrouPorIntentEditar = false;
            preencherCampos(c);
            btSalvarCliente.setText(getResources().getString(R.string.salvar));
        } else {
            btSalvarCliente.setText(getResources().getString(R.string.incluir));
            limparCampos();
        }
    }

    private void preencherCampos(Cursor c) {
        etNome.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.NOME)));
        etFone.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.FONE)));
        etEmail.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.EMAIL)));
        etRua.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.RUA)));
        etBairro.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.BAIRRO)));
        etCidade.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.CIDADE)));
        apagaSimilaridade();
    }

    public void apagaSimilaridade() {
        tvApelidoMatch.setText("");
        ivAlert.setVisibility(View.INVISIBLE);
    }

    public void avisaSimilaridade(String str) {
        tvApelidoMatch.setText(str);
        ivAlert.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("orcamento_id", orcamento_id); //vai e vem, nullPointerException
            setResult(RESULT_CANCELED, returnIntent);
            finish();
            return true;
        }

        if (id == R.id.deleteCliente) {
            final String str = etApelido.getText().toString();
            if (!str.isEmpty()) {
                if (dbAdapter.findFirstCliente(str)) {
                    //find first Cliente, find firt orçamento
                    if (!dbAdapter.findFirstOrcamento(str)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ClienteActivity.this);
                        builder.setMessage(R.string.confirma_exluir_cliente);
                        builder.setCancelable(true);
                        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (dbAdapter.deleteCliente(str) > 0) {
                                    L.t(getApplicationContext(), "Cliente se foi");
                                    etApelido.setText("");
                                    limparCampos();
                                    resetAdapters();
                                    dialog.cancel();
                                    startActivityVoltaParaCasa();

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
                        L.t(getApplicationContext(), "Cliente possui orcamentos");
                        dialogoClientePossuiOrcmantos();

                    }
                } else L.t(getApplicationContext(), "Cliente não existe");
            }
        }
        return true;
    }

    public void startActivityVoltaParaCasa() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void dialogoClienteExiste() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClienteActivity.this);
        builder.setMessage(R.string.cliente_ja_existe);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dialogoClientePossuiOrcmantos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClienteActivity.this);
        builder.setMessage(R.string.cliente_possui_orcamentos);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dialogoCamposVazios() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClienteActivity.this);
        builder.setMessage(R.string.campos_vazios);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void resetAdapters() {
        /** Adapters */
        adapterApelido.clear();
        adapterNome.clear();
        adapterApelido = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dbAdapter.retrieveAllApelidoDeClientes());
        adapterNome = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dbAdapter.retrieveAllNomeDeClientes());
        etApelido.setAdapter(adapterApelido);
        etNome.setAdapter(adapterNome);

    }

    private void limparCampos() {
        etNome.setText("");
        etFone.setText("");
        etEmail.setText("");
        etRua.setText("");
        etBairro.setText("");
        etCidade.setText("");
    }

    public void apagueCidade(View view) {
        etCidade.setText("");
    }

    public void apagueBairro(View view) {
        etBairro.setText("");
    }

    public void apagueRua(View view) {
        etRua.setText("");
    }

    public void apagueEmail(View view) { etEmail.setText(""); }

    public void apagueFone(View view) {
        etFone.setText("");
    }

    public void apagueNome(View view) {
        etNome.setText("");
    }
}
