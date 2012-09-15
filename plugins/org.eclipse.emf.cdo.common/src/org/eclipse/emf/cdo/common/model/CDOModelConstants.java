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
package org.eclipse.emf.cdo.common.model;

/**
 * Symbolic model constants commonly used in CDO.
 *
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOModelConstants
{
  /**
   * @since 4.2
   */
  public static final String RESOURCE_FOLDER_NODES_REFERENCE = "nodes".intern(); //$NON-NLS-1$

  /**
   * @since 4.2
   */
  public static final String RESOURCE_NODE_FOLDER_REFERENCE = "folder".intern(); //$NON-NLS-1$

  public static final String RESOURCE_NODE_NAME_ATTRIBUTE = "name".intern(); //$NON-NLS-1$

  /**
   * @since 4.0
   */
  public static final String CORE_PACKAGE_URI = "http://www.eclipse.org/emf/2002/Ecore".intern(); //$NON-NLS-1$

  /**
   * @since 4.0
   */
  public static final String ROOT_CLASS_NAME = "EObject".intern(); //$NON-NLS-1$

  /**
   * @since 4.0
   */
  public static final String RESOURCE_PACKAGE_URI = "http://www.eclipse.org/emf/CDO/Eresource/4.0.0".intern(); //$NON-NLS-1$

  /**
   * @since 4.0
   */
  public static final String RESOURCE_NODE_CLASS_NAME = "CDOResourceNode".intern(); //$NON-NLS-1$

  /**
   * @since 4.0
   */
  public static final String RESOURCE_FOLDER_CLASS_NAME = "CDOResourceFolder".intern(); //$NON-NLS-1$

  /**
   * @since 4.0
   */
  public static final String RESOURCE_CLASS_NAME = "CDOResource".intern(); //$NON-NLS-1$

  /**
   * @since 4.2
   */
  public static final String RESOURCE_LEAF_CLASS_NAME = "CDOResourceLeaf".intern(); //$NON-NLS-1$

  /**
   * @since 4.2
   */
  public static final String RESOURCE_FILE_CLASS_NAME = "CDOFileResource".intern(); //$NON-NLS-1$

  /**
   * @since 4.2
   */
  public static final String RESOURCE_BINARY_FILE_CLASS_NAME = "CDOBinaryResource".intern(); //$NON-NLS-1$

  /**
   * @since 4.2
   */
  public static final String RESOURCE_TEXT_FILE_CLASS_NAME = "CDOTextResource".intern(); //$NON-NLS-1$

  /**
   * @since 4.0
   */
  public static final String TYPES_PACKAGE_URI = "http://www.eclipse.org/emf/CDO/Etypes/4.0.0".intern(); //$NON-NLS-1$

  /**
   * @since 4.0
   */
  public static final String BLOB_CLASS_NAME = "Blob".intern(); //$NON-NLS-1$

  /**
   * @since 4.0
   */
  public static final String CLOB_CLASS_NAME = "Clob".intern(); //$NON-NLS-1$
}
