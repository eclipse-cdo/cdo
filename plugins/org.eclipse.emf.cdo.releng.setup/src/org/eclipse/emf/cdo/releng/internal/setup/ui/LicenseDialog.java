/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup.ui;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;

import org.eclipse.equinox.internal.p2.metadata.License;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.ILicense;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LicenseDialog extends AbstractSetupDialog
{
  private static final String LIST_WEIGHT = "ListSashWeight"; //$NON-NLS-1$

  private static final String LICENSE_WEIGHT = "LicenseSashWeight"; //$NON-NLS-1$

  private static final int PRIMARY_COLUMN_WIDTH = 60;

  private static final int COLUMN_WIDTH = 40;

  private static final int TABLE_HEIGHT = 10;

  private Map<ILicense, List<IInstallableUnit>> licensesToIUs;

  private SashForm sashForm;

  private TreeViewer viewer;

  private Text licenseTextBox;

  private boolean rememberAcceptedLicenses;

  public LicenseDialog(Shell parentShell, Map<ILicense, List<IInstallableUnit>> licensesToIUs)
  {
    super(parentShell, "Review Licenses", 1000, 600);
    this.licensesToIUs = licensesToIUs;
  }

  public boolean isRememberAcceptedLicenses()
  {
    return rememberAcceptedLicenses;
  }

  // @Override
  // protected int getContainerMargin()
  // {
  // return 10;
  // }

  @Override
  protected String getDefaultMessage()
  {
    return "Licenses must be reviewed and accepted before the software can be installed.";
  }

  @Override
  protected void createUI(Composite parent)
  {
    initializeDialogUnits(parent);

    List<IInstallableUnit> ius;
    Control control;

    if (licensesToIUs.size() == 1 && (ius = licensesToIUs.values().iterator().next()).size() == 1)
    {
      control = createLicenseContentSection(parent, ius.get(0));
    }
    else
    {
      sashForm = new SashForm(parent, SWT.HORIZONTAL);
      sashForm.setLayout(new GridLayout());
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      sashForm.setLayoutData(gd);

      createLicenseListSection(sashForm);
      createLicenseContentSection(sashForm, null);
      sashForm.setWeights(getSashWeights());

      control = sashForm;
    }

    Dialog.applyDialogFont(control);
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    final Button checkbox = createCheckbox(parent, "Remember accepted licenses");
    checkbox.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        rememberAcceptedLicenses = checkbox.getSelection();
      }
    });

    createButton(parent, IDialogConstants.OK_ID, "Accept", false);
    createButton(parent, IDialogConstants.CANCEL_ID, "Decline", true);
  }

  private void createLicenseListSection(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    composite.setLayout(layout);
    GridData gd = new GridData(GridData.FILL_BOTH);
    composite.setLayoutData(gd);

    // Label label = new Label(composite, SWT.NONE);
    // label.setText("&Licenses:");
    viewer = new TreeViewer(composite, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.setContentProvider(new LicenseContentProvider());
    viewer.setLabelProvider(new LicenseLabelProvider());
    viewer.setComparator(new ViewerComparator());
    viewer.setInput(licensesToIUs);

    viewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      public void selectionChanged(SelectionChangedEvent event)
      {
        handleSelectionChanged((IStructuredSelection)event.getSelection());
      }
    });

    gd = new GridData(GridData.FILL_BOTH);
    gd.widthHint = convertWidthInCharsToPixels(PRIMARY_COLUMN_WIDTH);
    gd.heightHint = convertHeightInCharsToPixels(TABLE_HEIGHT);

    viewer.getControl().setLayoutData(gd);
  }

  private Composite createLicenseContentSection(Composite parent, IInstallableUnit singleIU)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    composite.setLayout(layout);
    GridData gd = new GridData(GridData.FILL_BOTH);
    composite.setLayoutData(gd);

    // Label label = new Label(composite, SWT.NONE);
    // if (singleIU == null)
    // {
    // label.setText("License &text:");
    // }
    // else
    // {
    // label.setText(NLS.bind("License &text (for {0}):", getIUName(singleIU)));
    // }

    licenseTextBox = new Text(composite, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);
    licenseTextBox.setBackground(licenseTextBox.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
    initializeDialogUnits(licenseTextBox);
    gd = new GridData(SWT.FILL, SWT.FILL, true, true);
    gd.heightHint = convertHeightInCharsToPixels(TABLE_HEIGHT);
    gd.widthHint = convertWidthInCharsToPixels(COLUMN_WIDTH);
    licenseTextBox.setLayoutData(gd);

    if (singleIU != null)
    {
      String licenseBody = ""; //$NON-NLS-1$

      Iterator<ILicense> licenses = singleIU.getLicenses(null).iterator();
      ILicense license = licenses.hasNext() ? licenses.next() : null;
      if (license != null && license.getBody() != null)
      {
        licenseBody = license.getBody();
      }

      licenseTextBox.setText(licenseBody);
    }

    return composite;
  }

  private void handleSelectionChanged(IStructuredSelection selection)
  {
    if (!selection.isEmpty())
    {
      Object selected = selection.getFirstElement();
      if (selected instanceof License)
      {
        licenseTextBox.setText(((License)selected).getBody());
      }
      else if (selected instanceof IUWithLicenseParent)
      {
        licenseTextBox.setText(((IUWithLicenseParent)selected).license.getBody());
      }
    }
  }

  private int[] getSashWeights()
  {
    IDialogSettings settings = Activator.getDefault().getDialogSettings();
    IDialogSettings section = settings.getSection(getDialogSettingsName());
    if (section != null)
    {
      try
      {
        int[] weights = new int[2];
        if (section.get(LIST_WEIGHT) != null)
        {
          weights[0] = section.getInt(LIST_WEIGHT);
          if (section.get(LICENSE_WEIGHT) != null)
          {
            weights[1] = section.getInt(LICENSE_WEIGHT);
            return weights;
          }
        }
      }
      catch (NumberFormatException ex)
      {
        // Ignore if there actually was a value that didn't parse.
      }
    }

    return new int[] { 50, 50 };
  }

  private String getDialogSettingsName()
  {
    return getClass().getName();
  }

  private static String getIUName(IInstallableUnit iu)
  {
    StringBuffer buf = new StringBuffer();
    String name = iu.getProperty(IInstallableUnit.PROP_NAME, null);
    if (name != null)
    {
      buf.append(name);
    }
    else
    {
      buf.append(iu.getId());
    }
    buf.append(" "); //$NON-NLS-1$
    buf.append(iu.getVersion().toString());
    return buf.toString();
  }

  class IUWithLicenseParent
  {
    IInstallableUnit iu;

    ILicense license;

    IUWithLicenseParent(ILicense license, IInstallableUnit iu)
    {
      this.license = license;
      this.iu = iu;
    }
  }

  class LicenseContentProvider implements ITreeContentProvider
  {
    public Object[] getChildren(Object parentElement)
    {
      if (!(parentElement instanceof ILicense))
      {
        return new Object[0];
      }

      if (licensesToIUs.containsKey(parentElement))
      {
        List<IInstallableUnit> iusWithLicense = licensesToIUs.get(parentElement);
        IInstallableUnit[] ius = iusWithLicense.toArray(new IInstallableUnit[iusWithLicense.size()]);
        IUWithLicenseParent[] children = new IUWithLicenseParent[ius.length];
        for (int i = 0; i < ius.length; i++)
        {
          children[i] = new IUWithLicenseParent((ILicense)parentElement, ius[i]);
        }
        return children;
      }
      return null;
    }

    public Object getParent(Object element)
    {
      if (element instanceof IUWithLicenseParent)
      {
        return ((IUWithLicenseParent)element).license;
      }
      return null;
    }

    public boolean hasChildren(Object element)
    {
      return licensesToIUs.containsKey(element);
    }

    public Object[] getElements(Object inputElement)
    {
      return licensesToIUs.keySet().toArray();
    }

    public void dispose()
    {
      // Nothing to do
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
      // Nothing to do
    }
  }

  class LicenseLabelProvider extends LabelProvider
  {
    @Override
    public Image getImage(Object element)
    {
      return null;
    }

    @Override
    public String getText(Object element)
    {
      if (element instanceof License)
      {
        return getFirstLine(((License)element).getBody());
      }
      else if (element instanceof IUWithLicenseParent)
      {
        return getIUName(((IUWithLicenseParent)element).iu);
      }
      else if (element instanceof IInstallableUnit)
      {
        return getIUName((IInstallableUnit)element);
      }
      return ""; //$NON-NLS-1$
    }

    private String getFirstLine(String body)
    {
      int i = body.indexOf('\n');
      int j = body.indexOf('\r');
      if (i > 0)
      {
        if (j > 0)
        {
          return body.substring(0, i < j ? i : j);
        }
        return body.substring(0, i);
      }
      else if (j > 0)
      {
        return body.substring(0, j);
      }
      return body;
    }
  }
}
