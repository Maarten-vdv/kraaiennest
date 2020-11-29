package com.kraaiennest.kraaiennestapp.checkin;

import android.content.Intent;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import com.kraaiennest.kraaiennestapp.R;
import com.kraaiennest.kraaiennestapp.api.APIInterface;
import com.kraaiennest.kraaiennestapp.api.APIService;
import com.kraaiennest.kraaiennestapp.model.Child;
import com.kraaiennest.kraaiennestapp.register.ApiCallState;
import okhttp3.ResponseBody;
import org.parceler.Parcels;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.Map;

public class CheckInViewModel extends ViewModel {

    private List<Child> children;
    private String scriptId;
    private MutableLiveData<Child> child;
    private MutableLiveData<ApiCallState> apiCallState;
    private APIInterface api;
    private final Map<Integer, String> strings;

    public CheckInViewModel(Map<Integer, String> strings) {
        this.strings = strings;
        api = APIService.getClient().create(APIInterface.class);
    }

    public void loadExtra(Intent intent,String scriptId) {
        children = Parcels.unwrap(intent.getParcelableExtra("children"));
        this.scriptId = scriptId;
    }

    public LiveData<Child> getChild() {
        if (child == null) {
            child = new MutableLiveData<>();
        }
        return child;
    }

    public LiveData<String> getFullName() {
        return Transformations.map(getChild(), c -> c == null ? strings.get(R.string.scan_child) : c.getFullName());
    }

    public void loadChild(String userId) {
        if (children == null || child == null) {
            return;
        }
        child.setValue(children.stream().filter(c -> c.getQrId().equals(userId)).findFirst().orElse(null));
    }

    public MutableLiveData<ApiCallState> getCheckInState() {
        if (apiCallState == null) {
            apiCallState = new MutableLiveData<>(ApiCallState.IDLE);
        }
        return apiCallState;
    }

    public void checkInDone() {
        apiCallState.setValue(ApiCallState.IDLE);
    }

    public void createCheckIn() {
        apiCallState.setValue(ApiCallState.BUSY);
        if (child.getValue() != null) {
            Call<ResponseBody> checkIn = api.doPostCheckIn(scriptId, child.getValue());

            checkIn.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    child.setValue(null);
                    apiCallState.setValue(ApiCallState.SUCCESS);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    apiCallState.setValue(ApiCallState.ERROR);
                }
            });
        }
    }
}
