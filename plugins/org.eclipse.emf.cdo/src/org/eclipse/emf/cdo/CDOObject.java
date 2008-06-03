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
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;

import org.eclipse.emf.ecore.EObject;

/**
 * @author Eike Stepper
 */
public interface CDOObject extends EObject
{
  public CDOClass cdoClass();

  public CDOID cdoID();

  public CDOState cdoState();

  public CDOView cdoView();

  public CDOResource cdoResource();

  public CDORevision cdoRevision();

  public void cdoReload();
}
