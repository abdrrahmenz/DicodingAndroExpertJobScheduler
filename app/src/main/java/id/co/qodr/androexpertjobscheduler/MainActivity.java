package id.co.qodr.androexpertjobscheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnStart, btnCancel;
    private int jobId = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = (Button) findViewById(R.id.btn_start);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnStart.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start:
                startJob();
                break;
            case R.id.btn_cancel:
                cancelJob();
                break;
        }
    }

    /* Method startJob() merupakan method yang akan membuat obyek JobInfo
    sebagai komponen utama bagaimana sebuah proses di jalankan secara terjadwal. */
    private void startJob() {
        // Baris dibawah untuk mendapatkan nama komponen yang akan dieksekusi ketika semua kriteria/kondisi terpenuhi.
        ComponentName mServiceComponentJobService = new ComponentName(this, GetCurrentWeatherJobService.class);

        /* Kita menggunakan fasilitas Builder untuk membuat sebuah obyek JobInfo dan menempatkan
           semua kriteria yang ditentukan bagaimana sebuah proses dijalankan.
           Baris dibawah berbunyi kurang lebih seperti ini.
           Buatkan sebuah obyek JobInfo dengan kriteria sebagai berikut :
            1. Dapat dilakukan ketika terhubung ke wifi jadi gak bakal jalan kalau menggunakan paket data.
               Gunakan JobInfo.NETWORK_TYPE_ANY untuk tipe network apapun.
            2. Dapat dilakukan ketika device dalam posisi idle,
               ketika tidak ada interaksi pengguna diatas layar.
            3. Dijalankan dengan interval 180000 milliseconds untuk 3 menit,
               segera rubah menjadi 10800000 untuk 3 jam.
            4. Dapat dijalankan dengan posisi device tidak dalam posisi pengisian batere (charging).
        learn more -> https://developer.android.com/reference/android/app/job/JobInfo.Builder.html */
        JobInfo.Builder builder = new JobInfo.Builder(jobId, mServiceComponentJobService);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
        builder.setRequiresDeviceIdle(true);
        builder.setPeriodic(180000);
        builder.setRequiresCharging(false);

        /* Jika semua sudah diset sesuai dengan design yang diinginkan,
        selanjutnya tinggal meletakan obyek tersebut kedalam sebuah JobScheduler
        untuk dijalankan dengan format sebagai berikut : */
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());

        /* Selesai! Sekarang setelah kamu mengklik tombol ‘start’ maka saat itu pula method startJob()
        dan rangkaian didalamnya dijalankan. Tinggal tunggu saja hasilnya di notifikasi pengguna. Cakep! */
        Toast.makeText(this, "Job Service Started", Toast.LENGTH_SHORT).show();
    }

    /* Method cancelJob akan melakukan pembatalan terhadap JobScheduler yang telah dijalankan
       berdasarkan jobId yang sama dengan yang dibuat sebelumnya. */
    private void cancelJob() {
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.cancel(jobId);

        Toast.makeText(this, "Job Service canceled", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("GetWeather MainActivity", "onDestroy: ");
    }
}
