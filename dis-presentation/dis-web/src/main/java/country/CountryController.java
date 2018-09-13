package country;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.discovery.dis.domain.common.impl.Country;
import com.discovery.dis.service.CountryService;
import com.discovery.dis.web.filterGenerator.CountryFilterGenerator;
import com.discovery.dis.web.model.CountryLazyDataModel;
import com.discovery.dis.web.navigation.NavigationRuler;
import com.discovery.dis.web.util.FacesUtil;
import com.discovery.dis.web.view.IEntityListController;

@ManagedBean(name = "countryController")
@ViewScoped
public class CountryController implements IEntityListController {

	private static final Logger LOG = LoggerFactory.getLogger("dis");

	@ManagedProperty(value = "#{countryService}")
	private CountryService countryService;

	@ManagedProperty(value = "#{navigationRuler}")
	private NavigationRuler navigationRuler;

	@ManagedProperty("#{filterContainer.countryFilterGenerator}")
	private CountryFilterGenerator countryFilterGenerator;

	private Country selectedCountry;

	private boolean showTable = false;

	private CountryLazyDataModel countryLazyDataModel;

	public CountryController() {
	}

	@Override
	public String edit() {
		FacesUtil.addToRequestMap("countryCRUDController:country",
				selectedCountry);
		return navigationRuler.getCountryCRUDOutcome();

	}

	@PostConstruct
	private void init() {

		countryLazyDataModel = new CountryLazyDataModel(countryService,
				countryFilterGenerator, false);
		selectedCountry = (Country) FacesUtil.getFromRequestMap("countryCRUD");

		if (null != selectedCountry) {
			countryFilterGenerator.setName(selectedCountry.getName());
			countryFilterGenerator.setIsoNumericCode(selectedCountry
					.getIsoNumericCode());
			countryFilterGenerator.setIsoAlpha3Code(selectedCountry
					.getIsoAlpha3Code());
			countryFilterGenerator.setIsoAlpha2Code(selectedCountry
					.getIsoAlpha2Code());
			countryFilterGenerator.setEUMember(selectedCountry.isEUMember());
			countryFilterGenerator.setActive(selectedCountry.isActive());

			countryLazyDataModel.setSearch(true);
		} else {
			countryFilterGenerator.clearFilters();
		}

	}

	@Override
	public String add() {
		selectedCountry = new Country();
		selectedCountry.setName(countryFilterGenerator.getName());
		selectedCountry.setIsoNumericCode(countryFilterGenerator
				.getIsoNumericCode());
		selectedCountry.setIsoAlpha3Code(countryFilterGenerator
				.getIsoAlpha3Code());
		selectedCountry.setIsoAlpha2Code(countryFilterGenerator
				.getIsoAlpha2Code());
		selectedCountry.setEUMember(countryFilterGenerator.isEUMember());
		selectedCountry.setActive(countryFilterGenerator.isActive());

		FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
				.put("countryCRUDController:country", selectedCountry);

		return navigationRuler.getCountryCRUDPage();
	}

	public void showTable() {
		// if (showSavedCountry != null || (showCancelG != null &&
		// showCancelGeography)) {
		// RequestContext.getCurrentInstance().execute("PF('geographyDataTablePanel').show();");
		// showCancelGeography = false;
		// showSavedGeography = null;
		// }
		RequestContext.getCurrentInstance().update("countryform");
	}

	public void delete() {

		if (null != selectedCountry) {
			try {
				boolean delete = countryService.delete(selectedCountry);
				RequestContext.getCurrentInstance().update(
						"listCountryForm:countryDataTablePanel");
				selectedCountry = null;
			} catch (Exception e) {
				LOG.error("Error cancel : ", e);
				msgError();
			
			}
		}

	}
	
	private void msgError() {
		FacesUtil.errorMessage("message.country.delete");
	}


	public void clearFilters() {
		countryFilterGenerator.clearFilters();

	}

	public void filterTable() {
		showTable = true;
	}

	public CountryService getCountryService() {
		return countryService;
	}

	public void setCountryService(CountryService countryService) {
		this.countryService = countryService;
	}

	public NavigationRuler getNavigationRuler() {
		return navigationRuler;
	}

	public void setNavigationRuler(NavigationRuler navigationRuler) {
		this.navigationRuler = navigationRuler;
	}

	public CountryFilterGenerator getCountryFilterGenerator() {
		return countryFilterGenerator;
	}

	public void setCountryFilterGenerator(
			CountryFilterGenerator countryFilterGenerator) {
		this.countryFilterGenerator = countryFilterGenerator;
	}

	public static Logger getLog() {
		return LOG;
	}

	@Override
	public String copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String view() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isShowTable() {
		return showTable;
	}

	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}

	public CountryLazyDataModel getCountryLazyDataModel() {
		return countryLazyDataModel;
	}

	public void setCountryLazyDataModel(
			CountryLazyDataModel countryLazyDataModel) {
		this.countryLazyDataModel = countryLazyDataModel;
	}

	public Country getSelectedCountry() {
		return selectedCountry;
	}

	public void setSelectedCountry(Country selectedCountry) {
		this.selectedCountry = selectedCountry;
	}
}
