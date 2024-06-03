package com.example.idc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final OneDayDecorator oneDayDecorator=new OneDayDecorator();
    private Spinner spinner;
    MaterialCalendarView calendarView;

    private CustomSpinnerAdapter adapter;
    String selected_workout;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        calendarView.addDecorators(new SundayDecorator(), new SaturdayDecorator(), new MondayDecorator(), new TuesdayDecorator(),
                new ThursdayDecorator(), new WednesdayDecorator(), new FridayDecorator(), oneDayDecorator);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Intent intent = new Intent(MainActivity.this, activity_Calendar.class);
                startActivity(intent);
                finish();

            }
        });
        spinner = (Spinner)findViewById(R.id.spinner);

        String[] list =  {"운동 선택","푸쉬업","플랭크", "스쿼트", "런지", "풀업", "브릿지"};
        SpinnerAdapter s1Adapter = new SpinnerAdapter(this,android.R.layout.simple_spinner_item, list);
        spinner.setAdapter(s1Adapter);

        button= findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_workout = spinner.getSelectedItem().toString();
                if(selected_workout.equals("운동 선택")){
                    Toast.makeText(MainActivity.super.getApplicationContext(), "운동을 선택하세요", Toast.LENGTH_LONG ).show();
                }else{
                    Intent intent2 = new Intent(MainActivity.this, activity_Workout.class);
                    intent2.putExtra("workout",selected_workout);
                    startActivity(intent2);
                }
            }
        });
    }
}