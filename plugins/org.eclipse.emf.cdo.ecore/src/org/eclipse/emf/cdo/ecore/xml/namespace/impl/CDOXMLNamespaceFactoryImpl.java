/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Sebastien REVOL (CEA LIST) sebastien.revol@cea.fr - Initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.xml.namespace.impl;

import org.eclipse.emf.ecore.xml.namespace.XMLNamespaceDocumentRoot;

/**
 * @author Sebastien Revol
 */
public class CDOXMLNamespaceFactoryImpl extends org.eclipse.emf.ecore.xml.namespace.impl.XMLNamespaceFactoryImpl
{
  public CDOXMLNamespaceFactoryImpl()
  {
  }

  @Override
  public XMLNamespaceDocumentRoot createXMLNamespaceDocumentRoot()
  {
    return new XMLNamespaceDocumentRootImpl();
  }
}
