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
package org.eclipse.emf.cdo.explorer.checkouts;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.util.CDOTimeProvider;
import org.eclipse.emf.cdo.explorer.CDOExplorerElement;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EObject;

/**
 * A CDO checkout.
 *
 * @author Eike Stepper
 * @since 4.4
 * @apiviz.landmark
 */
public interface CDOCheckout extends CDOExplorerElement, CDOTimeProvider
{
  public CDORepository getRepository();

  public int getBranchID();

  public void setBranchID(int branchID);

  public long getTimeStamp();

  public void setTimeStamp(long timeStamp);

  public boolean isReadOnly();

  public void setReadOnly(boolean readOnly);

  public CDOID getRootID();

  public void setRootID(CDOID rootID);

  public State getState();

  public boolean isOpen();

  public void open();

  public void close();

  public CDOView getView();

  public EObject getRootObject();

  public RootType getRootType();

  public String getEditorID(CDOID objectID);

  public void setEditorID(CDOID objectID, String editorID);

  /**
   * @author Eike Stepper
   */
  public enum State
  {
    Opening, Open, Closing, Closed
  }

  /**
   * @author Eike Stepper
   */
  public enum RootType
  {
    Root, Folder, Resource, Object
  }
}
