package jp.rika.sumitomo.exifmap;

import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private double latitudeA;
    private double longitudeA;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        try {
            InputStream stream_in = this.getResources().getAssets().open("sample.JPG");
            ExifInterface exif = new ExifInterface(stream_in);

            if (exif != null) {
                String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);//緯度
                String latitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);//北緯or南緯
                String longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);//経度
                String longitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);//東経or西経

                //時,分,秒（60進数)
                Log.d("exif", "latitude : " + latitude);
                Log.d("exif", "latitudeRef : " + latitudeRef);
                Log.d("exif", "longitude : " + longitude);
                Log.d("exif", "longitudeRef : " + longitudeRef);

                //10進数に変換
                latitudeA = ExifHourMinSecToDegreesLatitude(latitude);
                longitudeA = ExifHourMinSecToDegreesLongitude(longitude);

                Log.d("DegreeExif","latitude : " + ExifLatitudeToDegrees(latitudeRef,latitude));
                Log.d("DegreeExif","longitude : " + ExifLongitudeToDegrees(longitudeRef,longitude));

            }
        } catch (IOException e) {
            Log.d("exif","exif is null");
            e.printStackTrace();
        }

    }
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        setMarker();
    }
    private void setMarker(){
        //マーカーを追加
        LatLng markerPos = new LatLng(latitudeA,longitudeA);
        MarkerOptions options = new MarkerOptions();
        options.position(markerPos);
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPos, 15));
    }
    private double ExifHourMinSecToDegreesLatitude(String latitudE) {
        String hourminsec[] = latitudE.split(",");
        String hour[] = hourminsec[0].split("/");
        String min[] = hourminsec[1].split("/");
        String sec[] = hourminsec[2].split("/");
        double dhour = (double)Integer.parseInt(hour[0]) / (double)Integer.parseInt(hour[1]);
        double dmin = (double)Integer.parseInt(min[0]) / (double)Integer.parseInt(min[1]);
        double dsec = (double)Integer.parseInt(sec[0]) / (double)Integer.parseInt(sec[1]);
        double degrees = dhour + dmin / 60.0 + dsec / 3600.0;
        return degrees;
    }

    private double ExifHourMinSecToDegreesLongitude(String longitudE) {
        String hourminsec[] = longitudE.split(",");
        String hour[] = hourminsec[0].split("/");
        String min[] = hourminsec[1].split("/");
        String sec[] = hourminsec[2].split("/");
        double dhour = (double)Integer.parseInt(hour[0]) / (double)Integer.parseInt(hour[1]);
        double dmin = (double)Integer.parseInt(min[0]) / (double)Integer.parseInt(min[1]);
        double dsec = (double)Integer.parseInt(sec[0]) / (double)Integer.parseInt(sec[1]);
        double degrees = dhour + dmin / 60.0 + dsec / 3600.0;
        return degrees;
    }

    private String ExifLatitudeToDegrees(String ref, String latitudE) {
        String answer = String.valueOf(ref.equals("S") ? -1.0 : 1.0 * ExifHourMinSecToDegreesLatitude(latitudE));
        return answer;
    }

    private String ExifLongitudeToDegrees(String ref, String longitudE) {
        String answer = String.valueOf(ref.equals("W") ? -1.0 : 1.0 * ExifHourMinSecToDegreesLongitude(longitudE));
        return answer;
    }

}