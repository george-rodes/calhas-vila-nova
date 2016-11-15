package br.com.valterdiascalhas.orcamentos;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * Created by George on 25/08/2016.
 */
public class OrcamentoAdapter extends ArrayAdapter<Orcamento> {
    String s;
    public OrcamentoAdapter(Context context, List<Orcamento> orcamentos) {
        super(context, 0, orcamentos);
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        // Get the data item for this position
        Orcamento orcamento = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (row == null) {
            row = LayoutInflater.from(getContext()).inflate(R.layout.row_order, parent, false);
        }
        // Lookup view for data population
        Resources r = getContext().getResources();
        TextView cod_orcamento = (TextView) row.findViewById(R.id.orcamento);
        TextView dataDoOrcamento = (TextView) row.findViewById(R.id.dataDoOrcamento);
        TextView clienteDoOrcamento = (TextView) row.findViewById(R.id.clienteDoOrcamento);
        TextView valor = (TextView) row.findViewById(R.id.valor);
        TextView local = (TextView) row.findViewById(R.id.local);
        // Populate the data into the template view using the data object
        s=r.getString(R.string.cliente)+ orcamento.getNome();
        clienteDoOrcamento.setText(s);
        DecimalFormatSymbols u = new DecimalFormatSymbols();
        u.setDecimalSeparator(',');
        u.setGroupingSeparator('.');
        String s = "#,##0.00";
        DecimalFormat w = new DecimalFormat(s, u);
        float totalfloat = (float) orcamento.getTotal() / 100;
        String strNum = w.format(totalfloat);

        s = r.getString(R.string.numero) + orcamento.getOrcamento();
        cod_orcamento.setText(s);
        s = r.getString(R.string.data) + orcamento.getData();
        dataDoOrcamento.setText(s);
        s = r.getString(R.string.valor_total) + strNum;
        valor.setText(s);
        s = r.getString(R.string.local) + orcamento.getRua()+", " + orcamento.getBairro() +", " + orcamento.getCidade();
        local.setText(s);

        // Return the completed view to render on screen
        return row;
    }


}
