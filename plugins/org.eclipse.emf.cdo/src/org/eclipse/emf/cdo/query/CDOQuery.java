/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.query;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.query.CDOQueryInfo;

import org.eclipse.net4j.util.collection.CloseableIterator;

import java.util.List;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOQuery extends CDOQueryInfo
{
  public <T> CloseableIterator<T> getResultAsync(Class<T> classObject);

  public <T> List<T> getResult(Class<T> classObject);

  /**
   * Sets the maximum number of results to retrieve.
   * 
   * @param maxResult
   *          the maximum number of results to retrieve
   * @return the same query instance
   * @throws IllegalArgumentException
   *           if argument is negative
   */
  public CDOQuery setMaxResults(int maxResult);

  /**
   * Binds an argument value to a named parameter.
   * 
   * @param name
   *          the parameter name
   * @param value
   *          the value to bind
   * @return the same query instance
   * @throws IllegalArgumentException
   *           if the parameter name does not correspond to a parameter in the query string or if the argument value is
   *           of incorrect type
   */
  public CDOQuery setParameter(String name, Object value);

  public CDOView getView();
}
