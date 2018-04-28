/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;




import command.CommandProvider;
import command.ICommand;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
 
@WebServlet(name="Controller",
        urlPatterns="/MarchenkoMedCenter/site/*")
 
public class ServletController extends HttpServlet {
    private final static CommandProvider commandProvider = CommandProvider.getInstance();
  
 
    @Override
    public void init() {
    }
 
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                 System.out.println("doGet");
        processRequest(request,response);
    }
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
          System.out.println("doPos");
        processRequest(request,response);
    }
 
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      System.out.println("12312312312");
       ICommand command = commandProvider.getCommand(request);
             System.out.println(command.toString());
              System.out.println("34535345345");
        String page = command.execute(request,response);
         System.out.println(page);
          System.out.println("8768678678");
       RequestDispatcher dispatcher = request.getRequestDispatcher(page);
//       
        if (dispatcher != null) {
            dispatcher.include(request, response);
        }
    }
 
    @Override
    public void destroy() {
 
    }
 
}
