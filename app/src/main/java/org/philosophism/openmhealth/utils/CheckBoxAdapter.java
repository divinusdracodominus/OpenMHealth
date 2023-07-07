package org.philosophism.openmhealth.utils;

import org.philosophism.openmhealth.api.Event;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.philosophism.openmhealth.R;

import java.nio.charset.StandardCharsets;

public class CheckBoxAdapter extends RecyclerView.Adapter<CheckBoxAdapter.CheckBoxHolder> {

        public interface ItemSelectedListener {
            void onSelect(View v, int index);
        }

        private String[] localDataSet;
        ItemSelectedListener onSelect = null;
        Context context;

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder)
         */
        public static class CheckBoxHolder extends RecyclerView.ViewHolder {
            private final CheckBox box;
            private int idx;
            private ItemSelectedListener onSelect;
            View currentview;

            public CheckBoxHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View
                currentview = view;
                box = view.findViewById(R.id.checkbox);
            }

            public void setData(String item, ItemSelectedListener listener, int index) {
                this.idx = index;
                this.onSelect = listener;
                box.setText(item);

                if(this.onSelect != null && this.currentview != null) {
                    currentview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onSelect.onSelect(v, idx);
                        }
                    });
                }else{
                    Log.i("MyDataManager", "onSelect is null");
                }
            }
        }

        /**
         * Initialize the dataset of the Adapter
         *
         * @param dataSet String[] containing the data to populate views to be used
         * by RecyclerView
         */
        public CheckBoxAdapter(String[] dataSet, ItemSelectedListener llistener) {
            localDataSet = dataSet;
            onSelect = llistener;
        }

        public CheckBoxAdapter(String[] dataSet) {
            this.localDataSet = dataSet;
        }

        public void setOnItemSelectedListener(ItemSelectedListener listener) {
            this.onSelect = listener;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public CheckBoxHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.checkbox_adapter, viewGroup, false);

            return new CheckBoxHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(CheckBoxHolder viewHolder, final int position) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.setData(localDataSet[position], this.onSelect, position);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return localDataSet.length;
        }
    }