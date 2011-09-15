/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Fluegge - bug 247226: Transparently support legacy models
 */
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.cdo.CDONotification;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.spi.cdo.CDOStore;
import org.eclipse.emf.spi.cdo.FSMUtil;

import java.util.List;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class CDOLegacyAdapter extends CDOLegacyWrapper implements Adapter.Internal
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOLegacyAdapter.class);

  /**
   * @since 3.0
   */
  public CDOLegacyAdapter(InternalEObject object)
  {
    super(object);

    instance.eAdapters().add(this);
    ((org.eclipse.emf.common.notify.impl.BasicNotifierImpl.EObservableAdapterList)instance.eAdapters())
        .addListener(new AdapterListListener());
  }

  public void setTarget(Notifier newTarget)
  {
    instance = (InternalEObject)newTarget;
  }

  public void unsetTarget(Notifier oldTarget)
  {
    if (instance == oldTarget)
    {
      instance = null;
    }
  }

  public Notifier getTarget()
  {
    return instance;
  }

  public boolean isAdapterForType(Object type)
  {
    return type == CDOLegacyAdapter.class;
  }

  public void notifyChanged(Notification msg)
  {
    if (msg.isTouch() || msg instanceof CDONotification)
    {
      return;
    }

    EStructuralFeature feature = (EStructuralFeature)msg.getFeature();
    if (view == null || feature == null || !(view instanceof CDOTransaction))
    {
      return;
    }

    CDOStore store = view.getStore();
    if (EMFUtil.isPersistent(feature))
    {
      switch (msg.getEventType())
      {
      case Notification.SET:
        store.set(instance, feature, msg.getPosition(), msg.getNewValue());
        break;

      case Notification.UNSET:
        store.unset(instance, feature);
        break;

      case Notification.MOVE:
        // TODO Is that correct?
        store.move(instance, feature, msg.getPosition(), (Integer)msg.getOldValue());
        break;

      case Notification.ADD:
        store.add(instance, feature, msg.getPosition(), msg.getNewValue());
        break;

      case Notification.ADD_MANY:
      {
        int pos = msg.getPosition();
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>)msg.getNewValue();
        for (Object object : list)
        {
          store.add(instance, feature, pos++, object);
        }

        break;
      }

      case Notification.REMOVE:
      {
        store.remove(instance, feature, msg.getPosition());
        break;
      }

      case Notification.REMOVE_MANY:
      {
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>)msg.getOldValue();
        for (int i = list.size() - 1; i >= 0; --i)
        {
          store.remove(instance, feature, i);
        }

        break;
      }
      }

      // Align Container for bidirectional references because this is not set in the store. See Bugzilla_246622_Test
      instanceToRevisionContainment();
    }
  }

  /**
   * @author Martin Flügge
   * @since 3.0
   */
  protected class AdapterListListener implements
      org.eclipse.emf.common.notify.impl.BasicNotifierImpl.EObservableAdapterList.Listener
  {
    /**
     * @since 4.0
     */
    public AdapterListListener()
    {
    }

    public void added(Notifier notifier, Adapter adapter)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Added : {0} to {1} ", adapter, CDOLegacyAdapter.this); //$NON-NLS-1$
      }

      if (!FSMUtil.isTransient(CDOLegacyAdapter.this))
      {
        cdoView().handleAddAdapter(CDOLegacyAdapter.this, adapter);
      }
    }

    public void removed(Notifier notifier, Adapter adapter)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Removed : {0} from {1} ", adapter, CDOLegacyAdapter.this); //$NON-NLS-1$
      }

      if (!FSMUtil.isTransient(CDOLegacyAdapter.this))
      {
        cdoView().handleRemoveAdapter(CDOLegacyAdapter.this, adapter);
      }
    }
  }
}
