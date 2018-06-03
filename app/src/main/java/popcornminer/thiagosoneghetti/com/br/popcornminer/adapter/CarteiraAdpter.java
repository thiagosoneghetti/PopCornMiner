package popcornminer.thiagosoneghetti.com.br.popcornminer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Carteira;
import popcornminer.thiagosoneghetti.com.br.popcornminer.R;

public class CarteiraAdpter extends BaseAdapter {
    //http://blog.alura.com.br/personalizando-uma-listview-no-android/

    List<Carteira> carteiras;
    Context context;

    public CarteiraAdpter(Context context, List<Carteira> carteiras){
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_lista_carteira,null);
        Carteira carteira = carteiras.get(position);

        // Pegando elementos da View pelo ID
        TextView txtDescricao = (TextView) view.findViewById(R.id.itListaCarteiraDescricao);
        TextView txtChavePublica = (TextView) view.findViewById(R.id.itListaCarteiraChavePublica);

        // Inserindo os dados do elemento na view
        txtDescricao.setText(carteira.getDescricao());
        txtChavePublica.setText(carteira.getChave_publica());

        return view;
    }

}
