/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.internal.util.factory.Factory;
import org.eclipse.net4j.tcp.ITCPConstants;

import org.eclipse.internal.net4j.bundle.OM;

/**
 * @author Eike Stepper
 */
public class TCPSelectorFactory extends Factory<TCPSelector>
{
  public static final String SELECTOR_GROUP = OM.BUNDLE_ID + ".selectors";

  public TCPSelectorFactory()
  {
    super(SELECTOR_GROUP, ITCPConstants.TYPE);
  }

  public TCPSelector create(String description)
  {
    return new TCPSelector();
  }
}
