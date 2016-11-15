package br.com.valterdiascalhas.orcamentos;

import android.os.Parcelable;

/**
 * Created by George on 24/08/2016.
 */
public class Cliente {
    private String apelido, nome, fone1, fone2, rua, bairro, cidade, obs;

    public Cliente(String apelido, String nome, String fone1, String fone2, String rua, String bairro, String cidade, String obs) {
        this.apelido = apelido;
        this.nome = nome;
        this.fone1 = fone1;
        this.fone2 = fone2;
        this.rua = rua;
        this.bairro = bairro;
        this.cidade = cidade;
        this.obs = obs;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFone1() {
        return fone1;
    }

    public void setFone1(String fone1) {
        this.fone1 = fone1;
    }

    public String getFone2() {
        return fone2;
    }

    public void setFone2(String fone2) {
        this.fone2 = fone2;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

}
