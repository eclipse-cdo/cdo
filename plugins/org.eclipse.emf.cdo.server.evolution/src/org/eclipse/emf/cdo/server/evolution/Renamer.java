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

  private boolean initialized;

  private int counter;

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
    if (!initialized)
    {
      initialized = true;
      initNames();
    }

    if (oldName != null)
    {
      oldNames.put(oldName, newName);
    }

    if (newName != null)
    {
      newNames.put(newName, oldName);
    }
  }

  public String addName(String oldName)
  {
    String newName = createUniqueName(++counter);
    addNames(oldName, newName);
    return newName;
  }

  public void run()
  {
    List<String> colliding = new ArrayList<String>();
    Set<String> names = new HashSet<String>();

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

        names.add(newName);
      }
    }

    Map<String, String> tempNames = new LinkedHashMap<String, String>();
    int tmpCounter = 0;

    while (!colliding.isEmpty())
    {
      String oldName = colliding.remove(0);
      String newName = oldNames.get(oldName);

      if (names.contains(newName) || colliding.contains(newName))
      {
        String tempName = createTmpName(++tmpCounter);
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

    oldNames.clear();
    newNames.clear();
    initialized = false;
  }

  protected void initNames()
  {
  }

  protected abstract void doRename(String oldName, String newName);

  protected String createTmpName(int id)
  {
    return getTmpPrefix() + id;
  }

  protected String getTmpPrefix()
  {
    return "CDO_TMP_";
  }

  protected String createUniqueName(int id)
  {
    return getUniquePrefix() + id;
  }

  protected String getUniquePrefix()
  {
    return "CDO_OLD_";
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
