package com.example.idc;

import android.os.Handler;
import android.util.Log;

import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult;

import org.opencv.core.Mat;

import java.util.concurrent.TimeUnit;

class LandmarkPoint{
    double x;
    double y;
    LandmarkPoint(float x, float y){
        this.x = x;
        this.y = y;
    }
}

class CounterStatus {
    boolean status;
    int cnt;
    public long lastUpdateTime;

    CounterStatus(boolean status, int cnt) {
        this.status = status;
        this.cnt = cnt;
        this.lastUpdateTime = 0;
    }
}

public class poseCounter {
    PoseLandmarkerResult poseLandmarkerResult;


    //필요 랜드마크 포인트 저장
    private LandmarkPoint l_shoulder;
    private LandmarkPoint l_elbow;
    private LandmarkPoint l_wrist;
    private LandmarkPoint r_shoulder;
    private LandmarkPoint r_elbow;
    private LandmarkPoint r_wrist;
    private LandmarkPoint l_hip;
    private LandmarkPoint l_knee;
    private LandmarkPoint l_ankle;
    private LandmarkPoint r_hip;
    private LandmarkPoint r_knee;
    private LandmarkPoint r_ankle;
    private LandmarkPoint r_mouth;
    private LandmarkPoint l_mouth;
    private LandmarkPoint nose;

    private double left_arm_angle;
    private double right_arm_angle;
    private double left_leg_angle;
    private double right_leg_angle;
    private double neck_angle;
    private double abdomen_angle;


    public poseCounter(PoseLandmarkerResult result) {
        poseLandmarkerResult = result;
    }

    public void saveLandmark(PoseLandmarkerResult poseLandmarkerResult) {


        //left arm
        l_shoulder = new LandmarkPoint(poseLandmarkerResult.landmarks().get(0).get(12).x(),
                poseLandmarkerResult.landmarks().get(0).get(12).y());
        l_elbow = new LandmarkPoint(poseLandmarkerResult.landmarks().get(0).get(14).x(),
                poseLandmarkerResult.landmarks().get(0).get(14).y());
        l_wrist = new LandmarkPoint(poseLandmarkerResult.landmarks().get(0).get(16).x(),
                poseLandmarkerResult.landmarks().get(0).get(16).y());

        //right arm
        r_shoulder = new LandmarkPoint(poseLandmarkerResult.landmarks().get(0).get(11).x(),
                poseLandmarkerResult.landmarks().get(0).get(11).y());
        r_elbow = new LandmarkPoint(poseLandmarkerResult.landmarks().get(0).get(13).x(),
                poseLandmarkerResult.landmarks().get(0).get(13).y());
        r_wrist = new LandmarkPoint(poseLandmarkerResult.landmarks().get(0).get(15).x(),
                poseLandmarkerResult.landmarks().get(0).get(15).y());

        //left leg
        l_hip = new LandmarkPoint(poseLandmarkerResult.landmarks().get(0).get(24).x(),
                poseLandmarkerResult.landmarks().get(0).get(24).y());
        l_knee = new LandmarkPoint(poseLandmarkerResult.landmarks().get(0).get(26).x(),
                poseLandmarkerResult.landmarks().get(0).get(26).y());
        l_ankle = new LandmarkPoint(poseLandmarkerResult.landmarks().get(0).get(28).x(),
                poseLandmarkerResult.landmarks().get(0).get(28).y());

        //right leg
        r_hip = new LandmarkPoint(poseLandmarkerResult.landmarks().get(0).get(23).x(),
                poseLandmarkerResult.landmarks().get(0).get(23).y());
        r_knee = new LandmarkPoint(poseLandmarkerResult.landmarks().get(0).get(25).x(),
                poseLandmarkerResult.landmarks().get(0).get(25).y());
        r_ankle = new LandmarkPoint(poseLandmarkerResult.landmarks().get(0).get(27).x(),
                poseLandmarkerResult.landmarks().get(0).get(27).y());


        r_mouth = new LandmarkPoint(poseLandmarkerResult.landmarks().get(0).get(9).x(),
                poseLandmarkerResult.landmarks().get(0).get(9).y());
        l_mouth = new LandmarkPoint(poseLandmarkerResult.landmarks().get(0).get(10).x(),
                poseLandmarkerResult.landmarks().get(0).get(10).y());
        nose = new LandmarkPoint(poseLandmarkerResult.landmarks().get(0).get(0).x(),
                poseLandmarkerResult.landmarks().get(0).get(0).y());


        left_arm_angle = calculate_angle(l_shoulder, l_elbow, l_wrist);
        right_arm_angle = calculate_angle(r_shoulder, r_elbow, r_wrist);
        left_leg_angle = calculate_angle(l_hip, l_knee, l_ankle);
        right_leg_angle = calculate_angle(r_hip, r_knee, r_ankle);

        LandmarkPoint shoulder_avg = new LandmarkPoint(
                (float) (r_shoulder.x + l_shoulder.x) / 2,
                (float) (r_shoulder.y + l_shoulder.y) / 2);
        LandmarkPoint mouth_avg = new LandmarkPoint(
                (float) (r_mouth.x + l_mouth.x) / 2,
                (float) (r_mouth.y + l_mouth.y) / 2);
        LandmarkPoint hip_avg = new LandmarkPoint(
                (float) (r_hip.x + l_hip.x) / 2,
                (float) (r_hip.y + l_hip.y) / 2);


        neck_angle = Math.abs(180 - calculate_angle(mouth_avg, shoulder_avg, hip_avg));

        LandmarkPoint sAvg = new LandmarkPoint(
                (float) (r_shoulder.x + l_shoulder.x) / 2,
                (float) (r_shoulder.y + l_shoulder.y) / 2);

        LandmarkPoint hAvg = new LandmarkPoint(
                (float) (r_hip.x + l_hip.x) / 2,
                (float) (r_hip.y + l_hip.y) / 2);

        LandmarkPoint kAvg = new LandmarkPoint(
                (float) (r_knee.x + l_knee.x) / 2,
                (float) (r_knee.y + l_knee.y) / 2);

        abdomen_angle = calculate_angle(sAvg, hAvg, kAvg);




    }

