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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Exercises the runtime-derived inventory and independent Maven Central consumer checks.
 *
 * @author Eike Stepper
 */
public class CentralVerificationToolTest
{
  private static final String[] BUNDLES = { "org.eclipse.net4j.util", "org.eclipse.net4j", "org.eclipse.net4j.tcp", "org.eclipse.net4j.db",
      "org.eclipse.net4j.db.jdbc", "org.eclipse.net4j.db.h2", "org.eclipse.emf.cdo.common", "org.eclipse.emf.cdo", "org.eclipse.emf.cdo.net4j",
      "org.eclipse.emf.cdo.server", "org.eclipse.emf.cdo.server.net4j", "org.eclipse.emf.cdo.server.db", "org.eclipse.net4j.jvm", "org.eclipse.net4j.ws",
      "org.eclipse.net4j.ws.jetty.server", "org.eclipse.net4j.db.postgresql", "org.eclipse.emf.cdo.server.embedded", "org.eclipse.emf.cdo.expressions",
      "org.eclipse.emf.cdo.security", "org.eclipse.emf.cdo.server.security", "org.eclipse.emf.cdo.examples.company" };

  private static final String[] VERSIONS = { "3.30.0", "4.22.0", "4.5.0", "4.14.0", "4.5.0", "4.6.0", "4.28.0", "4.30.0", "4.9.0", "4.26.0", "4.9.0", "4.15.0",
      "4.4.1", "2.0.0", "1.0.0", "4.5.1", "4.11.1", "4.5.0", "4.8.0", "4.13.0", "4.1.4" };

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void importsThePublishedBomAndReportsTheDerivedReleaseInventory() throws Exception
  {
    Fixture fixture = new Fixture();
    fixture.deriveInventory();
    fixture.writeBom();
    String[] arguments = fixture.arguments();
    CentralVerificationTool.prepare(arguments);
    fixture.writeTreeAndResolvedArtifacts();
    CentralVerificationTool.report(fixture.reportArguments());

    String consumer = Files.readString(fixture.consumer, StandardCharsets.UTF_8);
    assertTrue(consumer.contains("<scope>import</scope>"));
    assertTrue(consumer.contains("<type>pom</type>"));
    assertEquals(2, consumer.split("<version>", -1).length - 1);
    assertEquals(1, consumer.split("<type>", -1).length - 1);

    String report = Files.readString(fixture.report, StandardCharsets.UTF_8);
    assertTrue(report.contains("\"projectRelease\": \"4.36\""));
    assertTrue(report.contains("org.eclipse.cdo:org.eclipse.cdo.bom:pom:4.36.0"));
    assertTrue(report.contains("\"result\": \"PASS\""));
    assertTrue(report.contains("\"osgiVersion\": \"3.30.0.v20260901-1311\""));
    assertEquals(BUNDLES.length, report.split("\"mavenVersion\"", -1).length - 1);
  }

  @Test
  public void rejectsIncorrectPublishedBomMembershipAndVersions() throws Exception
  {
    Fixture fixture = new Fixture();
    fixture.deriveInventory();
    fixture.writeBom();
    String valid = Files.readString(fixture.bom, StandardCharsets.UTF_8);
    String[] invalidPoms = { valid
        .replaceFirst("<dependency><groupId>org.eclipse.cdo</groupId><artifactId>" + BUNDLES[0] + "</artifactId><version>[^<]+</version></dependency>", ""),
        valid.replace("<version>" + VERSIONS[0] + "</version>", "<version>99.0.0</version>") };

    for (String invalidPom : invalidPoms)
    {
      Files.writeString(fixture.bom, invalidPom, StandardCharsets.UTF_8);
      try
      {
        CentralVerificationTool.prepare(fixture.arguments());
        fail("Expected incorrect published BOM inventory to be rejected");
      }
      catch (IllegalArgumentException expected)
      {
        assertTrue(
            expected.getMessage().contains("expected managed dependencies") || expected.getMessage().contains("managed dependency inventory or versions"));
      }

      Files.writeString(fixture.bom, valid, StandardCharsets.UTF_8);
    }
  }

