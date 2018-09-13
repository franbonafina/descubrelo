package country;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.discovery.dis.domain.common.impl.Country;
import com.discovery.dis.domain.common.impl.TinValidation;
import com.discovery.dis.service.CountryService;
import com.discovery.dis.service.TinValidationService;
import com.discovery.dis.web.navigation.NavigationRuler;
import com.discovery.dis.web.security.LoginController;
import com.discovery.dis.web.util.FacesUtil;
import com.discovery.dis.web.view.IEntityCRUDController;
import com.discovery.dis.web.view.cache.SelectorController;

@ManagedBean(name = "countryCRUDController")
@ViewScoped
public class CountryCRUDController implements IEntityCRUDController {
	private static final Logger LOG = LoggerFactory.getLogger("dis");

	@ManagedProperty(value = "#{countryService}")
	private CountryService countryService;

	@ManagedProperty(value = "#{navigationRuler}")
	private NavigationRuler navigationRuler;

	@ManagedProperty(value = "#{selectorController}")
	private SelectorController selectorController;

	@ManagedProperty(value = "#{tinValidationService}")
	private TinValidationService tinValidationService;

	@ManagedProperty(value = "#{loginController}")
	private LoginController loginController;

	private Country country;

	private List<Country> countryList = new ArrayList<Country>();

	private List<TinValidation> validationList = new ArrayList<TinValidation>();

	private List<TinValidation> selectedValidationList = new ArrayList<TinValidation>();

	private boolean showTable = true;

	public CountryCRUDController() {

		super();
	}

	@PostConstruct
	public void init() {
		country = (Country) FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap()
				.get("countryCRUDController:country");
		countryList = selectorController.getCountryList();
		validationList = tinValidationService.findByCountry(country);

	}

	public boolean isNew() {
		return country.getId() == null ? true : false;
	}

	@Override
	public String createOrUpdate() {
		String outcome = null;

		try {
			if (validationList != null && !validationList.isEmpty()) {
				country = countryService.fetch(country);

				for (TinValidation tin : validationList) {
					tin.setCountry(country);
				}

				country.getTinsValidation().clear();
				country.getTinsValidation().addAll(validationList);
			}

			countryService.saveOrUpdate(country);
			FacesUtil.addToRequestMap("countryCRUD", country);

			msgSuccess();
			outcome = navigationRuler.getCountryListPage();

		} catch (Exception e) {
			e.printStackTrace();
			FacesUtil.errorMessage("message.country.changes.save.failure.b");
		}

		return outcome;
	}

	private void msgSuccess() {
		FacesUtil.infoMessages("message.common.changes.save.success");
	}

	@Override
	public String cancel() {

		return navigationRuler.getCountryListPage();
	}

	@Override
	public String delete() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addTinvalidation() {
		TinValidation tinValidation = new TinValidation();
		tinValidation.setCountry(country);
		tinValidation.setModifiedBy(loginController.getUsername());
		validationList.add(tinValidation);

		FacesUtil.infoMessage("message.country.createUpdate.tinValidation.add");
	}

	public void removeTinvalidation() {
		if (null != selectedValidationList && !selectedValidationList.isEmpty()) {
			for (TinValidation tinValidation : selectedValidationList) {
				validationList.remove(tinValidation);
			}
			FacesUtil
					.infoMessage("message.country.createUpdate.tinValidation.remove");
		}
		selectedValidationList.clear();
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

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public List<Country> getCountryList() {
		return countryList;
	}

	public void setCountryList(List<Country> countryList) {
		this.countryList = countryList;
	}

	public static Logger getLog() {
		return LOG;
	}

	public SelectorController getSelectorController() {
		return selectorController;
	}

	public void setSelectorController(SelectorController selectorController) {
		this.selectorController = selectorController;
	}

	public boolean isShowTable() {
		return showTable;
	}

	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}

	public List<TinValidation> getValidationList() {
		return validationList;
	}

	public void setValidationList(List<TinValidation> validationList) {
		this.validationList = validationList;
	}

	public TinValidationService getTinValidationService() {
		return tinValidationService;
	}

	public void setTinValidationService(
			TinValidationService tinValidationService) {
		this.tinValidationService = tinValidationService;
	}

	public LoginController getLoginController() {
		return loginController;
	}

	public void setLoginController(LoginController loginController) {
		this.loginController = loginController;
	}

	public List<TinValidation> getSelectedValidationList() {
		return selectedValidationList;
	}

	public void setSelectedValidationList(
			List<TinValidation> selectedValidationList) {
		this.selectedValidationList = selectedValidationList;
	}

}
