package org.eclipse.emf.cdo.evolution.presentation.quickfix;

import org.eclipse.emf.cdo.evolution.presentation.EvolutionEditor;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.edit.command.ChangeCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.statushandlers.StatusManager;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

/**
 * QuickFixPage is a page for the quick fixes of a marker.
 *
 * @since 3.4
 *
 */
public class QuickFixPage extends WizardPage
{
  private Map<DiagnosticResolution, Collection<Diagnostic>> resolutionsMap;

  private TableViewer resolutionsViewer;

  private CheckboxTableViewer diagnosticsViewer;

  private final Diagnostic[] selectedDiagnostics;

  private AdapterFactoryEditingDomain editingDomain;

  public QuickFixPage(String problemDescription, Diagnostic[] selectedDiagnostics, Map<DiagnosticResolution, Collection<Diagnostic>> resolutionsMap,
      AdapterFactoryEditingDomain editingDomain)
  {
    super("QuickFixPage");
    this.selectedDiagnostics = selectedDiagnostics;
    this.resolutionsMap = resolutionsMap;
    this.editingDomain = editingDomain;

    setTitle("Quick Fix");
    setMessage(problemDescription);
  }

  public void createControl(Composite parent)
  {
    initializeDialogUnits(parent);

    // Create a new composite as there is the title bar separator to deal with
    Composite control = new Composite(parent, SWT.NONE);
    control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    setControl(control);

    // PlatformUI.getWorkbench().getHelpSystem().setHelp(control, IWorkbenchHelpContextIds.PROBLEMS_VIEW);

    FormLayout layout = new FormLayout();
    layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
    layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
    layout.spacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
    control.setLayout(layout);

    Label resolutionsLabel = new Label(control, SWT.NONE);
    resolutionsLabel.setText("&Select a fix:");
    resolutionsLabel.setLayoutData(new FormData());

    createResolutionsList(control);

    FormData listData = new FormData();
    listData.top = new FormAttachment(resolutionsLabel, 0);
    listData.left = new FormAttachment(0);
    listData.right = new FormAttachment(100, 0);
    listData.height = convertHeightInCharsToPixels(10);
    resolutionsViewer.getControl().setLayoutData(listData);

    Label title = new Label(control, SWT.NONE);
    title.setText("&Problems:");
    FormData labelData = new FormData();
    labelData.top = new FormAttachment(resolutionsViewer.getControl(), 0);
    labelData.left = new FormAttachment(0);
    title.setLayoutData(labelData);

    createDiagnosticsViewer(control);

    Composite buttons = createTableButtons(control);
    FormData buttonData = new FormData();
    buttonData.top = new FormAttachment(title, 0);
    buttonData.right = new FormAttachment(100);
    buttonData.height = convertHeightInCharsToPixels(10);
    buttons.setLayoutData(buttonData);

    FormData tableData = new FormData();
    tableData.top = new FormAttachment(buttons, 0, SWT.TOP);
    tableData.left = new FormAttachment(0);
    tableData.bottom = new FormAttachment(100);
    tableData.right = new FormAttachment(buttons, 0);
    tableData.height = convertHeightInCharsToPixels(10);
    diagnosticsViewer.getControl().setLayoutData(tableData);

    Dialog.applyDialogFont(control);

    resolutionsViewer.setSelection(new StructuredSelection(resolutionsViewer.getElementAt(0)));

    diagnosticsViewer.setCheckedElements(selectedDiagnostics);

    setPageComplete(diagnosticsViewer.getCheckedElements().length > 0);
  }

