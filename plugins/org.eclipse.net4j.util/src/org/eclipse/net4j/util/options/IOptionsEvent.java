/*
 * Copyright (c) 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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
