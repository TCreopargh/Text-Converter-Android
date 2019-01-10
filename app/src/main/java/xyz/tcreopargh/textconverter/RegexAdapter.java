package xyz.tcreopargh.textconverter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RegexAdapter extends RecyclerView.Adapter<RegexAdapter.ViewHolder> implements OnClickListener {
    private List<CustomRegex> regexList;
    private OnItemClickListener onItemClickListener = null;

    public RegexAdapter(List<CustomRegex> regexList) {
        this.regexList = regexList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.regex_item,parent,false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomRegex customRegex = regexList.get(position);
        holder.labelBox.setText(customRegex.getLabel());
        holder.regexBox.setText(customRegex.getRegex());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return regexList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView regexBox;
        TextView labelBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            regexBox = itemView.findViewById(R.id.regexValue);
            labelBox = itemView.findViewById(R.id.regexLabel);
        }
    }
    public static interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }
}