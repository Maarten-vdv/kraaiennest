package com.kraaiennest.kraaiennestapp.qr;

import com.google.zxing.Result;

public interface QrCodeListener {

    void qrCodeFound(Result result);
}
