package popcornminer.thiagosoneghetti.com.br.popcornminer.Model;

import java.io.Serializable;

public class Carteira implements Serializable{
    private Long id;
    private String chave_publica;
    private String chave_privada;
    private String descricao;

    // Construtores
    public Carteira(Long id, String c_publica, String c_privada, String desc) {
        this.id = id;
        this.chave_publica = c_publica;
        this.chave_privada = c_privada;
        this.descricao = desc;
    }

    public Carteira(String c_publica, String c_privada, String desc) {
        this.chave_publica = c_publica;
        this.chave_privada = c_privada;
        this.descricao = desc;
    }

    // getters e setters
    public Long getId() {

        return id;
    }
    public void setId(Long id) {

        this.id = id;
    }

    public String getChave_publica() {
        return chave_publica;
    }

    public void setChave_publica(String chave_publica) {

        this.chave_publica = chave_publica;
    }

    public String getChave_privada() {

        return chave_privada;
    }
    public void setChave_privada(String chave_privada) {
        this.chave_privada = chave_privada;
    }

    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
