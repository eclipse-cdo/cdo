/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.connector;

import org.eclipse.net4j.acceptor.IAcceptor;

/**
 * @author Eike Stepper
 * @since 4.5
 */
public interface IServerConnector extends IConnector
{
  public IAcceptor getAcceptor();
}
