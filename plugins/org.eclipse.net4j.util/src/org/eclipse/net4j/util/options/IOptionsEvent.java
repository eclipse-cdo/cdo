/*
 * Copyright (c) 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.util.options;

import org.eclipse.net4j.util.event.IEvent;

/**
 * A generic {@link IEvent event} fired from an {@link IOptions options} object when an option has changed.
 *
 * @author Victor Roldan Betancort
 * @since 2.0
 */
public interface IOptionsEvent extends IEvent
{
  @Override
  public IOptions getSource();
}
