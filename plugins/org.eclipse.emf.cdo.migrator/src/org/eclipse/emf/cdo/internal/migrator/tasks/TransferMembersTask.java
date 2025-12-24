/*
 * Copyright (c) 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.migrator.tasks;

import org.eclipse.emf.cdo.internal.migrator.tasks.TransferMembersTask.SourceClass.SourceMember;

import org.apache.tools.ant.BuildException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public class TransferMembersTask extends ExpandTemplateTask
{
  private final List<SourceClass> sourceClasses = new ArrayList<>();

  private String importsPlaceholder = "IMPORTS";

  private String fieldsPlaceholder = "FIELDS";

  private String methodsPlaceholder = "METHODS";

  public SourceClass createSourceClass()
  {
    SourceClass sourceClass = new SourceClass();
    sourceClasses.add(sourceClass);
    return sourceClass;
  }

  public void setImportsPlaceholder(String importsPlaceholder)
  {
    this.importsPlaceholder = importsPlaceholder;
  }

  public void setFieldsPlaceholder(String fieldsPlaceholder)
  {
    this.fieldsPlaceholder = fieldsPlaceholder;
  }

  public void setMethodsPlaceholder(String methodsPlaceholder)
  {
    this.methodsPlaceholder = methodsPlaceholder;
  }

  @Override
  protected void checkAttributes() throws BuildException
  {
    super.checkAttributes();

    for (SourceClass sourceClass : sourceClasses)
    {
      sourceClass.checkAttributes();
    }

    assertTrue("'importsPlaceholder' must be specified.", importsPlaceholder != null && importsPlaceholder.length() != 0);
    assertTrue("'fieldsPlaceholder' must be specified.", fieldsPlaceholder != null && fieldsPlaceholder.length() != 0);
    assertTrue("'methodsPlaceholder' must be specified.", methodsPlaceholder != null && methodsPlaceholder.length() != 0);
  }

  @Override
  protected String generate(String content, Map<String, String> properties) throws Exception
  {
    Collector collector = new Collector(this);

    for (SourceClass sourceClass : sourceClasses)
    {
      collector.collect(sourceClass);
    }

    properties.put(importsPlaceholder, formatImports(collector.imports.keySet()));
    properties.put(fieldsPlaceholder, formatMembers(collector.fields.values()));
    properties.put(methodsPlaceholder, formatMembers(collector.methods.values()));

    return super.generate(content, properties);
  }

  private String formatImports(Collection<String> imports)
  {
    StringBuilder builder = new StringBuilder();
    boolean first = true;

    for (String name : imports)
    {
      if (first)
      {
        first = false;
      }
      else
      {
        builder.append(NL);
      }

      builder.append("import ");
      builder.append(name);
      builder.append(";");
    }

    return builder.toString();
  }

  private String formatMembers(Collection<List<String>> members)
  {
    StringBuilder builder = new StringBuilder();
    boolean first = true;

    for (List<String> texts : members)
    {
      for (String text : texts)
      {
        if (first)
        {
          first = false;
        }
        else
        {
          builder.append(NL);
          builder.append(NL);
        }

        builder.append(text);
      }
    }

    return builder.toString();
  }

  /**
   * @author Eike Stepper
   */
  public static final class SourceClass
  {
    private File file;

    private boolean imports = true;

    private final List<SourceField> sourceFields = new ArrayList<>();

    private final List<SourceMethod> sourceMethods = new ArrayList<>();

    public File getFile()
    {
      return file;
    }

    public void setFile(File file)
    {
      this.file = file;
    }

    public boolean isImports()
    {
      return imports;
    }

    public void setImports(boolean imports)
    {
      this.imports = imports;
    }

    public List<SourceField> getSourceFields()
    {
      return sourceFields;
    }

    public SourceField createSourceField()
    {
      SourceField sourceField = new SourceField();
      sourceFields.add(sourceField);
      return sourceField;
    }

    public List<SourceMethod> getSourceMethods()
    {
      return sourceMethods;
    }

    public SourceMethod createSourceMethod()
    {
      SourceMethod sourceMethod = new SourceMethod();
      sourceMethods.add(sourceMethod);
      return sourceMethod;
    }

    @Override
    public String toString()
    {
      return String.valueOf(file);
    }

    public void checkAttributes() throws BuildException
    {
      assertTrue("'file' must be specified.", file != null);
      assertTrue("'file' must be point to an existing file.", file.isFile());

      checkSourceMembers(sourceFields);
      checkSourceMembers(sourceMethods);
    }

    private static void checkSourceMembers(List<? extends SourceMember> sourceMembers)
    {
      for (SourceMember sourceMember : sourceMembers)
      {
        Pattern pattern = sourceMember.getPattern();
        assertTrue("'match' must be specified.", pattern != null);
      }
    }

    /**
     * @author Eike Stepper
     */
    public static abstract class SourceMember
    {
      private Pattern pattern;

      public Pattern getPattern()
      {
        return pattern;
      }

      public void setMatch(String regex)
      {
        pattern = Pattern.compile(regex);
      }
    }

    /**
     * @author Eike Stepper
     */
    public static final class SourceField extends SourceMember
    {
      @Override
      public String toString()
      {
        StringBuilder builder = new StringBuilder();
        builder.append("SourceField[");
        builder.append(getPattern());
        builder.append("]");
        return builder.toString();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static final class SourceMethod extends SourceMember
    {
      @Override
      public String toString()
      {
        StringBuilder builder = new StringBuilder();
        builder.append("SourceMethod[");
        builder.append(getPattern());
        builder.append("]");
        return builder.toString();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Collector
  {
    private static final Pattern IMPORT_PATTERN = Pattern.compile("[\\\\t ]*import\\s+([^;]+);");

    private static final Pattern FIELD_PATTERN = Pattern
        .compile("[\\\\t ]*((public|private|protected|static|final|transient|volatile)+\\s)+[\\$_\\w\\<\\>\\[\\]]*\\s+([\\$_\\w]+)\\s*(;|=[^;]*;)");

    private static final Pattern METHOD_PATTERN = Pattern.compile(
        "[\\t ]*((public|private|protected|static|final|native|synchronized|abstract|strictfp)+\\s)+[\\$_\\w\\<\\>\\[\\]]*\\s+(([\\$_\\w]+)\\([^\\)]*\\)?)[^\\{;]*(.)");

    public final Map<String, List<String>> imports = new LinkedHashMap<>();

    public final Map<String, List<String>> fields = new LinkedHashMap<>();

    public final Map<String, List<String>> methods = new LinkedHashMap<>();

    private final CDOTask task;

    public Collector(CDOTask task)
    {
      this.task = task;
    }

    public void collect(SourceClass sourceClass) throws IOException
    {
      String content = readTextFile(sourceClass.getFile());

      if (sourceClass.isImports())
      {
        task.verbose("Collecting imports of " + sourceClass);
        collect(content, IMPORT_PATTERN, 1, null, imports);
      }

      if (!sourceClass.getSourceFields().isEmpty())
      {
        task.verbose("Collecting fields of " + sourceClass);
        collect(content, FIELD_PATTERN, 3, sourceClass.getSourceFields(), fields);
      }

      if (!sourceClass.getSourceMethods().isEmpty())
      {
        task.verbose("Collecting methods of " + sourceClass);
        collect(content, METHOD_PATTERN, 4, sourceClass.getSourceMethods(), methods);
      }
    }

    private void collect(String content, Pattern pattern, int group, List<? extends SourceMember> sourceMembers, Map<String, List<String>> result)
    {
      Matcher matcher = pattern.matcher(content);
      int matcherStart = 0;

      while (matcher.find(matcherStart))
      {
        String name = matcher.group(group);
        String text = matcher.group(0);
        matcherStart = matcher.end() + 1;

        if (pattern == METHOD_PATTERN)
        {
          String braceOrSemicolon = matcher.group(5);
          if (braceOrSemicolon.equals("{"))
          {
            int start = matcherStart - 1;
            int end = content.length();
            int nesting = 1;

            for (int i = start; i < end; i++)
            {
              char c = content.charAt(i);
              if (c == '{')
              {
                ++nesting;
              }
              else if (c == '}')
              {
                if (--nesting == 0)
                {
                  matcherStart = i + 1;

                  String body = content.substring(start, matcherStart);
                  text += body;
                  break;
                }
              }
            }
          }
        }

        if (sourceMembers != null)
        {
          if (!matchName(name, sourceMembers))
          {
            continue;
          }
        }

        List<String> texts = result.get(name);
        if (texts == null)
        {
          texts = new ArrayList<>();
          result.put(name, texts);
        }

        texts.add(text);
        task.verbose("   " + name);
      }
    }

    private static boolean matchName(String name, List<? extends SourceMember> sourceMembers)
    {
      for (SourceMember sourceMember : sourceMembers)
      {
        Pattern pattern = sourceMember.getPattern();
        if (pattern.matcher(name).matches())
        {
          return true;
        }
      }

      return false;
    }
  }
}
