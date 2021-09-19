package com.verfrut.grupoverfrut_asistencia.ui.Consultas;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.verfrut.grupoverfrut_asistencia.R;
import com.verfrut.grupoverfrut_asistencia.Secciones.Secciones;
import com.verfrut.grupoverfrut_asistencia.Utilidades.Utilidades;

public class ContenedorConsulta extends Fragment {
    private AppBarLayout appBar;
    private TabLayout pestanas;
    private ViewPager viewPager;

    private SlideshowViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        if (Utilidades.rotacion == 0) {
            View parent = (View) container.getRootView();
            if (appBar == null) {
                appBar = parent.findViewById(R.id.appBar);
                pestanas = new TabLayout(getActivity());
                pestanas.setTabMode(TabLayout.MODE_SCROLLABLE);

                pestanas.setTabTextColors(Color.parseColor("#9fa8da"), Color.parseColor("#FFFFFF"));

                appBar.addView(pestanas);
                viewPager = (ViewPager) root.findViewById(R.id.vpconsulta);
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



        /*final TextView textView = root.findViewById(R.id.text_slideshow);
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;

    }

    private void llenarviewpager() {
        Secciones adapter=new Secciones(getFragmentManager());
        adapter.addfragment(new ListarMarcaciones(),"Listar Marcacion");
        adapter.addfragment(new MarcacionFragment(),"Consulta Marcacion");


        viewPager.setAdapter(adapter);

    }

    public void onDestroyView() {
        super.onDestroyView();
        if(Utilidades.rotacion==0){
            appBar.removeView(pestanas);
        }
    }

}