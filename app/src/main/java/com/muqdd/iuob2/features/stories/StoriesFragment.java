package com.muqdd.iuob2.features.stories;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.Auth;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.features.account.LoginFragment;
import com.muqdd.iuob2.features.main.Menu;
import com.muqdd.iuob2.models.ApiResponse;
import com.muqdd.iuob2.models.Story;
import com.muqdd.iuob2.models.User;
import com.muqdd.iuob2.network.IUOBApi;
import com.muqdd.iuob2.network.ServiceGenerator;
import com.muqdd.iuob2.views.MultiTouchDetectLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class StoriesFragment extends BaseFragment {

    private final static int STORY_REQUEST = 1;

    private final static Location SAKHEER_LOCATION = new Location("Sakheer");
    private final static Location ISA_TOWN_LOCATION = new Location("Isa-town");
    static {
        SAKHEER_LOCATION.setLongitude(50.510845);
        SAKHEER_LOCATION.setLatitude(26.048766);
        ISA_TOWN_LOCATION.setLongitude(26.048766);
        ISA_TOWN_LOCATION.setLatitude(26.048766);
    }
    private final static float SAKHEER_RADIUS = 1500;
    private final static float ISA_TOWN_RADIUS = 500;

    @BindView(R.id.main_content)
    protected SwipeRefreshLayout mainContent;
    @BindView(R.id.multi_touch_layout)
    protected MultiTouchDetectLayout multiTouchDetectLayout;
    @BindView(R.id.rv_my_stories)
    protected SuperRecyclerView recyclerView;

    // iuob stories
    @BindView(R.id.layout_iuob_stories)
    protected LinearLayout iuobStoriesLayout;
    @BindView(R.id.settings_layout)
    protected LinearLayout iuobStoriesSettings;
    @BindView(R.id.lbl_title)
    protected TextView iuobStoriesTitle;
    @BindView(R.id.img_story)
    protected ImageView iuobStoriesImg;

    private View mView;
    private IUOBApi iuobApi;
    private FastItemAdapter<StoryItem> fastAdapter;
    private boolean myStoriesLoading;
    private boolean iuobStoriesLoading;
    private boolean enableStories;
    private Location userLocation;

    public StoriesFragment() {
        // Required empty public constructor
    }

    public static StoriesFragment newInstance() {
        StoriesFragment fragment = new StoriesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, Menu.STORIES.toString());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_stories, container, false);
            ButterKnife.bind(this, mView);
            initiate();
        }
        setHasOptionsMenu(true);
        return mView;
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.add:
                getBaseActivity().getLastLocation(location -> {
                    if (location == null && userLocation == null) {
                        infoDialog("Sorry","We can't locate your location please open the map to checkout.",
                                "Cancel"
                        ).show();
                        return;
                    } else if (location != null){
                        userLocation = location;
                    }
                    float sakheerResult, isaTownResult;
                    sakheerResult = SAKHEER_LOCATION.distanceTo(userLocation);
                    isaTownResult = ISA_TOWN_LOCATION.distanceTo(userLocation);
                    if (enableStories || sakheerResult <= SAKHEER_RADIUS || isaTownResult <= ISA_TOWN_RADIUS) {
                        startActivityForResult(new Intent(getActivity(), CaptureActivity.class), STORY_REQUEST);
                        return;
                    }
                    infoDialog("Error", "You must in Sakheer/Isa Town Campus to post stories", "Close").show();
                });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle(title);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == STORY_REQUEST && resultCode == Activity.RESULT_OK && data.getExtras()!= null) {
            String resultPath = data.getExtras().getString(CaptureActivity.RESULT_PATH, "");
            uploadImage(resultPath);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initiate() {
        if (!Auth.isLoggedIn(getContext())){
            replaceFragment(LoginFragment.newInstance());
            return;
        }

        // initialize variables
        User user = Auth.getUserData(getContext());
        iuobApi = ServiceGenerator.createService(IUOBApi.class, user);
        enableStories = false;

        fastAdapter = new FastItemAdapter<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // disable refreshable list
        recyclerView.getSwipeToRefresh().setEnabled(false);

        // setup adapter
        recyclerView.setAdapter(fastAdapter);
        fastAdapter.withEventHook(new ClickEventHook<StoryItem>() {
            @NonNull
            @Override
            public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
                List<View> list = new ArrayList<>();
                if (viewHolder instanceof StoryItem.ViewHolder) {
                    list.add(((StoryItem.ViewHolder) viewHolder).mainContent);
                    list.add(((StoryItem.ViewHolder) viewHolder).settingsLayout);
                }
                return list;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<StoryItem> fastAdapter, StoryItem item) {
                if (v.getId() == R.id.settings_layout) {
                    showDeleteDialog(item.getData(), position);
                } else {
                    if (item.getData().getType().equals("photo")) {
                        String[] resources = new String[]{item.getData().toString()};
                        Intent a = new Intent(getContext(), StatusStoriesActivity.class);
                        a.putExtra(StatusStoriesActivity.STATUS_RESOURCES_KEY, resources);
                        a.putExtra(StatusStoriesActivity.STATUS_DURATION_KEY, 5000L);
                        a.putExtra(StatusStoriesActivity.IS_IMMERSIVE_KEY, true);
                        a.putExtra(StatusStoriesActivity.IS_CACHING_ENABLED_KEY, true);
                        startActivity(a);
                    } else {
                        infoDialog("Sorry", "Video stories will be supported soon.","Dismiss").show();
                    }
                }
            }
        });

        // setup iuob stories
        iuobStoriesTitle.setText("UOB Live");
        iuobStoriesImg.setBackground(null);
        iuobStoriesImg.setColorFilter(null);
        iuobStoriesImg.setImageTintList(null);
        Glide.with(this)
                .load(R.drawable.ic_logo)
                .asBitmap()
                .placeholder(R.drawable.ic_send_48dp)
                .into(new BitmapImageViewTarget(iuobStoriesImg) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        iuobStoriesImg.setImageDrawable(circularBitmapDrawable);
                    }
                });
        iuobStoriesSettings.setVisibility(View.GONE);
        iuobStoriesLayout.setVisibility(View.GONE);

        mainContent.setOnRefreshListener(this::getData);

        multiTouchDetectLayout.setMultiTouchEvent(5, (event, count) -> {
            enableStories = true;
        });

        getBaseActivity().requestLocationUpdates(new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null || locationResult.getLastLocation() != null) {
                    return;
                }
                userLocation = locationResult.getLastLocation();
            }
        });

        getData();
    }

    private void getData() {
        myStoriesLoading = true;
        iuobStoriesLoading = true;
        mainContent.setRefreshing(true);

        User user = Auth.getUserData(getContext());
        iuobApi.getMyStories(user.getId()).enqueue(
                new Callback<List<Story>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Story>> call, @NonNull Response<List<Story>> response) {
                        List<Story> stories = response.body();
                        if (stories != null) {
                            fastAdapter.set(StoryItem.fromList(stories));
                        }
                        myStoriesLoading = false;
                        stopRefreshing();
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Story>> call, @NonNull Throwable t) {
                        myStoriesLoading = false;
                        stopRefreshing();
                    }
                }
        );

        iuobApi.getStories().enqueue(new Callback<List<Story>>() {
            @Override
            public void onResponse(@NonNull Call<List<Story>> call, @NonNull Response<List<Story>> response) {
                List<Story> stories = response.body();
                if (stories != null && stories.size() > 0) {
                    List<String> resourcesList = new ArrayList<>();
                    for (Story story : stories) {
                        if (story.getType().equals("photo"))
                            resourcesList.add(story.toString());
                    }
                    final String[] resources = resourcesList.toArray(new String[]{});
                    if (resources.length > 0) {
                        iuobStoriesLayout.setVisibility(View.VISIBLE);
                        iuobStoriesLayout.setOnClickListener(v -> {
                            Intent a = new Intent(getContext(), StatusStoriesActivity.class);
                            a.putExtra(StatusStoriesActivity.STATUS_RESOURCES_KEY, resources);
                            a.putExtra(StatusStoriesActivity.STATUS_DURATION_KEY, 5000L);
                            a.putExtra(StatusStoriesActivity.IS_IMMERSIVE_KEY, true);
                            a.putExtra(StatusStoriesActivity.IS_CACHING_ENABLED_KEY, true);
                            startActivity(a);
                        });
                    } else {
                        iuobStoriesLayout.setVisibility(View.GONE);
                    }
                } else {
                    iuobStoriesLayout.setVisibility(View.GONE);
                }
                iuobStoriesLoading = false;
                stopRefreshing();
            }

            @Override
            public void onFailure(@NonNull Call<List<Story>> call, @NonNull Throwable t) {
                iuobStoriesLoading = false;
                stopRefreshing();
            }
        });
    }

    private void uploadImage(String filePath) {
        if (filePath.equals("")) {
            infoDialog("Sorry","Some thing goes wrong pleas try again later.",
                    "Cancel"
            ).show();
            return;
        }
        File file = new File(filePath);
        User user = Auth.getUserData(getContext());

        RequestBody image = RequestBody.create(MediaType.parse("image/png"), file);
        RequestBody createdBy = RequestBody.create(MediaType.parse("text/plain"),
                user.getId());
        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "photo");
        RequestBody duration = RequestBody.create(MediaType.parse("text/plain"), "5");
        RequestBody lat = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userLocation.getLatitude()));
        RequestBody lng = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userLocation.getLongitude()));
        iuobApi.storyUpload(createdBy, type, duration, lat, lng, image).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                ApiResponse apiResponse = response.body();
                if (apiResponse != null && apiResponse.getCode() == 200) {
                    infoDialog("Thanks", "Thank you for creating a story. " +
                                    "Your story will be reviewed and then it will appear in UOB Live",
                            "Close"
                    ).show();
                } else {
                    infoDialog("Sorry","Some thing goes wrong pleas try again later.",
                            "Cancel"
                    ).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                infoDialog("Sorry","Some thing goes wrong pleas try again later.",
                        "Cancel"
                ).show();
            }
        });
    }

    private void showDeleteDialog(final Story item, final int position) {
        if (getContext() == null) {
            return;
        }
        final Dialog dialog = new Dialog(getContext());
        // prepare dialog layout
        LayoutInflater inflater =
                (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            return;
        }
        final View dialogView = inflater.inflate(R.layout.dialog_delete_course, null);
        // init cancel button
        dialogView.findViewById(R.id.cancel).setOnClickListener(view -> {
            if (dialog.isShowing())
                dialog.dismiss();
        });
        // init delete button
        dialogView.findViewById(R.id.delete).setOnClickListener(view -> {
            iuobApi.deleteStory(item.getId()).enqueue(new Callback<List<Auth>>() {
                @Override
                public void onResponse(@NonNull Call<List<Auth>> call, @NonNull Response<List<Auth>> response) {

                }

                @Override
                public void onFailure(@NonNull Call<List<Auth>> call, @NonNull Throwable t) {

                }
            });

            ((FastItemAdapter)fastAdapter).remove(position);
            Snackbar.make(mainContent,"Story deleted",Snackbar.LENGTH_SHORT).show();
            if (dialog.isShowing())
                dialog.dismiss();
        });
        // show dialog
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void stopRefreshing() {
        mainContent.setRefreshing(!(!myStoriesLoading && !iuobStoriesLoading));
    }

}
