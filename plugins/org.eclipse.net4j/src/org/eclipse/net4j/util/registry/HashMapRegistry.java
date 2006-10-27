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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class HashMapRegistry<ID, E extends IRegistryElement<ID>> extends
    AbstractMappingRegistry<ID, E>
{
  private Map<ID, E> map;

  public HashMapRegistry()
  {
    this(DEFAULT_RESOLVING);
  }

  public HashMapRegistry(boolean resolving)
  {
    super(resolving);
    this.map = createMap();
  }

  @Override
  protected Map<ID, E> getMap()
  {
    return map;
  }

  protected Map<ID, E> createMap()
  {
    return new HashMap(0);
  }
}
