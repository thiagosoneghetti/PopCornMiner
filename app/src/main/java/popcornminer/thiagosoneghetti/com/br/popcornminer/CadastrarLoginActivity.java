package popcornminer.thiagosoneghetti.com.br.popcornminer;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_login);

        botaoCadastrar = findViewById(R.id.btCadastrarLogin);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);

        firebaseAuth = FirebaseAuth.getInstance();

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editEmail.getText().toString().equals("") || editSenha.getText().toString().equals("")){
                    // Verificando se os campos esão vazios, caso estejam apresenta uma mensagem informando.
                    if (editEmail.getText().toString().equals("") && editSenha.getText().toString().equals("")){
                        Toast.makeText(CadastrarLoginActivity.this, "Digite seu e-mail e sua senha!", Toast.LENGTH_SHORT).show();
                    }else if (editEmail.getText().toString().equals("")){
                        Toast.makeText(CadastrarLoginActivity.this, "Digite seu e-mail!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(CadastrarLoginActivity.this, "Digite sua senha!", Toast.LENGTH_SHORT).show();
                    }
                }else{

                    //Cadastro
                    firebaseAuth.createUserWithEmailAndPassword(editEmail.getText().toString(),editSenha.getText().toString())
                            .addOnCompleteListener(CadastrarLoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // verificando se o usuário foi criado com sucesso

                                    if (task.isSuccessful()){
                                        Toast.makeText(CadastrarLoginActivity.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
// Aqui será necessário ir para activity de login
                                    }else{
                                        Toast.makeText(CadastrarLoginActivity.this, "Erro ao cadastrar usuário!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }


            }
        });


    }
}
