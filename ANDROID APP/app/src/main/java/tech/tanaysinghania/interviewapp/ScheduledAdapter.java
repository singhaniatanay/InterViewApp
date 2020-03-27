package tech.tanaysinghania.interviewapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ScheduledAdapter extends RecyclerView.Adapter<ScheduledAdapter.ViewHolder>{
    public ScheduledAdapter(ScheduledData[] listdata) {
        this.listdata = listdata;
    }
    @Override
    public ScheduledAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.scheduled_item, parent, false);
        ScheduledAdapter.ViewHolder viewHolder = new ScheduledAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduledAdapter.ViewHolder holder, int position) {
        final ScheduledData myListData = listdata[position];
        holder.mTime.setText(listdata[position].getTime());
        holder.erEmail.setText(listdata[position].getEr());
        holder.eeEmail.setText(listdata[position].getEe());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"click on item: "+myListData.getEe(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.length;
    }

    private ScheduledData[] listdata;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTime,eeEmail,erEmail;
        public LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.mTime = (TextView) itemView.findViewById(R.id.interview_time);
            this.eeEmail = (TextView) itemView.findViewById(R.id.interviewee_email);
            this.erEmail = (TextView) itemView.findViewById(R.id.interviewer_email);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
        }
    }
}
