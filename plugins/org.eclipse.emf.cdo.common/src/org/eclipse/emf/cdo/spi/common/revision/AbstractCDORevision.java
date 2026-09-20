/*
 * Copyright (c) 2009-2013, 2015, 2018, 2019, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 212958
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionValueVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOClassInfo;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.text.MessageFormat;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class AbstractCDORevision implements InternalCDORevision, InternalCDOList.Owner
{
  private InternalCDOClassInfo classInfo;

  private RevisionPCLState pclState;

  /**
   * @since 3.0
   */
  protected AbstractCDORevision(EClass eClass)
  {
    if (eClass != null)
    {
      if (eClass.isAbstract())
      {
        throw new IllegalArgumentException(MessageFormat.format(Messages.getString("AbstractCDORevision.0"), eClass)); //$NON-NLS-1$
      }

      initClassInfo(eClass);
    }
  }

  /**
   * @since 4.2
   */
  protected AbstractCDORevision(InternalCDOClassInfo classInfo)
  {
    this.classInfo = classInfo;
  }

  /**
   * @since 4.2
   */
  @Override
  public final InternalCDOClassInfo getClassInfo()
  {
    return pclState == null ? classInfo : pclState.classInfo;
  }

  @Override
  public final void unloadedCountChanged(int delta)
  {
    int count = (pclState == null ? 0 : pclState.unloadedCount) + delta;
    if (count < 0)
    {
      throw new IllegalStateException("Negative unloaded count");
    }

    if (count == 0)
    {
      if (pclState != null)
      {
        classInfo = pclState.classInfo;
      }

      pclState = null;
    }
    else if (pclState == null)
    {
      pclState = new RevisionPCLState(classInfo, count);
    }
    else
    {
      pclState.unloadedCount = count;
    }
  }

  protected final int getUnloadedCount()
  {
    return pclState == null ? 0 : pclState.unloadedCount;
  }

  @Override
  public final EClass getEClass()
  {
    InternalCDOClassInfo classInfo = getClassInfo();
    if (classInfo != null)
    {
      return classInfo.getEClass();
    }

    return null;
  }

  /**
   * @since 4.2
   */
  @Override
  public InternalCDORevision getRevisionForID(CDOID id)
  {
    if (id != null && id.equals(getID()))
    {
      throw new ImplementationError();
    }

    return getClassInfo().getRevisionForID(id);
  }

  /**
   * @since 4.2
   */
  @Override
  public InternalCDORevision getProperRevision()
  {
    return this;
  }

  @Override
  public boolean isResourceNode()
  {
    return getClassInfo().isResourceNode();
  }

  @Override
  public boolean isResourceFolder()
  {
    return getClassInfo().isResourceFolder();
  }

  @Override
  public boolean isResource()
  {
    return getClassInfo().isResource();
  }

  @Override
  public CDORevisionData data()
  {
    return this;
  }

  @Override
  public CDORevision revision()
  {
    return this;
  }

  /**
   * @since 3.0
   */
  @Override
  public boolean isHistorical()
  {
    return getRevised() != UNSPECIFIED_DATE;
  }

  @Override
  public boolean isValid(long timeStamp)
  {
    long startTime = getTimeStamp();
    long endTime = getRevised();
    return CDOCommonUtil.isValidTimeStamp(timeStamp, startTime, endTime);
  }

  /**
   * @since 4.0
   */
  @Override
  public boolean isValid(CDOBranchPoint branchPoint)
  {
    return getBranch() == branchPoint.getBranch() && isValid(branchPoint.getTimeStamp());
  }

  /**
   * @since 4.1
   */
  @Override
  public boolean isReadable()
  {
    return getPermission().isReadable();
  }

  /**
   * @since 4.1
   */
  @Override
  public boolean isWritable()
  {
    return getPermission().isWritable();
  }

  /**
   * @since 4.2
   */
  @Override
  public void accept(CDORevisionValueVisitor visitor)
  {
    accept(visitor, (java.util.function.Predicate<EStructuralFeature>)t -> true);
  }

  /**
   * @since 4.2
   */
  @Override
  @Deprecated
  public void accept(CDORevisionValueVisitor visitor, org.eclipse.net4j.util.Predicate<EStructuralFeature> filter)
  {
    accept(visitor, org.eclipse.net4j.util.Predicates.toJava8(filter));
  }

  /**
   * @since 4.9
   */
  @Override
  public void accept(CDORevisionValueVisitor visitor, java.util.function.Predicate<EStructuralFeature> filter)
  {
    // This visitor traverses currently loaded values only. Unloaded positions are deliberately not materialized.
    for (EStructuralFeature feature : getClassInfo().getAllPersistentFeatures())
    {
      if (filter.test(feature))
      {
        if (feature.isMany())
        {
          CDOList list = getListOrNull(feature);
          if (list != null)
          {
            for (int i = 0, size = list.size(); i < size; i++)
            {
              if (list.isLoadedAt(i))
              {
                Object value = list.get(i, false);
                visitor.visit(feature, value, i);
              }
            }
          }
        }
        else
        {
          Object value = getValue(feature);
          visitor.visit(feature, value, CDOFeatureDelta.NO_INDEX);
        }
      }
    }
  }

  /**
   * @since 3.0
   */
  @Override
  public void adjustForCommit(CDOBranch branch, long timeStamp)
  {
    if (branch == getBranch())
    {
      // Same branch, increase version
      setVersion(getVersion() + 1);
    }
    else
    {
      // Different branch, start with v1
      setVersion(FIRST_VERSION);
    }

    setBranchPoint(branch.getPoint(timeStamp));
    setRevised(UNSPECIFIED_DATE);
  }

  @Override
  public int hashCode()
  {
    return ObjectUtil.hashCode(getID()) ^ ObjectUtil.hashCode(getBranch()) ^ getVersion();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDORevision)
    {
      CDORevision that = (CDORevision)obj;
      return getID() == that.getID() && getVersion() == that.getVersion() && getBranch() == that.getBranch();
    }

    return false;
  }

  @Override
  public String toString()
  {
    EClass eClass = getEClass();
    String name = eClass == null ? "Revision" : eClass.getName();

    String string = name + "@" + getID();

    CDOBranch branch = getBranch();
    if (branch != null)
    {
      string += ":" + branch.getID();
    }

    string += "v" + getVersion();

    if (isResourceNode())
    {
      String resourceNodeName = getResourceNodeName();
      if (resourceNodeName == null)
      {
        resourceNodeName = "/";
      }

      string += "(\"" + resourceNodeName + "\")";
    }

    return string;
  }

  @Override
  @Deprecated
  public CDOList getList(EStructuralFeature feature)
  {
    return getOrCreateList(feature);
  }

  @Override
  @Deprecated
  public CDOList getList(EStructuralFeature feature, int initialCapacity)
  {
    return getOrCreateList(feature, initialCapacity);
  }

  /**
   * @since 4.2
   */
  protected void initClassInfo(EClass eClass)
  {
    InternalCDOClassInfo newClassInfo = (InternalCDOClassInfo)CDOModelUtil.getClassInfo(eClass);

    if (pclState == null)
    {
      classInfo = newClassInfo;
    }
    else
    {
      pclState.classInfo = newClassInfo;
    }
  }

  /**
   * @since 3.0
   */
  protected EStructuralFeature[] getAllPersistentFeatures()
  {
    return getClassInfo().getAllPersistentFeatures();
  }

  /**
   * @since 3.0
   */
  protected int getFeatureIndex(EStructuralFeature feature)
  {
    return getClassInfo().getPersistentFeatureIndex(feature);
  }

  /**
   * @author Eike Stepper
   */
  private static final class RevisionPCLState
  {
    private InternalCDOClassInfo classInfo;

    private int unloadedCount;

    private RevisionPCLState(InternalCDOClassInfo classInfo, int unloadedCount)
    {
      this.classInfo = classInfo;
      this.unloadedCount = unloadedCount;
    }
  }
}
