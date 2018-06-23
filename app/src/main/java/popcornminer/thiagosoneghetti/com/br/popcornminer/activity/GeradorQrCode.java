package popcornminer.thiagosoneghetti.com.br.popcornminer.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import popcornminer.thiagosoneghetti.com.br.popcornminer.R;
import popcornminer.thiagosoneghetti.com.br.popcornminer.config.Firebase;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Carteira;

public class GeradorQrCode extends AppCompatActivity {

    private FirebaseAuth usuarioFirebase;
    private Carteira carteira;
    private TextView descricaoCarteira;
    private ImageView imagemQrCode;
    private Button btnVoltar;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerador_qr_code);

        // Chamando o objeto do Firebase que é responsável pela autenticação
        usuarioFirebase = Firebase.getFirebaseAutenticacao();
        // Pegando o contexto atual
        context = this;
        // Verificando se o usuário está logado, caso não, voltará para tela de inicio
        verificarSeUsuarioLogado();

        // Configurações menu superior (ActionBar)
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setIcon(R.mipmap.ic_launcher_foreground); // Atribuir um ícone na actionbar
        actionBar.setDisplayShowHomeEnabled(true); // Habilitar o título da barra de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilitar botão voltar
        
        // Recuperando os elementos da tela pelo ID
        descricaoCarteira = findViewById(R.id.idDescricaoCarteiraQR);
        imagemQrCode = findViewById(R.id.idImageViewQR);
        btnVoltar = findViewById(R.id.idBtnVoltarQR);

        // Recuperando os dados que foram passados na Activity anterior
        final Intent intentQR = getIntent();
        if(intentQR.getSerializableExtra("carteira") != null) {
            carteira = (Carteira) intentQR.getSerializableExtra("carteira");
            // Passando o nome da carteira selecionada para a tela, mostrando o nome da carteira na tela de transferencia
            descricaoCarteira.setText(carteira.getDescricao());
        } else {
            Toast.makeText(this, "carteira vazia", Toast.LENGTH_SHORT).show();
        }

        // Chamando método de geração de QR Code
        gerarQrCode(carteira);

        // Botão voltar para activity de transferencia
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTransf = new Intent(GeradorQrCode.this, TransferenciaActivity.class);
                startActivity(intentTransf);
                finish();
            }
        });

    }

    // Método de geração de QR Code
    private void gerarQrCode(Carteira carteira) {
        String chavepublica = carteira.getChave_publica();
        // Criando objeto do tipo MultiFormatWriter
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try{
            // Criando uma classe do objeto BitMatrix que recebe o obeto MultiFormatWriter, passando nele o texto a ser codificado, formato e tamanho
            BitMatrix bitMatrix = multiFormatWriter.encode(chavepublica, BarcodeFormat.QR_CODE, 2000,2000);
            // Criando objeto do tipo BarcodeEncoder
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            // Criando um bitmap que recebe o objeto barcodeEncoder, que utiliza o  bitMatrix para criação do bitmap
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            // Passando o bitmap criado para o ImageView que será mostrado na tela
            imagemQrCode.setImageBitmap(bitmap);

        } catch (WriterException e){
            e.printStackTrace();
        }

    }

    private void verificarSeUsuarioLogado(){
        usuarioFirebase = Firebase.getFirebaseAutenticacao();
        //Verificar se usuário está logado, caso não, volta para tela de login
        if ( usuarioFirebase.getCurrentUser() == null){
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            // Fecha todas activitys que estavam na fila
            finishAffinity();
        }
    }


    // Criação do Menu na action bar, onde é possivel fazer logout, ir para outras telas
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_transferencia,menu);
        if(usuarioFirebase.getCurrentUser() != null) {
            // Mudando o texto do botão sair para mostar Sair: Nome do usuário
            // Mudando o texto do botão sair para mostar Sair: Nome do usuário
            MenuItem menuItem = menu.findItem(R.id.bt_mtransf_sair);
            menuItem.setTitle("Sair: " + usuarioFirebase.getCurrentUser().getDisplayName());
        }

        return super.onCreateOptionsMenu(menu);
    }
    // Opções que foram configuradas para aparecer no menu, são acões para irem para outras telas, e fazer logout
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent btVoltar = new Intent(GeradorQrCode.this,TransferenciaActivity.class);
                startActivity(btVoltar);
                finish();
                break;
            case R.id.bt_mtransf_home:
                Intent irHome = new Intent(GeradorQrCode.this,MainActivity.class);
                startActivity(irHome);
                finish();
                break;
            case R.id.bt_mtransf_carteira:
                Intent irCarteira = new Intent(GeradorQrCode.this,CarteiraActivity.class);
                startActivity(irCarteira);
                finish();
                break;
            case R.id.bt_mtransf_sair:
                // Desconecta o usuário atual do aplicativo
                Toast.makeText(context, "Usuário " + usuarioFirebase.getCurrentUser().getDisplayName() +" desconectado.", Toast.LENGTH_SHORT).show();
                usuarioFirebase.signOut();
                Intent irLogin = new Intent(context, LoginActivity.class);
                startActivity(irLogin);
                // Fecha todas activitys que estavam na fila
                finishAffinity();
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }

}
