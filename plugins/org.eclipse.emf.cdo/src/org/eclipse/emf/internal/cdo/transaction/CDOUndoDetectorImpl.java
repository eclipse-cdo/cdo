/*
 * Copyright (c) 2014, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOUndoDetector;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 4.3
 */
public class CDOUndoDetectorImpl implements CDOUndoDetector
{
  @Override
  public boolean detectUndo(CDOTransaction transaction, CDORevision cleanRevision, CDORevision currentRevision, CDOFeatureDelta featureDelta)
  {
    EStructuralFeature feature = featureDelta.getFeature();
    InternalCDORevision cleanInternalRevision = (InternalCDORevision)cleanRevision;
    InternalCDORevision currentInternalRevision = (InternalCDORevision)currentRevision;

    if (ignore(feature, cleanInternalRevision, currentInternalRevision))
    {
      return false;
    }

    if (feature == CDOContainerFeatureDelta.CONTAINER_FEATURE)
    {
      return detectUndoContainer(transaction, cleanInternalRevision, currentInternalRevision);
    }

    Object cleanValue = cleanInternalRevision.getValue(feature);
    Object currentValue = currentInternalRevision.getValue(feature);

    if (feature instanceof EReference)
    {
      if (feature.isMany())
      {
        if (feature.isUnsettable())
        {
          if (cleanValue == null)
          {
            return currentValue == null;
          }

          if (currentValue == null)
          {
            return false;
          }
        }

        List<?> cleanList = (List<?>)cleanValue;
        List<?> currentList = (List<?>)currentValue;

        int cleanSize = size(cleanList);
        int currentSize = size(currentList);

        if (cleanSize != currentSize)
        {
          return false;
        }

        if (cleanSize != 0)
        {
          for (Iterator<?> cleanIterator = cleanList.iterator(), currentIterator = currentList.iterator(); //
              cleanIterator.hasNext();)
          {
            Object cleanID = cleanIterator.next();
            Object currentID = currentIterator.next();
            if (!equalReference(transaction, cleanID, currentID))
            {
              return false;
            }
          }
        }

        return true;
      }

      return equalReference(transaction, cleanValue, currentValue);
    }

    return ObjectUtil.equals(cleanValue, currentValue);
  }

  /**
   * @deprecated As of 4.5 {@link #detectUndo(CDOTransaction, CDORevision, CDORevision, CDOFeatureDelta)} is called.
   */
  @Deprecated
  protected boolean detectUndoContainer(InternalCDORevision cleanRevision, InternalCDORevision currentRevision)
  {
    throw new UnsupportedOperationException();
  }

  protected boolean detectUndoContainer(CDOTransaction transaction, InternalCDORevision cleanRevision, InternalCDORevision currentRevision)
  {
    CDOID cleanResourceID = cleanRevision.getResourceID();
    CDOID currentResourceID = currentRevision.getResourceID();
    if (cleanResourceID != currentResourceID)
    {
      return false;
    }

    int cleanContainerFeatureID = cleanRevision.getContainerFeatureID();
    int currentContainerFeatureID = currentRevision.getContainerFeatureID();

    if (cleanContainerFeatureID != currentContainerFeatureID)
    {
      return false;
    }

    Object cleanContainerID = cleanRevision.getContainerID();
    Object currentContainerID = currentRevision.getContainerID();

    if (!equalReference(transaction, cleanContainerID, currentContainerID))
    {
      return false;
    }

    return true;
  }

  protected boolean ignore(EStructuralFeature feature, InternalCDORevision cleanRevision, InternalCDORevision currentRevision)
  {
    return feature.isMany() && !cleanRevision.isUnchunked() && !currentRevision.isUnchunked();
  }

  private static boolean equalReference(CDOTransaction transaction, Object cleanValue, Object currentValue)
  {
    if (currentValue instanceof EObject && cleanValue instanceof CDOIDExternal)
    {
      CDOID id = ((CDOIDProvider)transaction).provideCDOID(currentValue);
      if (id != null)
      {
        currentValue = id;
      }
    }

    return cleanValue == currentValue;
  }

  private static int size(List<?> list)
  {
    if (list == null)
    {
      return 0;
    }

    return list.size();
  }

  /**
   * @author Eike Stepper
   */
  public static final class NoFeatures implements CDOUndoDetector
  {
    @Override
    public boolean detectUndo(CDOTransaction transaction, CDORevision cleanRevision, CDORevision currentRevision, CDOFeatureDelta featureDelta)
    {
      return false;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class SingleValuedFeatures extends CDOUndoDetectorImpl
  {
    @Override
    protected boolean ignore(EStructuralFeature feature, InternalCDORevision cleanRevision, InternalCDORevision currentRevision)
    {
      if (feature.isMany())
      {
        return false;
      }

      return super.ignore(feature, cleanRevision, currentRevision);
    }
  }
}
