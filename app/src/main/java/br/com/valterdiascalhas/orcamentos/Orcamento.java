package br.com.valterdiascalhas.orcamentos;

/**
 * Created by George on 25/08/2016.
 */
public class Orcamento {
    private int orcamento, ano, mes, dia;
    /** int para long **/
    private long total;
    private String nome, data, rua, bairro, cidade, observacao;
    //private float total;

    //Empty Constructor for Test
    public Orcamento(){

    }

    public Orcamento(int orcamento, String nome, String data,
                     int ano, int mes, int dia,
                     String rua, String bairro, String cidade,
                     long total, String observacao) {
        this.orcamento = orcamento;
        this.nome = nome;
        this.data = data;
        this.ano = ano;
        this.mes = mes;
        this.dia = dia;
        this.rua = rua;
        this.bairro = bairro;
        this.cidade = cidade;
        this.total = total; //centavos
        this.observacao = observacao;
    }




    public int getOrcamento() {
        return orcamento;
    }
    public void setOrcamento(int orcamento) {
        this.orcamento = orcamento;
    }
    public int getAno() {
        return ano;
    }
    public void setAno(int ano) {
        this.ano = ano;
    }
    public int getMes() {
        return mes;
    }
    public void setMes(int mes) {
        this.mes = mes;
    }
    public int getDia() {
        return dia;
    }
    public void setDia(int dia) {
        this.dia = dia;
    }
    public long getTotal() {
        return total;
    }
    public void setTotal(long total) {
        this.total = total;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
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
    public String getObservacao() {
        return observacao;
    }
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
