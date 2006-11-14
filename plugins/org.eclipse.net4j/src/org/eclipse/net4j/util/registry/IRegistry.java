/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.registry;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public interface IRegistry<K, V> extends Map<K, V>
{
  public void addRegistryListener(IRegistryListener<K, V> listener);

  public void removeRegistryListener(IRegistryListener<K, V> listener);

  public boolean isAutoCommit();

  public void setAutoCommit(boolean on);

  public void commit(boolean notifications);

  public void commit();

  public void dispose();
}
