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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nisith.onlinelearning.Adapters.HomeFragmentRecyclerViewAdapter;
import com.nisith.onlinelearning.Constant;
import com.nisith.onlinelearning.HomeActivity;
import com.nisith.onlinelearning.Model.QuestionAnswer;
import com.nisith.onlinelearning.R;

public class HomeFragment extends Fragment implements HomeActivity.OnFragmentDataCommunicationListener, HomeFragmentRecyclerViewAdapter.OnLoadingStateChangeListener {


    private TextView headingTextView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private HomeFragmentRecyclerViewAdapter recyclerViewAdapter;
    private CollectionReference rootCollectionRef;
    private String menuHeaderDocumentId, menuItemDocumentId, headingTitle;

    public HomeFragment(String menuHeaderDocumentId, String menuItemDocumentId, String headingTitle) {
        // Required empty public constructor
        this.menuHeaderDocumentId = menuHeaderDocumentId;
        this.menuItemDocumentId = menuItemDocumentId;
        this.headingTitle = headingTitle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.question_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        headingTextView = view.findViewById(R.id.heading_text_view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progressBar.setVisibility(View.GONE);
        rootCollectionRef = FirebaseFirestore.getInstance().collection(Constant.TOPICS);
        headingTextView.setText("Questions on "+headingTitle);
        setupRecyclerviewWithAdapter();
    }



    private void setupRecyclerviewWithAdapter(){
        FirestorePagingOptions<QuestionAnswer> firestorePagingOptions;
        if (menuHeaderDocumentId == null || menuItemDocumentId == null){
            firestorePagingOptions = createFireStorePagingOptions(Constant.DEFAULT_MENU_HEADER_DOCUMENT_ID, Constant.DEFAULT_MENU_ITEM_DOCUMENT_ID);
        }else {
            firestorePagingOptions = createFireStorePagingOptions(menuHeaderDocumentId, menuItemDocumentId);
        }

        recyclerViewAdapter = new HomeFragmentRecyclerViewAdapter(firestorePagingOptions, this);
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
    public void onDataCommunication(String menuHeaderDocumentId, String menuItemDocumentId, String headingTitle) {
        this.headingTitle = headingTitle;
        headingTextView.setText("Questions on "+headingTitle);
        if (recyclerViewAdapter != null){
            recyclerViewAdapter.updateOptions(createFireStorePagingOptions(menuHeaderDocumentId, menuItemDocumentId));
        }
    }

    @Override
    public void onLoadingStateChange(String state) {
        if (state.equals(Constant.DATA_LOADING_START)){
            //when data loading is start
            progressBar.setVisibility(View.VISIBLE);
        }else {
            //when data loading is finish or some error occer in fetching data
            progressBar.setVisibility(View.GONE);
        }
    }
}