package popcornminer.thiagosoneghetti.com.br.popcornminer;

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

public class CadastrarLoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button botaoCadastrar;
    private EditText editEmail;
    private EditText editSenha;
    private EditText editConfSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_login);

        botaoCadastrar = findViewById(R.id.btCadastrarLogin);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        editConfSenha = findViewById(R.id.editConfSenha);


        firebaseAuth = FirebaseAuth.getInstance();

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editEmail.getText().toString().equals("") || editSenha.getText().toString().equals("") || editConfSenha.getText().toString().equals("")){
                    // Verificando se os campos estão vazios, caso estejam apresenta uma mensagem informando.
                    if (editEmail.getText().toString().equals("") && editSenha.getText().toString().equals("") && editConfSenha.getText().toString().equals("")) {
                        Toast.makeText(CadastrarLoginActivity.this, "Digite seu e-mail e sua senha!", Toast.LENGTH_SHORT).show();
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
                        //Relização do Cadastro
                        firebaseAuth.createUserWithEmailAndPassword(editEmail.getText().toString(),editSenha.getText().toString())
                            .addOnCompleteListener(CadastrarLoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                // verificando se o usuário foi criado com sucesso
                                if (task.isSuccessful()){
                                    Toast.makeText(CadastrarLoginActivity.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                    // Deslogando usuário criado e jogando para tela de login
                                    firebaseAuth.signOut();
                                    Intent intent = new Intent(CadastrarLoginActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(CadastrarLoginActivity.this, "Usuário não foi cadastrado!", Toast.LENGTH_SHORT).show();
                                }
                                }
                            });
                    }else{
                        Toast.makeText(CadastrarLoginActivity.this, "Senhas não conferem!", Toast.LENGTH_SHORT).show();
                        editSenha.setText("");
                        editConfSenha.setText("");
                    }
                }


            }
        });


    }
}
