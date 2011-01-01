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
package org.eclipse.emf.cdo.server.internal.objectivity.db;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.internal.objectivity.ObjectivityStoreAccessor;
import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.mapper.IManyTypeMapper;
import org.eclipse.emf.cdo.server.internal.objectivity.mapper.ISingleTypeMapper;
import org.eclipse.emf.cdo.server.internal.objectivity.mapper.ITypeMapper;
import org.eclipse.emf.cdo.server.internal.objectivity.mapper.ObjyMapper;
import org.eclipse.emf.cdo.server.internal.objectivity.mapper.SingleReferenceMapper;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyBase;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyFeatureMapEntry;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyProxy;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.OBJYCDOIDUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;

import com.objy.as.app.Class_Object;
import com.objy.as.app.Class_Position;
import com.objy.as.app.Numeric_Value;
import com.objy.as.app.Relationship_Object;
import com.objy.as.app.String_Value;
import com.objy.as.app.VArray_Object;
import com.objy.db.app.Session;
import com.objy.db.app.ooId;
import com.objy.db.app.ooObj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ObjyObject
{

  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ObjyObject.class);

  protected Class_Object classObject;

  protected ObjyClass objyClass;

  protected Class_Object baseClassObject = null;

  protected Relationship_Object baseRel = null;

  protected Relationship_Object revisionsRel = null;

  protected Relationship_Object lastRevisionRel = null;

  protected ooId objectId;

  // protected boolean isRoot = false;

  // good for fast access.
  private Map<Class_Position, Object> featureMap = new HashMap<Class_Position, Object>();

  public ObjyObject(Class_Object classObject)
  {
    this.classObject = classObject;
    objyClass = ObjySchema.getObjyClass(classObject.type_of().name());

    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.format("...classObject type: {0} - oid: {1}", classObject.type_of().name(), classObject.objectID()
          .getStoreString());
    }
    baseRel = classObject.get_relationship(objyClass.resolve_position(ObjyBase.ATT_BASE));
    if (!baseRel.exists())
    {
      // we are the base...
      revisionsRel = classObject.get_relationship(objyClass.resolve_position(ObjyBase.ATT_REVISIONS));
      lastRevisionRel = classObject.get_relationship(objyClass.resolve_position(ObjyBase.ATT_LAST_REVISION));
    }
    else
    {
      baseClassObject = baseRel.get_class_obj();
      // TODO - we might want to delay getting the list of versions unless we need them.
      // revisionsRel = baseClassObject.get_relationship(objyClass.resolve_position(ObjyBase.ATT_REVISIONS));
      // lastRevisionRel = baseClassObject.get_relationship(objyClass.resolve_position(ObjyBase.ATT_LAST_REVISION));
    }
    setObjectId(classObject.objectID());
  }

  public ObjyClass objyClass()
  {
    return objyClass;
  }

  public Class_Object ooClassObject()
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }
    return classObject;
  }

  public void setObjectId(ooId objectId)
  {
    this.objectId = objectId;
  }

  /**
		 *
		 */
  public ooId ooId()
  {
    return objectId;
  }

  public void setEContainer(Object containerID)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }

    Class_Position position = objyClass.resolve_position(ObjyBase.ATT_CONTAINERID);
    SingleReferenceMapper.INSTANCE.setValue(this, position, containerID);
  }

  public Object getEContainer()
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }

    Class_Position position = objyClass.resolve_position(ObjyBase.ATT_CONTAINERID);
    Object value = SingleReferenceMapper.INSTANCE.getValue(this, position);

    return value;
  }

  public ooId getEContainerAsOid()
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }

    Class_Position position = objyClass.resolve_position(ObjyBase.ATT_CONTAINERID);
    ooId childOid = get_ooId(position);
    return childOid;
  }

  public void setEResource(Object resourceID)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }

    Class_Position position = objyClass.resolve_position(ObjyBase.ATT_RESOURCEID);
    SingleReferenceMapper.INSTANCE.setValue(this, position, resourceID);

  }

  public Object getEResource()
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }

    Class_Position position = objyClass.resolve_position(ObjyBase.ATT_RESOURCEID);
    Object value = SingleReferenceMapper.INSTANCE.getValue(this, position);

    return value;
  }

  public ooId getEResourceAsOid()
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }
    Class_Position position = objyClass.resolve_position(ObjyBase.ATT_RESOURCEID);
    ooId childOid = get_ooId(position);
    return childOid;
  }

  public void setEContainingFeature(int contFeature)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }
    Class_Position position = objyClass.resolve_position(ObjyBase.ATT_CONTAINER_FEATUERID);
    set_numeric(position, new Numeric_Value(contFeature));
  }

  public int getEContainingFeature()
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }
    Class_Position position = objyClass.resolve_position(ObjyBase.ATT_CONTAINER_FEATUERID);
    return get_numeric(position).intValue();
  }

  /**
   * This is used to cache the composite features, (manyAttributes, manyReference, and featureMap. TBD - verify the need
   * of this.
   */
  public Object getFeatureList(Class_Position position)
  {
    return featureMap.get(position);
  }

  public void setFeatureList(Class_Position position, Object object)
  {
    featureMap.put(position, object);
  }

  public int getVersion()
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }
    int version = classObject.get_numeric(objyClass.resolve_position(ObjyBase.ATT_VERSION)).intValue();
    return version;
  }

  public void setVersion(int version)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }
    classObject.set_numeric(objyClass.resolve_position(ObjyBase.ATT_VERSION), new Numeric_Value(version));
    getVersion(); // TBD, verify the need for this call!!!!
  }

  public long getCreationTime()
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }
    long creationTime = classObject.get_numeric(objyClass.resolve_position(ObjyBase.ATT_CREATION_TIME)).longValue();
    return creationTime;
  }

  public void setCreationTime(long creationTime)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }
    classObject.set_numeric(objyClass.resolve_position(ObjyBase.ATT_CREATION_TIME), new Numeric_Value(creationTime));
  }

  public long getRevisedTime()
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }
    long revisedTime = classObject.get_numeric(objyClass.resolve_position(ObjyBase.ATT_REVISED_TIME)).longValue();
    return revisedTime;
  }

  public void setRevisedTime(long revisedTime)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }
    classObject.set_numeric(objyClass.resolve_position(ObjyBase.ATT_REVISED_TIME), new Numeric_Value(revisedTime));
  }

  public void setBranchId(int branchId)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }
    classObject.set_numeric(objyClass.resolve_position(ObjyBase.ATT_BRANCHID), new Numeric_Value(branchId));
  }

  public long getBranchId()
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }
    int branchId = classObject.get_numeric(objyClass.resolve_position(ObjyBase.ATT_BRANCHID)).intValue();
    return branchId;
  }

  public ObjyObject copy(EClass eClass)
  {
    ObjyObject newObjyObject = null;
    ooObj obj = ooObj.create_ooObj(objectId);
    ooObj newObj = (ooObj)obj.copy(obj); // Objy internal copy.
    // Dependent structures, for example array of refs are not copies, so we
    // have to iterate and copy (deep copy).
    newObjyObject = new ObjyObject(Class_Object.class_object_from_oid(newObj.getOid()));

    try
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("=> ObjyObject.copy() - oid:" + ooId().getStoreString() + " version:" + getVersion());
      }
      for (EStructuralFeature feature : eClass.getEAllStructuralFeatures())
      {
        if (!(feature instanceof EAttribute || feature instanceof EReference) || !EMFUtil.isPersistent(feature))
        {
          continue;
        }

        if (feature.isMany())
        {
          // copy this feature to the new object.
        }
      }
    }
    catch (com.objy.as.asException ex)
    {
      ex.printStackTrace();
    }

    return newObjyObject;
  }

  /**
   * Use the revision info to update the object in the store.
   * 
   * @param storeAccessor
   * @param revision
   */
  public void update(ObjectivityStoreAccessor storeAccessor, InternalCDORevision revision)
  {
    try
    {

      if (TRACER_DEBUG.isEnabled())
      {
        checkSession();
        TRACER_DEBUG.trace("=> ObjyObject.update() - oid:" + ooId().getStoreString() + " - version:"
            + revision.getVersion());
      }

      // this is done in the updateDate()
      // setEContainer(revision.getContainerID());
      // setEResource(revision.getResourceID());
      // setEContainingFeature(revision.getContainingFeatureID());

      updateData(storeAccessor, revision);
    }
    catch (com.objy.as.asException ex)
    {
      ex.printStackTrace();
    }
  }

  /**
   * Use the revision info to update the object in the store.
   * 
   * @param storeAccessor
   * @param revision
   */
  private void updateData(ObjectivityStoreAccessor storeAccessor, InternalCDORevision revision)
  {
    EClass eClass = revision.getEClass();

    try
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("=> ObjyObject.updateData() - oid:" + ooId().getStoreString() + //$NON-NLS-1$
            " - version:" + revision.getVersion()); //$NON-NLS-1$
      }

      setVersion(revision.getVersion());

      setEContainer(revision.getContainerID());
      setEResource(revision.getResourceID());
      setEContainingFeature(revision.getContainingFeatureID());
      setCreationTime(revision.getTimeStamp());
      setRevisedTime(revision.getRevised());
      setBranchId(revision.getBranch().getID());

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
            TRACER_DEBUG.trace("In " + ooId().getStoreString() + " - Can't find mapper for feature "
                + feature.getName());
          }
          continue;
        }
        // -----------------------------------------------
        // TODO - this code definitely need refactoring!!!
        // -----------------------------------------------

        // Class_Position attr = objyClass.resolve_position(feature.getName());
        if (feature.isMany())
        {
          // Object newValue = revision.getValue(feature);
          // --- TEMP solution to fix one of the tests...
          CDOList list = revision.getList(feature);
          Object[] values = new Object[list.size()];
          // we need to pass a list of ooId objects.
          // TODO - This need some work!!!!
          for (int i = 0; i < values.length; i++)
          {
            // TODO - this code need refactoring...
            Object value = list.get(i);
            if (value instanceof CDOIDExternal)
            {
              // System.out.println("value is a proxy object - it should be handled by the mapper.");
              // create an ObjyProxy object to hold the the value.
              ObjyProxy proxyObject = ObjyProxy.createObject(ooId());
              proxyObject.setUri(((CDOIDExternal)value).getURI());
              values[i] = proxyObject.ooId();

            }
            else if (value instanceof CDOID)
            {
              values[i] = OBJYCDOIDUtil.getooId((CDOID)list.get(i));
            }
            else if (value instanceof FeatureMap.Entry)
            {
              FeatureMap.Entry entry = (FeatureMap.Entry)value;
              EStructuralFeature entryFeature = entry.getEStructuralFeature();
              Object entryValue = entry.getValue();

              ooId oid = null;
              if (entryValue instanceof CDOIDExternal)
              {
                // System.out.println("value is a proxy object - it should be handled by the mapper.");
                // create an ObjyProxy object to hold the the value.
                ObjyProxy proxyObject = ObjyProxy.createObject(ooId());
                proxyObject.setUri(((CDOIDExternal)entryValue).getURI());
                oid = proxyObject.ooId();
              }
              else if (entryValue instanceof CDOID)
              {
                oid = OBJYCDOIDUtil.getooId((CDOID)entryValue);
              }
              else
              {
                if (TRACER_DEBUG.isEnabled())
                {
                  TRACER_DEBUG.trace("OBJY: don't know what kind of entryValue is this!!! - " + entryValue);
                }
              }

              // FeatureMapEntry is a presistent class.
              ObjyFeatureMapEntry featureMapEntry = new ObjyFeatureMapEntry(entryFeature.getName(), oid, objectId);

              // this.cluster(featureMapEntry);
              values[i] = featureMapEntry;
            }
            else
            {
              // different feature then.
              // System.out.println("-->> Hmmm feature (" + i + ") -> feature:" + feature.getName() + " - value:" +
              // value);
              values[i] = value;
            }
          }

          ((IManyTypeMapper)mapper).setAll(this, feature, 0, values);
          // ((IManyTypeMapper)mapper).addAll(this, feature, values);
        }
        else
        {
          Object newValue = revision.get(feature, feature.getFeatureID());
          // if (newValue instanceof CDOIDExternal)
          // {
          // System.out.println("value is a proxy object");
          // }
          ((ISingleTypeMapper)mapper).setValue(this, feature, newValue);
        }
      }
    }
    catch (com.objy.as.asException ex)
    {
      ex.printStackTrace();
    }
  }

  public ObjyObject getLastRevision()
  {
    if (!lastRevisionRel.exists())
    {
      return this;
    }

    Class_Object lastRevision = lastRevisionRel.get_class_obj();
    return new ObjyObject(lastRevision);
  }

  public ObjyObject getRevisionByVersion(int version)
  {
    ObjyObject objyRevision = null;
    if (Math.abs(getVersion()) == version)
    {
      // there is a first time for everything...
      return this;
    }
    Session.getCurrent().setReturn_Class_Object(true);
    // int numRevisions = (int) revisions.size();

    @SuppressWarnings("unchecked")
    Iterator<Class_Object> itr = revisionsRel.get_iterator();
    while (itr.hasNext())
    {
      objyRevision = new ObjyObject(itr.next());
      if (Math.abs(objyRevision.getVersion()) == version)
      {
        return objyRevision;
      }
    }

    return null;
  }

  public void addToRevisions(ObjyObject objyRevision)
  {
    revisionsRel.add(objyRevision.objectId);
    // set it as last rev.
    lastRevisionRel.clear(); // Ouch!! performance issue...
    lastRevisionRel.form(objyRevision.objectId);
  }

  // /**
  // * Wrapper around ObjyObject to allow clustering of other objects near this one.
  // */
  // private void cluster(ooObj otherObj)
  // {
  // try
  // {
  // ooObj thisObj = ooObj.create_ooObj(objectId);
  // thisObj.cluster(otherObj);
  // }
  // catch (ObjyRuntimeException ex)
  // {
  // ex.printStackTrace();
  // }
  // }

  /**
   * Fetch data from the store and return a revision.
   */
  public boolean fetch(ObjectivityStoreAccessor storeAccessor, InternalCDORevision revision, int listChunk)
  {
    boolean bRet = true;
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }
    EClass eClass = revision.getEClass();

    try
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("=> ObjyObject.fetch() - oid:" + ooId().getStoreString() + " version:" + getVersion());
      }
      // Put the version of the objects;
      revision.setVersion(getVersion());
      revision.setContainerID(getEContainer());
      revision.setResourceID((CDOID)getEResource());
      revision.setContainingFeatureID(getEContainingFeature());
      long creationTime = getCreationTime();
      long revisedTime = getRevisedTime();

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("... ObjyObject.creationTime: " + creationTime);
        TRACER_DEBUG.trace("... ObjyObject.revisedTime : " + revisedTime);
      }

      revision.setRevised(revisedTime);

      for (EStructuralFeature feature : eClass.getEAllStructuralFeatures())
      {
        if (!(feature instanceof EAttribute || feature instanceof EReference) || !EMFUtil.isPersistent(feature))
        {
          continue;
        }

        if (feature.isMany())
        {
          int featureSize = size(feature);
          int chunkSize = featureSize;
          if (listChunk != CDORevision.UNCHUNKED)
          {
            chunkSize = Math.min(chunkSize, listChunk);
          }

          Object[] objects = getAll(feature, 0, chunkSize);
          // if (size > 0)
          {
            // TODO - we could use getList() then fill the array with values, we
            // also
            // need to consider the chunking...
            // InternalCDOList cdoList =
            // (InternalCDOList)CDOListFactory.DEFAULT.createList(objects.length,
            // objects.length, 0);
            // TODO - use the following line instead of creating the cdoList
            // above.
            MoveableList<Object> list = revision.getList(feature);

            // size = Math.min(size, 0);
            for (int i = 0; i < chunkSize; i++)
            {
              if (objects[i] instanceof ooId)
              {
                // TODO - this code need refactoring....

                Class_Object refClassObject = Class_Object.class_object_from_oid((ooId)objects[i]);

                if (refClassObject.type_of().name().equals(ObjyProxy.className))
                {
                  // System.out.println("OBJY: Got proxy: " + refClassObject.objectID().getStoreString());
                  ObjyProxy proxyObject = new ObjyProxy(refClassObject);
                  // cdoList.set(i,
                  // OBJYCDOIDUtil.createCDIDExternal(proxyObject));
                  list.add(OBJYCDOIDUtil.createCDIDExternal(proxyObject));
                }
                else
                {
                  CDOID childID = OBJYCDOIDUtil.getCDOID((ooId)objects[i]);
                  // cdoList.set(i, childID);
                  list.add(childID);
                }
                refClassObject = null;
              }
              else if (objects[i] instanceof ObjyFeatureMapEntry)
              {
                ObjyFeatureMapEntry mapEntry = (ObjyFeatureMapEntry)objects[i];
                ooId oid = mapEntry.getObject();
                CDOID cdoId = null;
                Class_Object refClassObject = Class_Object.class_object_from_oid(oid);
                if (refClassObject.type_of().name().equals(ObjyProxy.className))
                {
                  // System.out.println("OBJY: Got proxy: " + refClassObject.objectID().getStoreString());
                  ObjyProxy proxyObject = new ObjyProxy(refClassObject);
                  cdoId = OBJYCDOIDUtil.createCDIDExternal(proxyObject);
                }
                else
                {
                  cdoId = OBJYCDOIDUtil.getCDOID((ooId)objects[i]);
                }

                throw new UnsupportedOperationException("FIXME");

                // FIXME Uncomment the following 3 lines:
                // FeatureMap.Entry entry = CDORevisionUtil.createFeatureMapEntry(entryFeature, cdoId);
                // list.add(entry);
                // refClassObject = null;
              }
              else
              {
                // different feature then.
                // System.out.println("-->> Hmmm fetch() feature (" + i + ") -> feature:" + feature.getName()
                // + " - value:" + objects[i]);
                // cdoList.set(i, objects[i]);
                list.add(objects[i]);
              }
            }
            // fill the rest if needed.
            if (featureSize - chunkSize > 0)
            {
              for (int i = 0; i < featureSize - chunkSize; i++)
              {
                list.add(InternalCDOList.UNINITIALIZED);
              }
            }
          }
        }
        else
        {
          Object object = get(feature, 0);

          /**
           * TODO - verify if this is needed for 2.x if (cdoFeature.getType() == CDOType.CUSTOM) { object =
           * EcoreUtil.convertToString((EDataType)eFeature.getEType(), object); }
           */
          revision.set(feature, 0, object);
        }
      }
    }
    catch (com.objy.as.asException ex)
    {
      ex.printStackTrace();
    }

    return bRet;
  }

  /**
   * Fetch data for a specific feature from the store, and return a list of objects. Used by
   * ObjectivityStoreChunkAccessor
   */
  public Object[] fetch(ObjectivityStoreAccessor storeAccessor, EStructuralFeature feature, int startIndex,
      int chunkSize)
  {
    List<Object> results = new ArrayList<Object>();

    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }

    try
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("=> ObjyObject.fetch() - feature:" + feature.getName() + "from Object: "
            + ooId().getStoreString() + " version:" + getVersion());
      }
      int featureSize = size(feature);
      chunkSize = Math.min(featureSize - startIndex, chunkSize);

      Object[] objects = getAll(feature, startIndex, chunkSize);
      // if (size > 0)
      {
        for (int i = 0; i < chunkSize; i++)
        {
          if (objects[i] instanceof ooId)
          {
            // TODO - this code need refactoring....

            // System.out.println("-->> IS: getting Class_Object from OID: "
            // + childObject.getStoreString());
            Class_Object refClassObject = Class_Object.class_object_from_oid((ooId)objects[i]);

            if (refClassObject.type_of().name().equals(ObjyProxy.className))
            {
              ObjyProxy proxyObject = new ObjyProxy(refClassObject);

              results.add(OBJYCDOIDUtil.createCDIDExternal(proxyObject));
            }
            else
            {
              results.add(OBJYCDOIDUtil.getCDOID((ooId)objects[i]));
            }
          }
          else if (objects[i] instanceof ObjyFeatureMapEntry)
          {
            ObjyFeatureMapEntry mapEntry = (ObjyFeatureMapEntry)objects[i];
            ooId oid = mapEntry.getObject();
            CDOID cdoId = null;
            Class_Object refClassObject = Class_Object.class_object_from_oid(oid);
            if (refClassObject.type_of().name().equals(ObjyProxy.className))
            {
              ObjyProxy proxyObject = new ObjyProxy(refClassObject);
              cdoId = OBJYCDOIDUtil.createCDIDExternal(proxyObject);
            }
            else
            {
              cdoId = OBJYCDOIDUtil.getCDOID((ooId)objects[i]);
            }

            throw new UnsupportedOperationException("FIXME");

            // FIXME Uncomment the following 2 lines:
            // FeatureMap.Entry entry = CDORevisionUtil.createFeatureMapEntry(entryFeature, cdoId);
            // results.add(entry);
          }
        }
      }
    }
    catch (com.objy.as.asException ex)
    {
      ex.printStackTrace();
    }

    return results.toArray();
  }

  /**
   * Get the size of the composite object using the store info.
   * 
   * @param feature
   * @return
   */
  private int size(EStructuralFeature feature)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }

    // Class_Position position = objyClass().resolve_position(feature.getName());

    IManyTypeMapper mapper = (IManyTypeMapper)ObjyMapper.INSTANCE.getTypeMapper(feature);

    int size = mapper.size(this, feature);

    if (TRACER_DEBUG.isEnabled())
    {
      // TODO - verify the message.
      TRACER_DEBUG.trace("Size of object " + ooId().getStoreString() + " - is: " + size + " - feature: "
          + feature.getName());
    }
    return size;
  }

  /**
   * Get the value of an attribute from the persistent object.
   */
  public Object get(EStructuralFeature feature)
  {
    return get(feature, 0);
  }

  /**
   * Get the value of an attribute at index (if many) from the persistent object.
   * 
   * @param feature
   * @param index
   * @return
   */
  private Object get(EStructuralFeature feature, int index)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
    }

    if (TRACER_DEBUG.isEnabled())
    {
      // TODO - verify the message.
      TRACER_DEBUG.trace("Getting object " + objectId.getStoreString() + " <feature ' " + feature.getName() + "':"
          + feature.getEType() + "> from " + this);
    }

    // Class_Position position =
    // this.objyClass().resolve_position(feature.getName());

    ITypeMapper mapper = ObjyMapper.INSTANCE.getTypeMapper(feature);
    Object value = null;
    if (feature.isMany())
    {
      value = ((IManyTypeMapper)mapper).getValue(this, feature, index);
    }
    else
    {
      value = ((ISingleTypeMapper)mapper).getValue(this, feature);
    }

    return value;
  }

  /**
   * Get all the values of an attribute, used for feature.isMany(). This function is also used by the
   * ObjectivityStoreChunkReader to read chunks of data from a feature.
   * 
   * @param feature
   * @param size
   * @return
   */
  protected Object[] getAll(EStructuralFeature feature, int startIndex, int chunkSize)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
      // TODO - verify the message.
      TRACER_DEBUG.trace("Get All objects for ID: " + ooId().getStoreString() + " <feature ' " + feature/*
                                                                                                         * .getName( )
                                                                                                         */
          + "':" + feature.getEType() + "> from " + this);
    }

    assert feature.isMany();

    IManyTypeMapper mapper = (IManyTypeMapper)ObjyMapper.INSTANCE.getTypeMapper(feature);
    Object[] values = mapper.getAll(this, feature, startIndex, chunkSize);

    return values;
  }

  public void add(EStructuralFeature feature, int index, Object value)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
      TRACER_DEBUG.trace("Adding object " + value + " to " + ooId().getStoreString());
    }

    assert feature.isMany();

    IManyTypeMapper mapper = (IManyTypeMapper)ObjyMapper.INSTANCE.getTypeMapper(feature);

    // -- TODO -- verify the need to this one.
    // ensureObjectAttached(feature, value);

    // I believe we do the conversion in the add()
    // value = provider.convertToStore(ooObject, value);

    mapper.add(this, feature, index, value);
  }

  /***
   * @param feature
   */
  public void clear(EStructuralFeature feature)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
      TRACER_DEBUG.trace("Clear List for " + ooId().getStoreString());
    }

    // Class_Position position = objyClass.resolve_position(feature.getName());

    ITypeMapper mapper = ObjyMapper.INSTANCE.getTypeMapper(feature);

    ((IManyTypeMapper)mapper).clear(this, feature);
  }

  public void move(EStructuralFeature feature, int targetIndex, int sourceIndex)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
      TRACER_DEBUG.trace("Move element from " + sourceIndex + " to " + targetIndex);
    }

    ITypeMapper mapper = ObjyMapper.INSTANCE.getTypeMapper(feature);

    ((IManyTypeMapper)mapper).move(this, feature, targetIndex, sourceIndex);
  }

  public Object remove(EStructuralFeature feature, int index)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
      TRACER_DEBUG.trace("Remove object from '" + ooId().getStoreString() + "' at index " + index);
    }

    // Class_Position position = objyClass.resolve_position(feature.getName());

    IManyTypeMapper mapper = (IManyTypeMapper)ObjyMapper.INSTANCE.getTypeMapper(feature);

    Object value = mapper.remove(this, feature, index);

    if (feature instanceof EAttribute)
    {
      return value;
    }

    Object objectFromResource = OBJYCDOIDUtil.getCDOID((ooId)value);

    return objectFromResource;

  }

  public Object set(EStructuralFeature feature, int index, Object value)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      checkSession();
      TRACER_DEBUG.trace("Set object '" + ooId().getStoreString() + "' feature : " + feature.getName());
    }

    /*
     * int i =0; for (EClass superEClass : object.eClass().getESuperTypes()) { if
     * (feature.getEContainingClass().isSuperTypeOf(superEClass)) { break; } i++; break; } String className = null; if
     * (i == 0) { className = EProposedManager.getObjectivityClass(feature.getEContainingClass()); } else { className =
     * EProposedManager.getObjectivityClass(feature.getEContainingClass(), true); } Class_Position position =
     * ooObject.ooClass().resolve_position(className + "::" + feature.getName());
     */
    // Class_Position position = objyClass.resolve_position(feature.getName());

    ITypeMapper mapper = ObjyMapper.INSTANCE.getTypeMapper(feature);

    // --- TODO --- verify the need...
    // ensureObjectAttached(this, feature, value);

    /***
     * I believe we do the conversion in the setValue if (feature instanceof EReference) { value =
     * CDOIDUtil.getooId((CDOID)value); }
     ***/

    if (feature.isMany())
    {
      ((IManyTypeMapper)mapper).setValue(this, feature, index, value);
    }
    else
    {
      ((ISingleTypeMapper)mapper).setValue(this, feature, value);
    }

    return value;
  }

  /***
   * @param feature
   */
  public void unset(EStructuralFeature feature)
  {
    set(feature, 0, null);
  }

  protected void checkSession()
  {
    // 100610 - IS: disabled for now, since we have an issue with oojava_epl.jar "getSession()"
    // is not available.
    // if (TRACER_DEBUG.isEnabled())
    // {
    // try
    // {
    // if (!classObject.getPersistor().getSession().isOpen())
    // {
    // throw new Exception("Attempt to work on an object " + objectId.toString() + " without a trx. [Session: "
    // + classObject.getPersistor().getSession() + "]");
    // }
    // }
    // catch (Exception ex)
    // {
    // ex.printStackTrace();
    // } // for debugging.
    // }
  }

  /***
   * Iterate over the attributes and references and mark them deleted. This will only be called in non-audit mode.
   */
  public void delete(ObjectivityStoreAccessor storeAccessor, ObjyObjectManager objectManager)
  {
    EClass eClass = ObjySchema.getEClass(storeAccessor.getStore(), objyClass());

    for (EStructuralFeature feature : eClass.getEAllStructuralFeatures())
    {
      if (!(feature instanceof EAttribute || feature instanceof EReference) || !EMFUtil.isPersistent(feature))
      {
        continue;
      }

      if (feature.isMany())
      {
        // TODO - verify that we can do this to all referenced list.
        // I'm not sure if it's valid when you have many-many
        // relationship.
        Object[] objects = getAll(feature, 0, -1);

        for (int i = 0; i < objects.length; i++)
        {
          if (objects[i] instanceof ooId)
          {
            ooId oid = (ooId)objects[i];
            // TODO - this code need refactoring....
            ooObj obj = ooObj.create_ooObj(oid);
            if (obj.isDead())
            {
              continue;
            }

            Class_Object refClassObject = Class_Object.class_object_from_oid(oid);

            if (refClassObject.type_of().name().equals(ObjyProxy.className))
            {
              obj.delete();
            }
            else
            {
              // if this object is a parent (resourceId or
              // containerId) for obj, then we
              // need to mark obj version as (-1).
              ObjyObject childObjyObject = objectManager.getObject(oid);
              ooId containerId = childObjyObject.getEContainerAsOid();
              ooId resourceId = childObjyObject.getEResourceAsOid();
              int childVersion = childObjyObject.getVersion();
              if (containerId.equals(objectId) || resourceId.equals(objectId))
              {
                childObjyObject.setVersion(-childVersion);
              }

            }
          }
          else if (objects[i] instanceof ObjyFeatureMapEntry)
          {
            ObjyFeatureMapEntry mapEntry = (ObjyFeatureMapEntry)objects[i];
            ooId oid = mapEntry.getObject();
            ooObj obj = ooObj.create_ooObj(oid);
            obj.delete();
          }
          else
          {
            // different feature then.
            if (TRACER_DEBUG.isEnabled())
            {
              TRACER_DEBUG.trace("-->> No process to delete() feature (" + i + ") -> feature:" + feature.getName()
                  + " - value:" + objects[i] + " ... nothing to do here.");
            }
          }
        }
      }
      else
      {
        ITypeMapper mapper = ObjyMapper.INSTANCE.getTypeMapper(feature);

        if (mapper == null)
        {
          continue;
        }
        mapper.delete(this, feature);
      }
    }
  }

  // Wrapper functions over class object.
  public Numeric_Value get_numeric(Class_Position position)
  {
    return classObject.get_numeric(position);
  }

  public String_Value get_string(Class_Position position)
  {
    return classObject.get_string(position);
  }

  public void set_numeric(Class_Position position, Numeric_Value value)
  {
    classObject.set_numeric(position, value);
  }

  public VArray_Object get_varray(Class_Position position)
  {
    return classObject.get_varray(position);
  }

  public ooId get_ooId(Class_Position position)
  {
    return classObject.get_ooId(position);
  }

  public Class_Object get_class_obj(Class_Position position)
  {
    return classObject.get_class_obj(position);
  }

  public void set_ooId(Class_Position position, ooId object)
  {
    classObject.set_ooId(position, object);
  }

  /**
   * Return the CDOID for the ObjyObject, it will go up to the base revision, and return it's ID.
   */
  public CDOID getRevisionId()
  {
    ooId oid = objectId;

    if (baseClassObject != null)
    {
      oid = baseClassObject.objectID();
    }

    return OBJYCDOIDUtil.getCDOID(oid);
  }

  /**
   * Return the revision that satisfies the timeStamp and branchId constrains.
   */
  public ObjyObject getRevision(long timeStamp, int branchId)
  {
    ObjyObject objyRevision = null;
    Session.getCurrent().setReturn_Class_Object(true);
    // int numRevisions = (int) revisions.size();

    // evaluate current first.
    if (evaluateRevision(timeStamp, branchId, this))
    {
      return this;
    }

    // if we don't have other revisions.
    if (!lastRevisionRel.exists())
    {
      return null;
    }

    @SuppressWarnings("unchecked")
    Iterator<Class_Object> itr = revisionsRel.get_iterator();
    while (itr.hasNext())
    {
      objyRevision = new ObjyObject(itr.next());
      if (evaluateRevision(timeStamp, branchId, objyRevision))
      {
        return objyRevision;
      }
    }

    return null;
  }

  /**
   * return true if the objyRevision satisfies the constrains.
   */
  protected boolean evaluateRevision(long timeStamp, int branchId, ObjyObject objyRevision)
  {
    // check the branchId first.
    if (objyRevision.getBranchId() == branchId)
    {
      long revisedTS = objyRevision.getRevisedTime();
      if (timeStamp != 0)
      {
        long creationTS = objyRevision.getCreationTime();
        if (creationTS <= timeStamp && (revisedTS == 0 || revisedTS >= timeStamp))
        {
          return true;
        }
      }
      else if (revisedTS == 0) // return the latest version in that branch.
      {
        return true;
      }
    }
    return false;
  }

  public void detach(int version, CDOBranch branch, long timeStamp)
  {
    ObjyClass objyClass = ObjySchema.getObjyClass(ObjyBase.CLASS_NAME);
    Class_Object detachedClassObject = Class_Object.new_persistent_object(objyClass.getASClass(), objectId, false);
    ObjyObject detachedObjyObject = new ObjyObject(detachedClassObject);

    detachedObjyObject.setVersion(-(version + 1));
    detachedObjyObject.setBranchId(branch.getID());
    detachedObjyObject.setCreationTime(timeStamp);

    // add it to the revisions.
    addToRevisions(detachedObjyObject);
  }

}
