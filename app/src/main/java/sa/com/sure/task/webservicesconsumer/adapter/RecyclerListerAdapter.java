package sa.com.sure.task.webservicesconsumer.adapter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sa.com.sure.task.webservicesconsumer.R;
import sa.com.sure.task.webservicesconsumer.activity.MainActivity;
import sa.com.sure.task.webservicesconsumer.model.GithubUser;

/**
 * Created by HussainHajjar on 5/9/2017.
 */

public class RecyclerListerAdapter extends RecyclerView.Adapter<RecyclerListerAdapter.GithubUserViewHolder> {

    public interface ListItemHandler{
        void onListItemClick(String githubUserUrl);
    }

    private RecyclerListerAdapter.ListItemHandler mClickHander;

    private List<GithubUser> githubUsers;

    public RecyclerListerAdapter(ListItemHandler handler){
        mClickHander = handler;
    }

    public void setGithubUsers(List<GithubUser> users){
        // TODO save users in database
        this.githubUsers = users;
        notifyDataSetChanged();
    }

    @Override
    public GithubUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View userItemView = inflater.inflate(R.layout.gitub_user_item, parent, false);
        return new GithubUserViewHolder(userItemView);
    }

    @Override
    public void onBindViewHolder(GithubUserViewHolder holder, int position) {
        if(githubUsers != null && githubUsers.size() > 0){
            final GithubUser githubUser = githubUsers.get(position);
//            holder.mUserImageView.setImageURI(Uri.parse(githubUser.getAvatar_url()));// TODO: set image from url => githubUser.getAvatar_url()

            try {
                Bitmap image = new  AsyncTask<String, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(String... strings) {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(strings[0])
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            if(response.isSuccessful()){
                                InputStream userImageStream = response.body().byteStream();
                                Log.d("GET_USER_IMAGE", "Got image for: " + strings[0]);
                                return BitmapFactory.decodeStream(userImageStream);
                            }
                            else {
                                Log.d("GET_USER_IMAGE", "Cannot get user image.");
                                return BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.user);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute(githubUser.getAvatar_url()).get();
                holder.mUserImageView.setImageBitmap(image);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            holder.mUserTextView.setText(githubUser.getLogin());
        }
    }

    @Override
    public int getItemCount() {
        if(githubUsers == null) return 0;
        return githubUsers.size();
    }

    public class GithubUserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView mUserTextView;
        public final ImageView mUserImageView;

        public GithubUserViewHolder(View itemView) {
            super(itemView);
            mUserTextView = (TextView) itemView.findViewById(R.id.tv_user_name);
            mUserImageView = (ImageView) itemView.findViewById(R.id.iv_user_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("CLICK", "Clicked onListItemClick Adapter");
            GithubUser githubUser = githubUsers.get(getAdapterPosition());
            mClickHander.onListItemClick(githubUser.getHtml_url());
        }
    }
}
