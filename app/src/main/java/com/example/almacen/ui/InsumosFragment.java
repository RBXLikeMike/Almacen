package com.example.almacen.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.almacen.tools.ListAdapter;
import com.example.almacen.models.Insumo;
import com.example.almacen.R;
import com.google.android.gms.common.util.JsonUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class InsumosFragment extends Fragment {
    final List<Insumo> listaInsumos = new ArrayList<>();
    final List<Insumo> listaOriginal = new ArrayList<>();
    private RecyclerView rwInsumos;
    private ListAdapter listAdapter;
    private TextView tvAviso;
    View root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.purple_500));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_insumos, container, false);
        rwInsumos = root.findViewById(R.id.listRecyclerView);
        tvAviso = root.findViewById(R.id.tvAviso);

        inicializarFirebaseInsumo();

        return root;

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater){
        //inflater.inflate(R.menu.menu_buscar,menu);
        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView buscador = (SearchView) item.getActionView();
        buscador.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String s) {
                int longitud = s.length();
                if(longitud == 0)
                {
                    listaInsumos.clear();
                    listaInsumos.addAll(listaOriginal);
                }else{
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        List<Insumo> collecion = listaOriginal.stream().filter
                                (i->i.getNombre().toLowerCase().contains(s.toLowerCase())).
                                collect(Collectors.toList());
                        listaInsumos.clear();
                        listaInsumos.addAll(collecion);
                    }else {
                        listaInsumos.clear();
                        for (Insumo z: listaOriginal) {
                            if (z.getNombre().toLowerCase().contains(s.toLowerCase())){
                                listaInsumos.add(z);
                            }
                        }
                    }
                }
                adapter();
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void inicializarFirebaseInsumo() {
        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        DatabaseReference dr = fd.getReference("Insumos");
        System.out.println("inicialiso la base de datos");

        dr.orderByChild("nombre").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaInsumos.clear();

                for (DataSnapshot objSnap : snapshot.getChildren()) {
                    if(objSnap!=null) {
                        Insumo i = objSnap.getValue(Insumo.class);
                        listaInsumos.add(i);
                    }

                    adapter();
                }

                if(listaInsumos.size() == 0) tvAviso.setVisibility(View.VISIBLE);
                else tvAviso.setVisibility(View.INVISIBLE);

                listaOriginal.clear();
                listaOriginal.addAll(listaInsumos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void adapter(){
        listAdapter = new ListAdapter(listaInsumos, getActivity());
        rwInsumos.setHasFixedSize(true);
        rwInsumos.setLayoutManager(new LinearLayoutManager(getActivity()));
        rwInsumos.setAdapter(listAdapter);
    }
}