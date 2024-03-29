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
            "http://api-loadbal-1dvebfns148ag-db9989432bfe33f9.elb.us-east-1.amazonaws.com:8080/api/";

    @POST("get-status")
    Call<StatusRequestModel> getStatus(@Body StatusRequestModel requestModel);

    @POST("location")
    Call<SendLocationRequestModel> sendLocation(@Body SendLocationRequestModel requestModel);

    @POST("status")
    Call<StatusRequestModel> sendStatus(@Body StatusRequestModel requestModel);
}
