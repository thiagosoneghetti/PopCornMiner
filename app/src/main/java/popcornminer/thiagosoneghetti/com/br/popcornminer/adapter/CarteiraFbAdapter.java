package popcornminer.thiagosoneghetti.com.br.popcornminer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import popcornminer.thiagosoneghetti.com.br.popcornminer.R;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Carteira;

public class CarteiraFbAdapter extends ArrayAdapter<Carteira>{

    private ArrayList<Carteira> carteiras;
    private Context context;

    public CarteiraFbAdapter(Context c, ArrayList<Carteira> objects) {
        super(c, 0,  objects);
        this.carteiras = objects;
        this.context = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //View view = null;

        View view = LayoutInflater.from(context).inflate(R.layout.adapter_lista_carteira,null);

        // Verificar se a lista de carteiras está vazia
        if ( carteiras != null ){
            Log.i("carteiras","!= null");
            // Inicializar o objeto para montagem da view
            // Utilizando o serviço global de montagem de layout
            //LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            // Monta view a partir do XML
            //view = inflater.inflate(R.layout.adapter_lista_carteira, parent, false);

            // Pegando elementos da View pelo ID
            TextView txtDescricao =  view.findViewById(R.id.itListaCarteiraDescricao);
            TextView txtChavePublica = view.findViewById(R.id.itListaCarteiraChavePublica);

            // Inserindo os dados do elemento na view
            Carteira carteira = carteiras.get( position );

            txtDescricao.setText(carteira.getDescricao());
            txtChavePublica.setText(carteira.getChave_publica());

        } else {
            Log.i("carteiras","== null");
        }

        return view;
    }
}
