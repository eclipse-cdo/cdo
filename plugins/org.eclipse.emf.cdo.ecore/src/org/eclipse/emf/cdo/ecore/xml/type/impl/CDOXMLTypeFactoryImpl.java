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
package org.eclipse.emf.cdo.ecore.xml.type.impl;

import org.eclipse.emf.ecore.xml.type.XMLTypeDocumentRoot;

/**
 * @author Sebastien Revol
 */
public class CDOXMLTypeFactoryImpl extends org.eclipse.emf.ecore.xml.type.impl.XMLTypeFactoryImpl
{
  public CDOXMLTypeFactoryImpl()
  {
  }

  @Override
  public XMLTypeDocumentRoot createXMLTypeDocumentRoot()
  {
    return new XMLTypeDocumentRootImpl();
  }
}
