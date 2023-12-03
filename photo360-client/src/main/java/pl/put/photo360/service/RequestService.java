package pl.put.photo360.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.put.photo360.web.RegisterRequest;

@Service
public class RequestService {
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    @Autowired
    public RequestService(OkHttpClient okHttpClient, ObjectMapper objectMapper) {
        this.client = okHttpClient;
        this.objectMapper = objectMapper;
    }

    public Response registerUser(RegisterRequest registerRequest) throws Exception {
        String json = objectMapper.writeValueAsString(registerRequest);
        RequestBody formBody = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("http://127.0.0.1:8095/photo360/authorization/register")
                .addHeader("publicApiKey", "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJpt3Bs6fwMc2S7h5cpIP6nkG9DsISp0MfKTpwtt31/a1ZF2+Pv8I0f64CIcBj4GPWP4PWWe9nI4WSUKkf5CdxT6sUh4toHvBemfQiSw3sCaHfgL0WBrdqhqIxYUwsedb9ZuCXRp6acmbvqttNI2r5V8rsuT0nTDYCnVTl5OgnQIDAQAB")
                .post(formBody)
                .build();

        return client.newCall(request).execute();
    }
}
