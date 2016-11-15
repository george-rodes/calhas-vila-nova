package br.com.valterdiascalhas.orcamentos;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class ItemOrcamentoAdapter extends RecyclerView.Adapter<ItemOrcamentoAdapter.ItemViewHolder> {
    List<ItemOrcamento> itens;
    Context context;

    ItemOrcamentoAdapter(List<ItemOrcamento> itens, Context context) {
        this.itens = itens;
        this.context = context;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView material, metros, preco, total;
        CardView cardView;

        ItemViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardViewItemOrcamento);
            material = (TextView) itemView.findViewById(R.id.material);
            metros = (TextView) itemView.findViewById(R.id.metros);
            preco = (TextView) itemView.findViewById(R.id.preco);
            total = (TextView) itemView.findViewById(R.id.total);
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_orcamento_cv, viewGroup, false);
        ItemViewHolder pvh = new ItemViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder h, int i) {
        final int position = i;
        h.material.setText(itens.get(i).getMaterial());
        DecimalFormatSymbols u = new DecimalFormatSymbols();
        u.setDecimalSeparator(',');
        u.setGroupingSeparator('.');
        String s = "#,##0.00";
        final DecimalFormat w = new DecimalFormat(s, u);
        h.metros.setText(w.format((float) itens.get(i).getMetros() / 100));
        h.preco.setText(w.format((float) itens.get(i).getPrecometro() / 100));
        double f = (double) (itens.get(i).getMetros() * itens.get(i).getPrecometro()) / 10000;
        h.total.setText(w.format(f));
        h.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                DialogItemOrcamentoDetail dialog = new DialogItemOrcamentoDetail();
                long m = itens.get(position).getMetros();
                long p = itens.get(position).getPrecometro();
                int idItemOrc = itens.get(position).getId();
                String t = itens.get(position).getMaterial();
                Bundle args = new Bundle();
                args.putInt("idItemOrc",idItemOrc);
                args.putLong("metros", m);
                args.putLong("preco", p);
                args.putString("titulo", t);
                dialog.setArguments(args);
                /** FOR MARSHMALLOW ************/
                //dialog.setStyle(DialogFragment.STYLE_NORMAL,R.style.AppTheme);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //android.R.style.Theme_Material_Light_Dialog_Alert
                    dialog.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_Alert );
                }
                dialog.show(fm, "DetalheItem");
            }
        });
    }

    void avisaRemocao(int pos){
        this.notifyItemRemoved(pos);
    }
    int getItemOrcamentoId(int pos){
        return itens.get(pos).getId();
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }
}
