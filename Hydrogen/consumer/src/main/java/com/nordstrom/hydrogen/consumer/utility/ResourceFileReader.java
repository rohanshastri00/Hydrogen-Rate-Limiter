package com.nordstrom.hydrogen.consumer.utility;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.nordstrom.sharedlib.logging.ApplicationLogger;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ResourceFileReader {

  @Autowired
  ApplicationLogger logger;

  public List<String> readFileToListOfString(String fileName) throws IOException {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    InputStream inputStream = classloader.getResourceAsStream(fileName);
    Scanner scanner = new Scanner(inputStream);
    List<String> list = new ArrayList<>();

    try {
      while (scanner.hasNextLine()) {
        list.add(scanner.nextLine());
      }
    } finally {
      if (scanner != null) {
        scanner.close();
      }
    if (inputStream != null) {
      inputStream.close();
  }
    }

    return list;
  }

  public String readFileToString(String fileName) {
    try {
      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      InputStream inputStream = classloader.getResourceAsStream(fileName);
      String result  = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
      return result;
    } catch (IOException ex) {
      logger.error("could not convert inputstream to string");
    }
    return null;
  }

}
