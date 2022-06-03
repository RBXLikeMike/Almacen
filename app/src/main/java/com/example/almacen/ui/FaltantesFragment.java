package com.example.almacen.ui;

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

import com.example.almacen.R;
import com.example.almacen.models.Insumo;
import com.example.almacen.tools.ListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FaltantesFragment extends Fragment {
    final List<Insumo> listaInsumos = new ArrayList<>();
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    View root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_faltantes, container, false);
        recyclerView = root.findViewById(R.id.listRecyclerViewF);
        registerForContextMenu(recyclerView);

        inicializarFirebase();

        return root;

    }

    //agregar el menu contextual
    /*@Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_modificar, menu);
    }

    //agregar funcionalidad al menu contextual
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }*/

    /*@Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater){
        inflater.inflate(R.menu.menu_lista,menu);

        MenuItem item = menu.findItem(R.id.buscar);

        SearchView buscador = (SearchView) item.getActionView();
        buscador.setOnQueryTextListener(oyente);
        buscador.setQueryHint("Buscar");

        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nuevoCanto:
                nuevoCanto();
                break;
            case R.id.info:
                alertaAcercaDe();
                break;
        }
        return true;
    }

     */

    private void inicializarFirebase() {
        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        DatabaseReference dr = fd.getReference("Insumos");

        dr.orderByChild("nombre").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaInsumos.clear();

                for (DataSnapshot objSnap : snapshot.getChildren()) {
                    Insumo i = objSnap.getValue(Insumo.class);

                    if(i.getExistencia() < i.getCant_minima()) {
                        listaInsumos.add(i);

                        //adapter = new ArrayAdapter<>(Lista.this, android.R.layout.simple_list_item_1, listaCantos);

                        listAdapter = new ListAdapter(listaInsumos, getActivity(), getActivity().getApplication());
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(listAdapter);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        /*Intent i = new Intent(Lista.this, Nuevo.class);
        i.putExtra("getTipo", 0);
        startActivity(i);
        overridePendingTransition(R.anim.left_in,R.anim.left_out);
        finish();*/
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