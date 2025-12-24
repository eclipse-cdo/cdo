/*
 * Copyright (c) 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *************************************************************************/
package org.eclipse.emf.cdo.common.util;

import org.eclipse.net4j.util.collection.CloseableIterator;

/**
 * A closeable iterator that blocks on {@link #hasNext()} until the next element is available or the end of the
 * iteration is reached.
 *
 * @author Simon McDuff
 * @since 2.0
 */
public interface BlockingCloseableIterator<T> extends CloseableIterator<T>
{
  /**
   * Non-blocking call.
   */
  public T peek();
}
