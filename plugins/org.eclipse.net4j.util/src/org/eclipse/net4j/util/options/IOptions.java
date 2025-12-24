/*
 * Copyright (c) 2008, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.event.INotifier;

/**
 * Encapsulates a set of notifying configuration options.
 *
 * @since 2.0
 * @author Victor Roldan Betancort
 * @see IOptionsContainer
 */
public interface IOptions extends INotifier
{
  /**
   * Returns the container of this options object.
   */
  public IOptionsContainer getContainer();
}
