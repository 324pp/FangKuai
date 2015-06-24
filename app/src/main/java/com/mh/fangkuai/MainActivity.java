package com.mh.fangkuai;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.view.menu.MenuView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    private RefreshHandler redrawHandler = new RefreshHandler();
    private ClientView CV;
    private ClientView BLOCK;
    private boolean bLoop = true;
    private int downSpeed;
    private long score;
    private int  initSpeed;

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            MainActivity.this.update();
            //TetrisView.this.invalidate();
        }

        public void sleep(int delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };

    private void update() {
        if (CV.noFangKuai()) {
            FangKuai F = BLOCK.getFangKuai();
            F.X = GlobeConfig.BGColumns / 2 - 2;
            F.Y = F.getButtonRow() * -1;
            CV.setFangKuai(F);
            FangKuai fk = new FangKuai();
            BLOCK.setFangKuai(fk);
            BLOCK.reDraw();
        }
        boolean isover = CV.moveDown();
        if (CV.score > 0) setScore(CV.score);
        CV.reDraw();
        if (isover) return;
        if (bLoop) run();
    }

    private void run() {
        int s = GlobeConfig.BlockSpeed / 10;
        redrawHandler.sleep(GlobeConfig.BlockSpeed - s * (downSpeed - 1));
    }

    public void newGame() {
        CV.Inital();
        downSpeed = initSpeed;
        score = 0;
        setSpeed(0);
        setScore(0);
        FangKuai fk = new FangKuai();
        BLOCK.setFangKuai(fk);
        BLOCK.reDraw();
        run();
    }

    private void setSpeed(int s) {
        TextView TV = (TextView)findViewById(R.id.sudu);
        if (s == 0) {
            s = 1;
        }else {
            s = s + downSpeed;
        }
        if (s > 9) s = 9;
        TV.setText(s + "");
    }

    private void setScore(long s) {
        TextView TV = (TextView)findViewById(R.id.fenshu);
        if (s > 0) score = s + score;
        TV.setText(score + "");
        int t = (int)score / 10000;
        if (t > downSpeed) setSpeed(1);
    }

    private void moveLeft() {
        if (CV.isAllDown()) {
            bLoop = false;
            CV.moveLeft();
            CV.reDraw();
            bLoop = true;
            run();
        }
    }

    private void moveRight() {
        if (CV.isAllDown()) {
            bLoop = false;
            CV.moveRight();
            CV.reDraw();
            bLoop = true;
            run();
        }
    }

    private void moveDown() {
        bLoop = false;
        CV.toDown();
        if (CV.score > 0) setScore(CV.score);
        CV.reDraw();
        bLoop = true;
        run();
    }

    private void changeBlock() {
        if (CV.isAllDown()) {
            bLoop = false;
            CV.changeBlock();
            CV.reDraw();
            bLoop = true;
            run();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //redrawHandler.sleep(2000);
        initSpeed = 1;
        CV = (ClientView) findViewById(R.id.CV);
        BLOCK = (ClientView) findViewById(R.id.nextBlock);
        BLOCK.setBG(4, 4);

        Button button = (Button) findViewById(R.id.start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGame();
            }
        });

        ImageView left = (ImageView) findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveLeft();
            }
        });

        ImageView right = (ImageView) findViewById(R.id.right);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveRight();
            }
        });

        ImageView down = (ImageView) findViewById(R.id.down);
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveDown();
            }
        });

        ImageView change = (ImageView) findViewById(R.id.change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBlock();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
