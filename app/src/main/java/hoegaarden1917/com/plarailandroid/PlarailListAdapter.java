package hoegaarden1917.com.plarailandroid;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by 0000920402 on 2017/05/23.
 */

public class PlarailListAdapter extends ArrayAdapter<PlarailObject> {

    public PlarailListAdapter(Context context) {
        super(context,R.layout.item_plarail);
//        mNewsContents = contents ;
    };

    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            // create a new view from scratch, otherwise use the view passed in
             view = LayoutInflater.from(getContext()).inflate(R.layout.item_plarail, parent, false);
        }

        PlarailObject object = getItem(position) ;


        TextView txtTitle   = (TextView) view.findViewById(R.id.news_title);
//        TextView txtArticle = (TextView) view.findViewById(R.id.news_article);

//        ImageView imageView = (ImageView) view.findViewById(R.id.news_image);

//        txtTitle.setText(mNewsContents.getTitle(position));
//       txtArticle.setText(mNewsContents.getArticle(position));
//        Bitmap bitmap = BitmapFactory.decodeFile(getContext().getFilesDir() + "/" + mNewsContents.getImagePath(position));

        txtTitle.setText(object.name);
        return view ;
    };
}
