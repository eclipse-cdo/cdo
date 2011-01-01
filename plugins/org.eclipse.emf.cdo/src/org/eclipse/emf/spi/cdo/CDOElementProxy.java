/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface CDOElementProxy
{
  public int getIndex();

  /**
   * @since 3.0
   */
  public Object resolve(CDOSession session, CDORevision revision, EStructuralFeature feature, int index);
}
