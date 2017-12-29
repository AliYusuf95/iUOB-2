package com.muqdd.iuob2.features.map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseActivity;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.features.main.Menu;
import com.muqdd.iuob2.views.Fab;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class MapFragment extends BaseFragment implements OnMapReadyCallback {

    private final LatLng sakheerLocation = new LatLng(26.051588, 50.513387);
    private final LatLng isaTownLocation = new LatLng(26.165126, 50.545274);

    protected @BindView(R.id.main_content) RelativeLayout mainContent;
    protected @BindView(R.id.fab) Fab fab;
    protected @BindView(R.id.fab_sheet) View sheetView;
    protected @BindView(R.id.overlay) View overlay;
    protected @BindColor(R.color.white) int sheetColor;
    protected @BindColor(R.color.colorPrimaryDark) int fabColor;

    private View mView;
    private List<Location> locations;
    private MaterialSheetFab materialSheetFab;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, Menu.MAP.toString());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_map, container, false);
            ButterKnife.bind(this, mView);
            initiate();
        }
        setHasOptionsMenu(true);
        return mView;
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.sakheer:
                moveMapCamera(sakheerLocation, 15);
                return true;
            case R.id.isa_town:
                moveMapCamera(isaTownLocation, 16);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle(title);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // setup default to the map
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.getUiSettings().setMapToolbarEnabled(false);
        moveMapCamera(sakheerLocation, 15);

        // try to change Map Toolbar position
        if (mapFragment != null && mapFragment.getView() != null) {
            try {
                View toolbar = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).
                        getParent()).findViewById(Integer.parseInt("4"));

                // and next place it, for example, on bottom right (as Google Maps app)
                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
                // position on right bottom
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
                rlp.setMargins(30, 30, 0, 0);
            } catch (NullPointerException ex){
                Logger.w("Cant find Map Toolbar view.");
                mMap.getUiSettings().setMapToolbarEnabled(false);
            }
        }

        // Add pins in background for better performance
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                addLocation();
                runOnUi(new Runnable() {
                    @Override
                    public void run() {
                        for (Location location: locations){
                            mMap.addMarker(location.getMarker());
                        }
                    }
                });
            }
        });
    }

    @Optional
    @OnClick({R.id.terrain, R.id.hybrid, R.id.satellite, R.id.normal})
    public void onClick(View v) {
        if (mMap == null) {
            materialSheetFab.hideSheet();
            return;
        }
        switch (v.getId()) {
            case R.id.terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }
        materialSheetFab.hideSheet();
    }

    private void initiate() {
        locations = new ArrayList<>();
        // initialize map
        mapFragment = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);
        // Create material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);

        setOnBackPressedListener(new BaseActivity.OnBackPressedListener() {
            @Override
            public boolean onBack() {
                if (materialSheetFab.isSheetVisible()) {
                    materialSheetFab.hideSheet();
                    return true;
                }
                return false;
            }
        });
    }

    private void moveMapCamera(LatLng location, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,zoom));
    }

    private void addLocation(){
        // Add all fucking locations to the list
        locations.add(new Location("S40","IT College","Sakheer","Red","26.047918","50.509989"));
        locations.add(new Location("CS Department","Computer Science","Sakheer","Red","26.047903","50.510153"));
        locations.add(new Location("CE Department","Computer Engineering","Sakheer","Red","26.047486","50.51018"));
        locations.add(new Location("IS Department","Information System","Sakheer","Red","26.048315","50.510174"));
        locations.add(new Location("S47","Library","Sakheer","Red","26.049195","50.509944"));
        locations.add(new Location("S50","College of IT auditorium","Sakheer","Red","26.048161","50.509407"));
        locations.add(new Location("S51","Food court","Sakheer","Red","26.048513","50.508991"));
        locations.add(new Location("Boys Prayer","مصلى الشباب","Sakheer","Red","26.04839","50.508863"));
        locations.add(new Location("Girls Prayer","مصلى البنات","Sakheer","Red","26.048636","50.509123"));
        locations.add(new Location("S41","College of Science","Sakheer","Red","26.049544","50.509227"));
        locations.add(new Location("Chemistry Department","Chemistry","Sakheer","Red","26.049151","50.508938"));
        locations.add(new Location("Biology Department","Biology","Sakheer","Red","26.049568","50.508895"));
        locations.add(new Location("Physics Department","Physics","Sakheer","Red","26.049978","50.508865"));
        locations.add(new Location("S48","Science building","Sakheer","Red","26.0506","50.508326"));
        locations.add(new Location("S49","Laboratory Services","Sakheer","Red","26.05059","50.507747"));
        locations.add(new Location("S43","UOB press","Sakheer","Red","26.046595","50.508087"));
        locations.add(new Location("West Gate","البوابة الغربية","Sakheer","Red","26.048012","50.500544"));
        locations.add(new Location("Bus Station","محطة باصات المغادرة","Sakheer","Red","26.048802","50.512357"));
        locations.add(new Location("Male Dorms","سكن الطلاب","Sakheer","Red","26.049992","50.5124"));
        locations.add(new Location("Female Dorms","سكن الطالبات","Sakheer","Red","26.050036","50.514385"));
        locations.add(new Location("Mosque","UOB main mosque","Sakheer","Red","26.051299","50.512309"));
        locations.add(new Location("S39","College of Law","Sakheer","Red","26.047308","50.513382"));
        locations.add(new Location("Prayer and Restrooms","استراحة الحقوق","Sakheer","Red","26.046855","50.513371"));
        locations.add(new Location("S37","Registration","Sakheer","Red","26.048426","50.51336"));
        locations.add(new Location("S38","Swimming pool and GYM","Sakheer","Red","26.049004","50.513371"));
        locations.add(new Location("Deanship of Student Affairs","عمادة شؤون الطلبة","Sakheer","Red","26.050123","50.513366"));
        locations.add(new Location("G-2 S","الصالة الرياضية","Sakheer","Red","26.049891","50.513355"));
        locations.add(new Location("Food Court","","Sakheer","Red","26.050508","50.51336"));
        locations.add(new Location("Central Library","المكتبة المركزية","Sakheer","Red","26.051014","50.513366"));
        locations.add(new Location("Cafeteria Abu Ali","","Sakheer","Red","26.050735","50.514009"));
        locations.add(new Location("S21","Computer engineering labs","Sakheer","Red","26.048841","50.51594"));
        locations.add(new Location("S20-C","College of Applied Studies","Sakheer","Red","26.049164","50.51651"));
        locations.add(new Location("S20-B","French Studies","Sakheer","Red","26.049482","50.516088"));
        locations.add(new Location("S20-A","","Sakheer","Red","26.049966","50.517413"));
        locations.add(new Location("S20","English language center","Sakheer","Red","26.050077","50.516831"));
        locations.add(new Location("S22","Bahrain Teachers College","Sakheer","Red","26.050595","50.515219"));
        locations.add(new Location("S44","Bahrain Credit Media Center","Sakheer","Red","26.05105","50.515415"));
        locations.add(new Location("S45","Zain e-Learning Center","Sakheer","Red","26.052226","50.515421"));
        locations.add(new Location("S17","English Language and Literature","Sakheer","Red","26.052766","50.515109"));
        locations.add(new Location("S18","Events Hall","Sakheer","Red","26.053412","50.51641"));
        locations.add(new Location("Doctors Dorms","سكن الدكاترة","Sakheer","Red","26.054581","50.513548"));
        locations.add(new Location("Aswaq Mosa","أسواق موسى","Sakheer","Red","26.053265","50.514222"));
        locations.add(new Location("Book Shop","","Sakheer","Red","26.053274","50.513712"));
        locations.add(new Location("S46","Student Council","Sakheer","Red","26.052301","50.513983"));
        locations.add(new Location("Hall of Sheikh Abdul Aziz","قاعة الشيخ عبد العزيز","Sakheer","Red","26.052214","50.512741"));
        locations.add(new Location("Administration","مبنى الإدارة","Sakheer","Red","26.052219","50.513267"));
        locations.add(new Location("S1B","College of Business Administration","Sakheer","Red","26.051896","50.514251"));
        locations.add(new Location("S1A","College of Arts","Sakheer","Red","26.051352","50.514257"));
        locations.add(new Location("East Gate","البوابة الشرقية","Sakheer","Red","26.055462","50.517442"));
        locations.add(new Location("Main Gate","البوابة الرئيسية","Sakheer","Red","26.058123","50.508891"));
        locations.add(new Location("South Gate","البوابة الجنوبية","Sakheer","Red","26.03897","50.505303"));
        locations.add(new Location("15","Building 15","Sakheer","Red","26.164187","50.546899"));
        locations.add(new Location("27","Building 27","Sakheer","Red","26.165564","50.544962"));
        locations.add(new Location("16","Building 16","Sakheer","Red","26.164399","50.546105"));
        locations.add(new Location("12","Building 12","Sakheer","Red","26.166334","50.545579"));
        locations.add(new Location("Library","","Sakheer","Red","26.165491","50.546786"));
        locations.add(new Location("Workshop","","Sakheer","Red","26.165939","50.544291"));
        locations.add(new Location("Mosque","","Sakheer","Red","26.165646","50.547537"));
        locations.add(new Location("14","Engineering Building","Sakheer","Red","26.164846","50.54758"));
        locations.add(new Location("13","Workshop","Sakheer","Red","26.165337","50.547971"));
        locations.add(new Location("27A","Building 27A","Sakheer","Red","26.165776","50.544496"));
        locations.add(new Location("28","Building 28","Sakheer","Red","26.166031","50.54485"));
        locations.add(new Location("Workshop","","Sakheer","Red","26.166283","50.544206"));
        locations.add(new Location("33","Building 33","Sakheer","Red","26.166464","50.544547"));
        locations.add(new Location("35","Building 35","Sakheer","Red","26.166707","50.544362"));
        locations.add(new Location("32","Building 32","Sakheer","Red","26.166753","50.545083"));
        locations.add(new Location("36","Building 36","Sakheer","Red","26.167061","50.545011"));
        locations.add(new Location("Bshayer","","Sakheer","Red","26.166486","50.545392"));
        locations.add(new Location("31","Building 31","Sakheer","Red","26.166811","50.545884"));
    }

    private class Location {
        String title;
        String description;
        String location;
        String color;
        double latitude;
        double longitude;

        Location(String title, String description, String location,
                 String color, String latitude, String longitude) {
            this.title = title;
            this.description = description;
            this.location = location;
            this.color = color;
            this.latitude = Double.valueOf(latitude);
            this.longitude = Double.valueOf(longitude);
        }

        MarkerOptions getMarker() {
            return new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(title).snippet(description);
        }
    }
}
