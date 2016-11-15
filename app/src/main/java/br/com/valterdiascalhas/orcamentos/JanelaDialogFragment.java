package br.com.valterdiascalhas.orcamentos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by George on 29/08/2016.
 */
public class JanelaDialogFragment extends DialogFragment {
    View view;
    String s;
    public JanelaDialogFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       s = getArguments().getString("nome");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.janela,container,false);
        TextView textViewJanela = (TextView) view.findViewById(R.id.textViewJanela);
        textViewJanela.setText(s);
        return view;
    }
}
