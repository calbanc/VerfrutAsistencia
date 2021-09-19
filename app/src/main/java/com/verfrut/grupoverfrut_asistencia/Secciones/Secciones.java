package com.verfrut.grupoverfrut_asistencia.Secciones;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class Secciones extends FragmentStatePagerAdapter {
    private final List<Fragment> listafragments=new ArrayList<>();
    private final List<String>listatitulos=new ArrayList<>();

    public Secciones(FragmentManager fm) {
        super(fm);
    }
    public Secciones(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }
    public CharSequence getPageTitle(int position) {
        return listatitulos.get(position);
    }
    @Override
    public Fragment getItem(int position) {
        return listafragments.get(position);
    }
    @Override
    public int getCount() {
        return listafragments.size();
    }

    public void addfragment(Fragment fragment, String titulo){

        listafragments.add(fragment);
        listatitulos.add(titulo);
    }
}
