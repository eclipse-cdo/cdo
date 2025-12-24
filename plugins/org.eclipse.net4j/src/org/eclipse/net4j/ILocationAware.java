/*
 * Copyright (c) 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j;

/**
 * A concept that has a {@link Location location} in a {@link Location#CLIENT client}/{@link Location#SERVER server}
 * scenario.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public interface ILocationAware
{
  /**
   * Returns the location of this object in a {@link Location#CLIENT client}/{@link Location#SERVER server} scenario.
   */
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
   * A {@link Location location} in a {@link Location#CLIENT client}/{@link Location#SERVER server} scenario.
   *
   * @author Eike Stepper
   * @since 2.0
   */
  public enum Location
  {
    CLIENT, SERVER
  }
}
