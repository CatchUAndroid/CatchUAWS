package com.uren.catchu.MainPackage.MainFragments.Share.Models;

import android.graphics.Color;

import com.uren.catchu.R;

import java.util.ArrayList;
import java.util.List;

public class ColorPaletteModel {

    static int[] colorCodes1 = new int[]{
            R.color.Orange,
            R.color.LightSalmon,
            R.color.DarkOrange,
            R.color.Coral,
            R.color.HotPink,
            R.color.Tomato,
            R.color.OrangeRed,
            R.color.DeepPink,
            R.color.Fuchsia,
    };

    static int[] colorCodes2 = new int[]{
            R.color.DarkRed,
            R.color.BlueViolet,
            R.color.LightSkyBlue,
            R.color.SkyBlue,
            R.color.Gray,
            R.color.Olive,
            R.color.Purple,
            R.color.Maroon,
            R.color.Aquamarine,
    };

    static int[] colorCodes3 = new int[]{
            R.color.DarkCyan,
            R.color.Teal,
            R.color.Green,
            R.color.DarkGreen,
            R.color.Blue,
            R.color.MediumBlue,
            R.color.DarkBlue,
            R.color.Black,
            R.color.White,
    };

    public static int[] getColorList1(){
        return colorCodes1;
    }
    public static int[] getColorList2(){
        return colorCodes2;
    }
    public static int[] getColorList3(){
        return colorCodes3;
    }


}
