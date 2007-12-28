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
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.emf.cdo.CDOSession;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.swt.events.SelectionEvent;

/**
 * TODO Is this used/needed?
 * 
 * @author Eike Stepper
 */
public interface IPackageProvider
{
  public String getTitle();

  public EPackage getPackage(CDOSession session, SelectionEvent event);
}
