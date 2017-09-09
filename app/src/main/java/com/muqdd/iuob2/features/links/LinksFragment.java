package com.muqdd.iuob2.features.links;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.features.main.Menu;
import com.muqdd.iuob2.models.Link;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */
@SuppressWarnings("FieldCanBeLocal")
public class LinksFragment extends BaseFragment {

    @BindView(R.id.main_content) LinearLayout mainContent;
    @BindView(R.id.recycler_view) SuperRecyclerView recyclerView;

    private View mView;
    private List<Link> linksList;
    private FastItemAdapter<Link> fastAdapter;

    public LinksFragment() {
        // Required empty public constructor
    }

    public static LinksFragment newInstance() {
        LinksFragment fragment = new LinksFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, Menu.LINKS.toString());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_list, container, false);
            ButterKnife.bind(this, mView);
            initiate();
        }
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle(title);
        // stop hiding toolbar
        params.setScrollFlags(0);
    }

    private void initiate() {
        // initialize variables
        fastAdapter = new FastItemAdapter<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // disable refreshable list
        recyclerView.getSwipeToRefresh().setEnabled(false);

        // hard coded links
        linksList = new ArrayList<>();
        linksList.add(new Link("iUOB", "http://iuob.net"));
        linksList.add(new Link("UOB Website", "http://www.uob.edu.bh"));
        linksList.add(new Link("Enrollment", "http://www.online.uob.edu.bh/cgi/enr/all_enroll"));
        linksList.add(new Link("Exam Location", "http://www.online.uob.edu.bh/cgi/enr/examtable.exam"));
        linksList.add(new Link("Blackboard", "http://bb.uob.edu.bh"));
        linksList.add(new Link("Academic Calendar", "http://offline.uob.edu.bh/pages.aspx?module=pages&id=5366&SID=868"));
        linksList.add(new Link("Phonebook", "http://dir.uob.edu.bh/mainEn.aspx"));

        // setup adapter
        fastAdapter.set(linksList);
        recyclerView.setAdapter(fastAdapter);

        // open link on click
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<Link>() {
            @Override
            public boolean onClick(View v, IAdapter<Link> adapter, Link item, int position) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(item.url));
                startActivity(i);
                return false;
            }
        });
        // copy link on hold
        // TODO: need to fix click/long click behavior
        /*fastAdapter.withOnLongClickListener(new FastAdapter.OnLongClickListener<Link>() {
            @Override
            public boolean onLongClick(View v, IAdapter<Link> adapter, Link item, int position) {
                Logger.d(item.toString());
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("iUOB2", item.url);
                clipboard.setPrimaryClip(clip);
                Snackbar.make(mainContent,"Url copied",Snackbar.LENGTH_LONG).show();
                return false;
            }
        });*/
    }

}
