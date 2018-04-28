package command;


import command.impl.SignIn;
import command.impl.SignUp;
import static java.lang.System.console;

import javax.servlet.http.HttpServletRequest;
import java.util.EnumMap;
import java.util.Map;


public class CommandProvider {
    private final static CommandProvider INSTANCE = new CommandProvider();
    private Map<CommandName, ICommand> commands = new EnumMap<CommandName,ICommand>(CommandName.class);

    public CommandProvider() {

        commands.put(CommandName.SIGN_IN,new SignIn());
        commands.put(CommandName.SIGN_UP,new SignUp());

    }

    public static CommandProvider getInstance() {
        return INSTANCE;
    }


    public ICommand getCommand(HttpServletRequest request) {
        ICommand iCommand = commands.get(CommandName.WRONG_REQUEST);
        String command = request.getRequestURI();
        System.out.println(command);
        command=command.replace("/MarchenkoMedCenter/site/","");
        try {
            CommandName commandName = CommandName.valueOf(command.toUpperCase());
             System.out.println(commandName);
                System.out.println("555555555");
            iCommand = commands.get(commandName);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        return iCommand;
    }
}
