package sa.com.sure.task.webservicesconsumer.recycler.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import sa.com.sure.task.webservicesconsumer.recycler.viewholder.GithubUserViewHolder;

/**
 * Created by HussainHajjar on 5/10/2017.
 */

public class GithubUserAdapter extends RecyclerView.Adapter<GithubUserViewHolder> {
    @Override
    public GithubUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(GithubUserViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public interface onUserItemClickListener{
        void onUserItemClick(String userGithubUrl);
    }
}
