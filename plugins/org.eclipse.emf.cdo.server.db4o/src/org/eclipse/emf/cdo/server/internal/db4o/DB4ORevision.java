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
  public final static String ATTRIBUTE_CLASS_NAME = "className";

  public final static String ATTRIBUTE_PACKAGE_NS_URI = "packageNsURI";

  private String packageNsURI;

  private String className;

  private long id;

  private int version;

  private long timeStamp;

  private long resourceID;

  /**
   * Can be an external ID!
   */
  private Object containerID;

  private int containingFeatureID;

  private List<Object> values;

  // TODO enum RevisionType { NORMAL, ROOT_RESOURCE, RESOURCE, RESOURCE_FOLDER }??
  private boolean isResource;

  private boolean isResourceFolder;

  public DB4ORevision(String packageURI, String className, long id, int version, long resourceID, Object containerID,
      int containingFeatureID, List<Object> values, long timestamp, boolean isResource, boolean isResourceFolder)
  {
    setPackageURI(packageURI);
    setClassName(className);
    setID(id);
    setVersion(version);
    setResourceID(resourceID);
    setContainerID(containerID);
    setContainingFeatureID(containingFeatureID);
    setValues(values);
    setTimeStamp(timestamp);
    setResource(isResource);
    setResourceFolder(isResourceFolder);
  }

  public void setPackageURI(String packageURI)
  {
    packageNsURI = packageURI;
  }

  public String getPackageURI()
  {
    return packageNsURI;
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

  public int getBranchID()
  {
    return CDOBranch.MAIN_BRANCH_ID;
  }

  public void setVersion(int version)
  {
    this.version = version;
  }

  public int getVersion()
  {
    return version;
  }

  public long getRevised()
  {
    return CDORevision.UNSPECIFIED_DATE;
  }

  public void setResourceID(long resourceID)
  {
    this.resourceID = resourceID;
  }

  public long getResourceID()
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

  public void setResourceFolder(boolean isResourceFolder)
  {
    this.isResourceFolder = isResourceFolder;
  }

  public boolean isResourceFolder()
  {
    return isResourceFolder;
  }

  public boolean isResourceNode()
  {
    return isResource || isResourceFolder;
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
    boolean isResourceFolder = revision.isResourceFolder();

    long id = CDOIDUtil.getLong(revisionID);
    int version = revision.getVersion();
    long timeStamp = revision.getTimeStamp();
    long resourceID = CDOIDUtil.getLong(revision.getResourceID());
    Object containerID = getDB4OID((CDOID)revision.getContainerID());
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

    return new DB4ORevision(packageURI, className, id, version, resourceID, containerID, containingFeatureID, values,
        timeStamp, isResource, isResourceFolder);
  }

  public static InternalCDORevision getCDORevision(IStore store, DB4ORevision primitiveRevision)
  {
    String nsURI = primitiveRevision.getPackageURI();
    String className = primitiveRevision.getClassName();
    EPackage ePackage = store.getRepository().getPackageRegistry().getEPackage(nsURI);
    EClass eClass = (EClass)ePackage.getEClassifier(className);
    InternalCDORevision revision = (InternalCDORevision)CDORevisionFactory.DEFAULT.createRevision(eClass);

    CDOBranch branch = store.getRepository().getBranchManager().getMainBranch();
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

    if (id instanceof CDOID)
    {
      return (CDOID)id;
    }

    return CDOIDUtil.createLong((Long)id);
  }
}
