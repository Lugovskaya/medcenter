package command.impl;

import command.ICommand;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SignUp implements ICommand{
//    private ServiceFactory serviceFactory = ServiceFactory.getInstance();
//    private PageName pageName = PageName.INDEX;

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        String login =  request.getParameter("login_up");
        String password = request.getParameter("password_up");
        String email = request.getParameter("email");
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
//        ClientService clientService = serviceFactory.getClientService();
//        if(!clientService.findClientByLogin(login)){
//            if(!clientService.checkUniqueEmail(email)){
//                clientService.signUp(login,password,name,surname,email);
//            }
//        }
//        return pageName.getPath();'
      return "123";
    }


}
