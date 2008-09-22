/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.net4j.util.collection.MoveableList;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOList extends MoveableList<Object>
{
  /**
   * Returns the element at position index of this list and optionally resolves {@link CDOReferenceProxy proxies}.
   * <p>
   * 
   * @param index
   *          The position of the element to return from this list.
   * @param resolve
   *          A value of <code>false</code> indicates that {@link CDORevisionUtil#UNINITIALIZED} may be returned for
   *          unresolved elements. A value of <code>true</code> indicates that it should behave identical to
   *          {@link CDOList#get(int)}.
   */
  public Object get(int index, boolean resolve);
}
