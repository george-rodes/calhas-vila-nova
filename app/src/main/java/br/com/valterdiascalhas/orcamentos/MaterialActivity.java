package br.com.valterdiascalhas.orcamentos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;


public class MaterialActivity extends AppCompatActivity {
    DBAdapter dbAdapter;
    SQLiteDatabase sqLiteDatabase;
    Cursor c;
    ListView calhasListView;
    MaterialAdapter adapter;
    ArrayList<Material> materials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navegar_material);
        calhasListView = (ListView) findViewById(R.id.mycalhasListView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarNavCalhas);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Calhas Vila Nova");
        getSupportActionBar().setSubtitle("Modelos de Calha");

        dbAdapter = new DBAdapter(getApplicationContext());
        sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();
        c = dbAdapter.retrieveMateriais();

        materials = new ArrayList<>();

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                materials.add(new Material(c.getString(1), c.getString(2), c.getString(3), c.getString(4), null));
            }
        }
        adapter = new MaterialAdapter(this, materials);
        calhasListView.setAdapter(adapter);
        calhasListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                c.moveToPosition(position);
               // L.m(c.getString(c.getColumnIndex("codigo")) + " " + c.getString(c.getColumnIndex("imagem")));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.navigate_table_calha) {
            L.m("Inside Navegar Calhas, R.id.navigate_table_calha");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navergar_material, menu);
        return true;
    }





}
