package sa.com.sure.task.webservicesconsumer.recycler.viewholder;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import sa.com.sure.task.webservicesconsumer.R;

/**
 * Created by HussainHajjar on 5/10/2017.
 */

public class GithubUserViewHolder extends RecyclerView.ViewHolder {

    private TextView mUsernameTextView;
    private ImageView mUserAvatarImageView;

    public GithubUserViewHolder(View itemView) {
        super(itemView);
        mUsernameTextView = (TextView) itemView.findViewById(R.id.tv_user_name);
        mUserAvatarImageView = (ImageView) itemView.findViewById(R.id.iv_user_image);
    }

    public void setUserAvatar(Bitmap avatar){ mUserAvatarImageView.setImageBitmap(avatar); }
    public void setUsername(String username){ mUsernameTextView.setText(username); }
}
