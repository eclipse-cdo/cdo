/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.eclipse;


/**
 * The main plugin class to be used in the desktop.
 */
public class ExamplePlugin extends AbstractPlugin
{
  //The shared instance.
  private static ExamplePlugin plugin;

  /**
   * The constructor.
   */
  public ExamplePlugin()
  {
    if (plugin == null) plugin = this;
  }

  protected void doStart() throws Exception
  {
  }

  public void doStop() throws Exception
  {
    plugin = null;
  }

  /**
   * Returns the shared instance.
   */
  public static ExamplePlugin getDefault()
  {
    return plugin;
  }
}
