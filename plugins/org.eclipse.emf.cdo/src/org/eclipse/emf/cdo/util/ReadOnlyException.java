/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2014 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * An unchecked exception being thrown if write access to {@link CDOObject objects} of a {@link CDOView#isReadOnly()
 * read-only} view is attempted.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class ReadOnlyException extends CDOException
{
  private static final long serialVersionUID = 1L;

  public ReadOnlyException()
  {
  }

  public ReadOnlyException(String message)
  {
    super(message);
  }

  public ReadOnlyException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public ReadOnlyException(Throwable cause)
  {
    super(cause);
  }
}
