package popcornminer.thiagosoneghetti.com.br.popcornminer.Model;

import android.content.Context;

import java.io.Serializable;

public class Transferencia implements Serializable {
    private Long id;
    private String chave_privada;
    private String chave_publica;
    private Float valor;
    private String message;

    public Transferencia (Long id, String chave_privada, String chave_publica, Float valor){
        this.id = id;
        this.chave_privada = chave_privada;
        this.chave_publica = chave_publica;
        this.valor = valor;
    }

    public Transferencia (String chave_privada, String chave_publica, Float valor){
        this.chave_privada = chave_privada;
        this.chave_publica = chave_publica;
        this.valor = valor;
    }

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

    public void setChave_privada(String chave_privada) {
        this.chave_privada = chave_privada;
    }
    public String getChave_privada() {
        return chave_privada;
    }

    public Float getValor() {
        return valor;
    }
    public void setValor(Float valor) {
        this.valor = valor;
    }

    public String getMensagem() {
        return message;
    }

    public void setMensagem(String mensagem) {
        this.message = mensagem;
    }
}
