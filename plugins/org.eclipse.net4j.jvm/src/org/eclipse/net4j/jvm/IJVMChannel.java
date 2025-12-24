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
package org.eclipse.net4j.jvm;

import org.eclipse.net4j.channel.IChannel;

/**
 * A {@link IChannel channel} of a {@link IJVMConnector JVM connector}.
 *
 * @author Eike Stepper
 * @since 4.1
 */
public interface IJVMChannel extends IChannel
{
  public IJVMChannel getPeer();
}
