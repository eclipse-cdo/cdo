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
