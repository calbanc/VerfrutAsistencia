package com.verfrut.grupoverfrut_asistencia.ui.Instalacion;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.verfrut.grupoverfrut_asistencia.R;
import com.verfrut.grupoverfrut_asistencia.Secciones.Secciones;
import com.verfrut.grupoverfrut_asistencia.Utilidades.Utilidades;
import com.verfrut.grupoverfrut_asistencia.ui.Consultas.MarcacionFragment;

public class ContenedorInstalacion extends Fragment {

    private HomeViewModel homeViewModel;
    private AppBarLayout appBar;
    private TabLayout pestanas;
    private ViewPager viewPager;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        if (Utilidades.rotacion == 0) {
            View parent = (View) container.getRootView();
            if (appBar == null) {
                appBar = parent.findViewById(R.id.appBar);
                pestanas = new TabLayout(getActivity());
                pestanas.setTabMode(TabLayout.MODE_SCROLLABLE);

                pestanas.setTabTextColors(Color.parseColor("#9fa8da"), Color.parseColor("#FFFFFF"));

                appBar.addView(pestanas);
                viewPager = (ViewPager) root.findViewById(R.id.vpinstalacion);
                llenarviewpager();
                viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    }

                });
                pestanas.setupWithViewPager(viewPager);
            }
        }else{
            Utilidades.rotacion=1;
        }

        return root;
    }

    private void llenarviewpager() {
        Secciones adapter=new Secciones(getFragmentManager());

        adapter.addfragment(new InstalacionFragment(),"Instalar Equipo");
        adapter.addfragment(new RetiroFragment(),"Retirar Equipos");
        viewPager.setAdapter(adapter);
    }
    public void onDestroyView() {
        super.onDestroyView();
        if(Utilidades.rotacion==0){
            appBar.removeView(pestanas);
        }
    }
}