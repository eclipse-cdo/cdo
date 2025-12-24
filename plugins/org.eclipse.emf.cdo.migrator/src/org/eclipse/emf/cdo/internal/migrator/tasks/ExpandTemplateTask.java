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

import org.apache.tools.ant.BuildException;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class ExpandTemplateTask extends CDOTask
{
  private final List<TemplateProperty> templateProperties = new ArrayList<>();

  private File template;

  private File target;

  private String placeholderPrefix = "%%";

  private String placeholderSuffix = "%%";

  public TemplateProperty createTemplateProperty()
  {
    TemplateProperty templateProperty = new TemplateProperty();
    templateProperties.add(templateProperty);
    return templateProperty;
  }

  public void setTemplate(File template)
  {
    this.template = template;
  }

  public void setPlaceholderPrefix(String placeholderPrefix)
  {
    this.placeholderPrefix = placeholderPrefix;
  }

  public void setPlaceholderSuffix(String placeholderSuffix)
  {
    this.placeholderSuffix = placeholderSuffix;
  }

  public void setTarget(File target)
  {
    this.target = target;
  }

  @Override
  protected void checkAttributes() throws BuildException
  {
    assertTrue("'template' must be specified.", template != null);
    assertTrue("'template' must be point to an existing file.", template.isFile());
    assertTrue("'target' must be specified.", target != null);
    assertTrue("'placeholderPrefix' must be specified.", placeholderPrefix != null && placeholderPrefix.length() != 0);
    assertTrue("'placeholderSuffix' must be specified.", placeholderSuffix != null && placeholderSuffix.length() != 0);

    for (TemplateProperty templateProperty : templateProperties)
    {
      String name = templateProperty.getName();
      assertTrue("'name' of property must be specified.", name != null && name.length() != 0);
    }
  }

  @Override
  protected void doExecute() throws Exception
  {
    verbose("Expanding template " + template + " to target " + target);

    String content = readTextFile(template);
    Map<String, String> properties = getProperties();

    String result = generate(content, properties);
    writeTextFile(target, result);
  }

  protected String generate(String content, Map<String, String> properties) throws Exception
  {
    StringBuilder result = new StringBuilder(content);

    for (Map.Entry<String, String> entry : properties.entrySet())
    {
      String placeholder = formatPlaceholder(entry.getKey());
      String value = entry.getValue();
      if (value == null)
      {
        value = "";
      }

      int placeholderLength = placeholder.length();
      int valueLength = value.length();
      int start = 0;

      for (;;)
      {
        int pos = result.indexOf(placeholder, start);
        if (pos == -1)
        {
          break;
        }

        verbose("Expanding template property '" + entry.getKey() + "' at position " + pos);
        result.replace(pos, pos + placeholderLength, value);
        start = pos + valueLength;
      }
    }

    return result.toString();
  }

  protected String formatPlaceholder(String name)
  {
    return placeholderPrefix + name + placeholderSuffix;
  }

  protected Map<String, String> getProperties()
  {
    Map<String, String> result = new LinkedHashMap<>();

    for (TemplateProperty templateProperty : templateProperties)
    {
      result.put(templateProperty.getName(), templateProperty.getValue());
    }

    return result;
  }

  /**
   * @author Eike Stepper
   */
  public static final class TemplateProperty
  {
    private String name;

    private String value;

    public TemplateProperty()
    {
    }

    public String getName()
    {
      return name;
    }

    public void setName(String name)
    {
      this.name = name;
    }

    public String getValue()
    {
      return value;
    }

    public void setValue(String value)
    {
      this.value = value;
    }

    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append(name);
      builder.append(" = ");
      builder.append(value);
      return builder.toString();
    }
  }
}
