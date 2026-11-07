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
package org.eclipse.emf.cdo.releng;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Prepares and checks a read-only Maven Central consumer verification run.
 *
 * @author Eike Stepper
 */
final class CentralVerificationTool
{
  private static final String GROUP = "org.eclipse.cdo";

  private CentralVerificationTool()
  {
  }

  static void prepare(String[] args) throws Exception
  {
    if (args.length != 7)
    {
      throw new IllegalArgumentException("central-verify-prepare requires inventory, published BOM, consumer POM, tree, resolved directory, and report");
    }

    Map<String, String> trusted = inventory(new File(args[1]).toPath());
    Map<String, String> artifacts = artifacts(trusted);
    Path bom = new File(args[2]).toPath();
    Path consumer = new File(args[3]).toPath();
    verifyBom(bom, trusted, artifacts);
    writeConsumer(consumer, trusted, artifacts);
  }

  static void report(String[] args) throws Exception
  {
    if (args.length != 8)
    {
      throw new IllegalArgumentException(
          "central-verify-report requires inventory, published BOM, consumer POM, tree, resolved directory, report, and marker output");
    }

    Map<String, String> trusted = inventory(new File(args[1]).toPath());
    Map<String, String> artifacts = artifacts(trusted);
    Path bom = new File(args[2]).toPath();
    Path tree = new File(args[4]).toPath();
    Path resolved = new File(args[5]).toPath();
    Path report = new File(args[6]).toPath();
    Path marker = new File(args[7]).toPath();
    verifyBom(bom, trusted, artifacts);
    verifyTree(tree, artifacts);
    for (Map.Entry<String, String> artifact : artifacts.entrySet())
    {
      Path jar = resolved.resolve(artifact.getKey() + "-" + artifact.getValue() + ".jar");
      if (!Files.isRegularFile(jar) || Files.size(jar) == 0)
      {
        throw new IllegalArgumentException("Maven did not resolve published artifact " + GROUP + ":" + artifact.getKey() + ":jar:" + artifact.getValue());
      }
    }

    writeReport(report, trusted, artifacts);
    writeMarker(trusted, artifacts, marker);
  }

