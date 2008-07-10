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
package org.eclipse.emf.cdo.common.query;

import org.eclipse.net4j.util.collection.CloseableIterator;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public interface ResultReaderQueue<T> extends CloseableIterator<T>
{
  /**
   * Returns <tt>true</tt> if this task was cancelled before it completed normally.
   * 
   * @return <tt>true</tt> if this task was cancelled before it completed
   */
  boolean isCancelled();

  /**
   * Returns <tt>true</tt> if this task completed. Completion may be due to normal termination, an exception, or
   * cancellation -- in all of these cases, this method will return <tt>true</tt>.
   * 
   * @return <tt>true</tt> if this task completed
   */
  boolean isDone();
}
