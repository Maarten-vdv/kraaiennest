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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class RegisterViewModel extends ViewModel {

    private List<Child> children;
    private MutableLiveData<Child> child;
    private MutableLiveData<Integer> halfHours;

    private MutableLiveData<LocalDateTime> cutOff;

    private MutableLiveData<PartOfDay> partOfDay;

    private APIInterface api;
    private MutableLiveData<RegistrationState> registrationState;

    private final Map<Integer, String> strings;

    public RegisterViewModel(Map<Integer, String> strings) {
        this.strings = strings;
        api = APIService.getClient().create(APIInterface.class);
    }

    public void loadExtra(Intent intent) {
        children = Parcels.unwrap(intent.getParcelableExtra("children"));
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
    public LiveData<PartOfDay> getPartOfDay() {
        if(partOfDay == null) {
            LocalDateTime now = LocalDateTime.now();
            partOfDay = new MutableLiveData<>(now.getHour() > 11? PartOfDay.A : PartOfDay.O);
        }
        return partOfDay;
    }
        public LiveData<LocalDateTime> getCutOff() {
        if(cutOff == null) {
            LocalDateTime cutoff = LocalDateTime.now();
            if (cutoff.getHour() >= 12) {
                // evening
                cutoff = cutoff.withHour(15).withMinute(30);
            } else {
                // morning
                cutoff = cutoff.withHour(8).withMinute(0);
            }
            cutOff = new MutableLiveData<>(cutoff);
        }

            return cutOff;
        }

    public LiveData<String> getHalfHours() {
        if (halfHours == null) {
            halfHours = new MutableLiveData<>(calculateHalfHours());
        }
        return Transformations.map(halfHours, integer -> integer == null ? "" : integer.toString());
    }

    public MutableLiveData<RegistrationState> getRegistrationState() {
        if (registrationState == null) {
            registrationState = new MutableLiveData<>(RegistrationState.IDLE);
        }
        return registrationState;
    }

    private Integer calculateHalfHours() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, getCutOff().getValue()).abs();
        return Math.toIntExact(duration.toMinutes()) / 30;
    }

    public void createRegistration() {
        if(child.getValue() == null) {
            return;
        }

        registrationState.setValue(RegistrationState.BUSY);
        Registration registration = new Registration();
        registration.setChildId(child.getValue().getId());
        registration.setHalfHours(registration.halfHours);
        registration.setRealHalfHours(calculateHalfHours());
        registration.setRegistrationTime(LocalDateTime.now());
        registration.setPartOfDay(getPartOfDay().getValue());
        Call<ResponseBody> register = api.doPostRegister(registration);

        register.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                child.setValue(null);
                halfHours.setValue(null);
                registrationState.setValue(RegistrationState.SUCCESS);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                registrationState.setValue(RegistrationState.ERROR);
            }
        });
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    public void loadChild(String userId) {
        child.setValue(children.stream().filter(c -> c.getQrId().equals(userId)).findFirst().orElse(null));

    }

    public void addHalfHours(int i) {
        if (this.halfHours.getValue() != null) {
            this.halfHours.setValue(this.halfHours.getValue() + i);
        }
    }

    public void registrationDone() {
        registrationState.setValue(RegistrationState.IDLE);
    }
}

