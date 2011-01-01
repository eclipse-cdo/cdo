/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