  private static void writeMarker(Map<String, String> trusted, Map<String, String> artifacts, Path markerPath) throws Exception
  {
    List<String> approved = CDOAggregationTool.approvedBundles();
    if (!Pattern.matches("R[0-9]{8}-[0-9]{4}", trusted.get("drop.id")) || !artifacts.keySet().equals(new LinkedHashSet<>(approved))
        || !"org.eclipse.cdo".equals(trusted.get("bom.groupId")) || !"org.eclipse.cdo.bom".equals(trusted.get("bom.artifactId"))
        || !Pattern.matches("[0-9]+\\.[0-9]+\\.[0-9]+", trusted.get("bom.version")))
    {
      throw new IllegalArgumentException("Verified release inventory does not match the approved bundle and BOM coordinates");
    }

    List<String> lines = new ArrayList<>();
    for (String artifact : approved)
    {
      lines.add(artifact + "=" + artifacts.get(artifact));
    }

    lines.add(trusted.get("bom.artifactId") + "=" + trusted.get("bom.version"));
    Files.createDirectories(markerPath.getParent());
    Files.write(markerPath, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
  }

  private static Map<String, String> inventory(Path path) throws Exception
  {
    Map<String, String> properties = new LinkedHashMap<>();
    for (String line : Files.readAllLines(path, StandardCharsets.UTF_8))
    {
      String text = line.trim();
      if (!text.isEmpty() && !text.startsWith("#"))
      {
        int separator = text.indexOf('=');
        if (separator < 1 || properties.put(text.substring(0, separator).trim(), text.substring(separator + 1).trim()) != null)
        {
          throw new IllegalArgumentException("Malformed or duplicate release inventory entry: " + line);
        }
      }
    }

    return properties;
  }

  private static Map<String, String> artifacts(Map<String, String> properties)
  {
    Map<String, String> artifacts = new LinkedHashMap<>();
    for (Map.Entry<String, String> entry : properties.entrySet())
    {
      if (entry.getKey().startsWith("artifact."))
      {
        String artifact = entry.getKey().substring("artifact.".length());
        String version = entry.getValue();
        if (!Pattern.matches("[A-Za-z0-9_.-]+", artifact) || !Pattern.matches("[0-9][A-Za-z0-9_.-]*", version) || artifacts.put(artifact, version) != null)
        {
          throw new IllegalArgumentException("Invalid or duplicate derived artifact entry: " + entry.getKey());
        }
      }
    }

    Set<String> approved = new LinkedHashSet<>(CDOAggregationTool.approvedBundles());
    if (artifacts.size() != approved.size() || !artifacts.keySet().equals(new LinkedHashSet<>(approved)) || !properties.containsKey("drop.id")
        || !properties.containsKey("drop.location") || !properties.containsKey("project.release") || !properties.containsKey("bom.version"))
    {
      throw new IllegalArgumentException(
          "Derived release inventory must identify its source drop, project release, BOM, and exactly " + approved.size() + " artifacts");
    }

    return artifacts;
  }

  private static void verifyBom(Path bomPath, Map<String, String> trusted, Map<String, String> expected) throws Exception
  {
    Document document = CDOAggregationTool.parse(bomPath.toFile());
    Element project = document.getDocumentElement();
    String group = value(project, "groupId");
    String artifact = value(project, "artifactId");
    String version = value(project, "version");
    if (!trusted.get("bom.groupId").equals(group) || !trusted.get("bom.artifactId").equals(artifact) || !trusted.get("bom.version").equals(version))
    {
      throw new IllegalArgumentException("Published BOM coordinates do not match trusted inventory");
    }

    if (!"pom".equals(value(project, "packaging")))
    {
      throw new IllegalArgumentException("Published BOM packaging is not pom");
    }

    Element properties = child(project, "properties");
    if (!trusted.get("project.release").equals(value(properties, "cdo.project.release")) || !trusted.get("drop.id").equals(value(properties, "cdo.drop.id")))
    {
      throw new IllegalArgumentException("Published BOM release or drop identity does not match trusted inventory");
    }

    Element management = child(project, "dependencyManagement");
    Element dependencies = child(management, "dependencies");
    NodeList allDependencies = project.getElementsByTagNameNS("*", "dependency");
    NodeList allDependencySections = project.getElementsByTagNameNS("*", "dependencies");
    if (allDependencies.getLength() != expected.size() || allDependencySections.getLength() != 1)
    {
      throw new IllegalArgumentException("Published BOM must contain only the expected managed dependencies");
    }

    Map<String, String> actual = new LinkedHashMap<>();
    NodeList children = dependencies.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node node = children.item(i);
      if (node instanceof Element && "dependency".equals(node.getLocalName()))
      {
        Element dependency = (Element)node;
        int fieldCount = 0;
        NodeList fields = dependency.getChildNodes();
        for (int j = 0; j < fields.getLength(); j++)
        {
          Node field = fields.item(j);
          if (field instanceof Element)
          {
            fieldCount++;
            String fieldName = field.getLocalName();
            if (!"groupId".equals(fieldName) && !"artifactId".equals(fieldName) && !"version".equals(fieldName))
            {
              throw new IllegalArgumentException("Unexpected managed BOM dependency field " + fieldName);
            }
          }
        }

        if (fieldCount != 3)
        {
          throw new IllegalArgumentException("Managed BOM dependencies must contain exactly groupId, artifactId, and version");
        }

        String depGroup = value(dependency, "groupId");
        String depArtifact = value(dependency, "artifactId");
        if (actual.put(depGroup + ":" + depArtifact, value(dependency, "version")) != null)
        {
          throw new IllegalArgumentException("Duplicate managed coordinate " + depGroup + ":" + depArtifact);
        }
      }
    }

    Map<String, String> expectedCoordinates = new LinkedHashMap<>();
    expected.forEach((name, depVersion) -> expectedCoordinates.put(GROUP + ":" + name, depVersion));
    if (!actual.equals(expectedCoordinates))
    {
      throw new IllegalArgumentException("Published BOM managed dependency inventory or versions differ from trusted release data: " + actual);
    }
  }

  private static void writeConsumer(Path path, Map<String, String> trusted, Map<String, String> artifacts) throws Exception
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    Document document = factory.newDocumentBuilder().newDocument();
    String namespace = "http://maven.apache.org/POM/4.0.0";
    Element project = document.createElementNS(namespace, "project");
    document.appendChild(project);
    append(document, project, "modelVersion", "4.0.0");
    append(document, project, "groupId", "org.eclipse.cdo.releng");
    append(document, project, "artifactId", "maven-central-verification-consumer");
    append(document, project, "version", "1.0.0");
    Element management = document.createElementNS(namespace, "dependencyManagement");
    project.appendChild(management);
    Element managedDependencies = document.createElementNS(namespace, "dependencies");
    management.appendChild(managedDependencies);
    Element bom = document.createElementNS(namespace, "dependency");
    managedDependencies.appendChild(bom);
    append(document, bom, "groupId", trusted.get("bom.groupId"));
    append(document, bom, "artifactId", trusted.get("bom.artifactId"));
    append(document, bom, "version", trusted.get("bom.version"));
    append(document, bom, "type", "pom");
    append(document, bom, "scope", "import");
    Element dependencies = document.createElementNS(namespace, "dependencies");
    project.appendChild(dependencies);
    for (Map.Entry<String, String> artifact : artifacts.entrySet())
    {
      Element dependency = document.createElementNS(project.getNamespaceURI(), "dependency");
      append(document, dependency, "groupId", GROUP);
      append(document, dependency, "artifactId", artifact.getKey());
      dependencies.appendChild(dependency);
    }

