/*
 * Copyright (c) 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om.log;

import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger.Level;

/**
 * Filters log events before they are being passed to the registered {@link OMLogHandler log handlers}.
 *
 * @since 3.2
 * @author Eike Stepper
 * @see OMPlatform#addLogFilter(OMLogFilter)
 * @see OMPlatform#removeLogFilter(OMLogFilter)
 */
public interface OMLogFilter
{
  /**
   * Prevents the log event represented by the arguments from being passed to
   * {@link OMPlatform#addLogFilter(OMLogFilter) registered} {@link OMLogHandler log handlers}, if and only if
   * <code>true</code> is returned.
   */
  public boolean filter(OMLogger logger, Level level, String msg, Throwable t);
}
