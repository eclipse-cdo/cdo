/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/233273    
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.server.mem;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOModelConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IMEMStore;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.LongIDStore;
import org.eclipse.emf.cdo.spi.server.StoreAccessorPool;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Simon McDuff
 */
public class MEMStore extends LongIDStore implements IMEMStore
{
  public static final String TYPE = "mem"; //$NON-NLS-1$

  private long creationTime;

  private Map<CDOID, List<InternalCDORevision>> revisions = new HashMap<CDOID, List<InternalCDORevision>>();

  private int listLimit;

  private CDOID rootResourceID;

  /**
   * @param listLimit
   *          See {@link #setListLimit(int)}.
   * @since 2.0
   */
  public MEMStore(int listLimit)
  {
    super(TYPE, set(ChangeFormat.REVISION, ChangeFormat.DELTA), set(RevisionTemporality.NONE,
        RevisionTemporality.AUDITING), set(RevisionParallelism.NONE));
    setRevisionTemporality(RevisionTemporality.AUDITING);
    this.listLimit = listLimit;
  }

  public MEMStore()
  {
    this(UNLIMITED);
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

  public synchronized InternalCDORevision getRevision(CDOID id)
  {
    List<InternalCDORevision> list = revisions.get(id);
    if (list != null)
    {
      return list.get(list.size() - 1);
    }

    return null;
  }

  public synchronized InternalCDORevision getRevisionByVersion(CDOID id, int version)
  {
    if (getRepository().isSupportingAudits())
    {
      List<InternalCDORevision> list = revisions.get(id);
      if (list != null)
      {
        return getRevisionByVersion(list, version);
      }

      return null;
    }

    throw new UnsupportedOperationException();
  }

  /**
   * @since 2.0
   */
  public synchronized InternalCDORevision getRevisionByTime(CDOID id, long timeStamp)
  {
    if (getRepository().isSupportingAudits())
    {
      List<InternalCDORevision> list = revisions.get(id);
      if (list != null)
      {
        return getRevisionByTime(list, timeStamp);
      }

      return null;
    }

    throw new UnsupportedOperationException();
  }

  public synchronized void addRevision(InternalCDORevision revision)
  {
    CDOID id = revision.getID();
    int version = revision.getVersion();

    List<InternalCDORevision> list = revisions.get(id);
    if (list == null)
    {
      list = new ArrayList<InternalCDORevision>();
      revisions.put(id, list);
    }

    InternalCDORevision rev = getRevisionByVersion(list, version);
    if (rev != null)
    {
      throw new IllegalStateException("Concurrent modification of revision " + rev); //$NON-NLS-1$
    }

    rev = getRevisionByVersion(list, version - 1);
    if (rev != null)
    {
      rev.setRevised(revision.getCreated() - 1);
    }

    if (revision.isResource())
    {
      EStructuralFeature feature = revision.getEClass().getEStructuralFeature(
          CDOModelConstants.RESOURCE_NODE_NAME_ATTRIBUTE);
      CDOID revisionFolder = (CDOID)revision.data().getContainerID();
      String revisionName = (String)revision.data().get(feature, 0);

      IStoreAccessor accessor = StoreThreadLocal.getAccessor();
      CDOID resourceID = accessor.readResourceID(revisionFolder, revisionName, revision.getCreated());
      if (!CDOIDUtil.isNull(resourceID))
      {
        throw new IllegalStateException("Duplicate resource: " + revisionName + " (folderID=" + revisionFolder + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      }
    }

    list.add(revision);
    if (listLimit != UNLIMITED)
    {
      enforceListLimit(list);
    }
  }

  /**
   * @since 2.0
   */
  public synchronized boolean rollbackRevision(InternalCDORevision revision)
  {
    CDOID id = revision.getID();
    List<InternalCDORevision> list = revisions.get(id);
    if (list == null)
    {
      return false;
    }

    int version = revision.getVersion();
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
   * @since 2.0
   */
  public synchronized void removeID(CDOID id)
  {
    revisions.remove(id);
  }

  /**
   * @since 2.0
   */
  public synchronized void queryResources(IStoreAccessor.QueryResourcesContext context)
  {
    CDOID folderID = context.getFolderID();
    String name = context.getName();
    boolean exactMatch = context.exactMatch();
    for (List<InternalCDORevision> list : revisions.values())
    {
      if (!list.isEmpty())
      {
        InternalCDORevision revision = list.get(0);
        if (revision.isResourceNode())
        {
          revision = getRevisionByTime(list, context.getTimeStamp());
          if (revision != null)
          {
            CDOID revisionFolder = (CDOID)revision.data().getContainerID();
            if (CDOIDUtil.equals(revisionFolder, folderID))
            {
              EStructuralFeature feature = revision.getEClass().getEStructuralFeature(
                  CDOModelConstants.RESOURCE_NODE_NAME_ATTRIBUTE);
              String revisionName = (String)revision.data().get(feature, 0);
              boolean match = exactMatch || revisionName == null || name == null ? ObjectUtil
                  .equals(revisionName, name) : revisionName.startsWith(name);

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

  private InternalCDORevision getRevisionByTime(List<InternalCDORevision> list, long timeStamp)
  {
    for (InternalCDORevision revision : list)
    {
      if (timeStamp == CDORevision.UNSPECIFIED_DATE)
      {
        if (revision.isCurrent())
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

  private void enforceListLimit(List<InternalCDORevision> list)
  {
    while (list.size() > listLimit)
    {
      list.remove(0);
    }
  }

  public synchronized void handleRevisions(CDORevisionHandler handler)
  {
    for (List<InternalCDORevision> list : revisions.values())
    {
      for (InternalCDORevision revision : list)
      {
        if (!handleRevision(revision, handler))
        {
          return;
        }
      }
    }
  }

  private boolean handleRevision(InternalCDORevision revision, CDORevisionHandler handler)
  {
    return handler.handleRevision(revision);
  }

  public CDOID getRootResourceID()
  {
    if (rootResourceID != null)
    {
      return rootResourceID;
    }

    EClass eResourceEClass = EresourcePackage.eINSTANCE.getCDOResource();
    EAttribute nameAttr = EresourcePackage.eINSTANCE.getCDOResourceNode_Name();
    EReference folderRef = EresourcePackage.eINSTANCE.getCDOResourceNode_Folder();
    for (List<InternalCDORevision> list : revisions.values())
    {
      InternalCDORevision firstRev = list.get(0);
      if (firstRev.getEClass() == eResourceEClass)
      {
        CDOID folderID = (CDOID)firstRev.get(folderRef, 0);
        if (folderID.isNull())
        {
          String name = (String)firstRev.get(nameAttr, 0);
          if (name == null)
          {
            rootResourceID = firstRev.getID();
            return rootResourceID;
          }
        }
      }
    }

    throw new RuntimeException("Could not deduce rootResourceID");
  }
}
