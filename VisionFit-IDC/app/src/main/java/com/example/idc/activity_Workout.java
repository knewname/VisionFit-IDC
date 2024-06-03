package com.example.idc;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


import com.google.mediapipe.framework.MediaPipeException;
import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.components.containers.Connection;
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.core.Delegate;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.poselandmarker.*;

import static android.Manifest.permission.CAMERA;


public class activity_Workout extends AppCompatActivity
        implements CameraBridgeViewBase.CvCameraViewListener2 {
    Button start;
    Button pause;
    Button complete;
    LinearLayout timeCountLV;
    TextView hourTV, minuteTV, secondTV;
    TextView countTV, accuracyTV;
    String WorkoutResult;
    int count,accuracy;
    int hour, minute, second;

    String selected_workout;
    //opencv 카메라 관련
    private int m_Camidx = 1;//front : 1, back : 0
    private CameraBridgeViewBase m_CameraView;

    private Mat matInput;

    private static final int CAMERA_PERMISSION_CODE = 200;
    private static final String TAG = "opencv";

    //mediapipe

    private PoseLandmarker poseLandmarker;
    private PoseLandmarker.PoseLandmarkerOptions poseLandmarkerOptions;
    PoseLandmarkerResult poseLandmarkerResult;

     //timer

    private Timer timer;
    private boolean timerProcess =  false; //스톱워치가 돌아가고 있는지 확인

    //counter
    private poseCounter poseCounter;
    private CounterStatus counterStatus;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent=getIntent();
        selected_workout = intent.getStringExtra("workout");
        TextView workout=findViewById(R.id.workout);
        workout.setText(selected_workout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        timeCountLV = (LinearLayout)findViewById(R.id.timeCountLV);


        hourTV = (TextView)findViewById(R.id.hourTV);
        minuteTV = (TextView)findViewById(R.id.minuteTV);
        secondTV = (TextView)findViewById(R.id.secondTV);


        //opencv 관련
        m_CameraView = (CameraBridgeViewBase)findViewById(R.id.textureView);
        m_CameraView.setVisibility(SurfaceView.VISIBLE);
        m_CameraView.setCvCameraViewListener(this);
        m_CameraView.setCameraIndex(m_Camidx);


         try{

             BaseOptions baseOptions = BaseOptions.builder().
                     setModelAssetPath("pose_landmarker_lite.task").
                     setDelegate(Delegate.GPU).
                     build();
             poseLandmarkerOptions = PoseLandmarker.PoseLandmarkerOptions.builder().
                     setBaseOptions(baseOptions).
                     setRunningMode(RunningMode.LIVE_STREAM).
                     setResultListener(this::returnLivestreamResult).
                     build();

             poseLandmarker = poseLandmarker.createFromOptions(this, poseLandmarkerOptions);

         } catch (MediaPipeException e){

             e.printStackTrace();

         }

         countTV = findViewById(R.id.count);
         counterStatus = new CounterStatus(false, 0);
         poseCounter = new poseCounter(poseLandmarkerResult);


        start=findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                counterStatus.status = false;
                timerProcess = true;

                if(hourTV.getText() == "0" && minuteTV.getText() == "0" && secondTV.getText() == "0"){
                    hour = 0;
                    minute = 0;
                    second = 0;
                } else{
                    hour = Integer.parseInt(hourTV.getText().toString());
                    minute = Integer.parseInt(minuteTV.getText().toString());
                    second = Integer.parseInt(secondTV.getText().toString());

                }

                if(timer != null)
                    timer.cancel();

                timer = new Timer();

                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        // 반복실행할 구문

                        second++;
                        if(second == 60) {
                            //1초씩 증가
                            minute++;
                            second = 0;

                        } else if(minute == 60) {
                            // 1분 = 60초
                            hour++;
                            minute = 0;

                        }

                        //시, 분, 초가 10이하(한자리수) 라면
                        // 숫자 앞에 0을 붙인다 ( 8 -> 08 )
                        if(second <= 9){
                            secondTV.setText("0" + second);
                        } else {
                            secondTV.setText(Integer.toString(second));
                        }

                        if(minute <= 9){
                            minuteTV.setText("0" + minute);
                        } else {
                            minuteTV.setText(Integer.toString(minute));
                        }

                        if(hour <= 9){
                            hourTV.setText("0" + hour);
                        } else {
                            hourTV.setText(Integer.toString(hour));
                        }

                    }
                };

                //타이머를 실행
                timer.schedule(timerTask, 0, 1000); //Timer 실행
                start.setEnabled(false);
            }
        });
        pause=findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timer != null)
                    timer.cancel();
                start.setEnabled(true);
                timerProcess = false;
            }
        });
        complete=findViewById(R.id.complete);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                String W_Time=hourTV.getText().toString()+":"+minuteTV.getText().toString()+":"+secondTV.getText().toString()+":";
                WorkoutResult = selected_workout+":"+W_Time+ count +":"+ accuracy +"";
                Intent intent = new Intent(activity_Workout.this, activity_Calendar.class);
                intent.putExtra("운동결과", WorkoutResult.trim());
                startActivity(intent);
                finish();
            }
        });

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



    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        matInput = inputFrame.rgba();

        long timestamp = SystemClock.uptimeMillis();
        // Mat을 MPImage 변환
        MPImage mpImage = convertMatToMPImage(matInput);

        // MPImage를 PoseLandmarker에 전달하여 처리
        processMPImage(mpImage, timestamp);
        // 결과를 이용하여 연결된 관절들을 시각적으로 보여준
        matInput = procssMediapipe(matInput);


        return matInput;
    }




    // Convert ByteBuffer to Image
    public MPImage convertMatToMPImage(Mat mat){ //ByteBuffer buffer, int width, int height) {

        if (mat == null) {
            Log.e(TAG, "matInput is null");
            return null;
        }

        int width = mat.cols();
        int height = mat.rows();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);

        MPImage mpImage =  new BitmapImageBuilder(bitmap).build();
        return mpImage;

    }
    private void processMPImage(MPImage mpImage, long time) {
        // PoseLandmarker에 MPImage를 전달하여 처리
        if (poseLandmarker != null) {
            poseLandmarker.detectAsync(mpImage, time);
        }
    }



    private Mat procssMediapipe(Mat matInput){
        try{
            if(poseLandmarkerResult != null){
                if(poseLandmarkerResult.landmarks().size() > 0){


                    int cameraWidth = matInput.cols();
                    int cameraHeight = matInput.rows();


                    for (Connection poseLandmark : PoseLandmarker.POSE_LANDMARKS) {
                        if (poseLandmark != null) {
                            for(int i = 0; i < poseLandmarkerResult.landmarks().get(0).size(); i++) {
                                Imgproc.circle(
                                        matInput,
                                        new Point(poseLandmarkerResult.landmarks().get(0).get(poseLandmark.start()).x() * cameraWidth,
                                                poseLandmarkerResult.landmarks().get(0).get(poseLandmark.start()).y() * cameraHeight),
                                        4,
                                        new Scalar(245, 245, 220),
                                        Imgproc.LINE_AA,
                                        0
                                );


                            }

                            Imgproc.line(matInput,
                                    new Point(poseLandmarkerResult.landmarks().get(0).get(poseLandmark.start()).x() * cameraWidth,
                                            poseLandmarkerResult.landmarks().get(0).get(poseLandmark.start()).y() * cameraHeight),
                                    new Point(poseLandmarkerResult.landmarks().get(0).get(poseLandmark.end()).x()* cameraWidth,
                                            poseLandmarkerResult.landmarks().get(0).get(poseLandmark.end()).y() * cameraHeight),
                                    new Scalar(0, 255, 0, 150),
                                    6,
                                    Imgproc.LINE_4,
                                    0);

                        }
                    }
                    counter();
                }
            }

        } catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }finally {
            return matInput;

        }

    }

    private void counter(){
        if(timerProcess){
            poseCounter.saveLandmark(poseLandmarkerResult);
            if(selected_workout.equals("푸쉬업")) {
                counterStatus = poseCounter.pushUpCounter(counterStatus);
                count = counterStatus.cnt;
                countTV.setText(String.valueOf(count));
            }
            else if(selected_workout.equals("풀업")) {
                counterStatus = poseCounter.pullUpCount(counterStatus);
                count = counterStatus.cnt;
                countTV.setText(String.valueOf(count));
            }
            else if(selected_workout.equals("윗몸일으키기")){
                counterStatus = poseCounter.situpCounter(counterStatus);
                count = counterStatus.cnt;
                countTV.setText(String.valueOf(count));
            }
            else if(selected_workout.equals("스쿼트")) {
                counterStatus = poseCounter.squatCounter(counterStatus);
                count = counterStatus.cnt;
                countTV.setText(String.valueOf(count));
            }
        }
    }

    private void returnLivestreamResult(PoseLandmarkerResult result, MPImage input) {
        long finishTimeMs = SystemClock.uptimeMillis();
        long inferenceTime = finishTimeMs - result.timestampMs();
        poseLandmarkerResult = result;

    }



    //생명주기관련
    @Override
    protected void onStart() {
        super.onStart();
        boolean havePermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{CAMERA}, CAMERA_PERMISSION_CODE);
                havePermission = false;
            }
        }
        if (havePermission) {
            onCameraPermissionGranted();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (OpenCVLoader.initDebug()) {
            m_LoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }else {

        }


    }

    @Override
    protected void onPause() {
        // 액티비티가 일시정지되면 카메라를 닫음
        super.onPause();
        if (m_CameraView != null)
            m_CameraView.disableView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (m_CameraView != null)
            m_CameraView.disableView();
    }


    protected void onCameraPermissionGranted() {
        List<? extends CameraBridgeViewBase> cameraViews = getCameraViewList();
        if (cameraViews == null) {
            return;
        }
        for (CameraBridgeViewBase cameraBridgeViewBase: cameraViews) {
            if (cameraBridgeViewBase != null) {
                cameraBridgeViewBase.setCameraPermissionGranted();
            }
        }
    }

    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(m_CameraView);
    }

    private BaseLoaderCallback m_LoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    m_CameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };




}


