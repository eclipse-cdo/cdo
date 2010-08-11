/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */

package org.eclipse.emf.cdo.internal.server.db4o;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Victor Roldan Betancort
 */
public class PrimitiveRevision
{
  private String nsURI;

  private String className;

  private Object id;

  private int version;

  private int branchID;

  private long revised;

  private long timeStamp;

  private Object resourceID;

  private Object containerID;

  private int containingFeatureID;

  private List<Object> values;

  private boolean isResource;

  private boolean isResourceNode;

  private boolean isResourceFolder;

  private boolean isRootResource;

  public PrimitiveRevision(String nsURI, String className, Object id, int version, int branchID, long revised,
      Object resourceID, Object containerID, int containingFeatureID, List<Object> values, long timestamp,
      boolean isResource, boolean isResourceNode, boolean isResourceFolder, boolean isRootResource)
  {
    setNsURI(nsURI);
    setClassName(className);
    setId(id);
    setVersion(version);
    setBranchID(branchID);
    setRevised(revised);
    setResourceID(resourceID);
    setContainerID(containerID);
    setContainingFeatureID(containingFeatureID);
    setValues(values);
    setTimeStamp(timestamp);
    setResource(isResource);
    setResourceNode(isResourceNode);
    setResourceFolder(isResourceFolder);
    setRootResource(isRootResource);
  }

  public void setNsURI(String nsURI)
  {
    this.nsURI = nsURI;
  }

  public String getNsURI()
  {
    return nsURI;
  }

  public void setClassName(String className)
  {
    this.className = className;
  }

  public String getClassName()
  {
    return className;
  }

  public void setId(Object id)
  {
    this.id = id;
  }

  public Object getId()
  {
    return id;
  }

  public void setVersion(int version)
  {
    this.version = version;
  }

  public int getVersion()
  {
    return version;
  }

  public void setBranchID(int branchID)
  {
    this.branchID = branchID;
  }

  public int getBranchID()
  {
    return branchID;
  }

  public void setRevised(long revised)
  {
    this.revised = revised;
  }

  public long getRevised()
  {
    return revised;
  }

  public void setResourceID(Object resourceID)
  {
    this.resourceID = resourceID;
  }

  public Object getResourceID()
  {
    return resourceID;
  }

  public void setContainerID(Object containerID)
  {
    this.containerID = containerID;
  }

  public Object getContainerID()
  {
    return containerID;
  }

  public void setContainingFeatureID(int containingFeatureID)
  {
    this.containingFeatureID = containingFeatureID;
  }

  public int getContainingFeatureID()
  {
    return containingFeatureID;
  }

  public void setValues(List<Object> values)
  {
    this.values = values;
  }

  public List<Object> getValues()
  {
    return values;
  }

