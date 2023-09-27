/*
 * Copyright (c) 2013, 2020, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;

/**
 * @author Eike Stepper
 */
public interface IMappingConstants
{
  /*
   * Field names of attribute tables
   */

  public static final String ATTRIBUTES_ID = CDODBSchema.name("cdo_id"); //$NON-NLS-1$

  public static final String ATTRIBUTES_BRANCH = CDODBSchema.name("cdo_branch"); //$NON-NLS-1$

  public static final String ATTRIBUTES_VERSION = CDODBSchema.name("cdo_version"); //$NON-NLS-1$

  public static final String ATTRIBUTES_CLASS = CDODBSchema.name("cdo_class"); //$NON-NLS-1$

  public static final String ATTRIBUTES_CREATED = CDODBSchema.name("cdo_created"); //$NON-NLS-1$

  public static final String ATTRIBUTES_REVISED = CDODBSchema.name("cdo_revised"); //$NON-NLS-1$

  public static final String ATTRIBUTES_RESOURCE = CDODBSchema.name("cdo_resource"); //$NON-NLS-1$

  public static final String ATTRIBUTES_CONTAINER = CDODBSchema.name("cdo_container"); //$NON-NLS-1$

  public static final String ATTRIBUTES_FEATURE = CDODBSchema.name("cdo_feature"); //$NON-NLS-1$

  /*
   * Field names of list tables
   */

  public static final String LIST_FEATURE = CDODBSchema.name("cdo_feature"); //$NON-NLS-1$

  public static final String LIST_REVISION_ID = CDODBSchema.name("cdo_source"); //$NON-NLS-1$

  public static final String LIST_REVISION_VERSION = ATTRIBUTES_VERSION;

  public static final String LIST_REVISION_VERSION_ADDED = CDODBSchema.name("cdo_version_added"); //$NON-NLS-1$

  public static final String LIST_REVISION_VERSION_REMOVED = CDODBSchema.name("cdo_version_removed"); //$NON-NLS-1$

  public static final String LIST_REVISION_BRANCH = ATTRIBUTES_BRANCH;

  public static final String LIST_IDX = CDODBSchema.name("cdo_idx"); //$NON-NLS-1$

  public static final String LIST_VALUE = CDODBSchema.name("cdo_value"); //$NON-NLS-1$
}
