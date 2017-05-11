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

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sa.com.sure.task.webservicesconsumer.R;
import sa.com.sure.task.webservicesconsumer.recycler.adapter.GithubUserAdapter;
import sa.com.sure.task.webservicesconsumer.model.GithubUser;

public class GithubUsersActivity extends AppCompatActivity implements GithubUserAdapter.GithubUserItemHandler {

    // TODO save users in database
    // TODO don't show recycler until load is completed
    // TODO elegent the code
    // TODO do them remove all TODOs
    // TODO make sure all updates in code done for both activities

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
    public void onUserItemClick(int githubUserPosition) {
        String githubUserUrl = mRecyclerAdapter.getGithubUser(getCurrentTabPage(), githubUserPosition).getHtml_url();
        Uri uri = Uri.parse(githubUserUrl);
        Intent webBrowserItent = new Intent(Intent.ACTION_VIEW, uri);
        if(webBrowserItent.resolveActivity(getPackageManager()) != null){
            startActivity(webBrowserItent);
        }
        else Toast.makeText(this, "Could not found web browser app to open github user!", Toast.LENGTH_LONG).show();
    }

    private void loadGithubUsers() {
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
        if(toastMessage != null && toastMessage.length > 0){
            if(mToast != null) mToast.cancel();
            mToast = Toast.makeText(this, toastMessage[0], Toast.LENGTH_LONG);
            mToast.show();
        }
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
            int currentTabPage = getCurrentTabPage();
            if(mRecyclerAdapter.hasUsers(currentTabPage) &&
                    mRecyclerAdapter.getGithubUsers(currentTabPage).size() != 0) { // users already fetched
                mRecyclerAdapter.setGithubUsers(currentTabPage, null);
                if(IS_MENU_REFRESH_CLICKED){
                    showGithubUsersListView("Users up to date!");
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
            if (integers.length == 0) {
                showErrorMessageView("Didn't send page and per_page query params!.");
                return null;
            }
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(GithubWebServiceInterface.GITHUB_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            GithubWebServiceInterface service = retrofit.create(GithubWebServiceInterface.class);
            Call<List<GithubUser>> usersCall = service.getGithubUsers(integers[0], integers[1]);
//            return getUsersFromLocalJson();//TODO delete when limit is refreshed
            try {// TODO uncoment when getUsersFromLocalJson is commented
                Response<List<GithubUser>> response = usersCall.execute();
                if(response.code() == 200){
                    return response.body();
                } else {
                    showErrorMessageView("Could not get github users. " +
                            "\nResponse Code: " + response.code() + ", " +
                            "\nResponse Message: " + response.message());
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
            }
        }
    }

    private List<GithubUser> getUsersFromLocalJson() { // TODO ToDelete
        Gson gson = new Gson();
        String json = getResources().getString(R.string.users_json);
        List<GithubUser> users = gson.fromJson(json, new TypeToken<List<GithubUser>>(){}.getType());
        return users;
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
