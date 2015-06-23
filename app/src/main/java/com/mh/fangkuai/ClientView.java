package com.mh.fangkuai;

/**
 * Created by MH on 2015-06-18.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import org.w3c.dom.Text;

public class ClientView extends View {
    private int viewHeight;  //画布高度
    private int viewWidth;//画布宽度
    private int blockHeight;//块高度
    private int blockWidth;//块宽度
    private int blockBanJing;//圆角半径
    private Paint linePaint;//线画笔
    private Paint blockPaint;//块画笔
    private int bgColor; //背景颜色
    private FangKuai fangKuai;//方块
    private int maxY;//当前最高Y点
    private int groundBlock[][]; //背景

    public long score;

    public ClientView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ClientView);
        bgColor = array.getColor(R.styleable.ClientView_BackGround, GlobeConfig.BGColor);

        linePaint = new Paint();
        blockPaint = new Paint();
        groundBlock = new int[GlobeConfig.BGRows + 4][GlobeConfig.BGColumns + 4];

        linePaint.setColor(GlobeConfig.BGLineColor);
        maxY = GlobeConfig.BGRows - 1;
    };

	public void Inital() {
		for (int i=0; i<GlobeConfig.BGRows; i++) {
			for (int j=0; j<GlobeConfig.BGColumns; j++) {
				groundBlock[i][j] = 0;
			}
			groundBlock[i][GlobeConfig.BGColumns] = 1;
		}
        for (int i=0; i<GlobeConfig.BGColumns; i++) {
            groundBlock[GlobeConfig.BGRows][i] = 1;
        }
		fangKuai = null;
	}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int h = MeasureSpec.getSize(heightMeasureSpec) - 8;//getHeight();
        int w  = MeasureSpec.getSize(widthMeasureSpec) - 8;//getWidth();
        blockHeight = (int) Math.floor(h / GlobeConfig.BGRows);
        blockWidth = (int) Math.floor(w / GlobeConfig.BGColumns);
        blockBanJing = Math.min(blockHeight / 10, blockWidth / 10);
        viewHeight = blockHeight * GlobeConfig.BGRows;
        viewWidth = blockWidth * GlobeConfig.BGColumns;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*canvas.drawColor(GlobeConfig.BGColor);
        Paint p = new Paint();
        p.setColor(bgColor);
        Rect rect = new Rect(0, 0, viewWidth, viewHeight);
        canvas.drawRect(rect, p);*/

        drawGrid(canvas);
        drawFangKuai(canvas);
        drawGround(canvas);
    };

    public void reDraw() {
        this.invalidate();
    }

    //画表格
    protected void drawGrid(Canvas canvas) {
        for(int i=0; i<=GlobeConfig.BGColumns; i++) {
            canvas.drawLine(i * blockWidth,0,i * blockWidth,viewHeight,linePaint);
        }

        for(int i=0; i<=GlobeConfig.BGRows; i++) {
            canvas.drawLine(0,i * blockHeight,viewWidth,i * blockHeight,linePaint);
        }
    };

    private void drawFangKuai(Canvas canvas) {
        if (noFangKuai()) return;

        int fk[][] = fangKuai.getFangKuai();
        for (int i=0; i<fk.length; i++) {
            if (fangKuai.Y + i < 0 || fangKuai.Y + i >= GlobeConfig.BGRows) continue;

            int fk2[] = fk[i];
            for (int j=0; j<fk2.length; j++) {
                if (fangKuai.X + j < 0 || fangKuai.X + j >= GlobeConfig.BGColumns || fk2[j] == 0) continue;
                drawBlock(canvas, fangKuai.X + j, fangKuai.Y + i, fangKuai.color);
            }
        }
    }

    //画背景方块
    private void drawGround(Canvas canvas) {
        for (int i=0; i<GlobeConfig.BGRows; i++) {
            for (int j=0; j<GlobeConfig.BGColumns; j++) {
                if (groundBlock[i][j] == 0) continue;
                drawBlock(canvas, j, i, groundBlock[i][j]);
            }
        }
    }

    //设置活动方块
    public void setFangKuai(FangKuai fk) {
        fangKuai = fk;
    }

    //画方块
    private void drawBlock(Canvas canvas, int x, int y, int color) {
        blockPaint.setColor(color);
        RectF rect = new RectF(x * blockWidth + 2, y * blockHeight + 2, (x + 1) * blockWidth - 1, (y + 1) * blockHeight - 1);
        canvas.drawRoundRect(rect, blockBanJing, blockBanJing, blockPaint);
    }

    //p判断当前是否有活动方块
    public boolean noFangKuai() {
        return fangKuai == null ? true : false;
    }

    //下判断
    public boolean canDowm() {
        if (noFangKuai()) return false;
        if (fangKuai.Y + 3 < maxY) return true;

        int fk[][] = fangKuai.getFangKuai();
        for (int i=0; i<fk.length; i++) {
            if (fangKuai.Y + i < 0 || fangKuai.Y + i >= GlobeConfig.BGRows) continue;
            int fk2[] = fk[i];
            for (int j=0; j<fk2.length; j++) {
                if (fangKuai.X + j < 0 || fangKuai.X + j >= GlobeConfig.BGColumns || fk2[j] == 0) continue;
                if (groundBlock[fangKuai.Y + i + 1][fangKuai.X + j] != 0) return false;
            }
        }
        return true;
    }

    //向下
    public boolean moveDown() {
        if (noFangKuai()) return false;

        if (canDowm()) {
            fangKuai.Y++;
        }
        else {
            if (fangKuai.Y < 0) return true;  //game over
            int r = 0;
            int fk[][] = fangKuai.getFangKuai();
            for (int i=0; i<fk.length; i++) {
                if (fangKuai.Y + i < 0 || fangKuai.Y + i >= GlobeConfig.BGRows) continue;
                int fk2[] = fk[i];
                for (int j=0; j<fk2.length; j++) {
                    if (fangKuai.X + j < 0 || fangKuai.X + j >= GlobeConfig.BGColumns || fk2[j] == 0) continue;
                    maxY = Math.min(maxY, fangKuai.Y + i - 1);
                    groundBlock[fangKuai.Y + i][fangKuai.X + j] = fangKuai.color;
                }
                if (removeRow(fangKuai.Y + i)) r++;
            }
            caloScore(r);
            fangKuai = null;
        }
        return false;
    }

    //计算分数
    private void caloScore(int row) {
        switch(row) {
            case 1: score = 100;
            case 2: score = 300;
            case 3: score = 600;
            case 4: score = 1000;
            default: score = 0;
        }
    }

    //消行
    private boolean removeRow(int row) {
        boolean f = true;
        for (int i=0; i< GlobeConfig.BGColumns; i++) {
            if (groundBlock[row][i] == 0) {
                f = false;
                break;
            }
        }
        if (f) {
            for (int i=row; i>=0; i--) {
                for (int j = 0; j < GlobeConfig.BGColumns; j++) {
                    if (i == 0) {
                        groundBlock[i][j] = 0;
                    } else {
                        groundBlock[i][j] = groundBlock[i - 1][j];
                    }
                }
            }
        }
        return f;
    }

    //向左
    private boolean canLeft() {
        if (noFangKuai()) return false;
        if (fangKuai.X < -2) return false;
        if (fangKuai.Y + 3 < maxY && fangKuai.X > 0) return true;

        int fk[][] = fangKuai.getFangKuai();
        for (int i=0; i<fk.length; i++) {
            int fk2[] = fk[i];
            for (int j=0; j<fk2.length; j++) {
                if (fk2[j] == 0 || fangKuai.X + j < 0) continue;
                if (fangKuai.X + j < 1) return false;
                if (groundBlock[fangKuai.Y + i][fangKuai.X + j - 1] != 0) return false;
            }
        }
        return true;
    }

    public boolean moveLeft() {
        if (noFangKuai()) return false;
        if (canLeft())
            fangKuai.X--;
        return true;
    }

    //向右
    private boolean canRight() {
        if (noFangKuai()) return false;
        if (fangKuai.X > GlobeConfig.BGColumns - 2) return false;
        if (fangKuai.Y + 3 < maxY && fangKuai.X < GlobeConfig.BGColumns - 4) return true;

        int fk[][] = fangKuai.getFangKuai();
        for (int i=0; i<fk.length; i++) {
            int fk2[] = fk[i];
            for (int j=0; j<fk2.length; j++) {
                if (fk2[j] == 0) continue;
                if (groundBlock[fangKuai.Y + i][fangKuai.X + j + 1] != 0) return false;
            }
        }
        return true;
    }

    public boolean moveRight() {
        if (noFangKuai()) return false;
        if (canRight())
            fangKuai.X++;
        return true;
    }

    //变形
    private boolean canChangeBlock() {
        if (noFangKuai()) return false;
        if (fangKuai.Y + 3 < maxY && fangKuai.X > 0 && fangKuai.X < GlobeConfig.BGColumns - 4) return true;

        int tmpX = fangKuai.X;
        int tmpS = fangKuai.getStatus();
        fangKuai.setChange();

        int fk[][] = fangKuai.getFangKuai();
        for (int i=0; i<fk.length; i++) {
            int fk2[] = fk[i];
            for (int j=0; j<fk2.length; j++) {
                if (fk2[j] == 0) continue;
                boolean f = false;
                while (fangKuai.X + j < 0) {
                    if (moveLeft()) {
                        f = true;
                        break;
                    }
                }
                while (fangKuai.X + j >= GlobeConfig.BGColumns) {
                    if (moveRight()) {
                        f = true;
                        break;
                    }
                }
                if (fangKuai.X + j < 0 || groundBlock[fangKuai.Y + i][fangKuai.X + j] != 0) {
                    f = true;
                    break;
                }
                if (f) {
                    fangKuai.X = tmpX;
                    fangKuai.setStatus(tmpS);
                    return false;
                }
            }
        }
        fangKuai.X = tmpX;
        fangKuai.setStatus(tmpS);
        return true;
    }

    public boolean changeBlock() {
        if (noFangKuai()) return false;
        if (canChangeBlock())
            fangKuai.setChange();
        return true;
    }

    //到底
    public void toDown() {
        while (canDowm()) {
            moveDown();
        }
    }
}
