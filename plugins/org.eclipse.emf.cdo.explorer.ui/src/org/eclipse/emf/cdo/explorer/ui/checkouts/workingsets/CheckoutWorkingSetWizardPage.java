/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts.workingsets;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.explorer.ui.checkouts.CDOCheckoutContentProvider;
import org.eclipse.emf.cdo.explorer.ui.checkouts.CDOCheckoutLabelProvider;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IWorkingSetPage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CheckoutWorkingSetWizardPage extends WizardPage implements IWorkingSetPage
{
  public static final String WORKING_SET_ID = "org.eclipse.emf.cdo.explorer.ui.CheckoutWorkingSet"; //$NON-NLS-1$

  private static final String PAGE_NAME = "cdoCheckoutWorkingSetPage"; //$NON-NLS-1$

  private static final String PAGE_TITLE = "CDO Checkout Working Set";

  private final Set<Object> selectedElements = new HashSet<>();

  private IStructuredSelection initialSelection;

  private Text nameText;

  private TreeViewer treeViewer;

  private TableViewer tableViewer;

  private boolean firstCheck;

  private IWorkingSet workingSet;

  public CheckoutWorkingSetWizardPage()
  {
    super(PAGE_NAME, PAGE_TITLE, OM.getImageDescriptor("icons/wiz/new_checkout.gif"));

    firstCheck = true;

    setDescription("Enter a working set name and select the working set elements.");
    IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (activeWorkbenchWindow != null)
    {
      ISelection selection = activeWorkbenchWindow.getSelectionService().getSelection();
      if (selection instanceof IStructuredSelection)
      {
        initialSelection = (IStructuredSelection)selection;
      }
    }
  }

  private Object[] getInitialWorkingSetElements(IWorkingSet workingSet)
  {
    Object[] elements;
    if (workingSet == null)
    {
      if (initialSelection == null)
      {
        return new IAdaptable[0];
      }

      elements = initialSelection.toArray();
    }
    else
    {
      elements = workingSet.getElements();
    }

    Set<CDOCheckout> result = new LinkedHashSet<>();

    for (int i = 0; i < elements.length; i++)
    {
      Object element = elements[i];
      CDOCheckout checkout = CDOExplorerUtil.getCheckout(element);
      if (checkout != null)
      {
        result.add(checkout);
      }
    }

    return result.toArray();
  }

  private Object[] getInitialTreeSelection()
  {
    Object[][] result = new Object[1][];
    BusyIndicator.showWhile(getShell().getDisplay(), () -> {
      IStructuredSelection selection = initialSelection;
      if (selection == null)
      {
        IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
        if (part == null)
        {
          return;
        }

        selection = getStructuredSelection(part);
      }

      Object[] elements = selection.toArray();
      for (int i = 0; i < elements.length; i++)
      {
        elements[i] = CDOExplorerUtil.getCheckout(elements[i]);
      }

      result[0] = elements;
    });

    if (result[0] == null)
    {
      return new Object[0];
    }

    return result[0];
  }

  @Override
  public void createControl(Composite parent)
  {
    initializeDialogUnits(parent);

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout());
    composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
    setControl(composite);

    Label label = new Label(composite, SWT.WRAP);
    label.setText("&Working set name:");
    GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
    label.setLayoutData(gd);

    nameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
    nameText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
    nameText.addModifyListener(e -> validateInput());

    Composite leftCenterRightComposite = new Composite(composite, SWT.NONE);
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    gridData.heightHint = convertHeightInCharsToPixels(20);
    leftCenterRightComposite.setLayoutData(gridData);
    GridLayout gridLayout = new GridLayout(3, false);
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    leftCenterRightComposite.setLayout(gridLayout);

    Composite leftComposite = new Composite(leftCenterRightComposite, SWT.NONE);
    gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    gridData.widthHint = convertWidthInCharsToPixels(40);
    leftComposite.setLayoutData(gridData);
    gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    leftComposite.setLayout(gridLayout);

    Composite centerComposite = new Composite(leftCenterRightComposite, SWT.NONE);
    gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    centerComposite.setLayout(gridLayout);
    centerComposite.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false));

    Composite rightComposite = new Composite(leftCenterRightComposite, SWT.NONE);
    gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    gridData.widthHint = convertWidthInCharsToPixels(40);
    rightComposite.setLayoutData(gridData);
    gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    rightComposite.setLayout(gridLayout);

    createTree(leftComposite);
    createTable(rightComposite);

    if (workingSet != null)
    {
      nameText.setText(workingSet.getName());
    }

    initializeSelectedElements();
    validateInput();

    tableViewer.setInput(selectedElements);
    tableViewer.refresh(true);
    treeViewer.refresh(true);

    createButtonBar(centerComposite);

    nameText.setFocus();
    nameText.setSelection(0, nameText.getText().length());

    Dialog.applyDialogFont(composite);
  }

  private void createTree(Composite parent)
  {
    Label label = new Label(parent, SWT.NONE);
    label.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, false, false));
    label.setText("A&vailable checkouts:");

    treeViewer = CDOCheckoutContentProvider.createTreeViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI, e -> e instanceof CDOCheckout);
    // treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
    // treeViewer.setContentProvider(itemProvider);
    // treeViewer.setLabelProvider(new CDOCheckoutLabelProvider());
    // treeViewer.setInput(CDOExplorerUtil.getCheckoutManager());
    // treeViewer.setUseHashlookup(true);
    treeViewer.addFilter(new AddedElementsFilter());

    Tree tree = treeViewer.getTree();
    tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    if (getSelection() == null)
    {
      return;
    }

    Object[] selection = getInitialTreeSelection();
    if (selection.length > 0)
    {
      try
      {
        tree.setRedraw(false);

        for (Object s : selection)
        {
          treeViewer.expandToLevel(s, 0);
        }

        treeViewer.setSelection(new StructuredSelection(selection));
      }
      finally
      {
        tree.setRedraw(true);
      }
    }
  }

  private void createTable(Composite parent)
  {
    Label label = new Label(parent, SWT.WRAP);
    label.setText("Working set &content:");
    label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

    CDOCheckoutContentProvider contentProvider = new CDOCheckoutContentProvider();
    contentProvider.disposeWith(parent);

    tableViewer = new TableViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
    tableViewer.setUseHashlookup(true);
    tableViewer.setContentProvider(ArrayContentProvider.getInstance());
    tableViewer.setLabelProvider(new CDOCheckoutLabelProvider(contentProvider));
    tableViewer.setInput(selectedElements);

    Table table = tableViewer.getTable();
    table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
  }

  private void createButtonBar(Composite parent)
  {
    Label spacer = new Label(parent, SWT.NONE);
    spacer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

    Button addButton = new Button(parent, SWT.PUSH);
    addButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    addButton.setText("&Add ->");
    addButton.setEnabled(!treeViewer.getSelection().isEmpty());

    Button addAllButton = new Button(parent, SWT.PUSH);
    addAllButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    addAllButton.setText("A&dd All ->");
    addAllButton.setEnabled(treeViewer.getTree().getItems().length > 0);

    Button removeButton = new Button(parent, SWT.PUSH);
    removeButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    removeButton.setText("<- &Remove");
    removeButton.setEnabled(!tableViewer.getSelection().isEmpty());

    Button removeAllButton = new Button(parent, SWT.PUSH);
    removeAllButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false));
    removeAllButton.setText("<- R&emove All");
    removeAllButton.setEnabled(!selectedElements.isEmpty());

    treeViewer.addSelectionChangedListener(event -> addButton.setEnabled(!event.getSelection().isEmpty()));

    addButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        addTreeSelection();
        removeAllButton.setEnabled(true);
        addAllButton.setEnabled(treeViewer.getTree().getItems().length > 0);
      }
    });

    treeViewer.addDoubleClickListener(event -> {
      addTreeSelection();
      removeAllButton.setEnabled(true);
      addAllButton.setEnabled(treeViewer.getTree().getItems().length > 0);
    });

    tableViewer.addSelectionChangedListener(event -> removeButton.setEnabled(!event.getSelection().isEmpty()));

    removeButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        removeTableSelection();
        addAllButton.setEnabled(true);
        removeAllButton.setEnabled(!selectedElements.isEmpty());
      }
    });

    tableViewer.addDoubleClickListener(event -> {
      removeTableSelection();
      addAllButton.setEnabled(true);
      removeAllButton.setEnabled(!selectedElements.isEmpty());
    });

    addAllButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        for (TreeItem item : treeViewer.getTree().getItems())
        {
          selectedElements.add(item.getData());
        }

        tableViewer.refresh();
        treeViewer.refresh();

        addAllButton.setEnabled(false);
        removeAllButton.setEnabled(true);
      }
    });

    removeAllButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        selectedElements.clear();

        tableViewer.refresh();
        treeViewer.refresh();

        removeAllButton.setEnabled(false);
        addAllButton.setEnabled(true);
      }
    });
  }

  @SuppressWarnings("unchecked")
  private void addTreeSelection()
  {
    IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
    selectedElements.addAll(selection.toList());
    Object[] selectedElements = selection.toArray();
    tableViewer.add(selectedElements);
    treeViewer.remove(selectedElements);
    tableViewer.setSelection(selection);
    tableViewer.getControl().setFocus();
    validateInput();
  }

  private void removeTableSelection()
  {
    IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
    selectedElements.removeAll(selection.toList());
    Object[] selectedElements = selection.toArray();
    tableViewer.remove(selectedElements);

    try
    {
      treeViewer.getTree().setRedraw(false);
      ITreeContentProvider contentProvider = (ITreeContentProvider)treeViewer.getContentProvider();

      for (Object selectedElement : selectedElements)
      {
        treeViewer.refresh(contentProvider.getParent(selectedElement), true);
      }
    }
    finally
    {
      treeViewer.getTree().setRedraw(true);
    }

    treeViewer.setSelection(selection);
    treeViewer.getControl().setFocus();
    validateInput();
  }

  @Override
  public IWorkingSet getSelection()
  {
    return workingSet;
  }

  @Override
  public void setSelection(IWorkingSet workingSet)
  {
    Assert.isNotNull(workingSet, "Working set must not be null"); //$NON-NLS-1$
    this.workingSet = workingSet;

    if (getContainer() != null && getShell() != null && nameText != null)
    {
      firstCheck = false;
      nameText.setText(workingSet.getName());
      initializeSelectedElements();
      validateInput();
    }
  }

  @Override
  public void finish()
  {
    String workingSetName = nameText.getText();
    Set<Object> elements = selectedElements;

    if (workingSet == null)
    {
      IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
      workingSet = workingSetManager.createWorkingSet(workingSetName, elements.toArray(new IAdaptable[elements.size()]));
      workingSet.setId(WORKING_SET_ID);
    }

    workingSet.setName(workingSetName);
    workingSet.setElements(elements.toArray(new IAdaptable[elements.size()]));
  }

  private void validateInput()
  {
    String errorMessage = null;
    String infoMessage = null;
    String newText = nameText.getText();

    if (newText.equals(newText.trim()) == false)
    {
      errorMessage = "The name must not have leading or trailing whitespace.";
    }
    if (newText.isEmpty())
    {
      if (firstCheck)
      {
        setPageComplete(false);
        firstCheck = false;
        return;
      }

      errorMessage = "The name must not be empty.";
    }

    firstCheck = false;

    if (errorMessage == null && (workingSet == null || newText.equals(workingSet.getName()) == false))
    {
      for (IWorkingSet workingSet : PlatformUI.getWorkbench().getWorkingSetManager().getWorkingSets())
      {
        if (newText.equals(workingSet.getName()))
        {
          errorMessage = "A working set with that name already exists.";
        }
      }
    }

    if (!hasSelectedElement())
    {
      infoMessage = "No checkouts selected.";
    }

    setMessage(infoMessage, INFORMATION);
    setErrorMessage(errorMessage);
    setPageComplete(errorMessage == null);
  }

  private boolean hasSelectedElement()
  {
    return !selectedElements.isEmpty();
  }

  private void initializeSelectedElements()
  {
    selectedElements.addAll(Arrays.asList(getInitialWorkingSetElements(workingSet)));
  }

  private static IStructuredSelection getStructuredSelection(IWorkbenchPart part)
  {
    ISelectionProvider provider = part.getSite().getSelectionProvider();
    if (provider != null)
    {
      ISelection selection = provider.getSelection();
      if (selection instanceof IStructuredSelection)
      {
        return (IStructuredSelection)selection;
      }
    }

    return StructuredSelection.EMPTY;
  }

  /**
   * @author Eike Stepper
   */
  private final class AddedElementsFilter extends ViewerFilter
  {
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element)
    {
      return !selectedElements.contains(element);
    }
  }
}
