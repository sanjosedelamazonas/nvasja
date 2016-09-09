package org.sanjose.helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.data.jpa.repository.JpaRepository;

/*import org.sanjose.model.CentroCosto;
import org.sanjose.model.Cuenta;
import org.sanjose.model.CuentaContable;
import org.sanjose.model.LugarGasto;
import org.sanjose.model.Operacion.Tipo;
import org.sanjose.model.RubroInstitucional;
import org.sanjose.model.RubroProyecto;
import org.sanjose.web.helper.IOperacionTable;
*/
/*import com.vaadin.addon.BeanItemContainer.EntityItem;
import com.vaadin.addon.BeanItemContainer.BeanItemContainer;
import com.vaadin.addon.BeanItemContainer.BeanItemContainerFactory;
import com.vaadin.addon.BeanItemContainer.filter.Filters;
*/
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;

public class DataFilterUtil {

	static final Logger logger = Logger.getLogger(DataFilterUtil.class
			.getName());	

/*	public static void bindSearchTextField(final TextField tf, final String column, final String prompt, final BeanItemContainer container, 
			final HashMap<String, Filter> appliedFilters, HashMap<String, Property.ValueChangeListener> filterListeners) {
		bindSearchTextField(tf, column, prompt, container, appliedFilters, filterListeners, null);
	}*/
/*	
	public static void bindSearchTextField(final TextField tf, final String column, final String prompt, final BeanItemContainer container, 
			final HashMap<String, Filter> appliedFilters, HashMap<String, Property.ValueChangeListener> filterListeners, final IOperacionTable ot) {
		tf.setInputPrompt(prompt);
		tf.setImmediate(true);
		tf.removeListener(filterListeners.get(column));
		ValueChangeListener l = new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				logger.fine("Filters on " + column + " before: " + appliedFilters);
				logger.fine("Filters on " + column + " before: " + container.getAppliedFilters());
				if (event != null && event.getProperty() != null
						&& event.getProperty().getValue() != null && 
						!event.getProperty().getValue().toString().equals("")) {
					if (appliedFilters.containsKey(column)) {
						if (container.getFilters()!=null  
								&& container.getFilters().contains(appliedFilters.get(column))) 
							container.removeContainerFilter(appliedFilters.get(column));
						appliedFilters.remove(appliedFilters.get(column));
					}
					String filterString = "%" + (String)event.getProperty().getValue() + "%";
					Filter f = Filters.like(column, filterString, false);
					logger.fine("Created search filter: " + f);
					if (container!=null && (container.getFilters()==null || !container.getFilters().contains(f))) { 
						container.addContainerFilter(f);
						container.applyFilters();
						appliedFilters.put(column, f);
					}
				} else {
					tf.setInputPrompt(prompt);
					if (appliedFilters.containsKey(column)) {
						if (container.getFilters()!=null  
								&& container.getFilters().contains(appliedFilters.get(column))) 
							container.removeContainerFilter(appliedFilters.get(column));
						appliedFilters.remove(appliedFilters.get(column));
					}
				}
				if (ot!=null) ot.generateTotals();
				logger.fine("Filters on " + column + " after: " + appliedFilters);
				logger.fine("Filters on " + column + " after: " + container.getAppliedFilters());
			}
		};
		tf.addListener(l);
		filterListeners.put(column, l);		
	}*/
/**
 * Bind Combo to a boolean type of field. 
 * @values contains string values that represent true and false - in this order	
 */
	public static void bindBooleanComboBox(final ComboBox combo, String column,
			final String prompt, String[] values) {

		// propietarios.setWidth(ConfigurationUtil.get("COMMON_FIELD_WIDTH"));
		IndexedContainer c = new IndexedContainer();
		c.addContainerProperty(column, String.class, "");

		int i = 0;
		for (String value : values) {
			Item item = null; 
			if (i==0)
				item = c.addItem(new Boolean(true));
			else
				item = c.addItem(new Boolean(false));
			item.getItemProperty(column)
					.setValue(value);
			i++;
		}
		combo.setContainerDataSource(c);
		combo.setItemCaptionPropertyId(column);
		combo.setImmediate(true);
		combo.setInvalidAllowed(false);
		combo.setInputPrompt(prompt);
		combo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
	}
/*
	public static void bindBooleanComboBox(final ComboBox combo, final String column,
			final String prompt, String[] values,
			final BeanItemContainer container, 
			final HashMap<String, Filter> appliedFilters, HashMap<String, Property.ValueChangeListener> filterListeners) {
		bindBooleanComboBox(combo, column, prompt, values, container, appliedFilters, filterListeners, null);
	}
	
	
	public static void bindBooleanComboBox(final ComboBox combo, final String column,
			final String prompt, String[] values,
			final BeanItemContainer container, 
			final HashMap<String, Filter> appliedFilters, HashMap<String, Property.ValueChangeListener> filterListeners,  final IOperacionTable ot) {
		bindBooleanComboBox(combo, column, prompt, values);
		
		combo.removeListener(filterListeners.get(column));
		ValueChangeListener l = new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event != null && event.getProperty() != null
						&& event.getProperty().getValue() != null) {
					if (appliedFilters.containsKey(column)) {
						container.removeContainerFilter(appliedFilters.get(column));
						appliedFilters.remove(appliedFilters.get(column));
					}
					Boolean bool = (Boolean)event.getProperty().getValue();
					Filter f = Filters.eq(column, bool);
					container.addContainerFilter(f);
					container.applyFilters();
					appliedFilters.put(column, f);
				} else {
					if (appliedFilters.containsKey(column)) {
						container.removeContainerFilter(appliedFilters.get(column));
						appliedFilters.remove(appliedFilters.get(column));
					}
				}
				if (ot!=null) ot.generateTotals();
			}
		};
		combo.addListener(l);
		filterListeners.put(column, l);
		
	}*/

/*	
	public static void bindComboBox(final ComboBox combo, String column,
			final String prompt, Class clas, Filter filter) {
		bindComboBox(combo, column, prompt, clas, filter, null);
	}	*/
	
