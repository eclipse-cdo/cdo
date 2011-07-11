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
package org.eclipse.emf.cdo.releng.version.test;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class TestResourceChangeListener implements IResourceChangeListener
{
  public void resourceChanged(IResourceChangeEvent event)
  {
    try
    {
      System.out.println(getType(event));
      IResourceDelta delta = event.getDelta();
      if (delta != null)
      {
        delta.accept(new IResourceDeltaVisitor()
        {
          public boolean visit(IResourceDelta delta) throws CoreException
          {
            System.out.println("   " + delta.getFullPath() + "  -->  " + getKind(delta) + " " + getFlags(delta));
            return true;
          }
        });
      }
    }
    catch (CoreException ex)
    {
      ex.printStackTrace();
    }
  }

  public static String getType(IResourceChangeEvent event)
  {
    switch (event.getType())
    {
    case IResourceChangeEvent.POST_BUILD:
      return "POST_BUILD";
    case IResourceChangeEvent.POST_CHANGE:
      return "POST_CHANGE";
    case IResourceChangeEvent.PRE_BUILD:
      return "PRE_BUILD";
    case IResourceChangeEvent.PRE_CLOSE:
      return "PRE_CLOSE";
    case IResourceChangeEvent.PRE_DELETE:
      return "PRE_DELETE";
    case IResourceChangeEvent.PRE_REFRESH:
      return "PRE_REFRESH";
    default:
      return "Unknown event type: " + event.getType();
    }
  }

  public static String getKind(IResourceDelta delta)
  {
    switch (delta.getKind())
    {
    case IResourceDelta.ADDED:
      return "ADDED";
    case IResourceDelta.REMOVED:
      return "REMOVED";
    case IResourceDelta.CHANGED:
      return "CHANGED";
    case IResourceDelta.ADDED_PHANTOM:
      return "ADDED_PHANTOM";
    case IResourceDelta.REMOVED_PHANTOM:
      return "REMOVED_PHANTOM";
    default:
      return "Unknown delta kind: " + delta.getKind();
    }
  }

  public static String getFlags(IResourceDelta delta)
  {
    List<String> list = new ArrayList<String>();
    if (hasFlag(delta, IResourceDelta.CONTENT))
    {
      list.add("CONTENT");
    }

    if (hasFlag(delta, IResourceDelta.DERIVED_CHANGED))
    {
      list.add("DERIVED_CHANGED");
    }

    if (hasFlag(delta, IResourceDelta.DESCRIPTION))
    {
      list.add("DESCRIPTION");
    }

    if (hasFlag(delta, IResourceDelta.ENCODING))
    {
      list.add("ENCODING");
    }

    if (hasFlag(delta, IResourceDelta.LOCAL_CHANGED))
    {
      list.add("LOCAL_CHANGED");
    }

    if (hasFlag(delta, IResourceDelta.OPEN))
    {
      list.add("OPEN");
    }

    if (hasFlag(delta, IResourceDelta.MOVED_TO))
    {
      list.add("MOVED_TO");
    }

    if (hasFlag(delta, IResourceDelta.MOVED_FROM))
    {
      list.add("MOVED_FROM");
    }

    if (hasFlag(delta, IResourceDelta.COPIED_FROM))
    {
      list.add("COPIED_FROM");
    }

    if (hasFlag(delta, IResourceDelta.TYPE))
    {
      list.add("TYPE");
    }

    if (hasFlag(delta, IResourceDelta.SYNC))
    {
      list.add("SYNC");
    }

    if (hasFlag(delta, IResourceDelta.MARKERS))
    {
      list.add("MARKERS");
    }

    if (hasFlag(delta, IResourceDelta.REPLACED))
    {
      list.add("REPLACED");
    }

    if (list.isEmpty())
    {
      return "";
    }

    return list.toString();
  }

  public static boolean hasFlag(IResourceDelta delta, int flag)
  {
    return (delta.getFlags() & flag) != 0;
  }
}
