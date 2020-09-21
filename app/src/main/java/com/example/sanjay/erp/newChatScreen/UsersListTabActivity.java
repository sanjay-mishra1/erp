package com.example.sanjay.erp.newChatScreen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.erp.R;
import com.example.sanjay.erp.newChatScreen.ActivityFragment;
import com.example.sanjay.erp.newChatScreen.AllUser;
import static com.example.sanjay.erp.newChatScreen.AllUser.searchKey;

public class UsersListTabActivity extends AppCompatActivity {
    static public int Totalcount=0;
    static public boolean isListLoaded=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list_tab);
        TextView textView=findViewById(R.id.title);
        textView.setText(getSharedPreferences("USERCREDENTIALS",MODE_PRIVATE).getString("USERNAME","User"));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Glide.with(this).load(getSharedPreferences("USERCREDENTIALS",MODE_PRIVATE).getString("PROFILEIMG","User")).apply(RequestOptions.circleCropTransform()).into((ImageView) findViewById(R.id.img1));
        boolean isStaff = getSharedPreferences("USERCREDENTIALS", MODE_PRIVATE).getString("TYPE", "").equals("STAFF");
         if (!isStaff) {
             SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

             ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
             mViewPager.setAdapter(mSectionsPagerAdapter);

             TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

             mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
             tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
         }else{
             findViewById(R.id.frameBack).setVisibility(View.VISIBLE);
             FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
             fragmentTransaction2.replace(R.id.frameBack,new AllUser());
             fragmentTransaction2.commit();
             findViewById(R.id.tabs).setVisibility(View.GONE);
             findViewById(R.id.blockList).setVisibility(View.GONE);
             findViewById(R.id.container).setVisibility(View.GONE);
         }


    }



    public void searchuttonOnclick(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animation(true,view);
        }else{
            findViewById(R.id.searchBar).setVisibility(View.VISIBLE);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void animation(boolean show,View view) {
        final RelativeLayout cardView = findViewById(R.id.searchBar);

        int height = cardView.getHeight();
        int width = cardView.getWidth();
        int endRadius = (int) Math.hypot(width, height);
        int cx = (int) (view.getX());// + (cardView.getWidth()));
        int cy = (int) (view.getY());// + cardView.getHeight();

        if (show) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(cardView, cx, cy, cy, endRadius);
            revealAnimator.setDuration(400);
            revealAnimator.start();
            cardView.setVisibility(View.VISIBLE);
            // showZoomIn();
        } else {
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(cardView, cx, cy, cx, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    cardView.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(400);
            anim.start();
        }

    }

        public void endActivity(View view) {
            finish();
        }

        public void emptySearchbarClicked(View view) {
                 EditText editText=findViewById(R.id.searchEdit);


                if (!editText.getText().toString().trim().isEmpty())
                    editText.setText("");
                else endsearch(findViewById(R.id.searchBar));
                searchKey="";
         }

    public void endsearch(View view) {
        EditText editText=findViewById(R.id.searchEdit);
        if (!editText.getText().toString().trim().isEmpty())
            editText.setText("");
        searchKey="";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animation(false,findViewById(R.id.search));
        }else{
            findViewById(R.id.searchBar).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
       if (findViewById(R.id.searchBar).getVisibility()==View.VISIBLE){
           endsearch(findViewById(R.id.searchBar));
       }else
        super.onBackPressed();

    }

    public void blockListOnclick(View view) {
        //Toast.makeText(this,"Working",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this,BlockedUsersActivity.class));
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_users_list_tab, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format ));
           // getArguments().getInt(ARG_SECTION_NUMBER);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:return new AllUser();
               // case 1:return PlaceholderFragment.newInstance(position + 1);

                case 1:

                    return new ActivityFragment();
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
             return 2;
        }
    }
}
