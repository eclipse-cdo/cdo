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
package org.eclipse.net4j.util;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import java.io.PrintStream;


public final class PropertiesHelper
{
  private PropertiesHelper()
  {
  }

  public static void dumpProperties()
  {
    dumpProperties(System.out);
  }

  public static void dumpProperties(PrintStream out)
  {
    List<String> props = getSortedProperties();
    for (String prop : props)
    {
      out.println(prop);
    }
  }

  public static List<String> getSortedProperties()
  {
    List<String> result = new ArrayList<String>();
    Properties properties = System.getProperties();
    for (Iterator it = properties.entrySet().iterator(); it.hasNext();)
    {
      Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
      result.add(entry.getKey() + " = " + entry.getValue());
    }

    Collections.sort(result);
    return result;
  }
}
