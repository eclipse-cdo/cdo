/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Ibrahim Sallam - code refactoring for CDO 3.0
 */
package org.eclipse.emf.cdo.server.internal.objectivity;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.clustering.ObjyPlacementManager;
import org.eclipse.emf.cdo.server.internal.objectivity.clustering.ObjyPlacementManagerImpl;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyConnection;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySchema;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyScope;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySession;
import org.eclipse.emf.cdo.server.internal.objectivity.db.OoCommitInfoHandler;
import org.eclipse.emf.cdo.server.internal.objectivity.db.OoPropertyMapHandler;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyStoreInfo;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.OoResourceList;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.ObjyDb;
import org.eclipse.emf.cdo.server.objectivity.IObjectivityStore;
import org.eclipse.emf.cdo.server.objectivity.IObjectivityStoreConfig;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.LongIDStore;
import org.eclipse.emf.cdo.spi.server.Store;
import org.eclipse.emf.cdo.spi.server.StoreAccessorPool;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;

import com.objy.db.app.Connection;
import com.objy.db.app.ooId;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ObjectivityStore extends Store implements IObjectivityStore
{

  public static final String TYPE = "objectivity"; //$NON-NLS-1$

  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ObjectivityStore.class);

  protected ConcurrentLinkedQueue<ObjectivityStoreAccessor> writers = new ConcurrentLinkedQueue<ObjectivityStoreAccessor>();

  @ExcludeFromDump
  private transient StoreAccessorPool readerPool = new StoreAccessorPool(this, null);

  @ExcludeFromDump
  private transient StoreAccessorPool writerPool = new StoreAccessorPool(this, null);

  private ObjyConnection objyConnection = null;

  private IObjectivityStoreConfig storeConfig = null;

  private ObjyPlacementManager placementManager = null;

  private boolean firstTime = false;

  private int nActivate = 0;

  private Map<String, String> packageMapping = new HashMap<String, String>();

  private boolean requiredToSupportAudits;

  private boolean requiredToSupportBranches;

  private OoCommitInfoHandler ooCommitInfoHandler = null;

  private OoPropertyMapHandler ooPropertyMapHandler = null;

  private boolean storeInitialized = false;

  private long creationTime = CDORevision.UNSPECIFIED_DATE;

  // private boolean resetData = false;

  public ObjectivityStore(IObjectivityStoreConfig config)
  {
    // super(TYPE, set(ChangeFormat.REVISION, ChangeFormat.DELTA), set(
    // RevisionTemporality.NONE, RevisionTemporality.AUDITING),
    // set(RevisionParallelism.NONE));
    // setRevisionTemporality(RevisionTemporality.AUDITING);
    super(TYPE, LongIDStore.OBJECT_ID_TYPES, set(ChangeFormat.REVISION, ChangeFormat.DELTA), set(
        RevisionTemporality.NONE, RevisionTemporality.AUDITING), set(RevisionParallelism.NONE,
        RevisionParallelism.BRANCHING));
    storeConfig = config;
  }

  private void initStore()
  {
    // the caller already used the StoreConfig to open the connection
    // to the FD so, get the current here.
    objyConnection = ObjyConnection.INSTANCE;
    placementManager = new ObjyPlacementManagerImpl();

    // -----------------------------------------------------------------------
    // Initialize schema as needed, and also any other config information

    // connection to the FD.
    ObjyConnection.INSTANCE.connect(this, storeConfig.getFdName());
    Connection.current().setUserClassLoader(this.getClass().getClassLoader());

    ObjyConnection.INSTANCE.registerClass("org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyStoreInfo"); //$NON-NLS-1$
    ObjyConnection.INSTANCE.registerClass("org.eclipse.emf.cdo.server.internal.objectivity.schema.OoPackageInfo"); //$NON-NLS-1$
    ObjyConnection.INSTANCE.registerClass("org.eclipse.emf.cdo.server.internal.objectivity.schema.OoPackageUnit"); //$NON-NLS-1$
    ObjyConnection.INSTANCE.registerClass("org.eclipse.emf.cdo.server.internal.objectivity.schema.ASPackage"); //$NON-NLS-1$
    ObjyConnection.INSTANCE.registerClass("org.eclipse.emf.cdo.server.internal.objectivity.schema.OoCommitInfo"); //$NON-NLS-1$
    ObjyConnection.INSTANCE.registerClass("org.eclipse.emf.cdo.server.internal.objectivity.schema.OoProperty"); //$NON-NLS-1$

    ObjySession objySession = objyConnection.getWriteSessionFromPool("Main"); //$NON-NLS-1$
    objySession.setRecoveryAutomatic(true);
    objySession.begin();

    ObjySchema.createBaseSchema();

    try
    {
      ooId commitInfoListId = null;
      ooId propertyMapId = null;

      // check if we initialized the store for the first time.
      {
        ObjyScope objyScope = new ObjyScope(ObjyDb.CONFIGDB_NAME, ObjyDb.DEFAULT_CONT_NAME);
        ObjyStoreInfo objyStoreInfo = null;
        try
        {
          objyStoreInfo = (ObjyStoreInfo)objyScope.lookupObjectOid(ObjyDb.OBJYSTOREINFO_NAME);
        }
        catch (Exception ex)
        {
          // create the ObjyStoreInfo.
          objyStoreInfo = new ObjyStoreInfo(System.currentTimeMillis(), "...");
          objyScope.getContainerObj().cluster(objyStoreInfo);
          // flag as first time.
          firstTime = true;
        }
        creationTime = objyStoreInfo.getCreationTime();
      }

      // This is used for the package storage...
      ObjyScope objyScope = new ObjyScope(ObjyDb.CONFIGDB_NAME, ObjyDb.PACKAGESTORE_CONT_NAME);
      ObjyObject resourceList = getOrCreateResourceList();

      // get OIDs for the CommitInfoTree and the PropertyMap.
      if (commitInfoListId == null)
      {
        // commit info is per repository.
        commitInfoListId = ObjyDb.getOrCreateCommitInfoList(getRepository().getName());
      }
      if (propertyMapId == null)
      {
        propertyMapId = ObjyDb.getOrCreatePropertyMap();
      }

      ooCommitInfoHandler = new OoCommitInfoHandler(commitInfoListId);
      ooPropertyMapHandler = new OoPropertyMapHandler(propertyMapId);

      objySession.commit();

      storeInitialized = true;
    }
    catch (RuntimeException ex)
    {
      ex.printStackTrace();
      objySession.abort();
    }
    finally
    {
      objyConnection.returnSessionToPool(objySession);
    }
  }

  @Override
  protected IStoreAccessor createReader(ISession session)
  {
    return new ObjectivityStoreAccessor(this, session);
  }

  @Override
  protected IStoreAccessor createWriter(ITransaction transaction)
  {
    return new ObjectivityStoreAccessor(this, transaction);
  }

  @Override
  protected StoreAccessorPool getReaderPool(ISession session, boolean forReleasing)
  {
    return readerPool;
  }

  @Override
  protected StoreAccessorPool getWriterPool(IView view, boolean forReleasing)
  {
    return writerPool;
  }

  public long getCreationTime()
  {
    return creationTime;
  }

  public boolean isFirstTime()
  {
    return firstTime;
  }

  public ObjyConnection getConnection()
  {
    return objyConnection;

  }

  public boolean isRequiredToSupportAudits()
  {
    return requiredToSupportAudits;
  }

  public boolean isRequiredToSupportBranches()
  {
    return requiredToSupportBranches;
  }

  @Override
  protected void doBeforeActivate()
  {
    requiredToSupportAudits = getRepository().isSupportingAudits();
    requiredToSupportBranches = getRepository().isSupportingBranches();
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    // lazy initialization of the store.
    if (!storeInitialized)
    {
      initStore();
    }

    nActivate++;

    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace("doActivate - count: " + nActivate);
    }

    ObjySession objySession = objyConnection.getWriteSessionFromPool("Main");
    objySession.setRecoveryAutomatic(true);
    objySession.begin();

    try
    {
      if (!com.objy.db.app.Session.getCurrent().getFD().hasDB(getRepository().getName()))
      {
        // Create the repo DB.
        ObjyScope objyScope = new ObjyScope(getRepository().getName(), ObjyDb.DEFAULT_CONT_NAME);
        // ...do other initialisation of the repository here.
        // Note that in the current implementation we don't delete DBs by default, only delete
        // the containers (see ObjectivityStoreConfig.resetFD()) so any initialization done here
        // might not be repeated.
      }

      objySession.commit();
    }
    catch (RuntimeException ex)
    {
      objySession.abort();
    }
    finally
    {
      objyConnection.returnSessionToPool(objySession);
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    try
    {
      ObjySchema.resetCache();
      System.out.println(" -------- doDeactivate() ObjectivityStore ----------");
    }
    finally
    {
      ObjyConnection.INSTANCE.disconnect();
    }

    // readerPool.dispose();
    // writerPool.dispose();
    super.doDeactivate();

  }

  private ObjyObject createResourceList(ObjyScope objyScope)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.format("createResourceList()"); //$NON-NLS-1$
    }
    // TODO - this need refactoring...
    ObjyObject resourceList = OoResourceList.create(objyScope.getScopeContOid());
    objyScope.nameObj(ObjyDb.RESOURCELIST_NAME, resourceList);
    return resourceList;
  }

  /***
   * This function will return the resourceList after creation.
   */
  public ObjyObject getOrCreateResourceList()
  {
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.format("getOrCreateResourceList()"); //$NON-NLS-1$
    }
    ObjyScope objyScope = new ObjyScope(ObjyDb.CONFIGDB_NAME, ObjyDb.RESOURCELIST_CONT_NAME);
    ObjyObject objyObject = null;
    try
    {
      objyObject = objyScope.lookupObject(ObjyDb.RESOURCELIST_NAME);
    }
    catch (Exception ex)
    {
      // we need to create the resource.
      objyObject = createResourceList(objyScope);
    }

    return objyObject;
  }

  public ObjyPlacementManager getPlacementManager()
  {
    return placementManager;
  }

  public void addPackageMapping(String name1, String name2)
  {
    if (packageMapping.get(name1) == null)
    {
      packageMapping.put(name1, name2);
    }
  }

  public String getPackageMapping(String key)
  {
    return packageMapping.get(key);
  }

  public InternalCDORevision createRevision(ObjyObject objyObject, CDOID id)
  {
    EClass eClass = ObjySchema.getEClass(this, objyObject.objyClass());

    if (eClass == null)
    {
      System.out.println("OBJY: Can't find eClass for id:" + id);
      return null;
    }

    return super.createRevision(eClass, id);
  }

  public Map<String, String> getPropertyValues(Set<String> names)
  {
    ObjySession objySession = objyConnection.getReadSessionFromPool("Main");
    objySession.begin();
    Map<String, String> properties = ooPropertyMapHandler.getPropertyValues(names);
    objySession.commit();
    return properties;
  }

  public void setPropertyValues(Map<String, String> properties)
  {
    ObjySession objySession = objyConnection.getWriteSessionFromPool("Main");
    objySession.begin();
    ooPropertyMapHandler.setPropertyValues(properties);
    objySession.commit();
  }

  public void removePropertyValues(Set<String> names)
  {
    ObjySession objySession = objyConnection.getWriteSessionFromPool("Main");
    objySession.begin();
    ooPropertyMapHandler.removePropertyValues(names);
    objySession.commit();
  }

  public OoCommitInfoHandler getCommitInfoHandle()
  {
    return ooCommitInfoHandler;
  }

}
