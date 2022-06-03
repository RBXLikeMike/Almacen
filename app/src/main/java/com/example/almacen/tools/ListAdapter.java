package com.example.almacen.tools;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.almacen.R;
import com.example.almacen.models.Insumo;
import com.example.almacen.ui.NuevoInsumoActivity;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<Insumo> mData;
    private LayoutInflater inflater;
    private Context context;
    private Application application;

    public ListAdapter(List<Insumo> itemList, Context context, Application application){
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
        this.application = application;
    }

    @Override
    public int getItemCount(){return mData.size();}

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = inflater.from(parent.getContext()).inflate(R.layout.item_insumo, null, false);
        return new ListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListAdapter.ViewHolder holder, final int position){
        //holder.cv.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));
        holder.bindData(mData.get(position));

    }

    public void setItems(List<Insumo> items){mData=items;}

    public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView iconImage;
            TextView nombre, descripcion, cant_existencia, cantidad;
            CardView cv;
            ImageButton mas, menos;
            EditText cantidadsumar;

            ViewHolder(View itemView){
                super(itemView);
                iconImage = itemView.findViewById(R.id.imagen);
                nombre  = itemView.findViewById(R.id.insumo);
                descripcion = itemView.findViewById(R.id.descripcion);
                cant_existencia = itemView.findViewById(R.id.cantidad);
                cantidad = itemView.findViewById(R.id.cantidad);
                //cv = itemView.findViewById(R.id.cv);
                mas = itemView.findViewById(R.id.mas);
                menos = itemView.findViewById(R.id.menos);

                nombre.setSelected(true);
                descripcion.setSelected(true);

                mas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int numero;
                        numero = Integer.parseInt(cantidad.getText().toString());
                        numero++;
                        cantidad.setText(String.valueOf(numero));
                    }
                });
                menos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int numero;
                        numero = Integer.parseInt(cantidad.getText().toString());
                        if (numero >=0) {
                            numero--;
                        }
                        cantidad.setText(String.valueOf(numero));
                    }
                });


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Insumo insumo = mData.get(getAbsoluteAdapterPosition());

                        Intent i = new Intent(context, NuevoInsumoActivity.class);
                        i.putExtra("getTipo", 1);
                        i.putExtra("getID", insumo.getId());
                        i.putExtra("getNombre", insumo.getNombre());
                        i.putExtra("getDescripcion", insumo.getDescripcion());
                        i.putExtra("getMarca", insumo.getMarca());
                        i.putExtra("getExistencia", insumo.getExistencia());
                        i.putExtra("getCantidadMin", insumo.getCant_minima());
                        i.putExtra("getImagen", insumo.getImagen());

                        context.startActivity(i);
                        ((Activity) context).overridePendingTransition(R.animator.from_r, R.animator.to_l);

                    }
                });
            }

            void bindData(final Insumo item){
                //iconImage.setColorFilter(Color.parseColor(item.getImagen()), PorterDuff.Mode.SRC_IN);
                nombre.setText(item.getNombre());
                descripcion.setText(item.getDescripcion());
                cant_existencia.setText(String.valueOf(item.getExistencia()));
            }
    }

}
