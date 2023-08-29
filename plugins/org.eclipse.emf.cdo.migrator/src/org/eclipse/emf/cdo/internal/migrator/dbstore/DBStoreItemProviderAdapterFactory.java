/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.migrator.dbstore;

import org.eclipse.emf.ecore.provider.annotation.EAnnotationItemProviderAdapterFactory;

/**
 * @author Eike Stepper
 */
public class DBStoreItemProviderAdapterFactory extends EAnnotationItemProviderAdapterFactory.Reflective
{
  public DBStoreItemProviderAdapterFactory()
  {
    super(DBStoreAnnotationValidator.ANNOTATION_URI);
  }
}
