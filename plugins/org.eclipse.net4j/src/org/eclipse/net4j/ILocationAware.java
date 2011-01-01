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
package org.eclipse.net4j;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface ILocationAware
{
  public Location getLocation();

  /**
   * Same as <code>{@link #getLocation()} == {@link Location#CLIENT}</code>.
   */
  public boolean isClient();

  /**
   * Same as <code>{@link #getLocation()} == {@link Location#SERVER}</code>.
   */
  public boolean isServer();

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public enum Location
  {
    CLIENT, SERVER
  }
}
