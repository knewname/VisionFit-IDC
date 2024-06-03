package com.example.idc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class activity_Calendar extends AppCompatActivity {

    final OneDayDecorator oneDayDecorator=new OneDayDecorator();
    TextView selectDate;
    TextView valueText;
    TextView countText;
    TextView scoreText;
    int year,month,day;
    public String readDay = null;
    public String str=null;
    public String WorkoutResult;

    public MaterialCalendarView calendarView;
    public Button cha_Btn,del_Btn,save_Btn;
    public TextView diaryTextView,textView2;
    public EditText contextEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        diaryTextView=findViewById(R.id.diaryTextView);
        save_Btn=findViewById(R.id.save_Btn);
        del_Btn=findViewById(R.id.del_Btn);
        cha_Btn=findViewById(R.id.cha_Btn);
        textView2=findViewById(R.id.textView2);
        contextEditText=findViewById(R.id.contextEditText);

        valueText = findViewById(R.id.valueE);
        countText = findViewById(R.id.countE);
        scoreText = findViewById(R.id.scoreE);

        Date dateNow = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd", Locale.getDefault());

        calendarView=(MaterialCalendarView)findViewById(R.id.calendarView);
        calendarView.setSelectedDate(CalendarDay.today());

        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017,1,1))
                .setMaximumDate(CalendarDay.from(2030,12,31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        calendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.custom_months)));
        calendarView.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.custom_weekdays)));
        calendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new MondayDecorator(),
                new TuesdayDecorator(),
                new ThursdayDecorator(),
                new WednesdayDecorator(),
                new FridayDecorator(),
                oneDayDecorator);
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                diaryTextView.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(widget.VISIBLE);
                contextEditText.setVisibility(widget.VISIBLE);
                textView2.setVisibility(widget.INVISIBLE);
                cha_Btn.setVisibility(widget.INVISIBLE);
                del_Btn.setVisibility(widget.INVISIBLE);
                year= date.getYear();
                month= date.getMonth();
                day= date.getDay();
                diaryTextView.setText(year+"-"+(month+1)+"-"+day);
                contextEditText.setText("");
                checkDay(year, month, day);
            }
        });
        save_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDiary(readDay);
                str=contextEditText.getText().toString();
                textView2.setText(str);
                save_Btn.setVisibility(View.INVISIBLE);
                cha_Btn.setVisibility(View.VISIBLE);
                del_Btn.setVisibility(View.VISIBLE);
                contextEditText.setVisibility(View.INVISIBLE);
                textView2.setVisibility(View.VISIBLE);


            }
        });

        selectDate=findViewById(R.id.dateTextView);
        selectDate.setText("Date: "+dateFormat.format(dateNow));

        Intent intent = getIntent(); // startActivity 를 했으므로 그 intent 를 가져온다는 뜻.
        WorkoutResult = intent.getStringExtra("운동결과");



        if (WorkoutResult != null) {

            StringTokenizer resultToken = new StringTokenizer(WorkoutResult, ":");
            String result[] = new String[6];

            for(int i = 0 ; i < 6; i++)
                result[i] = resultToken.nextToken();

            valueText.setText(result[0]);
            countText.setText(result[4] + "개");
            scoreText.setText(result[5] + "%");

            Toast.makeText(this, result[0] + " " + result[1] + "시간 " +  result[2] + "분 " + result[3] + "초, " + result[4] + "개 " + result[5] + "%", Toast.LENGTH_SHORT).show();
            WorkoutResultSave(readDay, WorkoutResult);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

        }
        return super.onOptionsItemSelected(item);
    }
    public void  checkDay(int Year,int Month,int Day){
        readDay=""+Year+"-"+(Month+1)+""+"-"+Day+".txt";//저장할 파일 이름설정
        selectDate.setText("Date: "+Year+"-"+(Month+1)+""+"-"+Day);
        FileInputStream fis=null;//FileStream fis 변수

        try{
            fis=openFileInput(readDay);

            byte[] fileData=new byte[fis.available()];
            fis.read(fileData);
            fis.close();

            str=new String(fileData);
            if(str != null){
                StringTokenizer resultToken = new StringTokenizer(str.trim(), ":");
                String result[] = new String[6];

                for(int i = 0 ; i < 6; i++)
                    result[i] = resultToken.nextToken();

                str = result[0] + " " + result[1] + "시간 " +  result[2] + "분 " + result[3] + "초, " + result[4] + "개 " + result[5] + "%";

                valueText.setText(result[0]);
                countText.setText(result[4] + "개");
                scoreText.setText(result[5] + "%");

            }

            contextEditText.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.VISIBLE);
            textView2.setText(str);

            save_Btn.setVisibility(View.INVISIBLE);
            cha_Btn.setVisibility(View.VISIBLE);
            del_Btn.setVisibility(View.VISIBLE);

            cha_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contextEditText.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.INVISIBLE);
                    contextEditText.setText(str);
                    save_Btn.setVisibility(View.VISIBLE);
                    cha_Btn.setVisibility(View.INVISIBLE);
                    del_Btn.setVisibility(View.INVISIBLE);
                    textView2.setText(contextEditText.getText());
                }

            });
            del_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textView2.setVisibility(View.INVISIBLE);
                    contextEditText.setText("");
                    contextEditText.setVisibility(View.VISIBLE);
                    save_Btn.setVisibility(View.VISIBLE);
                    cha_Btn.setVisibility(View.INVISIBLE);
                    del_Btn.setVisibility(View.INVISIBLE);
                    removeDiary(readDay);
                }
            });
            if(textView2.getText()==null){
                textView2.setVisibility(View.INVISIBLE);
                diaryTextView.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(View.VISIBLE);
                cha_Btn.setVisibility(View.INVISIBLE);
                del_Btn.setVisibility(View.INVISIBLE);
                contextEditText.setVisibility(View.VISIBLE);
            }

        }catch (Exception e){

            valueText.setText(" ");
            countText.setText("0개");
            scoreText.setText("0%");
            e.printStackTrace();
        }
    }
    @SuppressLint("WrongConstant")
    public void removeDiary(String readDay){
        FileOutputStream fos=null;

        try{
            fos=openFileOutput(readDay,MODE_NO_LOCALIZED_COLLATORS);
            String content="";
            fos.write((content).getBytes());
            fos.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @SuppressLint("WrongConstant")
    public void saveDiary(String readDay){
        FileOutputStream fos=null;

        try{
            fos=openFileOutput(readDay,MODE_NO_LOCALIZED_COLLATORS);
            String content=contextEditText.getText().toString();
            fos.write((content).getBytes());
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @SuppressLint("WrongConstant")
    public void WorkoutResultSave(String readDay, String result) {
        if (readDay == null) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH는 0부터 시작하므로 1을 더해야 합니다.
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            readDay = year + "-" + month + "-" + day + ".txt";
        }

        FileOutputStream fos = null;

        try {
            fos = openFileOutput(readDay, Context.MODE_APPEND);
            String strText = "\n" + result;
            fos.write(strText.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}