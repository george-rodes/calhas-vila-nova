package br.com.valterdiascalhas.orcamentos;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/** not used
 * Created by George on 27/09/2016.
 * test OK
 */

public class SomeOtherViewHolder extends RecyclerView.ViewHolder {
    private TextView dataDoOrcamento;
    private TextView cod_orcamento;
    private TextView clienteDoOrcamento;
    private TextView valor;
    private TextView local;
    private CardView cardView;

    public SomeOtherViewHolder(View itemView) {
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
