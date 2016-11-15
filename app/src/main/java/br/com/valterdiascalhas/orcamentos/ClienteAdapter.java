package br.com.valterdiascalhas.orcamentos;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


/**
 * Created by George on 12/09/2016.
 */
public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ItemViewHolder> {
    private ClientListFragment.OnClienteFragmentListener mListener;
    List<Cliente> clientes;
    Context context;
    Intent intent;


    ClienteAdapter(List<Cliente> clientes, Context context, ClientListFragment.OnClienteFragmentListener mListener) {
        this.clientes = clientes;
        this.context = context;
        this.mListener = mListener; /** for fragment interaction*/
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView apelido,nome, fone, rua,bairro,cidade;
        ImageView  editCliente, email,orcamento;
        LinearLayout llOrcamento;

        public ItemViewHolder(View itemView) {
            super(itemView);
            /** para captar clique para o orcamento **/
            llOrcamento = (LinearLayout) itemView.findViewById(R.id.llOrcamento);
            apelido     = (TextView) itemView.findViewById(R.id.apelido);
            nome        = (TextView) itemView.findViewById(R.id.nome);
            fone        = (TextView) itemView.findViewById(R.id.fone);
            rua         = (TextView) itemView.findViewById(R.id.rua);
            bairro       = (TextView) itemView.findViewById(R.id.bairro);
            cidade      = (TextView) itemView.findViewById(R.id.cidade);
            /** clique para editar cliente*/
            editCliente= (ImageView) itemView.findViewById(R.id.ivEditCliente);
            /**clique para enviar email*/
            email = (ImageView) itemView.findViewById(R.id.ivEmail);
            /** clique para cadastrar novo ormamento **/
            orcamento= (ImageView) itemView.findViewById(R.id.ivOrcamento);
        }
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_cliente_cv, viewGroup, false);
        ItemViewHolder pvh = new ItemViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder h, int i) {
        final int position = i;
        h.apelido.setText(clientes.get(i).getApelido());
        h.nome.setText(clientes.get(i).getNome());
        h.fone.setText(clientes.get(i).getFone1());
        h.rua.setText(clientes.get(i).getRua());
        h.bairro.setText(clientes.get(i).getBairro());
        h.cidade.setText(clientes.get(i).getCidade());

        h.llOrcamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //L.m("Mostar Orcamentos" + clientes.get(position).getApelido());
                if (mListener != null) {
                    mListener.onClienteFragmentInteraction("l", clientes.get(position).getApelido() );
                }
            }
        });

        h.editCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //L.m("Editar Cadastro: " + clientes.get(position).getApelido());
                intent = new Intent(context, ClienteActivity.class);
                intent.putExtra("Origem", "ClienteAdapterEditIcon");
                intent.putExtra("apelido", clientes.get(position).getApelido() );
                context.startActivity(intent);
            }
        });

        h.email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                L.m("Enviar Email para: " + clientes.get(position).getApelido());
                String cabecalho = clientes.get(position).getApelido();
                String email= clientes.get(position).getFone2();
                intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                String[] sendTo = {email};
                intent.putExtra(Intent.EXTRA_EMAIL, sendTo);
                intent.putExtra(Intent.EXTRA_SUBJECT, cabecalho);
                intent.setType("message/rfc822");
                context.startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });


        h.orcamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, OrcamentoActivity.class);
                //intent.putExtra("Origem", "Novo Orcamento de Cliente");
                intent.putExtra("Origem", "ClienteAdapterAddOrcamentoIcon");
                intent.putExtra("apelido", clientes.get(position).getApelido());
                context.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return clientes.size();
    }


}