    double calculate_angle(LandmarkPoint start, LandmarkPoint mid, LandmarkPoint end) {
        double angle;
        double radians = Math.atan2(end.y - mid.y, end.x - mid.x) - Math.atan2(start.y - mid.y, start.x - mid.x);
        angle = Math.abs(radians * 180.0 / Math.PI);
        if (angle > 180.0)
            angle = 360 - angle;

        return angle;
    }

    CounterStatus pullUpCount(CounterStatus counterStatus) {

        double avg_shoulder_y = (l_shoulder.y + r_shoulder.y) / 2;
        long currentTime = System.currentTimeMillis();

        if (counterStatus.status) {
            if (nose.y > avg_shoulder_y) {
                if (currentTime - counterStatus.lastUpdateTime > TimeUnit.SECONDS.toMillis(2)) {
                    counterStatus.cnt += 1;
                    counterStatus.status = false;
                    counterStatus.lastUpdateTime = currentTime;
                }
            }
        } else {
            if (nose.y < avg_shoulder_y) {
                counterStatus.status = true;
            }
        }

        return counterStatus;
    }

    CounterStatus pushUpCounter(CounterStatus counterStatus) {

        double avg_arm_angle = (left_arm_angle + right_arm_angle) / 2;

        if (counterStatus.status) {
            if (avg_arm_angle < 70){
                counterStatus.cnt += 1;
                counterStatus.status = false;
            }
        } else {
            if (avg_arm_angle > 90)
                counterStatus.status = true;
        }
        return counterStatus;
    }

    CounterStatus squatCounter(CounterStatus counterStatus) {

        double avg_leg_angle = (left_leg_angle + right_leg_angle) / 2;

        if (counterStatus.status) {
            if (avg_leg_angle < 70){
                counterStatus.cnt += 1;
                counterStatus.status = false;
            }
        } else {
            if (avg_leg_angle > 160)
                counterStatus.status = true;
        }
        return counterStatus;
    }

    CounterStatus situpCounter(CounterStatus counterStatus) {


        if (counterStatus.status) {
            if (abdomen_angle < 55){
                counterStatus.cnt += 1;
                counterStatus.status = false;
            }
        } else {
            if (abdomen_angle > 105)
                counterStatus.status = true;
        }
        return counterStatus;
    }

}
