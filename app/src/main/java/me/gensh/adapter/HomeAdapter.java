package me.gensh.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by gensh on 2015/11/13.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    List<Map<String, Object>> listitems;
    String[] keySet;
    int[] idSet;
    int targetLayout;

    // Provide a suitable constructor (depends on the kind of dataset)
    public HomeAdapter(List<Map<String, Object>> listitems, int targetLayout, String[] keySet, int[] idSet) {
        this.listitems = listitems;
        this.keySet = keySet;
        this.idSet = idSet;
        this.targetLayout = targetLayout;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(targetLayout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        for (int i = 0; i < keySet.length; i++) {
            TextView tv = (TextView) holder.view.findViewById(idSet[i]);
            tv.setText(listitems.get(position).get(keySet[i]).toString());
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listitems.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }
}