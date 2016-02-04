/*
 * Copyright (c) 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.ecore.EObject;

/**
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @author Eike Stepper
 * @since 4.5
 */
public interface CDOUnit
{
  public CDOUnitManager getManager();

  public EObject getRoot();

  public int getElements();

  /**
   * Same as calling {@link #close() close(true}.
   */
  public void close();

  /**
   * Closes this unit and optionally {@link CDOView.Options#addChangeSubscriptionPolicy(CDOAdapterPolicy) resubscribes}
   * all contained objects for change notifications.
   */
  public void close(boolean resubscribe);
}
