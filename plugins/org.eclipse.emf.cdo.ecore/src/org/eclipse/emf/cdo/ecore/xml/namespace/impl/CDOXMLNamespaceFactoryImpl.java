/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
