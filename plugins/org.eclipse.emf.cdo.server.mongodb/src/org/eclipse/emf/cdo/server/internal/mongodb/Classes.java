/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.mongodb;

import org.eclipse.emf.cdo.eresource.EresourcePackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class Classes
{
  public static final String PROPERTIES = "props";

  private Map<EClassifier, Integer> classifierToIDs = new HashMap<EClassifier, Integer>();

  private Map<Integer, EClassifier> idToClassifiers = new HashMap<Integer, EClassifier>();

  private int resourceNodeClassID;

  private int resourceFolderClassID;

  private int resourceClassID;

  private int lastClassifierID;

  public Classes()
  {
  }

  public int getResourceNodeClassID()
  {
    return resourceNodeClassID;
  }

  public int getResourceFolderClassID()
  {
    return resourceFolderClassID;
  }

  public int getResourceClassID()
  {
    return resourceClassID;
  }

  public int getLastClassifierID()
  {
    return lastClassifierID;
  }

  public void setLastClassifierID(int lastClassifierID)
  {
    this.lastClassifierID = lastClassifierID;
  }

  public synchronized int mapNewClassifier(EClassifier classifier)
  {
    int id = ++lastClassifierID;
    classifierToIDs.put(classifier, id);
    idToClassifiers.put(id, classifier);

    if (classifier == EresourcePackage.eINSTANCE.getCDOResourceNode())
    {
      resourceNodeClassID = id;
    }
    else if (classifier == EresourcePackage.eINSTANCE.getCDOResourceFolder())
    {
      resourceFolderClassID = id;
    }
    else if (classifier == EresourcePackage.eINSTANCE.getCDOResource())
    {
      resourceClassID = id;
    }

    return id;
  }

  public synchronized int getClassifierID(EClassifier classifier)
  {
    return classifierToIDs.get(classifier);
  }

  public synchronized EClassifier getClassifier(int id)
  {
    return idToClassifiers.get(id);
  }

  public synchronized EClass getClass(int id)
  {
    return (EClass)idToClassifiers.get(id);
  }
}
