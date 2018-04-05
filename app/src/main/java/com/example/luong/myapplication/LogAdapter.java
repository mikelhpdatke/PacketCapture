package com.example.luong.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {
    private Context mContext;
    private List<String> list;

    public LogAdapter(Context context){
        this.mContext = context;
    }

    public void setList(List< String > list){
        this.list = list;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public LogAdapter.LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.log_item, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LogAdapter.LogViewHolder holder, int position) {
        holder.textView.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class LogViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public LogViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.log_line);
        }
    }
}
