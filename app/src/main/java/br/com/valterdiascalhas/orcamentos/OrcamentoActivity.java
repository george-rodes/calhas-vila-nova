package br.com.valterdiascalhas.orcamentos;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.app.DatePickerDialog;

import java.util.Calendar;

public class OrcamentoActivity extends AppCompatActivity {
    DBAdapter dbAdapter;
    SQLiteDatabase sqLiteDatabase;
    Toolbar toolbar;
    String origem, str, orcamento_id, apelido;
    AutoCompleteTextView etApelido, etRua, etBairro, etCidade;
    ArrayAdapter adapterApelido, adapterCidade, adapterBairro, adapterRua;
    Boolean avisadoPorFoco;
    Cursor c;
    EditText etDate, etValor,etObs;
    Button btSalvarCabecalho, btNovoCliente,btclearObsOrcamento;
    int mYear, mMonth, mDay;
    static final int INCLUIR_NOVO_CLIENTE = 1;  // The request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orcamento);
        dbAdapter = new DBAdapter(OrcamentoActivity.this);
        sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();
        toolbar = (Toolbar) findViewById(R.id.orcamento_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Novo Orçamento");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btclearObsOrcamento = (Button) findViewById(R.id.clearObsOrcamento);
        btNovoCliente = (Button) findViewById(R.id.novo_cliente_orcamento);
        btNovoCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                novoCliente();
            }
        });

        etDate = (EditText) findViewById(R.id.etDate);
        etApelido = (AutoCompleteTextView) findViewById(R.id.etApelidoOrcamento);
        etRua = (AutoCompleteTextView) findViewById(R.id.etRuaOrcamento);
        etBairro = (AutoCompleteTextView) findViewById(R.id.etBairroOrcamento);
        etCidade = (AutoCompleteTextView) findViewById(R.id.etCidadeOrcamento);
        etValor = (EditText) findViewById(R.id.etValorTotal);
        etObs = (EditText) findViewById(R.id.etObsOrcamento);
        btSalvarCabecalho = (Button) findViewById(R.id.btSalvarCabecalho);
        orcamento_id = "00";

        adapterApelido = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dbAdapter.retrieveAllApelidoDeClientes());
        adapterCidade = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.cidade));
        adapterBairro = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dbAdapter.retrieveAllBairros());
        adapterRua = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dbAdapter.retrieveAllRuas());

        etApelido.setAdapter(adapterApelido);
        etRua.setAdapter(adapterRua);
        etBairro.setAdapter(adapterBairro);
        etCidade.setAdapter(adapterCidade);

        etApelido.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    /** entrando */
                    str = etApelido.getText().toString();
                } else {
                    if (!dbAdapter.checkIfClienteApelidoExists(etApelido.getText().toString()).moveToNext() &&
                            !etApelido.getText().toString().isEmpty()) {
                        dialogoNovoCliente();
                    }
                }
            }
        });

        etDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                /* saindo */
                if (!b) {
                    if (!Utilidades.validarData(etDate.getText().toString())) {
                        dialogoDataInvalida();
                        etDate.setTextColor(Color.RED);
                    } else {
                        etDate.setTextColor(Color.BLACK);
                    }
                }
            }
        });

        etValor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (Utilidades.decNumStr(etValor.getText().toString(), getApplicationContext()).equals("Error")) {
                        L.t(getApplicationContext(), "Error");
                    }
                }
            }
        });

        /** 1. Novo Orcamento,
         * 2. Editar Cabecalho, carregar os campos
         * pedir dialogo de confirmacao
         * se confirmar ir para ItemOrcamentoActivity para incluir itens
         * */
        origem = getIntent().getStringExtra("Origem");
        L.m("OrçamentoActivity : Origem " + origem);
        if (origem.equals("ItemOrcamentoActivityEditarCabecalho")) {
            orcamento_id = getIntent().getStringExtra("orcamento_id");
            preencheCabecalho(orcamento_id);
            btSalvarCabecalho.setText(R.string.salvar);
        } else if (origem.equals("ClienteActivity") ||
                origem.equals("ClienteAdapterAddOrcamentoIcon") ||
                origem.equals("SearchResults") ||
                origem.equals("ItemOrcamentoActivityNovoOrcamento")) {
            //temos o cliente
            etApelido.setText(getIntent().getStringExtra("apelido"));
            buscaDataHoje();
            btSalvarCabecalho.setText(R.string.salvar_e_incluir_material);
        } else if (origem.equals("MainActivity")) {
            apelido = getIntent().getStringExtra("apelido");
            //verificar se o apelido é um cliente valido
            if ((c = dbAdapter.checkIfClienteApelidoExists(apelido)).moveToNext()) {
                etApelido.setText(apelido);
            }

            buscaDataHoje();
            btSalvarCabecalho.setText(R.string.salvar_e_incluir_material);



        }

        btclearObsOrcamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etObs.setText("");
            }
        });

    }

    public void preencheCabecalho(String orcamento_id) {
        c = dbAdapter.retrieveOrcamento(orcamento_id);
        if (c.moveToNext()) {
            etApelido.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.NOME)));
            etDate.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.DATA)));
            etRua.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.RUA)));
            etBairro.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.BAIRRO)));
            etCidade.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.CIDADE)));
            etObs.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.OBS)));
            etValor.setText(Utilidades.DividaPorCem(c.getInt(c.getColumnIndex(DBAdapter.DBHelper.TOTAL))));

        }

    }

    public void cliqueIncluirSalvar(View view) {
        String apelido = etApelido.getText().toString();
        int orcamento, ano, mes, dia;
        String nome, data, rua, bairro, cidade, observacao;
        int total;

        /**
         * Controlel
         * requisitos minimos:
         * 1. Apelido não Vazio
         * 2. Apelido existe, se não existr será gravado
         * 3. Data e valida
         * 4 Valor Total válido
         **/
        if (!apelido.isEmpty()) {
            if (dbAdapter.booleanClienteApelidoExists(apelido)) {
                if (Utilidades.validarData(etDate.getText().toString())) {
                    if (!Utilidades.decNumStr(etValor.getText().toString(), getApplicationContext()).equals("Error")) {
                        /**check if INSERT or UPDATE*/
                        if ((c = dbAdapter.retrieveOrcamento(orcamento_id)).moveToNext()) {
                            /** UPDATE **/
                            /**
                             * int orcamento, String nome, String data, int ano, int mes, int dia,
                             * String rua, String bairro, String cidade, float total, String observacao
                             **/

                            orcamento = c.getInt(c.getColumnIndex(DBAdapter.DBHelper.ORCAMENTO));
                            DiaMesAno d = new DiaMesAno(etDate.getText().toString());
                            nome = etApelido.getText().toString();
                            data = etDate.getText().toString();
                            ano = d.getAno();
                            mes = d.getMes();
                            dia = d.getDia();
                            rua = etRua.getText().toString();
                            bairro = etBairro.getText().toString();
                            inserirBairro(bairro);
                            inserirRua(rua);
                            cidade = etCidade.getText().toString();

                            String stotal = Utilidades.decNumStr(etValor.getText().toString(), getApplicationContext());
                            total = (int) (Float.parseFloat(stotal) * 100);
                            observacao = etObs.getText().toString();

                            Orcamento mOrcamento = new Orcamento(orcamento, nome, data, ano, mes, dia, rua, bairro, cidade, total, observacao);
                            /**L.m("\n"+orcamento+"\n"+nome+"\n"+data+"\n"+ano+"\n"+mes+"\n"+dia+"\n"+rua+"\n"+bairro+"\n"+cidade+"\n"+total+"\n"+observacao);*/

                            dbAdapter.updateOrcamento(mOrcamento);
                            //retorna para ItemOrcamentoActivity
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("orcamento_id", orcamento_id);
                            //test purpose
                            returnIntent.putExtra("eu vim de", " Orcamento Activity como returnIntert");
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        } else {
                            /** INSERT **/
                            orcamento_id = String.valueOf(dbAdapter.giveMeNextOrcamento());
                            DiaMesAno d = new DiaMesAno(etDate.getText().toString());
                            nome = etApelido.getText().toString();
                            data = etDate.getText().toString();
                            ano = d.getAno();
                            mes = d.getMes();
                            dia = d.getDia();
                            rua = etRua.getText().toString();
                            bairro = etBairro.getText().toString();
                            inserirBairro(bairro);
                            inserirRua(rua);
                            cidade = etCidade.getText().toString();

                            String stotal = Utilidades.decNumStr(etValor.getText().toString(), getApplicationContext());
                            total = (int) (Float.parseFloat(stotal) * 100);
                            observacao = etObs.getText().toString();

                            Orcamento mOrcamento = new Orcamento(dbAdapter.giveMeNextOrcamento(), nome, data, ano, mes, dia, rua, bairro, cidade, total, observacao);
                            /**L.m("\n"+orcamento+"\n"+nome+"\n"+data+"\n"+ano+"\n"+mes+"\n"+dia+"\n"+rua+"\n"+bairro+"\n"+cidade+"\n"+total+"\n"+observacao);*/

                            if (dbAdapter.insertOrcamento(mOrcamento)) {
                                L.t(this, getString(R.string.orcamento_cadastrado));
                                //intent para ItemOrcamentoActivity para acrescentar novos itens
                                //passar o cabecalho
                                Intent intent = new Intent(this, ItemOrcamentoActivity.class);
                                intent.putExtra("orcamento", orcamento_id);
                                intent.putExtra("cliente", nome);
                                intent.putExtra("data", data);
                                intent.putExtra("rua", rua);
                                intent.putExtra("bairro", bairro);
                                intent.putExtra("cidade", cidade);
                                intent.putExtra("observacao", observacao);
                                intent.putExtra("total",etValor.getText().toString());
                                startActivity(intent);
                                finish();
                            } else {
                                L.t(this, getString(R.string.orcamento_nao_cadastrado));
                            }
                        }
                    } else {
                        dialogoValorInvalido();
                    }
                    etDate.setTextColor(Color.BLACK);
                } else {
                    dialogoDataInvalida();
                    etDate.setTextColor(Color.RED);
                }
            } else dialogoNovoCliente();
        } else dialogoApelidoVazio();
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

        if (id == R.id.deleteOrcamento) {
            //there is an orcamento
            if ((c = dbAdapter.retrieveOrcamento(orcamento_id)).moveToNext()) {
                //has no children
                final String cliente = c.getString(c.getColumnIndex(DBAdapter.DBHelper.NOME));
                if (!(dbAdapter.retrieveItemOrcamento(orcamento_id)).moveToNext()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OrcamentoActivity.this);
                    builder.setMessage(R.string.confirma_exluir_orcamento);
                    builder.setCancelable(true);
                    builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (dbAdapter.deleteOrcamento(orcamento_id) > 0) {
                                L.t(getApplicationContext(), getString(R.string.orcamento_excluido));
                                limparCampos();
                                voltaParaCasa(cliente);
                                dialog.cancel();
                                //voltar para o cliente em questao e passar para o intent

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
                    dialogoOrcamentoPossuiMaterial();

                }
            } else L.t(getApplicationContext(), getString(R.string.orcamento_nao_existe));

        }
        return true;
    }

    public void voltaParaCasa(String cliente) {
        Intent intent = new Intent(this, SearchResults.class);
        intent.putExtra("apelido", cliente);
        intent.putExtra("Origem", "OrcamentoActivity");
        startActivity(intent);
        finish();

    }

    public void dialogoDataInvalida() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrcamentoActivity.this);
        builder.setMessage(R.string.data_invalida);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dialogoValorInvalido() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrcamentoActivity.this);
        builder.setMessage(R.string.valor_total_invalido);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dialogoApelidoVazio() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrcamentoActivity.this);
        builder.setMessage(R.string.informar_apelido);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dialogoOrcamentoPossuiMaterial() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrcamentoActivity.this);
        builder.setMessage(R.string.orcamento_possui_material);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void buscaDataHoje() {
        final Calendar c = Calendar.getInstance();
        etDate.setText(c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR));
    }

    public void novaData(View v) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int ano,
                                          int mes, int dia) {

                        etDate.setText(dia + "/" + (mes + 1) + "/" + ano);
                        etDate.setTextColor(Color.BLACK);
                        // usar para atualizar o banco aqui


                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void dialogoNovoCliente() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrcamentoActivity.this);
        builder.setMessage(R.string.cadastrar_novo_cliente);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.SIM, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                novoClienteSugerido();
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

    public void novoClienteSugerido() {
        //activity for result
        //se o usuario digitou um Nome desconhecido e sai navega vai aparecer um dialgo e le
        //only procede if client is new
        String s = etApelido.getText().toString();
        if (!dbAdapter.retrieveCliente(s).moveToNext()) {
            Intent intent = new Intent(this, ClienteActivity.class);
            intent.putExtra("Origem", "OrcamentoActivity");
            //se ocliente digitou algum Apelido que nao Existe
            intent.putExtra("apelido", s);
            startActivityForResult(intent, INCLUIR_NOVO_CLIENTE);
        } ;
    }

    public void novoCliente() {
            Intent intent = new Intent(this, ClienteActivity.class);
            intent.putExtra("Origem", "OrcamentoActivity");
            intent.putExtra("apelido", "");
            startActivityForResult(intent, INCLUIR_NOVO_CLIENTE);
    }


    public void apagueApelido(View view) {
        etApelido.setText("");
    }

    public void apagueData(View v) {
        etDate.setText("");
    }

    public void apagueRua(View v) {
        etRua.setText("");
    }

    public void apagueBairro(View v) {
        etBairro.setText("");
    }

    public void apagueCidade(View v) {
        etCidade.setText("");
    }

    public void apagueValorTotal(View v) {
        etValor.setText("");
    }

    public void limparCampos() {
        View v = getCurrentFocus();
        apagueApelido(v);
        apagueData(v);
        apagueRua(v);
        apagueBairro(v);
        apagueCidade(v);
        apagueValorTotal(v);
    }

    ;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == INCLUIR_NOVO_CLIENTE) {
                if (resultCode == RESULT_OK) {
                    adapterApelido.clear();
                    adapterApelido = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dbAdapter.retrieveAllApelidoDeClientes());
                    etApelido.setAdapter(adapterApelido);
                    etApelido.setText(data.getStringExtra("apelido"));
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_orcamento, menu);
        return true;
    }

}
