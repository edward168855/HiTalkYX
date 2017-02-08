package com.lpf.hitalkyx.Utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class ScreenMgr {
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
	}
	
	/**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

	// 获取屏幕宽高
	public static int[] getScreenSize(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		// 确保宽度>=高度
		if (screenWidth <= screenHeight) {
			int temp = screenWidth;
			screenWidth = screenHeight;
			screenHeight = temp;
		}
		int[] size = { screenWidth, screenHeight };
		return size;
	}
	
	// 获取屏幕宽
	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		// 确保宽度>=高度
		if (screenWidth <= screenHeight) {
			int temp = screenWidth;
			screenWidth = screenHeight;
			screenHeight = temp;
		}
		return screenWidth;
	}
	
	// 获取屏幕高
	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		// 确保宽度>=高度
		if (screenWidth <= screenHeight) {
			int temp = screenWidth;
			screenWidth = screenHeight;
			screenHeight = temp;
		}
		return screenHeight;
	}

	/**
	 * 获取屏幕密度
	 * @param context
	 * @return
	 */
	public static float getScreenDensity(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.density;
	}

	/**
     * 获得字体的缩放密度
     *
     * @param context
     * @return
     */
    public static float getScaledDensity(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.scaledDensity;
    }

}
