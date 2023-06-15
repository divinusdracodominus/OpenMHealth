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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.philosophism.openmhealth.R;

import java.nio.charset.StandardCharsets;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

        public interface ItemSelectedListener {
            void onSelect(View v, int index);
        }

        private Event[] localDataSet;
        ItemSelectedListener onSelect = null;
        Context context;

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder)
         */
        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView titleView;
            private final WebView descriptionView;
            private final ImageView imageView;
            private int idx;
            private ItemSelectedListener onSelect;
            View currentview;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View
                currentview = view;
                titleView = (TextView) view.findViewById(R.id.title);
                descriptionView = view.findViewById(R.id.description);
                imageView = view.findViewById(R.id.image);
            }

            public void setData(Context context, Event event, ItemSelectedListener listener, int index) {
                this.idx = index;
                this.onSelect = listener;
                this.titleView.setText(event.title);

                String encoded = Base64.encodeToString(event.description.getBytes(), Base64.NO_PADDING);
                this.descriptionView.loadData(encoded, "text/html", "base64");

                if(event.image_url != null) {
                    this.imageView.setVisibility(View.VISIBLE);
                    new ImageLoader(context, event.image_url, this.imageView).execute();
                }
                if(event.image_description != null) {
                    this.imageView.setContentDescription(event.description);
                }

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
        public EventsAdapter(Event[] dataSet, ItemSelectedListener llistener) {
            localDataSet = dataSet;
            onSelect = llistener;
        }

        public EventsAdapter(Context ctx, Event[] dataSet) {
            this.context = ctx;
            this.localDataSet = dataSet;
        }

        public void setOnItemSelectedListener(ItemSelectedListener listener) {
            this.onSelect = listener;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.events_display, viewGroup, false);

            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.setData(context, localDataSet[position], this.onSelect, position);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return localDataSet.length;
        }
    }