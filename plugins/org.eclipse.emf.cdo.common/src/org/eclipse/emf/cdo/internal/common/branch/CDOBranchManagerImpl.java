/*
 * Copyright (c) 2010-2013, 2015, 2016, 2019-2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.branch;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent;
import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent.ChangeKind;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager.CDOTagList.TagListEvent;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager.CDOTagList.TagRenamedEvent;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchTag;
import org.eclipse.emf.cdo.common.branch.CDOBranchTag.TagEvent;
import org.eclipse.emf.cdo.common.branch.CDOBranchTag.TagMovedEvent;
import org.eclipse.emf.cdo.common.branch.CDODuplicateBranchException;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.common.util.CDOTimeProvider;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.ContainerEvent;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.RemoteException;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.ref.ReferenceValueMap;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Eike Stepper
 */
public class CDOBranchManagerImpl extends Container<CDOBranch> implements InternalCDOBranchManager
{
  private CDOCommonRepository repository;

  private BranchLoader branchLoader;

  private InternalCDOBranch mainBranch;

  private Map<Integer, InternalCDOBranch> branches = createBranchMap();

  private Map<String, CDOBranchTagImpl> tags = createTagMap();

  private Reference<CDOTagListImpl> tagListReference;

  private List<TagChange> tagChangeQueue = new ArrayList<>();

  private int tagModCount = -1;

  public CDOBranchManagerImpl()
  {
  }

  @Override
  public CDOCommonRepository getRepository()
  {
    return repository;
  }

  @Override
  public void setRepository(CDOCommonRepository repository)
  {
    this.repository = repository;
  }

  @Override
  public BranchLoader getBranchLoader()
  {
    return branchLoader;
  }

  @Override
  public void setBranchLoader(BranchLoader branchLoader)
  {
    checkInactive();
    this.branchLoader = branchLoader;
  }

  @Override
  public CDOTimeProvider getTimeProvider()
  {
    return repository;
  }

  @Override
  @Deprecated
  public void setTimeProvider(CDOTimeProvider timeProvider)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void initMainBranch(boolean local, long timeStamp)
  {
    mainBranch = new CDOBranchImpl.Main(this, local, timeStamp);
  }

  @Override
  @Deprecated
  public void handleBranchCreated(InternalCDOBranch branch)
  {
    handleBranchChanged(branch, ChangeKind.CREATED);
  }

  @Override
  @Deprecated
  public void handleBranchChanged(InternalCDOBranch branch, ChangeKind changeKind)
  {
    handleBranchChanged(branch, changeKind, branch.getID());
  }

  @Override
  public void handleBranchChanged(InternalCDOBranch branch, ChangeKind changeKind, int... branchIDs)
  {
    switch (changeKind)
    {
    case CREATED:
    {
      CDOBranchPoint base = branch.getBase();
      InternalCDOBranch baseBranch = (InternalCDOBranch)base.getBranch();
      baseBranch.addChild(branch);

      fireEvent(new BranchCreatedEvent(branch));
      break;
    }

    case RENAMED:
    {
      // branch.basicSetName() has already been called in BranchNotificationIndication.indicating()
      fireEvent(new BranchChangedEvent(branch, changeKind, branchIDs));
      break;
    }

    case DELETED:
    {
      CDOBranch[] deletedBranches = new CDOBranch[branchIDs.length];

      synchronized (branches)
      {
        for (int i = 0; i < branchIDs.length; i++)
        {
          int branchID = branchIDs[i];

          try
          {
            branch = branches.get(branchID);
            if (branch != null)
            {
              branch.setDeleted();
              deletedBranches[i] = branch;
            }
          }
          catch (Exception ex)
          {
            OM.LOG.error(ex);
          }
        }
      }

      CDOTagListImpl tagList = getTagListOrNull();
      if (tagList != null)
      {
        tagList.branchesDeleted(deletedBranches);
      }

      fireBranchDeletedEvents(deletedBranches);
      fireEvent(new BranchChangedEvent(branch, changeKind, branchIDs));
      break;
    }

    default:
      break;
    }
  }

  private void fireBranchDeletedEvents(CDOBranch[] branches)
  {
    for (CDOBranch branch : branches)
    {
      if (branch != null)
      {
        ((InternalCDOBranch)branch).fireDeletedEvent();
      }
    }
  }

  @Override
  public CDOBranch[] getElements()
  {
    return new CDOBranch[] { getMainBranch() };
  }