  @Test
  public void verifiesOnlyTheTwentyOneRootDependenciesInMavenTree() throws Exception
  {
    Fixture fixture = new Fixture();
    fixture.deriveInventory();
    fixture.writeBom();
    fixture.writeTreeAndResolvedArtifacts();
    CentralVerificationTool.report(fixture.reportArguments());

    fixture.writeTree(fixture.expectedTree(true));
    expectTreeFailure(fixture, "unexpected direct dependency");

    List<String> lines = fixture.expectedTree();
    lines.remove(0);
    fixture.writeTree(lines);
    expectTreeFailure(fixture, "missing direct dependency");

    lines = fixture.expectedTree();
    lines.set(0, lines.get(0).replace(":" + VERSIONS[0] + ":compile", ":99.0.0:compile"));
    fixture.writeTree(lines);
    expectTreeFailure(fixture, "incorrect direct version");
  }

  @Test
  public void generatesExactlyTheApprovedBundleVersionsAndBomMarker() throws Exception
  {
    Fixture fixture = new Fixture();
    fixture.deriveInventory();
    fixture.verifyAndWriteMarker();

    List<String> lines = Files.readAllLines(fixture.marker, StandardCharsets.UTF_8);
    assertEquals(22, lines.size());
    Set<String> coordinates = new LinkedHashSet<>();
    for (int i = 0; i < BUNDLES.length; i++)
    {
      assertEquals(BUNDLES[i] + "=" + VERSIONS[i], lines.get(i));
      coordinates.add(lines.get(i).substring(0, lines.get(i).indexOf('=')));
    }

    assertEquals("org.eclipse.cdo.bom=4.36.0", lines.get(lines.size() - 1));
    coordinates.add("org.eclipse.cdo.bom");
    assertEquals(22, coordinates.size());
  }

