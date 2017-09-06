package id.co.qodr.androexpertjobscheduler;

import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;

/**
 * Created by adul on 06/09/17.
 *
 * Kelas GetCurrentWeatherJobService memilki dua fungsi  ketika dijalankan :
 * 1. Melakukan koneksi HTTP ke webservice dari openweathermap.org untuk mengakses data yang terdapat pada endpoint berikut : "http://api.openweathermap.org/data/2.5/weather?q="+CITY+"&appid="+APP_ID; untuk mendapatkan cuaca saat ini untuk kota yang ditentukan, disini kota Pekalongan
 * 2. Menampilkan notifikasi ke pengguna yang akan ditampilkan di panel notifikasi pada device tentang cuaca saat ini.
 *
 */

public class GetCurrentWeatherJobService extends JobService {
    public static final String TAG = "GetWeather";
    private final String APP_ID = "33f97808416c897fafc3e104ed2862a0";
    private final String CITY = "Pekalongan";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "onStartJob() Executed");
        getCurrentWeather(jobParameters);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "onStopJob() Executed");
        return false;
    }

    private void getCurrentWeather(final JobParameters jobParameters) {
        Log.d(TAG, "getCurrentWeather: is Running");
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.openweathermap.org/data/2.5/weather?q="+CITY+"&appid="+APP_ID;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                jobFinished(jobParameters, false);
                String result = new String(responseBody);
                Log.d(TAG, "onSuccess: "+result);
                try {
                    JSONObject responseObject = new JSONObject(result);
                    String currentWeather = responseObject.getJSONArray("weather").
                            getJSONObject(0).getString("main");
                    String description = responseObject.getJSONArray("weather").
                            getJSONObject(0).getString("description");
                    double tempInKelvin = responseObject.getJSONObject("main").getDouble("temp");

                    /* Karena data dari Temprature awal (dalam variable templnKelvin)
                    yang diberikan berada dalam bentuk kelvin maka harus dikurangi 273
                    untuk menjadikannya kedalam celcius */
                    double tempInCelcius = tempInKelvin - 273;

                    /* new DecimalFormat("##.##").format(tempInCelcius) →
                    digunakan untuk memformat tampilan agar hanya ada dua nilai dibelakang koma */
                    String temprature = new DecimalFormat("##.##").format(tempInCelcius);

                    String title = "Current Weather";
                    String message = currentWeather +", "+description+" with "+temprature+" celcius";

                    int notifId = 100;
                    showNotification(getApplicationContext(), title, message, notifId);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "onFailure: Failed Get Data");
                jobFinished(jobParameters, false);
            }
        });
    }

    private void showNotification(Context context, String title, String message, int notifId) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context,android.R.color.white))
                .setVibrate(new long[]{1000,1000,1000,1000,1000})
                .setSound(alarmSound);

        builder.setAutoCancel(true);
        notificationManager.notify(notifId, builder.build());
    }
}
