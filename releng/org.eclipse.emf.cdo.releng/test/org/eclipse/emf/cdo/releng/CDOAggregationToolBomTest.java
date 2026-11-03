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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Verifies deterministic generation of the standalone CDO project-release BOM.
 *
 * @author Eike Stepper
 */
public class CDOAggregationToolBomTest
{
  private static final String[] BUNDLES = { "org.eclipse.net4j.util", "org.eclipse.net4j", "org.eclipse.net4j.tcp", "org.eclipse.net4j.db",
      "org.eclipse.net4j.db.jdbc", "org.eclipse.net4j.db.h2", "org.eclipse.emf.cdo.common", "org.eclipse.emf.cdo", "org.eclipse.emf.cdo.net4j",
      "org.eclipse.emf.cdo.server", "org.eclipse.emf.cdo.server.net4j", "org.eclipse.emf.cdo.server.db" };

  private static final String PUBLISHING_METADATA = "project.url=https://eclipse.dev/cdo/\n" + "organization.name=Eclipse Foundation\n"
      + "organization.url=https://www.eclipse.org\n" + "license.name=EPL-2.0\n" + "license.url=https://www.eclipse.org/legal/epl-2.0/\n"
      + "license.distribution=repo\n" + "developers.url=https://github.com/eclipse-cdo/cdo/graphs/contributors\n" + "issue.system=GitHub\n"
      + "issue.url=https://github.com/eclipse-cdo/cdo/issues\n" + "scm.connection=scm:git:https://github.com/eclipse-cdo/cdo.git\n"
      + "scm.developerConnection=scm:git:https://github.com/eclipse-cdo/cdo.git\n";

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void generatesTwoSegmentReleaseBomWithExactlyApprovedManagedBundlesAndMetadata() throws Exception
  {
    Path root = fixture("4.36", "R20260901-1311");
    File bom = generate(root, "output");
    Document document = parse(bom);
    Element project = document.getDocumentElement();
    assertEquals("org.eclipse.cdo", childText(project, "groupId"));
    assertEquals("org.eclipse.cdo.bom", childText(project, "artifactId"));
    assertEquals("4.36.0", childText(project, "version"));
    assertEquals("pom", childText(project, "packaging"));
    assertEquals("4.36", childText(child(project, "properties"), "cdo.project.release"));
    assertEquals("R20260901-1311", childText(child(project, "properties"), "cdo.drop.id"));
    assertEquals("EPL-2.0", childText(child(child(project, "licenses"), "license"), "name"));
    assertEquals("Eclipse Foundation", childText(child(project, "organization"), "name"));
    assertEquals("https://eclipse.dev/cdo/", childText(project, "url"));
    assertEquals("https://github.com/eclipse-cdo/cdo/graphs/contributors", childText(child(child(project, "developers"), "developer"), "url"));
    assertEquals("0123456789abcdef0123456789abcdef01234567", childText(child(project, "scm"), "tag"));
    assertEquals("https://github.com/eclipse-cdo/cdo/issues", childText(child(project, "issueManagement"), "url"));

    Element management = child(project, "dependencyManagement");
    assertNotNull(management);
    assertEquals(12, elements(child(management, "dependencies"), "dependency").getLength());
    assertEquals(0, directElements(project, "dependencies"));
    for (int i = 0; i < BUNDLES.length; i++)
    {
      Element dependency = (Element)elements(child(management, "dependencies"), "dependency").item(i);
      assertEquals("org.eclipse.cdo", childText(dependency, "groupId"));
      assertEquals(BUNDLES[i], childText(dependency, "artifactId"));
      assertEquals("1.0." + i, childText(dependency, "version"));
    }

    assertTrue(new File(bom.getParentFile(), bom.getName() + ".sha256").isFile());
    try (java.util.stream.Stream<Path> files = Files.list(bom.getParentFile().toPath()))
    {
      assertEquals(5, files.count());
    }
  }

