/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity;

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
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Simon McDuff
 */
public class ObjectivityFeatureDeltaWriter implements CDOFeatureDeltaVisitor
{
  private ObjyObject objyObject = null;

  private EStructuralFeature eFeature = null;

  public ObjectivityFeatureDeltaWriter(ObjyObject objyObject)
  {
    this.objyObject = objyObject;
  }

  public void nextFeature()
  {
    eFeature = null;
  }

  protected void fillStructuralFeature(CDOFeatureDelta delta)
  {
    eFeature = delta.getFeature();
  }

  public void visit(CDOMoveFeatureDelta delta)
  {
    fillStructuralFeature(delta);
    objyObject.move(eFeature, delta.getNewPosition(), delta.getOldPosition());

  }

  public void visit(CDOAddFeatureDelta delta)
  {
    fillStructuralFeature(delta);
    objyObject.add(eFeature, delta.getIndex(), delta.getValue());
  }

  public void visit(CDORemoveFeatureDelta delta)
  {
    fillStructuralFeature(delta);
    objyObject.remove(eFeature, delta.getIndex());
  }

  public void visit(CDOSetFeatureDelta delta)
  {
    fillStructuralFeature(delta);
    Object value = delta.getValue();
    /**
     * TODO - verify if this is needed for 2.x if (delta.getType()== CDOType.CUSTOM) { value =
     * EcoreUtil.createFromString((EDataType)eFeature.getEType(), (String)value); }
     */
    objyObject.set(eFeature, delta.getIndex(), value);
  }

  public void visit(CDOUnsetFeatureDelta delta)
  {
    fillStructuralFeature(delta);
    objyObject.unset(eFeature);
  }

  public void visit(CDOListFeatureDelta deltas)
  {
    for (CDOFeatureDelta delta : deltas.getListChanges())
    {
      delta.accept(this);
    }
  }

  public void visit(CDOClearFeatureDelta delta)
  {
    fillStructuralFeature(delta);
    objyObject.clear(eFeature);

  }

  public void visit(CDOContainerFeatureDelta delta)
  {
    objyObject.setEContainer(delta.getContainerID());
    objyObject.setEContainingFeature(delta.getContainerFeatureID());
    objyObject.setEResource(delta.getResourceID());

  }
}
