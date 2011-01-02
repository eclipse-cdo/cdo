/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class CDOObjectReferenceImpl implements CDOObjectReference
{
  private CDOView view;

  private CDOID targetID;

  private CDOID sourceID;

  private CDOClassifierRef classifierRef;

  private String featureName;

  private int sourceIndex;

  public CDOObjectReferenceImpl(CDOView view, CDOID targetID, CDOID sourceID, CDOClassifierRef classifierRef,
      String featureName, int sourceIndex)
  {
    this.view = view;
    this.targetID = targetID;
    this.sourceID = sourceID;
    this.classifierRef = classifierRef;
    this.featureName = featureName;
    this.sourceIndex = sourceIndex;
  }

  public CDOObject getTargetObject()
  {
    return view.getObject(targetID);
  }

  public CDOObject getSourceObject()
  {
    return view.getObject(sourceID);
  }

  public EReference getSourceReference()
  {
    CDOPackageRegistry packageRegistry = view.getSession().getPackageRegistry();
    EClass eClass = (EClass)classifierRef.resolve(packageRegistry);
    return (EReference)eClass.getEStructuralFeature(featureName);
  }

  public int getSourceIndex()
  {
    return sourceIndex;
  }
}
