package popcornminer.thiagosoneghetti.com.br.popcornminer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button botaoSaldo;
    private Button botaoCarteira;
    private Button botaoTransferencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Menu PopCornMiner");

        //botaoSaldo = (Button) findViewById(R.id.btSaldoId);
        botaoCarteira = (Button) findViewById(R.id.btCarteiraId);
        botaoTransferencia = (Button) findViewById(R.id.btTransferenciaId);
/*
        botaoSaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SaldoActivity.class);
                startActivity(intent);
            }
        });
*/
        botaoCarteira.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CarteiraActivity.class);
                startActivity(intent);
            }
        });

        botaoTransferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,TransferenciaActivity.class);
                startActivity(intent);
            }
        });
    }
}
