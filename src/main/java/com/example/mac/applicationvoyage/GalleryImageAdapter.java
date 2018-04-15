    package  com.example.mac.applicationvoyage;
    import android.content.Context;
    import android.graphics.Bitmap;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.BaseAdapter;
    import android.widget.Gallery;
    import android.widget.ImageView;


    public class GalleryImageAdapter extends BaseAdapter {
        public Context mContext;

        public Bitmap[] mimage;

        public GalleryImageAdapter(Context context, Bitmap[] image) {
            mContext = context;
           mimage=image;
        }

        public int getCount() {
            return mimage.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }


        public View getView(int index, View view, ViewGroup viewGroup) {
            // TODO Auto-generated method stub
            ImageView i = new ImageView( mContext );

            i.setImageBitmap( mimage[index] );
            i.setLayoutParams( new Gallery.LayoutParams( 250, 250 ) );

            i.setScaleType( ImageView.ScaleType.CENTER_INSIDE );

            return i;

        }
    }
