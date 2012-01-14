/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.server.internal.objectivity.ObjectivityStoreAccessor;
import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.clustering.ObjyPlacementManager;
import org.eclipse.emf.cdo.server.internal.objectivity.mapper.ITypeMapper;
import org.eclipse.emf.cdo.server.internal.objectivity.mapper.ObjyMapper;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.OBJYCDOIDUtil;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.objy.as.app.Class_Object;
import com.objy.db.ObjyRuntimeException;
import com.objy.db.app.ooId;

import java.util.WeakHashMap;

public class ObjyObjectManager
{

  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ObjyObjectManager.class);

  // private static final ContextTracer TRACER_INFO = new ContextTracer(OM.INFO, ObjyObjectManager.class);

  // private Map<Long, ObjyObject> idToObjyObjectMap = new ReferenceValueMap.Weak<Long, ObjyObject>();
  private WeakHashMap<Long, ObjyObject> idToObjyObjectMap = new WeakHashMap<Long, ObjyObject>();

  private ObjyPlacementManager globalPlacementManager = null;

  public static int newObjCount = 0;

  public static int newInternalObjCount = 0;

  public static long getObjectTime = 0;

  public static long updateObjectTime = 0;

  public static long resourceCheckAndUpdateTime = 0;

  public ObjyObjectManager(ObjyPlacementManager placementManager)
  {
    globalPlacementManager = placementManager;
  }

  public int sizeOfObjectMap()
  {
    return idToObjyObjectMap.size();
  }

  /**
	 *
	 */
  public ObjyObject newObject(EClass eClass, ooId nearObject)
  {
    if (nearObject == null)
    {
      // TODO - we might need to use annotation for placement.
      nearObject = globalPlacementManager.getNearObject(null, null, eClass);
    }
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace("Create new object of type " + eClass.getName() + " near object "
          + (nearObject != null ? nearObject.getStoreString() : null));
    }

    Class_Object newClassObject = newClassObject(eClass, nearObject);
    ObjyObject objyObject = new ObjyObject(newClassObject);
    // idToObjyObjectMap.put(OBJYCDOIDUtil.getLong(objyObject.ooId()), objyObject);
    if (TRACER_DEBUG.isEnabled())
    {
      newObjCount++;
    }
    return objyObject;
  }

  /**
   * @param eClass
   * @return
   */
  // public Class_Object newClassObject(EClass eClass, ooId nearObject)
  // {
  // return newClassObject(eClass, nearObject/*, null*/);
  // }

  /**
   * Creates an Objy 'Class_Object' from an eClass. This will construct a shell Class_Object in the store using the
   * schema.
   */
  private Class_Object newClassObject(EClass eClass, ooId nearObject/* , InitializeValue init */)
  {

    // we don create classes on the fly...
    // TODO - can we pre-create using the model?
    ObjyClass objyClass = ObjySchema.getOrCreate(eClass);

    // System.out.println(">> Create new object of type " + eClass.getName() + " near object " + (nearObject != null ?
    // nearObject
    // .getStoreString() : null));

    Class_Object newClassObject = Class_Object.new_persistent_object(objyClass.getASClass(), nearObject, false);
    if (TRACER_DEBUG.isEnabled())
    {
      ObjyObjectManager.newInternalObjCount++;
    }

    // if (init != null)
    // {
    // init.init(newClassObject);
    // }

    // Initialize the object
    for (EStructuralFeature feature : eClass.getEAllStructuralFeatures())
    {
      if (!(feature instanceof EAttribute || feature instanceof EReference) || !EMFUtil.isPersistent(feature))
      {
        continue;
      }

      ITypeMapper mapper = ObjyMapper.INSTANCE.getTypeMapper(feature);
      if (mapper == null)
      {
        if (TRACER_DEBUG.isEnabled())
        {
          TRACER_DEBUG.trace("Can't find mapper for feature " + feature.getName());
        }
        continue;
      }

      // Class_Position attr = objyClass.resolve_position(feature.getName());

      mapper.initialize(newClassObject, feature);

      // TODO - verify the need for this (see ESessionImpl in the old code)
      // if (init != null)
      // {
      // init.init(feature);
      // }
    }
    return newClassObject;
  }

  /**
   * return an ObjyObject based on the id passed. usually this is used for existing object when trying to modify them.
   */
  public ObjyObject getObject(CDOID id)
  {
    if (id == null)
    {
      return null;
    }

    ooId oid = OBJYCDOIDUtil.getooId(id);
    return getObject(oid);
  }

  /**
   * return an ObjyObject based on the ooId passed.
   */
  public ObjyObject getObject(ooId oid)
  {
    ObjyObject objyObject = null;
    if (oid == null)
    {
      return objyObject;
    }

    // System.out.println("ObjyObjectManager.getObject_ooId("+oid.getStoreString()+")");
    objyObject = idToObjyObjectMap.get(OBJYCDOIDUtil.getLong(oid));
    if (objyObject == null)
    {
      try
      {
        objyObject = getObjectFromClassObject(Class_Object.class_object_from_oid(oid));
      }
      catch (ObjyRuntimeException ex)
      {
        ex.printStackTrace();
      }
    }

    return objyObject;
  }

  /***
   * The following are utility function that get the base attributes from the object.
   * 
   * @param objyObject
   * @return replaced by the ones in ObjyObject. public Object getEContainer(ObjyObject objyObject) { Class_Position
   *         position = objyObject.objyClass().resolve_position(ooBaseClass.ClassName_containerid); ooId oid = (ooId)
   *         SingleReferenceMapper.INSTANCE.getValue(objyObject, null, position, 0); return getObject(oid); } public
   *         Object getEResource(ObjyObject objyObject) { Class_Position position =
   *         objyObject.objyClass().resolve_position(ooBaseClass.ClassName_resourceid); ooId oid = (ooId)
   *         SingleReferenceMapper.INSTANCE.getValue(objyObject, null, position, 0); return getObject(oid); } public int
   *         getEContainingFeature(ObjyObject objyObject) { Class_Position position =
   *         objyObject.objyClass().resolve_position(ooBaseClass.ClassName_containerfeatureid); Integer value =
   *         (Integer)IntegerTypeMapper.INSTANCE.getValue(objyObject, null, position, 0); return value == null ? 0 :
   *         value; } public void setEContainer(ObjyObject objyObject, Object containerID) { //containerID =
   *         provider.convertToStore(ooObject, containerID); Class_Position position =
   *         objyObject.objyClass().resolve_position(ooBaseClass.ClassName_containerid);
   *         SingleReferenceMapper.INSTANCE.setValue(objyObject, null, position, 0, containerID); } public void
   *         setEResource(ObjyObject objyObject, Object resourceID) { //resourceID = provider.convertToStore(ooObject,
   *         resourceID); Class_Position position =
   *         objyObject.objyClass().resolve_position(ooBaseClass.ClassName_resourceid);
   *         SingleReferenceMapper.INSTANCE.setValue(objyObject, null, position, 0, resourceID); } public void
   *         setEContainingFeature(ObjyObject objyObject, int containerID) { Class_Position position =
   *         objyObject.objyClass().resolve_position(ooBaseClass.ClassName_containerfeatureid);
   *         IntegerTypeMapper.INSTANCE.setValue(objyObject, null, position, 0, containerID); }
   */

  /**
   * Construct an ObjyObject from an existing Class_Object and add it to the idToObjyObjectMap.
   * 
   * @param classObject
   * @return
   */
  private ObjyObject getObjectFromClassObject(Class_Object classObject)
  {
    ObjyObject objyObject = new ObjyObject(classObject);
    idToObjyObjectMap.put(OBJYCDOIDUtil.getLong(objyObject.ooId()), objyObject);
    return objyObject;
  }

  /***
   * TODO - Remove the eClass and just use the objyObject attributes to do the clean up.
   * 
   * @param objyObject
   */
  public void remove(ObjyObject objyObject)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace("ObjyObjectManager.delete(" + objyObject.ooId().getStoreString() + ")");
    }

    // //remove it from it's resource.
    // Object resource = objyObject.getEResource();
    // // locate the ObjyObject.
    // ObjyObject resourceObject = this.getObject((ooId)resource);

    synchronized (idToObjyObjectMap)
    {
      idToObjyObjectMap.remove(OBJYCDOIDUtil.getLong(objyObject.ooId()));
    }
  }

  // we could've used the CDO copy revision technique, but it will be expensive
  // to create the new copy over the Java/JNI boundaries, doing a low level
  // copy is faster.
  public ObjyObject copyRevision(ObjectivityStoreAccessor storeAccessor, ObjyObject objyObject)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace("ObjyObjectManager.copyRevision(" + objyObject.ooId().getStoreString() + ")");
    }
    EClass eClass = ObjySchema.getEClass(storeAccessor.getStore(), objyObject.objyClass());
    ObjyObject newObjyRevision = objyObject.copy(eClass, this);
    return newObjyRevision;
  }

  public ObjyPlacementManager getGlobalPlacementManager()
  {
    return globalPlacementManager;
  }
}
