package co.haslo.graphusingmp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    LineChart lineChart;
    int DATA_RANGE = 30;

    LineData lineData;
    LineDataSet setValueTransfer;

    ArrayList<Entry> entryData;
    ArrayList<String> convertEntryData;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
       lineChart = (LineChart) findViewById(R.id.mp_chart);
       chartInit();
       threadStart();
    }

    private void chartInit() {

        lineChart.setAutoScaleMinMaxEnabled(true);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setAxisMaximum(60f);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        entryData = new ArrayList<Entry>();
        setValueTransfer = new LineDataSet(entryData, "EMG DATA");
        setValueTransfer.setColor(Color.RED);
        setValueTransfer.setDrawValues(false);
        setValueTransfer.setDrawCircles(false);
        setValueTransfer.setAxisDependency(YAxis.AxisDependency.LEFT);

        lineData = new LineData();
        lineData.addDataSet(setValueTransfer);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    public void chartUpdate(int data) {
       if(entryData.size() > DATA_RANGE){
           entryData.remove(0);
           for(int i = 0; i < DATA_RANGE; i++){
               entryData.get(i).setX(i);
           }
       }
       entryData.add(new Entry(entryData.size(), data));
       setValueTransfer.notifyDataSetChanged();
       lineChart.notifyDataSetChanged();
       lineChart.invalidate();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
       @SuppressLint("HandlerLeak")
       @Override
        public void handleMessage(Message msg) {
           if(msg.what == 0) {
               int data = 0;
               data = (int)(Math.random()*1024);
               chartUpdate(data);
//               Log.d("Handler", data+"");
           }
       }
    };

   class GraphThread extends Thread {
       @Override
       public void run() {
           int i = 0;
           while(true){
               handler.sendEmptyMessage(i);
               try {
                   Thread.sleep(100);
               } catch (InterruptedException e){
                   e.printStackTrace();
               }
           }
       }
   }

   private void threadStart() {
       GraphThread thread = new GraphThread();
       thread.setDaemon(true);
       thread.start();
   }
}