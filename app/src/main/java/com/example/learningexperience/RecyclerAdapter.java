package com.example.learningexperience;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private static List<String> interestsList;

    public RecyclerAdapter(List<String> interests){
        interestsList = interests;
    }
    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.generated_task_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        String item = interestsList.get(position);
        holder.taskNumber.setText("Generated Task " + (position + 1));
        holder.taskDescription.setText("This is a short quiz about " + item);

    }

    @Override
    public int getItemCount() {
        return interestsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView taskNumber, taskDescription;
        Button goToTaskButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            taskNumber = itemView.findViewById(R.id.taskNumber);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            goToTaskButton = itemView.findViewById(R.id.goToTaskButton);


            //set on click listener
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    String clickedInterest = interestsList.get(position);

                    Intent intent = new Intent(itemView.getContext(), QuizActivity.class);
                    intent.putExtra("interest", clickedInterest);


                    intent.putExtra("taskNumber", String.valueOf(position + 1));
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
