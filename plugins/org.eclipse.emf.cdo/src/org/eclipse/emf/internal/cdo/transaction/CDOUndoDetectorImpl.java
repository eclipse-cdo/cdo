/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta.Type;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOUndoDetector;

import org.eclipse.net4j.util.ObjectUtil;

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
  public boolean detectUndo(CDOTransaction transaction, CDORevision cleanRevision, CDORevision revision,
      CDOFeatureDelta featureDelta)
  {
    EStructuralFeature feature = featureDelta.getFeature();
    if (ignore(feature))
    {
      return false;
    }

    if (ignore(cleanRevision))
    {
      return false;
    }

    if (ignore(revision))
    {
      return false;
    }

    InternalCDORevision rev1 = (InternalCDORevision)cleanRevision;
    InternalCDORevision rev2 = (InternalCDORevision)revision;

    if (featureDelta.getType() == Type.CONTAINER)
    {
      // return false;
      return detectUndoContainer(transaction, rev1, rev2, (CDOContainerFeatureDelta)featureDelta);
    }

    Object value1 = rev1.getValue(feature);
    Object value2 = rev2.getValue(feature);

    if (feature instanceof EReference)
    {
      if (feature.isMany())
      {
        List<?> list1 = (List<?>)value1;
        List<?> list2 = (List<?>)value2;

        int size1 = size(list1);
        int size2 = size(list2);

        if (size1 != size2)
        {
          return false;
        }

        if (size1 != 0)
        {
          for (Iterator<?> it1 = list1.iterator(), it2 = list2.iterator(); it1.hasNext();)
          {
            Object id1 = getID(it1.next());
            Object id2 = getID(it2.next());
            if (id1 != id2)
            {
              return false;
            }
          }
        }

        return true;
      }

      value1 = getID(value1);
      value2 = getID(value2);
      return value1 == value2;
    }

    return ObjectUtil.equals(value1, value2);
  }

  protected boolean detectUndoContainer(CDOTransaction transaction, InternalCDORevision cleanRevision,
      InternalCDORevision revision, CDOContainerFeatureDelta featureDelta)
  {
    CDOID resourceID1 = cleanRevision.getResourceID();
    CDOID resourceID2 = revision.getResourceID();
    if (resourceID1 != resourceID2)
    {
      return false;
    }

    int containingFeatureID1 = cleanRevision.getContainingFeatureID();
    int containingFeatureID2 = revision.getContainingFeatureID();
    if (containingFeatureID1 != containingFeatureID2)
    {
      return false;
    }

    Object c1 = cleanRevision.getContainerID();
    Object c2 = revision.getContainerID();

    // Potentially most expensive check because of EObject/ID conversion
    Object containerID1 = getID(c1);
    Object containerID2 = getID(c2);
    if (containerID1 != containerID2)
    {
      return false;
    }

    return true;
  }

  protected boolean ignore(EStructuralFeature feature)
  {
    return false;
  }

  protected boolean ignore(CDORevision revision)
  {
    return !((InternalCDORevision)revision).isUnchunked();
  }

  private static Object getID(Object value)
  {
    // TODO Write tests to see if EObject instead of CDOID instances need special handling
    // CDOID id = CDOIDUtil.getCDOID(value);
    // if (id != null)
    // {
    // return id;
    // }

    return value;
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
  public static final class NoFeatures extends CDOUndoDetectorImpl
  {
    @Override
    protected boolean ignore(EStructuralFeature feature)
    {
      return true;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class SingleValuedFeatures extends CDOUndoDetectorImpl
  {
    @Override
    protected boolean ignore(EStructuralFeature feature)
    {
      return feature.isMany();
    }
  }
}
