package com.nisith.onlinelearning.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.nisith.onlinelearning.Model.QuestionAnswer;
import com.nisith.onlinelearning.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class QAEditRecyclerAdapter extends FirestoreRecyclerAdapter<QuestionAnswer, QAEditRecyclerAdapter.MyViewHolder> {

    public QAEditRecyclerAdapter(@NonNull FirestoreRecyclerOptions<QuestionAnswer> options) {
        super(options);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_appearence_for_questio_answer_edit, parent, false);
        return new MyViewHolder(rootView);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull QuestionAnswer model) {
        String question = model.getQuestion();
        String answer = model.getAnswer();
        holder.questionTextView.setText(question);
        holder.answerTextView.setText(answer);
    }



    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView questionTextView;
        TextView answerTextView;
        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.question_text_view);
            answerTextView = itemView.findViewById(R.id.answer_text_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentReference documentReference = getSnapshots().getSnapshot(getAdapterPosition()).getReference();
                }
            });
        }
    }

}
