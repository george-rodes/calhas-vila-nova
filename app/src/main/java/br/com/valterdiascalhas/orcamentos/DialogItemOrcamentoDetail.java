package br.com.valterdiascalhas.orcamentos;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogItemOrcamentoDetail extends DialogFragment implements View.OnClickListener {
    View view;
    Button bsave, bcancel,bClearMetros, bClearPreco;
    EditText etMetro, etPreco;
    Integer idItemOrc;
    Long metros,preco;
    ImageView close;
    Communicator communicator;
    String titulo;
    private String current = "";

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        metros = getArguments().getLong("metros"); /** Centimentros **/
        preco = getArguments().getLong("preco"); /** Centavos **/
        titulo = getArguments().getString("titulo");
        idItemOrc = getArguments().getInt("idItemOrc");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        communicator = (Communicator) context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_item_orcamento_detail, null);
        //super.onCreateView(inflater, container, savedInstanceState);
        setCancelable(false);
        bsave = (Button) view.findViewById(R.id.btDialogSave);
        bcancel = (Button) view.findViewById(R.id.btDialogCancel);
        bClearMetros = (Button) view.findViewById(R.id.clearMetroDialog);
        bClearPreco = (Button) view.findViewById(R.id.clearPrecoDialog);
        close = (ImageView) view.findViewById(R.id.ivClose);
        etMetro = (EditText) view.findViewById(R.id.etMetros);
        etPreco = (EditText) view.findViewById(R.id.etPrecoMetro);
        bsave.setOnClickListener(this);
        bcancel.setOnClickListener(this);
        bClearMetros.setOnClickListener(this);
        bClearPreco.setOnClickListener(this);
        close.setOnClickListener(this);
        etMetro.setText(Utilidades.DividaPorCemLong(metros));
        etPreco.setText(Utilidades.DividaPorCemLong(preco));

        etMetro.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (Utilidades.decNumStr(etMetro.getText().toString(), getContext()).equals("Error")) {
                        L.t(getContext(), "Error");
                    }
                }
            }
        });
        etPreco.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (Utilidades.decNumStr(etPreco.getText().toString(), getContext()).equals("Error")) {
                        L.t(getContext(), "Error");
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btDialogCancel || id == R.id.ivClose) {
            dismiss();
        } else if (id == R.id.btDialogSave) {
            long p, m;
            String spreco = Utilidades.decNumStr(etPreco.getText().toString(), getContext());
            String smetro = Utilidades.decNumStr(etMetro.getText().toString(), getContext());
            if (!spreco.equals("Error") && !smetro.equals("Error")) {
                p = (long) (Float.parseFloat(spreco)*10000 )/100;
                m = (long) (Float.parseFloat(smetro) * 10000)/100;
                communicator.onDialogMessage(m, p, idItemOrc);
                dismiss();
            }
        } else if (id== R.id.clearMetroDialog){
            etMetro.setText("");
        } else if (id== R.id.clearPrecoDialog){
            etPreco.setText("");
        }

    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle(titulo);
        return dialog;
    }

    interface Communicator {
        public void onDialogMessage(long m, long p, int id);
    }


}
