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
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.runtime.PlatformObject;

/**
 * @author Eike Stepper
 */
public abstract class CDOCheckoutImpl extends PlatformObject implements CDOCheckout
{
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
    if (!isOpen())
    {
      view = openView();
    }
  }

  public final synchronized void close()
  {
    if (isOpen())
    {
      rootObject = null;

      view.close();
      view = null;
    }
  }

  public final synchronized CDOView getView()
  {
    open();
    return view;
  }

  public final synchronized EObject getRootObject()
  {
    if (rootObject == null)
    {
      rootObject = loadRootObject();
    }

    return rootObject;
  }

  @Override
  public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
  {
    if (adapter == EObject.class)
    {
      return getRootObject();
    }

    return super.getAdapter(adapter);
  }

  protected EObject loadRootObject()
  {
    CDOView view = getView();
    return view.getObject(rootID);
  }

  protected abstract CDOView openView();
}
