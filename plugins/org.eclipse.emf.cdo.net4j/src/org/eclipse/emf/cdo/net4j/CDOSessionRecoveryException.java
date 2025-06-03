/*
 * Copyright (c) 2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.net4j;

import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.internal.net4j.RecoveringCDOSessionImpl;

/**
 * An exception thrown from {@link RecoveringCDOSessionImpl#recoverSession}.
 *
 * @author Eike Stepper
 * @since 4.6
 */
public final class CDOSessionRecoveryException extends CDOException
{
  private static final long serialVersionUID = 1L;

  private static final String MESSAGE = "Session recovery failed";

  private final CDONet4jSession session;

  public CDOSessionRecoveryException(CDONet4jSession session)
  {
    super(MESSAGE);
    this.session = session;
  }

  public CDOSessionRecoveryException(CDONet4jSession session, Throwable cause)
  {
    super(MESSAGE, cause);
    this.session = session;
  }

  public CDONet4jSession getSession()
  {
    return session;
  }
}
