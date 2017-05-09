//package sa.com.sure.task.webservicesconsumer;
//
//import android.os.AsyncTask;
//import android.provider.Settings;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.widget.Toast;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//import retrofit2.http.GET;
//import retrofit2.http.Query;
//import sa.com.sure.task.Global;
//import sa.com.sure.task.webservicesconsumer.adapter.RecyclerListerAdapter;
//import sa.com.sure.task.webservicesconsumer.model.GithubUser;
//
///**
// * Created by HussainHajjar on 5/8/2017.
// */
//public class GetGithubUsersTask extends AsyncTask<Integer, Double, List<GithubUser>>{
//
//    public static void getGitHubUsers(int page, int usersPerPage) throws IOException {
//        GithubConsumerHttp service = getConsumer(GithubConsumerHttp.GITHUB_API_URL, GithubConsumerHttp.class);
//        Call<List<GithubUser>> usersCall = service.githubUsers(page, usersPerPage);
//        usersCall.execute();
////        Callback<List<GithubUser>> callback = new Callback<List<GithubUser>>() {
////            List<GithubUser> githubUsers = new ArrayList<>();
////            @Override
////            public void onResponse(Call<List<GithubUser>> call, Response<List<GithubUser>> response) {
////                if (response.code() == 200) {
////                    githubUsers = response.body();
////                    mAdapter
////                    Log.e(Global.getMethodName(), "Found " + githubUsers.size() + " users.");
////                } else {
////                    Log.e(Global.getMethodName(), "Github response code: " + response.code() + " with error message: " + response.message());
////                }
////            }
////            @Override
////            public void onFailure(Call<List<GithubUser>> call, Throwable t) {
////                Log.d("Error", t.getMessage());
////            }
////        };
////        usersCall.enqueue(callback);
//    }
//
//    public static <T> T getConsumer(String url, final Class<T> consumerClass){
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(url)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//            return retrofit.create(consumerClass);
//    }
//
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//    }
//
//    @Override
//    protected List<GithubUser> doInBackground(Integer... integers) {
//        GithubConsumerHttp service = getConsumer(GithubConsumerHttp.GITHUB_API_URL, GithubConsumerHttp.class);
//        Call<List<GithubUser>> usersCall = service.githubUsers(page, usersPerPage);
//        usersCall.execute();
//    }
//
//    @Override
//    protected void onPostExecute(List<GithubUser> githubUsers) {
//        super.onPostExecute(githubUsers);
//    }
//
//    interface GithubConsumerHttp {
//        String GITHUB_API_URL = "https://api.github.com/";
//        @GET("users")
//        Call<List<GithubUser>> githubUsers(@Query("page") int page, @Query("per_page") int perPage);
//    }
//}
////public interface GetGithubUsersTask {
////    String GITHUB_API_URL = "https://api.github.com/";
////    @GET("users")
////    Call<List<GithubUser>> githubUsers(@Query("page") int page, @Query("per_page") int perPage);
////}