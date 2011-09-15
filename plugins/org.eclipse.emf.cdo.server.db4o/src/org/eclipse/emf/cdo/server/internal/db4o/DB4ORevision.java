/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.spi.common.revision.CDOFeatureMapEntry;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;

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
        else if (listContainsInstancesOfClass(obj, CDOFeatureMapEntry.class)) // FeatureMap
        {
          values.add(i, DB4OFeatureMapEntry.getPrimitiveFeatureMapEntryList(obj));
        }
        else
        {
          // Prevent the explicit null-ref "NIL" from being serialized!
          if (obj == CDORevisionData.NIL)
          {
            obj = new ExplicitNull();
          }

          values.add(i, obj);
        }
      }
    }

    return new DB4ORevision(packageURI, className, id, version, resourceID, containerID, containingFeatureID, values,
        timeStamp, isResource, isResourceFolder);
  }

  public static InternalCDORevision getCDORevision(IStore store, DB4ORevision primitiveRevision)
  {
    IRepository repository = store.getRepository();
    CDORevisionFactory factory = ((InternalCDORevisionManager)repository.getRevisionManager()).getFactory();
    CDOBranch branch = repository.getBranchManager().getMainBranch();

    String nsURI = primitiveRevision.getPackageURI();
    String className = primitiveRevision.getClassName();
    EPackage ePackage = repository.getPackageRegistry().getEPackage(nsURI);
    EClass eClass = (EClass)ePackage.getEClassifier(className);
    InternalCDORevision revision = (InternalCDORevision)factory.createRevision(eClass);

    revision.setID(getCDOID(primitiveRevision.getID()));
    revision.setVersion(primitiveRevision.getVersion());
    revision.setBranchPoint(branch.getPoint(primitiveRevision.getTimeStamp()));
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
      else if (listContainsInstancesOfClass(value, DB4OFeatureMapEntry.class))
      {
        value = DB4OFeatureMapEntry.getCDOFeatureMapEntryList(eClass, value);
      }

      // Convert 'null' into the explicit null-ref "NIL" if appropriate
      if (value instanceof ExplicitNull)
      {
        value = CDORevisionData.NIL;
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

  public static boolean listContainsInstancesOfClass(Object obj, Class<?> clazz)
  {
    if (obj instanceof List)
    {
      List<?> list = (List<?>)obj;
      for (Object potentialFeatureMap : list)
      {
        if (!clazz.isAssignableFrom(potentialFeatureMap.getClass()))
        {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  private static final class DB4OFeatureMapEntry
  {

    private int featureID;

    private Object valueID;

    public DB4OFeatureMapEntry(int featureID, Object valueID)
    {
      setFeatureID(featureID);
      setValueID(valueID);
    }

    private void setFeatureID(int featureID)
    {
      this.featureID = featureID;
    }

    public int getFeatureID()
    {
      return featureID;
    }

    private void setValueID(Object valueID)
    {
      this.valueID = valueID;
    }

    public Object getValueID()
    {
      return valueID;
    }

    public static List<DB4OFeatureMapEntry> getPrimitiveFeatureMapEntryList(Object obj)
    {
      InternalCDOList cdoList = (InternalCDOList)obj;
      List<DB4OFeatureMapEntry> list = new ArrayList<DB4OFeatureMapEntry>();
      for (Object listElement : cdoList)
      {
        if (listElement instanceof FeatureMap.Entry)
        {
          FeatureMap.Entry entry = (FeatureMap.Entry)listElement;
          EStructuralFeature entryFeature = entry.getEStructuralFeature();
          CDOID entryValue = (CDOID)entry.getValue();
          DB4OFeatureMapEntry db4oEntry = new DB4OFeatureMapEntry(entryFeature.getFeatureID(), getDB4OID(entryValue));
          list.add(db4oEntry);
        }
      }
      return list;
    }

    public static CDOList getCDOFeatureMapEntryList(EClass eClass, Object value)
    {
      List<?> sourceList = (List<?>)value;
      CDOList list = CDOListFactory.DEFAULT.createList(sourceList.size(), sourceList.size(), CDORevision.UNCHUNKED);
      for (int j = 0; j < sourceList.size(); j++)
      {
        DB4OFeatureMapEntry mapEntry = (DB4OFeatureMapEntry)sourceList.get(j);
        EStructuralFeature entryFeature = eClass.getEStructuralFeature(mapEntry.getFeatureID());
        CDOID valueID = getCDOID(mapEntry.getValueID());
        list.set(j, CDORevisionUtil.createFeatureMapEntry(entryFeature, valueID));
      }
      return list;
    }
  }

  /**
   * @author Caspar De Groot
   */
  private static final class ExplicitNull
  {
  }
}
