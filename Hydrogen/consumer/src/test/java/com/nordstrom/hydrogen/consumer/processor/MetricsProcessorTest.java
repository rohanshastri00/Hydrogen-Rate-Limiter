package com.nordstrom.hydrogen.consumer.processor;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.nordstrom.hydrogen.consumer.TestUtility;
import com.nordstrom.hydrogen.consumer.domain.solr.MetricServiceFactory;
import com.nordstrom.hydrogen.consumer.model.Metric;
import com.nordstrom.hydrogen.consumer.model.MetricSpec;
import com.nordstrom.hydrogen.consumer.model.RiskLevel;
import com.nordstrom.hydrogen.consumer.service.DatadogService;
import com.nordstrom.hydrogen.consumer.service.MetricService;
import com.nordstrom.sharedlib.logging.ApplicationLogger;
import com.nordstrom.sharedlib.utility.YamlHelper;

@RunWith(MockitoJUnitRunner.class)
@EnableConfigurationProperties(MetricsProcessor.class)
public class MetricsProcessorTest extends TestUtility {

    @InjectMocks
    private MetricsProcessor metricsProcessor;

    @Mock
    private MetricService datadogService;

    @Mock
    private MetricServiceFactory metricServiceFactory;

    @Mock
    private ApplicationLogger logger;

    private YamlHelper yamlHelper;

    private String yamlResponse;

    @Spy
    private List<MetricSpec> metricSpecs = new ArrayList<>();


    @Before
    public void initialize() {
        this.datadogService = mock(DatadogService.class);
        this.metricServiceFactory = mock(MetricServiceFactory.class);
        this.yamlHelper = new YamlHelper();
        try {
            this.yamlResponse = readDocumentFile("testMetricConfig.yml");
        } catch (IOException ex) {
            logger.error("could not read yaml config file");
        }
        this.metricSpecs = new ArrayList<>(yamlHelper.deserializeYamlToList(yamlResponse, MetricSpec[].class));

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void executeInvoked_WhenNullMetricIsProvided_VerifyGood() throws Exception {
        when(metricServiceFactory.getService("datadog")).thenReturn(datadogService);

        Metric metric = new Metric(null, null, null);

        when(datadogService.execute(any(MetricSpec.class))).thenReturn(metric);

        RiskLevel actualValue = this.metricsProcessor.getRiskLevel();

        Assert.assertEquals(RiskLevel.GOOD, actualValue);

    }

    @Test
    public void executeInvoked_WhenNullMetricSpecIsProvided_VerifyGood() throws Exception {
        when(metricServiceFactory.getService("datadog")).thenReturn(datadogService);

        Metric metric = new Metric("solr.search_handler.p95{*}", 69.31232413, "");

        when(datadogService.execute(any(MetricSpec.class))).thenReturn(metric);

        RiskLevel actualValue = this.metricsProcessor.getRiskLevel();

        Assert.assertEquals(RiskLevel.GOOD, actualValue);

    }

    @Test
    public void executeInvoked_WhenValidMetricSpecIsProvided_VerifyGood() throws Exception {
        when(metricServiceFactory.getService("datadog")).thenReturn(datadogService);

        Metric metric = new Metric("solr.search_handler.p95{*}", 69.31232413, "");

        when(datadogService.execute(any(MetricSpec.class))).thenReturn(metric);

        RiskLevel actualValue = this.metricsProcessor.getRiskLevel();

        Assert.assertEquals(RiskLevel.GOOD, actualValue);

    }

    @Test
    public void executeInvoked_WhenValidMetricSpecIsProvided_VerifyMedium() throws Exception {
        when(metricServiceFactory.getService("datadog")).thenReturn(datadogService);

        Metric metric = new Metric("solr.search_handler.p95{*}", 170.12241324, "");

        when(datadogService.execute(any(MetricSpec.class))).thenReturn(metric);

        RiskLevel actualValue = this.metricsProcessor.getRiskLevel();

        Assert.assertEquals(RiskLevel.MEDIUM, actualValue);

    }


    @Test
    public void executeInvoked_WhenValidMetricSpecIsProvided_VerifyBad() throws Exception {
        when(metricServiceFactory.getService("datadog")).thenReturn(datadogService);

        Metric metric = new Metric("solr.search_handler.p95{*}", 400.32353454, "");

        when(datadogService.execute(any(MetricSpec.class))).thenReturn(metric);

        RiskLevel actualValue = this.metricsProcessor.getRiskLevel();

        Assert.assertEquals(RiskLevel.BAD, actualValue);

    }



}
