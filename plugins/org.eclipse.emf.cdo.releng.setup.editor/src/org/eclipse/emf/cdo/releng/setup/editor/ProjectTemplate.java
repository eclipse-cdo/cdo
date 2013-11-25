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

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.util.Collection;

/**
 * @author Eike Stepper
 */
public abstract class ProjectTemplate extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.releng.setup.projectTemplates";

  private final String label;

  private final String description;

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

  public final ProjectTemplate create(String description) throws ProductCreationException
  {
    return this;
  }

  @Override
  public final String toString()
  {
    return getLabel();
  }

  public boolean isValid(Project project)
  {
    return !project.getBranches().isEmpty();
  }

  public boolean isValid(Branch branch)
  {
    return !StringUtil.isEmpty(branch.getName());
  }

  public abstract Control createControl(Composite parent, Container container, Project project);

  public static boolean contains(Collection<?> collection, Class<?> type)
  {
    for (Object object : collection)
    {
      if (type.isInstance(object))
      {
        return true;
      }
    }

    return false;
  }

  /**
   * @author Eike Stepper
   */
  public interface Container
  {
    public TreeViewer getPreViewer();

    public void validate();
  }
}
