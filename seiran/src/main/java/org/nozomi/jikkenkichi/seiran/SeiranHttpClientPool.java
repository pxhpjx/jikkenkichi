package org.nozomi.jikkenkichi.seiran;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.nozomi.jikkenkichi.machikouba.pojo.BizException;
import org.nozomi.jikkenkichi.machikouba.pojo.LocalConfig;
import org.nozomi.jikkenkichi.machikouba.util.DebugTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Configuration
public class SeiranHttpClientPool {
    @Autowired
    LocalConfig localConfig;
    @Autowired
    CloseableHttpClient httpClient;

    //only a demo,so create a pool at will
    @Bean
    CloseableHttpClient createCloseableHttpClient() {
        PoolingHttpClientConnectionManager connectionManager
                = new PoolingHttpClientConnectionManager(60000, TimeUnit.MILLISECONDS);
        connectionManager.setMaxTotal(1000);
        connectionManager.setDefaultMaxPerRoute(50);

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .disableAutomaticRetries()
                .build();
    }

    /**
     * use default config to act http request
     * @param targetUrl
     * @param requestContent
     * @return
     */
    public String postRequest(String targetUrl, String requestContent) {
        HttpPost httpPost = new HttpPost(targetUrl);
        //httpPost.setConfig(null);
        if (requestContent != null) {
            StringEntity entity = new StringEntity(requestContent, localConfig.getCharset());
            entity.setContentEncoding(localConfig.getCharset());
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
        }

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity(), localConfig.getCharset());
            }
            DebugTool.print("failed with StatusCode " + response.getStatusLine().getStatusCode());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new BizException(targetUrl + " invoke failed");
    }

}
