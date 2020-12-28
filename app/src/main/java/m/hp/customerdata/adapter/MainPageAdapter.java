package m.hp.customerdata.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class MainPageAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentActivities;

    public MainPageAdapter(@NonNull FragmentManager fm, int behavior, List<Fragment> fragments) {
        super(fm, behavior);
        fragmentActivities = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentActivities.get(position);
    }

    @Override
    public int getCount() {
        return fragmentActivities.size();
    }
}
