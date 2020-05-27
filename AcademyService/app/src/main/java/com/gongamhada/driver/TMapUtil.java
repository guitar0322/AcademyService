package com.gongamhada.driver;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;
import java.util.Random;

public class TMapUtil {
    TMapView tMapView;
    Context context;
    TMapPolyLine tMapPolyLine;
    TMapPolyLine multiPathPolyLine;
    public TMapUtil(Context _context){
        context = _context;
        tMapView = new TMapView(context);
        tMapView.setSKTMapApiKey("c12f44ac-89a3-4580-993b-44df9a168afd");
    }
    public TMapPoint findTitlePOI(String POITitle){
        TMapPOIItem tMapPOIItem = null;
        class FindPOIThread extends Thread{
            public TMapPOIItem tMapPOIItem;
            String POITitle;
            public FindPOIThread(String _title){
                POITitle = _title;
            }
            public void run(){
                try {
                    tMapPOIItem = new TMapData().findTitlePOI(POITitle, 1).get(0);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        FindPOIThread findPOIThread = new FindPOIThread(POITitle);
        try{
            findPOIThread.start();
            findPOIThread.join();
            tMapPOIItem = findPOIThread.tMapPOIItem;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return tMapPOIItem.getPOIPoint();
    }

    public void addMultiPointPath(ArrayList<TMapPoint> wayPoints){
        multiPathPolyLine = new TMapPolyLine();
        for (int i = 0; i < wayPoints.size() - 1; i++){
            addPath(wayPoints.get(i), wayPoints.get(i+1), "path" + i+1);
            for(int j = 0; j < tMapPolyLine.getLinePoint().size(); j++){
                TMapPoint tMapPoint = tMapPolyLine.getLinePoint().get(j);
                multiPathPolyLine.addLinePoint(tMapPoint);
            }
        }
    }

    public class MultiPointPathFindThread extends Thread{
        TMapPoint startPoint;
        TMapPoint endPoint;
        ArrayList<TMapPoint> wayPoints;

        MultiPointPathFindThread(TMapPoint _startPoint, TMapPoint _endPoint, ArrayList<TMapPoint> _wayPoints){
            startPoint = _startPoint;
            endPoint = _endPoint;
            wayPoints = _wayPoints;
        }

        public void run(){
            try{
                tMapPolyLine = new TMapData().findMultiPointPathData(startPoint, endPoint, wayPoints, 2);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void addPath(TMapPoint startpoint, TMapPoint endpoint, String pathID){
        Random random = new Random();
        int lineColor = Color.argb(200, random.nextInt(255), random.nextInt(255), random.nextInt(255));
        PathFindThread pathFindThread = new PathFindThread(startpoint, endpoint);
        pathFindThread.start();
        try {
            pathFindThread.join();
            if(tMapPolyLine.getLinePoint().isEmpty())
                Log.d("singlePathLength", "empty");
            else
                Log.d("singlePathLength", Integer.toString(tMapPolyLine.getLinePoint().size()));
        }catch(InterruptedException e){
            Log.e("addPathError", "interruptedException in TMapUtil.addPath()");
        }

        tMapPolyLine.setLineColor(lineColor);
        tMapPolyLine.setLineWidth(2);
        tMapView.addTMapPolyLine(pathID, tMapPolyLine);
    }
    public class PathFindThread extends Thread{
        TMapPoint startpoint;
        TMapPoint endpoint;

        PathFindThread(TMapPoint _startpoint, TMapPoint _endpoint){
            startpoint = _startpoint;
            endpoint = _endpoint;
        }
        public void run(){
            try {
                tMapPolyLine = new TMapData().findPathData(startpoint, endpoint);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void CreateNumberMark(int howmany, ArrayList<TMapPoint> points) throws Exception{
        Bitmap bitmap;
        Log.d("howmanyMark", "=" + howmany);
        if(howmany != points.size()){
            throw new Exception("num of marker is not equals to num of points");
        }
        Log.d("howmanyMark", "=" + howmany);
        for(int i = 0; i < howmany; i++){
            String imageName = "num" + (i+1);
            String markerName = "marker" + imageName;
            int imageID = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
            bitmap = BitmapFactory.decodeResource(context.getResources(), imageID);
            bitmap = ResizeBitmap(bitmap);

            tMapView.addMarkerItem(markerName, createMarker(bitmap, 0.5f, 0, points.get(i))); // create marker
        }
    }
    public void addCustomIconMark(String markerName, Bitmap icon, float pivotX, float pivotY, TMapPoint point){
//        Bitmap resizeBitmap = ResizeBitmap(icon);
        tMapView.addMarkerItem(markerName, createMarker(icon, pivotX, pivotY, point));
    }
    public TMapMarkerItem createMarker(Bitmap icon, float pivotX, float pivotY, TMapPoint point){
        TMapMarkerItem markerItem = new TMapMarkerItem();
        markerItem.setIcon(icon);
        markerItem.setPosition(pivotX, pivotY); // 마커의 중심점을 중앙, 하단으로 설정
        markerItem.setTMapPoint(point); // 마커의 좌표 지정
        markerItem.setName("namsan"); // 마커의 타이틀 지정
        return markerItem;
    }

    public Bitmap ResizeBitmap(Bitmap origin){
        Bitmap result;
        int resizeLength = 60;
        result = Bitmap.createScaledBitmap(origin, resizeLength, resizeLength, false);
        if(result != origin){
            origin.recycle();
        }
        return result;
    }
}
