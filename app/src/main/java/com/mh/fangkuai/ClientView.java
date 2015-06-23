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
    private int bgRows;
    private int bgColumns;
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

        bgRows = GlobeConfig.BGRows;
        bgColumns = GlobeConfig.BGColumns;

        groundBlock = new int[bgRows + 4][bgColumns + 4];
        Inital();

        linePaint.setColor(GlobeConfig.BGLineColor);
        maxY = bgRows - 1;
    };

	public void Inital() {
		for (int i=0; i<bgRows; i++) {
			for (int j=0; j<bgColumns; j++) {
				groundBlock[i][j] = 0;
			}
			groundBlock[i][bgColumns] = 1;
		}
        for (int i=0; i<bgColumns; i++) {
            groundBlock[bgRows][i] = 1;
        }
		fangKuai = null;
	}

    public void setBG(int row, int col) {
        bgRows = row;
        bgColumns = col;
        Inital();
        reDraw();
    }

    public FangKuai getFangKuai() {
        return fangKuai;
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int h = MeasureSpec.getSize(heightMeasureSpec) - 8;//getHeight();
        int w  = MeasureSpec.getSize(widthMeasureSpec) - 8;//getWidth();
        blockHeight = (int) Math.floor(h / bgRows);
        blockWidth = (int) Math.floor(w / bgColumns);
        blockBanJing = Math.min(blockHeight / 10, blockWidth / 10);
        viewHeight = blockHeight * bgRows;
        viewWidth = blockWidth * bgColumns;
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
        for(int i=0; i<=bgColumns; i++) {
            canvas.drawLine(i * blockWidth,0,i * blockWidth,viewHeight,linePaint);
        }

        for(int i=0; i<=bgRows; i++) {
            canvas.drawLine(0,i * blockHeight,viewWidth,i * blockHeight,linePaint);
        }
    };

    private void drawFangKuai(Canvas canvas) {
        if (noFangKuai()) return;

        int fk[][] = fangKuai.getFangKuai();
        for (int i=0; i<fk.length; i++) {
            if (fangKuai.Y + i < 0 || fangKuai.Y + i >= bgRows) continue;

            int fk2[] = fk[i];
            for (int j=0; j<fk2.length; j++) {
                if (fangKuai.X + j < 0 || fangKuai.X + j >= bgColumns || fk2[j] == 0) continue;
                drawBlock(canvas, fangKuai.X + j, fangKuai.Y + i, fangKuai.color);
            }
        }
    }

    //画背景方块
    private void drawGround(Canvas canvas) {
        for (int i=0; i<bgRows; i++) {
            for (int j=0; j<bgColumns; j++) {
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
            if (fangKuai.Y + i < 0 || fangKuai.Y + i >= bgRows) continue;
            int fk2[] = fk[i];
            for (int j=0; j<fk2.length; j++) {
                if (fangKuai.X + j < 0 || fangKuai.X + j >= bgColumns || fk2[j] == 0) continue;
                if (groundBlock[fangKuai.Y + i + 1][fangKuai.X + j] != 0) return false;
            }
        }
        return true;
    }

    //向下
    public boolean moveDown() {
        if (noFangKuai()) return false;

        int r = 0;
        if (canDowm()) {
            fangKuai.Y++;
        }
        else {
            if (fangKuai.Y < 0) return true;  //game over
            int fk[][] = fangKuai.getFangKuai();
            for (int i=0; i<fk.length; i++) {
                if (fangKuai.Y + i < 0 || fangKuai.Y + i >= bgRows) continue;
                int fk2[] = fk[i];
                for (int j=0; j<fk2.length; j++) {
                    if (fangKuai.X + j < 0 || fangKuai.X + j >= bgColumns || fk2[j] == 0) continue;
                    maxY = Math.min(maxY, fangKuai.Y + i - 1);
                    groundBlock[fangKuai.Y + i][fangKuai.X + j] = fangKuai.color;
                }
                if (removeRow(fangKuai.Y + i)) r++;
            }
            fangKuai = null;
        }
        caloScore(r);
        return false;
    }

    //计算分数
    private void caloScore(int row) {
        switch(row) {
            case 1: score = 100;break;
            case 2: score = 300;break;
            case 3: score = 600;break;
            case 4: score = 1000;break;
            default: score = 0;
        }
    }

    //消行
    private boolean removeRow(int row) {
        boolean f = true;
        for (int i=0; i< bgColumns; i++) {
            if (groundBlock[row][i] == 0) {
                f = false;
                break;
            }
        }
        if (f) {
            for (int i=row; i>=0; i--) {
                for (int j = 0; j < bgColumns; j++) {
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
        if (fangKuai.X < -2 || fangKuai.Y <= fangKuai.getTopRow() * -1) return false;
        if (fangKuai.Y + 3 < maxY && fangKuai.X > 0) return true;

        int fk[][] = fangKuai.getFangKuai();
        for (int i=0; i<fk.length; i++) {
            int fk2[] = fk[i];
            for (int j=0; j<fk2.length; j++) {
                if (fk2[j] == 0 /*|| fangKuai.X + j < 0*/) continue;
                if (fangKuai.X + j < 1) return false;
                if (groundBlock[fangKuai.Y + i][fangKuai.X + j - 1] != 0) return false;
            }
        }
        return true;
    }

    public boolean moveLeft() {
        if (noFangKuai()) return false;
        if (canLeft() == false)  return false;
        fangKuai.X--;
        return true;
    }

    //向右
    private boolean canRight() {
        if (noFangKuai()) return false;
        if (fangKuai.X > bgColumns - 2 || fangKuai.Y <= fangKuai.getTopRow() * -1) return false;
        if (fangKuai.Y + 3 < maxY && fangKuai.X < bgColumns - 4) return true;

        int fk[][] = fangKuai.getFangKuai();
        for (int i=0; i<fk.length; i++) {
            int fk2[] = fk[i];
            for (int j=0; j<fk2.length; j++) {
                if (fk2[j] == 0) continue;
                if (fangKuai.X + j >= bgColumns - 1) return false;
                if (groundBlock[fangKuai.Y + i][fangKuai.X + j + 1] != 0) return false;
            }
        }
        return true;
    }

    public boolean moveRight() {
        if (noFangKuai()) return false;
        if (canRight() == false) return false;
        fangKuai.X++;
        return true;
    }

    //变形
    public boolean changeBlock() {
        if (noFangKuai()) return false;

        int tmpX = fangKuai.X;
        int tmpS = fangKuai.getStatus();
        fangKuai.setChange();

        if (fangKuai.Y + 3 < maxY && fangKuai.X > 0 && fangKuai.X < bgColumns - 4) return true;

        int fk[][] = fangKuai.getFangKuai();
        for (int i=0; i<fk.length; i++) {
            int fk2[] = fk[i];
            for (int j=0; j<fk2.length; j++) {
                if (fk2[j] == 0) continue;
                boolean f = false;
                while (fangKuai.X + j < 0) {
                    if (moveRight() == false) {
                        f = true;
                        break;
                    }
                }
                while (fangKuai.X + j >= bgColumns) {
                    if (moveLeft() == false) {
                        f = true;
                        break;
                    }
                }
                if (groundBlock[fangKuai.Y + i][fangKuai.X + j] != 0) {
                    f = true;
                }
                if (f) {
                    fangKuai.X = tmpX;
                    fangKuai.setStatus(tmpS);
                    return false;
                }
            }
        }
        return true;
    }

    //到底
    public void toDown() {
        while (canDowm()) {
            moveDown();
        }
    }
}
