/*
 * Copyright (c) 2009-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.rcp.internal.table;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ref.ReferenceValueMap;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;

import com.swtdesigner.ResourceManager;
import com.swtdesigner.SWTResourceManager;

import org.gastro.business.BusinessDay;
import org.gastro.business.BusinessFactory;
import org.gastro.business.Order;
import org.gastro.business.OrderDetail;
import org.gastro.inventory.MenuCard;
import org.gastro.inventory.Offering;
import org.gastro.inventory.Section;
import org.gastro.rcp.IModel;
import org.gastro.rcp.IModel.ITransactionalOperation;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class TableView extends ViewPart
{
  public static final String ID = "org.gastro.rcp.table.view";

  private static final AdapterFactory FACTORY = IModel.INSTANCE.getAdapterFactory();

  private static final Map<String, String> fakeImages = new ReferenceValueMap.Soft<>();

  private static int fakeImageID;

  private TreeViewer menuViewer;

  private Label menuTitle;

  private Label menuDescription;

  private Label menuImage;

  private Label menuPrice;

  private Label quantity;

  private Button buttonDelete;

  private Button buttonAdd;

  private EObject currentItem;

  private Adapter currentItemAdapter = new AdapterImpl()
  {
    @Override
    public void notifyChanged(Notification msg)
    {
      try
      {
        menuViewer.getTree().getDisplay().asyncExec(new Runnable()
        {
          @Override
          public void run()
          {
            try
            {
              showMenuCard();
            }
            catch (Exception ex)
            {
            }
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
  };

  private Order order;

  private OrderDetail orderDetail;

  private Adapter businessDayAdapter = new AdapterImpl()
  {
    @Override
    public void notifyChanged(Notification msg)
    {
      try
      {
        menuViewer.getTree().getDisplay().asyncExec(new Runnable()
        {
          @Override
          public void run()
          {
            try
            {
              menuViewer.setInput(IModel.INSTANCE.getBusinessDay().getMenuCard());
            }
            catch (Exception ex)
            {
            }
          }
        });
      }
      catch (Exception ex)
      {
      }
    }
  };

  private TableViewer orderViewer;

  public TableView()
  {
  }

  /**
   * Passing the focus request to the viewer's control.
   */
  @Override
  public void setFocus()
  {
    // Do nothing
  }

  @Override
  public void dispose()
  {
    IModel.INSTANCE.getBusinessDay().eAdapters().remove(businessDayAdapter);
    if (currentItem != null)
    {
      currentItem.eAdapters().remove(currentItemAdapter);
    }

    super.dispose();
  }

  /**
   * This is a callback that will allow us to create the viewer and initialize it.
   */
  @Override
  public void createPartControl(Composite parent)
  {
    BusinessDay businessDay = IModel.INSTANCE.getBusinessDay();
    businessDay.eAdapters().add(businessDayAdapter);

    parent.setLayout(new FillLayout(SWT.VERTICAL));
    {
      PShelf shelf = new PShelf(parent, SWT.NONE);
      shelf.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
      shelf.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
      shelf.setFont(SWTResourceManager.getFont("Comic Sans MS", 24, SWT.BOLD));
      shelf.setRenderer(new org.eclipse.nebula.widgets.pshelf.RedmondShelfRenderer());
      {
        PShelfItem shelfItem = new PShelfItem(shelf, SWT.NONE);
        GridLayout gridLayout = new GridLayout(3, false);
        gridLayout.horizontalSpacing = 0;
        gridLayout.marginHeight = 0;
        gridLayout.verticalSpacing = 0;
        gridLayout.marginWidth = 0;
        shelfItem.getBody().setLayout(gridLayout);
        {
          menuViewer = new TreeViewer(shelfItem.getBody(), SWT.NONE);
          menuViewer.addSelectionChangedListener(new ISelectionChangedListener()
          {
            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
              EObject item = (EObject)((IStructuredSelection)event.getSelection()).getFirstElement();
              if (item != currentItem)
              {
                if (currentItem != null)
                {
                  currentItem.eAdapters().remove(currentItemAdapter);
                }

                if (item != null)
                {
                  item.eAdapters().add(currentItemAdapter);
                }

                currentItem = item;
                showMenuCard();
              }
            }
          });

          menuViewer.setContentProvider(new AdapterFactoryContentProvider(FACTORY));
          menuViewer.setLabelProvider(new AdapterFactoryLabelProvider(FACTORY)
          {
            @Override
            public Image getImage(Object object)
            {
              return null;
            }
          });

          menuViewer.setInput(businessDay.getMenuCard());

          Tree tree = menuViewer.getTree();
          tree.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
          tree.setFont(SWTResourceManager.getFont("Comic Sans MS", 16, SWT.BOLD));
          {
            GridData gridData = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
            gridData.widthHint = 300;
            tree.setLayoutData(gridData);
          }
        }

        {
          Composite composite = new Composite(shelfItem.getBody(), SWT.NONE);
          composite.setLayout(new GridLayout(1, false));
          composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
          composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
          {
            menuTitle = new Label(composite, SWT.NONE);
            menuTitle.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
            menuTitle.setFont(SWTResourceManager.getFont("Comic Sans MS", 16, SWT.BOLD));
            menuTitle.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
            menuTitle.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
            menuTitle.setText("Titel");
          }

          {
            Label label = new Label(composite, SWT.SEPARATOR);
            {
              GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
              gridData.heightHint = 2;
              label.setLayoutData(gridData);
            }
          }

          {
            menuDescription = new Label(composite, SWT.WRAP);
            menuDescription.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
            menuDescription.setFont(SWTResourceManager.getFont("Comic Sans MS", 16, SWT.BOLD));
            menuDescription.setText("Beschreibung");
            menuDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            menuDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
          }

          {
            Composite composite_1 = new Composite(composite, SWT.NONE);
            composite_1.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
            composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
            GridLayout gridLayout_1 = new GridLayout(2, false);
            gridLayout_1.marginWidth = 0;
            gridLayout_1.marginHeight = 0;
            composite_1.setLayout(gridLayout_1);
            {
              menuImage = new Label(composite_1, SWT.NONE);
              menuImage.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1));
              menuImage.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
              menuImage.setText("Bild");
              menuImage.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
              menuImage.setFont(SWTResourceManager.getFont("Comic Sans MS", 16, SWT.BOLD));
            }

            {
              menuPrice = new Label(composite_1, SWT.NONE);
              menuPrice.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
              menuPrice.setAlignment(SWT.RIGHT);
              menuPrice.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
              menuPrice.setFont(SWTResourceManager.getFont("Comic Sans MS", 16, SWT.BOLD));
              menuPrice.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
              menuPrice.setText("Preis");
            }
          }
        }

        {
          Composite composite = new Composite(shelfItem.getBody(), SWT.NONE);
          composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
          GridLayout gridLayout_1 = new GridLayout(1, false);
          gridLayout_1.marginHeight = 0;
          gridLayout_1.marginWidth = 0;
          composite.setLayout(gridLayout_1);
          composite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
          {
            Button buttonPrev = new Button(composite, SWT.NONE);
            buttonPrev.setToolTipText("Voriger Eintrag");
            buttonPrev.addSelectionListener(new SelectionAdapter()
            {
              @Override
              public void widgetSelected(SelectionEvent e)
              {
                prevMenu();
              }
            });

            buttonPrev.setImage(ResourceManager.getPluginImage("org.gastro.rcp.table", "icons/up.png"));
          }

          {
            Label label = new Label(composite, SWT.NONE);
            label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
          }

          {
            quantity = new Label(composite, SWT.NONE);
            quantity.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
            quantity.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
            quantity.setAlignment(SWT.CENTER);
            quantity.setFont(SWTResourceManager.getFont("Comic Sans MS", 32, SWT.BOLD));
            quantity.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
            quantity.setText("0");
          }

          {
            Composite composite_1 = new Composite(composite, SWT.NONE);
            composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
            composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
            FillLayout fillLayout = new FillLayout(SWT.HORIZONTAL);
            fillLayout.spacing = 5;
            composite_1.setLayout(fillLayout);
            {
              buttonDelete = new Button(composite_1, SWT.NONE);
              buttonDelete.setToolTipText("Weniger bestellen");
              buttonDelete.addSelectionListener(new SelectionAdapter()
              {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                  deleteOrder();
                }
              });

              buttonDelete.setImage(ResourceManager.getPluginImage("org.gastro.rcp.table", "icons/delete.gif"));
            }

            {
              buttonAdd = new Button(composite_1, SWT.NONE);
              buttonAdd.setToolTipText("Mehr bestellen");
              buttonAdd.setImage(ResourceManager.getPluginImage("org.gastro.rcp.table", "icons/add.gif"));
              buttonAdd.addSelectionListener(new SelectionAdapter()
              {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                  addOrder();
                }
              });
            }
          }

          {
            Label label = new Label(composite, SWT.NONE);
            label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
          }

          {
            Button buttonNext = new Button(composite, SWT.NONE);
            buttonNext.setToolTipText("N\u00E4chster Eintrag");
            buttonNext.addSelectionListener(new SelectionAdapter()
            {
              @Override
              public void widgetSelected(SelectionEvent e)
              {
                nextMenu();
              }
            });

            buttonNext.setImage(ResourceManager.getPluginImage("org.gastro.rcp.table", "icons/down.png"));
          }
        }

        shelfItem.setImage(ResourceManager.getPluginImage("org.gastro.rcp.table", "icons/menucard.png"));
        shelfItem.setText(" Speisen und Getr\u00E4nke");
      }

      {
        PShelfItem shelfItem = new PShelfItem(shelf, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.horizontalSpacing = 0;
        gridLayout.verticalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        shelfItem.getBody().setLayout(gridLayout);
        {
          orderViewer = new TableViewer(shelfItem.getBody(), SWT.HIDE_SELECTION);

          Table table = orderViewer.getTable();
          table.setLinesVisible(true);
          table.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
          table.setFont(SWTResourceManager.getFont("Comic Sans MS", 16, SWT.BOLD));
          {
            GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
            gridData.widthHint = 300;
            table.setLayoutData(gridData);
          }

          {
            TableViewerColumn tableViewerColumn = new TableViewerColumn(orderViewer, SWT.NONE);
            TableColumn tblclmnQuantity = tableViewerColumn.getColumn();
            tblclmnQuantity.setResizable(false);
            tblclmnQuantity.setAlignment(SWT.RIGHT);
            tblclmnQuantity.setWidth(70);
            tblclmnQuantity.setText("Quantity");
          }

          {
            TableViewerColumn tableViewerColumn = new TableViewerColumn(orderViewer, SWT.NONE);
            TableColumn tblclmnOffering = tableViewerColumn.getColumn();
            tblclmnOffering.setWidth(410);
            tblclmnOffering.setText("Offering");
          }

          {
            TableViewerColumn tableViewerColumn = new TableViewerColumn(orderViewer, SWT.NONE);
            TableColumn tblclmnPrice = tableViewerColumn.getColumn();
            tblclmnPrice.setResizable(false);
            tblclmnPrice.setAlignment(SWT.RIGHT);
            tblclmnPrice.setWidth(100);
            tblclmnPrice.setText("Price");
          }

          {
            TableViewerColumn tableViewerColumn = new TableViewerColumn(orderViewer, SWT.NONE);
            TableColumn tblclmnSum = tableViewerColumn.getColumn();
            tblclmnSum.setResizable(false);
            tblclmnSum.setAlignment(SWT.RIGHT);
            tblclmnSum.setWidth(117);
            tblclmnSum.setText("Sum");
          }

          orderViewer.setContentProvider(new AdapterFactoryContentProvider(FACTORY));
          orderViewer.setLabelProvider(new AdapterFactoryLabelProvider(FACTORY)
          {
            @Override
            public String getColumnText(Object object, int columnIndex)
            {
              switch (columnIndex)
              {
              case 0:
                return "" + ((OrderDetail)object).getQuantity() + "x";
              case 1:
                return ((OrderDetail)object).getOffering().getName();
              case 2:
                return formatPrice(((OrderDetail)object).getOffering().getPrice());
              case 3:
                return formatPrice(((OrderDetail)object).getPrice());

              default:
                return super.getColumnText(object, columnIndex);
              }
            }

            @Override
            public Image getColumnImage(Object object, int columnIndex)
            {
              return null;
            }
          });
        }

        {
          Composite composite = new Composite(shelfItem.getBody(), SWT.NONE);
          composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
          composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
          GridLayout gridLayout_1 = new GridLayout(1, false);
          gridLayout_1.marginHeight = 0;
          gridLayout_1.marginWidth = 0;
          composite.setLayout(gridLayout_1);
          composite.setBounds(0, 0, 64, 64);
          {
            Button buttonCancel = new Button(composite, SWT.NONE);
            buttonCancel.addSelectionListener(new SelectionAdapter()
            {
              @Override
              public void widgetSelected(SelectionEvent e)
              {
                cancelOrder();
              }
            });

            buttonCancel.setToolTipText("Bestellung abbrechen");
            buttonCancel.setImage(ResourceManager.getPluginImage("org.gastro.rcp.table", "icons/cancel.png"));
          }

          {
            Label labelTotalPrice = new Label(composite, SWT.NONE);
            labelTotalPrice.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
            labelTotalPrice.setAlignment(SWT.CENTER);
            labelTotalPrice.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true, 1, 1));
          }

          {
            Button buttonSend = new Button(composite, SWT.NONE);
            buttonSend.addSelectionListener(new SelectionAdapter()
            {
              @Override
              public void widgetSelected(SelectionEvent e)
              {
                sendOrder();
              }
            });

            buttonSend.setToolTipText("Bestellung absenden");
            buttonSend.setImage(ResourceManager.getPluginImage("org.gastro.rcp.table", "icons/send.png"));
          }
        }

        shelfItem.setImage(ResourceManager.getPluginImage("org.gastro.rcp.table", "icons/order.png"));
        shelfItem.setText(" Bestellung");
      }

      {
        PShelfItem shelfItem = new PShelfItem(shelf, SWT.NONE);
        shelfItem.setImage(ResourceManager.getPluginImage("org.gastro.rcp.table", "icons/payment.png"));
        shelfItem.setText(" Bezahlung");
      }

      {
        PShelfItem shelfItem = new PShelfItem(shelf, SWT.NONE);
        shelfItem.setImage(ResourceManager.getPluginImage("org.gastro.rcp.table", "icons/service.png"));
        shelfItem.setText(" Service");
      }

      shelf.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          orderViewer.refresh(true);
        }
      });

      createNewOrder();
    }
  }

  protected void nextMenu()
  {
    EList<EObject> menuItems = getAllMenuItems();
    int index = menuItems.indexOf(currentItem);
    if (index >= 0 && index < menuItems.size() - 1)
    {
      menuViewer.setSelection(new StructuredSelection(menuItems.get(index + 1)));
    }
  }

  protected void prevMenu()
  {
    EList<EObject> menuItems = getAllMenuItems();
    int index = menuItems.indexOf(currentItem);
    if (index > 0)
    {
      menuViewer.setSelection(new StructuredSelection(menuItems.get(index - 1)));
    }
  }

  protected void showMenuCard()
  {
    OrderDetail oldOrderDetail = orderDetail;
    if (currentItem instanceof Section)
    {
      Section section = (Section)currentItem;
      updateMenuDetail( //
          section.getTitle(), //
          section.getText(), //
          null, //
          false);
      orderDetail = null;
    }
    else if (currentItem instanceof Offering)
    {
      Offering offering = (Offering)currentItem;
      updateMenuDetail( //
          offering.getName(), //
          offering.getDescription(), //
          formatPrice(offering.getPrice()), //
          true);
      orderDetail = getOrderDetail(offering);
    }

    if (oldOrderDetail != null && oldOrderDetail != orderDetail)
    {
      if (oldOrderDetail.getQuantity() == 0)
      {
        order.getOrderDetails().remove(oldOrderDetail);
      }
    }

    updateOrderDetail();
  }

  protected void addOrder()
  {
    if (orderDetail == null)
    {
      orderDetail = BusinessFactory.eINSTANCE.createOrderDetail();
      orderDetail.setOffering((Offering)currentItem);
      orderDetail.setQuantity(1);
      order.getOrderDetails().add(orderDetail);
    }
    else
    {
      orderDetail.setQuantity(orderDetail.getQuantity() + 1);
    }

    updateOrderDetail();
  }

  protected void deleteOrder()
  {
    int quantity = orderDetail.getQuantity();
    if (quantity == 1)
    {
      order.getOrderDetails().remove(orderDetail);
      orderDetail = null;
    }
    else
    {
      orderDetail.setQuantity(quantity - 1);
    }

    updateOrderDetail();
  }

  protected void sendOrder()
  {
    ITransactionalOperation<BusinessDay> operation = new ITransactionalOperation<BusinessDay>()
    {
      @Override
      public Object execute(BusinessDay businessDay)
      {
        businessDay.cdoWriteLock().lock();
        EList<Order> orders = businessDay.getOrders();
        order.setNumber(getNextOrderNumber(orders));
        orders.add(order);
        return null;
      }
    };

    IModel.INSTANCE.modify(IModel.INSTANCE.getBusinessDay(), operation);
    createNewOrder();
  }

  protected void cancelOrder()
  {
    createNewOrder();
  }

  private EList<EObject> getAllMenuItems()
  {
    MenuCard menuCard = (MenuCard)menuViewer.getInput();
    EList<EObject> result = new BasicEList<>();
    for (Iterator<EObject> it = menuCard.eAllContents(); it.hasNext();)
    {
      result.add(it.next());
    }

    return result;
  }

  private void createNewOrder()
  {
    order = BusinessFactory.eINSTANCE.createOrder();
    order.setTable((org.gastro.inventory.Table)IModel.INSTANCE.getStation());
    orderViewer.setInput(order);

    orderDetail = null;
    updateOrderDetail();
  }

  private int getNextOrderNumber(EList<Order> orders)
  {
    int count = orders.size();
    if (count > 0)
    {
      return orders.get(count - 1).getNumber() + 1;
    }

    return 1;
  }

  private OrderDetail getOrderDetail(Offering offering)
  {
    for (OrderDetail orderDetail : order.getOrderDetails())
    {
      if (orderDetail.getOffering() == offering)
      {
        return orderDetail;
      }
    }

    return null;
  }

  private void updateMenuDetail(String title, String description, String price, boolean withImage)
  {
    menuTitle.setText(StringUtil.safe(title));
    menuDescription.setText(StringUtil.safe(description));
    menuPrice.setText(StringUtil.safe(price));

    if (title == null || !withImage)
    {
      menuImage.setImage(null);
    }
    else
    {
      String name = fakeImages.get(title);
      if (name == null)
      {
        for (;;)
        {
          name = "meal-" + ++fakeImageID;
          Image image = getCachedImage(name);
          if (image != null)
          {
            fakeImages.put(title, name);
            menuImage.setImage(getCachedImage(name));
            break;
          }

          fakeImageID = 0;
        }
      }
      else
      {
        menuImage.setImage(getCachedImage(name));
      }
    }

    menuImage.getParent().getParent().layout(true);
  }

  private void updateOrderDetail()
  {
    if (currentItem instanceof Offering)
    {
      if (orderDetail != null)
      {
        quantity.setText("" + orderDetail.getQuantity());
        buttonDelete.setEnabled(true);
      }
      else
      {
        quantity.setText("0");
        buttonDelete.setEnabled(false);
      }

      quantity.setVisible(true);
      buttonAdd.setVisible(true);
      buttonDelete.setVisible(true);
    }
    else
    {
      quantity.setVisible(false);
      buttonAdd.setVisible(false);
      buttonDelete.setVisible(false);
    }
  }

  private Image getCachedImage(String name)
  {
    return ResourceManager.getPluginImage("org.gastro.rcp.table", "images/" + name + ".png");
  }

  private String formatPrice(float price)
  {
    return NumberFormat.getCurrencyInstance().format(price);
  }
}
