/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

/**
 * @author Eike Stepper
 */
public interface IMappingConstants
{
  /*
   * Field names of attribute tables
   */

  public static final String ATTRIBUTES_ID = "cdo_id"; //$NON-NLS-1$

  public static final String ATTRIBUTES_BRANCH = "cdo_branch"; //$NON-NLS-1$

  public static final String ATTRIBUTES_VERSION = "cdo_version"; //$NON-NLS-1$

  public static final String ATTRIBUTES_CLASS = "cdo_class"; //$NON-NLS-1$

  public static final String ATTRIBUTES_CREATED = "cdo_created"; //$NON-NLS-1$

  public static final String ATTRIBUTES_REVISED = "cdo_revised"; //$NON-NLS-1$

  public static final String ATTRIBUTES_RESOURCE = "cdo_resource"; //$NON-NLS-1$

  public static final String ATTRIBUTES_CONTAINER = "cdo_container"; //$NON-NLS-1$

  public static final String ATTRIBUTES_FEATURE = "cdo_feature"; //$NON-NLS-1$

  /*
   * Field names of list tables
   */

  public static final String LIST_FEATURE = "cdo_feature"; //$NON-NLS-1$

  public static final String LIST_REVISION_ID = "cdo_source"; //$NON-NLS-1$

  public static final String LIST_REVISION_VERSION = "cdo_version"; //$NON-NLS-1$

  public static final String LIST_REVISION_VERSION_ADDED = "cdo_version_added"; //$NON-NLS-1$

  public static final String LIST_REVISION_VERSION_REMOVED = "cdo_version_removed"; //$NON-NLS-1$

  public static final String LIST_REVISION_BRANCH = "cdo_branch"; //$NON-NLS-1$

  public static final String LIST_IDX = "cdo_idx"; //$NON-NLS-1$

  public static final String LIST_VALUE = "cdo_value"; //$NON-NLS-1$

  /*
   * Field names of featuremap tables
   */

  public static final String FEATUREMAP_REVISION_ID = LIST_REVISION_ID;

  public static final String FEATUREMAP_VERSION = LIST_REVISION_VERSION;

  public static final String FEATUREMAP_VERSION_ADDED = LIST_REVISION_VERSION_ADDED;

  public static final String FEATUREMAP_VERSION_REMOVED = LIST_REVISION_VERSION_REMOVED;

  public static final String FEATUREMAP_BRANCH = LIST_REVISION_BRANCH;

  public static final String FEATUREMAP_IDX = LIST_IDX;

  public static final String FEATUREMAP_TAG = LIST_FEATURE;

  public static final String FEATUREMAP_VALUE = LIST_VALUE;
}
