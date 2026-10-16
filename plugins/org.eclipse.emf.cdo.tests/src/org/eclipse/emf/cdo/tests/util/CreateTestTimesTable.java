
/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class CreateTestTimesTable
{
  public static void main(String[] args) throws Exception
  {
    if (args.length == 0)
    {
      System.err.println("Usage: java CreateTestTimesTable <input.xml> [<output.csv>]");
      System.exit(1);
    }

    File input = new File(args[0]);
    System.out.println("Input:  " + input);

    File output = args.length >= 2 ? new File(args[1]) : new File(input.toString() + ".csv");
    System.out.println("Output: " + output);

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

    Document document = factory.newDocumentBuilder().parse(input);
    NodeList suites = document.getElementsByTagName("testsuite");
    List<String> scenarios = new ArrayList<>();
    Map<String, Map<String, String>> times = new LinkedHashMap<>();

    for (int i = 0; i < suites.getLength(); i++)
    {
      Element suite = (Element)suites.item(i);
      String scenario = suite.getAttribute("name");

      if (!scenario.startsWith("Scenario["))
      {
        continue;
      }

      scenario = scenario.substring(8);
      scenarios.add(scenario);

      NodeList testCases = suite.getElementsByTagName("testcase");

      for (int j = 0; j < testCases.getLength(); j++)
      {
        Element testCase = (Element)testCases.item(j);
        String name = testCase.getAttribute("classname") + "." + testCase.getAttribute("name");
        String time = testCase.getAttribute("time");
        String previous = times.computeIfAbsent(name, key -> new LinkedHashMap<>()).putIfAbsent(scenario, time);

        if (previous != null)
        {
          throw new IllegalStateException("Duplicate test case in " + scenario + ": " + name);
        }
      }
    }

    Map<String, BigDecimal> averages = new LinkedHashMap<>();

    for (Map.Entry<String, Map<String, String>> entry : times.entrySet())
    {
      Map<String, String> values = entry.getValue();
      BigDecimal sum = BigDecimal.ZERO;

      for (String value : values.values())
      {
        sum = sum.add(new BigDecimal(value));
      }

      BigDecimal average = sum.divide(BigDecimal.valueOf(values.size()), 6, RoundingMode.HALF_UP);
      averages.put(entry.getKey(), average);
    }

    List<String> testCases = new ArrayList<>(times.keySet());
    testCases.sort((a, b) -> {
      int result = averages.get(b).compareTo(averages.get(a));
      return result != 0 ? result : a.compareTo(b);
    });

    try (BufferedWriter out = Files.newBufferedWriter(output.toPath(), StandardCharsets.UTF_8))
    {
      out.write('\uFEFF');
      out.write(csv("Test Case"));
      out.write(';');
      out.write(csv("Average"));

      for (String scenario : scenarios)
      {
        out.write(';');
        out.write(csv(scenario));
      }

      out.newLine();

      for (String testCase : testCases)
      {
        out.write(csv(testCase));
        out.write(';');
        out.write(averages.get(testCase).stripTrailingZeros().toPlainString().replace('.', ','));

        Map<String, String> values = times.get(testCase);

        for (String scenario : scenarios)
        {
          out.write(';');
          out.write(values.getOrDefault(scenario, "").replace('.', ','));
        }

        out.newLine();
      }
    }

    System.out.println(times.size() + " test cases, " + scenarios.size() + " scenarios");
  }

  private static String csv(String value)
  {
    return '"' + value.replace("\"", "\"\"") + '"';
  }
}
