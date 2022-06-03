package com.example.almacen.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.almacen.R;
import com.example.almacen.models.Insumo;
import com.example.almacen.ui.NuevoInsumoActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private final int limit = 5;
    private List<Insumo> mData;
    private LayoutInflater inflater;
    private Context context;
    private Application application;

    public ListAdapter(List<Insumo> itemList, Context context){
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
        this.application = application;
    }

    @Override
    public int getItemCount(){
        return mData.size();

        //if(mData.size() > limit) return limit;
        //else return mData.size();
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = inflater.inflate(R.layout.item_insumo2, null);
        return new ListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListAdapter.ViewHolder holder, final int position){
        holder.cv.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));
        holder.bindData(mData.get(position));
    }

    public void setItems(List<Insumo> items){mData=items;}

    public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView iconImage;
            TextView nombre, descripcion;
            CardView cv;
            ImageButton mas, menos;
            EditText cantidad;
            DatabaseReference dr;


            @SuppressLint("RestrictedApi")
            ViewHolder(View itemView){
                super(itemView);
                iconImage = itemView.findViewById(R.id.imagen);
                nombre  = itemView.findViewById(R.id.insumo);
                descripcion = itemView.findViewById(R.id.descripcion);
                //cant_existencia = itemView.findViewById(R.id.cantidad);
                cantidad = itemView.findViewById(R.id.cantidad);
                cv = itemView.findViewById(R.id.cv);
                mas = itemView.findViewById(R.id.mas);
                menos = itemView.findViewById(R.id.menos);

                FirebaseDatabase fd = FirebaseDatabase.getInstance();
                dr = fd.getReference("Insumos");

                nombre.setSelected(true);
                descripcion.setSelected(true);

                mas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int numero;
                        numero = Integer.parseInt(cantidad.getText().toString());
                        numero++;
                        cantidad.setText(String.valueOf(numero));

                        Insumo insumo = mData.get(getAbsoluteAdapterPosition());
                        insumo.setExistencia(numero);
                        dr.child(insumo.getId()).setValue(insumo);
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

                        Insumo insumo = mData.get(getAbsoluteAdapterPosition());
                        insumo.setExistencia(numero);
                        dr.child(insumo.getId()).setValue(insumo);
                    }
                });

                cantidad.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(!s.toString().isEmpty()) {
                            int numero = Integer.parseInt(s.toString());

                            Insumo insumo = mData.get(getAbsoluteAdapterPosition());
                            insumo.setExistencia(numero);
                            dr.child(insumo.getId()).setValue(insumo);
                        }
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

                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        context.startActivity(i);
                        //((Activity) context).overridePendingTransition(R.animator.from_r, R.animator.to_l);

                    }
                });
            }

            void bindData(final Insumo item){
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                DatabaseReference databaseReference = firebaseDatabase.getReference();

                DatabaseReference getImage = databaseReference.child("Insumos/" + item.getId() + "/imagen");

                getImage.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String link = dataSnapshot.getValue(String.class);

                        assert link != null;
                        if(!link.isEmpty()) Picasso.get().load(link).into(iconImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //if(!item.getImagen().isEmpty()) Picasso.get().load(item.getImagen()).into(iconImage);

                nombre.setText(item.getNombre());
                descripcion.setText(item.getDescripcion());
                cantidad.setText(String.valueOf(item.getExistencia()));
            }
    }

}
