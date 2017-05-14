package sa.com.sure.task.webservicesconsumer.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;
import java.util.Map;

import sa.com.sure.task.webservicesconsumer.R;
import sa.com.sure.task.webservicesconsumer.database.Column;
import sa.com.sure.task.webservicesconsumer.database.UsStateDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void lunchGithubUsersActivity(View view){
        Lunch.lunchActivity(this, GithubUsersActivity.class);
    }

    public void lunchNcStatesActivity(View view){
        Lunch.lunchActivity(this, UsStatesActivity.class);
    }
}
