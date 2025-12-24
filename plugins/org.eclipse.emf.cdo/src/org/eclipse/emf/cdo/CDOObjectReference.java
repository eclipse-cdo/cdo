/*
 * Copyright (c) 2010-2012, 2014, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOReference;

/**
 * Represents a {@link CDOObject} typed reference from one object to another object.
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOObjectReference extends CDOReference<CDOObject>
{
  /**
   * @since 4.6
   */
  public CDOID getTargetID();

  /**
   * @since 4.6
   */
  public CDOID getSourceID();
}
