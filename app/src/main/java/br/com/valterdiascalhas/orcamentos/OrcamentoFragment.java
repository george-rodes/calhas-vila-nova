package br.com.valterdiascalhas.orcamentos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class OrcamentoFragment extends Fragment {
    View rootView;
    ListView listViewOrder;
    DBAdapter dbAdapter;
    SQLiteDatabase sqLiteDatabase;
    Cursor c;
    String mFromColumns[];
    int mToFields[];
    String cliente;
    TextView orderCabecalho;
    ArrayList<Orcamento> orcamentos;
    OrcamentoAdapter adapter;

    public OrcamentoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            cliente = savedInstanceState.getString("cliente");
        } else cliente = "Desconhecido";
    }

    @Override
    public void onStart() {
        super.onStart();
        //During startup, we should check if there are arguments (data)
        //passed to this Fragment. We know the layout has already been
        //applied to the Fragment so we can safely call the method that
        //sets the article text
        Bundle args = getArguments();
        if (args != null) {
            //set the orcamento based on the argument passed in
            atualizeOrcamentosDoCliente(args.getString("what"));
        } else if (!cliente.equals("Desconhecido")) {
            //set the article based on the saved instance state defined during onCreateView
            atualizeOrcamentosDoCliente(cliente);
        }
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order_list, container, false);
        listViewOrder = (ListView) rootView.findViewById(R.id.listViewOrder);
        orderCabecalho = (TextView) rootView.findViewById(R.id.orderCabecalho);

        dbAdapter = new DBAdapter(rootView.getContext());
        sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();
        c = dbAdapter.retrieveOrcamentos("Desconhecido");
        orcamentos = new ArrayList<>();
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                orcamentos.add(new Orcamento(
                        c.getInt(1), c.getString(2), c.getString(3), c.getInt(4), c.getInt(5),
                        c.getInt(6), c.getString(7), c.getString(8), c.getString(9), c.getInt(10),
                        c.getString(11)));
            }
        }
        adapter = new OrcamentoAdapter(rootView.getContext(), orcamentos);
        listViewOrder.setAdapter(adapter);
        listViewOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                c.moveToPosition(position);
                Intent intent = new Intent(getActivity(), ItemOrcamentoActivity.class);
                intent.putExtra("orcamento", c.getString(c.getColumnIndex("orcamento")));
                intent.putExtra("cliente", c.getString(c.getColumnIndex("nome")));
                intent.putExtra("data", c.getString(c.getColumnIndex("data")));
                intent.putExtra("rua", c.getString(c.getColumnIndex("rua")));
                intent.putExtra("bairro", c.getString(c.getColumnIndex("bairro")));
                intent.putExtra("cidade", c.getString(c.getColumnIndex("cidade")));
                intent.putExtra("observacao", c.getString(c.getColumnIndex("observacao")));
                intent.putExtra("total", Utilidades.DividaPorCemString(c.getString(c.getColumnIndex("total"))));
                startActivity(intent);
            }
        });
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("cliente", cliente);
    }

    public void atualizeOrcamentosDoCliente(String nome) {
        cliente = nome;
        orderCabecalho.setText("Orçamentos\n" + nome);
        dbAdapter = new DBAdapter(getActivity());
        sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();
        c = dbAdapter.retrieveOrcamentos(nome);
        orcamentos.clear();
        if (c.getCount() > 0) {
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    orcamentos.add(new Orcamento(
                            c.getInt(1), c.getString(2), c.getString(3), c.getInt(4), c.getInt(5),
                            c.getInt(6), c.getString(7), c.getString(8), c.getString(9), c.getInt(10),
                            c.getString(11)));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //L.t(getContext(),"Orcamento Fragmento destroed");
    }
}
