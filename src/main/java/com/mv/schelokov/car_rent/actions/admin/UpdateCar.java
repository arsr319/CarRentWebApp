package com.mv.schelokov.car_rent.actions.admin;

import com.mv.schelokov.car_rent.actions.AbstractAction;
import com.mv.schelokov.car_rent.actions.JspForward;
import com.mv.schelokov.car_rent.consts.Jsps;
import com.mv.schelokov.car_rent.exceptions.ActionException;
import com.mv.schelokov.car_rent.model.entity.Car;
import com.mv.schelokov.car_rent.model.entity.builders.CarBuilder;
import com.mv.schelokov.car_rent.model.services.CarService;
import com.mv.schelokov.car_rent.model.services.exceptions.ServiceException;
import com.mv.schelokov.car_rent.model.validators.CarValidator;
import com.mv.schelokov.car_rent.model.validators.ValidationResult;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Maxim Chshelokov <schelokov.mv@gmail.com>
 */
public class UpdateCar extends AbstractAction {
    private static final Logger LOG = Logger.getLogger(UpdateCar.class);
    private static final String ERROR = "Unable to write car to database.";

    @Override
    public JspForward execute(HttpServletRequest req, HttpServletResponse res)
            throws ActionException {
        
        JspForward forward = new JspForward();
        
        if (isAdmin(req)) {
            try {
                int carId = getIntParam(req, "id");
                Car car = new CarBuilder()
                        .setId(carId)
                        .setLicensePlate(req.getParameter("plate"))
                        .setPrice(getIntParam(req, "price"))
                        .setYearOfMake(getIntParam(req, "year"))
                        .setModel(CarService.getModelByNameOrCreate(
                                req.getParameter("model"),
                                req.getParameter("make")))
                        .getCar();
                int validationResult = new CarValidator(car).validate();
                if (validationResult != ValidationResult.OK) {
                    req.setAttribute("car", car);
                    req.setAttribute("errParam", validationResult);
                    
                    forward.setUrl(Jsps.ADMIN_EDIT_CAR);
                    
                    return forward;
                }
                if (car.getId() == 0) {
                    car.setAvailable(true);
                    CarService.createCar(car);
                } else
                    CarService.updateCar(car);
                
                forward.setUrl("action/car_list");
                forward.setRedirect(true);
                
                return forward;
            }
            catch (ServiceException ex) {
                LOG.error(ERROR, ex);
                throw new ActionException(ERROR, ex);
            }
        }
        sendForbidden(res);
        return forward;
    }
}