package com.covitrack.david.covitrack.api;

import com.covitrack.david.covitrack.api.models.requests.BaseRequestModel;
import com.covitrack.david.covitrack.api.models.requests.SendLocationRequestModel;
import com.covitrack.david.covitrack.api.models.requests.StatusRequestModel;
import com.covitrack.david.covitrack.api.models.responses.BaseResponseModel;
import com.covitrack.david.covitrack.api.models.responses.StatusResponseModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {
    public final String BASE_API_URL =
            "http://tribunpeer1.northeurope.cloudapp.azure.com/covitrace/";

    @GET("getstatus")
    Call<StatusResponseModel> getStatus(@Body BaseRequestModel requestModel);

    @POST("sendlocation")
    Call<SendLocationRequestModel> sendLocation(@Body SendLocationRequestModel requestModel);

    @POST("sendstatus")
    Call<StatusRequestModel> sendStatus(@Body StatusRequestModel requestModel);
}
