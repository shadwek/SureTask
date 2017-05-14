package sa.com.sure.task.webservicesconsumer.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
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

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sa.com.sure.task.webservicesconsumer.R;
import sa.com.sure.task.webservicesconsumer.database.GithubDatabase;
import sa.com.sure.task.webservicesconsumer.database.UsStateDatabase;
import sa.com.sure.task.webservicesconsumer.dialog.Dialog;
import sa.com.sure.task.webservicesconsumer.model.UsState;
import sa.com.sure.task.webservicesconsumer.network.Network;
import sa.com.sure.task.webservicesconsumer.recycler.adapter.GithubUserAdapter;
import sa.com.sure.task.webservicesconsumer.model.GithubUser;

public class GithubUsersActivity extends AppCompatActivity implements GithubUserAdapter.GithubUserItemHandler {

    // change if you want more users per page
    private final int USERS_PER_PAGE = 50;
    private int LAST_FETCHED_USER_ID = -1;
    private boolean IS_MENU_REFRESH_CLICKED;

    private GithubUserAdapter mRecyclerAdapter;
    private RecyclerView mListRecyclerView;
    private TextView mErrorMessageTextView;
    private ProgressBar mLoadIndicatorProgressBar;
    private TabLayout mPageTabLayout;
    private Toast mToast;
    private Activity mActivity;
    private GithubDatabase mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_github_users);

        mListRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message);
        mLoadIndicatorProgressBar = (ProgressBar) findViewById(R.id.pb_load_indicator);
        mPageTabLayout = (TabLayout) findViewById(R.id.tl_pages_tab);
        mDbHelper = new GithubDatabase(this);
        mActivity = this;

        mPageTabLayout.getTabAt(0).setText("1");
        mPageTabLayout.getTabAt(1).setText("2");
        mPageTabLayout.getTabAt(2).setText("3");
        mPageTabLayout.getTabAt(3).setText("4");
        mPageTabLayout.getTabAt(4).setText(mActivity.getString(R.string.tab_next));
        mPageTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int secondPageNum = Integer.valueOf(mPageTabLayout.getTabAt(1).getText().toString());
                if(tab.getPosition() == 4){
                    mPageTabLayout.getTabAt(0).setText(mActivity.getString(R.string.tab_prev));
                    mPageTabLayout.getTabAt(1).setText(String.valueOf(secondPageNum + 3));
                    mPageTabLayout.getTabAt(2).setText(String.valueOf(secondPageNum + 4));
                    mPageTabLayout.getTabAt(3).setText(String.valueOf(secondPageNum + 5));
                    mPageTabLayout.getTabAt(4).setText(mActivity.getString(R.string.tab_next));
                    mPageTabLayout.getTabAt(1).select();
                }
                else if(tab.getPosition() == 0 && !tab.getText().equals("1")){
                    String firstItem = mActivity.getString(R.string.tab_prev);
                    if(secondPageNum == 5) firstItem = "1";
                    mPageTabLayout.getTabAt(0).setText(firstItem);
                    mPageTabLayout.getTabAt(1).setText(String.valueOf(secondPageNum - 3));
                    mPageTabLayout.getTabAt(2).setText(String.valueOf(secondPageNum - 2));
                    mPageTabLayout.getTabAt(3).setText(String.valueOf(secondPageNum - 1));
                    mPageTabLayout.getTabAt(4).setText(mActivity.getString(R.string.tab_next));
                    mPageTabLayout.getTabAt(3).select();
                }
                else loadGithubUsers();
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

        mRecyclerAdapter = new GithubUserAdapter(this);
        mListRecyclerView.setAdapter(mRecyclerAdapter);
        mListRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mListRecyclerView.setLayoutManager(layoutManager);
        loadGithubUsers();
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    @Override
    public void onUserItemClick(int githubUserPosition) {
        String githubUserUrl = mRecyclerAdapter.getGithubUser(getCurrentTabPage(), githubUserPosition).getHtml_url();
        Uri uri = Uri.parse(githubUserUrl);
        Intent webBrowserItent = new Intent(Intent.ACTION_VIEW, uri);
        if(webBrowserItent.resolveActivity(getPackageManager()) != null){
            startActivity(webBrowserItent);
        }
        else showToast(getString(R.string.error_no_web_browser_found));
    }

    private void loadGithubUsers() {
        if(!Network.hasInternetConnection(this)) {
            Dialog.showNoInternetDialog(this);
            showErrorMessageView(getString(R.string.dialog_no_internet_title));
            return;
        }
        new GetGithubUsersTask().execute(LAST_FETCHED_USER_ID + 1, USERS_PER_PAGE);
        mListRecyclerView.getLayoutManager().scrollToPosition(0);
    }

    private void showErrorMessageView(final String errorMessage){
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setText(errorMessage);
        mLoadIndicatorProgressBar.setVisibility(View.INVISIBLE);
        mListRecyclerView.setVisibility(View.INVISIBLE);
        mPageTabLayout.setVisibility(View.INVISIBLE);
    }

    private void showGithubUsersListView(final String... toastMessage){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mLoadIndicatorProgressBar.setVisibility(View.INVISIBLE);
        mListRecyclerView.setVisibility(View.VISIBLE);
        mPageTabLayout.setVisibility(View.VISIBLE);
        if(toastMessage != null && toastMessage.length > 0)
            showToast(toastMessage[0]);
    }

    private void showToast(final String message){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mToast != null) mToast.cancel();
                mToast = Toast.makeText(mActivity, message, Toast.LENGTH_LONG);
                mToast.show();
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
            IS_MENU_REFRESH_CLICKED = true;
            loadGithubUsers();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getCurrentTabPage(){
        TabLayout.Tab currentTab = mPageTabLayout.getTabAt(mPageTabLayout.getSelectedTabPosition());
        return Integer.parseInt(currentTab.getText().toString());
    }

    private class GetGithubUsersTask extends AsyncTask<Integer, Double, List<GithubUser>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mErrorMessageTextView.setVisibility(View.INVISIBLE);
            int currentTabPage = getCurrentTabPage();
            if(mRecyclerAdapter.hasUsers(currentTabPage) &&
                    mRecyclerAdapter.getGithubUsers(currentTabPage).size() != 0) { // users already fetched
                mRecyclerAdapter.setGithubUsers(currentTabPage, null);
                if(IS_MENU_REFRESH_CLICKED){
                    showGithubUsersListView(mActivity.getString(R.string.users_upto_date));
                    IS_MENU_REFRESH_CLICKED = false;
                }
                this.cancel(true);
            } else {
                mLoadIndicatorProgressBar.setVisibility(View.VISIBLE);
                mListRecyclerView.setVisibility(View.INVISIBLE);
                mPageTabLayout.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        protected List<GithubUser> doInBackground(Integer... integers) {
            if (integers.length == 0)
                return null;
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(GithubWebServiceInterface.GITHUB_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            GithubWebServiceInterface service = retrofit.create(GithubWebServiceInterface.class);
            Call<List<GithubUser>> usersCall = service.getGithubUsers(integers[0], integers[1]);
            try {
                Response<List<GithubUser>> response = usersCall.execute();
                if(response.code() == 200){
                    return response.body();
                } else {
                    showErrorMessageView(mActivity.getString(R.string.error_could_not_get_users)
                            .replace("$_responseCode", String.valueOf(response.code()))
                            .replace("$_message", response.message()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<GithubUser> githubUsers) {
            super.onPostExecute(githubUsers);
            if(githubUsers != null){
                showGithubUsersListView();
                mRecyclerAdapter.setGithubUsers(getCurrentTabPage(), githubUsers);
                LAST_FETCHED_USER_ID = githubUsers.get(githubUsers.size() - 1).getId();
                new DbInertGithubUsersTask().execute(githubUsers);
            }
        }
    }

    private class DbInertGithubUsersTask extends AsyncTask<List<GithubUser>, Void, Void>{

        @Override
        protected Void doInBackground(List<GithubUser>... githubUsers) {
            if (mDbHelper.has(githubUsers[0]))
                showToast(mActivity.getString(R.string.db_users_exist));
            int numInsertedUsers = mDbHelper.insert(githubUsers[0]);
            if( numInsertedUsers > 0)
                showToast(numInsertedUsers + " " + mActivity.getString(R.string.db_users_inserted));
            else showToast(mActivity.getString(R.string.db_error_could_not_insert_users));
            mDbHelper.close();
            return null;
        }
    }

    private interface GithubWebServiceInterface {
        String GITHUB_API_URL = "https://api.github.com/";
        @GET("users")
//        pagination in:  https://api.github.com/users?page=1&per_page=50   is NOT WORKING
//        Call<List<GithubUser>> getGithubUsers(@Query("page") int page, @Query("per_page") int perPage);
//        Solution is using since={userId} works with 50 increments to play as pages
//        i.e https://api.github.com/users?since=0&per_page=50
        Call<List<GithubUser>> getGithubUsers(@Query("since") int userId, @Query("per_page") int perPage);
    }
}
