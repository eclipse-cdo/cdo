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
package org.eclipse.emf.cdo.query;

import org.eclipse.emf.cdo.common.query.ResultReaderQueue;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOQueryResult<T> extends ResultReaderQueue<T>
{
  /**
   * It will throw an exception if not running.
   * <p>
   * Use close to terminate the query without throwing an exception
   */
  void cancel();
}
