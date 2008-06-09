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
package org.eclipse.emf.cdo.common.id;

import org.eclipse.emf.cdo.common.id.CDOID.Type;

import java.io.Serializable;

/**
 * @author Eike Stepper
 */
public interface CDOIDMetaRange extends Serializable
{
  public CDOID getLowerBound();

  public CDOID getUpperBound();

  public CDOID get(int index);

  public int size();

  public boolean isEmpty();

  public boolean contains(CDOID id);

  public CDOIDMetaRange increase();

  public Type getType();

  public boolean isTemporary();
}
