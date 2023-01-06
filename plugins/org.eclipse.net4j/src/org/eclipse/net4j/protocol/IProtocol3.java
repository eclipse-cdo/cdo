/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.protocol;

/**
 * A {@link IProtocol protocol} that is told when it is {@link #doWhenFullyConnected() fully connected}.
 *
 * @author Eike Stepper
 * @since 4.17
 */
public interface IProtocol3<INFRA_STRUCTURE> extends IProtocol2<INFRA_STRUCTURE>
{
  public default void doWhenFullyConnected() throws Exception
  {
  }
}
