package pl.put.photo360.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import pl.put.photo360.config.Configuration;
import java.io.IOException;

@Component
public class RestTemplateFilter implements ClientHttpRequestInterceptor {
    private final Configuration configuration;

    @Autowired
    public RestTemplateFilter(Configuration configuration) {
        this.configuration = configuration;
    }
    @Override
    public ClientHttpResponse intercept(HttpRequest request,
                                        byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        System.out.println(configuration.getPUBLIC_API_KEY());
        request.getHeaders().set("publicApiKey", configuration.getPUBLIC_API_KEY());
        return execution.execute(request, body);
    }
}
