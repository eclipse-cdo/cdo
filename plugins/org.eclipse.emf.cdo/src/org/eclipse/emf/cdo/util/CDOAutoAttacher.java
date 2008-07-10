/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOTransactionHandler;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;

import org.eclipse.emf.internal.cdo.InternalCDOObject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.List;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOAutoAttacher implements CDOTransactionHandler
{
  CDOTransaction cdoTransaction;

  class CDOFeatureDeltaVisitorAutoAttach implements CDOFeatureDeltaVisitor
  {

    Resource resource;

    CDOFeatureDeltaVisitorAutoAttach(Resource resource)
    {
      this.resource = resource;
    }

    public void visit(CDOAddFeatureDelta featureChange)
    {
      persist(resource, featureChange.getValue());
    }

    public void visit(CDOClearFeatureDelta featureChange)
    {

    }

    public void visit(CDOListFeatureDelta featureChange)
    {

    }

    public void visit(CDOMoveFeatureDelta featureChange)
    {

    }

    public void visit(CDORemoveFeatureDelta featureChange)
    {
    }

    public void visit(CDOSetFeatureDelta featureChange)
    {
      persist(resource, featureChange.getValue());
    }

    public void visit(CDOUnsetFeatureDelta featureChange)
    {
    }

    public void visit(CDOContainerFeatureDelta delta)
    {
    }

  };

  public CDOAutoAttacher(CDOTransaction transaction)
  {
    cdoTransaction = transaction;
    transaction.addHandler(this);
  }

  protected void persist(Resource res, Object object)
  {
    if (object instanceof CDOResource)
    {
      return;
    }

    if (!(object instanceof InternalCDOObject))
    {
      return;
    }

    res.getContents().add((InternalCDOObject)object);
  }

  /**
   * @param eObject
   */
  @SuppressWarnings("unchecked")
  private void handle(Resource resource, EObject eObject)
  {
    for (EReference reference : eObject.eClass().getEAllReferences())
    {
      if (reference.isMany())
      {
        List<EObject> list = (List<EObject>)eObject.eGet(reference);
        for (EObject element : list)
        {
          if (element.eResource() == null)
          {
            if (reference.isContainment())
            {
              handle(resource, element);
            }
            else
            {
              persist(resource, element);
            }
          }
        }
      }
      else
      {
        EObject element = (EObject)eObject.eGet(reference);

        if (element != null && element.eResource() == null)
        {
          // objectsAdded.add(element);
          if (reference.isContainment())
          {
            handle(resource, element);
          }
          else
          {
            persist(resource, element);
          }
        }
      }
    }

  }

  public void addingObject(CDOTransaction transaction, CDOObject object)
  {
    if (object instanceof CDOResource)
    {
      return;
    }

    // Persist the graph as well.
    handle(object.eResource(), object);
  }

  public void committingTransaction(CDOTransaction transaction)
  {

  }

  public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureChange)
  {
    if (object instanceof CDOResource)
    {
      return;
    }

    if (featureChange != null)
    {
      CDOFeatureDeltaVisitorAutoAttach featureChangeVisitor = new CDOFeatureDeltaVisitorAutoAttach(object.cdoResource());
      featureChange.accept(featureChangeVisitor);
    }

  }

  public void rolledBackTransaction(CDOTransaction transaction)
  {
  }

}
