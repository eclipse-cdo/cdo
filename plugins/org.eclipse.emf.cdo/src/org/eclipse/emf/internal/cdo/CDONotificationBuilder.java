/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.session.CDOPackageRegistry;

import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDONotificationBuilder implements CDOFeatureDeltaVisitor
{
  private CDODeltaNotificationImpl notification;

  private CDORevisionDelta delta;

  private InternalEObject internalObject;

  private CDOPackageRegistry packageRegistry;

  public CDONotificationBuilder(CDOPackageRegistry packageRegistry)
  {
    this.packageRegistry = packageRegistry;
  }

  public synchronized NotificationImpl buildNotification(InternalEObject internalObject, CDORevisionDelta delta)
  {
    notification = null;
    this.internalObject = internalObject;
    this.delta = delta;
    delta.accept(this);
    return notification;
  }

  public void visit(CDOMoveFeatureDelta delta)
  {
    EStructuralFeature eFeature = ModelUtil.getEFeature(delta.getFeature(), packageRegistry);
    int oldPosition = delta.getOldPosition();
    int newPosition = delta.getNewPosition();
    add(new CDODeltaNotificationImpl(internalObject, NotificationImpl.MOVE, eFeature.getFeatureID(), new Integer(
        oldPosition), null, newPosition));
  }

  public void visit(CDOAddFeatureDelta delta)
  {
    EStructuralFeature eFeature = ModelUtil.getEFeature(delta.getFeature(), packageRegistry);
    add(new CDODeltaNotificationImpl(internalObject, NotificationImpl.ADD, eFeature.getFeatureID(), null, delta
        .getValue(), delta.getIndex()));
  }

  public void visit(CDORemoveFeatureDelta delta)
  {
    EStructuralFeature eFeature = ModelUtil.getEFeature(delta.getFeature(), packageRegistry);
    add(new CDODeltaNotificationImpl(internalObject, NotificationImpl.REMOVE, eFeature.getFeatureID(), null, null,
        delta.getIndex()));
  }

  public void visit(CDOSetFeatureDelta delta)
  {
    EStructuralFeature eFeature = ModelUtil.getEFeature(delta.getFeature(), packageRegistry);
    add(new CDODeltaNotificationImpl(internalObject, NotificationImpl.SET, eFeature.getFeatureID(), null, delta
        .getValue()));
  }

  public void visit(CDOUnsetFeatureDelta delta)
  {
    EStructuralFeature eFeature = ModelUtil.getEFeature(delta.getFeature(), packageRegistry);
    add(new CDODeltaNotificationImpl(internalObject, NotificationImpl.UNSET, eFeature.getFeatureID(), null, null));
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
    EStructuralFeature eFeature = ModelUtil.getEFeature(delta.getFeature(), packageRegistry);
    add(new CDODeltaNotificationImpl(internalObject, NotificationImpl.REMOVE_MANY, eFeature.getFeatureID(), null, null));
  }

  public void visit(CDOContainerFeatureDelta delta)
  {
    add(new CDODeltaNotificationImpl(internalObject, NotificationImpl.SET,
        EcorePackage.eINSTANCE.eContainmentFeature(), null, delta.getContainerID()));
  }

  protected void add(CDODeltaNotificationImpl newNotificaton)
  {
    newNotificaton.setRevisionDelta(delta);
    if (notification == null)
    {
      notification = newNotificaton;
    }
    else
    {
      notification.add(newNotificaton);
    }
  }
}
