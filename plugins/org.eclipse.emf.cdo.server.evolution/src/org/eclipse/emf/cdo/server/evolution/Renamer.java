/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.evolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class Renamer implements Runnable
{
  private final Map<String, String> oldNames = new HashMap<String, String>();

  private final Map<String, String> newNames = new HashMap<String, String>();

  public Renamer()
  {
  }

  public Map<String, String> getOldNames()
  {
    return oldNames;
  }

  public Map<String, String> getNewNames()
  {
    return newNames;
  }

  public void addNames(String oldName, String newName)
  {
    if (oldName != null)
    {
      oldNames.put(oldName, newName);
    }

    if (newName != null)
    {
      newNames.put(newName, oldName);
    }
  }

  public void run()
  {
    List<String> colliding = new ArrayList<String>();
    Set<String> newNames = new HashSet<String>();

    for (Map.Entry<String, String> entry : oldNames.entrySet())
    {
      String oldName = entry.getKey();
      String newName = entry.getValue();

      if (newName != null && oldNames.containsKey(newName))
      {
        if (oldNames.get(newName) == null)
        {
          throw new RuntimeException("Rename from " + oldName + " to " + newName + " is not possible");
        }

        colliding.add(oldName);
      }
      else
      {
        if (newName != null)
        {
          doRename(oldName, newName);
        }

        newNames.add(newName);
      }
    }

    Map<String, String> tempNames = new LinkedHashMap<String, String>();
    int tempCounter = 0;

    while (!colliding.isEmpty())
    {
      String oldName = colliding.remove(0);
      String newName = oldNames.get(oldName);

      if (newNames.contains(newName) || colliding.contains(newName))
      {
        String tempName = createTempName(++tempCounter);
        tempNames.put(tempName, newName);

        doRename(oldName, tempName);
      }
      else
      {
        doRename(oldName, newName);
      }
    }

    for (Map.Entry<String, String> entry : tempNames.entrySet())
    {
      String tempName = entry.getKey();
      String newName = entry.getValue();
      doRename(tempName, newName);
    }
  }

  protected abstract void doRename(String oldName, String newName);

  protected String createTempName(int id)
  {
    return getTempPrefix() + id;
  }

  protected String getTempPrefix()
  {
    return "CDO_TMP_";
  }

  // public static void main(String[] args)
  // {
  // Renamer renamer = new Renamer()
  // {
  // @Override
  // protected void doRename(String oldName, String newName)
  // {
  // System.out.println(oldName + " --> " + newName);
  // }
  // };
  //
  // renamer.addNames("C", "X");
  // renamer.addNames("A", "B");
  // renamer.addNames("B", "A");
  // renamer.addNames("X", "M");
  // renamer.run();
  // }
}