  @Override
  public InternalCDOBranch getMainBranch()
  {
    checkActive();
    return mainBranch;
  }

  @Override
  public InternalCDOBranch getBranch(int branchID)
  {
    checkActive();
    if (branchID == CDOBranch.MAIN_BRANCH_ID)
    {
      return mainBranch;
    }

    if (!repository.isSupportingBranches())
    {
      return null;
    }

    InternalCDOBranch branch;
    synchronized (branches)
    {
      branch = branches.get(branchID);
      if (branch == null)
      {
        branch = new CDOBranchImpl(this, branchID, null, null);
        putBranch(branch);
      }
    }

    return branch;
  }

  @Override
  public InternalCDOBranch getBranch(int id, String name, InternalCDOBranch baseBranch, long baseTimeStamp)
  {
    synchronized (branches)
    {
      InternalCDOBranch branch = branches.get(id);
      if (branch == null)
      {
        branch = new CDOBranchImpl(this, id, name, baseBranch.getPoint(baseTimeStamp));
        putBranch(branch);
      }
      else if (branch.isProxy())
      {
        branch.setBranchInfo(name, baseBranch, baseTimeStamp);
      }

      return branch;
    }
  }

  @Override
  public InternalCDOBranch getBranch(int id, BranchInfo branchInfo)
  {
    String name = branchInfo.getName();
    InternalCDOBranch baseBranch = getBranch(branchInfo.getBaseBranchID());
    long baseTimeStamp = branchInfo.getBaseTimeStamp();
    return getBranch(id, name, baseBranch, baseTimeStamp);
  }

  @Override
  public InternalCDOBranch getBranch(String path)
  {
    if (path.startsWith(CDOBranch.PATH_SEPARATOR))
    {
      path = path.substring(1);
    }

    int sep = path.indexOf(CDOBranch.PATH_SEPARATOR);
    if (sep == -1)
    {
      if (CDOBranch.MAIN_BRANCH_NAME.equals(path))
      {
        return mainBranch;
      }

      return null;
    }

    if (!repository.isSupportingBranches())
    {
      return null;
    }

    String name = path.substring(0, sep);
    if (CDOBranch.MAIN_BRANCH_NAME.equals(name))
    {
      String rest = path.substring(sep + 1);
      return mainBranch.getBranch(rest);
    }

    return null;
  }

  @Override
  public int getBranches(int startID, int endID, CDOBranchHandler handler)
  {
    checkActive();
    if (!repository.isSupportingBranches())
    {
      if (startID <= CDOBranch.MAIN_BRANCH_ID && CDOBranch.MAIN_BRANCH_ID <= endID)
      {
        handler.handleBranch(mainBranch);
        return 1;
      }

      return 0;
    }

    return branchLoader.loadBranches(startID, endID, handler);
  }

  @Override
  public LinkedHashSet<CDOBranch> getBranches(int rootID)
  {
    LinkedHashSet<CDOBranch> result = new LinkedHashSet<>();

    InternalCDOBranch branch = getBranch(rootID);
    CDOBranchUtil.forEachBranchInTree(branch, result::add);

    return result;
  }

  @Override
  public InternalCDOBranch createBranch(int branchID, String name, InternalCDOBranch baseBranch, long baseTimeStamp) throws CDODuplicateBranchException
  {
    checkActive();

    if (!repository.isSupportingBranches())
    {
      throw new IllegalStateException("Branching is not supported");
    }

    checkBranchName(name);

    Pair<Integer, Long> result;
    try
    {
      result = branchLoader.createBranch(branchID, new BranchInfo(name, baseBranch.getID(), baseTimeStamp));
    }
    catch (RemoteException ex)
    {
      throw ex.unwrap();
    }

    int actualBranchID = result.getElement1();
    long actualBaseTimeStamp = result.getElement2();

    CDOBranchPoint base = baseBranch.getPoint(actualBaseTimeStamp);
    InternalCDOBranch branch = createBranch(actualBranchID, name, base, baseTimeStamp);

    synchronized (branches)
    {
      putBranch(branch);
    }

    handleBranchChanged(branch, ChangeKind.CREATED);
    return branch;
  }

  protected InternalCDOBranch createBranch(int branchID, String name, CDOBranchPoint base, long originalBaseTimeStamp)
  {
    return new CDOBranchImpl(this, branchID, name, base);
  }

