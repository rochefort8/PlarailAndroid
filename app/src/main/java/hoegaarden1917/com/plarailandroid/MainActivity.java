package hoegaarden1917.com.plarailandroid;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private PlarailListAdapter mListAdapter ;
    private ListView mListView;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener ;

    private BleCentral mBleCentral ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize listview and adapter
        mListAdapter  = new PlarailListAdapter(this) ;
        mListView = (ListView) findViewById(R.id.plarail_listView);
        mListView.setAdapter(mListAdapter);
        mListAdapter.notifyDataSetChanged();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //
            }
        });

        mOnRefreshListener  = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                PlarailObject object = new PlarailObject() ;
                object.name = "NaokyAndHiroky" ;
                mListAdapter.add(object) ;
                mListAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        };

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);

           mBleCentral = new BleCentral(this) ;
    }

}
