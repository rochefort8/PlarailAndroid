package hoegaarden1917.com.plarailandroid;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private PlarailListAdapter mListAdapter ;
    private ListView mListView;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener ;

    private BleCentral mBleCentral ;
    private Handler mHandler  ;
    private Button button0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize listview and adapter
        mListAdapter  = new PlarailListAdapter(this) ;

        mListView = (ListView) findViewById(R.id.plarail_listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mBleCentral.toggleSwitch();
            }
        });


        mListView.setAdapter(mListAdapter);
        mListAdapter.notifyDataSetChanged();


        mOnRefreshListener  = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mSwipeRefreshLayout.setRefreshing(false);
            }
        };

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        mHandler =new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    Log.d("message","1");
                    PlarailObject object = new PlarailObject() ;
                    object.name = "NaokyAndHiroky" ;
                    mListAdapter.add(object) ;
                    mListAdapter.notifyDataSetChanged();
                } else {
                    Log.d("message","0");
                    mListAdapter.clear();
                }
            }
        };

           mBleCentral = new BleCentral(this,mHandler) ;
    }

}
