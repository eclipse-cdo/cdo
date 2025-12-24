/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.collection;

import org.eclipse.net4j.util.om.pref.OMPreference;

/**
 * @author Eike Stepper
 */
public final class HistoryUtil
{
  private HistoryUtil()
  {
  }

  public static IHistory<String> createHistory()
  {
    return new History<>();
  }

  public static IHistory<String> createPreferenceHistory(OMPreference<String[]> preference)
  {
    return new PreferenceHistory(preference);
  }
}
