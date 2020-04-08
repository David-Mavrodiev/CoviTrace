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
            "https://covitrace-api.herokuapp.com/api/";

    @GET("status")
    Call<SendLocationRequestModel> getStatus(@Body BaseRequestModel requestModel);

    @POST("location")
    Call<SendLocationRequestModel> sendLocation(@Body SendLocationRequestModel requestModel);

    @POST("status")
    Call<StatusRequestModel> sendStatus(@Body StatusRequestModel requestModel);
}
