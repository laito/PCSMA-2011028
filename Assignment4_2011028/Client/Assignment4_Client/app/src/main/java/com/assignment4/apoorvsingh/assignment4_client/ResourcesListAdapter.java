package com.assignment4.apoorvsingh.assignment4_client;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Apoorv Singh on 4/8/2015.
 */
public class ResourcesListAdapter extends BaseAdapter implements View.OnClickListener  {

    private List<Resource> resourceList;
    private Activity myActivity;
    private Resources resources;
    private static LayoutInflater inflater;

    public ResourcesListAdapter(Activity myActivity, List<Resource> output, Resources resources) {
        resourceList = output;
        this.myActivity = myActivity;
        this.resources = resources;
        inflater = (LayoutInflater) myActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return resourceList.size();
    }

    @Override
    public Object getItem(int i) {
        return resourceList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return resourceList.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ResourceListViewHolder holder;
        if(view == null) {
            view = inflater.inflate(R.layout.resource, null);
            holder = new ResourceListViewHolder();
            holder.Name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        } else {
            holder = (ResourceListViewHolder) view.getTag();
        }
        if(resourceList.size() < 0) {
            holder.Name.setText("No Resources");
        } else {
            Resource curItem = null;
            curItem = resourceList.get(i);
            assert curItem != null;
            holder.Name.setText(curItem.getName());
            view.setOnClickListener(new OnItemClickListener(i));
            Button btnDownload = (Button) view.findViewById(R.id.button);
            Button btnDelete = (Button) view.findViewById(R.id.button2);
            btnDownload.setTag(curItem.getId());
            btnDelete.setTag(curItem.getId());
        }
        return view;
    }

    @Override
    public void onClick(View view) {

    }

    class ResourceListViewHolder {
        public TextView Name;
    }

    private class OnItemClickListener implements View.OnClickListener {
        private int mPosition;

        public OnItemClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View view) {
            /*
            Intent videoActivity = new Intent(myActivity, VideoActivity.class);
            Bundle args = new Bundle();
            args.putInt("videoID", (int) getItemId(mPosition));
            videoActivity.putExtras(args);
            myActivity.startActivity(videoActivity);
            */
        }
    }
}
