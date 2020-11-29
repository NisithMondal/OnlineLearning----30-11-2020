package com.nisith.onlinelearning.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.nisith.onlinelearning.Constant;
import com.nisith.onlinelearning.Model.QuestionAnswer;
import com.nisith.onlinelearning.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragmentRecyclerViewAdapter extends FirestorePagingAdapter<QuestionAnswer, HomeFragmentRecyclerViewAdapter.MyViewHolder> {

    public interface OnLoadingStateChangeListener{
        void onLoadingStateChange(String state);
    }

    private OnLoadingStateChangeListener loadingStateChangeListener;

    public HomeFragmentRecyclerViewAdapter(@NonNull FirestorePagingOptions<QuestionAnswer> options, OnLoadingStateChangeListener loadingStateChangeListener) {
        super(options);
        this.loadingStateChangeListener = loadingStateChangeListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_appearence_for_questio_answer_home, parent, false);
        return new MyViewHolder(rootView);
    }


    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull QuestionAnswer model) {
        String question = model.getQuestion();
        String answer = model.getAnswer();
        holder.questionTextView.setText(question);
        holder.answerTextView.setText(answer);
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);
        switch (state){
            case LOADING_INITIAL:
                loadingStateChangeListener.onLoadingStateChange(Constant.DATA_LOADING_START);
                break;
            case FINISHED:
                loadingStateChangeListener.onLoadingStateChange(Constant.DATA_LOADING_FINISH);
                break;
            case ERROR:
                loadingStateChangeListener.onLoadingStateChange(Constant.DATA_LOADING_ERROR);
                break;
//            case LOADED:
//                loadingStateChangeListener.onLoadingStateChange(Constant.DATA_LOADING_FINISH);
//                break;

            case LOADING_MORE:
                loadingStateChangeListener.onLoadingStateChange(Constant.DATA_LOADING_START);

        }
    }



    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView questionTextView, answerTextView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.question_text_view);
            answerTextView = itemView.findViewById(R.id.answer_text_view);
        }
    }

}
