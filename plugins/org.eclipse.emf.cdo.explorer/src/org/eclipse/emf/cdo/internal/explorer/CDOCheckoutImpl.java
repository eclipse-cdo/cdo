/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.explorer.CDOCheckout;
import org.eclipse.emf.cdo.explorer.CDOCheckoutManager;
import org.eclipse.emf.cdo.explorer.CDORepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent.Kind;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * @author Eike Stepper
 */
public abstract class CDOCheckoutImpl extends AdapterImpl implements CDOCheckout
{
  private final IListener viewListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof ILifecycleEvent)
      {
        ILifecycleEvent e = (ILifecycleEvent)event;
        if (e.getKind() == Kind.DEACTIVATED)
        {
          close();
        }
      }
    }
  };

  private final CDOCheckoutManager checkoutManager;

  private final CDORepository repository;

  private String branchPath;

  private long timeStamp;

  private boolean readOnly;

  private CDOID rootID;

  private String label;

  private CDOView view;

  private EObject rootObject;

  public CDOCheckoutImpl(CDOCheckoutManager checkoutManager, CDORepository repository, String branchPath,
      long timeStamp, boolean readOnly, CDOID rootID, String label)
  {
    this.checkoutManager = checkoutManager;
    this.label = label;
    this.repository = repository;
    this.branchPath = branchPath;
    this.timeStamp = timeStamp;
    this.readOnly = readOnly;
    this.rootID = rootID;
  }

  public final CDOCheckoutManager getCheckoutManager()
  {
    return checkoutManager;
  }

  public final CDORepository getRepository()
  {
    return repository;
  }

  public final String getBranchPath()
  {
    return branchPath;
  }

  public final void setBranchPath(String branchPath)
  {
    this.branchPath = branchPath;
  }

  public final long getTimeStamp()
  {
    return timeStamp;
  }

  public final void setTimeStamp(long timeStamp)
  {
    this.timeStamp = timeStamp;
  }

  public final boolean isReadOnly()
  {
    return readOnly;
  }

  public final void setReadOnly(boolean readOnly)
  {
    this.readOnly = readOnly;
  }

  public final CDOID getRootID()
  {
    return rootID;
  }

  public final void setRootID(CDOID rootID)
  {
    this.rootID = rootID;
  }

  public final String getLabel()
  {
    return label;
  }

  public final void setLabel(String label)
  {
    this.label = label;
  }

  public final boolean isOpen()
  {
    return view != null;
  }

  public final synchronized void open()
  {
    boolean opened = false;
    synchronized (viewListener)
    {
      if (!isOpen())
      {
        CDOSession session = ((CDORepositoryImpl)repository).openCheckout(this);

        view = openView(session);
        view.addListener(viewListener);

        rootObject = loadRootObject();
        rootObject.eAdapters().add(this);

        opened = true;
      }
    }

    if (opened)
    {
      ((CDOCheckoutManagerImpl)checkoutManager).fireCheckoutOpenEvent(this, true);
    }
  }

  public final synchronized void close()
  {
    boolean closed = false;
    synchronized (viewListener)
    {
      if (isOpen())
      {
        rootObject.eAdapters().remove(this);
        rootObject = null;

        view.close();
        view = null;

        closed = true;
      }
    }

    if (closed)
    {
      ((CDOCheckoutManagerImpl)checkoutManager).fireCheckoutOpenEvent(this, false);
    }
  }

  public final CDOView getView()
  {
    return view;
  }

  public final EObject getRootObject()
  {
    return rootObject;
  }

  @Override
  public boolean isAdapterForType(Object type)
  {
    if (type == CDOCheckout.class)
    {
      return true;
    }

    return super.isAdapterForType(type);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    if (adapter == EObject.class)
    {
      return rootObject;
    }

    return AdapterUtil.adapt(this, adapter);
  }

  @Override
  public String toString()
  {
    return getLabel();
  }

  protected EObject loadRootObject()
  {
    return view.getObject(rootID);
  }

  protected abstract CDOView openView(CDOSession session);
}
