package popcornminer.thiagosoneghetti.com.br.popcornminer.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import popcornminer.thiagosoneghetti.com.br.popcornminer.R;
import popcornminer.thiagosoneghetti.com.br.popcornminer.config.ConfiguracaoFirebase;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.Base64Custom;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.ConexaoInternet;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.Preferencias;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private Button botaoLogin;
    private TextView botaoActivityCadastrar;
    private EditText email;
    private EditText senha;
    private Usuario usuario;
    private Context context;
    private DatabaseReference firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Método que verifica se usuário ainda está logado, caso sim, ele não precisará inserir login e senha novamente
        verificarSeUsuarioLogado();

        context = this;
        // Recuperando os elementos da tela pelo ID
        botaoLogin = findViewById(R.id.btLogin);
        botaoActivityCadastrar = findViewById(R.id.btActivityCadastrar);
        email = findViewById(R.id.editEmailLogin);
        senha = findViewById(R.id.editSenhaLogin);

        //Ir para página de cadastro de usuário
        botaoActivityCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCadastrarUsuario();
            }
        });

        // Botão responsável por fazer login
        botaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificando se possui conexão com a internet
                Boolean conexaoInternet = ConexaoInternet.verificaConexao(context);
                if ( conexaoInternet == true ) {
                    usuario = new Usuario();
                    usuario.setEmail(email.getText().toString());
                    usuario.setSenha(senha.getText().toString());
                    // Verifica se algum campo está sem preencher
                    if (usuario.getEmail().equals("") || usuario.getSenha().equals("")) {
                        // Verificando se os campos estão vazios, caso estejam apresenta uma mensagem informando.
                        if (usuario.getEmail().equals("") && usuario.getSenha().equals("")) {
                            Toast.makeText(LoginActivity.this, "Digite seu e-mail e sua senha!", Toast.LENGTH_SHORT).show();
                        } else if (usuario.getEmail().equals("")) {
                            Toast.makeText(LoginActivity.this, "Digite seu e-mail!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Digite sua senha!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Metodo que faz a validação do login
                        validarLogin();
                    }
                } else {
                    Toast.makeText(context, "Sem conexão com a internet.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void verificarSeUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //Verificar se usuário está logado, caso sim, vai direto para tela principal
        if ( autenticacao.getCurrentUser() != null){
            abrirTelaPrincipal();
            finish();
        }
    }

    // Método que faz a validação do login
    private void validarLogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        // Fazer Login pelo email e senha inseridos
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(),usuario.getSenha())
        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // verificando se usuário faz login

                if (task.isSuccessful()){

                    // Convertendo o e-mail do usuário para Base 64 para gerar ID
                    String indentificadorUsuario = Base64Custom.codificarBase64( usuario.getEmail() );
                    usuario.setId( indentificadorUsuario );

                    // Salvando nao SharedPreferences o ID base 64 gerado pelo e-mail
                    // Convertendo e=mail para base 64, que será nosso ID único
                    Preferencias preferencias  = new Preferencias(context);
                    preferencias.salvarDados(indentificadorUsuario);

                    // Abre a pagina inicial do aplicativo
                    abrirTelaPrincipal();
                    // Mostra mensagem de boas vindas com o nome do usuário
                    mensagemBemVindo();

                }else{
                    // Tratamento de excessões
                    String erroLogin;
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        erroLogin = "Não existe conta vinculada a este email, ou a conta foi desativada!";
                    }catch (FirebaseAuthInvalidCredentialsException e) {
                        erroLogin = "Senha incorreta!";
                    }
                    catch (Exception e){
                        erroLogin = "Erro ao fazer login!";
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this, ""+erroLogin, Toast.LENGTH_SHORT).show();
                }
            }});

    }
    // metodo que vai para tela principal do aplicativo
    private void abrirTelaPrincipal (){
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        //Encerrando Activity de cadastro
        finish();
    }
    // metodo que vai para tela de cadastro de novo usuario
    private void abrirCadastrarUsuario (){
        Intent intent = new Intent(LoginActivity.this,CadastrarLoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Metodo para mostrar mensagem de boas vindas com nome do usuário
    public void mensagemBemVindo(){
        // Buscando o identificador do usuário atual para pesquisa no firebase
        Preferencias preferencias = new Preferencias(LoginActivity.this);

        String identificador = preferencias.getIdentificador();
        // Encontrando o usuário pelo seu identificador
        firebase = ConfiguracaoFirebase.getFirebase().child("usuarios").child(identificador);

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Usuário é instanciado, pegado seu nome para mostrar na tela
                Usuario usuario = dataSnapshot.getValue( Usuario.class );
                Toast.makeText(LoginActivity.this, "Seja Bem-Vindo, "+usuario.getNome()+"!", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
