package sa.com.sure.task.webservicesconsumer.activity;

import android.os.AsyncTask;
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

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sa.com.sure.task.webservicesconsumer.R;
import sa.com.sure.task.webservicesconsumer.model.UsState;
import sa.com.sure.task.webservicesconsumer.recycler.adapter.UsStateAdapter;

public class UsStatesActivity extends AppCompatActivity {

    // TODO save states in database

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

    private void loadUsStates(String state) {
        showUsStatesListView();
        new GetUsStatesTask().execute(state);
    }

    private void showErrorMessageView(final String errorMessage) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mErrorMessageTextView.setVisibility(View.VISIBLE);
                mErrorMessageTextView.setText(errorMessage);
                mListRecyclerView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void showUsStatesListView() {
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

    private class GetUsStatesTask extends AsyncTask<String, Void, UsState> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadIndicatorProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected UsState doInBackground(String... states) {
            if (states.length == 0) {
                showErrorMessageView("Didn't send US state params!.");
                return null;
            }
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(UsSTateWebServiceInterface.USSTATE_URL)
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build();
            UsSTateWebServiceInterface service = retrofit.create(UsSTateWebServiceInterface.class);
            Call<UsState> statesCall = service.getUsStates(states[0]);
            try {
                Response<UsState> response = statesCall.execute();
                if(response.code() == 200){
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
        protected void onPostExecute(UsState usStates) {
            super.onPostExecute(usStates);
            mLoadIndicatorProgressBar.setVisibility(View.INVISIBLE);
            if (usStates != null &&
                    usStates.getStates() != null &&
                    usStates.getStates().size() > 0) {
                mRecyclerAdapter.setUsStates(usStates);
                showUsStatesListView();
            } else {
                showErrorMessageView("Get US states task executed but no states found!");
            }
        }
    }

    private interface UsSTateWebServiceInterface {
        String USSTATE_URL = "http://www.webservicex.net/uszip.asmx/";
        @GET("GetInfoByState")
        Call<UsState> getUsStates(@Query("USState") String state);
    }
}