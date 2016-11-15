package br.com.valterdiascalhas.orcamentos;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchResults extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ClientListFragment.OnClienteFragmentListener {
    DBAdapter dbAdapter;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    String query, cabecalho,emailAddress;
    ListView listViewOrder;
    Cursor c;
    String apelido, origem;
    String id;
    TextView tvCabecalho, cabapelido, cabnome, cabfone, cabrua, cabbairro, cabcidade;
    ArrayList orcamentos;
    ImageView editCliente, email, orcamento;
    /**
     * RV
     **/
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results_cv);

        cabapelido = (TextView) findViewById(R.id.apelidoSearchResult);
        cabnome = (TextView) findViewById(R.id.nomeSearchResult);
        cabfone = (TextView) findViewById(R.id.foneSearchResult);
        cabrua = (TextView) findViewById(R.id.ruaSearchResult);
        cabbairro = (TextView) findViewById(R.id.bairroSearchResult);
        cabcidade = (TextView) findViewById(R.id.cidadeSearchResult);
        /** clique para editar cliente*/
        editCliente = (ImageView) findViewById(R.id.ivEditClienteSearchResult);
        /**clique para enviar email*/
        email = (ImageView) findViewById(R.id.ivEmailSearchResult);
        /** clique para cadastrar novo ormamento **/
        orcamento = (ImageView) findViewById(R.id.ivOrcamentoSearchResult);


        //listViewOrder = (ListView) findViewById(R.id.listViewOrderSearchResults);
        /** RV START **/
        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        /** RV END **/

        dbAdapter = new DBAdapter(SearchResults.this);
        sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_results_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);

        tvCabecalho = (TextView) findViewById(R.id.orderCabecalho);
        orcamentos = new ArrayList<>();

        Intent searchIntent = getIntent();

        origem = searchIntent.getStringExtra("Origem");

        if (searchIntent.getAction() != null) {
            if (searchIntent.getAction().equals(Intent.ACTION_VIEW)) {
                id = searchIntent.getData().getLastPathSegment();
                cursor = dbAdapter.getOneClient(id);
                if (cursor.moveToNext()) {
                    apelido = cursor.getString(cursor.getColumnIndex(DBAdapter.DBHelper.APELIDO));
                    cabecalho = getString(R.string.orcamentos) + "\n" + apelido;
                    tvCabecalho.setText(cabecalho);
                    c = dbAdapter.retrieveOrcamentos(apelido);
                    setUpTheAdapter(c);
                    carregarCabecalho(apelido);

                }
            }
        } else if (origem.equals("OrcamentoActivity") || origem.equals("ItemOrcamentoActivity")) {
            apelido = searchIntent.getStringExtra("apelido");
            if ((cursor = dbAdapter.checkIfClienteApelidoExists(apelido)).moveToNext()) {

                cabecalho = getString(R.string.orcamentos) + "\n" + apelido;
                tvCabecalho.setText(cabecalho);
                c = dbAdapter.retrieveOrcamentos(apelido);
                setUpTheAdapter(c);
                carregarCabecalho(apelido);
            }
        }
        editCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startClienteActivity();
            }
        });

        orcamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startOrcamentoActivity();
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarEmail();
            }
        });

    }

    public void enviarEmail(){

        String cabecalho = apelido;
        String email= emailAddress;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        String[] sendTo = {email};
        intent.putExtra(Intent.EXTRA_EMAIL, sendTo);
        intent.putExtra(Intent.EXTRA_SUBJECT, cabecalho);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Send Email"));
    }


    public void setUpTheAdapter(Cursor c) {

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                orcamentos.add(new Orcamento(
                        c.getInt(1), c.getString(2), c.getString(3), c.getInt(4), c.getInt(5),
                        c.getInt(6), c.getString(7), c.getString(8), c.getString(9), c.getInt(10),
                        c.getString(11)));
            }
        }
        /** RV START **/
        OrcamentoRVAdapter adapter = new OrcamentoRVAdapter(orcamentos, this);
        rv.setAdapter(adapter);
        /** RRV END **/

    }

    public void carregarCabecalho(String apelido) {
        if ((c= dbAdapter.retrieveCliente(apelido)).moveToNext()) {
            cabapelido.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.APELIDO)));
            cabnome.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.NOME)));
            cabfone.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.FONE)));
            cabrua.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.RUA)));
            cabbairro.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.BAIRRO)));
            cabcidade.setText(c.getString(c.getColumnIndex(DBAdapter.DBHelper.CIDADE)));
            emailAddress = (c.getString(c.getColumnIndex(DBAdapter.DBHelper.EMAIL)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_search_results, menu);
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
        } else if (id == R.id.novo_orcamento_search_results) {
            startOrcamentoActivity();
        }

        return true;
    }

    public void startOrcamentoActivity() {
        Intent intent = new Intent(this, OrcamentoActivity.class);
        intent.putExtra("Origem", "SearchResults");
        intent.putExtra("apelido", apelido);
        startActivity(intent);
    }

    static final int EDITAR_CLIENTE = 1147;
    public void startClienteActivity(){
        Intent intent = new Intent(this, ClienteActivity.class);
        intent.putExtra("Origem", "SearchResultEditIcon");
        intent.putExtra("apelido", apelido );
        startActivityForResult (intent,EDITAR_CLIENTE);
        //quando voltar precisa corrigir o cabecalho

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == EDITAR_CLIENTE) {
                if (resultCode == RESULT_OK) {
                    carregarCabecalho(data.getStringExtra("apelido"));
                } else if (resultCode == RESULT_CANCELED) {
                    //nothing changed
                }
            }
        }
    }

    /**
     * static final int INCLUIR_NOVO_ORCAMENTO = 47321; // Request Code RCMNT
     * public void startOrcamentoActivityForResult() {
     * Intent intent = new Intent(this, OrcamentoActivity.class);
     * intent.putExtra("Origem", "Novo Orcamento Search Results");
     * intent.putExtra("Cliente", cliente);
     * //passar tambes o nome
     * startActivityForResult(intent, INCLUIR_NOVO_ORCAMENTO);
     * }
     **/

    @Override
    public void onClienteFragmentInteraction(String who, String what) {
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        // L.m("Main Activity calling onAttachFragment: " + fragment.toString());
        super.onAttachFragment(fragment);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }
}
