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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeNoException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.tools.ToolProvider;
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
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Verifies deterministic generation of the standalone CDO project-release BOM.
 *
 * @author Eike Stepper
 */
public class CDOAggregationToolBomTest
{
  private static final String[] BUNDLES = { "org.eclipse.net4j.util", "org.eclipse.net4j", "org.eclipse.net4j.tcp", "org.eclipse.net4j.db",
      "org.eclipse.net4j.db.jdbc", "org.eclipse.net4j.db.h2", "org.eclipse.emf.cdo.common", "org.eclipse.emf.cdo", "org.eclipse.emf.cdo.net4j",
      "org.eclipse.emf.cdo.server", "org.eclipse.emf.cdo.server.net4j", "org.eclipse.emf.cdo.server.db", "org.eclipse.net4j.jvm", "org.eclipse.net4j.ws",
      "org.eclipse.net4j.ws.jetty.server", "org.eclipse.net4j.db.postgresql", "org.eclipse.emf.cdo.server.embedded", "org.eclipse.emf.cdo.expressions",
      "org.eclipse.emf.cdo.security", "org.eclipse.emf.cdo.server.security", "org.eclipse.emf.cdo.examples.company" };

  private static final String[] BOM_436_VERSIONS = { "3.30.0", "4.22.0", "4.5.0", "4.14.0", "4.5.0", "4.6.0", "4.28.0", "4.30.0", "4.9.0", "4.26.0", "4.9.0",
      "4.15.0" };

  private static final String[] FIXTURE_VERSIONS = { "3.30.0", "4.22.0", "4.5.0", "4.14.0", "4.5.0", "4.6.0", "4.28.0", "4.30.0", "4.9.0", "4.26.0", "4.9.0",
      "4.15.0", "4.4.1", "2.0.0", "1.0.0", "4.5.1", "4.11.1", "4.5.0", "4.8.0", "4.13.0", "4.1.4" };

