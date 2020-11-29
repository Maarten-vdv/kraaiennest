package com.kraaiennest.kraaiennestapp.scan;

import com.google.zxing.Result;

public interface QrCodeListener {

    void qrCodeFound(Result result);
}
