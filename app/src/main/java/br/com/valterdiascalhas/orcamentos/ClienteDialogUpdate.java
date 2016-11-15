package br.com.valterdiascalhas.orcamentos;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by George on 09/09/2016.
 */
public class ClienteDialogUpdate extends DialogFragment implements View.OnClickListener {
    Communicator communicator;
    View view;
    Button bsave, bcancel;
    EditText etDialogNome;
    //String[] apelido,nome,fone,email,rua,bairro,cidade;
    String apelido, nome, fone, email, rua, bairro, cidade;
    String  nomec, fonec, emailc, ruac, bairroc, cidadec, insertOrUpdate;
    TextView tvApelido, tvNome, tvFone, tvEmail, tvRua, tvBairro, tvCidade;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apelido = getArguments().getString("apelido");
        nome    = getArguments().getString("nome");
        fone    = getArguments().getString("fone");
        email    = getArguments().getString("email");
        rua     = getArguments().getString("rua");
        bairro  = getArguments().getString("bairro");
        cidade = getArguments().getString("cidade");
       /** Changes in Color **/
        nomec    = getArguments().getString("nomec");
        fonec    = getArguments().getString("fonec");
        emailc    = getArguments().getString("emailc");
        ruac     = getArguments().getString("ruac");
        bairroc  = getArguments().getString("bairroc");
        cidadec = getArguments().getString("cidadec");

        insertOrUpdate = getArguments().getString("InsertOrUpdate");

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        communicator = (Communicator) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Confirmação do Cadastro do Cliente");
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_cliente_update, null);
        setCancelable(false);
        bsave = (Button) view.findViewById(R.id.btDialogClienteUpdateSave);
        bcancel = (Button) view.findViewById(R.id.btDialogClienteUpdateCancel);
        bsave.setOnClickListener(this);
        bcancel.setOnClickListener(this);

        tvApelido = (TextView) view.findViewById(R.id.tvApelido);
        tvNome = (TextView) view.findViewById(R.id.tvNome);
        tvFone = (TextView) view.findViewById(R.id.tvFone);
        tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        tvRua = (TextView) view.findViewById(R.id.tvRua);
        tvBairro = (TextView) view.findViewById(R.id.tvBairro);
        tvCidade = (TextView) view.findViewById(R.id.tvCidade);

        tvApelido.setText(apelido);
        tvNome.setText(nome);
        setColor(tvNome,nomec);
        tvFone.setText(fone);
        setColor(tvFone,fonec);
        tvEmail.setText(email);
        setColor(tvEmail,emailc);
        tvRua.setText(rua);
        setColor(tvRua,ruac);
        tvBairro.setText(bairro);
        setColor(tvBairro,bairroc);
        tvCidade.setText(cidade);
        setColor(tvCidade,cidadec);
        return view;
    }

    public void setColor(TextView tv, String col){
        int color;
        if (col.equals("Red")){
            color =  R.color.red;
        } else color =  R.color.black;
        tv.setTextColor(ContextCompat.getColor(getContext(), color ));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btDialogClienteUpdateSave) {
            communicator.onDialogMessage(insertOrUpdate);
            dismiss();
        }
        if (id == R.id.btDialogClienteUpdateCancel) {
            dismiss();
        }
    }

    interface Communicator {
        public void onDialogMessage(String s);
    }

}




