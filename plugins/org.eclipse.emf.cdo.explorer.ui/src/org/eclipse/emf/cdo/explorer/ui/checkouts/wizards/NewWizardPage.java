/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts.wizards;

import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class NewWizardPage extends CheckoutNodeWizardPage
{
  private final String resourceType;

  private Text nameText;

  private boolean nameModified;

  private String name;

  public NewWizardPage(String resourceType, String title, IStructuredSelection selection)
  {
    super("NewPage", title, selection);
    this.resourceType = resourceType;
  }

  @Override
  public NewWizard getWizard()
  {
    return (NewWizard)super.getWizard();
  }

  @Override
  public final String getName()
  {
    return name;
  }

  @Override
  protected String getNodeMessage()
  {
    return "Select the parent folder";
  }

  @Override
  protected void doCreateControl(Composite parent)
  {
    setImageDescriptor(OM.getImageDescriptor("icons/wiz/new_" + resourceType + ".gif"));
    setMessage("Enter the name of the " + title.toLowerCase() + ".");

    super.doCreateControl(parent);

    Label nameLabel = new Label(parent, SWT.NONE);
    nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    nameLabel.setText("Name:");

    nameText = new Text(parent, SWT.BORDER);
    nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    nameText.addModifyListener(e -> {
      nameModified = true;
      validate();
    });
  }

  @Override
  protected void deferredInit()
  {
    super.deferredInit();
    if (getNode() != null)
    {
      nameText.setFocus();
    }
    else
    {
      getControl().getParent().setFocus();
    }

    nameText.selectAll();
  }

  @Override
  protected void doValidate() throws Exception
  {
    super.doValidate();

    String name = nameText.getText();
    if (!nameModified)
    {
      name = getUniqueName();
      nameText.setText(name);
    }
    else
    {
      if (name.length() == 0)
      {
        // throw new Exception("Name is empty.");
      }

      if (!isUnique(name))
      {
        throw new Exception("Name is not unique within the parent folder.");
      }

      this.name = name;
    }
  }

  private String getUniqueName()
  {
    Set<String> names = new HashSet<>();
    for (EObject eObject : getNodeChildren())
    {
      if (eObject instanceof CDOResourceNode)
      {
        CDOResourceNode node = (CDOResourceNode)eObject;
        String name = node.getName();
        if (name.startsWith(resourceType))
        {
          names.add(name);
        }
      }
    }

    for (int i = 1; i < Integer.MAX_VALUE; i++)
    {
      String name = resourceType + i;
      if (!names.contains(name))
      {
        return name;
      }
    }

    throw new IllegalStateException("Too many children");
  }

  private boolean isUnique(String name)
  {
    for (EObject eObject : getNodeChildren())
    {
      if (eObject instanceof CDOResourceNode)
      {
        CDOResourceNode node = (CDOResourceNode)eObject;
        if (ObjectUtil.equals(node.getName(), name))
        {
          return false;
        }
      }
    }

    return true;
  }
}
