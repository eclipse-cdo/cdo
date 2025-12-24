/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.editor;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.security.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.ui.CDOEditorInput;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewTargetChangedEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IPartSelectionListener;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * The main page of the "Security Manager" form editor.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class CDOSecurityPage extends FormPage
{
  private static final Object NO_INPUT = new Object();

  private IListener viewTargetListener;

  private Object formInput;

  private IActionBars actionBars;

  CDOSecurityPage(CDOSecurityFormEditor editor)
  {
    super(editor, "securityForm", Messages.CDOSecurityPage_1); //$NON-NLS-1$
  }

  @Override
  public void initialize(FormEditor editor)
  {
    super.initialize(editor);

    viewTargetListener = createViewTargetListener();
    getView().addListener(viewTargetListener);

    actionBars = getEditor().getActionBars();

    CDOResource resource = getEditor().getResource();
    if (resource == null)
    {
      formInput = NO_INPUT;
    }
    else
    {
      formInput = EcoreUtil.getObjectByType(resource.getContents(), SecurityPackage.Literals.REALM);
    }
  }

  @Override
  public CDOSecurityFormEditor getEditor()
  {
    return (CDOSecurityFormEditor)super.getEditor();
  }

  @Override
  protected void createFormContent(IManagedForm managedForm)
  {
    final ScrolledForm form = managedForm.getForm();
    final FormToolkit toolkit = managedForm.getToolkit();
    toolkit.decorateFormHeading(form.getForm());
    form.getToolBarManager().add(createEditAdvancedAction());

    class EmptySelectionForwarder extends AbstractFormPart implements IPartSelectionListener
    {
      private DetailsPart details;

      @Override
      public void selectionChanged(IFormPart part, ISelection selection)
      {
        if (selection.isEmpty() && details != null)
        {
          forwardEmptySelection();
        }
      }

      void setDetailsPart(DetailsPart detailsPart)
      {
        details = detailsPart;
      }

      void forwardEmptySelection()
      {
        // Show our preferred empty page
        details.selectionChanged(this, new StructuredSelection(EcoreFactory.eINSTANCE.createEObject()));
      }
    }

    final EmptySelectionForwarder emptySelectionForwarder = new EmptySelectionForwarder();
    MasterDetailsBlock masterDetail = new MasterDetailsBlock()
    {
      @Override
      protected void registerPages(DetailsPart detailsPart)
      {
        detailsPart.setPageProvider(EClassDetailsPageProvider.builder(actionBars) //
            .page(SecurityPackage.Literals.GROUP, new GroupDetailsPage(getEditingDomain(), getAdapterFactory())) //
            .page(SecurityPackage.Literals.USER, new UserDetailsPage(getEditingDomain(), getAdapterFactory())) //
            .page(SecurityPackage.Literals.ROLE, new RoleDetailsPage(getEditingDomain(), getAdapterFactory())) //
            .page(EcorePackage.Literals.EOBJECT, new EmptyDetailsPage()) //
            .build());

        emptySelectionForwarder.setDetailsPart(detailsPart);
      }

      @Override
      protected void createToolBarActions(IManagedForm managedForm)
      {
      }

      @Override
      protected void createMasterPart(IManagedForm managedForm, Composite parent)
      {
        Composite body = managedForm.getToolkit().createComposite(parent);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        body.setLayout(layout);

        addSection(new GroupsSection(getEditingDomain(), getAdapterFactory()), managedForm, body);
        addSection(new UsersSection(getEditingDomain(), getAdapterFactory()), managedForm, body);
        addSection(new RolesSection(getEditingDomain(), getAdapterFactory()), managedForm, body);
      }
    };

    form.setText(Messages.CDOSecurityPage_2);
    masterDetail.createContent(managedForm);

    managedForm.setInput(formInput);

    // Initialize the empty selection
    managedForm.addPart(emptySelectionForwarder);
    emptySelectionForwarder.forwardEmptySelection();

    form.updateToolBar();

    if (getEditor().isReadOnly())
    {
      managedForm.getMessageManager().addMessage(this, Messages.CDOSecurityPage_3, null, IMessageProvider.INFORMATION);
    }
    else
    {
      // Check for unsupported security constructs
      checkForUnsupportedModelContent();
    }

    // Update the message manager after the form's contents have been presented to
    // ensure the heading's summary of problems is up-to-date
    Display.getCurrent().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        getManagedForm().getMessageManager().update();
      }
    });
  }

  protected <S extends AbstractSectionPart<?>> S addSection(S section, IManagedForm managedForm, Composite parent)
  {
    section.setEditorActionBars(actionBars);
    managedForm.addPart(section);
    section.createContents(parent);
    return section;
  }

  @Override
  public void dispose()
  {
    try
    {
      CDOView view = getView();

      if (view != null)
      {
        view.removeListener(viewTargetListener);
      }
    }
    finally
    {
      super.dispose();
    }
  }

  protected AdapterFactory getAdapterFactory()
  {
    return getEditor().getAdapterFactory();
  }

  protected EditingDomain getEditingDomain()
  {
    return getEditor().getEditingDomain();
  }

  protected CDOView getView()
  {
    return getEditor().getView();
  }

  protected IListener createViewTargetListener()
  {
    return new IListener()
    {
      protected CDOID inputID;

      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDOViewTargetChangedEvent)
        {
          final CDOViewTargetChangedEvent e = (CDOViewTargetChangedEvent)event;
          getSite().getShell().getDisplay().asyncExec(new Runnable()
          {
            @Override
            public void run()
            {
              Object input = getManagedForm().getInput();
              if (input == NO_INPUT)
              {
                if (inputID != null)
                {
                  try
                  {
                    CDOObject object = getView().getObject(inputID);
                    getManagedForm().setInput(object);
                    inputID = null;
                  }
                  catch (Exception ex)
                  {
                    // Ignore
                  }
                }
              }
              else if (input instanceof EObject)
              {
                CDOObject object = CDOUtil.getCDOObject((EObject)input);
                if (object.cdoInvalid())
                {
                  if (e.getBranchPoint().getTimeStamp() == e.getOldBranchPoint().getTimeStamp())
                  {
                    inputID = null;
                    closeEditor();
                  }
                  else
                  {
                    inputID = object.cdoID();
                    getManagedForm().setInput(NO_INPUT);
                  }
                }
              }
            }
          });
        }
      }
    };
  }

  protected void closeEditor()
  {
    try
    {
      getSite().getShell().getDisplay().asyncExec(new Runnable()
      {
        @Override
        public void run()
        {
          try
          {
            getSite().getPage().closeEditor(getEditor(), false);
          }
          catch (Exception e)
          {
            OM.LOG.error(e);
          }
        }
      });
    }
    catch (Exception e)
    {
      OM.LOG.error(e);
    }
  }

  protected void checkForUnsupportedModelContent()
  {
    Object input = getManagedForm().getInput();
    if (!(input instanceof Realm))
    {
      getManagedForm().getMessageManager().addMessage(this, Messages.CDOSecurityPage_4, null, IMessageProvider.ERROR);
    }
  }

  private IAction createEditAdvancedAction()
  {
    return new Action(Messages.CDOSecurityPage_0,
        ExtendedImageRegistry.getInstance().getImageDescriptor(URI.createPlatformPluginURI(OM.BUNDLE_ID + "/icons/full/elcl16/advanced.gif", true))) //$NON-NLS-1$
    {
      @Override
      public void run()
      {
        try
        {
          IEditorInput newEditorInput = CDOEditorUtil.createCDOEditorInputWithEditingDomain((CDOEditorInput)getEditorInput(), getEditingDomain());
          getSite().getPage().openEditor(newEditorInput, CDOEditorUtil.getEditorID(), true, IWorkbenchPage.MATCH_INPUT | IWorkbenchPage.MATCH_ID);
        }
        catch (PartInitException e)
        {
          OM.LOG.error(e);
        }
      }
    };
  }
}
