//package sa.com.sure.task.webservicesconsumer.task;
//
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.AsyncTask;
//import android.util.Log;
//import android.widget.ImageView;
//
//import java.io.IOException;
//import java.io.InputStream;
//
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import sa.com.sure.task.webservicesconsumer.R;
//import sa.com.sure.task.webservicesconsumer.model.GithubUser;
//
///**
// * Created by HussainHajjar on 5/9/2017.
// */
//
//public class GetGithubUserAvatarTask extends AsyncTask<String, Void, Bitmap> {
//
//    public final static String AVATAR_SIZE_QUERY = "&s=80";
//
//    private ImageView mImageView;
//    private GithubUser mUser;
//
//    public GetGithubUserAvatarTask(ImageView imageView, GithubUser user){
//        this.mImageView = imageView;
//        mUser = user;
//    }
//
//    public boolean isTaskInitiated(){
//        return this.getStatus() == Status.PENDING;
//    }
//
//    public boolean isTastStarted(){
//        return this.getStatus() == Status.RUNNING;
//    }
//
//    public boolean isTaskDone(){
//        return this.getStatus() == Status.FINISHED;
//    }
//
//    @Override
//    protected Bitmap doInBackground(String... avatarUrlList) {
//        mUser.setStarted(true);
//        if(avatarUrlList == null || avatarUrlList.length <= 0){
//            return null;
//        }
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(avatarUrlList[0] + AVATAR_SIZE_QUERY)
//                .build();
//        try {
//            Response response = client.newCall(request).execute();
//            if (response.isSuccessful()) {
//                InputStream userImageStream = response.body().byteStream();
//                Log.d("GET_USER_IMAGE", "Got image for: " + avatarUrlList[0]);
//                return BitmapFactory.decodeStream(userImageStream);
//            } else {
//                    Log.w("GET_USER_IMAGE", "Cannot get user image.");
//                return BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.user);
//            }
//        } catch (IOException e) {
//            Log.e("DOWNLOAD", "Could not download avatar: " + avatarUrlList[0]);
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    @Override
//    protected void onPostExecute(Bitmap bitmap) {
//        if(this.mImageView != null) this.mImageView.setImageBitmap(bitmap);
//        mUser.setAvatar(bitmap);
//    }
//}