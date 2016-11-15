package br.com.valterdiascalhas.orcamentos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


public class ClientListFragment extends Fragment {

    private OnClienteFragmentListener mListener;
    View rootView;
    DBAdapter dbAdapter;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor,c;
    String cliente;
    private RecyclerView rv;
    ArrayList<Cliente> clientes;
    ClienteAdapter adapter;

    public ClientListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            cliente = savedInstanceState.getString("cliente");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_client_list, container, false);
        dbAdapter = new DBAdapter(rootView.getContext());
        sqLiteDatabase = dbAdapter.mydbHelper.getWritableDatabase();

        /** RV START **/
        rv = (RecyclerView) rootView.findViewById(R.id.rvClient);
        LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        clientes = new ArrayList<>();
        carregarClientes();
        /** EXTRA data in the constructor, mListener for fragment interaction**/
        adapter = new ClienteAdapter(clientes, rootView.getContext(),mListener);
        rv.setAdapter(adapter);
        /** RRV END **/
        return rootView;
    }

    public void carregarClientes(){
        c = dbAdapter.retrieveClientes();
        clientes.clear();
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                clientes.add(new Cliente(c.getString(1),c.getString(2), c.getString(3),
                        c.getString(4), c.getString(5), c.getString(6),c.getString(7),c.getString(8) ));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarClientes();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("cliente", cliente );
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnClienteFragmentListener) {
            mListener = (OnClienteFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnClienteFragmentListener");
        }
    }

    public interface OnClienteFragmentListener {
        void onClienteFragmentInteraction(String who, String what);
    }
}
