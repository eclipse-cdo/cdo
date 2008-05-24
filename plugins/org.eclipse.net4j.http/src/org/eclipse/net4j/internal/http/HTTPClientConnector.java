/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.http;

import org.eclipse.net4j.connector.ConnectorLocation;
import org.eclipse.net4j.http.IHTTPConnector;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class HTTPClientConnector extends HTTPConnector implements IHTTPConnector
{
  private String url;

  public HTTPClientConnector()
  {
  }

  public ConnectorLocation getLocation()
  {
    return ConnectorLocation.CLIENT;
  }

  public String getURL()
  {
    return url;
  }

  public void setURL(String url)
  {
    this.url = url;
  }

  @Override
  public String toString()
  {
    if (getUserID() == null)
    {
      return MessageFormat.format("HTTPClientConnector[{0}]", getURL()); //$NON-NLS-1$
    }

    return MessageFormat.format("HTTPClientConnector[{1}@{0}]", getURL(), getUserID()); //$NON-NLS-1$
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkArg(url, "url == null");
  }
}
