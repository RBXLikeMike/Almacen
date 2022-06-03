package com.example.almacen.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.almacen.models.Insumo;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Operaciones {
    private static final int File = 1 ;
    private static final int RESULT_OK = 1;

    DatabaseReference dr;
    Context contexto;

    public Operaciones(Context contexto) {
        this.contexto = contexto;

        FirebaseApp.initializeApp(contexto);
        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        dr = fd.getReference("Insumos");
    }

    public void agregar(Insumo insumo) {
        dr.child(insumo.getId()).setValue(insumo);

        Toast.makeText(contexto, "Insumo agregado", Toast.LENGTH_SHORT).show();
    }

    public void modificar(Insumo insumo) {
        dr.child(insumo.getId()).setValue(insumo);

        Toast.makeText(contexto, "Insumo modificado", Toast.LENGTH_SHORT).show();
    }

    public void eliminar(Insumo insumo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);

        builder.setTitle("Confirmar");
        builder.setMessage("¿Desea eliminar el insumo?");

        builder.setPositiveButton("SI", (dialog, which) -> {
            dr.child(insumo.getId()).removeValue();

            Toast.makeText(contexto, "Insumo eliminado", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());

        AlertDialog alert = builder.create();
        alert.show();
    }

    public List<Insumo> consultar() {
        final List<Insumo> listaInsumos = new ArrayList<>();

        dr.orderByChild("nombre").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot objSnap : snapshot.getChildren()) {
                    Insumo i = objSnap.getValue(Insumo.class);
                    listaInsumos.add(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return listaInsumos;
    }

}
