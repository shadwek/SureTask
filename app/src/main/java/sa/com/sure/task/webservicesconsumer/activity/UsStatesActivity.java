package sa.com.sure.task.webservicesconsumer.activity;

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

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sa.com.sure.task.webservicesconsumer.R;
import sa.com.sure.task.webservicesconsumer.model.UsState;
import sa.com.sure.task.webservicesconsumer.recycler.adapter.UsStateAdapter;

public class UsStatesActivity extends AppCompatActivity {

    // To change if you want another state
    private final String SELECTED_STATE = "NC";

    private UsStateAdapter mRecyclerAdapter;
    private RecyclerView mListRecyclerView;
    private TextView mErrorMessageTextView;
    private ProgressBar mLoadIndicatorProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us_states);

        mListRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message);
        mLoadIndicatorProgressBar = (ProgressBar) findViewById(R.id.pb_load_indicator);

        mRecyclerAdapter = new UsStateAdapter();
        mListRecyclerView.setAdapter(mRecyclerAdapter);
        mListRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mListRecyclerView.setLayoutManager(layoutManager);
        loadUsStates(SELECTED_STATE);
    }

    public void loadUsStates(String state) {
        showUsStatesListView();
        new GetUsStatesTask().execute(state);
    }

    public void showErrorMessageView(final String errorMessage) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mErrorMessageTextView.setVisibility(View.VISIBLE);
                mErrorMessageTextView.setText(errorMessage);
                mListRecyclerView.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void showUsStatesListView() {
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
        if (selectedItemId == R.id.action_refresh) {
            mRecyclerAdapter.setUsStates(null);
            loadUsStates(SELECTED_STATE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class GetUsStatesTask extends AsyncTask<String, Void, List<UsState>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadIndicatorProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<UsState> doInBackground(String... states) {
            if (states.length == 0) {
                showErrorMessageView("Didn't send US state params!.");
                return null;
            }
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(UsStateConsumerHttp.USSTATE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            UsStatesActivity.UsStateConsumerHttp consumer = retrofit.create(UsStatesActivity.UsStateConsumerHttp.class);
            Call<List<UsState>> statesCall = consumer.UsStates(states[0]);
            try {
                Response<List<UsState>> response = statesCall.execute();
                if(response.isSuccessful()){
                    return response.body();
                } else {
                    showErrorMessageView("Could not get US states. " +
                            "\nResponse Code: " + response.code() + ", " +
                            "\nResponse Message: " + response.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<UsState> usStates) {
            super.onPostExecute(usStates);
            mLoadIndicatorProgressBar.setVisibility(View.INVISIBLE);
            if (usStates != null && usStates.size() > 0) {
                showUsStatesListView();
                mRecyclerAdapter.setUsStates(usStates);
            } else {
                showErrorMessageView("Get US states task executed but no states found!");
            }
        }
    }

    public interface UsStateConsumerHttp {
        String USSTATE_URL = "http://www.webservicex.net/uszip.asmx";
        @GET("GetInfoByState")
        Call<List<UsState>> UsStates(@Query("USState") String state);
    }
}