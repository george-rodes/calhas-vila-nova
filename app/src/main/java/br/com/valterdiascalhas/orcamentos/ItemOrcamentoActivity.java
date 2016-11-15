package br.com.valterdiascalhas.orcamentos;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class ItemOrcamentoActivity extends AppCompatActivity implements DialogItemOrcamentoDetail.Communicator {
    DBAdapter dbAdapter;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor, c;
    String orcamento_id, data, rua, bairro, cidade, email, cabecalho, apelido, id, rodape,observacao;
    String material, total;
    ImageView ivDeleteOrcamento;
    ArrayList<ItemOrcamento> itens;
    ItemOrcamentoAdapter adapter;

    TextView itemOrderCabecalho;
    TextView itemOrcamentoFooter;

    private RecyclerView rv;
    static final int EDITAR_CABECALHO_ORCAMENTO = 114;
    static final int ADICIONAR_MATERIAL = 1724;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_orcamento);
        Toolbar toolbar = (Toolbar) findViewById(R.id.item_orcamento_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);
        itemOrderCabecalho = (TextView) findViewById(R.id.itemOrderCabecalho);
        itemOrcamentoFooter = (TextView) findViewById(R.id.itemOrcamentoFooter);
        ivDeleteOrcamento = (ImageView) findViewById(R.id.ivDeleteOrcamento);


        /** RV START **/
        rv = (RecyclerView) findViewById(R.id.itemOrderRV);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        /** RV END **/
        dbAdapter = new DBAdapter(ItemOrcamentoActivity.this);
        sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();
        itens = new ArrayList<>();

        Intent intent = getIntent();
        if (intent.hasExtra("orcamento")) {
            //L.m("intent.hasExtra(\"orcamento\")");
            /** falta Valor **/

            orcamento_id = intent.getStringExtra("orcamento");
            apelido = intent.getStringExtra("cliente");
            data = intent.getStringExtra("data");
            rua = intent.getStringExtra("rua");
            bairro = intent.getStringExtra("bairro");
            cidade = intent.getStringExtra("cidade");
            observacao = intent.getStringExtra("observacao");
            total = intent.getStringExtra("total");
            cabecalho = "Orçamento de " + data + ", Valor total: " + total +
                    "\nCliente " + apelido + "\n" +
                    "Local da Obra: " + rua + "\n" + bairro + ", " + cidade;
            itemOrderCabecalho.setText(cabecalho);
            carregarItensdoOrcamento();
            /** RV START **/
            adapter = new ItemOrcamentoAdapter(itens, this);
            rv.setAdapter(adapter);
            ItemTouchHelper.Callback callback = new SwipeHelper(adapter);
            ItemTouchHelper helper = new ItemTouchHelper(callback);
            helper.attachToRecyclerView(rv);
            /** RRV END **/
        } else {
            orcamento_id = "0";
            L.t(this, getString(R.string.orcamento_nao_definido));
        }

        ivDeleteOrcamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteOrcamento();
            }
        });
        itemOrderCabecalho.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (!observacao.isEmpty()) {
                    dialogoObservacao(observacao);
                }
                return false;
            }
        });
    }

    public void deleteOrcamento(){
        //there is an orcamento
        if ((c = dbAdapter.retrieveOrcamento(orcamento_id)).moveToNext()) {
            //has no children
            final String cliente = c.getString(c.getColumnIndex(DBAdapter.DBHelper.NOME));
            if (!(dbAdapter.retrieveItemOrcamento(orcamento_id)).moveToNext()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ItemOrcamentoActivity.this);
                builder.setMessage(R.string.confirma_exluir_orcamento);
                builder.setCancelable(true);
                builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (dbAdapter.deleteOrcamento(orcamento_id) > 0) {
                            L.t(getApplicationContext(), getString(R.string.orcamento_excluido));
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

    public void voltaParaCasa(String cliente) {
        Intent intent = new Intent(this, SearchResults.class);
        intent.putExtra("apelido", cliente);
        intent.putExtra("Origem", "ItemOrcamentoActivity");
        startActivity(intent);
        finish();

    }

    public void dialogoOrcamentoPossuiMaterial() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemOrcamentoActivity.this);
        builder.setMessage(R.string.orcamento_possui_material);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void dialogoObservacao(String s){
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemOrcamentoActivity.this);
        builder.setMessage("Observação\n" + s);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == EDITAR_CABECALHO_ORCAMENTO) {
                if (resultCode == RESULT_OK) {
                    preencherCabecalho(orcamento_id);
                } else if (resultCode == RESULT_CANCELED) {
                    //nothing changed
                }
            } else if (requestCode == ADICIONAR_MATERIAL) {
                carregarItensdoOrcamento();
                adapter.notifyDataSetChanged();
            } else if (requestCode == EDITAR_CLIENTE) {
                preencherCabecalho(data.getStringExtra("orcamento_id"));
            }
        }
    }

    public void preencherCabecalho(String orcamento_id) {
        Cursor c = dbAdapter.retrieveOrcamento(orcamento_id);
        if (c.moveToNext()) {
            cabecalho = "Orçamento de " + c.getString(c.getColumnIndex(DBAdapter.DBHelper.DATA))
                    + ", Valor total: " + Utilidades.DividaPorCemString(c.getString(c.getColumnIndex(DBAdapter.DBHelper.TOTAL)))
                    + "\nCliente " + c.getString(c.getColumnIndex(DBAdapter.DBHelper.NOME))
                    + "\n" + "Local da Obra: " + c.getString(c.getColumnIndex(DBAdapter.DBHelper.RUA))
                    + "\n" + c.getString(c.getColumnIndex(DBAdapter.DBHelper.BAIRRO))
                    + ", " + c.getString(c.getColumnIndex(DBAdapter.DBHelper.CIDADE));
            itemOrderCabecalho.setText(cabecalho);
            observacao = c.getString(c.getColumnIndex(DBAdapter.DBHelper.OBS));
        }
    }

    @Override
    public void onDialogMessage(long novoMetro, long novoPreco, int idItemOrc) {
        if (dbAdapter.updateItemOrcamento(Integer.toString(idItemOrc), novoMetro, novoPreco)) {
            L.t(this, "Orçamento Atualizado");
            carregarItensdoOrcamento();
            adapter.notifyDataSetChanged();
        }
    }

    public void carregarItensdoOrcamento() {
        c = dbAdapter.retrieveItemOrcamento(orcamento_id);
        itens.clear();
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                itens.add(new ItemOrcamento(c.getInt(0), c.getInt(1), c.getString(2), c.getLong(3), c.getLong(4), c.getString(5)));
            }
        }
        DecimalFormatSymbols u = new DecimalFormatSymbols();
        u.setDecimalSeparator(',');
        u.setGroupingSeparator('.');
        String s = "#,##0.00";
        DecimalFormat w = new DecimalFormat(s, u);
        long f, total = 0;
        for (ItemOrcamento io : itens) {
            f = (io.getMetros() * io.getPrecometro());
            total = total + f;
        }
        double totalfloat = (double) total / 10000;
        rodape = "Valor Total do Orçamento: R$ " + w.format(totalfloat);
        itemOrcamentoFooter.setText(rodape);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_orcamento, menu);
        /** SEARCH **/
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            /** Ihave to return where I came From **/
            Intent returnIntent = new Intent();
            //returnIntent.putExtra("busca", nameSearch);
            setResult(RESULT_OK, returnIntent);
            finish();
            return true;
        } else if (id == R.id.itemOrcamentoEnviarOrcamento) {
            startActivityEnviarOrcamento();
        } else if (id == R.id.itemOrcamentoNovoOrcamento) {
            startActivityNovoOrcamento();
        } else if (id == R.id.itemOrcamentoEditarcliente) {
            startActivityEditarCliente();
        } else if (id == R.id.itemOrcamentoCasa) {
            startActivityVoltaParaCasa();
        } else if (id == R.id.itemOrcamentoEditarMaterial) {
            startMaterialAddActivity();
        }
        return super.onOptionsItemSelected(item);


    }
    static final int EDITAR_MATERIAL = 1131;

    public void startMaterialAddActivity(){
        Intent intent = new Intent(this, MaterialAddActivity.class);
        intent.putExtra("Origem", "ItemOrcamentoActivity");
        startActivityForResult(intent, EDITAR_MATERIAL);
    }

    static final int EDITAR_CLIENTE = 1174;

    public void startActivityEditarCliente() {
        Intent intent = new Intent(this, ClienteActivity.class);
        intent.putExtra("Origem", "ItemOrcamentoActivity");
        intent.putExtra("apelido", apelido);
        intent.putExtra("orcamento_id", orcamento_id);
        startActivityForResult(intent, EDITAR_CLIENTE);
        //o orcamento_id vai e volta

    }

    public void startActivityAdicionarMaterial(View view) {
        Intent intent = new Intent(this, ItemAddDialogActivity.class);
        intent.putExtra("orcamento_id", orcamento_id);
        startActivityForResult(intent, ADICIONAR_MATERIAL);
    }

    public void starActivityEditarCabecalho(View v) {
        Intent intent = new Intent(this, OrcamentoActivity.class);
        intent.putExtra("Origem", "ItemOrcamentoActivityEditarCabecalho");
        intent.putExtra("orcamento_id", orcamento_id);
        startActivityForResult(intent, EDITAR_CABECALHO_ORCAMENTO);
    }

    public void startActivityVoltaParaCasa() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void startActivityNovoOrcamento() {
        Intent intent = new Intent(this, OrcamentoActivity.class);
        intent.putExtra("Origem", "ItemOrcamentoActivityNovoOrcamento");
        intent.putExtra("apelido", apelido);
        startActivity(intent);
        finish();
    }

    public void startActivityEnviarOrcamento() {
        Intent intent = null, chooser = null;
        c = dbAdapter.retrieveCliente(apelido);
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                email = c.getString(c.getColumnIndex("fone2"));
            }
        }
        StringBuilder sb = new StringBuilder();
        /**sb.setLength(0)**/
        sb.append("Segue a lista do material:\n\n");
        DecimalFormatSymbols u = new DecimalFormatSymbols();
        u.setDecimalSeparator(',');
        u.setGroupingSeparator('.');
        String s = "#,##0.00";
        DecimalFormat w = new DecimalFormat(s, u);
        long f, total = 0;
        for (ItemOrcamento io : itens) {
            f = io.getMetros() * io.getPrecometro();
            total = total + f;
            sb.append(io.getMaterial() + ": "
                    + w.format((float) io.getMetros() / 100) + "m X "
                    + w.format((float) io.getPrecometro() / 100) + " R$/m = R$"
                    + w.format((float) f / 10000) + ".\n\n");
        }

        sb.append(" Total de: R$" + w.format((float) total / 10000) + "\n");

        intent = new Intent(intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        String[] sendTo = {email};
        intent.putExtra(Intent.EXTRA_EMAIL, sendTo);
        intent.putExtra(Intent.EXTRA_SUBJECT, cabecalho);
        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        intent.setType("message/rfc822");
        chooser = Intent.createChooser(intent, "Send Email");
        startActivity(chooser);

    }

    public class SwipeHelper extends ItemTouchHelper.SimpleCallback {
        ItemOrcamentoAdapter adapter;

        SwipeHelper(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        SwipeHelper(ItemOrcamentoAdapter adapter) {
            //drag directions, swipe directions
            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT);
            this.adapter = adapter;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ItemOrcamentoActivity.this);
            builder.setMessage(R.string.remover_material_do_orcamento);
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.SIM, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    int i = viewHolder.getAdapterPosition();
                    int j = adapter.getItemOrcamentoId(i);
                    if (dbAdapter.deleteItemOrcamentobyId(j)) {
                        L.t(ItemOrcamentoActivity.this, getString(R.string.material_removido));
                        adapter.avisaRemocao(i);
                        carregarItensdoOrcamento();
                        adapter.notifyDataSetChanged();
                    } else
                        L.t(ItemOrcamentoActivity.this, getString(R.string.material_nao_removido));
                }
            });
            builder.setNegativeButton(R.string.NAO, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    carregarItensdoOrcamento();
                    adapter.notifyDataSetChanged();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }


}
