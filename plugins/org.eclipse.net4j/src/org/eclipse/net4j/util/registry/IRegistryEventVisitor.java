/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.registry;

/**
 * @author Eike Stepper
 */
public interface IRegistryEventVisitor<K, V>
{
  public void registered(K id, V element);

  public void deregistered(K id, V element);

  /**
   * @author Eike Stepper
   */
  public interface Filtered<K, V> extends IRegistryEventVisitor<K, V>
  {
    public boolean filter(K id, V element);
  }
}
