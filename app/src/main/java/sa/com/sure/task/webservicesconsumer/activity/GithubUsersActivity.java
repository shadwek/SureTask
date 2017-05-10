package sa.com.sure.task.webservicesconsumer.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sa.com.sure.task.webservicesconsumer.R;
import sa.com.sure.task.webservicesconsumer.recycler.adapter.RecyclerListerAdapter;
import sa.com.sure.task.webservicesconsumer.model.GithubUser;
import sa.com.sure.task.webservicesconsumer.task.GetGithubUserAvatarTask;

public class GithubUsersActivity extends AppCompatActivity implements RecyclerListerAdapter.ListItemHandler{

    private final int USERS_PER_PAGE = 50;

    private RecyclerListerAdapter mRecyclerAdapter;
    private RecyclerView mListRecyclerView;
    private TextView mErrorMessageTextView;
    private ProgressBar mLoadIndicatorProgressBar;
    private TabLayout mPageTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_github_users);

        mListRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message);
        mLoadIndicatorProgressBar = (ProgressBar) findViewById(R.id.pb_load_indicator);
        mPageTabLayout = (TabLayout) findViewById(R.id.tl_pages_tab);
        mPageTabLayout.getTabAt(0).setText("1");
        mPageTabLayout.getTabAt(1).setText("2");
        mPageTabLayout.getTabAt(2).setText("3");
        mPageTabLayout.getTabAt(3).setText("4");
        mPageTabLayout.getTabAt(4).setText("NEXT");
        mPageTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int secondPageNum = Integer.valueOf(mPageTabLayout.getTabAt(1).getText().toString());
                if(tab.getPosition() == 4){
                    mPageTabLayout.getTabAt(0).setText("PREV");
                    mPageTabLayout.getTabAt(1).setText(String.valueOf(secondPageNum + 3));
                    mPageTabLayout.getTabAt(2).setText(String.valueOf(secondPageNum + 4));
                    mPageTabLayout.getTabAt(3).setText(String.valueOf(secondPageNum + 5));
                    mPageTabLayout.getTabAt(4).setText("NEXT");
                    mPageTabLayout.getTabAt(1).select();
                }
                else if(tab.getPosition() == 0 && !tab.getText().equals("1")){
                    String firstItem = "PREV";
                    if(secondPageNum == 5) firstItem = "1";
                    mPageTabLayout.getTabAt(0).setText(firstItem);
                    mPageTabLayout.getTabAt(1).setText(String.valueOf(secondPageNum - 3));
                    mPageTabLayout.getTabAt(2).setText(String.valueOf(secondPageNum - 2));
                    mPageTabLayout.getTabAt(3).setText(String.valueOf(secondPageNum - 1));
                    mPageTabLayout.getTabAt(4).setText("NEXT");
                    mPageTabLayout.getTabAt(3).select();
                }
                else {
                    int pageNum = Integer.valueOf(tab.getText().toString()) - 1;
                    loadGithubUsers(pageNum * USERS_PER_PAGE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // nothing happens to old selected when user select another tab
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // nothing happens when user reselect tab item
            }
        });

        mRecyclerAdapter = new RecyclerListerAdapter(this);
        mListRecyclerView.setAdapter(mRecyclerAdapter);
        mListRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mListRecyclerView.setLayoutManager(layoutManager);
        loadGithubUsers(0);
    }

    @Override
    public void onListItemClick(String githubUserUrl) {
        Uri uri = Uri.parse(githubUserUrl);
        Intent webBrowserItent = new Intent(Intent.ACTION_VIEW, uri);
        if(webBrowserItent.resolveActivity(getPackageManager()) != null){
            startActivity(webBrowserItent);
        }
        else Toast.makeText(this, "Could not found web browser app to open github user!", Toast.LENGTH_LONG).show();
    }

    public void loadGithubUsers(int page){
        showGithubUsersListView();
        new GetGithubUsersTask().execute(page, USERS_PER_PAGE);
    }

    public void showErrorMessageView(final String errorMessage){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mErrorMessageTextView.setVisibility(View.VISIBLE);
                mErrorMessageTextView.setText(errorMessage);
                mListRecyclerView.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void showGithubUsersListView(){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mErrorMessageTextView.setVisibility(View.INVISIBLE);
                mListRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int selectedItemId = item.getItemId();
        if(selectedItemId == R.id.action_refresh){
            int currentTabPage = getCurrentTabPage();
            mRecyclerAdapter.setGithubUsers(currentTabPage, null);
            loadGithubUsers(currentTabPage);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public int getCurrentTabPage(){
        TabLayout.Tab currentTab = mPageTabLayout.getTabAt(mPageTabLayout.getSelectedTabPosition());
        return Integer.parseInt(currentTab.getText().toString());
    }

    public class GetGithubUsersTask extends AsyncTask<Integer, Double, List<GithubUser>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadIndicatorProgressBar.setVisibility(View.VISIBLE);
            if (mRecyclerAdapter.containsUsers(getCurrentTabPage())){
                mRecyclerAdapter.notifyDataSetChanged();
                this.cancel(true);
            }
        }

        @Override
        protected List<GithubUser> doInBackground(Integer... integers) {
            if (integers.length == 0) {
                showErrorMessageView("Didn't send page and per_page query params!.");
                return null;
            }
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(GithubConsumerHttp.GITHUB_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            GithubConsumerHttp consumer = retrofit.create(GithubConsumerHttp.class);
            Call<List<GithubUser>> usersCall = consumer.githubUsers(integers[0], integers[1]);
            final List<GithubUser> users = getUsersFromLocalJson();//TODO delete when limit is refreshed
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for(int i = 0; i <12; i++) {
                        try {
                            users.get(i).setAvatar(new GetGithubUserAvatarTask(null, users.get(i)).execute(users.get(i).getAvatar_url()).get());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
//                users.get(i).downloadAvatar(null,false);
//                users.get(i+2).setAvatar(new GetGithubUserAvatarTask().execute(users.get(i+2).getAvatar_url()).get());
                    }
                    //            users.get(12).downloadAvatar(null,true);
                }
            });
//            try {
//                this.wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            return users;

//            try {// TODO uncoment when getUsersFromLocalJson is commented
//                Response<List<GithubUser>> response = usersCall.execute();
//                if(response.isSuccessful()){
//                    return response.body();
//                } else {
//                    return getUsersFromLocalJson();//TODO delete when limit is refreshed
//                    showErrorMessageView("Could not get github users. " +
//                            "\nResponse Code: " + response.code() + ", " +
//                            "\nResponse Message: " + response.message());
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
        }

        @Override
        protected void onPostExecute(List<GithubUser> githubUsers) {
            super.onPostExecute(githubUsers);
            mLoadIndicatorProgressBar.setVisibility(View.INVISIBLE);
            if(githubUsers != null){
                for(int i = 12; i < 50; i++) {
                    try {
                        new GetGithubUserAvatarTask(null, githubUsers.get(i)).execute(githubUsers.get(i).getAvatar_url());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                showGithubUsersListView();
                mRecyclerAdapter.setGithubUsers(getCurrentTabPage(), githubUsers);
            }
            else {
                showErrorMessageView("Get Github users task executed but no users found!");
            }
        }

        @Override
        protected void onProgressUpdate(Double... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            mLoadIndicatorProgressBar.setVisibility(View.INVISIBLE);
                showGithubUsersListView();
                mRecyclerAdapter.setGithubUsers(getCurrentTabPage(), null);
            }
    }

    private List<GithubUser> getUsersFromLocalJson() { // TODO ToDelete
        Gson gson = new Gson();
        String json = getResources().getString(R.string.users_json);
        List<GithubUser> users = gson.fromJson(json, new TypeToken<List<GithubUser>>(){}.getType());
        return users;
    }

    public interface GithubConsumerHttp {
        String GITHUB_API_URL = "https://api.github.com/";
        @GET("users")
        // pagination in:  https://api.github.com/users?page=1&per_page=50   NOT WORKING
//        Call<List<GithubUser>> githubUsers(@Query("page") int page, @Query("per_page") int perPage);
        // since={userId} works with 50 increments to play as pages
        Call<List<GithubUser>> githubUsers(@Query("since") int userId, @Query("per_page") int perPage);
    }
}
