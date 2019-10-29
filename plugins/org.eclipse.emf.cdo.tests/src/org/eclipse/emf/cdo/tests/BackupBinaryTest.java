/*
 * Copyright (c) 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.server.CDOServerExporter;
import org.eclipse.emf.cdo.server.CDOServerImporter;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

/**
 * @author Eike Stepper
 */
public class BackupBinaryTest extends BackupTest
{
  @Override
  protected CDOServerExporter<?> createExporter(InternalRepository repo1)
  {
    return new CDOServerExporter.Binary(repo1);
  }

  @Override
  protected CDOServerImporter createImporter(InternalRepository repo2)
  {
    return new CDOServerImporter.Binary(repo2);
  }
}
