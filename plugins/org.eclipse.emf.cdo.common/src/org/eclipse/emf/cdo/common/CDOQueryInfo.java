/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.common;

import java.util.Map;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOQueryInfo
{
  public static final int UNLIMITED_RESULTS = -1;

  public String getQueryLanguage();

  public String getQueryString();

  public Map<String, Object> getParameters();

  /**
   * Returns the maximum number of results to retrieve or {@link #UNLIMITED_RESULTS} for no limitation.
   */
  public int getMaxResults();
}