  @Test
  public void failedMavenVerificationDoesNotGenerateMarker() throws Exception
  {
    Fixture fixture = new Fixture();
    fixture.deriveInventory();
    fixture.writeBom();
    fixture.writeTreeAndResolvedArtifacts();
    List<String> invalidTree = fixture.expectedTree();
    invalidTree.remove(0);
    fixture.writeTree(invalidTree);
    try
    {
      CentralVerificationTool.report(fixture.reportArguments());
      fail("Expected failed verification to block marker generation");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("dependency tree differs"));
      assertTrue(!Files.exists(fixture.marker));
    }
  }

  @Test
  public void reportNeverGeneratesMarkerForNonReleaseDrop() throws Exception
  {
    Fixture fixture = new Fixture();
    fixture.deriveInventory();
    String inventory = Files.readString(fixture.inventory, StandardCharsets.UTF_8).replace("drop.id=R20260901-1311", "drop.id=I20260901-1311")
        .replace("cdo.drop.id=R20260901-1311", "cdo.drop.id=I20260901-1311");
    Files.writeString(fixture.inventory, inventory, StandardCharsets.UTF_8);
    fixture.writeBom();
    fixture.writeTreeAndResolvedArtifacts();

    try
    {
      CentralVerificationTool.report(fixture.reportArguments());
      fail("Expected non-release drop marker generation to be rejected");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("approved bundle and BOM coordinates"));
      assertTrue(Files.readString(fixture.report, StandardCharsets.UTF_8).contains("\"result\": \"PASS\""));
      assertTrue(!Files.exists(fixture.marker));
    }
  }

  private static void expectTreeFailure(Fixture fixture, String description) throws Exception
  {
    try
    {
      CentralVerificationTool.report(fixture.reportArguments());
      fail("Expected " + description + " to be rejected");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("dependency tree differs"));
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class Fixture
  {
    private final Path root = temporaryFolder.getRoot().toPath();

    private final Path drop = root.resolve("drop");

    private final Path inventory = root.resolve("runtime-release-inventory.properties");

    private final Path bom = root.resolve("published-bom.pom");

    private final Path consumer = root.resolve("consumer/pom.xml");

    private final Path tree = root.resolve("dependency-tree.txt");

    private final Path resolved = root.resolve("resolved");

    private final Path report = root.resolve("verification-report.json");

    private final Path marker = root.resolve("maven-central.properties");

    private Fixture() throws Exception
    {
      Files.createDirectories(drop);
      Files.writeString(drop.resolve("web.properties"), "web.label=4.36\n", StandardCharsets.UTF_8);
      Files.writeString(drop.resolve("build-info.xml"),
          "<build qualifier=\"R20260901-1311\" revision=\"0123456789abcdef0123456789abcdef01234567\" branch=\"main\""
              + " eclipse=\"2026-09\" emf=\"2.42\" hudson=\"https://ci.eclipse.org/cdo/\" job=\"cdo\" number=\"1\" stream=\"4.36\""
              + " timestamp=\"2026-09-01T13:11:00Z\" train=\"2026-09\" trigger=\"test\" type=\"R\"/>",
          StandardCharsets.UTF_8);
      StringBuilder index = new StringBuilder("<index>\n");
      for (int i = 0; i < BUNDLES.length; i++)
      {
        index.append("<element name=\"").append(BUNDLES[i]).append("\" type=\"osgi.bundle\" version=\"").append(VERSIONS[i]).append(".v20260901-1311\"/>\n");
      }

      index.append("</index>\n");
      Files.writeString(drop.resolve("index.xml"), index, StandardCharsets.UTF_8);
    }

    private void deriveInventory() throws Exception
    {
      CDOAggregationTool.main(new String[] { "derive-release-inventory", drop.toString(), "R20260901-1311", inventory.toString() });
    }

    private void writeBom() throws Exception
    {
      Properties properties = new Properties();
      try (var input = Files.newInputStream(inventory))
      {
        properties.load(input);
      }

      StringBuilder dependencies = new StringBuilder();
      for (int i = 0; i < BUNDLES.length; i++)
      {
        dependencies.append("<dependency><groupId>org.eclipse.cdo</groupId><artifactId>").append(BUNDLES[i]).append("</artifactId><version>")
            .append(VERSIONS[i]).append("</version></dependency>");
      }

      Files.writeString(bom,
          "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"><modelVersion>4.0.0</modelVersion><groupId>org.eclipse.cdo</groupId>"
              + "<artifactId>org.eclipse.cdo.bom</artifactId><version>" + properties.getProperty("bom.version")
              + "</version><packaging>pom</packaging><properties><cdo.project.release>" + properties.getProperty("project.release")
              + "</cdo.project.release><cdo.drop.id>" + properties.getProperty("drop.id") + "</cdo.drop.id></properties><dependencyManagement><dependencies>"
              + dependencies + "</dependencies></dependencyManagement></project>",
          StandardCharsets.UTF_8);
    }

    private void writeTreeAndResolvedArtifacts() throws Exception
    {
      Properties properties = new Properties();
      try (var input = Files.newInputStream(inventory))
      {
        properties.load(input);
      }

      Files.createDirectories(resolved);
      for (String bundle : BUNDLES)
      {
        String version = properties.getProperty("artifact." + bundle);
        Files.write(resolved.resolve(bundle + "-" + version + ".jar"), new byte[] { 1 });
      }

      writeTree(expectedTree());
    }

    private List<String> expectedTree()
    {
      return expectedTree(false);
    }

    private List<String> expectedTree(boolean unexpectedDirectDependency)
    {
      List<String> lines = new ArrayList<>();
      for (int i = 0; i < BUNDLES.length; i++)
      {
        String rootPrefix = i == BUNDLES.length - 1 && !unexpectedDirectDependency ? "\\- " : "+- ";
        lines.add(rootPrefix + "org.eclipse.cdo:" + BUNDLES[i] + ":jar:" + VERSIONS[i] + ":compile");
        if (i == 1)
        {
          lines.add("|  +- org.eclipse.emf:transitive-under-intermediate:jar:1.0.0:compile");
          lines.add("|  |  \\- org.eclipse.emf:nested-transitive:jar:1.0.0:compile");
        }

        if (i == BUNDLES.length - 1)
        {
          String childPrefix = unexpectedDirectDependency ? "|  \\- " : "   \\- ";
          lines.add(childPrefix + "org.eclipse.emf:transitive-under-final:jar:1.0.0:compile");
          lines.add((unexpectedDirectDependency ? "|  |  \\- " : "      \\- ") + "org.eclipse.emf:nested-under-final:jar:1.0.0:compile");
        }
      }

      if (unexpectedDirectDependency)
      {
        lines.add("\\- org.eclipse.cdo:unexpected:jar:1.0.0:compile");
      }

      return lines;
    }

    private void writeTree(List<String> lines) throws Exception
    {
      Files.write(tree, lines, StandardCharsets.UTF_8);
    }

    private void verifyAndWriteMarker() throws Exception
    {
      writeBom();
      writeTreeAndResolvedArtifacts();
      CentralVerificationTool.report(reportArguments());
    }

    private String[] arguments()
    {
      return new String[] { "central-verify-prepare", inventory.toString(), bom.toString(), consumer.toString(), tree.toString(), resolved.toString(),
          report.toString() };
    }

    private String[] reportArguments()
    {
      return new String[] { "central-verify-report", inventory.toString(), bom.toString(), consumer.toString(), tree.toString(), resolved.toString(),
          report.toString(), marker.toString() };
    }
  }
}