  @Override
  public CDOBranch[] deleteBranches(int id, OMMonitor monitor)
  {
    checkActive();

    if (!(branchLoader instanceof BranchLoader5))
    {
      throw new UnsupportedOperationException("Deleting branches is not supported by " + branchLoader);
    }

    CDOBranch[] deletedBranches = ((BranchLoader5)branchLoader).deleteBranches(id, monitor);

    synchronized (branches)
    {
      for (int i = deletedBranches.length - 1; i >= 0; --i)
      {
        InternalCDOBranch branch = (InternalCDOBranch)deletedBranches[i];
        branch.setDeleted();
      }
    }

    fireBranchDeletedEvents(deletedBranches);
    return deletedBranches;
  }

  @Override
  @Deprecated
  public void renameBranch(CDOBranch branch, String newName)
  {
    branch.setName(newName);
  }

  @Override
  public int getTagModCount()
  {
    return tagModCount;
  }

  @Override
  public void setTagModCount(int tagModCount)
  {
    synchronized (tags)
    {
      boolean initial = this.tagModCount == -1;
      this.tagModCount = tagModCount;

      if (initial)
      {
        executeTagChanges();
      }
    }
  }

  @Override
  public void handleTagChanged(int modCount, String oldName, String newName, CDOBranchPoint branchPoint)
  {
    synchronized (tags)
    {
      tagChangeQueue.add(new TagChange(modCount, oldName, newName, branchPoint));
      tagChangeQueue.sort(null);
      executeTagChanges();
    }
  }

  protected void executeTagChanges()
  {
    while (!tagChangeQueue.isEmpty() && tagModCount != -1)
    {
      TagChange tagChange = tagChangeQueue.get(0);
      int newModCount = tagModCount + 1;

      if (tagChange.getModCount() == newModCount)
      {
        tagModCount = newModCount;
        executeTagChange(tagChange.getOldName(), tagChange.getNewName(), tagChange.getBranchPoint());
        tagChangeQueue.remove(0);
      }
    }
  }

  protected void executeTagChange(String oldName, String newName, CDOBranchPoint branchPoint)
  {
    switch (InternalCDOBranchManager.getTagChangeKind(oldName, newName, branchPoint))
    {
    case CREATED:
      createTagInternal(newName, branchPoint);
      break;

    case RENAMED:
      renameTagInternal(oldName, newName);
      break;

    case MOVED:
      moveTagInternal(oldName, branchPoint);
      break;

    case DELETED:
      deleteTagInternal(oldName);
      break;
    }
  }

  @Override
  public CDOBranchPoint changeTagWithModCount(AtomicInteger modCount, String oldName, String newName, CDOBranchPoint branchPoint)
  {
    synchronized (tags)
    {
      if (modCount != null)
      {
        if (modCount.get() != tagModCount)
        {
          throw new ConcurrentModificationException();
        }
      }

      CDOBranchPoint result = null;

      switch (InternalCDOBranchManager.getTagChangeKind(oldName, newName, branchPoint))
      {
      case CREATED:
      {
        CDOBranchTagImpl tag = tags.get(newName);
        if (tag != null)
        {
          throw new CDOException("Tag name exists: " + newName);
        }

        boolean success = false;
        if (branchLoader instanceof BranchLoader4)
        {
          result = ((BranchLoader4)branchLoader).changeTag(modCount, null, newName, branchPoint);
          if (result != null)
          {
            branchPoint = result;
            result = null;
          }

          success = true;
        }

        if (success)
        {
          result = createTagInternal(newName, branchPoint);
        }

        break;
      }

      case RENAMED:
      {
        ((BranchLoader4)branchLoader).changeTag(modCount, oldName, newName, null);
        renameTagInternal(oldName, newName);
        break;
      }

      case MOVED:
      {
        result = ((BranchLoader4)branchLoader).changeTag(modCount, oldName, null, branchPoint);
        if (result != null)
        {
          branchPoint = result;
          result = null;
        }

        moveTagInternal(oldName, branchPoint);
        break;
      }

      case DELETED:
      {
        ((BranchLoader4)branchLoader).changeTag(modCount, oldName, null, null);
        deleteTagInternal(oldName);
        break;
      }
      }

      ++tagModCount;
      if (modCount != null)
      {
        modCount.set(tagModCount);
      }

      return result;
    }
  }

  @Override
  public CDOBranchTag createTag(String name, CDOBranchPoint branchPoint)
  {
    synchronized (tags)
    {
      AtomicInteger modCount = new AtomicInteger(tagModCount);
      return (CDOBranchTag)changeTagWithModCount(modCount, null, name, branchPoint);
    }
  }