	public static void bindComboBox(final ComboBox combo, String column,  
			final String prompt, Class clas, Filter filter, String concatenatedColumn, JpaRepository repo) {
		
		BeanItemContainer beanItemContainer;  
		//if (elements==null) {
			beanItemContainer = new BeanItemContainer(clas, repo.findAll());
			if (filter!=null) beanItemContainer.addContainerFilter(filter);
		//}
		// propietarios.setWidth(ConfigurationUtil.get("COMMON_FIELD_WIDTH"));
		IndexedContainer c = new IndexedContainer();
		c.addContainerProperty(column, String.class, "");
		//BeanItemContainer.applyFilters();
		Collection<Object> ids = beanItemContainer.getItemIds();//ep.getAllEntityIdentifiers(BeanItemContainer, filter, null);
		for (Object id : ids) {
			Object entity = beanItemContainer.getItem(id).getBean();
			logger.fine("Got: " + entity);
			BeanItem bItem = new BeanItem(entity);
			Item item = c.addItem(entity);
			if (concatenatedColumn!=null) item.getItemProperty(column)
					.setValue(bItem.getItemProperty(column) + " " + bItem.getItemProperty(concatenatedColumn));
			else item.getItemProperty(column)
					.setValue(bItem.getItemProperty(column));
		}
		c.sort(new String[]{column},  new boolean[]{true});
		combo.setContainerDataSource(c);
		combo.setItemCaptionPropertyId(column);
		combo.setImmediate(true);
		combo.setInvalidAllowed(false);
		combo.setInputPrompt(prompt);
		combo.setFilteringMode(FilteringMode.CONTAINS);
		combo.addValueChangeListener(
				new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty() != null
						&& !event.getProperty().equals("")) {
					combo.select(event.getProperty().getValue());
				}
			}
		});
	}
	
	public static void bindComboBox(final ComboBox combo, String column, List elements, 
			final String prompt, String concatenatedColumn) {
		
		IndexedContainer c = new IndexedContainer();
		String idCol = null;
		String colProp = null;
		if (column.contains(".")) {
			idCol = column.substring(0, column.indexOf("."));
			colProp = column.substring(column.indexOf(".")+1);
		}	
		String contProp = (colProp!=null ? colProp : column);
		c.addContainerProperty(contProp, String.class, "");
		for (Object elem : elements) {
			logger.fine("Got: " + elem);
			BeanItem bItem = new BeanItem(elem);			
			Object value = null;
			if (column.contains(".")) {
				logger.fine("idCol: " + idCol + " colProp: " + colProp);
				Object idObj = bItem.getItemProperty(idCol).getValue();						
				logger.fine("Got subItem: " + idObj + " method: " + "get" + colProp.substring(0,1).toUpperCase() + colProp.substring(1));
				try {
					Method mth = idObj.getClass().getMethod("get" + colProp.substring(0,1).toUpperCase() + colProp.substring(1), new Class[] {});
					logger.fine("Got method: " + mth);
					mth.setAccessible(true);
					value = mth.invoke(idObj);					
				} catch (NoSuchMethodException nsm) { 
					logger.severe("Problem binding Combobox no method found for: " + column + " " + prompt + " " + concatenatedColumn + "\n" + nsm.getMessage());					
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					logger.severe("Problem binding Combobox for: " + column + " " + prompt + " " + concatenatedColumn + "\n" + e.getMessage() );
					e.printStackTrace();
				}
				logger.fine("Got value: " + value);
			} else {				
				if (bItem.getItemProperty(column)!=null)
					value = bItem.getItemProperty(column).getValue();
			}			
			Item item = c.addItem(value);
			if (concatenatedColumn!=null) 
				c.getContainerProperty(value, contProp).setValue(value + " " + bItem.getItemProperty(concatenatedColumn).getValue());
			else
				c.getContainerProperty(value, contProp).setValue(value);
/*			if (concatenatedColumn!=null) item.getItemProperty(contProp)
					.setValue(value + " " + bItem.getItemProperty(concatenatedColumn).getValue());
			else item.getItemProperty(contProp)
					.setValue(value);*/
		}
		c.sort(new String[]{contProp},  new boolean[]{true});
		combo.setContainerDataSource(c);
		combo.setItemCaptionPropertyId(contProp);
		combo.setImmediate(true);
		combo.setInvalidAllowed(false);
		combo.setInputPrompt(prompt);
		combo.setFilteringMode(FilteringMode.CONTAINS);
		combo.addValueChangeListener(
				new ValueChangeListener() {			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty() != null
						&& !event.getProperty().equals("")) {
					//logger.info("got val: " + event.getProperty().getValue());
					combo.select(event.getProperty().getValue());
				}								
				
			}
		});
	}	

