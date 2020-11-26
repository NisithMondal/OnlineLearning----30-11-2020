package com.nisith.onlinelearning.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.gson.internal.$Gson$Preconditions;
import com.nisith.onlinelearning.Constant;
import com.nisith.onlinelearning.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class UpdateQuestionFragment extends Fragment {


    public interface OnCloseWindowListener{
        void onCloseWindow();
    }

    private OnCloseWindowListener onCloseWindowListener;
    private String question, answer;
    private EditText questionEditText, answerEditText;
    private ImageView closeWindowImageView;
    private Button updateButton;
    private DocumentReference documentReference;

    public UpdateQuestionFragment(OnCloseWindowListener onCloseWindowListener) {
        // Required empty public constructor
        this.onCloseWindowListener = onCloseWindowListener;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_question, container, false);
        questionEditText = view.findViewById(R.id.question_edit_text);
        answerEditText = view.findViewById(R.id.answer_edit_text);
        updateButton = view.findViewById(R.id.update_question_button);
        closeWindowImageView = view.findViewById(R.id.close_window_icon);
        return view;
    }

    public void setData(String question, String answer, DocumentReference documentReference) {
        // Required empty public constructor
        this.question = question;
        this.answer = answer;
        this.documentReference = documentReference;
        questionEditText.setText(question);
        answerEditText.setText(answer);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        questionEditText.setText("");
        answerEditText.setText("");
        closeWindowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseWindowListener.onCloseWindow();
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newQuestion = questionEditText.getText().toString().trim();
                String newAnswer = answerEditText.getText().toString().trim();
                if (TextUtils.isEmpty(newQuestion)){
                    questionEditText.setError("Enter text");
                    questionEditText.requestFocus();
                    return;
                }else if (TextUtils.isEmpty(newAnswer)){
                    answerEditText.setError("Enter text");
                    answerEditText.requestFocus();
                    return;
                }
                if (documentReference != null){
                    Map<String, Object> map = new HashMap<>();
                    map.put("question", newQuestion);
                    map.put("answer", newAnswer);
                    documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getContext(), "Update Successfully", Toast.LENGTH_SHORT).show();
                                closeWindowImageView.performClick();
                            }else {
                                Toast.makeText(getContext(), "Not update "+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}