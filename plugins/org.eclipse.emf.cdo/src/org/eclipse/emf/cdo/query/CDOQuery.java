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

import java.util.List;

/**
 * @author Simon McDuff
 */
public interface CDOQuery
{
  
  public <T> CDOQueryResult<T> getResultIterator(Class<T> classObject) throws Exception;

  
  public <T> List<T> getResultList(Class<T> classObject) throws Exception;
  
  /**
   * Set the maximum number of results to retrieve.
   * @param maxResult
   * @return the same query instance
   * @throws IllegalArgumentException if argument is negative
   */
  public CDOQuery setMaxResults(int maxResult);
  public int getMaxResults();
  
  /**
   * Bind an argument to a named parameter.
   * @param name the parameter name
   * @param value
   * @return the same query instance
   * @throws IllegalArgumentException if parameter name does not
   *    correspond to parameter in query string
   *    or argument is of incorrect type
   */
  public CDOQuery setParameter(String name, Object value);
  
 
}
