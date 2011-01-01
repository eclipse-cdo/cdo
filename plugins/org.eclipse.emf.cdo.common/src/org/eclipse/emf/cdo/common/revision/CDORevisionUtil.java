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
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.internal.common.revision.CDOFeatureMapEntryImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionCacheImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionKeyImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionManagerImpl;
import org.eclipse.emf.cdo.spi.common.revision.CDOFeatureMapEntry;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Eike Stepper
 */
public final class CDORevisionUtil
{
  public static final Object UNINITIALIZED = new Uninitialized();

  private CDORevisionUtil()
  {
  }

  /**
   * Creates and returns a new memory sensitive revision cache that supports branches.
   * 
   * @since 4.0
   */
  public static CDORevisionCache createRevisionCache()
  {
    return new CDORevisionCacheImpl();
  }

  /**
   * @since 4.0
   */
  public static CDORevisionManager createRevisionManager()
  {
    return new CDORevisionManagerImpl();
  }

  /**
   * @since 4.0
   */
  public static CDORevisionManager createRevisionManager(CDORevisionCache cache)
  {
    InternalCDORevisionManager revisionManager = (InternalCDORevisionManager)createRevisionManager();
    revisionManager.setCache(cache);
    return revisionManager;
  }

  /**
   * @since 3.0
   */
  public static CDORevisionKey createRevisionKey(CDOID id, CDOBranch branch, int version)
  {
    return new CDORevisionKeyImpl(id, branch, version);
  }

  /**
   * @since 4.0
   */
  public static CDORevisionKey copyRevisionKey(CDORevisionKey source)
  {
    return new CDORevisionKeyImpl(source.getID(), source.getBranch(), source.getVersion());
  }

  /**
   * @since 4.0
   */
  public static String formatRevisionKey(CDORevisionKey key)
  {
    StringBuilder builder = new StringBuilder();
    CDOIDUtil.write(builder, key.getID());
    builder.append(":");
    builder.append(key.getBranch().getID());
    builder.append(":");
    builder.append(key.getVersion());
    return builder.toString();
  }

  /**
   * @since 4.0
   */
  public static CDORevisionKey parseRevisionKey(String source, CDOBranchManager branchManager)
  {
    StringTokenizer tokenizer = new StringTokenizer(source, ":");
    if (!tokenizer.hasMoreTokens())
    {
      throw new IllegalArgumentException("No ID segment");
    }

    String idSegment = tokenizer.nextToken();
    CDOID id = CDOIDUtil.read(idSegment);

    if (!tokenizer.hasMoreTokens())
    {
      throw new IllegalArgumentException("No branch segment");
    }

    String branchSegment = tokenizer.nextToken();
    CDOBranch branch = branchManager.getBranch(Integer.parseInt(branchSegment));

    if (!tokenizer.hasMoreTokens())
    {
      throw new IllegalArgumentException("No version segment");
    }

    String versionSegment = tokenizer.nextToken();
    int version = Integer.parseInt(versionSegment);

    return new CDORevisionKeyImpl(id, branch, version);
  }

  /**
   * @since 2.0
   */
  public static FeatureMap.Entry createFeatureMapEntry(EStructuralFeature feature, Object value)
  {
    return new CDOFeatureMapEntryImpl(feature, value);
  }

  /**
   * @since 3.0
   */
  public static CDOFeatureMapEntry createCDOFeatureMapEntry()
  {
    return new CDOFeatureMapEntryImpl();
  }

  /**
   * @since 3.0
   */
  public static Object remapID(Object value, Map<CDOID, CDOID> idMappings, boolean allowUnmappedTempIDs)
  {
    return CDORevisionImpl.remapID(value, idMappings, allowUnmappedTempIDs);
  }