  @Test
  public void acceptsThreeSegmentReleaseVersionAndProducesReproduciblePomBytes() throws Exception
  {
    Path root = fixture("4.36.1", "R20260901-1311");
    File first = generate(root, "first");
    File second = generate(root, "second");
    assertEquals("4.36.1", childText(parse(first).getDocumentElement(), "version"));
    assertTrue(Arrays.equals(Files.readAllBytes(first.toPath()), Files.readAllBytes(second.toPath())));
  }

  @Test
  public void rejectsMalformedReleaseVersion() throws Exception
  {
    Path root = fixture("4.36.0.qualifier", "R20260901-1311");
    try
    {
      generate(root, "output");
      fail("Expected invalid project-release version to be rejected");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("web.label project-release version"));
    }
  }

  @Test
  public void prepareCopiesProjectReleaseFromSelectedDropWebLabel() throws Exception
  {
    Path drop = temporaryFolder.newFolder().toPath();
    Files.writeString(drop.resolve("web.properties"), "web.label=4.36.1\n", StandardCharsets.UTF_8);
    Files.writeString(drop.resolve("build-info.xml"), buildInfo("R20260901-1311"), StandardCharsets.UTF_8);
    Files.writeString(drop.resolve("tp-macro.setup"), "<setup><repository url=\"https://download.eclipse.org/modeling/emf/cdo/drops/R20260901-1311\"/>"
        + "<repository url=\"https://download.eclipse.org/releases/2026-09\"/></setup>", StandardCharsets.UTF_8);
    Path metadata = drop.resolve("prepared.properties");
    Path repositories = drop.resolve("repositories.txt");

    CDOAggregationTool.main(new String[] { "prepare", drop.toString(), metadata.toString(), repositories.toString() });

    assertTrue(Files.readString(metadata, StandardCharsets.UTF_8).contains("cdo.project.release=4.36.1"));
    assertTrue(Files.readString(metadata, StandardCharsets.UTF_8).contains("cdo.drop.id=R20260901-1311"));
  }

  @Test
  public void rejectsReleaseAndDropMetadataThatDisagreeWithSelectedDrop() throws Exception
  {
    Path root = fixture("4.36", "R20260901-1311");
    Files.writeString(root.resolve("drop/web.properties"), "web.label=4.37\n", StandardCharsets.UTF_8);
    try
    {
      generate(root, "output");
      fail("Expected project-release metadata mismatch to be rejected");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("does not match selected drop web.label"));
    }

    Files.writeString(root.resolve("drop/web.properties"), "web.label=4.36\n", StandardCharsets.UTF_8);
    Files.writeString(root.resolve("drop/build-info.xml"), "<build qualifier=\"R20260902-1311\"/>\n", StandardCharsets.UTF_8);
    try
    {
      generate(root, "other-output");
      fail("Expected drop-ID metadata mismatch to be rejected");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("does not match selected drop qualifier"));
    }
  }

  @Test
  public void rejectsBundlePomWhoseCoordinatesDoNotMatchItsRepositoryPath() throws Exception
  {
    Path root = fixture("4.36", "R20260901-1311");
    Path pom = root.resolve("bundles/org/eclipse/cdo/" + BUNDLES[0] + "/1.0.0/" + BUNDLES[0] + "-1.0.0.pom");
    Files.writeString(pom, bundlePom("wrong.artifact", "1.0.0", "4.36", "R20260901-1311"), StandardCharsets.UTF_8);
    try
    {
      generate(root, "output");
      fail("Expected mismatched bundle POM coordinates to be rejected");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("Bundle POM coordinate mismatch"));
    }
  }

  @Test
  public void rejectsOutputNestedInsideBundleRepository() throws Exception
  {
    Path root = fixture("4.36", "R20260901-1311");
    File output = root.resolve("bundles/generated-bom").toFile();
    try
    {
      CDOAggregationTool.main(new String[] { "generate-bom", root.resolve("bundles").toString(), root.resolve("metadata.properties").toString(),
          root.resolve("publishing.properties").toString(), output.getPath(), root.resolve("drop").toString() });
      fail("Expected overlapping BOM and bundle repositories to be rejected");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("must be separate"));
    }
  }

  @Test
  public void overlayKeepsSingleMatchingDropProvenanceAndAddsProjectRelease() throws Exception
  {
    Path root = fixture("4.36", "R20260901-1311");
    String bundle = BUNDLES[0];
    Path pom = root.resolve("bundles/org/eclipse/cdo/" + bundle + "/1.0.0/" + bundle + "-1.0.0.pom");
    CDOAggregationTool.main(new String[] { "overlay", root.resolve("bundles").toString(), root.resolve("metadata.properties").toString(),
        root.resolve("publishing.properties").toString() });
    Element properties = child(parse(pom.toFile()).getDocumentElement(), "properties");
    assertEquals(1, directElements(properties, "cdo.drop.id"));
    assertEquals("R20260901-1311", childText(properties, "cdo.drop.id"));
    assertEquals("4.36", childText(properties, "cdo.project.release"));
  }

  @Test
  public void overlayRejectsChangedExistingDropProvenance() throws Exception
  {
    Path root = fixture("4.36", "R20260901-1311");
    String bundle = BUNDLES[0];
    Path pom = root.resolve("bundles/org/eclipse/cdo/" + bundle + "/1.0.0/" + bundle + "-1.0.0.pom");
    Files.writeString(pom, bundlePom(bundle, "1.0.0", "4.36", "R20260831-1311"), StandardCharsets.UTF_8);
    try
    {
      CDOAggregationTool.main(new String[] { "overlay", root.resolve("bundles").toString(), root.resolve("metadata.properties").toString(),
          root.resolve("publishing.properties").toString() });
      fail("Expected a changed pre-existing drop ID to be rejected");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("does not match the selected drop"));
    }
  }

  @Test
  public void classifiesAllAbsentCoordinatesAsNewAndWritesInventoryReport() throws Exception
  {
    Path root = classificationFixture("4.36", "R20260901-1311");
    try (CentralFixture central = new CentralFixture())
    {
      Map<String, String> report = classify(root, central, false);
      assertEquals("13", report.get("release.component.count"));
      assertEquals("13", report.get("publication.coordinate.count"));
      for (int i = 1; i <= 13; i++)
      {
        assertEquals("NEW", report.get("release.component." + i + ".classification"));
      }
    }
  }

  @Test
  public void classifiesExistingBundleCoordinatesAsReusableAndBomAsExplicitlyExisting() throws Exception
  {
    Path root = classificationFixture("4.36", "R20260901-1311");
    try (CentralFixture central = new CentralFixture())
    {
      central.loadProposedArtifacts(root);
      Map<String, String> report = classify(root, central, false);
      assertEquals("0", report.get("publication.coordinate.count"));
      for (int i = 1; i <= 12; i++)
      {
        assertEquals("REUSABLE", report.get("release.component." + i + ".classification"));
      }

      assertEquals("REUSABLE", report.get("release.component.13.classification"));
      assertTrue(report.get("release.component.13.reason").contains("not a new deployment candidate"));
      assertEquals("0", report.get("publication.coordinate.count"));
    }
  }

  @Test
  public void permitsTheMissingProjectReleasePropertyOnThePublished436Pom() throws Exception
  {
    Path root = classificationFixture("4.36", "R20260901-1311");
    try (CentralFixture central = new CentralFixture())
    {
      central.loadProposedArtifacts(root);
      central.replaceBundlePom(0, bundlePom(BUNDLES[0], "1.0.0", "4.36", "R20260901-1311").replace("<cdo.project.release>4.36</cdo.project.release>", ""));
      Map<String, String> report = classify(root, central, false);
      assertEquals("REUSABLE", report.get("release.component.1.classification"));
    }
  }

  @Test
  public void comparesPrettyPrintedPublishedPomsWithWhitespaceNodes() throws Exception
  {
    Path root = classificationFixture("4.36", "R20260901-1311");
    try (CentralFixture central = new CentralFixture())
    {
      central.loadProposedArtifacts(root);
      String pom = bundlePom(BUNDLES[0], "1.0.0", "4.36", "R20260901-1311").replace("<properties>", "<properties>\n  ")
          .replace("</properties>", "\n</properties>").replace("><", ">\n  <");
      central.replaceBundlePom(0, pom);
      Files.writeString(root.resolve("bundles/org/eclipse/cdo/" + BUNDLES[0] + "/1.0.0/" + BUNDLES[0] + "-1.0.0.pom"), pom, StandardCharsets.UTF_8);
      Map<String, String> report = classify(root, central, false);
      assertEquals(report.get("release.component.1.reason"), "REUSABLE", report.get("release.component.1.classification"));
    }
  }

  @Test
  public void rejectsChangedDropProvenanceForThe436Retrofit() throws Exception
  {
    Path root = classificationFixture("4.36", "R20260901-1311");
    try (CentralFixture central = new CentralFixture())
    {
      central.loadProposedArtifacts(root);
      central.replaceBundlePom(0, bundlePom(BUNDLES[0], "1.0.0", "4.35", "R20260831-1311"));
      Map<String, String> report = classify(root, central, true);
      assertEquals("CONFLICT", report.get("release.component.1.classification"));
      assertTrue(report.get("release.component.1.reason").contains("does not match the selected 4.36 retrofit drop"));
    }
  }

  @Test
  public void permitsOriginalTraceabilityFromEarlierReleaseOnAnUnchangedCoordinate() throws Exception
  {
    Path root = classificationFixture("4.37", "R20261001-0900");
    try (CentralFixture central = new CentralFixture())
    {
      central.loadProposedArtifacts(root);
      central.replaceBundlePom(0, bundlePom(BUNDLES[0], "1.0.0", "4.36", "R20260901-1311"));
      Map<String, String> report = classify(root, central, false);
      assertEquals("REUSABLE", report.get("release.component.1.classification"));
    }
  }

  @Test
  public void detectsChangedBinarySourcesAndJavadocBytes() throws Exception
  {
    for (String suffix : new String[] { ".jar", "-sources.jar", "-javadoc.jar" })
    {
      Path root = classificationFixture("4.37", "R20261001-0900");
      try (CentralFixture central = new CentralFixture())
      {
        central.loadProposedArtifacts(root);
        central.replaceBundleArtifact(0, suffix, new byte[] { 8, 7, 6 });
        Map<String, String> report = classify(root, central, true);
        assertEquals("CONFLICT", report.get("release.component.1.classification"));
        assertTrue(report.get("release.component.1.reason").contains("bytes differ"));
      }
    }
  }

  @Test
  public void reportsMissingClassifierAndContradictoryPartialPublicationAsLookupErrors() throws Exception
  {
    Path root = classificationFixture("4.37", "R20261001-0900");
    try (CentralFixture central = new CentralFixture())
    {
      central.loadProposedArtifacts(root);
      central.removeBundleArtifact(0, "-sources.jar");
      Map<String, String> report = classify(root, central, true);
      assertEquals("LOOKUP_ERROR", report.get("release.component.1.classification"));
      assertTrue(report.get("release.component.1.reason").contains("incomplete"));
    }

    try (CentralFixture central = new CentralFixture())
    {
      central.setStatus(path(BUNDLES[0], "1.0.0", ".pom"), 404);
      central.setArtifact(path(BUNDLES[0], "1.0.0", ".jar"), new byte[] { 1 });
      Map<String, String> report = classify(root, central, true);
      assertEquals("LOOKUP_ERROR", report.get("release.component.1.classification"));
      assertTrue(report.get("release.component.1.reason").contains("but artifact exists"));
    }

    try (CentralFixture central = new CentralFixture())
    {
      central.setArtifact(path(BUNDLES[0], "1.0.0", ".pom") + ".asc", new byte[] { 2 });
      Map<String, String> report = classify(root, central, true);
      assertEquals("LOOKUP_ERROR", report.get("release.component.1.classification"));
      assertTrue(report.get("release.component.1.reason").contains("signature exists"));
    }
  }

  @Test
  public void detectsDependencyAndUnknownPropertyChangesConservatively() throws Exception
  {
    for (int change = 0; change < 5; change++)
    {
      Path root = classificationFixture("4.37", "R20261001-0900");
      try (CentralFixture central = new CentralFixture())
      {
        central.loadProposedArtifacts(root);
        String proposed = Files.readString(root.resolve("bundles/org/eclipse/cdo/" + BUNDLES[0] + "/1.0.0/" + BUNDLES[0] + "-1.0.0.pom"));
        String changed;
        switch (change)
        {
        case 0:
          changed = proposed.replace("<version>1.0</version>", "<version>2.0</version>");
          break;

        case 1:
          changed = proposed.replace("<scope>compile</scope>", "<scope>runtime</scope>");
          break;

        case 2:
          changed = proposed.replace("</dependency>", "<optional>true</optional></dependency>");
          break;

        case 3:
          changed = proposed.replace("</dependency>",
              "<exclusions><exclusion><groupId>org.example</groupId><artifactId>other</artifactId>" + "</exclusion></exclusions></dependency>");
          break;

        default:
          changed = proposed.replace("</properties>", "<cdo.unrecognized>different</cdo.unrecognized></properties>");
          break;
        }

        central.replaceBundlePom(0, changed);
        Map<String, String> report = classify(root, central, true);
        assertEquals("CONFLICT", report.get("release.component.1.classification"));
        assertTrue(report.get("release.component.1.reason").contains("POM metadata differs"));
      }
    }
  }

  @Test
  public void reportsMissingCorruptAndUnavailableChecksumsAsLookupErrors() throws Exception
  {
    Path root = classificationFixture("4.37", "R20261001-0900");
    try (CentralFixture central = new CentralFixture())
    {
      central.loadProposedArtifacts(root);
      central.removeChecksum(path(BUNDLES[0], "1.0.0", ".pom"));
      Map<String, String> report = classify(root, central, true);
      assertEquals("LOOKUP_ERROR", report.get("release.component.1.classification"));
      assertTrue(report.get("release.component.1.reason").contains("No published SHA-256 or SHA-1 checksum"));
    }

    try (CentralFixture central = new CentralFixture())
    {
      central.loadProposedArtifacts(root);
      central.corruptChecksum(path(BUNDLES[0], "1.0.0", ".pom"));
      Map<String, String> report = classify(root, central, true);
      assertEquals("LOOKUP_ERROR", report.get("release.component.1.classification"));
      assertTrue(report.get("release.component.1.reason").contains("checksum mismatch"));
    }

    try (CentralFixture central = new CentralFixture())
    {
      central.loadProposedArtifacts(root);
      central.replaceBundlePom(0, "<project>");
      Map<String, String> report = classify(root, central, true);
      assertEquals("LOOKUP_ERROR", report.get("release.component.1.classification"));
    }
  }

  @Test
  public void mapsUnexpectedStatusesFailedConnectionsAndTruncatedDownloadsToLookupError() throws Exception
  {
    Path root = classificationFixture("4.37", "R20261001-0900");
    try (CentralFixture central = new CentralFixture())
    {
      central.setStatus(path(BUNDLES[0], "1.0.0", ".pom"), 503);
      Map<String, String> report = classify(root, central, true);
      assertEquals("LOOKUP_ERROR", report.get("release.component.1.classification"));
      assertTrue(report.get("release.component.1.reason").contains("HTTP 503"));
    }

    try (CentralFixture central = new CentralFixture())
    {
      central.loadProposedArtifacts(root);
      central.truncate(path(BUNDLES[0], "1.0.0", ".pom"));
      Map<String, String> report = classify(root, central, true);
      assertEquals("LOOKUP_ERROR", report.get("release.component.1.classification"));
      assertTrue(report.get("release.component.1.reason").contains("truncated") || report.get("release.component.1.reason").contains("Central request failed"));
    }

    try (CentralFixture central = new CentralFixture())
    {
      String base = central.baseUrl();
      central.close();
      Map<String, String> report = classify(root, base, true);
      assertEquals("LOOKUP_ERROR", report.get("release.component.1.classification"));
      assertTrue(report.get("release.component.1.reason").contains("Central request failed"));
    }
  }

  @Test
  public void anExistingBomIsNeverListedAmongNewPublicationCoordinates() throws Exception
  {
    Path root = classificationFixture("4.36", "R20260901-1311");
    try (CentralFixture central = new CentralFixture())
    {
      central.loadProposedArtifacts(root);
      central.replaceBomWithIncompatibleVersion();
      Map<String, String> report = classify(root, central, true);
      assertEquals("CONFLICT", report.get("release.component.13.classification"));
      assertEquals("0", report.get("publication.coordinate.count"));
    }
  }

  private Path fixture(String release, String dropID) throws Exception
  {
    Path root = temporaryFolder.newFolder().toPath();
    Path bundles = root.resolve("bundles");
    Files.createDirectories(bundles);
    for (int i = 0; i < BUNDLES.length; i++)
    {
      String bundle = BUNDLES[i];
      String version = "1.0." + i;
      Path directory = bundles.resolve("org/eclipse/cdo/" + bundle + "/" + version);
      Files.createDirectories(directory);
      Files.writeString(directory.resolve(bundle + "-" + version + ".pom"), bundlePom(bundle, version, release, dropID), StandardCharsets.UTF_8);
    }

    Files.writeString(root.resolve("metadata.properties"),
        "cdo.project.release=" + release + "\n" + "cdo.drop.id=" + dropID + "\n" + "cdo.git.commit=0123456789abcdef0123456789abcdef01234567\n",
        StandardCharsets.UTF_8);
    Files.writeString(root.resolve("publishing.properties"), PUBLISHING_METADATA, StandardCharsets.UTF_8);
    Path drop = root.resolve("drop");
    Files.createDirectories(drop);
    Files.writeString(drop.resolve("web.properties"), "web.label=" + release + "\n", StandardCharsets.UTF_8);
    Files.writeString(drop.resolve("build-info.xml"), buildInfo(dropID), StandardCharsets.UTF_8);
    return root;
  }

  private Path classificationFixture(String release, String dropID) throws Exception
  {
    Path root = fixture(release, dropID);
    for (int i = 0; i < BUNDLES.length; i++)
    {
      String bundle = BUNDLES[i];
      String version = "1.0." + i;
      Path directory = root.resolve("bundles/org/eclipse/cdo/" + bundle + "/" + version);
      for (String suffix : new String[] { ".jar", "-sources.jar", "-javadoc.jar" })
      {
        Files.write(directory.resolve(bundle + "-" + version + suffix), (bundle + suffix).getBytes(StandardCharsets.UTF_8));
      }
    }

    generate(root, "bom-output");
    return root;
  }

  private Map<String, String> classify(Path root, CentralFixture central, boolean expectFailure) throws Exception
  {
    return classify(root, central.baseUrl(), expectFailure);
  }

  private Map<String, String> classify(Path root, String repository, boolean expectFailure) throws Exception
  {
    Path report = root.resolve("classification.properties");
    try
    {
      CDOAggregationTool.main(new String[] { "classify-central", root.resolve("bundles").toString(), root.resolve("bom-output").toString(),
          root.resolve("metadata.properties").toString(), report.toString(), repository });
      if (expectFailure)
      {
        fail("Expected Central classification to report a conflict or lookup error");
      }
    }
    catch (IllegalStateException expected)
    {
      if (!expectFailure)
      {
        throw expected;
      }
    }

    Properties properties = new Properties();
    try (InputStream input = Files.newInputStream(report))
    {
      properties.load(input);
    }

    Map<String, String> result = new LinkedHashMap<>();
    for (String name : properties.stringPropertyNames())
    {
      result.put(name, properties.getProperty(name));
    }

    return result;
  }

  private static String path(String artifact, String version, String suffix)
  {
    return "org/eclipse/cdo/" + artifact + "/" + version + "/" + artifact + "-" + version + suffix;
  }

  private static String buildInfo(String dropID)
  {
    return "<build qualifier=\"" + dropID + "\" revision=\"0123456789abcdef0123456789abcdef01234567\" branch=\"main\" eclipse=\"2026-09\""
        + " emf=\"2.42\" hudson=\"https://ci.eclipse.org/cdo/\" job=\"cdo\" number=\"1\" stream=\"4.36\" timestamp=\"2026-09-01T13:11:00Z\""
        + " train=\"2026-09\" trigger=\"test\" type=\"R\"/>\n";
  }

  private File generate(Path root, String outputName) throws Exception
  {
    File output = root.resolve(outputName).toFile();
    CDOAggregationTool.main(new String[] { "generate-bom", root.resolve("bundles").toString(), root.resolve("metadata.properties").toString(),
        root.resolve("publishing.properties").toString(), output.getPath(), root.resolve("drop").toString() });
    String release = Files.readAllLines(root.resolve("metadata.properties"), StandardCharsets.UTF_8).get(0).substring("cdo.project.release=".length());
    String normalized = release.split("\\.").length == 2 ? release + ".0" : release;
    return output.toPath().resolve("org/eclipse/cdo/org.eclipse.cdo.bom/" + normalized + "/org.eclipse.cdo.bom-" + normalized + ".pom").toFile();
  }

  private static String bundlePom(String artifact, String version, String release, String dropID)
  {
    return "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"><modelVersion>4.0.0</modelVersion><groupId>org.eclipse.cdo</groupId>" + "<artifactId>"
        + artifact + "</artifactId><version>" + version + "</version><packaging>jar</packaging><properties>" + "<cdo.drop.id>" + dropID
        + "</cdo.drop.id><cdo.project.release>" + release + "</cdo.project.release></properties>"
        + "<dependencies><dependency><groupId>org.example</groupId><artifactId>support</artifactId><version>1.0</version><scope>compile</scope>"
        + "</dependency></dependencies></project>";
  }

  private static Document parse(File file) throws Exception
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
    return factory.newDocumentBuilder().parse(file);
  }

  private static Element child(Element parent, String name)
  {
    NodeList elements = elements(parent, name);
    return elements.getLength() == 0 ? null : (Element)elements.item(0);
  }

  private static String childText(Element parent, String name)
  {
    Element element = child(parent, name);
    return element == null ? "" : element.getTextContent().trim();
  }

  private static NodeList elements(Element parent, String name)
  {
    return parent.getElementsByTagNameNS("http://maven.apache.org/POM/4.0.0", name);
  }

  private static int directElements(Element parent, String name)
  {
    int count = 0;
    for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling())
    {
      if (node instanceof Element && name.equals(node.getLocalName()))
      {
        count++;
      }
    }

    return count;
  }

  private static final class CentralFixture implements AutoCloseable
  {
    private final Map<String, byte[]> artifacts = new LinkedHashMap<>();

    private final Map<String, Integer> statuses = new LinkedHashMap<>();

    private final Map<String, byte[]> checksumOverrides = new LinkedHashMap<>();

    private final Set<String> missingChecksums = new LinkedHashSet<>();

    private final Set<String> truncatedArtifacts = new LinkedHashSet<>();

    private final HttpServer server;

    CentralFixture() throws IOException
    {
      server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
      server.createContext("/", this::handle);
      server.start();
    }

    String baseUrl()
    {
      return "http://127.0.0.1:" + server.getAddress().getPort() + "/";
    }

    void loadProposedArtifacts(Path root) throws IOException
    {
      for (int i = 0; i < BUNDLES.length; i++)
      {
        String bundle = BUNDLES[i];
        String version = "1.0." + i;
        Path directory = root.resolve("bundles/org/eclipse/cdo/" + bundle + "/" + version);
        String prefix = path(bundle, version, "");
        for (String suffix : new String[] { ".pom", ".jar", "-sources.jar", "-javadoc.jar" })
        {
          artifacts.put(prefix + suffix, Files.readAllBytes(directory.resolve(bundle + "-" + version + suffix)));
        }
      }

      String metadata = Files.readString(root.resolve("metadata.properties"), StandardCharsets.UTF_8);
      String release = Arrays.stream(metadata.split("\\R")).filter(line -> line.startsWith("cdo.project.release=")).findFirst().get()
          .substring("cdo.project.release=".length());
      String bomVersion = release.split("\\.").length == 2 ? release + ".0" : release;
      Path bomPom = root.resolve("bom-output/org/eclipse/cdo/org.eclipse.cdo.bom/" + bomVersion + "/org.eclipse.cdo.bom-" + bomVersion + ".pom");
      artifacts.put("org/eclipse/cdo/org.eclipse.cdo.bom/" + bomPom.getParent().getFileName() + "/" + bomPom.getFileName(), Files.readAllBytes(bomPom));
    }

    void replaceBundlePom(int index, String content)
    {
      String bundle = BUNDLES[index];
      String version = "1.0." + index;
      artifacts.put(path(bundle, version, ".pom"), content.getBytes(StandardCharsets.UTF_8));
    }

    void replaceBundleArtifact(int index, String suffix, byte[] bytes)
    {
      artifacts.put(path(BUNDLES[index], "1.0." + index, suffix), bytes);
    }

    void removeBundleArtifact(int index, String suffix)
    {
      artifacts.remove(path(BUNDLES[index], "1.0." + index, suffix));
    }

    void removeChecksum(String artifactPath)
    {
      missingChecksums.add(artifactPath + ".sha256");
    }

    void corruptChecksum(String artifactPath)
    {
      checksumOverrides.put(artifactPath + ".sha256", "0000000000000000000000000000000000000000000000000000000000000000".getBytes(StandardCharsets.US_ASCII));
    }

    void setArtifact(String path, byte[] bytes)
    {
      artifacts.put(path, bytes);
    }

    void setStatus(String path, int status)
    {
      statuses.put(path, status);
    }

    void truncate(String path)
    {
      truncatedArtifacts.add(path);
    }

    void replaceBomWithIncompatibleVersion()
    {
      for (String path : artifacts.keySet())
      {
        if (path.contains("org.eclipse.cdo.bom") && path.endsWith(".pom"))
        {
          artifacts.put(path, new String(artifacts.get(path), StandardCharsets.UTF_8).replace("<packaging>pom</packaging>", "<packaging>jar</packaging>")
              .getBytes(StandardCharsets.UTF_8));
          return;
        }
      }
    }

    @Override
    public void close()
    {
      server.stop(0);
    }

    private void handle(HttpExchange exchange) throws IOException
    {
      String path = exchange.getRequestURI().getPath().substring(1);
      int status = statuses.getOrDefault(path, 200);
      if (status != 200)
      {
        exchange.sendResponseHeaders(status, -1);
        exchange.close();
        return;
      }

      byte[] body = artifacts.get(path);
      if (path.endsWith(".sha256"))
      {
        if (missingChecksums.contains(path))
        {
          body = null;
        }
        else if (checksumOverrides.containsKey(path))
        {
          body = checksumOverrides.get(path);
        }
        else
        {
          byte[] artifact = artifacts.get(path.substring(0, path.length() - ".sha256".length()));
          body = artifact == null ? null : hex(sha256(artifact)).getBytes(StandardCharsets.US_ASCII);
        }
      }

      if (body == null)
      {
        exchange.sendResponseHeaders(404, -1);
        exchange.close();
        return;
      }

      if (truncatedArtifacts.contains(path))
      {
        exchange.sendResponseHeaders(200, body.length + 10L);
        try (OutputStream output = exchange.getResponseBody())
        {
          output.write(body);
        }

        return;
      }

      exchange.sendResponseHeaders(200, body.length);
      try (OutputStream output = exchange.getResponseBody())
      {
        output.write(body);
      }
    }

    private static byte[] sha256(byte[] bytes)
    {
      try
      {
        return MessageDigest.getInstance("SHA-256").digest(bytes);
      }
      catch (Exception e)
      {
        throw new AssertionError(e);
      }
    }

    private static String hex(byte[] bytes)
    {
      StringBuilder result = new StringBuilder(bytes.length * 2);
      for (byte value : bytes)
      {
        result.append(String.format("%02x", value & 0xff));
      }

      return result.toString();
    }
  }
}