  private Composite createTableButtons(Composite control)
  {
    Composite buttonComposite = new Composite(control, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
    layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
    buttonComposite.setLayout(layout);

    Button selectAll = new Button(buttonComposite, SWT.PUSH);
    selectAll.setText("Select &All");
    selectAll.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
    selectAll.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent arg0)
      {
        diagnosticsViewer.setAllChecked(true);
        setPageComplete(!resolutionsViewer.getStructuredSelection().isEmpty());
      }
    });

    Button deselectAll = new Button(buttonComposite, SWT.PUSH);
    deselectAll.setText("&Deselect All");
    deselectAll.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
    deselectAll.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent arg0)
      {
        diagnosticsViewer.setAllChecked(false);
        setPageComplete(false);
      }
    });

    return buttonComposite;
  }

  private void createResolutionsList(Composite control)
  {
    resolutionsViewer = new TableViewer(control, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
    resolutionsViewer.setContentProvider(new IStructuredContentProvider()
    {
      public Object[] getElements(Object inputElement)
      {
        return resolutionsMap.keySet().toArray();
      }

      public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
      {
      }

      public void dispose()
      {
      }
    });

    resolutionsViewer.setLabelProvider(new LabelProvider()
    {
      @Override
      public String getText(Object element)
      {
        return ((DiagnosticResolution)element).getText();
      }

      @Override
      public Image getImage(Object element)
      {
        return ((DiagnosticResolution)element).getImage();
      }
    });

    resolutionsViewer.setComparator(new ViewerComparator()
    {
      /**
       * This comparator compares the resolutions based on the relevance of the
       * resolutions. Any resolution that doesn't implement DiagnosticResolutionRelevance
       * will be deemed to have relevance 0 (default value for relevance). If both
       * resolutions have the same relevance, then marker resolution label string will
       * be used for comparing the resolutions.
       *
       * @see DiagnosticResolutionRelevance#getRelevanceForResolution()
       */
      @Override
      public int compare(Viewer viewer, Object e1, Object e2)
      {
        int relevanceMarker1 = e1 instanceof DiagnosticResolutionRelevance ? ((DiagnosticResolutionRelevance)e1).getRelevanceForResolution() : 0;
        int relevanceMarker2 = e2 instanceof DiagnosticResolutionRelevance ? ((DiagnosticResolutionRelevance)e2).getRelevanceForResolution() : 0;
        if (relevanceMarker1 != relevanceMarker2)
        {
          return Integer.valueOf(relevanceMarker2).compareTo(Integer.valueOf(relevanceMarker1));
        }

        return ((DiagnosticResolution)e1).getText().compareTo(((DiagnosticResolution)e2).getText());
      }
    });

    resolutionsViewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      public void selectionChanged(SelectionChangedEvent event)
      {
        diagnosticsViewer.refresh();
        setPageComplete(diagnosticsViewer.getCheckedElements().length > 0);
      }
    });

    resolutionsViewer.setInput(this);
  }

  private void createDiagnosticsViewer(Composite parent)
  {
    diagnosticsViewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE);
    diagnosticsViewer.setContentProvider(new DiagnosticsContentProvider());
    diagnosticsViewer.setInput(this);

    Table table = diagnosticsViewer.getTable();
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    EvolutionEditor.createDiagnosticElementColumn(diagnosticsViewer, editingDomain.getAdapterFactory());
    EvolutionEditor.createDiagnosticResourceColumn(diagnosticsViewer, editingDomain.getAdapterFactory());

    diagnosticsViewer.addCheckStateListener(new ICheckStateListener()
    {
      public void checkStateChanged(CheckStateChangedEvent event)
      {
        if (event.getChecked() == true)
        {
          setPageComplete(true);
        }
        else
        {
          setPageComplete(diagnosticsViewer.getCheckedElements().length > 0);
        }
      }
    });

    // new OpenAndLinkWithEditorHelper(markersTable)
    // {
    // {
    // setLinkWithEditor(false);
    // }
    //
    // @Override
    // protected void activate(ISelection selection)
    // {
    // open(selection, true);
    // }
    //
    // /** Not supported*/
    //
    // @Override
    // protected void linkToEditor(ISelection selection)
    // {
    // }
    //
    // @Override
    // protected void open(ISelection selection, boolean activate)
    // {
    // if (selection.isEmpty())
    // {
    // return;
    // }
    // Diagnostic marker = (Diagnostic)((IStructuredSelection)selection).getFirstElement();
    // if (marker != null && marker.getResource() instanceof IFile)
    // {
    // try
    // {
    // IDE.openEditor(site.getPage(), marker, activate);
    // }
    // catch (PartInitException e)
    // {
    // MarkerSupportInternalUtilities.showViewError(e);
    // }
    // }
    // }
    // };
  }

  public Diagnostic getSelectedDiagnostic()
  {
    IStructuredSelection selection = diagnosticsViewer.getStructuredSelection();
    if (!selection.isEmpty())
    {
      if (selection.size() == 1)
      {
        return (Diagnostic)selection.getFirstElement();
      }
    }

    return null;
  }

  void performFinish(IProgressMonitor monitor)
  {
    final DiagnosticResolution resolution = getSelectedResolution();
    if (resolution == null)
    {
      return;
    }

    final Object[] checked = diagnosticsViewer.getCheckedElements();
    if (checked.length == 0)
    {
      return;
    }

    try
    {
      getWizard().getContainer().run(false, true, new IRunnableWithProgress()
      {
        public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
        {
          final Diagnostic[] diagnostics = new Diagnostic[checked.length];
          System.arraycopy(checked, 0, diagnostics, 0, checked.length);

          ChangeCommand command = new ChangeCommand(editingDomain.getResourceSet())
          {
            @Override
            protected void doExecute()
            {
              resolution.run(diagnostics, monitor);
            }
          };

          editingDomain.getCommandStack().execute(command);
        }
      });
    }
    catch (InvocationTargetException e)
    {
      StatusManager.getManager().handle(QuickFixWizard.newStatus(IStatus.ERROR, e.getLocalizedMessage(), e));
    }
    catch (InterruptedException e)
    {
      StatusManager.getManager().handle(QuickFixWizard.newStatus(IStatus.ERROR, e.getLocalizedMessage(), e));
    }
  }

  private DiagnosticResolution getSelectedResolution()
  {
    return (DiagnosticResolution)resolutionsViewer.getStructuredSelection().getFirstElement();
  }

  /**
   * @author Eike Stepper
   */
  private final class DiagnosticsContentProvider implements IStructuredContentProvider
  {
    public Object[] getElements(Object inputElement)
    {
      DiagnosticResolution selected = getSelectedResolution();
      if (selected != null && resolutionsMap.containsKey(selected))
      {
        return resolutionsMap.get(selected).toArray();
      }

      return DiagnosticResolution.NO_DIAGNOSTICS;
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    public void dispose()
    {
    }
  }
}
