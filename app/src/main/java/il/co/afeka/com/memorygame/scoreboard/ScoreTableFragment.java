package il.co.afeka.com.memorygame.scoreboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import il.co.afeka.com.memorygame.ClassApplication;
import il.co.afeka.com.memorygame.R;



public class ScoreTableFragment extends Fragment {
    RecyclerView recyclerView;
    RecyclerView.Adapter<UserViewerAdapter.ViewHolder> adapter;
    List<UserItem> values;

    public ScoreTableFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_score_table, container, false);


        recyclerView = view.findViewById(R.id.recyclerScoresTable);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        values = new ArrayList<>();
        ClassApplication application = (ClassApplication)getActivity().getApplication();
        DatabaseProvider provider = application.getDatabaseProvider();
        values = provider.getMyInv();
        if (values == null) {
            //no data to show
        } else {
            Collections.sort(values);
            adapter = new UserViewerAdapter(values, getContext());
            recyclerView.setAdapter(adapter);
        }

        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}
