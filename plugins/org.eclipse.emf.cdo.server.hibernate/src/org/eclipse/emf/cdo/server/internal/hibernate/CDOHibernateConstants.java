/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

/**
 * Maintains constants used in the CDO-Hibernate integration.
 * 
 * @author Martin Taal
 */
public class CDOHibernateConstants
{
  public static final String ID_PROPERTY = "id"; //$NON-NLS-1$

  public static final String RESOURCE_PROPERTY = "resourceID"; //$NON-NLS-1$

  public static final String RESOURCE_PROPERTY_COLUMN = "resource_id"; //$NON-NLS-1$

  public static final String CONTAINER_PROPERTY = "containerID"; //$NON-NLS-1$

  public static final String CONTAINER_PROPERTY_COLUMN = "container_id"; //$NON-NLS-1$

  public static final String FEATUREMAP_PROPERTY_FEATURE = "fme_feature"; //$NON-NLS-1$

  public static final String FEATUREMAP_PROPERTY_CDATA = "fme_mixed_cdata"; //$NON-NLS-1$

  public static final String FEATUREMAP_PROPERTY_COMMENT = "fme_mixed_comment"; //$NON-NLS-1$

  public static final String FEATUREMAP_PROPERTY_TEXT = "fme_mixed_text"; //$NON-NLS-1$

  public static final String FEATUREMAP_PROPERTY_ANY_PRIMITIVE = "fme_any_data"; //$NON-NLS-1$

  public static final String FEATUREMAP_PROPERTY_ANY_REFERENCE = "fme_any_reference"; //$NON-NLS-1$

  public static final String PROPERTY_SEPARATOR = "_"; //$NON-NLS-1$

  public static final String NL = "\n"; //$NON-NLS-1$

  public static final String UTF8 = "UTF-8"; //$NON-NLS-1$
}
