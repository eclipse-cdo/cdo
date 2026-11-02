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

import javax.tools.DocumentationTool;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
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
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystemException;
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
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
      "org.eclipse.emf.cdo.server.db");

  private static final List<String> CLASSIFIERS = Arrays.asList("sources", "javadoc");

  private CDOAggregationTool()
  {
  }

  public static void main(String[] args) throws Exception
  {
    if (args.length == 0)
    {
      throw new IllegalArgumentException(
          "Usage: prepare|overlay|verify|consumer-versions|sources-javadoc|native-classifier-diagnostics|checksums|audit|publish-check|publication-scope-check|verify-package|publish-policy|policy-test|stream-test|signature-test|workspace-safety-test|publication-scope-test|safe-delete-work|validate-workspace-path ...");
    }

    switch (args[0])
    {
    case "prepare":
      PrepareCommand.execute(args);
      break;

    case "drop-info":
      verifyDropInfo(args);
      break;

    case "overlay":
      OverlayCommand.execute(args);
      break;

    case "verify":
      VerifyCommand.execute(args);
      break;

    case "consumer-versions":
      ConsumerVersionsCommand.execute(args);
      break;

    case "sources-javadoc":
      SourcesJavadocCommand.execute(args);
      break;

    case "native-classifier-diagnostics":
      NativeClassifierDiagnosticsCommand.execute(args);
      break;

    case "checksums":
      ChecksumsCommand.execute(args);
      break;

    case "audit":
      AuditCommand.execute(args);
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

  private static void publicationScopeCheck(String[] args) throws Exception
  {
    if (args.length != 2)
    {
      throw new IllegalArgumentException("publication-scope-check requires a staging directory");
    }

    File staging = requireDirectory(args[1], "staging directory");
    requireWorkspaceOutput(staging, "staging directory");
    rejectSymbolicLinks(staging.toPath(), "staging directory");
    int artifacts = AuditCommand.validatePublicationScope(staging, false);
    System.out.println("Validated publication scope for " + artifacts + " approved CDO artifacts.");
  }

  /**
   * Implements {@code prepare}: args are command, drop, metadata, repositories.
   *
   * @author Eike Stepper
   */
  private static final class PrepareCommand
  {
    private static final LinkedHashMap<String, String> ATTRIBUTES = new LinkedHashMap<>();

    private static final String CDO_LOCATION = "org.eclipse.cbi.p2repo.cdo-location";

    private static final String SIMREL_LOCATION = "org.eclipse.cbi.p2repo.simrel-location";

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
      if (args.length != 4)
      {
        throw new IllegalArgumentException("prepare requires drop, metadata, repositories");
      }

      requireWorkspaceOutput(new File(args[2]), "build metadata output");
      requireWorkspaceOutput(new File(args[3]), "validation repository list output");
      DropLocation drop = DropLocation.parse(args[1]);

      Document info = parse(drop.open("build-info.xml"), "build-info.xml");
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
      derived.put(CDO_LOCATION, dropRepository);
      derived.put(SIMREL_LOCATION, simrelRepository);

      writeProperties(Path.of(args[2]), derived);
      Files.write(Path.of(args[3]), List.of(simrelRepository), StandardCharsets.UTF_8);
      System.out.println(CDO_LOCATION + "=" + dropRepository);
      System.out.println(SIMREL_LOCATION + "=" + simrelRepository);
      System.out.println("Validated CDO and SimRel repositories; build=" + dropID);
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

        for (Map.Entry<String, String> entry : values.entrySet())
        {
          Element property = child(properties, entry.getKey());
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

      System.out.println("Added " + values.size() + " traceability properties to " + overlaid + " approved CDO POMs.");
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
   * Implements {@code verify}: args are command, aggregated repository, metadata properties.
   *
   * @author Eike Stepper
   */
  private static final class VerifyCommand
  {
    private static final Pattern FORBIDDEN = Pattern
        .compile("(?:org\\.eclipse\\.net4j\\.db\\.(?:derby|hsqldb|mysql|oracle|postgresql|db2)|org\\.eclipse\\.emf\\.cdo\\.(?:ui|tests|examples))");

    public static void execute(String[] args) throws Exception
    {
      File root = requireDirectory(args[1], "aggregated repository");
      requireWorkspaceOutput(root, "aggregated repository");
      rejectSymbolicLinks(root.toPath(), "aggregated repository");
      Map<String, String> values = readProperties(requireFile(new File(args[2]), "metadata properties"));
      List<File> poms = find(root, ".pom");

      Map<String, String> actualVersions = new LinkedHashMap<>();

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

        for (String key : values.keySet())
        {
          if (props == null || !values.get(key).equals(text(child(props, key))))
          {
            throw new IllegalArgumentException("Missing traceability property " + key + " in " + file);
          }
        }
      }

      if (!actualVersions.keySet().equals(new LinkedHashSet<>(BUNDLES)))
      {
        throw new IllegalArgumentException("Generated CDO coordinates differ. Expected=" + BUNDLES + " actual=" + actualVersions.keySet());
      }

      for (File file : findAll(root))
      {
        if (FORBIDDEN.matcher(file.getPath()).find())
        {
          throw new IllegalArgumentException("Forbidden artifact in output: " + file);
        }
      }

      System.out.println("Verified twelve CDO coordinates, traceability properties, external aggregation artifacts, and excluded-artifact guard.");
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
   * Implements {@code sources-javadoc}: args are command, drop, final repository, work directory, Maven repository.
   *
   * @author Eike Stepper
   */
  private static final class SourcesJavadocCommand
  {
    private static final Set<String> REQUIRED = Set.of( //
        "org.eclipse.core.expressions", //
        "org.eclipse.core.jobs", //
        "org.eclipse.core.runtime", //
        "org.eclipse.equinox.app", //
        "org.eclipse.equinox.common", //
        "org.eclipse.equinox.preferences", //
        "org.eclipse.equinox.registry", //
        "org.eclipse.osgi", //
        "org.eclipse.osgi.services", //
        "org.osgi.service.component", //
        "org.osgi.service.log", //
        "org.osgi.service.prefs");

    public static void execute(String[] args) throws Exception
    {
      if (args.length != 5)
      {
        throw new IllegalArgumentException("sources-javadoc requires drop, finalRepository, workDirectory, mavenRepository");
      }

      DropLocation drop = DropLocation.parse(args[1]);
      File repository = requireDirectory(args[2], "Maven repository");
      File work = new File(args[3]);
      requireWorkspaceOutput(repository, "Maven repository");
      requireWorkspaceOutput(work, "sources/Javadoc work directory");
      rejectSymbolicLinks(repository.toPath(), "Maven repository");
      File dropPlugins = drop.materializePlugins(new File(work, "drop-plugins"));
      Files.createDirectories(work.toPath());

      List<File> plugins = Arrays.asList(dropPlugins.listFiles((dir, name) -> name.endsWith(".jar")));
      List<File> binaries = new ArrayList<>();
      List<File> classpathJars = new ArrayList<>();

      try (Stream<Path> stream = Files.walk(repository.toPath()))
      {
        stream.filter(p -> p.toString().endsWith(".jar")).forEach(p -> classpathJars.add(p.toFile()));
      }

      classpathJars.addAll(plugins);
      File localMavenRepository = new File(args[4]);
      if (localMavenRepository.isDirectory())
      {
        addLatestP2Jars(localMavenRepository, classpathJars);
      }
      else
      {
        throw new IllegalStateException("Maven dependency cache is not available for Javadoc classpath: " + localMavenRepository);
      }

      for (String bundle : BUNDLES)
      {
        File binary = findBundle(plugins, bundle);
        binaries.add(binary);

        Manifest binaryManifest = manifest(binary);
        String binaryVersion = binaryManifest.getMainAttributes().getValue("Bundle-Version");

        File source = findSource(plugins, bundle, binaryVersion);
        if (source == null)
        {
          throw new IllegalArgumentException("No matching source plugin for " + bundle + " version " + binaryVersion);
        }

        File versionDirectory = singleVersionDirectory(repository, bundle);
        String version = versionDirectory.getName();

        File sourceJar = new File(versionDirectory, bundle + "-" + version + "-sources.jar");
        File javadocJar = new File(versionDirectory, bundle + "-" + version + "-javadoc.jar");
        File sourceDirectory = new File(work, bundle + "/sources");

        extractSources(source, sourceDirectory);
        jarDirectory(sourceDirectory, sourceJar);
        generateJavadoc(sourceDirectory, classpathJars, work, bundle, javadocJar);
        validateClassifier(sourceJar, true, bundle);
        validateClassifier(javadocJar, false, bundle);
        System.out.println("Source/Javadoc: " + bundle + " <- " + source.getName());
      }
    }

    private static void addLatestP2Jars(File localRepository, List<File> classpathJars) throws IOException
    {
      Map<String, File> latest = new LinkedHashMap<>();
      Path p2 = localRepository.toPath().resolve("p2/osgi/bundle");
      if (!Files.isDirectory(p2))
      {
        return;
      }

      try (Stream<Path> stream = Files.list(p2))
      {
        stream.filter(Files::isDirectory).forEach(bundleDirectory -> {
          String bsn = bundleDirectory.getFileName().toString();
          if (!REQUIRED.contains(bsn))
          {
            return;
          }

          try
          {
            File selected;
            try (Stream<Path> jars = Files.walk(bundleDirectory))
            {
              selected = jars //
                  .filter(p -> p.toString().endsWith(".jar") && !p.toString().contains("source") && !p.toString().contains("javadoc")) //
                  .max(Comparator.comparingLong(p -> p.toFile().lastModified())) //
                  .map(Path::toFile) //
                  .orElse(null);
            }

            if (selected != null)
            {
              latest.put(bsn, selected);
            }
          }
          catch (IOException e)
          {
            throw new RuntimeException(e);
          }
        });
      }

      classpathJars.addAll(latest.values());
      addLatestMavenGroup(localRepository, "org/eclipse/emf", classpathJars);
    }

    private static void addLatestMavenGroup(File localRepository, String groupPath, List<File> classpathJars) throws IOException
    {
      Path group = localRepository.toPath().resolve(groupPath);
      if (!Files.isDirectory(group))
      {
        return;
      }

      try (Stream<Path> stream = Files.list(group))
      {
        stream.filter(Files::isDirectory).forEach(artifact -> {
          try
          {
            File selected;

            try (Stream<Path> jars = Files.walk(artifact))
            {
              selected = jars.filter(p -> p.toString().endsWith(".jar") && !p.toString().contains("source") && !p.toString().contains("javadoc"))
                  .max(Comparator.comparingLong(p -> p.toFile().lastModified())).map(Path::toFile).orElse(null);
            }

            if (selected != null)
            {
              classpathJars.add(selected);
            }
          }
          catch (IOException e)
          {
            throw new RuntimeException(e);
          }
        });
      }
    }

    private static File findBundle(List<File> plugins, String bundle) throws Exception
    {
      File result = null;

      for (File file : plugins)
      {
        Manifest m = manifest(file);
        if (bundle.equals(symbolicName(m.getMainAttributes().getValue("Bundle-SymbolicName"))))
        {
          if (result != null)
          {
            throw new IllegalArgumentException("Multiple binary plugins for " + bundle);
          }

          result = file;
        }
      }

      if (result == null)
      {
        throw new IllegalArgumentException("Missing binary plugin for " + bundle);
      }

      return result;
    }

    private static File findSource(List<File> plugins, String bundle, String version) throws Exception
    {
      File result = null;

      for (File file : plugins)
      {
        Manifest m = manifest(file);

        String declaration = m.getMainAttributes().getValue("Eclipse-SourceBundle");
        if (declaration != null && bundle.equals(symbolicName(declaration)))
        {
          String sourceVersion = declaration.replaceFirst(".*version=\\\"([^\\\"]+)\\\".*", "$1");
          if (version.equals(sourceVersion))
          {
            if (result != null)
            {
              throw new IllegalArgumentException("Multiple matching source plugins for " + bundle + " " + version);
            }

            result = file;
          }
        }
      }

      return result;
    }

    private static String symbolicName(String value)
    {
      if (value == null)
      {
        return null;
      }

      int separator = value.indexOf(';');
      return (separator < 0 ? value : value.substring(0, separator)).trim();
    }

    private static Manifest manifest(File jar) throws IOException
    {
      try (JarFile jf = new JarFile(jar))
      {
        return jf.getManifest();
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

    private static void extractSources(File sourceJar, File target) throws IOException
    {
      delete(target);
      Files.createDirectories(target.toPath());

      int javaFiles = 0;

      try (JarFile jf = new JarFile(sourceJar))
      {
        Enumeration<JarEntry> entries = jf.entries();

        while (entries.hasMoreElements())
        {
          JarEntry entry = entries.nextElement();
          if (entry.isDirectory() || entry.getName().startsWith("META-INF/") && !entry.getName().equals("META-INF/services/"))
          {
            continue;
          }

          File out = new File(target, entry.getName());
          Files.createDirectories(out.toPath().getParent());

          try (InputStream in = jf.getInputStream(entry))
          {
            Files.copy(in, out.toPath());
          }

          if (entry.getName().endsWith(".java"))
          {
            javaFiles++;
          }
        }
      }

      if (javaFiles == 0)
      {
        throw new IllegalArgumentException("Source plugin is empty: " + sourceJar);
      }
    }

    private static void jarDirectory(File directory, File jar) throws IOException
    {
      Files.createDirectories(jar.toPath().getParent());

      try (JarOutputStream out = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(jar))))
      {
        try (Stream<Path> stream = Files.walk(directory.toPath()))
        {
          stream.filter(Files::isRegularFile).sorted().forEach(path -> {
            try
            {
              String name = directory.toPath().relativize(path).toString().replace(File.separatorChar, '/');
              JarEntry entry = new JarEntry(name);
              entry.setTime(0L);
              out.putNextEntry(entry);

              Files.copy(path, out);
              out.closeEntry();
            }
            catch (IOException e)
            {
              throw new RuntimeException(e);
            }
          });
        }
      }
    }

    private static void generateJavadoc(File sourceDirectory, List<File> classpathJars, File work, String bundle, File output) throws Exception
    {
      DocumentationTool tool = ToolProvider.getSystemDocumentationTool();
      if (tool == null)
      {
        throw new IllegalStateException("No Javadoc tool is available in the running JDK");
      }

      File docs = new File(work, bundle + "/javadoc");
      delete(docs);
      Files.createDirectories(docs.toPath());
      List<File> sources = new ArrayList<>();

      try (Stream<Path> stream = Files.walk(sourceDirectory.toPath()))
      {
        stream.filter(p -> p.toString().endsWith(".java")).forEach(p -> sources.add(p.toFile()));
      }

      String classpath = classpathJars.stream().map(File::getAbsolutePath).reduce((a, b) -> a + File.pathSeparator + b).orElse("");
      StandardJavaFileManager fm = tool.getStandardFileManager(null, null, StandardCharsets.UTF_8);

      try
      {
        Iterable<? extends JavaFileObject> units = fm.getJavaFileObjectsFromFiles(sources);
        List<String> options = Arrays.asList("-quiet", "-Xdoclint:none", "-source", "11", "-classpath", classpath, "-d", docs.getAbsolutePath());

        Boolean ok = tool.getTask(new PrintWriter(System.err), fm, null, null, options, units).call();
        if (!Boolean.TRUE.equals(ok))
        {
          throw new IllegalArgumentException("Javadoc generation failed for " + bundle);
        }
      }
      finally
      {
        try
        {
          fm.close();
        }
        catch (IOException e)
        {
          // JDK 21 on Windows can report a stale ZipFS entry while closing a
          // file manager. The helper process exits immediately, releasing it.
          if (!(e instanceof FileSystemException) && !(e instanceof AccessDeniedException))
          {
            throw e;
          }
        }
      }

      jarDirectory(docs, output);
    }

    private static void validateClassifier(File jar, boolean sources, String bundle) throws IOException
    {
      if (!jar.isFile() || jar.length() == 0)
      {
        throw new IllegalArgumentException("Empty classifier JAR for " + bundle + ": " + jar);
      }

      try (JarFile jf = new JarFile(jar))
      {
        Set<String> entries = new LinkedHashSet<>();
        Enumeration<JarEntry> enumeration = jf.entries();
        while (enumeration.hasMoreElements())
        {
          JarEntry entry = enumeration.nextElement();
          if (!entry.isDirectory())
          {
            entries.add(entry.getName());
          }
        }

        if (sources)
        {
          if (entries.stream().noneMatch(name -> name.endsWith(".java")))
          {
            throw new IllegalArgumentException("Sources classifier contains no Java sources for " + bundle + ": " + jar);
          }
        }
        else
        {
          boolean hasPackage = entries.stream().anyMatch(name -> name.endsWith("/package-summary.html"));
          boolean hasClass = entries.stream().anyMatch(name -> name.contains("/") && name.endsWith(".html") && !name.endsWith("/package-summary.html")
              && !name.endsWith("/package-use.html") && !name.endsWith("/package-tree.html"));
          boolean hasJavadocIndex = entries.contains("index.html") && (entries.contains("element-list") || entries.contains("package-list"));
          if (!hasJavadocIndex || !hasPackage || !hasClass)
          {
            throw new IllegalArgumentException("Javadoc classifier lacks standard entry points or API pages for " + bundle + ": " + jar);
          }
        }
      }
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
        File versionDirectory = SourcesJavadocCommand.singleVersionDirectory(repository, bundle);
        String version = versionDirectory.getName();

        for (String classifier : CLASSIFIERS)
        {
          File jar = new File(versionDirectory, bundle + "-" + version + "-" + classifier + ".jar");
          if (!jar.isFile() || jar.length() == 0)
          {
            throw new IllegalArgumentException("Missing or empty classifier JAR: " + jar);
          }

          writeChecksums(jar);
        }
      }

      validate(repository);
      int expectedJars = BUNDLES.size() * CLASSIFIERS.size();
      int expectedSidecars = expectedJars * ALGORITHMS.size();
      System.out.println("Generated and validated " + expectedJars + " classifier JARs and " + expectedSidecars + " checksum files.");
    }

    private static void validate(File repository) throws Exception
    {
      int jars = 0;
      int sidecars = 0;

      for (String bundle : BUNDLES)
      {
        File versionDirectory = SourcesJavadocCommand.singleVersionDirectory(repository, bundle);
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
            if (!digest(jar, algorithm.getValue()).equals(actual))
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
                  .equals(Files.readString(checksum.toPath(), StandardCharsets.UTF_8).trim()))
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
        for (Map.Entry<String, String> entry : metadata.entrySet())
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

      SourcesJavadocCommand.delete(staging);
      copyMavenStaging(repository, staging);
      Files.copy(metadataFile.toPath(), new File(staging, "cdo-build-metadata.properties").toPath());
      validatePublicationScope(staging, false);

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
            .equals(Files.readString(checksum.toPath(), StandardCharsets.UTF_8).trim()))
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
      String name = segments[5];
      for (String artifactName : new String[] { base + ".pom", base + ".jar", base + "-sources.jar", base + "-javadoc.jar" })
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
      Map<String, String> versions = new LinkedHashMap<>();
      for (File pom : find(staging, ".pom"))
      {
        Element project = parse(pom).getDocumentElement();
        String group = text(project, "groupId");
        String artifact = text(project, "artifactId");
        String version = text(project, "version");
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
        throw new IllegalArgumentException("Staging must contain exactly the twelve approved CDO/Net4j coordinates; found " + versions.keySet());
      }

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
            throw new IllegalArgumentException("PACKAGE_FOR_CENTRAL requires a non-empty signature: " + signaturePath);
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
  }

  /**
   * Enforces the Maven Central publication policy at the publication boundary.
   * Validation and local staging deliberately do not use this policy.
   *
   * @author Eike Stepper
   */
  private static final class PublishPolicyCommand
  {
    private static final String VALIDATE_ONLY = "VALIDATE_ONLY";

    private static final String DRY_RUN = "DRY_RUN";

    private static final String PACKAGE_FOR_CENTRAL = "PACKAGE_FOR_CENTRAL";

    public static void execute(String[] args) throws Exception
    {
      if (args.length != 3)
      {
        throw new IllegalArgumentException("publish-policy requires build metadata properties and mode (VALIDATE_ONLY, DRY_RUN, or PACKAGE_FOR_CENTRAL)");
      }

      Map<String, String> metadata = readProperties(requireFile(new File(args[1]), "metadata properties"));
      String buildType = metadata.get("cdo.build.type");
      enforce(buildType, args[2]);
      System.out.println("Maven publishing policy " + args[2] + " accepted for build type " + buildType + ".");
    }

    public static void test()
    {
      for (String buildType : new String[] { "R", "S", "I" })
      {
        enforce(buildType, VALIDATE_ONLY);
      }

      for (String buildType : new String[] { "R", "S", "I" })
      {
        enforce(buildType, DRY_RUN);
      }

      for (String buildType : new String[] { "S", "I", "", null })
      {
        expectFailure(buildType, PACKAGE_FOR_CENTRAL);
      }

      expectFailure("X", VALIDATE_ONLY);
      expectFailure(null, PACKAGE_FOR_CENTRAL);
      expectFailure("R", "PUBLISH");
      System.out.println("Maven publishing policy tests passed for R, S, I, unknown, and missing build types.");
    }

    private static void enforce(String buildType, String mode)
    {
      if (!VALIDATE_ONLY.equals(mode) && !DRY_RUN.equals(mode) && !PACKAGE_FOR_CENTRAL.equals(mode))
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

      if (PACKAGE_FOR_CENTRAL.equals(mode) && !"R".equals(buildType))
      {
        throw new IllegalArgumentException("Maven Central packaging is restricted to type=R; build type is " + buildType);
      }
    }

    private static void expectFailure(String buildType, String mode)
    {
      try
      {
        enforce(buildType, mode);
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
      PublishPolicyCommand.enforce(buildType, mode);

      boolean signaturesRequired = "SIGNED".equals(args[4]);
      int artifacts = validateStaging(staging, signaturesRequired);
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

    private static int validateStaging(File staging, boolean signaturesRequired) throws Exception
    {
      int artifacts = AuditCommand.validatePublicationScope(staging, signaturesRequired);
      if (artifacts != 48)
      {
        throw new IllegalArgumentException("Expected exactly 48 approved Maven artifacts in staging; found " + artifacts);
      }

      return artifacts;
    }

    private static String value(Map<String, String> metadata, String key)
    {
      String value = metadata.get(key);
      return value == null ? "" : value;
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

  private static void verifyPackage(String[] args) throws Exception
  {
    if (args.length != 9)
    {
      throw new IllegalArgumentException(
          "verify-package requires staging, signed repository, ZIP, metadata, fingerprint, key file, GPG executable, and summary");
    }

    File staging = requireDirectory(args[1], "validated Maven staging");
    File signedRepository = requireDirectory(args[2], "signed local Maven repository");
    File zip = requireFile(new File(args[3]), "deployment ZIP");

    requireWorkspaceOutput(staging, "validated Maven staging");
    requireWorkspaceOutput(signedRepository, "signed local Maven repository");
    requireWorkspaceOutput(zip, "deployment ZIP");
    requireWorkspaceOutput(new File(args[4]), "build metadata");
    requireWorkspaceOutput(new File(args[8]), "package verification report");
    rejectSymbolicLinks(staging.toPath(), "validated Maven staging");
    rejectSymbolicLinks(signedRepository.toPath(), "signed local Maven repository");

    Map<String, String> metadata = readProperties(requireFile(new File(args[4]), "build metadata"));
    PublishPolicyCommand.enforce(metadata.get("cdo.build.type"), "PACKAGE_FOR_CENTRAL");

    String expectedFingerprint = args[5].replace(" ", "").toUpperCase();
    if (!expectedFingerprint.matches("(?:[0-9A-F]{40}|[0-9A-F]{64})"))
    {
      throw new IllegalArgumentException("Expected primary-key fingerprint must be a full 40- or 64-character hexadecimal fingerprint");
    }

    AuditCommand.validatePublicationScope(staging, true);
    File keyFile = requireFile(new File(args[6]), "Jenkins signing-key credential");
    File orgDirectory = requireDirectory(new File(staging, "org").getPath(), "staging/org");
    List<File> files = regularFiles(orgDirectory);
    List<File> artifacts = new ArrayList<>();

    for (File file : files)
    {
      if (file.getName().endsWith(".jar") || file.getName().endsWith(".pom"))
      {
        artifacts.add(file);
      }
    }

    if (artifacts.size() != 48)
    {
      throw new IllegalArgumentException("Expected 48 main, POM, sources, and Javadoc artifacts; found " + artifacts.size());
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
      bundles.add(bundle);

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

    if (bundles.size() != BUNDLES.size() || !bundles.equals(new LinkedHashSet<>(BUNDLES)) || coordinates.size() != 12)
    {
      throw new IllegalArgumentException("Maven repository does not contain exactly one version of each of the twelve approved bundles: " + coordinates);
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

    if (!expectedSignatures.equals(actualSignatures) || actualSignatures.size() != 48)
    {
      throw new IllegalArgumentException("Deployment repository must contain exactly the 48 expected detached signatures");
    }

    verifySignatures(args[7], keyFile, expectedFingerprint, artifacts, staging);
    verifyDeploymentZip(zip, staging, actualFiles);

    String sha256 = ChecksumsCommand.digest(zip, "SHA-256");

    try (PrintWriter out = new PrintWriter(new FileOutputStream(args[8], true), true, StandardCharsets.UTF_8))
    {
      out.println("signatures.verified=48");
      out.println("signature.verification=VALIDATED");
      out.println("signing.primary.fingerprint=" + expectedFingerprint);
      out.println("deployment.zip=" + zip.getName());
      out.println("deployment.zip.sha256=" + sha256);
      out.println("central.publication.performed=false");
    }

    System.out.println("Verified 48 OpenPGP signatures, staged repository bytes, checksums, and exact ZIP inventory. Publication performed=false.");
  }

  private static void verifySignatures(String gpg, File secretKey, String expectedFingerprint, List<File> artifacts, File staging) throws Exception
  {
    Path work = Files.createTempDirectory(staging.toPath().getParent(), "cdo-pgp-verify-");
    securePath(work, true);

    try
    {
      Path importHome = Files.createDirectory(work.resolve("import"));
      Path verifyHome = Files.createDirectory(work.resolve("verify"));
      securePath(importHome, true);
      securePath(verifyHome, true);
      Path publicKey = work.resolve("expected-public-key.gpg");

      gpg(gpg, importHome, "--import", secretKey.getAbsolutePath());
      Set<String> importedPrimaryKeys = primaryFingerprints(gpg, importHome);
      if (!importedPrimaryKeys.contains(expectedFingerprint))
      {
        throw new IllegalArgumentException("Jenkins signing-key credential does not contain the configured primary fingerprint");
      }

      gpg(gpg, importHome, "--output", publicKey.toString(), "--export", expectedFingerprint);
      if (!Files.isRegularFile(publicKey) || Files.size(publicKey) == 0)
      {
        throw new IllegalArgumentException("Unable to export public key for the configured primary fingerprint");
      }

      securePath(publicKey, false);
      stopGpgAgent(gpg, importHome);

      SourcesJavadocCommand.delete(importHome.toFile());
      gpg(gpg, verifyHome, "--import", publicKey.toString());
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
      if (Files.isDirectory(work.resolve("import")))
      {
        stopGpgAgent(gpg, work.resolve("import"));
      }

      if (Files.isDirectory(work.resolve("verify")))
      {
        stopGpgAgent(gpg, work.resolve("verify"));
      }

      SourcesJavadocCommand.delete(work.toFile());
    }
  }

  private static void stopGpgAgent(String gpg, Path home) throws Exception
  {
    Path configuredGpg = Paths.get(gpg);
    Path gpgconfPath = configuredGpg.getParent() == null ? Paths.get("gpgconf")
        : configuredGpg.toAbsolutePath().normalize().resolveSibling(isWindows() ? "gpgconf.exe" : "gpgconf");

    ProcessBuilder builder = new ProcessBuilder(gpgconfPath.toString(), "--homedir", home.toString(), "--kill", "gpg-agent");
    builder.redirectErrorStream(true);
    builder.redirectOutput(ProcessBuilder.Redirect.DISCARD);

    Process process = builder.start();
    if (!process.waitFor(10, TimeUnit.SECONDS))
    {
      process.destroyForcibly();
      throw new IllegalArgumentException("Timed out stopping the isolated GnuPG agent");
    }

    if (process.exitValue() != 0)
    {
      throw new IllegalArgumentException("Unable to stop the isolated GnuPG agent");
    }
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
      throw new IllegalArgumentException("GnuPG is required for PACKAGE_FOR_CENTRAL signature verification", e);
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

    Path work = workspace == null || workspace.isBlank() ? Files.createTempDirectory("cdo-signature-tests-")
        : Files.createTempDirectory(Paths.get(workspace), "cdo-signature-tests-");
    Path firstHome = work.resolve("first-key");
    Path secondHome = work.resolve("second-key");

    securePath(work, true);

    try
    {
      Files.createDirectory(firstHome);
      Files.createDirectory(secondHome);

      securePath(firstHome, true);
      securePath(secondHome, true);

      gpg(gpgExecutable, firstHome, "--pinentry-mode", "loopback", "--passphrase", "", "--quick-generate-key", "CDO temporary signing key", "rsa2048", "sign",
          "1d");
      gpg(gpgExecutable, secondHome, "--pinentry-mode", "loopback", "--passphrase", "", "--quick-generate-key", "CDO wrong test key", "rsa2048", "sign", "1d");

      String firstFingerprint = primaryFingerprints(gpgExecutable, firstHome).iterator().next();
      String secondFingerprint = primaryFingerprints(gpgExecutable, secondHome).iterator().next();

      File firstSecretKey = exportSecretKey(gpgExecutable, firstHome, firstFingerprint, work.resolve("first-secret-key.gpg"));
      File secondSecretKey = exportSecretKey(gpgExecutable, secondHome, secondFingerprint, work.resolve("second-secret-key.gpg"));

      File staging = work.resolve("staging").toFile();
      File signedRepository = work.resolve("signed-repository").toFile();
      File metadata = work.resolve("metadata.properties").toFile();
      File summary = work.resolve("summary.properties").toFile();
      File zip = work.resolve("deployment.zip").toFile();

      Files.createDirectories(staging.toPath());
      Files.createDirectories(signedRepository.toPath());
      Files.writeString(metadata.toPath(), "cdo.build.type=R\ncdo.drop.id=R20990101-0000\ncdo.git.commit=deadbeef\n", StandardCharsets.UTF_8);
      Files.writeString(summary.toPath(), "publishing.mode=PACKAGE_FOR_CENTRAL\n", StandardCharsets.UTF_8);

      createSignedFixture(gpgExecutable, firstHome, firstFingerprint, staging, signedRepository);
      createDeploymentZip(staging, zip, "");

      String[] valid = packageArguments(staging, signedRepository, zip, metadata, firstFingerprint, firstSecretKey, gpgExecutable, summary);
      verifyPackage(valid);

      File artifact = new File(staging, "org/eclipse/cdo/" + BUNDLES.get(0) + "/1.0.0/" + BUNDLES.get(0) + "-1.0.0.jar");
      File deployedArtifact = new File(signedRepository, "org/eclipse/cdo/" + BUNDLES.get(0) + "/1.0.0/" + BUNDLES.get(0) + "-1.0.0.jar");
      byte[] originalArtifact = Files.readAllBytes(artifact.toPath());

      Files.write(artifact.toPath(), Arrays.copyOf(originalArtifact, originalArtifact.length + 1));
      Files.write(deployedArtifact.toPath(), Files.readAllBytes(artifact.toPath()));

      ChecksumsCommand.writeChecksums(artifact);

      createDeploymentZip(staging, zip, "");
      expectPackageFailure(packageArguments(staging, signedRepository, zip, metadata, firstFingerprint, firstSecretKey, gpgExecutable, summary),
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
      expectPackageFailure(packageArguments(staging, signedRepository, zip, metadata, firstFingerprint, firstSecretKey, gpgExecutable, summary),
          "GnuPG operation failed", "corrupted signature");

      Files.write(signature.toPath(), originalSignature);
      Files.write(deployedSignature.toPath(), originalSignature);

      createDeploymentZip(staging, zip, "");
      expectPackageFailure(packageArguments(staging, signedRepository, zip, metadata, secondFingerprint, secondSecretKey, gpgExecutable, summary),
          "GnuPG operation failed", "signature from a wrong key");
      expectPackageFailure(packageArguments(staging, signedRepository, zip, metadata, secondFingerprint, firstSecretKey, gpgExecutable, summary),
          "does not contain the configured primary fingerprint", "wrong expected key fingerprint");
      expectPackageFailure(packageArguments(staging, signedRepository, zip, metadata, "invalid-fingerprint", firstSecretKey, gpgExecutable, summary),
          "full 40- or 64-character hexadecimal fingerprint", "malformed expected fingerprint");
      expectPackageFailure(
          packageArguments(staging, signedRepository, zip, metadata, firstFingerprint, work.resolve("missing-key.gpg").toFile(), gpgExecutable, summary),
          "Jenkins signing-key credential", "missing signing key file");

      File checksum = new File(artifact.getPath() + ".sha256");
      byte[] correctChecksum = Files.readAllBytes(checksum.toPath());
      Files.writeString(checksum.toPath(), "0".repeat(64), StandardCharsets.US_ASCII);

      createDeploymentZip(staging, zip, "");
      expectPackageFailure(packageArguments(staging, signedRepository, zip, metadata, firstFingerprint, firstSecretKey, gpgExecutable, summary),
          "Missing or invalid checksum", "mismatched checksum");
      Files.write(checksum.toPath(), correctChecksum);

      File unexpectedSignature = new File(artifact.getPath() + ".unexpected.asc");
      Files.writeString(unexpectedSignature.toPath(), "-----BEGIN PGP SIGNATURE-----\nextra\n", StandardCharsets.US_ASCII);
      createDeploymentZip(staging, zip, "");
      expectPackageFailure(packageArguments(staging, signedRepository, zip, metadata, firstFingerprint, firstSecretKey, gpgExecutable, summary),
          "Unexpected file in deployment repository", "unexpected signature");
      Files.delete(unexpectedSignature.toPath());

      File missingSignature = new File(signature.getPath());
      Files.delete(missingSignature.toPath());
      createDeploymentZip(staging, zip, "");
      expectPackageFailure(packageArguments(staging, signedRepository, zip, metadata, firstFingerprint, firstSecretKey, gpgExecutable, summary),
          "Missing, malformed, or non-armored signature", "missing signature");
      Files.write(missingSignature.toPath(), originalSignature);

      Files.delete(artifact.toPath());
      createDeploymentZip(staging, zip, "");
      expectPackageFailure(packageArguments(staging, signedRepository, zip, metadata, firstFingerprint, firstSecretKey, gpgExecutable, summary), "Expected 48",
          "missing Maven artifact");
      Files.write(artifact.toPath(), originalArtifact);
      ChecksumsCommand.writeChecksums(artifact);

      createDeploymentZip(staging, zip, "unexpected-wrapper/");
      expectPackageFailure(packageArguments(staging, signedRepository, zip, metadata, firstFingerprint, firstSecretKey, gpgExecutable, summary),
          "inventory mismatch", "enclosing ZIP directory");

      createDeploymentZip(staging, zip, "");
      verifyPackage(packageArguments(staging, signedRepository, zip, metadata, firstFingerprint, firstSecretKey, gpgExecutable, summary));
      System.out.println(
          "Disposable GnuPG signing tests passed: valid package, changed bytes, corrupt signature, wrong key/fingerprint, missing key/signature/artifact, and invalid ZIP layout.");
    }
    finally
    {
      if (Files.isDirectory(firstHome))
      {
        stopGpgAgent(gpgExecutable, firstHome);
      }

      if (Files.isDirectory(secondHome))
      {
        stopGpgAgent(gpgExecutable, secondHome);
      }

      SourcesJavadocCommand.delete(work.toFile());
    }
  }

  private static File exportSecretKey(String gpg, Path home, String fingerprint, Path destination) throws Exception
  {
    gpg(gpg, home, "--output", destination.toString(), "--export-secret-keys", fingerprint);
    securePath(destination, false);
    return destination.toFile();
  }

  private static String[] packageArguments(File staging, File signedRepository, File zip, File metadata, String fingerprint, File secretKey, String gpg,
      File summary)
  {
    return new String[] { "verify-package", staging.getPath(), signedRepository.getPath(), zip.getPath(), metadata.getPath(), fingerprint, secretKey.getPath(),
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
    SourcesJavadocCommand.delete(target.toFile());
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
      metadataValues.put("cdo.drop.id", "R20990101-0000");
      metadataValues.put("cdo.git.commit", "deadbeef");
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

      VerifyCommand.execute(new String[] { "verify", repository.toString(), metadata.toString() });
      AuditCommand.execute(new String[] { "audit", repository.toString(), metadata.toString(), report.toString(), staging.toString() });
      PublishCheckCommand.execute(new String[] { "publish-check", staging.toString(), metadata.toString(), "VALIDATE_ONLY", "" });
      assertPublicationStaging(staging, false);

      for (File artifact : publicationArtifacts(staging.toFile()))
      {
        Files.writeString(Path.of(artifact.getPath() + ".asc"), "test signature");
      }

      PublishCheckCommand.execute(new String[] { "publish-check", staging.toString(), metadata.toString(), "PACKAGE_FOR_CENTRAL", "SIGNED" });
      assertPublicationStaging(staging, true);

      Path stagedH2 = staging.resolve("com/h2database/h2/2.0.0/h2-2.0.0.pom");
      Files.createDirectories(stagedH2.getParent());
      Files.writeString(stagedH2, Files.readString(h2Pom));
      expectPublicationScopeFailure(staging.toFile(), "Unapproved Maven publication coordinate");
      expectPublishCheckFailure(staging.toFile(), metadata.toFile(), "VALIDATE_ONLY", "");
      expectPublishCheckFailure(staging.toFile(), metadata.toFile(), "PACKAGE_FOR_CENTRAL", "SIGNED");
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
          "Publication scope tests passed: H2 resolves in raw aggregation, remains unchanged, and is excluded from VALIDATE_ONLY and PACKAGE_FOR_CENTRAL staging.");
    }
    finally
    {
      SourcesJavadocCommand.delete(testRoot.toFile());
    }
  }

  private static void createPublicationScopeFixture(Path repository) throws Exception
  {
    for (String bundle : BUNDLES)
    {
      Path versionDirectory = repository.resolve("org/eclipse/cdo/" + bundle + "/1.0.0");
      Files.createDirectories(versionDirectory);

      String dependency = BUNDLES.get(7).equals(bundle)
          ? "<dependencies><dependency><groupId>com.h2database</groupId><artifactId>h2</artifactId><version>2.0.0</version></dependency></dependencies>"
          : "";
      String pomContent = "<project><modelVersion>4.0.0</modelVersion><groupId>org.eclipse.cdo</groupId><artifactId>" + bundle
          + "</artifactId><version>1.0.0</version><name>" + bundle + "</name><description>Fixture " + bundle + "</description>" + dependency + "</project>";
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
      return paths.filter(Files::isRegularFile).map(Path::toFile).filter(file -> file.getName().endsWith(".pom") || file.getName().endsWith(".jar"))
          .sorted(Comparator.comparing(File::getPath)).collect(Collectors.toList());
    }
  }

  private static void assertPublicationStaging(Path staging, boolean signaturesPresent) throws Exception
  {
    List<File> artifacts = publicationArtifacts(staging.toFile());
    if (artifacts.size() != 48 || Files.exists(staging.resolve("com/h2database")))
    {
      throw new AssertionError("Publication staging must contain exactly 48 CDO Maven artifacts and no H2 files");
    }

    try (Stream<Path> paths = Files.walk(staging.resolve("org")))
    {
      List<Path> files = paths.filter(Files::isRegularFile).collect(Collectors.toList());
      int expectedFiles = 240 + (signaturesPresent ? 48 : 0);
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

  private static void expectPublishCheckFailure(File staging, File metadata, String mode, String signatures) throws Exception
  {
    try
    {
      PublishCheckCommand.execute(new String[] { "publish-check", staging.getPath(), metadata.getPath(), mode, signatures });
    }
    catch (IllegalArgumentException e)
    {
      if (e.getMessage().contains("Unapproved Maven publication coordinate"))
      {
        return;
      }

      throw e;
    }

    throw new AssertionError("Expected publish-check to reject out-of-scope Maven artifacts for " + mode);
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
      if (!requireSafeWorkspaceChild(output.toString(), workspace.toString(), "test output").equals(output))
      {
        throw new AssertionError("Safe workspace descendant was rejected or changed");
      }

      expectUnsafeWorkspacePath("", workspace.toString());
      expectUnsafeWorkspacePath(workspace.toString(), workspace.toString());
      expectUnsafeWorkspacePath(outside.toString(), Paths.get("/").toString());
      expectUnsafeWorkspacePath(outside.toString(), workspace.toString());
      expectUnsafeWorkspacePath(workspace.resolve("../outside").toString(), workspace.toString());
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
      SourcesJavadocCommand.delete(testRoot.toFile());
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

  private static Document parse(File file) throws Exception
  {
    return parse(new FileInputStream(file), file.getPath());
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

    public File materializePlugins(File target) throws IOException
    {
      if ("file".equalsIgnoreCase(uri.getScheme()))
      {
        return requireDirectory(Path.of(uri).resolve("plugins").toString(), "drop/plugins");
      }

      requireWorkspaceOutput(target, "downloaded drop plugin directory");
      if (Files.exists(target.toPath(), LinkOption.NOFOLLOW_LINKS))
      {
        rejectSymbolicLinks(target.toPath(), "downloaded drop plugin directory");
      }

      Files.createDirectories(target.toPath());
      String listing;
      try (InputStream in = open("plugins/"))
      {
        listing = new String(in.readAllBytes(), StandardCharsets.UTF_8);
      }

      Pattern href = Pattern.compile("href=[\\\"']([^\\\"']+\\.jar)[\\\"']", Pattern.CASE_INSENSITIVE);
      Matcher matcher = href.matcher(listing);
      int downloaded = 0;
      while (matcher.find())
      {
        String name = matcher.group(1);
        int slash = name.lastIndexOf('/');
        name = slash < 0 ? name : name.substring(slash + 1);
        String fileName = name;
        boolean needed = BUNDLES.stream().anyMatch(bundle -> fileName.startsWith(bundle + "-") || fileName.startsWith(bundle + "_")
            || fileName.startsWith(bundle + ".source") || fileName.startsWith(bundle + "_source"));
        if (!needed)
        {
          continue;
        }

        Path destination = target.toPath().resolve(name);
        try (InputStream in = open("plugins/" + name))
        {
          Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
        }
        downloaded++;
      }

      if (downloaded == 0)
      {
        throw new IOException(
            "No required plugin JARs were found at " + uri.resolve("plugins/") + "; verify that the drop URL exposes a browsable plugins directory");
      }

      return target;
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
   * Reports the raw Maven classifiers generated by CBI before the CDO overlay
   * or the custom source/Javadoc generation target changes the repository.
   *
   * @author Eike Stepper
   */
  private static final class NativeClassifierDiagnosticsCommand
  {
    public static void execute(String[] args) throws Exception
    {
      if (args.length != 2)
      {
        throw new IllegalArgumentException("native-classifier-diagnostics requires the raw Maven repository");
      }

      File repository = requireDirectory(args[1], "raw Maven repository");
      rejectSymbolicLinks(repository.toPath(), "raw Maven repository");

      for (String artifactId : BUNDLES)
      {
        File artifactDirectory = new File(repository, "org/eclipse/cdo/" + artifactId);
        File[] versions = artifactDirectory.listFiles(File::isDirectory);
        if (versions == null || versions.length == 0)
        {
          reportMissing(artifactId);
          continue;
        }

        Arrays.sort(versions, Comparator.comparing(File::getName));
        for (File versionDirectory : versions)
        {
          reportArtifact(artifactId, versionDirectory);
        }
      }
    }

    private static void reportMissing(String artifactId)
    {
      System.out.println(
          "org.eclipse.cdo:" + artifactId + ":<missing>\tbinary=missing\tpom=missing\tsources=missing\tjavadoc=missing\tjavadocEntries=[]\tREADME=<missing>");
    }

    private static void reportArtifact(String artifactId, File versionDirectory) throws Exception
    {
      String version = versionDirectory.getName();
      String baseName = artifactId + "-" + version;
      File binary = new File(versionDirectory, baseName + ".jar");
      File pom = new File(versionDirectory, baseName + ".pom");
      File sources = new File(versionDirectory, baseName + "-sources.jar");
      File javadoc = new File(versionDirectory, baseName + "-javadoc.jar");

      String coordinate = "org.eclipse.cdo:" + artifactId + ":" + version;
      String pomStatus = status(pom);
      if ("present".equals(pomStatus))
      {
        try
        {
          Element project = parse(pom).getDocumentElement();
          coordinate = text(project, "groupId") + ":" + text(project, "artifactId") + ":" + text(project, "version");
        }
        catch (Exception e)
        {
          pomStatus = "malformed";
        }
      }

      JavadocContents javadocContents = inspectJavadoc(javadoc);
      System.out.println(coordinate + "\tbinary=" + status(binary) + "\tpom=" + pomStatus + "\tsources=" + status(sources) + "\tjavadoc="
          + javadocContents.status + "\tjavadocEntries=" + javadocContents.entries + "\tREADME=" + javadocContents.readme);
    }

    private static String status(File file)
    {
      if (!file.isFile())
      {
        return "missing";
      }

      return file.length() == 0 ? "empty" : "present";
    }

    private static JavadocContents inspectJavadoc(File javadoc)
    {
      if (!javadoc.isFile())
      {
        return new JavadocContents("missing", "[]", "<missing>");
      }

      if (javadoc.length() == 0)
      {
        return new JavadocContents("empty", "[]", "<missing>");
      }

      try (ZipFile zip = new ZipFile(javadoc))
      {
        List<String> entries = new ArrayList<>();
        Enumeration<? extends ZipEntry> enumeration = zip.entries();

        while (enumeration.hasMoreElements())
        {
          ZipEntry entry = enumeration.nextElement();
          if (!entry.isDirectory())
          {
            entries.add(entry.getName());
          }
        }

        entries.sort(String::compareTo);
        ZipEntry readme = zip.getEntry("README.txt");
        String readmeContent = "<missing>";

        if (readme != null)
        {
          try (InputStream input = zip.getInputStream(readme))
          {
            readmeContent = escape(new String(input.readAllBytes(), StandardCharsets.UTF_8));
          }
        }

        String status = entries.isEmpty() ? "empty" : "present";
        return new JavadocContents(status, entries.toString(), readmeContent);
      }
      catch (IOException e)
      {
        return new JavadocContents("invalid", "[]", "<unreadable>");
      }
    }

    private static String escape(String value)
    {
      return "\"" + value.replace("\\", "\\\\").replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t").replace("\"", "\\\"") + "\"";
    }

    /**
     * Captures the deterministic summary of one native Javadoc classifier.
     *
     * @author Eike Stepper
     */
    private static final class JavadocContents
    {
      private final String status;

      private final String entries;

      private final String readme;

      public JavadocContents(String status, String entries, String readme)
      {
        this.status = status;
        this.entries = entries;
        this.readme = readme;
      }
    }
  }
}
