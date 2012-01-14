/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.conflicts;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.Map;
import java.util.Observer;
import java.util.Set;

/**
 * Since Dawn 2.0 this interface is deprecated and will be removed soon. Because it was never really used, there will be
 * no surragote.
 * 
 * @author Martin Fluegge
 */
@Deprecated
public interface ResourceSynchronizer
{
  public static final int NO_CONFLICT = -1;

  public static final int REMOTELY_DELTETION_CONFLICT = 0;

  public static final int LOCALLY_DELTETION_CONFLICT = 1;

  public static final int REMOTELY_AND_LOCALLY_CHANGED_CONFLICT = 2;

  void loadLastResource();

  void saveLastResource(Resource localResource);

  void setGloballyLocked(Set<String> lockedObjects);

  Set<String> getGloballyLocked();

  void setRemoteLocks(Map<String, Integer> lockedObjects);

  void setIgnored(EObject obj);

  void setIgnored(String id);

  void setLastResource(Resource lastResource);

  Resource getLastResource();

  void unIgnored(String id);

  int getConflictType(String key);

  void resolveDeletedLocallyConflict(String xmiId);

  void resolveChangedLocalyAndRemotellyConflict(String xmiId);

  void resolveDeletedRemotellyConflict(String xmiId);

  // void updateViewWithRemoteView(View obj);
  //
  // void lastResourceDeleteView(View newView);
  //
  // void lastResourceChangeView(View newView);

  boolean isConflicted();

  void cleanIgnoreList();

  void setSelectedElements(Set<EObject> selectedElements);

  Set<EObject> getSelectedElements();

  void setIgnoreList(Set<String> ignoreList);

  Set<String> getIgnoreList();

  void setLocallyLocked(Set<String> locallyLocked);

  Set<String> getLocallyLocked();

  void addObserver(Observer observer);
}
