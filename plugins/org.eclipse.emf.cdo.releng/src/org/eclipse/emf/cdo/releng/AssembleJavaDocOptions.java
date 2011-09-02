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
package org.eclipse.emf.cdo.releng;

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

/**
 * @author Eike Stepper
 */
public class AssembleJavaDocOptions
{
  private static final String EXPORT_PACKAGE = "Export-Package";

  private static final AntLib ANTLIB = new AntLib();

  private static File workspace;

  private static File plugins;

  private static File releng;

  public static void main(String[] args) throws Exception
  {
    String workspacePath = args.length == 0 ? "../../.." : args[0];
    workspace = new File(workspacePath).getCanonicalFile();
    plugins = new File(workspace, "plugins");
    releng = new File(plugins, "org.eclipse.emf.cdo.releng");

    for (File plugin : plugins.listFiles())
    {
      if (plugin.isDirectory())
      {
        Properties buildProperties = getProperties(new File(plugin, "build.properties"));
        String javadocProject = buildProperties.getProperty("org.eclipse.emf.cdo.releng.javadoc.project");
        if (javadocProject != null)
        {
          assembleJavaDocOptions(plugin, javadocProject);
        }
      }
    }

    Collection<JavaDoc> values = ANTLIB.getJavaDocs();
    for (JavaDoc javaDoc : values)
    {
      javaDoc.generateAnt();
      javaDoc.generateToc();
    }

    ANTLIB.generate();
  }

  private static void assembleJavaDocOptions(File plugin, String javadocProject) throws IOException, BundleException
  {
    SourcePlugin sourcePlugin = ANTLIB.getSourcePlugin(plugin.getName());

    JavaDoc javaDoc = ANTLIB.getJavaDoc(javadocProject);
    javaDoc.getSourcePlugins().add(sourcePlugin);

    Manifest manifest = getManifest(plugin);
    for (ManifestElement manifestElement : getManifestElements(manifest))
    {
      String packageName = manifestElement.getValue().trim();
      if (isPublic(manifestElement))
      {
        javaDoc.getSourceFolders().add(plugin.getName() + "/src/" + packageName.replace('.', '/'));
        javaDoc.getPackageNames().add(packageName);
        sourcePlugin.getPackageNames().add(packageName);
      }
      else
      {
        javaDoc.getPackageExcludes().add(packageName);
      }
    }
  }

  private static boolean isPublic(ManifestElement manifestElement)
  {
    return manifestElement.getDirective("x-internal") == null && manifestElement.getDirective("x-friends") == null;
  }

  private static ManifestElement[] getManifestElements(Manifest manifest) throws BundleException
  {
    Attributes attributes = manifest.getMainAttributes();
    String exportPackage = attributes.getValue(EXPORT_PACKAGE);
    ManifestElement[] manifestElements = ManifestElement.parseHeader(EXPORT_PACKAGE, exportPackage);
    return manifestElements;
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

  private static Properties getProperties(File file) throws IOException
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
      finally
      {
        if (in != null)
        {
          in.close();
        }
      }
    }

    return properties;
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

        Properties buildProperties = getProperties(new File(javaDoc.getProject(), "build.properties"));
        String depends = buildProperties.getProperty("org.eclipse.emf.cdo.releng.javadoc.depends");
        if (depends != null)
        {
          StringTokenizer tokenizer = new StringTokenizer(depends, ",");
          while (tokenizer.hasMoreTokens())
          {
            String depend = tokenizer.nextToken().trim();
            javaDoc.getDependencies().add(depend);
          }
        }
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
        out = new FileWriter(new File(releng, "javadoc/javadocLib.ant"));
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
              + "/javadoc.ant\" target=\"${delegate.target}\" />\n");
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
      Properties pluginProperties = getProperties(new File(getProject(), "plugin.properties"));
      label = pluginProperties.getProperty("pluginName");
      if (label == null)
      {
        label = "Plugin " + projectName;
      }
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

    public JavaDoc(String projectName)
    {
      this.projectName = projectName;
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

    public void generateAnt() throws IOException
    {
      File project = getProject();
      FileWriter out = null;
      FileReader in = null;

      try
      {
        out = new FileWriter(new File(project, "javadoc.ant"));
        BufferedWriter writer = new BufferedWriter(out);

        try
        {
          in = new FileReader(new File(releng, "javadoc/javadocTemplate.ant"));
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
                File docFiles = new File("../../../plugins/" + sourceFolder + "/doc-files");
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
            else if ("<!-- DEPENDENCIES -->".equals(id))
            {
              for (String dependency : sort(getAllDependencies()))
              {
                writer.write("\t\t\t<link href=\"MAKE-RELATIVE/" + dependency
                    + "/javadoc\" offline=\"true\" packagelistloc=\"plugins/" + dependency + "/javadoc\" />\n");
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
        out = new FileWriter(new File(project, "toc.xml"));
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
