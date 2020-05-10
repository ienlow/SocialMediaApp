package com.socialmediaapp.ui.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.client.UserStateListener;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.socialmediaapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.amazonaws.mobile.client.AWSMobileClient.*;

public class HomeScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        class BackgroundWorker extends AsyncTask {
            List<Map<String, AttributeValue>> result;
            List<String> commentsList = new ArrayList<>();
            int count;
            QueryResult queryResult;

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    // Query dynamoDb
                    // returns a list of comments
                    // TODO finalize the partition key
                    AmazonDynamoDBClient dynamoDBClient;
                    dynamoDBClient = new AmazonDynamoDBClient(getInstance().getAWSCredentials());
                    QueryRequest queryRequest = new QueryRequest("NewsFeed").addKeyConditionsEntry("newsFeedId", new Condition().withComparisonOperator("EQ").withAttributeValueList(new AttributeValue().withN("1"))).withAttributesToGet("comments");
                    queryResult = dynamoDBClient.query(queryRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                count = queryResult.getCount();
                result = queryResult.getItems();
                for (Map<String, AttributeValue> item : result) {
                    for (int i = 0; i < item.get("comments").getL().size(); i++) {
                        commentsList.add(item.get("comments").getL().get(i).getS());
                    }
                    //Toast.makeText(HomeScreen.this, item.get("comments").getL().get(0).getS(), Toast.LENGTH_SHORT).show();
                }
                Log.i("ListCount:", String.valueOf(count));
                Log.i("List", commentsList.get(0));
                Log.i("List", commentsList.get(1));

                List<NewsFeed> list = new ArrayList<>();
                list.add(new NewsFeed("Come out to support Sigma Phi Epsilon tonight @ 7PM", "url", "01/02/2012", 5, commentsList.size(), 3));
                //list.add(new NewsFeed("TEST2"));
                ListView listView = findViewById(R.id.home_screen_list_view);
                HomeScreenAdapter adapter = new HomeScreenAdapter(getApplicationContext(), R.id.news_feed_list_view, list);
                listView.setAdapter(adapter);
            }
        }
        BackgroundWorker backgroundWorker = new BackgroundWorker();
        backgroundWorker.execute();
    }

    public void logout(View view) {
        getInstance().signOut();
        getInstance().addUserStateListener(new UserStateListener() {
            @Override
            public void onUserStateChanged(UserStateDetails userStateDetails) {
                switch (userStateDetails.getUserState()){
                    case GUEST:
                        Log.i("userState", "user is in guest mode");
                        break;
                    case SIGNED_OUT:
                        Log.i("userState", "user is signed out");
                        break;
                    case SIGNED_IN:
                        Log.i("userState", "user is signed in");
                        break;
                    case SIGNED_OUT_USER_POOLS_TOKENS_INVALID:
                        Log.i("userState", "need to login again");
                        break;
                    case SIGNED_OUT_FEDERATED_TOKENS_INVALID:
                        Log.i("userState", "user logged in via federation, but currently needs new tokens");
                        break;
                    default:
                        Log.e("userState", "unsupported");
                }
            }
        });
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        finish();
    }
}