  @Override
  public void renameTag(String oldName, String newName)
  {
    synchronized (tags)
    {
      AtomicInteger modCount = new AtomicInteger(tagModCount);
      changeTagWithModCount(modCount, oldName, newName, null);
    }
  }

  @Override
  public void moveTag(CDOBranchTag tag, CDOBranchPoint branchPoint)
  {
    synchronized (tags)
    {
      String name = tag.getName();

      AtomicInteger modCount = new AtomicInteger(tagModCount);
      changeTagWithModCount(modCount, name, null, branchPoint);
    }
  }

  @Override
  public void deleteTag(CDOBranchTag tag)
  {
    synchronized (tags)
    {
      String name = tag.getName();

      AtomicInteger modCount = new AtomicInteger(tagModCount);
      changeTagWithModCount(modCount, name, null, null);
    }
  }

  private CDOBranchTagImpl createTagInternal(String name, CDOBranchPoint branchPoint)
  {
    CDOBranchTagImpl tag = new CDOBranchTagImpl(name, branchPoint.getBranch(), branchPoint.getTimeStamp());
    tags.put(name, tag);

    CDOTagListImpl tagList = getTagListOrNull();
    if (tagList != null)
    {
      tagList.addTag(tag);
    }

    return tag;
  }

  private void renameTagInternal(String oldName, String newName)
  {
    CDOBranchTagImpl tag = tags.remove(oldName);
    tag.setNameInternal(newName);
    tags.put(newName, tag);

    CDOTagListImpl tagList = getTagListOrNull();
    if (tagList != null)
    {
      tagList.fireTagRenamedEvent(tag, oldName, newName);
    }

    tag.fireTagRenamedEvent(oldName, newName);
  }

  private void moveTagInternal(String name, CDOBranchPoint branchPoint)
  {
    CDOBranchTagImpl tag = tags.get(name);
    CDOBranchPoint oldBranchPoint = CDOBranchUtil.copyBranchPoint(tag);
    tag.moveInternal(branchPoint.getBranch(), branchPoint.getTimeStamp());

    CDOTagListImpl tagList = getTagListOrNull();
    if (tagList != null)
    {
      tagList.fireTagMovedEvent(tag, oldBranchPoint, branchPoint);
    }

    tag.fireTagMovedEvent(oldBranchPoint, branchPoint);
  }

  private void deleteTagInternal(String name)
  {
    CDOBranchTagImpl tag = tags.remove(name);
    tag.deleteInternal();

    CDOTagListImpl tagList = getTagListOrNull();
    if (tagList != null)
    {
      tagList.removeTag(tag);
    }

    tag.fireTagDeletedEvent();
  }

  @Override
  public CDOBranchTag getTag(String name)
  {
    synchronized (tags)
    {
      CDOBranchTagImpl tag = tags.get(name);
      if (tag == null && branchLoader instanceof BranchLoader4)
      {
        AtomicReference<BranchInfo> result = new AtomicReference<>();
        ((BranchLoader4)branchLoader).loadTags(name, branchInfo -> result.set(branchInfo));

        BranchInfo branchInfo = result.get();
        if (branchInfo != null)
        {
          int branchID = branchInfo.getBaseBranchID();
          long timeStamp = branchInfo.getBaseTimeStamp();

          InternalCDOBranch branch = getBranch(branchID);
          tag = new CDOBranchTagImpl(name, branch, timeStamp);
          tags.put(name, tag);
        }
      }

      return tag;
    }
  }

  @Override
  public CDOTagList getTagList()
  {
    synchronized (tags)
    {
      CDOTagListImpl tagList = getTagListOrNull();
      if (tagList != null)
      {
        return tagList;
      }

      if (branchLoader instanceof BranchLoader4)
      {
        CDOTagListImpl newTagList = new CDOTagListImpl();
        ((BranchLoader4)branchLoader).loadTags(null, branchInfo -> newTagList.addTag(branchInfo));
        tagListReference = createTagListReference(newTagList);

        newTagList.activate();
        return newTagList;
      }

      return null;
    }
  }

  private CDOTagListImpl getTagListOrNull()
  {
    if (tagListReference != null)
    {
      CDOTagListImpl tagList = tagListReference.get();
      if (tagList != null)
      {
        return tagList;
      }
    }

    return null;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("BranchManager[loader={0}]", branchLoader);
  }

  /**
   * {@link #branches} must be synchronized by caller!
   */
  private boolean putBranch(InternalCDOBranch branch)
  {
    int id = branch.getID();
    if (branches.containsKey(id))
    {
      return false;
    }

    branches.put(id, branch);
    return true;
  }

