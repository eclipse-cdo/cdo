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

import java.util.Map;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOQueryInfo
{
  String getQueryLanguage();

  String getQueryString();

  Map<String, Object> getParameters();

  /**
   * Get the maximum number of results to retrieve.
   * 
   * @param maxResult
   * @return the same query instance
   * @throws IllegalArgumentException
   *           if argument is negative
   */
  int getMaxResults();
}