  public void setTimeStamp(long timeStamp)
  {
    this.timeStamp = timeStamp;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public void setResource(boolean isResource)
  {
    this.isResource = isResource;
  }

  public boolean isResource()
  {
    return isResource;
  }

  public void setResourceNode(boolean isResourceNode)
  {
    this.isResourceNode = isResourceNode;
  }

  public boolean isResourceNode()
  {
    return isResourceNode;
  }

  public void setResourceFolder(boolean isResourceFolder)
  {
    this.isResourceFolder = isResourceFolder;
  }

  public boolean isResourceFolder()
  {
    return isResourceFolder;
  }

  static public PrimitiveRevision getPrimitiveRevision(InternalCDORevision revision)
  {
    CDOClassInfo classInfo = revision.getClassInfo();
    EClass eClass = classInfo.getEClass();
    String nsURI = eClass.getEPackage().getNsURI();
    String className = eClass.getName();

    if (revision.getID().isTemporary())
    {
      System.err.println("TEMPORARY CDOID");
    }

    boolean isResource = revision.isResource();
    boolean isResourceNode = revision.isResourceNode();
    boolean isResourceFolder = revision.isResourceFolder();
    boolean isRootResource = CDOIDUtil.getLong(revision.getID()) == 1;

    Object id = getObjectFromId(revision.getID());
    int version = revision.getVersion();
    int branchID = revision.getBranch().getID();
    long timeStamp = revision.getTimeStamp();
    long revised = revision.getRevised();
    Object resourceID = getObjectFromId(revision.getResourceID());
    Object containerID = getObjectFromId((CDOID)revision.getContainerID());
    int containingFeatureID = revision.getContainingFeatureID();

    EStructuralFeature[] features = classInfo.getAllPersistentFeatures();
    List<Object> values = new ArrayList<Object>(features.length);
    if (features.length > 0)
    {
      for (int i = 0; i < features.length; i++)
      {
        EStructuralFeature feature = features[i];
        Object obj = revision.getValue(feature);

        // We will process CDOList for EReferences to get rid of CDOIDs (we want to get only primitive types,
        // otherwise the database will persist unwanted objects coming from Object-level relationships

        // Multi-valued EAttributes (also kept in CDOList) will be saved as is
        if (obj instanceof InternalCDOList && feature instanceof EReference)
        {
          InternalCDOList cdoList = (InternalCDOList)obj;
          List<Object> list = new ArrayList<Object>();
          for (Object listElement : cdoList)
          {
            if (!(listElement instanceof CDOID))
            {
              throw new IllegalStateException("CDOList should contain only CDOID instances but received "
                  + listElement.getClass().getName() + " instead");
            }
            else
            {
              list.add(getObjectFromId((CDOID)listElement));
            }
          }
          values.add(i, list);
        }
        else
        {
          values.add(i, obj);
        }
      }
    }
    return new PrimitiveRevision(nsURI, className, id, version, branchID, revised, resourceID, containerID,
        containingFeatureID, values, timeStamp, isResource, isResourceNode, isResourceFolder, isRootResource);
  }

  public static InternalCDORevision getRevision(IStore store, PrimitiveRevision primitiveRevision)
  {
    String nsURI = primitiveRevision.getNsURI();
    String className = primitiveRevision.getClassName();
    EPackage ePackage = store.getRepository().getPackageRegistry().getEPackage(nsURI);
    EClass eClass = (EClass)ePackage.getEClassifier(className);
    InternalCDORevision revision = (InternalCDORevision)CDORevisionFactory.DEFAULT.createRevision(eClass);

    revision.setID(getIDFromObject(primitiveRevision.getId()));
    revision.setVersion(primitiveRevision.getVersion());
    int branchID = primitiveRevision.getBranchID();
    CDOBranch branch = store.getRepository().getBranchManager().getBranch(branchID);
    CDOBranchPoint point = branch.getPoint(primitiveRevision.getTimeStamp());
    revision.setBranchPoint(point);
    revision.setRevised(primitiveRevision.getRevised());
    revision.setResourceID(getIDFromObject(primitiveRevision.getResourceID()));
    revision.setContainerID(getIDFromObject(primitiveRevision.getContainerID()));
    revision.setContainingFeatureID(primitiveRevision.getContainingFeatureID());
    EStructuralFeature[] features = revision.getClassInfo().getAllPersistentFeatures();

    int i = 0;
    List<?> values = primitiveRevision.getValues();
    for (Object value : values)
    {
      EStructuralFeature feature = features[i++];
      // transform List to CDOList
      if (feature instanceof EReference && value instanceof List<?>)
      {
        List<?> sourceList = (List<?>)value;
        // CDOList list = new CDOListImpl(sourceList.size(), sourceList.size());
        CDOList list = CDOListFactory.DEFAULT.createList(sourceList.size(), sourceList.size(), sourceList.size());
        for (int j = 0; j < sourceList.size(); j++)
        {
          list.set(j, getIDFromObject(sourceList.get(j)));
        }
        revision.setValue(feature, list);
      }
      else
      {
        revision.setValue(feature, value);
      }
    }

    return revision;
  }

  public static Object getObjectFromId(CDOID cdoid)
  {
    Object objectID = null;
    if (cdoid.isExternal())
    {
      objectID = new String(((CDOIDExternal)cdoid).getURI());
    }
    else
    {
      objectID = CDOIDUtil.getLong(cdoid);
    }
    return objectID;
  }

  public static CDOID getIDFromObject(Object id)
  {
    if (id == null)
    {
      return CDOID.NULL;
    }
    else if (id instanceof String)
    {
      return CDOIDUtil.createExternal((String)id);
    }
    else
    {
      return CDOIDUtil.createLong((Long)id);
    }
  }

  public static boolean compareIDObject(Object id1, Object id2)
  {
    if (id1.getClass().equals(id2.getClass()))
    {
      if (id1.equals(id2))
      {
        return true;
      }
    }

    return false;
  }

  public void setRootResource(boolean isRootResource)
  {
    this.isRootResource = isRootResource;
  }

  public boolean isRootResource()
  {
    return isRootResource;
  }

}
