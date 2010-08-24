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

package org.eclipse.emf.cdo.server.internal.db4o;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
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
public class DB4ORevision
{
  private String packageURI;

  private String className;

  private long id;

  private int version;

  private int branchID;

  private long revised;

  private long timeStamp;

  private long resourceID;

  private long containerID;

  private int containingFeatureID;

  private List<Object> values;

  // TODO enum RevisionType { NORMAL, ROOT_RESOURCE, RESOURCE, RESOURCE_FOLDER }??
  private boolean isResource;

  private boolean isResourceNode;

  private boolean isResourceFolder;

  private boolean isRootResource;

  public DB4ORevision(String packageURI, String className, long id, int version, int branchID, long revised,
      long resourceID, long containerID, int containingFeatureID, List<Object> values, long timestamp,
      boolean isResource, boolean isResourceNode, boolean isResourceFolder, boolean isRootResource)
  {
    setPackageURI(packageURI);
    setClassName(className);
    setID(id);
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

  public void setPackageURI(String packageURI)
  {
    this.packageURI = packageURI;
  }

  public String getPackageURI()
  {
    return packageURI;
  }

  public void setClassName(String className)
  {
    this.className = className;
  }

  public String getClassName()
  {
    return className;
  }

  public void setID(long id)
  {
    this.id = id;
  }

  public long getID()
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

  public void setResourceID(long resourceID)
  {
    this.resourceID = resourceID;
  }

  public long getResourceID()
  {
    return resourceID;
  }

  public void setContainerID(long containerID)
  {
    this.containerID = containerID;
  }

  public long getContainerID()
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

  public void setRootResource(boolean isRootResource)
  {
    this.isRootResource = isRootResource;
  }

  public boolean isRootResource()
  {
    return isRootResource;
  }

  public static DB4ORevision getDB4ORevision(InternalCDORevision revision)
  {
    CDOClassInfo classInfo = revision.getClassInfo();
    EClass eClass = classInfo.getEClass();
    String packageURI = eClass.getEPackage().getNsURI();
    String className = eClass.getName();

    CDOID revisionID = revision.getID();
    if (revisionID.isTemporary())
    {
      throw new IllegalArgumentException("TEMPORARY CDOID: " + revisionID);
    }

    boolean isResource = revision.isResource();
    boolean isResourceNode = revision.isResourceNode();
    boolean isResourceFolder = revision.isResourceFolder();
    boolean isRootResource = CDOIDUtil.getLong(revisionID) == 1;

    long id = (Long)getDB4OID(revisionID);
    int version = revision.getVersion();
    int branchID = revision.getBranch().getID();
    long timeStamp = revision.getTimeStamp();
    long revised = revision.getRevised();
    long resourceID = (Long)getDB4OID(revision.getResourceID());
    long containerID = (Long)getDB4OID((CDOID)revision.getContainerID());
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

            list.add(getDB4OID((CDOID)listElement));
          }

          values.add(i, list);
        }
        else
        {
          values.add(i, obj);
        }
      }
    }

    return new DB4ORevision(packageURI, className, id, version, branchID, revised, resourceID, containerID,
        containingFeatureID, values, timeStamp, isResource, isResourceNode, isResourceFolder, isRootResource);
  }

  public static InternalCDORevision getCDORevision(IStore store, DB4ORevision primitiveRevision)
  {
    String nsURI = primitiveRevision.getPackageURI();
    String className = primitiveRevision.getClassName();
    EPackage ePackage = store.getRepository().getPackageRegistry().getEPackage(nsURI);
    EClass eClass = (EClass)ePackage.getEClassifier(className);
    InternalCDORevision revision = (InternalCDORevision)CDORevisionFactory.DEFAULT.createRevision(eClass);

    CDOBranch branch = store.getRepository().getBranchManager().getBranch(primitiveRevision.getBranchID());
    CDOBranchPoint point = branch.getPoint(primitiveRevision.getTimeStamp());

    revision.setID(getCDOID(primitiveRevision.getID()));
    revision.setVersion(primitiveRevision.getVersion());
    revision.setBranchPoint(point);
    revision.setRevised(primitiveRevision.getRevised());
    revision.setResourceID(getCDOID(primitiveRevision.getResourceID()));
    revision.setContainerID(getCDOID(primitiveRevision.getContainerID()));
    revision.setContainingFeatureID(primitiveRevision.getContainingFeatureID());
    EStructuralFeature[] features = revision.getClassInfo().getAllPersistentFeatures();

    int i = 0;
    for (Object value : primitiveRevision.getValues())
    {
      EStructuralFeature feature = features[i++];
      if (feature instanceof EReference && value instanceof List<?>)
      {
        List<?> sourceList = (List<?>)value;
        CDOList list = CDOListFactory.DEFAULT.createList(sourceList.size(), sourceList.size(), CDORevision.UNCHUNKED);
        for (int j = 0; j < sourceList.size(); j++)
        {
          list.set(j, getCDOID(sourceList.get(j)));
        }

        value = list;
      }

      revision.setValue(feature, value);
    }

    return revision;
  }

  public static Object getDB4OID(CDOID id)
  {
    if (id.isExternal())
    {
      return new String(((CDOIDExternal)id).getURI());
    }

    return CDOIDUtil.getLong(id);
  }

  public static CDOID getCDOID(Object id)
  {
    if (id == null)
    {
      return CDOID.NULL;
    }

    if (id instanceof String)
    {
      return CDOIDUtil.createExternal((String)id);
    }

    return CDOIDUtil.createLong((Long)id);
  }
}
