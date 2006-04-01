/*******************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Sympedia Methods and Tools.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *******************************************************************************/
package org.eclipse.emf.cdo.server;


import org.eclipse.net4j.util.eclipse.AbstractPlugin;
import org.eclipse.net4j.util.eclipse.Element;
import org.eclipse.net4j.util.eclipse.ListExtensionParser;

import java.util.List;


/**
 * The main plugin class to be used in the desktop.
 */
public class CdoServerPlugin extends AbstractPlugin
{
  //The shared instance.
  private static CdoServerPlugin plugin;

  /**
   * The constructor.
   */
  public CdoServerPlugin()
  {
    if (plugin == null) plugin = this;
  }

  protected void doStart() throws Exception
  {
  }

  protected void doStop() throws Exception
  {
    plugin = null;
  }

  /**
   * Returns the shared instance.
   */
  public static CdoServerPlugin getDefault()
  {
    return plugin;
  }


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