  /**
   * @since 3.0
   */
  public static String dumpAllRevisions(Map<CDOBranch, List<CDORevision>> map)
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(baos);
    dumpAllRevisions(map, out);
    return baos.toString();
  }

  /**
   * @since 3.0
   */
  public static void dumpAllRevisions(Map<CDOBranch, List<CDORevision>> map, PrintStream out)
  {
    new AllRevisionsDumper.Stream.Plain(map, out).dump();
  }

  /**
   * @since 4.0
   */
  public static abstract class AllRevisionsDumper
  {
    private Map<CDOBranch, List<CDORevision>> map;

    public AllRevisionsDumper(Map<CDOBranch, List<CDORevision>> map)
    {
      this.map = map;
    }

    public Map<CDOBranch, List<CDORevision>> getMap()
    {
      return map;
    }

    public void dump()
    {
      ArrayList<CDOBranch> branches = new ArrayList<CDOBranch>(map.keySet());
      Collections.sort(branches);

      dumpStart(branches);
      for (CDOBranch branch : branches)
      {
        dumpBranch(branch);

        List<CDORevision> revisions = map.get(branch);
        Collections.sort(revisions, new CDORevisionComparator());

        for (CDORevision revision : revisions)
        {
          dumpRevision(revision);
        }
      }

      dumpEnd(branches);
    }

    protected void dumpStart(List<CDOBranch> branches)
    {
    }

    protected void dumpEnd(List<CDOBranch> branches)
    {
    }

    protected abstract void dumpBranch(CDOBranch branch);

    protected abstract void dumpRevision(CDORevision revision);

    /**
     * @author Eike Stepper
     */
    public static abstract class Stream extends AllRevisionsDumper
    {
      private PrintStream out;

      public Stream(Map<CDOBranch, List<CDORevision>> map, PrintStream out)
      {
        super(map);
        this.out = out;
      }

      public PrintStream out()
      {
        return out;
      }

      /**
       * @author Eike Stepper
       */
      public static class Plain extends Stream
      {
        public static final int pad = 48;

        public Plain(Map<CDOBranch, List<CDORevision>> map, PrintStream out)
        {
          super(map, out);
        }

        @Override
        protected void dumpEnd(List<CDOBranch> branches)
        {
          out().println();
        }

        @Override
        protected void dumpBranch(CDOBranch branch)
        {
          out().println(
              padTimeRange(branch.getName() + "[" + branch.getID() + "]", pad, branch.getBase().getTimeStamp(), //$NON-NLS-1$ //$NON-NLS-2$
                  CDORevision.UNSPECIFIED_DATE));
        }

        @Override
        protected void dumpRevision(CDORevision revision)
        {
          out().println(padTimeRange("  " + revision, pad, revision.getTimeStamp(), revision.getRevised())); //$NON-NLS-1$
        }

        private static String padTimeRange(String s, int pos, long t1, long t2)
        {
          StringBuffer buffer = new StringBuffer(s);
          while (buffer.length() < pos)
          {
            buffer.append(' ');
          }

          buffer.append(CDOCommonUtil.formatTimeStamp(t1));
          buffer.append("/");
          buffer.append(CDOCommonUtil.formatTimeStamp(t2));
          return buffer.toString();
        }
      }

      /**
       * @author Eike Stepper
       */
      public static class Html extends Stream
      {
        public Html(Map<CDOBranch, List<CDORevision>> map, PrintStream out)
        {
          super(map, out);
        }

        @Override
        protected void dumpStart(List<CDOBranch> branches)
        {
          out().println("<table border=\"0\">");
        }

        @Override
        protected void dumpEnd(List<CDOBranch> branches)
        {
          out().println("</table>");
        }

        @Override
        protected void dumpBranch(CDOBranch branch)
        {
          PrintStream out = out();
          out.println("<tr>");
          out.println("<td>");
          out.println("<h4>" + branch.getName() + "[" + branch.getID() + "]</h4>");
          out.println("</td>");
          out.println("<td>");
          out.println("<h4>" + CDOCommonUtil.formatTimeStamp(branch.getBase().getTimeStamp()) + " / "
              + CDOCommonUtil.formatTimeStamp(CDORevision.UNSPECIFIED_DATE) + "</h4>");
          out.println("</td>");
          out.println("</tr>");
        }

        @Override
        protected void dumpRevision(CDORevision revision)
        {
          PrintStream out = out();

          out.println("<tr>");
          out.println("<td>&nbsp;&nbsp;&nbsp;&nbsp;");
          dumpRevision(revision, out);
          out.println("&nbsp;&nbsp;&nbsp;&nbsp;</td>");

          out.println("<td>");
          out.println(CDOCommonUtil.formatTimeStamp(revision.getTimeStamp()) + " / "
              + CDOCommonUtil.formatTimeStamp(revision.getRevised()));
          out.println("</td>");
          out.println("</tr>");
        }

        protected void dumpRevision(CDORevision revision, PrintStream out)
        {
          out.println(revision);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Uninitialized
  {
    public Uninitialized()
    {
    }

    @Override
    public String toString()
    {
      return Messages.getString("CDORevisionUtil.0"); //$NON-NLS-1$
    }
  }
}