  protected Map<Integer, InternalCDOBranch> createBranchMap()
  {
    return new ReferenceValueMap.Soft<>();
  }

  protected Map<String, CDOBranchTagImpl> createTagMap()
  {
    return new ReferenceValueMap.Soft<>();
  }

  protected Reference<CDOTagListImpl> createTagListReference(CDOTagListImpl tagList)
  {
    return new SoftReference<>(tagList);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(repository, "repository"); //$NON-NLS-1$
    checkState(branchLoader, "branchLoader"); //$NON-NLS-1$
  }

  public static void checkBranchName(String name) throws IllegalArgumentException
  {
    if (StringUtil.isEmpty(name))
    {
      throw new IllegalArgumentException("Branch name is empty");
    }

    if (name.indexOf('/') != -1)
    {
      throw new IllegalArgumentException("Branch name contains '/'");
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class CDOTagListImpl extends Container<CDOBranchTag> implements CDOTagList
  {
    private final List<CDOBranchTag> list = new ArrayList<>();

    private CDOBranchTag[] array;

    public CDOTagListImpl()
    {
    }

    @Override
    public CDOBranchManager getBranchManager()
    {
      return CDOBranchManagerImpl.this;
    }

    @Override
    public CDOBranchTag[] getElements()
    {
      return getTags();
    }

    @Override
    public CDOBranchTag[] getTags()
    {
      synchronized (list)
      {
        if (array == null)
        {
          array = list.toArray(new CDOBranchTag[list.size()]);
          Arrays.sort(array);
        }

        return array;
      }
    }

    @Override
    public CDOBranchTag[] getTags(CDOBranch branch)
    {
      List<CDOBranchTag> result = new ArrayList<>();

      synchronized (list)
      {
        for (CDOBranchTag tag : list)
        {
          if (tag.getBranch() == branch)
          {
            result.add(tag);
          }
        }
      }

      return result.toArray(new CDOBranchTag[result.size()]);
    }

    public void branchesDeleted(CDOBranch[] deletedBranches)
    {
      IListener[] listeners = getListeners();
      ContainerEvent<CDOBranchTag> event = listeners.length == 0 ? null : new ContainerEvent<>(this);
      List<CDOBranchTagImpl> deletedTags = new ArrayList<>();

      synchronized (tags)
      {
        synchronized (list)
        {
          for (Iterator<CDOBranchTag> it = list.iterator(); it.hasNext();)
          {
            CDOBranchTagImpl tag = (CDOBranchTagImpl)it.next();

            for (CDOBranch deletedBranch : deletedBranches)
            {
              if (tag.getBranch() == deletedBranch)
              {
                it.remove();
                tags.remove(tag.getName());
                tag.deleteInternal();

                if (event != null)
                {
                  event.addDelta(tag, IContainerDelta.Kind.REMOVED);
                }

                if (tag.hasListeners())
                {
                  deletedTags.add(tag);
                }

                break;
              }
            }
          }
        }
      }

      if (event != null && !event.isEmpty())
      {
        fireEvent(event, listeners);
      }

      for (CDOBranchTagImpl deletedTag : deletedTags)
      {
        deletedTag.fireTagDeletedEvent();
      }
    }

    private void addTag(BranchInfo branchInfo)
    {
      String name = branchInfo.getName();
      CDOBranchTagImpl tag = tags.get(name);
      if (tag == null)
      {
        int branchID = branchInfo.getBaseBranchID();
        long timeStamp = branchInfo.getBaseTimeStamp();

        InternalCDOBranch branch = getBranch(branchID);
        tag = new CDOBranchTagImpl(name, branch, timeStamp);
        tags.put(name, tag);
      }

      addTag(tag);
    }

    private void addTag(CDOBranchTag tag)
    {
      synchronized (list)
      {
        list.add(tag);
        array = null;
      }

      if (isActive())
      {
        fireElementAddedEvent(tag);
      }
    }

    private void removeTag(CDOBranchTag tag)
    {
      synchronized (list)
      {
        if (list.remove(tag))
        {
          array = null;
        }
        else
        {
          tag = null;
        }
      }

      if (tag != null)
      {
        fireElementRemovedEvent(tag);
      }
    }

    private void fireTagRenamedEvent(CDOBranchTagImpl tag, String oldName, String newName)
    {
      fireEvent(new TagRenamedEventImpl(this, tag, oldName, newName));
    }

    private void fireTagMovedEvent(CDOBranchTagImpl tag, CDOBranchPoint oldBranchPoint, CDOBranchPoint newBranchPoint)
    {
      fireEvent(new TagMovedEventImpl(this, tag, oldBranchPoint, newBranchPoint));
    }
  }

  /**
   * @author Eike Stepper
   */
  private static abstract class TagListEventImpl extends Event implements TagListEvent, TagEvent
  {
    private static final long serialVersionUID = 1L;

    private final CDOBranchTagImpl tag;

    public TagListEventImpl(CDOTagListImpl tagList, CDOBranchTagImpl tag)
    {
      super(tagList);
      this.tag = tag;
    }

    @Override
    public CDOTagList getTagList()
    {
      return (CDOTagList)getSource();
    }

    @Override
    public CDOBranchTagImpl getTag()
    {
      return tag;
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "tag=" + tag;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class TagRenamedEventImpl extends TagListEventImpl implements TagRenamedEvent
  {
    private static final long serialVersionUID = 1L;

    private final String oldName;

    private final String newName;

    public TagRenamedEventImpl(CDOTagListImpl tagList, CDOBranchTagImpl tag, String oldName, String newName)
    {
      super(tagList, tag);
      this.oldName = oldName;
      this.newName = newName;
    }

    @Override
    public String getOldName()
    {
      return oldName;
    }

    @Override
    public String getNewName()
    {
      return newName;
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return super.formatAdditionalParameters() + ", oldName=" + oldName + ", newName=" + newName;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class TagMovedEventImpl extends TagListEventImpl implements TagMovedEvent
  {
    private static final long serialVersionUID = 1L;

    private final CDOBranchPoint oldBranchPoint;

    private final CDOBranchPoint newBranchPoint;

    public TagMovedEventImpl(CDOTagListImpl tagList, CDOBranchTagImpl tag, CDOBranchPoint oldBranchPoint, CDOBranchPoint newBranchPoint)
    {
      super(tagList, tag);
      this.oldBranchPoint = oldBranchPoint;
      this.newBranchPoint = newBranchPoint;
    }

    @Override
    public CDOBranchPoint getOldBranchPoint()
    {
      return oldBranchPoint;
    }

    @Override
    public CDOBranchPoint getNewBranchPoint()
    {
      return newBranchPoint;
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return super.formatAdditionalParameters() + ", oldBranchPoint=" + oldBranchPoint + ", newBranchPoint=" + newBranchPoint;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class TagChange implements Comparable<TagChange>
  {
    private final int modCount;

    private final String oldName;

    private final String newName;

    private final CDOBranchPoint branchPoint;

    public TagChange(int modCount, String oldName, String newName, CDOBranchPoint branchPoint)
    {
      this.modCount = modCount;
      this.oldName = oldName;
      this.newName = newName;
      this.branchPoint = branchPoint;
    }

    public int getModCount()
    {
      return modCount;
    }

    public String getOldName()
    {
      return oldName;
    }

    public String getNewName()
    {
      return newName;
    }

    public CDOBranchPoint getBranchPoint()
    {
      return branchPoint;
    }

    @Override
    public int compareTo(TagChange o)
    {
      return Integer.compare(modCount, o.modCount);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class BranchChangedEvent extends Event implements CDOBranchChangedEvent
  {
    private static final long serialVersionUID = 1L;

    private final CDOBranch branch;

    private final ChangeKind changeKind;

    private final int[] branchIDs;

    public BranchChangedEvent(CDOBranch branch, ChangeKind changeKind, int... branchIDs)
    {
      super(branch.getBranchManager());
      this.branch = branch;
      this.changeKind = changeKind;
      this.branchIDs = branchIDs;
    }

    @Override
    public CDOBranchManager getSource()
    {
      return (CDOBranchManager)super.getSource();
    }

    @Override
    public CDOBranch getBranch()
    {
      return branch;
    }

    @Override
    public int[] getBranchIDs()
    {
      return branchIDs;
    }

    @Override
    public ChangeKind getChangeKind()
    {
      return changeKind;
    }
  }

  /**
   * @author Eike Stepper
   */
  @Deprecated
  private static final class BranchCreatedEvent extends BranchChangedEvent implements org.eclipse.emf.cdo.common.branch.CDOBranchCreatedEvent
  {
    private static final long serialVersionUID = 1L;

    public BranchCreatedEvent(CDOBranch branch)
    {
      super(branch, ChangeKind.CREATED, branch.getID());
    }
  }
}