  private static final String PUBLISHING_METADATA = "project.url=https://eclipse.dev/cdo/\n" + "organization.name=Eclipse Foundation\n"
      + "organization.url=https://www.eclipse.org\n" + "license.name=EPL-2.0\n" + "license.url=https://www.eclipse.org/legal/epl-2.0/\n"
      + "license.distribution=repo\n" + "developers.url=https://github.com/eclipse-cdo/cdo/graphs/contributors\n" + "issue.system=GitHub\n"
      + "issue.url=https://github.com/eclipse-cdo/cdo/issues\n" + "scm.connection=scm:git:https://github.com/eclipse-cdo/cdo.git\n"
      + "scm.developerConnection=scm:git:https://github.com/eclipse-cdo/cdo.git\n";

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void checkedInAggregationModelMapsInternalAndJettyBundleRequirements() throws Exception
  {
    File classes = new File(CDOAggregationTool.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    File model = new File(new File(classes.getParentFile(), "central"), "CDO.aggr");
    assertTrue("Missing aggregation model: " + model, model.isFile());

    Document document = parse(model);
    NodeList mappings = document.getElementsByTagName("mavenDependencyMapping");
    Set<String> mappingKeys = new LinkedHashSet<>();
    String packagePattern = null;
    for (int i = 0; i < mappings.getLength(); i++)
    {
      Element mapping = (Element)mappings.item(i);
      String namespace = mapping.getAttribute("namespacePattern");
      String name = mapping.getAttribute("namePattern");
      String groupId = mapping.getAttribute("groupId");
      String artifactId = mapping.getAttribute("artifactId");
      mappingKeys.add(namespace + "|" + name + "|" + groupId + "|" + artifactId);
      if ("(org\\.eclipse\\.(emf\\.cdo|net4j).*)".equals(name))
      {
        assertEquals("(.*)", mapping.getAttribute("versionRangePattern"));
        assertEquals("$1", mapping.getAttribute("versionRangeTemplate"));
      }

      if ("java\\.package".equals(namespace))
      {
        packagePattern = name;
      }
    }

    assertTrue(mappingKeys.contains("osgi\\.bundle|(org\\.eclipse\\.(emf\\.cdo|net4j).*)|org.eclipse.cdo|$1"));
    assertTrue(mappingKeys.contains("osgi\\.bundle|org\\.eclipse\\.jetty\\.server|org.eclipse.jetty|jetty-server"));
    assertTrue(mappingKeys.contains("osgi\\.bundle|org\\.eclipse\\.jetty\\.http|org\\.eclipse\\.jetty\\.util|org\\.eclipse\\.jetty\\.websocket\\.api|!|!"));
    assertTrue(mappingKeys.contains("osgi\\.bundle|org\\.eclipse\\.jetty\\.websocket\\.server|org.eclipse.jetty.websocket|jetty-websocket-jetty-server"));
    assertTrue(mappingKeys.contains("java\\.package|org\\.postgresql(\\..*)?|org.postgresql|postgresql"));
    assertNotNull(packagePattern);
    assertTrue("The package mapping must retain PostgreSQL's optional OSGi requirements", "org.postgresql".matches(packagePattern));
  }

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
    assertEquals(BUNDLES.length, elements(child(management, "dependencies"), "dependency").getLength());
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
  public void approvedPublicationInventoryContainsExactlyTwentyOneBundles()
  {
    assertEquals(21, BUNDLES.length);
    assertEquals(Arrays.asList(BUNDLES), CDOAggregationTool.approvedBundles());
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
  public void preparesHistorical436BomOnlyStagingFromDropInventory() throws Exception
  {
    Path root = fixture("4.36", "R20260901-1311");
    Path repository = root.resolve("bom-repository");
    Path staging = root.resolve("bom-staging");
    CDOAggregationTool.main(new String[] { "generate-bom-436", root.resolve("metadata.properties").toString(), root.resolve("publishing.properties").toString(),
        repository.toString(), root.resolve("drop").toString() });
    CDOAggregationTool.main(new String[] { "stage-bom-436", repository.toString(), root.resolve("metadata.properties").toString(),
        root.resolve("publishing.properties").toString(), root.resolve("drop").toString(), staging.toString() });

    try
    {
      CDOAggregationTool.main(new String[] { "generate-bom-436", root.resolve("metadata.properties").toString(),
          root.resolve("publishing.properties").toString(), repository.toString(), root.resolve("drop").toString() });
      fail("Expected historical BOM generation to refuse an existing coordinate");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("BOM output coordinate already exists"));
    }

    Path versionDirectory = staging.resolve("org/eclipse/cdo/org.eclipse.cdo.bom/4.36.0");
    File pom = versionDirectory.resolve("org.eclipse.cdo.bom-4.36.0.pom").toFile();
    assertTrue(Files.isRegularFile(pom.toPath()));
    Element project = parse(pom).getDocumentElement();
    assertEquals("org.eclipse.cdo", childText(project, "groupId"));
    assertEquals("org.eclipse.cdo.bom", childText(project, "artifactId"));
    assertEquals("4.36.0", childText(project, "version"));
    assertEquals("pom", childText(project, "packaging"));
    assertEquals("4.36", childText(child(project, "properties"), "cdo.project.release"));
    assertEquals("R20260901-1311", childText(child(project, "properties"), "cdo.drop.id"));
    assertEquals(0, directElements(project, "dependencies"));
    NodeList managed = elements(child(child(project, "dependencyManagement"), "dependencies"), "dependency");
    assertEquals(BOM_436_VERSIONS.length, managed.getLength());
    for (int i = 0; i < BOM_436_VERSIONS.length; i++)
    {
      Element dependency = (Element)managed.item(i);
      assertEquals("org.eclipse.cdo", childText(dependency, "groupId"));
      assertEquals(BUNDLES[i], childText(dependency, "artifactId"));
      assertEquals(BOM_436_VERSIONS[i], childText(dependency, "version"));
    }

    Path inventory = deriveInventory(root);
    Properties inventoryProperties = new Properties();
    try (InputStream input = Files.newInputStream(inventory))
    {
      inventoryProperties.load(input);
    }

    assertEquals("4.36", inventoryProperties.getProperty("project.release"));
    assertEquals("4.36.0", inventoryProperties.getProperty("bom.version"));
    assertEquals("DROP", inventoryProperties.getProperty("cdo.project.release.source"));
    assertEquals("R20260901-1311", inventoryProperties.getProperty("drop.id"));
    assertEquals(BOM_436_VERSIONS[0], inventoryProperties.getProperty("artifact." + BUNDLES[0]));
    assertEquals(BOM_436_VERSIONS[0] + ".v20260901-1311", inventoryProperties.getProperty("osgi." + BUNDLES[0]));
    assertEquals("0123456789abcdef0123456789abcdef01234567", inventoryProperties.getProperty("cdo.git.commit"));
    assertEquals(21 + BUNDLES.length * 2, inventoryProperties.size());
    Set<String> unsignedFiles = new LinkedHashSet<>();
    try (Stream<Path> paths = Files.walk(staging))
    {
      paths.filter(Files::isRegularFile).map(staging::relativize).map(Path::toString).map(path -> path.replace(File.separatorChar, '/'))
          .forEach(unsignedFiles::add);
    }

    String pomPath = "org/eclipse/cdo/org.eclipse.cdo.bom/4.36.0/org.eclipse.cdo.bom-4.36.0.pom";
    Set<String> expectedSignedZipFiles = new LinkedHashSet<>();
    expectedSignedZipFiles.add(pomPath);
    expectedSignedZipFiles.add(pomPath + ".asc");
    expectedSignedZipFiles.add(pomPath + ".md5");
    expectedSignedZipFiles.add(pomPath + ".sha1");
    expectedSignedZipFiles.add(pomPath + ".sha256");
    expectedSignedZipFiles.add(pomPath + ".sha512");
    Set<String> expectedUnsignedFiles = new LinkedHashSet<>(expectedSignedZipFiles);
    expectedUnsignedFiles.remove(pomPath + ".asc");
    assertEquals(expectedUnsignedFiles, unsignedFiles);
    assertEquals(6, expectedSignedZipFiles.size());

    try (Stream<Path> paths = Files.walk(staging))
    {
      assertEquals(0, paths.filter(path -> path.toString().endsWith(".jar") || path.toString().endsWith(".asc")).count());
    }
  }

  @Test
  public void rejectsMissingDuplicateMalformedAndUnsupportedReleaseDropBundleEntries() throws Exception
  {
    Path root = fixture("4.36", "R20260901-1311");
    Path index = root.resolve("drop/index.xml");
    String valid = Files.readString(index, StandardCharsets.UTF_8);
    String[] invalidIndexes = { valid.replaceFirst("<element name=\"" + BUNDLES[0] + "\" type=\"osgi.bundle\"[^>]*/>", ""),
        valid.replace("</index>", "<element name=\"" + BUNDLES[0] + "\" type=\"osgi.bundle\" version=\"9.9.9.v2\"/></index>"),
        valid.replace(BOM_436_VERSIONS[0] + ".v20260901-1311", "1.2.bad.qualifier"),
        valid.replace(BOM_436_VERSIONS[0] + ".v20260901-1311", "1.2.3.4.qualifier"),
        valid.replace(BOM_436_VERSIONS[0] + ".v20260901-1311", "01.2.3.qualifier") };

    for (String invalidIndex : invalidIndexes)
    {
      Files.writeString(index, invalidIndex, StandardCharsets.UTF_8);
      try
      {
        deriveInventory(root);
        fail("Expected malformed release-drop inventory to be rejected");
      }
      catch (IllegalArgumentException expected)
      {
        assertTrue(expected.getMessage().contains("Expected exactly one") || expected.getMessage().contains("version"));
      }
    }

    Files.writeString(index, valid, StandardCharsets.UTF_8);
  }

  @Test
  public void preservesTheCbiMavenReleaseTrickAndRejectsDropIdentityMismatches() throws Exception
  {
    Path root = fixture("4.36", "R20260901-1311");
    Path index = root.resolve("drop/index.xml");
    String valid = Files.readString(index, StandardCharsets.UTF_8);
    Files.writeString(index, valid.replace(BOM_436_VERSIONS[0] + ".v20260901-1311", "1.2.3.4-r"), StandardCharsets.UTF_8);
    Properties inventory = readProperties(deriveInventory(root));
    assertEquals("1.2.3.4-r", inventory.getProperty("artifact." + BUNDLES[0]));

    try
    {
      CDOAggregationTool.main(
          new String[] { "derive-release-inventory", root.resolve("drop").toString(), "R20260901-9999", root.resolve("wrong-drop.properties").toString() });
      fail("Expected selected drop identity mismatch to fail");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("does not match drop build-info qualifier"));
    }

    Files.writeString(index, valid, StandardCharsets.UTF_8);
    Files.writeString(root.resolve("metadata.properties"),
        "cdo.project.release=4.37\ncdo.drop.id=R20260901-1311\ncdo.git.commit=0123456789abcdef0123456789abcdef01234567\n", StandardCharsets.UTF_8);
    try
    {
      CDOAggregationTool.main(new String[] { "generate-bom-436", root.resolve("metadata.properties").toString(),
          root.resolve("publishing.properties").toString(), root.resolve("mismatch-output").toString(), root.resolve("drop").toString() });
      fail("Expected build metadata release mismatch to fail");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("metadata does not match"));
    }
  }

  @Test
  public void bomCoordinateAbsenceCheckRejectsExistingAndAmbiguousCentralResponses() throws Exception
  {
    Path root = fixture("4.36", "R20260901-1311");
    try (CentralFixture central = new CentralFixture())
    {
      CDOAggregationTool
          .main(new String[] { "check-bom-central", root.resolve("metadata.properties").toString(), root.resolve("drop").toString(), central.baseUrl() });
      String pom = "org/eclipse/cdo/org.eclipse.cdo.bom/4.36.0/org.eclipse.cdo.bom-4.36.0.pom";
      central.setArtifact(pom, "already published".getBytes(StandardCharsets.UTF_8));
      expectBomCentralFailure(root, central, "already exists");

      central.setStatus(pom, 429);
      expectBomCentralFailure(root, central, "HTTP 429");
    }
  }

  @Test
  public void rejectsASelectedDropOutsideThePinned436Retrofit() throws Exception
  {
    Path root = fixture("4.35", "R20260831-1311");
    try
    {
      CDOAggregationTool.main(new String[] { "generate-bom-436", root.resolve("metadata.properties").toString(),
          root.resolve("publishing.properties").toString(), root.resolve("bom-repository").toString(), root.resolve("drop").toString() });
      fail("Expected a non-4.36 release/drop to be rejected");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("Selected drop must be CDO release 4.36"));
    }
  }

  @Test
  public void rejectsMissingAndUnexpectedFilesAndInvalidChecksumsBeforeSignatureVerification() throws Exception
  {
    Path root = fixture("4.36", "R20260901-1311");
    Path repository = root.resolve("bom-repository");
    Path staging = root.resolve("bom-staging");
    Path signedRepository = root.resolve("signed-repository");
    Path zip = root.resolve("deployment.zip");
    Path publicKey = root.resolve("public-key.gpg");
    Files.writeString(root.resolve("metadata.properties"), "cdo.build.type=R\n", StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    Files.createDirectories(signedRepository);
    Files.write(zip, new byte[] { 1 });
    Files.write(publicKey, new byte[] { 1 });
    CDOAggregationTool.main(new String[] { "generate-bom-436", root.resolve("metadata.properties").toString(), root.resolve("publishing.properties").toString(),
        repository.toString(), root.resolve("drop").toString() });
    CDOAggregationTool.main(new String[] { "stage-bom-436", repository.toString(), root.resolve("metadata.properties").toString(),
        root.resolve("publishing.properties").toString(), root.resolve("drop").toString(), staging.toString() });

    String base = "org/eclipse/cdo/org.eclipse.cdo.bom/4.36.0/org.eclipse.cdo.bom-4.36.0.pom";
    Path signature = staging.resolve(base + ".asc");
    String[] arguments = { "verify-bom-package", staging.toString(), signedRepository.toString(), zip.toString(),
        root.resolve("metadata.properties").toString(), root.resolve("publishing.properties").toString(), root.resolve("drop").toString(), "A".repeat(40),
        publicKey.toString(), "gpg-is-not-reached", root.resolve("summary.properties").toString() };
    expectBomPackageValidationFailure(arguments, "Missing, malformed, or non-armored BOM signature");

    Files.writeString(signature, "-----BEGIN PGP SIGNATURE-----\nfixture\n", StandardCharsets.US_ASCII);
    Path sha1 = staging.resolve(base + ".sha1");
    byte[] checksum = Files.readAllBytes(sha1);
    Files.delete(sha1);
    expectBomPackageValidationFailure(arguments, "Missing or invalid BOM checksum");
    Files.write(sha1, checksum);

    Path sha256 = staging.resolve(base + ".sha256");
    byte[] sha256Bytes = Files.readAllBytes(sha256);
    Files.writeString(sha256, "0".repeat(64), StandardCharsets.US_ASCII);
    expectBomPackageValidationFailure(arguments, "Missing or invalid BOM checksum");
    Files.write(sha256, sha256Bytes);

    Path unexpected = staging.resolve("org/eclipse/cdo/org.eclipse.cdo.bom/4.36.0/org.eclipse.cdo.bom-4.36.0.jar");
    Files.writeString(unexpected, "BOM packages have no JARs", StandardCharsets.UTF_8);
    expectBomPackageValidationFailure(arguments, "BOM-only deployment inventory mismatch");
  }

  @Test
  public void publicationScopeReportsTheFirstMissingOrUnexpectedSignatureFailure() throws Exception
  {
    Path root = temporaryFolder.newFolder().toPath();
    Path staging = root.resolve("staging");
    Path metadata = root.resolve("metadata.properties");
    Files.writeString(metadata,
        "cdo.build.type=R\ncdo.build.stream=4.x\ncdo.project.release=4.37\ncdo.project.release.source=DROP\ncdo.drop.id=R20260901-1311\n",
        StandardCharsets.UTF_8);
    createPublicationStaging(staging, true);

    String[] validArguments = { "publish-check", staging.toString(), metadata.toString(), "PACKAGE", "SIGNED" };
    CDOAggregationTool.main(validArguments);

    Path missingSignature = staging.resolve(path(BUNDLES[0], "1.0.0", ".pom") + ".asc");
    byte[] signature = Files.readAllBytes(missingSignature);
    Files.delete(missingSignature);
    expectPublicationScopeFailure(validArguments, "PACKAGE requires a non-empty signature");
    Files.write(missingSignature, signature);

    Path unexpectedSignature = staging.resolve(path(BUNDLES[0], "1.0.0", ".pom") + ".unexpected.asc");
    Files.writeString(unexpectedSignature, "-----BEGIN PGP SIGNATURE-----\nfixture\n", StandardCharsets.US_ASCII);
    expectPublicationScopeFailure(validArguments, "Unexpected file in publication staging");
    Files.delete(unexpectedSignature);

    Path missingArtifact = staging.resolve(path(BUNDLES[0], "1.0.0", ".jar"));
    Files.delete(missingArtifact);
    expectPublicationScopeFailure(validArguments, "Missing or empty staged Maven artifact");
  }

  @Test
  public void publicationScopeCheckUsesSelectedExamplesVersionFromBuildMetadata() throws Exception
  {
    Path root = temporaryFolder.newFolder().toPath();
    Path staging = Files.createDirectories(root.resolve("staging"));
    Path metadata = root.resolve("metadata.properties");
    Files.writeString(metadata, "cdo.project.release=4.37\ncdo.drop.id=R20260901-1311\ncdo.git.commit=deadbeef\nexamples.distribution.version=1.0.16\n",
        StandardCharsets.UTF_8);
    createPublicationStaging(staging, true, "1.0.16");

    CDOAggregationTool.main(new String[] { "publication-scope-check", staging.toString(), metadata.toString() });
  }

  @Test
  public void regularPackageRequiresAndValidatesTheProjectReleaseBom() throws Exception
  {
    Path root = temporaryFolder.newFolder().toPath();
    Path staging = root.resolve("staging");
    Path metadata = root.resolve("metadata.properties");
    Files.writeString(metadata,
        "cdo.build.type=R\ncdo.build.stream=4.x\ncdo.project.release=4.37\ncdo.project.release.source=OVERRIDE\ncdo.drop.id=R20260901-1311\n",
        StandardCharsets.UTF_8);
    createPublicationStaging(staging, false);
    String[] arguments = { "publish-check", staging.toString(), metadata.toString(), "PACKAGE", "" };
    expectPublicationScopeFailure(arguments, "PACKAGE staging must contain the project-release BOM");

    createRegularBomFixture(staging, "4.37", "OVERRIDE", "R20260901-1311");
    CDOAggregationTool.main(arguments);
    Properties summary = new Properties();
    try (InputStream input = Files.newInputStream(staging.resolve("maven-publishing-summary.properties")))
    {
      summary.load(input);
    }

    assertEquals(Integer.toString(BUNDLES.length * 4 + 3), summary.getProperty("staging.artifacts"));

    Path bom = staging.resolve("org/eclipse/cdo/org.eclipse.cdo.bom/4.37.0/org.eclipse.cdo.bom-4.37.0.pom");
    String validPom = Files.readString(bom);
    Files.writeString(bom, validPom.replace("<version>1.0.0</version>", "<version>1.0.1</version>"), StandardCharsets.UTF_8);
    writeChecksums(bom);
    expectPublicationScopeFailure(arguments, "Generated BOM member mismatch");

    Files.writeString(bom, validPom.replace("<cdo.drop.id>R20260901-1311</cdo.drop.id>", "<cdo.drop.id>S20260901-1311</cdo.drop.id>"), StandardCharsets.UTF_8);
    writeChecksums(bom);
    expectPublicationScopeFailure(arguments, "Staged project-release BOM has inconsistent coordinate, release, or drop provenance");
  }

  @Test
  public void regularBomGenerationUsesNormalizedProjectReleaseAndAllSelectedBundleVersions() throws Exception
  {
    Path root = temporaryFolder.newFolder().toPath();
    Path bundles = Files.createDirectory(root.resolve("bundles"));
    Path output = root.resolve("bom-repository");
    Path metadata = root.resolve("metadata.properties");
    Path publishing = root.resolve("publishing.properties");
    Path drop = Files.createDirectory(root.resolve("drop"));
    Files.writeString(metadata,
        "cdo.build.type=R\ncdo.project.release=4.37\ncdo.project.release.source=DROP\ncdo.drop.id=R20260901-1311\ncdo.git.commit=0123456789abcdef0123456789abcdef01234567\n",
        StandardCharsets.UTF_8);
    Files.writeString(publishing, PUBLISHING_METADATA, StandardCharsets.UTF_8);
    Files.writeString(drop.resolve("web.properties"), "web.label=4.37\n", StandardCharsets.UTF_8);
    Files.writeString(drop.resolve("build-info.xml"), "<build qualifier=\"R20260901-1311\" type=\"R\"/>", StandardCharsets.UTF_8);

    for (int i = 0; i < BUNDLES.length; i++)
    {
      String bundle = BUNDLES[i];
      String version = FIXTURE_VERSIONS[i];
      Path versionDirectory = bundles.resolve("org/eclipse/cdo/" + bundle + "/" + version);
      Files.createDirectories(versionDirectory);
      Files.writeString(versionDirectory.resolve(bundle + "-" + version + ".pom"),
          "<project><groupId>org.eclipse.cdo</groupId><artifactId>" + bundle + "</artifactId><version>" + version + "</version></project>",
          StandardCharsets.UTF_8);
    }

    CDOAggregationTool
        .main(new String[] { "generate-bom", bundles.toString(), metadata.toString(), publishing.toString(), output.toString(), drop.toString() });

    Path bomPom = output.resolve("org/eclipse/cdo/org.eclipse.cdo.bom/4.37.0/org.eclipse.cdo.bom-4.37.0.pom");
    Document document = parse(bomPom.toFile());
    Element project = document.getDocumentElement();
    assertEquals("4.37.0", childText(project, "version"));
    assertEquals("4.37", childText(child(project, "properties"), "cdo.project.release"));
    assertEquals("DROP", childText(child(project, "properties"), "cdo.project.release.source"));
    assertEquals("R20260901-1311", childText(child(project, "properties"), "cdo.drop.id"));
    NodeList dependencies = document.getElementsByTagNameNS("*", "dependency");
    assertEquals(BUNDLES.length, dependencies.getLength());
    for (int i = 0; i < BUNDLES.length; i++)
    {
      Element dependency = (Element)dependencies.item(i);
      assertEquals(BUNDLES[i], childText(dependency, "artifactId"));
      assertEquals(FIXTURE_VERSIONS[i], childText(dependency, "version"));
    }
  }

  @Test
  public void standaloneExamplesPreparationRejectsDeploymentWithoutReleaseBom() throws Exception
  {
    Path root = temporaryFolder.newFolder().toPath();
    Path staging = root.resolve("staging");
    Path metadata = root.resolve("metadata.properties");
    Path deploymentZip = root.resolve("deployment.zip");
    Path testDirectory = root.resolve("standalone-test");
    Files.writeString(metadata,
        "cdo.build.type=R\ncdo.build.stream=4.x\ncdo.project.release=4.37\ncdo.project.release.source=DROP\ncdo.drop.id=R20260901-1311\n",
        StandardCharsets.UTF_8);
    createPublicationStaging(staging, false);
    createDeploymentZip(staging, deploymentZip);

    try
    {
      CDOAggregationTool.main(new String[] { "prepare-standalone-examples", deploymentZip.toString(), testDirectory.toString(), metadata.toString() });
      fail("Expected standalone examples preparation to reject a deployment without its exact release BOM");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("project-release BOM in final deployment ZIP"));
    }
  }

  @Test
  public void standaloneExamplesPreparationAcceptsPortableBomPropertyReference() throws Exception
  {
    Path root = temporaryFolder.newFolder().toPath();
    Path staging = root.resolve("staging");
    Path metadata = root.resolve("metadata.properties");
    Path deploymentZip = root.resolve("deployment.zip");
    Path testDirectory = root.resolve("standalone-test");
    Files.writeString(metadata,
        "cdo.build.type=R\ncdo.build.stream=4.x\ncdo.project.release=4.37\ncdo.project.release.source=DROP\ncdo.drop.id=R20260901-1311\n",
        StandardCharsets.UTF_8);
    createPublicationStaging(staging, true);

    File classes = new File(CDOAggregationTool.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    Path central = new File(classes.getParentFile(), "central").toPath();
    Path distribution = central.resolve("examples/distribution").toAbsolutePath();
    Path workspace = distribution.resolve("../../../../..").normalize();
    String examplesVersion = examplesDistributionVersion();
    Path examplesZip = staging
        .resolve("org/eclipse/cdo/cdo-examples-distribution/" + examplesVersion + "/cdo-examples-distribution-" + examplesVersion + "-examples.zip");
    createExamplesZip(workspace, distribution, examplesZip, null, false, false, "4.37.0");
    createDeploymentZip(staging, deploymentZip);

    CDOAggregationTool.main(new String[] { "prepare-standalone-examples", deploymentZip.toString(), testDirectory.toString(), metadata.toString() });

    String packagedPom = Files.readString(testDirectory.resolve("examples/pom.xml"), StandardCharsets.UTF_8);
    assertTrue(packagedPom.contains("<version>${cdo.project.release}</version>"));
    assertTrue(packagedPom.contains("<cdo.project.release>4.37.0</cdo.project.release>"));
  }

  @Test
  public void standaloneExamplesArtifactProvenanceMustComeFromDeploymentBundle() throws Exception
  {
    Path root = temporaryFolder.newFolder().toPath();
    Path validRepository = createStandaloneProvenanceFixture(root.resolve("valid"), "cdo-deployment-bundle", "cdo-deployment-bundle", "cdo-deployment-bundle",
        true, true);
    CDOAggregationTool.main(provenanceArguments(validRepository, root.resolve("valid/examples")));

    Path centralRepository = createStandaloneProvenanceFixture(root.resolve("central"), "central", "central", "cdo-deployment-bundle", true, true);
    expectProvenanceFailure(centralRepository, root.resolve("central/examples"), "central", "cdo-deployment-bundle");

    Path mixedRepository = createStandaloneProvenanceFixture(root.resolve("mixed"), "cdo-deployment-bundle", "central", "cdo-deployment-bundle", true, true);
    expectProvenanceFailure(mixedRepository, root.resolve("mixed/examples"), "central", "cdo-deployment-bundle");

    Path missingTrackingRepository = createStandaloneProvenanceFixture(root.resolve("missing-tracking"), "cdo-deployment-bundle", "cdo-deployment-bundle",
        "cdo-deployment-bundle", true, true);
    Files.delete(missingTrackingRepository.resolve("org/eclipse/cdo/org.eclipse.emf.cdo.server.db/4.37.0/_remote.repositories"));
    expectProvenanceFailure(missingTrackingRepository, root.resolve("missing-tracking/examples"), "origin", "missing");

    Path locallyInstalledRepository = createStandaloneProvenanceFixture(root.resolve("locally-installed"), "", "", "cdo-deployment-bundle", true, true);
    expectProvenanceFailure(locallyInstalledRepository, root.resolve("locally-installed/examples"), "locally installed", "cdo-deployment-bundle");

    Path missingBomRepository = createStandaloneProvenanceFixture(root.resolve("missing-bom"), "cdo-deployment-bundle", "cdo-deployment-bundle",
        "cdo-deployment-bundle", true, false);
    expectProvenanceFailure(missingBomRepository, root.resolve("missing-bom/examples"), "org.eclipse.cdo.bom:4.37.0", "cdo-deployment-bundle");

    Path incompleteRepository = createStandaloneProvenanceFixture(root.resolve("incomplete"), "cdo-deployment-bundle", "cdo-deployment-bundle",
        "cdo-deployment-bundle", false, true);
    expectProvenanceFailure(incompleteRepository, root.resolve("incomplete/examples"), "org.eclipse.emf.cdo.server.db", "cdo-deployment-bundle");

    Path emptyRepository = Files.createDirectories(root.resolve("empty/repository"));
    createStandaloneModulePoms(root.resolve("empty/examples"));
    expectProvenanceFailure(emptyRepository, root.resolve("empty/examples"), "No CDO artifacts", "cdo-deployment-bundle");
  }

  @Test
  public void standaloneExamplesRepositoryConfigurationPrefersDeploymentBundleWhenCentralHasSameVersion() throws Exception
  {
    File classes = new File(CDOAggregationTool.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    Path centralDirectory = new File(classes.getParentFile(), "central").toPath();
    Element settings = parse(centralDirectory.resolve("standalone-examples-settings.xml").toFile()).getDocumentElement();
    Element mirror = (Element)settings.getElementsByTagNameNS("*", "mirror").item(0);
    assertEquals("central-only", genericChildText(mirror, "id"));
    assertEquals("*,!cdo-deployment-bundle,!central-only", genericChildText(mirror, "mirrorOf"));
    assertEquals("${env.CDO_CENTRAL_REPOSITORY}", genericChildText(mirror, "url"));
    NodeList settingsProfiles = settings.getElementsByTagNameNS("*", "profile");
    assertEquals(1, settingsProfiles.getLength());
    Element settingsProfile = (Element)settingsProfiles.item(0);
    assertEquals("standalone-examples-repositories", genericChildText(settingsProfile, "id"));
    NodeList settingsRepositories = settingsProfile.getElementsByTagNameNS("*", "repository");
    assertEquals("cdo-deployment-bundle", genericChildText((Element)settingsRepositories.item(0), "id"));
    assertEquals("${env.CDO_DEPLOYMENT_REPOSITORY}", genericChildText((Element)settingsRepositories.item(0), "url"));
    assertEquals("central", genericChildText((Element)settingsRepositories.item(1), "id"));
    assertEquals("${env.CDO_CENTRAL_REPOSITORY}", genericChildText((Element)settingsRepositories.item(1), "url"));
    Element pluginRepository = (Element)settingsProfile.getElementsByTagNameNS("*", "pluginRepository").item(0);
    assertEquals("central-only", genericChildText(pluginRepository, "id"));
    assertEquals("https://repo.maven.apache.org/maven2", genericChildText(pluginRepository, "url"));
    assertEquals(1, settings.getElementsByTagNameNS("*", "activeProfile").getLength());
    assertEquals("standalone-examples-repositories", settings.getElementsByTagNameNS("*", "activeProfile").item(0).getTextContent().trim());

    Element portablePom = parse(centralDirectory.resolve("examples/distribution/portable-pom.xml").toFile()).getDocumentElement();
    Element profiles = child(portablePom, "profiles");
    NodeList profileNodes = profiles.getElementsByTagNameNS("*", "profile");
    Element deploymentProfile = null;
    for (int i = 0; i < profileNodes.getLength(); i++)
    {
      Element profile = (Element)profileNodes.item(i);
      if ("cdo-deployment-repository".equals(childText(profile, "id")))
      {
        deploymentProfile = profile;
      }
    }

    assertNotNull(deploymentProfile);
    NodeList configuredRepositories = child(deploymentProfile, "repositories").getElementsByTagNameNS("*", "repository");
    assertEquals(2, configuredRepositories.getLength());
    Element repository = (Element)configuredRepositories.item(0);
    assertEquals("cdo-deployment-bundle", childText(repository, "id"));
    assertEquals("${cdo.deployment.repository}", childText(repository, "url"));
    Element centralRepository = (Element)configuredRepositories.item(1);
    assertEquals("central", childText(centralRepository, "id"));
    assertEquals("https://repo.maven.apache.org/maven2", childText(centralRepository, "url"));

    Document build = parse(centralDirectory.resolve("build.xml").toFile());
    NodeList targets = build.getElementsByTagName("target");
    Element verifyTarget = null;
    for (int i = 0; i < targets.getLength(); i++)
    {
      Element target = (Element)targets.item(i);
      if ("verify-standalone-examples".equals(target.getAttribute("name")))
      {
        verifyTarget = target;
      }
    }

    assertNotNull(verifyTarget);
    NodeList arguments = verifyTarget.getElementsByTagName("arg");
    StringBuilder targetXml = new StringBuilder();
    for (int i = 0; i < arguments.getLength(); i++)
    {
      targetXml.append(((Element)arguments.item(i)).getAttribute("value")).append('\n');
    }

    assertTrue(targetXml.toString(), targetXml.toString().contains("standalone-examples-settings.xml"));
    assertTrue(targetXml.toString(), targetXml.toString().contains("-Dmaven.repo.local=${consumer.examples.maven.repository}"));
    assertTrue(targetXml.toString(), targetXml.toString().contains("-Dcdo.deployment.repository=${consumer.examples.repository.url}"));
    NodeList environment = verifyTarget.getElementsByTagName("env");
    Map<String, String> environmentValues = new LinkedHashMap<>();
    for (int i = 0; i < environment.getLength(); i++)
    {
      Element variable = (Element)environment.item(i);
      environmentValues.put(variable.getAttribute("key"), variable.getAttribute("value"));
    }

    assertEquals("${consumer.examples.repository.url}", environmentValues.get("CDO_DEPLOYMENT_REPOSITORY"));
    assertEquals("${central.repository.url}", environmentValues.get("CDO_CENTRAL_REPOSITORY"));

    for (String module : new String[] { "embedded-h2", "remote-server", "remote-client" })
    {
      Element modulePom = parse(centralDirectory.resolve("examples/" + module + "/pom.xml").toFile()).getDocumentElement();
      Element moduleProfiles = child(modulePom, "profiles");
      NodeList moduleProfileNodes = moduleProfiles.getElementsByTagNameNS("*", "profile");
      Element checkoutProfile = null;
      for (int i = 0; i < moduleProfileNodes.getLength(); i++)
      {
        Element profile = (Element)moduleProfileNodes.item(i);
        if ("checkout-source-copy".equals(childText(profile, "id")))
        {
          checkoutProfile = profile;
        }
      }

      assertNotNull(checkoutProfile);
      NodeList plugins = child(child(checkoutProfile, "build"), "plugins").getElementsByTagNameNS("*", "plugin");
      Map<String, String> pluginVersions = new LinkedHashMap<>();
      for (int i = 0; i < plugins.getLength(); i++)
      {
        Element plugin = (Element)plugins.item(i);
        pluginVersions.put(childText(plugin, "artifactId"), childText(plugin, "version"));
      }

      assertEquals("3.5.0", pluginVersions.get("maven-resources-plugin"));
      assertEquals("3.6.2", pluginVersions.get("build-helper-maven-plugin"));
    }

    Path collisionRoot = temporaryFolder.newFolder().toPath();
    Path deploymentRepo = collisionRoot.resolve("deployment");
    Path centralRepo = collisionRoot.resolve("central");
    writeCollisionArtifact(deploymentRepo, "4.5.0");
    writeCollisionArtifact(centralRepo, "4.5.0");
    String coordinatePath = "org/eclipse/cdo/org.eclipse.net4j.db.jdbc/4.5.0/org.eclipse.net4j.db.jdbc-4.5.0.pom";
    assertEquals(Files.readString(deploymentRepo.resolve(coordinatePath)), Files.readString(centralRepo.resolve(coordinatePath)));
    String firstProjectRepository = childText(repository, "id");
    assertEquals("cdo-deployment-bundle", firstProjectRepository);
    assertEquals("central", childText(centralRepository, "id"));
  }

  @Test
  public void mavenSelectsDeploymentRepositoryWhenCentralHasTheSameCdoGav() throws Exception
  {
    Path root = temporaryFolder.newFolder().toPath();
    Path deploymentRepository = root.resolve("deployment-repository");
    Path centralRepository = root.resolve("central-repository");
    String artifact = "org.eclipse.net4j.db.jdbc";
    String version = "4.5.0";
    writeMavenSelectionArtifact(deploymentRepository, artifact, version, "DeploymentOnly");
    writeMavenSelectionArtifact(centralRepository, artifact, version, "CentralOnly");

    Path project = Files.createDirectory(root.resolve("consumer"));
    Path source = project.resolve("src/main/java/consumer/Consumer.java");
    Files.createDirectories(source.getParent());
    Files.writeString(source, "package consumer; import fixture.DeploymentOnly; public final class Consumer { DeploymentOnly value; }", StandardCharsets.UTF_8);
    Files.writeString(project.resolve("pom.xml"), "<project><modelVersion>4.0.0</modelVersion><groupId>example</groupId>"
        + "<artifactId>repository-precedence-test</artifactId><version>1.0.0</version><properties><maven.compiler.release>17</maven.compiler.release>"
        + "</properties><dependencies><dependency><groupId>org.eclipse.cdo</groupId><artifactId>" + artifact + "</artifactId><version>" + version
        + "</version></dependency></dependencies><profiles><profile><id>portable-pom-repository</id><activation><activeByDefault>true</activeByDefault></activation>"
        + "<repositories><repository><id>cdo-deployment-bundle</id><url>${cdo.deployment.repository}</url></repository></repositories></profile></profiles>"
        + "<build><plugins><plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-resources-plugin</artifactId>"
        + "<version>3.5.0</version></plugin><plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-compiler-plugin</artifactId>"
        + "<version>3.13.0</version></plugin></plugins></build></project>", StandardCharsets.UTF_8);

    File classes = new File(CDOAggregationTool.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    Path settings = new File(classes.getParentFile(), "central/standalone-examples-settings.xml").toPath();
    Path localRepository = root.resolve("maven-local");
    Path output = root.resolve("maven-output.txt");
    List<String> command = standaloneMavenCommand();
    command.addAll(Arrays.asList("-gs", settings.toString(), "-s", settings.toString(), "-Dmaven.repo.local=" + localRepository,
        "-Dcdo.deployment.repository=" + centralRepository.toUri().toASCIIString(), "-B", "-ntp", "-f", project.resolve("pom.xml").toString(), "compile"));
    ProcessBuilder processBuilder = new ProcessBuilder(command).directory(project.toFile()).redirectErrorStream(true).redirectOutput(output.toFile());
    processBuilder.environment().put("CDO_DEPLOYMENT_REPOSITORY", deploymentRepository.toUri().toASCIIString());
    processBuilder.environment().put("CDO_CENTRAL_REPOSITORY", centralRepository.toUri().toASCIIString());

    Process process;
    try
    {
      process = processBuilder.start();
    }
    catch (IOException ex)
    {
      assumeNoException("Maven executable is unavailable for repository precedence integration test", ex);
      return;
    }

    if (!process.waitFor(5, TimeUnit.MINUTES))
    {
      process.destroyForcibly();
      fail("Maven repository precedence integration timed out: " + Files.readString(output, StandardCharsets.UTF_8));
    }

    String log = Files.readString(output, StandardCharsets.UTF_8);
    assertEquals(log, 0, process.exitValue());

    Path resolvedArtifact = localRepository.resolve("org/eclipse/cdo/" + artifact + "/" + version);
    try (JarFile jar = new JarFile(resolvedArtifact.resolve(artifact + "-" + version + ".jar").toFile()))
    {
      assertNotNull("Maven selected Central's duplicate artifact", jar.getEntry("fixture/DeploymentOnly.class"));
      assertTrue(jar.getEntry("fixture/CentralOnly.class") == null);
    }

    Properties tracking = new Properties();
    try (InputStream input = Files.newInputStream(resolvedArtifact.resolve("_remote.repositories")))
    {
      tracking.load(input);
    }

    assertTrue(tracking.containsKey(artifact + "-" + version + ".pom>cdo-deployment-bundle"));
    assertTrue(tracking.containsKey(artifact + "-" + version + ".jar>cdo-deployment-bundle"));
    assertFalse(tracking.containsKey(artifact + "-" + version + ".pom>central-only"));
    assertFalse(tracking.containsKey(artifact + "-" + version + ".jar>central-only"));
  }

  @Test
  public void mavenResolvesTransitiveVersionRangeFromConsumerDeploymentMetadata() throws Exception
  {
    Path root = temporaryFolder.newFolder().toPath();
    Path deploymentRepository = root.resolve("deployment-repository");
    String rangedArtifact = "org.eclipse.emf.cdo.range-target";
    String consumerArtifact = "org.eclipse.emf.cdo.range-consumer";
    writeMavenSelectionArtifact(deploymentRepository, rangedArtifact, "4.2.1", "RangeTarget");
    writeMavenSelectionArtifact(deploymentRepository, rangedArtifact, "4.3.0", "RangeTarget");
    writeMavenSelectionArtifact(deploymentRepository, consumerArtifact, "1.0.0", "RangeConsumer");
    Path consumerPom = deploymentRepository.resolve("org/eclipse/cdo/" + consumerArtifact + "/1.0.0/" + consumerArtifact + "-1.0.0.pom");
    Files.writeString(consumerPom,
        "<project><modelVersion>4.0.0</modelVersion><groupId>org.eclipse.cdo</groupId><artifactId>" + consumerArtifact
            + "</artifactId><version>1.0.0</version><dependencies><dependency><groupId>org.eclipse.cdo</groupId><artifactId>" + rangedArtifact
            + "</artifactId><version>[4.0.0,5.0.0)</version></dependency></dependencies></project>",
        StandardCharsets.UTF_8);

    CDOAggregationTool.main(new String[] { "consumer-repository-metadata", deploymentRepository.toString() });
    Path metadata = deploymentRepository.resolve("org/eclipse/cdo/" + rangedArtifact + "/maven-metadata.xml");
    String metadataText = Files.readString(metadata, StandardCharsets.UTF_8);
    assertTrue(metadataText.contains("<version>4.2.1</version>"));
    assertTrue(metadataText.contains("<version>4.3.0</version>"));
    assertFalse(metadataText.contains("<version>4.4.0</version>"));

    Path project = Files.createDirectory(root.resolve("consumer-project"));
    Path source = project.resolve("src/main/java/consumer/Consumer.java");
    Files.createDirectories(source.getParent());
    Files.writeString(source, "package consumer; import fixture.RangeTarget; public final class Consumer { RangeTarget value; }", StandardCharsets.UTF_8);
    Files.writeString(project.resolve("pom.xml"),
        "<project><modelVersion>4.0.0</modelVersion><groupId>example</groupId>"
            + "<artifactId>transitive-range-test</artifactId><version>1.0.0</version><properties><maven.compiler.release>17</maven.compiler.release>"
            + "</properties><dependencies><dependency><groupId>org.eclipse.cdo</groupId><artifactId>" + consumerArtifact
            + "</artifactId><version>1.0.0</version></dependency></dependencies><repositories><repository><id>cdo-deployment-bundle</id>"
            + "<url>${cdo.deployment.repository}</url></repository></repositories><build><plugins><plugin><groupId>org.apache.maven.plugins</groupId>"
            + "<artifactId>maven-compiler-plugin</artifactId><version>3.13.0</version></plugin></plugins></build></project>",
        StandardCharsets.UTF_8);

    File classes = new File(CDOAggregationTool.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    Path settings = new File(classes.getParentFile(), "central/central-only-settings.xml").toPath();
    Path localRepository = root.resolve("maven-local");
    Path output = root.resolve("maven-output.txt");
    List<String> command = standaloneMavenCommand();
    command.addAll(Arrays.asList("-gs", settings.toString(), "-s", settings.toString(), "-Dmaven.repo.local=" + localRepository,
        "-Dcdo.deployment.repository=" + deploymentRepository.toUri().toASCIIString(), "-B", "-ntp", "-f", project.resolve("pom.xml").toString(), "compile"));
    ProcessBuilder processBuilder = new ProcessBuilder(command).directory(project.toFile()).redirectErrorStream(true).redirectOutput(output.toFile());

    Process process;
    try
    {
      process = processBuilder.start();
    }
    catch (IOException ex)
    {
      assumeNoException("Maven executable is unavailable for transitive version-range integration test", ex);
      return;
    }

    if (!process.waitFor(5, TimeUnit.MINUTES))
    {
      process.destroyForcibly();
      fail("Maven transitive version-range integration timed out: " + Files.readString(output, StandardCharsets.UTF_8));
    }

    String log = Files.readString(output, StandardCharsets.UTF_8);
    assertEquals(log, 0, process.exitValue());
    Path resolvedArtifact = localRepository.resolve("org/eclipse/cdo/" + rangedArtifact + "/4.3.0");
    assertTrue("Maven did not resolve the highest available deployment version", Files.isRegularFile(resolvedArtifact.resolve(rangedArtifact + "-4.3.0.jar")));
    Properties tracking = new Properties();
    try (InputStream input = Files.newInputStream(resolvedArtifact.resolve("_remote.repositories")))
    {
      tracking.load(input);
    }

    assertTrue(tracking.containsKey(rangedArtifact + "-4.3.0.pom>cdo-deployment-bundle"));
    assertTrue(tracking.containsKey(rangedArtifact + "-4.3.0.jar>cdo-deployment-bundle"));
  }

  @Test
  public void rejectsMissingModifiedAndConflictingExamplesDistributionArtifacts() throws Exception
  {
    Path root = temporaryFolder.newFolder().toPath();
    Path staging = root.resolve("staging");
    Path metadata = root.resolve("metadata.properties");
    Files.writeString(metadata,
        "cdo.build.type=R\ncdo.build.stream=4.x\ncdo.project.release=4.37\ncdo.project.release.source=DROP\ncdo.drop.id=R20260901-1311\ncdo.git.commit=deadbeef\n",
        StandardCharsets.UTF_8);
    createPublicationStaging(staging, false);
    String[] arguments = { "publish-check", staging.toString(), metadata.toString(), "VALIDATE", "" };
    CDOAggregationTool.main(arguments);

    String examplesVersion = examplesDistributionVersion();
    Path zip = staging
        .resolve("org/eclipse/cdo/cdo-examples-distribution/" + examplesVersion + "/cdo-examples-distribution-" + examplesVersion + "-examples.zip");
    byte[] originalZip = Files.readAllBytes(zip);
    Path checksum = Path.of(zip + ".sha256");
    byte[] originalChecksum = Files.readAllBytes(checksum);
    Files.delete(zip);
    expectPublicationScopeFailure(arguments, "Missing or empty staged examples distribution artifact");
    Files.write(zip, originalZip);
    Files.write(zip, new byte[] { 'X' }, StandardOpenOption.APPEND);
    expectPublicationScopeFailure(arguments, "Missing or invalid checksum for staged examples distribution artifact");
    Files.write(zip, originalZip);
    Files.write(checksum, originalChecksum);

    String conflictingVersion = examplesVersion + ".1";
    Path conflictingPom = staging
        .resolve("org/eclipse/cdo/cdo-examples-distribution/" + conflictingVersion + "/cdo-examples-distribution-" + conflictingVersion + ".pom");
    Files.createDirectories(conflictingPom.getParent());
    Files.writeString(conflictingPom,
        "<project><groupId>org.eclipse.cdo</groupId><artifactId>cdo-examples-distribution</artifactId><version>" + conflictingVersion + "</version></project>",
        StandardCharsets.UTF_8);
    expectPublicationScopeFailure(arguments, "Unapproved Maven publication coordinate");
  }

  @Test
  public void stagesStandaloneExamplesPomAndZipAgainstSelectedDropCandidates() throws Exception
  {
    Path root = temporaryFolder.newFolder().toPath();
    Path candidates = Files.createDirectory(root.resolve("candidates"));
    Path staging = Files.createDirectory(root.resolve("staging"));
    Path metadata = root.resolve("metadata.properties");
    String dropCommit = "0123456789abcdef0123456789abcdef01234567";
    String examplesSourceCommit = "fedcba9876543210fedcba9876543210fedcba98";
    Files.writeString(metadata, "cdo.build.type=R\ncdo.drop.id=R20260901-1311\ncdo.git.commit=" + dropCommit + "\ncdo.project.release=4.36.0\n"
        + "examples.distribution.version=" + examplesDistributionVersion() + "\n", StandardCharsets.UTF_8);

    createCandidate(candidates, "org.eclipse.emf.cdo.server.embedded", "4.11.1");
    createCandidate(candidates, "org.eclipse.emf.cdo.examples.company", "4.1.4");
    createCandidate(candidates, "org.eclipse.net4j.util", "3.31.0");
    createCandidate(candidates, "org.eclipse.net4j.db.postgresql", "4.5.1");

    File classes = new File(CDOAggregationTool.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    Path central = new File(classes.getParentFile(), "central").toPath();
    Path distribution = central.resolve("examples/distribution").toAbsolutePath();
    Path workspace = distribution.resolve("../../../../..").normalize();
    Path zip = root.resolve("examples.zip");
    String[] args = { "stage-examples-distribution", workspace.toString(), candidates.toString(), zip.toString(), distribution.resolve("pom.xml").toString(),
        metadata.toString(), central.resolve("publishing.properties").toString(), staging.toString(), examplesSourceCommit };

    createExamplesZip(workspace, distribution, zip, "4.1.5", false, false);
    try
    {
      CDOAggregationTool.main(args);
      fail("Expected an examples dependency version that differs from the selected drop to be rejected");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("Examples dependency version for org.eclipse.emf.cdo.examples.company"));
    }

    createExamplesZip(workspace, distribution, zip, null, true, false);
    try
    {
      CDOAggregationTool.main(args);
      fail("Expected an examples ZIP with an unexpected file to be rejected");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("Examples ZIP file inventory differs"));
    }

    createExamplesZip(workspace, distribution, zip, null, false, true);
    try
    {
      CDOAggregationTool.main(args);
      fail("Expected an examples ZIP with modified canonical Java source to be rejected");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("Packaged example source differs from its canonical JDT source"));
    }

    createExamplesZip(workspace, distribution, zip);
    CDOAggregationTool.main(args);

    String examplesVersion = examplesDistributionVersion();
    Path publishedPom = staging
        .resolve("org/eclipse/cdo/cdo-examples-distribution/" + examplesVersion + "/cdo-examples-distribution-" + examplesVersion + ".pom");
    Element project = parse(publishedPom.toFile()).getDocumentElement();
    Element properties = child(project, "properties");
    assertEquals(examplesSourceCommit, childText(child(project, "scm"), "tag"));
    assertEquals(examplesSourceCommit, childText(properties, "cdo.examples.source.git.commit"));
    assertEquals(dropCommit, childText(properties, "cdo.git.commit"));
    assertEquals("R20260901-1311", childText(properties, "cdo.drop.id"));
    assertEquals("4.36.0", childText(properties, "cdo.project.release"));
    assertTrue(child(properties, "examples.cdo.project.release") == null);
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
  public void bomSignedRepositoryAllowsMavenMetadataOnlyAtTheBomArtifactRoot() throws Exception
  {
    Set<String> signedFilesAllowed = new LinkedHashSet<>();
    String metadata = "org/eclipse/cdo/org.eclipse.cdo.bom/maven-metadata.xml";
    assertTrue(CDOAggregationTool.isAllowedSignedBomRepositoryPath(metadata, signedFilesAllowed));
    for (String checksum : new String[] { ".md5", ".sha1", ".sha256", ".sha512" })
    {
      assertTrue(CDOAggregationTool.isAllowedSignedBomRepositoryPath(metadata + checksum, signedFilesAllowed));
    }

    assertFalse(CDOAggregationTool.isAllowedSignedBomRepositoryPath("org/eclipse/cdo/org.eclipse.emf.cdo/maven-metadata.xml", signedFilesAllowed));
    assertFalse(CDOAggregationTool.isAllowedSignedBomRepositoryPath("org/eclipse/cdo/org.eclipse.cdo.bom/4.36.0/maven-metadata.xml", signedFilesAllowed));
    assertFalse(CDOAggregationTool.isAllowedSignedBomRepositoryPath("org/eclipse/cdo/org.eclipse.cdo.bom/other.txt", signedFilesAllowed));
    assertFalse(CDOAggregationTool.isAllowedSignedBomRepositoryPath(metadata + ".asc", signedFilesAllowed));
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
  public void releaseInventoryUsesPreparedOverrideWhenDropHasNoWebProperties() throws Exception
  {
    Path root = classificationFixture("4.37", "R20260901-1311");
    Path drop = root.resolve("drop");
    Files.delete(drop.resolve("web.properties"));
    Path metadata = root.resolve("prepared-metadata.properties");
    Files.writeString(metadata, "cdo.project.release=4.37\ncdo.project.release.source=OVERRIDE\ncdo.drop.id=R20260901-1311\n", StandardCharsets.UTF_8);
    Path inventory = root.resolve("release-inventory.properties");

    CDOAggregationTool.main(new String[] { "derive-release-inventory", drop.toString(), "R20260901-1311", inventory.toString(), metadata.toString() });

    Properties values = new Properties();
    try (InputStream input = Files.newInputStream(inventory))
    {
      values.load(input);
    }

    assertEquals("4.37", values.getProperty("cdo.project.release"));
    assertEquals("OVERRIDE", values.getProperty("cdo.project.release.source"));
    assertEquals("4.37.0", values.getProperty("bom.version"));

    Files.writeString(drop.resolve("web.properties"), "web.label=4.36\n", StandardCharsets.UTF_8);
    try
    {
      CDOAggregationTool.main(
          new String[] { "derive-release-inventory", drop.toString(), "R20260901-1311", root.resolve("conflict.properties").toString(), metadata.toString() });
      fail("Expected a present conflicting drop label to be rejected");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("does not match selected drop web.label"));
    }
  }

  @Test
  public void bomGenerationUsesPreparedOverrideWhenDropHasNoWebProperties() throws Exception
  {
    Path root = fixture("4.37", "R20260901-1311");
    Files.delete(root.resolve("drop/web.properties"));
    Files.writeString(root.resolve("metadata.properties"), "cdo.project.release.source=OVERRIDE\n", StandardCharsets.UTF_8, StandardOpenOption.APPEND);

    File bom = generate(root, "override-output");

    Element properties = child(parse(bom).getDocumentElement(), "properties");
    assertEquals("4.37", childText(properties, "cdo.project.release"));
    assertEquals("OVERRIDE", childText(properties, "cdo.project.release.source"));
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
      assertEquals("23", report.get("release.component.count"));
      assertEquals("23", report.get("publication.coordinate.count"));
      for (int i = 1; i <= BUNDLES.length + 2; i++)
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
      central.replaceBundlePom(0, bundlePom(BUNDLES[0], "1.0.0", "4.36", "R20260901-1311"));
      Map<String, String> report = classify(root, central, false);
      assertEquals("0", report.get("publication.coordinate.count"));
      for (int i = 1; i <= BUNDLES.length; i++)
      {
        assertEquals("REUSABLE", report.get("release.component." + i + ".classification"));
      }

      assertEquals("REUSABLE", report.get("release.component." + (BUNDLES.length + 1) + ".classification"));
      assertEquals("REUSABLE", report.get("release.component." + (BUNDLES.length + 2) + ".classification"));
      assertTrue(report.get("release.component." + (BUNDLES.length + 2) + ".reason").contains("Canonical POM exists"));
      assertEquals("0", report.get("publication.coordinate.count"));
    }
  }

  @Test
  public void reusesExistingCoordinatesRegardlessOfPublishedArtifactContents() throws Exception
  {
    Path root = classificationFixture("4.37", "R20261001-0900");
    try (CentralFixture central = new CentralFixture())
    {
      central.loadProposedArtifacts(root);
      central.replaceBundlePom(0, "not the proposed POM");
      central.replaceBundleArtifact(0, ".jar", new byte[] { 4, 5, 6 });

      Map<String, String> report = classify(root, central, false);
      assertEquals("REUSABLE", report.get("release.component.1.classification"));
      assertEquals("REUSABLE", report.get("release.component.2.classification"));
      assertEquals("0", report.get("publication.coordinate.count"));
      assertEquals("23", report.get("classification.reusable.count"));
      assertEquals("0", report.get("classification.new.count"));
      assertEquals("4.37", report.get("cdo.project.release"));
    }
  }

  @Test
  public void selectsFirstExamplesPatchAndPropagatesItIntoFinalStaging() throws Exception
  {
    Path root = temporaryFolder.newFolder().toPath();
    Map<String, Path> candidate = prepareExamplesVersionCandidate(root, "4.37.0");
    try (CentralFixture central = new CentralFixture())
    {
      selectExamplesVersion(candidate, central);
      Properties metadata = readProperties(candidate.get("metadata"));
      assertEquals("1.0.0", metadata.getProperty("examples.distribution.version"));
      assertEquals("NEW", readProperties(root.resolve("examples-version-selection.properties")).getProperty("examples.distribution.selection"));

      Path finalStaging = Files.createDirectories(root.resolve("final-staging"));
      String sourceRevision = Files.readString(candidate.get("source.revision"), StandardCharsets.US_ASCII);
      CDOAggregationTool.main(new String[] { "stage-examples-distribution", candidate.get("workspace").toString(), candidate.get("candidates").toString(),
          candidate.get("zip").toString(), candidate.get("pom").toString(), candidate.get("metadata").toString(), candidate.get("publishing").toString(),
          finalStaging.toString(), sourceRevision });
      Path finalPom = finalStaging.resolve("org/eclipse/cdo/cdo-examples-distribution/1.0.0/cdo-examples-distribution-1.0.0.pom");
      Path candidatePom = candidate.get("initial.staging").resolve("org/eclipse/cdo/cdo-examples-distribution/1.0.0/cdo-examples-distribution-1.0.0.pom");
      assertEquals(Files.readString(candidatePom, StandardCharsets.UTF_8), Files.readString(finalPom, StandardCharsets.UTF_8));
      assertTrue(Files.isRegularFile(finalStaging.resolve("org/eclipse/cdo/cdo-examples-distribution/1.0.0/cdo-examples-distribution-1.0.0-examples.zip")));
    }
  }

  @Test
  public void selectedExamplesVersionStagesWithPreviouslyGeneratedBundlesAndBom() throws Exception
  {
    Path root = fixture("4.37", "R20261001-0900");
    Path metadataFile = root.resolve("metadata.properties");
    Files.writeString(metadataFile,
        Files.readString(metadataFile, StandardCharsets.UTF_8) + "cdo.build.type=R\ncdo.build.stream=4.x\ncdo.project.release.source=DROP\n",
        StandardCharsets.UTF_8);
    for (int index = 0; index < BUNDLES.length; index++)
    {
      String bundle = BUNDLES[index];
      String version = "1.0." + index;
      Path directory = root.resolve("bundles/org/eclipse/cdo/" + bundle + "/" + version);
      writeChecksums(directory.resolve(bundle + "-" + version + ".pom"));
      for (String suffix : new String[] { ".jar", "-sources.jar", "-javadoc.jar" })
      {
        Path artifact = directory.resolve(bundle + "-" + version + suffix);
        Files.writeString(artifact, "fixture " + bundle + suffix, StandardCharsets.UTF_8);
        writeChecksums(artifact);
      }
    }

    File generatedBom = generate(root, "generated-bom");
    Path candidateRoot = Files.createDirectories(root.resolve("examples-candidate"));
    Map<String, Path> candidate = prepareExamplesVersionCandidate(candidateRoot, "4.37.0");
    candidate.put("metadata", metadataFile);
    Path staging = Files.createDirectories(root.resolve("complete-staging"));
    try (Stream<Path> files = Files.walk(root.resolve("bundles")))
    {
      for (Path file : files.filter(Files::isRegularFile).collect(Collectors.toList()))
      {
        Path destination = staging.resolve(root.resolve("bundles").relativize(file));
        Files.createDirectories(destination.getParent());
        Files.copy(file, destination);
      }
    }

    try (CentralFixture central = new CentralFixture())
    {
      setExamplesVersionMetadata(central, "1.0.15");
      String candidatePom = Files.readString(
          candidate.get("initial.staging").resolve("org/eclipse/cdo/cdo-examples-distribution/1.0.0/cdo-examples-distribution-1.0.0.pom"),
          StandardCharsets.UTF_8);
      String oldPom = candidatePom.replace("<version>1.0.0</version>", "<version>1.0.15</version>").replace("<cdo.project.release>4.37.0</cdo.project.release>",
          "<cdo.project.release>4.37</cdo.project.release>");
      byte[] originalZip = Files.readAllBytes(candidate.get("zip"));
      byte[] oldZip = Arrays.copyOf(originalZip, originalZip.length + 1);
      oldZip[oldZip.length - 1] = 1;
      publishExamplesVersion(central, "1.0.15", oldPom.getBytes(StandardCharsets.UTF_8), oldZip);
      selectExamplesVersion(candidate, central);
      assertEquals("1.0.16", readProperties(metadataFile).getProperty("examples.distribution.version"));
    }

    CDOAggregationTool.main(new String[] { "stage-release-bom", root.resolve("generated-bom").toString(), metadataFile.toString(), staging.toString() });
    CDOAggregationTool.main(new String[] { "stage-examples-distribution", candidate.get("workspace").toString(), candidate.get("candidates").toString(),
        candidate.get("zip").toString(), candidate.get("pom").toString(), metadataFile.toString(), candidate.get("publishing").toString(), staging.toString(),
        Files.readString(candidate.get("source.revision"), StandardCharsets.US_ASCII) });
    Path stagedExamplesPom = staging.resolve("org/eclipse/cdo/cdo-examples-distribution/1.0.16/cdo-examples-distribution-1.0.16.pom");
    Element stagedPomProperties = child(parse(stagedExamplesPom.toFile()).getDocumentElement(), "properties");
    assertTrue("Packaging control metadata must not become publication provenance", child(stagedPomProperties, "examples.distribution.version") == null);
    try (Stream<Path> files = Files.walk(staging))
    {
      for (Path artifact : files.filter(Files::isRegularFile)
          .filter(file -> file.toString().endsWith(".pom") || file.toString().endsWith(".jar") || file.toString().endsWith(".zip"))
          .collect(Collectors.toList()))
      {
        Files.writeString(Path.of(artifact + ".asc"), "-----BEGIN PGP SIGNATURE-----\nfixture\n", StandardCharsets.US_ASCII);
      }
    }

    assertTrue(generatedBom.isFile());
    CDOAggregationTool.main(new String[] { "publish-check", staging.toString(), metadataFile.toString(), "PACKAGE", "SIGNED" });
  }

  @Test
  public void selectsTheHighestNumericEquivalentExamplesPatchAndIgnoresPermittedProvenance() throws Exception
  {
    Path root = temporaryFolder.newFolder().toPath();
    Map<String, Path> candidate = prepareExamplesVersionCandidate(root, "4.37.0");
    String basePom = Files.readString(
        candidate.get("initial.staging").resolve("org/eclipse/cdo/cdo-examples-distribution/1.0.0/cdo-examples-distribution-1.0.0.pom"),
        StandardCharsets.UTF_8);
    byte[] zip = Files.readAllBytes(candidate.get("zip"));
    try (CentralFixture central = new CentralFixture())
    {
      setExamplesVersionMetadata(central, "1.0.9", "1.0.15");
      String historicalPom = basePom.replace("<version>1.0.0</version>", "<version>1.0.9</version>")
          .replace("<cdo.drop.id>R20261001-0900</cdo.drop.id>", "<cdo.drop.id>R20260901-1311</cdo.drop.id>")
          .replace("<cdo.project.release>4.37.0</cdo.project.release>", "<cdo.project.release>4.36.0</cdo.project.release>")
          .replace("<cdo.git.commit>0123456789abcdef0123456789abcdef01234567</cdo.git.commit>",
              "<cdo.git.commit>aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa</cdo.git.commit>")
          .replace("<cdo.examples.source.git.commit>fedcba9876543210fedcba9876543210fedcba98</cdo.examples.source.git.commit>",
              "<cdo.examples.source.git.commit>bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb</cdo.examples.source.git.commit>")
          .replace("<tag>fedcba9876543210fedcba9876543210fedcba98</tag>", "<tag>bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb</tag>");
      publishExamplesVersion(central, "1.0.9", historicalPom.getBytes(StandardCharsets.UTF_8), zip);
      String newestPom = basePom.replace("<version>1.0.0</version>", "<version>1.0.15</version>");
      publishExamplesVersion(central, "1.0.15", newestPom.getBytes(StandardCharsets.UTF_8), zip);

      selectExamplesVersion(candidate, central);

      assertEquals("1.0.15", readProperties(candidate.get("metadata")).getProperty("examples.distribution.version"));
    }
  }

  @Test
  public void advancesExamplesPatchForChangedZipAndEmbeddedBom() throws Exception
  {
    for (String release : new String[] { "4.36.0", "4.37.0" })
    {
      Path root = temporaryFolder.newFolder().toPath();
      Map<String, Path> candidate = prepareExamplesVersionCandidate(root, release);
      String basePom = Files.readString(
          candidate.get("initial.staging").resolve("org/eclipse/cdo/cdo-examples-distribution/1.0.0/cdo-examples-distribution-1.0.0.pom"),
          StandardCharsets.UTF_8);
      byte[] publishedZip = Files.readAllBytes(candidate.get("zip"));
      if ("4.37.0".equals(release))
      {
        Path oldZip = root.resolve("old-bom-examples.zip");
        createExamplesZip(candidate.get("workspace"), candidate.get("distribution"), oldZip, null, false, false, "4.36.0");
        publishedZip = Files.readAllBytes(oldZip);
      }
      else
      {
        publishedZip = Arrays.copyOf(publishedZip, publishedZip.length + 1);
        publishedZip[publishedZip.length - 1] = 0;
      }

      try (CentralFixture central = new CentralFixture())
      {
        setExamplesVersionMetadata(central, "1.0.15");
        publishExamplesVersion(central, "1.0.15", basePom.replace("<version>1.0.0</version>", "<version>1.0.15</version>").getBytes(StandardCharsets.UTF_8),
            publishedZip);
        selectExamplesVersion(candidate, central);
        assertEquals("1.0.16", readProperties(candidate.get("metadata")).getProperty("examples.distribution.version"));
      }
    }
  }

  @Test
  public void failsClosedOnMalformedCentralExamplesHistoryAndIncompletePublishedCoordinates() throws Exception
  {
    Path root = temporaryFolder.newFolder().toPath();
    Map<String, Path> candidate = prepareExamplesVersionCandidate(root, "4.37.0");
    try (CentralFixture central = new CentralFixture())
    {
      central.setArtifact("org/eclipse/cdo/cdo-examples-distribution/maven-metadata.xml", "not-xml".getBytes(StandardCharsets.UTF_8));
      expectExamplesVersionSelectionFailure(candidate, central);
    }

    try (CentralFixture central = new CentralFixture())
    {
      central.setStatus("org/eclipse/cdo/cdo-examples-distribution/maven-metadata.xml", 503);
      expectExamplesVersionSelectionFailure(candidate, central);
    }

    try (CentralFixture central = new CentralFixture())
    {
      setExamplesVersionMetadata(central, "1.0.15");
      expectExamplesVersionSelectionFailure(candidate, central);
    }

  }

  @Test
  public void incrementalPackageAcceptsFingerprintArgumentAfterPreparingANewCoordinate() throws Exception
  {
    Path root = temporaryFolder.newFolder().toPath();
    Path staging = Files.createDirectories(root.resolve("staging"));
    Path manifest = root.resolve("classification.properties");
    Path publicKey = Files.write(root.resolve("public-key.gpg"), new byte[] { 1 });
    Path upload = root.resolve("incremental.zip");
    Path summary = root.resolve("summary.properties");
    createPublicationStaging(staging, true, "1.0.15");

    StringBuilder contents = new StringBuilder("release.component.count=23\npublication.coordinate.count=1\npublication.coordinate.1=org.eclipse.cdo:")
        .append(BUNDLES[0]).append(":1.0.0\nclassification.new.count=1\nclassification.reusable.count=22\n")
        .append("classification.lookup_error.count=0\ncdo.project.release=4.37\ncdo.drop.id=R20261001-0900\nexamples.distribution.version=1.0.15\n");
    for (int index = 0; index < BUNDLES.length; index++)
    {
      appendClassification(contents, index + 1, BUNDLES[index], "1.0.0", "bundle");
      int start = contents.indexOf("release.component." + (index + 1) + ".classification=REUSABLE");
      if (index == 0)
      {
        int end = contents.indexOf("\n", start);
        contents.replace(start, end, "release.component." + (index + 1) + ".classification=NEW");
      }
    }

    appendClassification(contents, BUNDLES.length + 1, "cdo-examples-distribution", "1.0.15", "examples");
    appendClassification(contents, BUNDLES.length + 2, "org.eclipse.cdo.bom", "4.37.0", "bom");
    Files.writeString(manifest, contents, StandardCharsets.UTF_8);

    try
    {
      CDOAggregationTool.main(new String[] { "incremental-package", staging.toString(), manifest.toString(), upload.toString(), "A".repeat(40),
          publicKey.toString(), "missing-gpg-executable", summary.toString() });
      fail("Expected GnuPG validation to be reached for the NEW coordinate");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue("The NEW coordinate must advance to OpenPGP verification: " + expected.getMessage(), expected.getMessage().contains("GnuPG"));
      assertFalse("The output ZIP path must not be interpreted as the key fingerprint", expected.getMessage().contains("fingerprint"));
    }
    catch (IOException expected)
    {
      assertTrue("The NEW coordinate must advance to isolated GnuPG cleanup", expected.getMessage().contains("gpgconf.exe"));
      assertFalse("The output ZIP path must not be interpreted as the key fingerprint", expected.getMessage().contains("fingerprint"));
    }
  }

  private void expectExamplesVersionSelectionFailure(Map<String, Path> candidate, CentralFixture central) throws Exception
  {
    try
    {
      selectExamplesVersion(candidate, central);
      fail("Expected Central examples version selection to fail closed");
    }
    catch (Exception expected)
    {
      assertFalse("Failure must explain the Central/version validation problem", expected.getMessage() == null || expected.getMessage().isBlank());
    }
  }

  @Test
  public void incrementalPreparationReportsANoopForAnEmptyNewCoordinateSet() throws Exception
  {
    Path root = temporaryFolder.newFolder().toPath();
    Path staging = Files.createDirectories(root.resolve("staging"));
    Path manifest = root.resolve("classification.properties");
    Path publicKey = Files.write(root.resolve("public-key.gpg"), new byte[] { 1 });
    Path upload = root.resolve("incremental.zip");
    Path summary = root.resolve("summary.properties");
    createPublicationStaging(staging, true, "1.0.15");
    StringBuilder contents = new StringBuilder("release.component.count=23\npublication.coordinate.count=0\n")
        .append("classification.new.count=0\nclassification.reusable.count=23\nclassification.lookup_error.count=0\n")
        .append("cdo.project.release=4.37\ncdo.drop.id=R20261001-0900\nexamples.distribution.version=1.0.15\n");
    int index = 1;
    for (int bundleIndex = 0; bundleIndex < BUNDLES.length; bundleIndex++)
    {
      String bundle = BUNDLES[bundleIndex];
      String version = "1.0.0";
      appendClassification(contents, index++, bundle, version, "bundle");
    }

    appendClassification(contents, index++, "cdo-examples-distribution", "1.0.15", "examples");
    appendClassification(contents, index, "org.eclipse.cdo.bom", "4.37.0", "bom");
    Files.writeString(manifest, contents, StandardCharsets.UTF_8);
    CDOAggregationTool.main(new String[] { "incremental-package", root.resolve("staging").toString(), manifest.toString(), upload.toString(), "A".repeat(40),
        publicKey.toString(), "gpg-is-not-reached", summary.toString() });

    assertFalse(Files.exists(upload));
    assertTrue(Files.readString(summary, StandardCharsets.UTF_8).contains("incremental.publication=NOT_REQUIRED"));

    String firstBundle = "org.eclipse.cdo:" + BUNDLES[0] + ":1.0.0";
    String incomplete = contents.toString().replace("publication.coordinate.count=0", "publication.coordinate.count=1")
        .replace("classification.new.count=0", "classification.new.count=1").replace("classification.reusable.count=23", "classification.reusable.count=22")
        .replace("release.component.1.classification=REUSABLE", "release.component.1.classification=NEW")
        .replace("release.component.1.reason=Equivalent\n", "release.component.1.reason=New\npublication.coordinate.1=" + firstBundle + "\n");
    Files.writeString(manifest, incomplete, StandardCharsets.UTF_8);
    Files.delete(staging.resolve(path(BUNDLES[0], "1.0.0", ".jar")));
    Files.delete(summary);
    try
    {
      CDOAggregationTool.main(new String[] { "incremental-package", root.resolve("staging").toString(), manifest.toString(), upload.toString(), "A".repeat(40),
          publicKey.toString(), "gpg-is-not-reached", summary.toString() });
      fail("Expected an incomplete NEW coordinate to fail closed");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("required incremental publication artifact"));
    }
  }

  private static void appendClassification(StringBuilder contents, int index, String artifact, String version, String kind)
  {
    String group = "org.eclipse.cdo";
    if ("bom".equals(kind))
    {
      artifact = "org.eclipse.cdo.bom";
    }

    contents.append("release.component.").append(index).append(".coordinate=").append(group).append(':').append(artifact).append(':').append(version)
        .append('\n').append("release.component.").append(index).append(".kind=").append(kind).append('\n').append("release.component.").append(index)
        .append(".classification=REUSABLE\nrelease.component.").append(index).append(".reason=Equivalent\n");
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
    StringBuilder index = new StringBuilder("<index>\n");
    for (int i = 0; i < BUNDLES.length; i++)
    {
      index.append("  <element name=\"").append(BUNDLES[i]).append("\" type=\"osgi.bundle\" version=\"").append(FIXTURE_VERSIONS[i])
          .append(".v20260901-1311\"/>\n");
      index.append("  <element name=\"").append(BUNDLES[i]).append("\" type=\"org.eclipse.update.feature\" version=\"99.0.0\"/>\n");
    }

    index.append("</index>\n");
    Files.writeString(drop.resolve("index.xml"), index, StandardCharsets.UTF_8);
    return root;
  }

  private static String examplesDistributionVersion() throws Exception
  {
    File classes = new File(CDOAggregationTool.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    Path propertiesFile = new File(classes.getParentFile(), "central/examples/distribution/package.properties").toPath();
    Properties properties = new Properties();
    try (InputStream input = Files.newInputStream(propertiesFile))
    {
      properties.load(input);
    }

    String versionLine = properties.getProperty("release.version.line");
    if (versionLine == null || !versionLine.matches("[0-9]+\\.[0-9]+"))
    {
      throw new IOException("Invalid examples distribution release.version.line in " + propertiesFile);
    }

    return versionLine + ".0";
  }

  private Map<String, Path> prepareExamplesVersionCandidate(Path root, String projectRelease) throws Exception
  {
    Path metadata = root.resolve("build-metadata.properties");
    String sourceCommit = "0123456789abcdef0123456789abcdef01234567";
    String sourceRevision = "fedcba9876543210fedcba9876543210fedcba98";
    Files.writeString(metadata,
        "cdo.build.type=R\ncdo.build.stream=4.x\ncdo.project.release=" + projectRelease
            + "\ncdo.project.release.source=DROP\ncdo.drop.id=R20261001-0900\ncdo.git.commit=" + sourceCommit + "\nexamples.distribution.version="
            + examplesDistributionVersion() + "\n",
        StandardCharsets.UTF_8);

    File classes = new File(CDOAggregationTool.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    Path central = new File(classes.getParentFile(), "central").toPath();
    Path distribution = central.resolve("examples/distribution").toAbsolutePath();
    Path workspace = distribution.resolve("../../../../..").normalize();
    Path candidates = Files.createDirectories(root.resolve("candidate-repository"));
    createCandidate(candidates, "org.eclipse.emf.cdo.server.embedded", "4.11.1");
    createCandidate(candidates, "org.eclipse.emf.cdo.examples.company", "4.1.4");
    createCandidate(candidates, "org.eclipse.net4j.util", "3.31.0");
    createCandidate(candidates, "org.eclipse.net4j.db.postgresql", "4.5.1");
    Path zip = root.resolve("candidate-examples.zip");
    createExamplesZip(workspace, distribution, zip, null, false, false, projectRelease);
    Path initialStaging = Files.createDirectories(root.resolve("initial-staging"));
    String[] stageArguments = { "stage-examples-distribution", workspace.toString(), candidates.toString(), zip.toString(),
        distribution.resolve("pom.xml").toString(), metadata.toString(), central.resolve("publishing.properties").toString(), initialStaging.toString(),
        sourceRevision };
    CDOAggregationTool.main(stageArguments);

    Map<String, Path> result = new LinkedHashMap<>();
    result.put("metadata", metadata);
    result.put("zip", zip);
    result.put("pom", distribution.resolve("pom.xml"));
    result.put("publishing", central.resolve("publishing.properties"));
    result.put("source.revision", root.resolve("source-revision.txt"));
    Files.writeString(result.get("source.revision"), sourceRevision, StandardCharsets.US_ASCII);
    result.put("package.properties", distribution.resolve("package.properties"));
    result.put("initial.staging", initialStaging);
    result.put("distribution", distribution);
    result.put("workspace", workspace);
    result.put("selection.work", root);
    result.put("candidates", candidates);
    return result;
  }

  private void selectExamplesVersion(Map<String, Path> candidate, CentralFixture central) throws Exception
  {
    Path sourceRevision = candidate.get("source.revision");
    CDOAggregationTool.main(new String[] { "select-examples-version", candidate.get("zip").toString(), candidate.get("pom").toString(),
        candidate.get("metadata").toString(), candidate.get("publishing").toString(), Files.readString(sourceRevision, StandardCharsets.US_ASCII),
        central.baseUrl(), candidate.get("selection.work").toString(), candidate.get("package.properties").toString() });
  }

  private void publishExamplesVersion(CentralFixture central, String version, byte[] pom, byte[] zip)
  {
    String prefix = "org/eclipse/cdo/cdo-examples-distribution/" + version + "/cdo-examples-distribution-" + version;
    central.setArtifact(prefix + ".pom", pom);
    central.setArtifact(prefix + "-examples.zip", zip);
  }

  private void setExamplesVersionMetadata(CentralFixture central, String... versions)
  {
    StringBuilder xml = new StringBuilder(
        "<metadata><groupId>org.eclipse.cdo</groupId><artifactId>cdo-examples-distribution</artifactId><versioning><versions>");
    for (String version : versions)
    {
      xml.append("<version>").append(version).append("</version>");
    }

    xml.append("</versions></versioning></metadata>");
    central.setArtifact("org/eclipse/cdo/cdo-examples-distribution/maven-metadata.xml", xml.toString().getBytes(StandardCharsets.UTF_8));
  }

  private Properties readProperties(Path file) throws IOException
  {
    Properties properties = new Properties();
    try (InputStream input = Files.newInputStream(file))
    {
      properties.load(input);
    }

    return properties;
  }

  private Path deriveInventory(Path root) throws Exception
  {
    Path inventory = root.resolve("runtime-release-inventory.properties");
    CDOAggregationTool.main(new String[] { "derive-release-inventory", root.resolve("drop").toString(), "R20260901-1311", inventory.toString() });
    return inventory;
  }

  private void createDeploymentZip(Path staging, Path deploymentZip) throws Exception
  {
    try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(deploymentZip)); Stream<Path> paths = Files.walk(staging.resolve("org")))
    {
      List<Path> entries = paths.sorted().collect(Collectors.toList());
      for (Path path : entries)
      {
        String relative = staging.relativize(path).toString().replace(File.separatorChar, '/');
        if (Files.isDirectory(path))
        {
          output.putNextEntry(new ZipEntry(relative + "/"));
          output.closeEntry();
          continue;
        }

        output.putNextEntry(new ZipEntry(relative));
        Files.copy(path, output);
        output.closeEntry();
      }
    }
  }

  private void createPublicationStaging(Path staging, boolean includeBom) throws Exception
  {
    createPublicationStaging(staging, includeBom, examplesDistributionVersion());
  }

  private void createPublicationStaging(Path staging, boolean includeBom, String examplesVersion) throws Exception
  {
    String[][] algorithms = { { "md5", "MD5" }, { "sha1", "SHA-1" }, { "sha256", "SHA-256" }, { "sha512", "SHA-512" } };
    for (String bundle : BUNDLES)
    {
      for (String suffix : new String[] { ".pom", ".jar", "-sources.jar", "-javadoc.jar" })
      {
        String relative = path(bundle, "1.0.0", suffix);
        Path artifact = staging.resolve(relative);
        Files.createDirectories(artifact.getParent());
        String content = suffix.equals(".pom")
            ? "<project><groupId>org.eclipse.cdo</groupId><artifactId>" + bundle + "</artifactId><version>1.0.0</version></project>\n"
            : "fixture artifact " + relative + "\n";
        Files.writeString(artifact, content, StandardCharsets.UTF_8);
        for (String[] algorithm : algorithms)
        {
          byte[] digest = MessageDigest.getInstance(algorithm[1]).digest(Files.readAllBytes(artifact));
          StringBuilder hexadecimal = new StringBuilder();
          for (byte value : digest)
          {
            hexadecimal.append(String.format("%02x", value & 0xff));
          }

          Files.writeString(Path.of(artifact + "." + algorithm[0]), hexadecimal.toString(), StandardCharsets.US_ASCII);
        }

        Files.writeString(Path.of(artifact + ".asc"), "-----BEGIN PGP SIGNATURE-----\nfixture\n", StandardCharsets.US_ASCII);
      }
    }

    for (String suffix : new String[] { ".pom", "-examples.zip" })
    {
      String artifact = "cdo-examples-distribution-" + examplesVersion + suffix;
      Path file = staging.resolve("org/eclipse/cdo/cdo-examples-distribution/" + examplesVersion + "/" + artifact);
      Files.createDirectories(file.getParent());
      String content = suffix.equals(".pom")
          ? "<project><modelVersion>4.0.0</modelVersion><groupId>org.eclipse.cdo</groupId><artifactId>cdo-examples-distribution</artifactId>" + "<version>"
              + examplesVersion
              + "</version><packaging>pom</packaging><name>CDO Examples</name><description>Examples ZIP</description><url>https://eclipse.dev/cdo/</url>"
              + "<organization><name>Eclipse Foundation</name><url>https://www.eclipse.org</url></organization><licenses><license><name>EPL-2.0</name>"
              + "<url>https://www.eclipse.org/legal/epl-2.0/</url><distribution>repo</distribution></license></licenses><developers><developer><name>CDO</name>"
              + "<url>https://github.com/eclipse-cdo/cdo/graphs/contributors</url></developer></developers><scm><connection>scm:git:https://github.com/eclipse-cdo/cdo.git</connection>"
              + "<developerConnection>scm:git:https://github.com/eclipse-cdo/cdo.git</developerConnection><url>https://eclipse.dev/cdo/</url><tag>abcdef0123456789abcdef0123456789abcdef01</tag></scm>"
              + "<issueManagement><system>GitHub</system><url>https://github.com/eclipse-cdo/cdo/issues</url></issueManagement><properties><cdo.git.commit>deadbeef</cdo.git.commit>"
              + "<cdo.drop.id>R20260901-1311</cdo.drop.id><cdo.project.release>4.37</cdo.project.release>"
              + "<cdo.examples.source.git.commit>abcdef0123456789abcdef0123456789abcdef01</cdo.examples.source.git.commit>" + "</properties></project>\n"
          : "fixture examples zip\n";
      Files.writeString(file, content, StandardCharsets.UTF_8);
      for (String[] algorithm : algorithms)
      {
        byte[] digest = MessageDigest.getInstance(algorithm[1]).digest(Files.readAllBytes(file));
        StringBuilder hexadecimal = new StringBuilder();
        for (byte value : digest)
        {
          hexadecimal.append(String.format("%02x", value & 0xff));
        }

        Files.writeString(Path.of(file + "." + algorithm[0]), hexadecimal.toString(), StandardCharsets.US_ASCII);
      }

      Files.writeString(Path.of(file + ".asc"), "-----BEGIN PGP SIGNATURE-----\nfixture\n", StandardCharsets.US_ASCII);
    }

    if (includeBom)
    {
      createRegularBomFixture(staging);
    }
  }

  private void createRegularBomFixture(Path staging) throws Exception
  {
    createRegularBomFixture(staging, "4.37", "DROP", "R20260901-1311");
  }

  private void createRegularBomFixture(Path staging, String release, String releaseSource, String dropID) throws Exception
  {
    String version = release.split("\\.").length == 2 ? release + ".0" : release;
    String artifact = "org.eclipse.cdo.bom";
    Path directory = staging.resolve("org/eclipse/cdo/" + artifact + "/" + version);
    Files.createDirectories(directory);
    StringBuilder pom = new StringBuilder("<project><modelVersion>4.0.0</modelVersion><groupId>org.eclipse.cdo</groupId><artifactId>").append(artifact)
        .append("</artifactId><version>").append(version).append("</version><packaging>pom</packaging><properties><cdo.project.release>").append(release)
        .append("</cdo.project.release><cdo.project.release.source>").append(releaseSource).append("</cdo.project.release.source><cdo.drop.id>").append(dropID)
        .append("</cdo.drop.id></properties>").append("<dependencyManagement><dependencies>");
    for (String bundle : BUNDLES)
    {
      pom.append("<dependency><groupId>org.eclipse.cdo</groupId><artifactId>").append(bundle).append("</artifactId><version>1.0.0</version></dependency>");
    }

    pom.append("</dependencies></dependencyManagement></project>\n");
    Path bomPom = directory.resolve(artifact + "-" + version + ".pom");
    Files.writeString(bomPom, pom.toString(), StandardCharsets.UTF_8);
    String[][] algorithms = { { "md5", "MD5" }, { "sha1", "SHA-1" }, { "sha256", "SHA-256" }, { "sha512", "SHA-512" } };
    for (String[] algorithm : algorithms)
    {
      byte[] digest = MessageDigest.getInstance(algorithm[1]).digest(Files.readAllBytes(bomPom));
      StringBuilder hexadecimal = new StringBuilder();
      for (byte value : digest)
      {
        hexadecimal.append(String.format("%02x", value & 0xff));
      }

      Files.writeString(Path.of(bomPom + "." + algorithm[0]), hexadecimal.toString(), StandardCharsets.US_ASCII);
    }

    Files.writeString(Path.of(bomPom + ".asc"), "-----BEGIN PGP SIGNATURE-----\nfixture\n", StandardCharsets.US_ASCII);
  }

  private void writeChecksums(Path artifact) throws Exception
  {
    String[][] algorithms = { { "md5", "MD5" }, { "sha1", "SHA-1" }, { "sha256", "SHA-256" }, { "sha512", "SHA-512" } };
    byte[] contents = Files.readAllBytes(artifact);
    for (String[] algorithm : algorithms)
    {
      byte[] digest = MessageDigest.getInstance(algorithm[1]).digest(contents);
      StringBuilder hexadecimal = new StringBuilder();
      for (byte value : digest)
      {
        hexadecimal.append(String.format("%02x", value & 0xff));
      }

      Files.writeString(Path.of(artifact + "." + algorithm[0]), hexadecimal.toString(), StandardCharsets.US_ASCII);
    }
  }

  private void expectPublicationScopeFailure(String[] arguments, String message) throws Exception
  {
    try
    {
      CDOAggregationTool.main(arguments);
      fail("Expected publication-scope validation to fail with " + message);
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().startsWith(message));
    }
  }

  private void expectBomCentralFailure(Path root, CentralFixture central, String message) throws Exception
  {
    try
    {
      CDOAggregationTool
          .main(new String[] { "check-bom-central", root.resolve("metadata.properties").toString(), root.resolve("drop").toString(), central.baseUrl() });
      fail("Expected BOM Central absence check to fail with " + message);
    }
    catch (IllegalStateException expected)
    {
      assertTrue(expected.getMessage().contains(message));
    }
  }

  private void expectBomPackageValidationFailure(String[] arguments, String message) throws Exception
  {
    try
    {
      CDOAggregationTool.main(arguments);
      fail("Expected BOM package validation to fail with " + message);
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains(message));
    }
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
    String examplesVersion = examplesDistributionVersion();
    Path examplesDirectory = root.resolve("bundles/org/eclipse/cdo/cdo-examples-distribution/" + examplesVersion);
    Files.createDirectories(examplesDirectory);
    Files.writeString(examplesDirectory.resolve("cdo-examples-distribution-" + examplesVersion + ".pom"),
        "<project><modelVersion>4.0.0</modelVersion><groupId>org.eclipse.cdo</groupId><artifactId>cdo-examples-distribution</artifactId><version>"
            + examplesVersion + "</version>" + "<packaging>pom</packaging><properties><cdo.drop.id>" + dropID + "</cdo.drop.id><cdo.project.release>" + release
            + "</cdo.project.release></properties></project>",
        StandardCharsets.UTF_8);
    Files.writeString(examplesDirectory.resolve("cdo-examples-distribution-" + examplesVersion + "-examples.zip"), "examples", StandardCharsets.UTF_8);
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

  private String[] provenanceArguments(Path repository, Path examples) throws Exception
  {
    Path metadata = temporaryFolder.newFile().toPath();
    Files.writeString(metadata, "cdo.project.release=4.37\n", StandardCharsets.UTF_8);
    return new String[] { "verify-standalone-provenance", repository.toString(), examples.toString(), metadata.toString() };
  }

  private void expectProvenanceFailure(Path repository, Path examples, String messagePart, String expectedRepository) throws Exception
  {
    try
    {
      CDOAggregationTool.main(provenanceArguments(repository, examples));
      fail("Expected standalone artifact provenance verification to fail");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage(), expected.getMessage().contains(messagePart));
      assertTrue(expected.getMessage(), expected.getMessage().contains(expectedRepository));
    }
  }

  private Path createStandaloneProvenanceFixture(Path root, String artifactPomOrigin, String artifactJarOrigin, String bomOrigin, boolean includeArtifact,
      boolean includeBom) throws Exception
  {
    Path repository = Files.createDirectories(root.resolve("repository"));
    createStandaloneModulePoms(root.resolve("examples"));
    if (includeArtifact)
    {
      writeResolverArtifact(repository, "org.eclipse.emf.cdo.server.db", "4.37.0", "jar", artifactPomOrigin, artifactJarOrigin, true);
    }

    if (includeBom)
    {
      writeResolverArtifact(repository, "org.eclipse.cdo.bom", "4.37.0", "pom", bomOrigin, null, false);
    }

    Path thirdParty = repository.resolve("com/example/unrelated/1.0/unrelated-1.0.jar");
    Files.createDirectories(thirdParty.getParent());
    Files.writeString(thirdParty, "unrelated", StandardCharsets.UTF_8);
    return repository;
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

  @Test
  public void mavenExamplesDistributionBuildProducesReproducibleZipBytes() throws Exception
  {
    File classes = new File(CDOAggregationTool.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    Path central = new File(classes.getParentFile(), "central").toPath();
    Path project = central.resolve("examples/distribution");
    Path settings = central.resolve("standalone-examples-settings.xml");
    Path output = temporaryFolder.newFolder().toPath().resolve("examples-maven-output.txt");
    List<String> baseCommand = standaloneMavenCommand();
    baseCommand.addAll(Arrays.asList("-gs", settings.toString(), "-s", settings.toString(), "-B", "-ntp", "-DskipTests", "-Drevision=1.0-SNAPSHOT",
        "-Dexamples.cdo.project.release=4.37.0", "-f", project.resolve("pom.xml").toString(), "package"));

    byte[][] archives = new byte[2][];
    for (int build = 0; build < archives.length; build++)
    {
      ProcessBuilder processBuilder = new ProcessBuilder(baseCommand).directory(project.toFile()).redirectErrorStream(true).redirectOutput(output.toFile());
      Process process;
      try
      {
        process = processBuilder.start();
      }
      catch (IOException ex)
      {
        assumeNoException("Maven executable is unavailable for examples reproducibility integration test", ex);
        return;
      }

      if (!process.waitFor(5, TimeUnit.MINUTES))
      {
        process.destroyForcibly();
        fail("Maven examples distribution build timed out: " + Files.readString(output, StandardCharsets.UTF_8));
      }

      String log = Files.readString(output, StandardCharsets.UTF_8);
      assertEquals(log, 0, process.exitValue());
      Path archive = project.resolve("target/cdo-examples-distribution-1.0-SNAPSHOT-examples.zip");
      assertTrue("Maven did not create the examples distribution ZIP", Files.isRegularFile(archive));
      archives[build] = MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(archive));
    }

    assertTrue("Unchanged examples inputs must produce byte-identical release ZIP files", Arrays.equals(archives[0], archives[1]));
  }

  private static List<String> standaloneMavenCommand()
  {
    String userProfile = System.getenv("USERPROFILE");
    if (userProfile != null)
    {
      Path wrapper = Path.of(userProfile, "Programs", "codex-mvn.ps1");
      if (Files.isRegularFile(wrapper))
      {
        return new ArrayList<>(Arrays.asList("powershell.exe", "-NoLogo", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", wrapper.toString()));
      }
    }

    if (File.separatorChar == '\\')
    {
      return new ArrayList<>(Arrays.asList("cmd.exe", "/c", "mvn.cmd"));
    }

    return new ArrayList<>(List.of("mvn"));
  }

  private static void writeMavenSelectionArtifact(Path repository, String artifact, String version, String className) throws Exception
  {
    Path directory = repository.resolve("org/eclipse/cdo/" + artifact + "/" + version);
    Files.createDirectories(directory);
    Files.writeString(directory.resolve(artifact + "-" + version + ".pom"), "<project><modelVersion>4.0.0</modelVersion>"
        + "<groupId>org.eclipse.cdo</groupId><artifactId>" + artifact + "</artifactId><version>" + version + "</version></project>", StandardCharsets.UTF_8);

    Path source = repository.resolve("fixture-src/fixture/" + className + ".java");
    Path classes = repository.resolve("fixture-classes");
    Files.createDirectories(source.getParent());
    Files.createDirectories(classes);
    Files.writeString(source, "package fixture; public final class " + className + " {}", StandardCharsets.UTF_8);
    assertNotNull("A JDK is required to build deterministic repository fixture artifacts", ToolProvider.getSystemJavaCompiler());
    assertEquals(0, ToolProvider.getSystemJavaCompiler().run(null, null, null, "-d", classes.toString(), source.toString()));

    Path jar = directory.resolve(artifact + "-" + version + ".jar");
    try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jar)))
    {
      output.putNextEntry(new JarEntry("fixture/" + className + ".class"));
      Files.copy(classes.resolve("fixture/" + className + ".class"), output);
      output.closeEntry();
    }
  }

  private static void writeCollisionArtifact(Path repository, String version) throws Exception
  {
    Path directory = repository.resolve("org/eclipse/cdo/org.eclipse.net4j.db.jdbc/" + version);
    Files.createDirectories(directory);
    Files.writeString(directory.resolve("org.eclipse.net4j.db.jdbc-" + version + ".pom"),
        "<project><groupId>org.eclipse.cdo</groupId>" + "<artifactId>org.eclipse.net4j.db.jdbc</artifactId><version>" + version + "</version></project>",
        StandardCharsets.UTF_8);
    Files.writeString(directory.resolve("org.eclipse.net4j.db.jdbc-" + version + ".jar"), "same version in both repositories", StandardCharsets.UTF_8);
  }

  private static String genericChildText(Element parent, String name)
  {
    NodeList children = parent.getElementsByTagNameNS("*", name);
    return children.getLength() == 0 ? "" : children.item(0).getTextContent().trim();
  }

  private static void createStandaloneModulePoms(Path examples) throws Exception
  {
    for (String module : new String[] { "embedded-h2", "remote-server", "remote-client" })
    {
      Path pom = examples.resolve(module + "/pom.xml");
      Files.createDirectories(pom.getParent());
      String dependencies = "embedded-h2".equals(module)
          ? "<dependencies><dependency><groupId>org.eclipse.cdo</groupId><artifactId>org.eclipse.emf.cdo.server.db</artifactId></dependency></dependencies>"
          : "";
      Files.writeString(pom, "<project><groupId>org.eclipse.cdo.examples</groupId><artifactId>" + module + "</artifactId>" + dependencies + "</project>",
          StandardCharsets.UTF_8);
    }
  }

  private static void writeResolverArtifact(Path repository, String artifact, String version, String packaging, String pomOrigin, String jarOrigin,
      boolean includeJar) throws Exception
  {
    Path directory = repository.resolve("org/eclipse/cdo/" + artifact + "/" + version);
    Files.createDirectories(directory);
    Path pom = directory.resolve(artifact + "-" + version + ".pom");
    Files.writeString(pom, "<project><groupId>org.eclipse.cdo</groupId><artifactId>" + artifact + "</artifactId><version>" + version + "</version><packaging>"
        + packaging + "</packaging></project>", StandardCharsets.UTF_8);
    Properties tracking = new Properties();
    tracking.setProperty(pom.getFileName() + ">" + pomOrigin, "");
    if (includeJar)
    {
      Path jar = directory.resolve(artifact + "-" + version + ".jar");
      Files.writeString(jar, "resolved jar", StandardCharsets.UTF_8);
      tracking.setProperty(jar.getFileName() + ">" + jarOrigin, "");
    }

    try (var output = Files.newOutputStream(directory.resolve("_remote.repositories")))
    {
      tracking.store(output, "Maven Resolver fixture");
    }
  }

  private static void createCandidate(Path repository, String artifact, String version) throws Exception
  {
    Path versionDirectory = repository.resolve("org/eclipse/cdo/" + artifact + "/" + version);
    Files.createDirectories(versionDirectory);
    Files.writeString(versionDirectory.resolve(artifact + "-" + version + ".pom"),
        "<project><groupId>org.eclipse.cdo</groupId><artifactId>" + artifact + "</artifactId><version>" + version + "</version></project>",
        StandardCharsets.UTF_8);
    Files.writeString(versionDirectory.resolve(artifact + "-" + version + ".jar"), "verified candidate fixture", StandardCharsets.UTF_8);
  }

  private static void createExamplesZip(Path workspace, Path distribution, Path zip) throws Exception
  {
    createExamplesZip(workspace, distribution, zip, null, false, false);
  }

  private static void createExamplesZip(Path workspace, Path distribution, Path zip, String companyVersion, boolean includeUnexpectedFile,
      boolean modifyJavaSource) throws Exception
  {
    createExamplesZip(workspace, distribution, zip, companyVersion, includeUnexpectedFile, modifyJavaSource, "4.36.0");
  }

  private static void createExamplesZip(Path workspace, Path distribution, Path zip, String companyVersion, boolean includeUnexpectedFile,
      boolean modifyJavaSource, String projectRelease) throws Exception
  {
    String[][] entries = { { "pom.xml", distribution.resolve("portable-pom.xml").toString() }, { "README.md", distribution.resolve("README.md").toString() },
        { "LICENSE", workspace.resolve("LICENSE").toString() }, { "embedded-h2/pom.xml", distribution.getParent().resolve("embedded-h2/pom.xml").toString() },
        { "remote-server/pom.xml", distribution.getParent().resolve("remote-server/pom.xml").toString() },
        { "remote-client/pom.xml", distribution.getParent().resolve("remote-client/pom.xml").toString() },
        { "embedded-h2/src/main/java/org/eclipse/emf/cdo/examples/plain/embedded/EmbeddedH2Example.java",
            workspace.resolve("plugins/org.eclipse.emf.cdo.examples/src/org/eclipse/emf/cdo/examples/plain/embedded/EmbeddedH2Example.java").toString() },
        { "remote-server/src/main/java/org/eclipse/emf/cdo/examples/plain/server/RemoteServer.java",
            workspace.resolve("plugins/org.eclipse.emf.cdo.examples/src/org/eclipse/emf/cdo/examples/plain/server/RemoteServer.java").toString() },
        { "remote-client/src/main/java/org/eclipse/emf/cdo/examples/plain/client/RemoteClient.java",
            workspace.resolve("plugins/org.eclipse.emf.cdo.examples/src/org/eclipse/emf/cdo/examples/plain/client/RemoteClient.java").toString() } };

    try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(zip)))
    {
      for (String[] entry : entries)
      {
        output.putNextEntry(new ZipEntry(entry[0]));
        if ("pom.xml".equals(entry[0]))
        {
          String portablePom = Files.readString(Path.of(entry[1]), StandardCharsets.UTF_8).replace("@examples.cdo.project.release@", projectRelease);
          if (companyVersion != null)
          {
            portablePom = portablePom.replace("<cdo.company.version>4.1.4</cdo.company.version>",
                "<cdo.company.version>" + companyVersion + "</cdo.company.version>");
          }

          output.write(portablePom.getBytes(StandardCharsets.UTF_8));
        }
        else if (modifyJavaSource && entry[0].equals("remote-client/src/main/java/org/eclipse/emf/cdo/examples/plain/client/RemoteClient.java"))
        {
          byte[] source = Files.readAllBytes(Path.of(entry[1]));
          byte[] modifiedSource = Arrays.copyOf(source, source.length + 1);
          modifiedSource[modifiedSource.length - 1] = '\n';
          output.write(modifiedSource);
        }
        else
        {
          Files.copy(Path.of(entry[1]), output);
        }

        output.closeEntry();
      }

      if (includeUnexpectedFile)
      {
        output.putNextEntry(new ZipEntry("unexpected.txt"));
        output.write("unexpected file".getBytes(StandardCharsets.UTF_8));
        output.closeEntry();
      }
    }
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

  /**
   * @author Eike Stepper
   */
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

    void loadProposedArtifacts(Path root) throws Exception
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

      String examplesVersion = examplesDistributionVersion();
      Path examplesDirectory = root.resolve("bundles/org/eclipse/cdo/cdo-examples-distribution/" + examplesVersion);
      String examplesPrefix = "org/eclipse/cdo/cdo-examples-distribution/" + examplesVersion + "/cdo-examples-distribution-" + examplesVersion;
      artifacts.put(examplesPrefix + ".pom", Files.readAllBytes(examplesDirectory.resolve("cdo-examples-distribution-" + examplesVersion + ".pom")));
      artifacts.put(examplesPrefix + "-examples.zip",
          Files.readAllBytes(examplesDirectory.resolve("cdo-examples-distribution-" + examplesVersion + "-examples.zip")));

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
