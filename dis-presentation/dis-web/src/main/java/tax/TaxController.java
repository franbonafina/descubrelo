package tax;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.discovery.dis.domain.impl.Company;
import com.discovery.dis.domain.security.impl.User;
import com.discovery.dis.security.UserService;
import com.discovery.dis.web.navigation.NavigationRuler;
import com.discovery.dis.web.security.LoginController;

@ManagedBean(name = "taxContoller")
@ViewScoped
public class TaxController {

    @ManagedProperty(value = "#{loginController}")
    private LoginController loginController;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    @ManagedProperty(value = "#{navigationRuler}")
    private NavigationRuler navigationRuler;

    private Company selectedCompany;

    public TaxController() {}

    @PostConstruct
    public void init() {
    	
        User user = loginController.getUser();
        userService.fetchUserCompanies(user);

        selectedCompany = (Company) FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
				.get("selectedCompanyTax");
		if (null == selectedCompany) {
			selectedCompany = user.getDefaultCompany();
		}
        
    }

    /*
     * Listeners
     */

    public String search() {
        FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
                        .put("selectedCompanyTax", selectedCompany);

        return navigationRuler.getTaxCRUDOutcome();
    }

    /*
     * Getters & Setters
     */

    public NavigationRuler getNavigationRuler() {
        return navigationRuler;
    }

    public void setNavigationRuler(NavigationRuler navigationRuler) {
        this.navigationRuler = navigationRuler;
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public Company getSelectedCompany() {
        return selectedCompany;
    }

    public void setSelectedCompany(Company selectedCompany) {
        this.selectedCompany = selectedCompany;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
