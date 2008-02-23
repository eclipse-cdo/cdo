/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.protocol.CDOProtocolView;
import org.eclipse.emf.cdo.protocol.model.CDOPackageManager;

/**
 * @author Eike Stepper
 */
public interface ITransaction extends IView
{
  /**
   * Returns the ID of this transactional view. Same as {@link CDOProtocolView#getViewID() getViewID()}.
   */
  public int getTransactionID();

  /**
   * Returns the temporary, transactional package manager associated with this ITransaction during the process of a
   * commit operation. In addition to the packages registered with the session
   * {@link IRepository#getPackageManager() package manager} this package manager also contains the new packages that
   * are part of the commit operation.
   * 
   * @return a temporary, transactional package manager if this ITransaction is in the process of a commit operation,
   *         <code>null</code> otherwise.
   */
  public CDOPackageManager getPackageManager();
}
