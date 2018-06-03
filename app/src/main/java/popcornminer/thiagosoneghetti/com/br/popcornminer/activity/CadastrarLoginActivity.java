package popcornminer.thiagosoneghetti.com.br.popcornminer.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import popcornminer.thiagosoneghetti.com.br.popcornminer.R;
import popcornminer.thiagosoneghetti.com.br.popcornminer.config.ConfiguracaoFirebase;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Usuario;

public class CadastrarLoginActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private Button botaoCadastrar;
    private EditText editNome;
    private EditText editEmail;
    private EditText editSenha;
    private EditText editConfSenha;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_login);

        botaoCadastrar = findViewById(R.id.btCadastrarLogin);
        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        editConfSenha = findViewById(R.id.editConfSenha);

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editEmail.getText().toString().equals("") || editSenha.getText().toString().equals("") || editConfSenha.getText().toString().equals("")){
                    // Verificando se os campos estão vazios, caso estejam apresenta uma mensagem informando.
                    if (editEmail.getText().toString().equals("") && editSenha.getText().toString().equals("")
                            && editConfSenha.getText().toString().equals("") && editNome.getText().toString().equals("")) {
                        Toast.makeText(CadastrarLoginActivity.this, "Digite seu nome, e-mail e sua senha!", Toast.LENGTH_SHORT).show();
                    }else if (editNome.getText().toString().equals("")){
                        Toast.makeText(CadastrarLoginActivity.this, "Digite seu nome!", Toast.LENGTH_SHORT).show();
                    }else if (editEmail.getText().toString().equals("")){
                        Toast.makeText(CadastrarLoginActivity.this, "Digite seu e-mail!", Toast.LENGTH_SHORT).show();
                    }else if (editSenha.getText().toString().equals("")){
                        Toast.makeText(CadastrarLoginActivity.this, "Digite sua senha!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(CadastrarLoginActivity.this, "Digite a confirmação de senha!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    // Se a senha for igual a confirmação de senha fará o cadastro
                    if (editSenha.getText().toString().equals(editConfSenha.getText().toString())){

                        usuario = new Usuario();
                        usuario.setNome( editNome.getText().toString() );
                        usuario.setEmail( editEmail.getText().toString() );
                        usuario.setSenha( editSenha.getText().toString() );

                        // Método responsável por salvar usuário no Firebase
                        cadastrarUsuario();

                    }else{
                        // Verificando se a senha e confirmação de senha conferem
                        Toast.makeText(CadastrarLoginActivity.this, "Senhas não conferem!", Toast.LENGTH_SHORT).show();
                        editSenha.setText("");
                        editConfSenha.setText("");
                    }
                }


            }
        });
    }

    private void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //Relização do Cadastro
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha())
                .addOnCompleteListener(CadastrarLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // verificando se o usuário foi criado com sucesso
                        if (task.isSuccessful()){
                            Toast.makeText(CadastrarLoginActivity.this, "Sucesso ao cadastrar usuário!", Toast.LENGTH_SHORT).show();

                            // Recuperando usuário criado no Firebase
                            FirebaseUser usuarioFirebase = task.getResult().getUser();
                            // Pegando o ID do usuário criado no Firebase
                            usuario.setId( usuarioFirebase.getUid() );
                            // Método responsável por salvar os dados do usuário no Firebase
                            usuario.salvar();

                            Intent intent = new Intent(CadastrarLoginActivity.this, LoginActivity.class);
                            startActivity(intent);

                            // Deslogando usuário criado e jogando para tela de login
                            autenticacao.signOut();
                            //Encerrando Activity de cadastro
                            finish();

                            }else{
                                // Tratamento de excessões
                                String erroExcessao;
                                try {
                                    throw task.getException();
                                }catch (FirebaseAuthWeakPasswordException e){
                                    erroExcessao = "Digite uma senha mais forte, contendo mais caracteres e com letras e números!";
                                }catch (FirebaseAuthInvalidCredentialsException e){
                                    erroExcessao = "E-mail digitado é inválido, digite um novo e-mail!";
                                }catch (FirebaseAuthUserCollisionException e){
                                    erroExcessao = "E-mail já existente, digite um novo e-mail!";
                                }catch (Exception e){
                                    erroExcessao = "Erro ao efeturar cadastro!";
                                    e.printStackTrace();
                                }
                                Toast.makeText(CadastrarLoginActivity.this, ""+erroExcessao, Toast.LENGTH_SHORT).show();
                            }
                    }
                });

    };

}
