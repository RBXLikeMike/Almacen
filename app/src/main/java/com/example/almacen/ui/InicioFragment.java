package com.example.almacen.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.almacen.R;
import com.example.almacen.tools.ViewPageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class InicioFragment extends Fragment {

    ViewPageAdapter adapter;
    ViewPager2 viewPager2;
    View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_inicio, container, false);

        viewPager2 = root.findViewById(R.id.visualisacion);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.purple_500));
        }

        adapter = new ViewPageAdapter(getChildFragmentManager(), getLifecycle());

        //Añadir los fragmentos
        adapter.addFragment(new FaltantesFragment());
        adapter.addFragment(new InsumosFragment());

        viewPager2.setAdapter(adapter);

        TabLayout tableLayout =  root.findViewById(R.id.tablelayout);

        new TabLayoutMediator(tableLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                switch (position){
                    case 0: tab.setText("Faltantes");
                        break;
                    case 1: tab.setText("Insumos");
                        break;
                }



            }
        }).attach();

        root.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), NuevoInsumoActivity.class);
                i.putExtra("getTipo",0);
                startActivity(i);
            }
        });

        return root;
    }

}
