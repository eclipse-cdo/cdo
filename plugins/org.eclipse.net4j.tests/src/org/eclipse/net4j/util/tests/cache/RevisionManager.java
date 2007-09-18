/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.tests.cache;

import org.eclipse.net4j.internal.util.cache.Cache;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class RevisionManager extends Cache<Revision>
{
  private Map<Integer, TimeLine> timeLines = new HashMap<Integer, TimeLine>();

  public RevisionManager()
  {
  }

  public Revision getRevision(int id, int version)
  {
    TimeLine timeLine = timeLines.get(id);
    if (timeLine == null)
    {
      timeLine = new TimeLine(id);
      timeLines.put(id, timeLine);
    }

    return timeLine.getRevision(version);
  }

  public void evictElements(int elementCount)
  {
  }

  protected Revision loadRevision(int id, int version)
  {
    Revision revision = new Revision(id, version);
    System.out.println("Loaded " + revision + " (free=" + Runtime.getRuntime().freeMemory() + ", total="
        + Runtime.getRuntime().totalMemory() + ", max=" + Runtime.getRuntime().maxMemory() + ")");
    return revision;
  }

  protected int getRevisionSize(Revision revision)
  {
    return 1;
  }

  @Override
  protected void unreachableElement(Reference<? extends Revision> reference)
  {
    System.out.println("Unreachable: " + reference);
  }

  /**
   * @author Eike Stepper
   */
  private class TimeLine
  {
    private int id;

    private List<Reference<Revision>> revisions = new ArrayList<Reference<Revision>>();

    public TimeLine(int id)
    {
      this.id = id;
    }

    public int getID()
    {
      return id;
    }

    public Revision getRevision(int version)
    {
      for (Iterator<Reference<Revision>> it = revisions.iterator(); it.hasNext();)
      {
        Reference<Revision> reference = it.next();
        Revision revision = reference.get();
        if (revision != null)
        {
          if (revision.getVersion() == version)
          {
            return revision;
          }
        }
        else
        {
          it.remove();
          break;
        }
      }

      long time = System.currentTimeMillis();
      Revision revision = loadRevision(id, version);
      time = System.currentTimeMillis() - time;
      int size = getRevisionSize(revision);

      revisions.add(new CacheElement(revision, getReferenceQueue()));
      return revision;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class CacheElement extends WeakReference<Revision>
  {
    private int id;

    private int version;

    public CacheElement(Revision revision, ReferenceQueue<Revision> queue)
    {
      super(revision, queue);
      id = revision.getID();
      version = revision.getVersion();
    }

    public int getID()
    {
      return id;
    }

    public int getVersion()
    {
      return version;
    }

    public Revision getRevision()
    {
      return get();
    }

    @Override
    public String toString()
    {
      return "Ref" + id + "v" + version + (get() == null ? " cleared" : " set");
    }
  }
}
