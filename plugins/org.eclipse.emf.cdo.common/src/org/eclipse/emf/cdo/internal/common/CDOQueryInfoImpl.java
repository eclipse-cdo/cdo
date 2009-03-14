/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.common;

import org.eclipse.emf.cdo.common.CDOQueryInfo;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Simon McDuff
 */
public class CDOQueryInfoImpl implements CDOQueryInfo
{
  protected String queryLanguage;

  protected String queryString;

  protected Map<String, Object> parameters = new HashMap<String, Object>();

  protected int maxResults = UNLIMITED_RESULTS;

  public CDOQueryInfoImpl(String queryLanguage, String queryString)
  {
    this.queryLanguage = queryLanguage;
    this.queryString = queryString;
  }

  public CDOQueryInfoImpl(CDODataInput in) throws IOException
  {
    queryLanguage = in.readString();
    queryString = in.readString();
    maxResults = in.readInt();

    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      String key = in.readString();
      Object object = in.readCDORevisionOrPrimitiveOrClassifier();
      parameters.put(key, object);
    }
  }

  public void write(CDODataOutput out) throws IOException
  {
    out.writeString(queryLanguage);
    out.writeString(queryString);
    out.writeInt(maxResults);

    out.writeInt(parameters.size());
    for (Entry<String, Object> entry : parameters.entrySet())
    {
      out.writeString(entry.getKey());
      out.writeCDORevisionOrPrimitiveOrClassifier(entry.getValue());
    }
  }

  public String getQueryString()
  {
    return queryString;
  }

  public String getQueryLanguage()
  {
    return queryLanguage;
  }

  public Map<String, Object> getParameters()
  {
    return Collections.unmodifiableMap(parameters);
  }

  public void addParameter(String key, Object value)
  {
    parameters.put(key, value);
  }

  public int getMaxResults()
  {
    return maxResults;
  }

  public CDOQueryInfoImpl setMaxResults(int maxResults)
  {
    this.maxResults = maxResults;
    return this;
  }
}
