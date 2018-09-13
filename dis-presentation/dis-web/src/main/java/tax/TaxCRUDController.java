package tax;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

import com.discovery.dis.domain.billing.impl.InvoiceType;
import com.discovery.dis.domain.common.impl.Tax;
import com.discovery.dis.domain.common.impl.Tax.CALCULATION_BASE;
import com.discovery.dis.domain.common.impl.Tax.CALCULATION_TYPE;
import com.discovery.dis.domain.common.impl.Tax.TaxComparator;
import com.discovery.dis.domain.impl.Company;
import com.discovery.dis.domain.util.BigDecimalUtil;
import com.discovery.dis.domain.util.SetUtil;
import com.discovery.dis.entity.CustomerService;
import com.discovery.dis.security.UserService;
import com.discovery.dis.service.CompanyService;
import com.discovery.dis.service.TaxService;
import com.discovery.dis.web.navigation.NavigationRuler;
import com.discovery.dis.web.security.LoginController;
import com.discovery.dis.web.util.FacesUtil;
import com.discovery.dis.web.view.IEntityCRUDController;
import com.discovery.dis.web.view.cache.SelectorController;

@ManagedBean(name = "taxCRUDController")
@ViewScoped
public class TaxCRUDController implements IEntityCRUDController {
	private static final Logger LOG = LoggerFactory.getLogger("dis");

	@ManagedProperty(value = "#{loginController}")
	private LoginController loginController;

	@ManagedProperty(value = "#{userService}")
	private UserService userService;

	@ManagedProperty(value = "#{navigationRuler}")
	private NavigationRuler navigationRuler;

	@ManagedProperty(value = "#{companyService}")
	private CompanyService companyService;

	@ManagedProperty(value = "#{taxService}")
	private TaxService taxService;

	@ManagedProperty(value = "#{customerService}")
	private CustomerService customerService;

	@ManagedProperty(value = "#{selectorController}")
	private SelectorController selectorController;

	private List<Tax> taxList = new ArrayList<Tax>();
	private List<Tax> selectedTax = new ArrayList<Tax>();
	private List<CALCULATION_BASE> calculationBases;
	private List<CALCULATION_TYPE> calculationTypes;

	private Company selectedCompany;
	private Boolean active;

	private CALCULATION_BASE calculationBase;
	private CALCULATION_TYPE calculationType;
	private InvoiceType invoiceType;
	private boolean onEditionMode = false;

	@PostConstruct
	public void init() {
		selectedCompany = (Company) FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
				.get("selectedCompanyTax");
		if (null != selectedCompany) {

			initTaxList();

			calculationBases = new ArrayList<Tax.CALCULATION_BASE>(Arrays.asList(CALCULATION_BASE.values()));

			calculationTypes = new ArrayList<Tax.CALCULATION_TYPE>(Arrays.asList(CALCULATION_TYPE.values()));
			calculationTypes.remove(CALCULATION_TYPE.CALCULATED);
		} else {
			taxList = new ArrayList<Tax>();
		}

	}

	private void initTaxList() {
		selectedCompany = companyService.fetchCompanyTaxes(selectedCompany);

		taxList = new ArrayList<Tax>(selectedCompany.getTaxes());
		Collections.sort(taxList, new TaxComparator());
	}

	public void addTaxLine() {

		Tax tax = new Tax();
		tax.setTaxCode(null);
		tax.setDescription(null);
		tax.setParameterValue(null);
		tax.setCalculationType(calculationType);
		tax.setActive(false);
		tax.setCalculationBase(calculationBase);
		tax.setCompany(selectedCompany);

		taxList.add(tax);
		Collections.sort(taxList, new TaxComparator());
		// TODO just for Tax Identification
		tax.setId(Long.valueOf(taxList.size() * -1));
	}

	public void removeTaxLine() {
		try {
			if (null != selectedTax && !selectedTax.isEmpty()) {
				// TODO just for Tax Identification
				for (Tax tax : selectedTax) {
					if (0l > tax.getId().longValue()) {
						// taxList.contains(tax);
						taxList.remove(tax);
					}
				}
				taxList.removeAll(selectedTax);
				Collections.sort(taxList, new TaxComparator());
				selectedTax.clear();
			}
		} catch (Exception e) {
			FacesUtil.errorMessage("message.withholding.error.remove");
			LOG.error("Error remove line : ", e);
		}

	}

