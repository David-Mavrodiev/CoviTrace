package com.covitrack.david.covitrack.api;

import com.covitrack.david.covitrack.App;
import com.covitrack.david.covitrack.api.models.requests.BaseRequestModel;
import com.covitrack.david.covitrack.api.models.requests.SendLocationRequestModel;
import com.covitrack.david.covitrack.api.models.requests.StatusRequestModel;
import com.covitrack.david.covitrack.api.models.responses.BaseResponseModel;
import com.covitrack.david.covitrack.api.models.responses.StatusResponseModel;
import com.covitrack.david.covitrack.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CovitraceApiService {
    public void getStatus(String uniqueId, Callback<StatusResponseModel> callback) {
        Retrofit builder = new Retrofit.Builder()
                .baseUrl(Api.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BaseRequestModel requestModel = new BaseRequestModel();
        requestModel.setUniqueId(uniqueId);

        Api api = builder.create(Api.class);
        Call<StatusResponseModel> getStatusCall = api.getStatus(requestModel);
        getStatusCall.enqueue(callback);
    }

    public void sendLocation(String uniqueId, Double latitude, Double longitude,
                             Callback<SendLocationRequestModel> callback) {
        Retrofit builder = new Retrofit.Builder()
                .baseUrl(Api.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SendLocationRequestModel requestModel = new SendLocationRequestModel();
        requestModel.setUniqueId(uniqueId);
        requestModel.setLatitude(latitude);
        requestModel.setLongitude(longitude);

        Api api = builder.create(Api.class);
        Call<SendLocationRequestModel> sendLocationCall = api.sendLocation(requestModel);
        sendLocationCall.enqueue(callback);
    }

    public void sendStatus(String uniqueId, int flag, Callback<StatusRequestModel> callback) {
        Retrofit builder = new Retrofit.Builder()
                .baseUrl(Api.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        StatusRequestModel requestModel = new StatusRequestModel();
        requestModel.setUniqueId(uniqueId);

        if (Constants.INFECTED == flag) {
            requestModel.setInfected(true);
        } else if (Constants.CONTACTED == flag) {
            requestModel.setContacted(true);
        }

        Api api = builder.create(Api.class);
        Call<StatusRequestModel> sendStatusCall = api.sendStatus(requestModel);
        sendStatusCall.enqueue(callback);
    }
}
