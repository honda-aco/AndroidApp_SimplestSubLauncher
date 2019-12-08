package com.ns.aco.sp3.view;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ns.aco.sp3.R;

public class FragmentPager extends Fragment {

    private final static String BACKGROUND_COLOR = "background_color";
    private static FragmentPager _fragmentPager0;
    private static FragmentPager _fragmentPager1;
    private static FragmentPager _fragmentPager2;

    public static FragmentPager newInstance(int position) {
    // public static FragmentPager newInstance(@ColorRes int IdRes) {
        switch (position) {
            case 0:
                if (_fragmentPager0 == null){
                    _fragmentPager0 = new FragmentPager();
                }
                return _fragmentPager0;
            case 1:
                if (_fragmentPager1 == null){
                    _fragmentPager1 = new FragmentPager();
                }
                return _fragmentPager1;
            case 2:
                if (_fragmentPager2 == null){
                    _fragmentPager2 = new FragmentPager();
                }
                return _fragmentPager2;
        }
        // Bundle bundle = new Bundle();
        // bundle.putInt(BACKGROUND_COLOR, IdRes);
        // _fragmentPager.setArguments(bundle);
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, null);
        // LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fragment_main_linearlayout);
        // linearLayout.setBackgroundResource(getArguments().getInt(BACKGROUND_COLOR));
        return view;
    }
}
