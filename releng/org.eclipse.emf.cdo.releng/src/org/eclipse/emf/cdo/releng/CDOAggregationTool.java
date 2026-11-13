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

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Standard-JDK support used by the Ant orchestration; no CDO build logic lives here.
 *
 * @author Eike Stepper
 */
public final class CDOAggregationTool
{
  private static final List<String> BUNDLES = Arrays.asList( //
      "org.eclipse.net4j.util", //
      "org.eclipse.net4j", //
      "org.eclipse.net4j.tcp", //
      "org.eclipse.net4j.db", //
      "org.eclipse.net4j.db.jdbc", //
      "org.eclipse.net4j.db.h2", //
      "org.eclipse.emf.cdo.common", //
      "org.eclipse.emf.cdo", //
      "org.eclipse.emf.cdo.net4j", //
      "org.eclipse.emf.cdo.server", //
      "org.eclipse.emf.cdo.server.net4j", //
      "org.eclipse.emf.cdo.server.db", //
      "org.eclipse.net4j.jvm", //
      "org.eclipse.net4j.ws", //
      "org.eclipse.net4j.ws.jetty.server", //
      "org.eclipse.net4j.db.postgresql", //
      "org.eclipse.emf.cdo.server.embedded", //
      "org.eclipse.emf.cdo.expressions", //
      "org.eclipse.emf.cdo.security", //
      "org.eclipse.emf.cdo.server.security", //
      "org.eclipse.emf.cdo.examples.company");

  private static final List<String> BUNDLES_436 = List.of("org.eclipse.net4j.util", "org.eclipse.net4j", "org.eclipse.net4j.tcp", "org.eclipse.net4j.db",
      "org.eclipse.net4j.db.jdbc", "org.eclipse.net4j.db.h2", "org.eclipse.emf.cdo.common", "org.eclipse.emf.cdo", "org.eclipse.emf.cdo.net4j",
      "org.eclipse.emf.cdo.server", "org.eclipse.emf.cdo.server.net4j", "org.eclipse.emf.cdo.server.db");

  private static final List<String> CLASSIFIERS = Arrays.asList("sources", "javadoc");

  private static final String EXAMPLES_DISTRIBUTION = "cdo-examples-distribution";

  private static final String EXAMPLES_DISTRIBUTION_VERSION_LINE = loadExamplesDistributionVersionLine();

  private static String EXAMPLES_DISTRIBUTION_VERSION = EXAMPLES_DISTRIBUTION_VERSION_LINE + ".0";

  private static final String RELEASE_BOM = "org.eclipse.cdo.bom";

  private static final String CDO_LOCATION_PROPERTY = "org.eclipse.cbi.p2repo.cdo-location";

  private static final String SIMREL_LOCATION_PROPERTY = "org.eclipse.cbi.p2repo.simrel-location";

  private static final Set<String> INTERNAL_CBI_PROPERTIES = Set.of(CDO_LOCATION_PROPERTY, SIMREL_LOCATION_PROPERTY);

  private static final Pattern R_DROP_ID = Pattern.compile("R[0-9]{8}-[0-9]{4}");

  private static final Pattern OSGI_VERSION = Pattern.compile("([0-9]+)\\.([0-9]+)\\.([0-9]+)(?:\\.([A-Za-z0-9_-]+))?");

  private static final Pattern MAJOR_VERSION_RANGE = Pattern.compile("\\[([0-9]+)\\.0\\.0,([0-9]+)\\.0\\.0\\)");

  private static final Pattern VERSION_MAJOR = Pattern.compile("([0-9]+)\\.[0-9]+\\.[0-9]+(?:[.-].*)?");

  private static final Pattern MAVEN_RELEASE_TRICK = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+(?:\\.[0-9]+-[a-zA-Z][a-zA-Z0-9_]*)?");

  private CDOAggregationTool()
  {
  }

  private static String loadExamplesDistributionVersionLine()
  {
    List<Path> candidates = List.of(Paths.get("examples/distribution/package.properties"), Paths.get("central/examples/distribution/package.properties"),
        Paths.get("releng/org.eclipse.emf.cdo.releng/central/examples/distribution/package.properties"));
    for (Path candidate : candidates)
    {
      if (Files.isRegularFile(candidate))
      {
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(candidate))
        {
          properties.load(input);
        }
        catch (IOException ex)
        {
          throw new ExceptionInInitializerError(ex);
        }

        String versionLine = properties.getProperty("release.version.line");
        if (versionLine == null || !versionLine.matches("[0-9]+\\.[0-9]+"))
        {
          throw new ExceptionInInitializerError("Invalid release.version.line in " + candidate);
        }

        return versionLine;
      }
    }

    throw new ExceptionInInitializerError("Unable to locate examples/distribution/package.properties");
  }

  private static void loadSelectedExamplesDistributionVersion(String[] args) throws IOException
  {
    EXAMPLES_DISTRIBUTION_VERSION = EXAMPLES_DISTRIBUTION_VERSION_LINE + ".0";
    String selectedVersion = System.getProperty("cdo.examples.distribution.version");
    if (selectedVersion == null)
    {
      for (String argument : args)
      {
        if (argument.endsWith(".properties") && Files.isRegularFile(Paths.get(argument)))
        {
          Properties properties = new Properties();
          try (InputStream input = Files.newInputStream(Paths.get(argument)))
          {
            properties.load(input);
          }

          selectedVersion = properties.getProperty("examples.distribution.version");
          if (selectedVersion != null)
          {
            break;
          }
        }
      }
    }

    if (selectedVersion != null)
    {
      if (!selectedVersion.matches(Pattern.quote(EXAMPLES_DISTRIBUTION_VERSION_LINE) + "\\.[0-9]+"))
      {
        throw new IllegalArgumentException(
            "Selected examples distribution version is outside configured line " + EXAMPLES_DISTRIBUTION_VERSION_LINE + ": " + selectedVersion);
      }

      EXAMPLES_DISTRIBUTION_VERSION = selectedVersion;
    }
  }

  public static void main(String[] args) throws Exception
  {
    loadSelectedExamplesDistributionVersion(args);

    if (args.length == 0)
    {
      throw new IllegalArgumentException(
          "Usage: prepare|prepare-metadata-test|overlay|generate-bom|stage-release-bom|prepare-standalone-examples|verify-standalone-provenance|generate-bom-436|stage-bom-436|check-bom-central|verify-bom-package|classify-central|verify|platform-version-range-test|consumer-versions|consumer-dependency-tree-check|consumer-dependency-tree-test|derive-release-inventory|central-verify-prepare|central-verify-report|resolve-drop|drop-selection-test|native-classifier-validation|checksums|audit|select-examples-version|stage-examples-distribution|publish-check|publication-scope-check|verify-package|resolve-signing-subkey-fingerprint|signing-subkey-test|publish-policy|policy-test|stream-test|signature-test|workspace-safety-test|publication-scope-test|native-classifier-test|safe-delete-work|validate-workspace-path ...");
    }

    switch (args[0])
    {
    case "prepare":
      PrepareCommand.execute(args);
      break;

    case "normalize-release":
      if (args.length != 3)
      {
        throw new IllegalArgumentException("normalize-release requires build metadata and output file");
      }

      Map<String, String> releaseMetadata = readProperties(requireFile(new File(args[1]), "build metadata"));
      String projectRelease = releaseMetadata.get("cdo.project.release");
      if (projectRelease == null || projectRelease.isBlank())
      {
        throw new IllegalArgumentException(
            "PACKAGE examples distribution requires cdo.project.release; supply OVERRIDE_VERSION when the selected drop has no web.label");
      }

      String normalizedRelease = BomCommand.normalizeProjectReleaseVersion(projectRelease);
      Files.writeString(Path.of(args[2]), normalizedRelease, StandardCharsets.UTF_8);
      System.out.println("Resolved examples CDO project release " + normalizedRelease + " from " + releaseMetadata.get("cdo.project.release.source"));
      break;

    case "prepare-metadata-test":
      prepareMetadataTest(args);
      break;

    case "drop-info":
      verifyDropInfo(args);
      break;

    case "derive-release-inventory":
      deriveReleaseInventory(args);
      break;

    case "overlay":
      OverlayCommand.execute(args);
      break;

    case "generate-bom":
      BomCommand.execute(args);
      break;

    case "stage-release-bom":
      stageReleaseBom(args);
      break;

    case "prepare-standalone-examples":
      prepareStandaloneExamples(args);
      break;

    case "verify-standalone-provenance":
      verifyStandaloneArtifactProvenance(args);
      break;

    case "generate-bom-436":
      Bom436Command.generate(args);
      break;

    case "stage-bom-436":
      Bom436Command.stage(args);
      break;

    case "check-bom-central":
      Bom436Command.checkCentral(args);
      break;

    case "verify-bom-package":
      verifyBomPackage(args);
      break;

    case "classify-central":
      CentralClassificationCommand.execute(args);
      break;

    case "verify":
      VerifyCommand.execute(args);
      break;

    case "platform-version-range-test":
      platformVersionRangeTest(args);
      break;

    case "consumer-versions":
      ConsumerVersionsCommand.execute(args);
      break;

    case "consumer-dependency-tree-check":
      consumerDependencyTreeCheck(args);
      break;

    case "consumer-dependency-tree-test":
      consumerDependencyTreeTest(args);
      break;

    case "central-verify-prepare":
      CentralVerificationTool.prepare(args);
      break;

    case "central-verify-report":
      CentralVerificationTool.report(args);
      break;

    case "native-classifier-validation":
      NativeClassifierValidationCommand.execute(args);
      break;

    case "native-classifier-test":
      nativeClassifierTest(args);
      break;

    case "resolve-drop":
      resolveDrop(args);
      break;

    case "drop-selection-test":
      dropSelectionTest(args);
      break;

    case "checksums":
      ChecksumsCommand.execute(args);
      break;

    case "audit":
      AuditCommand.execute(args);
      break;

    case "stage-examples-distribution":
      ExamplesDistributionCommand.execute(args);
      break;

    case "select-examples-version":
      ExamplesDistributionVersionCommand.execute(args);
      break;

    case "publish-check":
      PublishCheckCommand.execute(args);
      break;

    case "publication-scope-check":
      publicationScopeCheck(args);
      break;

    case "publication-scope-test":
      publicationScopeTest(args);
      break;

    case "verify-package":
      verifyPackage(args);
      break;

    case "incremental-package":
      incrementalPackage(args);
      break;

    case "resolve-signing-subkey-fingerprint":
      resolveSigningSubkeyFingerprint(args);
      break;

    case "signing-subkey-test":
      signingSubkeyFingerprintTest();
      break;

    case "signature-test":
      signatureTest(args);
      break;

    case "stream-test":
      streamComparisonTest(args);
      break;

    case "workspace-safety-test":
      workspaceSafetyTest(args);
      break;

    case "safe-delete-work":
      safeDeleteWork(args);
      break;

    case "validate-workspace-path":
      validateWorkspacePath(args);
      break;

    case "publish-policy":
      PublishPolicyCommand.execute(args);
      break;

    case "policy-test":
      PublishPolicyCommand.test();
      break;

    default:
      throw new IllegalArgumentException("Unknown command: " + args[0]);
    }
  }

  static boolean isAllowedSignedBomRepositoryPath(String relative, Set<String> signedFilesAllowed)
  {
    String bomArtifactRoot = "org/eclipse/cdo/" + Bom436Command.BOM_ARTIFACT;
    return signedFilesAllowed.contains(relative) || relative.startsWith(bomArtifactRoot + "/") && isMavenMetadataPath(relative);
  }

  static List<String> approvedBundles()
  {
    return List.copyOf(BUNDLES);
  }

  static Document parse(File file) throws Exception
  {
    return parse(new FileInputStream(file), file.getPath());
  }

  /**
   * Implements {@code prepare}: args are command, drop, metadata, repositories.
   *
   * @author Eike Stepper
   */
  private static final class PrepareCommand
  {
    private static final LinkedHashMap<String, String> ATTRIBUTES = new LinkedHashMap<>();

    static
    {
      ATTRIBUTES.put("qualifier", "cdo.drop.id");
      ATTRIBUTES.put("revision", "cdo.git.commit");
      ATTRIBUTES.put("branch", "cdo.build.branch");
      ATTRIBUTES.put("eclipse", "cdo.eclipse.version");
      ATTRIBUTES.put("emf", "cdo.emf.version");
      ATTRIBUTES.put("hudson", "cdo.jenkins.url");
      ATTRIBUTES.put("job", "cdo.jenkins.job");
      ATTRIBUTES.put("number", "cdo.jenkins.build");
      ATTRIBUTES.put("stream", "cdo.build.stream");
      ATTRIBUTES.put("timestamp", "cdo.build.timestamp");
      ATTRIBUTES.put("train", "cdo.eclipse.simrel");
      ATTRIBUTES.put("trigger", "cdo.build.trigger");
      ATTRIBUTES.put("type", "cdo.build.type");

    }

    public static void execute(String[] args) throws Exception
    {
      if (args.length != 4 && args.length != 5)
      {
        throw new IllegalArgumentException("prepare requires drop, metadata, repositories");
      }

      requireWorkspaceOutput(new File(args[2]), "build metadata output");
      requireWorkspaceOutput(new File(args[3]), "validation repository list output");
      DropLocation drop = DropLocation.parse(args[1]);

      Map<String, String> values = readBuildMetadata(drop, args.length == 5 ? args[4] : "");
      String dropID = values.get("cdo.drop.id");

      LinkedHashSet<String> repositories = extractRepositories(drop.open("tp-macro.setup"));
      if (repositories.isEmpty())
      {
        throw new IllegalArgumentException("No repository locations found in tp-macro.setup at drop location " + drop.location());
      }

      Map<String, String> derived = new LinkedHashMap<>(values);
      String dropSource = repositories.stream()
          .filter(repository -> repository.contains(dropID) || repository.equals("https://download.eclipse.org/modeling/emf/cdo/updates")).findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Target-platform CDO repository is missing"));
      String dropRepository = validationLocation(dropSource, dropID, drop);
      String simrelRepository = repositories.stream().filter(PrepareCommand::isSimrelRepository).findFirst()
          .map(repository -> validationLocation(repository, dropID, drop))
          .orElseThrow(() -> new IllegalArgumentException("Target-platform SimRel repository is missing"));
      derived.put(CDO_LOCATION_PROPERTY, dropRepository);
      derived.put(SIMREL_LOCATION_PROPERTY, simrelRepository);

      writeProperties(Path.of(args[2]), derived);
      Files.write(Path.of(args[3]), List.of(simrelRepository), StandardCharsets.UTF_8);
      System.out.println(CDO_LOCATION_PROPERTY + "=" + dropRepository);
      System.out.println(SIMREL_LOCATION_PROPERTY + "=" + simrelRepository);
      System.out.println("Validated CDO and SimRel repositories; build=" + dropID);
    }

    private static Map<String, String> readBuildMetadata(DropLocation drop) throws Exception
    {
      return readBuildMetadata(drop, "");
    }

    private static Map<String, String> readBuildMetadata(DropLocation drop, String override) throws Exception
    {
      Document info;
      try (InputStream input = drop.open("build-info.xml"))
      {
        info = parse(input, "build-info.xml");
      }

      Element build = info.getDocumentElement();
      if (!"build".equals(build.getNodeName()))
      {
        throw new IllegalArgumentException("build-info.xml root is not <build>");
      }

      Map<String, String> values = new LinkedHashMap<>();
      for (Map.Entry<String, String> attribute : ATTRIBUTES.entrySet())
      {
        values.put(attribute.getValue(), required(build, attribute.getKey()));
      }

      String dropID = values.get("cdo.drop.id");
      if (dropID.isBlank())
      {
        throw new IllegalArgumentException("build/@qualifier must not be empty");
      }

      String buildType = values.get("cdo.build.type");
      if (!"R".equals(buildType) && !"S".equals(buildType) && !"I".equals(buildType))
      {
        throw new IllegalArgumentException("Unsupported CDO build type: " + buildType);
      }

      if (!override.isEmpty() && !override.matches("[0-9]+(?:\\.[0-9]+){1,2}"))
      {
        throw new IllegalArgumentException("OVERRIDE_VERSION must be a numeric major.minor or major.minor.patch version");
      }

      Properties webProperties = new Properties();
      try (InputStream input = drop.openOptional("web.properties"))
      {
        if (input != null)
        {
          webProperties.load(input);
        }
      }

      String projectRelease = webProperties.getProperty("web.label");
      if (projectRelease != null && !projectRelease.matches("[0-9]+(?:\\.[0-9]+){1,2}"))
      {
        throw new IllegalArgumentException("web.properties defines a malformed numeric web.label project-release version");
      }

      if (projectRelease == null)
      {
        projectRelease = override;
        if (!projectRelease.isEmpty())
        {
          values.put("cdo.project.release.source", "OVERRIDE");
        }
      }
      else
      {
        if (!override.isEmpty() && !BomCommand.normalizeProjectReleaseVersion(projectRelease).equals(BomCommand.normalizeProjectReleaseVersion(override)))
        {
          throw new IllegalArgumentException("OVERRIDE_VERSION conflicts with the selected drop web.label");
        }

        values.put("cdo.project.release.source", "DROP");
      }

      if (!projectRelease.isEmpty())
      {
        String stream = values.get("cdo.build.stream");
        if (stream.matches("[0-9]+\\.x") && !projectRelease.split("\\.")[0].equals(stream.substring(0, stream.indexOf('.'))))
        {
          throw new IllegalArgumentException("Project release major does not match build-info.xml stream " + stream);
        }

        values.put("cdo.project.release", projectRelease);
      }

      return values;
    }

    private static boolean isSimrelRepository(String repository)
    {
      String value = repository.toLowerCase();
      return value.contains("download.eclipse.org/releases/") || value.contains("simrel");
    }
  }

  /**
   * Implements {@code overlay}: args are command, aggregated repository,
   * build metadata properties, and stable publishing metadata properties.
   *
   * @author Eike Stepper
   */
  private static final class OverlayCommand
  {
    public static void execute(String[] args) throws Exception
    {
      if (args.length != 4)
      {
        throw new IllegalArgumentException("overlay requires repository, build metadata, publishing metadata");
      }

      File root = requireDirectory(args[1], "aggregated repository");
      requireWorkspaceOutput(root, "aggregated repository");
      rejectSymbolicLinks(root.toPath(), "aggregated repository");
      Map<String, String> values = readProperties(requireFile(new File(args[2]), "metadata properties"));
      Map<String, String> publishing = readProperties(requireFile(new File(args[3]), "publishing metadata"));

      List<File> poms = find(root, ".pom");
      int overlaid = 0;
      for (File file : poms)
      {
        Document document = parse(file);
        Element project = document.getDocumentElement();
        String group = text(project, "groupId");
        String artifact = text(project, "artifactId");
        if (!"org.eclipse.cdo".equals(group) || !BUNDLES.contains(artifact))
        {
          continue;
        }

        Element properties = child(project, "properties");
        if (properties == null)
        {
          properties = document.createElementNS(project.getNamespaceURI(), "properties");
          Node modelVersion = child(project, "modelVersion");
          project.insertBefore(properties, modelVersion == null ? project.getFirstChild() : modelVersion.getNextSibling());
        }

        removeInternalCbiProperties(project);

        if (countChildren(properties, "cdo.drop.id") > 1)
        {
          throw new IllegalArgumentException("Multiple cdo.drop.id properties found in " + file);
        }

        for (Map.Entry<String, String> entry : values.entrySet())
        {
          if (INTERNAL_CBI_PROPERTIES.contains(entry.getKey()))
          {
            continue;
          }

          Element property = child(properties, entry.getKey());
          if ("cdo.drop.id".equals(entry.getKey()) && property != null && !entry.getValue().equals(text(property)))
          {
            throw new IllegalArgumentException("Existing cdo.drop.id property does not match the selected drop in " + file + ": " + text(property));
          }

          if (property == null)
          {
            property = document.createElementNS(project.getNamespaceURI(), entry.getKey());
            properties.appendChild(property);
          }

          property.setTextContent(entry.getValue());
        }

        set(project, "url", publishing.get("project.url"));
        Element organization = ensure(project, "organization");
        set(organization, "name", publishing.get("organization.name"));
        set(organization, "url", publishing.get("organization.url"));

        Element licenses = ensure(project, "licenses");
        Element license = ensure(licenses, "license");
        set(license, "name", publishing.get("license.name"));
        set(license, "url", publishing.get("license.url"));
        set(license, "distribution", publishing.get("license.distribution"));

        Element developers = ensure(project, "developers");
        Element developer = ensure(developers, "developer");
        set(developer, "url", publishing.get("developers.url"));

        Element issues = ensure(project, "issueManagement");
        set(issues, "system", publishing.get("issue.system"));
        set(issues, "url", publishing.get("issue.url"));

        Element scm = ensure(project, "scm");
        set(scm, "connection", publishing.get("scm.connection"));
        set(scm, "developerConnection", publishing.get("scm.developerConnection"));
        set(scm, "tag", values.get("cdo.git.commit"));
        set(scm, "url", publishing.get("project.url"));

        write(document, file);
        ChecksumsCommand.writeChecksums(file);
        overlaid++;
      }

      if (overlaid == 0)
      {
        throw new IllegalArgumentException("No approved CDO POMs found under " + root);
      }

      System.out.println("Added " + publicationMetadata(values).size() + " traceability properties to " + overlaid + " approved CDO POMs.");
    }

    private static Element ensure(Element parent, String name)
    {
      Element result = child(parent, name);
      if (result == null)
      {
        result = parent.getOwnerDocument().createElementNS(parent.getNamespaceURI(), name);
        parent.appendChild(result);
      }
      return result;
    }

    private static void set(Element parent, String name, String value)
    {
      if (value != null && !value.isBlank())
      {
        ensure(parent, name).setTextContent(value);
      }
    }
  }

  /**
   * Generates a standalone temporary project-release BOM repository without changing bundle staging.
   *
   * @author Eike Stepper
   */
  private static final class BomCommand
  {
    private static final String MAVEN_NAMESPACE = "http://maven.apache.org/POM/4.0.0";

    public static void execute(String[] args) throws Exception
    {
      if (args.length != 6)
      {
        throw new IllegalArgumentException(
            "generate-bom requires bundle repository, build metadata, publishing metadata, output repository, and selected drop location");
      }

      File bundles = requireDirectory(args[1], "approved bundle repository");
      File metadataFile = requireFile(new File(args[2]), "build metadata");
      File publishingFile = requireFile(new File(args[3]), "publishing metadata");
      File output = new File(args[4]);
      requireWorkspaceOutput(bundles, "approved bundle repository");
      requireWorkspaceOutput(output, "BOM output repository");
      rejectSymbolicLinks(bundles.toPath(), "approved bundle repository");
      if (output.exists())
      {
        rejectSymbolicLinks(output.toPath(), "BOM output repository");
      }

      Path bundlePath = bundles.toPath().toAbsolutePath().normalize();
      Path outputPath = output.toPath().toAbsolutePath().normalize();
      if (bundlePath.startsWith(outputPath) || outputPath.startsWith(bundlePath))
      {
        throw new IllegalArgumentException("BOM output repository must be separate from the approved bundle repository");
      }

      Map<String, String> metadata = readProperties(metadataFile);
      Map<String, String> publishing = readProperties(publishingFile);
      String release = requiredMetadata(metadata, "cdo.project.release");
      String dropID = requiredMetadata(metadata, "cdo.drop.id");
      String version = normalizeProjectReleaseVersion(release);
      DropLocation drop = DropLocation.parse(args[5]);
      Properties webProperties = new Properties();
      try (InputStream input = drop.openOptional("web.properties"))
      {
        if (input != null)
        {
          webProperties.load(input);
        }
      }

      String selectedRelease = webProperties.getProperty("web.label");
      if (selectedRelease != null && !selectedRelease.isBlank()
          && !normalizeProjectReleaseVersion(release).equals(normalizeProjectReleaseVersion(selectedRelease)))
      {
        throw new IllegalArgumentException("BOM project-release metadata " + release + " does not match selected drop web.label " + selectedRelease);
      }

      if ((selectedRelease == null || selectedRelease.isBlank()) && !"OVERRIDE".equals(metadata.get("cdo.project.release.source")))
      {
        throw new IllegalArgumentException("Selected drop has no web.label and release metadata is not marked as an explicit override");
      }

      Document buildInfo = parse(drop.open("build-info.xml"), "build-info.xml");
      String selectedDropID = required(buildInfo.getDocumentElement(), "qualifier");
      if (!dropID.equals(selectedDropID))
      {
        throw new IllegalArgumentException("BOM drop-ID metadata " + dropID + " does not match selected drop qualifier " + selectedDropID);
      }

      if (!dropID.matches("[A-Za-z][A-Za-z0-9._-]*"))
      {
        throw new IllegalArgumentException("Invalid cdo.drop.id metadata: " + dropID);
      }

      File pom = generate(bundles, output, metadata, publishing, version);
      System.out.println("Generated standalone CDO BOM POM " + pom + " for release " + release + " from drop " + dropID + ".");
    }

    static String normalizeProjectReleaseVersion(String release)
    {
      if (release == null || !release.matches("[0-9]+\\.[0-9]+(?:\\.[0-9]+)?"))
      {
        throw new IllegalArgumentException("Invalid web.label project-release version: " + release);
      }

      return release.split("\\.").length == 2 ? release + ".0" : release;
    }

    private static File generate(File bundles, File output, Map<String, String> metadata, Map<String, String> publishing, String version) throws Exception
    {
      return generate(bundles, output, metadata, publishing, version, BUNDLES);
    }

    private static File generate(File bundles, File output, Map<String, String> metadata, Map<String, String> publishing, String version, List<String> approved)
        throws Exception
    {
      Map<String, String> bundleVersions = new LinkedHashMap<>();
      for (String bundle : approved)
      {
        File artifactDirectory = new File(bundles, "org/eclipse/cdo/" + bundle);
        File[] versions = artifactDirectory.listFiles(File::isDirectory);
        if (versions == null || versions.length != 1)
        {
          throw new IllegalArgumentException(
              "Expected exactly one actual Maven version directory for approved BOM member " + bundle + " in " + artifactDirectory);
        }

        String bundleVersion = versions[0].getName();
        File bundlePom = new File(versions[0], bundle + "-" + bundleVersion + ".pom");
        if (!bundlePom.isFile())
        {
          throw new IllegalArgumentException("Missing bundle POM for BOM member " + bundle + " at " + bundlePom);
        }

        Element bundleProject = parse(bundlePom).getDocumentElement();
        String actualCoordinate = text(bundleProject, "groupId") + ":" + text(bundleProject, "artifactId") + ":" + text(bundleProject, "version");
        String expectedCoordinate = "org.eclipse.cdo:" + bundle + ":" + bundleVersion;
        if (!expectedCoordinate.equals(actualCoordinate))
        {
          throw new IllegalArgumentException(
              "Bundle POM coordinate mismatch for BOM member " + bundle + ": expected " + expectedCoordinate + " but found " + actualCoordinate);
        }

        bundleVersions.put(bundle, bundleVersion);
      }

      return generate(output, metadata, publishing, version, bundleVersions, approved);
    }

    private static File generate(File output, Map<String, String> metadata, Map<String, String> publishing, String version, Map<String, String> bundleVersions,
        List<String> approved) throws Exception
    {
      String release = requiredMetadata(metadata, "cdo.project.release");
      String dropID = requiredMetadata(metadata, "cdo.drop.id");
      if (!bundleVersions.keySet().equals(new LinkedHashSet<>(approved)))
      {
        throw new IllegalArgumentException("BOM source inventory does not contain exactly the approved bundle coordinates: " + bundleVersions.keySet());
      }

      File pom = new File(output, "org/eclipse/cdo/org.eclipse.cdo.bom/" + version + "/org.eclipse.cdo.bom-" + version + ".pom");
      if (pom.exists())
      {
        throw new IllegalArgumentException("BOM output coordinate already exists: org.eclipse.cdo:org.eclipse.cdo.bom:" + version);
      }

      Document document = newDocument();
      Element project = element(document, "project");
      project.setAttribute("xmlns", MAVEN_NAMESPACE);
      project.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
      project.setAttribute("xsi:schemaLocation", MAVEN_NAMESPACE + " https://maven.apache.org/xsd/maven-4.0.0.xsd");
      document.appendChild(project);
      append(document, project, "modelVersion", "4.0.0");
      append(document, project, "groupId", "org.eclipse.cdo");
      append(document, project, "artifactId", "org.eclipse.cdo.bom");
      append(document, project, "version", version);
      append(document, project, "packaging", "pom");
      append(document, project, "name", "CDO Maven BOM");
      append(document, project, "description", "Dependency management for CDO project release " + release);
      append(document, project, "url", requiredMetadata(publishing, "project.url"));
      appendOrganization(document, project, publishing);
      appendLicenses(document, project, publishing);
      appendDevelopers(document, project, publishing);
      appendIssues(document, project, publishing);
      appendScm(document, project, publishing, metadata);

      Element properties = append(document, project, "properties", null);
      append(document, properties, "cdo.project.release", release);
      append(document, properties, "cdo.project.release.source", metadata.getOrDefault("cdo.project.release.source", "DROP"));
      append(document, properties, "cdo.drop.id", dropID);
      Element management = append(document, project, "dependencyManagement", null);
      Element dependencies = append(document, management, "dependencies", null);
      for (String bundle : approved)
      {
        Element dependency = append(document, dependencies, "dependency", null);
        append(document, dependency, "groupId", "org.eclipse.cdo");
        append(document, dependency, "artifactId", bundle);
        append(document, dependency, "version", bundleVersions.get(bundle));
      }

      Files.createDirectories(pom.toPath().getParent());
      write(document, pom);
      ChecksumsCommand.writeChecksums(pom);
      return pom;
    }

    private static void appendOrganization(Document document, Element project, Map<String, String> publishing)
    {
      Element organization = append(document, project, "organization", null);
      append(document, organization, "name", requiredMetadata(publishing, "organization.name"));
      append(document, organization, "url", requiredMetadata(publishing, "organization.url"));
    }

    private static void appendLicenses(Document document, Element project, Map<String, String> publishing)
    {
      Element licenses = append(document, project, "licenses", null);
      Element license = append(document, licenses, "license", null);
      append(document, license, "name", requiredMetadata(publishing, "license.name"));
      append(document, license, "url", requiredMetadata(publishing, "license.url"));
      append(document, license, "distribution", requiredMetadata(publishing, "license.distribution"));
    }

    private static void appendDevelopers(Document document, Element project, Map<String, String> publishing)
    {
      Element developers = append(document, project, "developers", null);
      Element developer = append(document, developers, "developer", null);
      append(document, developer, "url", requiredMetadata(publishing, "developers.url"));
    }

    private static void appendIssues(Document document, Element project, Map<String, String> publishing)
    {
      Element issues = append(document, project, "issueManagement", null);
      append(document, issues, "system", requiredMetadata(publishing, "issue.system"));
      append(document, issues, "url", requiredMetadata(publishing, "issue.url"));
    }

    private static void appendScm(Document document, Element project, Map<String, String> publishing, Map<String, String> metadata)
    {
      Element scm = append(document, project, "scm", null);
      append(document, scm, "connection", requiredMetadata(publishing, "scm.connection"));
      append(document, scm, "developerConnection", requiredMetadata(publishing, "scm.developerConnection"));
      append(document, scm, "tag", requiredMetadata(metadata, "cdo.git.commit"));
      append(document, scm, "url", requiredMetadata(publishing, "project.url"));
    }

    private static Element append(Document document, Element parent, String name, String value)
    {
      Element child = element(document, name);
      if (value != null)
      {
        child.setTextContent(value);
      }

      parent.appendChild(child);
      return child;
    }

    private static Element element(Document document, String name)
    {
      return document.createElementNS(MAVEN_NAMESPACE, name);
    }

    private static String requiredMetadata(Map<String, String> properties, String key)
    {
      String value = properties.get(key);
      if (value == null || value.isBlank())
      {
        throw new IllegalArgumentException("Missing required publishing metadata: " + key);
      }

      return value;
    }
  }

  /**
   * Implements the isolated one-time CDO 4.36 BOM retrofit path.
   *
   * @author Eike Stepper
   */
  private static final class Bom436Command
  {
    private static final String RELEASE = "4.36";

    private static final String DROP_ID = "R20260901-1311";

    private static final String BOM_ARTIFACT = "org.eclipse.cdo.bom";

    private static final String[] CHECKSUMS = { "md5", "sha1", "sha256", "sha512" };

    public static void generate(String[] args) throws Exception
    {
      if (args.length != 5)
      {
        throw new IllegalArgumentException("generate-bom-436 requires metadata, publishing metadata, output repository, and selected drop");
      }

      Map<String, String> metadata = readProperties(requireFile(new File(args[1]), "build metadata"));
      Map<String, String> publishing = readProperties(requireFile(new File(args[2]), "publishing metadata"));
      Map<String, String> inventory = verifySelectedDrop(metadata, args[4]);
      Map<String, String> versions = releaseInventoryVersions(inventory, BUNDLES_436);
      File output = new File(args[3]);
      requireWorkspaceOutput(output, "CDO 4.36 BOM repository");
      if (output.exists())
      {
        rejectSymbolicLinks(output.toPath(), "CDO 4.36 BOM repository");
      }

      File pom = BomCommand.generate(output, metadata, publishing, inventory.get("bom.version"), versions, BUNDLES_436);
      validatePom(pom, metadata, publishing, versions, BUNDLES_436);
      System.out.println("Generated the isolated CDO 4.36 BOM from release-drop metadata: " + pom);
    }

    public static void stage(String[] args) throws Exception
    {
      if (args.length != 6)
      {
        throw new IllegalArgumentException("stage-bom-436 requires BOM repository, build metadata, publishing metadata, selected drop, and staging directory");
      }

      File repository = requireDirectory(args[1], "generated CDO 4.36 BOM repository");
      requireWorkspaceOutput(repository, "generated CDO 4.36 BOM repository");
      rejectSymbolicLinks(repository.toPath(), "generated CDO 4.36 BOM repository");
      Map<String, String> metadata = readProperties(requireFile(new File(args[2]), "build metadata"));
      Map<String, String> publishing = readProperties(requireFile(new File(args[3]), "publishing metadata"));
      Map<String, String> inventory = verifySelectedDrop(metadata, args[4]);
      Map<String, String> versions = releaseInventoryVersions(inventory, BUNDLES_436);
      File source = bomPom(repository);
      validatePom(source, metadata, publishing, versions, BUNDLES_436);
      File staging = new File(args[5]);
      requireWorkspaceOutput(staging, "CDO 4.36 BOM staging");
      if (staging.exists())
      {
        rejectSymbolicLinks(staging.toPath(), "CDO 4.36 BOM staging");
        try (Stream<Path> entries = Files.list(staging.toPath()))
        {
          if (entries.findAny().isPresent())
          {
            throw new IllegalArgumentException("CDO 4.36 BOM staging must be absent or empty: " + staging);
          }
        }
      }

      String relativePom = bomRelativePom();
      File target = new File(staging, relativePom.replace('/', File.separatorChar));
      Files.createDirectories(target.toPath().getParent());
      Files.copy(source.toPath(), target.toPath());
      ChecksumsCommand.writeChecksums(target);
      validateStaging(staging, metadata, publishing, versions, false, BUNDLES_436);
      System.out.println("Prepared POM-only CDO 4.36 BOM staging with four verified checksum sidecars.");
    }

    public static void checkCentral(String[] args) throws Exception
    {
      if (args.length != 4)
      {
        throw new IllegalArgumentException("check-bom-central requires build metadata, selected drop, and Central repository URL");
      }

      Map<String, String> metadata = readProperties(requireFile(new File(args[1]), "build metadata"));
      verifySelectedDrop(metadata, args[2]);
      URI repository = CentralClassificationCommand.repositoryURI(args[3]);
      String base = "org/eclipse/cdo/" + BOM_ARTIFACT + "/4.36.0/" + BOM_ARTIFACT + "-4.36.0";
      List<String> paths = new ArrayList<>();
      for (String suffix : List.of(".pom", ".jar", "-sources.jar", "-javadoc.jar"))
      {
        paths.add(base + suffix);
        paths.add(base + suffix + ".asc");
        for (String checksum : CHECKSUMS)
        {
          paths.add(base + suffix + "." + checksum);
          paths.add(base + suffix + ".asc." + checksum);
        }
      }

      for (String path : paths)
      {
        CentralClassificationCommand.Response response = CentralClassificationCommand.request(repository, path);
        if (response.status == HttpURLConnection.HTTP_OK)
        {
          throw new IllegalStateException("CDO 4.36 BOM coordinate already exists on Maven Central: " + path);
        }

        if (response.status != HttpURLConnection.HTTP_NOT_FOUND)
        {
          throw new IllegalStateException("Cannot establish CDO 4.36 BOM coordinate absence: HTTP " + response.status + " for " + path);
        }
      }

      System.out.println("Maven Central returned authoritative HTTP 404 for every possible CDO 4.36 BOM artifact and sidecar.");
    }

    private static Map<String, String> verifySelectedDrop(Map<String, String> metadata, String selectedDrop) throws Exception
    {
      Map<String, String> inventory = readReleaseInventory(selectedDrop, BUNDLES_436);
      verifyMetadata(metadata, inventory);
      if (!RELEASE.equals(inventory.get("project.release")) || !DROP_ID.equals(inventory.get("drop.id")))
      {
        throw new IllegalArgumentException("Selected drop must be CDO release " + RELEASE + " with qualifier " + DROP_ID);
      }

      return inventory;
    }

    private static void verifyMetadata(Map<String, String> metadata, Map<String, String> inventory)
    {
      if (!inventory.get("project.release").equals(metadata.get("cdo.project.release")) || !inventory.get("drop.id").equals(metadata.get("cdo.drop.id"))
          || releaseInventoryVersions(inventory, BUNDLES_436).size() != BUNDLES_436.size())
      {
        throw new IllegalArgumentException("BOM-only retrofit metadata does not match the selected CDO 4.36 release drop " + DROP_ID);
      }
    }

    private static File bomPom(File repository)
    {
      return new File(repository, bomRelativePom().replace('/', File.separatorChar));
    }

    private static String bomRelativePom()
    {
      return "org/eclipse/cdo/" + BOM_ARTIFACT + "/4.36.0/" + BOM_ARTIFACT + "-4.36.0.pom";
    }

    private static void validatePom(File pom, Map<String, String> metadata, Map<String, String> publishing, Map<String, String> versions, List<String> approved)
        throws Exception
    {
      Element project = parse(pom).getDocumentElement();
      if (!"org.eclipse.cdo".equals(text(project, "groupId")) || !BOM_ARTIFACT.equals(text(project, "artifactId")) || !"4.36.0".equals(text(project, "version"))
          || !"pom".equals(text(project, "packaging")))
      {
        throw new IllegalArgumentException("Generated BOM does not have the required org.eclipse.cdo:org.eclipse.cdo.bom:4.36.0 POM coordinate");
      }

      if (child(project, "dependencies") != null)
      {
        throw new IllegalArgumentException("Generated BOM must not declare ordinary dependencies");
      }

      Element properties = child(project, "properties");
      if (!RELEASE.equals(text(properties, "cdo.project.release")) || !DROP_ID.equals(text(properties, "cdo.drop.id"))
          || !RELEASE.equals(metadata.get("cdo.project.release")) || !DROP_ID.equals(metadata.get("cdo.drop.id"))
          || directChildren(properties, "cdo.project.release").size() != 1 || directChildren(properties, "cdo.drop.id").size() != 1)
      {
        throw new IllegalArgumentException("Generated BOM has incorrect project-release/drop traceability");
      }

      if (!BomCommand.requiredMetadata(publishing, "project.url").equals(text(project, "url"))
          || !BomCommand.requiredMetadata(publishing, "organization.name").equals(text(child(project, "organization"), "name"))
          || !BomCommand.requiredMetadata(publishing, "organization.url").equals(text(child(project, "organization"), "url"))
          || !BomCommand.requiredMetadata(publishing, "license.name").equals(text(child(child(project, "licenses"), "license"), "name"))
          || !BomCommand.requiredMetadata(publishing, "license.url").equals(text(child(child(project, "licenses"), "license"), "url"))
          || !BomCommand.requiredMetadata(publishing, "license.distribution").equals(text(child(child(project, "licenses"), "license"), "distribution"))
          || !BomCommand.requiredMetadata(publishing, "developers.url").equals(text(child(child(project, "developers"), "developer"), "url"))
          || !BomCommand.requiredMetadata(publishing, "issue.system").equals(text(child(project, "issueManagement"), "system"))
          || !BomCommand.requiredMetadata(publishing, "issue.url").equals(text(child(project, "issueManagement"), "url"))
          || !BomCommand.requiredMetadata(publishing, "scm.connection").equals(text(child(project, "scm"), "connection"))
          || !BomCommand.requiredMetadata(publishing, "scm.developerConnection").equals(text(child(project, "scm"), "developerConnection"))
          || !BomCommand.requiredMetadata(publishing, "project.url").equals(text(child(project, "scm"), "url"))
          || !BomCommand.requiredMetadata(metadata, "cdo.git.commit").equals(text(child(project, "scm"), "tag")))
      {
        throw new IllegalArgumentException("Generated BOM is missing required Maven Central project metadata");
      }

      Element dependencies = child(child(project, "dependencyManagement"), "dependencies");
      List<Element> managed = directChildren(dependencies, "dependency");
      NodeList allDependencies = project.getElementsByTagNameNS("*", "dependency");
      NodeList allDependencySections = project.getElementsByTagNameNS("*", "dependencies");
      if (managed.size() != approved.size() || elementChildren(dependencies).size() != approved.size() || allDependencies.getLength() != approved.size()
          || allDependencySections.getLength() != 1)
      {
        throw new IllegalArgumentException("Generated BOM must manage exactly " + approved.size() + " published CDO bundle coordinates");
      }

      for (int i = 0; i < approved.size(); i++)
      {
        Element dependency = managed.get(i);
        String bundle = approved.get(i);
        if (!"org.eclipse.cdo".equals(text(dependency, "groupId")) || !bundle.equals(text(dependency, "artifactId"))
            || !versions.get(bundle).equals(text(dependency, "version")) || directChildren(dependency, "groupId").size() != 1
            || directChildren(dependency, "artifactId").size() != 1 || directChildren(dependency, "version").size() != 1)
        {
          throw new IllegalArgumentException("Generated BOM member does not match published coordinate org.eclipse.cdo:" + bundle + ":" + versions.get(bundle));
        }
      }
    }

    private static List<Element> directChildren(Element parent, String name)
    {
      List<Element> result = new ArrayList<>();
      if (parent != null)
      {
        for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling())
        {
          if (node instanceof Element && name.equals(node.getLocalName()))
          {
            result.add((Element)node);
          }
        }
      }

      return result;
    }

    private static List<Element> elementChildren(Element parent)
    {
      List<Element> result = new ArrayList<>();
      if (parent != null)
      {
        for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling())
        {
          if (node instanceof Element)
          {
            result.add((Element)node);
          }
        }
      }

      return result;
    }

    private static void validateStaging(File staging, Map<String, String> metadata, Map<String, String> publishing, Map<String, String> versions,
        boolean signed, List<String> approved) throws Exception
    {
      rejectSymbolicLinks(staging.toPath(), "CDO 4.36 BOM staging");
      File pom = new File(staging, bomRelativePom().replace('/', File.separatorChar));
      validatePom(pom, metadata, publishing, versions, approved);
      Set<String> expected = new LinkedHashSet<>();
      expected.add(bomRelativePom());
      for (String checksum : CHECKSUMS)
      {
        String path = bomRelativePom() + "." + checksum;
        File file = new File(staging, path.replace('/', File.separatorChar));
        if (!file.isFile() || !ChecksumsCommand.digest(pom, checksumAlgorithm(checksum)).equalsIgnoreCase(Files.readString(file.toPath()).trim()))
        {
          throw new IllegalArgumentException("Missing or invalid BOM checksum: " + path);
        }

        expected.add(path);
      }

      String signaturePath = bomRelativePom() + ".asc";
      File signature = new File(staging, signaturePath.replace('/', File.separatorChar));
      if (signed)
      {
        if (!signature.isFile() || !Files.readString(signature.toPath(), StandardCharsets.US_ASCII).startsWith("-----BEGIN PGP SIGNATURE-----"))
        {
          throw new IllegalArgumentException("Missing, malformed, or non-armored BOM signature: " + signaturePath);
        }

        expected.add(signaturePath);
      }
      else if (signature.exists())
      {
        throw new IllegalArgumentException("Unsigned BOM staging unexpectedly contains a signature: " + signaturePath);
      }

      List<File> files = regularFiles(staging);
      Set<String> actual = new LinkedHashSet<>();
      for (File file : files)
      {
        actual.add(relativePath(staging, file));
      }

      if (!actual.equals(expected))
      {
        Set<String> missing = new LinkedHashSet<>(expected);
        missing.removeAll(actual);
        Set<String> unexpected = new LinkedHashSet<>(actual);
        unexpected.removeAll(expected);
        throw new IllegalArgumentException("BOM-only deployment inventory mismatch; missing=" + missing + " unexpected=" + unexpected);
      }
    }

    private static String checksumAlgorithm(String checksum)
    {
      switch (checksum)
      {
      case "md5":
        return "MD5";
      case "sha1":
        return "SHA-1";
      case "sha256":
        return "SHA-256";
      case "sha512":
        return "SHA-512";
      default:
        throw new IllegalArgumentException("Unknown checksum algorithm: " + checksum);
      }
    }
  }

  /**
   * Classifies a release's immutable Maven coordinates against a read-only Central repository.
   *
   * @author Eike Stepper
   */
  private static final class CentralClassificationCommand
  {
    private static final String DEFAULT_REPOSITORY = "https://repo.maven.apache.org/maven2/";

    private static final String[] CHECKSUMS = { "sha256", "sha1", "md5" };

    private static final Map<String, String> DIGESTS = Map.of("sha256", "SHA-256", "sha1", "SHA-1", "md5", "MD5");

    private static final Set<String> PROVENANCE_PROPERTIES = Set.of("cdo.drop.id", "cdo.project.release", "cdo.project.release.source", "cdo.git.commit",
        "cdo.build.branch", "cdo.build.stream", "cdo.build.timestamp", "cdo.build.trigger", "cdo.build.type", "cdo.examples.source.git.commit");

    private static final int MAX_ARTIFACT_SIZE = 100 * 1024 * 1024;

    public static void execute(String[] args) throws Exception
    {
      if (args.length != 6 && args.length != 7)
      {
        throw new IllegalArgumentException(
            "classify-central requires bundle repository, BOM repository, build metadata, report, Central URL, and optional signed examples staging");
      }

      File bundles = requireDirectory(args[1], "approved bundle repository");
      File bomRepository = requireDirectory(args[2], "generated BOM repository");
      Map<String, String> metadata = readProperties(requireFile(new File(args[3]), "build metadata"));
      File report = new File(args[4]);
      requireWorkspaceOutput(bundles, "approved bundle repository");
      requireWorkspaceOutput(bomRepository, "generated BOM repository");
      requireWorkspaceOutput(report, "Central coordinate classification report");
      rejectSymbolicLinks(bundles.toPath(), "approved bundle repository");
      rejectSymbolicLinks(bomRepository.toPath(), "generated BOM repository");
      String release = metadataValue(metadata, "cdo.project.release");
      String dropID = metadataValue(metadata, "cdo.drop.id");
      String bomVersion = BomCommand.normalizeProjectReleaseVersion(release);
      URI repository = repositoryURI(args[5]);
      File examplesRepository = args.length == 7 ? requireDirectory(args[6], "signed examples distribution repository") : bundles;
      List<Coordinate> inventory = collectInventory(bundles, bomRepository, examplesRepository, release, dropID, bomVersion);
      List<CoordinateResult> results = new ArrayList<>();
      for (Coordinate coordinate : inventory)
      {
        results.add(classify(coordinate, repository, release, dropID));
      }

      writeReport(report, release, dropID, inventory, results);
      long failures = results.stream()
          .filter(result -> result.classification == Classification.CONFLICT || result.classification == Classification.LOOKUP_ERROR).count();
      System.out.println("Central coordinate classification report: " + report + "; total=" + inventory.size() + "; NEW=" + count(results, Classification.NEW)
          + "; REUSABLE=" + count(results, Classification.REUSABLE) + "; CONFLICT=" + count(results, Classification.CONFLICT) + "; LOOKUP_ERROR="
          + count(results, Classification.LOOKUP_ERROR) + ".");
      if (failures != 0)
      {
        throw new IllegalStateException("Central classification found " + failures + " conflict(s) or lookup error(s); see " + report);
      }
    }

    private static long count(List<CoordinateResult> results, Classification classification)
    {
      return results.stream().filter(result -> result.classification == classification).count();
    }

    private static URI repositoryURI(String value)
    {
      if (value == null || value.isBlank())
      {
        value = DEFAULT_REPOSITORY;
      }

      URI uri = URI.create(value.endsWith("/") ? value : value + "/");
      if (uri.getScheme() == null || !("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme())))
      {
        throw new IllegalArgumentException("Central repository URL must use HTTP(S): " + value);
      }

      return uri;
    }

    private static List<Coordinate> collectInventory(File bundles, File bomRepository, File examplesRepository, String release, String dropID,
        String bomVersion) throws Exception
    {
      List<Coordinate> inventory = new ArrayList<>();
      Map<String, String> versions = new LinkedHashMap<>();
      for (String bundle : BUNDLES)
      {
        File directory = new File(bundles, "org/eclipse/cdo/" + bundle);
        File[] versionDirectories = directory.listFiles(File::isDirectory);
        if (versionDirectories == null || versionDirectories.length != 1)
        {
          throw new IllegalArgumentException("Expected one release coordinate directory for " + bundle + " in " + directory);
        }

        File versionDirectory = versionDirectories[0];
        String version = versionDirectory.getName();
        File pom = requireFile(new File(versionDirectory, bundle + "-" + version + ".pom"), "proposed bundle POM");
        validateCoordinatePom(pom, "org.eclipse.cdo", bundle, version, false, release, dropID);
        for (String suffix : new String[] { ".jar", "-sources.jar", "-javadoc.jar" })
        {
          File artifact = new File(versionDirectory, bundle + "-" + version + suffix);
          if (!artifact.isFile() || artifact.length() == 0)
          {
            throw new IllegalArgumentException("Missing or empty proposed bundle artifact: " + artifact);
          }
        }

        versions.put(bundle, version);
        inventory.add(new Coordinate("org.eclipse.cdo", bundle, version, versionDirectory, false));
      }

      File examplesDirectory = new File(examplesRepository, "org/eclipse/cdo/" + EXAMPLES_DISTRIBUTION + "/" + EXAMPLES_DISTRIBUTION_VERSION);
      File examplesPom = requireFile(new File(examplesDirectory, EXAMPLES_DISTRIBUTION + "-" + EXAMPLES_DISTRIBUTION_VERSION + ".pom"),
          "examples distribution POM");
      validateCoordinatePom(examplesPom, "org.eclipse.cdo", EXAMPLES_DISTRIBUTION, EXAMPLES_DISTRIBUTION_VERSION, true, release, dropID);
      requireFile(new File(examplesDirectory, EXAMPLES_DISTRIBUTION + "-" + EXAMPLES_DISTRIBUTION_VERSION + "-examples.zip"), "examples distribution ZIP");
      inventory.add(new Coordinate("org.eclipse.cdo", EXAMPLES_DISTRIBUTION, EXAMPLES_DISTRIBUTION_VERSION, examplesDirectory, false, true));

      File bomVersionDirectory = new File(bomRepository, "org/eclipse/cdo/org.eclipse.cdo.bom/" + bomVersion);
      File bomPom = requireFile(new File(bomVersionDirectory, "org.eclipse.cdo.bom-" + bomVersion + ".pom"), "generated BOM POM");
      validateCoordinatePom(bomPom, "org.eclipse.cdo", "org.eclipse.cdo.bom", bomVersion, true, release, dropID);
      validateBomMembers(bomPom, versions);
      inventory.add(new Coordinate("org.eclipse.cdo", "org.eclipse.cdo.bom", bomVersion, bomVersionDirectory, true));
      return inventory;
    }

    private static void validateBomMembers(File pom, Map<String, String> versions) throws Exception
    {
      Element project = parse(pom).getDocumentElement();
      if (child(project, "dependencies") != null)
      {
        throw new IllegalArgumentException("Generated BOM must not declare ordinary dependencies: " + pom);
      }

      Element management = child(project, "dependencyManagement");
      Element dependencies = child(management, "dependencies");
      NodeList nodes = dependencies == null ? null : dependencies.getElementsByTagNameNS("*", "dependency");
      if (nodes == null || nodes.getLength() != versions.size())
      {
        throw new IllegalArgumentException("Generated BOM must manage exactly " + versions.size() + " approved bundle coordinates: " + pom);
      }

      int index = 0;
      for (Map.Entry<String, String> expected : versions.entrySet())
      {
        Element dependency = (Element)nodes.item(index++);
        String coordinate = text(dependency, "groupId") + ":" + text(dependency, "artifactId") + ":" + text(dependency, "version");
        String expectedCoordinate = "org.eclipse.cdo:" + expected.getKey() + ":" + expected.getValue();
        if (!expectedCoordinate.equals(coordinate))
        {
          throw new IllegalArgumentException("Generated BOM member mismatch: expected " + expectedCoordinate + " but found " + coordinate);
        }
      }
    }

    private static CoordinateResult classify(Coordinate coordinate, URI repository, String release, String dropID)
    {
      try
      {
        return classifyExisting(coordinate, repository, release, dropID);
      }
      catch (LookupFailure e)
      {
        return new CoordinateResult(coordinate, Classification.LOOKUP_ERROR, e.getMessage());
      }
      catch (Exception e)
      {
        return new CoordinateResult(coordinate, Classification.LOOKUP_ERROR, "Unable to verify Central coordinate: " + concise(e));
      }
    }

    private static CoordinateResult classifyExisting(Coordinate coordinate, URI repository, String release, String dropID) throws Exception
    {
      List<String> suffixes = coordinate.bom ? List.of(".pom")
          : coordinate.examples ? List.of(".pom", "-examples.zip") : List.of(".pom", ".jar", "-sources.jar", "-javadoc.jar");
      String base = mavenPath(coordinate);
      Response pomResponse = request(repository, base + ".pom");
      if (pomResponse.status == HttpURLConnection.HTTP_NOT_FOUND)
      {
        for (String suffix : suffixes)
        {
          if (!".pom".equals(suffix))
          {
            Response partial = request(repository, base + suffix);
            if (partial.status == HttpURLConnection.HTTP_OK)
            {
              return new CoordinateResult(coordinate, Classification.LOOKUP_ERROR, "POM returned 404 but artifact exists: " + suffix);
            }

            if (partial.status != HttpURLConnection.HTTP_NOT_FOUND)
            {
              return new CoordinateResult(coordinate, Classification.LOOKUP_ERROR,
                  "POM returned 404 but artifact lookup returned HTTP " + partial.status + " for " + suffix);
            }
          }

          Response signature = request(repository, base + suffix + ".asc");
          if (signature.status == HttpURLConnection.HTTP_OK)
          {
            return new CoordinateResult(coordinate, Classification.LOOKUP_ERROR, "POM/artifact returned 404 but a detached signature exists for " + suffix);
          }

          if (signature.status != HttpURLConnection.HTTP_NOT_FOUND)
          {
            return new CoordinateResult(coordinate, Classification.LOOKUP_ERROR,
                "Ambiguous detached-signature lookup for absent artifact " + suffix + ": HTTP " + signature.status);
          }

          for (String checksum : CHECKSUMS)
          {
            Response sidecar = request(repository, base + suffix + "." + checksum);
            if (sidecar.status == HttpURLConnection.HTTP_OK)
            {
              return new CoordinateResult(coordinate, Classification.LOOKUP_ERROR, "POM and artifact returned 404 but checksum exists for " + suffix);
            }

            if (sidecar.status != HttpURLConnection.HTTP_NOT_FOUND)
            {
              return new CoordinateResult(coordinate, Classification.LOOKUP_ERROR,
                  "Ambiguous checksum lookup for absent artifact " + suffix + ": HTTP " + sidecar.status);
            }
          }
        }

        return new CoordinateResult(coordinate, Classification.NEW, "POM and every required artifact/classifier are absent from Central.");
      }

      if (pomResponse.status != HttpURLConnection.HTTP_OK)
      {
        return new CoordinateResult(coordinate, Classification.LOOKUP_ERROR, "POM lookup returned HTTP " + pomResponse.status);
      }

      Map<String, byte[]> published = new LinkedHashMap<>();
      published.put(".pom", verifyPublishedArtifact(repository, base + ".pom", pomResponse.body));
      for (String suffix : suffixes)
      {
        if (".pom".equals(suffix))
        {
          continue;
        }

        published.put(suffix, verifyPublishedArtifact(repository, base + suffix, null));
      }

      String pomConflict = comparePoms(coordinate, published.get(".pom"), release, dropID);
      if (pomConflict != null)
      {
        return new CoordinateResult(coordinate, Classification.CONFLICT, pomConflict);
      }

      for (String suffix : suffixes)
      {
        File proposedFile = new File(coordinate.versionDirectory, coordinate.artifact + "-" + coordinate.version + suffix);
        if (!proposedFile.isFile() || proposedFile.length() == 0)
        {
          return new CoordinateResult(coordinate, Classification.LOOKUP_ERROR, "Proposed coordinate is incomplete; missing " + proposedFile.getName());
        }

        if (!".pom".equals(suffix) && !Arrays.equals(Files.readAllBytes(proposedFile.toPath()), published.get(suffix)))
        {
          return new CoordinateResult(coordinate, Classification.CONFLICT, "Published artifact bytes differ: " + proposedFile.getName());
        }
      }

      String reason = coordinate.bom ? "Existing BOM coordinate is equivalent and is not a new deployment candidate."
          : "All published artifacts and consumer-relevant POM metadata match.";
      return new CoordinateResult(coordinate, Classification.REUSABLE, reason);
    }

    private static byte[] verifyPublishedArtifact(URI repository, String path, byte[] firstResponse) throws LookupFailure
    {
      byte[] artifact = firstResponse;
      if (artifact == null)
      {
        Response response = request(repository, path);
        if (response.status != HttpURLConnection.HTTP_OK)
        {
          throw new LookupFailure("Published coordinate is incomplete: " + path + " returned HTTP " + response.status);
        }

        artifact = response.body;
      }

      boolean strongChecksumFound = false;
      for (String checksum : CHECKSUMS)
      {
        Response response = request(repository, path + "." + checksum);
        if (response.status == HttpURLConnection.HTTP_NOT_FOUND)
        {
          continue;
        }

        if (response.status != HttpURLConnection.HTTP_OK)
        {
          throw new LookupFailure("Checksum lookup returned HTTP " + response.status + " for " + path + "." + checksum);
        }

        String value = new String(response.body, StandardCharsets.US_ASCII).trim().split("\\s+")[0];
        String algorithm = DIGESTS.get(checksum);
        String expectedLength = "SHA-256".equals(algorithm) ? "64" : "SHA-1".equals(algorithm) ? "40" : "32";
        if (!value.matches("(?i)[0-9a-f]{" + expectedLength + "}"))
        {
          throw new LookupFailure("Malformed published checksum for " + path + "." + checksum);
        }

        String actual = digest(artifact, algorithm);
        if (!actual.equalsIgnoreCase(value))
        {
          throw new LookupFailure("Published checksum mismatch for " + path + "." + checksum);
        }

        if ("sha256".equals(checksum) || "sha1".equals(checksum))
        {
          strongChecksumFound = true;
        }
      }

      if (!strongChecksumFound)
      {
        throw new LookupFailure("No published SHA-256 or SHA-1 checksum is available for " + path);
      }

      return artifact;
    }

    private static Response request(URI repository, String path) throws LookupFailure
    {
      HttpURLConnection connection = null;
      try
      {
        connection = (HttpURLConnection)repository.resolve(path).toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(30000);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("Accept-Encoding", "identity");
        int status = connection.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK)
        {
          InputStream error = connection.getErrorStream();
          if (error != null)
          {
            error.close();
          }

          return new Response(status, new byte[0]);
        }

        long contentLength = connection.getContentLengthLong();
        if (contentLength > MAX_ARTIFACT_SIZE)
        {
          throw new LookupFailure("Central response exceeds maximum artifact size: " + path);
        }

        byte[] body;
        try (InputStream input = connection.getInputStream())
        {
          body = input.readNBytes(MAX_ARTIFACT_SIZE + 1);
        }

        if (body.length > MAX_ARTIFACT_SIZE || contentLength >= 0 && body.length != contentLength)
        {
          throw new LookupFailure("Central response is oversized or truncated: " + path);
        }

        return new Response(status, body);
      }
      catch (IOException e)
      {
        throw new LookupFailure("Central request failed for " + path + ": " + concise(e), e);
      }
      finally
      {
        if (connection != null)
        {
          connection.disconnect();
        }
      }
    }

    private static String comparePoms(Coordinate coordinate, byte[] publishedBytes, String release, String dropID) throws Exception
    {
      Document published = parse(new ByteArrayInputStream(publishedBytes), "published POM " + coordinate.coordinate());
      Document proposed = parse(new File(coordinate.versionDirectory, coordinate.artifact + "-" + coordinate.version + ".pom"));
      Element publishedProject = published.getDocumentElement();
      Element proposedProject = proposed.getDocumentElement();
      if (!coordinate.group.equals(text(publishedProject, "groupId")) || !coordinate.artifact.equals(text(publishedProject, "artifactId"))
          || !coordinate.version.equals(text(publishedProject, "version")))
      {
        throw new LookupFailure("Published POM coordinates do not match repository path for " + coordinate.coordinate());
      }

      String proposedPackaging = packaging(proposedProject);
      String publishedPackaging = packaging(publishedProject);
      if (!proposedPackaging.equals(publishedPackaging))
      {
        return "POM packaging changed from " + publishedPackaging + " to " + proposedPackaging;
      }

      if (!coordinate.bom && !coordinate.examples)
      {
        if (!coordinate.group.equals(text(proposedProject, "groupId")) || !coordinate.artifact.equals(text(proposedProject, "artifactId"))
            || !coordinate.version.equals(text(proposedProject, "version")))
        {
          return "Proposed POM coordinates do not match repository path.";
        }

        Element proposedProperties = child(proposedProject, "properties");
        Element publishedProperties = child(publishedProject, "properties");
        String proposedDrop = text(child(proposedProperties, "cdo.drop.id"));
        String proposedRelease = text(child(proposedProperties, "cdo.project.release"));
        if (!dropID.equals(proposedDrop) || !release.equals(proposedRelease))
        {
          return "Proposed bundle POM traceability does not match selected drop/project release.";
        }

        String publishedDrop = text(child(publishedProperties, "cdo.drop.id"));
        if (!publishedDrop.matches("[A-Za-z][A-Za-z0-9._-]*"))
        {
          throw new LookupFailure("Published bundle POM has missing or invalid cdo.drop.id provenance: " + coordinate.coordinate());
        }

        if ("4.36".equals(release) && !dropID.equals(publishedDrop))
        {
          return "Published cdo.drop.id " + publishedDrop + " does not match the selected 4.36 retrofit drop " + dropID;
        }

        String publishedRelease = text(child(publishedProperties, "cdo.project.release"));
        if (!publishedRelease.isEmpty() && !publishedRelease.matches("[0-9]+\\.[0-9]+(?:\\.[0-9]+)?"))
        {
          throw new LookupFailure("Published bundle POM has malformed cdo.project.release provenance: " + coordinate.coordinate());
        }

        if ("4.36".equals(release) && !publishedRelease.isEmpty() && !release.equals(publishedRelease))
        {
          return "Published cdo.project.release " + publishedRelease + " does not match the selected 4.36 retrofit release.";
        }

        removeProvenanceProperties(publishedProject);
        removeProvenanceProperties(proposedProject);
        removeOriginalSCMTag(publishedProject);
        removeOriginalSCMTag(proposedProject);
      }
      else if (coordinate.examples)
      {
        Element proposedProperties = child(proposedProject, "properties");
        if (!coordinate.group.equals(text(proposedProject, "groupId")) || !coordinate.artifact.equals(text(proposedProject, "artifactId"))
            || !coordinate.version.equals(text(proposedProject, "version")))
        {
          return "Proposed examples distribution POM coordinates do not match repository path.";
        }

        if (!dropID.equals(text(child(proposedProperties, "cdo.drop.id"))) || !release.equals(text(child(proposedProperties, "cdo.project.release"))))
        {
          return "Proposed examples distribution POM traceability does not match selected drop/project release.";
        }

        removeProvenanceProperties(publishedProject);
        removeProvenanceProperties(proposedProject);
        removeOriginalSCMTag(publishedProject);
        removeOriginalSCMTag(proposedProject);
      }

      String difference = semanticDifference(publishedProject, proposedProject, "/project");
      if (difference != null)
      {
        return "Consumer-relevant POM metadata differs at " + difference + " after the explicit provenance-property allowlist.";
      }

      return null;
    }

    private static void validateCoordinatePom(File pom, String group, String artifact, String version, boolean bom, String release, String dropID)
        throws Exception
    {
      Element project = parse(pom).getDocumentElement();
      String actual = text(project, "groupId") + ":" + text(project, "artifactId") + ":" + text(project, "version");
      String expected = group + ":" + artifact + ":" + version;
      if (!expected.equals(actual))
      {
        throw new IllegalArgumentException("Proposed POM coordinate mismatch: expected " + expected + " but found " + actual + " in " + pom);
      }

      String expectedPackaging = bom ? "pom" : "jar";
      if (!expectedPackaging.equals(packaging(project)))
      {
        throw new IllegalArgumentException("Proposed POM has unexpected packaging for " + expected + ": " + packaging(project));
      }

      if (!bom && !EXAMPLES_DISTRIBUTION.equals(artifact))
      {
        Element properties = child(project, "properties");
        if (!dropID.equals(text(child(properties, "cdo.drop.id"))) || !release.equals(text(child(properties, "cdo.project.release")))
            || !"org.eclipse.cdo".equals(group))
        {
          throw new IllegalArgumentException("Proposed bundle POM has incorrect drop/project-release traceability: " + pom);
        }
      }
      else if (EXAMPLES_DISTRIBUTION.equals(artifact))
      {
        Element properties = child(project, "properties");
        if (!dropID.equals(text(child(properties, "cdo.drop.id"))) || !release.equals(text(child(properties, "cdo.project.release"))))
        {
          throw new IllegalArgumentException("Proposed examples distribution POM has incorrect drop/project-release traceability: " + pom);
        }
      }
      else
      {
        Element properties = child(project, "properties");
        if (!dropID.equals(text(child(properties, "cdo.drop.id"))) || !release.equals(text(child(properties, "cdo.project.release"))))
        {
          throw new IllegalArgumentException("Generated BOM has incorrect drop/project-release traceability: " + pom);
        }
      }
    }

    private static void removeProvenanceProperties(Element project)
    {
      Element properties = child(project, "properties");
      if (properties == null)
      {
        return;
      }

      NodeList children = properties.getChildNodes();
      for (int i = children.getLength() - 1; i >= 0; i--)
      {
        Node node = children.item(i);
        if (node instanceof Element && PROVENANCE_PROPERTIES.contains(localName(node)))
        {
          properties.removeChild(node);
        }
      }

      if (!properties.hasChildNodes())
      {
        project.removeChild(properties);
      }
    }

    private static void removeOriginalSCMTag(Element project)
    {
      Element scm = child(project, "scm");
      Element tag = child(scm, "tag");
      if (tag != null)
      {
        scm.removeChild(tag);
      }
    }

    private static String semanticDifference(Node left, Node right, String path)
    {
      if (left instanceof Element != right instanceof Element)
      {
        return path;
      }

      if (left instanceof Element)
      {
        Element leftElement = (Element)left;
        Element rightElement = (Element)right;
        String leftName = localName(leftElement);
        String rightName = localName(rightElement);
        if (!leftName.equals(rightName) || !equal(leftElement.getNamespaceURI(), rightElement.getNamespaceURI()))
        {
          return path;
        }

        if (!attributes(leftElement).equals(attributes(rightElement)))
        {
          return path + "/@attributes";
        }

        List<Node> leftChildren = normalizedChildren(leftElement);
        List<Node> rightChildren = normalizedChildren(rightElement);
        if ("properties".equals(leftName))
        {
          leftChildren.sort(Comparator.comparing(CentralClassificationCommand::localName));
          rightChildren.sort(Comparator.comparing(CentralClassificationCommand::localName));
        }

        if (leftChildren.size() != rightChildren.size())
        {
          return path + "/children";
        }

        for (int i = 0; i < leftChildren.size(); i++)
        {
          Node leftChild = leftChildren.get(i);
          Node rightChild = rightChildren.get(i);
          String childName = leftChild instanceof Element ? localName(leftChild) : "text";
          String difference = semanticDifference(leftChild, rightChild, path + "/" + childName);
          if (difference != null)
          {
            return difference;
          }
        }

        return null;
      }

      return left.getTextContent().trim().equals(right.getTextContent().trim()) ? null : path;
    }

    private static List<Node> normalizedChildren(Element element)
    {
      List<Node> result = new ArrayList<>();
      for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling())
      {
        if (child instanceof Element
            || (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE) && !child.getTextContent().trim().isEmpty())
        {
          result.add(child);
        }
      }

      return result;
    }

    private static Map<String, String> attributes(Element element)
    {
      Map<String, String> result = new TreeMap<>();
      for (int i = 0; i < element.getAttributes().getLength(); i++)
      {
        Node attribute = element.getAttributes().item(i);
        if (!"http://www.w3.org/2000/xmlns/".equals(attribute.getNamespaceURI()))
        {
          String name = attribute.getNamespaceURI() + "|" + localName(attribute);
          result.put(name, attribute.getNodeValue());
        }
      }

      return result;
    }

    private static String localName(Node node)
    {
      String name = node.getLocalName();
      return name == null ? node.getNodeName() : name;
    }

    private static boolean equal(Object left, Object right)
    {
      return left == null ? right == null : left.equals(right);
    }

    private static String packaging(Element project)
    {
      String packaging = text(project, "packaging");
      return packaging.isEmpty() ? "jar" : packaging;
    }

    private static String mavenPath(Coordinate coordinate)
    {
      return coordinate.group.replace('.', '/') + "/" + coordinate.artifact + "/" + coordinate.version + "/" + coordinate.artifact + "-" + coordinate.version;
    }

    private static void writeReport(File report, String release, String dropID, List<Coordinate> inventory, List<CoordinateResult> results) throws IOException
    {
      requireWorkspaceOutput(report, "Central coordinate classification report");
      Files.createDirectories(report.toPath().getParent());
      try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(report.toPath(), StandardCharsets.UTF_8)))
      {
        out.println("cdo.project.release=" + release);
        out.println("cdo.drop.id=" + dropID);
        out.println("examples.distribution.version=" + EXAMPLES_DISTRIBUTION_VERSION);
        out.println("release.component.count=" + inventory.size());
        for (Classification classification : Classification.values())
        {
          long count = results.stream().filter(result -> result.classification == classification).count();
          out.println("classification." + classification.name().toLowerCase(Locale.ROOT) + ".count=" + count);
        }

        List<CoordinateResult> newCoordinates = results.stream().filter(result -> result.classification == Classification.NEW).collect(Collectors.toList());
        out.println("publication.coordinate.count=" + newCoordinates.size());
        for (int i = 0; i < results.size(); i++)
        {
          CoordinateResult result = results.get(i);
          int index = i + 1;
          out.println("release.component." + index + ".coordinate=" + result.coordinate.coordinate());
          out.println("release.component." + index + ".kind=" + (result.coordinate.bom ? "bom" : result.coordinate.examples ? "examples" : "bundle"));
          out.println("release.component." + index + ".classification=" + result.classification);
          out.println("release.component." + index + ".reason=" + result.reason.replace("\r", " ").replace("\n", " "));
        }

        for (int i = 0; i < newCoordinates.size(); i++)
        {
          out.println("publication.coordinate." + (i + 1) + "=" + newCoordinates.get(i).coordinate.coordinate());
        }
      }
    }

    private static String concise(Exception e)
    {
      String message = e.getMessage();
      return message == null || message.isBlank() ? e.getClass().getSimpleName() : message;
    }

    private static String metadataValue(Map<String, String> metadata, String key)
    {
      String value = metadata.get(key);
      if (value == null || value.isBlank())
      {
        throw new IllegalArgumentException("Missing required build metadata: " + key);
      }

      return value;
    }

    private static String digest(byte[] bytes, String algorithm)
    {
      byte[] digest;
      try
      {
        digest = MessageDigest.getInstance(algorithm).digest(bytes);
      }
      catch (NoSuchAlgorithmException e)
      {
        throw new IllegalStateException("Required digest algorithm is unavailable: " + algorithm, e);
      }

      StringBuilder hex = new StringBuilder(digest.length * 2);
      for (byte value : digest)
      {
        hex.append(String.format(Locale.ROOT, "%02x", value & 0xff));
      }

      return hex.toString();
    }

    private enum Classification
    {
      NEW, REUSABLE, CONFLICT, LOOKUP_ERROR
    }

    private static final class Coordinate
    {
      private final String group;

      private final String artifact;

      private final String version;

      private final File versionDirectory;

      private final boolean bom;

      private final boolean examples;

      Coordinate(String group, String artifact, String version, File versionDirectory, boolean bom)
      {
        this(group, artifact, version, versionDirectory, bom, false);
      }

      Coordinate(String group, String artifact, String version, File versionDirectory, boolean bom, boolean examples)
      {
        this.group = group;
        this.artifact = artifact;
        this.version = version;
        this.versionDirectory = versionDirectory;
        this.bom = bom;
        this.examples = examples;
      }

      String coordinate()
      {
        return group + ":" + artifact + ":" + version;
      }
    }

    private static final class CoordinateResult
    {
      private final Coordinate coordinate;

      private final Classification classification;

      private final String reason;

      CoordinateResult(Coordinate coordinate, Classification classification, String reason)
      {
        this.coordinate = coordinate;
        this.classification = classification;
        this.reason = reason;
      }
    }

    private static final class Response
    {
      private final int status;

      private final byte[] body;

      Response(int status, byte[] body)
      {
        this.status = status;
        this.body = body;
      }
    }

    private static final class LookupFailure extends Exception
    {
      private static final long serialVersionUID = 1L;

      LookupFailure(String message)
      {
        super(message);
      }

      LookupFailure(String message, Throwable cause)
      {
        super(message, cause);
      }
    }
  }

  /**
   * Selects the examples distribution patch version by comparing its candidate contents with Maven Central.
   *
   * @author Eike Stepper
   */
  private static final class ExamplesDistributionVersionCommand
  {
    public static void execute(String[] args) throws Exception
    {
      if (args.length != 9)
      {
        throw new IllegalArgumentException(
            "select-examples-version requires candidate ZIP, source POM, build metadata, publishing metadata, source revision, Central URL, work directory, and package properties");
      }

      File zip = requireFile(new File(args[1]), "candidate examples distribution ZIP");
      File sourcePom = requireFile(new File(args[2]), "examples distribution source POM");
      File metadataFile = requireFile(new File(args[3]), "build metadata");
      File publishingFile = requireFile(new File(args[4]), "publishing metadata");
      String sourceRevision = args[5].trim();
      URI repository = CentralClassificationCommand.repositoryURI(args[6]);
      File workDirectory = requireDirectory(args[7], "examples version selection work directory");
      File packagePropertiesFile = requireFile(new File(args[8]), "examples distribution package properties");
      Map<String, String> metadata = readProperties(metadataFile);
      Map<String, String> publishing = readProperties(publishingFile);
      Map<String, String> packageProperties = readProperties(packagePropertiesFile);
      String versionLine = BomCommand.requiredMetadata(packageProperties, "release.version.line");
      if (!versionLine.matches("[0-9]+\\.[0-9]+"))
      {
        throw new IllegalArgumentException("Invalid examples distribution release.version.line: " + versionLine);
      }

      if (!sourceRevision.matches("[0-9a-fA-F]{40}"))
      {
        throw new IllegalArgumentException("Examples source checkout has an invalid Git revision: " + sourceRevision);
      }

      String release = BomCommand.requiredMetadata(metadata, "cdo.project.release");
      String dropID = BomCommand.requiredMetadata(metadata, "cdo.drop.id");
      String examplesMetadataPath = "org/eclipse/cdo/" + EXAMPLES_DISTRIBUTION + "/maven-metadata.xml";
      CentralClassificationCommand.Response response = CentralClassificationCommand.request(repository, examplesMetadataPath);
      List<Integer> patches = new ArrayList<>();
      if (response.status == HttpURLConnection.HTTP_OK)
      {
        Document document = parse(new ByteArrayInputStream(response.body), "Maven Central examples distribution metadata");
        Element metadataElement = document.getDocumentElement();
        if (!EXAMPLES_DISTRIBUTION.equals(text(metadataElement, "artifactId")) || !"org.eclipse.cdo".equals(text(metadataElement, "groupId")))
        {
          throw new IllegalArgumentException("Maven Central metadata identifies a different examples distribution coordinate");
        }

        Element versionsElement = child(child(metadataElement, "versioning"), "versions");
        NodeList versionNodes = versionsElement == null ? null : versionsElement.getElementsByTagNameNS("*", "version");
        if (versionNodes == null || versionNodes.getLength() == 0)
        {
          throw new IllegalArgumentException("Maven Central examples distribution metadata has no version inventory");
        }

        Set<String> seenVersions = new LinkedHashSet<>();
        Pattern linePattern = Pattern.compile(Pattern.quote(versionLine) + "\\.([0-9]+)");
        for (int i = 0; i < versionNodes.getLength(); i++)
        {
          String version = versionNodes.item(i).getTextContent().trim();
          if (version.isEmpty() || !seenVersions.add(version))
          {
            throw new IllegalArgumentException("Maven Central examples distribution metadata contains an empty or duplicate version");
          }

          Matcher matcher = linePattern.matcher(version);
          if (matcher.matches())
          {
            try
            {
              patches.add(Integer.valueOf(matcher.group(1)));
            }
            catch (NumberFormatException ex)
            {
              throw new IllegalArgumentException("Invalid examples distribution patch version in Maven Central metadata: " + version, ex);
            }
          }
          else if (version.startsWith(versionLine + ".") && !version.matches(Pattern.quote(versionLine) + "\\.[0-9]+-[A-Za-z][A-Za-z0-9.-]*"))
          {
            throw new IllegalArgumentException("Malformed examples distribution version in Maven Central metadata: " + version);
          }
        }
      }
      else if (response.status != HttpURLConnection.HTTP_NOT_FOUND)
      {
        throw new IllegalArgumentException("Maven Central examples distribution metadata lookup returned HTTP " + response.status);
      }

      patches.sort(Comparator.naturalOrder());
      int latestPatch = patches.isEmpty() ? -1 : patches.get(patches.size() - 1);
      if (patches.stream().distinct().count() != patches.size())
      {
        throw new IllegalArgumentException("Maven Central examples distribution metadata contains duplicate numeric patch versions");
      }

      File candidateDirectory = Files.createTempDirectory(workDirectory.toPath(), "examples-version-candidate-").toFile();
      try
      {
        if (latestPatch >= 0)
        {
          String latestVersion = versionLine + "." + latestPatch;
          CentralClassificationCommand.Coordinate latest = candidateCoordinate(candidateDirectory, zip, sourcePom, metadata, publishing, sourceRevision,
              latestVersion);
          CentralClassificationCommand.CoordinateResult result = CentralClassificationCommand.classifyExisting(latest, repository, release, dropID);
          if (result.classification == CentralClassificationCommand.Classification.LOOKUP_ERROR)
          {
            throw new IllegalStateException("Unable to validate latest published examples distribution " + latestVersion + ": " + result.reason);
          }

          if (result.classification == CentralClassificationCommand.Classification.NEW)
          {
            throw new IllegalStateException("Maven Central metadata lists examples distribution " + latestVersion + " but its artifacts are absent");
          }

          if (result.classification == CentralClassificationCommand.Classification.REUSABLE)
          {
            select(metadataFile, metadata, workDirectory, versionLine, latestVersion, latestVersion, "REUSED", result.reason, zip);
            return;
          }
        }

        int nextPatch = latestPatch + 1;
        if (nextPatch < 0)
        {
          throw new IllegalArgumentException("Examples distribution patch version is exhausted for line " + versionLine);
        }

        String nextVersion = versionLine + "." + nextPatch;
        CentralClassificationCommand.Coordinate next = candidateCoordinate(candidateDirectory, zip, sourcePom, metadata, publishing, sourceRevision,
            nextVersion);
        CentralClassificationCommand.CoordinateResult collision = CentralClassificationCommand.classifyExisting(next, repository, release, dropID);
        if (collision.classification != CentralClassificationCommand.Classification.NEW)
        {
          throw new IllegalStateException("Selected next examples distribution coordinate is not absent from Central: " + nextVersion + " ("
              + collision.classification + ": " + collision.reason + ")");
        }

        String latestVersion = latestPatch < 0 ? "" : versionLine + "." + latestPatch;
        select(metadataFile, metadata, workDirectory, versionLine, latestVersion, nextVersion, "NEW", "Candidate differs from the latest published patch.",
            zip);
      }
      finally
      {
        delete(candidateDirectory);
      }
    }

    private static CentralClassificationCommand.Coordinate candidateCoordinate(File parent, File zip, File sourcePom, Map<String, String> metadata,
        Map<String, String> publishing, String sourceRevision, String version) throws Exception
    {
      File versionDirectory = new File(parent, version);
      Files.createDirectories(versionDirectory.toPath());
      File pom = new File(versionDirectory, EXAMPLES_DISTRIBUTION + "-" + version + ".pom");
      File proposedZip = new File(versionDirectory, EXAMPLES_DISTRIBUTION + "-" + version + "-examples.zip");
      ExamplesDistributionCommand.writePublishedPom(sourcePom, pom, metadata, publishing, version, sourceRevision);
      Files.copy(zip.toPath(), proposedZip.toPath());
      return new CentralClassificationCommand.Coordinate("org.eclipse.cdo", EXAMPLES_DISTRIBUTION, version, versionDirectory, false, true);
    }

    private static void select(File metadataFile, Map<String, String> metadata, File workDirectory, String versionLine, String latestVersion,
        String selectedVersion, String selection, String reason, File zip) throws Exception
    {
      metadata.put("examples.distribution.version", selectedVersion);
      writeProperties(metadataFile.toPath(), metadata);
      Map<String, String> report = new TreeMap<>();
      report.put("examples.distribution.version.line", versionLine);
      report.put("examples.distribution.latest.central.version", latestVersion);
      report.put("examples.distribution.version", selectedVersion);
      report.put("examples.distribution.selection", selection);
      report.put("examples.distribution.reason", reason);
      report.put("examples.distribution.candidate.zip.sha256", ChecksumsCommand.digest(zip, "SHA-256"));
      writeProperties(new File(workDirectory, "examples-version-selection.properties").toPath(), report);
      EXAMPLES_DISTRIBUTION_VERSION = selectedVersion;
      System.out.println("Selected examples distribution " + EXAMPLES_DISTRIBUTION + ":" + selectedVersion + " (" + selection + ").");
    }
  }

  /**
   * Implements {@code verify}: args are command, aggregated repository, metadata properties.
   *
   * @author Eike Stepper
   */
  private static final class VerifyCommand
  {
    private static final Pattern FORBIDDEN = Pattern.compile(
        "(?:org\\.eclipse\\.net4j\\.db\\.(?:derby|hsqldb|mysql|oracle|db2|postgresql(?=[A-Za-z0-9_.]))|org\\.eclipse\\.emf\\.cdo\\.(?:ui|tests|examples(?!\\.company(?:\\\\|/|-[0-9]|$))))");

    public static void execute(String[] args) throws Exception
    {
      File root = requireDirectory(args[1], "aggregated repository");
      requireWorkspaceOutput(root, "aggregated repository");
      rejectSymbolicLinks(root.toPath(), "aggregated repository");
      Map<String, String> values = readProperties(requireFile(new File(args[2]), "metadata properties"));
      List<File> poms = find(root, ".pom");

      Map<String, String> actualVersions = new LinkedHashMap<>();
      int platformDependencies = 0;

      for (File file : poms)
      {
        Document doc = parse(file);
        Element project = doc.getDocumentElement();

        String group = text(project, "groupId");
        String artifact = text(project, "artifactId");

        if (!isCdoPublicationGroup(group))
        {
          continue;
        }

        String version = text(project, "version");
        if (!"org.eclipse.cdo".equals(group) || !BUNDLES.contains(artifact) || version.isBlank())
        {
          throw new IllegalArgumentException("Unapproved CDO publication coordinate in " + file + ": " + group + ":" + artifact + ":" + version);
        }

        platformDependencies += verifyPlatformDependencyRanges(project, file);

        String expectedPath = "org/eclipse/cdo/" + artifact + "/" + version + "/" + artifact + "-" + version + ".pom";
        if (!expectedPath.equals(relativePath(root, file)))
        {
          throw new IllegalArgumentException("CDO POM does not match its Maven coordinate path: " + file);
        }

        if (actualVersions.putIfAbsent(artifact, version) != null)
        {
          throw new IllegalArgumentException("Duplicate CDO publication coordinate for " + artifact);
        }

        if (!values.get("cdo.git.commit").equals(text(child(child(project, "scm"), "tag"))))
        {
          throw new IllegalArgumentException("SCM tag mismatch in " + file);
        }

        Element props = child(project, "properties");

        rejectInternalCbiProperties(project, file);

        for (Map.Entry<String, String> entry : publicationMetadata(values).entrySet())
        {
          if (props == null || !entry.getValue().equals(text(child(props, entry.getKey()))))
          {
            throw new IllegalArgumentException("Missing traceability property " + entry.getKey() + " in " + file);
          }
        }
      }

      if (!actualVersions.keySet().equals(new LinkedHashSet<>(BUNDLES)))
      {
        throw new IllegalArgumentException("Generated CDO coordinates differ. Expected=" + BUNDLES + " actual=" + actualVersions.keySet());
      }

      if (platformDependencies == 0)
      {
        throw new IllegalArgumentException("No Eclipse Platform, Equinox, or OSGi dependencies were found in the generated CDO POMs");
      }

      for (File file : findAll(root))
      {
        if (FORBIDDEN.matcher(file.getPath()).find())
        {
          throw new IllegalArgumentException("Forbidden artifact in output: " + file);
        }
      }

      System.out.println("Verified " + BUNDLES.size() + " CDO coordinates, " + platformDependencies
          + " Eclipse Platform, Equinox, and OSGi dependency ranges, traceability properties, external aggregation artifacts, and excluded-artifact guard.");
    }

    private static int verifyPlatformDependencyRanges(Element project, File pom)
    {
      Element dependencies = child(project, "dependencies");
      int count = 0;
      for (Node node = dependencies == null ? null : dependencies.getFirstChild(); node != null; node = node.getNextSibling())
      {
        if (!(node instanceof Element) || !"dependency".equals(node.getLocalName()))
        {
          continue;
        }

        Element dependency = (Element)node;
        String group = text(dependency, "groupId");
        if (!group.startsWith("org.eclipse.platform") && !group.startsWith("org.eclipse.equinox") && !group.startsWith("org.eclipse.osgi")
            && !group.equals("org.osgi") && !group.startsWith("org.osgi."))
        {
          continue;
        }

        String version = text(dependency, "version");
        Matcher range = MAJOR_VERSION_RANGE.matcher(version);
        if (!range.matches() || Integer.parseInt(range.group(2)) != Integer.parseInt(range.group(1)) + 1)
        {
          throw new IllegalArgumentException("External Platform/Equinox/OSGi dependency is not mapped to a major-version range in " + pom + ": " + group + ":"
              + text(dependency, "artifactId") + ":" + version);
        }

        count++;
      }

      return count;
    }
  }

  /**
   * Implements {@code consumer-versions}: args are command, repository, output, metadata.
   *
   * @author Eike Stepper
   */
  private static final class ConsumerVersionsCommand
  {
    public static void execute(String[] args) throws Exception
    {
      if (args.length != 4)
      {
        throw new IllegalArgumentException("consumer-versions requires repository, output, metadata");
      }

      File repository = requireDirectory(args[1], "Maven repository");
      File output = new File(args[2]);
      requireWorkspaceOutput(repository, "Maven repository");
      requireWorkspaceOutput(output, "consumer versions output");
      Map<String, String> metadata = readProperties(requireFile(new File(args[3]), "metadata properties"));
      LinkedHashMap<String, String> versions = new LinkedHashMap<>();

      for (String bundle : BUNDLES)
      {
        File artifactDirectory = new File(repository, "org/eclipse/cdo/" + bundle);
        File[] versionsFound = artifactDirectory.listFiles(File::isDirectory);
        if (versionsFound == null || versionsFound.length != 1)
        {
          throw new IllegalArgumentException("Expected exactly one version directory for " + bundle + " in " + artifactDirectory);
        }

        String version = versionsFound[0].getName();
        File pom = new File(versionsFound[0], bundle + "-" + version + ".pom");
        if (!pom.isFile())
        {
          throw new IllegalArgumentException("Missing POM for " + bundle + ": " + pom);
        }

        versions.put("cdo.version." + bundle, version);
      }

      for (String key : Arrays.asList("cdo.drop.id", "cdo.git.commit", "cdo.eclipse.simrel"))
      {
        String value = metadata.get(key);
        if (value == null || value.isBlank())
        {
          throw new IllegalArgumentException("Missing metadata property " + key);
        }
        versions.put(key, value);
      }

      Files.createDirectories(output.toPath().getParent());
      List<String> lines = new ArrayList<>();
      versions.forEach((key, value) -> lines.add(key + "=" + value));
      Files.write(output.toPath(), lines, StandardCharsets.UTF_8);
      System.out.println("Resolved " + BUNDLES.size() + " consumer artifact versions from " + repository);
    }
  }

  /**
   * Implements {@code checksums}: args are command and the final Maven
   * repository. Creates and validates MD5, SHA-1, SHA-256, and SHA-512
   * sidecars for all source and Javadoc classifiers.
   *
   * @author Eike Stepper
   */
  private static final class ChecksumsCommand
  {
    private static final LinkedHashMap<String, String> ALGORITHMS = new LinkedHashMap<>();

    static
    {
      ALGORITHMS.put("md5", "MD5");
      ALGORITHMS.put("sha1", "SHA-1");
      ALGORITHMS.put("sha256", "SHA-256");
      ALGORITHMS.put("sha512", "SHA-512");
    }

    public static void execute(String[] args) throws Exception
    {
      if (args.length != 2)
      {
        throw new IllegalArgumentException("checksums requires finalRepository");
      }

      File repository = requireDirectory(args[1], "Maven repository");
      requireWorkspaceOutput(repository, "Maven repository");
      rejectSymbolicLinks(repository.toPath(), "Maven repository");
      for (String bundle : BUNDLES)
      {
        File versionDirectory = singleVersionDirectory(repository, bundle);
        String version = versionDirectory.getName();

        for (String classifier : CLASSIFIERS)
        {
          File jar = new File(versionDirectory, bundle + "-" + version + "-" + classifier + ".jar");
          if (!jar.isFile() || jar.length() == 0)
          {
            throw new IllegalArgumentException("Missing or empty classifier JAR: " + jar);
          }

          ensureChecksums(jar);
        }
      }

      validate(repository);
      int expectedJars = BUNDLES.size() * CLASSIFIERS.size();
      int expectedSidecars = expectedJars * ALGORITHMS.size();
      System.out.println("Preserved or generated and validated " + expectedJars + " classifier JARs and " + expectedSidecars + " checksum files.");
    }

    private static void validate(File repository) throws Exception
    {
      int jars = 0;
      int sidecars = 0;

      for (String bundle : BUNDLES)
      {
        File versionDirectory = singleVersionDirectory(repository, bundle);
        String version = versionDirectory.getName();

        for (String classifier : CLASSIFIERS)
        {
          File jar = new File(versionDirectory, bundle + "-" + version + "-" + classifier + ".jar");
          if (!jar.isFile() || jar.length() == 0)
          {
            throw new IllegalArgumentException("Missing or empty classifier JAR: " + jar);
          }

          jars++;

          for (Map.Entry<String, String> algorithm : ALGORITHMS.entrySet())
          {
            File checksum = new File(jar.getPath() + "." + algorithm.getKey());
            if (!checksum.isFile() || checksum.length() == 0)
            {
              throw new IllegalArgumentException("Missing or empty checksum file: " + checksum);
            }

            String actual = Files.readString(checksum.toPath(), StandardCharsets.UTF_8).trim();
            if (!digest(jar, algorithm.getValue()).equalsIgnoreCase(actual))
            {
              throw new IllegalArgumentException("Checksum mismatch for " + jar + " (" + algorithm.getKey() + ")");
            }

            sidecars++;
          }
        }
      }

      int expectedJars = BUNDLES.size() * CLASSIFIERS.size();
      int expectedSidecars = expectedJars * ALGORITHMS.size();
      if (jars != expectedJars || sidecars != expectedSidecars)
      {
        throw new IllegalArgumentException(
            "Expected " + expectedJars + " classifier JARs and " + expectedSidecars + " checksum files, got " + jars + " and " + sidecars);
      }
    }

    private static void writeChecksums(File file) throws IOException
    {
      for (Map.Entry<String, String> algorithm : ALGORITHMS.entrySet())
      {
        File checksum = new File(file.getPath() + "." + algorithm.getKey());
        Files.writeString(checksum.toPath(), digest(file, algorithm.getValue()), StandardCharsets.UTF_8);
      }
    }

    private static void ensureChecksums(File file) throws Exception
    {
      for (Map.Entry<String, String> algorithm : ALGORITHMS.entrySet())
      {
        File checksum = new File(file.getPath() + "." + algorithm.getKey());
        String expected = digest(file, algorithm.getValue());
        if (checksum.isFile())
        {
          String actual = Files.readString(checksum.toPath(), StandardCharsets.UTF_8).trim();
          if (!expected.equalsIgnoreCase(actual))
          {
            throw new IllegalArgumentException("Existing CBI checksum is incorrect: " + checksum);
          }
        }
        else
        {
          Files.writeString(checksum.toPath(), expected, StandardCharsets.UTF_8);
        }
      }
    }

    private static String digest(File file, String algorithm) throws IOException
    {
      final MessageDigest digest;
      try
      {
        digest = MessageDigest.getInstance(algorithm);
      }
      catch (NoSuchAlgorithmException e)
      {
        throw new IllegalArgumentException("Unsupported checksum algorithm: " + algorithm, e);
      }

      try (InputStream in = new FileInputStream(file))
      {
        byte[] buffer = new byte[8192];
        int count;
        while ((count = in.read(buffer)) != -1)
        {
          digest.update(buffer, 0, count);
        }
      }

      StringBuilder result = new StringBuilder();
      for (byte value : digest.digest())
      {
        result.append(String.format("%02x", value & 0xff));
      }
      return result.toString();
    }
  }

  /**
   * Adds the release-matched Plain Java ZIP and its standalone publication POM to staging.
   *
   * @author Eike Stepper
   */
  private static final class ExamplesDistributionCommand
  {
    private static final String[] SOURCES = { "embedded-h2/src/main/java/org/eclipse/emf/cdo/examples/plain/embedded/EmbeddedH2Example.java",
        "remote-server/src/main/java/org/eclipse/emf/cdo/examples/plain/server/RemoteServer.java",
        "remote-client/src/main/java/org/eclipse/emf/cdo/examples/plain/client/RemoteClient.java" };

    public static void execute(String[] args) throws Exception
    {
      if (args.length != 9)
      {
        throw new IllegalArgumentException(
            "stage-examples-distribution requires examples checkout, candidate repository, ZIP, source POM, build metadata, publishing metadata, staging, and examples source revision");
      }

      File workspace = requireReadOnlyCheckout(args[1], "examples source checkout");
      File candidateRepository = requireDirectory(args[2], "selected-drop Maven repository");
      File zip = requireFile(new File(args[3]), "examples distribution ZIP");
      File sourcePom = requireFile(new File(args[4]), "distribution POM");
      Map<String, String> metadata = readProperties(requireFile(new File(args[5]), "build metadata"));
      Map<String, String> publishing = readProperties(requireFile(new File(args[6]), "publishing metadata"));
      File staging = requireDirectory(args[7], "Central staging");
      String examplesSourceRevision = args[8].trim();

      requireWorkspaceOutput(candidateRepository, "selected-drop Maven repository");
      requireWorkspaceOutput(zip, "examples distribution ZIP");
      requireWorkspaceOutput(sourcePom, "distribution POM");
      requireWorkspaceOutput(staging, "Central staging");
      rejectSymbolicLinks(zip.toPath(), "examples distribution ZIP");
      rejectSymbolicLinks(staging.toPath(), "Central staging");
      PublishPolicyCommand.enforce(metadata.get("cdo.build.type"), "PACKAGE", metadata.get("cdo.project.release"), metadata.get("cdo.build.stream"));

      String commit = BomCommand.requiredMetadata(metadata, "cdo.git.commit");
      if (!commit.matches("[0-9a-fA-F]{40}"))
      {
        throw new IllegalArgumentException("Selected CDO drop has an invalid cdo.git.commit: " + commit);
      }

      if (!examplesSourceRevision.matches("[0-9a-fA-F]{40}"))
      {
        throw new IllegalArgumentException("Examples source checkout has an invalid Git revision: " + examplesSourceRevision);
      }

      validateDistributionZip(workspace, candidateRepository, zip, metadata);
      Map<String, String> packageProperties = readProperties(
          requireFile(new File(sourcePom.getParentFile(), "package.properties"), "distribution package properties"));
      String releaseVersion = BomCommand.requiredMetadata(metadata, "examples.distribution.version");
      String versionLine = BomCommand.requiredMetadata(packageProperties, "release.version.line");
      if (!releaseVersion.matches(Pattern.quote(versionLine) + "\\.[0-9]+"))
      {
        throw new IllegalArgumentException("Selected examples distribution version is outside configured line " + versionLine + ": " + releaseVersion);
      }

      String directory = "org/eclipse/cdo/" + EXAMPLES_DISTRIBUTION + "/" + releaseVersion;
      File versionDirectory = new File(staging, directory.replace('/', File.separatorChar));
      File publishedPom = new File(versionDirectory, EXAMPLES_DISTRIBUTION + "-" + releaseVersion + ".pom");
      File publishedZip = new File(versionDirectory, EXAMPLES_DISTRIBUTION + "-" + releaseVersion + "-examples.zip");
      if (Files.exists(publishedPom.toPath(), LinkOption.NOFOLLOW_LINKS) || Files.exists(publishedZip.toPath(), LinkOption.NOFOLLOW_LINKS))
      {
        throw new IllegalArgumentException(
            "Examples distribution release coordinate already exists in staging: " + EXAMPLES_DISTRIBUTION + ":" + releaseVersion);
      }

      Files.createDirectories(versionDirectory.toPath());
      Files.copy(zip.toPath(), publishedZip.toPath());
      writePublishedPom(sourcePom, publishedPom, metadata, publishing, releaseVersion, examplesSourceRevision);
      ChecksumsCommand.writeChecksums(publishedPom);
      ChecksumsCommand.writeChecksums(publishedZip);
      System.out.println("Staged selected examples distribution " + EXAMPLES_DISTRIBUTION + ":" + releaseVersion + " with ZIP classifier examples.");
    }

    private static void writePublishedPom(File sourcePom, File publishedPom, Map<String, String> metadata, Map<String, String> publishing,
        String releaseVersion, String examplesSourceRevision) throws Exception
    {
      Document pomDocument = parse(sourcePom);
      Element project = pomDocument.getDocumentElement();
      String sourceVersion = text(project, "version");
      if (!"org.eclipse.cdo".equals(text(project, "groupId")) || !EXAMPLES_DISTRIBUTION.equals(text(project, "artifactId"))
          || !(sourceVersion.matches(Pattern.quote(EXAMPLES_DISTRIBUTION_VERSION_LINE) + "-SNAPSHOT") || "${revision}".equals(sourceVersion))
          || !"pom".equals(text(project, "packaging")) || child(project, "parent") != null)
      {
        throw new IllegalArgumentException("Development distribution POM must be the standalone org.eclipse.cdo:" + EXAMPLES_DISTRIBUTION + " project");
      }

      setChildText(project, "version", releaseVersion);
      Element build = child(project, "build");
      if (build != null)
      {
        project.removeChild(build);
      }

      Element buildProperties = child(project, "properties");
      if (buildProperties != null)
      {
        Element buildOnlyRelease = child(buildProperties, "examples.cdo.project.release");
        if (buildOnlyRelease != null)
        {
          buildProperties.removeChild(buildOnlyRelease);
        }
      }

      Element scm = child(project, "scm");
      setChildText(scm, "tag", examplesSourceRevision);
      setChildText(scm, "url", publishing.get("project.url"));
      addTraceabilityProperties(pomDocument, project, metadata, examplesSourceRevision);
      write(pomDocument, publishedPom);
      validateDistributionPom(publishedPom, metadata, publishing, releaseVersion, examplesSourceRevision);
    }

    private static void validateDistributionZip(File workspace, File candidateRepository, File zip, Map<String, String> metadata) throws Exception
    {
      Set<String> expectedFiles = Set.of("pom.xml", "README.md", "LICENSE", "embedded-h2/pom.xml", "remote-server/pom.xml", "remote-client/pom.xml", SOURCES[0],
          SOURCES[1], SOURCES[2]);
      try (ZipFile archive = new ZipFile(zip))
      {
        List<String> fileEntries = archive.stream().filter(entry -> !entry.isDirectory()).map(ZipEntry::getName).collect(Collectors.toList());
        Set<String> actualFiles = new LinkedHashSet<>(fileEntries);
        if (fileEntries.size() != actualFiles.size() || !actualFiles.equals(expectedFiles))
        {
          throw new IllegalArgumentException("Examples ZIP file inventory differs from the approved three-module layout: " + actualFiles);
        }

        for (String source : SOURCES)
        {
          File canonicalSource = new File(workspace, "plugins/org.eclipse.emf.cdo.examples/src/" + source.substring(source.indexOf("/src/main/java/") + 15));
          if (!canonicalSource.isFile())
          {
            throw new IllegalArgumentException("Missing canonical JDT example source: " + canonicalSource);
          }

          try (InputStream packaged = archive.getInputStream(archive.getEntry(source)))
          {
            if (!Arrays.equals(Files.readAllBytes(canonicalSource.toPath()), packaged.readAllBytes()))
            {
              throw new IllegalArgumentException("Packaged example source differs from its canonical JDT source: " + source);
            }
          }
        }

        Document portablePom = parse(archive.getInputStream(archive.getEntry("pom.xml")), "portable examples POM");
        String projectRelease = BomCommand.normalizeProjectReleaseVersion(BomCommand.requiredMetadata(metadata, "cdo.project.release"));
        if (!projectRelease.equals(text(child(portablePom.getDocumentElement(), "properties"), "cdo.project.release")))
        {
          throw new IllegalArgumentException("Portable POM CDO BOM version does not match the selected release " + projectRelease);
        }

        Element rootProperties = child(portablePom.getDocumentElement(), "properties");
        verifyCandidateVersion(candidateRepository, "org.eclipse.emf.cdo.server.embedded", text(rootProperties, "cdo.embedded.version"));
        verifyCandidateVersion(candidateRepository, "org.eclipse.emf.cdo.examples.company", text(rootProperties, "cdo.company.version"));
        verifyCandidateVersion(candidateRepository, "org.eclipse.net4j.util", text(rootProperties, "cdo.net4j.util.version"));
        verifyCandidateVersion(candidateRepository, "org.eclipse.net4j.db.postgresql", text(rootProperties, "cdo.net4j.postgresql.version"));
      }
    }

    private static void verifyCandidateVersion(File repository, String artifact, String expectedVersion)
    {
      File artifactDirectory = new File(repository, "org/eclipse/cdo/" + artifact);
      File[] versions = artifactDirectory.listFiles(File::isDirectory);
      if (expectedVersion.isBlank() || versions == null || versions.length != 1 || !expectedVersion.equals(versions[0].getName()))
      {
        throw new IllegalArgumentException("Examples dependency version for " + artifact + " does not match the selected CDO drop: expected " + expectedVersion
            + ", candidate=" + (versions == null ? "missing" : Arrays.toString(versions)));
      }

      File versionDirectory = versions[0];
      File candidatePom = new File(versionDirectory, artifact + "-" + expectedVersion + ".pom");
      File candidateJar = new File(versionDirectory, artifact + "-" + expectedVersion + ".jar");
      if (!candidatePom.isFile() || candidatePom.length() == 0 || !candidateJar.isFile() || candidateJar.length() == 0)
      {
        throw new IllegalArgumentException("Selected-drop dependency candidate is incomplete for " + artifact + ": " + versionDirectory);
      }

      try
      {
        Element project = parse(candidatePom).getDocumentElement();
        if (!"org.eclipse.cdo".equals(text(project, "groupId")) || !artifact.equals(text(project, "artifactId"))
            || !expectedVersion.equals(text(project, "version")))
        {
          throw new IllegalArgumentException("Selected-drop dependency candidate POM has conflicting coordinates: " + candidatePom);
        }
      }
      catch (Exception e)
      {
        throw new IllegalArgumentException("Unable to validate selected-drop dependency candidate POM: " + candidatePom, e);
      }
    }

    private static void validateDistributionPom(File pom, Map<String, String> metadata, Map<String, String> publishing, String version,
        String examplesSourceRevision) throws Exception
    {
      Element project = parse(pom).getDocumentElement();
      if (!"org.eclipse.cdo".equals(text(project, "groupId")) || !EXAMPLES_DISTRIBUTION.equals(text(project, "artifactId"))
          || !version.equals(text(project, "version")) || !"pom".equals(text(project, "packaging")) || child(project, "parent") != null)
      {
        throw new IllegalArgumentException("Staged distribution POM has invalid coordinates or a parent POM: " + pom);
      }

      for (String required : new String[] { "name", "description", "url", "organization", "licenses", "developers", "scm", "issueManagement" })
      {
        if (child(project, required) == null || text(child(project, required)).isBlank())
        {
          throw new IllegalArgumentException("Distribution POM is missing Central metadata <" + required + ">");
        }
      }

      if (!publishing.get("project.url").equals(text(project, "url")) || !publishing.get("scm.connection").equals(text(child(project, "scm"), "connection"))
          || !examplesSourceRevision.equals(text(child(project, "scm"), "tag")))
      {
        throw new IllegalArgumentException("Distribution POM SCM or project URL does not match the examples source checkout");
      }

      Element properties = child(project, "properties");
      rejectInternalCbiProperties(project, pom);
      if (child(properties, "examples.cdo.project.release") != null
          || !BomCommand.requiredMetadata(metadata, "cdo.project.release").equals(text(properties, "cdo.project.release")))
      {
        throw new IllegalArgumentException("Distribution POM contains missing or build-only project-release metadata");
      }

      for (Map.Entry<String, String> entry : publicationMetadata(metadata).entrySet())
      {
        if (properties == null || !entry.getValue().equals(text(child(properties, entry.getKey()))))
        {
          throw new IllegalArgumentException("Distribution POM traceability mismatch for " + entry.getKey());
        }
      }

      if (!examplesSourceRevision.equals(text(child(properties, "cdo.examples.source.git.commit"))))
      {
        throw new IllegalArgumentException("Distribution POM examples source revision does not match the examples source checkout");
      }
    }

    private static void addTraceabilityProperties(Document document, Element project, Map<String, String> metadata, String examplesSourceRevision)
    {
      Element properties = child(project, "properties");
      if (properties == null)
      {
        properties = document.createElementNS(project.getNamespaceURI(), "properties");
        project.appendChild(properties);
      }

      for (Map.Entry<String, String> entry : publicationMetadata(metadata).entrySet())
      {
        Element property = child(properties, entry.getKey());
        if (property == null)
        {
          property = document.createElementNS(project.getNamespaceURI(), entry.getKey());
          properties.appendChild(property);
        }

        property.setTextContent(entry.getValue());
      }

      Element examplesSourceCommit = child(properties, "cdo.examples.source.git.commit");
      if (examplesSourceCommit == null)
      {
        examplesSourceCommit = document.createElementNS(project.getNamespaceURI(), "cdo.examples.source.git.commit");
        properties.appendChild(examplesSourceCommit);
      }

      examplesSourceCommit.setTextContent(examplesSourceRevision);
    }

    private static void setChildText(Element parent, String name, String value)
    {
      if (parent == null || value == null || value.isBlank())
      {
        throw new IllegalArgumentException("Missing distribution POM element or metadata value: " + name);
      }

      Element child = child(parent, name);
      if (child == null)
      {
        child = parent.getOwnerDocument().createElementNS(parent.getNamespaceURI(), name);
        parent.appendChild(child);
      }

      child.setTextContent(value);
    }
  }

  /**
   * Audits the generated repository and creates a local staging copy. Args are
   * command, final repository, metadata file, report file, and staging directory.
   *
   * @author Eike Stepper
   */
  private static final class AuditCommand
  {
    private static final LinkedHashMap<String, String> DEPENDENCY_CLASSIFICATIONS = new LinkedHashMap<>();

    static
    {
      DEPENDENCY_CLASSIFICATIONS.put("org.eclipse.cdo", "internal-cdo");
      DEPENDENCY_CLASSIFICATIONS.put("org.eclipse.emf", "emf");
      DEPENDENCY_CLASSIFICATIONS.put("org.eclipse.platform", "eclipse-platform");
      DEPENDENCY_CLASSIFICATIONS.put("com.h2database", "jdbc-external");
      DEPENDENCY_CLASSIFICATIONS.put("org.osgi.", "orbit-external");
      DEPENDENCY_CLASSIFICATIONS.put("org.apache.", "orbit-external");
    }

    public static void execute(String[] args) throws Exception
    {
      if (args.length != 5)
      {
        throw new IllegalArgumentException("audit requires finalRepository, metadata, report, stagingDirectory");
      }

      File repository = requireDirectory(args[1], "Maven repository");
      rejectSymbolicLinks(repository.toPath(), "Maven repository");
      File metadataFile = requireFile(new File(args[2]), "metadata properties");
      File reportFile = new File(args[3]);
      File staging = new File(args[4]);
      requireWorkspaceOutput(reportFile, "audit report");
      requireWorkspaceOutput(staging, "staging directory");
      if (Files.exists(staging.toPath(), LinkOption.NOFOLLOW_LINKS))
      {
        rejectSymbolicLinks(staging.toPath(), "staging directory");
      }

      Map<String, String> metadata = readProperties(metadataFile);
      List<String> findings = new ArrayList<>();
      List<File> poms = find(repository, ".pom");
      Set<String> coordinates = new LinkedHashSet<>();
      Map<String, Integer> coordinateCounts = new LinkedHashMap<>();
      Map<String, Integer> dependencyClasses = new LinkedHashMap<>();

      for (File pom : poms)
      {
        Document document = parse(pom);
        Element project = document.getDocumentElement();

        String group = text(project, "groupId");
        String artifact = text(project, "artifactId");
        String version = text(project, "version");
        if (!isCdoPublicationGroup(group))
        {
          continue;
        }

        if (!"org.eclipse.cdo".equals(group) || !BUNDLES.contains(artifact) || version.isEmpty())
        {
          findings.add("Unapproved CDO publication coordinate in " + pom + ": " + group + ":" + artifact + ":" + version);
          continue;
        }

        coordinates.add(group + ":" + artifact + ":" + version);
        coordinateCounts.merge(artifact, 1, Integer::sum);

        String expectedPomPath = "org/eclipse/cdo/" + artifact + "/" + version + "/" + artifact + "-" + version + ".pom";
        if (!expectedPomPath.equals(relativePath(repository, pom)))
        {
          findings.add("CDO POM does not match its Maven coordinate path: " + pom);
        }

        File versionDirectory = pom.getParentFile();
        File mainJar = new File(versionDirectory, artifact + "-" + version + ".jar");
        if (!mainJar.isFile() || mainJar.length() == 0)
        {
          findings.add("Missing main JAR: " + mainJar);
        }
        else
        {
          validateChecksums(mainJar, findings);
        }

        validateChecksums(pom, findings);

        for (String classifier : CLASSIFIERS)
        {
          File classifierJar = new File(versionDirectory, artifact + "-" + version + "-" + classifier + ".jar");
          if (!classifierJar.isFile() || classifierJar.length() == 0)
          {
            findings.add("Missing classifier JAR: " + classifierJar);
          }
          else
          {
            for (String suffix : ChecksumsCommand.ALGORITHMS.keySet())
            {
              File checksum = new File(classifierJar.getPath() + "." + suffix);
              if (!checksum.isFile() || checksum.length() == 0)
              {
                findings.add("Missing classifier checksum: " + checksum);
              }
              else if (!ChecksumsCommand.digest(classifierJar, ChecksumsCommand.ALGORITHMS.get(suffix)) //
                  .equalsIgnoreCase(Files.readString(checksum.toPath(), StandardCharsets.UTF_8).trim()))
              {
                findings.add("Checksum mismatch: " + checksum);
              }
            }
          }
        }

        for (String required : Arrays.asList("name", "description", "url", "licenses", "developers", "scm"))
        {
          if (child(project, required) == null || text(child(project, required)).isEmpty())
          {
            findings.add("Missing Maven Central metadata <" + required + "> in " + pom);
          }
        }

        Element properties = child(project, "properties");
        rejectInternalCbiProperties(project, pom);

        for (Map.Entry<String, String> entry : publicationMetadata(metadata).entrySet())
        {
          if (properties == null || !entry.getValue().equals(text(child(properties, entry.getKey()))))
          {
            findings.add("Traceability mismatch for " + entry.getKey() + " in " + pom);
          }
        }

        String commit = metadata.get("cdo.git.commit");
        if (commit != null && !commit.equals(text(child(child(project, "scm"), "tag"))))
        {
          findings.add("SCM tag does not match cdo.git.commit in " + pom);
        }

        String dependencies = text(child(project, "dependencies"));
        if (dependencies.matches(".*org\\.eclipse\\.net4j\\.db\\.(derby|mysql|oracle|postgresql|hsqldb|db2).*"))
        {
          findings.add("Unapproved JDBC dependency in " + pom);
        }

        Element dependenciesElement = child(project, "dependencies");
        if (dependenciesElement != null)
        {
          NodeList dependencyNodes = dependenciesElement.getChildNodes();

          for (int i = 0; i < dependencyNodes.getLength(); i++)
          {
            Node dependencyNode = dependencyNodes.item(i);
            if (!(dependencyNode instanceof Element) || !"dependency".equals(dependencyNode.getLocalName()))
            {
              continue;
            }

            Element dependency = (Element)dependencyNode;
            String dependencyGroup = text(dependency, "groupId");
            String dependencyArtifact = text(dependency, "artifactId");
            String classification = classify(dependencyGroup);
            dependencyClasses.merge(classification, 1, Integer::sum);

            if ("p2.osgi.bundle".equals(dependencyGroup) || dependencyArtifact.startsWith("p2.osgi.bundle"))
            {
              findings.add("Synthetic p2 dependency coordinate: " + dependencyGroup + ":" + dependencyArtifact + " in " + pom);
            }

            if ("org.eclipse.cdo".equals(dependencyGroup) && !BUNDLES.contains(dependencyArtifact))
            {
              findings.add("Unpublished internal CDO dependency: " + dependencyArtifact + " in " + pom);
            }
          }
        }
      }

      Set<String> expectedArtifacts = new LinkedHashSet<>();
      for (String bundle : BUNDLES)
      {
        expectedArtifacts.add("org.eclipse.cdo:" + bundle);
        if (coordinateCounts.getOrDefault(bundle, 0) != 1)
        {
          findings.add("Expected exactly one Maven coordinate for " + bundle + ", found " + coordinateCounts.getOrDefault(bundle, 0));
        }
      }

      Set<String> actualArtifacts = new LinkedHashSet<>();
      for (String coordinate : coordinates)
      {
        String[] parts = coordinate.split(":");
        actualArtifacts.add(parts[0] + ":" + parts[1]);
      }

      if (!expectedArtifacts.equals(actualArtifacts))
      {
        findings.add("Unexpected CDO coordinates. Expected=" + expectedArtifacts + " actual=" + actualArtifacts);
      }

      if (coordinates.size() != BUNDLES.size())
      {
        findings.add("Expected exactly " + BUNDLES.size() + " CDO versions, found " + coordinates.size() + ": " + coordinates);
      }

      delete(staging);
      copyMavenStaging(repository, staging);
      Files.copy(metadataFile.toPath(), new File(staging, "cdo-build-metadata.properties").toPath());
      validatePublicationScope(staging, false, false);

      String status = findings.isEmpty() ? "READY_WITHOUT_SIGNATURES" : "NOT_READY";

      List<String> report = new ArrayList<>();
      report.add("status=" + status);
      report.add("repository=" + repository.getAbsolutePath());
      report.add("cdo.drop.id=" + value(metadata, "cdo.drop.id"));
      report.add("cdo.git.commit=" + value(metadata, "cdo.git.commit"));
      report.add("coordinates=" + actualArtifacts.size());
      report.add("classifierJars=" + actualArtifacts.size() * CLASSIFIERS.size());
      report.add("classifierChecksums=" + actualArtifacts.size() * CLASSIFIERS.size() * ChecksumsCommand.ALGORITHMS.size());
      for (Map.Entry<String, Integer> entry : dependencyClasses.entrySet())
      {
        report.add("dependencies." + entry.getKey() + "=" + entry.getValue());
      }
      report.add("pgpSignatures=NOT_GENERATED");
      report.add("findings=" + findings.size());
      report.addAll(findings);

      Files.createDirectories(Path.of(args[3]).toAbsolutePath().getParent());
      Files.write(Path.of(args[3]), report, StandardCharsets.UTF_8);
      Files.copy(Path.of(args[3]), new File(staging, "maven-central-readiness-audit.txt").toPath(), StandardCopyOption.REPLACE_EXISTING);

      System.out.println("Audit report: " + args[3]);
      System.out.println("Local staging: " + staging);

      if (!findings.isEmpty())
      {
        throw new IllegalArgumentException("Maven Central readiness audit found " + findings.size() + " issue(s); see " + args[3]);
      }
    }

    private static String value(Map<String, String> values, String key)
    {
      return values.getOrDefault(key, "");
    }

    private static String classify(String dependencyGroup)
    {
      for (Map.Entry<String, String> entry : DEPENDENCY_CLASSIFICATIONS.entrySet())
      {
        String key = entry.getKey();
        if (key.endsWith(".") ? dependencyGroup.startsWith(key) : dependencyGroup.equals(key))
        {
          return entry.getValue();
        }
      }

      return "unclassified-external";
    }

    private static void validateChecksums(File file, List<String> findings) throws Exception
    {
      for (Map.Entry<String, String> algorithm : ChecksumsCommand.ALGORITHMS.entrySet())
      {
        File checksum = new File(file.getPath() + "." + algorithm.getKey());
        if (!checksum.isFile() || checksum.length() == 0)
        {
          findings.add("Missing checksum: " + checksum);
        }
        else if (!ChecksumsCommand.digest(file, algorithm.getValue()) //
            .equalsIgnoreCase(Files.readString(checksum.toPath(), StandardCharsets.UTF_8).trim()))
        {
          findings.add("Checksum mismatch: " + checksum);
        }
      }
    }

    /** Copies only Maven publication files; p2 content/artifacts indexes are
     * intentionally excluded from the Central upload candidate. */
    private static void copyMavenStaging(File source, File target) throws IOException
    {
      try (Stream<Path> stream = Files.walk(source.toPath()))
      {
        stream.filter(Files::isRegularFile).filter(path -> {
          String relative = source.toPath().relativize(path).toString().replace(File.separatorChar, '/');
          if (!isApprovedPublicationPath(relative))
          {
            return false;
          }

          String name = path.getFileName().toString();
          return isMavenPublicationFile(name);
        }).forEach(path -> {
          try
          {
            Path destination = target.toPath().resolve(source.toPath().relativize(path));
            Files.createDirectories(destination.getParent());
            Files.copy(path, destination);
          }
          catch (IOException e)
          {
            throw new RuntimeException(e);
          }
        });
      }
    }

    private static boolean isApprovedPublicationPath(String path)
    {
      String[] segments = path.split("/");
      if (segments.length == 6 && "org".equals(segments[0]) && "eclipse".equals(segments[1]) && "cdo".equals(segments[2])
          && EXAMPLES_DISTRIBUTION.equals(segments[3]) && EXAMPLES_DISTRIBUTION_VERSION.equals(segments[4]))
      {
        String base = EXAMPLES_DISTRIBUTION + "-" + EXAMPLES_DISTRIBUTION_VERSION;
        return isApprovedArtifactFile(segments[5], base, true);
      }

      if (segments.length == 6 && "org".equals(segments[0]) && "eclipse".equals(segments[1]) && "cdo".equals(segments[2]) && RELEASE_BOM.equals(segments[3])
          && segments[4].matches("[0-9]+\\.[0-9]+\\.[0-9]+"))
      {
        String base = RELEASE_BOM + "-" + segments[4];
        return isApprovedArtifactFile(segments[5], base, true) && !segments[5].equals(base + "-examples.zip");
      }

      if (segments.length == 5 && "org".equals(segments[0]) && "eclipse".equals(segments[1]) && "cdo".equals(segments[2]) && BUNDLES.contains(segments[3]))
      {
        return isMavenMetadataPath(path);
      }

      if (segments.length != 6 || !"org".equals(segments[0]) || !"eclipse".equals(segments[1]) || !"cdo".equals(segments[2]) || !BUNDLES.contains(segments[3])
          || segments[4].isBlank())
      {
        return false;
      }

      String base = segments[3] + "-" + segments[4];
      return isApprovedArtifactFile(segments[5], base, false);
    }

    private static boolean isApprovedArtifactFile(String name, String base, boolean distribution)
    {
      String[] artifactNames = distribution ? new String[] { base + ".pom", base + "-examples.zip" }
          : new String[] { base + ".pom", base + ".jar", base + "-sources.jar", base + "-javadoc.jar" };
      for (String artifactName : artifactNames)
      {
        if (name.equals(artifactName) || name.equals(artifactName + ".asc") || name.matches(Pattern.quote(artifactName) + "\\.(?:md5|sha1|sha256|sha512)"))
        {
          return true;
        }
      }

      return false;
    }

    private static boolean isMavenPublicationFile(String name)
    {
      return name.endsWith(".jar") || name.endsWith(".pom") || name.endsWith(".asc") || name.endsWith(".md5") || name.endsWith(".sha1")
          || name.endsWith(".sha256") || name.endsWith(".sha512") || "maven-metadata.xml".equals(name)
          || name.matches("maven-metadata\\.xml\\.(?:md5|sha1|sha256|sha512)");
    }

    private static int validatePublicationScope(File staging, boolean signaturesRequired) throws Exception
    {
      return validatePublicationScope(staging, signaturesRequired, true);
    }

    private static int validatePublicationScope(File staging, boolean signaturesRequired, boolean distributionRequired) throws Exception
    {
      return validatePublicationScope(staging, signaturesRequired, distributionRequired, false, null);
    }

    private static int validatePublicationScope(File staging, boolean signaturesRequired, boolean distributionRequired, boolean bomRequired,
        Map<String, String> metadata) throws Exception
    {
      Map<String, String> versions = new LinkedHashMap<>();
      boolean distributionPomSeen = false;
      boolean bomPomSeen = false;
      File regularBomPom = null;

      for (File pom : find(staging, ".pom"))
      {
        Element project = parse(pom).getDocumentElement();
        rejectInternalCbiProperties(project, pom);
        String group = text(project, "groupId");
        String artifact = text(project, "artifactId");
        String version = text(project, "version");
        if ("org.eclipse.cdo".equals(group) && EXAMPLES_DISTRIBUTION.equals(artifact) && EXAMPLES_DISTRIBUTION_VERSION.equals(version))
        {
          String expectedPom = "org/eclipse/cdo/" + artifact + "/" + version + "/" + artifact + "-" + version + ".pom";
          if (distributionPomSeen || !expectedPom.equals(relativePath(staging, pom)))
          {
            throw new IllegalArgumentException("Invalid or duplicate staged examples distribution POM: " + pom);
          }

          validateExamplesPublicationPom(project, pom);
          if (metadata != null)
          {
            Element properties = child(project, "properties");
            String expectedRelease = metadata.get("cdo.project.release");
            String expectedDrop = metadata.get("cdo.drop.id");
            if (expectedRelease != null && !expectedRelease.isBlank()
                && !BomCommand.normalizeProjectReleaseVersion(expectedRelease)
                    .equals(BomCommand.normalizeProjectReleaseVersion(text(properties, "cdo.project.release")))
                || expectedDrop != null && !expectedDrop.isBlank() && !expectedDrop.equals(text(properties, "cdo.drop.id")))
            {
              throw new IllegalArgumentException("Examples distribution POM release or drop metadata does not match the selected build: " + pom);
            }

            String expectedCommit = metadata.get("cdo.git.commit");
            if (expectedCommit != null && !expectedCommit.isBlank() && !expectedCommit.equals(text(properties, "cdo.git.commit")))
            {
              throw new IllegalArgumentException("Examples distribution POM CDO binary provenance does not match the selected drop: " + pom);
            }
          }

          distributionPomSeen = true;
          continue;
        }

        if ("org.eclipse.cdo".equals(group) && RELEASE_BOM.equals(artifact))
        {
          String expectedPom = "org/eclipse/cdo/" + RELEASE_BOM + "/" + version + "/" + RELEASE_BOM + "-" + version + ".pom";
          if (bomPomSeen || !expectedPom.equals(relativePath(staging, pom)))
          {
            throw new IllegalArgumentException("Invalid or duplicate staged project-release BOM POM: " + pom);
          }

          bomPomSeen = true;
          regularBomPom = pom;
          continue;
        }

        if (!"org.eclipse.cdo".equals(group) || !BUNDLES.contains(artifact) || version.isBlank())
        {
          throw new IllegalArgumentException("Unapproved Maven publication coordinate in staging: " + group + ":" + artifact + ":" + version);
        }

        String expectedPom = "org/eclipse/cdo/" + artifact + "/" + version + "/" + artifact + "-" + version + ".pom";
        if (!expectedPom.equals(relativePath(staging, pom)))
        {
          throw new IllegalArgumentException("Staged POM path does not match its Maven coordinate: " + pom);
        }

        if (versions.putIfAbsent(artifact, version) != null)
        {
          throw new IllegalArgumentException("Duplicate Maven publication coordinate in staging: " + group + ":" + artifact);
        }
      }

      if (!versions.keySet().equals(new LinkedHashSet<>(BUNDLES)))
      {
        throw new IllegalArgumentException(
            "Staging must contain exactly the " + BUNDLES.size() + " approved CDO/Net4j coordinates; found " + versions.keySet());
      }

      if (distributionRequired && !distributionPomSeen)
      {
        throw new IllegalArgumentException("Staging must contain the controlled examples distribution POM " + EXAMPLES_DISTRIBUTION_VERSION);
      }

      if (bomRequired && !bomPomSeen)
      {
        throw new IllegalArgumentException("PACKAGE staging must contain the project-release BOM " + RELEASE_BOM);
      }

      if (bomPomSeen)
      {
        if (!versions.keySet().equals(new LinkedHashSet<>(BUNDLES)))
        {
          throw new IllegalArgumentException("The project-release BOM requires the complete approved CDO bundle inventory");
        }

        Element project = parse(regularBomPom).getDocumentElement();
        Element properties = child(project, "properties");
        String release = metadata == null ? text(properties, "cdo.project.release") : BomCommand.requiredMetadata(metadata, "cdo.project.release");
        String dropID = metadata == null ? text(properties, "cdo.drop.id") : BomCommand.requiredMetadata(metadata, "cdo.drop.id");
        String releaseVersion = BomCommand.normalizeProjectReleaseVersion(release);
        String actualVersion = text(project, "version");
        if (!"org.eclipse.cdo".equals(text(project, "groupId")) || !RELEASE_BOM.equals(text(project, "artifactId")) || !releaseVersion.equals(actualVersion)
            || !"pom".equals(text(project, "packaging")) || !release.equals(text(properties, "cdo.project.release"))
            || !dropID.equals(text(properties, "cdo.drop.id")))
        {
          throw new IllegalArgumentException("Staged project-release BOM has inconsistent coordinate, release, or drop provenance: " + regularBomPom);
        }

        String releaseSource = text(properties, "cdo.project.release.source");
        String expectedReleaseSource = metadata == null ? null : metadata.get("cdo.project.release.source");
        if (!"DROP".equals(releaseSource) && !"OVERRIDE".equals(releaseSource)
            || expectedReleaseSource != null && !expectedReleaseSource.isBlank() && !expectedReleaseSource.equals(releaseSource))
        {
          throw new IllegalArgumentException("Staged project-release BOM has invalid cdo.project.release.source: " + regularBomPom);
        }

        Map<String, String> approvedVersions = new LinkedHashMap<>();
        for (String bundle : BUNDLES)
        {
          approvedVersions.put(bundle, versions.get(bundle));
        }

        CentralClassificationCommand.validateBomMembers(regularBomPom, approvedVersions);
      }

      boolean distributionIncluded = distributionRequired || distributionPomSeen;

      Set<String> allowedFiles = new LinkedHashSet<>();
      List<File> artifacts = new ArrayList<>();
      for (String bundle : BUNDLES)
      {
        String version = versions.get(bundle);
        String prefix = "org/eclipse/cdo/" + bundle + "/" + version + "/" + bundle + "-" + version;
        for (String suffix : new String[] { ".pom", ".jar", "-sources.jar", "-javadoc.jar" })
        {
          String relative = prefix + suffix;
          File artifact = new File(staging, relative.replace('/', File.separatorChar));
          if (!artifact.isFile() || artifact.length() == 0)
          {
            throw new IllegalArgumentException("Missing or empty staged Maven artifact: " + relative);
          }

          artifacts.add(artifact);
          allowedFiles.add(relative);
          for (Map.Entry<String, String> algorithm : ChecksumsCommand.ALGORITHMS.entrySet())
          {
            String checksumPath = relative + "." + algorithm.getKey();
            File checksum = new File(staging, checksumPath.replace('/', File.separatorChar));
            if (!checksum.isFile() || !ChecksumsCommand.digest(artifact, algorithm.getValue()).equalsIgnoreCase(Files.readString(checksum.toPath()).trim()))
            {
              throw new IllegalArgumentException("Missing or invalid checksum for staged artifact: " + checksumPath);
            }

            allowedFiles.add(checksumPath);
          }

          String signaturePath = relative + ".asc";
          File signature = new File(staging, signaturePath.replace('/', File.separatorChar));
          allowedFiles.add(signaturePath);
          if (signaturesRequired && (!signature.isFile() || signature.length() == 0))
          {
            throw new IllegalArgumentException("PACKAGE requires a non-empty signature: " + signaturePath);
          }
        }

        String metadataPath = "org/eclipse/cdo/" + bundle + "/maven-metadata.xml";
        File mavenMetadata = new File(staging, metadataPath.replace('/', File.separatorChar));
        if (mavenMetadata.isFile())
        {
          allowedFiles.add(metadataPath);
          for (String suffix : new String[] { ".md5", ".sha1", ".sha256", ".sha512" })
          {
            String checksumPath = metadataPath + suffix;
            if (new File(staging, checksumPath.replace('/', File.separatorChar)).isFile())
            {
              allowedFiles.add(checksumPath);
            }
          }
        }
      }

      if (bomPomSeen)
      {
        String version = text(parse(regularBomPom).getDocumentElement(), "version");
        String prefix = "org/eclipse/cdo/" + RELEASE_BOM + "/" + version + "/" + RELEASE_BOM + "-" + version;
        String relative = prefix + ".pom";
        File artifact = new File(staging, relative.replace('/', File.separatorChar));
        if (!artifact.isFile() || artifact.length() == 0)
        {
          throw new IllegalArgumentException("Missing or empty staged project-release BOM POM: " + relative);
        }

        artifacts.add(artifact);
        allowedFiles.add(relative);
        for (Map.Entry<String, String> algorithm : ChecksumsCommand.ALGORITHMS.entrySet())
        {
          String checksumPath = relative + "." + algorithm.getKey();
          File checksum = new File(staging, checksumPath.replace('/', File.separatorChar));
          if (!checksum.isFile() || !ChecksumsCommand.digest(artifact, algorithm.getValue()).equalsIgnoreCase(Files.readString(checksum.toPath()).trim()))
          {
            throw new IllegalArgumentException("Missing or invalid checksum for staged project-release BOM: " + checksumPath);
          }

          allowedFiles.add(checksumPath);
        }

        String signaturePath = relative + ".asc";
        File signature = new File(staging, signaturePath.replace('/', File.separatorChar));
        allowedFiles.add(signaturePath);
        if (signaturesRequired && (!signature.isFile() || signature.length() == 0))
        {
          throw new IllegalArgumentException("PACKAGE requires a non-empty signature: " + signaturePath);
        }
      }

      if (distributionIncluded)
      {
        String examplesPrefix = "org/eclipse/cdo/" + EXAMPLES_DISTRIBUTION + "/" + EXAMPLES_DISTRIBUTION_VERSION + "/" + EXAMPLES_DISTRIBUTION + "-"
            + EXAMPLES_DISTRIBUTION_VERSION;
        for (String suffix : new String[] { ".pom", "-examples.zip" })
        {
          String relative = examplesPrefix + suffix;
          File artifact = new File(staging, relative.replace('/', File.separatorChar));
          if (!artifact.isFile() || artifact.length() == 0)
          {
            throw new IllegalArgumentException("Missing or empty staged examples distribution artifact: " + relative);
          }

          artifacts.add(artifact);
          allowedFiles.add(relative);
          for (Map.Entry<String, String> algorithm : ChecksumsCommand.ALGORITHMS.entrySet())
          {
            String checksumPath = relative + "." + algorithm.getKey();
            File checksum = new File(staging, checksumPath.replace('/', File.separatorChar));
            if (!checksum.isFile() || !ChecksumsCommand.digest(artifact, algorithm.getValue()).equalsIgnoreCase(Files.readString(checksum.toPath()).trim()))
            {
              throw new IllegalArgumentException("Missing or invalid checksum for staged examples distribution artifact: " + checksumPath);
            }

            allowedFiles.add(checksumPath);
          }

          String signaturePath = relative + ".asc";
          File signature = new File(staging, signaturePath.replace('/', File.separatorChar));
          allowedFiles.add(signaturePath);
          if (signaturesRequired && (!signature.isFile() || signature.length() == 0))
          {
            throw new IllegalArgumentException("PACKAGE requires a non-empty signature: " + signaturePath);
          }
        }
      }

      try (Stream<Path> paths = Files.walk(staging.toPath()))
      {
        for (Path path : paths.filter(Files::isRegularFile).collect(Collectors.toList()))
        {
          String relative = relativePath(staging, path.toFile());
          if (relative.startsWith("org/") || relative.split("/").length >= 4 || isMavenPublicationFile(path.getFileName().toString()))
          {
            if (!allowedFiles.contains(relative))
            {
              throw new IllegalArgumentException("Unexpected file in publication staging: " + relative);
            }
          }
        }
      }

      return artifacts.size();
    }

    private static void validateExamplesPublicationPom(Element project, File pom)
    {
      if (!"pom".equals(text(project, "packaging")) || child(project, "parent") != null || child(project, "build") != null)
      {
        throw new IllegalArgumentException("Examples distribution POM must be standalone pom packaging without build configuration: " + pom);
      }

      String[][] requiredPaths = { { "name" }, { "description" }, { "url" }, { "organization", "name" }, { "organization", "url" },
          { "licenses", "license", "name" }, { "licenses", "license", "url" }, { "licenses", "license", "distribution" }, { "developers", "developer", "name" },
          { "developers", "developer", "url" }, { "scm", "connection" }, { "scm", "developerConnection" }, { "scm", "url" }, { "issueManagement", "system" },
          { "issueManagement", "url" } };
      for (String[] path : requiredPaths)
      {
        Element element = project;
        for (String segment : path)
        {
          element = child(element, segment);
        }

        if (element == null || text(element).isBlank())
        {
          throw new IllegalArgumentException("Examples distribution POM is missing Maven Central metadata " + String.join("/", path) + ": " + pom);
        }
      }

      Element properties = child(project, "properties");
      if (child(properties, "examples.cdo.project.release") != null)
      {
        throw new IllegalArgumentException("Examples distribution POM must not contain build-only examples.cdo.project.release metadata: " + pom);
      }

      String dropCommit = text(properties, "cdo.git.commit");
      String examplesSourceCommit = text(properties, "cdo.examples.source.git.commit");
      String dropID = text(properties, "cdo.drop.id");
      String projectRelease = text(properties, "cdo.project.release");
      try
      {
        BomCommand.normalizeProjectReleaseVersion(projectRelease);
      }
      catch (IllegalArgumentException e)
      {
        throw new IllegalArgumentException("Examples distribution POM has invalid cdo.project.release metadata: " + pom, e);
      }

      if (properties == null || dropCommit.isBlank() || dropID.isBlank() || !examplesSourceCommit.matches("[0-9a-fA-F]{40}")
          || !examplesSourceCommit.equals(text(child(project, "scm"), "tag")))
      {
        throw new IllegalArgumentException("Examples distribution POM is missing distinct CDO drop and examples source provenance: " + pom);
      }
    }
  }

  /**
   * Enforces the Maven Central publication policy at the publication boundary.
   * Validation and local staging deliberately do not use this policy.
   *
   * @author Eike Stepper
   */
  private static final class PublishPolicyCommand
  {
    private static final String VALIDATE = "VALIDATE";

    private static final String PACKAGE = "PACKAGE";

    private static final String PACKAGE_BOM_436_FOR_CENTRAL = "PACKAGE_BOM_436_FOR_CENTRAL";

    public static void execute(String[] args) throws Exception
    {
      if (args.length != 3)
      {
        throw new IllegalArgumentException("publish-policy requires build metadata properties and mode (VALIDATE or PACKAGE)");
      }

      Map<String, String> metadata = readProperties(requireFile(new File(args[1]), "metadata properties"));
      String buildType = metadata.get("cdo.build.type");
      enforce(buildType, args[2], metadata.get("cdo.project.release"), metadata.get("cdo.build.stream"));
      System.out.println("Maven publishing policy " + args[2] + " accepted for build type " + buildType + ".");
    }

    public static void test()
    {
      for (String buildType : new String[] { "R", "S", "I" })
      {
        enforce(buildType, VALIDATE, null, "4.x");
        expectFailure(buildType, PACKAGE, null, "4.x");
        enforce(buildType, PACKAGE, "4.37", "4.x");
      }

      for (String buildType : new String[] { "", null })
      {
        expectFailure(buildType, VALIDATE, null, "4.x");
        expectFailure(buildType, PACKAGE, null, "4.x");
      }

      expectFailure("X", VALIDATE, null, "4.x");
      expectFailure("X", PACKAGE, "4.37", "4.x");
      expectFailure("R", "PUBLISH", "4.37", "4.x");
      expectFailure("R", PACKAGE, "4.x", "4.x");
      expectFailure("R", PACKAGE, "5.0", "4.x");
      enforce("R", PACKAGE_BOM_436_FOR_CENTRAL, null, "4.x");
      for (String buildType : new String[] { "S", "I" })
      {
        expectFailure(buildType, PACKAGE_BOM_436_FOR_CENTRAL, null, "4.x");
      }

      System.out.println("Maven publishing policy tests passed for VALIDATE and PACKAGE with R, S, I, unknown, and missing build types.");
    }

    private static void enforce(String buildType, String mode, String projectRelease, String buildStream)
    {
      if (!VALIDATE.equals(mode) && !PACKAGE.equals(mode) && !PACKAGE_BOM_436_FOR_CENTRAL.equals(mode))
      {
        throw new IllegalArgumentException("Unknown Maven publishing mode: " + mode);
      }

      if (buildType == null || buildType.isBlank())
      {
        throw new IllegalArgumentException("build-info.xml does not define a supported build type");
      }

      if (!"R".equals(buildType) && !"S".equals(buildType) && !"I".equals(buildType))
      {
        throw new IllegalArgumentException("Unsupported build-info.xml build type: " + buildType);
      }

      if (PACKAGE_BOM_436_FOR_CENTRAL.equals(mode) && !"R".equals(buildType))
      {
        throw new IllegalArgumentException("Historical BOM packaging is restricted to type=R; build type is " + buildType);
      }

      if (PACKAGE.equals(mode))
      {
        if (projectRelease == null || projectRelease.isBlank())
        {
          throw new IllegalArgumentException(
              "PACKAGE requires a valid project-release version; supply OVERRIDE_VERSION when the selected drop has no web.label");
        }

        BomCommand.normalizeProjectReleaseVersion(projectRelease);
        if (buildStream != null && buildStream.matches("[0-9]+\\.x")
            && !projectRelease.split("\\.")[0].equals(buildStream.substring(0, buildStream.indexOf('.'))))
        {
          throw new IllegalArgumentException("Project release major does not match build-info.xml stream " + buildStream);
        }
      }
    }

    private static void expectFailure(String buildType, String mode, String projectRelease, String buildStream)
    {
      try
      {
        enforce(buildType, mode, projectRelease, buildStream);
        throw new AssertionError("Expected publishing policy failure for build type " + buildType + " and mode " + mode);
      }
      catch (IllegalArgumentException expected)
      {
        // Expected policy rejection.
      }
    }
  }

  /**
   * Validates a local Maven staging directory and applies the publishing
   * boundary checks. Args are command, staging directory, build metadata,
   * mode, and a signature-presence marker. No network operation is performed.
   *
   * @author Eike Stepper
   */
  private static final class PublishCheckCommand
  {
    static void execute(String[] args) throws Exception
    {
      if (args.length != 5)
      {
        throw new IllegalArgumentException("publish-check requires staging, metadata, mode, and signature-presence marker");
      }

      File staging = requireDirectory(args[1], "staging directory");
      requireWorkspaceOutput(staging, "staging directory");
      rejectSymbolicLinks(staging.toPath(), "staging directory");
      Map<String, String> metadata = readProperties(requireFile(new File(args[2]), "metadata properties"));
      String mode = args[3];
      String buildType = metadata.get("cdo.build.type");
      PublishPolicyCommand.enforce(buildType, mode, metadata.get("cdo.project.release"), metadata.get("cdo.build.stream"));

      boolean signaturesRequired = "SIGNED".equals(args[4]);
      boolean packageMode = "PACKAGE".equals(mode);
      int artifacts = validateStaging(staging, signaturesRequired, packageMode, packageMode, metadata);
      File summary = new File(staging, "maven-publishing-summary.properties");

      try (PrintWriter out = new PrintWriter(summary, StandardCharsets.UTF_8.name()))
      {
        out.println("cdo.build.type=" + value(metadata, "cdo.build.type"));
        out.println("cdo.drop.id=" + value(metadata, "cdo.drop.id"));
        out.println("cdo.git.commit=" + value(metadata, "cdo.git.commit"));
        out.println("publishing.mode=" + mode);
        out.println("staging.artifacts=" + artifacts);
        out.println("central.publication.performed=false");
        out.println("signatures.present=" + signaturesRequired);
        out.println("signature.verification=DEFERRED_TO_PACKAGE_VERIFICATION");
        List<File> poms = find(staging, ".pom");
        out.println("coordinate.count=" + poms.size());
        int index = 0;
        for (File pom : poms)
        {
          Document document = parse(pom);
          Element project = document.getDocumentElement();
          out.println("coordinate." + (++index) + "=" + text(project, "groupId") + ":" + text(project, "artifactId") + ":" + text(project, "version"));
        }
      }

      System.out.println("Validated " + artifacts + " staging artifacts for " + mode + ". Central publication performed=false. Summary: " + summary);
    }

    private static int validateStaging(File staging, boolean signaturesRequired, boolean distributionRequired, boolean bomRequired,
        Map<String, String> metadata) throws Exception
    {
      int artifacts = AuditCommand.validatePublicationScope(staging, signaturesRequired, distributionRequired, bomRequired, metadata);
      File distributionPom = new File(staging, ("org/eclipse/cdo/" + EXAMPLES_DISTRIBUTION + "/" + EXAMPLES_DISTRIBUTION_VERSION + "/" + EXAMPLES_DISTRIBUTION
          + "-" + EXAMPLES_DISTRIBUTION_VERSION + ".pom").replace('/', File.separatorChar));
      int expectedArtifacts = BUNDLES.size() * 4 + (distributionRequired || distributionPom.isFile() ? 2 : 0) + (bomRequired ? 1 : 0);
      if (artifacts != expectedArtifacts)
      {
        throw new IllegalArgumentException("Expected exactly " + expectedArtifacts + " approved Maven artifacts in staging; found " + artifacts);
      }

      return artifacts;
    }

    private static String value(Map<String, String> metadata, String key)
    {
      String value = metadata.get(key);
      return value == null ? "" : value;
    }

  }

  private static File singleVersionDirectory(File repository, String bundle)
  {
    File artifact = new File(repository, "org/eclipse/cdo/" + bundle);
    File[] versions = artifact.listFiles(File::isDirectory);
    if (versions == null || versions.length != 1)
    {
      throw new IllegalArgumentException("Expected one Maven version directory for " + bundle + " in " + artifact);
    }

    return versions[0];
  }

  private static void delete(File file) throws IOException
  {
    if (file.exists())
    {
      try (Stream<Path> stream = Files.walk(file.toPath()))
      {
        stream.sorted(Comparator.reverseOrder()).forEach(path -> {
          try
          {
            Files.delete(path);
          }
          catch (IOException e)
          {
            throw new RuntimeException(e);
          }
        });
      }
    }
  }

  private static void publicationScopeCheck(String[] args) throws Exception
  {
    if (args.length != 2 && args.length != 3)
    {
      throw new IllegalArgumentException("publication-scope-check requires a staging directory and optional build metadata");
    }

    File staging = requireDirectory(args[1], "staging directory");
    requireWorkspaceOutput(staging, "staging directory");
    rejectSymbolicLinks(staging.toPath(), "staging directory");
    Map<String, String> metadata = args.length == 3 ? readProperties(requireFile(new File(args[2]), "build metadata")) : null;
    int artifacts = metadata == null ? AuditCommand.validatePublicationScope(staging, false)
        : AuditCommand.validatePublicationScope(staging, false, true, false, metadata);
    System.out.println("Validated publication scope for " + artifacts + " approved CDO artifacts.");
  }

  private static Map<String, String> publicationMetadata(Map<String, String> metadata)
  {
    Map<String, String> result = new LinkedHashMap<>();
    for (Map.Entry<String, String> entry : metadata.entrySet())
    {
      if (!INTERNAL_CBI_PROPERTIES.contains(entry.getKey()) && !"examples.distribution.version".equals(entry.getKey()))
      {
        result.put(entry.getKey(), entry.getValue());
      }
    }

    return result;
  }

  private static void removeInternalCbiProperties(Element project)
  {
    NodeList projectChildren = project.getChildNodes();
    for (int i = 0; i < projectChildren.getLength(); i++)
    {
      Node projectChild = projectChildren.item(i);
      String name = projectChild.getLocalName();
      if (name == null)
      {
        name = projectChild.getNodeName();
      }

      if (projectChild instanceof Element && "properties".equals(name))
      {
        Element properties = (Element)projectChild;
        for (String property : INTERNAL_CBI_PROPERTIES)
        {
          Element child = child(properties, property);
          while (child != null)
          {
            properties.removeChild(child);
            child = child(properties, property);
          }
        }
      }
    }
  }

  private static void rejectInternalCbiProperties(Element project, File pom)
  {
    NodeList projectChildren = project.getChildNodes();
    for (int i = 0; i < projectChildren.getLength(); i++)
    {
      Node projectChild = projectChildren.item(i);
      String name = projectChild.getLocalName();
      if (name == null)
      {
        name = projectChild.getNodeName();
      }

      if (projectChild instanceof Element && "properties".equals(name))
      {
        Element properties = (Element)projectChild;
        for (String property : INTERNAL_CBI_PROPERTIES)
        {
          if (child(properties, property) != null)
          {
            throw new IllegalArgumentException("Internal CBI property must not be published in " + pom + ": " + property);
          }
        }
      }
    }
  }

  private static int countChildren(Element parent, String name)
  {
    int count = 0;
    NodeList children = parent.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node node = children.item(i);
      if (node instanceof Element && name.equals(node.getLocalName() == null ? node.getNodeName() : node.getLocalName()))
      {
        count++;
      }
    }

    return count;
  }

  private static void consumerDependencyTreeCheck(String[] args) throws Exception
  {
    if (args.length != 2)
    {
      throw new IllegalArgumentException("consumer-dependency-tree-check requires a Maven dependency-tree file");
    }

    File dependencyTree = requireFile(new File(args[1]), "Maven consumer dependency tree");
    requireWorkspaceOutput(dependencyTree, "Maven consumer dependency tree");
    String tree = Files.readString(dependencyTree.toPath(), StandardCharsets.UTF_8);
    verifyNoSnapshotDependencies(tree);
    System.out.println("Verified that the Central-only consumer dependency tree contains no SNAPSHOT dependencies.");
  }

  private static void consumerDependencyTreeTest(String[] args)
  {
    if (args.length != 1)
    {
      throw new IllegalArgumentException("consumer-dependency-tree-test takes no arguments");
    }

    verifyNoSnapshotDependencies(
        "org.eclipse.cdo.releng:cdo-maven-consumer-test:jar:1.0.0-SNAPSHOT\n+- org.eclipse.cdo:org.eclipse.net4j:jar:4.23.0:compile\n");

    try
    {
      verifyNoSnapshotDependencies(
          "org.eclipse.cdo.releng:cdo-maven-consumer-test:jar:1.0.0-SNAPSHOT\n+- org.eclipse.platform:org.eclipse.osgi:jar:3.24.300-SNAPSHOT:compile\n");
      throw new AssertionError("Expected a SNAPSHOT dependency to be rejected");
    }
    catch (IllegalArgumentException expected)
    {
      if (!expected.getMessage().contains("SNAPSHOT dependency"))
      {
        throw expected;
      }
    }

    System.out.println("Central-only dependency-tree checks passed: consumer project SNAPSHOT accepted; SNAPSHOT dependency rejected.");
  }

  private static void verifyNoSnapshotDependencies(String tree)
  {
    String[] lines = tree.split("\\R");
    if (lines.length < 2 || lines[0].isBlank())
    {
      throw new IllegalArgumentException("Maven consumer dependency tree is empty or malformed");
    }

    for (int i = 1; i < lines.length; i++)
    {
      if (lines[i].toUpperCase(Locale.ROOT).contains("SNAPSHOT"))
      {
        throw new IllegalArgumentException("Maven consumer dependency tree contains a SNAPSHOT dependency: " + lines[i].trim());
      }
    }
  }

  private static void platformVersionRangeTest(String[] args)
  {
    if (args.length != 1)
    {
      throw new IllegalArgumentException("platform-version-range-test takes no arguments");
    }

    verifyPlatformVersionRange("3.25.0", "[3.0.0,4.0.0)");
    verifyPlatformVersionRange("4.12.300", "[4.0.0,5.0.0)");

    try
    {
      verifyPlatformVersionRange("3.25.0", "3.25.0");
      throw new AssertionError("Expected an exact provider version to be rejected");
    }
    catch (IllegalArgumentException expected)
    {
      if (!expected.getMessage().contains("does not match major-version range"))
      {
        throw expected;
      }
    }

    System.out.println("Platform dependency major-version range checks passed.");
  }

  private static void verifyPlatformVersionRange(String providerVersion, String range)
  {
    Matcher versionMatcher = VERSION_MAJOR.matcher(providerVersion);
    if (!versionMatcher.matches())
    {
      throw new IllegalArgumentException("Invalid provider bundle version: " + providerVersion);
    }

    int major = Integer.parseInt(versionMatcher.group(1));
    String expected = "[" + major + ".0.0," + (major + 1) + ".0.0)";
    Matcher rangeMatcher = MAJOR_VERSION_RANGE.matcher(range);
    if (!expected.equals(range) || !rangeMatcher.matches() || Integer.parseInt(rangeMatcher.group(2)) != major + 1)
    {
      throw new IllegalArgumentException(
          "Dependency version '" + range + "' does not match major-version range " + expected + " for provider " + providerVersion);
    }
  }

  private static void verifyDropInfo(String[] args) throws Exception
  {
    if (args.length != 3)
    {
      throw new IllegalArgumentException("drop-info requires drop location and selected drop ID");
    }

    DropLocation drop = DropLocation.parse(args[1]);
    Document info = parse(drop.open("build-info.xml"), "build-info.xml");
    Element build = info.getDocumentElement();
    if (!"build".equals(build.getNodeName()))
    {
      throw new IllegalArgumentException("build-info.xml root is not <build>");
    }

    String actualDropID = required(build, "qualifier");
    if (!args[2].equals(actualDropID))
    {
      throw new IllegalArgumentException("Selected drop ID " + args[2] + " does not match build-info.xml qualifier " + actualDropID);
    }

    String buildType = required(build, "type");
    if (!"R".equals(buildType) && !"S".equals(buildType) && !"I".equals(buildType))
    {
      throw new IllegalArgumentException("Unsupported build-info.xml build type: " + buildType);
    }

    System.out.println("Verified publicly accessible CDO drop " + actualDropID + " with build type " + buildType + ".");
  }

  private static void stageReleaseBom(String[] args) throws Exception
  {
    if (args.length != 4)
    {
      throw new IllegalArgumentException("stage-release-bom requires generated BOM repository, build metadata, and staging directory");
    }

    File bomRepository = requireDirectory(args[1], "generated release BOM repository");
    File metadataFile = requireFile(new File(args[2]), "build metadata");
    File staging = requireDirectory(args[3], "Maven staging");
    requireWorkspaceOutput(bomRepository, "generated release BOM repository");
    requireWorkspaceOutput(staging, "Maven staging");
    rejectSymbolicLinks(bomRepository.toPath(), "generated release BOM repository");
    rejectSymbolicLinks(staging.toPath(), "Maven staging");

    Map<String, String> metadata = readProperties(metadataFile);
    String release = BomCommand.requiredMetadata(metadata, "cdo.project.release");
    String releaseVersion = BomCommand.normalizeProjectReleaseVersion(release);
    String dropID = BomCommand.requiredMetadata(metadata, "cdo.drop.id");
    String relativePom = "org/eclipse/cdo/" + RELEASE_BOM + "/" + releaseVersion + "/" + RELEASE_BOM + "-" + releaseVersion + ".pom";
    File sourcePom = new File(bomRepository, relativePom.replace('/', File.separatorChar));
    sourcePom = requireFile(sourcePom, "generated release BOM POM");
    Map<String, String> versions = new LinkedHashMap<>();
    for (String bundle : BUNDLES)
    {
      File versionDirectory = singleVersionDirectory(staging, bundle);
      String version = versionDirectory.getName();
      File bundlePom = requireFile(new File(versionDirectory, bundle + "-" + version + ".pom"), "staged CDO bundle POM");
      Element project = parse(bundlePom).getDocumentElement();
      if (!"org.eclipse.cdo".equals(text(project, "groupId")) || !bundle.equals(text(project, "artifactId")) || !version.equals(text(project, "version")))
      {
        throw new IllegalArgumentException("Staged bundle coordinate does not match its Maven path: " + bundlePom);
      }

      versions.put(bundle, version);
    }

    Element project = parse(sourcePom).getDocumentElement();
    String coordinate = text(project, "groupId") + ":" + text(project, "artifactId") + ":" + text(project, "version");
    if (!("org.eclipse.cdo:" + RELEASE_BOM + ":" + releaseVersion).equals(coordinate) || !"pom".equals(text(project, "packaging")))
    {
      throw new IllegalArgumentException("Generated release BOM has invalid coordinates or packaging: " + sourcePom);
    }

    Element properties = child(project, "properties");
    if (!release.equals(text(properties, "cdo.project.release")) || !dropID.equals(text(properties, "cdo.drop.id")))
    {
      throw new IllegalArgumentException("Generated release BOM has inconsistent release or selected-drop provenance: " + sourcePom);
    }

    String releaseSource = text(properties, "cdo.project.release.source");
    String expectedReleaseSource = metadata.get("cdo.project.release.source");
    if (!"DROP".equals(releaseSource) && !"OVERRIDE".equals(releaseSource)
        || expectedReleaseSource != null && !expectedReleaseSource.isBlank() && !expectedReleaseSource.equals(releaseSource))
    {
      throw new IllegalArgumentException("Generated release BOM has invalid project-release source metadata: " + sourcePom);
    }

    CentralClassificationCommand.validateBomMembers(sourcePom, versions);

    File destination = new File(staging, relativePom.replace('/', File.separatorChar));
    if (Files.exists(destination.toPath(), LinkOption.NOFOLLOW_LINKS))
    {
      throw new IllegalArgumentException("Release BOM coordinate already exists in staging: " + coordinate);
    }

    Files.createDirectories(destination.toPath().getParent());
    Files.copy(sourcePom.toPath(), destination.toPath());
    for (Map.Entry<String, String> algorithm : ChecksumsCommand.ALGORITHMS.entrySet())
    {
      File sourceChecksum = requireFile(new File(sourcePom.getPath() + "." + algorithm.getKey()), "generated release BOM checksum");
      if (!ChecksumsCommand.digest(sourcePom, algorithm.getValue()).equalsIgnoreCase(Files.readString(sourceChecksum.toPath()).trim()))
      {
        throw new IllegalArgumentException("Generated release BOM checksum is invalid: " + sourceChecksum);
      }

      Files.copy(sourceChecksum.toPath(), new File(destination.getPath() + "." + algorithm.getKey()).toPath());
    }

    System.out.println("Staged POM-only release BOM " + coordinate + " for selected drop " + dropID + ".");
  }

  private static void prepareStandaloneExamples(String[] args) throws Exception
  {
    if (args.length != 4)
    {
      throw new IllegalArgumentException("prepare-standalone-examples requires final deployment ZIP, isolated test directory, and build metadata");
    }

    File deploymentZip = requireFile(new File(args[1]), "final signed deployment ZIP");
    File testDirectory = new File(args[2]);
    File metadataFile = requireFile(new File(args[3]), "build metadata");
    requireWorkspaceOutput(deploymentZip, "final signed deployment ZIP");
    requireWorkspaceOutput(testDirectory, "standalone examples test directory");
    rejectSymbolicLinks(deploymentZip.toPath(), "final signed deployment ZIP");
    if (Files.exists(testDirectory.toPath(), LinkOption.NOFOLLOW_LINKS))
    {
      rejectSymbolicLinks(testDirectory.toPath(), "standalone examples test directory");
      if (!testDirectory.isDirectory() || !isDirectoryEmpty(testDirectory.toPath()))
      {
        throw new IllegalArgumentException("Standalone examples test directory must be a new or empty directory: " + testDirectory);
      }
    }

    Map<String, String> metadata = readProperties(metadataFile);
    String release = BomCommand.requiredMetadata(metadata, "cdo.project.release");
    String releaseVersion = BomCommand.normalizeProjectReleaseVersion(release);
    String dropID = BomCommand.requiredMetadata(metadata, "cdo.drop.id");
    Path repository = testDirectory.toPath().resolve("deployment-repository").toAbsolutePath().normalize();
    Path examples = testDirectory.toPath().resolve("examples").toAbsolutePath().normalize();
    Files.createDirectories(repository);
    Files.writeString(testDirectory.toPath().resolve("deployment-repository-url.txt"), repository.toUri().toASCIIString(), StandardCharsets.UTF_8);
    extractZip(deploymentZip, repository, null, true);

    File bomPom = new File(repository.toFile(),
        ("org/eclipse/cdo/" + RELEASE_BOM + "/" + releaseVersion + "/" + RELEASE_BOM + "-" + releaseVersion + ".pom").replace('/', File.separatorChar));
    bomPom = requireFile(bomPom, "project-release BOM in final deployment ZIP");
    Map<String, String> versions = new LinkedHashMap<>();
    for (String bundle : BUNDLES)
    {
      File versionDirectory = singleVersionDirectory(repository.toFile(), bundle);
      String version = versionDirectory.getName();
      requireFile(new File(versionDirectory, bundle + "-" + version + ".pom"), "deployed CDO bundle POM");
      versions.put(bundle, version);
    }

    Element bom = parse(bomPom).getDocumentElement();
    Element bomProperties = child(bom, "properties");
    if (!release.equals(text(bomProperties, "cdo.project.release")) || !dropID.equals(text(bomProperties, "cdo.drop.id")))
    {
      throw new IllegalArgumentException("The release BOM in the final deployment ZIP has inconsistent project-release or drop metadata");
    }

    CentralClassificationCommand.validateBomMembers(bomPom, versions);
    File examplesZip = new File(repository.toFile(), "org/eclipse/cdo/" + EXAMPLES_DISTRIBUTION + "/" + EXAMPLES_DISTRIBUTION_VERSION + "/"
        + EXAMPLES_DISTRIBUTION + "-" + EXAMPLES_DISTRIBUTION_VERSION + "-examples.zip");
    examplesZip = requireFile(examplesZip, "standalone examples ZIP in final deployment ZIP");
    Set<String> expectedExamples = new LinkedHashSet<>(List.of("pom.xml", "README.md", "LICENSE", "embedded-h2/pom.xml", "remote-server/pom.xml",
        "remote-client/pom.xml", "embedded-h2/src/main/java/org/eclipse/emf/cdo/examples/plain/embedded/EmbeddedH2Example.java",
        "remote-server/src/main/java/org/eclipse/emf/cdo/examples/plain/server/RemoteServer.java",
        "remote-client/src/main/java/org/eclipse/emf/cdo/examples/plain/client/RemoteClient.java"));
    extractZip(examplesZip, examples, expectedExamples, false);
    if (!expectedExamples.isEmpty())
    {
      throw new IllegalArgumentException("Standalone examples ZIP is missing approved files: " + expectedExamples);
    }

    validateStandaloneExamples(examples.toFile(), releaseVersion);
    System.out.println("Prepared standalone examples from the verified deployment ZIP using its release BOM " + releaseVersion + ".");
  }

  private static void verifyStandaloneArtifactProvenance(String[] args) throws Exception
  {
    if (args.length != 4)
    {
      throw new IllegalArgumentException("verify-standalone-provenance requires isolated Maven repository, examples root, and build metadata");
    }

    Path repository = Paths.get(args[1]).toAbsolutePath().normalize();
    Path examples = Paths.get(args[2]).toAbsolutePath().normalize();
    Path metadataFile = Paths.get(args[3]).toAbsolutePath().normalize();
    if (!Files.isDirectory(repository, LinkOption.NOFOLLOW_LINKS) || !repository.equals(repository.toRealPath())
        || !Files.isDirectory(examples, LinkOption.NOFOLLOW_LINKS) || !examples.equals(examples.toRealPath()))
    {
      throw new IllegalArgumentException("Standalone provenance paths must be existing non-symlink directories");
    }

    Map<String, String> metadata = readProperties(requireFile(metadataFile.toFile(), "build metadata"));
    String projectRelease = BomCommand.requiredMetadata(metadata, "cdo.project.release");
    String releaseVersion = BomCommand.normalizeProjectReleaseVersion(projectRelease);
    verifyStandaloneArtifactProvenance(repository, examples, releaseVersion);
  }

  private static void verifyStandaloneArtifactProvenance(Path repository, Path examples, String releaseVersion) throws Exception
  {
    Path cdoGroup = repository.resolve("org/eclipse/cdo");
    if (!Files.isDirectory(cdoGroup, LinkOption.NOFOLLOW_LINKS) || Files.isSymbolicLink(cdoGroup))
    {
      throw new IllegalArgumentException("No CDO artifacts were resolved in the isolated Maven repository; expected repository ID " + "cdo-deployment-bundle");
    }

    Map<String, List<Path>> filesByCoordinate = new TreeMap<>();
    try (Stream<Path> files = Files.walk(cdoGroup))
    {
      for (Path file : files.filter(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)).collect(Collectors.toList()))
      {
        Path relative = cdoGroup.relativize(file);
        if (relative.getNameCount() != 3)
        {
          continue;
        }

        String artifact = relative.getName(0).toString();
        String version = relative.getName(1).toString();
        String name = file.getFileName().toString();
        String baseName = artifact + "-" + version;
        boolean pom = name.equals(baseName + ".pom");
        boolean jar = name.startsWith(baseName + "-") && name.endsWith(".jar") || name.equals(baseName + ".jar");
        if (pom || jar)
        {
          filesByCoordinate.computeIfAbsent(artifact + ":" + version, key -> new ArrayList<>()).add(file);
        }
      }
    }

    if (filesByCoordinate.isEmpty())
    {
      throw new IllegalArgumentException("No CDO artifacts were resolved in the isolated Maven repository; expected repository ID " + "cdo-deployment-bundle");
    }

    Set<String> resolvedArtifacts = new LinkedHashSet<>();
    for (Map.Entry<String, List<Path>> artifactEntry : filesByCoordinate.entrySet())
    {
      String[] coordinate = artifactEntry.getKey().split(":", 2);
      String artifactId = coordinate[0];
      String version = coordinate[1];
      Path artifactPom = cdoGroup.resolve(artifactId).resolve(version).resolve(artifactId + "-" + version + ".pom");
      if (!Files.isRegularFile(artifactPom, LinkOption.NOFOLLOW_LINKS))
      {
        throw new IllegalArgumentException(
            "Resolved CDO artifact " + artifactEntry.getKey() + " is missing its POM; expected repository ID cdo-deployment-bundle");
      }

      Element pom = parse(artifactPom.toFile()).getDocumentElement();
      if (!"org.eclipse.cdo".equals(text(pom, "groupId")) || !artifactId.equals(text(pom, "artifactId")) || !version.equals(text(pom, "version")))
      {
        throw new IllegalArgumentException("Resolved CDO artifact POM coordinate does not match its repository path: " + artifactEntry.getKey());
      }

      String packaging = text(pom, "packaging");
      boolean pomOnly = "pom".equals(packaging);
      if (!pomOnly && artifactEntry.getValue().stream().noneMatch(path -> path.getFileName().toString().equals(artifactId + "-" + version + ".jar")))
      {
        throw new IllegalArgumentException(
            "Resolved CDO artifact " + artifactEntry.getKey() + " is missing its JAR; expected repository ID " + "cdo-deployment-bundle");
      }

      for (Path artifactFile : artifactEntry.getValue())
      {
        verifyResolverOrigin(artifactFile, artifactEntry.getKey(), "cdo-deployment-bundle");
      }

      resolvedArtifacts.add(artifactId + ":" + version);
    }

    String bomCoordinate = RELEASE_BOM + ":" + releaseVersion;
    if (!resolvedArtifacts.contains(bomCoordinate))
    {
      throw new IllegalArgumentException(
          "Release BOM " + bomCoordinate + " was not resolved from the isolated Maven repository; expected repository ID " + "cdo-deployment-bundle");
    }

    Set<String> requiredArtifacts = new LinkedHashSet<>();
    for (String module : List.of("embedded-h2", "remote-server", "remote-client"))
    {
      Element modulePom = parse(requireFile(examples.resolve(module).resolve("pom.xml").toFile(), "standalone examples module POM")).getDocumentElement();
      Element dependencies = child(modulePom, "dependencies");
      NodeList dependencyNodes = dependencies == null ? null : dependencies.getElementsByTagNameNS("*", "dependency");
      for (int i = 0; dependencyNodes != null && i < dependencyNodes.getLength(); i++)
      {
        Element dependency = (Element)dependencyNodes.item(i);
        if ("org.eclipse.cdo".equals(text(dependency, "groupId")))
        {
          requiredArtifacts.add(text(dependency, "artifactId"));
        }
      }
    }

    if (requiredArtifacts.isEmpty())
    {
      throw new IllegalArgumentException("Standalone examples modules declare no CDO dependencies to verify");
    }

    for (String artifact : requiredArtifacts)
    {
      boolean resolved = resolvedArtifacts.stream().anyMatch(coordinate -> coordinate.startsWith(artifact + ":"));
      if (!resolved)
      {
        throw new IllegalArgumentException("Standalone examples CDO dependency " + artifact
            + " was not resolved in the isolated Maven repository; expected repository ID cdo-deployment-bundle");
      }
    }

    System.out.println("Verified Maven Resolver provenance for " + resolvedArtifacts.size() + " CDO artifacts, including " + bomCoordinate
        + ", from repository ID cdo-deployment-bundle.");
  }

  private static void verifyResolverOrigin(Path artifact, String coordinate, String expectedRepository) throws IOException
  {
    Path trackingFile = artifact.getParent().resolve("_remote.repositories");
    Properties tracking = new Properties();
    if (!Files.isRegularFile(trackingFile, LinkOption.NOFOLLOW_LINKS))
    {
      throw new IllegalArgumentException(
          "Maven Resolver origin for " + coordinate + " file " + artifact.getFileName() + " is missing; expected repository ID " + expectedRepository);
    }

    try (InputStream input = Files.newInputStream(trackingFile))
    {
      tracking.load(input);
    }

    String prefix = artifact.getFileName() + ">";
    Set<String> repositories = tracking.stringPropertyNames().stream().filter(key -> key.startsWith(prefix)).map(key -> key.substring(prefix.length()))
        .collect(Collectors.toCollection(LinkedHashSet::new));
    if (repositories.size() != 1 || !repositories.contains(expectedRepository))
    {
      String recorded = repositories.isEmpty() ? "unknown or locally installed"
          : repositories.stream().map(repository -> repository.isEmpty() ? "locally installed (no remote repository)" : repository).collect(Collectors.toSet())
              .toString();
      throw new IllegalArgumentException(
          "Maven Resolver origin for " + coordinate + " file " + artifact.getFileName() + " is " + recorded + "; expected repository ID " + expectedRepository);
    }
  }

  private static void validateStandaloneExamples(File examples, String releaseVersion) throws Exception
  {
    Path rootPom = examples.toPath().resolve("pom.xml");
    Element project = parse(requireFile(rootPom.toFile(), "packaged standalone examples root POM")).getDocumentElement();
    Element properties = child(project, "properties");
    if (!releaseVersion.equals(text(properties, "cdo.project.release")) || child(properties, "examples.cdo.project.release") != null)
    {
      throw new IllegalArgumentException("Packaged standalone examples root POM has inconsistent project-release metadata");
    }

    List<String> modules = new ArrayList<>();
    Element modulesElement = child(project, "modules");
    NodeList moduleNodes = modulesElement == null ? null : modulesElement.getElementsByTagNameNS("*", "module");
    if (moduleNodes != null)
    {
      for (int i = 0; i < moduleNodes.getLength(); i++)
      {
        modules.add(moduleNodes.item(i).getTextContent().trim());
      }
    }

    if (!modules.equals(List.of("embedded-h2", "remote-server", "remote-client")))
    {
      throw new IllegalArgumentException("Packaged standalone examples root POM must build the three approved modules: " + modules);
    }

    Element dependencyManagement = child(project, "dependencyManagement");
    Element dependencies = child(dependencyManagement, "dependencies");
    NodeList dependencyNodes = dependencies == null ? null : dependencies.getElementsByTagNameNS("*", "dependency");
    boolean importsReleaseBom = false;
    if (dependencyNodes != null)
    {
      for (int i = 0; i < dependencyNodes.getLength(); i++)
      {
        Element dependency = (Element)dependencyNodes.item(i);
        if (RELEASE_BOM.equals(text(dependency, "artifactId")) && "org.eclipse.cdo".equals(text(dependency, "groupId"))
            && "${cdo.project.release}".equals(text(dependency, "version")) && "pom".equals(text(dependency, "type"))
            && "import".equals(text(dependency, "scope")) && releaseVersion.equals(text(properties, "cdo.project.release")))
        {
          importsReleaseBom = true;
        }
      }
    }

    if (!importsReleaseBom)
    {
      throw new IllegalArgumentException("Packaged standalone examples do not import the selected release BOM " + releaseVersion);
    }

    boolean hasDeploymentRepository = false;
    Element profiles = child(project, "profiles");
    NodeList profileNodes = profiles == null ? null : profiles.getElementsByTagNameNS("*", "profile");
    if (profileNodes != null)
    {
      for (int i = 0; i < profileNodes.getLength(); i++)
      {
        Element profile = (Element)profileNodes.item(i);
        Element repositories = child(profile, "repositories");
        NodeList repositoryNodes = repositories == null ? null : repositories.getElementsByTagNameNS("*", "repository");
        if (!"cdo-deployment-repository".equals(text(profile, "id")) || repositoryNodes == null)
        {
          continue;
        }

        for (int j = 0; j < repositoryNodes.getLength(); j++)
        {
          Element repository = (Element)repositoryNodes.item(j);
          if ("cdo-deployment-bundle".equals(text(repository, "id")) && "${cdo.deployment.repository}".equals(text(repository, "url")))
          {
            hasDeploymentRepository = true;
          }
        }
      }
    }

    if (!hasDeploymentRepository)
    {
      throw new IllegalArgumentException("Packaged standalone examples must declare the cdo-deployment-bundle file repository profile");
    }

    for (String module : List.of("embedded-h2", "remote-server", "remote-client"))
    {
      requireFile(examples.toPath().resolve(module).resolve("pom.xml").toFile(), "packaged standalone examples module POM");
    }
  }

  private static boolean isDirectoryEmpty(Path directory) throws IOException
  {
    try (Stream<Path> children = Files.list(directory))
    {
      return !children.findAny().isPresent();
    }
  }

  private static void extractZip(File archiveFile, Path destination, Set<String> expectedFiles, boolean deploymentRepository) throws Exception
  {
    Path root = destination.toAbsolutePath().normalize();
    Files.createDirectories(root);
    Set<String> names = new LinkedHashSet<>();
    try (ZipFile archive = new ZipFile(archiveFile))
    {
      Enumeration<? extends ZipEntry> entries = archive.entries();
      while (entries.hasMoreElements())
      {
        ZipEntry entry = entries.nextElement();
        String name = entry.getName();
        validateZipEntryName(name);
        if (!names.add(name))
        {
          throw new IllegalArgumentException("Duplicate ZIP entry: " + name);
        }

        if (entry.isDirectory())
        {
          continue;
        }

        if (deploymentRepository)
        {
          if (!name.startsWith("org/") || !AuditCommand.isApprovedPublicationPath(name))
          {
            throw new IllegalArgumentException("Unexpected file in signed deployment ZIP: " + name);
          }
        }
        else if (expectedFiles == null || !expectedFiles.remove(name))
        {
          throw new IllegalArgumentException("Unexpected file in standalone examples ZIP: " + name);
        }

        Path file = root.resolve(name).normalize();
        if (!file.startsWith(root))
        {
          throw new IllegalArgumentException("ZIP entry escapes its extraction directory: " + name);
        }

        if (Files.exists(file, LinkOption.NOFOLLOW_LINKS))
        {
          throw new IllegalArgumentException("ZIP entry would overwrite an existing extracted file: " + name);
        }

        Files.createDirectories(file.getParent());
        try (InputStream input = archive.getInputStream(entry))
        {
          Files.copy(input, file);
        }
      }
    }

  }

  private static void validateZipEntryName(String name)
  {
    if (name == null || name.isBlank() || name.indexOf('\\') >= 0 || name.startsWith("/") || name.matches("^[A-Za-z]:.*"))
    {
      throw new IllegalArgumentException("Unsafe ZIP entry path: " + name);
    }

    for (String segment : name.split("/"))
    {
      if ("..".equals(segment) || ".".equals(segment))
      {
        throw new IllegalArgumentException("Unsafe ZIP entry path: " + name);
      }
    }
  }

  private static void verifyPackage(String[] args) throws Exception
  {
    if (args.length != 9)
    {
      throw new IllegalArgumentException(
          "verify-package requires staging, signed repository, ZIP, metadata, primary fingerprint, public key file, GPG executable, and summary");
    }

    File staging = requireDirectory(args[1], "validated Maven staging");
    File signedRepository = requireDirectory(args[2], "signed local Maven repository");
    File zip = requireFile(new File(args[3]), "deployment ZIP");

    requireWorkspaceOutput(staging, "validated Maven staging");
    requireWorkspaceOutput(signedRepository, "signed local Maven repository");
    requireWorkspaceOutput(zip, "deployment ZIP");
    requireWorkspaceOutput(new File(args[4]), "build metadata");
    requireWorkspaceOutput(new File(args[6]), "GnuPG public verification key");
    requireWorkspaceOutput(new File(args[8]), "package verification report");
    rejectSymbolicLinks(staging.toPath(), "validated Maven staging");
    rejectSymbolicLinks(signedRepository.toPath(), "signed local Maven repository");

    Map<String, String> metadata = readProperties(requireFile(new File(args[4]), "build metadata"));
    PublishPolicyCommand.enforce(metadata.get("cdo.build.type"), "PACKAGE", metadata.get("cdo.project.release"), metadata.get("cdo.build.stream"));

    String expectedFingerprint = args[5].replace(" ", "").toUpperCase();
    if (!expectedFingerprint.matches("(?:[0-9A-F]{40}|[0-9A-F]{64})"))
    {
      throw new IllegalArgumentException("Expected primary-key fingerprint must be a full 40- or 64-character hexadecimal fingerprint");
    }

    AuditCommand.validatePublicationScope(staging, true, true, true, metadata);
    File publicKey = requireFile(new File(args[6]), "GnuPG public verification key");
    if (Files.isSymbolicLink(publicKey.toPath()))
    {
      throw new IllegalArgumentException("GnuPG public verification key must not be a symbolic link");
    }
    File orgDirectory = requireDirectory(new File(staging, "org").getPath(), "staging/org");
    List<File> files = regularFiles(orgDirectory);
    List<File> artifacts = new ArrayList<>();

    for (File file : files)
    {
      if (file.getName().endsWith(".jar") || file.getName().endsWith(".pom") || file.getName().endsWith(".zip"))
      {
        artifacts.add(file);
      }
    }

    int expectedArtifacts = BUNDLES.size() * 4 + 3;
    if (artifacts.size() != expectedArtifacts)
    {
      throw new IllegalArgumentException("Expected " + expectedArtifacts + " main, POM, sources, and Javadoc artifacts; found " + artifacts.size());
    }

    Set<String> bundles = new LinkedHashSet<>();
    Set<String> coordinates = new LinkedHashSet<>();
    Set<String> expectedFiles = new LinkedHashSet<>();
    Set<String> expectedSignatures = new LinkedHashSet<>();
    String[] algorithms = { "md5", "sha1", "sha256", "sha512" };
    String[] digests = { "MD5", "SHA-1", "SHA-256", "SHA-512" };

    for (File artifact : artifacts)
    {
      String relative = relativePath(staging, artifact);
      String[] path = relative.split("/");
      if (path.length != 6 || !"org".equals(path[0]) || !"eclipse".equals(path[1]) || !"cdo".equals(path[2]))
      {
        throw new IllegalArgumentException("Unexpected Maven repository path: " + relative);
      }

      String bundle = path[3];
      String version = path[4];
      boolean distribution = EXAMPLES_DISTRIBUTION.equals(bundle);
      boolean bom = RELEASE_BOM.equals(bundle);
      if (distribution)
      {
        if (!EXAMPLES_DISTRIBUTION_VERSION.equals(version))
        {
          throw new IllegalArgumentException("Unexpected examples distribution release version: " + version);
        }
      }
      else if (!bom)
      {
        bundles.add(bundle);
      }

      if (artifact.getName().endsWith(".pom"))
      {
        Element project = parse(artifact).getDocumentElement();

        String group = text(project, "groupId");
        String artifactID = text(project, "artifactId");
        String pomVersion = text(project, "version");
        if (!"org.eclipse.cdo".equals(group) || !bundle.equals(artifactID) || !version.equals(pomVersion))
        {
          throw new IllegalArgumentException("POM coordinates do not match repository path: " + relative);
        }

        coordinates.add(group + ":" + artifactID + ":" + pomVersion);
        if (distribution)
        {
          Element properties = child(project, "properties");
          if (!metadata.get("cdo.git.commit").equals(text(properties, "cdo.git.commit"))
              || !metadata.get("cdo.drop.id").equals(text(properties, "cdo.drop.id")))
          {
            throw new IllegalArgumentException("Examples distribution POM CDO binary provenance does not match the selected drop: " + relative);
          }
        }
      }

      expectedFiles.add(relative);

      for (int i = 0; i < algorithms.length; i++)
      {
        String sidecar = relative + "." + algorithms[i];

        File checksum = new File(staging, sidecar.replace('/', File.separatorChar));
        if (!checksum.isFile() || !ChecksumsCommand.digest(artifact, digests[i]).equalsIgnoreCase(Files.readString(checksum.toPath()).trim()))
        {
          throw new IllegalArgumentException("Missing or invalid checksum: " + sidecar);
        }

        expectedFiles.add(sidecar);
      }

      String signaturePath = relative + ".asc";
      File signature = new File(staging, signaturePath.replace('/', File.separatorChar));
      if (!signature.isFile() || !new String(Files.readAllBytes(signature.toPath()), StandardCharsets.US_ASCII).startsWith("-----BEGIN PGP SIGNATURE-----"))
      {
        throw new IllegalArgumentException("Missing, malformed, or non-armored signature: " + signaturePath);
      }

      expectedFiles.add(signaturePath);
      expectedSignatures.add(signaturePath);

      File deployed = new File(signedRepository, relative.replace('/', File.separatorChar));
      File deployedSignature = new File(signedRepository, signaturePath.replace('/', File.separatorChar));
      if (!deployed.isFile() || !sameBytes(artifact, deployed))
      {
        throw new IllegalArgumentException("Local signed repository artifact differs from validated staging: " + relative);
      }

      if (!deployedSignature.isFile() || !sameBytes(signature, deployedSignature))
      {
        throw new IllegalArgumentException("Staged signature differs from local Maven GPG output: " + signaturePath);
      }
    }

    if (bundles.size() != BUNDLES.size() || !bundles.equals(new LinkedHashSet<>(BUNDLES)) || coordinates.size() != BUNDLES.size() + 2)
    {
      throw new IllegalArgumentException(
          "Maven repository does not contain exactly one version of each of the " + BUNDLES.size() + " approved bundles: " + coordinates);
    }

    for (String bundle : BUNDLES)
    {
      File artifactDirectory = new File(orgDirectory, "eclipse/cdo/" + bundle);
      File[] versionDirectories = artifactDirectory.listFiles(File::isDirectory);
      if (versionDirectories == null || versionDirectories.length != 1)
      {
        throw new IllegalArgumentException("Expected one release version directory for " + bundle);
      }

      String version = versionDirectories[0].getName();
      String prefix = "org/eclipse/cdo/" + bundle + "/" + version + "/" + bundle + "-" + version;
      for (String suffix : new String[] { ".jar", "-sources.jar", "-javadoc.jar", ".pom" })
      {
        if (!expectedFiles.contains(prefix + suffix))
        {
          throw new IllegalArgumentException("Missing required Maven file: " + prefix + suffix);
        }
      }
    }

    Set<String> actualFiles = new LinkedHashSet<>();
    Set<String> actualSignatures = new LinkedHashSet<>();

    for (File file : files)
    {
      if (Files.isSymbolicLink(file.toPath()))
      {
        throw new IllegalArgumentException("Symbolic links are not permitted in the Maven deployment tree: " + file);
      }

      String relative = relativePath(staging, file);
      actualFiles.add(relative);

      if (relative.endsWith(".asc"))
      {
        actualSignatures.add(relative);
      }

      if (!expectedFiles.contains(relative) && !isMavenMetadataPath(relative))
      {
        throw new IllegalArgumentException("Unexpected file in deployment repository: " + relative);
      }
    }

    if (!expectedSignatures.equals(actualSignatures) || actualSignatures.size() != expectedArtifacts)
    {
      throw new IllegalArgumentException("Deployment repository must contain exactly the " + expectedArtifacts + " expected detached signatures");
    }

    verifySignatures(args[7], publicKey, expectedFingerprint, artifacts, staging);
    verifyDeploymentZip(zip, staging, actualFiles);

    String sha256 = ChecksumsCommand.digest(zip, "SHA-256");

    try (PrintWriter out = new PrintWriter(new FileOutputStream(args[8], true), true, StandardCharsets.UTF_8))
    {
      out.println("signatures.verified=" + expectedArtifacts);
      out.println("signature.verification=VALIDATED");
      out.println("signing.primary.fingerprint=" + expectedFingerprint);
      out.println("deployment.zip=" + zip.getName());
      out.println("deployment.zip.sha256=" + sha256);
      out.println("central.publication.performed=false");
    }

    System.out.println(
        "Verified " + expectedArtifacts + " OpenPGP signatures, staged repository bytes, checksums, and exact ZIP inventory. Publication performed=false.");
  }

  private static void incrementalPackage(String[] args) throws Exception
  {
    if (args.length != 9)
    {
      throw new IllegalArgumentException(
          "incremental-package requires signed staging, classification manifest, complete QA ZIP, output ZIP, primary fingerprint, public key, GPG executable, and summary");
    }

    File staging = requireDirectory(args[1], "verified complete QA staging");
    File manifestFile = requireFile(new File(args[2]), "Central classification manifest");
    File completeQaZip = requireFile(new File(args[3]), "verified complete QA deployment ZIP");
    File zip = new File(args[4]);
    File publicKey = requireFile(new File(args[6]), "GnuPG public verification key");
    File summary = new File(args[8]);
    for (File output : List.of(zip, summary))
    {
      requireWorkspaceOutput(output, "incremental publication output");
      if (Files.exists(output.toPath(), LinkOption.NOFOLLOW_LINKS))
      {
        throw new IllegalArgumentException("Incremental output already exists: " + output);
      }
    }

    requireWorkspaceOutput(staging, "verified complete QA staging");
    requireWorkspaceOutput(manifestFile, "Central classification manifest");
    requireWorkspaceOutput(completeQaZip, "verified complete QA deployment ZIP");
    rejectSymbolicLinks(staging.toPath(), "verified complete QA staging");

    File orgDirectory = requireDirectory(new File(staging, "org").getPath(), "complete QA staging/org");
    Set<String> completeFiles;
    try
    {
      completeFiles = new LinkedHashSet<>();
      for (File file : regularFiles(orgDirectory))
      {
        completeFiles.add(relativePath(staging, file));
      }
    }
    catch (IOException ex)
    {
      throw new IllegalArgumentException("Unable to enumerate complete QA staging files", ex);
    }

    verifyDeploymentZip(completeQaZip, staging, completeFiles);
    if (Files.isSymbolicLink(publicKey.toPath()))
    {
      throw new IllegalArgumentException("GnuPG public verification key must not be a symbolic link");
    }

    Map<String, String> manifest = readProperties(manifestFile);
    int componentCount = integerProperty(manifest, "release.component.count");
    if (componentCount != BUNDLES.size() + 2)
    {
      throw new IllegalArgumentException("Classification manifest must identify exactly " + (BUNDLES.size() + 2) + " release coordinates");
    }

    Map<String, String> classifications = new LinkedHashMap<>();
    Map<String, Integer> counts = new LinkedHashMap<>();
    List<String> newCoordinates = new ArrayList<>();
    Set<String> manifestKeys = new LinkedHashSet<>();
    for (int index = 1; index <= componentCount; index++)
    {
      String prefix = "release.component." + index + ".";
      String coordinate = requiredProperty(manifest, prefix + "coordinate");
      String classification = requiredProperty(manifest, prefix + "classification");
      String kind = requiredProperty(manifest, prefix + "kind");
      requiredProperty(manifest, prefix + "reason");
      manifestKeys.add(prefix + "coordinate");
      manifestKeys.add(prefix + "kind");
      manifestKeys.add(prefix + "classification");
      manifestKeys.add(prefix + "reason");
      if (classifications.putIfAbsent(coordinate, classification) != null)
      {
        throw new IllegalArgumentException("Duplicate coordinate in Central classification manifest: " + coordinate);
      }

      if (!Set.of("NEW", "REUSABLE", "CONFLICT", "LOOKUP_ERROR").contains(classification))
      {
        throw new IllegalArgumentException("Unknown Central classification " + classification + " for " + coordinate);
      }

      String[] coordinateParts = coordinate.split(":", -1);
      if (coordinateParts.length != 3 || !"org.eclipse.cdo".equals(coordinateParts[0]))
      {
        throw new IllegalArgumentException("Invalid coordinate in Central classification manifest: " + coordinate);
      }

      String expectedKind;
      if (BUNDLES.contains(coordinateParts[1]))
      {
        expectedKind = "bundle";
        File versionDirectory = singleVersionDirectory(staging, coordinateParts[1]);
        if (!versionDirectory.getName().equals(coordinateParts[2]))
        {
          throw new IllegalArgumentException("Classified bundle version does not match complete QA package: " + coordinate);
        }
      }
      else if (RELEASE_BOM.equals(coordinateParts[1]))
      {
        expectedKind = "bom";
        String releaseVersion = BomCommand.normalizeProjectReleaseVersion(requiredProperty(manifest, "cdo.project.release"));
        if (!releaseVersion.equals(coordinateParts[2]))
        {
          throw new IllegalArgumentException("Classified BOM version does not match normalized project release: " + coordinate);
        }
      }
      else if (EXAMPLES_DISTRIBUTION.equals(coordinateParts[1]))
      {
        expectedKind = "examples";
        String selectedExamplesVersion = requiredProperty(manifest, "examples.distribution.version");
        if (!selectedExamplesVersion.matches(Pattern.quote(EXAMPLES_DISTRIBUTION_VERSION_LINE) + "\\.[0-9]+")
            || !selectedExamplesVersion.equals(coordinateParts[2]))
        {
          throw new IllegalArgumentException("Classified examples version does not match the selected package version: " + coordinate);
        }
      }
      else
      {
        throw new IllegalArgumentException("Unapproved coordinate in Central classification manifest: " + coordinate);
      }

      if (!expectedKind.equals(kind))
      {
        throw new IllegalArgumentException("Classification kind mismatch for " + coordinate + ": " + kind);
      }

      counts.merge(classification, 1, Integer::sum);
      if ("NEW".equals(classification))
      {
        newCoordinates.add(coordinate);
      }
    }

    for (String classification : List.of("NEW", "REUSABLE", "CONFLICT", "LOOKUP_ERROR"))
    {
      manifestKeys.add("classification." + classification.toLowerCase(Locale.ROOT) + ".count");
      if (integerProperty(manifest, "classification." + classification.toLowerCase(Locale.ROOT) + ".count") != counts.getOrDefault(classification, 0))
      {
        throw new IllegalArgumentException("Classification count is inconsistent for " + classification);
      }
    }

    if (counts.getOrDefault("CONFLICT", 0) != 0 || counts.getOrDefault("LOOKUP_ERROR", 0) != 0)
    {
      throw new IllegalArgumentException("Cannot prepare publication with CONFLICT or LOOKUP_ERROR coordinates");
    }

    int publicationCount = integerProperty(manifest, "publication.coordinate.count");
    manifestKeys.add("publication.coordinate.count");
    if (publicationCount != newCoordinates.size())
    {
      throw new IllegalArgumentException("Publication coordinate count does not match NEW classifications");
    }

    for (int index = 0; index < newCoordinates.size(); index++)
    {
      if (!newCoordinates.get(index).equals(requiredProperty(manifest, "publication.coordinate." + (index + 1))))
      {
        throw new IllegalArgumentException("Publication coordinate inventory does not match NEW classifications");
      }

      manifestKeys.add("publication.coordinate." + (index + 1));
    }

    for (String key : manifest.keySet())
    {
      if (!manifestKeys.contains(key)
          && !Set.of("cdo.project.release", "cdo.drop.id", "examples.distribution.version", "release.component.count").contains(key))
      {
        throw new IllegalArgumentException("Unexpected property in Central classification manifest: " + key);
      }

      if (key.startsWith("publication.coordinate.") && !"publication.coordinate.count".equals(key))
      {
        int ordinal = parseOrdinal(key, "publication.coordinate.");
        if (ordinal < 1 || ordinal > publicationCount)
        {
          throw new IllegalArgumentException("Unexpected publication coordinate entry: " + key);
        }
      }
    }

    if (newCoordinates.isEmpty())
    {
      Files.createDirectories(summary.toPath().getParent());
      Files.writeString(summary.toPath(), "incremental.publication=NOT_REQUIRED\nnew.coordinates=0\ncentral.publication.performed=false\n",
          StandardCharsets.UTF_8);
      System.out.println("No NEW Central coordinates; Sonatype upload package is not required. Publication performed=false.");
      return;
    }

    List<File> artifacts = new ArrayList<>();
    Set<String> expectedFiles = new LinkedHashSet<>();
    Set<String> selectedArtifacts = new LinkedHashSet<>();
    Map<String, File> filesToPackage = new TreeMap<>();
    String[] checksumSuffixes = { ".md5", ".sha1", ".sha256", ".sha512" };
    String[] checksumAlgorithms = { "MD5", "SHA-1", "SHA-256", "SHA-512" };
    for (String coordinate : newCoordinates)
    {
      String[] parts = coordinate.split(":", -1);
      if (parts.length != 3 || !"org.eclipse.cdo".equals(parts[0]))
      {
        throw new IllegalArgumentException("Invalid or unapproved publication coordinate: " + coordinate);
      }

      String artifact = parts[1];
      String version = parts[2];
      if (!BUNDLES.contains(artifact) && !RELEASE_BOM.equals(artifact) && !EXAMPLES_DISTRIBUTION.equals(artifact))
      {
        throw new IllegalArgumentException("Unapproved publication coordinate: " + coordinate);
      }

      if (EXAMPLES_DISTRIBUTION.equals(artifact) && !EXAMPLES_DISTRIBUTION_VERSION.equals(version))
      {
        throw new IllegalArgumentException("Examples distribution coordinate does not match package.properties: " + coordinate);
      }

      if (!selectedArtifacts.add(artifact))
      {
        throw new IllegalArgumentException("Duplicate artifact in NEW coordinate inventory: " + artifact);
      }

      String base = "org/eclipse/cdo/" + artifact + "/" + version + "/" + artifact + "-" + version;
      List<String> artifactSuffixes = RELEASE_BOM.equals(artifact) ? List.of(".pom")
          : EXAMPLES_DISTRIBUTION.equals(artifact) ? List.of(".pom", "-examples.zip") : List.of(".pom", ".jar", "-sources.jar", "-javadoc.jar");
      Set<String> coordinateFiles = new LinkedHashSet<>();
      for (String suffix : artifactSuffixes)
      {
        String relative = base + suffix;
        File file = requireFile(new File(staging, relative.replace('/', File.separatorChar)), "required incremental publication artifact");
        artifacts.add(file);
        expectedFiles.add(relative);
        coordinateFiles.add(relative);
        filesToPackage.put(relative, file);

        for (int index = 0; index < checksumSuffixes.length; index++)
        {
          String sidecar = relative + checksumSuffixes[index];
          File checksum = requireFile(new File(staging, sidecar.replace('/', File.separatorChar)), "required artifact checksum");
          String actual = ChecksumsCommand.digest(file, checksumAlgorithms[index]);
          if (!actual.equalsIgnoreCase(Files.readString(checksum.toPath(), StandardCharsets.US_ASCII).trim()))
          {
            throw new IllegalArgumentException("Checksum mismatch in complete QA staging: " + sidecar);
          }

          expectedFiles.add(sidecar);
          coordinateFiles.add(sidecar);
          filesToPackage.put(sidecar, checksum);
        }

        String signaturePath = relative + ".asc";
        File signature = requireFile(new File(staging, signaturePath.replace('/', File.separatorChar)), "required OpenPGP signature");
        if (!Files.readString(signature.toPath(), StandardCharsets.US_ASCII).startsWith("-----BEGIN PGP SIGNATURE-----"))
        {
          throw new IllegalArgumentException("Malformed OpenPGP signature in complete QA staging: " + signaturePath);
        }

        expectedFiles.add(signaturePath);
        coordinateFiles.add(signaturePath);
        filesToPackage.put(signaturePath, signature);
      }

      validateNoUnexpectedCoordinateFiles(staging, artifact, version, coordinateFiles);
    }

    String bomCoordinate = "org.eclipse.cdo:" + RELEASE_BOM + ":"
        + BomCommand.normalizeProjectReleaseVersion(requiredProperty(manifest, "cdo.project.release"));
    String examplesCoordinate = "org.eclipse.cdo:" + EXAMPLES_DISTRIBUTION + ":" + EXAMPLES_DISTRIBUTION_VERSION;
    if (!classifications.containsKey(bomCoordinate) || !classifications.containsKey(examplesCoordinate))
    {
      throw new IllegalArgumentException("Classification manifest omits or misidentifies the release BOM or examples coordinate");
    }

    for (String bundle : BUNDLES)
    {
      if (!classifications.containsKey("org.eclipse.cdo:" + bundle + ":" + singleVersionDirectory(staging, bundle).getName()))
      {
        throw new IllegalArgumentException("Classification manifest omits an approved bundle coordinate: " + bundle);
      }
    }

    if (classifications.size() != BUNDLES.size() + 2 || !new LinkedHashSet<>(BUNDLES).equals(classifications.keySet().stream()
        .map(coordinate -> coordinate.split(":", -1)[1]).filter(BUNDLES::contains).collect(Collectors.toCollection(LinkedHashSet::new))))
    {
      throw new IllegalArgumentException("Classification manifest does not identify exactly the approved release coordinates");
    }

    String fingerprint = args[5].replace(" ", "").toUpperCase(Locale.ROOT);
    if (!fingerprint.matches("(?:[0-9A-F]{40}|[0-9A-F]{64})"))
    {
      throw new IllegalArgumentException("Expected primary-key fingerprint must be a full hexadecimal fingerprint");
    }

    verifySignatures(args[7], publicKey, fingerprint, artifacts, staging);
    Files.createDirectories(zip.toPath().getParent());

    try
    {
      try (ZipOutputStream output = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(zip.toPath()))))
      {
        Set<String> directories = new LinkedHashSet<>();
        for (String path : expectedFiles)
        {
          int slash = path.lastIndexOf('/');
          while (slash > 0)
          {
            directories.add(path.substring(0, slash + 1));
            slash = path.lastIndexOf('/', slash - 1);
          }
        }

        for (String directory : directories.stream().sorted().collect(Collectors.toList()))
        {
          output.putNextEntry(new ZipEntry(directory));
          output.closeEntry();
        }

        for (Map.Entry<String, File> entry : filesToPackage.entrySet())
        {
          output.putNextEntry(new ZipEntry(entry.getKey()));
          Files.copy(entry.getValue().toPath(), output);
          output.closeEntry();
        }
      }

      verifyDeploymentZip(zip, staging, expectedFiles);
    }
    catch (Exception | Error ex)
    {
      Files.deleteIfExists(zip.toPath());
      throw ex;
    }
    Files.createDirectories(summary.toPath().getParent());
    StringBuilder report = new StringBuilder("incremental.publication=READY\nnew.coordinates=").append(newCoordinates.size()).append("\nupload.zip=")
        .append(zip.getName()).append("\nupload.zip.sha256=").append(ChecksumsCommand.digest(zip, "SHA-256")).append("\nsignatures.verified=")
        .append(artifacts.size()).append("\nfiles.verified=").append(expectedFiles.size()).append("\ncentral.publication.performed=false\n");
    Files.writeString(summary.toPath(), report.toString(), StandardCharsets.UTF_8);
    System.out.println("Verified incremental upload ZIP with " + newCoordinates.size() + " NEW coordinates, " + expectedFiles.size() + " files, and "
        + artifacts.size() + " signatures. Publication performed=false.");
  }

  private static void validateNoUnexpectedCoordinateFiles(File staging, String artifact, String version, Set<String> expectedFiles) throws IOException
  {
    File directory = new File(staging, "org/eclipse/cdo/" + artifact + "/" + version);
    try (Stream<Path> paths = Files.list(directory.toPath()))
    {
      for (Path path : paths.filter(Files::isRegularFile).collect(Collectors.toList()))
      {
        String relative = relativePath(staging, path.toFile());
        if (!expectedFiles.contains(relative))
        {
          throw new IllegalArgumentException("Unexpected file in NEW coordinate directory: " + relative);
        }
      }
    }
  }

  private static String requiredProperty(Map<String, String> properties, String key)
  {
    String value = properties.get(key);
    if (value == null || value.isBlank())
    {
      throw new IllegalArgumentException("Missing required manifest property: " + key);
    }

    return value;
  }

  private static int integerProperty(Map<String, String> properties, String key)
  {
    try
    {
      return Integer.parseInt(requiredProperty(properties, key));
    }
    catch (NumberFormatException ex)
    {
      throw new IllegalArgumentException("Invalid integer manifest property: " + key, ex);
    }
  }

  private static int parseOrdinal(String key, String prefix)
  {
    try
    {
      return Integer.parseInt(key.substring(prefix.length()));
    }
    catch (NumberFormatException ex)
    {
      throw new IllegalArgumentException("Invalid ordinal manifest property: " + key, ex);
    }
  }

  private static void verifyBomPackage(String[] args) throws Exception
  {
    if (args.length != 11)
    {
      throw new IllegalArgumentException(
          "verify-bom-package requires staging, signed repository, ZIP, build metadata, publishing metadata, inventory, primary fingerprint, public key, GPG executable, and summary");
    }

    File staging = requireDirectory(args[1], "CDO 4.36 BOM staging");
    File signedRepository = requireDirectory(args[2], "signed CDO 4.36 BOM repository");
    File zip = requireFile(new File(args[3]), "CDO 4.36 BOM deployment ZIP");
    File metadataFile = requireFile(new File(args[4]), "build metadata");
    File publishingFile = requireFile(new File(args[5]), "publishing metadata");
    Map<String, String> metadata = readProperties(metadataFile);
    Map<String, String> publishing = readProperties(publishingFile);
    Map<String, String> inventory = Bom436Command.verifySelectedDrop(metadata, args[6]);
    Map<String, String> versions = releaseInventoryVersions(inventory, BUNDLES_436);
    PublishPolicyCommand.enforce(metadata.get("cdo.build.type"), "PACKAGE_BOM_436_FOR_CENTRAL", metadata.get("cdo.project.release"),
        metadata.get("cdo.build.stream"));
    requireWorkspaceOutput(staging, "CDO 4.36 BOM staging");
    requireWorkspaceOutput(signedRepository, "signed CDO 4.36 BOM repository");
    requireWorkspaceOutput(zip, "CDO 4.36 BOM deployment ZIP");
    requireWorkspaceOutput(new File(args[8]), "GnuPG public verification key");
    requireWorkspaceOutput(new File(args[10]), "CDO 4.36 BOM package summary");
    rejectSymbolicLinks(staging.toPath(), "CDO 4.36 BOM staging");
    rejectSymbolicLinks(signedRepository.toPath(), "signed CDO 4.36 BOM repository");
    File publicKey = requireFile(new File(args[8]), "GnuPG public verification key");
    if (Files.isSymbolicLink(publicKey.toPath()))
    {
      throw new IllegalArgumentException("GnuPG public verification key must not be a symbolic link");
    }

    Bom436Command.validateStaging(staging, metadata, publishing, versions, true, BUNDLES_436);
    String expectedFingerprint = args[7].replace(" ", "").toUpperCase();
    if (!expectedFingerprint.matches("(?:[0-9A-F]{40}|[0-9A-F]{64})"))
    {
      throw new IllegalArgumentException("Expected primary-key fingerprint must be a full 40- or 64-character hexadecimal fingerprint");
    }

    String relativePom = "org/eclipse/cdo/org.eclipse.cdo.bom/4.36.0/org.eclipse.cdo.bom-4.36.0.pom";
    String relativeSignature = relativePom + ".asc";
    File pom = new File(staging, relativePom.replace('/', File.separatorChar));
    File signature = new File(staging, relativeSignature.replace('/', File.separatorChar));
    File deployedPom = new File(signedRepository, relativePom.replace('/', File.separatorChar));
    File deployedSignature = new File(signedRepository, relativeSignature.replace('/', File.separatorChar));
    if (!deployedPom.isFile() || !sameBytes(pom, deployedPom))
    {
      throw new IllegalArgumentException("Signed local repository POM differs from validated BOM staging");
    }

    if (!deployedSignature.isFile() || !sameBytes(signature, deployedSignature))
    {
      throw new IllegalArgumentException("Staged BOM signature differs from local Maven GPG output");
    }

    Set<String> signedFilesAllowed = new LinkedHashSet<>();
    signedFilesAllowed.add(relativePom);
    signedFilesAllowed.add(relativeSignature);
    for (String checksum : Bom436Command.CHECKSUMS)
    {
      signedFilesAllowed.add(relativePom + "." + checksum);
    }

    for (File file : regularFiles(signedRepository))
    {
      String relative = relativePath(signedRepository, file);
      if (!isAllowedSignedBomRepositoryPath(relative, signedFilesAllowed))
      {
        throw new IllegalArgumentException("Unexpected file in signed BOM repository: " + relative);
      }
    }

    verifySignatures(args[9], publicKey, expectedFingerprint, List.of(pom), staging);
    Set<String> expectedFiles = new LinkedHashSet<>();
    for (File file : regularFiles(staging))
    {
      expectedFiles.add(relativePath(staging, file));
    }

    verifyDeploymentZip(zip, staging, expectedFiles);
    String sha256 = ChecksumsCommand.digest(zip, "SHA-256");
    String summary = "deployment.type=BOM_ONLY\ncoordinate=org.eclipse.cdo:org.eclipse.cdo.bom:4.36.0\n" + "signatures.verified=1\n"
        + "signature.verification=VALIDATED\nsigning.primary.fingerprint=" + expectedFingerprint + "\n" + "deployment.zip=" + zip.getName() + "\n"
        + "deployment.zip.sha256=" + sha256 + "\ncentral.publication.performed=false\n";
    Files.writeString(new File(args[10]).toPath(), summary, StandardCharsets.UTF_8);
    System.out.println("Verified the POM-only CDO 4.36 BOM signature, checksums, local signing output, and exact ZIP inventory. Publication performed=false.");
  }

  private static void verifySignatures(String gpg, File publicKey, String expectedFingerprint, List<File> artifacts, File staging) throws Exception
  {
    String workspace = System.getenv("CDO_WORKSPACE_ROOT");
    Path temporaryParent = workspace == null || workspace.isBlank() ? staging.toPath().getParent() : Paths.get(workspace).toRealPath();
    Path work = Files.createTempDirectory(temporaryParent, "g-");
    securePath(work, true);

    try
    {
      Path verifyHome = Files.createDirectory(work.resolve("v"));
      securePath(verifyHome, true);
      validateGpgSocketPaths(gpg, verifyHome);
      gpg(gpg, verifyHome, "--import", publicKey.getAbsolutePath());
      if (!primaryFingerprints(gpg, verifyHome).equals(Set.of(expectedFingerprint)))
      {
        throw new IllegalArgumentException("Isolated verification keyring contains an unexpected primary key");
      }

      for (File artifact : artifacts)
      {
        String signature = relativePath(staging, artifact) + ".asc";
        File signatureFile = new File(staging, signature.replace('/', File.separatorChar));
        String output = gpg(gpg, verifyHome, "--status-fd", "1", "--verify", signatureFile.getAbsolutePath(), artifact.getAbsolutePath());
        requireValidSignature(output, expectedFingerprint, signatureFile);
      }
    }
    finally
    {
      try
      {
        if (Files.isDirectory(work.resolve("v")))
        {
          stopGpgAgent(gpg, work.resolve("v"));
        }
      }
      finally
      {
        delete(work.toFile());
      }
    }
  }

  private static void stopGpgAgent(String gpg, Path home) throws Exception
  {
    runGpgconf(gpg, home, "--kill", "gpg-agent");
  }

  private static void validateGpgSocketPaths(String gpg, Path home) throws Exception
  {
    if (isWindows())
    {
      return;
    }

    String version = gpg(gpg, home, "--version").lines().findFirst().orElse("unknown GnuPG version");
    String directories = runGpgconf(gpg, home, "--list-dirs");
    Set<String> sockets = new LinkedHashSet<>();
    for (String line : directories.split("\\R"))
    {
      int separator = line.indexOf(':');
      if (separator > 0 && (line.startsWith("agent-socket:") || line.startsWith("agent-extra-socket:") || line.startsWith("agent-browser-socket:")
          || line.startsWith("agent-ssh-socket:")))
      {
        String socket = line.substring(separator + 1);
        sockets.add(socket);
        int pathBytes = socket.getBytes(StandardCharsets.UTF_8).length;
        int maximumPathBytes = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("linux") ? 107 : 99;
        if (pathBytes > maximumPathBytes)
        {
          throw new IllegalArgumentException("GnuPG " + version + " socket path exceeds the safe Unix-domain limit: " + socket + " (" + pathBytes + " bytes)");
        }
      }
    }

    if (sockets.isEmpty())
    {
      throw new IllegalArgumentException("GnuPG " + version + " did not report any agent socket paths for isolated home " + home);
    }

    System.out.println("GnuPG socket preflight: " + version + "; " + String.join(", ", sockets));
  }

  private static String runGpgconf(String gpg, Path home, String... arguments) throws Exception
  {
    Path gpgconfPath = gpgconfPath(gpg);
    Path output = Files.createTempFile(home, "gconf-", ".txt");
    securePath(output, false);

    List<String> command = new ArrayList<>(Arrays.asList(gpgconfPath.toString(), "--homedir", home.toString()));
    command.addAll(Arrays.asList(arguments));
    ProcessBuilder builder = new ProcessBuilder(command);
    builder.redirectErrorStream(true);
    builder.redirectOutput(output.toFile());
    builder.environment().put("GNUPGHOME", home.toString());
    builder.environment().put("LC_ALL", "C");
    builder.environment().put("LANG", "C");

    try
    {
      Process process = builder.start();
      if (!process.waitFor(10, TimeUnit.SECONDS))
      {
        process.destroyForcibly();
        throw new IllegalArgumentException("Timed out running gpgconf for isolated GnuPG home " + home);
      }

      String result = Files.readString(output, StandardCharsets.UTF_8);
      if (process.exitValue() != 0)
      {
        throw new IllegalArgumentException("gpgconf failed with exit code " + process.exitValue() + " for isolated GnuPG home " + home + ": " + result.trim());
      }

      return result;
    }
    finally
    {
      Files.deleteIfExists(output);
    }
  }

  private static Path gpgconfPath(String gpg)
  {
    Path configuredGpg = Paths.get(gpg);
    return configuredGpg.getParent() == null ? Paths.get(isWindows() ? "gpgconf.exe" : "gpgconf")
        : configuredGpg.toAbsolutePath().normalize().resolveSibling(isWindows() ? "gpgconf.exe" : "gpgconf");
  }

  private static boolean isWindows()
  {
    return System.getProperty("os.name", "").toLowerCase().contains("win");
  }

  private static String gpg(String executable, Path home, String... arguments) throws Exception
  {
    Path output = Files.createTempFile(home, "gpg-result-", ".txt");
    securePath(output, false);

    List<String> command = new ArrayList<>(Arrays.asList(executable, "--no-options", "--batch", "--no-tty", "--homedir", home.toString()));
    command.addAll(Arrays.asList(arguments));

    ProcessBuilder builder = new ProcessBuilder(command);
    builder.redirectErrorStream(true);
    builder.redirectOutput(output.toFile());
    builder.environment().put("GNUPGHOME", home.toString());
    builder.environment().put("LC_ALL", "C");
    builder.environment().put("LANG", "C");

    Path executablePath = new File(executable).toPath().toAbsolutePath().normalize();
    Path executableDirectory = executablePath.getParent();
    if (executableDirectory != null && executablePath.getNameCount() > 1)
    {
      String path = builder.environment().getOrDefault("PATH", "");
      builder.environment().put("PATH", executableDirectory + File.pathSeparator + path);
    }

    Process process;

    try
    {
      process = builder.start();
    }
    catch (IOException e)
    {
      throw new IllegalArgumentException("GnuPG is required for PACKAGE signature verification", e);
    }

    process.getOutputStream().close();
    if (!process.waitFor(60, TimeUnit.SECONDS))
    {
      process.destroyForcibly();
      throw new IllegalArgumentException("GnuPG operation timed out");
    }

    String result = Files.readString(output, StandardCharsets.UTF_8);
    Files.deleteIfExists(output);

    if (process.exitValue() != 0)
    {
      String diagnostic = result.trim();
      if (diagnostic.length() > 800)
      {
        diagnostic = diagnostic.substring(diagnostic.length() - 800);
      }

      throw new IllegalArgumentException("GnuPG operation failed with exit code " + process.exitValue() + (diagnostic.isEmpty() ? "" : ": " + diagnostic));
    }

    return result;
  }

  private static Set<String> primaryFingerprints(String gpg, Path home) throws Exception
  {
    String output = gpg(gpg, home, "--with-colons", "--fingerprint", "--fingerprint", "--list-keys");
    Set<String> fingerprints = new LinkedHashSet<>();
    boolean primary = false;

    for (String line : output.split("\\R"))
    {
      String[] fields = line.split(":", -1);
      if ("pub".equals(fields[0]))
      {
        primary = true;
      }
      else if ("sub".equals(fields[0]))
      {
        primary = false;
      }
      else if (primary && "fpr".equals(fields[0]))
      {
        fingerprints.add(fields[9].toUpperCase());
        primary = false;
      }
    }

    return fingerprints;
  }

  private static void resolveSigningSubkeyFingerprint(String[] args) throws Exception
  {
    if (args.length != 6)
    {
      throw new IllegalArgumentException(
          "resolve-signing-subkey-fingerprint requires GnuPG, a key file, the expected primary fingerprint, and fingerprint and public-key output files");
    }

    String gpg = args[1];
    File keyFile = requireFile(new File(args[2]), "Jenkins signing-key credential");
    String expectedPrimary = args[3].replace(" ", "").toUpperCase();
    if (!expectedPrimary.matches("(?:[0-9A-F]{40}|[0-9A-F]{64})"))
    {
      throw new IllegalArgumentException("Expected primary-key fingerprint must be a full 40- or 64-character hexadecimal fingerprint");
    }

    Path output = requireWorkspaceOutput(new File(args[4]), "signing-subkey fingerprint output");
    Path publicKeyOutput = requireWorkspaceOutput(new File(args[5]), "public verification key output");
    Files.deleteIfExists(output);
    Files.deleteIfExists(publicKeyOutput);

    String workspace = System.getenv("CDO_WORKSPACE_ROOT");
    if (workspace == null || workspace.isBlank())
    {
      throw new IllegalArgumentException("CDO_WORKSPACE_ROOT is required to inspect the signing key safely");
    }

    Path workspaceRoot = Paths.get(workspace).toRealPath();
    if (workspaceRoot.equals(workspaceRoot.getRoot()))
    {
      throw new IllegalArgumentException("Refusing to inspect a signing key under a filesystem root");
    }

    Path temporaryHome = Files.createTempDirectory(workspaceRoot, "g-");
    String signingFingerprint;

    try
    {
      securePath(temporaryHome, true);
      validateGpgSocketPaths(gpg, temporaryHome);
      gpg(gpg, temporaryHome, "--import", keyFile.getAbsolutePath());
      if (!primaryFingerprints(gpg, temporaryHome).contains(expectedPrimary))
      {
        throw new IllegalArgumentException("Jenkins signing-key credential does not contain the configured primary fingerprint");
      }

      String listing = gpg(gpg, temporaryHome, "--with-colons", "--fixed-list-mode", "--fingerprint", "--fingerprint", "--list-secret-keys");
      signingFingerprint = findSigningSubkeyFingerprint(listing, expectedPrimary);
      gpg(gpg, temporaryHome, "--output", publicKeyOutput.toString(), "--export", expectedPrimary);
      if (!Files.isRegularFile(publicKeyOutput) || Files.size(publicKeyOutput) == 0)
      {
        throw new IllegalArgumentException("Unable to export public key for the configured primary fingerprint");
      }
    }
    finally
    {
      try
      {
        stopGpgAgent(gpg, temporaryHome);
      }
      finally
      {
        delete(temporaryHome.toFile());
      }
    }

    Files.writeString(output, signingFingerprint + "\n", StandardCharsets.US_ASCII);
    securePath(output, false);
    securePath(publicKeyOutput, false);
    System.out.println("Resolved one usable signing subkey under the configured primary key.");
  }

  private static String findSigningSubkeyFingerprint(String listing, String expectedPrimary)
  {
    String normalizedPrimary = expectedPrimary.replace(" ", "").toUpperCase();
    if (!normalizedPrimary.matches("(?:[0-9A-F]{40}|[0-9A-F]{64})"))
    {
      throw new IllegalArgumentException("Expected primary-key fingerprint must be a full 40- or 64-character hexadecimal fingerprint");
    }

    String currentPrimary = null;
    String currentRecord = "";
    boolean usableSigningSubkey = false;
    List<String> candidates = new ArrayList<>();

    for (String line : listing.split("\\R"))
    {
      String[] fields = line.split(":", -1);
      if (fields.length == 0)
      {
        continue;
      }

      String record = fields[0];
      if (record.startsWith("sec"))
      {
        currentPrimary = null;
        currentRecord = "primary";
      }
      else if (record.startsWith("ssb"))
      {
        currentRecord = "subkey";
        String validity = fields.length > 1 ? fields[1] : "";
        String capabilities = fields.length > 11 ? fields[11] : "";
        String token = fields.length > 14 ? fields[14] : "";
        boolean revokedExpiredOrDisabled = Arrays.asList("r", "e", "d", "i", "n").contains(validity);
        usableSigningSubkey = "ssb".equals(record) && !revokedExpiredOrDisabled && capabilities.indexOf('s') >= 0 && (token.isEmpty() || "+".equals(token));
      }
      else if ("fpr".equals(record))
      {
        if (fields.length <= 9)
        {
          throw new IllegalArgumentException("Malformed GnuPG fingerprint record while inspecting the signing key");
        }

        String fingerprint = fields[9].replace(" ", "").toUpperCase();
        if ("primary".equals(currentRecord))
        {
          currentPrimary = fingerprint;
        }
        else if ("subkey".equals(currentRecord) && usableSigningSubkey && normalizedPrimary.equals(currentPrimary))
        {
          if (!fingerprint.matches("[0-9A-F]{40}"))
          {
            throw new IllegalArgumentException("GnuPG returned a malformed signing-subkey fingerprint");
          }

          candidates.add(fingerprint);
        }

        currentRecord = "";
        usableSigningSubkey = false;
      }
    }

    if (candidates.size() != 1)
    {
      throw new IllegalArgumentException("Expected exactly one usable signing subkey for the configured primary fingerprint; found " + candidates.size());
    }

    return candidates.get(0);
  }

  private static void signingSubkeyFingerprintTest()
  {
    String expectedPrimary = "1".repeat(40);
    String otherPrimary = "2".repeat(40);
    String unknownPrimary = "7".repeat(40);
    String encryptionFingerprint = "3".repeat(40);
    String unavailableSigningFingerprint = "4".repeat(40);
    String signingFingerprint = "5".repeat(40);
    String otherSigningFingerprint = "6".repeat(40);

    String listing = String.join("\n", "sec#:-:3072:1:PRIMARY:1700000000::::::sc:", "fpr:::::::::" + expectedPrimary + ":", "uid:-::::1700000000:::Primary:",
        "ssb:-:3072:1:ENCRYPTION:1700000000::::::e:", "fpr:::::::::" + encryptionFingerprint + ":", "ssb#:-:3072:1:UNAVAILABLE:1700000000::::::s:",
        "fpr:::::::::" + unavailableSigningFingerprint + ":", "ssb:-:3072:1:SIGNING:1700000000::::::s:", "fpr:::::::::" + signingFingerprint + ":",
        "sec#:-:3072:1:OTHER:1700000000::::::sc:", "fpr:::::::::" + otherPrimary + ":", "ssb:-:3072:1:OTHER_SIGNING:1700000000::::::s:",
        "fpr:::::::::" + otherSigningFingerprint + ":");

    if (!signingFingerprint.equals(findSigningSubkeyFingerprint(listing, expectedPrimary)))
    {
      throw new AssertionError("Did not select the unique usable signing subkey belonging to the expected primary key");
    }

    String ambiguousListing = listing.replace("sec#:-:3072:1:OTHER:1700000000::::::sc:",
        "ssb:-:3072:1:DUPLICATE:1700000000::::::s:\nfpr:::::::::" + otherSigningFingerprint + ":\nsec#:-:3072:1:OTHER:1700000000::::::sc:");
    expectSigningSubkeyFailure(ambiguousListing, expectedPrimary, "ambiguous signing subkeys");
    expectSigningSubkeyFailure(listing.replace("ssb:-:3072:1:SIGNING:1700000000::::::s:", "ssb:-:3072:1:SIGNING:1700000000::::::e:"), expectedPrimary,
        "no signing-capable subkey");
    expectSigningSubkeyFailure(listing.replace("ssb:-:3072:1:SIGNING:", "ssb:e:3072:1:SIGNING:"), expectedPrimary, "expired signing subkey");
    expectSigningSubkeyFailure(listing, unknownPrimary, "wrong primary fingerprint");
    expectSigningSubkeyFailure(listing.replace(signingFingerprint, "not-a-fingerprint"), expectedPrimary, "malformed signing-subkey fingerprint");

    System.out
        .println("Signing-subkey selection tests passed: primary identity, signing capability, unavailable subkeys, ambiguity, and malformed fingerprints.");
  }

  private static void expectSigningSubkeyFailure(String listing, String expectedPrimary, String scenario)
  {
    try
    {
      findSigningSubkeyFingerprint(listing, expectedPrimary);
    }
    catch (IllegalArgumentException e)
    {
      return;
    }

    throw new AssertionError("Expected signing-subkey selection failure for " + scenario);
  }

  private static void requireValidSignature(String output, String expectedPrimary, File signature)
  {
    List<String[]> valid = new ArrayList<>();

    for (String line : output.split("\\R"))
    {
      if (!line.startsWith("[GNUPG:] "))
      {
        continue;
      }

      String[] fields = line.split("\\s+");
      String status = fields.length > 1 ? fields[1] : "";

      if (Arrays.asList("BADSIG", "ERRSIG", "NO_PUBKEY", "EXPSIG", "EXPKEYSIG", "REVKEYSIG", "KEYEXPIRED", "SIGEXPIRED").contains(status))
      {
        throw new IllegalArgumentException("GnuPG rejected signature status " + status + " for " + signature.getName());
      }

      if ("VALIDSIG".equals(status))
      {
        valid.add(fields);
      }
    }

    if (valid.size() != 1 || valid.get(0).length < 11)
    {
      throw new IllegalArgumentException("Expected one valid detached signature status for " + signature.getName());
    }

    String[] fields = valid.get(0);
    String signingFingerprint = fields[2].toUpperCase();
    String primaryFingerprint = fields.length > 11 ? fields[11].toUpperCase() : signingFingerprint;
    if (!signingFingerprint.matches("(?:[0-9A-F]{40}|[0-9A-F]{64})") || !expectedPrimary.equals(primaryFingerprint)
        || fields.length <= 11 && !expectedPrimary.equals(signingFingerprint))
    {
      throw new IllegalArgumentException("Signature does not match the configured primary-key fingerprint: " + signature.getName());
    }
  }

  private static void verifyDeploymentZip(File zip, File staging, Set<String> expectedFiles) throws Exception
  {
    Set<String> files = new LinkedHashSet<>();
    Set<String> directories = new LinkedHashSet<>();

    try (ZipFile archive = new ZipFile(zip))
    {
      Enumeration<? extends ZipEntry> entries = archive.entries();

      while (entries.hasMoreElements())
      {
        ZipEntry entry = entries.nextElement();

        String name = entry.getName();
        if (name.startsWith("/") || name.contains("\\") || name.matches("(^|.*/)\\.\\.(/.*|$)"))
        {
          throw new IllegalArgumentException("Unsafe Sonatype bundle ZIP path: " + name);
        }

        if (entry.isDirectory())
        {
          directories.add(name);
        }
        else if (!files.add(name))
        {
          throw new IllegalArgumentException("Duplicate Sonatype bundle ZIP entry: " + name);
        }
      }

      if (!files.equals(expectedFiles))
      {
        Set<String> missing = new LinkedHashSet<>(expectedFiles);
        missing.removeAll(files);

        Set<String> unexpected = new LinkedHashSet<>(files);
        unexpected.removeAll(expectedFiles);

        throw new IllegalArgumentException("Sonatype ZIP inventory mismatch; missing=" + missing + " unexpected=" + unexpected);
      }

      Set<String> expectedDirectories = new LinkedHashSet<>();

      for (String path : expectedFiles)
      {
        int slash = path.lastIndexOf('/');

        while (slash > 0)
        {
          expectedDirectories.add(path.substring(0, slash + 1));
          slash = path.lastIndexOf('/', slash - 1);
        }
      }

      if (!directories.equals(expectedDirectories))
      {
        throw new IllegalArgumentException("Sonatype ZIP contains an enclosing directory or unexpected directory layout");
      }

      for (String path : expectedFiles)
      {
        File file = new File(staging, path.replace('/', File.separatorChar));
        ZipEntry entry = archive.getEntry(path);

        try (InputStream in = archive.getInputStream(entry))
        {
          if (!sameBytes(file, in))
          {
            throw new IllegalArgumentException("Sonatype ZIP bytes differ from validated staging: " + path);
          }
        }
      }
    }
  }

  private static List<File> regularFiles(File directory) throws IOException
  {
    try (Stream<Path> paths = Files.walk(directory.toPath()))
    {
      return paths.filter(Files::isRegularFile).map(Path::toFile).sorted(Comparator.comparing(File::getPath)).collect(Collectors.toList());
    }
  }

  private static boolean sameBytes(File left, File right) throws IOException
  {
    try (InputStream in = new FileInputStream(left))
    {
      return sameBytes(right, in);
    }
  }

  private static boolean sameBytes(File file, InputStream other) throws IOException
  {
    try (InputStream in = new FileInputStream(file))
    {
      return sameBytes(in, other);
    }
  }

  private static boolean sameBytes(InputStream left, InputStream right) throws IOException
  {
    byte[] leftBuffer = new byte[8192];
    byte[] rightBuffer = new byte[8192];

    while (true)
    {
      int leftLength = readChunk(left, leftBuffer);
      int rightLength = readChunk(right, rightBuffer);
      if (leftLength != rightLength)
      {
        return false;
      }

      if (leftLength < 0)
      {
        return true;
      }

      for (int i = 0; i < leftLength; i++)
      {
        if (leftBuffer[i] != rightBuffer[i])
        {
          return false;
        }
      }
    }
  }

  private static int readChunk(InputStream in, byte[] buffer) throws IOException
  {
    int length = 0;

    while (length < buffer.length)
    {
      int read = in.read(buffer, length, buffer.length - length);
      if (read < 0)
      {
        return length == 0 ? -1 : length;
      }

      if (read == 0)
      {
        int value = in.read();
        if (value < 0)
        {
          return length == 0 ? -1 : length;
        }

        buffer[length++] = (byte)value;
      }
      else
      {
        length += read;
      }
    }

    return length;
  }

  private static void prepareMetadataTest(String[] args) throws Exception
  {
    if (args.length != 2)
    {
      throw new IllegalArgumentException("prepare-metadata-test requires the workspace root");
    }

    Path workspace = Path.of(args[1]).toAbsolutePath().normalize();
    Path testRoot = Files.createTempDirectory(workspace, "cdo-prepare-metadata-test-").toRealPath();

    try
    {
      Path iBuild = Files.createDirectory(testRoot.resolve("i-build"));
      writeBuildInfoFixture(iBuild, "I", "I20261110-1356");
      Map<String, String> iBuildMetadata = PrepareCommand.readBuildMetadata(DropLocation.parse(iBuild.toString()));
      if (!"I".equals(iBuildMetadata.get("cdo.build.type")) || !"4.x".equals(iBuildMetadata.get("cdo.build.stream"))
          || iBuildMetadata.containsKey("cdo.project.release"))
      {
        throw new AssertionError("I-build metadata must retain its stream and omit project-release metadata");
      }

      Map<String, String> iBuildOverride = PrepareCommand.readBuildMetadata(DropLocation.parse(iBuild.toString()), "4.37");
      if (!"4.37".equals(iBuildOverride.get("cdo.project.release")) || !"OVERRIDE".equals(iBuildOverride.get("cdo.project.release.source")))
      {
        throw new AssertionError("I-build metadata must accept an explicit project-release override");
      }

      Path sBuild = Files.createDirectory(testRoot.resolve("s-build"));
      writeBuildInfoFixture(sBuild, "S", "S20261110-1357");
      Map<String, String> sBuildOverride = PrepareCommand.readBuildMetadata(DropLocation.parse(sBuild.toString()), "4.37");
      if (!"4.37".equals(sBuildOverride.get("cdo.project.release")) || !"OVERRIDE".equals(sBuildOverride.get("cdo.project.release.source")))
      {
        throw new AssertionError("S-build metadata must accept an explicit project-release override");
      }

      Path rBuild = Files.createDirectory(testRoot.resolve("r-build"));
      writeBuildInfoFixture(rBuild, "R", "R20261110-1356");
      Files.writeString(rBuild.resolve("web.properties"), "web.label=4.36\n", StandardCharsets.UTF_8);
      Map<String, String> rBuildMetadata = PrepareCommand.readBuildMetadata(DropLocation.parse(rBuild.toString()));
      if (!"R".equals(rBuildMetadata.get("cdo.build.type")) || !"4.36".equals(rBuildMetadata.get("cdo.project.release")))
      {
        throw new AssertionError("R-build metadata must retain the validated project release");
      }

      Map<String, String> matchingOverride = PrepareCommand.readBuildMetadata(DropLocation.parse(rBuild.toString()), "4.36.0");
      if (!"DROP".equals(matchingOverride.get("cdo.project.release.source")))
      {
        throw new AssertionError("A matching override must retain DROP as the release source");
      }

      try
      {
        PrepareCommand.readBuildMetadata(DropLocation.parse(rBuild.toString()), "4.37");
        throw new AssertionError("Conflicting release override was accepted");
      }
      catch (IllegalArgumentException expected)
      {
        if (!expected.getMessage().contains("conflicts"))
        {
          throw expected;
        }
      }

      Path invisibleBuild = Files.createDirectory(testRoot.resolve("invisible-r-build"));
      writeBuildInfoFixture(invisibleBuild, "R", "R20261110-1358");
      Map<String, String> overrideMetadata = PrepareCommand.readBuildMetadata(DropLocation.parse(invisibleBuild.toString()), "4.37");
      if (!"4.37".equals(overrideMetadata.get("cdo.project.release")) || !"OVERRIDE".equals(overrideMetadata.get("cdo.project.release.source")))
      {
        throw new AssertionError("Invisible R-drop must use the explicit release override");
      }

      Map<String, String> invisibleBuildMetadata = PrepareCommand.readBuildMetadata(DropLocation.parse(invisibleBuild.toString()));
      if (invisibleBuildMetadata.containsKey("cdo.project.release") || invisibleBuildMetadata.containsKey("cdo.project.release.source"))
      {
        throw new AssertionError("A drop without web.label must leave project-release metadata undefined");
      }

      PublishPolicyCommand.enforce("R", "VALIDATE", invisibleBuildMetadata.get("cdo.project.release"), invisibleBuildMetadata.get("cdo.build.stream"));
      expectPackageReleaseFailure("R", invisibleBuildMetadata.get("cdo.project.release"), invisibleBuildMetadata.get("cdo.build.stream"));

      for (String buildType : new String[] { "R", "S", "I" })
      {
        Path matrixBuild = Files.createDirectory(testRoot.resolve("release-matrix-" + buildType));
        writeBuildInfoFixture(matrixBuild, buildType, buildType + "20261110-1358");
        DropLocation matrixDrop = DropLocation.parse(matrixBuild.toString());

        Map<String, String> absentRelease = PrepareCommand.readBuildMetadata(matrixDrop);
        PublishPolicyCommand.enforce(buildType, "VALIDATE", absentRelease.get("cdo.project.release"), absentRelease.get("cdo.build.stream"));
        expectPackageReleaseFailure(buildType, absentRelease.get("cdo.project.release"), absentRelease.get("cdo.build.stream"));

        Files.writeString(matrixBuild.resolve("web.properties"), "web.label=4.37\n", StandardCharsets.UTF_8);
        Map<String, String> dropRelease = PrepareCommand.readBuildMetadata(matrixDrop);
        assertProjectRelease(dropRelease, "4.37", "DROP", buildType);
        PublishPolicyCommand.enforce(buildType, "PACKAGE", dropRelease.get("cdo.project.release"), dropRelease.get("cdo.build.stream"));

        Map<String, String> matrixMatchingOverride = PrepareCommand.readBuildMetadata(matrixDrop, "4.37.0");
        assertProjectRelease(matrixMatchingOverride, "4.37", "DROP", buildType);
        PublishPolicyCommand.enforce(buildType, "PACKAGE", matrixMatchingOverride.get("cdo.project.release"), matrixMatchingOverride.get("cdo.build.stream"));

        expectMetadataFailure(matrixDrop, "4.38", "conflicts");

        Files.writeString(matrixBuild.resolve("web.properties"), "web.label=invalid\n", StandardCharsets.UTF_8);
        expectMetadataFailure(matrixDrop, "", "malformed numeric web.label");
        expectMetadataFailure(matrixDrop, "4.37", "malformed numeric web.label");

        Files.writeString(matrixBuild.resolve("web.properties"), "web.label=5.0\n", StandardCharsets.UTF_8);
        expectMetadataFailure(matrixDrop, "", "does not match build-info.xml stream");

        Files.delete(matrixBuild.resolve("web.properties"));
        Map<String, String> overrideRelease = PrepareCommand.readBuildMetadata(matrixDrop, "4.37");
        assertProjectRelease(overrideRelease, "4.37", "OVERRIDE", buildType);
        PublishPolicyCommand.enforce(buildType, "PACKAGE", overrideRelease.get("cdo.project.release"), overrideRelease.get("cdo.build.stream"));
      }

      if (!"S".equals(PrepareCommand.readBuildMetadata(DropLocation.parse(sBuild.toString())).get("cdo.build.type")))
      {
        throw new AssertionError("S-build must be accepted for technical validation");
      }

      Path unsupportedBuild = Files.createDirectory(testRoot.resolve("unsupported-build"));
      writeBuildInfoFixture(unsupportedBuild, "X", "X20261110-1360");
      try
      {
        PrepareCommand.readBuildMetadata(DropLocation.parse(unsupportedBuild.toString()));
        throw new AssertionError("Unsupported build type was accepted");
      }
      catch (IllegalArgumentException expected)
      {
        if (!expected.getMessage().contains("Unsupported CDO build type"))
        {
          throw expected;
        }
      }

      try
      {
        PrepareCommand.readBuildMetadata(DropLocation.parse(sBuild.toString()), "4.37-SNAPSHOT");
        throw new AssertionError("Malformed release override was accepted");
      }
      catch (IllegalArgumentException expected)
      {
        if (!expected.getMessage().contains("numeric major.minor"))
        {
          throw expected;
        }
      }

      Path invalidRBuild = Files.createDirectory(testRoot.resolve("invalid-r-build"));
      writeBuildInfoFixture(invalidRBuild, "R", "R20261110-1357");
      Files.writeString(invalidRBuild.resolve("web.properties"), "web.label=4.x\n", StandardCharsets.UTF_8);
      try
      {
        PrepareCommand.readBuildMetadata(DropLocation.parse(invalidRBuild.toString()));
        throw new AssertionError("R-build metadata accepted a non-numeric web.label");
      }
      catch (IllegalArgumentException expected)
      {
        if (!expected.getMessage().contains("numeric web.label"))
        {
          throw expected;
        }
      }

      System.out.println("Prepare metadata tests passed for R/S/I types, invisible-release overrides, normalization, and strict validation.");
    }
    finally
    {
      delete(testRoot.toFile());
    }
  }

  private static void assertProjectRelease(Map<String, String> metadata, String release, String source, String buildType)
  {
    if (!buildType.equals(metadata.get("cdo.build.type")) || !release.equals(metadata.get("cdo.project.release"))
        || !source.equals(metadata.get("cdo.project.release.source")))
    {
      throw new AssertionError("Unexpected project-release metadata for " + buildType + ": " + metadata);
    }
  }

  private static void expectMetadataFailure(DropLocation drop, String override, String diagnostic) throws Exception
  {
    try
    {
      PrepareCommand.readBuildMetadata(drop, override);
      throw new AssertionError("Expected project-release metadata failure containing " + diagnostic);
    }
    catch (IllegalArgumentException expected)
    {
      if (!expected.getMessage().contains(diagnostic))
      {
        throw expected;
      }
    }
  }

  private static void expectPackageReleaseFailure(String buildType, String projectRelease, String buildStream)
  {
    try
    {
      PublishPolicyCommand.enforce(buildType, "PACKAGE", projectRelease, buildStream);
      throw new AssertionError("PACKAGE accepted missing project-release metadata for build type " + buildType);
    }
    catch (IllegalArgumentException expected)
    {
      if (!expected.getMessage().contains("OVERRIDE_VERSION"))
      {
        throw expected;
      }
    }
  }

  private static void writeBuildInfoFixture(Path directory, String type, String qualifier) throws IOException
  {
    String buildInfo = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><build qualifier=\"" + qualifier
        + "\" revision=\"0123456789abcdef\" branch=\"master\" eclipse=\"4.42\" emf=\"2.48\" hudson=\"https://ci.eclipse.org/cdo/\""
        + " job=\"emf-cdo-build/master\" number=\"716\" stream=\"4.x\" timestamp=\"20261110-1356\" train=\"2026-12\" trigger=\"test\" type=\"" + type + "\"/>";
    Files.writeString(directory.resolve("build-info.xml"), buildInfo, StandardCharsets.UTF_8);
  }

  private static void streamComparisonTest(String[] args) throws Exception
  {
    if (args.length != 1)
    {
      throw new IllegalArgumentException("stream-test takes no arguments");
    }

    byte[] content = new byte[20_017];

    for (int i = 0; i < content.length; i++)
    {
      content[i] = (byte)(i * 31);
    }

    InputStream fiveByteReads = new ByteArrayInputStream(content)
    {
      @Override
      public synchronized int read(byte[] bytes, int offset, int length)
      {
        return super.read(bytes, offset, Math.min(length, 5));
      }
    };

    InputStream threeByteReads = new ByteArrayInputStream(content)
    {
      @Override
      public synchronized int read(byte[] bytes, int offset, int length)
      {
        return super.read(bytes, offset, Math.min(length, 3));
      }
    };

    if (!sameBytes(fiveByteReads, threeByteReads))
    {
      throw new AssertionError("Equal byte sequences with different read chunk boundaries did not compare equal");
    }

    byte[] modified = content.clone();
    modified[8193]++;

    if (sameBytes(new ByteArrayInputStream(content), new ByteArrayInputStream(modified))
        || sameBytes(new ByteArrayInputStream(content), new ByteArrayInputStream(Arrays.copyOf(content, content.length - 1))))
    {
      throw new AssertionError("Different byte sequences compared equal");
    }

    System.out.println("Stream comparison tests passed for differing chunk sizes, changed bytes, and truncated input.");
  }

  private static boolean isCdoPublicationGroup(String group)
  {
    return "org.eclipse.cdo".equals(group) || group != null && group.startsWith("org.eclipse.cdo.");
  }

  private static String relativePath(File root, File file) throws IOException
  {
    return root.toPath().toAbsolutePath().normalize().relativize(file.toPath().toAbsolutePath().normalize()).toString().replace(File.separatorChar, '/');
  }

  private static boolean isMavenMetadataPath(String path)
  {
    return path.matches("org/eclipse/cdo/[^/]+/maven-metadata\\.xml(?:\\.(?:md5|sha1|sha256|sha512))?");
  }

  private static void securePath(Path path, boolean directory) throws IOException
  {
    try
    {
      Files.setPosixFilePermissions(path, PosixFilePermissions.fromString(directory ? "rwx------" : "rw-------"));
    }
    catch (UnsupportedOperationException e)
    {
      AclFileAttributeView view = Files.getFileAttributeView(path, AclFileAttributeView.class);
      if (view == null)
      {
        throw new IOException("Filesystem does not support owner-only temporary GnuPG permissions: " + path, e);
      }

      UserPrincipal owner = Files.getOwner(path);
      AclEntry.Builder entry = AclEntry.newBuilder().setType(AclEntryType.ALLOW).setPrincipal(owner).setPermissions(EnumSet.allOf(AclEntryPermission.class));

      if (directory)
      {
        entry.setFlags(AclEntryFlag.FILE_INHERIT, AclEntryFlag.DIRECTORY_INHERIT);
      }

      view.setAcl(List.of(entry.build()));
    }
  }

  private static void signatureTest(String[] args) throws Exception
  {
    if (args.length != 2)
    {
      throw new IllegalArgumentException("signature-test requires the GnuPG executable path");
    }

    String gpgExecutable = args[1];
    String workspace = System.getenv("CDO_WORKSPACE_ROOT");

    Path work = workspace == null || workspace.isBlank() ? Files.createTempDirectory("g-") : Files.createTempDirectory(Paths.get(workspace), "g-");
    Path firstHome = work.resolve("a");
    Path secondHome = work.resolve("b");

    securePath(work, true);

    try
    {
      Files.createDirectory(firstHome);
      Files.createDirectory(secondHome);

      securePath(firstHome, true);
      securePath(secondHome, true);
      validateGpgSocketPaths(gpgExecutable, firstHome);
      validateGpgSocketPaths(gpgExecutable, secondHome);

      gpg(gpgExecutable, firstHome, "--pinentry-mode", "loopback", "--passphrase", "", "--quick-generate-key", "CDO temporary signing key", "rsa2048", "cert",
          "1d");
      gpg(gpgExecutable, secondHome, "--pinentry-mode", "loopback", "--passphrase", "", "--quick-generate-key", "CDO wrong test key", "rsa2048", "cert", "1d");

      String firstFingerprint = primaryFingerprints(gpgExecutable, firstHome).iterator().next();
      String secondFingerprint = primaryFingerprints(gpgExecutable, secondHome).iterator().next();
      gpg(gpgExecutable, firstHome, "--pinentry-mode", "loopback", "--passphrase", "", "--quick-add-key", firstFingerprint, "rsa2048", "sign", "1d");
      gpg(gpgExecutable, secondHome, "--pinentry-mode", "loopback", "--passphrase", "", "--quick-add-key", secondFingerprint, "rsa2048", "sign", "1d");

      String firstSigningFingerprint = findSigningSubkeyFingerprint(
          gpg(gpgExecutable, firstHome, "--with-colons", "--fixed-list-mode", "--fingerprint", "--fingerprint", "--list-secret-keys"), firstFingerprint);
      String secondSigningFingerprint = findSigningSubkeyFingerprint(
          gpg(gpgExecutable, secondHome, "--with-colons", "--fixed-list-mode", "--fingerprint", "--fingerprint", "--list-secret-keys"), secondFingerprint);

      File firstPublicKey = exportPublicKey(gpgExecutable, firstHome, firstFingerprint, work.resolve("first-public-key.gpg"));
      File secondPublicKey = exportPublicKey(gpgExecutable, secondHome, secondFingerprint, work.resolve("second-public-key.gpg"));

      File staging = work.resolve("staging").toFile();
      File signedRepository = work.resolve("signed-repository").toFile();
      File metadata = work.resolve("metadata.properties").toFile();
      File summary = work.resolve("summary.properties").toFile();
      File zip = work.resolve("deployment.zip").toFile();

      Files.createDirectories(staging.toPath());
      Files.createDirectories(signedRepository.toPath());
      Files.writeString(metadata.toPath(),
          "cdo.build.type=R\ncdo.build.stream=4.x\ncdo.project.release=4.37\ncdo.drop.id=R20990101-0000\ncdo.git.commit=deadbeef\n", StandardCharsets.UTF_8);
      Files.writeString(summary.toPath(), "publishing.mode=PACKAGE\n", StandardCharsets.UTF_8);

      createSignedFixture(gpgExecutable, firstHome, firstSigningFingerprint, staging, signedRepository);
      createDeploymentZip(staging, zip, "");

      String[] valid = packageArguments(staging, signedRepository, zip, metadata, firstFingerprint, firstPublicKey, gpgExecutable, summary);
      verifyPackage(valid);

      File bomPom = new File(staging, "org/eclipse/cdo/" + RELEASE_BOM + "/4.37.0/" + RELEASE_BOM + "-4.37.0.pom");
      File deployedBomPom = new File(signedRepository, relativePath(staging, bomPom).replace('/', File.separatorChar));
      byte[] originalBom = Files.readAllBytes(bomPom.toPath());
      Files.writeString(bomPom.toPath(), new String(originalBom, StandardCharsets.UTF_8).replace("<version>1.0.0</version>", "<version>1.0.1</version>"),
          StandardCharsets.UTF_8);
      Files.write(deployedBomPom.toPath(), Files.readAllBytes(bomPom.toPath()));
      ChecksumsCommand.writeChecksums(bomPom);
      createDeploymentZip(staging, zip, "");
      expectPackageFailure(valid, "Generated BOM member mismatch", "tampered release BOM contents");
      Files.write(bomPom.toPath(), originalBom);
      Files.write(deployedBomPom.toPath(), originalBom);
      ChecksumsCommand.writeChecksums(bomPom);

      File bomSignature = new File(bomPom.getPath() + ".asc");
      File deployedBomSignature = new File(deployedBomPom.getPath() + ".asc");
      byte[] originalBomSignature = Files.readAllBytes(bomSignature.toPath());
      byte[] corruptedBomSignature = originalBomSignature.clone();
      int bomSignatureBody = new String(corruptedBomSignature, StandardCharsets.US_ASCII).indexOf("\n\n") + 2;
      corruptedBomSignature[bomSignatureBody] = corruptedBomSignature[bomSignatureBody] == 'A' ? (byte)'B' : (byte)'A';
      Files.write(bomSignature.toPath(), corruptedBomSignature);
      Files.write(deployedBomSignature.toPath(), corruptedBomSignature);
      createDeploymentZip(staging, zip, "");
      expectPackageFailure(valid, "GnuPG operation failed", "tampered release BOM signature");
      Files.write(bomSignature.toPath(), originalBomSignature);
      Files.write(deployedBomSignature.toPath(), originalBomSignature);

      File[] bomChecksums = new File[ChecksumsCommand.ALGORITHMS.size()];
      int checksumIndex = 0;
      for (String algorithm : ChecksumsCommand.ALGORITHMS.keySet())
      {
        bomChecksums[checksumIndex++] = new File(bomPom.getPath() + "." + algorithm);
      }

      byte[][] originalBomChecksums = new byte[bomChecksums.length][];
      byte[] savedBom = Files.readAllBytes(bomPom.toPath());
      byte[] savedBomSignature = Files.readAllBytes(bomSignature.toPath());
      Files.delete(bomPom.toPath());
      Files.delete(bomSignature.toPath());
      for (int i = 0; i < bomChecksums.length; i++)
      {
        originalBomChecksums[i] = Files.readAllBytes(bomChecksums[i].toPath());
        Files.delete(bomChecksums[i].toPath());
      }

      createDeploymentZip(staging, zip, "");
      expectPackageFailure(valid, "PACKAGE staging must contain the project-release BOM", "missing project-release BOM");
      for (int i = 0; i < bomChecksums.length; i++)
      {
        Files.write(bomChecksums[i].toPath(), originalBomChecksums[i]);
      }

      Files.write(bomPom.toPath(), savedBom);
      Files.write(bomSignature.toPath(), savedBomSignature);

      File artifact = new File(staging, "org/eclipse/cdo/" + BUNDLES.get(0) + "/1.0.0/" + BUNDLES.get(0) + "-1.0.0.jar");
      File deployedArtifact = new File(signedRepository, "org/eclipse/cdo/" + BUNDLES.get(0) + "/1.0.0/" + BUNDLES.get(0) + "-1.0.0.jar");
      byte[] originalArtifact = Files.readAllBytes(artifact.toPath());

      Files.write(artifact.toPath(), Arrays.copyOf(originalArtifact, originalArtifact.length + 1));
      Files.write(deployedArtifact.toPath(), Files.readAllBytes(artifact.toPath()));

      ChecksumsCommand.writeChecksums(artifact);

      createDeploymentZip(staging, zip, "");
      expectPackageFailure(packageArguments(staging, signedRepository, zip, metadata, firstFingerprint, firstPublicKey, gpgExecutable, summary),
          "GnuPG operation failed", "modified artifact bytes");

      Files.write(artifact.toPath(), originalArtifact);
      Files.write(deployedArtifact.toPath(), originalArtifact);
      ChecksumsCommand.writeChecksums(artifact);

      File signature = new File(artifact.getPath() + ".asc");
      File deployedSignature = new File(deployedArtifact.getPath() + ".asc");

      byte[] originalSignature = Files.readAllBytes(signature.toPath());
      byte[] corruptedSignature = originalSignature.clone();

      int body = new String(corruptedSignature, StandardCharsets.US_ASCII).indexOf("\n\n") + 2;
      corruptedSignature[body] = corruptedSignature[body] == 'A' ? (byte)'B' : (byte)'A';

      Files.write(signature.toPath(), corruptedSignature);
      Files.write(deployedSignature.toPath(), corruptedSignature);

      createDeploymentZip(staging, zip, "");
      expectPackageFailure(packageArguments(staging, signedRepository, zip, metadata, firstFingerprint, firstPublicKey, gpgExecutable, summary),
          "GnuPG operation failed", "corrupted signature");

      Files.write(signature.toPath(), originalSignature);
      Files.write(deployedSignature.toPath(), originalSignature);

      createDeploymentZip(staging, zip, "");
      expectPackageFailure(packageArguments(staging, signedRepository, zip, metadata, secondFingerprint, secondPublicKey, gpgExecutable, summary),
          "GnuPG operation failed", "signature from a wrong key");
      expectPackageFailure(packageArguments(staging, signedRepository, zip, metadata, secondFingerprint, firstPublicKey, gpgExecutable, summary),
          "unexpected primary key", "wrong expected key fingerprint");
      expectPackageFailure(packageArguments(staging, signedRepository, zip, metadata, "invalid-fingerprint", firstPublicKey, gpgExecutable, summary),
          "full 40- or 64-character hexadecimal fingerprint", "malformed expected fingerprint");
      expectPackageFailure(
          packageArguments(staging, signedRepository, zip, metadata, firstFingerprint, work.resolve("missing-key.gpg").toFile(), gpgExecutable, summary),
          "GnuPG public verification key", "missing public key file");

      File checksum = new File(artifact.getPath() + ".sha256");
      byte[] correctChecksum = Files.readAllBytes(checksum.toPath());
      Files.writeString(checksum.toPath(), "0".repeat(64), StandardCharsets.US_ASCII);

      createDeploymentZip(staging, zip, "");
      expectPackageFailure(packageArguments(staging, signedRepository, zip, metadata, firstFingerprint, firstPublicKey, gpgExecutable, summary),
          "Missing or invalid checksum", "mismatched checksum");
      Files.write(checksum.toPath(), correctChecksum);

      File unexpectedSignature = new File(artifact.getPath() + ".unexpected.asc");
      Files.writeString(unexpectedSignature.toPath(), "-----BEGIN PGP SIGNATURE-----\nextra\n", StandardCharsets.US_ASCII);
      createDeploymentZip(staging, zip, "");
      expectPackageFailure(packageArguments(staging, signedRepository, zip, metadata, firstFingerprint, firstPublicKey, gpgExecutable, summary),
          "Unexpected file in publication staging", "unexpected signature");
      Files.delete(unexpectedSignature.toPath());

      File missingSignature = new File(signature.getPath());
      Files.delete(missingSignature.toPath());
      createDeploymentZip(staging, zip, "");
      expectPackageFailure(packageArguments(staging, signedRepository, zip, metadata, firstFingerprint, firstPublicKey, gpgExecutable, summary),
          "PACKAGE requires a non-empty signature", "missing signature");
      Files.write(missingSignature.toPath(), originalSignature);

      Files.delete(artifact.toPath());
      createDeploymentZip(staging, zip, "");
      expectPackageFailure(packageArguments(staging, signedRepository, zip, metadata, firstFingerprint, firstPublicKey, gpgExecutable, summary),
          "Missing or empty staged Maven artifact", "missing Maven artifact");
      Files.write(artifact.toPath(), originalArtifact);
      ChecksumsCommand.writeChecksums(artifact);

      createDeploymentZip(staging, zip, "unexpected-wrapper/");
      expectPackageFailure(packageArguments(staging, signedRepository, zip, metadata, firstFingerprint, firstPublicKey, gpgExecutable, summary),
          "inventory mismatch", "enclosing ZIP directory");

      createDeploymentZip(staging, zip, "");
      verifyPackage(packageArguments(staging, signedRepository, zip, metadata, firstFingerprint, firstPublicKey, gpgExecutable, summary));
      bom436SignatureTest(gpgExecutable, firstHome, firstFingerprint, firstPublicKey, secondHome, secondSigningFingerprint, work);
      System.out.println(
          "Disposable GnuPG signing tests passed: bundle package protections and POM-only BOM inventory, checksums, identity, signature, and ZIP validation.");
    }
    finally
    {
      try
      {
        if (Files.isDirectory(firstHome))
        {
          stopGpgAgent(gpgExecutable, firstHome);
        }
      }
      finally
      {
        try
        {
          if (Files.isDirectory(secondHome))
          {
            stopGpgAgent(gpgExecutable, secondHome);
          }
        }
        finally
        {
          delete(work.toFile());
        }
      }
    }
  }

  private static File exportPublicKey(String gpg, Path home, String fingerprint, Path destination) throws Exception
  {
    gpg(gpg, home, "--output", destination.toString(), "--export", fingerprint);
    securePath(destination, false);
    return destination.toFile();
  }

  private static void bom436SignatureTest(String gpg, Path firstHome, String firstPrimary, File firstPublicKey, Path secondHome, String secondSigning,
      Path work) throws Exception
  {
    Path root = work.resolve("bom-436-test");
    Path staging = root.resolve("staging");
    Path signedRepository = root.resolve("signed-repository");
    Path bomRepository = root.resolve("bom-repository");
    Path metadata = root.resolve("metadata.properties");
    Path publishing = root.resolve("publishing.properties");
    Path drop = root.resolve("drop");
    Path zip = root.resolve("bom-deployment.zip");
    Path summary = root.resolve("summary.properties");
    Files.createDirectories(root);
    Files.createDirectories(signedRepository);
    Files.createDirectories(drop);
    Files.writeString(drop.resolve("web.properties"), "web.label=4.36\n", StandardCharsets.UTF_8);
    Files.writeString(drop.resolve("build-info.xml"),
        "<build qualifier=\"R20260901-1311\" revision=\"0123456789abcdef0123456789abcdef01234567\" branch=\"main\""
            + " eclipse=\"2026-09\" emf=\"2.42\" hudson=\"https://ci.eclipse.org/cdo/\" job=\"cdo\" number=\"1\" stream=\"4.36\""
            + " timestamp=\"2026-09-01T13:11:00Z\" train=\"2026-09\" trigger=\"signature-test\" type=\"R\"/>",
        StandardCharsets.UTF_8);
    Map<String, String> buildMetadata = new LinkedHashMap<>();
    buildMetadata.put("cdo.build.type", "R");
    buildMetadata.put("cdo.project.release", "4.36");
    buildMetadata.put("cdo.drop.id", "R20260901-1311");
    buildMetadata.put("cdo.git.commit", "0123456789abcdef0123456789abcdef01234567");
    writeProperties(metadata, buildMetadata);
    String publishingText = "project.url=https://eclipse.dev/cdo/\norganization.name=Eclipse Foundation\norganization.url=https://www.eclipse.org\n"
        + "license.name=EPL-2.0\nlicense.url=https://www.eclipse.org/legal/epl-2.0/\nlicense.distribution=repo\n"
        + "developers.url=https://github.com/eclipse-cdo/cdo/graphs/contributors\nissue.system=GitHub\n"
        + "issue.url=https://github.com/eclipse-cdo/cdo/issues\nscm.connection=scm:git:https://github.com/eclipse-cdo/cdo.git\n"
        + "scm.developerConnection=scm:git:https://github.com/eclipse-cdo/cdo.git\n";
    Files.writeString(publishing, publishingText, StandardCharsets.UTF_8);
    Map<String, String> publishingMetadata = readProperties(publishing.toFile());
    Map<String, String> versions = new LinkedHashMap<>();
    String[] publishedVersions = { "3.30.0", "4.22.0", "4.5.0", "4.14.0", "4.5.0", "4.6.0", "4.28.0", "4.30.0", "4.9.0", "4.26.0", "4.9.0", "4.15.0" };
    for (int i = 0; i < BUNDLES_436.size(); i++)
    {
      versions.put(BUNDLES_436.get(i), publishedVersions[i]);
    }

    StringBuilder index = new StringBuilder("<index>\n");
    for (int i = 0; i < BUNDLES_436.size(); i++)
    {
      index.append("<element name=\"").append(BUNDLES_436.get(i)).append("\" type=\"osgi.bundle\" version=\"").append(publishedVersions[i])
          .append(".v20260901-1311\"/>\n");
    }

    index.append("</index>\n");
    Files.writeString(drop.resolve("index.xml"), index, StandardCharsets.UTF_8);
    File bomPom = BomCommand.generate(bomRepository.toFile(), buildMetadata, publishingMetadata, "4.36.0", versions, BUNDLES_436);
    Bom436Command
        .stage(new String[] { "stage-bom-436", bomRepository.toString(), metadata.toString(), publishing.toString(), drop.toString(), staging.toString() });

    String relativePom = Bom436Command.bomRelativePom();
    File stagedPom = new File(staging.toFile(), relativePom.replace('/', File.separatorChar));
    File stagedSignature = new File(stagedPom.getPath() + ".asc");
    File deployedPom = new File(signedRepository.toFile(), relativePom.replace('/', File.separatorChar));
    File deployedSignature = new File(deployedPom.getPath() + ".asc");
    Files.createDirectories(deployedPom.toPath().getParent());
    Files.copy(bomPom.toPath(), deployedPom.toPath());
    gpg(gpg, firstHome, "--armor", "--local-user",
        findSigningSubkeyFingerprint(gpg(gpg, firstHome, "--with-colons", "--fixed-list-mode", "--fingerprint", "--fingerprint", "--list-secret-keys"),
            firstPrimary),
        "--output", stagedSignature.getAbsolutePath(), "--detach-sign", stagedPom.getAbsolutePath());
    Files.copy(stagedSignature.toPath(), deployedSignature.toPath());
    createDeploymentZip(staging.toFile(), zip.toFile(), "");
    String[] args = bomPackageArguments(staging.toFile(), signedRepository.toFile(), zip.toFile(), metadata.toFile(), publishing.toFile(), drop.toFile(),
        firstPrimary, firstPublicKey, gpg, summary.toFile());
    verifyBomPackage(args);

    byte[] signatureBytes = Files.readAllBytes(stagedSignature.toPath());
    Files.delete(stagedSignature.toPath());
    createDeploymentZip(staging.toFile(), zip.toFile(), "");
    expectBomPackageFailure(args, "Missing, malformed, or non-armored BOM signature", "missing BOM signature");
    Files.write(stagedSignature.toPath(), signatureBytes);

    File sha1 = new File(stagedPom.getPath() + ".sha1");
    byte[] sha1Bytes = Files.readAllBytes(sha1.toPath());
    Files.delete(sha1.toPath());
    createDeploymentZip(staging.toFile(), zip.toFile(), "");
    expectBomPackageFailure(args, "Missing or invalid BOM checksum", "missing BOM checksum");
    Files.write(sha1.toPath(), sha1Bytes);

    byte[] corruptedSignature = signatureBytes.clone();
    int body = new String(corruptedSignature, StandardCharsets.US_ASCII).indexOf("\n\n") + 2;
    corruptedSignature[body] = corruptedSignature[body] == 'A' ? (byte)'B' : (byte)'A';
    Files.write(stagedSignature.toPath(), corruptedSignature);
    Files.write(deployedSignature.toPath(), corruptedSignature);
    createDeploymentZip(staging.toFile(), zip.toFile(), "");
    expectBomPackageFailure(args, "GnuPG operation failed", "corrupted BOM signature");
    Files.write(stagedSignature.toPath(), signatureBytes);
    Files.write(deployedSignature.toPath(), signatureBytes);

    File checksum = new File(stagedPom.getPath() + ".sha256");
    byte[] checksumBytes = Files.readAllBytes(checksum.toPath());
    Files.writeString(checksum.toPath(), "0".repeat(64), StandardCharsets.US_ASCII);
    createDeploymentZip(staging.toFile(), zip.toFile(), "");
    expectBomPackageFailure(args, "Missing or invalid BOM checksum", "invalid BOM checksum");
    Files.write(checksum.toPath(), checksumBytes);

    File unexpected = new File(staging.toFile(), relativePom.replace(".pom", ".jar").replace('/', File.separatorChar));
    Files.writeString(unexpected.toPath(), "no binaries belong in a BOM package", StandardCharsets.UTF_8);
    createDeploymentZip(staging.toFile(), zip.toFile(), "");
    expectBomPackageFailure(args, "inventory mismatch", "unexpected BOM deployment file");
    Files.delete(unexpected.toPath());

    gpg(gpg, secondHome, "--armor", "--local-user", secondSigning, "--yes", "--output", stagedSignature.getAbsolutePath(), "--detach-sign",
        stagedPom.getAbsolutePath());
    Files.copy(stagedSignature.toPath(), deployedSignature.toPath(), StandardCopyOption.REPLACE_EXISTING);
    createDeploymentZip(staging.toFile(), zip.toFile(), "");
    expectBomPackageFailure(args, "GnuPG operation failed", "BOM signature from an untrusted signing identity");
    gpg(gpg, firstHome, "--armor", "--local-user",
        findSigningSubkeyFingerprint(gpg(gpg, firstHome, "--with-colons", "--fixed-list-mode", "--fingerprint", "--fingerprint", "--list-secret-keys"),
            firstPrimary),
        "--yes", "--output", stagedSignature.getAbsolutePath(), "--detach-sign", stagedPom.getAbsolutePath());
    Files.copy(stagedSignature.toPath(), deployedSignature.toPath(), StandardCopyOption.REPLACE_EXISTING);
    createDeploymentZip(staging.toFile(), zip.toFile(), "");
    expectBomPackageFailure(bomPackageArguments(staging.toFile(), signedRepository.toFile(), zip.toFile(), metadata.toFile(), publishing.toFile(),
        drop.toFile(), "F".repeat(firstPrimary.length()), firstPublicKey, gpg, summary.toFile()), "unexpected primary key",
        "incorrect BOM verification identity");
    verifyBomPackage(args);
  }

  private static String[] bomPackageArguments(File staging, File signedRepository, File zip, File metadata, File publishing, File drop, String fingerprint,
      File publicKey, String gpg, File summary)
  {
    return new String[] { "verify-bom-package", staging.getPath(), signedRepository.getPath(), zip.getPath(), metadata.getPath(), publishing.getPath(),
        drop.getPath(), fingerprint, publicKey.getPath(), gpg, summary.getPath() };
  }

  private static void expectBomPackageFailure(String[] args, String message, String scenario) throws Exception
  {
    try
    {
      verifyBomPackage(args);
    }
    catch (Exception e)
    {
      if (e.getMessage() != null && e.getMessage().contains(message))
      {
        return;
      }

      throw new AssertionError("Unexpected failure for " + scenario + ": " + e.getMessage(), e);
    }

    throw new AssertionError("Expected failure for " + scenario);
  }

  private static String[] packageArguments(File staging, File signedRepository, File zip, File metadata, String fingerprint, File publicKey, String gpg,
      File summary)
  {
    return new String[] { "verify-package", staging.getPath(), signedRepository.getPath(), zip.getPath(), metadata.getPath(), fingerprint, publicKey.getPath(),
        gpg, summary.getPath() };
  }

  private static void expectPackageFailure(String[] args, String message, String scenario) throws Exception
  {
    try
    {
      verifyPackage(args);
    }
    catch (Exception e)
    {
      if (e.getMessage() != null && e.getMessage().contains(message))
      {
        return;
      }

      throw new AssertionError("Unexpected failure for " + scenario + ": " + e.getMessage(), e);
    }

    throw new AssertionError("Expected failure for " + scenario);
  }

  private static void createSignedFixture(String gpg, Path keyHome, String fingerprint, File staging, File signedRepository) throws Exception
  {
    for (String bundle : BUNDLES)
    {
      File versionDirectory = new File(staging, "org/eclipse/cdo/" + bundle + "/1.0.0");
      Files.createDirectories(versionDirectory.toPath());

      String base = bundle + "-1.0.0";
      File pom = new File(versionDirectory, base + ".pom");
      Files.writeString(pom.toPath(), "<project><groupId>org.eclipse.cdo</groupId><artifactId>" + bundle + "</artifactId><version>1.0.0</version></project>",
          StandardCharsets.UTF_8);

      List<File> artifacts = new ArrayList<>();
      artifacts.add(pom);

      for (String classifier : new String[] { "", "-sources", "-javadoc" })
      {
        File jar = new File(versionDirectory, base + classifier + ".jar");
        Files.writeString(jar.toPath(), "temporary signing test artifact " + bundle + classifier, StandardCharsets.UTF_8);
        artifacts.add(jar);
      }

      for (File artifact : artifacts)
      {
        ChecksumsCommand.writeChecksums(artifact);
        String relative = relativePath(staging, artifact);
        File deployed = new File(signedRepository, relative.replace('/', File.separatorChar));
        Files.createDirectories(deployed.getParentFile().toPath());
        Files.copy(artifact.toPath(), deployed.toPath());

        File signature = new File(artifact.getPath() + ".asc");
        File deployedSignature = new File(deployed.getPath() + ".asc");
        gpg(gpg, keyHome, "--armor", "--local-user", fingerprint, "--output", signature.getAbsolutePath(), "--detach-sign", artifact.getAbsolutePath());
        Files.copy(signature.toPath(), deployedSignature.toPath());
      }
    }

    createSignedReleaseBomFixture(staging);
    createSignedExamplesFixture(staging);
    for (String relative : new String[] { "org/eclipse/cdo/" + RELEASE_BOM + "/4.37.0/" + RELEASE_BOM + "-4.37.0.pom",
        "org/eclipse/cdo/" + EXAMPLES_DISTRIBUTION + "/1.0.0/" + EXAMPLES_DISTRIBUTION + "-1.0.0.pom",
        "org/eclipse/cdo/" + EXAMPLES_DISTRIBUTION + "/1.0.0/" + EXAMPLES_DISTRIBUTION + "-1.0.0-examples.zip" })
    {
      File artifact = new File(staging, relative.replace('/', File.separatorChar));
      File deployed = new File(signedRepository, relative.replace('/', File.separatorChar));
      Files.createDirectories(deployed.toPath().getParent());
      Files.copy(artifact.toPath(), deployed.toPath());
      File signature = new File(artifact.getPath() + ".asc");
      gpg(gpg, keyHome, "--armor", "--local-user", fingerprint, "--output", signature.getAbsolutePath(), "--detach-sign", artifact.getAbsolutePath());
      Files.copy(signature.toPath(), new File(deployed.getPath() + ".asc").toPath());
    }
  }

  private static void createSignedReleaseBomFixture(File staging) throws Exception
  {
    String version = "4.37.0";
    File directory = new File(staging, "org/eclipse/cdo/" + RELEASE_BOM + "/" + version);
    Files.createDirectories(directory.toPath());
    StringBuilder pom = new StringBuilder("<project><modelVersion>4.0.0</modelVersion><groupId>org.eclipse.cdo</groupId><artifactId>").append(RELEASE_BOM)
        .append("</artifactId><version>").append(version)
        .append("</version><packaging>pom</packaging><properties><cdo.project.release>4.37</cdo.project.release>").append(
            "<cdo.project.release.source>DROP</cdo.project.release.source><cdo.drop.id>R20990101-0000</cdo.drop.id></properties><dependencyManagement><dependencies>");
    for (String bundle : BUNDLES)
    {
      pom.append("<dependency><groupId>org.eclipse.cdo</groupId><artifactId>").append(bundle).append("</artifactId><version>1.0.0</version></dependency>");
    }

    pom.append("</dependencies></dependencyManagement></project>");
    File artifact = new File(directory, RELEASE_BOM + "-" + version + ".pom");
    Files.writeString(artifact.toPath(), pom.toString(), StandardCharsets.UTF_8);
    ChecksumsCommand.writeChecksums(artifact);
  }

  private static void createSignedExamplesFixture(File staging) throws Exception
  {
    File directory = new File(staging, "org/eclipse/cdo/" + EXAMPLES_DISTRIBUTION + "/" + EXAMPLES_DISTRIBUTION_VERSION);
    Files.createDirectories(directory.toPath());
    String sourceRevision = "abcdef0123456789abcdef0123456789abcdef01";
    String pom = "<project><modelVersion>4.0.0</modelVersion><groupId>org.eclipse.cdo</groupId><artifactId>" + EXAMPLES_DISTRIBUTION + "</artifactId>"
        + "<version>1.0.0</version><packaging>pom</packaging><name>CDO Examples</name><description>Examples ZIP</description><url>https://eclipse.dev/cdo/</url>"
        + "<organization><name>Eclipse Foundation</name><url>https://www.eclipse.org</url></organization><licenses><license><name>EPL-2.0</name>"
        + "<url>https://www.eclipse.org/legal/epl-2.0/</url><distribution>repo</distribution></license></licenses><developers><developer><name>CDO</name>"
        + "<url>https://github.com/eclipse-cdo/cdo/graphs/contributors</url></developer></developers><scm><connection>scm:git:https://github.com/eclipse-cdo/cdo.git</connection>"
        + "<developerConnection>scm:git:https://github.com/eclipse-cdo/cdo.git</developerConnection><url>https://eclipse.dev/cdo/</url><tag>" + sourceRevision
        + "</tag></scm><issueManagement><system>GitHub</system><url>https://github.com/eclipse-cdo/cdo/issues</url></issueManagement><properties>"
        + "<cdo.git.commit>deadbeef</cdo.git.commit><cdo.drop.id>R20990101-0000</cdo.drop.id><cdo.project.release>4.37</cdo.project.release>"
        + "<cdo.examples.source.git.commit>" + sourceRevision + "</cdo.examples.source.git.commit></properties></project>";
    File pomFile = new File(directory, EXAMPLES_DISTRIBUTION + "-" + EXAMPLES_DISTRIBUTION_VERSION + ".pom");
    File zipFile = new File(directory, EXAMPLES_DISTRIBUTION + "-" + EXAMPLES_DISTRIBUTION_VERSION + "-examples.zip");
    Files.writeString(pomFile.toPath(), pom, StandardCharsets.UTF_8);
    Files.writeString(zipFile.toPath(), "signed examples fixture", StandardCharsets.UTF_8);
    ChecksumsCommand.writeChecksums(pomFile);
    ChecksumsCommand.writeChecksums(zipFile);
  }

  private static void createDeploymentZip(File staging, File zip, String prefix) throws Exception
  {
    requireWorkspaceOutput(zip, "test deployment ZIP");
    File orgDirectory = new File(staging, "org");
    List<Path> paths;

    try (Stream<Path> stream = Files.walk(orgDirectory.toPath()))
    {
      paths = stream.sorted().collect(Collectors.toList());
    }

    try (ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zip))))
    {
      for (Path path : paths)
      {
        String relative = staging.toPath().relativize(path).toString().replace(File.separatorChar, '/');
        String entryName = prefix + relative + (Files.isDirectory(path) ? "/" : "");
        out.putNextEntry(new ZipEntry(entryName));

        if (Files.isRegularFile(path))
        {
          Files.copy(path, out);
        }

        out.closeEntry();
      }
    }
  }

  private static LinkedHashSet<String> extractRepositories(InputStream setup) throws Exception
  {
    LinkedHashSet<String> result = new LinkedHashSet<>();

    Document document = parse(setup, "tp-macro.setup");
    NodeList nodes = document.getElementsByTagNameNS("*", "repository");

    for (int i = 0; i < nodes.getLength(); i++)
    {
      Element e = (Element)nodes.item(i);

      String url = e.getAttribute("url").trim();
      if (url.isEmpty())
      {
        continue;
      }

      if (url.contains("${") || url.contains("@"))
      {
        throw new IllegalArgumentException("Unresolved Oomph repository URL: " + url);
      }

      result.add(url);
    }

    return result;
  }

  /**
   * This is a legacy correction for a bug in promoter.TPMacroSetup.insertDropRepository(BuildInfo).
   */
  private static String validationLocation(String url, String dropID, DropLocation drop)
  {
    // See promoter.TPMacroSetup.FIXED_TOKEN
    String FIXED_TOKEN = "https://download.eclipse.org/modeling/emf/cdo/updates";
    String resolved = FIXED_TOKEN.equals(url) ? "https://download.eclipse.org/modeling/emf/cdo/drops/" + dropID : url;
    String promotedDrop = "https://download.eclipse.org/modeling/emf/cdo/drops/" + dropID;
    return promotedDrop.equals(resolved) ? drop.location() : resolved;
  }

  private static String required(Element e, String name)
  {
    String value = e.getAttribute(name);
    if (value == null || value.isBlank())
    {
      throw new IllegalArgumentException("Missing build/@" + name);
    }

    return value;
  }

  private static File requireFile(File file, String label)
  {
    if (!file.isFile())
    {
      throw new IllegalArgumentException("Missing " + label + ": " + file);
    }

    return file;
  }

  private static Path requireSafeWorkspaceChild(String targetValue, String workspaceValue, String label) throws IOException
  {
    if (targetValue == null || targetValue.isBlank() || workspaceValue == null || workspaceValue.isBlank())
    {
      throw new IllegalArgumentException(label + " and workspace root must be non-empty paths");
    }

    Path suppliedRoot = Paths.get(workspaceValue);
    if (!suppliedRoot.isAbsolute() || !Files.isDirectory(suppliedRoot, LinkOption.NOFOLLOW_LINKS))
    {
      throw new IllegalArgumentException("Workspace root must be an existing absolute directory: " + workspaceValue);
    }

    Path root = suppliedRoot.normalize();
    if (root.getParent() == null || !root.equals(root.toRealPath()))
    {
      throw new IllegalArgumentException("Workspace root must be a non-root directory that does not resolve through a symlink: " + workspaceValue);
    }

    Path suppliedTarget = Paths.get(targetValue);
    if (!suppliedTarget.isAbsolute())
    {
      throw new IllegalArgumentException(label + " must be an absolute path: " + targetValue);
    }

    for (Path segment : suppliedTarget)
    {
      if ("..".equals(segment.toString()))
      {
        throw new IllegalArgumentException(label + " must not contain parent traversal: " + targetValue);
      }
    }

    Path target = suppliedTarget.normalize();
    if (target.equals(root) || !target.startsWith(root))
    {
      throw new IllegalArgumentException(label + " must be a strict descendant of the Jenkins workspace: " + targetValue);
    }

    Path current = root;

    for (Path segment : root.relativize(target))
    {
      current = current.resolve(segment);
      if (Files.isSymbolicLink(current))
      {
        throw new IllegalArgumentException(label + " must not pass through a symbolic link: " + current);
      }

      if (Files.exists(current, LinkOption.NOFOLLOW_LINKS) && !current.equals(current.toRealPath()))
      {
        throw new IllegalArgumentException(label + " resolves through a filesystem link: " + current);
      }
    }

    return target;
  }

  private static Path requireWorkspaceOutput(File output, String label) throws IOException
  {
    String workspace = System.getenv("CDO_WORKSPACE_ROOT");
    if (workspace == null || workspace.isBlank())
    {
      return output.toPath().toAbsolutePath().normalize();
    }

    return requireSafeWorkspaceChild(output.getPath(), workspace, label);
  }

  private static File requireReadOnlyCheckout(String value, String label) throws IOException
  {
    Path suppliedPath = Paths.get(value).toAbsolutePath();
    Path path = suppliedPath.normalize();
    if (!suppliedPath.equals(path))
    {
      throw new IllegalArgumentException(label + " must not contain path traversal segments: " + value);
    }
    if (!path.isAbsolute() || !Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS) || Files.isSymbolicLink(path) || !path.equals(path.toRealPath()))
    {
      throw new IllegalArgumentException(label + " must be a canonical directory without symbolic links: " + value);
    }

    Path current = path.getRoot();
    for (Path segment : path)
    {
      current = current.resolve(segment);
      if (Files.isSymbolicLink(current))
      {
        throw new IllegalArgumentException(label + " must not pass through a symbolic link: " + current);
      }
    }

    String workspace = System.getenv("CDO_WORKSPACE_ROOT");
    if (workspace != null && !workspace.isBlank() && !path.equals(Paths.get(workspace).toRealPath()))
    {
      throw new IllegalArgumentException(label + " must be the configured Jenkins workspace root: " + path);
    }

    return path.toFile();
  }

  private static void safeDeleteWork(String[] args) throws Exception
  {
    if (args.length != 3)
    {
      throw new IllegalArgumentException("safe-delete-work requires a target directory and workspace root");
    }

    Path target = requireSafeWorkspaceChild(args[1], args[2], "work directory");
    if (!Files.exists(target, LinkOption.NOFOLLOW_LINKS))
    {
      return;
    }

    rejectSymbolicLinks(target, "work directory");
    delete(target.toFile());
  }

  private static void nativeClassifierTest(String[] args) throws Exception
  {
    if (args.length != 2)
    {
      throw new IllegalArgumentException("native-classifier-test requires a workspace root");
    }

    Path workspace = Paths.get(args[1]).toAbsolutePath().normalize();
    if (!Files.isDirectory(workspace, LinkOption.NOFOLLOW_LINKS) || !workspace.equals(workspace.toRealPath()))
    {
      throw new IllegalArgumentException("Native classifier test workspace must be an existing non-symlink directory: " + workspace);
    }

    Path testRoot = Files.createTempDirectory(workspace, "cdo-native-classifier-test-").toRealPath();
    requireSafeWorkspaceChild(testRoot.toString(), workspace.toString(), "native classifier test files");
    String commit = "cdfa15b192bb09ae4aee4c479de839a3d153e5d2";
    Map<String, String> metadata = Map.of("cdo.git.commit", commit);

    try
    {
      Path validRepository = testRoot.resolve("valid");
      createNativeClassifierFixture(validRepository, commit);
      NativeClassifierValidationCommand.validate(validRepository.toFile(), metadata);
      File existingChecksum = new File(singleVersionDirectory(validRepository.toFile(), BUNDLES.get(0)), BUNDLES.get(0) + "-1.0.0-sources.jar.sha256");
      Files.writeString(existingChecksum.toPath(),
          ChecksumsCommand.digest(new File(existingChecksum.getParentFile(), BUNDLES.get(0) + "-1.0.0-sources.jar"), "SHA-256"), StandardCharsets.UTF_8);
      byte[] originalChecksum = Files.readAllBytes(existingChecksum.toPath());
      ChecksumsCommand.execute(new String[] { "checksums", validRepository.toString() });
      if (!Arrays.equals(originalChecksum, Files.readAllBytes(existingChecksum.toPath())))
      {
        throw new AssertionError("Correct CBI checksum sidecar was rewritten");
      }

      File incorrectChecksum = new File(singleVersionDirectory(validRepository.toFile(), BUNDLES.get(1)), BUNDLES.get(1) + "-1.0.0-javadoc.jar.sha1");
      Files.writeString(incorrectChecksum.toPath(), "incorrect", StandardCharsets.UTF_8);
      try
      {
        ChecksumsCommand.execute(new String[] { "checksums", validRepository.toString() });
        throw new AssertionError("Incorrect preexisting checksum was accepted");
      }
      catch (IllegalArgumentException e)
      {
        if (!e.getMessage().contains("Existing CBI checksum is incorrect"))
        {
          throw e;
        }
      }

      expectInvalidNativeFixture(testRoot, "missing-source", metadata, "Missing or empty sources JAR");
      expectInvalidNativeFixture(testRoot, "empty-source-archive", metadata, "Missing or empty sources JAR");
      expectInvalidNativeFixture(testRoot, "corrupt-source-archive", metadata, "Unreadable sources archive");
      expectInvalidNativeFixture(testRoot, "empty-javadoc", metadata, "Missing or empty Javadoc JAR");
      expectInvalidNativeFixture(testRoot, "corrupt-javadoc", metadata, "Unreadable Javadoc archive");
      expectInvalidNativeFixture(testRoot, "mismatched-readme-artifact", metadata, "does not identify the expected source repository");
      expectInvalidNativeFixture(testRoot, "mismatched-readme-revision", metadata, "does not identify the expected source repository");
      expectInvalidNativeFixture(testRoot, "empty-java-source", metadata, "Empty Java source");
      expectInvalidNativeFixture(testRoot, "mismatched-coordinate", metadata, "Unexpected native Maven coordinate");
      expectInvalidNativeFixture(testRoot, "missing-pom", metadata, "Missing or empty POM");
      expectInvalidNativeFixture(testRoot, "malformed-pom", metadata, null);

      System.out.println("Native classifier tests passed for valid artifacts and eleven missing, empty, corrupt, or mismatched cases.");
    }
    finally
    {
      delete(testRoot.toFile());
    }
  }

  private static void resolveDrop(String[] args) throws Exception
  {
    if (args.length != 4)
    {
      throw new IllegalArgumentException("resolve-drop requires the requested ID, drops.txt file, and resolved-ID output file");
    }

    String requested = args[1];
    String listing = null;

    if (requested == null || requested.isBlank() || "default".equals(requested.trim()))
    {
      File dropListFile = requireFile(new File(args[2]), "Eclipse CDO drops.txt file");
      requireWorkspaceOutput(dropListFile, "drop list");
      listing = Files.readString(dropListFile.toPath(), StandardCharsets.UTF_8);
    }

    String dropId = resolveDropSelection(requested, listing);
    File output = new File(args[3]);
    Path outputPath = requireWorkspaceOutput(output, "resolved drop ID");

    Files.createDirectories(outputPath.getParent());
    Files.writeString(outputPath, dropId, StandardCharsets.UTF_8);

    System.out.println("Resolved CDO drop ID: " + dropId);
  }

  private static String resolveDropSelection(String requested, String listing)
  {
    String value = requested == null ? "" : requested.trim();
    if (!value.isEmpty() && !"default".equals(value))
    {
      if (!value.matches("[A-Za-z][A-Za-z0-9._-]*"))
      {
        throw new IllegalArgumentException("Invalid CDO_DROP_ID: " + value);
      }

      return value;
    }

    Set<String> candidates = new LinkedHashSet<>();

    if (listing != null)
    {
      for (String line : listing.split("\\R"))
      {
        String candidate = line.trim();

        if (R_DROP_ID.matcher(candidate).matches())
        {
          candidates.add(candidate);
        }
      }
    }

    return candidates.stream().max(String::compareTo)
        .orElseThrow(() -> new IllegalArgumentException("The Eclipse CDO drops.txt file contains no valid R-drop IDs"));
  }

  private static void dropSelectionTest(String[] args)
  {
    if (args.length != 1)
    {
      throw new IllegalArgumentException("drop-selection-test takes no arguments");
    }

    String unorderedDropList = "R20260901-1311\r\nI20261030-1200\nS20261030-1200\nR20261030-1200\n" + "R20261030-12000\nR2026103A-1200\nnot-a-drop\n";
    String selected = resolveDropSelection("default", unorderedDropList);

    if (!"R20261030-1200".equals(selected))
    {
      throw new AssertionError("Unexpected latest R-drop selection: " + selected);
    }

    String oneRDrop = "I20261030-1200\nR20260901-1311\nS20261029-0544\n";
    if (!"R20260901-1311".equals(resolveDropSelection("", oneRDrop)))
    {
      throw new AssertionError("R-drop was not selected from the drops.txt format");
    }

    if (!"R20260901-1311".equals(resolveDropSelection("R20260901-1311", "not a drops.txt file and intentionally ignored")))
    {
      throw new AssertionError("Explicit CDO_DROP_ID changed or attempted release discovery");
    }

    expectDropSelectionFailure("default", "M20261030-1200\nI20261030-1200\nS20261029-0544\n");
    expectDropSelectionFailure("", "");
    expectDropSelectionFailure("default", "R20260901-1311x\nR2026090-1311\nR20260901-13110\n");

    System.out.println("Drop selection tests passed for unordered R-drops, non-R entries, malformed names, empty listings, and explicit bypass.");
  }

  private static void expectDropSelectionFailure(String requested, String listing)
  {
    try
    {
      resolveDropSelection(requested, listing);
    }
    catch (IllegalArgumentException e)
    {
      if (e.getMessage().contains("no valid R-drop IDs"))
      {
        return;
      }

      throw e;
    }

    throw new AssertionError("Invalid or empty drops.txt content was accepted");
  }

  private static void expectInvalidNativeFixture(Path testRoot, String scenario, Map<String, String> metadata, String expectedMessage) throws Exception
  {
    Path repository = testRoot.resolve(scenario);
    String commit = metadata.get("cdo.git.commit");
    createNativeClassifierFixture(repository, commit);
    String artifactId = BUNDLES.get(0);
    File versionDirectory = singleVersionDirectory(repository.toFile(), artifactId);
    String baseName = artifactId + "-1.0.0";

    switch (scenario)
    {
    case "missing-source":
      Files.delete(new File(versionDirectory, baseName + "-sources.jar").toPath());
      break;

    case "empty-source-archive":
      Files.write(new File(versionDirectory, baseName + "-sources.jar").toPath(), new byte[0]);
      break;

    case "corrupt-source-archive":
      Files.write(new File(versionDirectory, baseName + "-sources.jar").toPath(), new byte[] { 1, 2, 3 });
      break;

    case "empty-javadoc":
      Files.write(new File(versionDirectory, baseName + "-javadoc.jar").toPath(), new byte[0]);
      break;

    case "corrupt-javadoc":
      Files.write(new File(versionDirectory, baseName + "-javadoc.jar").toPath(), new byte[] { 1, 2, 3 });
      break;

    case "mismatched-readme-artifact":
      writeJavadocFixture(new File(versionDirectory, baseName + "-javadoc.jar").toPath(),
          "https://github.com/eclipse-cdo/cdo/tree/" + commit + "/plugins/org.eclipse.net4j");
      break;

    case "mismatched-readme-revision":
      writeJavadocFixture(new File(versionDirectory, baseName + "-javadoc.jar").toPath(),
          "https://github.com/eclipse-cdo/cdo/tree/0000000000000000000000000000000000000000/plugins/" + artifactId);
      break;

    case "empty-java-source":
      Map<String, byte[]> emptySource = new LinkedHashMap<>();
      emptySource.put("org/example/Fixture.java", new byte[0]);
      writeZipFixture(new File(versionDirectory, baseName + "-sources.jar").toPath(), emptySource);
      break;

    case "mismatched-coordinate":
      Files.writeString(new File(versionDirectory, baseName + ".pom").toPath(),
          "<project><groupId>org.eclipse.cdo</groupId><artifactId>wrong</artifactId><version>1.0.0</version></project>", StandardCharsets.UTF_8);
      break;

    case "missing-pom":
      Files.delete(new File(versionDirectory, baseName + ".pom").toPath());
      break;

    case "malformed-pom":
      Files.writeString(new File(versionDirectory, baseName + ".pom").toPath(), "<project", StandardCharsets.UTF_8);
      break;

    default:
      throw new AssertionError("Unknown native classifier test scenario: " + scenario);
    }

    try
    {
      NativeClassifierValidationCommand.validate(repository.toFile(), metadata);
      throw new AssertionError("Invalid native classifier fixture was accepted: " + scenario);
    }
    catch (AssertionError e)
    {
      throw e;
    }
    catch (Exception e)
    {
      if (expectedMessage != null && (e.getMessage() == null || !e.getMessage().contains(expectedMessage)))
      {
        throw new AssertionError("Unexpected failure for " + scenario + ": " + e, e);
      }
    }
  }

  private static void createNativeClassifierFixture(Path repository, String commit) throws IOException
  {
    for (String artifactId : BUNDLES)
    {
      Path versionDirectory = repository.resolve("org/eclipse/cdo/" + artifactId + "/1.0.0");
      Files.createDirectories(versionDirectory);
      String baseName = artifactId + "-1.0.0";
      Files.writeString(versionDirectory.resolve(baseName + ".pom"),
          "<project><groupId>org.eclipse.cdo</groupId><artifactId>" + artifactId + "</artifactId><version>1.0.0</version></project>", StandardCharsets.UTF_8);

      Map<String, byte[]> binary = new LinkedHashMap<>();
      binary.put("META-INF/MANIFEST.MF", "Manifest-Version: 1.0\n\n".getBytes(StandardCharsets.UTF_8));
      binary.put("org/example/Fixture.class", new byte[] { 1 });
      writeZipFixture(versionDirectory.resolve(baseName + ".jar"), binary);

      Map<String, byte[]> sources = new LinkedHashMap<>();
      sources.put("org/example/Fixture.java", "package org.example; class Fixture {}\n".getBytes(StandardCharsets.UTF_8));
      writeZipFixture(versionDirectory.resolve(baseName + "-sources.jar"), sources);

      writeJavadocFixture(versionDirectory.resolve(baseName + "-javadoc.jar"), "https://github.com/eclipse-cdo/cdo/tree/" + commit + "/plugins/" + artifactId);
    }
  }

  private static void writeJavadocFixture(Path file, String sourceReference) throws IOException
  {
    Map<String, byte[]> entries = new LinkedHashMap<>();
    entries.put("META-INF/MANIFEST.MF", "Manifest-Version: 1.0\n\n".getBytes(StandardCharsets.UTF_8));
    entries.put("README.txt", ("CBI-generated documentation for the source plug-in at " + sourceReference + "\n").getBytes(StandardCharsets.UTF_8));
    writeZipFixture(file, entries);
  }

  private static void writeZipFixture(Path file, Map<String, byte[]> entries) throws IOException
  {
    Files.createDirectories(file.getParent());
    try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(file)))
    {
      for (Map.Entry<String, byte[]> entry : entries.entrySet())
      {
        ZipEntry zipEntry = new ZipEntry(entry.getKey());
        zipEntry.setTime(0L);
        output.putNextEntry(zipEntry);
        output.write(entry.getValue());
        output.closeEntry();
      }
    }
  }

  private static void validateWorkspacePath(String[] args) throws IOException
  {
    if (args.length != 3)
    {
      throw new IllegalArgumentException("validate-workspace-path requires a target directory and workspace root");
    }

    requireSafeWorkspaceChild(args[1], args[2], "work directory");
  }

  private static void rejectSymbolicLinks(Path directory, String label) throws IOException
  {
    if (Files.isSymbolicLink(directory))
    {
      throw new IllegalArgumentException(label + " contains a symbolic link: " + directory);
    }

    try (Stream<Path> paths = Files.walk(directory))
    {
      Path link = paths.filter(Files::isSymbolicLink).findFirst().orElse(null);
      if (link != null)
      {
        throw new IllegalArgumentException(label + " contains a symbolic link: " + link);
      }
    }
  }

  private static void publicationScopeTest(String[] args) throws Exception
  {
    if (args.length != 3)
    {
      throw new IllegalArgumentException("publication-scope-test requires workspace root and stable publishing properties file");
    }

    Path testWorkspace = Paths.get(args[1]).toAbsolutePath().normalize();
    if (!Files.isDirectory(testWorkspace, LinkOption.NOFOLLOW_LINKS) || !testWorkspace.equals(testWorkspace.toRealPath()))
    {
      throw new IllegalArgumentException("Publication scope test workspace must be an existing non-symlink directory: " + testWorkspace);
    }

    Path testRoot = Files.createTempDirectory(testWorkspace, "cdo-publication-scope-test-").toRealPath();
    requireSafeWorkspaceChild(testRoot.toString(), testWorkspace.toString(), "publication scope test files");

    Path repository = Files.createDirectory(testRoot.resolve("repository"));
    Path staging = testRoot.resolve("staging").toAbsolutePath();
    Path metadata = testRoot.resolve("metadata.properties");
    Path report = testRoot.resolve("audit.txt");
    Path h2Pom = repository.resolve("com/h2database/h2/2.0.0/h2-2.0.0.pom");
    Path h2Jar = repository.resolve("com/h2database/h2/2.0.0/h2-2.0.0.jar");

    try
    {
      Map<String, String> metadataValues = new LinkedHashMap<>();
      metadataValues.put("cdo.build.type", "R");
      metadataValues.put("cdo.build.stream", "4.x");
      metadataValues.put("cdo.project.release", "4.37");
      metadataValues.put("cdo.project.release.source", "DROP");
      metadataValues.put("cdo.drop.id", "R20990101-0000");
      metadataValues.put("cdo.git.commit", "deadbeef");
      metadataValues.put(CDO_LOCATION_PROPERTY, "file:/workspace/private-drop/");
      metadataValues.put(SIMREL_LOCATION_PROPERTY, "https://download.eclipse.org/releases/test");
      writeProperties(metadata, metadataValues);
      createPublicationScopeFixture(repository);

      byte[] originalH2Pom = Files.readAllBytes(h2Pom);
      byte[] originalH2Jar = Files.readAllBytes(h2Jar);
      File publishingMetadata = requireFile(new File(args[2]), "stable publishing metadata");
      OverlayCommand.execute(new String[] { "overlay", repository.toString(), metadata.toString(), publishingMetadata.getPath() });

      if (!Arrays.equals(originalH2Pom, Files.readAllBytes(h2Pom)) || !Arrays.equals(originalH2Jar, Files.readAllBytes(h2Jar)))
      {
        throw new AssertionError("Metadata overlay modified the external H2 artifact");
      }

      File cdoPom = new File(repository.toFile(), "org/eclipse/cdo/" + BUNDLES.get(7) + "/1.0.0/" + BUNDLES.get(7) + "-1.0.0.pom");
      if (!Files.readString(cdoPom.toPath()).contains("com.h2database"))
      {
        throw new AssertionError("Metadata overlay removed the legitimate H2 Maven dependency");
      }

      String cleanCdoPom = Files.readString(cdoPom.toPath());
      if (cleanCdoPom.contains(CDO_LOCATION_PROPERTY) || cleanCdoPom.contains(SIMREL_LOCATION_PROPERTY))
      {
        throw new AssertionError("Metadata overlay left an internal CBI repository property in a publication POM");
      }

      VerifyCommand.execute(new String[] { "verify", repository.toString(), metadata.toString() });
      Files.writeString(cdoPom.toPath(),
          cleanCdoPom.replace("</properties>", "<" + CDO_LOCATION_PROPERTY + ">file:/workspace/private-drop/</" + CDO_LOCATION_PROPERTY + "></properties>"));
      expectVerifyFailure(repository.toFile(), metadata.toFile(), "Internal CBI property must not be published");
      Files.writeString(cdoPom.toPath(), cleanCdoPom);

      AuditCommand.execute(new String[] { "audit", repository.toString(), metadata.toString(), report.toString(), staging.toString() });
      stageExamplesFixture(staging);
      PublishCheckCommand.execute(new String[] { "publish-check", staging.toString(), metadata.toString(), "VALIDATE", "" });
      assertPublicationStaging(staging, false);

      byte[] cleanStagedPom = Files.readAllBytes(staging.resolve("org/eclipse/cdo/" + BUNDLES.get(7) + "/1.0.0/" + BUNDLES.get(7) + "-1.0.0.pom"));
      Path stagedPom = staging.resolve("org/eclipse/cdo/" + BUNDLES.get(7) + "/1.0.0/" + BUNDLES.get(7) + "-1.0.0.pom");
      Files.writeString(stagedPom,
          cleanCdoPom.replace("</properties>", "<" + CDO_LOCATION_PROPERTY + ">file:/workspace/private-drop/</" + CDO_LOCATION_PROPERTY + "></properties>"));
      ChecksumsCommand.writeChecksums(stagedPom.toFile());
      expectPublicationScopeFailure(staging.toFile(), "Internal CBI property must not be published");
      expectPublishCheckFailure(staging.toFile(), metadata.toFile(), "VALIDATE", "", "Internal CBI property must not be published");
      Files.write(stagedPom, cleanStagedPom);
      ChecksumsCommand.writeChecksums(stagedPom.toFile());

      createSignedReleaseBomFixture(staging.toFile());
      for (File artifact : publicationArtifacts(staging.toFile()))
      {
        Files.writeString(Path.of(artifact.getPath() + ".asc"), "test signature");
      }

      PublishCheckCommand.execute(new String[] { "publish-check", staging.toString(), metadata.toString(), "PACKAGE", "SIGNED" });
      assertPublicationStaging(staging, true);

      Files.writeString(stagedPom,
          cleanCdoPom.replace("</properties>", "<" + CDO_LOCATION_PROPERTY + ">file:/workspace/private-drop/</" + CDO_LOCATION_PROPERTY + "></properties>"));
      ChecksumsCommand.writeChecksums(stagedPom.toFile());
      expectPublishCheckFailure(staging.toFile(), metadata.toFile(), "PACKAGE", "SIGNED", "Internal CBI property must not be published");
      Files.write(stagedPom, cleanStagedPom);
      ChecksumsCommand.writeChecksums(stagedPom.toFile());

      Path stagedH2 = staging.resolve("com/h2database/h2/2.0.0/h2-2.0.0.pom");
      Files.createDirectories(stagedH2.getParent());
      Files.writeString(stagedH2, Files.readString(h2Pom));
      expectPublicationScopeFailure(staging.toFile(), "Unapproved Maven publication coordinate");
      expectPublishCheckFailure(staging.toFile(), metadata.toFile(), "VALIDATE", "", "Unapproved Maven publication coordinate");
      expectPublishCheckFailure(staging.toFile(), metadata.toFile(), "PACKAGE", "SIGNED", "Unapproved Maven publication coordinate");
      Files.delete(stagedH2);

      Path stagedH2Module = staging.resolve("com/h2database/h2/2.0.0/h2-2.0.0.module");
      Files.createDirectories(stagedH2Module.getParent());
      Files.writeString(stagedH2Module, "external H2 module metadata");
      expectPublicationScopeFailure(staging.toFile(), "Unexpected file in publication staging");
      Files.delete(stagedH2Module);

      File missingPom = new File(repository.toFile(), "org/eclipse/cdo/" + BUNDLES.get(0) + "/1.0.0/" + BUNDLES.get(0) + "-1.0.0.pom");
      Path savedMissingPom = testRoot.resolve("missing.pom");
      Files.copy(missingPom.toPath(), savedMissingPom);
      Files.delete(missingPom.toPath());
      expectVerifyFailure(repository.toFile(), metadata.toFile(), "Generated CDO coordinates differ");
      Files.copy(savedMissingPom, missingPom.toPath());

      String duplicateBundle = BUNDLES.get(0);
      Path duplicatePom = repository.resolve("org/eclipse/cdo/" + duplicateBundle + "/2.0.0/" + duplicateBundle + "-2.0.0.pom");
      Files.createDirectories(duplicatePom.getParent());
      String duplicateContent = Files.readString(missingPom.toPath()).replace("<version>1.0.0</version>", "<version>2.0.0</version>");
      Files.writeString(duplicatePom, duplicateContent);
      expectVerifyFailure(repository.toFile(), metadata.toFile(), "Duplicate CDO publication coordinate");
      Files.delete(duplicatePom);

      Path unapprovedPom = repository.resolve("org/eclipse/cdo/cdo.extra/1.0.0/cdo.extra-1.0.0.pom");
      Files.createDirectories(unapprovedPom.getParent());
      Files.writeString(unapprovedPom, "<project><groupId>org.eclipse.cdo</groupId><artifactId>cdo.extra</artifactId><version>1.0.0</version></project>");
      expectVerifyFailure(repository.toFile(), metadata.toFile(), "Unapproved CDO publication coordinate");

      System.out.println(
          "Publication scope tests passed: internal CBI repository properties are stripped and rejected, and external H2 remains unchanged and excluded from staging.");
    }
    finally
    {
      delete(testRoot.toFile());
    }
  }

  private static void createPublicationScopeFixture(Path repository) throws Exception
  {
    for (String bundle : BUNDLES)
    {
      Path versionDirectory = repository.resolve("org/eclipse/cdo/" + bundle + "/1.0.0");
      Files.createDirectories(versionDirectory);

      String dependency = BUNDLES.get(7).equals(bundle)
          ? "<dependencies><dependency><groupId>org.eclipse.platform</groupId><artifactId>org.eclipse.core.runtime</artifactId><version>[3.0.0,4.0.0)</version></dependency>"
              + "<dependency><groupId>com.h2database</groupId><artifactId>h2</artifactId><version>2.0.0</version></dependency></dependencies>"
          : "";
      String pomContent = "<project><modelVersion>4.0.0</modelVersion><groupId>org.eclipse.cdo</groupId><artifactId>" + bundle
          + "</artifactId><version>1.0.0</version><name>" + bundle + "</name><description>Fixture " + bundle + "</description><properties><"
          + CDO_LOCATION_PROPERTY + ">file:/workspace/private-drop/</" + CDO_LOCATION_PROPERTY + "><" + SIMREL_LOCATION_PROPERTY
          + ">https://download.eclipse.org/releases/test</" + SIMREL_LOCATION_PROPERTY + "></properties>" + dependency + "</project>";
      Path pom = versionDirectory.resolve(bundle + "-1.0.0.pom");
      Files.writeString(pom, pomContent);

      for (String classifier : new String[] { "", "-sources", "-javadoc" })
      {
        Path jar = versionDirectory.resolve(bundle + "-1.0.0" + classifier + ".jar");
        Files.writeString(jar, "fixture " + bundle + classifier);
        ChecksumsCommand.writeChecksums(jar.toFile());
      }
    }

    Path h2Directory = repository.resolve("com/h2database/h2/2.0.0");
    Files.createDirectories(h2Directory);
    Files.writeString(h2Directory.resolve("h2-2.0.0.pom"),
        "<project><modelVersion>4.0.0</modelVersion><groupId>com.h2database</groupId><artifactId>h2</artifactId><version>2.0.0</version></project>");
    Files.writeString(h2Directory.resolve("h2-2.0.0.jar"), "external H2 fixture artifact");
  }

  private static List<File> publicationArtifacts(File staging) throws IOException
  {
    try (Stream<Path> paths = Files.walk(staging.toPath()))
    {
      return paths.filter(Files::isRegularFile).map(Path::toFile)
          .filter(file -> file.getName().endsWith(".pom") || file.getName().endsWith(".jar") || file.getName().endsWith(".zip"))
          .sorted(Comparator.comparing(File::getPath)).collect(Collectors.toList());
    }
  }

  private static void stageExamplesFixture(Path staging) throws Exception
  {
    String bundle = BUNDLES.get(0);
    Path sourcePom = staging.resolve("org/eclipse/cdo/" + bundle + "/1.0.0/" + bundle + "-1.0.0.pom");
    String pom = Files.readString(sourcePom);
    pom = pom.replace("<artifactId>" + bundle + "</artifactId>", "<artifactId>" + EXAMPLES_DISTRIBUTION + "</artifactId>");
    pom = pom.replace("<version>1.0.0</version>", "<version>" + EXAMPLES_DISTRIBUTION_VERSION + "</version>");
    pom = pom.replace("</modelVersion>", "</modelVersion><packaging>pom</packaging>");
    String examplesSourceRevision = "abcdef0123456789abcdef0123456789abcdef01";
    pom = pom.replace("<tag>deadbeef</tag>", "<tag>" + examplesSourceRevision + "</tag>");
    pom = pom.replaceFirst("(<developer>\\s*)<url>", "$1<name>CDO</name><url>");
    pom = pom.replace("</properties>", "<cdo.git.commit>deadbeef</cdo.git.commit><cdo.drop.id>R20990101-0000</cdo.drop.id><cdo.examples.source.git.commit>"
        + examplesSourceRevision + "</cdo.examples.source.git.commit></properties>");
    pom = pom.replace("</project>", "<name>CDO Examples</name><description>Examples ZIP</description><url>https://eclipse.dev/cdo/</url>"
        + "<organization><name>Eclipse Foundation</name><url>https://www.eclipse.org</url></organization><licenses><license><name>EPL-2.0</name>"
        + "<url>https://www.eclipse.org/legal/epl-2.0/</url><distribution>repo</distribution></license></licenses><scm><connection>scm:git:https://github.com/eclipse-cdo/cdo.git</connection>"
        + "<developerConnection>scm:git:https://github.com/eclipse-cdo/cdo.git</developerConnection><url>https://eclipse.dev/cdo/</url><tag>"
        + examplesSourceRevision
        + "</tag></scm><issueManagement><system>GitHub</system><url>https://github.com/eclipse-cdo/cdo/issues</url></issueManagement></project>");
    Path versionDirectory = staging.resolve("org/eclipse/cdo/" + EXAMPLES_DISTRIBUTION + "/" + EXAMPLES_DISTRIBUTION_VERSION);
    Files.createDirectories(versionDirectory);
    Path distributionPom = versionDirectory.resolve(EXAMPLES_DISTRIBUTION + "-" + EXAMPLES_DISTRIBUTION_VERSION + ".pom");
    Files.writeString(distributionPom, pom);
    ChecksumsCommand.writeChecksums(distributionPom.toFile());
    Path zip = versionDirectory.resolve(EXAMPLES_DISTRIBUTION + "-" + EXAMPLES_DISTRIBUTION_VERSION + "-examples.zip");
    Files.writeString(zip, "fixture examples ZIP");
    ChecksumsCommand.writeChecksums(zip.toFile());
  }

  private static void assertPublicationStaging(Path staging, boolean signaturesPresent) throws Exception
  {
    List<File> artifacts = publicationArtifacts(staging.toFile());
    int expectedArtifacts = BUNDLES.size() * 4 + 2 + (signaturesPresent ? 1 : 0);
    if (artifacts.size() != expectedArtifacts || Files.exists(staging.resolve("com/h2database")))
    {
      throw new AssertionError("Publication staging must contain exactly " + expectedArtifacts + " CDO Maven artifacts and no H2 files");
    }

    try (Stream<Path> paths = Files.walk(staging.resolve("org")))
    {
      List<Path> files = paths.filter(Files::isRegularFile).collect(Collectors.toList());
      int expectedFiles = expectedArtifacts * 5 + (signaturesPresent ? expectedArtifacts : 0);
      if (files.size() != expectedFiles)
      {
        throw new AssertionError("Expected " + expectedFiles + " CDO Maven artifacts, checksums, and signatures under org; found " + files.size());
      }

      for (Path path : files)
      {
        String relative = staging.relativize(path).toString().replace(File.separatorChar, '/');
        if (!relative.startsWith("org/eclipse/cdo/") || !AuditCommand.isApprovedPublicationPath(relative))
        {
          throw new AssertionError("Unexpected file in publication staging: " + relative);
        }
      }
    }
  }

  private static void expectPublicationScopeFailure(File staging, String expectedMessage) throws Exception
  {
    try
    {
      AuditCommand.validatePublicationScope(staging, true);
    }
    catch (IllegalArgumentException e)
    {
      if (e.getMessage().contains(expectedMessage))
      {
        return;
      }

      throw e;
    }

    throw new AssertionError("Expected publication scope failure containing: " + expectedMessage);
  }

  private static void expectPublishCheckFailure(File staging, File metadata, String mode, String signatures, String expectedMessage) throws Exception
  {
    try
    {
      PublishCheckCommand.execute(new String[] { "publish-check", staging.getPath(), metadata.getPath(), mode, signatures });
    }
    catch (IllegalArgumentException e)
    {
      if (e.getMessage().contains(expectedMessage))
      {
        return;
      }

      throw e;
    }

    throw new AssertionError("Expected publish-check to reject publication staging for " + mode + " with " + expectedMessage);
  }

  private static void expectVerifyFailure(File repository, File metadata, String expectedMessage) throws Exception
  {
    try
    {
      VerifyCommand.execute(new String[] { "verify", repository.getPath(), metadata.getPath() });
    }
    catch (IllegalArgumentException e)
    {
      if (e.getMessage().contains(expectedMessage))
      {
        return;
      }

      throw e;
    }

    throw new AssertionError("Expected raw aggregation verification failure containing: " + expectedMessage);
  }

  private static void workspaceSafetyTest(String[] args) throws Exception
  {
    if (args.length != 1)
    {
      throw new IllegalArgumentException("workspace-safety-test takes no arguments");
    }

    String configuredWorkspace = System.getenv("CDO_WORKSPACE_ROOT");
    Path testRoot = configuredWorkspace == null || configuredWorkspace.isBlank() ? Files.createTempDirectory("cdo-workspace-safety-").toRealPath()
        : Files.createTempDirectory(Paths.get(configuredWorkspace), "cdo-workspace-safety-").toRealPath();
    Path workspace = Files.createDirectory(testRoot.resolve("workspace")).toRealPath();
    Path output = Files.createDirectories(workspace.resolve("work/output"));
    Path outside = Files.createDirectory(testRoot.resolve("outside"));

    try
    {
      Path checkoutRoot = configuredWorkspace == null || configuredWorkspace.isBlank() ? testRoot : Paths.get(configuredWorkspace).toRealPath();
      if (!requireReadOnlyCheckout(checkoutRoot.toString(), "test checkout").toPath().equals(checkoutRoot))
      {
        throw new AssertionError("Canonical checkout root was rejected or changed");
      }

      if (!requireSafeWorkspaceChild(output.toString(), workspace.toString(), "test output").equals(output))
      {
        throw new AssertionError("Safe workspace descendant was rejected or changed");
      }

      expectUnsafeWorkspacePath("", workspace.toString());
      expectUnsafeWorkspacePath(workspace.toString(), workspace.toString());
      expectUnsafeWorkspacePath(outside.toString(), Paths.get("/").toString());
      expectUnsafeWorkspacePath(outside.toString(), workspace.toString());
      expectUnsafeWorkspacePath(workspace.resolve("../outside").toString(), workspace.toString());
      expectUnsafeReadOnlyCheckout(workspace.resolve("../workspace").toString());
      expectUnsafeDelete(workspace.toString(), workspace.toString());

      Path safeWork = Files.createDirectories(workspace.resolve("safe-work/nested"));
      Files.writeString(safeWork.resolve("temporary.txt"), "temporary");
      safeDeleteWork(new String[] { "safe-delete-work", safeWork.getParent().toString(), workspace.toString() });

      if (Files.exists(safeWork.getParent()))
      {
        throw new AssertionError("Safe workspace cleanup did not remove its work directory");
      }

      Path link = workspace.resolve("link");
      boolean symlinksSupported = true;

      try
      {
        Files.createSymbolicLink(link, outside);
      }
      catch (UnsupportedOperationException | IOException | SecurityException e)
      {
        symlinksSupported = false;
      }

      if (symlinksSupported)
      {
        expectUnsafeWorkspacePath(link.resolve("generated").toString(), workspace.toString());
        expectUnsafeReadOnlyCheckout(link.toString());
        Path marker = Files.writeString(outside.resolve("must-survive.txt"), "safe");

        try
        {
          safeDeleteWork(new String[] { "safe-delete-work", link.toString(), workspace.toString() });
          throw new AssertionError("Symlink work directory was deleted");
        }
        catch (IllegalArgumentException e)
        {
          if (!Files.exists(marker))
          {
            throw new AssertionError("Rejected symlink deletion modified the outside target");
          }
        }
      }

      System.out.println("Workspace safety tests passed for empty, root, outside, traversal, and "
          + (symlinksSupported ? "symlink-escape" : "platform-unavailable symlink") + " paths.");
    }
    finally
    {
      delete(testRoot.toFile());
    }
  }

  private static void expectUnsafeWorkspacePath(String target, String workspace) throws IOException
  {
    try
    {
      requireSafeWorkspaceChild(target, workspace, "test output");
    }
    catch (IllegalArgumentException e)
    {
      return;
    }

    throw new AssertionError("Unsafe workspace path was accepted: " + target);
  }

  private static void expectUnsafeReadOnlyCheckout(String path)
  {
    try
    {
      requireReadOnlyCheckout(path, "test checkout");
      throw new AssertionError("Unsafe checkout path was accepted: " + path);
    }
    catch (IllegalArgumentException expected)
    {
      // Expected rejection.
    }
    catch (IOException e)
    {
      throw new AssertionError("Unable to validate unsafe checkout path: " + path, e);
    }
  }

  private static void expectUnsafeDelete(String target, String workspace) throws Exception
  {
    try
    {
      safeDeleteWork(new String[] { "safe-delete-work", target, workspace });
    }
    catch (IllegalArgumentException e)
    {
      return;
    }

    throw new AssertionError("Unsafe deletion target was accepted: " + target);
  }

  private static File requireDirectory(String path, String label)
  {
    File file = new File(path);
    if (!file.isDirectory())
    {
      throw new IllegalArgumentException("Missing " + label + " directory: " + file);
    }

    return file;
  }

  private static Document newDocument() throws Exception
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
    return factory.newDocumentBuilder().newDocument();
  }

  private static Document parse(InputStream in, String label) throws Exception
  {
    DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
    f.setNamespaceAware(true);
    f.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    f.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    f.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

    DocumentBuilder b = f.newDocumentBuilder();

    try (InputStream input = in)
    {
      return b.parse(input);
    }
  }

  private static List<File> find(File root, String suffix)
  {
    List<File> result = new ArrayList<>();

    for (File f : findAll(root))
    {
      if (f.isFile() && f.getName().endsWith(suffix))
      {
        result.add(f);
      }
    }

    return result;
  }

  private static List<File> findAll(File root)
  {
    List<File> result = new ArrayList<>();

    File[] files = root.listFiles();
    if (files == null)
    {
      return result;
    }

    for (File f : files)
    {
      result.add(f);

      if (f.isDirectory())
      {
        result.addAll(findAll(f));
      }
    }

    return result;
  }

  private static Element child(Node parent, String name)
  {
    if (parent == null)
    {
      return null;
    }

    NodeList list = parent.getChildNodes();

    for (int i = 0; i < list.getLength(); i++)
    {
      Node n = list.item(i);
      if (n instanceof Element && name.equals(n.getLocalName() == null ? n.getNodeName() : n.getLocalName()))
      {
        return (Element)n;
      }
    }

    return null;
  }

  private static String text(Element e, String name)
  {
    Element c = child(e, name);
    return text(c);
  }

  private static String text(Node node)
  {
    return node == null ? "" : node.getTextContent().trim();
  }

  private static void write(Document doc, File file) throws Exception
  {
    requireWorkspaceOutput(file, "XML output");
    TransformerFactory tf = TransformerFactory.newInstance();
    tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

    Transformer t = tf.newTransformer();
    t.setOutputProperty(OutputKeys.INDENT, "yes");
    t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

    try (Writer out = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8))
    {
      t.transform(new DOMSource(doc), new StreamResult(out));
    }
  }

  private static void writeProperties(Path path, Map<String, String> values) throws IOException
  {
    requireWorkspaceOutput(path.toFile(), "properties output");
    List<String> lines = new ArrayList<>();
    values.forEach((k, v) -> lines.add(k + "=" + v));

    Files.createDirectories(path.getParent());
    Files.write(path, lines, StandardCharsets.UTF_8);
  }

  private static Map<String, String> readProperties(File file) throws IOException
  {
    Map<String, String> result = new LinkedHashMap<>();

    for (String line : Files.readAllLines(file.toPath(), StandardCharsets.UTF_8))
    {
      int i = line.indexOf('=');
      if (i > 0)
      {
        result.put(line.substring(0, i), line.substring(i + 1));
      }
    }

    return result;
  }

  private static void deriveReleaseInventory(String[] args) throws Exception
  {
    if (args.length != 4 && args.length != 5)
    {
      throw new IllegalArgumentException(
          "derive-release-inventory requires selected drop, expected drop ID, output properties file, and optional prepared metadata");
    }

    Map<String, String> metadata = args.length == 5 && new File(args[4]).isFile() ? readProperties(new File(args[4])) : null;
    Map<String, String> inventory = readReleaseInventory(args[1], BUNDLES, metadata);
    if (!inventory.get("drop.id").equals(args[2]))
    {
      throw new IllegalArgumentException("Selected drop ID " + args[2] + " does not match drop build-info qualifier " + inventory.get("drop.id"));
    }

    writeProperties(Path.of(args[3]), inventory);
    System.out.println("Derived " + BUNDLES.size() + " approved Maven coordinates for project release " + inventory.get("project.release") + " from "
        + inventory.get("drop.location"));
  }

  private static Map<String, String> readReleaseInventory(String location, List<String> approved) throws Exception
  {
    return readReleaseInventory(location, approved, null);
  }

  private static Map<String, String> readReleaseInventory(String location, List<String> approved, Map<String, String> metadata) throws Exception
  {
    DropLocation drop = DropLocation.parse(location);
    Properties web = new Properties();
    try (InputStream input = drop.openOptional("web.properties"))
    {
      if (input != null)
      {
        web.load(input);
      }
    }

    String dropRelease = web.getProperty("web.label");
    String release;
    String releaseSource;
    if (metadata == null)
    {
      release = dropRelease;
      releaseSource = "DROP";
    }
    else
    {
      release = BomCommand.requiredMetadata(metadata, "cdo.project.release");
      releaseSource = BomCommand.requiredMetadata(metadata, "cdo.project.release.source");
    }

    String bomVersion = BomCommand.normalizeProjectReleaseVersion(release);
    Document buildInfo = parse(drop.open("build-info.xml"), "build-info.xml");
    Element build = buildInfo.getDocumentElement();
    if (!"build".equals(localName(build)))
    {
      throw new IllegalArgumentException("build-info.xml root is not <build>");
    }

    Map<String, String> provenance = new LinkedHashMap<>();
    for (Map.Entry<String, String> attribute : PrepareCommand.ATTRIBUTES.entrySet())
    {
      provenance.put(attribute.getValue(), required(build, attribute.getKey()));
    }

    String dropID = provenance.get("cdo.drop.id");
    if (!dropID.matches("[A-Za-z][A-Za-z0-9._-]*"))
    {
      throw new IllegalArgumentException("Invalid release-drop qualifier: " + dropID);
    }

    if (metadata != null && !dropID.equals(metadata.get("cdo.drop.id")))
    {
      throw new IllegalArgumentException("Prepared build metadata does not match selected drop qualifier " + dropID);
    }

    if (dropRelease != null && !dropRelease.isBlank() && !BomCommand.normalizeProjectReleaseVersion(dropRelease).equals(bomVersion))
    {
      throw new IllegalArgumentException("Prepared project release " + release + " does not match selected drop web.label " + dropRelease);
    }

    if ((dropRelease == null || dropRelease.isBlank()) && !"OVERRIDE".equals(releaseSource))
    {
      throw new IllegalArgumentException("Selected drop has no web.label and release metadata is not marked as an explicit override");
    }

    if (!"DROP".equals(releaseSource) && !"OVERRIDE".equals(releaseSource))
    {
      throw new IllegalArgumentException("Invalid cdo.project.release.source in prepared metadata: " + releaseSource);
    }

    Document index = parse(drop.open("index.xml"), "index.xml");
    Map<String, String> inventory = new LinkedHashMap<>();
    inventory.put("drop.id", dropID);
    inventory.put("drop.location", drop.location());
    inventory.put("project.release", release);
    inventory.put("cdo.project.release", release);
    inventory.put("cdo.project.release.source", releaseSource);
    inventory.putAll(provenance);
    inventory.put("bom.groupId", "org.eclipse.cdo");
    inventory.put("bom.artifactId", "org.eclipse.cdo.bom");
    inventory.put("bom.version", bomVersion);
    NodeList elements = index.getElementsByTagNameNS("*", "element");
    for (String bundle : approved)
    {
      List<Element> matches = new ArrayList<>();
      for (int i = 0; i < elements.getLength(); i++)
      {
        Node node = elements.item(i);
        if (node instanceof Element)
        {
          Element element = (Element)node;
          if (bundle.equals(element.getAttribute("name")) && "osgi.bundle".equals(element.getAttribute("type")))
          {
            matches.add(element);
          }
        }
      }

      if (matches.size() != 1)
      {
        throw new IllegalArgumentException("Expected exactly one osgi.bundle index.xml entry for " + bundle + ", found " + matches.size());
      }

      String osgiVersion = matches.get(0).getAttribute("version");
      inventory.put("osgi." + bundle, osgiVersion);
      inventory.put("artifact." + bundle, toMavenReleaseVersion(osgiVersion, bundle));
    }

    return inventory;
  }

  private static Map<String, String> releaseInventoryVersions(Map<String, String> inventory, List<String> approved)
  {
    Map<String, String> versions = new LinkedHashMap<>();
    for (String bundle : approved)
    {
      String version = inventory.get("artifact." + bundle);
      if (version == null)
      {
        throw new IllegalArgumentException("Release inventory is missing approved bundle " + bundle);
      }

      versions.put(bundle, version);
    }

    return versions;
  }

  private static String toMavenReleaseVersion(String version, String bundle)
  {
    Matcher osgi = OSGI_VERSION.matcher(version);
    if (!osgi.matches())
    {
      throw new IllegalArgumentException("Unsupported or malformed OSGi version for " + bundle + ": " + version);
    }

    for (int i = 1; i <= 3; i++)
    {
      if (!osgi.group(i).matches("0|[1-9][0-9]*"))
      {
        throw new IllegalArgumentException("Non-canonical OSGi version component for " + bundle + ": " + version);
      }

      try
      {
        Integer.parseInt(osgi.group(i));
      }
      catch (NumberFormatException ex)
      {
        throw new IllegalArgumentException("OSGi version component is outside the supported integer range for " + bundle + ": " + version, ex);
      }
    }

    if (MAVEN_RELEASE_TRICK.matcher(version).matches())
    {
      return version;
    }

    return osgi.group(1) + "." + osgi.group(2) + "." + osgi.group(3);
  }

  private static String localName(Node node)
  {
    return node.getLocalName() == null ? node.getNodeName() : node.getLocalName();
  }

  /**
   * Resolves resources from either a local promoted drop or an HTTP(S) drop.
   *
   * @author Eike Stepper
   */
  private static final class DropLocation
  {
    private final String value;

    private final URI uri;

    public DropLocation(String value, URI uri)
    {
      this.value = value;
      this.uri = uri;
    }

    public String location()
    {
      return value.endsWith("/") ? value : value + "/";
    }

    public InputStream open(String resource) throws IOException
    {
      URI resourceURI = uri.resolve(resource);
      if ("file".equalsIgnoreCase(resourceURI.getScheme()))
      {
        return Files.newInputStream(Path.of(resourceURI));
      }

      URL url = resourceURI.toURL();
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      connection.setRequestMethod("GET");
      connection.setDoOutput(false);
      connection.setConnectTimeout(30000);
      connection.setReadTimeout(30000);
      connection.setInstanceFollowRedirects(true);
      if (connection.getResponseCode() >= 400)
      {
        throw new IOException("Unable to read drop resource " + resourceURI + ": HTTP " + connection.getResponseCode());
      }

      return connection.getInputStream();
    }

    public InputStream openOptional(String resource) throws IOException
    {
      URI resourceURI = uri.resolve(resource);
      if ("file".equalsIgnoreCase(resourceURI.getScheme()))
      {
        Path path = Path.of(resourceURI);
        return Files.exists(path) ? Files.newInputStream(path) : null;
      }

      URL url = resourceURI.toURL();
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      connection.setRequestMethod("GET");
      connection.setConnectTimeout(30000);
      connection.setReadTimeout(30000);
      connection.setInstanceFollowRedirects(true);
      int status = connection.getResponseCode();
      if (status == HttpURLConnection.HTTP_NOT_FOUND)
      {
        return null;
      }

      if (status >= 400)
      {
        throw new IOException("Unable to read drop resource " + resourceURI + ": HTTP " + status);
      }

      return connection.getInputStream();
    }

    public static DropLocation parse(String value)
    {
      if (value == null || value.isBlank())
      {
        throw new IllegalArgumentException("Drop location is empty; supply a local directory or an HTTP(S) drop URL");
      }

      if (value.matches("(?i)^https?://.+"))
      {
        return new DropLocation(value, URI.create(value.endsWith("/") ? value : value + "/"));
      }

      File directory = requireDirectory(value, "drop");
      return new DropLocation(directory.toURI().toString(), directory.toURI());
    }
  }

  /**
   * Validates and reports the native Maven artifacts generated by CBI before the CDO metadata overlay.
   *
   * @author Eike Stepper
   */
  private static final class NativeClassifierValidationCommand
  {
    public static void execute(String[] args) throws Exception
    {
      if (args.length != 3)
      {
        throw new IllegalArgumentException("native-classifier-validation requires the raw Maven repository and build metadata");
      }

      File repository = requireDirectory(args[1], "raw Maven repository");
      requireWorkspaceOutput(repository, "raw Maven repository");
      rejectSymbolicLinks(repository.toPath(), "raw Maven repository");
      Map<String, String> metadata = readProperties(requireFile(new File(args[2]), "build metadata"));
      validate(repository, metadata);
      System.out.println("Validated native binary, POM, sources, and Javadoc artifacts for all " + BUNDLES.size() + " approved coordinates.");
    }

    private static void validate(File repository, Map<String, String> metadata) throws Exception
    {
      String commit = metadata.get("cdo.git.commit");
      if (commit == null || !commit.matches("[0-9a-fA-F]{40,64}"))
      {
        throw new IllegalArgumentException("Build metadata has no valid cdo.git.commit for native Javadoc reference validation");
      }

      for (String artifactId : BUNDLES)
      {
        File versionDirectory = singleVersionDirectory(repository, artifactId);
        String version = versionDirectory.getName();
        String baseName = artifactId + "-" + version;
        File binary = new File(versionDirectory, baseName + ".jar");
        File pom = new File(versionDirectory, baseName + ".pom");
        File sources = new File(versionDirectory, baseName + "-sources.jar");
        File javadoc = new File(versionDirectory, baseName + "-javadoc.jar");

        requireNonEmpty(binary, "binary JAR");
        validateArchive(binary, "binary JAR", artifactId);
        requireNonEmpty(pom, "POM");
        Element project = parse(pom).getDocumentElement();
        String coordinate = text(project, "groupId") + ":" + text(project, "artifactId") + ":" + text(project, "version");
        String expected = "org.eclipse.cdo:" + artifactId + ":" + version;
        if (!expected.equals(coordinate))
        {
          throw new IllegalArgumentException("Unexpected native Maven coordinate in " + pom + ": " + coordinate + " (expected " + expected + ")");
        }

        requireNonEmpty(sources, "sources JAR");
        validateSources(sources, artifactId);
        requireNonEmpty(javadoc, "Javadoc JAR");
        validateJavadoc(javadoc, artifactId, commit);

        System.out.println(expected + "\tbinary=valid[" + checksumStatus(binary) + "]\tpom=valid[" + checksumStatus(pom) + "]\tsources=valid["
            + checksumStatus(sources) + "]\tjavadoc=valid[" + checksumStatus(javadoc) + "]");
      }
    }

    private static String checksumStatus(File artifact)
    {
      StringBuilder result = new StringBuilder();
      for (String suffix : ChecksumsCommand.ALGORITHMS.keySet())
      {
        if (result.length() > 0)
        {
          result.append(',');
        }

        File sidecar = new File(artifact.getPath() + "." + suffix);
        result.append(suffix).append('=').append(sidecar.isFile() ? sidecar.length() == 0 ? "empty" : "present" : "missing");
      }

      return result.toString();
    }

    private static void requireNonEmpty(File file, String label)
    {
      if (!file.isFile() || file.length() == 0)
      {
        throw new IllegalArgumentException("Missing or empty " + label + ": " + file);
      }
    }

    private static void validateArchive(File file, String label, String artifactId) throws IOException
    {
      try (JarFile jar = new JarFile(file))
      {
        boolean hasContent = false;
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements())
        {
          JarEntry entry = entries.nextElement();
          if (!entry.isDirectory())
          {
            hasContent = true;
            drain(jar.getInputStream(entry));
          }
        }

        if (!hasContent)
        {
          throw new IllegalArgumentException("Empty " + label + " archive for " + artifactId + ": " + file);
        }
      }
      catch (IOException e)
      {
        throw new IllegalArgumentException("Unreadable " + label + " archive for " + artifactId + ": " + file, e);
      }
    }

    private static void validateSources(File file, String artifactId) throws IOException
    {
      int javaFiles = 0;
      try (JarFile jar = new JarFile(file))
      {
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements())
        {
          JarEntry entry = entries.nextElement();
          if (!entry.isDirectory())
          {
            long size = drain(jar.getInputStream(entry));
            if (entry.getName().endsWith(".java"))
            {
              if (size == 0)
              {
                throw new IllegalArgumentException("Empty Java source in " + file + ": " + entry.getName());
              }

              javaFiles++;
            }
          }
        }
      }
      catch (IOException e)
      {
        throw new IllegalArgumentException("Unreadable sources archive for " + artifactId + ": " + file, e);
      }

      if (javaFiles == 0)
      {
        throw new IllegalArgumentException("Sources archive contains no Java source files for " + artifactId + ": " + file);
      }
    }

    private static void validateJavadoc(File file, String artifactId, String commit) throws IOException
    {
      try (ZipFile zip = new ZipFile(file))
      {
        ZipEntry manifest = zip.getEntry("META-INF/MANIFEST.MF");
        ZipEntry readme = zip.getEntry("README.txt");
        if (manifest == null || manifest.isDirectory() || readme == null || readme.isDirectory())
        {
          throw new IllegalArgumentException("Native CBI Javadoc archive must contain META-INF/MANIFEST.MF and README.txt: " + file);
        }

        String content = null;
        long manifestSize = 0;
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements())
        {
          ZipEntry entry = entries.nextElement();
          if (entry.isDirectory())
          {
            continue;
          }

          if ("README.txt".equals(entry.getName()))
          {
            try (InputStream in = zip.getInputStream(entry))
            {
              content = new String(in.readAllBytes(), StandardCharsets.UTF_8).trim();
            }
          }
          else
          {
            long entrySize = drain(zip.getInputStream(entry));
            if ("META-INF/MANIFEST.MF".equals(entry.getName()))
            {
              manifestSize = entrySize;
            }
          }
        }

        Pattern sourceReference = Pattern.compile(
            "https://github\\.com/eclipse-cdo/cdo/(?:tree|blob)/" + Pattern.quote(commit) + "/plugins/" + Pattern.quote(artifactId) + "(?:/|[?#\\s]|$)");
        if (manifestSize == 0 || content == null || content.length() < 40 || !sourceReference.matcher(content).find())
        {
          throw new IllegalArgumentException(
              "Native CBI Javadoc README does not identify the expected source repository, revision, and plugin for " + artifactId + ": " + file);
        }
      }
      catch (IOException e)
      {
        throw new IllegalArgumentException("Unreadable Javadoc archive for " + artifactId + ": " + file, e);
      }
    }

    private static long drain(InputStream input) throws IOException
    {
      try (InputStream in = input)
      {
        byte[] buffer = new byte[8192];
        long count = 0;
        int read;
        while ((read = in.read(buffer)) != -1)
        {
          count += read;
        }

        return count;
      }
    }
  }
}
