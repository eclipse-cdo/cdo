/*
 * Copyright (c) 2012, 2013, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.EresourceFactory;
import org.eclipse.emf.cdo.internal.ui.actions.NewTopLevelResourceNodeAction.Type;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public abstract class NewResourceNodeAction extends TransactionalBackgroundAction
{
  private final Type type;

  private String name;

  public NewResourceNodeAction(IWorkbenchPage page, Type type, CDOObject object)
  {
    super(page, type.getTitle(), type.getTooltip(), type.getImageDescriptor(), object);
    this.type = type;
  }

  public final Type getType()
  {
    return type;
  }

  public final String getName()
  {
    return name;
  }

  @Override
  protected void preRun()
  {
    CDOResourceNode object = (CDOResourceNode)getObject();

    String initialValue = (type == Type.FOLDER ? "folder" : "resource") + (AbstractViewAction.lastResourceNumber + 1);
    InputDialog dialog = new InputDialog(getShell(), getText(), Messages.getString("NewResourceNodeAction.8"), initialValue,
        new ResourceNodeNameInputValidator(object));
    if (dialog.open() == Dialog.OK)
    {
      ++AbstractViewAction.lastResourceNumber;
      name = dialog.getValue();
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected final void doRun(CDOTransaction transaction, CDOObject object, IProgressMonitor progressMonitor) throws Exception
  {
    CDOResourceNode newResourceNode = createNewResourceNode();
    newResourceNode.setName(name);

    if (object instanceof CDOResourceFolder)
    {
      ((CDOResourceFolder)object).getNodes().add(newResourceNode);
    }
    else
    {
      transaction.getRootResource().getContents().add(newResourceNode);
    }
  }

  protected abstract CDOResourceNode createNewResourceNode();

  /**
   * @author Eike Stepper
   */
  public static class Folder extends NewResourceNodeAction
  {
    public Folder(IWorkbenchPage page, CDOObject object)
    {
      super(page, Type.FOLDER, object);
    }

    @Override
    protected CDOResourceNode createNewResourceNode()
    {
      return EresourceFactory.eINSTANCE.createCDOResourceFolder();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Model extends NewResourceNodeAction
  {
    public Model(IWorkbenchPage page, CDOObject object)
    {
      super(page, Type.MODEL, object);
    }

    @Override
    protected CDOResourceNode createNewResourceNode()
    {
      return EresourceFactory.eINSTANCE.createCDOResource();
    }

    @Override
    protected void postRun(CDOView view, CDOObject object)
    {
      String resourcePath = ((CDOResourceNode)object).getPath() + "/" + getName();
      CDOEditorUtil.openEditor(getPage(), view, resourcePath);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Binary extends NewResourceNodeAction
  {
    public Binary(IWorkbenchPage page, CDOObject object)
    {
      super(page, Type.BINARY, object);
    }

    @Override
    protected CDOResourceNode createNewResourceNode()
    {
      return EresourceFactory.eINSTANCE.createCDOBinaryResource();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Text extends NewResourceNodeAction
  {
    public Text(IWorkbenchPage page, CDOObject object)
    {
      super(page, Type.TEXT, object);
    }

    @Override
    protected CDOResourceNode createNewResourceNode()
    {
      return EresourceFactory.eINSTANCE.createCDOTextResource();
    }
  }
}
