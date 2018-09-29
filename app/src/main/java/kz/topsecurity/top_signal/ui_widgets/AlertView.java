package kz.topsecurity.top_signal.ui_widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

import kz.topsecurity.top_signal.R;

public class AlertView extends SurfaceView implements SurfaceHolder.Callback  {

    private Paint[] paint_colors;
    private int width;
    private int height;
    Rectangle_Coordinates[] rectangle_coordinates ;
    Rectangle_Coordinates[] new_rectangle_coordinates ;
    int rectangle_count = 2;
    int range_by_height;
    int range_by_width;
    boolean isDrawStarted = false;
    int origin_x = 0;
    int origin_y = 0;
    boolean ifDrawStarted = false;

    DrawThread drawThread;

    public AlertView(Context context) {
        super(context);
        initPaint();
    }

    public AlertView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public AlertView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    void initPaint(){
        rectangle_coordinates = new Rectangle_Coordinates[rectangle_count];
        new_rectangle_coordinates = new Rectangle_Coordinates[rectangle_count];
        paint_colors = new Paint[2];
        paint_colors[0] = new Paint();
        paint_colors[0].setColor(Color.BLACK);
        paint_colors[1] = new Paint();
        paint_colors[1].setColor(Color.RED);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.width = w;
        this.height = h;
        this.range_by_height = h/(rectangle_count);
        this.range_by_width = w/(rectangle_count);
        if(!ifDrawStarted) {
            getHolder().addCallback(this);
            ifDrawStarted = true;
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    boolean checkArrayIsEmpty( Rectangle_Coordinates[] k){
        if(k!=null && k.length>0 && k[0]!=null && k[1]!=null)
            return false;
        else
           return true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new DrawThread(getHolder(), getResources());
        drawThread.setRunning(true);
        drawThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        // завершаем работу потока
        drawThread.setRunning(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // если не получилось, то будем пытаться еще и еще
            }
        }
    }

    class DrawThread extends Thread{
        private boolean runFlag = false;
        private SurfaceHolder surfaceHolder;
        private long prevTime;
        int biggest_rectangle_width = 0;
        int biggest_rectangle_height = 0;
        private RectF rect1;
        private RectF rect2;
        private double deltaX;
        private double deltaY;
        private int backgroundColor;

        public DrawThread(SurfaceHolder surfaceHolder, Resources resources){
            this.surfaceHolder = surfaceHolder;
            origin_x = width/2;
            origin_y = height/2;
            // сохраняем текущее время
            prevTime = System.currentTimeMillis();
            new_rectangle_coordinates[0] =  new Rectangle_Coordinates(
                    origin_x - biggest_rectangle_width/2,
                    origin_y - biggest_rectangle_height/2 ,
                    origin_y + biggest_rectangle_height/2 ,
                    origin_x + biggest_rectangle_height/2 ,
                    biggest_rectangle_width/10);
            new_rectangle_coordinates[1] =  new Rectangle_Coordinates(
                    origin_x - biggest_rectangle_width/2,
                    origin_y - biggest_rectangle_height/2 ,
                    origin_y + biggest_rectangle_height/2 ,
                    origin_x + biggest_rectangle_height/2 ,
                    biggest_rectangle_width/10);

            rect1 = new RectF(
                    new_rectangle_coordinates[0].getLeft_corner(),
                    new_rectangle_coordinates[0].getTop_corner(),
                    new_rectangle_coordinates[0].getRight_corner(),
                    new_rectangle_coordinates[0].getBottom_corner());

            rect2 = new RectF(
                    new_rectangle_coordinates[1].getLeft_corner(),
                    new_rectangle_coordinates[1].getTop_corner(),
                    new_rectangle_coordinates[1].getRight_corner(),
                    new_rectangle_coordinates[1].getBottom_corner());
            backgroundColor = Color.WHITE ;

            deltaX = 10;
            int ratio = (int) (deltaX * height)/width;
            deltaY = ratio;
        }

        public void setRunning(boolean run) {
            runFlag = run;
        }

        @Override
        public void run() {
            Canvas canvas;
            while (runFlag) {
                // получаем текущее время и вычисляем разницу с предыдущим
                // сохраненным моментом времени
                long now = System.currentTimeMillis();
                long elapsedTime = now - prevTime;
                if (elapsedTime > 30){
                    // если прошло больше 30 миллисекунд - сохраним текущее время
                    // и повернем картинку на 2 градуса.
                    // точка вращения - центр картинки
                    prevTime = now;
                    //matrix.preRotate(2.0f, picture.getWidth() / 2, picture.getHeight() / 2);
                    if(biggest_rectangle_height>height && biggest_rectangle_width>width){
                        new_rectangle_coordinates[0].setLeft_corner( new_rectangle_coordinates[1].getLeft_corner());
                        new_rectangle_coordinates[0].setTop_corner( new_rectangle_coordinates[1].getTop_corner());
                        new_rectangle_coordinates[0].setBottom_corner(new_rectangle_coordinates[1].getBottom_corner());
                        new_rectangle_coordinates[0].setRight_corner(new_rectangle_coordinates[1].getRight_corner());
                        new_rectangle_coordinates[0].setCorner_radius(new_rectangle_coordinates[1].getCorner_radius());

                        new_rectangle_coordinates[1].setLeft_corner( 0);
                        new_rectangle_coordinates[1].setTop_corner( 0);
                        new_rectangle_coordinates[1].setBottom_corner(0);
                        new_rectangle_coordinates[1].setRight_corner(0);
                        new_rectangle_coordinates[1].setCorner_radius(0);
                        biggest_rectangle_width = new_rectangle_coordinates[0].getRight_corner() - new_rectangle_coordinates[0].getLeft_corner();
                        biggest_rectangle_height = new_rectangle_coordinates[0].getBottom_corner() - new_rectangle_coordinates[0].getTop_corner();
                        int tmp_color = paint_colors[0].getColor();
                        backgroundColor = tmp_color;
                        paint_colors[0].setColor(paint_colors[1].getColor());
                        paint_colors[1].setColor(tmp_color);

                    }
                    else {
                        biggest_rectangle_width += deltaX;
                        biggest_rectangle_height += deltaY;
                        new_rectangle_coordinates[0].setLeft_corner(origin_x - biggest_rectangle_width / 2);
                        new_rectangle_coordinates[0].setTop_corner(origin_y - biggest_rectangle_height / 2);
                        new_rectangle_coordinates[0].setRight_corner(origin_x + biggest_rectangle_width / 2);
                        new_rectangle_coordinates[0].setBottom_corner(origin_y + biggest_rectangle_height / 2);
                        new_rectangle_coordinates[0].setCorner_radius(biggest_rectangle_width / 10);
                    }
                    if(biggest_rectangle_height >range_by_width && biggest_rectangle_height>range_by_height){
                        new_rectangle_coordinates[1].setLeft_corner(origin_x - (biggest_rectangle_width-range_by_width)/2);
                        new_rectangle_coordinates[1].setTop_corner( origin_y - (biggest_rectangle_height-range_by_width)/2 );
                        new_rectangle_coordinates[1].setRight_corner(origin_x + (biggest_rectangle_width-range_by_width)/2);
                        new_rectangle_coordinates[1].setBottom_corner(origin_y + (biggest_rectangle_height-range_by_width)/2);
                        new_rectangle_coordinates[1].setCorner_radius((new_rectangle_coordinates[1].getRight_corner() - new_rectangle_coordinates[1].getLeft_corner())/10);
                    }

                    rect1.set(new_rectangle_coordinates[0].getLeft_corner(),
                            new_rectangle_coordinates[0].getTop_corner(),
                            new_rectangle_coordinates[0].getRight_corner(),
                            new_rectangle_coordinates[0].getBottom_corner());

                    rect2.set(new_rectangle_coordinates[1].getLeft_corner(),
                            new_rectangle_coordinates[1].getTop_corner(),
                            new_rectangle_coordinates[1].getRight_corner(),
                            new_rectangle_coordinates[1].getBottom_corner());
                }
                canvas = null;
                try {
                    // получаем объект Canvas и выполняем отрисовку
                    canvas = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder) {
                        canvas.drawColor(backgroundColor);
                       // canvas.drawBitmap(picture, matrix, null);
                        canvas.drawRoundRect(
                                rect1,
                                new_rectangle_coordinates[0].getCorner_radius(),
                                new_rectangle_coordinates[0].getCorner_radius(),
                                paint_colors[0]);
                        canvas.drawRoundRect(
                                rect2,
                                new_rectangle_coordinates[1].getCorner_radius(),
                                new_rectangle_coordinates[1].getCorner_radius(),
                                paint_colors[1]);
                    }
                }
                finally {
                    if (canvas != null) {
                        // отрисовка выполнена. выводим результат на экран
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}
