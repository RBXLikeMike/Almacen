package com.example.almacen.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.AdapterView;

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
    View root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_insumos, container, false);
        rwInsumos = root.findViewById(R.id.listRecyclerView);
        registerForContextMenu(rwInsumos);

        inicializarFirebaseInsumo();

        return root;

    }

    //agregar el menu contextual
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_modificar, menu);
    }

    //agregar funcionalidad al menu contextual
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater){
        inflater.inflate(R.menu.menu_buscar,menu);
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
                        List<Insumo> collecion = listaInsumos.stream().filter
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
                listaOriginal.clear();

                for (DataSnapshot objSnap : snapshot.getChildren()) {
                    Insumo i = objSnap.getValue(Insumo.class);
                    listaInsumos.add(i);
                    listaOriginal.add(i);
                    //adapter = new ArrayAdapter<>(Lista.this, android.R.layout.simple_list_item_1, listaCantos);
                    adapter();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void adapter(){

        listAdapter = new ListAdapter(listaInsumos, getActivity(), getActivity().getApplication());
        rwInsumos.setHasFixedSize(true);
        rwInsumos.setLayoutManager(new LinearLayoutManager(getActivity()));
        rwInsumos.setAdapter(listAdapter);

   }

    private void alertaAcercaDe(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Acerca de");
        String mensaje = "\u00a9 Version 0.1";
        builder.setMessage(mensaje);
        builder.setCancelable(true);
        builder.setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
        AlertDialog alerta = builder.create();
        alerta.show();
    }

    private void nuevoCanto() {
        Intent i = new Intent(getActivity(), NuevoInsumoActivity.class);
        i.putExtra("getTipo", 0);
        startActivity(i);
    }

    private void verCanto(Insumo insumo) {
        Intent i = new Intent(getActivity(), GestionarFragment.class);
        i.putExtra("getID", insumo.getId());
        i.putExtra("getNombre", insumo.getNombre());
        i.putExtra("getDescripcion", insumo.getDescripcion());
        i.putExtra("getMarca", insumo.getMarca());
        i.putExtra("getExistencia", insumo.getExistencia());
        i.putExtra("getCantidadMin", insumo.getCant_minima());
        i.putExtra("getImagen", insumo.getImagen());

        startActivity(i);
        //overridePendingTransition(R.anim.left_in,R.anim.left_out);
        //finish();
    }

    private static List<Insumo> filter(List<Insumo> insumos, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<Insumo> filteredModelList = new ArrayList<>();

        for (Insumo insumo : insumos) {
            final String nombre = insumo.getNombre().toLowerCase();
            final String id = insumo.getId();
            if (nombre.contains(lowerCaseQuery) || id.equals(query)) {
                filteredModelList.add(insumo);
            }
        }

        return filteredModelList;
    }

    SearchView.OnQueryTextListener oyente = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String query) {
            //adapter.getFilter().filter(newText);
            final List<Insumo> filteredModelList = filter(listaInsumos, query);
            listAdapter.setItems(filteredModelList);
            return true;
        }
    };

}