package com.kraaiennest.opvang.repository;

import com.kraaiennest.opvang.api.APIService;
import com.kraaiennest.opvang.model.Child;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChildRepository {
    private final APIService apiService;
    private List<Child> childCache;

    public ChildRepository(APIService apiService) {

        this.apiService = apiService;
    }

    public CompletableFuture<List<Child>> getChildren() {
        if (childCache != null) {
            return CompletableFuture.supplyAsync(() -> childCache);
        }
        return apiService.doGetChildren().whenComplete((children, action) -> childCache = children);
    }

    public CompletableFuture<Child> findByQrId(String id) {
        if (childCache != null) {
            return CompletableFuture.supplyAsync(() -> childCache.stream().filter(c -> c.getQrId().equals(id)).findFirst().orElse(null));
        }
       return getChildren().thenApply(children -> childCache.stream().filter(c -> c.getQrId().equals(id)).findFirst().orElse(null));
    }

    public CompletableFuture<Child> findByPIN(String pin) {
        if (childCache != null) {
            return CompletableFuture.supplyAsync(() -> childCache.stream().filter(c -> c.getPIN().equals(pin)).findFirst().orElse(null));
        }
        return getChildren().thenApply(children -> childCache.stream().filter(c -> c.getPIN().equals(pin)).findFirst().orElse(null));
    }

    public CompletableFuture<Child> findByNFC(String nfcId) {
        if (childCache != null) {
            return CompletableFuture.supplyAsync(() -> childCache.stream().filter(c -> c.getNfcId().equals(nfcId)).findFirst().orElse(null));
        }
        return getChildren().thenApply(children -> childCache.stream().filter(c -> c.getNfcId().equals(nfcId)).findFirst().orElse(null));
    }
}
