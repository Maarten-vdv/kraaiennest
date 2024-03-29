package com.kraaiennest.opvang.activities.scan;

import static android.graphics.ImageFormat.YUV_420_888;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class QrCodeAnalyzer implements ImageAnalysis.Analyzer {

   private final List<Integer> formats = Collections.singletonList(YUV_420_888);
   private final List<QrCodeListener> listeners = new ArrayList<>();
    private final MultiFormatReader reader;

    public QrCodeAnalyzer() {
        reader = new MultiFormatReader();
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, Collections.singletonList(BarcodeFormat.QR_CODE));
        hints.put(DecodeHintType.TRY_HARDER, true);
        reader.setHints(hints);
    }

    public void register(QrCodeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {

        if (!formats.contains(image.getFormat())) {
            Log.e("QRCodeAnalyzer", "Expected YUV, now = " + image.getFormat());
            return;
        }

        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes, 0, bytes.length);

        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
                bytes,
                image.getWidth(), image.getHeight(),
                0, 0,
                image.getWidth(), image.getHeight(),
                false
        );

        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            // Whenever reader fails to detect a QR code in image
            // it throws NotFoundException
           Result result = reader.decode(binaryBitmap);
            onQrCodesDetected(result);
        } catch (NotFoundException e) {
            // do nothing
        } finally {
            image.close();
        }
    }

    private void onQrCodesDetected(Result result) {
        listeners.forEach(l -> l.qrCodeFound(result));
    }
}
