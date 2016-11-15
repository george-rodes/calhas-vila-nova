package br.com.valterdiascalhas.orcamentos;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

/**
 * Created by George on 27/08/2016.
 */
public class OrcamentoRVAdapter extends RecyclerView.Adapter<OrcamentoRVAdapter.OrderViewHolder> {
    String s;
    List<Orcamento> orcamentos;
    Context context;

    OrcamentoRVAdapter(List<Orcamento> orcamentos, Context context) {
        this.orcamentos = orcamentos;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_order_cv, viewGroup, false);
        OrderViewHolder pvh = new OrderViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(OrderViewHolder orderViewHolder, int i) {
        final int position = i;
        orderViewHolder.dataDoOrcamento.setText(orcamentos.get(i).getData());
        Resources r =   context.getResources();
        // Populate the data into the template view using the data object
        s=r.getString(R.string.cliente)+ orcamentos.get(i).getNome();
        orderViewHolder.clienteDoOrcamento.setText(s);
        DecimalFormatSymbols u = new DecimalFormatSymbols();
        u.setDecimalSeparator(',');
        u.setGroupingSeparator('.');
        String s = "#,##0.00";
        DecimalFormat w = new DecimalFormat(s, u);

       // String strNum = w.format(orcamentos.get(i).getTotal());
        float totalfloat = (float) orcamentos.get(i).getTotal() / 100;
        String strNum = w.format(totalfloat);
        final String totalIntent = strNum;

        s = r.getString(R.string.numero) + orcamentos.get(i).getOrcamento();
        orderViewHolder.cod_orcamento.setText(s);
        s = r.getString(R.string.data) + orcamentos.get(i).getData();
        orderViewHolder.dataDoOrcamento.setText(s);
        s = r.getString(R.string.valor_total) + strNum;
        orderViewHolder.valor.setText(s);
        s = r.getString(R.string.local) + orcamentos.get(i).getRua()+", " + orcamentos.get(i).getBairro() +", " + orcamentos.get(i).getCidade();
        orderViewHolder.local.setText(s);

        orderViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //L.m("Ordenumber is: "+ orcamentos.get(position).getOrcamento()  );
                Intent intent = new Intent(context, ItemOrcamentoActivity.class);
                String i = Integer.toString(orcamentos.get(position).getOrcamento());
                intent.putExtra("orcamento",i);
                intent.putExtra("cliente",orcamentos.get(position).getNome());
                intent.putExtra("data",orcamentos.get(position).getData());
                intent.putExtra("rua",orcamentos.get(position).getRua());
                intent.putExtra("bairro",orcamentos.get(position).getBairro());
                intent.putExtra("cidade",orcamentos.get(position).getCidade());
                intent.putExtra("observacao", orcamentos.get(position).getObservacao());
                intent.putExtra("total",totalIntent);
                context.startActivity(intent);

            }
        });




    }

    @Override
    public int getItemCount() {
        return orcamentos.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView dataDoOrcamento;
        TextView cod_orcamento;
        TextView clienteDoOrcamento;
        TextView valor;
        TextView local;
        CardView cardView;

        OrderViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            dataDoOrcamento = (TextView) itemView.findViewById(R.id.dataDoOrcamento);
            cod_orcamento = (TextView) itemView.findViewById(R.id.orcamento);
            dataDoOrcamento = (TextView) itemView.findViewById(R.id.dataDoOrcamento);
            clienteDoOrcamento = (TextView) itemView.findViewById(R.id.clienteDoOrcamento);
            valor = (TextView) itemView.findViewById(R.id.valor);
            local = (TextView) itemView.findViewById(R.id.local);
        }


    }


}