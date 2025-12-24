/*
 * Copyright (c) 2008, 2010-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.util.options;

import org.eclipse.net4j.util.event.Event;

/**
 * The default implementation of an {@link IOptionsEvent options event}.
 *
 * @since 2.0
 * @author Victor Roldan Betancort
 */
public class OptionsEvent extends Event implements IOptionsEvent
{
  private static final long serialVersionUID = 1L;

  public OptionsEvent(IOptions source)
  {
    super(source);
  }

  @Override
  public IOptions getSource()
  {
    return (IOptions)super.getSource();
  }
}
