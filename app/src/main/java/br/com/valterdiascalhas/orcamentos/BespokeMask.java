package br.com.valterdiascalhas.orcamentos;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;
import java.util.Locale;

public abstract class BespokeMask {

    public static String unmask(String s) {
        return s.replaceAll("[.]", "").replaceAll("[-]", "").replaceAll("[,]", "")
                .replaceAll("[/]", "").replaceAll("[(]", "")
                .replaceAll("[)]", "");
    }

    public static TextWatcher insert(final String mask, final EditText ediTxt) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";
            public void onTextChanged(CharSequence s, int start, int before,int count) {
                String str = BespokeMask.unmask(s.toString());
                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }

                int i = 0;
                if( str.length() > old.length()){
                    for (char m : mask.toCharArray()) {
                        if (m != '#') {
                            mascara += m;
                            continue;
                        }else{
                            try {
                                mascara += str.charAt(i);
                            } catch (Exception e) {
                                break;
                            }
                            i++;
                        }
                    }
                }else{
                    mascara = s.toString();
                }



                isUpdating = true;
                /***********/
                //NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
                //mascara = nf.format(Integer.valueOf(str));
                //isUpdating = true;
                //ediTxt.setText(mascara);
                //ediTxt.setSelection(mascara.length());

                /*************/
                ediTxt.setText(mascara);
                ediTxt.setSelection(mascara.length());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        };
    }
}