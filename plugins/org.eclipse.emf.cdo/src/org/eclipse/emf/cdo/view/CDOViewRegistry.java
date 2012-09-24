/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOViewRegistry.Registration;

import org.eclipse.net4j.util.container.IContainer;

/**
 * A global registry of all open {@link CDOView views} and {@link CDOTransaction transactions}.
 *
 * @author Eike Stepper
 * @since 4.2
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.composedOf {@link org.eclipse.emf.cdo.view.CDOView}
 */
public interface CDOViewRegistry extends IContainer<Registration>
{
  public static final CDOViewRegistry INSTANCE = org.eclipse.emf.internal.cdo.view.CDOViewRegistryImpl.INSTANCE;

  public static final int NOT_REGISTERED = 0;

  public int[] getIDs();

  public CDOView[] getViews();

  public int getID(CDOView view);

  public CDOView getView(int id);

  /**
   * @author Eike Stepper
   */
  public interface Registration
  {
    public int getID();

    public CDOView getView();
  }
}
