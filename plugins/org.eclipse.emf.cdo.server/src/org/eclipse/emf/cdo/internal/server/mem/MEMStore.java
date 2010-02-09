/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Simon McDuff - bug 233273
 *    Eike Stepper - maintenance
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.cdo.internal.server.mem;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOModelConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IMEMStore;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.SyntheticCDORevision;
import org.eclipse.emf.cdo.spi.server.LongIDStore;
import org.eclipse.emf.cdo.spi.server.StoreAccessorPool;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Simon McDuff
 */
public class MEMStore extends LongIDStore implements IMEMStore, BranchLoader
{
  public static final String TYPE = "mem"; //$NON-NLS-1$

  private long creationTime;

  private Map<Integer, BranchInfo> branchInfos = new HashMap<Integer, BranchInfo>();

  private Map<Object, List<InternalCDORevision>> revisions = new HashMap<Object, List<InternalCDORevision>>();

  private List<CommitInfo> commitInfos = new ArrayList<CommitInfo>();

  private int listLimit;

  @ExcludeFromDump
  private transient EStructuralFeature resourceNameFeature;

  /**
   * @param listLimit
   *          See {@link #setListLimit(int)}.
   * @since 2.0
   */
  public MEMStore(int listLimit)
  {
    super(TYPE, set(ChangeFormat.REVISION, ChangeFormat.DELTA), set(RevisionTemporality.NONE,
        RevisionTemporality.AUDITING), set(RevisionParallelism.NONE, RevisionParallelism.BRANCHING));
    setRevisionTemporality(RevisionTemporality.AUDITING);
    setRevisionParallelism(RevisionParallelism.BRANCHING);
    this.listLimit = listLimit;
  }

  public MEMStore()
  {
    this(UNLIMITED);
  }

  public synchronized int createBranch(BranchInfo branchInfo)
  {
    int id = branchInfos.size() + 1;
    branchInfos.put(id, branchInfo);
    return id;
  }

  public synchronized BranchInfo loadBranch(int branchID)
  {
    return branchInfos.get(branchID);
  }

  public synchronized SubBranchInfo[] loadSubBranches(int branchID)
  {
    List<SubBranchInfo> result = new ArrayList<SubBranchInfo>();
    for (Entry<Integer, BranchInfo> entry : branchInfos.entrySet())
    {
      BranchInfo branchInfo = entry.getValue();
      if (branchInfo.getBaseBranchID() == branchID)
      {
        int id = entry.getKey();
        result.add(new SubBranchInfo(id, branchInfo.getName(), branchInfo.getBaseTimeStamp()));
      }
    }

    return result.toArray(new SubBranchInfo[result.size()]);
  }

