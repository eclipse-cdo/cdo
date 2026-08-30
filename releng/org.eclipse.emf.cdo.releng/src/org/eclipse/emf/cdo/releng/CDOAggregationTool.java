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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
          "Usage: prepare|overlay|verify|consumer-versions|sources-javadoc|checksums|audit|publish-check|publish-policy|policy-test ...");
    }

    switch (args[0])
    {
    case "prepare":
      PrepareCommand.execute(args);
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

    case "checksums":
      ChecksumsCommand.execute(args);
      break;

    case "audit":
      AuditCommand.execute(args);
      break;

    case "publish-check":
      PublishCheckCommand.execute(args);
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

      File drop = requireDirectory(args[1], "drop");
      File buildInfo = requireFile(new File(drop, "build-info.xml"), "build-info.xml");
      File setup = requireFile(new File(drop, "tp-macro.setup"), "tp-macro.setup");

      Document info = parse(buildInfo);
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

      LinkedHashSet<String> repositories = extractRepositories(setup);
      if (repositories.isEmpty())
      {
        throw new IllegalArgumentException("No repository locations found in " + setup);
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

      Map<String, String> values = readProperties(requireFile(new File(args[2]), "metadata properties"));
      Map<String, String> publishing = readProperties(requireFile(new File(args[3]), "publishing metadata"));

      File root = requireDirectory(args[1], "aggregated repository");
      List<File> poms = find(root, ".pom");
      if (poms.isEmpty())
      {
        throw new IllegalArgumentException("No generated POMs found under " + root);
      }

      for (File file : poms)
      {
        Document document = parse(file);
        Element project = document.getDocumentElement();

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
      }

      System.out.println("Added " + values.size() + " traceability properties to " + poms.size() + " POMs.");
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
    private static final Pattern FORBIDDEN = Pattern.compile(
        "(?:com\\.h2database|org\\.eclipse\\.net4j\\.db\\.(?:derby|hsqldb|mysql|oracle|postgresql|db2)|org\\.eclipse\\.emf\\.cdo\\.(?:ui|tests|examples))");

    public static void execute(String[] args) throws Exception
    {
      Map<String, String> values = readProperties(requireFile(new File(args[2]), "metadata properties"));

      File root = requireDirectory(args[1], "aggregated repository");
      List<File> poms = find(root, ".pom");

      Set<String> expected = new LinkedHashSet<>();

      for (String bundle : BUNDLES)
      {
        expected.add("org.eclipse.cdo:" + bundle);
      }

      Set<String> actual = new LinkedHashSet<>();

      for (File file : poms)
      {
        Document doc = parse(file);
        Element project = doc.getDocumentElement();

        String coordinate = text(project, "groupId") + ":" + text(project, "artifactId");
        actual.add(coordinate);

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

      if (!expected.equals(actual))
      {
        throw new IllegalArgumentException("Generated coordinates differ. Expected=" + expected + " actual=" + actual);
      }

      for (File file : findAll(root))
      {
        if (FORBIDDEN.matcher(file.getPath()).find())
        {
          throw new IllegalArgumentException("Forbidden artifact in output: " + file);
        }
      }

      System.out.println("Verified " + poms.size() + " coordinates, traceability properties, and excluded-artifact guard.");
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

      File dropPlugins = requireDirectory(new File(args[1], "plugins").getPath(), "drop/plugins");
      File repository = requireDirectory(args[2], "Maven repository");
      File work = new File(args[3]);
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
        boolean content = jf.entries().hasMoreElements();
        if (!content)
        {
          throw new IllegalArgumentException("Classifier JAR contains no entries: " + jar);
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
      File metadataFile = requireFile(new File(args[2]), "metadata properties");

      Map<String, String> metadata = readProperties(metadataFile);
      List<String> findings = new ArrayList<>();
      List<File> poms = find(repository, ".pom");
      Set<String> coordinates = new LinkedHashSet<>();
      Map<String, Integer> dependencyClasses = new LinkedHashMap<>();

      for (File pom : poms)
      {
        Document document = parse(pom);
        Element project = document.getDocumentElement();

        String group = text(project, "groupId");
        String artifact = text(project, "artifactId");
        String version = text(project, "version");
        if (!"org.eclipse.cdo".equals(group) || artifact.isEmpty() || version.isEmpty())
        {
          continue;
        }

        coordinates.add(group + ":" + artifact + ":" + version);

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
        if (dependencies.contains("com.h2database") //
            || dependencies.matches(".*org\\.eclipse\\.net4j\\.db\\.(derby|mysql|oracle|postgresql|hsqldb|db2).*"))
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

      File staging = new File(args[4]);
      SourcesJavadocCommand.delete(staging);
      copyMavenStaging(repository, staging);
      Files.copy(metadataFile.toPath(), new File(staging, "cdo-build-metadata.properties").toPath());

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
          String name = path.getFileName().toString();
          if (name.startsWith("artifacts.jar") || name.startsWith("content.jar") || name.startsWith("aggregate.jar"))
          {
            return false;
          }

          return name.endsWith(".jar") || name.endsWith(".pom") || name.endsWith(".md5") || name.endsWith(".sha1") || name.endsWith(".sha256")
              || name.endsWith(".sha512") || "maven-metadata.xml".equals(name);
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

    private static final String PUBLISH = "PUBLISH";

    public static void execute(String[] args) throws Exception
    {
      if (args.length != 3)
      {
        throw new IllegalArgumentException("publish-policy requires build metadata properties and mode (VALIDATE_ONLY, DRY_RUN, or PUBLISH)");
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
        expectFailure(buildType, PUBLISH);
      }

      expectFailure("X", VALIDATE_ONLY);
      expectFailure(null, PUBLISH);
      System.out.println("Maven publishing policy tests passed for R, S, I, unknown, and missing build types.");
    }

    private static void enforce(String buildType, String mode)
    {
      if (!VALIDATE_ONLY.equals(mode) && !DRY_RUN.equals(mode) && !PUBLISH.equals(mode))
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

      if (PUBLISH.equals(mode) && !"R".equals(buildType))
      {
        throw new IllegalArgumentException("Maven Central PUBLISH is restricted to type=R; build type is " + buildType);
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
   * mode, and optional uploader identifier. No network operation is performed.
   *
   * @author Eike Stepper
   */
  private static final class PublishCheckCommand
  {
    private static final String[] CHECKSUMS = { ".md5", ".sha1", ".sha256", ".sha512" };

    static void execute(String[] args) throws Exception
    {
      if (args.length != 5)
      {
        throw new IllegalArgumentException("publish-check requires staging, metadata, mode, and uploader identifier");
      }

      File staging = requireDirectory(args[1], "staging directory");
      Map<String, String> metadata = readProperties(requireFile(new File(args[2]), "metadata properties"));
      String mode = args[3];
      String buildType = metadata.get("cdo.build.type");
      PublishPolicyCommand.enforce(buildType, mode);

      int artifacts = validateStaging(staging, false);
      File summary = new File(staging, "maven-publishing-summary.properties");

      try (PrintWriter out = new PrintWriter(summary, StandardCharsets.UTF_8.name()))
      {
        out.println("cdo.build.type=" + value(metadata, "cdo.build.type"));
        out.println("cdo.drop.id=" + value(metadata, "cdo.drop.id"));
        out.println("cdo.git.commit=" + value(metadata, "cdo.git.commit"));
        out.println("publishing.mode=" + mode);
        out.println("staging.artifacts=" + artifacts);
        out.println("upload.performed=false");
      }

      if (PublishPolicyCommand.PUBLISH.equals(mode))
      {
        if (args[4].isBlank())
        {
          throw new IllegalArgumentException("PUBLISH requires an explicitly configured uploader; none is configured");
        }

        requireEnvironment("MAVEN_CENTRAL_USERNAME");
        requireEnvironment("MAVEN_CENTRAL_PASSWORD");
        requireEnvironment("MAVEN_GPG_KEY_ID");
        requireEnvironment("MAVEN_GPG_PASSPHRASE");
        validateStaging(staging, true);

        throw new IllegalArgumentException("PGP/upload execution is not enabled in this prototype; no upload was performed");
      }

      System.out.println("Validated " + artifacts + " staging artifacts for " + mode + " (upload disabled). Summary: " + summary);
    }

    private static int validateStaging(File staging, boolean signaturesRequired) throws Exception
    {
      List<File> artifacts = new ArrayList<>();
      try (Stream<Path> files = Files.walk(staging.toPath()))
      {
        files.filter(Files::isRegularFile).map(Path::toFile).filter(file -> file.getName().endsWith(".jar") || file.getName().endsWith(".pom"))
            .forEach(artifacts::add);
      }

      if (artifacts.isEmpty())
      {
        throw new IllegalArgumentException("Staging directory contains no Maven JAR or POM artifacts: " + staging);
      }

      for (File artifact : artifacts)
      {
        if (artifact.length() == 0)
        {
          throw new IllegalArgumentException("Empty staging artifact: " + artifact);
        }

        for (String checksum : CHECKSUMS)
        {
          File sidecar = new File(artifact.getPath() + checksum);
          if (!sidecar.isFile() || sidecar.length() == 0)
          {
            throw new IllegalArgumentException("Missing or empty checksum for staging artifact: " + sidecar);
          }
        }

        if (signaturesRequired)
        {
          File signature = new File(artifact.getPath() + ".asc");
          if (!signature.isFile() || signature.length() == 0)
          {
            throw new IllegalArgumentException("PUBLISH requires a non-empty PGP signature: " + signature);
          }
        }
      }

      return artifacts.size();
    }

    private static String value(Map<String, String> metadata, String key)
    {
      String value = metadata.get(key);
      return value == null ? "" : value;
    }

    private static void requireEnvironment(String name)
    {
      if (System.getenv(name) == null || System.getenv(name).isBlank())
      {
        throw new IllegalArgumentException("Missing Jenkins credential environment variable: " + name);
      }
    }
  }

  private static LinkedHashSet<String> extractRepositories(File setup) throws Exception
  {
    LinkedHashSet<String> result = new LinkedHashSet<>();

    Document document = parse(setup);
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
  private static String validationLocation(String url, String dropID, File drop)
  {
    // See promoter.TPMacroSetup.FIXED_TOKEN
    String FIXED_TOKEN = "https://download.eclipse.org/modeling/emf/cdo/updates";
    String resolved = FIXED_TOKEN.equals(url) ? "https://download.eclipse.org/modeling/emf/cdo/drops/" + dropID : url;
    String promotedDrop = "https://download.eclipse.org/modeling/emf/cdo/drops/" + dropID;
    return promotedDrop.equals(resolved) ? drop.toURI().toString() : resolved;
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
    DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
    f.setNamespaceAware(true);
    f.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    f.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    f.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

    DocumentBuilder b = f.newDocumentBuilder();

    try (InputStream in = new FileInputStream(file))
    {
      return b.parse(in);
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
}
