package com.mv.schelokov.carent.actions;

import com.mv.schelokov.carent.actions.interfaces.AbstractAction;
import com.mv.schelokov.carent.actions.consts.Jsps;
import com.mv.schelokov.carent.actions.consts.SessionAttr;
import com.mv.schelokov.carent.actions.exceptions.ActionException;
import com.mv.schelokov.carent.model.entity.User;
import com.mv.schelokov.carent.model.entity.builders.RoleBuilder;
import com.mv.schelokov.carent.model.entity.builders.UserBuilder;
import com.mv.schelokov.carent.model.services.UserService;
import com.mv.schelokov.carent.model.services.exceptions.ServiceException;
import com.mv.schelokov.carent.model.validators.UserValidator;
import com.mv.schelokov.carent.model.validators.ValidationResult;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

/**
 *
 * @author Maxim Chshelokov <schelokov.mv@gmail.com>
 */
public class Login extends AbstractAction {
    
    private static final Logger LOG = Logger.getLogger(Login.class);
    private static final String ERROR = "Failed to login";

    @Override
    public JspForward execute(HttpServletRequest req, HttpServletResponse res)
            throws ActionException {
        User user = new UserBuilder()
                .setLogin(req.getParameter("email"))
                .setPassword(req.getParameter("pass"))
                .setRole(new RoleBuilder().setId(1).getRole())
                .getUser();
        try {
            int validationResult = new UserValidator(user).validate();
            if (validationResult == ValidationResult.OK) {
                List userList = new UserService()
                        .getUserByCredentials(user);
                if (userList.size() == 1) {
                    user = (User) userList.get(0);
                    HttpSession session = req.getSession();
                    session.setAttribute(SessionAttr.USER, user);
                    if (isAdmin(req)) {
                        return new JspForward("action/admin_actions", true);
                    } else {
                        return new JspForward("action/welcome", true);
                    }
                } else
                    validationResult = ValidationResult.USER_NOT_FOUND;
            }
            req.setAttribute("errParam", validationResult);
            req.setAttribute("sign", false);
            req.setAttribute("user_edit", user);
            return new JspForward(Jsps.HOME);
        } catch (ServiceException ex) {
            LOG.error(ERROR, ex);
            throw new ActionException(ERROR, ex);
        }
    }
}