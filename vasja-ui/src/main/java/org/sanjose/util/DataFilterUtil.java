package org.sanjose.util;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

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

public class DataFilterUtil {

	private static final Logger logger = Logger.getLogger(DataFilterUtil.class
			.getName());	

/**
 * Bind Combo to a boolean type of field. 
 * @values contains string values that represent true and false - in this order	
 */
	@SuppressWarnings("unchecked")
    public static void bindBooleanComboBox(final ComboBox combo, String column,
                                           final String prompt, String[] values) {
		IndexedContainer c = new IndexedContainer();
		c.addContainerProperty(column, String.class, "");

		int i = 0;
		for (String value : values) {
			Item item = null; 
			if (i==0)
				item = c.addItem(Boolean.TRUE);
			else
				item = c.addItem(Boolean.FALSE);
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



	/**
	 * Bind Combo to a boolean type of field.
	 * @values contains string values that represent true and false - in this order
	 */
	@SuppressWarnings("unchecked")
	public static void bindTipoMonedaOptionGroup(final OptionGroup combo, String column) {

		Map<Character, String> valMap = new HashMap<>();
		valMap.put('0', "Soles");
		valMap.put('1',"Dolares");
		valMap.put('2',"Euros");
		IndexedContainer c = new IndexedContainer();
		c.addContainerProperty(column, String.class, "");

		int i = 0;
		Character[] keys = valMap.keySet().toArray(new Character[0]);
		Arrays.sort(valMap.keySet().toArray());
		for (Character value : keys) {
			Item item = c.addItem(value);
			item.getItemProperty(column)
					.setValue(valMap.get(value));
			i++;
		}
		combo.setContainerDataSource(c);
		combo.setItemCaptionPropertyId(column);
		combo.setImmediate(true);
		combo.setInvalidAllowed(false);
	}

	public static void bindTipoMonedaComboBox(final ComboBox combo, String column,
											  final String prompt) {
		bindTipoMonedaComboBox(combo, column, prompt, true);
	}

	@SuppressWarnings("unchecked")
	public static void bindTipoMonedaComboBox(final ComboBox combo, String column,
											  final String prompt, boolean showNum) {

		Map<Character, String> valMap = new HashMap<>();
		valMap.put('0',"S/");
		valMap.put('1',"$");
		valMap.put('2',"€");

		// propietarios.setWidth(ConfigurationUtil.get("COMMON_FIELD_WIDTH"));
		IndexedContainer c = new IndexedContainer();
		c.addContainerProperty(column, String.class, "");

		int i = 0;
		Character[] keys = valMap.keySet().toArray(new Character[0]);
		Arrays.sort(valMap.keySet().toArray());
		for (Character value : keys) {
			Item item = c.addItem(value);
			item.getItemProperty(column)
					.setValue((showNum ? value + " " : "") + valMap.get(value));
			i++;
		}
		combo.setContainerDataSource(c);
		combo.setItemCaptionPropertyId(column);
		combo.setImmediate(true);
		combo.setInvalidAllowed(false);
		combo.setInputPrompt(prompt);
		combo.setFilteringMode(FilteringMode.CONTAINS);
    }

    public static void bindZeroOneComboBox(final ComboBox combo, String column,
                                           final String prompt) {
        Map<Character, String> valMap = new TreeMap<>();
        valMap.put('0', "0");
        valMap.put('1', "1");
        bindFixedValComboBox(combo, column, prompt, valMap);
    }

	public static void bindGeneroComboBox(final ComboBox combo, String column,
										  final String prompt) {
		Map<Character, String> valMap = new TreeMap<>();
		valMap.put('F', "Femenino");
		valMap.put('M', "Masculino");
		bindFixedValComboBox(combo, column, prompt, valMap);
	}


	public static void bindTipoPersonaComboBox(final ComboBox combo, String column,
											  final String prompt) {
		Map<Character, String> valMap = new TreeMap<>();
		valMap.put('N',"Natural");
		valMap.put('J',"Juridico");
		bindFixedValComboBox(combo, column, prompt, valMap);
	}

	public static void bindTipoDestinoComboBox(final ComboBox combo, String column,
											   final String prompt) {
		Map<Character, String> valMap = new TreeMap<>();
		valMap.put('0',"Proveedor");
		valMap.put('1',"Empleado");
		valMap.put('2',"Cliente");
		valMap.put('3',"Tercero");
		bindFixedValComboBox(combo, column, prompt, valMap);
	}

	@SuppressWarnings("unchecked")
	private static void bindFixedValComboBox(final ComboBox combo, String column,
											 final String prompt, Map<Character, String> valMap) {

		IndexedContainer c = new IndexedContainer();
		c.addContainerProperty(column, String.class, "");

		int i = 0;
		Character[] keys = valMap.keySet().toArray(new Character[0]);
		Arrays.sort(valMap.keySet().toArray());
		for (Character value : keys) {
			Item item = c.addItem(value);
			item.getItemProperty(column)
					.setValue(valMap.get(value));
			i++;
		}
		combo.setContainerDataSource(c);
		combo.setItemCaptionPropertyId(column);
		combo.setImmediate(true);
		combo.setInvalidAllowed(false);
		if (prompt!=null) combo.setInputPrompt(prompt);
		combo.setFilteringMode(FilteringMode.CONTAINS);
	}

	@SuppressWarnings("unchecked")
	public static void bindFixedStringValComboBox(final ComboBox combo, String column,
											 final String prompt, Map<String, String> valMap) {

		IndexedContainer c = new IndexedContainer();
		c.addContainerProperty(column, String.class, "");

		int i = 0;
		String[] keys = valMap.keySet().toArray(new String[0]);
		Arrays.sort(valMap.keySet().toArray());
		for (String value : keys) {
			Item item = c.addItem(value);
			item.getItemProperty(column)
					.setValue(valMap.get(value));
			i++;
		}
		combo.setContainerDataSource(c);
		combo.setItemCaptionPropertyId(column);
		combo.setImmediate(true);
		combo.setInvalidAllowed(false);
		if (prompt!=null) combo.setInputPrompt(prompt);
		combo.setFilteringMode(FilteringMode.CONTAINS);
	}


	@SuppressWarnings("unchecked")
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
		if (prompt!=null) combo.setInputPrompt(prompt);
		combo.setFilteringMode(FilteringMode.CONTAINS);
		combo.addValueChangeListener(
				(ValueChangeListener) event -> {
                    if (event.getProperty() != null
                            && !event.getProperty().equals("")) {
                        combo.select(event.getProperty().getValue());
                    }
                });
	}


	public static void refreshComboBox(final ComboBox combo, String column, List elements, String concatenatedColumn) {
		refreshComboBox(combo, elements, column, concatenatedColumn, null);
	}

	@SuppressWarnings("unchecked")
	public static void refreshComboBox(final ComboBox combo, List elements,
									   String firstColumn, String concatenatedColumn, String idColumn) {

		IndexedContainer c = (IndexedContainer)combo.getContainerDataSource();
		c.removeAllItems();
		String idCol = null;
		String colProp = null;
		String column = idColumn != null ? idColumn : firstColumn;
		if (column.contains(".")) {
			idCol = column.substring(0, column.indexOf("."));
			colProp = column.substring(column.indexOf(".")+1);
		}
		String contProp = (colProp!=null ? colProp : column);
		c.addContainerProperty(contProp, String.class, "");
		for (Object elem : elements) {
			BeanItem bItem = new BeanItem(elem);
			Object value = null;
			if (column.contains(".")) {
				Object idObj = bItem.getItemProperty(idCol).getValue();
				try {
					Method mth = idObj.getClass().getMethod("get" + (colProp != null ? colProp.substring(0, 1).toUpperCase() : null) + colProp.substring(1), new Class[] {});
					mth.setAccessible(true);
					value = mth.invoke(idObj);
				} catch (NoSuchMethodException nsm) {
					logger.severe("Problem binding Combobox no method found for: " + column + " " + concatenatedColumn + "\n" + nsm.getMessage());
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					logger.severe("Problem binding Combobox for: " + column + "  " + concatenatedColumn + "\n" + e.getMessage() );
					e.printStackTrace();
				}
			} else {
				if (bItem.getItemProperty(column)!=null)
					value = bItem.getItemProperty(column).getValue();
			}
			c.addItem(value);
			if (concatenatedColumn!=null) {
				Property prop = c.getContainerProperty(value, contProp);
				prop.setValue((idColumn != null ? bItem.getItemProperty(firstColumn).getValue() : value) + " " + bItem.getItemProperty(concatenatedColumn).getValue());
			}
			else
				c.getContainerProperty(value, contProp).setValue(idColumn != null ? bItem.getItemProperty(firstColumn).getValue() : value);
		}
		c.sort(new String[]{contProp},  new boolean[]{true});
	}


	public static void bindComboBox(final ComboBox combo, String column, List elements,
									String concatenatedColumn) {
		bindComboBox(combo, column, elements, null, concatenatedColumn);
	}

	public static void bindComboBox(final ComboBox combo, String column, List elements,
									final String prompt, String concatenatedColumn) {
		bindComboBox(combo, elements, prompt, column, concatenatedColumn, null);

	}

	public static void bindComboBox(final ComboBox combo, List elements,
									final String prompt, String firstColumn, String concatenatedColumn, String idColumn) {
		@SuppressWarnings("unchecked")

		IndexedContainer c = new IndexedContainer();
		String idCol = null;
		String colProp = null;
		String column = idColumn != null ? idColumn : firstColumn;
		if (column.contains(".")) {
			idCol = column.substring(0, column.indexOf("."));
			colProp = column.substring(column.indexOf(".")+1);
		}	
		String contProp = (colProp!=null ? colProp : column);
		c.addContainerProperty(contProp, String.class, "");
		for (Object elem : elements) {
			//logger.fine("Got: " + elem);
			BeanItem bItem = new BeanItem(elem);			
			Object value = null;
			if (column.contains(".")) {
				//logger.fine("idCol: " + idCol + " colProp: " + colProp);
				Object idObj = bItem.getItemProperty(idCol).getValue();						
				//logger.fine("Got subItem: " + idObj + " method: " + "get" + (colProp != null ? colProp.substring(0, 1).toUpperCase() : null) + colProp.substring(1));
				try {
					Method mth = idObj.getClass().getMethod("get" + colProp.substring(0,1).toUpperCase() + colProp.substring(1), new Class[] {});
					mth.setAccessible(true);
					value = mth.invoke(idObj);					
				} catch (NoSuchMethodException nsm) { 
					logger.severe("Problem binding Combobox no method found for: " + column + " " + prompt + " " + concatenatedColumn + "\n" + nsm.getMessage());					
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					logger.severe("Problem binding Combobox for: " + column + " " + prompt + " " + concatenatedColumn + "\n" + e.getMessage() );
					e.printStackTrace();
				}
			} else {				
				if (bItem.getItemProperty(column)!=null && !GenUtil.objNullOrEmpty(bItem.getItemProperty(column).getValue()))
					value = bItem.getItemProperty(column).getValue();
			}			
			Item item = c.addItem(value);
			if (concatenatedColumn != null)
				c.getContainerProperty(value, contProp).setValue((idColumn != null ? bItem.getItemProperty(firstColumn).getValue() : value)
						+ " " + bItem.getItemProperty(concatenatedColumn).getValue());
			else
				c.getContainerProperty(value, contProp).setValue(idColumn != null ? bItem.getItemProperty(firstColumn).getValue() : value);
		}
		c.sort(new String[]{contProp},  new boolean[]{true});
		combo.setContainerDataSource(c);
		combo.setItemCaptionPropertyId(contProp);
		combo.setImmediate(true);
		combo.setInvalidAllowed(false);
		if (prompt!=null) combo.setInputPrompt(prompt);
		combo.setFilteringMode(FilteringMode.CONTAINS);
		combo.setId("my-custom-combobox");
		combo.addValueChangeListener(
				(ValueChangeListener) event -> {
                    if (event.getProperty() != null
                            && !event.getProperty().equals("")) {
                        //logger.info("got val: " + event.getProperty().getValue());
                        combo.select(event.getProperty().getValue());
                    }

                });
	}
}
