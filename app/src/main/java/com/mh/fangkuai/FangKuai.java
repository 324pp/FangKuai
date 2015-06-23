package com.mh.fangkuai;

/**
 * Created by MH on 2015-06-18.
 */

import android.graphics.Color;

import java.util.Random;

public class FangKuai {
    //方块 4*4
    private int fangkuai[][][];
    //状态
    private int status;
    //颜色
    public int color;

    //位置
    public int X;
    public int Y;

    public FangKuai() {
        fangkuai = GlobeConfig.getFangKuai();
        status = new Random().nextInt(4);
        X = GlobeConfig.BGColumns / 2 - 2;
        Y = -4;
        color = Color.rgb(new Random().nextInt(200), new Random().nextInt(200), new Random().nextInt(200));
    };

    //设置方块
    public void setFangKuai(int fk[][][])
    {
        fangkuai = fk;
        status = new Random().nextInt(4);
    };

    //取方块点4×4
    public int[][] getFangKuai()
    {
        return fangkuai[status];
    }

    //变形
    public void setChange() {
        status++;
        if (status > 3) status = 0;
    }

    //状态
    public int getStatus() {
        return status;
    }

    public void setStatus(int s) {
        status = s;
    }
}
