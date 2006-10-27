/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal;


import org.eclipse.emf.cdo.core.util.extensions.Element;
import org.eclipse.emf.cdo.core.util.extensions.ListExtensionParser;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.List;


/**
 * @author Eike Stepper
 */
public class Activator implements BundleActivator
{
  public Activator()
  {
  }

  public void start(BundleContext context) throws Exception
  {
    CDOServer.BUNDLE.setBundleContext(context);
  }

  public void stop(BundleContext context) throws Exception
  {
    CDOServer.BUNDLE.setBundleContext(null);
  }


  /**
   * @author Eike Stepper
   */
  public class MappingElement extends Element
  {
    protected String uri;

    protected String map;

    public String getMap()
    {
      return map;
    }

    public void setMap(String map)
    {
      this.map = map;
    }

    public String getUri()
    {
      return uri;
    }

    public void setUri(String url)
    {
      this.uri = url;
    }

    public String toString()
    {
      return "Mapping(" + uri + ", " + map + ")";
    }
  }


  /**
   * @author Eike Stepper
   */
  public class MappingExtensionParser extends ListExtensionParser
  {
    public MappingExtensionParser(List list)
    {
      super(list);

      addFactory("mapping", new Element.Factory()
      {
        public Element createElementData()
        {
          return new MappingElement();
        }
      });
    }
  }
}
