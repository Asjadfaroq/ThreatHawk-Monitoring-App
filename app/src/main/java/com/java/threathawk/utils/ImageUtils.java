package com.java.threathawk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class ImageUtils {
    public static Bitmap blurBitmap(Context context, Bitmap inputBitmap, float blurRadius) {
        // Create a copy of the input bitmap to avoid modifying the original
        Bitmap bitmap = inputBitmap.copy(inputBitmap.getConfig(), true);

        // Initialize RenderScript
        RenderScript rs = RenderScript.create(context);

        // Allocate memory for Renderscript to work with
        Allocation input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
        Allocation output = Allocation.createTyped(rs, input.getType());

        // Load the blur script
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setInput(input);

        // Set the blur radius
        script.setRadius(blurRadius);

        // Start the ScriptIntrinsicBlur
        script.forEach(output);

        // Copy the output to the bitmap
        output.copyTo(bitmap);

        // Destroy the RenderScript
        rs.destroy();

        return bitmap;
    }
}
