/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.doc;

import org.eclipse.osgi.util.ManifestElement;

import org.osgi.framework.BundleException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public class AssembleScripts
{
  private static final String EXPORT_PACKAGE = "Export-Package";

  private static final AntLib ANTLIB = new AntLib();

  private static final Pattern PACKAGE_INFO_PATTERN = Pattern.compile(".*<body[^>]*>\\s*(.*)\\s*<p>\\s*</body>.*",
      Pattern.MULTILINE | Pattern.DOTALL);

  private static final String NL = System.getProperty("line.separator");

  private static File workspace;

  private static File plugins;

  private static File releng;

  public static void main(String[] args) throws Exception
  {
    try
    {
      String workspacePath = args.length == 0 ? "../.." : args[0];
      workspace = new File(workspacePath).getCanonicalFile();
      plugins = new File(workspace, "plugins");
      releng = new File(plugins, "org.eclipse.emf.cdo.releng.doc");

      for (File plugin : plugins.listFiles())
      {
        if (plugin.isDirectory())
        {
          Properties buildProperties = getProperties(new File(plugin, "build.properties"));
          String javadocProject = buildProperties.getProperty("doc.project");
          if (javadocProject != null)
          {
            Set<String> excludedPackages = getExcludedPackages(buildProperties);
            assembleJavaDocOptions(plugin, javadocProject, excludedPackages);
          }
        }
      }

      for (JavaDoc javaDoc : ANTLIB.getJavaDocs())
      {
        assembleArticleOptions(javaDoc);

        javaDoc.generateAnt();
        javaDoc.generateToc();
      }

      ANTLIB.generate();
      ANTLIB.generateDebug();
      System.out.println();

      for (JavaDoc javaDoc : ANTLIB.getJavaDocs())
      {
        for (SourcePlugin sourcePlugin : javaDoc.getSourcePlugins())
        {
          sourcePlugin.validatePackageInfos();
        }
      }

      System.out.println();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      throw ex;
    }
  }

  private static void assembleJavaDocOptions(File plugin, String javadocProject, Set<String> excludedPackages)
      throws IOException, BundleException
  {
    SourcePlugin sourcePlugin = ANTLIB.getSourcePlugin(plugin.getName());
    Set<String> packageNames = sourcePlugin.getPackageNames();

    JavaDoc javaDoc = ANTLIB.getJavaDoc(javadocProject);
    javaDoc.getSourcePlugins().add(sourcePlugin);

    Manifest manifest = getManifest(plugin);
    for (ManifestElement manifestElement : getManifestElements(manifest))
    {
      String packageName = manifestElement.getValue().trim();
      if (isPublic(manifestElement) && !excludedPackages.contains(packageName))
      {
        javaDoc.getSourceFolders().add(plugin.getName() + "/src/" + packageName.replace('.', '/'));
        javaDoc.getPackageNames().add(packageName);
        packageNames.add(packageName);
      }
      else
      {
        javaDoc.getPackageExcludes().add(packageName);
      }
    }
  }

  private static void assembleArticleOptions(JavaDoc javaDoc) throws IOException, BundleException
  {
    File plugin = javaDoc.getProject();
    Manifest manifest = getManifest(plugin);

    ManifestElement[] manifestElements = getManifestElements(manifest);
    if (manifestElements == null || manifestElements.length == 0)
    {
      System.err.println("Warning: No public packages in " + plugin.getName());
    }
    else
    {
      for (ManifestElement manifestElement : manifestElements)
      {
        if (isPublic(manifestElement))
        {
          String packageName = manifestElement.getValue().trim();
          javaDoc.getArticlePackages().add(packageName);
        }
      }
    }
  }

  private static boolean isPublic(ManifestElement manifestElement)
  {
    return manifestElement.getDirective("x-internal") == null && manifestElement.getDirective("x-friends") == null;
  }

  private static Set<String> getExcludedPackages(Properties buildProperties)
  {
    Set<String> excludedPackages = new HashSet<String>();

    String javadocExclude = buildProperties.getProperty("org.eclipse.emf.cdo.releng.javadoc.exclude");
    if (javadocExclude != null)
    {
      for (String exclude : javadocExclude.split(","))
      {
        exclude = exclude.trim();
        if (exclude.length() != 0)
        {
          excludedPackages.add(exclude);
        }
      }
    }
    return excludedPackages;
  }

  private static ManifestElement[] getManifestElements(Manifest manifest) throws BundleException
  {
    Attributes attributes = manifest.getMainAttributes();
    String exportPackage = attributes.getValue(EXPORT_PACKAGE);
    return ManifestElement.parseHeader(EXPORT_PACKAGE, exportPackage);
  }

  private static Manifest getManifest(File plugin) throws IOException
  {
    File metaInf = new File(plugin, "META-INF");
    File manifest = new File(metaInf, "MANIFEST.MF");
    InputStream in = null;

    try
    {
      in = new FileInputStream(manifest);
      return new Manifest(in);
    }
    finally
    {
      if (in != null)
      {
        in.close();
      }
    }
  }

  public static Properties getProperties(File file)
  {
    Properties properties = new Properties();

    if (file.exists())
    {
      InputStream in = null;

      try
      {
        in = new FileInputStream(file);
        properties.load(in);
      }
      catch (IOException ex)
      {
        throw new RuntimeException(ex);
      }
      finally
      {
        if (in != null)
        {
          try
          {
            in.close();
          }
          catch (IOException ex)
          {
            ex.printStackTrace();
          }
        }
      }
    }

    return properties;
  }

  public static List<String> getDependencies(File projectFolder)
  {
    List<String> result = new ArrayList<String>();

    Properties buildProperties = getProperties(new File(projectFolder, "build.properties"));
    String depends = buildProperties.getProperty("doc.depends");
    if (depends != null)
    {
      StringTokenizer tokenizer = new StringTokenizer(depends, ",");
      while (tokenizer.hasMoreTokens())
      {
        String depend = tokenizer.nextToken().trim();
        result.add(depend);
      }
    }

    return result;
  }

  public static String getPluginName(File projectFolder)
  {
    Properties pluginProperties = getProperties(new File(projectFolder, "plugin.properties"));
    String pluginName = pluginProperties.getProperty("pluginName");
    if (pluginName == null)
    {
      pluginName = "Plugin " + projectFolder.getName();
    }

    return pluginName;
  }

  private static List<String> sort(Collection<String> collection)
  {
    List<String> result = new ArrayList<String>(collection);
    Collections.sort(result);
    return result;
  }

  public static void writeGenerationWarning(BufferedWriter writer) throws IOException
  {
    writer.write("\t<!-- =========================================== -->\n");
    writer.write("\t<!-- THIS FILE HAS BEEN GENERATED, DO NOT CHANGE -->\n");
    writer.write("\t<!-- =========================================== -->\n");
  }

  /**
   * @author Eike Stepper
   */
  private static class AntLib
  {
    private Map<String, SourcePlugin> sourcePlugins = new HashMap<String, SourcePlugin>();

    private Map<String, JavaDoc> javaDocs = new HashMap<String, JavaDoc>();

    public AntLib()
    {
    }

    public SourcePlugin getSourcePlugin(String projectName) throws IOException
    {
      SourcePlugin sourcePlugin = sourcePlugins.get(projectName);
      if (sourcePlugin == null)
      {
        sourcePlugin = new SourcePlugin(projectName);
        sourcePlugins.put(projectName, sourcePlugin);
      }

      return sourcePlugin;
    }

    public Collection<JavaDoc> getJavaDocs()
    {
      return javaDocs.values();
    }

    public Collection<JavaDoc> getJavaDocsSortedByDependencies()
    {
      List<JavaDoc> javaDocs = new ArrayList<JavaDoc>(getJavaDocs());
      Collections.sort(javaDocs, new Comparator<JavaDoc>()
      {
        public int compare(JavaDoc javaDoc1, JavaDoc javaDoc2)
        {
          String name1 = javaDoc1.getProject().getName();
          if (javaDoc2.getAllDependencies().contains(name1))
          {
            return -1;
          }

          String name2 = javaDoc2.getProject().getName();
          if (javaDoc1.getAllDependencies().contains(name2))
          {
            return 1;
          }

          return 0;
        }
      });

      return javaDocs;
    }

    public JavaDoc getJavaDoc(String projectName) throws IOException
    {
      JavaDoc javaDoc = javaDocs.get(projectName);
      if (javaDoc == null)
      {
        javaDoc = new JavaDoc(projectName);
        javaDocs.put(projectName, javaDoc);

        javaDoc.getDependencies().addAll(getDependencies(javaDoc.getProject()));
      }

      return javaDoc;
    }

    public JavaDoc getJavaDocIfExists(String projectName)
    {
      JavaDoc javaDoc = javaDocs.get(projectName);
      if (javaDoc == null)
      {
        throw new IllegalStateException("JavaDoc project not found: " + projectName);
      }

      return javaDoc;
    }

    public void generate() throws IOException
    {
      FileWriter out = null;

      try
      {
        File target = new File(releng, "buildLib.ant");
        System.out.println("Generating " + target.getCanonicalPath());

        out = new FileWriter(target);
        BufferedWriter writer = new BufferedWriter(out);

        writer.write("<?xml version=\"1.0\"?>\n");
        writer.write("<project name=\"JavaDocLib\" default=\"delegate\" basedir=\"..\">\n");

        writer.write("\n");
        writeGenerationWarning(writer);
        writer.write("\n");

        writer.write("\t<target name=\"delegate\">\n");

        for (JavaDoc javaDoc : (List<JavaDoc>)getJavaDocsSortedByDependencies())
        {
          writer.write("\t\t<ant antfile=\"plugins/" + javaDoc.getProject().getName()
              + "/build.xml\" target=\"${delegate.target}\" />\n");
        }

        writer.write("\t</target>\n");
        writer.write("\n");
        writer.write("</project>\n");
        writer.flush();
      }
      finally
      {
        if (out != null)
        {
          out.close();
        }
      }
    }

    public void generateDebug() throws IOException
    {
      FileWriter out = null;

      try
      {
        File target = new File(releng, "debug/frame.html");
        System.out.println("Generating " + target.getCanonicalPath());

        out = new FileWriter(target);
        BufferedWriter writer = new BufferedWriter(out);

        List<JavaDoc> javaDocs = new ArrayList<JavaDoc>(getJavaDocsSortedByDependencies());
        Collections.reverse(javaDocs);

        for (JavaDoc javaDoc : javaDocs)
        {
          for (SourcePlugin sourcePlugin : javaDoc.getSortedSourcePlugins())
          {
            List<String> sortedPackageNames = sourcePlugin.getSortedPackageNames();
            writer.write("<b><a href=\"../../" + javaDoc.getProject().getName() + "/javadoc/"
                + sortedPackageNames.get(0).replace('.', '/') + "/package-summary.html\" target=\"debugDetails\">"
                + sourcePlugin.getLabel() + "</a></b>\n");
            writer.write("<ul>\n");

            for (String packageName : sortedPackageNames)
            {
              writer.write("\t<li><a href=\"../../" + javaDoc.getProject().getName() + "/javadoc/"
                  + packageName.replace('.', '/') + "/package-summary.html\" target=\"debugDetails\">" + packageName
                  + "</a>\n");
            }

            writer.write("</ul>\n");
          }
        }

        writer.flush();
      }
      finally
      {
        if (out != null)
        {
          out.close();
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class SourcePlugin implements Comparable<SourcePlugin>
  {
    private String projectName;

    private String label;

    private Set<String> packageNames = new HashSet<String>();

    public SourcePlugin(String projectName) throws IOException
    {
      this.projectName = projectName;
      label = getPluginName(getProject());
    }

    @Override
    public String toString()
    {
      return projectName;
    }

    public File getProject()
    {
      return new File(plugins, projectName);
    }

    public String getLabel()
    {
      return label;
    }

    public Set<String> getPackageNames()
    {
      return packageNames;
    }

    public List<String> getSortedPackageNames()
    {
      List<String> names = new ArrayList<String>(packageNames);
      Collections.sort(names);
      return names;
    }

    public int compareTo(SourcePlugin o)
    {
      return getLabel().compareTo(o.getLabel());
    }

    public void validatePackageInfos() throws IOException
    {
      Set<String> packageNames2 = getPackageNames();
      for (String packageName : packageNames2)
      {
        File packageFolder = new File(getProject(), "src/" + packageName.replace('.', '/'));
        File packageInfo = new File(packageFolder, "package-info.java");
        if (!packageInfo.isFile())
        {
          File packageHtml = new File(packageFolder, "package.html");
          if (packageHtml.isFile())
          {
            convertPackageHTML(packageHtml, packageInfo, packageName);
          }

          if (!packageInfo.isFile())
          {
            System.err.println("Package info missing: " + packageInfo.getCanonicalPath());
          }
        }
      }
    }

    private void convertPackageHTML(File packageHtml, File packageInfo, String packageName) throws IOException
    {
      int length = (int)packageHtml.length();
      char[] content = new char[length];

      FileReader reader = null;

      try
      {
        reader = new FileReader(packageHtml);
        if (reader.read(content) != length)
        {
          throw new IOException("Invalid file length: " + packageHtml.getCanonicalPath());
        }
      }
      finally
      {
        if (reader != null)
        {
          reader.close();
        }
      }

      String input = new String(content);
      Matcher matcher = PACKAGE_INFO_PATTERN.matcher(input);
      if (!matcher.matches())
      {
        System.err.println("No match: " + packageHtml.getCanonicalPath());
        return;
      }

      System.out.println("Converting " + packageHtml.getCanonicalPath());
      String comment = matcher.group(1);
      FileWriter out = null;

      try
      {
        out = new FileWriter(packageInfo);
        BufferedWriter writer = new BufferedWriter(out);

        writer.write("/*" + NL);
        writer.write(" * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others." + NL);
        writer.write(" * All rights reserved. This program and the accompanying materials" + NL);
        writer.write(" * are made available under the terms of the Eclipse Public License v1.0" + NL);
        writer.write(" * which accompanies this distribution, and is available at" + NL);
        writer.write(" * http://www.eclipse.org/legal/epl-v10.html" + NL);
        writer.write(" * " + NL);
        writer.write(" * Contributors:" + NL);
        writer.write(" *    Eike Stepper - initial API and implementation" + NL);
        writer.write(" */" + NL);
        writer.write(NL);
        writer.write("/**" + NL);

        String[] lines = comment.split("\n");
        for (String line : lines)
        {
          writer.write(" * ");
          writer.write(line);
          writer.write(NL);
        }

        writer.write(" */" + NL);
        writer.write("package " + packageName + ";" + NL);
        writer.flush();
      }
      finally
      {
        if (out != null)
        {
          out.close();
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class JavaDoc
  {
    private String projectName;

    private Set<String> dependencies = new HashSet<String>();

    private Set<SourcePlugin> sourcePlugins = new HashSet<SourcePlugin>();

    private List<String> sourceFolders = new ArrayList<String>();

    private Set<String> packageNames = new HashSet<String>();

    private Set<String> packageExcludes = new HashSet<String>();

    private Set<String> articlePackages = new HashSet<String>();

    public JavaDoc(String projectName)
    {
      this.projectName = projectName;
    }

    @Override
    public String toString()
    {
      return projectName;
    }

    public File getProject()
    {
      return new File(plugins, projectName);
    }

    public Set<String> getDependencies()
    {
      return dependencies;
    }

    public Set<String> getAllDependencies()
    {
      Set<String> result = new HashSet<String>();
      recurseDependencies(this, result);
      return result;
    }

    private void recurseDependencies(JavaDoc javaDoc, Set<String> result)
    {
      for (String dependency : javaDoc.getDependencies())
      {
        if (result.add(dependency))
        {
          JavaDoc child = ANTLIB.getJavaDocIfExists(dependency);
          recurseDependencies(child, result);
        }
      }
    }

    public Set<SourcePlugin> getSourcePlugins()
    {
      return sourcePlugins;
    }

    public List<SourcePlugin> getSortedSourcePlugins()
    {
      List<SourcePlugin> plugins = new ArrayList<SourcePlugin>(sourcePlugins);
      Collections.sort(plugins);
      return plugins;
    }

    public List<String> getSourceFolders()
    {
      return sourceFolders;
    }

    public Set<String> getPackageNames()
    {
      return packageNames;
    }

    public Set<String> getPackageExcludes()
    {
      return packageExcludes;
    }

    public Set<String> getArticlePackages()
    {
      return articlePackages;
    }

    public void generateAnt() throws IOException
    {
      File project = getProject();
      FileWriter out = null;
      FileReader in = null;

      try
      {
        File target = new File(project, "build.xml");
        System.out.println("Generating " + target.getCanonicalPath());

        out = new FileWriter(target);
        BufferedWriter writer = new BufferedWriter(out);

        try
        {
          in = new FileReader(new File(releng, "buildTemplate.ant"));
          BufferedReader reader = new BufferedReader(in);

          String line;
          while ((line = reader.readLine()) != null)
          {
            line = line.replace("${PROJECT-NAME}", projectName);

            String id = line.trim();
            if ("<!-- GENERATION WARNING -->".equals(id))
            {
              writeGenerationWarning(writer);
            }
            else if ("<!-- ARTICLE SKIP -->".equals(id))
            {
              if (articlePackages.isEmpty())
              {
                writer.write("\t<property name=\"article.skip\" value=\"true\" />\n");
              }
            }
            else if ("<!-- SOURCE FOLDERS -->".equals(id))
            {
              for (String sourceFolder : sort(sourceFolders))
              {
                writer.write("\t\t\t\t<include name=\"" + sourceFolder + "/*.java\" />\n");
              }
            }
            else if ("<!-- COPY DOC FILES -->".equals(id))
            {
              CharArrayWriter buffer = new CharArrayWriter();
              buffer.write("\t\t<copy todir=\"${destdir}\" verbose=\"true\" failonerror=\"false\">\n");
              buffer.write("\t\t\t<cutdirsmapper dirs=\"2\" />\n");
              buffer.write("\t\t\t<fileset dir=\"plugins\" defaultexcludes=\"true\">\n");

              boolean exist = false;
              for (String sourceFolder : sort(sourceFolders))
              {
                File docFiles = new File("../../plugins/" + sourceFolder + "/doc-files");
                if (docFiles.isDirectory())
                {
                  exist = true;
                  buffer.write("\t\t\t\t<include name=\"" + sourceFolder + "/doc-files/**\" />\n");
                }
              }

              if (exist)
              {
                buffer.write("\t\t\t</fileset>\n");
                buffer.write("\t\t</copy>\n");
                buffer.write("\n");
                writer.write(buffer.toCharArray());
              }
            }
            else if ("<!-- PACKAGE NAMES -->".equals(id))
            {
              for (String packageName : sort(packageNames))
              {
                writer.write("\t\t\t<package name=\"" + packageName + "\" />\n");
              }
            }
            else if ("<!-- PACKAGE EXCLUDES -->".equals(id))
            {
              for (String packageExclude : sort(packageExcludes))
              {
                writer.write("\t\t\t<excludepackage name=\"" + packageExclude + "\" />\n");
              }
            }
            else if ("<!-- ARTICLE PACKAGES -->".equals(id))
            {
              for (String articlePackage : sort(articlePackages))
              {
                writer.write("\t\t\t<package name=\"" + articlePackage + "\" />\n");
              }

              for (String dependency : sort(getAllDependencies()))
              {
                JavaDoc javaDoc = ANTLIB.getJavaDoc(dependency);
                for (String articlePackage : sort(javaDoc.getArticlePackages()))
                {
                  writer.write("\t\t\t<package name=\"" + articlePackage + "\" />\n");
                }
              }
            }
            else if ("<!-- JAVADOC DEPENDENCIES -->".equals(id))
            {
              for (String dependency : sort(getAllDependencies()))
              {
                writer.write("\t\t\t<link href=\"MAKE-RELATIVE/" + dependency
                    + "/javadoc\" offline=\"true\" packagelistloc=\"plugins/" + dependency + "/javadoc\" />\n");
              }
            }
            else if ("<!-- ARTICLE DEPENDENCIES -->".equals(id))
            {
              for (String dependency : sort(getAllDependencies()))
              {
                writer.write("\t\t\t\t<include name=\"" + dependency + "/src/**/*.java\" />\n");
              }
            }
            else if ("<!-- GROUPS -->".equals(id))
            {
              for (SourcePlugin sourcePlugin : getSortedSourcePlugins())
              {
                writer.write("\t\t\t<group title=\"" + sourcePlugin.getLabel() + "\">\n");

                for (String packageName : sourcePlugin.getSortedPackageNames())
                {
                  writer.write("\t\t\t\t<package name=\"" + packageName + "\" />\n");
                }

                writer.write("\t\t\t</group>\n");
              }
            }
            else
            {
              writer.write(line);
              writer.write("\n");
            }
          }

          writer.flush();
        }
        finally
        {
          if (in != null)
          {
            in.close();
          }
        }
      }
      finally
      {
        if (out != null)
        {
          out.close();
        }
      }
    }

    public void generateToc() throws IOException
    {
      File project = getProject();
      FileWriter out = null;
      FileReader in = null;

      try
      {
        File target = new File(project, "toc.xml");
        System.out.println("Generating " + target.getCanonicalPath());

        out = new FileWriter(target);
        BufferedWriter writer = new BufferedWriter(out);

        try
        {
          in = new FileReader(new File(project, "tocTemplate.xml"));
          BufferedReader reader = new BufferedReader(in);

          String line;
          while ((line = reader.readLine()) != null)
          {
            String id = line.trim();
            if ("<!-- GROUPS -->".equals(id))
            {
              for (SourcePlugin sourcePlugin : getSortedSourcePlugins())
              {
                List<String> sortedPackageNames = sourcePlugin.getSortedPackageNames();
                writer.write("\t\t<topic label=\"" + sourcePlugin.getLabel() + "\" href=\"javadoc/"
                    + sortedPackageNames.get(0).replace('.', '/') + "/package-summary.html\">\n");

                for (String packageName : sortedPackageNames)
                {
                  writer.write("\t\t\t<topic label=\"" + packageName + "\" href=\"javadoc/"
                      + packageName.replace('.', '/') + "/package-summary.html\" />\n");
                }

                writer.write("\t\t</topic>\n");
              }
            }
            else
            {
              writer.write(line);
              writer.write("\n");
            }
          }

          writer.flush();
        }
        finally
        {
          if (in != null)
          {
            in.close();
          }
        }
      }
      finally
      {
        if (out != null)
        {
          out.close();
        }
      }
    }
  }
}
