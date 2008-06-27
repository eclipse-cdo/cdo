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
package org.eclipse.emf.cdo.internal.common.query;

import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.query.CDOQueryParameter;
import org.eclipse.emf.cdo.common.util.CDOInstanceUtil;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
/**
 * @author Simon McDuff
 */
public class CDOQueryParameterImpl implements CDOQueryParameter
{
	private String queryLanguage;
	
	private String queryString;
	
	private Map<String, Object> parameters = new HashMap<String, Object>();
	
	private int maxResult = -1;
	
	public CDOQueryParameterImpl(String language, String queryString)
	{
		this.queryLanguage = language;
		this.queryString = queryString;
	}
	
	public CDOQueryParameterImpl(ExtendedDataInput in, CDOIDObjectFactory objectFactory, CDOPackageManager packageManager) throws IOException
	{
		queryLanguage = in.readString();
		queryString = in.readString();
		maxResult = in.readInt();
		
		int size = in.readInt();
		for (int i =0; i< size; i++)
		{
		  String key = in.readString();
		  Object object = CDOInstanceUtil.readObject(in, objectFactory, packageManager);
		  parameters.put(key, object);
		}
	}
	
	public void write(ExtendedDataOutput out) throws IOException
	{
	   out.writeString(queryLanguage);
	   out.writeString(queryString);
	   out.writeInt(maxResult);
	   
	   out.writeInt(parameters.size());
	   for (Entry<String, Object> entry : parameters.entrySet())
	   {
		   out.writeString(entry.getKey());
		   Object value = entry.getValue();
		   CDOInstanceUtil.writeObject(out, value);
	   }
	}

	

	public String getQueryString()
	{
		return queryString;
	}
	
	public String getQueryLanguage()
	{
		return this.queryLanguage;
	}
	
	public Map<String, Object> getParameters()
	{
	  return parameters;
	}

  public int getMaxResult()
  {
    return maxResult;
  }

  public void setMaxResult(int maxResult)
  {
    this.maxResult = maxResult;
  }
}
