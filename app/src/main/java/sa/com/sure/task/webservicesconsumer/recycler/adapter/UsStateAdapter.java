package sa.com.sure.task.webservicesconsumer.recycler.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import sa.com.sure.task.webservicesconsumer.R;
import sa.com.sure.task.webservicesconsumer.model.UsState;
import sa.com.sure.task.webservicesconsumer.recycler.viewholder.UsStateViewHolder;

/**
 * Created by HussainHajjar on 5/10/2017.
 */

public class UsStateAdapter extends RecyclerView.Adapter<UsStateViewHolder> {

    private List<UsState.State> mStates;

    public UsStateAdapter(){ mStates = new ArrayList<>(); }

    public void setUsStates(UsState usStates){
        if(usStates == null) usStates = new UsState();
        this.mStates = usStates.getStates();
        notifyDataSetChanged();
    }

    @Override
    public UsStateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View stateItemView = inflater.inflate(R.layout.us_state_item, parent, false);
        return new UsStateViewHolder(stateItemView);
    }

    @Override
    public void onBindViewHolder(UsStateViewHolder holder, int position) {
        if(mStates == null || mStates.size() == 0) return;
        UsState.State usState = mStates.get(position);
        holder.setCity(usState.getCity());
        holder.setState(usState.getState());
        holder.setZipCode(usState.getZipCode());
        holder.setTimeZone(usState.getTimeZone());
        holder.setAreaCode(usState.getAreaCode());
    }

    @Override
    public int getItemCount() {
        if(mStates == null || mStates.size() == 0) return 0;
        return mStates.size();
    }
}
