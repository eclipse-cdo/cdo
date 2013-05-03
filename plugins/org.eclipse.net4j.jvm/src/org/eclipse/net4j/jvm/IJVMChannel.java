/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
