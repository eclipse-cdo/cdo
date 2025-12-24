/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
  @Deprecated
  public static final int NO_CONFLICT = -1;

  @Deprecated
  public static final int REMOTELY_DELTETION_CONFLICT = 0;

  @Deprecated
  public static final int LOCALLY_DELTETION_CONFLICT = 1;

  @Deprecated
  public static final int REMOTELY_AND_LOCALLY_CHANGED_CONFLICT = 2;

  @Deprecated
  void loadLastResource();

  @Deprecated
  void saveLastResource(Resource localResource);

  @Deprecated
  void setGloballyLocked(Set<String> lockedObjects);

  @Deprecated
  Set<String> getGloballyLocked();

  @Deprecated
  void setRemoteLocks(Map<String, Integer> lockedObjects);

  @Deprecated
  void setIgnored(EObject obj);

  @Deprecated
  void setIgnored(String id);

  @Deprecated
  void setLastResource(Resource lastResource);

  @Deprecated
  Resource getLastResource();

  @Deprecated
  void unIgnored(String id);

  @Deprecated
  int getConflictType(String key);

  @Deprecated
  void resolveDeletedLocallyConflict(String xmiId);

  @Deprecated
  void resolveChangedLocalyAndRemotellyConflict(String xmiId);

  @Deprecated
  void resolveDeletedRemotellyConflict(String xmiId);

  // void updateViewWithRemoteView(View obj);
  //
  // void lastResourceDeleteView(View newView);
  //
  // void lastResourceChangeView(View newView);

  @Deprecated
  boolean isConflicted();

  @Deprecated
  void cleanIgnoreList();

  @Deprecated
  void setSelectedElements(Set<EObject> selectedElements);

  @Deprecated
  Set<EObject> getSelectedElements();

  @Deprecated
  void setIgnoreList(Set<String> ignoreList);

  @Deprecated
  Set<String> getIgnoreList();

  @Deprecated
  void setLocallyLocked(Set<String> locallyLocked);

  @Deprecated
  Set<String> getLocallyLocked();

  @Deprecated
  void addObserver(Observer observer);
}
