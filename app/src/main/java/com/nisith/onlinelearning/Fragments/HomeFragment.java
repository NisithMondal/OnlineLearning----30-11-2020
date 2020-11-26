package com.nisith.onlinelearning.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nisith.onlinelearning.Adapters.HomeRecyclerViewAdapter;
import com.nisith.onlinelearning.Constant;
import com.nisith.onlinelearning.HomeActivity;
import com.nisith.onlinelearning.Model.QuestionAnswer;
import com.nisith.onlinelearning.R;

public class HomeFragment extends Fragment implements HomeActivity.OnFragmentDataCommunicationListener {


    private RecyclerView recyclerView;
    private HomeRecyclerViewAdapter recyclerViewAdapter;
    private CollectionReference rootCollectionRef;
    private String menuHeaderDocumentId, menuItemDocumentId;

    public HomeFragment(String menuHeaderDocumentId, String menuItemDocumentId) {
        // Required empty public constructor
        this.menuHeaderDocumentId = menuHeaderDocumentId;
        this.menuItemDocumentId = menuItemDocumentId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.question_recycler_view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rootCollectionRef = FirebaseFirestore.getInstance().collection(Constant.TOPICS);
        setupRecyclerviewWithAdapter();
    }



    private void setupRecyclerviewWithAdapter(){
        FirestorePagingOptions<QuestionAnswer> firestorePagingOptions;
        if (menuHeaderDocumentId == null || menuItemDocumentId == null){
            firestorePagingOptions = createFireStorePagingOptions("Programming Languages", "Java");
        }else {
            firestorePagingOptions = createFireStorePagingOptions(menuHeaderDocumentId, menuItemDocumentId);
        }

        recyclerViewAdapter = new HomeRecyclerViewAdapter(firestorePagingOptions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerViewAdapter);
    }


    private FirestorePagingOptions<QuestionAnswer> createFireStorePagingOptions(String menuHeaderDocumentId, String menuItemDocumentId){
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(Constant.pageSize)
                .build();
        Query query = rootCollectionRef.document(menuHeaderDocumentId).collection(Constant.LANGUAGE_COLLECTION)
                .document(menuItemDocumentId).collection(Constant.QUESTIONS_COLLECTION).orderBy(Constant.TIME_STAMP);
        return new FirestorePagingOptions.Builder<QuestionAnswer>()
                .setLifecycleOwner(this)
                .setQuery(query, config,QuestionAnswer.class)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (recyclerViewAdapter != null){
            recyclerViewAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recyclerViewAdapter != null){
            recyclerViewAdapter.stopListening();
        }
    }


    @Override
    public void onDataCommunication(String menuHeaderDocumentId, String menuItemDocumentId) {
        if (recyclerViewAdapter != null){
            recyclerViewAdapter.updateOptions(createFireStorePagingOptions(menuHeaderDocumentId, menuItemDocumentId));
        }
    }
}