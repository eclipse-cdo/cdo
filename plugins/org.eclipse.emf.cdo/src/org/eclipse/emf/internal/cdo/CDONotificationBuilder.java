/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
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
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;

import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;

import java.util.Set;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDONotificationBuilder implements CDOFeatureDeltaVisitor
{
  private InternalCDORevisionManager revisionManager;

  private InternalEObject object;

  private CDORevisionDelta revisionDelta;

  private CDODeltaNotificationImpl notification;

  private InternalCDORevision revision;

  private boolean revisionLookedUp;

  private Set<CDOObject> detachedObjects;

  public CDONotificationBuilder(InternalCDORevisionManager revisionManager)
  {
    this.revisionManager = revisionManager;
  }

  public InternalCDORevisionManager getRevisionManager()
  {
    return revisionManager;
  }

  public synchronized NotificationImpl buildNotification(InternalEObject object, CDORevisionDelta revisionDelta,
      Set<CDOObject> detachedObjects)
  {
    notification = null;
    revision = null;
    revisionLookedUp = false;

    this.object = object;
    this.revisionDelta = revisionDelta;
    this.detachedObjects = detachedObjects;
    revisionDelta.accept(this);
    return notification;
  }

  public void visit(CDOMoveFeatureDelta delta)
  {
    int oldPosition = delta.getOldPosition();
    int newPosition = delta.getNewPosition();
    add(new CDODeltaNotificationImpl(object, NotificationImpl.MOVE, getEFeatureID(delta.getFeature()), new Integer(
        oldPosition), null, newPosition));
  }

  public void visit(CDOAddFeatureDelta delta)
  {
    add(new CDODeltaNotificationImpl(object, NotificationImpl.ADD, getEFeatureID(delta.getFeature()), null, delta
        .getValue(), delta.getIndex()));
  }

  public void visit(CDORemoveFeatureDelta delta)
  {
    EStructuralFeature feature = delta.getFeature();
    int index = delta.getIndex();
    if (!revisionLookedUp)
    {
      revision = revisionManager.getRevisionByVersion(revisionDelta.getID(), revisionDelta, CDORevision.UNCHUNKED,
          false);
    }

    Object oldValue = revision == null ? null : revision.get(feature, index);
    if (oldValue instanceof CDOID)
    {
      CDOID id = (CDOID)oldValue;
      oldValue = null;
      for (CDOObject detachedObject : detachedObjects)
      {
        if (detachedObject.cdoID().equals(id))
        {
          oldValue = detachedObject;
          break;
        }
      }
    }

    add(new CDODeltaNotificationImpl(object, NotificationImpl.REMOVE, getEFeatureID(feature), oldValue, null, index));
  }

  public void visit(CDOSetFeatureDelta delta)
  {
    add(new CDODeltaNotificationImpl(object, NotificationImpl.SET, getEFeatureID(delta.getFeature()), null, delta
        .getValue()));
  }

  public void visit(CDOUnsetFeatureDelta delta)
  {
    add(new CDODeltaNotificationImpl(object, NotificationImpl.UNSET, getEFeatureID(delta.getFeature()), null, null));
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
    add(new CDODeltaNotificationImpl(object, NotificationImpl.REMOVE_MANY, getEFeatureID(delta.getFeature()), null,
        null));
  }

  public void visit(CDOContainerFeatureDelta delta)
  {
    add(new CDODeltaNotificationImpl(object, NotificationImpl.SET, EcorePackage.eINSTANCE.eContainmentFeature(), null,
        delta.getContainerID()));
  }

  protected void add(CDODeltaNotificationImpl newNotificaton)
  {
    newNotificaton.setRevisionDelta(revisionDelta);
    if (notification == null)
    {
      notification = newNotificaton;
    }
    else
    {
      notification.add(newNotificaton);
    }
  }

  private int getEFeatureID(EStructuralFeature eFeature)
  {
    return object.eClass().getFeatureID(eFeature);
  }
}
