package hoegaarden1917.com.plarailandroid;

import android.bluetooth.BluetoothGatt;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private PlarailListAdapter mListAdapter ;
    private ListView mListView;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener ;

    private BleCentral mBleCentral ;
    private Handler mHandler  ;
    private Button button0 ;
    private static final int REQUEST_CODE = 0;

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

                PlarailObject object = (PlarailObject)parent.getItemAtPosition(position) ;
                BluetoothGatt gatt = object.getGatt() ;
                Log.d("PLA","Pos "+ Integer.toString(position) + "/" + gatt.getDevice().getAddress()) ;

                object.toggleSwitch();
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
                    PlarailObject object = new PlarailObject(mBleCentral,(BluetoothGatt)msg.obj) ;
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

        button0 = (Button)findViewById(R.id.button_start);
        button0.setOnClickListener((View v) -> {
            Log.d("OnClock","OnClock");
                try {
                    // インテント作成
                    Intent intent = new Intent(
                            RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // ACTION_WEB_SEARCH
                    intent.putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(
                            RecognizerIntent.EXTRA_PROMPT,
                            "VoiceRecognitionTest"); // お好きな文字に変更できます

                    // インテント発行
                    startActivityForResult(intent, REQUEST_CODE);
                } catch (ActivityNotFoundException e) {
                    // このインテントに応答できるアクティビティがインストールされていない場合
                    Toast.makeText(MainActivity.this,
                            "ActivityNotFoundException", Toast.LENGTH_LONG).show();
                }
            Log.d("OnClock","OnClock");
        });
    }
    // アクティビティ終了時に呼び出される
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 自分が投げたインテントであれば応答する
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String resultsString = "";

            // 結果文字列リスト
            ArrayList<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            String departure = "出発" ;
            String stop = "停止" ;
            int operation = 0 ;

            for (int i = 0; i< results.size(); i++) {
                String s = results.get(i) ;

                if (s.indexOf(departure) != -1) {
                    operation = 1 ;
                    break ;
                }
                if (s.indexOf(stop) != -1) {
                    operation = 2 ;
                    break ;
                }
            }
            String sss = "Nothing" ;
            PlarailObject object = mListAdapter.getItem(0) ;

            if (operation != 0) {
                switch (operation) {
                    case 1 :
                        object.willStart();
                        sss = departure ;
                        break ;
                    case 2 :
                        object.willStop();
                        sss = stop ;
                        break ;
                    default:
                        break ;
                }
            }

            // トーストを使って結果を表示
            Toast.makeText(this, sss, Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
