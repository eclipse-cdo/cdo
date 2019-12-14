/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.widgets.SearchField;
import org.eclipse.net4j.util.ui.widgets.SearchField.FilterHandler;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class SelectClassDialog extends TitleAreaDialog
{
  private final IWorkbenchPage page;

  private final String title;

  private final String message;

  private EPackage.Registry registry = EPackage.Registry.INSTANCE;

  private SearchField packageSearch;

  private TableViewer packageViewer;

  private SearchField classSearch;

  private EClass selectedClass;

  private TableViewer classViewer;

  public SelectClassDialog(IWorkbenchPage page, String title, String message)
  {
    super(page.getWorkbenchWindow().getShell());
    this.page = page;
    this.title = title;
    this.message = message;
    setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);
  }

  public IWorkbenchPage getPage()
  {
    return page;
  }

  public EPackage.Registry getRegistry()
  {
    return registry;
  }

  public void setRegistry(EPackage.Registry registry)
  {
    this.registry = registry;
  }

  public EClass getSelectedClass()
  {
    return selectedClass;
  }

  @Override
  protected Point getInitialSize()
  {
    return new Point(750, 600);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    getShell().setText(title);
    setTitle(title);
    setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_PACKAGE_MANAGER));
    setMessage(message);

    Composite composite = new Composite((Composite)super.createDialogArea(parent), SWT.NONE);
    composite.setLayoutData(UIUtil.createGridData());
    composite.setLayout(new FillLayout(SWT.HORIZONTAL));

    SashForm sashForm = new SashForm(composite, SWT.SMOOTH);

    GridLayout packageLayout = new GridLayout(1, false);
    packageLayout.marginWidth = 0;
    packageLayout.marginHeight = 0;

    Composite packageComposite = new Composite(sashForm, SWT.NONE);
    packageComposite.setLayout(packageLayout);

    PackageContentProvider packageContentProvider = new PackageContentProvider();
    packageSearch = new SearchField(packageComposite, packageContentProvider)
    {
      @Override
      protected void finishFilter()
      {
        packageViewer.getControl().setFocus();
      }
    };

    packageSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    packageViewer = new TableViewer(packageComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
    packageViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    // Does NOT work with update(element): packageViewer.setUseHashlookup(true);
    packageViewer.setContentProvider(packageContentProvider);
    packageViewer.setLabelProvider(new PackageLabelProvider(packageViewer));
    packageViewer.setInput(registry);
    packageViewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged(SelectionChangedEvent event)
      {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        String nsURI = (String)selection.getFirstElement();
        EPackage ePackage = ((EPackage.Registry)packageViewer.getInput()).getEPackage(nsURI);
        classViewer.setInput(ePackage);

        try
        {
          packageViewer.update(nsURI, null); // Doe NOT work with hash lookup!!!
        }
        catch (Exception ex)
        {
          //$FALL-THROUGH$
        }
      }
    });

    GridLayout classLayout = new GridLayout(1, false);
    classLayout.marginWidth = 0;
    classLayout.marginHeight = 0;

    Composite classComposite = new Composite(sashForm, SWT.NONE);
    classComposite.setLayout(classLayout);

    ClassContentProvider classContentProvider = new ClassContentProvider();
    classSearch = new SearchField(classComposite, classContentProvider)
    {
      @Override
      protected void finishFilter()
      {
        classViewer.getControl().setFocus();
      }
    };

    classSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    classViewer = new TableViewer(classComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
    classViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    classViewer.setUseHashlookup(true);
    classViewer.setContentProvider(classContentProvider);
    classViewer.setLabelProvider(new ClassLabelProvider());
    classViewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged(SelectionChangedEvent event)
      {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        selectedClass = (EClass)selection.getFirstElement();

        updateOkButton();
      }
    });

    classViewer.addDoubleClickListener(new IDoubleClickListener()
    {
      @Override
      public void doubleClick(DoubleClickEvent event)
      {
        okPressed();
      }
    });

    sashForm.setWeights(new int[] { 2, 1 });
    return composite;
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    super.createButtonsForButtonBar(parent);
    updateOkButton();
  }

  private void updateOkButton()
  {
    Button button = getButton(IDialogConstants.OK_ID);
    if (button != null)
    {
      button.setEnabled(selectedClass != null);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class PackageContentProvider implements IStructuredContentProvider, FilterHandler
  {
    private Viewer viewer;

    private Object input;

    private String filter;

    private String[] elements;

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
      this.viewer = viewer;
      input = newInput;
      computeElements();
    }

    @Override
    public void handleFilter(String filter)
    {
      this.filter = filter == null ? null : filter.toLowerCase();
      computeElements();
      viewer.refresh();
    }

    @Override
    public Object[] getElements(Object inputElement)
    {
      return elements;
    }

    @Override
    public void dispose()
    {
      elements = null;
    }

    private void computeElements()
    {
      if (input instanceof EPackage.Registry)
      {
        final EPackage.Registry registry = (EPackage.Registry)input;

        List<String> nsURIs = new ArrayList<>();
        for (String nsURI : registry.keySet())
        {
          if (filter == null || nsURI.toLowerCase().contains(filter))
          {
            nsURIs.add(nsURI);
          }
        }

        elements = nsURIs.toArray(new String[nsURIs.size()]);
        Arrays.sort(elements);
      }
      else
      {
        elements = null;
      }

      if (elements == null)
      {
        elements = new String[0];
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class PackageLabelProvider extends LabelProvider
  {
    private static final Image PACKAGE_IMAGE = SharedIcons.getImage(SharedIcons.OBJ_EPACKAGE);

    private static final Image UNKNOWN_IMAGE = SharedIcons.getImage(SharedIcons.OBJ_EPACKAGE_UNKNOWN);

    private final Viewer viewer;

    public PackageLabelProvider(Viewer viewer)
    {
      this.viewer = viewer;
    }

    @Override
    public Image getImage(Object element)
    {
      String nsURI = (String)element;
      EPackage.Registry registry = (Registry)viewer.getInput();
      Object value = registry.get(nsURI);
      if (value instanceof EPackage)
      {
        return PACKAGE_IMAGE;
      }

      return UNKNOWN_IMAGE;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ClassContentProvider implements IStructuredContentProvider, FilterHandler
  {
    private Viewer viewer;

    private Object input;

    private String filter;

    private EClass[] elements;

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
      this.viewer = viewer;
      input = newInput;
      computeElements();
    }

    @Override
    public void handleFilter(String filter)
    {
      this.filter = filter == null ? null : filter.toLowerCase();
      computeElements();
      viewer.refresh();
    }

    @Override
    public Object[] getElements(Object inputElement)
    {
      return elements;
    }

    @Override
    public void dispose()
    {
      elements = null;
    }

    private void computeElements()
    {
      if (input instanceof EPackage)
      {
        EPackage ePackage = (EPackage)input;

        List<EClass> eClasses = new ArrayList<>();
        for (EClassifier eClassifier : ePackage.getEClassifiers())
        {
          if (eClassifier instanceof EClass)
          {
            EClass eClass = (EClass)eClassifier;
            if (!eClass.isAbstract() && !eClass.isInterface())
            {
              if (filter == null || eClass.getName().toLowerCase().contains(filter))
              {
                eClasses.add(eClass);
              }
            }
          }
        }

        elements = eClasses.toArray(new EClass[eClasses.size()]);
        Arrays.sort(elements, new Comparator<EClass>()
        {
          @Override
          public int compare(EClass c1, EClass c2)
          {
            return StringUtil.safe(c1.getName()).compareTo(StringUtil.safe(c2.getName()));
          }
        });
      }
      else
      {
        elements = null;
      }

      if (elements == null)
      {
        elements = new EClass[0];
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ClassLabelProvider extends LabelProvider
  {
    private static final Image CLASS_IMAGE = SharedIcons.getImage(SharedIcons.OBJ_ECLASS);

    private final ComposedAdapterFactory adapterFactory;

    private final ILabelProvider labelProvider;

    public ClassLabelProvider()
    {
      adapterFactory = CDOEditor.createAdapterFactory(true);
      labelProvider = new AdapterFactoryLabelProvider(adapterFactory);
    }

    @Override
    public void dispose()
    {
      labelProvider.dispose();
      adapterFactory.dispose();
      super.dispose();
    }

    @Override
    public Image getImage(Object element)
    {
      try
      {
        EClass eClass = (EClass)element;
        EObject eObject = EcoreUtil.create(eClass);
        Image image = labelProvider.getImage(eObject);
        if (image != null)
        {
          return image;
        }
      }
      catch (Exception ex)
      {
        //$FALL-THROUGH$
      }

      return CLASS_IMAGE;
    }

    @Override
    public String getText(Object element)
    {
      return ((EClass)element).getName();
    }
  }
}
