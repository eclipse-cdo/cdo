/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.ui;

import org.eclipse.net4j.buddies.common.ICollaborationContainer;
import org.eclipse.net4j.util.ui.StructuredContentProvider;

/**
 * @author Eike Stepper
 */
public class CollaborationsContentProvider extends StructuredContentProvider<ICollaborationContainer>
{
  public CollaborationsContentProvider()
  {
  }

  @Override
  public Object[] getElements(Object inputElement)
  {
    return getInput().getElements();
  }

  @Override
  protected void connectInput(ICollaborationContainer input)
  {
    input.addListener(this);
  }

  @Override
  protected void disconnectInput(ICollaborationContainer input)
  {
    input.removeListener(this);
  }
}
