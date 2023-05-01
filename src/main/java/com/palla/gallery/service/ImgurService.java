package com.palla.gallery.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palla.gallery.dto.ImageResponseDto;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class ImgurService {

    @Value("${imgur.client_id}")
    private String clientId;

    @Value("${imgur.client_secret}")
    private String clientSecret;

    @Value("${imgur.refresh_token}")
    private String refreshToken;

    @Value("${imgur.grant_type}")
    private String grantType;

    @Value("${imgur.host_url}")
    private String hostUrl;

    public String generateNewAccessToken() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        RequestBody formBody = new FormBody.Builder()
                .add("refresh_token", refreshToken).add("client_id", clientId)
                .add("client_secret", clientSecret).add("grant_type", grantType).build();

        Request request = new Request.Builder().url(hostUrl + "/oauth2/token").post(formBody).build();
        Call call = client.newCall(request);
        ResponseBody response = call.execute().body();
        return response.toString();
    }

    public void deleteImage(String deleteHash) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, "{}");
        Request request = new Request.Builder()
                .url(hostUrl + "/3/image/" + deleteHash).method("DELETE", body)
                .addHeader("Authorization", "Client-ID " + clientId).build();
        client.newCall(request).execute();
    }

    public ImageResponseDto uploadImage(MultipartFile file) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        RequestBody requestBody = buildRequestBody(file);
        Request request = buildRequest(requestBody);
        Call call = client.newCall(request);
        return convertResponse(call.execute().body().string());
    }

    private ImageResponseDto convertResponse(String response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response, ImageResponseDto.class);
    }

    private RequestBody buildRequestBody(MultipartFile file) throws IOException {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", Base64.getEncoder().encodeToString(file.getBytes()))
                .addFormDataPart("type", "base64").build();
    }

    private Request buildRequest(RequestBody requestBody) {
        return new Request.Builder().url(hostUrl + "/3/image").post(requestBody)
                .addHeader("Authorization", "Client-ID " + clientId).build();
    }

}
