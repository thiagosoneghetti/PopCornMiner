package popcornminer.thiagosoneghetti.com.br.popcornminer.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import popcornminer.thiagosoneghetti.com.br.popcornminer.config.ConfiguracaoFirebase;

public class Usuario {

    private String id;
    private String nome;
    private String email;
    private String senha;

    public Usuario() {
    }

    public void salvar(){
        DatabaseReference referenciaDatabase = ConfiguracaoFirebase.getFirebase();
        // Adicionando um usuário a referencia do Firebase, em seguida o ID, e depois passando
        referenciaDatabase.child("usuarios").child( getId() ).setValue( this );

    }

    // @Exclude - é utilizado para o Firebase ignorar o método e não salvar no servidor
    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    // @Exclude - é utilizado para o Firebase ignorar o método e não salvar no servidor
    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
