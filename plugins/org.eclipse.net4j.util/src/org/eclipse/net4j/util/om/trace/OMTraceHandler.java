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
package org.eclipse.net4j.util.om.trace;

/**
 * Handles {@link TraceHandlerEvent trace events}, for example appends them to a {@link PrintTraceHandler stream} sends them to a
 * {@link RemoteTraceHandler remote} trace handler.
 *
 * @author Eike Stepper
 * @see PrintTraceHandler#CONSOLE
 * @see RemoteTraceHandler
 */
@FunctionalInterface
public interface OMTraceHandler
{
  public void traced(OMTraceHandlerEvent event);
}