/*
	public static void bindComboBox(final ComboBox combo, String column,
			final String prompt, Class clas) {
		bindComboBox(combo, column, prompt, clas, null); 
	}
	*/
	
	/*public static void bindCuentasBox(final ComboBox combo) {
		bindCuentasBox(null, combo);
	}
	*/
	/*public static void bindCuentasBox(Filter filter, final ComboBox combo) {
		// propietarios.setWidth(ConfigurationUtil.get("COMMON_FIELD_WIDTH"));
		IndexedContainer c = new IndexedContainer();
		c.addContainerProperty("id", Long.class, "");
		c.addContainerProperty("numero", String.class, "");
		BeanItemContainer BeanItemContainer;
		BeanItemContainer = BeanItemContainerFactory.make(Cuenta.class, ConfigurationUtil
				.getCuentaEP().getEntityManager());
		BeanItemContainer.addContainerFilter(filter);
		BeanItemContainer.applyFilters();
		Collection<Object> ids = BeanItemContainer.getItemIds();
		for (Object id : ids) {
			Object entity = BeanItemContainer.getItem(id).getEntity();
			BeanItem bItem = new BeanItem(entity);
			Item item = c.addItem(entity);
			item.getItemProperty("numero")
					.setValue(bItem.getItemProperty("numero") + " "
							+ bItem.getItemProperty("nombre") 
							// + " " + bItem.getItemProperty("pen") + " " + bItem.getItemProperty("usd")
							);
		}
		c.sort(new String[]{"numero","nombre"},  new boolean[]{true,true});
		combo.setContainerDataSource(c);
		combo.setItemCaptionPropertyId("numero");
		combo.setImmediate(true);
		combo.setInvalidAllowed(false);
		combo.setInputPrompt("Seleccione cuenta");
		combo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);		
	}*/
	
	/*public static void bindCuentasBox(final ComboBox combo, Set<Cuenta> cuentas) {
		// propietarios.setWidth(ConfigurationUtil.get("COMMON_FIELD_WIDTH"));
		IndexedContainer c = new IndexedContainer();
		c.addContainerProperty("id", Long.class, "");
		c.addContainerProperty("numero", String.class, "");
		for (Cuenta entity  : cuentas) {
			BeanItem bItem = new BeanItem(entity);
			Item item = c.addItem(entity);
			item.getItemProperty("numero")
					.setValue(bItem.getItemProperty("numero") + " "
							+ bItem.getItemProperty("nombre"));
		}
		c.sort(new String[]{"numero","nombre"},  new boolean[]{true,true});
		combo.setContainerDataSource(c);
		combo.setItemCaptionPropertyId("numero");
		combo.setImmediate(true);
		combo.setInvalidAllowed(false);
		combo.setInputPrompt("Seleccione cuenta");
		combo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);		
	}	

	public static void bindCentroCostoBox(final ComboBox combo) {
		IndexedContainer c = new IndexedContainer();
		c.addContainerProperty("id", Long.class, "");
		c.addContainerProperty("codigo", String.class, "");
		BeanItemContainer BeanItemContainer = BeanItemContainerFactory.make(CentroCosto.class, ConfigurationUtil.getEntityManager());
		BeanItemContainer.addContainerFilter(Filters.eq("isLeaf", new Boolean(true)));
		BeanItemContainer.applyFilters();
		Collection<Object> ids = BeanItemContainer.getItemIds();
		for (Object id : ids) {
			EntityItem entity = BeanItemContainer.getItem(id);
			CentroCosto cc = (CentroCosto)entity.getEntity();
			//logger.info("CC: " + id + " " + bItem.toString());
			Item item = c.addItem(entity.getEntity());
			item.getItemProperty("codigo")
					.setValue(cc.getCodigo() + " " + cc.getNombre());
		}
		c.sort(new String[]{"codigo"},  new boolean[]{true});
		combo.setContainerDataSource(c);
		combo.setItemCaptionPropertyId("codigo");
		combo.setImmediate(true);
		combo.setInvalidAllowed(false);
		combo.setInputPrompt("Seleccione centro de costo");
		combo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);		
	}
	
	public static void bindLugarGastoBox(final ComboBox combo) {
		IndexedContainer c = new IndexedContainer();
		c.addContainerProperty("id", Long.class, "");
		c.addContainerProperty("codigo", String.class, "");
		BeanItemContainer BeanItemContainer = BeanItemContainerFactory.make(LugarGasto.class, ConfigurationUtil.getEntityManager());
		BeanItemContainer.addContainerFilter(Filters.eq("isLeaf", new Boolean(true)));
		BeanItemContainer.applyFilters();
		Collection<Object> ids = BeanItemContainer.getItemIds();
		for (Object id : ids) {
			EntityItem entity = BeanItemContainer.getItem(id);
			LugarGasto cc = (LugarGasto)entity.getEntity();
			//logger.info("CC: " + id + " " + bItem.toString());
			Item item = c.addItem(entity.getEntity());
			item.getItemProperty("codigo")
					.setValue(cc.getCodigo() + " " + cc.getNombre());
		}
		c.sort(new String[]{"codigo"},  new boolean[]{true});
		combo.setContainerDataSource(c);
		combo.setItemCaptionPropertyId("codigo");
		combo.setImmediate(true);
		combo.setInvalidAllowed(false);
		combo.setInputPrompt("Seleccione Lugar de Gasto");
		combo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);		
	}
	
	public static void bindCuentaContableBox(final ComboBox combo) {
		IndexedContainer c = new IndexedContainer();
		c.addContainerProperty("id", Long.class, "");
		c.addContainerProperty("codigo", String.class, "");
		BeanItemContainer BeanItemContainer = BeanItemContainerFactory.make(CuentaContable.class, ConfigurationUtil.getEntityManager());
		BeanItemContainer.addContainerFilter(Filters.eq("isLeaf", new Boolean(true)));
		BeanItemContainer.applyFilters();
		Collection<Object> ids = BeanItemContainer.getItemIds();
		for (Object id : ids) {
			EntityItem entity = BeanItemContainer.getItem(id);
			CuentaContable cc = (CuentaContable)entity.getEntity();
			//logger.info("CC: " + id + " " + bItem.toString());
			Item item = c.addItem(entity.getEntity());
			item.getItemProperty("codigo")
					.setValue(cc.getCodigo() + " " + cc.getNombre());
		}
		c.sort(new String[]{"codigo"},  new boolean[]{true});
		combo.setContainerDataSource(c);
		combo.setItemCaptionPropertyId("codigo");
		combo.setImmediate(true);
		combo.setInvalidAllowed(false);
		combo.setInputPrompt("Seleccione Cuenta contable");
		combo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);		
	}
	
	public static void bindRubroProyectoBox(final ComboBox combo, final BeanItemContainer container, 
			final HashMap<String, Filter> appliedFilters, HashMap<String, Property.ValueChangeListener> filterListeners, Cuenta cuenta) {

		final String prompt = "filtro rubro del proyecto";
		final String column = "rubroProyecto";
		IndexedContainer c = new IndexedContainer();
		c.addContainerProperty("id", Long.class, "");
		c.addContainerProperty("codigo", String.class, "");
		Filter fCuenta = null;
		BeanItemContainer BeanItemContainer = BeanItemContainerFactory.make(RubroProyecto.class, ConfigurationUtil.getEntityManager());
		BeanItemContainer.addContainerFilter(Filters.eq("isLeaf", new Boolean(true)));

		if (cuenta!=null) BeanItemContainer.addContainerFilter(Filters.joinFilter("cuentaRubroProyecto", Filters.eq("id", cuenta.getId())));
		BeanItemContainer.applyFilters();
		Collection<Object> ids = BeanItemContainer.getItemIds();
		for (Object id : ids) {
			EntityItem entity = BeanItemContainer.getItem(id);
			RubroProyecto cc = (RubroProyecto)entity.getEntity();
			Item item = c.addItem(entity.getEntity());
			item.getItemProperty("codigo")
					.setValue(cc.getCodigo() + " " + cc.getNombre());
		}
		c.sort(new String[]{"codigo"},  new boolean[]{true});
		combo.setContainerDataSource(c);
		combo.setItemCaptionPropertyId("codigo");
		combo.setImmediate(true);
		combo.setInvalidAllowed(false);
		combo.setInputPrompt(prompt);
		combo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		//	
		if (appliedFilters!=null) addValueChangeListener(combo, column, prompt, container, appliedFilters, filterListeners);

		
	}
	public static void bindRubroInstitucionalBox(final ComboBox combo, final BeanItemContainer container, 
			final HashMap<String, Filter> appliedFilters, HashMap<String, Property.ValueChangeListener> filterListeners) {
		
		final String prompt = "filtro rubro institucional";
		final String column = "rubroInstitucional";
		IndexedContainer c = new IndexedContainer();
		c.addContainerProperty("id", Long.class, "");
		c.addContainerProperty("codigo", String.class, "");
		BeanItemContainer BeanItemContainer = BeanItemContainerFactory.make(RubroInstitucional.class, ConfigurationUtil.getEntityManager());
		BeanItemContainer.addContainerFilter(Filters.eq("isLeaf", new Boolean(true)));
		BeanItemContainer.applyFilters();
		Collection<Object> ids = BeanItemContainer.getItemIds();
		for (Object id : ids) {
			EntityItem entity = BeanItemContainer.getItem(id);
			RubroInstitucional cc = (RubroInstitucional)entity.getEntity();
			logger.info("RI: " + id + " " + entity.toString());
			Item item = c.addItem(entity.getEntity());
			item.getItemProperty("codigo")
					.setValue(cc.getCodigo() + " " + cc.getNombre());
		}
		c.sort(new String[]{"codigo"},  new boolean[]{true});
		combo.setContainerDataSource(c);
		combo.setItemCaptionPropertyId("codigo");
		combo.setImmediate(true);
		combo.setInvalidAllowed(false);
		combo.setInputPrompt(prompt);
		combo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);		
		
		if (appliedFilters!=null) addValueChangeListener(combo, column, prompt, container, appliedFilters, filterListeners);
	}	
	
	
	public static void bindComboBox(final ComboBox combo, String column,
			final String prompt, String[] values) {

		IndexedContainer c = new IndexedContainer();
		c.addContainerProperty(column, String.class, "");

		for (String value : values) {
			Item item = null; 
			item = c.addItem(value);
			item.getItemProperty(column)
					.setValue(value);
		}
		c.sort(new String[]{column},  new boolean[]{true});
		combo.setContainerDataSource(c);
		combo.setItemCaptionPropertyId(column);
		combo.setImmediate(true);
		combo.setInvalidAllowed(false);
		combo.setInputPrompt(prompt);
		combo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);		
	}

	public static void bindComboBox(final ComboBox combo, final String column,
			final String prompt, Tipo[] values,
			final BeanItemContainer container, 
			final HashMap<String, Filter> appliedFilters, HashMap<String, Property.ValueChangeListener> filterListeners) {
		bindComboBox(combo, column, prompt, values, container, appliedFilters, filterListeners, null);
	}
	
	public static void bindComboBox(final ComboBox combo, final String column,
			final String prompt, Tipo[] values,
			final BeanItemContainer container, 
			final HashMap<String, Filter> appliedFilters, HashMap<String, Property.ValueChangeListener> filterListeners, final IOperacionTable ot) {
		IndexedContainer c = new IndexedContainer();
		c.addContainerProperty(column, String.class, "");

		for (Tipo value : values) {
			Item item = null; 
			item = c.addItem(value);
			item.getItemProperty(column)
					.setValue(value.toString());
		}
		c.sort(new String[]{column},  new boolean[]{true});
		combo.setContainerDataSource(c);
		combo.setItemCaptionPropertyId(column);
		combo.setImmediate(true);
		combo.setInvalidAllowed(false);
		combo.setInputPrompt(prompt);

		combo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
				
		addValueChangeListener(combo, column, prompt, container, appliedFilters, filterListeners);			
	}	
	
	public static void bindComboBox(final ComboBox combo, final String column,
			final String prompt, String [] values,
			final BeanItemContainer container,
			final HashMap<String, Filter> appliedFilters, HashMap<String, Property.ValueChangeListener> filterListeners) {
		
		bindComboBox(combo, column, prompt, values);
		addValueChangeListener(combo, column, prompt, container, appliedFilters, filterListeners);					
	}
	
	public static void bindComboBox(final ComboBox combo, String column,
			final String prompt, Class clas,
			final BeanItemContainer container, final String joinColumn,
			final HashMap<String, Filter> appliedFilters, HashMap<String, Property.ValueChangeListener> filterListeners) {
		bindComboBox(combo, column, prompt, clas, container, joinColumn, appliedFilters, filterListeners, null);
	}
	
	public static void bindComboBox(final ComboBox combo, String column,
			final String prompt, Class clas,
			final BeanItemContainer container, final String joinColumn,
			final HashMap<String, Filter> appliedFilters, HashMap<String, Property.ValueChangeListener> filterListeners, final IOperacionTable ot) {
		
		bindComboBox(combo, column, prompt, clas);
		combo.removeListener(filterListeners.get(joinColumn));
		ValueChangeListener l = new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event != null && event.getProperty() != null
						&& event.getProperty().getValue() != null) {
					if (appliedFilters.containsKey(joinColumn)) {
						container.removeContainerFilter(appliedFilters.get(joinColumn));
						container.applyFilters();
						appliedFilters.remove(appliedFilters.get(joinColumn));
					}
					BeanItem bItem = new BeanItem(event.getProperty()
							.getValue());
					Filter f = Filters.joinFilter(joinColumn, Filters.eq("id",
							(Long) bItem.getItemProperty("id").getValue()));
					container.addContainerFilter(f);
					container.applyFilters();
					appliedFilters.put(joinColumn, f);									
				} else {
					combo.setInputPrompt(prompt);
					if (appliedFilters.containsKey(joinColumn)) {
						container.removeContainerFilter(appliedFilters.get(joinColumn));
						appliedFilters.remove(appliedFilters.get(joinColumn));
					}
				}
				if (ot!=null) ot.generateTotals();
			}
		};
		combo.addListener(l);
		filterListeners.put(joinColumn, l);
	}

	public static void bindDateField(final DateField df, final int comparisonType, final String column, final String prompt, final BeanItemContainer container, 
			final HashMap<String, Filter> appliedFilters, HashMap<String, Property.ValueChangeListener> filterListeners) {
		bindDateField(df, comparisonType, column, prompt, container, appliedFilters, filterListeners, null);
	}
	
	public static void bindDateField(final DateField df, final int comparisonType, final String column, final String prompt, final BeanItemContainer container, 
			final HashMap<String, Filter> appliedFilters, HashMap<String, Property.ValueChangeListener> filterListeners, final IOperacionTable ot) {
		df.setResolution(DateField.RESOLUTION_DAY);
		df.setImmediate(true);
		df.setDateFormat("yyyy.MM.dd");
		df.removeListener(filterListeners.get(column));
		ValueChangeListener l = new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				String filterName = column + new Integer(comparisonType).toString();
				if (event != null && event.getProperty() != null
						&& event.getProperty().getValue() != null) {
					if (appliedFilters.containsKey(filterName)) {
						container.removeContainerFilter(appliedFilters.get(filterName));
						appliedFilters.remove(appliedFilters.get(filterName));
					}
					Date date = (Date)event.getProperty().getValue();
					Filter f = null;
					if (comparisonType==0)
						f= Filters.eq(column, date);
					else if (comparisonType>0)
						f = Filters.gteq(column, ConfigurationUtil.getBeginningOfDay(date));
					else
						f = Filters.lteq(column, ConfigurationUtil.getEndOfDay(date));
					container.addContainerFilter(f);
					container.applyFilters();
					appliedFilters.put(filterName, f);
				} else {
					if (appliedFilters.containsKey(filterName)) {
						container.removeContainerFilter(appliedFilters.get(filterName));
						appliedFilters.remove(appliedFilters.get(filterName));
					}
				}
				if (ot!=null) ot.generateTotals();
			}
		};
		df.addListener(l);
		filterListeners.put(column, l);		
	}	*/
}