    Files.createDirectories(path.getParent());
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.transform(new DOMSource(document), new StreamResult(path.toFile()));
  }

  private static void verifyTree(Path path, Map<String, String> expected) throws Exception
  {
    String tree = Files.readString(path, StandardCharsets.UTF_8);
    Map<String, String> actual = new LinkedHashMap<>();
    for (String line : tree.split("\\R"))
    {
      String text = line.replaceFirst("^\\[INFO\\]\\s*", "");
      if (text.startsWith("+- ") || text.startsWith("\\- "))
      {
        text = text.substring(3);
        String[] parts = text.split(":");
        if (parts.length >= 5)
        {
          if (!"jar".equals(parts[2]))
          {
            throw new IllegalArgumentException("Maven resolved a non-JAR direct consumer dependency: " + text);
          }

          String coordinate = parts[0] + ":" + parts[1];
          if (actual.put(coordinate, parts[3]) != null)
          {
            throw new IllegalArgumentException("Duplicate direct consumer dependency " + coordinate);
          }
        }
      }
    }

    Map<String, String> expectedCoordinates = new LinkedHashMap<>();
    expected.forEach((name, version) -> expectedCoordinates.put(GROUP + ":" + name, version));
    if (!actual.equals(expectedCoordinates))
    {
      throw new IllegalArgumentException("Maven dependency tree differs from trusted inventory: " + actual);
    }
  }

  private static void writeReport(Path report, Map<String, String> trusted, Map<String, String> artifacts) throws Exception
  {
    List<String> lines = new ArrayList<>();
    lines.add("{");
    lines.add("  \"sourceDrop\": {");
    lines.add("    \"id\": \"" + json(trusted.get("drop.id")) + "\",");
    lines.add("    \"location\": \"" + json(trusted.get("drop.location")) + "\",");
    lines.add("    \"projectRelease\": \"" + json(trusted.get("project.release")) + "\",");
    lines.add("    \"gitCommit\": \"" + json(trusted.get("cdo.git.commit")) + "\",");
    lines.add("    \"buildBranch\": \"" + json(trusted.get("cdo.build.branch")) + "\",");
    lines.add("    \"jenkinsJob\": \"" + json(trusted.get("cdo.jenkins.job")) + "\",");
    lines.add("    \"jenkinsBuild\": \"" + json(trusted.get("cdo.jenkins.build")) + "\",");
    lines.add("    \"buildTimestamp\": \"" + json(trusted.get("cdo.build.timestamp")) + "\",");
    lines.add("    \"buildType\": \"" + json(trusted.get("cdo.build.type")) + "\"");
    lines.add("  },");
    lines.add("  \"bom\": \"" + trusted.get("bom.groupId") + ":" + trusted.get("bom.artifactId") + ":pom:" + trusted.get("bom.version") + "\",");
    lines.add("  \"result\": \"PASS\",");
    lines.add("  \"derivedInventory\": [");
    int index = 0;
    for (Map.Entry<String, String> entry : artifacts.entrySet())
    {
      String comma = ++index == artifacts.size() ? "" : ",";
      lines.add("    {\"artifactId\": \"" + entry.getKey() + "\", \"osgiVersion\": \"" + json(trusted.get("osgi." + entry.getKey()))
          + "\", \"mavenVersion\": \"" + entry.getValue() + "\"}" + comma);
    }

    lines.add("  ],");
    lines.add("  \"verifiedArtifacts\": [");
    index = 0;
    for (Map.Entry<String, String> entry : artifacts.entrySet())
    {
      String comma = ++index == artifacts.size() ? "" : ",";
      lines.add("    \"" + GROUP + ":" + entry.getKey() + ":jar:" + entry.getValue() + "\"" + comma);
    }

    lines.add("  ]");
    lines.add("}");
    Files.createDirectories(report.getParent());
    Files.write(report, lines, StandardCharsets.UTF_8);
  }

  private static String json(String value)
  {
    if (value == null)
    {
      return "";
    }

    return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
  }

  private static Element child(Element parent, String name)
  {
    NodeList nodes = parent.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      if (node instanceof Element && name.equals(node.getLocalName()))
      {
        return (Element)node;
      }
    }

    throw new IllegalArgumentException("Missing POM element " + name);
  }

  private static String value(Element parent, String name)
  {
    return child(parent, name).getTextContent().trim();
  }

  private static void append(Document document, Element parent, String name, String value)
  {
    Element child = document.createElementNS(parent.getNamespaceURI(), name);
    child.setTextContent(value);
    parent.appendChild(child);
  }

}
