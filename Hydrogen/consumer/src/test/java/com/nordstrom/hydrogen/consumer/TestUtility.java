package com.nordstrom.hydrogen.consumer;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.nordstrom.hydrogen.consumer.model.EventRecord;
import com.nordstrom.sharedlib.stream.ConsumerClient;

public class TestUtility {

  @Rule
  public ExpectedException exceptionThrown = ExpectedException.none();
  
  public EventRecord prepareEventRecord(String jsonString) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(jsonString, EventRecord.class);
  }
  
  public void setExpectedArgumentException(String expectedMessage, Class<? extends Throwable> throwableClass) {
    exceptionThrown.expect(throwableClass);
    exceptionThrown.expectMessage(expectedMessage);
  }

  public EventRecord eventRecordGenerator(String recordValue) throws IOException{
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(recordValue, EventRecord.class);
  }

  public JSONObject jsonDocumentGenerator(String json) throws IOException{
    return (JSONObject) JSONValue.parse(json);
  }

  public void mockKafkaRecordGenerator(String recordKey, String recordValue, ConsumerClient consumerClient) throws IOException {
    Map <String, String> consumerRecords = new HashMap<>();
    consumerRecords.put(recordKey, recordValue);
    when(consumerClient.poll(anyLong())).thenReturn(consumerRecords);
  }

  public JSONObject readDocumentJsonFile(String fileName) throws ParseException, IOException {
    return (JSONObject) new JSONParser().parse(readDocumentFile(fileName));
  }

  public String readDocumentFile(String fileName) throws IOException {
    URL documentUrl = this.getClass().getResource(fileName);

    String documentPath = documentUrl.getPath();
    BufferedReader br = new BufferedReader(new FileReader(documentPath));
    try {
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();
      while (line != null) {
        sb.append(line);
        sb.append("\n");
        line = br.readLine();
      }
      return sb.toString();
    } finally {
      br.close();
    }
  }
}
