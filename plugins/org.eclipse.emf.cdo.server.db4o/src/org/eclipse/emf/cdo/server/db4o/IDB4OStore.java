/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */

package org.eclipse.emf.cdo.server.db4o;

import org.eclipse.emf.cdo.server.IStore;

/**
 * @author Victor Roldan Betancort
 */
public interface IDB4OStore extends IStore
{
  public static final String TYPE = "db4o";

  public String getStoreLocation();

  public int getPort();
}
