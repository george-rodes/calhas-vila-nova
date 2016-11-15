package br.com.valterdiascalhas.orcamentos;

/**
 * Created by George on 16/08/2016.
 */
public class Material {
    private String codigo; //unique
    private String nome;
    private String materiaprima;
    private String imagem;
    private String obs;
 /** codigo: Agua Furtada C105
  * nome: Agua Furtada
  * materiaprima: Galvanizado 050
  * imagem: material.jpg **/


    public Material(String codigo, String nome, String materiaprima, String imagem, String obs) {
        this.codigo = codigo;
        this.nome = nome;
        this.materiaprima = materiaprima;
        this.imagem = imagem;
        this.obs = obs;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMateriaprima() {
        return materiaprima;
    }

    public void setMateriaprima(String materiaprima) {
        this.materiaprima = materiaprima;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String campo1) {
        this.obs = campo1;
    }
}
