package com.nordstrom.hydrogen.consumer.utility;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nordstrom.hydrogen.consumer.domain.solr.SolrDeploymentChecker;
import com.nordstrom.hydrogen.consumer.domain.solr.SolrStatusParser;
import com.nordstrom.hydrogen.consumer.domain.solr.SolrUrlBuilder;
import com.nordstrom.hydrogen.consumer.model.SolrStatus;
import com.nordstrom.sharedlib.logging.ApplicationLogger;

@Component
public class RateLimiterHelper {
  private static final int PING_TIMEOUT = 2000;
  private static final int BAD_HTTP_RESPONSE_CODE_THRESHOLD = 400;

  private SolrStatus prevSolrStatus = SolrStatus.DEFAULT;
  private SolrStatus currSolrStatus = SolrStatus.DEFAULT;

  @Autowired
  private ApplicationLogger logger;

  @Autowired
  private RateLimiterConfigHelper helper;

  @Autowired
  private SolrUrlBuilder solrUrlBuilder;

  @Autowired
  SolrDeploymentChecker solrDeploymentChecker;

  @Autowired
  SolrStatusParser solrStatusParser;

  public Double getNewIngestionRate() {
    SolrStatus solrStatus = getSolrStatus();
    setPrevAndCurrSolrStatus(solrStatus);

    if (SolrStatus.ACTIVE.equals(solrStatus)) {
      return helper.getRateByMetrics();
    } else {
      return helper.getRateBySolrStatus(solrStatus);
    }
  }

  private SolrStatus getSolrStatus() {
    SolrStatus solrStatus = SolrStatus.DOWN;
    HttpURLConnection urlConn = null;
    boolean isSolrReachable = false;

    try {
      boolean isSolrDeploying = solrDeploymentChecker.isSolrDeploying();

      String solrStatusresponse = null;
      if (!isSolrDeploying) {
        //TODO: Connecting to Solr should be done via SolrService
        String solrUrl = solrUrlBuilder.buildClusterStatusUrl().toString();
        URL url = new URL(solrUrl);
        urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setConnectTimeout(PING_TIMEOUT);
        urlConn.connect();

        isSolrReachable = isSolrReachable(urlConn);

        if (isSolrReachable) {
          solrStatusresponse = getClusterStatusResponse(urlConn);
        }
      }

      solrStatus = solrStatusParser.getSolrStatus(isSolrDeploying, isSolrReachable, solrStatusresponse);

    } catch (Exception ex) {
      logger.error(ex, "An exception occurred when determining solr status");
    } finally {
      if (urlConn != null) {
        try {
          if (isSolrReachable) {
            urlConn.getInputStream().close();
          }
          urlConn.disconnect();
        } catch (IOException ioEx) {
          logger.error(ioEx, "Exception happens when closing the connection to Solr");
        }
      }
    }

    return solrStatus;
  }

  private boolean isSolrReachable(HttpURLConnection urlConn) throws IOException {
    return urlConn != null && urlConn.getResponseCode() < BAD_HTTP_RESPONSE_CODE_THRESHOLD;
  }

  private String getClusterStatusResponse(HttpURLConnection urlConn) throws IOException {
    InputStream inputStream = urlConn.getInputStream();
    String response = IOUtils.toString(inputStream);
    inputStream.close();
    return response;
  }

  private synchronized void setPrevAndCurrSolrStatus(SolrStatus solrStatus) {
    prevSolrStatus = currSolrStatus;
    currSolrStatus = solrStatus;
  }

  public synchronized String getPrevAndCurrSolrStatus() {
    StringBuilder sb = new StringBuilder();
    sb.append(prevSolrStatus.name());
    sb.append("/");
    sb.append(currSolrStatus.name());

    return sb.toString();
  }
}
