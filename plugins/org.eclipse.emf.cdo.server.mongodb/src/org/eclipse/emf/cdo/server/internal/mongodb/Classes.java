/*
 * Copyright (c) 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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

  private Map<EClassifier, Integer> classifierToIDs = new HashMap<>();

  private Map<Integer, EClassifier> idToClassifiers = new HashMap<>();

  private MongoDBStore store;

  private boolean initialized;

  private int lastClassifierID;

  private int resourceNodeClassID;

  private int resourceFolderClassID;

  private int resourceClassID;

  public Classes(MongoDBStore store)
  {
    this.store = store;
  }

  public MongoDBStore getStore()
  {
    return store;
  }

  public int getLastClassifierID()
  {
    return lastClassifierID;
  }

  public void setLastClassifierID(int lastClassifierID)
  {
    this.lastClassifierID = lastClassifierID;
  }

  public synchronized int getResourceNodeClassID()
  {
    initialize();
    return resourceNodeClassID;
  }

  public synchronized int getResourceFolderClassID()
  {
    initialize();
    return resourceFolderClassID;
  }

  public synchronized int getResourceClassID()
  {
    initialize();
    return resourceClassID;
  }

  public synchronized int mapNewClassifier(EClassifier classifier)
  {
    int id = ++lastClassifierID;
    mapClassifier(classifier, id);
    return id;
  }

  public synchronized void mapClassifier(EClassifier classifier, int id)
  {
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
  }

  public synchronized int getClassifierID(EClassifier classifier)
  {
    initialize();
    return classifierToIDs.get(classifier);
  }

  public synchronized EClassifier getClassifier(int id)
  {
    initialize();
    return idToClassifiers.get(id);
  }

  public synchronized EClass getClass(int id)
  {
    return (EClass)getClassifier(id);
  }

  private void initialize()
  {
    if (!initialized)
    {
      Commits commits = store.getCommits();
      commits.initializeClassifiers();
      initialized = true;
    }
  }
}
