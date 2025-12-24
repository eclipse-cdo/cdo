/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.CDOElement;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;

import org.eclipse.net4j.util.factory.ProductCreationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 */
public class ResourceGroupModifier implements CDOCheckoutContentModifier
{
  private static final Map<CDOResourceNode, ResourceGroup> RESOURCE_GROUPS = new WeakHashMap<>();

  private final String suffix;

  public ResourceGroupModifier(String extension)
  {
    suffix = "." + extension;
  }

  public final String getExtension()
  {
    return suffix.substring(1);
  }

  @Override
  public Object[] modifyChildren(Object parent, Object[] children)
  {
    Map<String, ResourceGroup> resourceGroups = null;

    for (Object child : children)
    {
      if (child instanceof CDOResourceNode)
      {
        CDOResourceNode resourceNode = (CDOResourceNode)child;
        String name = resourceNode.getName();
        if (name != null && name.endsWith(suffix))
        {
          if (resourceGroups == null)
          {
            resourceGroups = new HashMap<>();
          }

          String groupName = name.substring(0, name.length() - suffix.length());
          ResourceGroup resourceGroup = getResourceGroup(resourceNode);
          resourceGroups.put(groupName, resourceGroup);
        }
      }
    }

    if (resourceGroups != null)
    {
      List<Object> result = new ArrayList<>();
      for (Object child : children)
      {
        child = replacement(resourceGroups, child);
        if (child != null)
        {
          result.add(child);
        }
      }

      children = result.toArray();
    }

    return children;
  }

  protected ResourceGroup createResourceGroup(CDOResourceNode resourceNode)
  {
    return new ResourceGroup(resourceNode);
  }

  private ResourceGroup getResourceGroup(CDOResourceNode resourceNode)
  {
    synchronized (RESOURCE_GROUPS)
    {
      ResourceGroup resourceGroup = RESOURCE_GROUPS.get(resourceNode);
      if (resourceGroup == null)
      {
        resourceGroup = createResourceGroup(resourceNode);
        RESOURCE_GROUPS.put(resourceNode, resourceGroup);
      }
      else
      {
        resourceGroup.reset();
      }

      return resourceGroup;
    }
  }

  private Object replacement(Map<String, ResourceGroup> elements, Object child)
  {
    if (child instanceof CDOResourceNode)
    {
      CDOResourceNode resourceNode = (CDOResourceNode)child;
      String name = resourceNode.getName();
      if (name != null)
      {
        int lastDot = name.lastIndexOf('.');
        if (lastDot != -1)
        {
          String groupName = name.substring(0, lastDot);
          CDOElement element = elements.get(groupName);
          if (element != null)
          {
            element.addChild(resourceNode);

            if (name.endsWith(suffix))
            {
              return element;
            }

            return null;
          }
        }
      }
    }

    return child;
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends CDOCheckoutContentModifier.Factory
  {
    public static final String TYPE_PREFIX = "resource.group.";

    private final String extension;

    public Factory(String extension)
    {
      super(TYPE_PREFIX + extension);
      this.extension = extension;
    }

    @Override
    public final ResourceGroupModifier create(String description) throws ProductCreationException
    {
      return new ResourceGroupModifier(extension);
    }
  }
}
