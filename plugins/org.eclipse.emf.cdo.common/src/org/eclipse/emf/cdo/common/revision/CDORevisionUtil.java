/*
 * Copyright (c) 2008-2016, 2018-2023, 2025 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.id.CDOWithID;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.commit.CDOChangeSetDataImpl;
import org.eclipse.emf.cdo.internal.common.commit.CDOChangeSetImpl;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisableImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionCacheAuditing;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionCacheBranching;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionCacheNonAuditing;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionKeyImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionManagerImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.ManagedRevisionProvider;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Consumer;

/**
 * Various static helper methods for dealing with {@link CDORevision revisions}.
 *
 * @author Eike Stepper
 */
public final class CDORevisionUtil
{
  public static final Object UNINITIALIZED = new Uninitialized();

  private static EAttribute resourceNodeNameAttribute;

  private CDORevisionUtil()
  {
  }

  /**
   * Creates and returns a new memory sensitive revision cache.
   *
   * @since 4.0
   */
  public static CDORevisionCache createRevisionCache(boolean supportingAudits, boolean supportingBranches)
  {
    if (supportingBranches)
    {
      return new CDORevisionCacheBranching();
    }

    if (supportingAudits)
    {
      return new CDORevisionCacheAuditing();
    }

    return new CDORevisionCacheNonAuditing();
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
   * @since 4.0
   */
  public static CDORevisable copyRevisable(CDORevisable source)
  {
    return new CDORevisableImpl(source);
  }

  /**
   * @since 4.0
   */
  public static CDORevisable createRevisable(CDOBranch branch, int version, long timeStamp, long revised)
  {
    return new CDORevisableImpl(branch, version, timeStamp, revised);
  }

  /**
   * @since 4.0
   */
  public static CDORevisionKey copyRevisionKey(CDORevisionKey source)
  {
    return new CDORevisionKeyImpl(source.getID(), source.getBranch(), source.getVersion());
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
   * @deprecated As of 4.5 {@link org.eclipse.emf.ecore.util.FeatureMap feature maps} are no longer supported.
   */
  @Deprecated
  public static org.eclipse.emf.ecore.util.FeatureMap.Entry createFeatureMapEntry(EStructuralFeature feature, Object value)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 3.0
   * @deprecated As of 4.5 {@link org.eclipse.emf.ecore.util.FeatureMap feature maps} are no longer supported.
   */
  @Deprecated
  public static org.eclipse.emf.cdo.spi.common.revision.CDOFeatureMapEntry createCDOFeatureMapEntry()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 4.0
   */
  public static CDORevisionDelta createDelta(CDORevision revision)
  {
    return new CDORevisionDeltaImpl(revision);
  }

  /**
   * @since 4.0
   */
  public static CDOChangeSetData createChangeSetData(Set<CDOID> ids, final CDOBranchPoint startPoint, final CDOBranchPoint endPoint,
      final CDORevisionManager revisionManager)
  {
    CDORevisionProvider startProvider = new ManagedRevisionProvider(revisionManager, startPoint);
    CDORevisionProvider endProvider = new ManagedRevisionProvider(revisionManager, endPoint);
    return createChangeSetData(ids, startProvider, endProvider);
  }

  /**
   * @since 4.0
   */
  public static CDOChangeSetData createChangeSetData(Set<CDOID> ids, CDORevisionProvider startProvider, CDORevisionProvider endProvider)
  {
    return createChangeSetData(ids, startProvider, endProvider, false);
  }

  /**
   * @since 4.1
   */
  public static CDOChangeSetData createChangeSetData(Set<CDOID> ids, CDORevisionProvider startProvider, CDORevisionProvider endProvider,
      boolean useStartVersions)
  {
    List<CDOIDAndVersion> newObjects = new ArrayList<>();
    List<CDORevisionKey> changedObjects = new ArrayList<>();
    List<CDOIDAndVersion> detachedObjects = new ArrayList<>();
    for (CDOID id : ids)
    {
      CDORevision startRevision = startProvider.getRevision(id);
      CDORevision endRevision = endProvider.getRevision(id);

      if (startRevision == null && endRevision != null)
      {
        if (useStartVersions)
        {
          ((InternalCDORevision)endRevision).setVersion(0);
        }

        newObjects.add(endRevision);
      }
      else if (startRevision != null && endRevision == null)
      {
        detachedObjects.add(CDOIDUtil.createIDAndVersion(id, CDOBranchVersion.UNSPECIFIED_VERSION));
      }
      else if (startRevision != null && endRevision != null)
      {
        if (!startRevision.equals(endRevision))
        {
          if (useStartVersions)
          {
            ((InternalCDORevision)endRevision).setVersion(startRevision.getVersion());
          }

          CDORevisionDelta delta = endRevision.compare(startRevision);
          if (!delta.isEmpty())
          {
            changedObjects.add(delta);
          }
        }
      }
    }

    return createChangeSetData(newObjects, changedObjects, detachedObjects);
  }

  /**
   * @since 4.0
   */
  public static CDOChangeSetData createChangeSetData(List<CDOIDAndVersion> newObjects, List<CDORevisionKey> changedObjects,
      List<CDOIDAndVersion> detachedObjects)
  {
    return new CDOChangeSetDataImpl(newObjects, changedObjects, detachedObjects);
  }

  /**
   * @since 4.0
   */
  public static CDOChangeSet createChangeSet(CDOBranchPoint startPoint, CDOBranchPoint endPoint, CDOChangeSetData data)
  {
    return new CDOChangeSetImpl(startPoint, endPoint, data);
  }

  /**
   * @since 3.0
   */
  public static Object remapID(Object value, Map<CDOID, CDOID> idMappings, boolean allowUnmappedTempIDs)
  {
    return CDORevisionImpl.remapID(value, idMappings, allowUnmappedTempIDs);
  }

  /**
   * @since 4.5
   */
  public static boolean isTreeRestructuring(InternalCDORevisionDelta[] deltas)
  {
    for (InternalCDORevisionDelta delta : deltas)
    {
      for (CDOFeatureDelta featureDelta : delta.getFeatureDeltas())
      {
        EStructuralFeature feature = featureDelta.getFeature();
        if (feature == CDOContainerFeatureDelta.CONTAINER_FEATURE)
        {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * @since 4.3
   */
  public static boolean isContained(CDOID child, CDOID container, CDORevisionProvider provider)
  {
    InternalCDORevision revision = (InternalCDORevision)provider.getRevision(child);
    return isContained(revision, container, provider);
  }

  /**
   * @since 4.3
   */
  public static boolean isContained(InternalCDORevision child, CDOID container, CDORevisionProvider provider)
  {
    if (child.getID() == container)
    {
      return true;
    }

    CDORevision parent = getParentRevision(child, provider);
    if (parent != null)
    {
      return isContained((InternalCDORevision)parent, container, provider);
    }

    return false;
  }

  /**
   * @since 4.5
   */
  public static void handleParentRevisions(CDORevision revision, CDORevisionProvider provider, CDORevisionHandler handler)
  {
    CDORevision parentRevision = getParentRevision(revision, provider);
    if (parentRevision != null)
    {
      if (handler.handleRevision(parentRevision))
      {
        handleParentRevisions(parentRevision, provider, handler);
      }
    }
  }

  /**
   * @since 4.5
   */
  public static CDORevision getParentRevision(CDORevision revision, CDORevisionProvider provider)
  {
    CDOID parentID;
    CDORevisionData data = revision.data();

    Object containerID = data.getContainerID();
    if (containerID instanceof CDOWithID)
    {
      parentID = ((CDOWithID)containerID).cdoID();
    }
    else
    {
      parentID = (CDOID)containerID;
    }

    if (CDOIDUtil.isNull(parentID))
    {
      parentID = data.getResourceID();
      if (CDOIDUtil.isNull(parentID))
      {
        return null;
      }

      if (parentID == revision.getID())
      {
        // This must be the root resource!
        return null;
      }
    }

    return provider.getRevision(parentID);
  }

  /**
   * @since 4.26
   */
  public static CDORevision getParentRevision(CDORevision revision, CDORevisionProvider provider, EClass type)
  {
    CDORevision parentRevision = getParentRevision(revision, provider);
    if (parentRevision != null && !type.isSuperTypeOf(parentRevision.getEClass()))
    {
      parentRevision = getParentRevision(parentRevision, provider, type);
    }

    return parentRevision;
  }

  /**
   * @since 4.21
   */
  public static boolean hasChildRevisions(CDORevision container)
  {
    InternalCDORevision revisionData = (InternalCDORevision)container;
    CDOClassInfo classInfo = revisionData.getClassInfo();

    for (EStructuralFeature feature : classInfo.getAllPersistentContainments())
    {
      if (feature instanceof EReference)
      {
        if (feature.isMany())
        {
          CDOList list = revisionData.getListOrNull(feature);
          if (list != null && !list.isEmpty())
          {
            return true;
          }
        }
        else
        {
          Object value = revisionData.getValue(feature);
          if (value != null)
          {
            return true;
          }
        }
      }
    }

    return false;
  }

  /**
   * @since 4.5
   */
  /**
   * @since 4.4
   */
  public static List<CDORevision> getChildRevisions(CDOID container, CDORevisionProvider provider)
  {
    return getChildRevisions(container, provider, false);
  }

  /**
   * @since 4.5
   */
  public static List<CDORevision> getChildRevisions(CDOID container, CDORevisionProvider provider, boolean onlyProperContents)
  {
    CDORevision revision = provider.getRevision(container);
    return getChildRevisions(revision, provider, onlyProperContents);
  }

  /**
   * @since 4.4
   */
  public static List<CDORevision> getChildRevisions(CDORevision container, CDORevisionProvider provider)
  {
    return getChildRevisions(container, provider, false);
  }

  /**
   * @since 4.5
   */
  public static List<CDORevision> getChildRevisions(CDORevision container, CDORevisionProvider provider, boolean onlyProperContents)
  {
    List<CDORevision> children = new ArrayList<>();
    forEachChildRevision(container, provider, onlyProperContents, children::add);
    return children;
  }

  /**
   * @since 4.18
   */
  public static void forEachChildRevision(CDOID container, CDORevisionProvider provider, boolean onlyProperContents, Consumer<CDORevision> consumer)
  {
    CDORevision revision = provider.getRevision(container);
    forEachChildRevision(revision, provider, onlyProperContents, consumer);
  }

  /**
   * @since 4.18
   */
  public static void forEachChildRevision(CDORevision container, CDORevisionProvider provider, boolean onlyProperContents, Consumer<CDORevision> consumer)
  {
    InternalCDORevision revisionData = (InternalCDORevision)container;
    CDOClassInfo classInfo = revisionData.getClassInfo();

    if (onlyProperContents && classInfo.isResource())
    {
      onlyProperContents = false;
    }

    for (EStructuralFeature feature : classInfo.getAllPersistentContainments())
    {
      if (feature instanceof EReference)
      {
        if (feature.isMany())
        {
          CDOList list = revisionData.getListOrNull(feature);
          if (list != null)
          {
            for (Object value : list)
            {
              forChildRevision(value, provider, onlyProperContents, consumer);
            }
          }
        }
        else
        {
          Object value = revisionData.getValue(feature);
          forChildRevision(value, provider, onlyProperContents, consumer);
        }
      }
    }
  }

  private static void forChildRevision(Object value, CDORevisionProvider provider, boolean onlyProperContents, Consumer<CDORevision> consumer)
  {
    if (value instanceof CDOID)
    {
      CDOID id = (CDOID)value;
      CDORevision child = provider.getRevision(id);
      if (child != null)
      {
        if (onlyProperContents)
        {
          // Check proper contents (i.e., no containment proxy).
          CDOID resourceID = child.data().getResourceID();
          if (!CDOIDUtil.isNull(resourceID))
          {
            return;
          }
        }

        consumer.accept(child);
      }
    }
  }

  /**
   * @since 4.15
   */
  public static String getResourceNodeName(CDORevision revision)
  {
    EAttribute attribute = getResourceNodeNameAttribute(revision);
    if (attribute != null)
    {
      return (String)((InternalCDORevision)revision).get(attribute, 0);
    }

    return null;
  }

  /**
   * @since 4.3
   */
  public static String getResourceNodePath(CDOID id, CDORevisionProvider provider)
  {
    CDORevision revision = provider.getRevision(id);
    return getResourceNodePath(revision, provider);
  }

  /**
   * @since 4.0
   */
  public static String getResourceNodePath(CDORevision revision, CDORevisionProvider provider)
  {
    StringBuilder builder = new StringBuilder();
    getResourceNodePath((InternalCDORevision)revision, provider, builder);
    return builder.toString();
  }

  private static void getResourceNodePath(InternalCDORevision revision, CDORevisionProvider provider, StringBuilder result)
  {
    InternalCDORevision container = null;
    if (!revision.isResourceNode())
    {
      container = getResourceRevision(revision, provider);
    }
    else
    {
      container = (InternalCDORevision)getParentRevision(revision, provider);
    }

    if (container != null)
    {
      getResourceNodePath(container, provider, result);
    }

    EAttribute attribute = getResourceNodeNameAttribute(revision);
    if (attribute != null)
    {
      int length = result.length();
      if (length == 0 || result.charAt(length - 1) != '/')
      {
        result.append("/");
      }

      String name = (String)revision.get(attribute, 0);
      if (name != null) // Exclude root resource
      {
        result.append(name);
      }
    }
  }

  private static InternalCDORevision getResourceRevision(InternalCDORevision revision, CDORevisionProvider provider)
  {
    CDOID resourceID = revision.getResourceID();
    if (revision.isResourceNode())
    {
      return null;
    }

    if (CDOIDUtil.isNull(resourceID))
    {
      CDOID parentID = null;
      Object containerID = revision.getContainerID();
      if (containerID instanceof CDOWithID)
      {
        parentID = ((CDOWithID)containerID).cdoID();
      }
      else
      {
        parentID = (CDOID)containerID;
      }

      if (!CDOIDUtil.isNull(parentID))
      {
        InternalCDORevision parentRevision = (InternalCDORevision)provider.getRevision(parentID);
        return getResourceRevision(parentRevision, provider);
      }
    }

    return (InternalCDORevision)provider.getRevision(resourceID);
  }

  private static EAttribute getResourceNodeNameAttribute(CDORevision revision)
  {
    if (revision.isResourceNode())
    {
      if (resourceNodeNameAttribute == null)
      {
        resourceNodeNameAttribute = (EAttribute)revision.getEClass().getEStructuralFeature("name");
      }

      return resourceNodeNameAttribute;
    }

    return null;
  }

  /**
   * @since 4.8
   */
  public static CDOListFeatureDelta compareLists(CDORevision originRevision, CDORevision dirtyRevision, EStructuralFeature feature)
  {
    return CDORevisionDeltaImpl.compareLists((InternalCDORevision)originRevision, (InternalCDORevision)dirtyRevision, feature);
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
   * Dumps {@link CDORevision revisions}, sorted and grouped by {@link CDOBranch branch}, to various output formats and
   * targets. Concrete output formats and targets are implemented by subclasses.
   *
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
      ArrayList<CDOBranch> branches = new ArrayList<>(map.keySet());
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
     * A {@link AllRevisionsDumper revision dumper} that directs all output to a stream. The concrete output format is
     * implemented by subclasses.
     *
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
       * A {@link Stream revision dumper} that directs all output as plain text to a stream.
       *
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
          out().println(padTimeRange(branch.getName() + "[" + branch.getID() + "]", pad, branch.getBase().getTimeStamp(), //$NON-NLS-1$ //$NON-NLS-2$
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
       * A {@link Stream revision dumper} that directs all output as HTML text to a stream.
       *
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
          if (!branch.isMainBranch())
          {
            out.println("<tr><td>&nbsp;</td><td>&nbsp;</td></tr>");
          }

          out.println("<tr>");
          out.println("<td>");
          out.println("<h4>" + branch.getName() + " [" + branch.getID() + "]</h4>");
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
          out.println(CDOCommonUtil.formatTimeStamp(revision.getTimeStamp()) + " / " + CDOCommonUtil.formatTimeStamp(revision.getRevised()));
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
   * Compares {@link CDORevisionKey revision keys} by {@link CDORevision#getID() ID} and
   * {@link CDORevision#getVersion() version}.
   *
   * @author Eike Stepper
   * @since 4.0
   */
  public static class CDORevisionComparator implements Comparator<CDORevisionKey>
  {
    public CDORevisionComparator()
    {
    }

    @Override
    public int compare(CDORevisionKey rev1, CDORevisionKey rev2)
    {
      int result = rev1.getID().compareTo(rev2.getID());
      if (result == 0)
      {
        int version1 = rev1.getVersion();
        int version2 = rev2.getVersion();
        result = version1 < version2 ? -1 : version1 == version2 ? 0 : 1;
      }

      return result;
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
