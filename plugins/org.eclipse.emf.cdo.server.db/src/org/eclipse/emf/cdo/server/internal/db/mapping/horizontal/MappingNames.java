/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.net4j.db.DBUtil;

/**
 * @author Eike Stepper
 */
public final class MappingNames
{
  public static final String CDO_OBJECTS = name("cdo_objects"); //$NON-NLS-1$

  /*
   * Field names of attribute tables
   */

  public static final String ATTRIBUTES_ID = name("cdo_id"); //$NON-NLS-1$

  public static final String ATTRIBUTES_BRANCH = name("cdo_branch"); //$NON-NLS-1$

  public static final String ATTRIBUTES_VERSION = name("cdo_version"); //$NON-NLS-1$

  public static final String ATTRIBUTES_CLASS = name("cdo_class"); //$NON-NLS-1$

  public static final String ATTRIBUTES_CREATED = name("cdo_created"); //$NON-NLS-1$

  public static final String ATTRIBUTES_REVISED = name("cdo_revised"); //$NON-NLS-1$

  public static final String ATTRIBUTES_RESOURCE = name("cdo_resource"); //$NON-NLS-1$

  public static final String ATTRIBUTES_CONTAINER = name("cdo_container"); //$NON-NLS-1$

  public static final String ATTRIBUTES_FEATURE = name("cdo_feature"); //$NON-NLS-1$

  /*
   * Field names of list tables
   */

  public static final String LIST_FEATURE = name("cdo_feature"); //$NON-NLS-1$

  public static final String LIST_REVISION_ID = name("cdo_source"); //$NON-NLS-1$

  public static final String LIST_REVISION_BRANCH = ATTRIBUTES_BRANCH;

  public static final String LIST_REVISION_VERSION = ATTRIBUTES_VERSION;

  public static final String LIST_REVISION_VERSION_ADDED = name("cdo_version_added"); //$NON-NLS-1$

  public static final String LIST_REVISION_VERSION_REMOVED = name("cdo_version_removed"); //$NON-NLS-1$

  public static final String LIST_IDX = name("cdo_idx"); //$NON-NLS-1$

  public static final String LIST_VALUE = name("cdo_value"); //$NON-NLS-1$

  private static String name(String name)
  {
    return DBUtil.name(name, MappingNames.class);
  }
}
