package com.example.ovsadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog; //tarih seçmek için.
import android.app.TimePickerDialog; //saat seçmek için.
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker; //kullanıcıdan date bilgisini almak için.
import android.widget.EditText; //saat bilgisini almak için.
import android.widget.TextView; //parti adı için
import android.widget.TimePicker; //kullanıcıdan saat bilgisini edittext ile alıyoruz.
//Kullanacağımız Edittext’in touch listener’ı sayesinde manuel giriş yerine TimePickerDialog nesnesini çağıracağız.
import android.widget.Toast; //kullanıcıya bilgilendirici mesaj vermek için

import com.example.ovsadmin.Class.PartyGraph; //setter ve getter metodlarını kullanmak için.
import com.github.mikephil.charting.charts.BarChart; //barchart
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate; 
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat; //tarih ve saat işlemleri
import java.util.ArrayList;
import java.util.Calendar;        //tarih ve saat işlemleri

public class MainActivity extends AppCompatActivity {
    //bileşenleri tanımladım.
    DatabaseReference refP,refT,refV;
    private EditText et_party;
    private Button btn_savePArty,btn_startVote,btn_endVote,btn_showRes;
    private TextView tv_startDate,tv_endDate;
    private BarChart barChart;

    private ArrayList<BarEntry>  barEntryArrayList;
    private ArrayList<String> labelsNames;
    private ArrayList<PartyGraph> graphList = new ArrayList<>();
    int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {  //Bu metod uygulama açıldığında çalıştırılan metod.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init(){

        refP = FirebaseDatabase.getInstance().getReference("Parties");
        refT = FirebaseDatabase.getInstance().getReference("VoteTime");
        refV = FirebaseDatabase.getInstance().getReference("TotalVotes");

        et_party = findViewById(R.id.et_partyname);
        btn_startVote = findViewById(R.id.btn_startVote);
        btn_endVote = findViewById(R.id.btn_endVote);
        btn_savePArty = findViewById(R.id.btn_saveParty);
        btn_showRes = findViewById(R.id.btn_showRes);
        tv_startDate = findViewById(R.id.tv_startDate);
        tv_endDate = findViewById(R.id.tv_endDate);
        barChart = findViewById(R.id.barChart);

    }

    public void saveParty(View view) {
        String party = et_party.getText().toString();
        addParties(party);
        et_party.setText("");
    }

    public void startDate(View view) {
        dateTimePicker(tv_startDate);
    }

    public void endDate(View view) {
        dateTimePicker(tv_endDate);
    }

    public void startVote(View view) {
        String startTime = tv_startDate.getText().toString();
        String endTime = tv_endDate.getText().toString();
        if (!startTime.equals("Start Date") && !endTime.equals("End Date")){
            refT.child("start").setValue(startTime);
            refT.child("end").setValue(endTime);

            refT.child("status").setValue("true");
        }else {
            Toast.makeText(getApplicationContext(),"You entered an incomplete or incorrect date",Toast.LENGTH_SHORT).show(); // yanlış zaman aralığı girdiniz
        }

        Toast.makeText(getApplicationContext(),"Voting time frame has been adjusted ... ",Toast.LENGTH_SHORT).show(); //oylama zaman aralığı ayarlandı
    }

    public void endVote(View view) {
        refT.child("status").setValue("false");
        Toast.makeText(getApplicationContext(),"Voting process ended ... ",Toast.LENGTH_SHORT).show(); // oylama zamanı bitti
    }

    public void showRes(View view) {
        //barEntryArrayList.clear();
        //labelsNames.clear();
        showGraph();
    }

    public void addParties(String party){
        refP.push().child("partyname").setValue(party);
        refV.child(party).setValue(0);
        Toast.makeText(getApplicationContext(),"Party addition is successful ... ",Toast.LENGTH_SHORT).show();
    }

    public void dateTimePicker(TextView tv){
        Calendar calendar = Calendar.getInstance();
        //DatePickerDialog
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                //TimePickerDialog
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,month);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
                        tv.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };

                new TimePickerDialog(MainActivity.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MONTH),true).show();

            }
        };
        new DatePickerDialog(MainActivity.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void showGraph(){
        barEntryArrayList = new ArrayList<>();
        labelsNames = new ArrayList<>();


        refV.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        graphList.add(new PartyGraph(dataSnapshot.getKey(),Integer.parseInt(dataSnapshot.getValue().toString())));
                    }

                    for (int i = 0; i < graphList.size(); i ++){
                        String parties = graphList.get(i).getPartyName();
                        int votes = graphList.get(i).getVote();
                        total += votes;
                        barEntryArrayList.add(new BarEntry(i,votes));
                        labelsNames.add(parties);
                    }

                    BarDataSet barDataSet = new BarDataSet(barEntryArrayList,"Total Votes : "+total);
                    barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    Description description = new Description();
                    description.setText("Parties");
                    barChart.setDescription(description);
                    BarData barData = new BarData(barDataSet);
                    barChart.setData(barData);

                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(labelsNames));

                    xAxis.setPosition(XAxis.XAxisPosition.TOP);
                    xAxis.setDrawGridLines(false);
                    xAxis.setDrawAxisLine(false);
                    xAxis.setGranularity(1f);
                    xAxis.setLabelCount(labelsNames.size());
                    xAxis.setLabelRotationAngle(270);
                    barChart.animateY(2000);
                    barChart.invalidate();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}