package com.example.funnybirds;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
    private int viewWidth;
    private int viewHeight;
    private int points = 0;
    private Sprite playerBird;
    private Sprite enemyBird;
    private final int timerInterval = 30;
    private Timer timer;

    public int getPoints() {
        return points;
    }

    private OnGameOverListener gameOverListener;

    public void setGameOverListener(OnGameOverListener gameOverListener) {
        this.gameOverListener = gameOverListener;
    }

    public GameView(Context context) {
        super(context);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.player);
        int w = b.getWidth() / 5;
        int h = b.getHeight() / 3;
        Rect firstFrame = new Rect(0, 0, w, h);
        playerBird = new Sprite(10, 0, 0, 100, firstFrame, b);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (i == 2 && j == 3) {
                    continue;
                }
                playerBird.addFrame(new Rect(j * w, i * h, j * w + w, i * w + w));
            }
        }
        timer = new Timer();
        timer.start();

        b = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);
        w = b.getWidth() / 5;
        h = b.getHeight() / 3;

        firstFrame = new Rect(4 * w, 0, 5 * w, h);
        enemyBird = new Sprite(2000, 250, -300, 0, firstFrame, b);
        for (int i = 0; i < 3; i++) {
            for (int j = 4; j >= 0; j--) {
                if (i == 0 && j == 4) {
                    continue;
                }
                if (i == 2 && j == 0) {
                    continue;
                }
                enemyBird.addFrame(new Rect(j * w, i * h, j * w + w, i * w + w));
            }
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(250, 127, 199, 255);
        playerBird.draw(canvas);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setTextSize(55.0f);
        p.setColor(Color.WHITE);
        canvas.drawText(points + "", viewWidth - 100, 70, p);
        enemyBird.draw(canvas);
    }

    protected void update() {
        playerBird.update(timerInterval);
        if (playerBird.getY() + playerBird.getFrameHeight() > viewHeight) {
            playerBird.setY(viewHeight - playerBird.getFrameHeight());
            playerBird.setVelocityY(-playerBird.getVelocityY());
            points--;
        } else if (playerBird.getY() < 0) {
            playerBird.setY(0);
            playerBird.setVelocityY(-playerBird.getVelocityY());
        }

        enemyBird.update(timerInterval);
        if (enemyBird.getX() < -enemyBird.getFrameWidth()) {
            teleportEnemy();
            points += 10;
        }
        if (enemyBird.intersect(playerBird)) {
            teleportEnemy();
            points -= 40;
        }
        if (isGameOver(points)) {
            stop();
        }
        invalidate();
    }

    private void teleportEnemy() {
        enemyBird.setX(viewWidth + Math.random() * 500);
        enemyBird.setY(Math.random() * (viewHeight - enemyBird.getFrameHeight()));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN) {
            if (event.getY() < playerBird.getBoundingBoxRect().top) {
                playerBird.setVelocityY(-100);
                points--;
            } else if (event.getY() > (playerBird.getBoundingBoxRect().bottom)) {
                playerBird.setVelocityY(100);
                points--;
            }
        }
        return true;
    }

    protected boolean isGameOver(int points) {
        if (points < -50) {
            return true;
        } else {
            return false;
        }
    }

    protected void stop() {
        playerBird.setVelocityY(0);
        enemyBird.setVelocityY(0);
        enemyBird.setVelocityX(0);

        if (timer != null) {
            timer.cancel();
        }

        if (gameOverListener != null) {
            gameOverListener.onGameOver(points);
        }
    }

    class Timer extends CountDownTimer {
        public Timer() {
            super(Integer.MAX_VALUE, timerInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            update();
        }

        @Override
        public void onFinish() {
        }
    }
}