package popcornminer.thiagosoneghetti.com.br.popcornminer.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import popcornminer.thiagosoneghetti.com.br.popcornminer.R;
import popcornminer.thiagosoneghetti.com.br.popcornminer.config.ConfiguracaoFirebase;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private Button botaoLogin;
    private TextView botaoActivityCadastrar;
    private EditText email;
    private EditText senha;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        botaoLogin = findViewById(R.id.btLogin);
        botaoActivityCadastrar = findViewById(R.id.btActivityCadastrar);
        email = findViewById(R.id.editEmailLogin);
        senha = findViewById(R.id.editSenhaLogin);

        //Ir para página de cadastro de usuário
        botaoActivityCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCadastroUsuario();
            }
        });

        botaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usuario = new Usuario();
                usuario.setEmail( email.getText().toString() );
                usuario.setSenha( senha.getText().toString() );

                if (usuario.getEmail().equals("") || usuario.getSenha().equals("")){
                    // Verificando se os campos estão vazios, caso estejam apresenta uma mensagem informando.
                    if (usuario.getEmail().equals("") && usuario.getSenha().equals("")){
                        Toast.makeText(LoginActivity.this, "Digite seu e-mail e sua senha!", Toast.LENGTH_SHORT).show();
                    }else if (usuario.getEmail().equals("")){
                        Toast.makeText(LoginActivity.this, "Digite seu e-mail!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(LoginActivity.this, "Digite sua senha!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    validarLogin();
                }
            }
        });
    }

    private void validarLogin(){

        // Fazer Login
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(),usuario.getSenha())
        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // verificando se usuário faz login

                if (task.isSuccessful()){
                    FirebaseUser usuarioFirebase = task.getResult().getUser();
                    // Buscando e-mail do usuário no Firebase
                    String email = usuarioFirebase.getEmail();

                    abrirTelaPrincipal();
                    Toast.makeText(LoginActivity.this, "Logado com "+ email +". Seja Bem-Vindo!", Toast.LENGTH_SHORT).show();
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

    private void abrirTelaPrincipal (){
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        //Encerrando Activity de cadastro
        finish();
    }

    private void abrirCadastroUsuario (){
        Intent intent = new Intent(LoginActivity.this,CadastrarLoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Verificar se usuário está logado
        if ( autenticacao.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            Log.i("verificaUsuario", "Usuario está logado!");
        }else{
            Log.i("verificausuario", "Usuário não está logado");
        }
    }
}