	@Override
	public String createOrUpdate() {
		String outcome = null;
		try {
			if (null != selectedCompany && isValid()) {

				// TODO just for Tax Identification
				for (Tax tax : taxList) {
					if (0l > tax.getId().longValue()) {
						tax.setId(null);
					}
				}

				selectedCompany.getTaxes().clear();
				selectedCompany.getTaxes().addAll(new HashSet<Tax>(taxList));

				companyService.saveOrUpdate(selectedCompany);
				taxList = new ArrayList<Tax>(taxList);
				Collections.sort(taxList, new TaxComparator());

				// outcome = navigationRuler.getTaxListPage();
				FacesUtil.infoMessage("message.common.changes.save.success");
				setOnEditionMode(false);
			} else {
				FacesUtil.errorMessage("message.common.changes.save.failure");
			}

		} catch (org.springframework.dao.DataIntegrityViolationException e) {
			e.printStackTrace();
			// todo. mensaje
			FacesUtil.errorMessage("message.withholding.error.unremovable");
			
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return outcome;
	}

	public boolean edit() {

		if (!isOnEditionMode()) {
			setOnEditionMode(true);
		}
		return onEditionMode;

	}

	private boolean isValid() {
		boolean isValid = true;

		for (Tax tax : taxList) {
			if (null == tax.getTaxCode()) {
				FacesUtil.errorMessage("message.withholding.validation.error.taxCode.required");
				isValid = false;
				break;
			} else if (null == tax.getInvoiceType()) {
				FacesUtil.errorMessage("message.withholding.validation.error.invoiceType.required");
				isValid = false;
				break;
			} else if (null == tax.getCalculationType()) {
				FacesUtil.errorMessage("message.withholding.validation.error.calculationType.required");
				isValid = false;
				break;
			} else if (null == tax.getDescription()) {
				FacesUtil.errorMessage("message.withholding.validation.error.description.required");
				isValid = false;
				break;
			} else {
				if (tax.isCalculationTypePercentage()) {

					if (null == tax.getParameterValue() || null == tax.getCalculationBase()) {
						FacesUtil.errorMessage("message.withholding.validation.error.percentage");
						isValid = false;
						break;
					}

					if (null != tax.getParameterValue()
							&& (BigDecimalUtil.isGreaterThan(tax.getParameterValue(), new BigDecimal(100l))
									|| BigDecimalUtil.isLessThanZero(tax.getParameterValue()))) {
						FacesUtil.errorMessage("message.withholding.validation.error.percentage.between0and100");
						isValid = false;
						break;
					}
				}

				if (tax.isCalculationTypeFixed() && (null == tax.getParameterValue())) {
					FacesUtil.errorMessage("message.withholding.validation.error.fixed");
					isValid = false;
					break;
				}
			}

		}

		return isValid;
	}

	@Override
	public String cancel() {
		String outcome = null;
		setOnEditionMode(false);
		initTaxList();
		return outcome;
	}

	public String back() {
		FacesUtil.addToRequestMap("selectedCompanyTax", selectedCompany);
		return navigationRuler.getTaxListPage();
	}

	@Override
	public String delete() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

	public LoginController getLoginController() {
		return loginController;
	}

	public void setLoginController(LoginController loginController) {
		this.loginController = loginController;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public Company getSelectedCompany() {
		return selectedCompany;
	}

	public void setSelectedCompany(Company selectedCompany) {
		this.selectedCompany = selectedCompany;
	}

	public NavigationRuler getNavigationRuler() {
		return navigationRuler;
	}

	public void setNavigationRuler(NavigationRuler navigationRuler) {
		this.navigationRuler = navigationRuler;
	}

	public CompanyService getCompanyService() {
		return companyService;
	}

	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public List<Tax> getTaxList() {
		return taxList;
	}

	public void setTaxList(List<Tax> taxList) {
		this.taxList = taxList;
	}

	public List<Tax> getSelectedTax() {
		return selectedTax;
	}

	public void setSelectedTax(List<Tax> selectedTax) {
		this.selectedTax = selectedTax;
	}

	public List<CALCULATION_BASE> getCalculationBases() {
		return calculationBases;
	}

	public void setCalculationBases(List<CALCULATION_BASE> calculationBases) {
		this.calculationBases = calculationBases;
	}

	public CALCULATION_BASE getCalculationBase() {
		return calculationBase;
	}

	public void setCalculationBase(CALCULATION_BASE calculationBase) {
		this.calculationBase = calculationBase;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public List<CALCULATION_TYPE> getCalculationTypes() {
		return calculationTypes;
	}

	public void setCalculationTypes(List<CALCULATION_TYPE> calculationTypes) {
		this.calculationTypes = calculationTypes;
	}

	public CALCULATION_TYPE getCalculationType() {
		return calculationType;
	}

	public void setCalculationType(CALCULATION_TYPE calculationType) {
		this.calculationType = calculationType;
	}

	public InvoiceType getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
	}

	public boolean isOnEditionMode() {
		return onEditionMode;
	}

	public void setOnEditionMode(boolean onEditionMode) {
		this.onEditionMode = onEditionMode;
	}

	public TaxService getTaxService() {
		return taxService;
	}

	public void setTaxService(TaxService taxService) {
		this.taxService = taxService;
	}

	public SelectorController getSelectorController() {
		return selectorController;
	}

	public void setSelectorController(SelectorController selectorController) {
		this.selectorController = selectorController;
	}
}