  public synchronized void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    InternalCDOCommitInfoManager manager = getRepository().getCommitInfoManager();
    for (int i = 0; i < commitInfos.size(); i++)
    {
      CommitInfo info = commitInfos.get(i);
      if (startTime != CDOBranchPoint.UNSPECIFIED_DATE && info.getTimeStamp() < startTime)
      {
        continue;
      }

      if (endTime != CDOBranchPoint.UNSPECIFIED_DATE && info.getTimeStamp() > endTime)
      {
        continue;
      }

      if (branch != null && info.getBranch() != branch)
      {
        continue;
      }

      info.handle(manager, handler);
    }
  }

  /**
   * @since 2.0
   */
  public int getListLimit()
  {
    return listLimit;
  }

  /**
   * @since 2.0
   */
  public synchronized void setListLimit(int listLimit)
  {
    if (listLimit != UNLIMITED && this.listLimit != listLimit)
    {
      for (List<InternalCDORevision> list : revisions.values())
      {
        enforceListLimit(list);
      }
    }

    this.listLimit = listLimit;
  }

  /**
   * @since 2.0
   */
  public synchronized List<InternalCDORevision> getCurrentRevisions()
  {
    ArrayList<InternalCDORevision> simpleRevisions = new ArrayList<InternalCDORevision>();
    Iterator<List<InternalCDORevision>> itr = revisions.values().iterator();
    while (itr.hasNext())
    {
      List<InternalCDORevision> list = itr.next();
      InternalCDORevision revision = list.get(list.size() - 1);
      simpleRevisions.add(revision);
    }

    return simpleRevisions;
  }

  public synchronized InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    if (getRepository().isSupportingAudits())
    {
      Object listKey = getListKey(id, branchVersion.getBranch());
      List<InternalCDORevision> list = revisions.get(listKey);
      if (list != null)
      {
        return getRevisionByVersion(list, branchVersion.getVersion());
      }

      return null;
    }

    throw new UnsupportedOperationException();
  }

  /**
   * @since 2.0
   */
  public synchronized InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    Object listKey = getListKey(id, branchPoint.getBranch());
    if (branchPoint.getTimeStamp() == CDORevision.UNSPECIFIED_DATE)
    {
      List<InternalCDORevision> list = revisions.get(listKey);
      if (list != null)
      {
        return list.get(list.size() - 1);
      }

      return null;
    }

    if (!getRepository().isSupportingAudits())
    {
      throw new UnsupportedOperationException("Auditing not supported");
    }

    List<InternalCDORevision> list = revisions.get(listKey);
    if (list != null)
    {
      return getRevision(list, branchPoint);
    }

    return null;
  }

  public synchronized void addRevision(InternalCDORevision revision)
  {
    Object listKey = getListKey(revision.getID(), revision.getBranch());
    List<InternalCDORevision> list = revisions.get(listKey);
    if (list == null)
    {
      list = new ArrayList<InternalCDORevision>();
      revisions.put(listKey, list);
    }

    addRevision(list, revision);
  }

  public synchronized void addCommitInfo(CDOBranch branch, long timeStamp, String userID, String comment)
  {
    int index = commitInfos.size() - 1;
    while (index > 0)
    {
      CommitInfo info = commitInfos.get(index);
      if (timeStamp > info.getTimeStamp())
      {
        break;
      }
    }

    CommitInfo commitInfo = new CommitInfo(branch, timeStamp, userID, comment);
    commitInfos.add(index + 1, commitInfo);
  }

  /**
   * @since 2.0
   */
  public synchronized boolean rollbackRevision(InternalCDORevision revision)
  {
    CDOID id = revision.getID();
    CDOBranch branch = revision.getBranch();
    int version = revision.getVersion();

    Object listKey = getListKey(id, branch);
    List<InternalCDORevision> list = revisions.get(listKey);
    if (list == null)
    {
      return false;
    }

    for (Iterator<InternalCDORevision> it = list.iterator(); it.hasNext();)
    {
      InternalCDORevision rev = it.next();
      if (rev.getVersion() == version)
      {
        it.remove();
        return true;
      }
      else if (rev.getVersion() == version - 1)
      {
        rev.setRevised(CDORevision.UNSPECIFIED_DATE);
      }
    }

    return false;
  }

  /**
   * @since 3.0
   */
  public synchronized DetachedCDORevision detachObject(CDOID id, CDOBranch branch, long timeStamp)
  {
    Object listKey = getListKey(id, branch);
    List<InternalCDORevision> list = revisions.get(listKey);
    if (list != null)
    {
      InternalCDORevision revision = getRevision(list, branch.getHead());
      if (revision != null)
      {
        revision.setRevised(timeStamp - 1);
      }
    }

    int version;
    if (list == null)
    {
      list = new ArrayList<InternalCDORevision>();
      revisions.put(listKey, list);
      version = CDOBranchVersion.FIRST_VERSION;
    }
    else
    {
      version = getHighestVersion(list) + 1;
    }

    DetachedCDORevision detached = new DetachedCDORevision(id, branch, version, timeStamp);
    addRevision(list, detached);
    return detached;
  }

  /**
   * @since 2.0
   */
  public synchronized void queryResources(IStoreAccessor.QueryResourcesContext context)
  {
    CDOID folderID = context.getFolderID();
    String name = context.getName();
    boolean exactMatch = context.exactMatch();
    for (Entry<Object, List<InternalCDORevision>> entry : revisions.entrySet())
    {
      CDOBranch branch = getBranch(entry.getKey());
      if (branch != context.getBranch())
      {
        continue;
      }

      List<InternalCDORevision> list = entry.getValue();
      if (list.isEmpty())
      {
        continue;
      }

      InternalCDORevision revision = list.get(0);
      if (revision instanceof SyntheticCDORevision)
      {
        continue;
      }

      if (!revision.isResourceNode())
      {
        continue;
      }

      revision = getRevision(list, context);
      if (revision == null || revision instanceof DetachedCDORevision)
      {
        continue;
      }

      CDOID revisionFolder = (CDOID)revision.data().getContainerID();
      if (!CDOIDUtil.equals(revisionFolder, folderID))
      {
        continue;
      }

      String revisionName = (String)revision.data().get(resourceNameFeature, 0);
      boolean useEquals = exactMatch || revisionName == null || name == null;
      boolean match = useEquals ? ObjectUtil.equals(revisionName, name) : revisionName.startsWith(name);

      if (match)
      {
        if (!context.addResource(revision.getID()))
        {
          // No more results allowed
          break;
        }
      }
    }
  }

  @Override
  public MEMStoreAccessor createReader(ISession session)
  {
    return new MEMStoreAccessor(this, session);
  }

  /**
   * @since 2.0
   */
  @Override
  public MEMStoreAccessor createWriter(ITransaction transaction)
  {
    return new MEMStoreAccessor(this, transaction);
  }

  /**
   * @since 2.0
   */
  public long getCreationTime()
  {
    return creationTime;
  }

  public boolean isFirstTime()
  {
    return true;
  }

  public Map<CDOBranch, List<CDORevision>> getAllRevisions()
  {
    Map<CDOBranch, List<CDORevision>> result = new HashMap<CDOBranch, List<CDORevision>>();
    InternalCDOBranchManager branchManager = getRepository().getBranchManager();
    result.put(branchManager.getMainBranch(), new ArrayList<CDORevision>());

    for (Integer branchID : branchInfos.keySet())
    {
      InternalCDOBranch branch = branchManager.getBranch(branchID);
      result.put(branch, new ArrayList<CDORevision>());
    }

    for (List<InternalCDORevision> list : revisions.values())
    {
      for (InternalCDORevision revision : list)
      {
        CDOBranch branch = revision.getBranch();
        List<CDORevision> resultList = result.get(branch);
        resultList.add(revision);
      }
    }

    return result;
  }

  /**
   * @since 2.0
   */
  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    creationTime = System.currentTimeMillis();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    revisions.clear();
    super.doDeactivate();
  }

  @Override
  protected StoreAccessorPool getReaderPool(ISession session, boolean forReleasing)
  {
    // Pooling of store accessors not supported
    return null;
  }

  @Override
  protected StoreAccessorPool getWriterPool(IView view, boolean forReleasing)
  {
    // Pooling of store accessors not supported
    return null;
  }

  private Object getListKey(CDOID id, CDOBranch branch)
  {
    if (getRevisionParallelism() == RevisionParallelism.NONE)
    {
      return id;
    }

    return new ListKey(id, branch);
  }

  private CDOBranch getBranch(Object key)
  {
    if (key instanceof ListKey)
    {
      return ((ListKey)key).getBranch();
    }

    return getRepository().getBranchManager().getMainBranch();
  }

  private int getHighestVersion(List<InternalCDORevision> list)
  {
    int version = CDOBranchVersion.UNSPECIFIED_VERSION;
    for (InternalCDORevision revision : list)
    {
      if (revision.getVersion() > version)
      {
        version = revision.getVersion();
      }
    }

    return version;
  }

  private InternalCDORevision getRevisionByVersion(List<InternalCDORevision> list, int version)
  {
    for (InternalCDORevision revision : list)
    {
      if (revision.getVersion() == version)
      {
        return revision;
      }
    }

    return null;
  }

  private InternalCDORevision getRevision(List<InternalCDORevision> list, CDOBranchPoint branchPoint)
  {
    long timeStamp = branchPoint.getTimeStamp();
    for (InternalCDORevision revision : list)
    {
      if (timeStamp == CDORevision.UNSPECIFIED_DATE)
      {
        if (!revision.isHistorical())
        {
          return revision;
        }
      }
      else
      {
        if (revision.isValid(timeStamp))
        {
          return revision;
        }
      }
    }

    return null;
  }

  private void addRevision(List<InternalCDORevision> list, InternalCDORevision revision)
  {
    // Check version conflict
    int version = revision.getVersion();
    InternalCDORevision rev = getRevisionByVersion(list, version);
    if (rev != null)
    {
      throw new IllegalStateException("Concurrent modification of " + rev.getEClass().getName() + "@" + rev.getID());
    }

    // Revise old revision
    int oldVersion = version - 1;
    if (oldVersion >= CDORevision.UNSPECIFIED_VERSION)
    {
      InternalCDORevision oldRevision = getRevisionByVersion(list, oldVersion);
      if (oldRevision != null)
      {
        oldRevision.setRevised(revision.getTimeStamp() - 1);
      }
    }

    // Check duplicate resource
    if (!(revision instanceof SyntheticCDORevision) && revision.isResource())
    {
      checkDuplicateResource(revision);
    }

    // Adjust the list
    list.add(revision);
    if (listLimit != UNLIMITED)
    {
      enforceListLimit(list);
    }
  }

  private void checkDuplicateResource(InternalCDORevision revision)
  {
    if (resourceNameFeature == null)
    {
      resourceNameFeature = revision.getEClass().getEStructuralFeature(CDOModelConstants.RESOURCE_NODE_NAME_ATTRIBUTE);
    }

    CDOID revisionFolder = (CDOID)revision.data().getContainerID();
    String revisionName = (String)revision.data().get(resourceNameFeature, 0);

    IStoreAccessor accessor = StoreThreadLocal.getAccessor();

    CDOID resourceID = accessor.readResourceID(revisionFolder, revisionName, revision);
    if (!CDOIDUtil.isNull(resourceID))
    {
      throw new IllegalStateException("Duplicate resource: " + revisionName + " (folderID=" + revisionFolder + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
  }

  private void enforceListLimit(List<InternalCDORevision> list)
  {
    while (list.size() > listLimit)
    {
      list.remove(0);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ListKey
  {
    private CDOID id;

    private CDOBranch branch;

    public ListKey(CDOID id, CDOBranch branch)
    {
      this.id = id;
      this.branch = branch;
    }

    public CDOID getID()
    {
      return id;
    }

    public CDOBranch getBranch()
    {
      return branch;
    }

    @Override
    public int hashCode()
    {
      return id.hashCode() ^ branch.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj == this)
      {
        return true;
      }

      if (obj instanceof ListKey)
      {
        ListKey that = (ListKey)obj;
        return id.equals(that.getID()) && branch.equals(that.getBranch());
      }

      return false;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("{0}:{1}", id, branch.getID());
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class CommitInfo
  {
    private CDOBranch branch;

    private long timeStamp;

    private String userID;

    private String comment;

    public CommitInfo(CDOBranch branch, long timeStamp, String userID, String comment)
    {
      this.branch = branch;
      this.timeStamp = timeStamp;
      this.userID = userID;
      this.comment = comment;
    }

    public CDOBranch getBranch()
    {
      return branch;
    }

    public long getTimeStamp()
    {
      return timeStamp;
    }

    public void handle(InternalCDOCommitInfoManager manager, CDOCommitInfoHandler handler)
    {
      CDOCommitInfo commitInfo = manager.createCommitInfo(branch, timeStamp, userID, comment, null);
      handler.handleCommitInfo(commitInfo);
    }
  }
}
