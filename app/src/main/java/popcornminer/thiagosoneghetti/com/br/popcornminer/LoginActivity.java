package popcornminer.thiagosoneghetti.com.br.popcornminer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button botaoLogin;
    private Button botaoActivityCadastrar;
    private EditText editEmailLogin;
    private EditText editSenhaLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        botaoLogin = findViewById(R.id.btLogin);
        botaoActivityCadastrar = findViewById(R.id.btActivityCadastrar);
        editEmailLogin = findViewById(R.id.editEmailLogin);
        editSenhaLogin = findViewById(R.id.editSenhaLogin);

        firebaseAuth = FirebaseAuth.getInstance();

        //Ir para página de cadastro de usuário
        botaoActivityCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,CadastrarLoginActivity.class);
                startActivity(intent);
            }
        });

        botaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editEmailLogin.getText().toString().equals("") || editSenhaLogin.getText().toString().equals("")){
                    // Verificando se os campos estão vazios, caso estejam apresenta uma mensagem informando.
                    if (editEmailLogin.getText().toString().equals("") && editSenhaLogin.getText().toString().equals("")){
                        Toast.makeText(LoginActivity.this, "Digite seu e-mail e sua senha!", Toast.LENGTH_SHORT).show();
                    }else if (editEmailLogin.getText().toString().equals("")){
                        Toast.makeText(LoginActivity.this, "Digite seu e-mail!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(LoginActivity.this, "Digite sua senha!", Toast.LENGTH_SHORT).show();
                    }
                }else{

                    //Login
                    firebaseAuth.signInWithEmailAndPassword(editEmailLogin.getText().toString(),editSenhaLogin.getText().toString())
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // verificando se usuário faz login

                                    if (task.isSuccessful()){
                                        Toast.makeText(LoginActivity.this, "Usuário logado com sucesso!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(LoginActivity.this, "Usuário não foi autenticado!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Verificar se usuário está logado
        if ( firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            Log.i("verificaUsuario", "Usuario está logado!");
        }else{
            Log.i("verificausuario", "Usuário não está logado");
        }
    }
}
