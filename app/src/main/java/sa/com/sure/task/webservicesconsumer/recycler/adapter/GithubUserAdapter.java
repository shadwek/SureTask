package sa.com.sure.task.webservicesconsumer.recycler.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import sa.com.sure.task.webservicesconsumer.R;
import sa.com.sure.task.webservicesconsumer.model.GithubUser;
import sa.com.sure.task.webservicesconsumer.recycler.viewholder.GithubUserViewHolder;

/**
 * Created by HussainHajjar on 5/10/2017.
 */

public class GithubUserAdapter extends RecyclerView.Adapter<GithubUserViewHolder> {

    private int mCurrentPage;

    private ConcurrentHashMap<Integer, List<GithubUser>> mPageUsers;

    private GithubUserItemHandler mUserItemHandler;

    public GithubUserAdapter(GithubUserItemHandler handler){
        mPageUsers = new ConcurrentHashMap<>();
        mUserItemHandler = handler;
    }

    public void setGithubUsers(int page, List<GithubUser> users){ mPageUsers.put(page, users); }

    public GithubUser getGithubUser(int page, int githubUserPosition) {
        return this.mPageUsers.get(page).get(githubUserPosition);
    }

    public boolean hasUsers(int page){ return mPageUsers.containsKey(page); }

    @Override
    public GithubUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View githubUserItemView = inflater.inflate(R.layout.gitub_user_item, parent, false);
        return new GithubUserViewHolder(githubUserItemView, mUserItemHandler);
    }

    @Override
    public void onBindViewHolder(GithubUserViewHolder holder, int position) {
        if(mPageUsers == null || hasUsers(mCurrentPage) ||
                mPageUsers.get(mCurrentPage).get(position) == null) return;
        GithubUser githubUser = mPageUsers.get(mCurrentPage).get(position);
        holder.setUsername(githubUser.getLogin());
        holder.setUserAvatar(githubUser.getAvatar());
    }

    @Override
    public int getItemCount() { return 0; }

    public interface GithubUserItemHandler {
        void onUserItemClick(int githubUserPosition);
    }
}