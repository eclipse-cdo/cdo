/*
 * Copyright (c) 2008, 2011, 2012, 2021, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.spi.net4j;

import org.eclipse.net4j.ITransportConfigAware;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.util.security.INegotiatorAware;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface InternalAcceptor extends IAcceptor, ITransportConfigAware, INegotiatorAware
{
  /**
   * @since 4.16
   */
  public boolean needsBufferProvider();
}
