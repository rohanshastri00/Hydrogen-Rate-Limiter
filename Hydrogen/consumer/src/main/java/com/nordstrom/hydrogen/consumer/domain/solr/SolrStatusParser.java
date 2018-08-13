package com.nordstrom.hydrogen.consumer.domain.solr;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nordstrom.hydrogen.consumer.model.SolrStatus;
import com.nordstrom.sharedlib.logging.ApplicationLogger;
import com.nordstrom.sharedlib.utility.JsonHelper;

@Component
public class SolrStatusParser {
  @Autowired
  private ClusterStatusResponseParser responseParser;

  @Autowired
  private JsonHelper jsonHelper;

  @Autowired
  private ApplicationLogger logger;

  public SolrStatus getSolrStatus(boolean isSolrDeploying, boolean isSolrReachable, String clusterStatusResponse) throws Exception {
    SolrStatus solrStatus = SolrStatus.DOWN;

    if (isSolrDeploying) {
      return SolrStatus.DEPLOYING;
    }

    if (isSolrReachable) {
      solrStatus = SolrStatus.ACTIVE;

      JSONObject responseJson = jsonHelper.convertToJsonObject(clusterStatusResponse);
      List<String> unhealthyNodes = responseParser.getUnhealthyNodes(responseJson);

      if (unhealthyNodes != null && unhealthyNodes.size() > 0) {
        solrStatus = SolrStatus.HAS_UNHEALTHY_NODES;
        logger.error("Solr has the following unhealthy nodes: %s", String.join(", ", unhealthyNodes));
      }
    }

    return solrStatus;
  }
}
