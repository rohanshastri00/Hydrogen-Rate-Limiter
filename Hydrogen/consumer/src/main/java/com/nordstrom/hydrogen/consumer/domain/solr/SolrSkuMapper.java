package com.nordstrom.hydrogen.consumer.domain.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.mutable.MutableInt;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.nordstrom.hydrogen.consumer.model.EventRecord;
import com.nordstrom.hydrogen.consumer.utility.ResourceFileReader;
import com.nordstrom.sharedlib.logging.ApplicationLogger;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SolrSkuMapper {

  private static final String SOLR_SCHEMA_FIELDS_FILE = "SolrProductSchemaFields.txt";
  private static final String SOLR_SCHEMA_DYNAMIC_FIELDS_FILE = "SolrProductSchemaDynamicFields.txt";

  private static final String SKU_DOCUMENTS_KEY = "SkuDocuments";

  private static final int DYNAMIC_FIELDS_COUNT_THRESHOLD = 20;

  private final List<String> solrProductSchemaFields;
  private final List<String> solrProductSchemaDynamicFields;

  @Autowired
  private ApplicationLogger logger;

  public SolrSkuMapper(ResourceFileReader resourceFileReader) throws IOException {
    solrProductSchemaFields = resourceFileReader.readFileToListOfString(SOLR_SCHEMA_FIELDS_FILE);
    solrProductSchemaDynamicFields = resourceFileReader.readFileToListOfString(SOLR_SCHEMA_DYNAMIC_FIELDS_FILE);
  }

  @SuppressWarnings("unchecked")
  public List<JSONObject> map(EventRecord eventRecord, JSONObject styleJson) {
    List<String> skuIdsToMap = eventRecord.getSkuIds();
    if (!styleJson.containsKey(SKU_DOCUMENTS_KEY) || !(styleJson.get(SKU_DOCUMENTS_KEY) instanceof JSONArray)) {
      throw new RuntimeException(String.format("Error: Style Object should contain the key %s", SKU_DOCUMENTS_KEY));
    }
    
    List<JSONObject> skusResultJson = new ArrayList<JSONObject>();
    JSONArray skuJsonArray = (JSONArray) styleJson.get(SKU_DOCUMENTS_KEY);
    for (Object skuDocumentObject : skuJsonArray) {
      JSONObject skuResultJson = new JSONObject();
      JSONObject skuJson = (JSONObject) skuDocumentObject;
      String skuId = String.valueOf(skuJson.get("SkuId"));
      MutableInt dynamicFieldCount = new MutableInt(0);
      if (skuIdsToMap == null || skuIdsToMap.contains(skuId)) {
        skuJson.keySet().stream()
        .filter(key -> isValidSolrField(key, dynamicFieldCount))
        .forEach(key -> skuResultJson.put(String.valueOf(key).toLowerCase(), skuJson.get(key)));
        skusResultJson.add(skuResultJson);
      }
    }

    return skusResultJson;
  }

  private boolean isValidSolrField(Object key, MutableInt dynamicFieldCount) {
    return solrProductSchemaFields.contains(String.valueOf(key).toLowerCase()) ||
            isAllowedDynamicSolrField(String.valueOf(key).toLowerCase(), dynamicFieldCount);
  }

  private boolean isAllowedDynamicSolrField(String key, MutableInt dynamicFieldCount) {

    for (String dynamicField : solrProductSchemaDynamicFields) {
      if (key.matches(dynamicField) && isDynamicFieldCountReachingLimit(dynamicFieldCount)) {
        dynamicFieldCount.increment();
        return true;
      }
    }

    return false;
  }

  private boolean isDynamicFieldCountReachingLimit(MutableInt dynamicFieldCount) {
    if (dynamicFieldCount.intValue() == DYNAMIC_FIELDS_COUNT_THRESHOLD) {
      logger.error("There are %d dynamic fileds, more than threshold: %d. please contact Relevance Team, " +
              "ignore all remaining dynamic fields", dynamicFieldCount.intValue() + 1, DYNAMIC_FIELDS_COUNT_THRESHOLD);
      return false;
    } else if (dynamicFieldCount.intValue() > DYNAMIC_FIELDS_COUNT_THRESHOLD) {
      return false;
    }

    return true;
  }
}
