/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.editor;

import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Eike Stepper
 */
public abstract class ProjectTemplate extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.releng.setup.projectTemplates";

  private final String label;

  private final String description;

  private final Project project = SetupFactory.eINSTANCE.createProject();

  protected ProjectTemplate(String type, String label, String description)
  {
    super(PRODUCT_GROUP, type);
    this.label = label == null ? type : label;
    this.description = StringUtil.safe(description);
  }

  protected ProjectTemplate(String type, String label)
  {
    this(type, label, null);
  }

  public final String getLabel()
  {
    return label;
  }

  public final String getDescription()
  {
    return description;
  }

  public final Project getProject()
  {
    return project;
  }

  public final ProjectTemplate create(String description) throws ProductCreationException
  {
    return this;
  }

  @Override
  public final String toString()
  {
    return getLabel();
  }

  public boolean isPageValid()
  {
    return true;
  }

  public abstract Control createControl(Composite parent);

  protected final Branch addBranch(String name)
  {
    Branch branch = SetupFactory.eINSTANCE.createBranch();
    branch.setName(name);
    project.getBranches().add(branch);
    return branch;
  }
}
