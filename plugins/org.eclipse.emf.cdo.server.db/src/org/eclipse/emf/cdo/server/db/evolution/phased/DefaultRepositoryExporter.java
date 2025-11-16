/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db.evolution.phased;

import org.eclipse.emf.cdo.server.CDOServerExporter;
import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.util.factory.AnnotationFactory.InjectAttribute;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Evolves the models with the given mapping strategy, context, and store accessor.
 * <p>
 * This method performs the following steps:
 * <ol>
 * <li>It delegates the model evolution to the mapping strategy, which is responsible for updating the database schema,
 * migrating the existing data, and changing the container feature IDs in case of shifted features.</li>
 * <li>It updates the system tables, including the cdo_package_units table, cdo_package_infos table, and
 * cdo_external_refs table.</li>
 * <li>It commits the transaction unless it's a dry run, in which case it rolls back the changes.</li>
 * </ol>
 *
 * @author Eike Stepper
 * @since 4.14
 * @noreference This package is currently considered <i>provisional</i>.
 * @noimplement This package is currently considered <i>provisional</i>.
 * @noextend This package is currently considered <i>provisional</i>.
 */
public class DefaultRepositoryExporter extends BasicPhaseHandler
{
  /**
   * The factory type of the default repository exporter.
   */
  public static final String FACTORY_TYPE = "default-repository-exporter"; //$NON-NLS-1$

  private boolean binary;

  /**
   * Creates an XML repository exporter.
   */
  public DefaultRepositoryExporter()
  {
    this(false);
  }

  /**
   * Creates a repository exporter that is either binary or XML.
   */
  public DefaultRepositoryExporter(boolean binary)
  {
    setBinary(binary);
  }

  public boolean isBinary()
  {
    return binary;
  }

  @InjectAttribute(name = "binary")
  public void setBinary(boolean binary)
  {
    checkInactive();
    this.binary = binary;
  }

  @Override
  public void execute(Context context) throws Exception
  {
    PhasedModelEvolutionSupport support = context.getSupport();
    IRepository repository = support.getStore().getRepository();

    CDOServerExporter<?> exporter;
    String fileName;

    if (binary)
    {
      exporter = new CDOServerExporter.Binary(repository);
      fileName = "export.bin";
    }
    else
    {
      exporter = new CDOServerExporter.XML(repository);
      fileName = "export.xml";
    }

    File evolutionFolder = support.getEvolutionFolder();
    File exportFile = new File(evolutionFolder, fileName);
    context.log("Exporting repository to " + exportFile.getAbsolutePath());

    try (OutputStream out = IOUtil.buffered(new FileOutputStream(exportFile)))
    {
      exporter.exportRepository(out);
    }
  }
}