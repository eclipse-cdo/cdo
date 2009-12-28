/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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
  public static final String RESOURCE_PROPERTY = "resourceID";

  public static final String RESOURCE_PROPERTY_COLUMN = "resource_id";

  public static final String CONTAINER_PROPERTY = "containerID";

  public static final String CONTAINER_PROPERTY_COLUMN = "container_id";

  public static final String FEATUREMAP_PROPERTY_FEATURE = "fme_feature";

  public static final String FEATUREMAP_PROPERTY_CDATA = "fme_mixed_cdata";

  public static final String FEATUREMAP_PROPERTY_COMMENT = "fme_mixed_comment";

  public static final String FEATUREMAP_PROPERTY_TEXT = "fme_mixed_text";

  public static final String FEATUREMAP_PROPERTY_ANY_PRIMITIVE = "fme_any_data";

  public static final String FEATUREMAP_PROPERTY_ANY_REFERENCE = "fme_any_reference";
}
