package sa.com.sure.task.webservicesconsumer.activity;

import android.app.Activity;
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
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sa.com.sure.task.webservicesconsumer.R;
import sa.com.sure.task.webservicesconsumer.database.UsStateDatabase;
import sa.com.sure.task.webservicesconsumer.dialog.Dialog;
import sa.com.sure.task.webservicesconsumer.model.UsState;
import sa.com.sure.task.webservicesconsumer.network.Network;
import sa.com.sure.task.webservicesconsumer.recycler.adapter.UsStateAdapter;

public class UsStatesActivity extends AppCompatActivity {

    // To change if you want another state
    private final String SELECTED_STATE = "NC";

    private UsStateAdapter mRecyclerAdapter;
    private RecyclerView mListRecyclerView;
    private TextView mErrorMessageTextView;
    private ProgressBar mLoadIndicatorProgressBar;
    private Toast mToast;
    private Activity mActivity;
    private UsStateDatabase mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us_states);

        mListRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message);
        mLoadIndicatorProgressBar = (ProgressBar) findViewById(R.id.pb_load_indicator);
        mDbHelper = new UsStateDatabase(this);
        mActivity = this;

        mRecyclerAdapter = new UsStateAdapter();
        mListRecyclerView.setAdapter(mRecyclerAdapter);
        mListRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mListRecyclerView.setLayoutManager(layoutManager);
        loadUsStates(SELECTED_STATE);
    }

    private void loadUsStates(String state) {
        if(!Network.hasInternetConnection(this)) {
            Dialog.showNoInternetDialog(this);
            showErrorMessageView(mActivity.getString(R.string.dialog_no_internet_title));
            return;
        }
        new GetUsStatesTask().execute(state);
        mListRecyclerView.getLayoutManager().scrollToPosition(0);
    }

    private void showErrorMessageView(final String errorMessage) {
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setText(errorMessage);
        mLoadIndicatorProgressBar.setVisibility(View.INVISIBLE);
        mListRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void showUsStatesListView(final String... toastMessage){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mLoadIndicatorProgressBar.setVisibility(View.INVISIBLE);
        mListRecyclerView.setVisibility(View.VISIBLE);
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
        if (selectedItemId == R.id.action_refresh) {
            loadUsStates(SELECTED_STATE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetUsStatesTask extends AsyncTask<String, Void, UsState> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mErrorMessageTextView.setVisibility(View.INVISIBLE);
            mLoadIndicatorProgressBar.setVisibility(View.VISIBLE);
            mListRecyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected UsState doInBackground(String... states) {
            if (states.length == 0)
                return null;
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(UsStateWebServiceInterface.USSTATE_URL)
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build();
            UsStateWebServiceInterface service = retrofit.create(UsStateWebServiceInterface.class);
            Call<UsState> statesCall = service.getUsStates(states[0]);
            try {
                Response<UsState> response = statesCall.execute();
                if(response.code() == 200){
                    return response.body();
                } else {
                    showErrorMessageView(mActivity.getString(R.string.error_could_not_get_states)
                            .replace("$_responseCode", String.valueOf(response.code()))
                            .replace("$_message", response.message()));
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
                showUsStatesListView();
                mRecyclerAdapter.setUsStates(usStates);
                new DbInertUsStatesTask().execute(usStates.getStates());
            }
        }
    }

    private class DbInertUsStatesTask extends AsyncTask<List<UsState.State>, Void, Void>{

        @Override
        protected Void doInBackground(List<UsState.State>... states) {
            if (mDbHelper.has(states[0]))
                showToast(mActivity.getString(R.string.db_states_exist));
            int numInsertedUsers = mDbHelper.insert(states[0]);
            if( numInsertedUsers > 0)
                showToast(numInsertedUsers + " " + mActivity.getString(R.string.db_states_inserted));
            else showToast(mActivity.getString(R.string.db_error_could_not_insert_states));
            mDbHelper.close();
            return null;
        }
    }

    private interface UsStateWebServiceInterface {
        String USSTATE_URL = "http://www.webservicex.net/uszip.asmx/";
        @GET("GetInfoByState")
        Call<UsState> getUsStates(@Query("USState") String state);
    }
}