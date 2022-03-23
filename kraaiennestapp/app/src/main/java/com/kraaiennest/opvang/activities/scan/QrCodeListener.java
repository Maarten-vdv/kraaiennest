package com.kraaiennest.opvang.activities.scan;

import com.google.zxing.Result;

public interface QrCodeListener {

    void qrCodeFound(Result result);
}
