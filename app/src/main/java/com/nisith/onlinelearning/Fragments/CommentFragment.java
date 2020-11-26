package com.nisith.onlinelearning.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nisith.onlinelearning.R;


public class CommentFragment extends Fragment {


    public CommentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Toast.makeText(getContext(), "CommentFragment onCreateView() is called", Toast.LENGTH_SHORT).show();
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
//        Toast.makeText(getContext(), "CommentFragment onStart() is called", Toast.LENGTH_SHORT).show();
    }

}