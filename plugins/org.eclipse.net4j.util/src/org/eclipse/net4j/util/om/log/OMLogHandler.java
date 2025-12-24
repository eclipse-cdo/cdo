/*
 * Copyright (c) 2007, 2011, 2012, 2024 Eike Stepper (Loehne, Germany) and others.
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
 * Handles {@link OMLogFilter filtered} log events, for example appends them to a {@link PrintLogHandler stream} or
 * the Eclipse {@link EclipseLoggingBridge error log}.
 *
 * @author Eike Stepper
 * @see OMPlatform#addLogHandler(OMLogHandler)
 * @see OMPlatform#removeLogHandler(OMLogHandler)
 * @see EclipseLoggingBridge#INSTANCE
 * @see PrintLogHandler#CONSOLE
 * @see OSGiLoggingBridge#INSTANCE
 * @see FileLogHandler
 */
@FunctionalInterface
public interface OMLogHandler
{
  public void logged(OMLogger logger, Level level, String msg, Throwable t);
}
