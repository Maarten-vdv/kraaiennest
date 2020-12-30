package com.kraaiennest.kraaiennestapp.register;

import android.content.Intent;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import com.kraaiennest.kraaiennestapp.R;
import com.kraaiennest.kraaiennestapp.api.APIInterface;
import com.kraaiennest.kraaiennestapp.api.APIService;
import com.kraaiennest.kraaiennestapp.model.Child;
import com.kraaiennest.kraaiennestapp.model.PartOfDay;
import com.kraaiennest.kraaiennestapp.model.Registration;
import okhttp3.ResponseBody;
import org.parceler.Parcels;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;

public class RegisterViewModel extends ViewModel {

    private List<Child> children;
    private MutableLiveData<Child> child;
    private MutableLiveData<Integer> halfHours;

    private MutableLiveData<LocalDateTime> cutOff;

    private MutableLiveData<PartOfDay> partOfDay;

    private APIInterface api;
    private MutableLiveData<ApiCallState> registrationState;

    private final Map<Integer, String> strings;
    private final DateTimeFormatter dateTimeFormatter;
    private String scriptId;

    public RegisterViewModel(Map<Integer, String> strings, DateTimeFormatter dateTimeFormatter) {
        this.strings = strings;
        this.dateTimeFormatter = dateTimeFormatter;
        api = APIService.getClient().create(APIInterface.class);
    }

    public void loadExtra(Intent intent, String scriptId) {
        children = Parcels.unwrap(intent.getParcelableExtra("children"));
        this.scriptId = scriptId;
    }

    public LiveData<Child> getChild() {
        if (child == null) {
            child = new MutableLiveData<>();
            child.setValue(null);
        }
        return child;
    }

    public LiveData<String> getFullName() {
        return Transformations.map(getChild(), c -> c == null ? strings.get(R.string.scan_child) : c.getFullName());
    }

    public LiveData<PartOfDay> getPartOfDay() {
        if (partOfDay == null) {
            LocalDateTime now = LocalDateTime.now();
            partOfDay = new MutableLiveData<>(now.getHour() > 11 ? PartOfDay.A : PartOfDay.O);
        }
        return partOfDay;
    }

    public LiveData<LocalDateTime> getCutOff() {
        if (cutOff == null) {
            LocalDateTime cutoff = LocalDateTime.now();
            if (cutoff.getHour() >= 12) {
                // evening
                cutoff = cutoff.withHour(15).withMinute(45);
            } else {
                // morning
                cutoff = cutoff.withHour(8).withMinute(0);
            }
            cutOff = new MutableLiveData<>(cutoff);
        }

        return cutOff;
    }

    public LiveData<String> getCutOffValue() {
        return Transformations.map(getCutOff(), c -> c == null ? "" : c.format(dateTimeFormatter));
    }

    public LiveData<String> getHalfHours() {
        if (halfHours == null) {
            halfHours = new MutableLiveData<>(calculateHalfHours());
        }

        return Transformations.map(halfHours, integer -> integer == null ? "" : format("{0}{1}", integer.toString(), strings.get(R.string.half_hours)));
    }

    public MutableLiveData<ApiCallState> getRegistrationState() {
        if (registrationState == null) {
            registrationState = new MutableLiveData<>(ApiCallState.IDLE);
        }
        return registrationState;
    }

    private Integer calculateHalfHours() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, getCutOff().getValue()).abs();
        return Math.toIntExact(duration.toMinutes()) / 30;
    }

    public void createRegistration() {
        if (child.getValue() == null) {
            return;
        }

        registrationState.setValue(ApiCallState.BUSY);
        Registration registration = new Registration();
        registration.setChildId(child.getValue().getId());
        registration.setHalfHours(halfHours.getValue());
        registration.setRealHalfHours(calculateHalfHours());
        registration.setRegistrationTime(LocalDateTime.now());
        registration.setPartOfDay(getPartOfDay().getValue());
        Call<ResponseBody> register = api.doPostRegister(scriptId, registration);

        register.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                child.setValue(null);
                halfHours.setValue(null);
                registrationState.setValue(ApiCallState.SUCCESS);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                registrationState.setValue(ApiCallState.ERROR);
            }
        });
    }

    public void loadChild(String userId) {
        if (children == null || child == null) {
            return;
        }
        child.setValue(children.stream().filter(c -> c.getQrId().equals(userId)).findFirst().orElse(null));
    }

    public void addHalfHours(int i) {
        if (this.halfHours.getValue() != null) {
            this.halfHours.setValue(this.halfHours.getValue() + i);
        }
    }

    public void registrationDone() {
        registrationState.setValue(ApiCallState.IDLE);
    }
}

