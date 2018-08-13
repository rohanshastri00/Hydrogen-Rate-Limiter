package com.nordstrom.hydrogen.consumer.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.nordstrom.hydrogen.consumer.TestUtility;
import com.nordstrom.hydrogen.consumer.config.DatadogConfig;
import com.nordstrom.hydrogen.consumer.model.Metric;
import com.nordstrom.hydrogen.consumer.model.MetricSpec;
import com.nordstrom.sharedlib.http.HttpClient;
import com.nordstrom.sharedlib.model.SharedLibHttpRequest;
import com.nordstrom.sharedlib.model.SharedLibHttpResponse;


public class DatadogServiceTest extends TestUtility {

    @InjectMocks
    private DatadogService datadogService;

    @Mock
    private HttpClient httpClient;

    @Mock
    private DatadogConfig datadogConfig;

    @Mock
    private SharedLibHttpRequest metricRequest;

    @Mock
    private SharedLibHttpResponse metricResponse;

    @Mock
    private JSONObject jsonObj;

    @Mock
    private JSONArray pointListArr;

    @Mock
    private MetricSpec mockSpec;

    private String datadogResponse;

    @Before
    public void initialize() throws Exception{
        this.datadogConfig = mock(DatadogConfig.class);
        this.httpClient = mock(HttpClient.class);
        this.metricRequest = mock(SharedLibHttpRequest.class);
        this.metricResponse = mock(SharedLibHttpResponse.class);
        this.mockSpec = mock(MetricSpec.class);
        this.datadogResponse = this.readDocumentJsonFile("datadogTestResponse.json").toString();

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void executeInvoked_WhenMetricSpecIsNull_ThrowsIllegalArgumentException() throws Exception {
        this.setExpectedArgumentException("The [metricSpec] argument cannot be null", IllegalArgumentException.class);

        this.datadogService.execute(null);
        Assert.fail("Expected Illegal Arugment Exception");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void executeInvoked_WhenValidJSONIsProvided_VerifyCalculatedAverage() throws Exception {

        when(httpClient.get(any(SharedLibHttpRequest.class))).thenReturn(new SharedLibHttpResponse(200, datadogResponse));

        Metric actualValue = this.datadogService.execute(mockSpec);

        Assert.assertEquals(69.77361541681098, actualValue.getValue(), 0);
    }


}
