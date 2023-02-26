package com.jay.gulistore.search.config;


import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 1、导入依赖
 * 2、编写配置,给容器中注入一个RestHighlevelClient
 * 3、参照API https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.4/java-rest-high-getting-started-initialization.html
 */


@Configuration
public class GulistoreElasticSearchConfig {


    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization", "Bearer " + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory
//                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }





    @Bean
    public RestHighLevelClient esRestClient() {

        RestClientBuilder builder = null;
        //final String hostname, final int port, final String scheme
        builder = RestClient.builder(new HttpHost("192.168.56.10", 9200, "http"));
        RestHighLevelClient client = new RestHighLevelClient(builder);

//        RestHighLevelClient client = new RestHighLevelClient(
//                //如果es有多个，指定es的地址和端口号以及协议名
//                RestClient.builder(
//                        new HttpHost("192.168.218.128", 9200, "http")));
        return client;
    }

}
