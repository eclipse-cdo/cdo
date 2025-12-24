/*
 * Copyright (c) 2011, 2012, 2014 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * An unchecked exception being thrown if {@link CDOLock locks} could not be
 * {@link CDOView#lockObjects(java.util.Collection, org.eclipse.net4j.util.concurrent.IRWLockManager.LockType, long)
 * acquired} within the specified timeout period.
 *
 * @author Caspar De Groot
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class LockTimeoutException extends CDOException
{
  private static final long serialVersionUID = 293135415513673577L;

  public LockTimeoutException()
  {
  }
}
