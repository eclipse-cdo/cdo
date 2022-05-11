/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author Eike Stepper
 * @since 3.19
 */
public final class PluginUtil
{
  private static final String ATT_CLASS = "class";

  private static final String ATT_PREDECESSOR = "predecessor";

  private PluginUtil()
  {
  }

  public static <T> T instantiate(IConfigurationElement element)
  {
    return instantiate(element, ATT_CLASS);
  }

  public static <T> T instantiate(IConfigurationElement element, String classAttribute)
  {
    try
    {
      @SuppressWarnings("unchecked")
      T extension = (T)element.createExecutableExtension(classAttribute);
      return extension;
    }
    catch (CoreException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static IConfigurationElement[] getConfigurationElements(String namespace, String extensionPointName)
  {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    return registry.getConfigurationElementsFor(namespace, extensionPointName);
  }

  /**
   * Removes all elements with a 'class' attribute that is specified in the 'predecessor' list of any other element.
   */
  public static IConfigurationElement[] removePredecessors(IConfigurationElement[] elements)
  {
    return removePredecessors(elements, ATT_CLASS, ATT_PREDECESSOR);
  }

  /**
   * Removes all elements with a 'class' attribute that is specified in the 'predecessor' list of any other element.
   */
  public static IConfigurationElement[] removePredecessors(IConfigurationElement[] elements, String classAttribute, String predecessorAttribute)
  {
    Map<String, List<IConfigurationElement>> classToElements = new HashMap<>();
    Set<String> predecessors = new HashSet<>();

    for (IConfigurationElement element : elements)
    {
      // Also map elements without a class attribute.
      String className = StringUtil.safe(element.getAttribute(classAttribute));
      classToElements.computeIfAbsent(className, k -> new ArrayList<>()).add(element);

      predecessors.addAll(getPredecessors(element, predecessorAttribute));
    }

    for (String predecessor : predecessors)
    {
      classToElements.remove(predecessor);
    }

    List<IConfigurationElement> result = new ArrayList<>();

    for (Map.Entry<String, List<IConfigurationElement>> entry : classToElements.entrySet())
    {
      List<IConfigurationElement> list = entry.getValue();
      result.addAll(list);
    }

    return result.toArray(new IConfigurationElement[result.size()]);
  }

  private static Set<String> getPredecessors(IConfigurationElement element, String predecessorAttribute)
  {
    String value = element.getAttribute(predecessorAttribute);
    if (value == null)
    {
      return Collections.emptySet();
    }

    Set<String> predecessors = new HashSet<>();
    StringTokenizer tokenizer = new StringTokenizer(value, " ");

    while (tokenizer.hasMoreTokens())
    {
      String predecessor = tokenizer.nextToken().trim();
      if (predecessor.length() != 0)
      {
        predecessors.add(predecessor);
      }
    }

    return predecessors;
  }
}
