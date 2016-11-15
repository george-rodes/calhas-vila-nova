package br.com.valterdiascalhas.orcamentos;

import java.util.StringTokenizer;

/**
 * Created by George on 25/09/2016.
 */

public class DiaMesAno {

    private int dia;
    private int mes;
    private int ano;

    public DiaMesAno(String data) {
        StringTokenizer token = new StringTokenizer(data, "/");
        this.dia =  Integer.parseInt(token.nextToken());
        this.mes =  Integer.parseInt(token.nextToken());
        this.ano =  Integer.parseInt(token.nextToken());
    }

    public int getDia() {
        return dia;
    }

    public int getMes() {
        return mes;
    }

    public int getAno() {
        return ano;
    }
}
