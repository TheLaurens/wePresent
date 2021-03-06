package wepresent.wepresent;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import wepresent.wepresent.mappers.AsyncTaskReport;
import wepresent.wepresent.mappers.Mapper;
import wepresent.wepresent.mappers.SlidesMapper;

public class SlidesActivity extends Fragment implements AsyncTaskReport {

    private LinearLayout linLayout;
    private SlidesMapper slidesMapper;
    private int sessionId;
    private ArrayList<Map<String, String>> slides;
    SharedPreferences sharedpreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_slides, container, false);
    }

    public void done(Mapper.MapperSort mapper) {
        if(slidesMapper.isSlidesSuccesful()) {
            slides = slidesMapper.getSlides();
            displaySlides();
        } else { //TODO hij kan ze heus wel vinden, hij moet gewoon niet zo zeuren
            if (sessionId>0) {
                Toast.makeText(getActivity().getApplicationContext(), "Slides not available for this session", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onResume(){
        sharedpreferences = this.getActivity().getSharedPreferences("appData", Context.MODE_PRIVATE);

        super.onResume();
        //TODO zorg dat je al gecachede plaatjes displayed inplaats van alles gewoon weer opnieuw op te halen
        if(slidesMapper == null) {
            Bundle b = getArguments();
            sessionId = sharedpreferences.getInt("SessionID", 0);
            slidesMapper = new SlidesMapper(this);
            slidesMapper.start(sessionId);
        }
    }

    private void displaySlides() {
        //TODO Misschien niet het geheugen van de telefoon gebruiken om de slides te cachen, but then again; I DON'T CARE
        // Get where the images should go
        linLayout = (LinearLayout) getView().findViewById(R.id.linearLayout);

        // For each slide
        for ( Map<String, String> slide : slides ) {
            //Create the image loader
            ImageLoader.ImageCache imageCache = new BitmapLruCache();
            ImageLoader imageLoader = new ImageLoader(Volley.newRequestQueue(getView().getContext()), imageCache);

            //Set the image
            NetworkImageView image = new NetworkImageView(getView().getContext());
            image.setImageUrl(slide.get("SlideURL"), imageLoader);
            image.setId(Integer.parseInt(slide.get("id")));
            image.setAdjustViewBounds(true);

            // Add it to the view
            linLayout.addView(image);

            // Add a listener
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Go to the slideView activity
                    Intent intent = new Intent(getActivity(), SlideViewActivity.class);
                    intent.putExtra("SlideID", v.getId());
                    startActivity(intent);
                }
            });
        }
    }

    public static Bundle createBundle(String s) {
        Bundle bundle = new Bundle();
        //bundle.putString( EXTRA_TITLE, title );
        return bundle;
    }
}
