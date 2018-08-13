package com.nordstrom.hydrogen.consumer.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.OptionalDouble;

import org.apache.http.client.utils.URIBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nordstrom.hydrogen.consumer.config.DatadogConfig;
import com.nordstrom.hydrogen.consumer.model.Metric;
import com.nordstrom.hydrogen.consumer.model.MetricSpec;
import com.nordstrom.sharedlib.http.HttpClient;
import com.nordstrom.sharedlib.model.SharedLibHttpRequest;
import com.nordstrom.sharedlib.model.SharedLibHttpResponse;
import com.nordstrom.sharedlib.utility.JsonHelper;
import com.nordstrom.sharedlib.utility.ValidationHelper;

@Service
public class DatadogService implements MetricService {


    @Autowired
    private HttpClient httpClient;

    @Autowired
    private DatadogConfig datadogConfig;

    private SharedLibHttpRequest metricRequest;

    private SharedLibHttpResponse metricResponse;


    public Metric execute(MetricSpec metricSpec) throws Exception {
        ValidationHelper.validateArgumentForNull(metricSpec, "metricSpec");
        JSONObject jsonObj = createJSON((getRequest(buildURL(metricSpec.getMinsAgo(), metricSpec.getQuery()))));
        return new Metric(metricSpec.getQuery(), calculateValue(parseJSONforPointList(jsonObj)), "");
    }


    private URI buildURL(double minsAgo, String query) throws Exception {
        String apiKey = this.datadogConfig.getApiKey();
        String applicationKey = this.datadogConfig.getApplicationKey();
        String from = Long.toString((System.currentTimeMillis() / 1000) - ((long) minsAgo * 60 * 1000));
        String to = Long.toString(System.currentTimeMillis() / 1000);

        URIBuilder builder = new URIBuilder("https://api.datadoghq.com/api/v1/query?");
        return builder.addParameter("api_key", apiKey)
                .addParameter("application_key", applicationKey)
                .addParameter("from", from)
                .addParameter("to", to)
                .addParameter("query", query)
                .build();

    }

    private SharedLibHttpResponse getRequest(URI uri) throws Exception {
        this.metricRequest = new SharedLibHttpRequest()
                .withRequestUrl(uri);
        this.metricResponse = this.httpClient.get(metricRequest);
        return metricResponse;
    }

    private JSONObject createJSON(SharedLibHttpResponse metricResponse) throws Exception {
        String fullJSONstr = metricResponse.getResult();
        JsonHelper jsonHelper = new JsonHelper();
        JSONObject jsonObj = jsonHelper.convertToJsonObject(fullJSONstr);
        return jsonObj;
    }

    private JSONArray parseJSONforPointList(JSONObject jsonObj) throws Exception {
        JSONArray seriesList = (JSONArray) jsonObj.get("series");
        JSONObject seriesObj = (JSONObject) seriesList.get(0);
        JSONArray pointList = (JSONArray) seriesObj.get("pointlist");
        return pointList;
    }


    private Double calculateValue (JSONArray pointList) {
        ArrayList<Double> values = new ArrayList<Double>();
        for (Object arr : (JSONArray) pointList) {
            JSONArray jsonArr = (JSONArray) arr;
            Double current = (Double) jsonArr.get(1);
            values.add(current);
        }
        OptionalDouble avg = values
                .stream()
                .mapToDouble(a -> a)
                .average();

        Double average = 0.0;
        if (avg.isPresent()) {
            average = avg.getAsDouble();
        }

        return average;
    }

}
