package sa.com.sure.task.webservicesconsumer.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import sa.com.sure.task.webservicesconsumer.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void lunchActivity(Context fromActivity, Class toActivity){
        Intent toIntent = new Intent(fromActivity, toActivity);
        if(toIntent.resolveActivity(getPackageManager()) != null){
            startActivity(toIntent);
        }
    }

    public void lunchGithubUsersActivity(View view){
        lunchActivity(this, GithubUsersActivity.class);
    }

    public void lunchNcStatesActivity(View view){
        lunchActivity(this, UsStatesActivity.class);
    }

//    public List<GithubUser> getUsers(int page, int usersPerPage){
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(GetUsStatesTask.GITHUB_API_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        GetUsStatesTask service = retrofit.create(GetUsStatesTask.class);
//        Call<List<GithubUser>> usersCall = service.githubUsers(page, usersPerPage);
//        Callback<List<GithubUser>> callback = new Callback<List<GithubUser>>() {
//            List<GithubUser> githubUsers = new ArrayList<>();
//            @Override
//            public void onResponse(Call<List<GithubUser>> call, Response<List<GithubUser>> response) {
//                if (response.code() == 200) {
//                    githubUsers = response.body();
//                    for(GithubUser user : githubUsers) Log.d("User", user.getLogin());
//                    Toast.makeText(MainActivity.this, "Got users: " + githubUsers.size(), Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(MainActivity.this, "Did not work: " + String.valueOf(response.code()), Toast.LENGTH_LONG).show();
//                }
//            }
//            @Override
//            public void onFailure(Call<List<GithubUser>> call, Throwable t) {
//                Log.d("Error", t.getMessage());
//            }
//            public List<GithubUser> getUsers(){
//                return this.githubUsers;
//            }
//        };
//        usersCall.enqueue(callback);
//        return callback.getUsers();
//
//    }
}
