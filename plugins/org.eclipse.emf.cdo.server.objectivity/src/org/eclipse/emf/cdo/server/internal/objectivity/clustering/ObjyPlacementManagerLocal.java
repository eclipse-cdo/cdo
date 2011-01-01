/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.clustering;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.server.internal.objectivity.ObjectivityStore;
import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyScope;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySession;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.OBJYCDOIDUtil;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.ObjyDb;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOIDLong;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;

import com.objy.db.app.ooId;

import java.util.HashMap;
import java.util.Map;

/***
 * This class will attempt to cluster objects with their container, or with a resource. It should be able to use the
 * global clustering which will use model elements to locate where an object will end up.
 * 
 * @author Ibrahim Sallam
 */
public class ObjyPlacementManagerLocal
{

  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ObjyPlacementManagerLocal.class);

  // private static final ContextTracer TRACER_ERROR = new ContextTracer(OM.ERROR, ObjyPlacementManagerLocal.class);

  // private static final ContextTracer TRACER_INFO = new ContextTracer(OM.INFO, ObjyPlacementManagerLocal.class);

  private String repositoryName = null;

  private ObjyPlacementManager globalPlacementManager = null;

  ObjySession objySession = null;

  InternalCommitContext commitContext = null;

  Map<CDOID, InternalCDORevision> newObjectsMap;

  Map<CDOID, CDOID> idMapper;

  public ObjyPlacementManagerLocal(ObjectivityStore objyStore, ObjySession objySession,
      InternalCommitContext commitContext)
  {
    repositoryName = objyStore.getRepository().getName();
    globalPlacementManager = objyStore.getGlobalPlacementManager();

    this.objySession = objySession;
    this.commitContext = commitContext;
    // first put them in a map for easy lookup and processing....
    newObjectsMap = new HashMap<CDOID, InternalCDORevision>();
    for (InternalCDORevision revision : commitContext.getNewObjects())
    {
      newObjectsMap.put(revision.getID(), revision);
    }

    idMapper = new HashMap<CDOID, CDOID>();
  }

  public void processRevision(InternalCDORevision revision)
  {
    // the revision could've been processed in case if it's a container
    // object and we reached it while processing another revision.
    if (isIdProcessed(revision.getID()))
    {
      return;
    }

    // create the object and add it to mapping, this will recursively call
    // other object creation as needed, based on the default clustering of
    // having each object is stored with its container.
    createObjectAndAddToMapping(revision);
  }

  private ObjyObject createObjectAndAddToMapping(InternalCDORevision revision)
  {
    ObjyObject objyObject = createObject(revision);

    CDOID newID = OBJYCDOIDUtil.getCDOID(objyObject.ooId());

    // nearObject = objyObject.ooId();
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace("Adding object to mapping from " + revision.getID() + " to " + newID);
    }
    commitContext.addIDMapping(revision.getID(), newID);
    // keep a track of this mapping.
    idMapper.put(revision.getID(), newID);

    return objyObject;
  }

  protected boolean isIdProcessed(CDOID id)
  {
    // if the ID in the idMapper, then we did process the revision alreay
    return idMapper.get(id) != null;
  }

  protected ObjyObject createObject(InternalCDORevision revision)
  {
    ooId nearObject = null;
    EClass eClass = revision.getEClass();
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace("Creating new object with " + revision + " " + eClass);
    }
    if (revision.isResourceNode())
    {
      // The resourcelist is in the ConfigDB, but each resource is in a resource
      // container in the repo database, except the first root (ID == resourceID).
      if (revision.getID() == revision.getResourceID()) // Check with Eike!
      {
        nearObject = objySession.getResourceList(repositoryName).ooId();
      }
      else
      {
        ObjyScope objyScope = new ObjyScope(repositoryName, ObjyDb.RESOURCELIST_CONT_NAME);
        nearObject = objyScope.getScopeContOid();
      }
    }
    else
    {
      nearObject = getNearObject(revision);
    }

    if (nearObject == null)
    {
      // we have to put it somewhere.
      // call the global placement manager.
      nearObject = globalPlacementManager.getNearObject(null, null, revision.getEClass());
    }

    ObjyObject objyObject = objySession.getObjectManager().newObject(eClass, nearObject);

    // // if it's a resource, collect it.
    // if (revision.isResourceNode())
    // {
    // // Add resource to the list
    // ObjyResourceList resourceList = objySession.getResourceList();
    //
    // // before we update the data into the object we need to check
    // // if it's a resource and we're trying to add a duplicate.
    // // TODO - do we need to check for Folder and resouce, or is the isResourceNode()
    // // check is enough?!!!
    // if (revision.isResourceFolder() || revision.isResource())
    // {
    // // this call will throw exception if we have a duplicate resource we trying to add.
    // resourceList.checkDuplicateResources(revision);
    // }
    // SmartLock.lock(objyObject);
    // resourceList.add(objyObject);
    // }

    return objyObject;
  }

  /***
   * This function might be called recursively throw the call to createAndAddToMapping() to create all the container
   * objects and or resources needed to cluster the rest of the new objects...
   */
  protected ooId getNearObject(InternalCDORevision revision)
  {
    ooId nearObject = null;
    // find the new object which is either a container or a resource.
    Object cdoId = revision.getContainerID();

    if (cdoId instanceof CDOID && (CDOID)cdoId != CDOID.NULL)
    {
      nearObject = getOidFromCDOID((CDOID)cdoId);
    }
    else
    {
      // use the resource...
      CDOID resourceId = revision.getResourceID();
      nearObject = getOidFromCDOID(resourceId);
    }
    return nearObject;
  }

  protected ooId getOidFromCDOID(CDOID cdoId)
  {
    ooId oid = null;

    // if (OBJYCDOIDUtil.isValidObjyId(cdoId))
    // oid = OBJYCDOIDUtil.getooId(cdoId);

    if (cdoId instanceof AbstractCDOIDLong)
    {
      oid = OBJYCDOIDUtil.getooId(cdoId);
    }
    else if (cdoId instanceof CDOIDTemp)
    {
      // see if we've seen it before
      CDOID nearId = idMapper.get(cdoId);
      if (nearId != null)
      {
        oid = OBJYCDOIDUtil.getooId(nearId);
      }
      else
      {
        // create that object since it wasn't created and mapped yet.
        InternalCDORevision containerRevision = newObjectsMap.get(cdoId);
        if (containerRevision != null)
        {
          oid = createObjectAndAddToMapping(containerRevision).ooId();
        }
      }
    }

    return oid;
  }

}
