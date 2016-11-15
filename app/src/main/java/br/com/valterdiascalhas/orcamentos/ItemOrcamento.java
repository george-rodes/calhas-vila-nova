package br.com.valterdiascalhas.orcamentos;

public class ItemOrcamento {
    private int orcamento;
    private int id;
    private long metros, precometro;
    private String material, observacao;

    public ItemOrcamento(int id, int orcamento, String material, long metros, long precometro, String observacao) {
        this.id = id;
        this.orcamento = orcamento;
        this.material = material;
        this.metros =  metros ;
        this.precometro =  precometro ;
        this.observacao = observacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(int orcamento) {
        this.orcamento = orcamento;
    }

    public long getMetros() {
        return metros;
    }

    public void setMetros(long metros) {
        this.metros = metros;
    }

    public long getPrecometro() {
        return precometro;
    }

    public void setPrecometro(long precometro) {
        this.precometro = precometro;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
