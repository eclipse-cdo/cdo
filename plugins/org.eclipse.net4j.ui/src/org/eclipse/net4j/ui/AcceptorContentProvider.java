/*
 * Copyright (c) 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.ui;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.ui.StructuredContentProvider;

import org.eclipse.spi.net4j.AcceptorFactory;

/**
 * A {@link StructuredContentProvider structured content provider} that shows {@link IAcceptor acceptors}.
 *
 * @author Eike Stepper
 * @since 4.0
 */
public class AcceptorContentProvider extends StructuredContentProvider<IManagedContainer>
{
  public static final String PRODUCT_GROUP = AcceptorFactory.PRODUCT_GROUP;

  public AcceptorContentProvider()
  {
  }

  @Override
  public Object[] getElements(Object inputElement)
  {
    return getInput().getElements(PRODUCT_GROUP);
  }

  @Override
  protected void connectInput(IManagedContainer input)
  {
    input.addListener(this);
  }

  @Override
  protected void disconnectInput(IManagedContainer input)
  {
    input.removeListener(this);
  }
}
