package popcornminer.thiagosoneghetti.com.br.popcornminer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import popcornminer.thiagosoneghetti.com.br.popcornminer.activity.GeradorQrCode;
import popcornminer.thiagosoneghetti.com.br.popcornminer.activity.NovaTransferenciaActivity;
import popcornminer.thiagosoneghetti.com.br.popcornminer.activity.TransferenciaActivity;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.ConexaoInternet;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Carteira;
import popcornminer.thiagosoneghetti.com.br.popcornminer.R;

public class TransferenciaAdpter extends BaseAdapter {
    //http://blog.alura.com.br/personalizando-uma-listview-no-android/

    TransferenciaActivity transferenciaActivity;
    List<Carteira> carteiras;
    Context context;

    public TransferenciaAdpter(Context context, List<Carteira> carteiras){
        this.context = context;
        this.carteiras = carteiras;
    }

    // Informando o total de itens da lista
    @Override
    public int getCount() {
        return carteiras.size();
    }

    // Devolvendo o item da lista pela posição
    @Override
    public Object getItem(int position) {
        return carteiras.get(position) ;
    }

    // Devolvendo o id do item da lista
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.adapter_lista_transferencia,null);
        final Carteira carteira = carteiras.get(position);

        // Pegando elementos da View pelo ID
        TextView txtDescricao = view.findViewById(R.id.itListaTransferenciaDescricao);
        TextView txtChavePublica = view.findViewById(R.id.itListaTransferenciaChavePublica);
        ImageView btnSaldo = view.findViewById(R.id.btSaldoLT);
        ImageView btnTransferir = view.findViewById(R.id.btTransferirLT);
        ImageView btnGerarQR = view.findViewById(R.id.btGerarQrCodeLT);

        // Inserindo os dados do elemento na view
        txtDescricao.setText(carteira.getDescricao());
        txtChavePublica.setText(carteira.getChave_publica());

        // Ao clicar no botão chama a chama a função de consulta de saldo
        btnSaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verifica se há conexão com a internet
                Boolean conexaoInternet = ConexaoInternet.verificaConexao(context);
                if ( conexaoInternet == true) {
                    // Método que faz a consulta no servidor
                    carteira.saldoUC(carteira, context);
                } else {
                    Toast.makeText(context, "Sem conexão com a internet.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Ao clicar no botão chama é passado a carteira para a activity de transferencia
        btnTransferir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), NovaTransferenciaActivity.class);
                intent.putExtra("carteira", carteira);
                view.getContext().startActivity(intent);
            }
        });

        // Ao clicar no botão chama é passado a carteira para a activity de gerar QR Code
        btnGerarQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), GeradorQrCode.class);
                intent.putExtra("carteira", carteira);
                view.getContext().startActivity(intent);
            }
        });

        return view;
    }

}
