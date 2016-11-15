package br.com.valterdiascalhas.orcamentos;

import android.app.SearchManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ClientListFragment.OnClienteFragmentListener {

    DBAdapter dbAdapter;
    SQLiteDatabase sqLiteDatabase;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView imageViewCameraResult;
    OrcamentoFragment orcamentoFragment;
    ClientListFragment clientListFragment;
    String apelido;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check if the activity is using the layout version
        //with the FrameLayout (one Pane).  if so, we have to add the fragment
        //(it wont be done automatically )
        if (findViewById(R.id.container) != null) {
            //However if were being restored from a previous state,
            //then dont do anything
            if (savedInstanceState != null) {
                return;
            }
            //Crate an instance of the Headline Fragment
            clientListFragment = new ClientListFragment();
            //In the case this activity was started with special instructions from an Intent,
            //pass the Intent's extras to the fragment as arguments
            clientListFragment.setArguments(getIntent().getExtras());
            //Ask the Fragment manager to add it to the FrameLayout

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, clientListFragment)
                    .commit();
        }

        //L.m("Helloo, MainActivity OnCreate. Do NOT delete me!");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*** VERIFICAÇÕES INICIAIS DO BANCO DE DADOS ***/
        dbAdapter = new DBAdapter(getApplicationContext());
        sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();

        if (!Utilidades.existeTabela("cliente", MainActivity.this) &&
                !Utilidades.existeTabela("ormamento", MainActivity.this) &&
                !Utilidades.existeTabela("itemorcamento", MainActivity.this) &&
                !Utilidades.existeTabela("material", MainActivity.this)) {
            dbAdapter.mydbHelper.onCreate(sqLiteDatabase);
        }

        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra("origem")){
                if (intent.getStringExtra("origem").equals("orcamento excluido"));{

                 //passar para

                }
            }
        }

        apelido = "vazio";
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        /** SEARCH **/
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.novo_cliente || id == R.id.novo_cliente2) {
            //we never come back here
            //startClienteActivityForResult();
            startClienteActivity();

            return true;
        } else if (id == R.id.novo_orcamento || id == R.id.novo_orcamento2) {
            //we never come back here
            //startOrcamentoActivityForResult();
            startOrcamentoActivity();

            return true;
        } else if (id == R.id.editar_calha) {
            startMaterialAddActivity();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startMaterialAddActivity(){
        Intent intent = new Intent(this, MaterialAddActivity.class);
        intent.putExtra("Origem", "MainActivity");
        startActivity(intent);
    }

    public void startClienteActivity(){
        Intent intent = new Intent(this, ClienteActivity.class);
        intent.putExtra("Origem", "MainActivity");
        startActivity(intent);
    }

    public void startOrcamentoActivity(){
        Intent intent = new Intent(this, OrcamentoActivity.class);
        intent.putExtra("Origem", "MainActivity");
        //como posso saber se algum foi escolhido?
        intent.putExtra("apelido", apelido);
        startActivity(intent);
    }





    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.drawer_novo_cliente) {
            startClienteActivity();

        } else if (id == R.id.nav_orcamentos) {
            startOrcamentoActivity();

        } else if (id == R.id.nav_modelo_calha) {
            startActivity(new Intent(this, MaterialListActivity.class));
            return true;
        } else if (id == R.id.editar_calha) {
            startMaterialAddActivity();
            return true;
        } else if (id == R.id.backup) {
            startActivity(new Intent(this, BackupActivity.class));
            return true;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onClienteFragmentInteraction(String who, String what) {
        apelido = what; //for the intent
        //sending to orderListFragment
        //L.t(getApplicationContext(),"I want do See the Orders!");
        //Capture the orcamento fragment from the activity's DUAL-PANE layout
        //When does this become null?
        orcamentoFragment = (OrcamentoFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentDown);
        //if we dont find one, we must not be in two pane mode


        if (orcamentoFragment == null) {
            //we must be in two pane layout
            callOrcamentoFragment(who, what);

        } else {
            /** getSupportFragmentManager() returns not null, but the rest is null */
            if (orcamentoFragment.adapter != null) {
                orcamentoFragment.atualizeOrcamentosDoCliente(what);
            } else callOrcamentoFragment(who, what);
        }
    }

    public void callOrcamentoFragment(String who, String what) {
        OrcamentoFragment swapFragment = new OrcamentoFragment();
        Bundle args = new Bundle();
        args.putString("who", who);
        args.putString("what", what);
        swapFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, swapFragment)
                .addToBackStack(null)
                .commit();
    }



}

