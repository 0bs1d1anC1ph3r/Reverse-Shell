package obs1d1anc1ph3r.reverseshell.plugins;

public interface CommandPlugin {

    String getCommandName();

    String execute(String[] args);
}
