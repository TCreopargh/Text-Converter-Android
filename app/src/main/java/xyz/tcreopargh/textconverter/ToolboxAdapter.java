package xyz.tcreopargh.textconverter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import java.util.Objects;

public class ToolboxAdapter extends ArrayAdapter<ListItems> {
    private int resourceId;
    public ToolboxAdapter(Context context, int textViewResourceId, List<ListItems> objects) {
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItems listItems = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null) {
            view=LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder=new ViewHolder();
            viewHolder.listSubtitle=view.findViewById(R.id.listSubtitle);
            viewHolder.listName=view.findViewById(R.id.listLabel);
            view.setTag(viewHolder);
        }
        else {
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.listName.setText(Objects.requireNonNull(listItems).getLabel());
        viewHolder.listSubtitle.setText(listItems.getSubtitle());
        return view;
    }
    class ViewHolder {
        TextView listSubtitle;
        TextView listName;
    }
}