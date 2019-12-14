/*
 * Copyright (c) 2009-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.rcp.internal.department;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider.FontAndColorProvider;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

import com.swtdesigner.ResourceManager;
import com.swtdesigner.SWTResourceManager;

import org.gastro.business.BusinessDay;
import org.gastro.business.Order;
import org.gastro.business.OrderDetail;
import org.gastro.business.OrderState;
import org.gastro.inventory.Department;
import org.gastro.inventory.Product;
import org.gastro.rcp.IModel;
import org.gastro.rcp.IModel.ITransactionalOperation;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class OrdersView extends ViewPart
{
  public static final String ID = "org.gastro.rcp.orders.view";

  private static final AdapterFactory FACTORY = IModel.INSTANCE.getAdapterFactory();

  private TreeViewer treeViewer;

  private Adapter businessDayAdapter = new AdapterImpl()
  {
    @Override
    public void notifyChanged(Notification msg)
    {
      try
      {
        treeViewer.getTree().getDisplay().asyncExec(new Runnable()
        {
          @Override
          public void run()
          {
            try
            {
              treeViewer.refresh(true);
              treeViewer.expandAll();
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

  public OrdersView()
  {
  }

  /**
   * Passing the focus request to the viewer's control.
   */
  @Override
  public void setFocus()
  {
  }

  @Override
  public void dispose()
  {
    IModel.INSTANCE.getBusinessDay().eAdapters().remove(businessDayAdapter);
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

    {
      treeViewer = new TreeViewer(parent, SWT.NONE);
      treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
      {
        @Override
        public void selectionChanged(SelectionChangedEvent event)
        {
          Object object = ((IStructuredSelection)event.getSelection()).getFirstElement();
          if (object instanceof OrderDetail)
          {
            OrderDetail orderDetail = (OrderDetail)object;
            IModel.INSTANCE.modify(orderDetail, new ITransactionalOperation<OrderDetail>()
            {
              @Override
              public Object execute(OrderDetail orderDetail)
              {
                orderDetail.setState(orderDetail.getState() == OrderState.ORDERED ? OrderState.SERVED : OrderState.ORDERED);
                return null;
              }
            });

            treeViewer.refresh(true);
          }
        }
      });

      Tree tree = treeViewer.getTree();
      tree.setToolTipText("Klicken um Zustand zu wechseln");
      tree.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
      tree.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
      {
        TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        TreeColumn trclmnTitel = treeViewerColumn.getColumn();
        trclmnTitel.setToolTipText("Klicken um Zustand zu wechseln");
        trclmnTitel.setWidth(400);
        trclmnTitel.setText("Angebot");
      }

      {
        TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        TreeColumn trclmnQuantity = treeViewerColumn.getColumn();
        trclmnQuantity.setResizable(false);
        trclmnQuantity.setAlignment(SWT.RIGHT);
        trclmnQuantity.setWidth(50);
        trclmnQuantity.setText("Menge");
      }

      {
        TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        TreeColumn trclmnPreis = treeViewerColumn.getColumn();
        trclmnPreis.setAlignment(SWT.RIGHT);
        trclmnPreis.setResizable(false);
        trclmnPreis.setWidth(100);
        trclmnPreis.setText("Preis");
      }

      {
        TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        TreeColumn trclmnSumme = treeViewerColumn.getColumn();
        trclmnSumme.setAlignment(SWT.RIGHT);
        trclmnSumme.setResizable(false);
        trclmnSumme.setWidth(100);
        trclmnSumme.setText("Summe");
      }

      treeViewer.setContentProvider(new AdapterFactoryContentProvider(FACTORY)
      {
        @Override
        public Object[] getElements(Object object)
        {
          return getChildren(object);
        }

        @Override
        public Object[] getChildren(Object object)
        {
          Department department = (Department)IModel.INSTANCE.getStation();
          if (object instanceof BusinessDay)
          {
            BusinessDay businessDay = (BusinessDay)object;
            List<Object> result = new ArrayList<>();
            for (Order order : businessDay.getOrders())
            {
              for (OrderDetail orderDetail : order.getOrderDetails())
              {
                if (orderDetail.getState() == OrderState.ORDERED)
                {
                  Product product = orderDetail.getOffering().getProduct();
                  if (product != null)
                  {
                    if (product.getDepartment() != department)
                    {
                      continue;
                    }
                  }

                  result.add(order);
                  break;
                }
              }
            }

            return result.toArray();
          }

          if (object instanceof Order)
          {
            Order order = (Order)object;
            List<Object> result = new ArrayList<>();
            for (OrderDetail orderDetail : order.getOrderDetails())
            {
              Product product = orderDetail.getOffering().getProduct();
              if (product == null || product.getDepartment() == department)
              {
                result.add(orderDetail);
              }
            }

            return result.toArray();
          }

          return super.getChildren(object);
        }

        @Override
        public boolean hasChildren(Object object)
        {
          return getChildren(object).length != 0;
        }
      });

      treeViewer.setLabelProvider(new FontAndColorProvider(FACTORY, treeViewer)
      {
        @Override
        public String getColumnText(Object object, int columnIndex)
        {
          if (object instanceof Order)
          {
            Order order = (Order)object;
            switch (columnIndex)
            {
            case 0:
              return order.getTable().getStationID();
            case 1:
            case 2:
            case 3:
              return "";
            }
          }

          if (object instanceof OrderDetail)
          {
            OrderDetail orderDetail = (OrderDetail)object;
            switch (columnIndex)
            {
            case 0:
              return orderDetail.getOffering().getName();
            case 1:
              return "" + orderDetail.getQuantity() + "x";
            case 2:
              return formatPrice(orderDetail.getOffering().getPrice());
            case 3:
              return formatPrice(orderDetail.getOffering().getPrice() * orderDetail.getQuantity());
            }
          }

          return super.getColumnText(object, columnIndex);
        }

        @Override
        public Image getColumnImage(Object object, int columnIndex)
        {
          if (columnIndex == 0 && object instanceof OrderDetail)
          {
            OrderDetail orderDetail = (OrderDetail)object;
            if (orderDetail.getState() == OrderState.ORDERED)
            {
              return ResourceManager.getPluginImage("org.gastro.rcp.department", "icons/ordered.gif");
            }

            return ResourceManager.getPluginImage("org.gastro.rcp.department", "icons/served.gif");
          }

          return null;
        }

        @Override
        public Color getForeground(Object object, int columnIndex)
        {
          if (object instanceof OrderDetail)
          {
            OrderDetail orderDetail = (OrderDetail)object;
            if (orderDetail.getState() != OrderState.ORDERED)
            {
              return treeViewer.getTree().getDisplay().getSystemColor(SWT.COLOR_GRAY);
            }
          }

          return super.getForeground(object, columnIndex);
        }

        private String formatPrice(float price)
        {
          return NumberFormat.getCurrencyInstance().format(price);
        }
      });

      treeViewer.setInput(businessDay);
      treeViewer.expandAll();
    }
  }
}
