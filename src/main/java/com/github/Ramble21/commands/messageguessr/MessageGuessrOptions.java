package com.github.Ramble21.commands.messageguessr;

public class MessageGuessrOptions {
    private int timeCutoff;
    private boolean hideOldMembers;
    private boolean hideCommands;
    private boolean useNicknames;

    public MessageGuessrOptions(int timeCutoff, boolean hideOldMembers, boolean hideCommands, boolean useNicknames) {
        this.timeCutoff = timeCutoff;
        this.hideOldMembers = hideOldMembers;
        this.hideCommands = hideCommands;
        this.useNicknames = useNicknames;
    }

    public int getTimeCutoff() {
        return timeCutoff;
    }

    public void setTimeCutoff(int timeCutoff) {
        this.timeCutoff = timeCutoff;
    }

    public boolean hidesOldMembers() {
        return hideOldMembers;
    }

    public void setHideOldMembers(boolean hideOldMembers) {
        this.hideOldMembers = hideOldMembers;
    }

    public boolean hidesCommands() {
        return hideCommands;
    }

    public void setHideCommands(boolean hideCommands) {
        this.hideCommands = hideCommands;
    }

    public boolean usesNicknames() {
        return useNicknames;
    }

    public void setUseNicknames(boolean useNicknames) {
        this.useNicknames = useNicknames;
    }

}